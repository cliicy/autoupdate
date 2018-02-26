package com.ca.arcflash.webservice.replication;

import java.net.ConnectException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.ha.webservice.MonitorWebClientManager;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.jni.FileItemModel;
import com.ca.arcflash.webservice.jni.HyperVRepParameterModel;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.SourceItemModel;
import com.ca.arcflash.webservice.jni.VMwareRepParameterModel;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.replication.BackupDestinationInfo;
import com.ca.arcflash.webservice.data.VMwareConnParams;

public class ReplicationProxy {
	
	private static Logger logger = Logger.getLogger(ReplicationProxy.class);
	
	public static final String HART_FOLDER = "HART";
	
	private static ReplicationProxy proxy = new ReplicationProxy();
	
	private ReplicationProxy(){}
	
	public static synchronized ReplicationProxy getInstance(){
		return proxy;
	}
	
	public String getVMDiskSignature(ReplicationJobScript jobScript,String esxHost,String esxUser,String esxPassword,
			 						 String moreInf,int port,VMwareConnParams exParams, String snapMoref, String vmdkUrl,String jobID){
		
		String signature = "";
		boolean isProxy = false;
		try {
			isProxy = isProxyEnabled(jobScript);
		} catch (Exception e) {
			return signature;
		}
		
		if(isProxy){
			HeartBeatJobScript heartJobScript = HAService.getInstance().getHeartBeatJobScript(jobScript.getAFGuid());
			WebServiceClientProxy client = null;
			try {
				client = MonitorWebClientManager.getMonitorWebClientProxy(heartJobScript);
			} catch (Exception e) {
				if (e.getCause() instanceof ConnectException || e.getCause() instanceof SSLHandshakeException ||
						e.getCause() instanceof SocketException || e.getCause() instanceof SSLException) {
					String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_FAIL_TO_CONNECT_HOST, heartJobScript.getHeartBeatMonitorHostName(), e.getMessage());
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, Long.parseLong(jobID), Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				}
				logger.error(e);
			}
			
			
			try {
				signature = client.getServiceV2().getVMDiskSignature(jobScript.getAFGuid(), esxHost,esxUser,esxPassword,
						 												moreInf,port, snapMoref, vmdkUrl,jobID, exParams);	
			} catch (Exception e) {
				if (e.getCause() instanceof ConnectException || e.getCause() instanceof SSLHandshakeException ||
						e.getCause() instanceof SocketException || e.getCause() instanceof SSLException) {
					String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_FAIL_TO_CONNECT_HOST, heartJobScript.getHeartBeatMonitorHostName(), e.getMessage());
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, Long.parseLong(jobID), Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				}
				logger.error(e);
			}
			

		
		}else {
			NativeFacade facade = BackupService.getInstance().getNativeFacade();

			signature = facade.GetVMDKSignature(jobScript.getAFGuid(), esxHost,esxUser,esxPassword,
												moreInf,port,exParams,snapMoref, vmdkUrl,jobID);
		}
		
		return signature;
		
	}
	
	public int replicate(ReplicationJobScript jobScript,
						 SessionInfo session,
						 long jobID,
						 Boolean isSmartCopy,
						 String startSession,String endSession,
						 VMwareRepParameterModel repModel){
		
		int result = 1;
		
		HyperVRepParameterModel prams = createHyperVTransParams(
										jobScript, jobID, session,repModel);
		if (prams == null) {
			String msg = "cannot create replication script";
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			return result;
		}
		
		if(isSmartCopy){
			logger.info("Smart Copy is Enabled.");
			logger.info("Smart copy begin session: " + startSession);
			logger.info("Smart copy end session: " + endSession);
			addSmartCopyInfo(prams,isSmartCopy,startSession,endSession);
		}
		
		prams.setVmwareRepModel(repModel);
		
		HACommon.printJobScript(prams);
		
		result = (int)CommonService.getInstance().getNativeFacade()
				.HyperVRep(prams);
		
		return result;
	}
	
	private boolean isProxyEnabled(ReplicationJobScript jobScript) throws Exception{
		
		if(jobScript == null){
			logger.error("Replication job script is not set.");
			throw new Exception("Replication job script is not set");
		}
		
		List<ReplicationDestination> destList = jobScript.getReplicationDestination();
		if(destList == null || destList.size() == 0){
			logger.error("Replication destination is not set.");
			throw new Exception("Replication destination is not set");
		}
		
		ReplicationDestination dest = destList.get(0);
		boolean isProxy = dest.isProxyEnabled();
		return isProxy;
	}
	
	private HyperVRepParameterModel createHyperVTransParams(
			ReplicationJobScript jobScript, long shrmemid,
			SessionInfo session,VMwareRepParameterModel vmwareRepModel) {
		
		ReplicationDestination replicationDestinaiton = jobScript.getReplicationDestination().get(0);
		HyperVRepParameterModel re = null;

		BackupDestinationInfo backupDestinationInfo = null;
		try {
			backupDestinationInfo = BaseReplicationCommand.getBackupDestinationInfo(jobScript, false, -1);
		} catch (ServiceException e) {
			logger.debug(e);
			return null;
		}

		if (backupDestinationInfo == null || backupDestinationInfo.getBackupDestination() == null)
			return null;

		re = new HyperVRepParameterModel();
		re.setAfGuid(jobScript.getAFGuid());
		re.setbCompressOnWire(false);
		re.setbEncryptOnWire(false);
		re.setbOverwriteExist(false);
		re.setPwszCryptPassword(null);
		re.setUlCtlFlag(4);
		re.setUlDesVHDFormat(0);
		re.setUlJobType(0);
		re.setUlProtocol(HyperVRepParameterModel.HAJS_PROTOCOL_SOCKET);
		re.setUlSrcItemCnt(1);
		re.setJobID(shrmemid);
		
		ArrayList<SourceItemModel> pSrcItemList = new ArrayList<SourceItemModel>();

		SourceItemModel sim = new SourceItemModel();

		sim.setPwszPath(session.getSessionFolder());
		sim.setPwszSFPassword(backupDestinationInfo.getNetConnPwd());
		sim.setPwszSFUsername(backupDestinationInfo.getNetConnUserName());

		//construct FileItemModel begin
		List<FileItemModel> fileItems = vmwareRepModel.getFiles();
		//construct FilteItemModel ends.
		
		sim.setDiskCount(fileItems.size());
		sim.setFiles(fileItems);
		
		pSrcItemList.add(sim);

		re.setpSrcItemList(pSrcItemList);
//		re.setPwszJobID(session.getSessionGuid()+"_"+shrmemid);
		// save session will use the same job ID of dengfeng
		re.setPwszJobID(session.getSessionGuid());
		// why local username
		re.setPwszLocalPassword(null);
		re.setPwszLocalUsername(null); // deng feng will get it from D2D

		HeartBeatJobScript heartBeatJobScript = HAService.getInstance().getHeartBeatJobScript(jobScript.getAFGuid());
		
		re.setPwszDesHostName(heartBeatJobScript.getHeartBeatMonitorHostName());
		re.setPwszDesPort(HAService.getInstance().getHATranServerPort(jobScript.getAFGuid()));
		re.setPwszUserName(heartBeatJobScript.getHeartBeatMonitorUserName());
		re.setPwszPassword(heartBeatJobScript.getHeartBeatMonitorPassword());

		String d2dFolder = HAService.getInstance().getMonitorD2DInstallPath(jobScript.getAFGuid());
		if(!d2dFolder.endsWith("\\")){
			d2dFolder += "\\";
		}
		d2dFolder += HART_FOLDER;
		
		re.setPwszDesFolder(d2dFolder);

		String productNode = HACommon.getProductionServerNameByAFRepJobScript(jobScript);
		
		re.setPwszProductNode(productNode);

		re.setUlReplicaConvType(replicationDestinaiton.isProxyEnabled()?1:0);
		
		re.setBackupDescType(jobScript.getBackupDestType());
		
		return re;
	}
	
	private void addSmartCopyInfo(HyperVRepParameterModel model,boolean isSmartCopy,String beginSession,String endSession){
		model.setbSmartCopy(isSmartCopy);
		if(!StringUtil.isEmptyOrNull(beginSession) && !StringUtil.isEmptyOrNull(endSession)){
			model.setUlScSessBegin(Long.valueOf(beginSession.substring(1)));
			model.setUlScSessEnd(Long.valueOf(endSession.substring(1)));
		}
	}
}

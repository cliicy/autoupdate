package com.ca.arcflash.webservice.replication;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.failover.model.BackupInfo;
import com.ca.arcflash.failover.model.BackupInfoFactory;
import com.ca.arcflash.ha.ReplicationConfiguration;
import com.ca.arcflash.ha.model.internal.HAException;
import com.ca.arcflash.ha.modelWebService.MonitorWebServiceErrorCode;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.SharedFolder;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.jni.HyperVRepParameterModel;
import com.ca.arcflash.webservice.jni.SourceItemModel;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;

public class ShareFolderReplicationCommand  extends BaseTransReplicationCommand {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5833959145338054409L;
	private static final Logger log = Logger
	.getLogger(ShareFolderReplicationCommand.class);
	protected List<String> createTransCommands(String jobID,ReplicationJobScript jobScript,
			SessionInfo sessionInfo){
		
		ReplicationDestination des = jobScript.getReplicationDestination().get(0);
		
		SharedFolder sharedFolder = (SharedFolder) des;
		File session = new File(sessionInfo.getSessionFolder());
		List<String> commands = new LinkedList<String>();
		commands.add(CommonUtil.D2DInstallPath + "BIN\\HATransClient.exe");
		commands.add("-share");
		commands.add("-id:" + jobID+"_"+ session.getName());
		commands.add("-src:"+ session.getAbsolutePath());
		commands.add("-des:"+ sharedFolder.getUNCPath());
		commands.add("-username:"+ sharedFolder.getUserName());
		commands.add("-userpwd:"+ sharedFolder.getPassword());
		commands.add("-productnode:"+ ServiceContext.getInstance().getLocalMachineName());
		commands.add("-desformat:"	+ sharedFolder.getDesCompressType());
		if (sharedFolder.getNetworkThrottlingInKB() != null
				&& sharedFolder.getNetworkThrottlingInKB() > 0) 
		{
			commands.add("-throttling:"+ sharedFolder.getNetworkThrottlingInKB());
		}
//		if (log.isDebugEnabled()) {
//			log.debug(Arrays.toString(commands.toArray(
//					new String[0])));
//		} // this may cause a Fortify Privacy violation issue .
		return commands;
	}
	//Use dengfeng's interface to get the latest GUID of session replicated
	@Override
	String[] getGuidsOfReplicatedSessions(ReplicationJobScript jobScript) throws Exception {
		
		ReplicationDestination curDes = jobScript.getReplicationDestination().get(0); 
		
		String []EMPTY = new String[0];;
		if(!(curDes instanceof SharedFolder)) return EMPTY;
 		
//		ReplicationConfiguration repCon = HAService.getInstance().getRelipcationConfiguration();
		ReplicationDestination preVious = null;
//		if(repCon!=null){
//			List<ReplicationDestination> list = repCon.getReplicationDestination();
//			if(!list.isEmpty()) {
//				preVious = list.get(0);
//			}
//		}
		if(preVious == null) return EMPTY;
		if(!(preVious instanceof SharedFolder)) return EMPTY;
		
		SharedFolder sd = (SharedFolder)preVious;
		List<String> sessions = new ArrayList<String>();
		List<String> sessionsGuidResult = new ArrayList<String>();
		String hostname = ServiceContext.getInstance().getLocalMachineName();
		if(hostname.isEmpty()|| hostname.toLowerCase().equals("localhost")) 
			throw new Exception(ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAIL_GET_SESSIONS));
		String dest = sd.getUNCPath();
		if(!dest.endsWith("\\")) {
			dest = dest+"\\";
		}
		dest=dest+hostname;
		long ret = BrowserService.getInstance().getNativeFacade().getReplicatedSessions(dest, sessions, null,null,sd.getUserName(), sd.getPassword());
		for(String backupXMLContent:sessions){
			BackupInfo backupInfoFromString = BackupInfoFactory.getBackupInfoFromString(backupXMLContent);
			if(backupInfoFromString!=null){
				sessionsGuidResult.add(backupInfoFromString.getSessionGUID());
			}

		}
		
		if(ret!=0)
			throw new Exception(ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAIL_GET_SESSIONS));
		return sessionsGuidResult.toArray(EMPTY);
	}

	@Override
	protected HyperVRepParameterModel createHyperVTransParams(
			BackupDestinationInfo backupDestinationInfo, ReplicationJobScript  jobScript, long shrmemid,
			SessionInfo session) {
		ReplicationDestination replicationDestinaiton  = jobScript.getReplicationDestination().get(0);
		HyperVRepParameterModel re = null;
		
		SharedFolder arcflash = (SharedFolder) replicationDestinaiton;
		 re = new HyperVRepParameterModel();
		
		re.setbCompressOnWire(false);
		re.setbEncryptOnWire(false);
		re.setbOverwriteExist(false);
		re.setPwszCryptPassword(null);
		
		re.setUlCtlFlag(0);
		re.setUlDesVHDFormat(arcflash.getDesCompressType());
		re.setUlJobType(0);
		re.setUlProtocol(HyperVRepParameterModel.HAJS_PROTOCOL_SHAREFOLEDR);
		re.setUlThrottling(arcflash.getNetworkThrottlingInKB());
		re.setUlSrcItemCnt(1);
		
		ArrayList<SourceItemModel> pSrcItemList = new ArrayList<SourceItemModel> ();
		
		SourceItemModel sim = new SourceItemModel();
		
		sim.setPwszPath(session.getSessionFolder());
		sim.setPwszSFPassword(backupDestinationInfo.getNetConnPwd());
		sim.setPwszSFUsername(backupDestinationInfo.getNetConnUserName());
		
		
		pSrcItemList.add(sim);
		
		re.setpSrcItemList(pSrcItemList);
		//re.setPwszJobID(session.getSessionGuid()+"_"+shrmemid);
		//save session will use the same job ID of dengfeng
		re.setPwszJobID(session.getSessionGuid());
		//why local username
		re.setPwszLocalPassword(null);
		re.setPwszLocalUsername(null);
		
		re.setPwszDesHostName(arcflash.getUNCPath());
		re.setPwszDesPort(null);
		re.setPwszUserName(arcflash.getUserName());
		re.setPwszPassword(arcflash.getPassword());
		
		re.setPwszDesFolder(null);
		
		String productNode = HACommon.getProductionServerNameByAFRepJobScript(jobScript);
		re.setPwszProductNode(productNode);
		
		re.setBackupDescType(jobScript.getBackupDestType());
		return re;
	}
	@Override
	protected int preProcess(ReplicationJobScript jobScript, BackupDestinationInfo backupDestinationInfo) {
		return 1;
		
	}

}

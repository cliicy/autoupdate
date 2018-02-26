package com.ca.arcflash.webservice.replication;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.modelmanager.RepositoryUtil;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.failover.model.ADRConfigureUtil;
import com.ca.arcflash.failover.model.BackupInfo;
import com.ca.arcflash.failover.model.Disk;
import com.ca.arcflash.ha.model.DiskInfo;
import com.ca.arcflash.ha.model.ProductionServerRoot;
import com.ca.arcflash.ha.model.ReplicaRoot;
import com.ca.arcflash.ha.model.TransServerReplicaRoot;
import com.ca.arcflash.ha.model.internal.HAException;
import com.ca.arcflash.ha.modelWebService.MonitorWebServiceErrorCode;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.ha.webservice.MonitorWebClientManager;
import com.ca.arcflash.jobscript.alert.AlertType;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ARCFlashStorage;
import com.ca.arcflash.jobscript.replication.DiskDestination;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMStorage;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.jni.FileItemModel;
import com.ca.arcflash.webservice.jni.HyperVRepParameterModel;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.SourceItemModel;
import com.ca.arcflash.webservice.replication.BaseReplicationCommand.NotFullMachineBackupSessionException;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSPhereFailoverService;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;

public abstract class BaseTransReplicationCommand extends BaseReplicationCommand {
	public static String getHostNameForVmguid(String vmguid) {
		return HAService.getInstance().getHostNameForVmguid(vmguid);
		
	}	
	
	// Smart Copy
	public BaseTransReplicationCommand() {
	}
	// Smart Copy
	
	/**
	 * this function is invoked by JNI's callback
	 * @param pwszJobID
	 * @param uliTotal
	 * @param uliTrans
	 */
	public static void hyperVUpdateRepJobMonitorProgress(String afGuid,String pwszJobID, long uliTotal, long uliTrans){
		RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(afGuid);
		NumberFormat percentInstance = new DecimalFormat("#0");
		String format = "0";
		long preT = 0;
		synchronized (jobMonitor) {
			jobMonitor.setRepJobID(pwszJobID);
			jobMonitor.setRepTotalSize(uliTotal);
			jobMonitor.setRepTransedSize(uliTrans);
			preT = jobMonitor.getRepTransedSize();
			
			jobMonitor.setRepPhase(RepJobMonitor.REP_JOB_PHASE_DATA_TRANSFER);
			
			if(jobMonitor.getRepPhase() == RepJobMonitor.REP_JOB_PHASE_DATA_TRANSFER) {
				//jobMonitor.setRepElapsedTime(System.currentTimeMillis() - jobMonitor.getRepStartTime());
				jobMonitor.setRepElapsedTime((System.nanoTime() - jobMonitor.getRepStartNanoTime()) / 1000 / 1000);
				jobMonitor.setRepJobElapsedTime(System.currentTimeMillis() - jobMonitor.getRepJobStartTime());
			}
			
			}
		if (log.isDebugEnabled()) {
			if (uliTotal > 0) {
				float preP = 100 * ((preT * 1.0f) / uliTotal);
				float x = 100 * ((uliTrans * 1.0f) / uliTotal);

				if (x > 5 + preP) {
					format = percentInstance.format(x);
					log.debug("From rep progress callback: " + pwszJobID
							+ "-> transfered/Total:" + format + "%(" + uliTrans
							+ "/" + uliTotal + ")");
				}
			}
		}

	}
	
	/**
	 * This funciton is called by JNI's call back
	 * typedef enum _e_hadt_severity_level
		{
		    MSG_INFO = 0,
		    MSG_WARNING = 1,
		    MSG_ERROR = 2,
		} HADT_SEVERITY_LEVEL;

	 * @param pwszJobID
	 * @param eLevel
	 */
	public static void hyperVUpdateRepJobMonitorReportMsg(String afGuid,String pwszJobID,
			int eLevel, long ulMsgID, ArrayList<String> ppParams) {
		long jobID = -1;
		RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(afGuid);
		synchronized (jobMonitor) {
			jobID = jobMonitor.getId();
		}
		
		String mess = "";
		if(ulMsgID < 20000) {
			if (ulMsgID == ReplicationMessage.AFRES_AFREPC_SOCK_FAIL_AUTH) {
				HeartBeatJobScript heartBeatJobScript = HAService.getInstance().getHeartBeatJobScript(afGuid);
				if (heartBeatJobScript != null) {
					mess = ReplicationMessage.getResource(
							ReplicationMessage.AFRES_AFREPC_PREX + ulMsgID, heartBeatJobScript.getHeartBeatMonitorUserName(), heartBeatJobScript.getHeartBeatMonitorHostName());
				}
			} else {
				if (ppParams == null || ppParams.isEmpty())
					mess = ReplicationMessage
							.getResource(ReplicationMessage.AFRES_AFREPC_PREX + ulMsgID);
				else
					mess = ReplicationMessage.getResource(
							ReplicationMessage.AFRES_AFREPC_PREX + ulMsgID, (String[])(ppParams
									.toArray(new String[0])));
			}
		}
		else {
			mess = VMwareNonProxyConversionManager.getVDDKErrorMsg(ulMsgID, null, null, afGuid);
		}
		
		if(mess != null){
			
			HACommon.addActivityLogByAFGuid(eLevel, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { mess,"", "", "", "" }, afGuid);
			
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -3349949644544414907L;
	private static final Logger log = Logger
			.getLogger(BaseTransReplicationCommand.class);
	private String replicationSubRoot = null;
	
	private void destroyHyperVVM(ReplicationJobScript jobScript) {
		try {
			String vmUUID = jobScript.getReplicationDestination().get(0).getVmUUID();
			WebServiceClientProxy client = MonitorWebClientManager
					.getMonitorWebClientProxy(HAService.getInstance()
							.getHeartBeatJobScript(jobScript.getAFGuid()));
			client.getServiceV2().destroyHyperVVM(vmUUID);
		} catch (NullPointerException e) {
			log.error(e.getMessage());
		}
	}
	
	protected int doReplication(ReplicationJobScript jobScript,
			ReplicationDestination replicationDestinaiton, BackupDestinationInfo backupDestinationInfo) {
		
		//set it for detecting smart copy
		setMaxSnapshotCount(HACommon.getMaxSnapshotCountForHyperV(jobScript.getAFGuid()));
		
		RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(jobScript.getAFGuid());
		
		long jobID = jobMonitor.getId();
		
		setJobMonitorSnapshotCount(jobScript.getAFGuid());
		
		if (!validatePlatformDestination(jobScript.getAFGuid(), jobID, jobScript, null))
			return HACommon.REPLICATE_FAILURE;
		
		
		try {
			boolean needRecreateVm = false;
			do {
			String lastSessionName=null;
			int replicateTime = 0;
			SessionInfo[] sessions = null;
			
			int createVMResult = preProcess(jobScript, backupDestinationInfo);
			if (createVMResult == CommonUtil.VM_CREATE_CANCELED)
				return HACommon.REPLICATE_CANCEL;
			else if (createVMResult == CommonUtil.VM_CREATE_FAILED) {
				String msg = ReplicationMessage.getResource(
						ReplicationMessage.AFRES_AFREPC_PREX + ReplicationMessage.AFRES_AFREPC_JOB_FAILED);

				HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
				return HACommon.REPLICATE_FAILURE;
			}
			needRecreateVm = false;
			
			String snapShotUID = HAService.getInstance().getRunningSnapShotGuidForProduction(jobScript.getAFGuid());
			if(snapShotUID!=null)
			{
				String msg = ReplicationMessage.getMonitorServiceErrorString(MonitorWebServiceErrorCode.Repository_Session_SnapShot_VM_POWEROFF, "");
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
				HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
				return HACommon.REPLICATE_FAILURE;
			}
			
			BaseReplicationJobContext jobContext = getJobContext( jobID );
			
			do{
				synchronized (jobMonitor) {
					if (isVirtualStandbyCancelled(jobMonitor))
						return HACommon.REPLICATE_CANCEL;
				}

				replicateTime++;
				if(replicateTime > 1)
				{
					lastSessionName = sessions[sessions.length-1].getSessionName();
					sessions = null;
				}   

				try {
					sessions = getNextSessionsToReplicate(jobScript,true,lastSessionName,true, jobContext, backupDestinationInfo);
				}
				catch(Exception e) {
					String msg = "";
					log.error("Unable to get next sessions.", e);
					long logLevel = Constants.AFRES_AFALOG_WARNING;
					if (e instanceof NotFullMachineBackupSessionException) {
						logLevel = Constants.AFRES_AFALOG_ERROR;
						msg = ReplicationMessage.getResource(ReplicationMessage.AFRES_AFREPC_PREX
								+ ReplicationMessage.AFRES_AFREPC_JOB_FAILED);
					} else {
						msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_GET_NEXT_SESSION);
					}
					HACommon.addActivityLogByAFGuid(logLevel, jobID, Constants.AFRES_AFJWBS_GENERAL,
							new String[] { msg,	"", "", "", "" }, jobScript.getAFGuid());
					if (replicateTime > 1) {
						// The current session is successfully converted. So the job should succeed though it failed to get next sessions.
						return HACommon.REPLICATE_SUCCESS;
					} else {
						msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_RET_MSG, e.getMessage());
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL,
								new String[] { msg,	"", "", "", "" }, jobScript.getAFGuid());
						HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
						return HACommon.REPLICATE_FAILURE;
					}
				}
				
				if (sessions.length == 0) {
					log.info("executeReplicate has no session to replicate");
					if(replicateTime == 1){
						addActivityLog(jobScript.getAFGuid(), Constants.AFRES_AFALOG_INFO,
								ReplicationMessage.REPLICATION_NO_BACKUP_SESSION);
						return HACommon.REPLICATE_NOSESSIONS;
					}
					
					return HACommon.REPLICATE_SUCCESS;
				}
				
				if(log.isDebugEnabled()){
					StringBuilder sb = new StringBuilder();
					for(SessionInfo session:sessions){
						if(sb.length()==0)
						sb.append(session.getSessionFolder());
						else sb.append(", ").append(session.getSessionFolder());
					}
					log.debug("will to replicate session:"+ sb.toString());
					
				}

				if (isVSBWithoutHASupport() && !jobScript.getBackupToRPS())
				{
					if (checkIfSessionMerging(jobScript, sessions, jobID))
						return HACommon.REPLICATE_SKIPPED;
				}

				String mess = "";
				if (sessions.length == 1) {
					mess = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_SESSION,sessions[0].getSessionName());
				} else {
					mess = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_SESSIONS,sessions[0].getSessionName(), sessions[sessions.length - 1].getSessionName());
				}
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { mess,"", "", "", "" }, jobScript.getAFGuid());

				String vmName = jobScript.getReplicationDestination().get(0).getVirtualMachineDisplayName();
				int ret = replicateSessions(backupDestinationInfo, sessions, jobScript,jobID, vmName);
				if (ret == HACommon.REPLICATE_DISKSIZECHANE) {
					destroyHyperVVM(jobScript);
					needRecreateVm = true;
					break;
					// TODO
				}
				if(ret != HACommon.REPLICATE_SUCCESS)
				{
					log.error("Replication Job finishes with ERROR | return code="+ ret);
					return ret;
				}
				
			}while((sessions != null) && !isVirtualStandbyPaused( jobScript ));
			
			} while (needRecreateVm);

		} catch (Exception e1) {
			
			log.error("Failed to replicate.",e1);
			
			{
				String msg = ReplicationMessage.getResource(
						ReplicationMessage.AFRES_AFREPC_PREX + ReplicationMessage.AFRES_AFREPC_JOB_FAILED);
				
				if(e1 instanceof HAException){
					HAException he = (HAException) e1;
					String mes = ReplicationMessage.getMonitorServiceErrorString(he.getCode(),he.getMessage());
					if(!StringUtil.isEmptyOrNull(mes)) 
						msg = mes;
				}else	if(e1 instanceof SOAPFaultException){
					SOAPFaultException he = (SOAPFaultException) e1;
					String errorCode = he.getFault().getFaultCodeAsQName().getLocalPart();
					String mes = null;
					if(!MonitorWebServiceErrorCode.Repository_NO_SYSTEM_DISK_FOUND.equals(errorCode))
						mes = ReplicationMessage.getMonitorServiceErrorString(errorCode,he.getMessage());
					else
						mes = printRepFailError(jobScript, errorCode);
						
					if(!StringUtil.isEmptyOrNull(mes)) 
						msg = mes;
					
				} else if (e1 instanceof NotFullMachineBackupSessionException) {
					// Use common error message here.
				}
				else {
					msg = printRepFailError(jobScript);
				}
			
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
												new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				
				HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
			}
			
			return HACommon.REPLICATE_FAILURE;

		}finally{
			calculateThroughput(jobMonitor,jobScript.getAFGuid());
		}
		
		return HACommon.REPLICATE_SUCCESS;
		
	}

	String printRepFailError(ReplicationJobScript jobScript) {
		return printRepFailError(jobScript, null);
	}
	
	String printRepFailError(ReplicationJobScript jobScript, String erroCode) {
		String msg = "";
		ReplicationDestination  dest = jobScript.getReplicationDestination().get(0);
		if(dest instanceof ARCFlashStorage){
			ARCFlashStorage tmp = (ARCFlashStorage)dest;
			String vmName = tmp.getVirtualMachineDisplayName();
			String hostName = tmp.getHostName();
			if(StringUtil.isEmptyOrNull(erroCode))
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVER_HYPERV_FAIL_UNEXPECTED,
						vmName, hostName);
			else
				msg = ReplicationMessage.getMonitorServiceErrorString(erroCode,vmName, hostName); 
		}
		else
			log.error("Print log error.");
		return msg;
	}
	
	private int replicateSessions(BackupDestinationInfo backupDestinationInfo, SessionInfo[] sessions, 
							ReplicationJobScript jobScript, long jobID, String vmName) throws Exception {
		
		ReplicationDestination  replicationDestinaiton = jobScript.getReplicationDestination().get(0);
		
		RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(jobScript.getAFGuid());
		
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		long exitValue = 0;
		
		log.debug("Inside replicateSessions");
		
		NativeFacade facade = BackupService.getInstance().getNativeFacade();
		
		//SmartCopy is for pre-existing sessions before vcm setting is made
		boolean isSmartCopy = getSmartCopyFlag();
		
		/*
		 * if user select to merge all sessions to replicate to one after resume VCM, 
		 * it will be a special smart copy case, start session and end session will be explicitly specified later. 
		 */
		
		boolean forceSmartCopy = HAService.getInstance().isNextReplicationForcedToMergeAllSessions(jobScript.getAFGuid());
		
		if(!forceSmartCopy){
			forceSmartCopy = HAService.getInstance().isIncrmentalBackupComplete(jobScript);
		}
		log.info("Force SmartCopy flag: " + Boolean.toString(forceSmartCopy));
		
		String startSession = null;
		String endSession = null;
		StringBuilder smartCopySessions = new StringBuilder();
		List<SessionInfo> copiedSessions = new ArrayList<SessionInfo>();
		if (isSmartCopy || forceSmartCopy) {
			int from = 0;
			
			if(isSmartCopy){
				
				startSession = getSmartCopySynthetizeStart();
				endSession = getSmartCopySynthetizeSession();
				
				for (from = 0;from < sessions.length; from++) {
					if(sessions[from].getSessionName().equals(endSession)){
						break;
					}
				}
				
				log.info("smart copy begin session: " + startSession);
				log.info("smart copy end session: " + endSession);
				
			}
			
			
			if( forceSmartCopy ){
				
				if(!isSmartCopy){
					startSession = sessions[0].getSessionName();
				}
				
				from = sessions.length-1;
				endSession = sessions[from].getSessionName();
				
				log.info("force merge session " + startSession + " - " + endSession);
				
			}
			
			if(!isSmartCopy){
				isSmartCopy = true;
			}
			
			for (int i = 0; i <= from; i++) {
				smartCopySessions.append(sessions[i].getSessionName() + " ");
				copiedSessions.add(sessions[i]);
			}
			log.info("smart copy sessions: " + smartCopySessions.toString());
			sessions = Arrays.copyOfRange(sessions, from, sessions.length);
		}
		
		long toTransferBackupSize = 0;
		List<String> nextSessions = new LinkedList<String> ();
		{
//			StringBuilder sb = new StringBuilder();
			for(SessionInfo session:sessions){
//				if(sb.length()==0)
//				sb.append(session.getSessionName());
//				else sb.append(", ").append(session.getSessionName());
				nextSessions.add(session.getSessionName());
				toTransferBackupSize += session.getSessionDataSize();
			}
			
//			if(containCompressedData)
//				toTransferBackupSize = 0; // if contains compressed session, then the backup size is unknown(0).
			
//			String mess = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_SESSIONS,sb.toString());
//		
//			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
//					new String[] { mess,"", "", "", "" }, jobScript.getAFGuid());
		
		}
		
		String afGuid = jobScript.getAFGuid();
		RepositoryUtil repository = null;
		try{
			String xml = CommonUtil.getRepositoryConfPath();
			repository = RepositoryUtil.getInstance(xml);
		}catch (Exception e) {}
		
		for (SessionInfo session : sessions) {
			
			synchronized (jobMonitor) {
				if (isVirtualStandbyCancelled(jobMonitor))
				return HACommon.REPLICATE_CANCEL;
			}
			
			if(copiedSessions.isEmpty()){
				copiedSessions.add(session);
			}
			
			if (isSmartCopy) {
				if(startSession.compareToIgnoreCase(endSession) == 0){
					String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_SESSION_BEGIN, startSession);
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				} else {
					String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_SESSION_BEGIN_SC, startSession, endSession);
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				}
			} else {
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_SESSION_BEGIN, session.getSessionName());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			}
				
			synchronized (jobMonitor) {
				
				if(isSmartCopy){
					jobMonitor.setRepSessionName(smartCopySessions.toString().trim());
				}else{
					jobMonitor.setRepSessionName(session.getSessionName());
				}
					
				jobMonitor.setRepSessionBackupTime(session.getBackupTime());
			}

			try {
				connectToRemote(backupDestinationInfo, jobScript.getAFGuid(), jobID);
			} catch (Exception e) {
				log.warn("Failed to connect to backup destination" + backupDestinationInfo.getBackupDestination(), e);
			}

			ADRConfigure adrConfigure;
			try {
				adrConfigure = HACommon.getAdrConfigure(session);
			} catch (FileNotFoundException e1) {
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILTO_ACCESS_SESSION);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				adrConfigure=null;
			}
			if(adrConfigure == null){
				log.error("adrconfigure is null");
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILTO_PARSE_ADRCONFIGURE,
						 session.getSessionName());
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				return HACommon.REPLICATE_FAILURE;
			}
			
			try {
				if (HAService.getInstance().isDiskSizeChanged(
						HAService.getInstance().ifVmOwnedByAgent(
								vmName,
								jobScript,
								HAService.getInstance().getHeartBeatJobScript(
										jobScript.getAFGuid())), adrConfigure)) {
					log.warn("Detect disk size is changed!");
					return HACommon.REPLICATE_DISKSIZECHANE;
				}
			} catch (NullPointerException e) {

			}
			
			if(HACommon.isTargetPhysicalMachine(jobScript.getAFGuid())){
				//issue 20064227
				//VSphere backedup VM may not have adrconfigure.xml, so hyperV does not support it.
				Disk bootDisk = ADRConfigureUtil.getBootDisk(adrConfigure);
				if(bootDisk.getDiskType() == HACommon.DYNAMIC_DISK_TYPE){
					
					String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_BOOT_VOLUME_ON_DYNAMIC_DISK);		
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
													new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
					return HACommon.REPLICATE_FAILURE;
				}
			}
			
			//issue19941427
			//Attach disks in same order as source VM, 
			//and issue an warning at the first time of virtual conversion.
			if(!HACommon.isTargetPhysicalMachine(jobScript.getAFGuid())){
				//Update disk with system/boot volume info for issue19941427
				if(!ADRConfigureUtil.isBootAndSystemVolumeOnOneDisk(adrConfigure)
				   && !LogSysBootOnDiffDisk){
					// If adrConfigure is parsed from VMDiskInof.xml instead of Adrconfigure.xml, now it's hard to identify if sys and boot volumes are on diff disks
					if (!adrConfigure.isPartialAdrconfigure()) {
						String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_SYSTEM_BOOT_VOLUME_ON_DIFF_DISK);
						
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
														new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
						
						LogSysBootOnDiffDisk = true;
					}
				}
			}
			
			TransServerReplicaRoot root = getReplicaRoot(jobScript);
			if(root!=null) {
				HAService.getInstance().updateReplicationJobScript(jobScript, adrConfigure);
				updateRepository(root.getVmuuid(),root.getVmname(),jobScript, -1);
			} else {
				log.error("Failed to get subroot from repository.");
				
				String msg = printRepFailError(jobScript);
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
												new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
				
				return HACommon.REPLICATE_FAILURE;
			}
			
			try {
				//to avoid destination conflict,program will check whether there is name conflict in 
				//replication destination.A sub root will be created for the destination.
				//This sub root will be appended to the destination. If the sub root already in the destination.
				//There replace it with the new sub root.
				HyperVRepParameterModel subRootParamModel = createParamForRepDestSubRoot(jobScript, session, backupDestinationInfo);
				replicationSubRoot = facade.GetDestSubRoot(subRootParamModel);
				log.info("subRoot from native: " + replicationSubRoot);
				
				int result = checkReplicationSubRoot(jobScript);
				if(result!=0){
					String msg = printRepFailError(jobScript);
					
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
													new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
					
					return HACommon.REPLICATE_FAILURE;
				}
				
			} catch (ServiceException e) {
				log.error("Failed to generate subroot.",e);
				
				String msg = printRepFailError(jobScript);
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
												new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
				
				return HACommon.REPLICATE_FAILURE;
			}
						
			String sessionNumber = session.getSessionName();
			
			warnIncompleteSession(nativeFacade, jobID, adrConfigure,
						sessionNumber,jobScript.getAFGuid());
			
			//fix issue 19437678, check replication destination's space before do convert -begin
			HashMap<String, DiskInfo> freeStorageGroup = getAllStorageFreeSize(replicationDestinaiton, jobScript.getAFGuid());
			if (compareSessionSizeWithStorageSize(jobScript, backupDestinationInfo, replicationDestinaiton, session, jobID, freeStorageGroup) == false)
			{
				log.error("Session Size > storage Size, replication Exit! ");

				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CHECK_DESTINATIONSIZE_ERROR,
						new String[] { session.getSessionName(), "" });
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
				return HACommon.REPLICATE_FAILURE;
			}
			//fix issue 19437678, check replication destination's space before do convert -end	
												
			HyperVRepParameterModel prams = createHyperVTransParams(
					backupDestinationInfo, jobScript, jobID, session);
			if(isSmartCopy){
					addSmartCopyInfo(prams,isSmartCopy,startSession,endSession);
			}
			if (prams == null) {
				log.error("cannot create replication script");
					
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILED_CREATE_JOBSCRIPT);
					
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
					
				return HACommon.REPLICATE_FAILURE;
			}
				
			if(toTransferBackupSize > 0)
				toTransferBackupSize -= session.getSessionDataSize();
				
			synchronized (jobMonitor) {
					
				nextSessions.remove(0);	
				jobMonitor.setToRepSessions(nextSessions.toArray(new String[0]));
				jobMonitor.setTotalSessionNumbers(sessions.length);
				jobMonitor.setToRepSessionsSize(toTransferBackupSize);
				jobMonitor.setRepStartTime(System.currentTimeMillis());
				jobMonitor.setRepStartNanoTime(System.nanoTime());
				jobMonitor.setRepTotalSize(1);
				//jobMonitor.setRepTransedSize(0);
					
			}
			{
				String lastConvertedSession = ConversionHistoryUtil.getDefaultConversionHistory()
						.getLastConvertedSession(jobScript.getAFGuid(),root.getVmuuid(), backupDestinationInfo.getBackupDestination());
				if (lastConvertedSession != null && session.getSessionName().compareToIgnoreCase(lastConvertedSession) > 0) {
					nativeFacade.DetectAndRemoveObsolteBitmap(backupDestinationInfo.getBackupDestination(),
							lastConvertedSession, session.getSessionName());
				}
			}
				
			setJobMonitorSnapshotCount(jobScript.getAFGuid());
				
			HACommon.printJobScript(prams);
				
			exitValue = CommonService.getInstance().getNativeFacade()
						.HyperVRep(prams);

			try {
				connectToRemote(backupDestinationInfo, jobScript.getAFGuid(), jobID);
			} catch (Exception e) {
				log.warn("Failed to connect to backup destination" + backupDestinationInfo.getBackupDestination(), e);
			}
				// Reset smart copy flag after first session
			isSmartCopy = false;
			setSmartCopyFlag(false);
			smartCopySessions.delete(0, smartCopySessions.length());

			if (exitValue != 0) {

				log.error("trans client exit: " + exitValue);
				String msg = "";
				
				if(exitValue != HACommon.REPLICATE_CANCEL) {
					msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAIL_COMMON_FAILURE,root.getVmname());
				}
				
				if(exitValue == HACommon.PASSWORD_FILE_DAMAGE){
					msg = ReplicationMessage.getResource(
							ReplicationMessage.REPLICATION_SESSION_PASSWORD_FILE_DAMAGE);
					
				}
				
				if(!StringUtil.isEmptyOrNull(msg)) {
					log.error("Virtual Standby job failed:" + msg);
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
					
					HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
				}
				
				onReplicationFailure(jobScript, copiedSessions);
				
				return (int)exitValue;
				
			} else {

				// Delete bitmap also
				nativeFacade.DeleteSessionBitmap(backupDestinationInfo.getBackupDestination(), session.getSessionName());
				//clear incremental complete flag
				HAService.getInstance().clearIncrementalBackupComplete(jobScript);
				
				try {
					BackupInfo backupInfo = getBackupInfo(session);
					Date backupTimeDate = BackupConverterUtil.string2Date(backupInfo.getDate() + " " + backupInfo.getTime());
					jobScript.setReplicateTime(backupTimeDate.getTime());
					// fanda03 fix 163423.  call this function just used to clear replica time. same as VMWareBasereplicationCommand line 1005
					updateRepository(root.getVmuuid(),root.getVmname(),jobScript, -1);
					
					//add this storage logic, if it has some problem, you can delete it. because if you don't save replicationJobScript, the time in that object is 0 every time
					HAService.getInstance().updateRepJobScript(jobScript);
					
				} catch (Exception e) {
					log.warn("Failed to get the backup info:"+ e.getMessage());
				}
				
				synchronized (jobMonitor) {
					if (isVirtualStandbyCancelled(jobMonitor))
						return HACommon.REPLICATE_CANCEL;
					jobMonitor
							.setRepPhase(RepJobMonitor.REP_JOB_PHASE_POST_DATA_TRANSFER);
				}
				
				if( postReplication(jobScript, copiedSessions)!=0 )
				{
					copiedSessions.clear();

					synchronized (jobMonitor) {
						if (isVirtualStandbyCancelled(jobMonitor))
							return HACommon.REPLICATE_CANCEL;
					}
					return HACommon.REPLICATE_FAILURE;
				}
				saveConversionHistoryToDatastore(backupDestinationInfo.getBackupDestination(), session);
				
				try
				{
					ConversionHistoryUtil.getDefaultConversionHistory().updateLastConversion(jobScript.getAFGuid(), 
						root.getVmuuid(), backupDestinationInfo.getBackupDestination(), session.getSessionName());
				}catch (Exception e) {
					log.error("Update conversion history error. ", e);
				}
				//update older destination folder
				try{
					
					String primaryDestFolder = replicationDestinaiton.getDiskDestinations().get(0).getStorage().getName();
					String standbyVM = replicationDestinaiton.getVirtualMachineDisplayName();
					String subRoot = standbyVM + "\\" + replicationSubRoot;
					if(!primaryDestFolder.endsWith(subRoot)){
						log.info("primaryDestFolder=" + primaryDestFolder);
						primaryDestFolder += "\\" + subRoot;
					}
					
					primaryDestFolder += "\\" + HACommon.getProductionServerNameByAFRepJobScript(jobScript);
					repository.getProductionServerRoot(afGuid).getReplicaRoot().setLastRepDest(primaryDestFolder);
					repository.saveProductionServerRoot();
					uploadRepositoryToHyperV(root.getVmuuid(), root.getVmname(), jobScript);
					
				}catch (Exception e) {}
				
				copiedSessions.clear();
				
				//spawn a thread to monitor VM
				if(!isVSBWithoutHASupport() && jobScript.isVSphereBackup()){
					log.info("Monitor the status of VM " + jobScript.getAFGuid());
					try {
						if (!jobScript.getBackupToRPS())
							VSPhereFailoverService.getInstance().monitorVM(jobScript.getAFGuid());
						else {
							log.info("BackupToRPS: Monitor VM on the HBBU backup proxy.");
							FailoverJobScript failoverScript = HAService.getInstance().getFailoverJobScript(jobScript.getAFGuid());
							HAService.getInstance().getD2DService(failoverScript).getServiceV2().monitorVMForHA(jobScript.getAFGuid());
						}
					} catch (Exception e) {
						log.error("monitor thread does not start up." + e.getMessage(),e);
					}
				}
				
				synchronized (jobMonitor) {
					if (isVirtualStandbyCancelled(jobMonitor))
						return HACommon.REPLICATE_CANCEL;
					jobMonitor
							.setRepPhase(RepJobMonitor.REP_JOB_PHASE_SESSION_END);
				}
			}
		}
		
		return HACommon.REPLICATE_SUCCESS;
	}
	
	/**
	 * this method can be used to create VM or other things needed to do before trans session
	 * @param jobScript
	 * @throws HAException 
	 */
	protected abstract int preProcess(ReplicationJobScript jobScript, BackupDestinationInfo backupDestinationInfo) throws HAException;

	protected abstract List<String> createTransCommands(String jobID,
			ReplicationJobScript jobScript, SessionInfo session);

	protected abstract HyperVRepParameterModel createHyperVTransParams(BackupDestinationInfo backupDestinationInfo,
			ReplicationJobScript jobScript, long shrmemid, SessionInfo session);

	
	protected int postReplication(ReplicationJobScript jobScript,
			List<SessionInfo> sessions) throws SOAPFaultException {

		return 0;
	}
	
	protected int onReplicationFailure(ReplicationJobScript jobScript, List<SessionInfo> sessions)
			throws SOAPFaultException {
		return 0;
	}

	private void addSmartCopyInfo(HyperVRepParameterModel model,boolean isSmartCopy,String beginSession,String endSession){
		model.setbSmartCopy(isSmartCopy);
		if(!StringUtil.isEmptyOrNull(beginSession) && !StringUtil.isEmptyOrNull(endSession)){
			model.setUlScSessBegin(Long.valueOf(beginSession.substring(1)));
			model.setUlScSessEnd(Long.valueOf(endSession.substring(1)));
		}
	}
	private HyperVRepParameterModel createParamForRepDestSubRoot(ReplicationJobScript jobScript,
			SessionInfo session, BackupDestinationInfo backupDestinationInfo) {
		ReplicationDestination dest = jobScript.getReplicationDestination().get(0);
		HyperVRepParameterModel re = null;

		ARCFlashStorage arcflash = (ARCFlashStorage) dest;
		re = new HyperVRepParameterModel();

		ArrayList<SourceItemModel> pSrcItemList = new ArrayList<SourceItemModel>();

		SourceItemModel sim = new SourceItemModel();

		sim.setPwszPath(session.getSessionFolder());
		sim.setPwszSFPassword(backupDestinationInfo.getNetConnPwd());
		sim.setPwszSFUsername(backupDestinationInfo.getNetConnUserName());

		// construct FileItemModel begin
		List<DiskDestination> diskDest = dest.getDiskDestinations();
		List<FileItemModel> fileItems = new ArrayList<FileItemModel>();
		Set<String> pathSet = new HashSet<String>();
		for (DiskDestination diskDestination : diskDest) {
			pathSet.add(diskDestination.getStorage().getName());
		}

		Iterator<String> it = pathSet.iterator();
		FileItemModel tmp = null;
		String fileDest = null;
		while (it.hasNext()) {
			tmp = new FileItemModel();
			fileDest = it.next();
			if(fileDest.indexOf(dest.getVirtualMachineDisplayName()) == -1){
				if(!fileDest.endsWith("\\")){
					fileDest += "\\";
				}
				fileDest += dest.getVirtualMachineDisplayName();
			}
			tmp.setFileDestination(fileDest);
			fileItems.add(tmp);
		}
		// construct FilteItemModel ends.
		sim.setFiles(fileItems);

		pSrcItemList.add(sim);

		re.setpSrcItemList(pSrcItemList);
		re.setUlSrcItemCnt(1);
		re.setPwszDesHostName(arcflash.getHostName());
		re.setPwszDesPort(HAService.getInstance().getHATranServerPort(jobScript.getAFGuid()));
		re.setPwszUserName(arcflash.getUserName());
		re.setPwszPassword(arcflash.getPassword());
		
		re.setPwszProductNode(HACommon.getProductionServerNameByAFRepJobScript(jobScript));
		
		RepositoryUtil repository = RepositoryUtil.getInstance(CommonUtil.getRepositoryConfPath());
		String lastRepDest = null;
		try {
			ReplicaRoot root = repository.getProductionServerRoot(jobScript.getAFGuid()).getReplicaRoot();
			if(root != null)
				lastRepDest = root.getLastRepDest();
		} catch (HAException e1) {
			log.warn(e1.getMessage());
		}

		re.setPwszOldDesFolder(lastRepDest==null?"":lastRepDest);

		return re;
	}
	
	protected TransServerReplicaRoot getReplicaRoot(ReplicationJobScript jobScript) {
		ReplicaRoot replicaRoot = null;
		
		ProductionServerRoot prodRoot = null;
		String afguid = jobScript.getAFGuid();
		String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
		try{
			prodRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(afguid);
			if(prodRoot!=null) {
				replicaRoot = prodRoot.getReplicaRoot();
			}
		}catch (Exception e)
		{
			log.error("Failed to get the getReplicaRoot:"+e.getMessage());
		}
		if (replicaRoot == null)
			return null;
		
		if (replicaRoot instanceof TransServerReplicaRoot)
			return (TransServerReplicaRoot) replicaRoot;
		else
			return null;
	}
	
	protected boolean updateRepository(String vmuuid, String vmname, ReplicationJobScript jobScript, int vmGeneration) {
		return saveVMUUIDToRepository(vmuuid, vmname, jobScript, vmGeneration)
				&& uploadRepositoryToHyperV(vmuuid, vmname, jobScript);
	}
	
	protected boolean uploadRepositoryToHyperV(String vmuuid, String vmname,
			ReplicationJobScript jobScript) {
		String afguid = jobScript.getAFGuid();
		HeartBeatJobScript heartBeatJobScript = HAService.getInstance().getHeartBeatJobScript(afguid);
		ProductionServerRoot serverRoot = null;
		try {
			 serverRoot = RepositoryUtil.getInstance(CommonUtil.getRepositoryConfPath()).getProductionServerRoot(afguid);
		} catch (HAException e) {
			return true;
		}
		if (serverRoot == null) {
			return true;
		}
		WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);
		try {
			String remoteFile = HAService.getInstance().generateRemoteRepositoryPath(vmname, jobScript);
			long ret = client.getServiceV2().uploadProductionServerRoot(serverRoot, remoteFile); 
			if (ret == 0)
				return true;
			log.error(String.format("Fail to upload repository.xml to hyper-V server, return code: %d. ", ret));
			
		} catch (Exception e) {
			log.error("Fail to upload repository.xml to Hyper-V server.", e);
		}
		return false;
	}
	
	
	protected boolean saveVMUUIDToRepository(String vmuuid,String vmname,ReplicationJobScript jobScript, int vmGeneration){
		
		ProductionServerRoot prodRoot = null;
		String afguid = jobScript.getAFGuid();
		String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
		try{
			prodRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(afguid);
		}catch (Exception e) {}
		
		try{

			if(prodRoot == null){
				prodRoot = new ProductionServerRoot();
			}
			
			prodRoot.setProductionServerAFUID(afguid);
			prodRoot.setProductionServerHostname(getHostNameByUUId(afguid));
			
			TransServerReplicaRoot transRoot = null;
			ReplicaRoot root = prodRoot.getReplicaRoot();
			if(root != null) {
				if(root instanceof TransServerReplicaRoot) {
					transRoot = (TransServerReplicaRoot)root;
				}
				else {
					return false;
				}
			}
			else {
				transRoot = new TransServerReplicaRoot();
			}

			transRoot.setVmuuid(vmuuid);
			transRoot.setVmname(vmname);
			if (vmGeneration != -1)
				transRoot.SetVmGeneration(vmGeneration);
			long replicateTime = jobScript.getReplicateTime();
			transRoot.setReplicaTime(replicateTime);
			if(replicateTime > 0)
				prodRoot.clearMostRecentReplic();
			List<DiskDestination> diskDestinations = jobScript.getReplicationDestination().get(0).getDiskDestinations();
			transRoot.getDiskDestinations().clear();
			for (DiskDestination diskDestination : diskDestinations) {
				transRoot.getDiskDestinations().add(diskDestination);
			}
			prodRoot.setReplicaRoot(transRoot);
		
			RepositoryUtil.getInstance(xml).saveProductionServerRoot(prodRoot);
			
			return true;
			
		}catch(Exception e){
			
			log.error("Failed to save repository.xml!!!!");
			return false;
		}
	}

	private HashMap<String, DiskInfo> getAllStorageFreeSize(ReplicationDestination dest, String afguid){
		HashMap<String, DiskInfo> storageFreeSizeGroup = new HashMap<String, DiskInfo>();
		
		for (DiskDestination destination : dest.getDiskDestinations()) {
			VMStorage vmStorage = destination.getStorage();
			String storageName = vmStorage.getName();
//			String diskName = storageName.substring(0, (storageName.indexOf(":") + 1));
			
			if (!storageFreeSizeGroup.containsKey(storageName)) {
				
				DiskInfo storageSize = HAService.getInstance().GetMonitorDiskFreeSize(afguid, storageName);
				if(storageSize!=null)
					storageFreeSizeGroup.put(storageName, storageSize);
			}
			
		}
		return storageFreeSizeGroup;
	}
	
	protected int checkReplicationSubRoot(ReplicationJobScript jobScript){
		String afGuid = jobScript.getAFGuid();
		RepositoryUtil repository = null;
		String subRoot = replicationSubRoot;
	
		try {
			//to avoid destination conflict,program will check whether there is name conflict in 
			//replication destination.A sub root will be created for the destination.
			//This sub root will be appended to the destination. If the sub root already in the destination.
			//There replace it with the new sub root.
			String xml = CommonUtil.getRepositoryConfPath();
			repository = RepositoryUtil.getInstance(xml);
			
			ReplicationDestination  replicationDestinaiton = jobScript.getReplicationDestination().get(0);
			String repositorySubRoot = repository.getProductionServerRoot(afGuid).getReplicaRoot().getRepSubRoot();
			String standbyVMName = replicationDestinaiton.getVirtualMachineDisplayName();
			
			if(subRoot != null){
				
				String oldSubRoot = repositorySubRoot;
				if(StringUtil.isEmptyOrNull(oldSubRoot)){
					oldSubRoot = "\\";
				}else{
					oldSubRoot += "\\";
				}
					
				List<DiskDestination> diskDestinations = replicationDestinaiton.getDiskDestinations();
				String tmpSubRoot = standbyVMName +"\\" +subRoot;
				for (DiskDestination diskDestination : diskDestinations) {
					
					String tmp = diskDestination.getStorage().getName();
					if(!tmp.endsWith("\\")){
						tmp += "\\";
					}
					if(tmp.endsWith(tmpSubRoot+"\\")){
						continue;
					}
					
					if(!(tmpSubRoot).equals(oldSubRoot)){
						if(tmp.endsWith(oldSubRoot)){
							tmp = tmp.substring(0,tmp.length() - oldSubRoot.length());
							if(!tmp.endsWith("\\")){
								tmp += "\\";
							}
						}
					}
					
					tmp += tmpSubRoot;
					
					diskDestination.getStorage().setName(tmp);
					
				}
				
				repository.getProductionServerRoot(afGuid).getReplicaRoot().setRepSubRoot(tmpSubRoot);
				repository.saveProductionServerRoot();
				
			}
			
		} catch (HAException e) {
			log.error(e.getMessage(),e);
			return 1;
			
		} catch (Exception e) {
			log.error("Failed to generate subroot.",e);
			return 1;
		}
		
		return 0;
	}
	
}

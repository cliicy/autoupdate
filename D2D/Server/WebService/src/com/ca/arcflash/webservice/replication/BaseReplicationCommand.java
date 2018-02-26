package com.ca.arcflash.webservice.replication;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.MSPManualConversionConstants;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.modelmanager.RepositoryUtil;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.failover.model.ADRConfigureFactory;
import com.ca.arcflash.failover.model.ADRConfigureUtil;
import com.ca.arcflash.failover.model.BackupInfo;
import com.ca.arcflash.failover.model.BackupInfoFactory;
import com.ca.arcflash.ha.event.VCMEvent;
import com.ca.arcflash.ha.event.VCMEventManager;
import com.ca.arcflash.ha.event.VCMEventType;
import com.ca.arcflash.ha.event.VCMVMType;
import com.ca.arcflash.ha.model.DiskInfo;
import com.ca.arcflash.ha.model.ProductionServerRoot;
import com.ca.arcflash.ha.model.VCMSavePolicyWarning;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.ha.model.internal.HAException;
import com.ca.arcflash.ha.modelWebService.MonitorWebServiceErrorCode;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.ha.utils.SessionPasswordCheckStatus;
import com.ca.arcflash.ha.utils.VCMPolicyUtils;
import com.ca.arcflash.ha.utils.VirtualConversionEmailAlertUtil;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.ha.webservice.MonitorWebClientManager;
import com.ca.arcflash.jni.common.JJobHistory;
import com.ca.arcflash.job.failover.FailoverJob;
import com.ca.arcflash.jobqueue.JobQueueFactory;
import com.ca.arcflash.jobqueue.encrypt.Base64;
import com.ca.arcflash.jobscript.alert.AlertType;
import com.ca.arcflash.jobscript.base.GenerateType;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ARCFlashStorage;
import com.ca.arcflash.jobscript.replication.DiskDestination;
import com.ca.arcflash.jobscript.replication.DiskModel;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.jobscript.replication.ReplicationCommand;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMStorage;
import com.ca.arcflash.jobscript.replication.VMwareESXStorage;
import com.ca.arcflash.jobscript.replication.VMwareVirtualCenterStorage;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.service.common.WebServiceErrorMessages;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.common.LicenseCheckException;
import com.ca.arcflash.webservice.common.LicenseCheckManager;
import com.ca.arcflash.webservice.common.VCMMachineInfo;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.data.MachineDetail;
import com.ca.arcflash.webservice.data.MachineType;
import com.ca.arcflash.webservice.data.RPSDataStoreInfo;
import com.ca.arcflash.webservice.data.backup.Account;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupStatus;
import com.ca.arcflash.webservice.data.backup.RpsPolicy4D2D;
import com.ca.arcflash.webservice.data.edge.datasync.vcm.VCMJobReport;
import com.ca.arcflash.webservice.data.restore.BackupD2D;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.d2dstatus.D2DStatusServiceImpl;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.edge.datasync.job.ConversionJobSyncMonitor;
import com.ca.arcflash.webservice.edge.license.LICENSEDSTATUS;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyCheckStatus;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyQueryStatus;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacade.RHAScenarioState;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.RemoteVCMSessionMonitor;
import com.ca.arcflash.webservice.service.AbstractMergeService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.MergeService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereMergeService;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.rps.SettingsService;

public abstract class BaseReplicationCommand extends ReplicationCommand {
	private static final Logger log = Logger
			.getLogger(BaseReplicationCommand.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 5942152832171306659L;

	// Smart Copy
	// COPIED: The sessions that resident on ESX Server/HyperV side.
	// MAX: The maximum snapshot count supported by VCM.
	// NEW: Sessions on Monitee that haven’t copied to destination.
	// FULL: Full backup session
	public final static int VCM_SMART_COPY_COPIED_NEW_LESS_MAX = 0;
	public final static int VCM_SMART_COPY_COPIED_NEW_EQUALS_MAX = 1;
	public final static int VCM_SMART_COPY_NEW_STARTS_FULL = 2;
	public final static int VCM_SMART_COPY_NEW_LESS_MAX = 3;
	public final static int VCM_SMART_COPY_NEW_EQUALS_MAX = 4;
	public final static int VCM_SMART_COPY_NEW_LARGER_MAX = 5;
	public final static int VCM_SMART_COPY_UNKNOWN = 6;
	
	private static int maxSnapshotCount = 0;
	
	private int smartCopyMethod = VCM_SMART_COPY_UNKNOWN;
	private boolean smartCopyFlag; // Whether Smart Copy needed
	private String smartCopySynthetizeSession; // Which session need do
														// Smart Copy
	private String smartCopySynthetizeStart; // Start from which session
	
	//If system volume and boot volume are on different disks,print acvitity logs
	boolean LogSysBootOnDiffDisk = false;
		
	protected void setMaxSnapshotCount(int count) {
		maxSnapshotCount = count;
	}

	protected int getMaxSnapshotCount() {
		return maxSnapshotCount;
	}

	private void setSmartCopyMethod(int method) {
		smartCopyMethod = method;
	}

	public int getSmartCopyMethod() {
		return smartCopyMethod;
	}

	protected void setSmartCopyFlag(boolean flag) {
		smartCopyFlag = flag;
	}

	private void setSmartCopySynthetizeSession(String sessionName) {
		smartCopySynthetizeSession = sessionName;
	}

	private void setSmartCopySynthetizeStart(String sessionName) {
		smartCopySynthetizeStart = sessionName;
	}

	public boolean getSmartCopyFlag() {
		return smartCopyFlag;
	}

	public String getSmartCopySynthetizeSession() {
		return smartCopySynthetizeSession;
	}

	public String getSmartCopySynthetizeStart() {
		return smartCopySynthetizeStart;
	}

	// Smart Copy

	/**
	 * Get the session GUIDs from replication destination. 1. for VM related
	 * replication desti, get it from VMSnapshotsInfo and snapshots of the VM 2.
	 * for sharedfolder desti, get it from replication destination chain Return
	 * an array of GUIDs
	 * 
	 * @return
	 */
	abstract String[] getGuidsOfReplicatedSessions(
			ReplicationJobScript jobScript) throws Exception;

	abstract protected int doReplication(ReplicationJobScript jobScript,
			ReplicationDestination replicationDestinaiton, BackupDestinationInfo backupDestinationInfo);

	private int generateType = GenerateType.Default;
	
	public void setGenerateType( int generateType )
	{
		this.generateType = generateType;
	}
	
	public boolean isVSBWithoutHASupport()
	{
		return (this.generateType == GenerateType.MSPManualConversion || this.generateType == GenerateType.NoHASupport);
	}
	
	public boolean isSessionPasswordNeeded() {
		return this.generateType == GenerateType.MSPManualConversion;
	}
	
	
	private void resumeMergeJob(ReplicationJobScript jobScript) {
		try {
			String uuid = jobScript.getAFGuid();
			if (!jobScript.getBackupToRPS() && !isVSBWithoutHASupport()) {
				if(HACommon.isTargetPhysicalMachine(uuid)) {
					MergeService.getInstance().jobEnd(this);
					MergeService.getInstance().resumeMerge(AbstractMergeService.MergeEvent.OTHER_JOB_START);
				}else {
					VSphereMergeService.getInstance().jobEnd(uuid, this);
					VSphereMergeService.getInstance().resumeVMMerge(
							AbstractMergeService.MergeEvent.OTHER_JOB_START, uuid);
				}
			}
		}catch(Throwable e) {
			log.error("Failed to resume merge job: " + e.getMessage(), e);
		}
		
	}
	
	private void pauseMergeJob(ReplicationJobScript jobScript) {
		try {
			String afGUID = jobScript.getAFGuid();
			if (!jobScript.getBackupToRPS() && !isVSBWithoutHASupport()) {
				if(HACommon.isTargetPhysicalMachine(afGUID)) {
					MergeService.getInstance().pauseMerge(
							AbstractMergeService.MergeEvent.OTHER_JOB_START, null, this);
				}else {
					VSphereMergeService.getInstance().pauseMerge(
							AbstractMergeService.MergeEvent.OTHER_JOB_START, 
							afGUID,
							null,
							this);
				}
			}

		}catch(Throwable e){
			log.error("Failed to pause merge: " + e.getMessage(), e);
		}
	}
	//fanda03 fix 144269
	private String takeVSSSnapShot(  NativeFacade nativeFacade, ReplicationJobScript jobScript, long jobID, BackupDestinationInfo backupDestinationInfo){
		try
		{
			BaseReplicationJobContext jobContext = this.getJobContext( jobID );
			jobContext.setManualConversionUtility( new ManualConversionUtility( nativeFacade ) );
			jobContext.getManualConversionUtility().prepareForManualConversion( jobScript.getAFGuid(), backupDestinationInfo );
			return null;
		}
		catch (Exception e)
		{
			String errorDetails = "";
			String errorDetailsMsgKey = "";
			
			if ((e instanceof ManualConversionUtility.CreateVssManagerException) ||
				(e instanceof ManualConversionUtility.CreateSnapshotSetException))
			{
				errorDetails = "Failed to create VSS snapshot.";
				errorDetailsMsgKey = ReplicationMessage.REPLICATION_FAILTO_CREATEVSSSNAPSHOT;
			}
			else if (e instanceof ManualConversionUtility.GetSessionVolumesException)
			{
				errorDetails = "Failed to get session volumes from VSS snapshot.";
				errorDetailsMsgKey = ReplicationMessage.REPLICATION_FAILTO_GETSESSIONVOLUMESFROMVSSSNAPSHOT;
			}
			else // unknown reason
			{
				errorDetails = "Unknown reason.";
				errorDetailsMsgKey = ReplicationMessage.REPLICATION_FAILED_WITH_UNKNONWREASON;
			}
			
			log.error( "Error preparing for manual conversion. " + errorDetails, e );
			
			String detailsMessage = ReplicationMessage.getResource( errorDetailsMsgKey );
			String message = ReplicationMessage.getResource(
				ReplicationMessage.REPLICATION_FAILTO_PREPAREFORMANUALCONVERSION, detailsMessage );
			
//			HACommon.addActivityLogByAFGuid(
//				Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL,
//				new String[] { message, "", "", "", "" },
//				jobScript.getAFGuid() );
			
			return message;
		}
	}
	private boolean checkRHAScenario( NativeFacade nativeFacade, ReplicationJobScript jobScript, long jobID ) {
		try
		{
			BaseReplicationJobContext jobContext = this.getJobContext( jobID );
			if (jobContext == null)
			{
				log.error( "Job context is null. Job ID: " + jobID );
				throw new Exception();
			}
			
			boolean ret = true;
			ManualConversionUtility manualConversionUtility = jobContext.getManualConversionUtility();
			RHAScenarioState scenarioState = manualConversionUtility.getRHAScenarioStateByNode( jobScript.getAFGuid() );
			if ((scenarioState == RHAScenarioState.Sync) || (scenarioState == RHAScenarioState.Unknown))
			{
				log.warn( "RHA scenario is synchronizing or its state is sync or unknown. The conversion job will be cancelled. " +
					"Scenario State: " + scenarioState );
				
				RemoteVCMSessionMonitor.getInstance().setHasPendingJobs( jobScript.getAFGuid(), true );
				
				String messageKey;
				
				if (scenarioState == RHAScenarioState.Sync)
					messageKey = ReplicationMessage.REPLICATION_CANCELLED_DUETO_RHASYNC;
				else // RHAScenarioState.Unknown
					messageKey = ReplicationMessage.REPLICATION_CANCELLED_DUETO_UNKNOWNRHASTATE;
				
				String message = ReplicationMessage.getResource( messageKey );
				
				HACommon.addActivityLogByAFGuid(
					Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL,
					new String[] { message, "", "", "", "" },
					jobScript.getAFGuid() );
				ret = false;
			}
			return ret; 
		}
		catch (Exception e)
		{
			log.warn( "Error getting RHA scenario state. The conversion job will be cancelled." );
			
			String message = ReplicationMessage.getResource(
				ReplicationMessage.REPLICATION_CANCELLED_DUETO_GETTINGRHASTATEFAILED );
			
			HACommon.addActivityLogByAFGuid(
				Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL,
				new String[] { message, "", "", "", "" },
				jobScript.getAFGuid() );
			return false;
		}
	}
	private int CreateSessionBitmap(ReplicationJobScript jobScript, long jobID, String srcSessionRoot, String destSessionDest, NativeFacade nativeFacade){
		
		if (ManualConversionUtility.isVSBWithoutHASupport(jobScript) && !jobScript.getBackupToRPS()) {
			log.info("Skip create session bitmap for remote VSB with RHA integrated.");
			return 0;
		}
		
		int ret = -1;
		try {
			ret = nativeFacade.CreateSessionBitmap( srcSessionRoot, destSessionDest, jobScript.getBackupDestType() );
			log.info("Skip virtual standby job, and create session bitmap returns [" + ret + "]");
		} catch (Exception e) {
			log.error("Skip virtual standby job, Exception occurred while creating session bitmap:" + e.getMessage(), e);
			bitMapErrorActivityLog(  jobID,  jobScript	);
		}
		
		return ret;
	}
	private void bitMapErrorActivityLog( long jobID, ReplicationJobScript jobScript	){
		String bitMapError = ReplicationMessage.getResource( ReplicationMessage.REPLICATION_FAILED_CREATE_BITMAP );
		HACommon.addActivityLogByAFGuid(
				Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL,
				new String[] { bitMapError, "", "", "", "" },
				jobScript.getAFGuid() );
	}
	
	private void cleanVSSSnapShot( long jobID )
	{
		BaseReplicationJobContext jobContext = this.getJobContext( jobID );
		if (jobContext != null)
		{
			try 
			{
				if (isVSBWithoutHASupport() && jobContext.getManualConversionUtility() != null)
					jobContext.getManualConversionUtility().cleanUpForManualConversion();
			}
			catch (Exception e)
			{
				log.error( "Error cleaning up for manual conversion.", e );
			}
			
			jobContext.setManualConversionUtility( null );
		}
		else // job context is null
		{
			log.error( "Job context is null. Job ID: " + jobID );
		}
	}
	private boolean vcmPreProcess( BackupDestinationInfo backupDestinationInfo, NativeFacade nativeFacade  , ReplicationJobScript jobScript, long jobID ){
		//prepare vcm; connect to remote; pause merge job, create vss snapshot
		try {
			connectToRemote(backupDestinationInfo,jobScript.getAFGuid(),jobID);
		} catch (Exception e1) {
			log.error("Failed to connect to remote configuration destination",e1);
			log.error("RemotePath: " + backupDestinationInfo.getBackupDestination());
			return false;
		}
		pauseMergeJob(jobScript);
		return true;
		//end prepare;
	}
	
	private Map<Long, BaseReplicationJobContext> jobContextMap =
		new HashMap<Long, BaseReplicationJobContext>();
	
	private void saveJobContext( long jobId, BaseReplicationJobContext jobContext )
	{
		synchronized (jobContextMap)
		{
			this.jobContextMap.put( jobId, jobContext );
		}
	}
	
	private void removeJobContext( long jobId )
	{
		synchronized (jobContextMap)
		{
			this.jobContextMap.remove( jobId );
		}
	}
	
	protected BaseReplicationJobContext getJobContext( long jobId )
	{
		synchronized (jobContextMap)
		{
			return this.jobContextMap.get( jobId );
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	// If subclasses need some specific variables in job context, they should
	// define new job context class that derived from BaseReplicationJobContext,
	// and then override this method to return specific job context object.
	//
	// Pang, Bo (panbo01)
	// 2013-02-01
	
	protected BaseReplicationJobContext createJobContext()
	{
		return new BaseReplicationJobContext();
	}
	
	protected boolean isVirtualStandbyPaused( ReplicationJobScript jobScript )
	{
		return !jobScript.getAutoReplicate() || jobScript.getIsPlanPaused();
	}
	
	protected boolean isVirtualStandbyCancelled(RepJobMonitor jobMonitor) {
		if (jobMonitor.isCurrentJobCancelled()) {

			log.info("The conversion job has been cancelled.");
			return true;
		}
		return false;
	}
	protected void updateConversionJobHistory(NativeFacade nativeFacade, ReplicationJobScript jobScript, long jobID) {
		log.info("Adding conversion job history.");
		
		try {
			JJobHistory jobHistory = new JJobHistory();
			jobHistory.setJobId(jobID);
			jobHistory.setJobType(Constants.AF_JOBTYPE_CONVERSION);
			jobHistory.setDatastoreUUID(jobScript.getDataStoreUUID());
			
			RPSDataStoreInfo rpsDataStoreInfo = getRpsDataStoreInfo(jobScript, jobScript.getDataStoreUUID());
			jobHistory.setDatastoreVersion(rpsDataStoreInfo.getVersion());
			jobHistory.setTargetUUID(rpsDataStoreInfo.getRpsServerId());
			
			jobHistory.setPlanUUID(jobScript.getPlanUUID());

			if(!HACommon.isTargetPhysicalMachine(jobScript.getAFGuid())) {
				jobHistory.setJobDisposeNodeUUID(jobScript.getAFGuid());
				jobHistory.setJobDisposeNode(jobScript.getAgentNodeName());				
			} 
			nativeFacade.updateJobHistory( jobHistory );			
		} catch (Exception e) {
			log.error("Fail to update conversion job history.", e);
		}
	}
	
	protected RPSDataStoreInfo getRpsDataStoreInfo(ReplicationJobScript script, String dataStoreUUID ){
		if(script == null)
			return new RPSDataStoreInfo();
		
		if (!script.getBackupToRPS())
			return new RPSDataStoreInfo();
		
		boolean httpProtocol = script.getRpsProtocol() == 1;
		RpsHost host = new RpsHost(script.getRpsHostName(), script.getRpsPort(), httpProtocol,
				script.getRpsUserName(), WSJNI.AFDecryptString(script.getRpsPassword()));
		return SettingsService.instance().getRpsDataStoreInfo(host, dataStoreUUID);
	}	
	
	@Override
	public int executeReplicate(String id, ReplicationJobScript jobScript) {
		// - Check If There Is Already A Job Running -
		Lock jobLock = CommonService.getInstance().getRepJobLock(jobScript.getAFGuid());
		boolean canRun = jobLock.tryLock();
		if (!canRun) {

			Date date1 = new Date();
			
		  	SimpleDateFormat formatter = new SimpleDateFormat(CommonNativeInstance.getICommonNative().getDateTimeFormat().getTimeDateFormat(),DataFormatUtil.getDateFormatLocale());
			String dateString = formatter.format(date1);
			
			String msg = ReplicationMessage.getResource(ReplicationMessage.SKIP_ONE_REPLICAION, dateString);
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, -1, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "", "",
							"", "" }, jobScript.getAFGuid());
			
			setPendingJob(jobScript);
			return HACommon.REPLICATE_SKIPPED;
		}
		
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		long jobID = -1;
		try{
			jobID = nativeFacade.getJobID();
		}catch (Exception e){
			log.error( "Error retreiving new job ID.", e );
		}
		
		RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(jobScript.getAFGuid());
		synchronized (jobMonitor) {
			jobMonitor.clear();
			jobMonitor.setRepPhase(RepJobMonitor.REP_JOB_PHASE_START);
			jobMonitor.setRepJobStartTime(System.currentTimeMillis());
			jobMonitor.setCurrentJobCancelled(false);
			jobMonitor.setRepJobStatus(JobStatus.JOBSTATUS_ACTIVE);
			jobMonitor.setId(jobID);
		}	
		
		log.info("Start conversion job, job ID = " + jobID);

		updateConversionJobHistory(nativeFacade, jobScript, jobID);

		String msg = ReplicationMessage.getResource(
				"AFRES_AFREPC_"  + ReplicationMessage.AFRES_AFREPC_JOB_START);
		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
				new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
		BaseReplicationJobContext jobContext = this.createJobContext();
		saveJobContext( jobID, jobContext );

		Date startTime = new Date();
		int rc = HACommon.REPLICATE_FAILURE;
		try {
			// sync conversion job info to edge vcm
			// syncConversionJobInfo(jobScript.getAFGuid(), jobMonitor);

			rc = executeReplicationJob(jobScript, nativeFacade, jobMonitor);
		} catch (Throwable e) {
			log.error("Failed replication." + e.getMessage(),e);
		} finally {
			
			updateVCMStatusAndEvent(nativeFacade, jobScript, startTime, jobMonitor, rc, jobID);

			synchronized (jobMonitor) {
				if (rc == HACommon.REPLICATE_SUCCESS  || rc == HACommon.REPLICATE_HOTADD_SUCCESS || rc == HACommon.REPLICATE_NOSESSIONS)
					jobMonitor.setRepJobStatus(JobStatus.JOBSTATUS_FINISHED);
				else if (rc == HACommon.REPLICATE_CANCEL)
					jobMonitor.setRepJobStatus(JobStatus.JOBSTATUS_CANCELLED);
				else if (rc == HACommon.REPLICATE_SKIPPED)
					jobMonitor.setRepJobStatus(JobStatus.JOBSTATUS_SKIPPED);
				else
					jobMonitor.setRepJobStatus(JobStatus.JOBSTATUS_FAILED);

				jobMonitor.setRepPhase(RepJobMonitor.REP_JOB_PHASE_EXIT);
			}

			HAService.getInstance().reportJobMonitor(jobScript.getAFGuid());

			HAService.getInstance().syncVCMStatus2Monitor(jobScript.getAFGuid());
			
			removeJobContext( jobID );

			// << [lijwe02]
			try {
				Thread.sleep(3 * 1000); // waiting for the sync status thread.
			} catch (InterruptedException e) {
				log.info("InterruptedException in baseReplicationCommand!", e);
			}

			synchronized (jobMonitor) {
				jobMonitor.clear();
			}
			
			jobLock.unlock();
		}
		
		// [lijwe02] update vcm policy setting fix unassign policy when job is running >>
		log.info("Replicate job finished, try to refersh vcm status.");
		try {
			int state=HAService.getInstance().RefreshBackupConfigSettingWithEdge(jobScript.getAFGuid(), jobScript.getPlanUUID());
			log.info("VCM RefreshBackupConfigSettingWithEdge "+jobScript.getAFGuid()+" is "+state);
		} catch (Exception e) {
			log.error("Error on refresh VCM status.", e);
		}
		return rc;
	}

	public int executeReplicationJob(ReplicationJobScript jobScript, NativeFacade nativeFacade, RepJobMonitor jobMonitor) {
		long jobID = jobMonitor.getId();
		String srcSessionRoot = "";
		int licenseResult = -1;

		activityLogForRepDest(jobScript, jobID);

		BaseReplicationJobContext jobContext = getJobContext(jobID);

		// - Get Backup Configuration and Session Root -
		BackupDestinationInfo backupDestinationInfo = null;
		try{
			backupDestinationInfo = getBackupDestinationInfo(jobScript, true, jobID);
		}catch (Exception e){
			log.error( "Error getting backup configuration." + e.getMessage(), e );
		}
		if (backupDestinationInfo == null || backupDestinationInfo.getBackupDestination() == null) {
			String msg = ReplicationMessage
					.getResource(ReplicationMessage.REPLICATION_NO_BACKUP_CONF);
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
			return HACommon.REPLICATE_FAILURE;
		}

		// - Update Session Password for RVCM -
		if (isSessionPasswordNeeded()) {
			if (!jobScript.getBackupToRPS())
				RemoteVCMSessionMonitor.getInstance().onConversionStarted(jobScript.getAFGuid());
			
			if (!validateSessionPasswords(jobScript, jobID, backupDestinationInfo)) {
				log.error("Failed to validate session password.");
				return HACommon.REPLICATE_FAILURE;
			}
		}
		
		srcSessionRoot = backupDestinationInfo.getBackupDestination();
		
		// - Take VSS Snapshot for RVCM -
		boolean snapshotstate=true;
		String snapshotmsg="";
		if (isVSBWithoutHASupport() && !jobScript.getBackupToRPS())
		{
			snapshotmsg=takeVSSSnapShot( nativeFacade, jobScript, jobID, backupDestinationInfo );
			snapshotstate=(snapshotmsg==null);
			
			if(snapshotstate){
				try
				{
					srcSessionRoot = jobContext.getManualConversionUtility().translateToSnapshotPath(
							backupDestinationInfo.getBackupDestination());
				}
				catch (Exception e)
				{
					log.error( "Error translating backup destination to the one in VSS snapshot.", e );
				}
			}
		}
		
		// - Try to register VDDK driver in case it is NOT installed when upgrade -
		try {
			checkVDDKDriver(jobScript);
		} catch (ServiceException e) {
			log.error(e);
			String msg = CommonService.getInstance().getServiceError(e.getErrorCode(), e.getMultipleArguments());
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
			msg = ReplicationMessage.getResource(
					ReplicationMessage.AFRES_AFREPC_PREX + ReplicationMessage.AFRES_AFREPC_JOB_FAILED);
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
			
			return HACommon.REPLICATE_FAILURE;
		} catch (Exception e) {
			log.error(e);
			String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_RET_MSG, e.getMessage());
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg, "", "", "", "" }, jobScript.getAFGuid());
		}
		
		// - Begin the Job -
		int iret = 0;
		try
		{
			if(!snapshotstate){
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { snapshotmsg, "", "",
						"", "" }, jobScript.getAFGuid());
				
				CreateSessionBitmap(jobScript, jobID, srcSessionRoot, backupDestinationInfo.getBackupDestination(), nativeFacade);

				String msg1 = ReplicationMessage.getResource(
						ReplicationMessage.AFRES_AFREPC_PREX + ReplicationMessage.AFRES_AFREPC_JOB_FAILED);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg1,"", "", "", "" }, jobScript.getAFGuid());

				HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg1);
				return HACommon.REPLICATE_FAILURE;
			}
			
			String msg = "";
			ReplicationDestination replicationDestinaiton = HAService.getInstance()
					.getReplicationDestinaiton(jobScript);

			// - Prepare for Conversion Job -
			if (isVSBWithoutHASupport() && !jobScript.getBackupToRPS() && !checkRHAScenario( nativeFacade, jobScript, jobID ))
				return HACommon.REPLICATE_SKIPPED;
			
			if( !vcmPreProcess( backupDestinationInfo,  nativeFacade  ,  jobScript,  jobID ) ){
				//add the activity log for issue 161509 <zhaji22>
				String message = ReplicationMessage.getResource(
						ReplicationMessage.AFRES_AFREPC_PREX + ReplicationMessage.AFRES_AFREPC_JOB_FAILED);
				HACommon.addActivityLogByAFGuid(
					Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL,
					new String[] { message, "", "", "", "" },
					jobScript.getAFGuid() );
				//end
				return HACommon.REPLICATE_FAILURE;
			}
			
			// - Check If Virtual Standby Is Paused -
			//fanad03 fix 144269; getAutoReplicate() == false means conversion job is paused.we create bitmap and exit
//			if (isVirtualStandbyPaused( jobScript )) {
//				log.info("Automatical Virtual Conversion is PAUSED, generate Bitmap instead");
//				CreateSessionBitmap(jobScript, jobID, srcSessionRoot, backupDestinationInfo.getBackupDestination(), nativeFacade);
//				
//				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICAIION_PAUSED);
//				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
//						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
//
//				setPendingJob(jobScript);
//				return HACommon.REPLICATE_SKIPPED;
//			}

			// Check if backup session exist
			if(!isSessionExist(backupDestinationInfo.getBackupDestination())){
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICAION_NO_SESSIONS);
				HACommon.addActivityLogByAFGuid(
						Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg, "", "", "", "" },
						jobScript.getAFGuid() );
				return HACommon.REPLICATE_NOSESSIONS;
			}
			
			// - Check License -
			licenseResult = checkEmailAlertAndLicense(jobScript, jobID);
			if(licenseResult == HACommon.REPLICATE_FAILURE){
				
				CreateSessionBitmap(jobScript, jobID, srcSessionRoot, backupDestinationInfo.getBackupDestination(), nativeFacade);
				return HACommon.REPLICATE_LICENSE_FAILURE;
			}
			
			// - Validate Admin Account -
			if (!validateAdminAccount(jobScript, jobID, nativeFacade)) {
				return HACommon.REPLICATE_FAILURE;
			}
			

			if (HAService.getInstance().detectIfFailoverOngoing(jobScript.getAFGuid())) {
				log.warn("A failover is running. Skip the current Virtual Standby job.");
				msg = ReplicationMessage.getResource(ReplicationMessage.AFRES_AFREPC_SKIP_VSB_FOR_FAILOVER);			
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				CreateSessionBitmap(jobScript, jobID, srcSessionRoot, backupDestinationInfo.getBackupDestination(), nativeFacade);
				iret = HACommon.REPLICATE_SKIPPED;
			} else {
				// - Do Conversion -
				log.info("Automatical Virtual Conversion is ENABLED, go for replication");
				long log_level = Constants.AFRES_AFALOG_INFO;
				iret = doReplication(jobScript, replicationDestinaiton, backupDestinationInfo);
				
				switch (iret) {
					case HACommon.REPLICATE_SUCCESS :
					case HACommon.REPLICATE_NOSESSIONS :
						msg = ReplicationMessage.getResource("AFRES_AFREPC_" + ReplicationMessage.AFRES_AFREPC_JOB_FINISHED);
						HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionSuccess, msg);
						break;
					case HACommon.REPLICATE_SKIPPED :
						msg = ReplicationMessage.getResource("AFRES_AFREPC_" + ReplicationMessage.AFRES_AFREPC_JOB_SKIPPED);
						log_level = Constants.AFRES_AFALOG_WARNING;
						break;
					case HACommon.REPLICATE_CANCEL :
						msg = ReplicationMessage.getResource(ReplicationMessage.AFRES_AFREPC_16388);
						log_level = Constants.AFRES_AFALOG_WARNING;
						HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.CancelConversionJob, msg);
						CreateSessionBitmap(jobScript, jobID, srcSessionRoot, backupDestinationInfo.getBackupDestination(), nativeFacade);
						break;
					default:
						CreateSessionBitmap(jobScript, jobID, srcSessionRoot, backupDestinationInfo.getBackupDestination(), nativeFacade);
						msg = null;
						break;
				}
				
				if (msg != null)
					HACommon.addActivityLogByAFGuid(log_level, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			}
		} catch (Exception e) {
			
			log.error("Failed replication." + e.getMessage(),e);
			return HACommon.REPLICATE_FAILURE;
			
		} finally {
			// - Clean Up -
			resumeMergeJob(jobScript);
			cleanVSSSnapShot( jobID );
			
			HAService.getInstance().syncADRConfigureToVCM(jobScript.getAFGuid(), jobID, backupDestinationInfo);// sync adapter into to Edge
			
			if(licenseResult != -1 && licenseResult != HACommon.REPLICATE_FAILURE) {//License is ok.
				
				closeRemoteConnect(backupDestinationInfo);
	
				// whatever the replication succeed or not, the flag for merge all
				// sessions will be turned off.
				HAService.getInstance().forceNextReplicationMergeAllSessions(jobScript.getAFGuid(),false);
			}
		}
		
		return iret;
	}

	// - Try to register VDDK driver in case it is NOT installed when upgrade -
	private synchronized static void checkVDDKDriver(ReplicationJobScript jobScript) throws Exception {
		if(jobScript.getVirtualType()!= VirtualizationType.HyperV){
			log.debug("begin installing VDDK service");
			try {
				HeartBeatJobScript heartBeatJobScript = HAService.getInstance().getHeartBeatJobScript(jobScript.getAFGuid());
				StringBuilder installedVDDKMachine = new StringBuilder();
				boolean isVDDKNeedRebootMachine = VCMPolicyUtils.installVDDKService(jobScript, heartBeatJobScript, installedVDDKMachine);
				if(isVDDKNeedRebootMachine){
					log.warn("install VDDK service complete , with reboot machine required");
					throw new ServiceException(FlashServiceErrorCode.VCM_VDDK_SERVICE_REQUIRE_REBOOT, new Object[]{installedVDDKMachine.toString()});
				}else{
					log.info("install VDDk service ok, instaled machine:"+installedVDDKMachine.toString());					
				}
			} catch (Exception e) {
				log.error("install vddk service failed. ", e);
				throw e;
			}
		}
	}
	
	/**
	 * Because some reason, the job is not running, for remote vcm, we will pending this job, the job will run again
	 * when it can run
	 * 
	 * @param uuid
	 *            The uuid for the node
	 */
	public static void setPendingJob(ReplicationJobScript jobScript) {
		if (ManualConversionUtility.isVSBWithoutHASupport(jobScript) && !jobScript.getBackupToRPS()) {
			String uuid = jobScript.getAFGuid();
			// log.info("Another job is running or the job is paused for node:" + uuid);
			RemoteVCMSessionMonitor.getInstance().setHasPendingJobs(uuid, true);
		}
	}
	
	private void syncConversionJobInfo(final String uuid, final RepJobMonitor jobMonitor) {
		log.info("Start to sync conversion job to Central side.");
		
		Thread syncThread = new Thread(new Runnable(){

			@Override
			public void run() {
				while (true) {
					D2DStatusInfo d2dStatusInfo = null;
					
					if (jobMonitor.getRepPhase() == RepJobMonitor.REP_JOB_PHASE_EXIT) {
						log.info("Conversion job finished, sync d2d status to Central side.");
						d2dStatusInfo = D2DStatusServiceImpl.getInstance().getVCMStatusInfo(uuid);
					}
					
					ConversionJobSyncMonitor.getInstance().addSyncData(uuid, jobMonitor, d2dStatusInfo);
					
					if (jobMonitor.getRepPhase() == RepJobMonitor.REP_JOB_PHASE_EXIT) {
						log.info("End of sync conversion job.");
						break;
					}
					
					try {
						Thread.sleep(3000);
					} catch(InterruptedException e) {
						log.warn("InterruptedException, syncConversionJobInfo was failed", e);
					}
				}
			}
		});
		
		CommonService.getInstance().getUtilTheadPool().submit(syncThread);
	}

	private VCMJobReport getVCMJobReport(ReplicationJobScript jobScript, RepJobMonitor jobMonitor){
		
		VCMJobReport jobReport = new VCMJobReport();
		
		boolean isProxyEnabled = false;
		ReplicationDestination dest = jobScript.getReplicationDestination().get(0);
		if(dest instanceof ARCFlashStorage){
			ARCFlashStorage tmp = (ARCFlashStorage)dest;
			jobReport.setVMDestType(1);
			jobReport.setHypervisorHostname(tmp.getHostName());

			isProxyEnabled = true;
		}else if(dest instanceof VMwareESXStorage){
			VMwareESXStorage tmp = (VMwareESXStorage)dest;
			jobReport.setVMDestType(0);
			jobReport.setHypervisorHostname(tmp.getESXHostName());

			isProxyEnabled = tmp.isProxyEnabled();
		}else if(dest instanceof VMwareVirtualCenterStorage){
			VMwareVirtualCenterStorage tmp = (VMwareVirtualCenterStorage)dest;
			jobReport.setVMDestType(0);
			jobReport.setHypervisorHostname(tmp.getESXHostName()==null||tmp.getESXHostName().isEmpty()?tmp.getEsxName():tmp.getESXHostName());
			jobReport.setVCenterHostname(tmp.getVirtualCenterHostName());

			isProxyEnabled = tmp.isProxyEnabled();
		}
		
		if (isProxyEnabled) {
			HeartBeatJobScript hbJobScript = HAService.getInstance().getHeartBeatJobScript(jobScript.getAFGuid());
			if (hbJobScript != null) {
				jobReport.setMonitorHostname(hbJobScript.getHeartBeatMonitorHostName());
			}
		}
		
		jobReport.setConvertSessions(jobMonitor.getRepSessionName());
		jobReport.setPrevConvertSessions(jobMonitor.getRepPrevSessionName());
		return jobReport;
	}

	private void updateVCMStatusAndEvent(NativeFacade nativeFacade, ReplicationJobScript jobScript, Date startTime, RepJobMonitor jobMonitor, int rc, long jobID) {
		try {
			int jobStatus = JobStatus.JOBSTATUS_FINISHED;
			
			if (rc == HACommon.REPLICATE_SUCCESS  || rc == HACommon.REPLICATE_HOTADD_SUCCESS || rc == HACommon.REPLICATE_NOSESSIONS) {
				jobStatus = JobStatus.JOBSTATUS_FINISHED;
			}
			else if (rc == HACommon.REPLICATE_CANCEL) {
				saveReplicationStatus(jobScript.getAFGuid(), BackupStatus.Canceled, startTime.getTime(), jobMonitor.getRepJobElapsedTime());
				
				jobStatus = JobStatus.JOBSTATUS_CANCELLED;
			}else if(rc == HACommon.REPLICATE_SKIPPED){
				jobStatus = JobStatus.JOBSTATUS_SKIPPED;
			}else{
				saveReplicationStatus(jobScript.getAFGuid(), BackupStatus.Failed, startTime.getTime(), jobMonitor.getRepJobElapsedTime());
	
				String msg1 = ReplicationMessage.getResource(
						ReplicationMessage.AFRES_AFREPC_PREX + ReplicationMessage.AFRES_AFREPC_JOB_FAILED);
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg1,"", "", "", "" }, jobScript.getAFGuid());
				
				if (rc == HACommon.REPLICATE_LICENSE_FAILURE)
					jobStatus = JobStatus.JOBSTATUS_LICENSE_FAILED;
				else
					jobStatus = JobStatus.JOBSTATUS_FAILED;
			}

			VCMJobReport jobReport = getVCMJobReport(jobScript, jobMonitor);
			
			String extraJobInfo = null;
			try {
				String jobReportStr = CommonUtil.marshal(jobReport);
				log.info("VCMJobReport XML String: " + jobReportStr);
				
				extraJobInfo = Base64.encode(jobReportStr);
			} catch (JAXBException e) {
				log.error("Failed to marshal VCMJobReport", e);
			}

			nativeFacade.markD2DJobEnd(jobID, jobStatus, extraJobInfo);
		}catch (Exception e){
			log.error( "Fail to update VCM status and event.", e );
		}
	}
	
	int checkEmailAlertAndLicense(ReplicationJobScript jobScript, long jobID){
		
		HAService.getInstance().checkAlertEmailSetting(jobScript.getAFGuid());
		
		LICENSEDSTATUS license = null;
		String msg = null;
		try {
			license = checkLicense(jobScript.getAFGuid(), null, true);
		}catch(LicenseCheckException checkEx) {
			if(LicenseCheckException.FAIL_CONNECT_EDGE.equals(checkEx.getErrorCode())) {
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAIL_LICENSE_NOT_CONNECT_EDGE);
				
			}
			else if(LicenseCheckException.EDGE_INTERNAL_ERROR.equals(checkEx.getErrorCode())){
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAIL_LICENSE_EDGE_ERROR);
			}
			
			if(msg != null) {
				logLicenseFail(jobScript, msg, jobID);
				return HACommon.REPLICATE_FAILURE;
			}
		}
		
		if (license != LICENSEDSTATUS.TRIAL && license != LICENSEDSTATUS.VALID) {

			msg = ReplicationMessage
					.getResource(ReplicationMessage.REPLICATION_FAIL_FOR_LICENSE_FAIL);
			
			logLicenseFail(jobScript, msg, jobID);
			return HACommon.REPLICATE_FAILURE;
		} 
		
		return HACommon.REPLICATE_SUCCESS;
		
	}
	
	private void logLicenseFail(ReplicationJobScript jobScript, String msg, long jobID) {
		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "", "",
				"", "" }, jobScript.getAFGuid());
		
		String alertCauseMessage = VirtualConversionEmailAlertUtil.getAlertMessage(AlertType.LicenseFailed);
		HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, alertCauseMessage);
	}

	public static LICENSEDSTATUS checkLicense(String afGuid, MachineDetail detail, boolean needConnect) throws LicenseCheckException {
		ReplicationJobScript jobscript = HAService.getInstance().getReplicationJobScript(afGuid);
		if(jobscript == null){
			log.error("Failed to get job script , uuid:"+afGuid);
			return null;
		}
		
		if (ManualConversionUtility.isVSBOnMSP(jobscript)) {
			// In Oolong, there's no need to check VSB license on MSP side
			log.info("Skip license check for uuid: " + afGuid + " on MSP side.");
			if (detail != null) {
				detail.setHostName(jobscript.getAgentNodeName());
				detail.setMachineType(MachineType.PHYSICAL);
			}
			return LICENSEDSTATUS.VALID;
		}
		
		VCMMachineInfo vcmMachineInfo = getVCMMachineInfo(jobscript, needConnect);
		if (vcmMachineInfo == null) {
			log.error("Failed to get machine info from backup destination.");
			throw new LicenseCheckException("Failed to get machine info from backup destination.", LicenseCheckException.EDGE_INTERNAL_ERROR); 
		}

		// CPM need node name instead of hostname
		vcmMachineInfo.getMachineInfo().setHostName(jobscript.getAgentNodeName());
		vcmMachineInfo.getMachineInfo().setHostUuid(afGuid);
		
		// check license from edge. 
		LicenseCheckManager manager = LicenseCheckManager.getInstance();
		LICENSEDSTATUS licStatus = manager.checkVCMLicense(vcmMachineInfo);
		
		if (detail != null) {
			detail.setHostName(vcmMachineInfo.getMachineInfo().getHostName());
			if (vcmMachineInfo.getNodeType() == VCMMachineInfo.VCMNodeType.HBBU_VM)
				detail.setMachineType(MachineType.ESX_VM);
			else
				detail.setMachineType(MachineType.PHYSICAL);
		}
		return licStatus;
	}

//	private static MachineDetail getMachineDetailForLVCM(String afGuid) throws LicenseCheckException {
//		MachineDetail detail = null; 
//		FailoverJobScript failoverScript = HAService.getInstance().getFailoverJobScript(afGuid);
//		if(!failoverScript.isVSphereBackup()) {
//			detail = MachineDetailManager.getInstance().getMachineDetail();
//			detail.setHostName(failoverScript.getProductionServerName());
//		}
//		else{
//			detail = MachineDetailManager.getInstance().getMachineDetail(afGuid);
//			if(detail == null) {
//				detail = new MachineDetail();
//			}
//			detail.setHostName(failoverScript.getProductionServerName());
//		}
//		
//		return detail;
//	}
//	
//	private static MachineDetail getMachineDetailForRVCM(ReplicationJobScript jobscript, BackupDestinationInfo backupDestinationInfo) throws LicenseCheckException {
//		
//		String afGuid = jobscript.getAFGuid();
//		log.debug("checkLicenseForRVCM , afGUID:"+afGuid);
//		
//		MachineDetail detail = null;
//		// For BacktoRPS or RVCM , we get machine type info from backup session.
//		detail = BrowserService.getInstance().getNativeFacade().getMachineDetailFromBackupSession(backupDestinationInfo.getBackupDestination());
//		if(detail == null){
//			String hostName = null;
//			
//			if (jobscript.getBackupToRPS()) {
//				hostName = jobscript.getAgentNodeName();
//				detail = new MachineDetail();
//				detail.setHostName(hostName);
//				if (!jobscript.isVSphereBackup()) {
//					detail.setMachineType(MachineType.PHYSICAL);
//				} else {
//					detail.setHypervisorHostName(jobscript.getEsxHostnameForHBBU());
//					log.info("HBBU license, esx name: "+jobscript.getEsxHostnameForHBBU());
//				}
//			}
//			else {
//				// if can not get machine type from backup session , change to physical license
//				log.error("Failed to get machine type from backup session. use physical license");
//				VirtualMachine vm = new VirtualMachine();
//				vm.setVmInstanceUUID(afGuid);
//				VMBackupConfiguration vmBackup = null;
//				try {
//					vmBackup = VSphereService.getInstance().getVMBackupConfiguration(vm);
//					hostName = vmBackup.getBackupVM().getVmName();
//				} catch (ServiceException e) {
//					log.error(e.getMessage(), e);				
//				}
//				
//				detail = new MachineDetail();
//				detail.setHostName(hostName);
//				detail.setMachineType(MachineType.PHYSICAL);
//			}
//		}
//		
//		if (jobscript.getBackupToRPS()) {
//			if (!jobscript.isVSphereBackup()) {
//				detail.setMachineType(MachineType.PHYSICAL);
//			} else {
//				detail.setHypervisorHostName(jobscript.getEsxHostnameForHBBU());
//				log.info("HBBU license, esx name: "+jobscript.getEsxHostnameForHBBU());
//			}
//		}
//		else { // RVCM with RHA integrated
//			// If this is an ESX_VM type, we get hypervisor host name from machineDetail.xml file.
//			if(detail.getMachineType() == MachineType.HYPERV_VM || detail.getMachineType() == MachineType.ESX_VM || detail.getMachineType() == MachineType.VSPHERE_ESX_VM){
//				MachineDetail machineInfo = MachineDetailManager.getInstance().getMachineDetail(afGuid);
//				if (machineInfo == null || StringUtil.isEmptyOrNull(machineInfo.getHypervisorHostName())){
//					// if can not get hypervisor info , change to physical license
//					detail.setMachineType(MachineType.PHYSICAL);
//					log.info("change license from ESX_VM to PHYSICAL");
//				}else{
//					// this mean it has been specified from VCM [specify esx server] dialog. 
//					detail.setHypervisorHostName(machineInfo.getHypervisorHostName());
//					log.info("get esx host name for ESX_VM license , esx name:"+machineInfo.getHypervisorHostName());
//				}
//			}
//		}
//		return detail;
//	}
	
	// check if sessions exist already , so that we can check machine type for RVCM
	private static boolean isSessionExist(String dest){
		if(WSJNI.AFCheckFolderContainsBackup(dest)){
			// check if there is session folder in VStore folder.
			File file = new File(dest,"VStore");
			if(file.exists()){
				String[] dirs = file.list();
				if(dirs != null && dirs.length > 0){
					return true;
				} else {
					log.error(String.format("The VStore is empty under '%s'.", dest));
				}
			} else {
				log.error(String.format("No sub folder 'VStore' under '%s'", dest));
			}
		} else {
			log.error(String.format("The '%s' is not a backup folder.", dest));
			
		}
		return false;
	}
	
	private SessionInfo[] getSessionsForManualConversion( String afGuid,
		boolean connect, BaseReplicationJobContext jobContext, BackupDestinationInfo backupDestinationInfo ) throws ServiceException
	{
		SessionInfo[] EMPTY = new SessionInfo[0];

		if (backupDestinationInfo == null || backupDestinationInfo.getBackupDestination() == null)
			return EMPTY;
		
		String remotePath = backupDestinationInfo.getBackupDestination();
		if (StringUtil.isEmptyOrNull(remotePath)) {
			throw new ServiceException(
					"Null or empty backup configuration destination!",new Object[0]);
		}
		boolean isRemote = CommonUtil.isRemote(remotePath);

		try {

			if (!connect && isRemote) {
				long jobID = -1;
				try {
					jobID = CommonService.getInstance().getRepJobMonitorInternal(afGuid).getId();
				}
				catch (Exception e)	{
					log.warn( "No job id found for " + afGuid + " when trying to get sessions." );
				}
				try {
					connectToRemote(backupDestinationInfo,afGuid, jobID);
				} catch (Exception e) {
					String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAIL_CONNECT_BACKUP_DEST, backupDestinationInfo.getBackupDestination());
					throw new ServiceException(msg, new Object[0]);
				}
			}

			// here we get the sessions
			List<String> dests = new ArrayList<String>();

			long ret = BrowserService.getInstance().getNativeFacade()
					.GetAllBackupDestinations(remotePath, dests);
			// the dests contains the dest in the time order, from old to new
			if (ret != 0)
				return EMPTY;
			
			List<String> newDestinations = new ArrayList<String>();
			
			try
			{
				ManualConversionUtility manualConversionUtility = jobContext.getManualConversionUtility();
				for (String dest : dests)
				{
					String originalVolumeName = dest.substring( 0, 2 );
					String snapshotVolume = manualConversionUtility.getSnapshotDeviceName( originalVolumeName );
					String newDest = snapshotVolume + dest.substring( 2, dest.length() - 1 );
					newDestinations.add( newDest );
				}
			}
			catch (Exception e)
			{
				log.error( "Error traslating original volume to snapshot volume.", e );
				throw new ServiceException( "Error traslating original volume to snapshot volume.", new Object[0] );
			}
			
			return getSessionsFromSessionFolders( newDestinations );
			
		} finally {
			if (!connect && isRemote) {
				try {
					closeRemoteConnect(backupDestinationInfo);
				} catch (Exception e) {
					log.debug(e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 1.Connect to current destination 2.Return an array of sessionInfo
	 * [Path,GUID,backuptype] in sessionNumber order 3.Disconnect it
	 * 
	 * @param connect
	 *            it means if this function should deal with opening connection
	 *            and closing connection
	 * @return
	 * @throws ServiceException
	 */
	static SessionInfo[] getSessionsFromBackupDestnation(BackupDestinationInfo backupDestinationInfo) 
			throws ServiceException {
		
		SessionInfo[] EMPTY = new SessionInfo[0];

		try {
			// here we get the sessions
			List<String> dests = new ArrayList<String>();

			long ret = BrowserService.getInstance().getNativeFacade()
					.GetAllBackupDestinations(backupDestinationInfo.getBackupDestination(), dests);
			// the dests contains the dest in the time order, from old to new
			if (ret != 0)
				return EMPTY;
			
			return getSessionsFromSessionFolders( dests );
			
		} catch (Exception e) {
			log.debug(e.getMessage());
			return EMPTY;
		}
	}
	
	
	static SessionInfo[] getSessionsFromBackupDestnation(String afGuid, BackupDestinationInfo backupDestinationInfo) throws ServiceException {
		
		SessionInfo[] EMPTY = new SessionInfo[0];

		if (backupDestinationInfo == null || backupDestinationInfo.getBackupDestination() == null)
			return EMPTY;
		
		String remotePath = backupDestinationInfo.getBackupDestination();
		if (StringUtil.isEmptyOrNull(remotePath)) {
			throw new ServiceException(
					"Null or empty backup configuration destination!",new Object[0]);
		}
		
		boolean isRemote = CommonUtil.isRemote(remotePath);


		if (isRemote) {
			long jobID = -1;
			try {
				jobID = CommonService.getInstance().getRepJobMonitorInternal(afGuid).getId();
			}
			catch (Exception e)	{
				log.warn( "No job id found for " + afGuid + " when trying to get sessions." );
			}
			try {
				connectToRemote(backupDestinationInfo,afGuid, jobID);
			} catch (Exception e) {
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAIL_CONNECT_BACKUP_DEST, backupDestinationInfo.getBackupDestination());
				throw new ServiceException(msg, new Object[0]);
			}
		}

		// here we get the sessions
		List<String> dests = new ArrayList<String>();

		long ret = BrowserService.getInstance().getNativeFacade()
					.GetAllBackupDestinations(remotePath, dests);
			// the dests contains the dest in the time order, from old to new
		if (ret != 0){
			log.error("retcode="+ret);
			return EMPTY;
		}
			
		return getSessionsFromSessionFolders( dests );
			
	}
	

	private static SessionInfo[] getSessionsFromSessionFolders(
		List<String> sessionFolders ) throws ServiceException
	{
		SessionInfo[] EMPTY = new SessionInfo[0];
		List<SessionInfo> rets = new ArrayList<SessionInfo>();
		
		for (String dest : sessionFolders) {
			if (!dest.endsWith("\\"))
				dest += "\\";
			File f = new File(dest + "VStore");
			File[] listSessions = f.listFiles();
			if (listSessions == null)
				continue;
			Arrays.sort(listSessions, new Comparator<File>() {

				@Override
				public int compare(File o1, File o2) {
					return o1.getName().compareTo(o2.getName());

				}
			});

			for (File session : listSessions) {
				
				if(!checkBackupSessionConfigFile(session)){
					log.warn("backup config file is not avaliable.This session will be skipped.");
					continue;
				}
				if (!checkIndexOfBackupSessionFile(session)) {
					log.warn("Session index file is not avaliable.This session will be skipped.");
					continue;
				}
				String backupinfoXML = session.getPath() + "\\BackupInfo.XML";
				try {
					String backupContent = CommonUtil
							.readFileAsString(backupinfoXML);
					BackupInfo backupInfo = BackupInfoFactory
							.getBackupInfoFromString(backupContent);
					if (backupInfo != null
							&& backupInfo.getBackupStatus().equals(
									"Finished")) {
						SessionInfo rtem = new SessionInfo();
						rtem.setSessionGuid(backupInfo.getSessionGUID());
						rtem.setSessionName(session.getName());
						try {
							rtem.setBackupTime(BackupConverterUtil
									.string2Date(
											backupInfo.getDate() + " "
													+ backupInfo.getTime())
									.getTime());
						} catch (Exception e) {
						} // ignore time format exception
						rtem.setSessionFolder(session.getPath());
						rtem.setSessionType(backupInfo.getBackupType());

						// chefr03: UPDATE_BACKUP_INFO
						rtem.setSessionFullMachineFlag(backupInfo
								.getFullMachineFlag());
						// chefr03: UPDATE_BACKUP_INFO

						rtem.setSessionCompressType(backupInfo
								.getCompressType());
						rtem.setSessionDataSize(backupInfo
								.getTransferDataSize());
						rtem.setSessionCatalogSize(backupInfo
								.getSessionCatalogSize());
						rtem.setEncryptType(backupInfo.getEncryptType());
						rtem.setEncryptPasswordHash(backupInfo.getEncryptPasswordHash());
						rtem.setBackupID(backupInfo.getBackupID());
						rets.add(rtem);
					} else {
						log
								.debug("We run into a incomplete session, so return the previous ones!");
						return rets.toArray(EMPTY);
					}

				} catch (Exception e) {
					log.error("Failed to parse " + backupinfoXML, e);
					return rets.toArray(EMPTY);
				}
			}
		}

		return rets.toArray(EMPTY);
	}
	
	protected BackupInfo getSessionBackupInfo(File session) {
		if (session == null || !session.exists()) {
			return null;
		}
		try {
			String backupinfoXML = session.getPath() + "\\" + MSPManualConversionConstants.BACKUP_INFO_FILENAME;
			File tmp = new File(backupinfoXML);
			if (!tmp.exists()) {
				log.warn(backupinfoXML + " does not exist.This session will be skipped.");
				return null;
			}
			String backupContent = CommonUtil.readFileAsString(backupinfoXML);
			return BackupInfoFactory.getBackupInfoFromString(backupContent);
		} catch (HAException e) {
			log.error("Failed to get BackupInfo from folder:" + session.getAbsolutePath(), e);
		} catch (Exception e) {
			log.error("Failed to get BackupInfo from folder:" + session.getAbsolutePath(), e);
		}
		return null;
	}

	void addActivityLog(String afGuid, long level, String key,
			String... pars) {
		
		long jobID = CommonService.getInstance().getRepJobMonitorInternal(
				afGuid).getId();
		String msg = ReplicationMessage.getResource(key, pars);

		HACommon.addActivityLogByAFGuid(level, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, afGuid);
			
	}

	private static int hasSystemAndBootVolume(SessionInfo session, String afGuid, long jobID)
			throws ServiceException {
		String adrConfigXML = session.getSessionFolder() + "\\AdrConfigure.xml";

		try {
			ADRConfigure adrConfigure = ADRConfigureFactory
					.parseADRConfigureXML(adrConfigXML);

			boolean bHasBootVolume = (ADRConfigureUtil
					.isBootVolumeBackuped(adrConfigure) == 1);
			if(!bHasBootVolume)
				return -1;
			boolean bHasSystemVolume = (ADRConfigureUtil
					.isSystemVolumeBackuped(adrConfigure) == 1);

			log.debug("Check session " + session.getSessionName() + " at "
					+ session.getSessionFolder());
			
			if(!bHasSystemVolume)
				return -2;
			
			return 0;

		} catch (FileNotFoundException e) {
			String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILTO_ACCESS_SESSION);
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, afGuid);
			log.error("Exception occurred:",e);
			throw new ServiceException(
					"Exception occurred while checking session volumes", new Object[0]);
		} catch (Exception e1) {
			log.error("Exception occurred:",e1);
			throw new ServiceException(
					"Exception occurred while checking session volumes", new Object[0]);
		}

	}

	/**
	 * 
	 * checkExistedSessionsQualifiedForVirtualConversion() Check whether the
	 * existed sessions are qualified to do Virtual Conversion 1. From the
	 * newest sessions to the latest full session 2. Both incremental and full
	 * sessions include System/Boot Volume
	 * 
	 * @param des
	 * @param connect
	 *            it means if this function should deal with opening connection
	 *            and closing connection
	 * @return true: Qualified false: Not qualified
	 */
	public static boolean checkSessionsOKForVCM() throws HAException {

		SessionInfo[] backupSessions;
		boolean bRequireFullMachine = CommonUtil
				.ifReplicateRequiresBackupEntireMachine();
		if (bRequireFullMachine == false) {
			log
					.info("Registry flag [ReplicateRequireBackupEntireMachine] is configured as [false]");
			log
					.info("Virtual Conversion will not check existed sessions for Full Machine, but to check Boot Volume/System Volume only");
		}

		BackupDestinationInfo backupDestinationInfo = new BackupDestinationInfo();
		
		try{
			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			if (configuration == null || configuration.getDestination() == null){
				return false;
			}
			String remotePath = configuration.getDestination();
			backupDestinationInfo.setBackupDestination(remotePath);
			backupDestinationInfo.setConnInfo(BackupService.getInstance().getCONN_INFO(configuration));
		}
		catch (Exception e) {
			log.info("Null or empty backup configuration destination!");
			return false;
		}

		String afGuid = HAService.getInstance().retrieveCurrentNodeID();
		boolean isRemote = CommonUtil.isRemote(backupDestinationInfo.getBackupDestination());
		if (isRemote) {
			try {
				connectToRemote(backupDestinationInfo,afGuid,-1);
			} catch (Exception e) {
				log.info("Failed to connect to configuration destination");
				return false;
			}
		}
		try {
			backupSessions = getSessionsFromBackupDestnation(backupDestinationInfo);

			if (backupSessions.length == 0) {
				log.info("No backup sessions found, no qualified sessions as well");
				return false;
			}

			for (int i = backupSessions.length - 1; i >= 0; i--) {
				SessionInfo session = backupSessions[i];
				if (0 != hasSystemAndBootVolume(session,afGuid,-1)) {
					log.warn("Session " + session.getSessionName()
							+ " isn't qualified for virtual conversion.");
					return false;
				}
				// chefr03: UPDATE_BACKUP_INFO
				if (bRequireFullMachine == true) {
					if (!session.getSessionFullMachineFlag()) {
						log.warn("Session " + session.getSessionName()
								+ " doesn't contain Full Machine volumes.");
						return false;
					}
				}
				// chefr03: UPDATE_BACKUP_INFO

				if (session.ifFull()) {
					log.info("Get quailified sessions start from "
							+ session.getSessionName());
					return true;
				}
			}
		} catch (ServiceException e) {
			log.error("Exception occurred while checkSessionsOKForVCM()",e);
			return false;
		} finally {
			try {
				if (isRemote)
					closeRemoteConnect(backupDestinationInfo);
			} catch (Exception e) {
				log.debug(e.getMessage());
			}
		}

		return false;
	}
	
	@SuppressWarnings("serial")
	public static class NotFullMachineBackupSessionException extends Exception {}
	
	
	private String getMaxReplicatedSessionName(
			List<ConversionSessionItem> localSessions,
			SortedSet<String> remoteSessions) {
		String maxReplicatedSessionName = "S0000000000";
		for(ConversionSessionItem local : localSessions) {
			if (remoteSessions.contains(local.getSessionGuid())) {
				String name = local.getSessionName();
				if (name.compareTo(maxReplicatedSessionName) > 0)
					maxReplicatedSessionName = name;
			}
		}
		return maxReplicatedSessionName;
	}

	/**
	 * 
	 1.getSessionsFromBackupDestnation() 2.getGuidsOfReplicatedSessions()
	 * 3.For each session in BackupSessions Check if it is in replicas, if not.
	 * check if it is full or the previous on is in, return the session else
	 * return empty array
	 * 
	 * @param des
	 * @param connect
	 *            it means if this function should deal with opening connection
	 *            and closing connection
	 * @param lastSessionName
	 *            add this param for deal with copying extra session in one
	 *            round offline copy. inside one round offline copy when copy
	 *            extra session, we should know the last session name which has
	 *            been copied in previous while cycle to compare
	 * @param jobContext MSP Manual Conversion will create VSS Snapshot, 
	 * 			  but manualConversionUtility object might null, the object will create 
	 * 			  when do replication job, but the method can call from outside, 
	 * 			  so we use this parameter to identify whether use VSS snapshot or not
	 * 			  If call from outside replication command, the parameter is null,
	 * 			  Otherwise, the parameter is the job context. 
	 * @return the sessionsInfo that need to replicate. I.e.
	 *         [d:\vstore\session01,d:\vstore\session02]
	 */
	
	public SessionInfo[] getNextSessionsToReplicate(
			ReplicationJobScript jobScript, boolean connect,
			String lastSessionName,boolean checkSmartCopy, 
			BaseReplicationJobContext jobContext, BackupDestinationInfo backupDestinationInfo) throws Exception, NotFullMachineBackupSessionException {

		SessionInfo[] EMPTY = new SessionInfo[0];
		SessionInfo[] backupSessions = EMPTY;
		boolean bRequireFullMachine = CommonUtil
				.ifReplicateRequiresBackupEntireMachine();
		if (bRequireFullMachine == false) {
			log.info("Registry flag [ReplicateRequireBackupEntireMachine] is configured as [false]");
			log.info("Virtual Conversion will not check existed sessions for Full Machine, but to check Boot Volume/System Volume only");
		}

		log.info("Get the sessions to be replicated from " + backupDestinationInfo.getBackupDestination());
		
		try {
			backupSessions = (isVSBWithoutHASupport() && (jobContext != null) && (jobContext.getManualConversionUtility()) != null) ?
				this.getSessionsForManualConversion( jobScript.getAFGuid(), connect, jobContext, backupDestinationInfo ) :
				getSessionsFromBackupDestnation( jobScript.getAFGuid(), backupDestinationInfo );
		} catch (ServiceException e) {
			log.error(e.getMessage(),e);
		}

		if (lastSessionName != null) {
			log.debug("lastSessionName not null, it is:" + lastSessionName);
			if (lastSessionName
					.equals(backupSessions[backupSessions.length - 1]
							.getSessionName())) {
				log
						.info("No more Sessions need to be replicated in this round offline copy!");
				return EMPTY;
			} else {
				log.info("More Sessions need to be replicated in this round offline copy!");
			}
		}
		
		
		// FIXME we must use set destination to do this to make sure the
		// ReplicationConfiguration has the latest replication destination
		String[] guidsArray = new String[0];
		// ReplicationDestination dest = null;

		guidsArray = getGuidsOfReplicatedSessions(jobScript);
		
		
		String lastLocalConvertedGuid = null;
		List<ConversionSessionItem> localConvertedItems = new ArrayList<ConversionSessionItem>();
		try {
			ConversionSessionHistoryUtil history = new ConversionSessionHistoryUtil(
					ConversionSessionHistoryUtil.getFilePathFromBackupDestination(backupDestinationInfo
									.getBackupDestination()));
			localConvertedItems = history.getHistory().getItems();
			int size = localConvertedItems.size(); 
			if (size > 0)
				lastLocalConvertedGuid = localConvertedItems.get(size - 1)
						.getSessionGuid();
		} catch (Exception e) {
			log.warn("Get local converted session history error. ", e);
		}
		String lastVmConvertedGuid = null;
		if (guidsArray.length > 0)
			lastVmConvertedGuid = guidsArray[guidsArray.length - 1];
		

		SortedSet<String> guids = new TreeSet<String>();
		Collections.addAll(guids, guidsArray);
	
		List<SessionInfo> results = new ArrayList<SessionInfo>();

		// chefr03, check system and boot volumes before adding to session list
		// Check the session list reversely to get a chain of sessions which
		// include:
		// 1. A full backup session + system volume + boot volume
		// 2. Succeeding incremental sessions + system volume + boot volume
		int startIndex = 0;
		String sessionNameList = "";
		boolean lastGuidOfVmInDatastore = false;
		for (int i = backupSessions.length - 1; i >= 0; i--) {
			SessionInfo session = backupSessions[i];
			if (StringUtil.equals(lastVmConvertedGuid, session.getSessionGuid())) {
				lastGuidOfVmInDatastore = true;
			}
			if(HACommon.isTargetPhysicalMachine(jobScript.getAFGuid())){
				try {
					long jobID = CommonService.getInstance().getRepJobMonitorInternal(
							jobScript.getAFGuid()).getId();
					int result = hasSystemAndBootVolume(session,jobScript.getAFGuid(), jobID);
					if(result != 0)
					{
						switch(result)
						{
							case -1:
								addActivityLog(jobScript.getAFGuid(),Constants.AFRES_AFALOG_WARNING,
										ReplicationMessage.REPLICATION_NO_BOOTVOLUME,session.getSessionName());
								log.error(session.getSessionName() + "does not backup boot volume");
								break;
							case -2:
								addActivityLog(jobScript.getAFGuid(),Constants.AFRES_AFALOG_WARNING,
										ReplicationMessage.REPLICATION_NO_SYSTEMVOLUME,session.getSessionName());
								log.error(session.getSessionName() + "does not backup system volume");
								break;
						}
						return EMPTY;
					}
					// chefr03: UPDATE_BACKUP_INFO
					if (bRequireFullMachine == true) {
						if (!session.getSessionFullMachineFlag()) {
							addActivityLog(jobScript.getAFGuid(),
									Constants.AFRES_AFALOG_WARNING,
									ReplicationMessage.REPLICAION_NO_SESSIONS);
							log.error(session.getSessionName() + "is not a full machine backup.");
							return EMPTY; // break;
						}
					}
					// chefr03: UPDATE_BACKUP_INFO
				} catch (ServiceException e) {
					log.error(e.getMessage(),e);
					return EMPTY;
				}
			}
			
			if(isVSBWithoutHASupport()){
				
				if (!session.getSessionFullMachineFlag()) {
					
					addActivityLog(jobScript.getAFGuid(),
								Constants.AFRES_AFALOG_ERROR,
								ReplicationMessage.REPLICAION_NOT_FULL_MACHINE_SESSION,
								new String[]{session.getSessionName()});
					
//					addActivityLog(jobScript.getAFGuid(),
//							Constants.AFRES_AFALOG_WARNING,
//							ReplicationMessage.REPLICAION_NO_SESSIONS);
					
					log.error(session.getSessionName() + "is not a full machine backup.");
					
//					return EMPTY;
					throw new NotFullMachineBackupSessionException();
				}
			}

			if (lastSessionName != null) {
				if (lastSessionName.equals(session.getSessionName())) {
					startIndex = i + 1;
					break;
				}

			} else {
				if (sessionNameList != "") {
					sessionNameList += ",";
				}
				sessionNameList = sessionNameList + session.getSessionName();
				if (session.ifFull()) {
					// addActivityLog(Constants.AFRES_AFALOG_INFO,
					// ReplicationMessage.REPLICATION_OFFLINE_COPY_SESSION_CHAIN,
					// sessionNameList);
					// sessionNameList = "";
					startIndex = i;
					break;
				}
			}
		}

		if (!results.isEmpty()) {
			results.clear();
		}

		String maxReplicatedSessionName = getMaxReplicatedSessionName(localConvertedItems, guids);
		String startCopySession = null;
		for (int i = startIndex; i < backupSessions.length; i++) {
			SessionInfo session = backupSessions[i];
			if (guids.contains(session.getSessionGuid())
					|| maxReplicatedSessionName.compareTo(session.getSessionName()) >= 0) {
				// addActivityLog(Constants.AFRES_AFALOG_INFO,
				// ReplicationMessage.REPLICATION_SKIP_COPIED_SESSION,
				// session.getSessionName());
				// chefr03, 19546353
				// If a newer session has been replicated, the older sessions
				// should be
				// already been replicated, but since they may be purged by VCM,
				// we can't
				// get them from the copied session list, in this case, we need
				// clear them
				// manually and don't add them into the session list of
				// replication
				if (!results.isEmpty()) {
					results.clear();
					startCopySession = null;
					log.info("clear result. " + session.getSessionGuid() + " has been copied.");
				}
				continue;
			}
			
			if(startCopySession == null) {
				startCopySession = session.getSessionName();
				log.info("start session:" + startCopySession);
			}
			// addActivityLog(Constants.AFRES_AFALOG_INFO,
			// ReplicationMessage.REPLICATION_ADD_COPY_SESSION,
			// session.getSessionName());
			results.add(session);
		}
		
		boolean enforceFullLink = false;
		if (StringUtil.equals(lastVmConvertedGuid, lastLocalConvertedGuid)) {
			log.info("The last session in VM was converted from local datastore.");
		} else if (lastGuidOfVmInDatastore) {
			log.info("The last session in VM came from local datastore.");
		} else {
			log.info("The last session in VM doesn't match any local sessions. enforce full link.");
			enforceFullLink = true;
		}

		// Smart Copy
		if(checkSmartCopy){
			
			checkForVCMSmartCopy(jobScript, backupDestinationInfo, startCopySession, guids, backupSessions, results, enforceFullLink);
//			if (getSmartCopyMethod() == VCM_SMART_COPY_UNKNOWN) {
//				addActivityLog(jobScript.getAFGuid(), Constants.AFRES_AFALOG_ERROR,
//						ReplicationMessage.REPLICATION_SMART_COPY_METHOD_UNKNOWN);
//				return EMPTY;
//			}
		}
		
		// Smart Copy

		return results.toArray(EMPTY);
	}
	
	protected void saveConversionHistoryToDatastore(String backupDest, SessionInfo session) {
		try {
			String fileName = ConversionSessionHistoryUtil.getFilePathFromBackupDestination(backupDest);
			ConversionSessionItem item = new ConversionSessionItem();
			item.setSessionName(session.getSessionName());
			item.setSessionGuid(session.getSessionGuid());
			item.setConversionTime(new Date());
			ConversionSessionHistoryUtil util = new ConversionSessionHistoryUtil(fileName);
			util.addItem(item);
		} catch (Exception e) {
			log.warn("Fail to save conversion history to datastore.", e);
		}
	}

	protected int checkForVCMSmartCopy(ReplicationJobScript jobScript, BackupDestinationInfo backupDestinationInfo, String startCopySession,
			SortedSet<String> copiedSessions, SessionInfo[] totalSessions,
			List<SessionInfo> toBeCopiedSessions, boolean enforceFullLink) {
		if (totalSessions.length == 0)
			return -1;
		
		int method;

		String afGuid = jobScript.getAFGuid();
		
		try {
			int iRetentionCount = jobScript.getStandbySnapshots();
			int iMaxSnapshotCount = getMaxSnapshotCount();
			int iCopiedSessions = copiedSessions.size();
			int iTotalSessions = totalSessions.length;
			int iToBeCopiedSessions = toBeCopiedSessions.size();
			int iMaxCopySessions = Math.min(iRetentionCount, iMaxSnapshotCount);

			log.info("-------------------------------------");
			log.info("Retention count       = " + iRetentionCount);
			log.info("Snapshot count limit  = " + iMaxSnapshotCount);
			log.info("VCM Max snapshots     = " + iMaxCopySessions);
			log.info("Copied sessions       = " + iCopiedSessions);
			log.info("Total sessions        = " + iTotalSessions);
			log.info("To be copied sessions = " + iToBeCopiedSessions);
			log.info("-------------------------------------");

			if ((iCopiedSessions + iToBeCopiedSessions) < iMaxCopySessions) {
				log
						.info("Smart Copy Case: [Copied sessions] plus [To be copied sessions] is less than [Max snapshot count]");
				method = VCM_SMART_COPY_COPIED_NEW_LESS_MAX;
			} else if ((iCopiedSessions + iToBeCopiedSessions) == iMaxCopySessions) {
				log
						.info("Smart Copy Case: [Copied sessions] plus [To be copied sessions] equals to [Max snapshot count]");
				method = VCM_SMART_COPY_COPIED_NEW_EQUALS_MAX;
			} else {

				if (iToBeCopiedSessions < iMaxCopySessions) {
					log
							.info("Smart Copy Case: [To be copied sessions] is less than [Max snapshot count]");
					method = VCM_SMART_COPY_NEW_LESS_MAX;
				} else if (iToBeCopiedSessions == iMaxCopySessions) {
					log
							.info("Smart Copy Case: [To be copied sessions] equals to [Max snapshot count]");
					method = VCM_SMART_COPY_NEW_EQUALS_MAX;
				} else { // (iToBeCopiedSessions > iMaxCopySessions)
					log
							.info("Smart Copy Case: [To be copied sessions] is larger than [Max snapshot count]");
					method = VCM_SMART_COPY_NEW_LARGER_MAX;
				}
			}

			// Get bitmap list
			
			SessionInfo[] EMPTY = new SessionInfo[0];
			SessionInfo[] sessions = toBeCopiedSessions.toArray(EMPTY);
			
			List<String> vBitmapList = new ArrayList<String>();

			String currentBackupDestination = backupDestinationInfo.getBackupDestination();
			
			NativeFacade nativeFacade = BackupService.getInstance()
					.getNativeFacade();
			int result = nativeFacade.GetSessionBitmapList(
					currentBackupDestination, vBitmapList);

			if (result != 0) {
				log.error("Smart Copy: Can't get bitmap list");
				setSmartCopyFlag(true);
				setSmartCopySynthetizeStart("S0000000000");
				setSmartCopySynthetizeSession(sessions[sessions.length-1].getSessionName());
				return VCM_SMART_COPY_UNKNOWN;
			}
			
			if (iCopiedSessions == 0 || enforceFullLink) {
				log.info("This is the first time to run conversion. Remove useless bitmap files which may be obsolete.");
				for (SessionInfo session : toBeCopiedSessions) {
					nativeFacade.DeleteSessionBitmap(backupDestinationInfo.getBackupDestination(), session.getSessionName());
				}
			}

			if(toBeCopiedSessions.size() > 0 && vBitmapList.size() > 0){
				SessionInfo lastSessionName = toBeCopiedSessions.get(toBeCopiedSessions.size() - 1);
				String bitMapLastSessionName = vBitmapList.get(vBitmapList.size() - 1);

				int bitmapSize = vBitmapList.size();
				if (lastSessionName.getSessionName().compareTo(
						bitMapLastSessionName) > 0) {
					bitmapSize += 1;
				}

				if (bitmapSize > iMaxCopySessions && iToBeCopiedSessions >= iMaxCopySessions) {
					log.info("bitmap count is larger than max session count and to-be-copied-sessions.Conduct smart copy.");
					method = VCM_SMART_COPY_NEW_LARGER_MAX;
				}
			}
			
			// Setting smart copy flags
			setSmartCopyMethod(method);

			if ((method == VCM_SMART_COPY_COPIED_NEW_LESS_MAX)
					|| (method == VCM_SMART_COPY_COPIED_NEW_EQUALS_MAX)
					|| (method == VCM_SMART_COPY_NEW_LESS_MAX)
					|| (method == VCM_SMART_COPY_NEW_EQUALS_MAX)) {
				log.info("No smart copy needed");
				setSmartCopyFlag(false);
				return method;
			} else if (method == VCM_SMART_COPY_UNKNOWN) { // TODO, throws
															// exception?
				setSmartCopyFlag(false);
				return method;
			}

			log.info("Smart Copy needed");
			setSmartCopyFlag(true);

			log.info("Smart Copy: The first not copied session is ["
					+ startCopySession + "]");
			// Some session may already been purged, but the bitmap is available
			setSmartCopySynthetizeStart(startCopySession);
			int SynthetizeSessionIndex = iToBeCopiedSessions - iMaxCopySessions;
			if (SynthetizeSessionIndex >= sessions.length)
				SynthetizeSessionIndex = sessions.length - 1;
			setSmartCopySynthetizeSession(sessions[SynthetizeSessionIndex].getSessionName());
			log.info("Smart Copy: the first to be copied session is ["
					+ sessions[0].getSessionName() + "]");
			log.info("Smart Copy: Run Smart Copy for sessions from  ["
					+ getSmartCopySynthetizeStart() + "] to ["
					+ getSmartCopySynthetizeSession() + "]");
			log.info("Smart Copy: the last to be copied session is  ["
					+ sessions[iToBeCopiedSessions - 1].getSessionName() + "]");
		} catch (Exception e) {
			log.error("Smart Copy Case: error occurred, can't start Smart Copy",e);
			setSmartCopyFlag(false);
			method = VCM_SMART_COPY_UNKNOWN;
		}

		return method;
	}

	protected void warnIncompleteSession(NativeFacade nativeFacade,
			long shrmemid, ADRConfigure adrConfigure, String sessionNumber,String afGuid) {
		
		if(!HACommon.isTargetPhysicalMachine(afGuid))
			return;
		
		int bootVolumeBackuped = ADRConfigureUtil
				.isBootVolumeBackuped(adrConfigure);
		int d2dVolumeBackuped = ADRConfigureUtil
				.isD2DVolumeBackuped(adrConfigure);
		int systemVolumeBackuped = ADRConfigureUtil
				.isSystemVolumeBackuped(adrConfigure);
		if (bootVolumeBackuped != 1) {
			String msg = ReplicationMessage
					.getResource(ReplicationMessage.REPLICATION_NO_BOOTVOLUME,
							sessionNumber);
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, shrmemid, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, afGuid);
			
			
		}
		if (d2dVolumeBackuped != 1) {
			String msg = ReplicationMessage.getResource(
					ReplicationMessage.REPLICATION_NO_D2DVOLUME, sessionNumber);
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, shrmemid, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, afGuid);
			
		}
		
		if (systemVolumeBackuped != 1) {
			String msg = ReplicationMessage.getResource(
					ReplicationMessage.REPLICATION_NO_SYSTEMVOLUME,
					sessionNumber);
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, shrmemid, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, afGuid);
			
		}
	}

	public static void closeRemoteConnect(BackupDestinationInfo backupDestinationInfo) {
		if (backupDestinationInfo == null)
			return;

		String remotePath = backupDestinationInfo.getBackupDestination();
		boolean isRemote = CommonUtil.isRemote(remotePath);
		if (isRemote) {
			try {
				BrowserService.getInstance().getNativeFacade().NetCancel(
						remotePath, false);
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}
		}
	}

	public static void connectToRemote(BackupDestinationInfo backupDestinationInfo,String afGuid,long jobId)
			throws Exception {
		if (backupDestinationInfo == null)
			return;
		String remotePath = backupDestinationInfo.getBackupDestination();
		boolean isRemote = CommonUtil.isRemote(remotePath);

		if (isRemote) {
			BackupService.CONN_INFO info = backupDestinationInfo.getConnInfo();
			String domain = info.getDomain();
			String userName = info.getUserName();
			if (!StringUtil.isEmptyOrNull(domain)) {
				if (!domain.trim().endsWith("\\"))
					domain += "\\";
				userName = domain + userName;
			}

			try {
				long netConn = BrowserService.getInstance().getNativeFacade()
						.NetConn(userName, info.getPwd(),
								remotePath);
				if (netConn != 0) {
					log.error("Failed to connect to remote configuration destination");
					log.error("RemotePath: " + remotePath);
					
					//add activity log <zhaji22>
					String msg = ReplicationMessage.getResource(
							ReplicationMessage.AFRES_AFREPC_PREX + ReplicationMessage.AFRES_AFREPC_REMOTE_DEST_CONN_FAILED,remotePath);
					HACommon.addActivityLogByAFGuid(
						Constants.AFRES_AFALOG_ERROR, jobId, Constants.AFRES_AFJWBS_GENERAL,
						new String[] { msg, "", "", "", "" },
						afGuid);
					//end
					throw new Exception(msg);
				}
			} catch (ServiceException e) {
				//add activity log <zhaji22>
				String msg = ReplicationMessage.getResource(
						ReplicationMessage.AFRES_AFREPC_PREX + ReplicationMessage.AFRES_AFREPC_REMOTE_DEST_CONN_FAILED,remotePath);
				HACommon.addActivityLogByAFGuid(
					Constants.AFRES_AFALOG_ERROR, jobId, Constants.AFRES_AFJWBS_GENERAL,
					new String[] { msg, "", "", "", "" },
					afGuid);
				//end
				throw new Exception(msg);
			}

		}

	}
	
	protected boolean compareSessionSizeWithStorageSize(ReplicationJobScript jobScript, BackupDestinationInfo backupDestinationInfo, ReplicationDestination dest, 
			SessionInfo session, long jobId, HashMap<String, DiskInfo> storageFreeSizeGroup) {
		
		boolean ret=true;
		
		VirtualizationType virtualizationType = jobScript.getVirtualType();
		String afguid = jobScript.getAFGuid();
		HashMap<String, Long> replicationSizeGroup = new HashMap<String, Long>();
		List<DiskDestination> diskDest = dest.getDiskDestinations();
		
		long sessionSize = session.getSessionDataSize();
		String sessionName = session.getSessionName();
		
		try {
			
			log.info("Do d2d file compare for session: " +  sessionName + " session size=" + sessionSize);
			
			int idx=0;
			int nStartSessNo=0, nEndSessNo=0; 
			
			String filePath = null;
			String userName = null;	
			String password = null;	
			String strSessNo = null;	
			
			filePath = backupDestinationInfo.getBackupDestination();
			boolean isRemote = CommonUtil.isRemote(filePath);
			if (isRemote) {
				try {
					connectToRemote(backupDestinationInfo,afguid,jobId);
				}catch (Exception e) {
					log.error(e.getMessage());
					return true;
				}
				
			}
		
			File sessonFolder = new File(session.getSessionFolder());
			
			if (ManualConversionUtility.isVSBWithoutHASupport(jobScript) && !jobScript.getBackupToRPS()) {
				filePath = sessonFolder.getAbsolutePath();
				filePath = filePath.substring(0, filePath.lastIndexOf("\\"));
				filePath = filePath.substring(0, filePath.lastIndexOf("\\"));
			}
			
			for (DiskDestination d : diskDest) {
				
				DiskModel disk = d.getDisk();
				VMStorage vmstore = d.getStorage();
				String storageName = vmstore.getName();
//				String storageDiskName ="";
//				if(virtualizationType == VirtualizationType.HyperV) {
//					storageDiskName = storageName.substring(0, (storageName.indexOf(":") + 1));
//				}
//				else {
//					storageDiskName = storageName;
//				}
		
				String sig = disk.getSignature();
				String d2dFile = sessonFolder.getAbsolutePath() + "\\disk" + sig + ".D2D";
				String d2dFileName = "disk" + sig + ".D2D";
				File d2d = new File(d2dFile);
				if (d2d.exists()) {
					//get d2dfile size -begin
					
					idx = sessionName.indexOf((int)'S');
					strSessNo = sessionName.substring(idx+1);
					nStartSessNo = nEndSessNo = StringUtil.string2Int(strSessNo, 0); 
					
					if(nStartSessNo <=0 ){
						log.error("SessNO is not right! sessionName=" + sessionName);
						return true;
					}
		
					try {
						NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
						long d2dFileSize = nativeFacade.getD2DFileSize(filePath, userName, password, d2dFileName, nStartSessNo, nEndSessNo, jobScript.getBackupDestType());
						if(!replicationSizeGroup.containsKey(storageName)) {
							replicationSizeGroup.put(storageName, d2dFileSize);
						}
						else {
							long totalReplicationSize = replicationSizeGroup.get(storageName)+d2dFileSize;
							replicationSizeGroup.put(storageName, totalReplicationSize);
						}
					}catch(Throwable e){
						log.error("Get D2D file: [" + d2dFile + "]" + " size Error!");
						log.error(e.getMessage());
						// return true;
					}
		
				}
				else{
					log.error("d2dFile: " + d2dFile + " doesn't exist");
					// return true;
				}
				
			}
			
			VirtualConversionEmailAlertUtil.sendAlertForWarningSpace(replicationSizeGroup.keySet(), storageFreeSizeGroup, afguid, jobId);
			//Begin to compare the free space
			for (String key : replicationSizeGroup.keySet()) {
				long replicationSizeBytes = replicationSizeGroup.get(key);
				if(storageFreeSizeGroup.containsKey(key)) {
					long freeSizeBytes = storageFreeSizeGroup.get(key).getFreeSize();
					
					if(replicationSizeBytes >= freeSizeBytes) {
						log.error("The replication size is larger than the free size on the path: " + key + ". Free size: " + freeSizeBytes);
						return false;
					}
				}
				
			}
		}catch (Exception e1) {
			log.error("compareSessionSizeWithStorageSize throw exception: " + e1.getMessage(), e1);
			return true;
		}

		return ret;
	}
	
	String getHostNameByUUId(String uuid) throws UnknownHostException {
		String hostName = "";
		
		FailoverJob failoverJob = (FailoverJob)JobQueueFactory.getDefaultJobQueue().findFailoverJobByID(uuid);
		if(failoverJob != null) {
			FailoverJobScript failoverJobScript = failoverJob.getJobScript();
			hostName = failoverJobScript.getProductionServerName();
		}
		
		if(StringUtil.isEmptyOrNull(hostName)) {
			hostName = InetAddress.getLocalHost().getHostName();
		}
		
		return hostName;
	}
	
	BackupInfo getBackupInfo(SessionInfo session){
		
		try {
			
			String backupinfoXML = session.getSessionFolder()
					+ "\\BackupInfo.XML";
			BackupInfo backupInfo = BackupInfoFactory
					.getBackupInfoFromURI(backupinfoXML);
			return backupInfo;
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return null;
		}
		
	}
	
	private void saveVCMEvent(ReplicationJobScript jobScript, Date startTime, String status){
		try {
			VCMEventManager evnetManager = VCMEventManager.getInstance();
			VCMEvent event = getVCMEvent(jobScript);
			event.setStartTime(startTime);
			event.setStatus(status);
			evnetManager.saveVCMEvent(jobScript.getAFGuid(),event);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
	private VCMEvent getVCMEvent(ReplicationJobScript jobScript){
		
		VCMEvent event = new VCMEvent();
		
		event.setEndTime(new Date());
		event.setTaskGuid(UUID.randomUUID().toString());
		event.setTaskName("Conversion task");
		event.setTaskType(VCMEventType.OfflineCopy);
		
		if(HACommon.isTargetPhysicalMachine(jobScript.getAFGuid())){
		
			try {
				event.setSrcHostName(InetAddress.getLocalHost().getHostName());
			} catch (UnknownHostException e) {
			}
		}
		else if(jobScript.getBackupToRPS()){
			event.setSrcHostName(jobScript.getAgentNodeName());
			//
		}else{
			
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(jobScript.getAFGuid());
			VMBackupConfiguration vmBackup = null;
			try {
				vmBackup = VSphereService.getInstance().getVMBackupConfiguration(vm);
			} catch (ServiceException e) {
				log.error(e.getMessage() == null ? e : e.getMessage());				
			}
			if(vmBackup == null){
				return null;
			}
			BackupVM backupVM = vmBackup.getBackupVM();
			
			event.setSrcHostName(backupVM.getVmHostName());
			event.setSrcVMName(backupVM.getVmName());
			event.setSrcVirtualCenterName(backupVM.getEsxServerName());
			
		}
		
		event.setSrcVMUUID(jobScript.getAFGuid());
		
		FailoverJobScript failoverJobScript = HAService.getInstance().getFailoverJobScript(jobScript.getAFGuid());
		Virtualization virtual = failoverJobScript.getFailoverMechanism().get(0);
		
		event.setDestVMName(virtual.getVirtualMachineDisplayName());
		
		ReplicationDestination dest = jobScript.getReplicationDestination().get(0);
		if(dest instanceof ARCFlashStorage){
			ARCFlashStorage tmp = (ARCFlashStorage)dest;
			event.setDestHostName(tmp.getHostName());
			event.setDestVMType(VCMVMType.HyperV);
			event.setProxy(tmp.isProxyEnabled());
			
		}else if(dest instanceof VMwareESXStorage){
			VMwareESXStorage tmp = (VMwareESXStorage)dest;
			event.setDestHostName(tmp.getESXHostName());
			event.setDestVMType(VCMVMType.VMware);
			event.setProxy(tmp.isProxyEnabled());
			
		}else if(dest instanceof VMwareVirtualCenterStorage){
			VMwareVirtualCenterStorage tmp = (VMwareVirtualCenterStorage)dest;
			event.setDestHostName(tmp.getESXHostName()==null||tmp.getESXHostName().isEmpty()?tmp.getEsxName():tmp.getESXHostName());
			event.setDestVirtualCenterName(tmp.getVirtualCenterHostName());
			event.setDestVMType(VCMVMType.VirtualCenter);
			event.setProxy(tmp.isProxyEnabled());
		}
		
		HeartBeatJobScript hbJobScript = HAService.getInstance().getHeartBeatJobScript(jobScript.getAFGuid());
		if (hbJobScript != null) {
			event.setVcmMonitorHost(hbJobScript.getHeartBeatMonitorHostName());
		}
		if(!HACommon.isTargetPhysicalMachine(jobScript.getAFGuid())){
			//set vm instance uuid
			event.setAfGuid(jobScript.getAFGuid());
		}
		long jobID = CommonService.getInstance().getRepJobMonitorInternal(jobScript.getAFGuid()).getId();
		event.setJobID(jobID);
		
		ProductionServerRoot prodRoot = null;
		String afguid = jobScript.getAFGuid();
		String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
		try{
			prodRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(afguid);
			String vmuuid = prodRoot.getReplicaRoot().getVmuuid();
			String vmname = prodRoot.getReplicaRoot().getVmname();
			event.setDestVMUUID(vmuuid);
			event.setDestVMName(vmname);
		}catch (Exception e) {}
		
		
		return event;
		
		
	}
	
	void setJobMonitorSnapshotCount(String afGuid){
		VMSnapshotsInfo[] snapshots;
		try {
			RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(afGuid);
			if(jobMonitor != null){
				synchronized (jobMonitor) {
					//numOfToRep is the number of sessions not converted in current VSB job
					int numOfToRep = jobMonitor.getToRepSessions().length;
					//totalSessionNumbers is the number of total sessions need be converted in current VSB job
					int totalSessionNumbers = jobMonitor.getTotalSessionNumbers();
					//convertingNum is the number of which session of total sessions need be converted in current VSB job
					int convertingNum = totalSessionNumbers - numOfToRep;
					//because after UI call getCurrentSnapshotCount(), UI will add 1,
					//so, avoid UI modifying, here convertingNum - 1
					jobMonitor.setCurrentSnapshotCount(convertingNum - 1);
				}
			}
		} catch (Exception e) {
		}
		
	}
	
	void calculateThroughput(RepJobMonitor jobMonitor,String afGuid){
		
		// print throughput in activity log, for fix issue 19681167, Sep.16
		
		if(jobMonitor != null){
			
			long totalSize = jobMonitor.getAllSessionTransSize();
			long repElapsedTime = jobMonitor.getAllSessionTransTime(); // in
																	// milliseconds
			String dataSizeStr = VirtualConversionEmailAlertUtil.bytes2String(totalSize);
			double min = ((double) repElapsedTime) / (1000 * 60);
			double throughput = 0;
			if(min > 0.000001)
				throughput = ((double)totalSize) / min;
			
			String time = getFormatedTime(repElapsedTime);

			String throughputStr = throughputBytes2String(throughput);
			
			String msg = ReplicationMessage.getResource(
					ReplicationMessage.AFRES_AFREPC_16389, new String[] {
							dataSizeStr, time,
							throughputStr });

			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobMonitor.getId(), Constants.AFRES_AFJWBS_GENERAL, 
											new String[] { msg,"", "", "", "" }, afGuid);
			

		}
	}
	
	public static String getFormatedTime(long time) {
		long sec = (time > 500 && time < 1000) ? 1 : time / 1000;
		long min = 0;
		long hour = 0;
		if(sec >= 60) {
			min = sec / 60; 
			sec = sec % 60;
			
			if(min >= 60) {
				hour = min / 60;
				min = min % 60;
			}
		}
		
		if(hour > 0) 
			return ReplicationMessage.getResource(ReplicationMessage.REPLICAION_TIME_HOUR, hour+"", min+"", sec+"");
		else if(min > 0)
			return ReplicationMessage.getResource(ReplicationMessage.REPLICAION_TIME_MIN, min+"", sec+"");
		else
			return ReplicationMessage.getResource(ReplicationMessage.REPLICAION_TIME_SEC, sec+"");
			
	}
	
	public static String throughputBytes2String(double bytes){	
		DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(DataFormatUtil.getDateFormatLocale());
		DecimalFormat number = new DecimalFormat("0.00", formatSymbols);
	
		if (bytes<(1024*1024*1024)) {
			String mb = number.format(bytes/(1024*1024));
			if(mb.startsWith("1024"))
				return ReplicationMessage.getResource(ReplicationMessage.REPLICAION_THROUGHPUT_GB, "1");
			
			return ReplicationMessage.getResource(ReplicationMessage.REPLICAION_THROUGHPUT_MB, mb);
		}
		else
			return ReplicationMessage.getResource(ReplicationMessage.REPLICAION_THROUGHPUT_GB, number.format(bytes/(1024*1024*1024)));
	}
	
	
	void activityLogForRepCancel(ReplicationJobScript jobScript,long jobID) {
		
		String msg = ReplicationMessage.getResource(ReplicationMessage.AFRES_AFREPC_16388);
		
		HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL, 
				new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
		
	}

	private void activityLogForRepDest(ReplicationJobScript jobScript, long jobID) {
		ReplicationDestination replicationDestinaiton = HAService.getInstance()
				.getReplicationDestinaiton(jobScript);
		if (replicationDestinaiton == null){
			return ;
		}
		
		try{
			String hypervisor_hostname = "";
			if (replicationDestinaiton instanceof ARCFlashStorage) {
				ARCFlashStorage dest = (ARCFlashStorage)replicationDestinaiton;
				hypervisor_hostname = dest.getHostName();
			}else if (replicationDestinaiton instanceof VMwareESXStorage){
				VMwareESXStorage dest = (VMwareESXStorage)replicationDestinaiton;
				hypervisor_hostname = dest.getESXHostName();
			}else if (replicationDestinaiton instanceof VMwareVirtualCenterStorage) {
					VMwareVirtualCenterStorage dest = (VMwareVirtualCenterStorage)replicationDestinaiton;
					hypervisor_hostname = dest.getVirtualCenterHostName();
			}

			String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICAION_BEGIN, 
					ReplicationMessage.getReplicationDestType(replicationDestinaiton.getDestProtocol()), hypervisor_hostname);
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());

			if (replicationDestinaiton instanceof VMwareESXStorage || replicationDestinaiton instanceof VMwareVirtualCenterStorage) {
				HeartBeatJobScript hbJobScript = HAService.getInstance().getHeartBeatJobScript(jobScript.getAFGuid());
				if(hbJobScript == null){
					log.error("heartbeatjobscript is null.");
					return ;
				}
				String monitor = hbJobScript.getHeartBeatMonitorHostName();
				if (replicationDestinaiton.isProxyEnabled()) {
					msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_DEST_MONITOR_AS_PROXY, monitor);
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				} else {
					msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_DEST_MONITOR_NOT_AS_PROXY, monitor);
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
							new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
				}
			}
		}catch(Exception e){
			log.error("Fail to get replication destination from job script", e);
		}
	}
	
	private void saveReplicationStatus(String afguid, int status, long milliSeconds, long repJobDuration) {
		try {
			String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
			ProductionServerRoot prodRoot = null;
			try {
				RepositoryUtil instance = RepositoryUtil.getInstance(xml);
				prodRoot = instance.getProductionServerRoot(afguid);
			}
			catch(HAException ex) {
				log.info("ProductionServerRoot has not been set.");
			}
			
			if(prodRoot == null) {
				prodRoot = new ProductionServerRoot();
				prodRoot.setProductionServerAFUID(afguid);
				prodRoot.setProductionServerHostname(getHostNameByUUId(afguid));
			}
			
			prodRoot.setMostRecentRepStatus(status);
			if(milliSeconds <= 0)
				milliSeconds = System.currentTimeMillis();
			
			prodRoot.setMostRecentRepTime(HACommon.date2String(milliSeconds));
			prodRoot.setMostRecentRepTimeMilli(milliSeconds);
			prodRoot.setSendToMonitorAfterReplic(false);
			prodRoot.setMostRecentRepDuration(repJobDuration);
//			RepJobMonitor jobMonitor = CommonService.getInstance().getRepJobMonitorInternal(afguid);
//			
//			synchronized (jobMonitor) {
//				jobMonitor.setRepJobStartTime(System.currentTimeMillis());
//				jobMonitor.setCurrentJobCancelled(false);
//			}
			
			 RepositoryUtil.getInstance(xml).saveProductionServerRoot(prodRoot);
			
		}
		catch(Exception e) {
			log.error("Fails to save the replication status and time to repository.xml. Error msg:" + e.getMessage());
		}
	}
	
	protected boolean validateSessionPasswords(SessionInfo[] sessions, ReplicationJobScript jobScript, long jobID,
			String sessionDestination) {
		if (sessions == null || sessions.length == 0) {
			return true;
		}
		int validPasswords = 0;
		for (int i = sessions.length - 1; i >= 0; i--) {
			SessionInfo sessionInfo = sessions[i];
			SessionPasswordCheckStatus status = HACommon.checkAndUpdateSessionPassword(sessionInfo, jobScript,
					sessionDestination);
			switch (status) {
			case INVALID:
				if (validPasswords == 0) {
					// The first session password is invalid, write activity log and return false
					String message = ReplicationMessage.getResource(
							ReplicationMessage.REPLICATION_FAILTO_VAIIDATE_SESSION_PASSWORD,
							sessionInfo.getSessionName());
					log.error(message);
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL,
							new String[] { message, "", "", "", "" }, jobScript.getAFGuid());
					HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, message);
					return false;
				}
				return true;
			case PLAIN:
			case UPDATED:
				return true;
			case VALID:
			default:
				validPasswords++;
			}
		}
		return true;
	}
	
	protected boolean validateSessionPasswords(ReplicationJobScript jobScript, long jobID, BackupDestinationInfo backupDestinationInfo) {
		try {
			SessionInfo[] sessions = getSessionsFromBackupDestnation(jobScript.getAFGuid(), backupDestinationInfo);
			return validateSessionPasswords(sessions, jobScript, jobID, backupDestinationInfo.getBackupDestination());
		} catch (ServiceException e) {
			log.error("Failed to get the backup sessions for validate session passwords.", e);
		}
		return true;
	}
	
	public static boolean checkBackupSessionConfigFile(File session){
		if(session == null){
			log.error("session is null, return false");
			return false;
		}
		if(!session.exists()){
			log.error(session.getAbsoluteFile() + " does not exit, return false");
			return false;
		}
		
		// check if backupInfo.xml exist
		File backupinfoXML = new File(session.getPath() + "\\BackupInfo.XML");
		if(!backupinfoXML.exists()){
			log.error(backupinfoXML.getAbsoluteFile() + " does not exist, return false");
			return false;
		}
		
		// check if AdrConfigure.xml(D2D) or VMDiskInfo.xml(HBBU) exist 
		File adrConfigXML = new File(session.getPath() + "\\AdrConfigure.xml");
		File VMDiskInfoXML = new File(session.getPath() + "\\VMDiskInfo.xml"); 
		if(!adrConfigXML.exists() && !VMDiskInfoXML.exists()){
			log.error("Both AdrConfigure.xml and VMDiskInfo.xml not exist, return false");
			return false;
		}
		return true;
	}
	
	public static boolean checkIndexOfBackupSessionFile(File session){
		if(session == null)
			return false;

		// check if the corresponding index file of backup session exists
		int indexSessionNum = session.getPath().lastIndexOf("\\");
		if(indexSessionNum <= 0)
			return false;
		String sessionNum = session.getPath().substring(indexSessionNum + 1, session.getPath().length());
		
		int indexRootPath = session.getPath().lastIndexOf("\\VStore\\");
		if(indexRootPath <= 0)
			return false;
		String backupRootPath = session.getPath().substring(0, indexRootPath);
		
		String sessionIndexFilePath = backupRootPath + "\\Index\\" + sessionNum + ".idx";
		File sessionIndexFile = new File(sessionIndexFilePath);
		if(!sessionIndexFile.exists()){
			log.info(sessionIndexFilePath + " does not exit, return false");
			return false;
		}
		return true;
	}

	public static BackupDestinationInfo getBackupDestinationInfo(ReplicationJobScript jobScript, boolean updateScriptAndPrint, long jobID) throws ServiceException{
		BackupDestinationInfo info = new BackupDestinationInfo();
		if (!jobScript.getBackupToRPS()){
			BackupConfiguration configuration = HACommon.getBackupConfigurationViaAFGuid(jobScript.getAFGuid());
			info.setBackupDestination(configuration.getDestination());
			info.setConnInfo(BackupService.getInstance().getCONN_INFO(configuration));
			
			if (updateScriptAndPrint){
				String msg = ReplicationMessage
						.getResource(ReplicationMessage.REPLICATION_BACKUP_TO_SHARE_FOLDER, jobScript.getAgentNodeName(), configuration.getDestination());
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			}
		}
		else{
			String rpsPwd = WSJNI.AFDecryptString(jobScript.getRpsPassword());
			
			if (updateScriptAndPrint) {
				try{
					BackupD2D backupD2D = null;
					String rpsPolicyUUID = "";
					try{
						backupD2D = HAService.getInstance().getRPSPolicyUUID(jobScript.getRpsHostName(), jobScript.getRpsUserName(), 
								rpsPwd, jobScript.getRpsPort(), jobScript.getRpsProtocol(), jobScript.getAFGuid());
					}catch (Exception e){
						log.error("Failed to get the RPS policy UUID from RPS server:" + e.getMessage());
					}
					if(backupD2D != null && !StringUtil.isEmptyOrNull(backupD2D.getPolicyUUID())){
						rpsPolicyUUID = backupD2D.getPolicyUUID();
					}
					if(StringUtil.isEmptyOrNull(rpsPolicyUUID)){
						rpsPolicyUUID = jobScript.getRpsPolicyUUID();
					}
					
					RpsPolicy4D2D rpsPolicy = HAService.getInstance().getRPSPolicy(jobScript.getRpsHostName(), jobScript.getRpsUserName(), 
							rpsPwd, jobScript.getRpsPort(), jobScript.getRpsProtocol(), rpsPolicyUUID, true);
					
					if (rpsPolicy != null) {
						jobScript.setRpsPolicyUUID(rpsPolicyUUID);
						jobScript.setDataStoreUUID(rpsPolicy.getDataStoreName());
						jobScript.setDataStoreDisplayName(rpsPolicy.getDataStoreDisplayName());
						jobScript.setDataStorePath(rpsPolicy.getStorePath());
						if(!StringUtil.isEmptyOrNull(rpsPolicy.getStoreUserName())){
							jobScript.setDataStoreUserName(rpsPolicy.getStoreUserName());
							jobScript.setDataStorePassword(WSJNI.AFEncryptString(rpsPolicy.getStorePassword()));
						}
						HAService.getInstance().updateRepJobScript(jobScript);
					}
				}catch (Exception e){
					log.error("Failed to get the RPS policy from RPS server:" + e.getMessage());
				}
			}

			String backupDestination = jobScript.getDataStorePath();
			if (!backupDestination.endsWith("\\")) {
				backupDestination += "\\";
			}

			backupDestination += jobScript.getAgentBackupDestName();
			info.setBackupDestination(backupDestination);
			
			if (updateScriptAndPrint){
				String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_BACKUP_TO_RPS, jobScript.getAgentNodeName(), 
						backupDestination, jobScript.getRpsHostName(), jobScript.getDataStoreDisplayName());
				
				HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
						new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			}

			String userName = jobScript.getRpsUserName();
			String password = rpsPwd;
			if(!StringUtil.isEmptyOrNull(jobScript.getDataStoreUserName())){
				userName = jobScript.getDataStoreUserName();
				password = WSJNI.AFDecryptString(jobScript.getDataStorePassword());
			}
			BackupService.CONN_INFO connInfo = new BackupService.CONN_INFO();
			String domain = "";
			if (userName != null && userName.trim().length() > 0) {
				userName = userName.trim();
				int index = userName.indexOf("\\");
				if (index > 0) {
					domain = userName.substring(0, index);
					userName = userName.substring(index + 1);
				}
			}
			connInfo.setDomain(domain);
			connInfo.setUserName(userName);
			connInfo.setPwd(password);
			
			info.setConnInfo(connInfo);
		}
		return info;
	}

	protected boolean checkIfSessionMerging(ReplicationJobScript jobScript, SessionInfo[] sessions, long jobID) {
		String rootPath = sessions[0].getSessionFolder().substring(0, sessions[0].getSessionFolder().lastIndexOf("\\"));
		rootPath = rootPath.substring(0, rootPath.lastIndexOf("\\"));
		int beginSessNum = convertSessionNameToSessionNumber(sessions[0].getSessionName());
		int endSessNum = convertSessionNameToSessionNumber(sessions[sessions.length - 1].getSessionName());
		boolean isMerging = BrowserService.getInstance().getNativeFacade()
				.CheckVHDMerging(rootPath, beginSessNum, endSessNum);
		if (isMerging) {
			log.info("There have merging session(s) between " + sessions[0].getSessionName() + " and " + sessions[sessions.length - 1].getSessionName());
			String msg = ReplicationMessage.getResource(
					"AFRES_AFREPC_"  + ReplicationMessage.AFRES_AFREPC_SRC_LOCK_FAILED, rootPath);
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL,
					new String[] { msg,	"", "", "", "" }, jobScript.getAFGuid());
			
			RemoteVCMSessionMonitor.getInstance().setHasPendingJobs( jobScript.getAFGuid(), true );
		}
		
		return isMerging;
	}
	protected int convertSessionNameToSessionNumber(String sessionName) {
		String str = sessionName.substring(1);
		return Integer.parseInt(str);
	}
	
	public static VCMMachineInfo getVCMMachineInfo(ReplicationJobScript jobscript, boolean needConnect) {
		BackupDestinationInfo backupDestinationInfo = null;
		try {
			backupDestinationInfo = getBackupDestinationInfo(jobscript, false, -1);
		} catch (ServiceException e) {
			log.warn("Failed to get backup configuration.", e);
			return null;
		}

		String remotePath = backupDestinationInfo.getBackupDestination();
		boolean isRemote = CommonUtil.isRemote(remotePath);
		try {
			if (needConnect && isRemote) {
				try {
					BaseReplicationCommand.connectToRemote(backupDestinationInfo, jobscript.getAFGuid(), -1);
				} catch (HAException e) {
					log.warn("Failed to connect to configuration destination " + backupDestinationInfo.getBackupDestination(), e);
				}
			}

			log.info("Get machine detail from backup destination " + backupDestinationInfo.getBackupDestination());
			NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
			VCMMachineInfo vcmMachineInfo = nativeFacade.getMachineDetailFromBackupSession(backupDestinationInfo.getBackupDestination());
			return vcmMachineInfo;
		} catch (Exception e) {
			log.warn("Exception occurred while getting machine detail " + e.getMessage(), e);
		} finally {
			if (needConnect && isRemote) {
				try {
					BaseReplicationCommand.closeRemoteConnect(backupDestinationInfo);
				} catch (Exception e) {
				}
			}
		}
		return null;
	}
	
	public boolean validatePlatformDestination(String afguid, long jobID, ReplicationJobScript replicationJobScript, CAVirtualInfrastructureManager vmwareOBJ) {
		List<VCMSavePolicyWarning> warningList = new ArrayList<VCMSavePolicyWarning>();
		try {
			HAService.getInstance().validatePlatformDestination(afguid, replicationJobScript, null, vmwareOBJ, warningList);
			if (warningList.size() > 0) {
				for (int i = 0; i < warningList.size(); i++) {
					String warnMsg = WebServiceErrorMessages.getServiceError(warningList.get(i).getWarningCode(), warningList.get(i).getWarningMessages());
					HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_WARNING, jobID, Constants.AFRES_AFJWBS_GENERAL,
							new String[] { warnMsg,	"", "", "", "" }, afguid);
				}
			}
			return true;
		} catch (ServiceException e) {
			log.error("Failed to validate hypervisor destination.");
			log.error(e.getMessage());
			String errMsg = WebServiceErrorMessages.getServiceError(e.getErrorCode(), e.getMultipleArguments());
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL,
					new String[] { errMsg,	"", "", "", "" }, afguid);
			return false;			
		} catch (Exception e) {
			log.error(e);
			return false;
		}
	}
	
	private boolean validateAdminAccount(ReplicationJobScript jobScript, long jobID, NativeFacade nativeFacade) {
		String accountName = "";
		try {
			// Validate admin account in the registry 
			// If system admin change the password, and D2D didn't update the password,
			// the replication job will fail 
			// so we check before the replication process
			// Related to RTC defect 140124
			Account account = nativeFacade.getAdminAccount();
			accountName = account.getUserName();
			nativeFacade.validateAdminAccount(account);
		} catch (Exception e) {
			log.error("Failed to validate Admin Account.", e);
			String hostName = HACommon.getRealHostName();
			String errorMessage = null;
			
			if (isVSBWithoutHASupport() && !jobScript.getBackupToRPS()) {
				if(!StringUtil.isEmptyOrNull(hostName)) {
					errorMessage = ReplicationMessage.getResource(
							ReplicationMessage.REPLICATION_FAILTO_VALIDATE_ADMIN_ACCOUNT_WITH_EDGEHOST,
							accountName, hostName);
				} else {
					errorMessage = ReplicationMessage.getResource(
							ReplicationMessage.REPLICATION_FAILTO_VALIDATE_ADMIN_ACCOUNT, accountName);
				}
			} else {
				errorMessage = ReplicationMessage.getResource("AFRES_AFREPC_" + ReplicationMessage.AFRES_AFREPC_SOCK_FAIL_AUTH, 
						accountName, hostName);
			}
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL,
					new String[] { errorMessage, "", "", "", "" }, jobScript.getAFGuid());
			return false;
		}
		
		return true;
	}
}

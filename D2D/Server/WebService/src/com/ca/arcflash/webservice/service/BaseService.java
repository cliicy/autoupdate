package com.ca.arcflash.webservice.service;

import java.util.Observable;
import java.util.Observer;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreHealthStatus;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreRunningState;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4D2D;
import com.ca.arcflash.service.jni.model.JActLogDetails;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.BackupInformationSummary;
import com.ca.arcflash.webservice.data.RPSInfo;
import com.ca.arcflash.webservice.data.RpsDataStoreHealthStatus;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.job.rps.BaseJobArg;
import com.ca.arcflash.webservice.data.job.rps.CatalogJobArg;
import com.ca.arcflash.webservice.data.job.rps.IJobDependency;
import com.ca.arcflash.webservice.data.job.rps.JobDependencySource;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;
import com.ca.arcflash.webservice.jni.model.JObjRet;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.rps.RPSServiceProxyManager;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class BaseService implements Observer {
	
	private static final Logger logger = Logger.getLogger(BaseService.class);
	
	public static final String JOB_GROUP_RESTORE_NAME			=	"RestoreJobGroup";
	public static final String JOB_NAME_RESTORE					=	"RestoreJob";
	public static final String JOB_NAME_COPYJOB					=	"CopyJob";
	
	public static final String JOB_GROUP_BACKUP_NAME			=	"BackupJobGroup";
	public static final String JOB_NAME_BACKUP_FULL				=	"FullBackupJob";
	public static final String JOB_NAME_BACKUP_INCREMENTAL		=	"IncrementalBackupJob";
	public static final String JOB_NAME_BACKUP_RESYNC			=	"ResyncBackupJob";
	public static final String JOB_NAME_BACKUP_NOW_SUFFIX       =   "$ONCE";
	
	public static final String JOB_GROUP_CATALOG_NAME           =  "CatalogJobGroup";
	public static final String JOB_NAME_OD_CATALOG             =   "OnDemandCatalogJob";
	public static final String JOB_NAME_CATALOG                 =   "CatalogJob";
	
	public static final String TRIGGER_GROUP_BACKUP_NAME		=	"BackupTriggerGroup";
	public static final String TRIGGER_NAME_BACKUP_FULL			=	"FullBackupTrigger";
	public static final String TRIGGER_NAME_BACKUP_INCREMENTAL	=	"IncremantalBackupTrigger";
	public static final String TIGGER_NAME_BACKUP_RESYNC		=	"ResyncBackupTrigger";

	public static final String TRIGGER_GROUP_BACKUP_NAME_FULL	=	"FullBackupTriggerGroup";
	public static final String TRIGGER_GROUP_BACKUP_NAME_INCREMENTAL =	"IncremantalBackupTriggerGroup";
	public static final String TRIGGER_GROUP_BACKUP_NAME_RESYNC	=	"ResyncBackupTriggerGroup";
	
	//AdvanceSchedule
	public static final String BACKUP_SCHEDULE_CALENDAR_NAME    =   "BackupCalendar";
	public static final String BACKUP_TRIGGER_NAME              =   "BackupTrigger";
	public static final String BACKUP_TYPE_FLAG                 =   "BackupTypeFlag";
	
	public static final String SUNDAY_FOR_DAILY_SCHEDULE        =   "SUN,";
	public static final String MONDAY_FOR_DAILY_SCHEDULE        =   "MON,";
	public static final String TUESDAY_FOR_DAILY_SCHEDULE        =  "TUE,";
	public static final String WEDNESDAY_FOR_DAILY_SCHEDULE     =   "WED,";
	public static final String THURSDAY_FOR_DAILY_SCHEDULE      =   "THU,";
	public static final String FRIDAY_FOR_DAILY_SCHEDULE        =   "FRI,";
	public static final String SATURDAY_FOR_DAILY_SCHEDULE        =   "SAT";
	
	
	// FOR D2D Archiver
	public static final String JOB_GROUP_ARCHIVE_BACKUP_NAME	=	"ArchiveBackupJobGroup";
	public static final String JOB_NAME_ARCHIVE_BACKUP			=	"FileCopyJob";
	public static final String JOB_GROUP_ARCHIVE_PURGE_NAME		=	"ArchivePurgeJobGroup";
	public static final String JOB_NAME_ARCHIVE_PURGE			=	"FileCopyPurgeJob";
	public static final String JOB_GROUP_ARCHIVE_RESTORE_NAME	=	"ArchiveRestoreJobGroup";
	public static final String JOB_NAME_ARCHIVE_RESTORE			=	"FileCopyRestoreJob";
	public static final String JOB_GROUP_ARCHIVE_CATALOGSYNC	=	"ArchiveCatalogSyncJobGroup";
	public static final String JOB_NAME_ARCHIVE_CATALOGSYNC		=	"FileCopyCatalogSyncJob";
	public static final String JOB_GROUP_ARCHIVE_SOURCEDELETE	=	"ArchiveSourceDeleteJobGroup";
	public static final String JOB_NAME_ARCHIVE_SOURCEDELETE	=	"FileArchiveSourceDeleteJob";
	public static final String JOB_GROUP_ARCHIVE_DELETE			=   "ArchiveDeleteJobGroup";
	public static final String JOB_NAME_ARCHIVE_DELETE			=	"FileArchiveDeleteJob";
	public static final String JOB_NAME_ARCHIVE_PURGE_FOR_FC	=	JOB_NAME_ARCHIVE_PURGE + "4FC";
	
	public static final String TRIGGER_GROUP_ARCHIVE_BACKUP		=	"ArchiveBackupTriggerGroup";
	public static final String TRIGGER_NAME_ARCHIVE_BACKUP		=	"ArchiveBackupTrigger";
	public static final String TRIGGER_GROUP_ARCHIVE_PURGE		=	"ArchivePurgeTriggerGroup";
	public static final String TRIGGER_NAME_ARCHIVE_PURGE		=	"ArchivePurgeTrigger";
	public static final String TRIGGER_GROUP_ARCHIVE_RESTORE	=	"ArchiveRestoreTriggerGroup";
	public static final String TRIGGER_NAME_ARCHIVE_RESTORE		=	"ArchiveRestoreTrigger";
	public static final String TRIGGER_GROUP_ARCHIVE_CATALOG	=	"ArchiveCatalogTriggerGroup";
	public static final String TRIGGER_NAME_ARCHIVE_CATALOG		=	"ArchiveCatalogTrigger";
	public static final String TRIGGER_GROUP_ARCHIVE_SOURCEDELETE	=	"ArchiveSourceDeleteTriggerGroup";
	public static final String TRIGGER_NAME_ARCHIVE_SOURCEDELETE	=	"ArchiveSourceDeleteTrigger";
	public static final String TRIGGER_GROUP_ARCHIVE_DELETE		=	"ArchiveDeleteTriggerGroup";
	public static final String TRIGGER_NAME_ARCHIVE_DELETE		=	"ArchiveDeleteTrigger";
	public static final String TRIGGER_NAME_ARCHIVE_PURGE_FOR_FC	=	TRIGGER_NAME_ARCHIVE_PURGE + "4FC";
	
	public static final String JOB_GROUP_ARCHIVE_SOURCEDELETE_MAKEUP = "ArchiveSourceDeleteMakeupJobGroup";
	public static final String TRIGGER_GROUP_ARCHIVE_SOURCEDELETE_MAKEUP = "ArchiveSourceDeleteMakeupTriggerGroup";
	public static final String JOB_GROUP_ARCHIVE_DELETE_MAKEUP	=	"ArchiveDeleteMakeupJobGroup";
	public static final String TRIGGER_GROUP_ARCHIVE_DELETE_MAKEUP	=	"ArchiveDeleteMakeupTriggerGroup";
	public static final String JOB_GROUP_ARCHIVE_MAKEUP_NAME		=	"ArchiveMakeupJobGroup";
	public static final String TRIGGER_GROUP_ARCHIVE_MAKEUP_NAME  	=	"ArchiveMakeupTriggerJobGroup";
	public static final String JOB_GROUP_ARCHIVE_PURGE_MAKEUP		= 	"ArchivePurgeMakeupJobGroup";
	public static final String TRIGGER_GROUP_ARCHIVE_PURGE_MAKEUP	=	"ArchivePurgeMakeupTriggerGroup";
	
	public static final String MANUAL_BACKUP_AS_BACKUPSET_FLAG = "ManualBackupSetStart";

	// RPS Merge job
	public static final String MERGE_JOB_GROUP					=	"MergeJobGroup";	// for merge job group and trigger group
	public static final String MERGE_JOB_NAME_PREFIX            =   "MergeJob_%s@%s";	    // for merge job name and trigger name
	public static final String MERGE_JOB_NAME_SUFFIX_ONCE		=	"_Once";            // scheduled by web service

	// RPS Replication job
	public static final String REPLICATION_JOB_GROUP				=	"ReplicationJobGroup";	// for replication job group and trigger group
	public static final String REPLICATION_JOB_NAME_PREFIX          =   "ReplicationJob_%s@%s";	    // for replication job name and trigger name
	public static final String REPLICATION_JOB_NAME_SUFFIX_ONCE		=	"_Once";            	// scheduled by web service
	//for rps information
	public static final String RPS_POLICY_UUID                  = "rpsPolicyUUID";
	public static final String RPS_DATASTORE_UUID               = "rpsDataStoreUUID";
	public static final String RPS_HOST                         = "rpsHost";
	public static final String RPS_DATASTORE_DISPLAY_NAME       = "rpsDataStoreDisplayName";
	
	public static final String JOB_ID = "jobID";
	public static final String JOB_TYPE = "jobType";
	
	public static final String ON_DEMAND_JOB					= "onDemandJob";
	
	public static final String RPS_CATALOG_GENERATION = "rpsCatalogGeneration";
    /**
     * @author Robin Gong
     * This is for edge, in that edge will need different behaviour than D2D. 
     * For example, if we want to validate the recovery point copy setting, we don't want to validate the license.  
     * This field will be set true by Edge's MockD2DServiceImpl
     */
    protected boolean edgeFlag = false;
    
	private ServiceException internalErrorAxisFault = generateAxisFault(FlashServiceErrorCode.Common_ErrorOccursInService);
	private static NativeFacade nativeFacade = null;

	private static String vmNameRegex = "[\\\\/:*?\"<>|]";
	
	public final static long ERROR_RWLOCK_BETWEEN_DNJOBS_AND_OTHERJOBS = -998;
	
	static {
		nativeFacade = new NativeFacadeImpl();
	}

	public NativeFacade getNativeFacade(){
		return nativeFacade;
	}

	protected ServiceException generateAxisFault(String errorCode){
		return new ServiceException("",errorCode);
	}
	
	protected ServiceException generateAxisFault(String message, String errorCode) {
		return new ServiceException(message, errorCode);
	}
	
	protected ServiceException generateInternalErrorAxisFault(){
		return internalErrorAxisFault;
	}

	@Override
	public void update(Observable o, Object arg) {
		//do nothing in base service		
	}

	public boolean isEdgeFlag() {
		return edgeFlag;
	}

	public void setEdgeFlag(boolean edgeFlag) {
		this.edgeFlag = edgeFlag;
	}
	
	protected void checkForMergeRunning(String jobType) throws ServiceException{
		if(MergeService.getInstance().isUseBackupSet()) {
			return;
		}
		if(MergeService.getInstance().isJobRunning()) {
			throw new ServiceException(FlashServiceErrorCode.Common_MergeJobRunning, 
					new String[] {WebServiceMessages.getResource("mergeJobRunning", 
							jobType)});
		}
	}
	
	
	public boolean canRunMergeNow(String[] dependencies, JobDependencySource source) 
				throws ServiceException {
		if(dependencies == null || dependencies.length == 0)
			return true;
		
		for(String depJobName : dependencies){
			IJobDependency job = ServiceUtils.getJobDependency(depJobName);
			if(job != null && job.needRun(source))
				return false;
		}
		
		return true;
	}
	
	public void launchCatalogDepenciesJob( CatalogJobArg arg) {
		String[] dependencies = arg.getJobDependencies();
		
		if(dependencies == null || dependencies.length == 0)
			return;
		
		for(String depJobName : dependencies) {
			if(IJobDependency.COPY_JOB.equals(depJobName))
				ArchiveService.getInstance().submitArchiveJob();
			else if(IJobDependency.FILECOPY_JOB.equals(depJobName))
				CopyService.getInstance().submitCopyJob(arg.getSessNum(),
						arg.getDestination(), arg.getUserName(),
						arg.getPassword());
		}
	}
	
	protected void setRPSInfo(BackupConfiguration configuration,
			BackupInformationSummary returnBackupInformationSummary) {
		
		if (!configuration.isD2dOrRPSDestType())
		{
			boolean bNotExist = false;
			RpsHost tempRPSHost = configuration.getBackupRpsDestSetting().getRpsHost();
			DataStoreHealthStatus dsHealth = DataStoreHealthStatus.UNKNOWN;
			DataStoreStatusListElem[] source = null;
			try {
				IRPSService4D2D client = RPSServiceProxyManager.getRPSServiceClient(tempRPSHost.getRhostname(), tempRPSHost.getUsername(),
						tempRPSHost.getPassword(), tempRPSHost.getPort(), tempRPSHost.isHttpProtocol() ? "http" : "https", tempRPSHost.getUuid());

				if (client != null) {
					dsHealth = client.getDataStoreHealthStatus(configuration.getBackupRpsDestSetting().getRPSDataStore());
					source = client.getDataStoreStatus(configuration.getBackupRpsDestSetting().getRPSDataStore());
				}
			} catch (Exception e) {
				logger.error("Failed to get data store status", e);
				if(e instanceof SOAPFaultException)
				{
					SOAPFaultException se = (SOAPFaultException)e;
					if(se.getFault().getFaultCode().equalsIgnoreCase(FlashServiceErrorCode.RPS_DATASTORE_INSTANCE_NOT_EXISTS))
						bNotExist = true;
				}
			}

			switch (dsHealth) {
			case GREEN:
				returnBackupInformationSummary.setDsHealth(RpsDataStoreHealthStatus.GREEN);
				break;
			case YELLOW:
				returnBackupInformationSummary.setDsHealth(RpsDataStoreHealthStatus.YELLOW);
				break;
			case RED:
				returnBackupInformationSummary.setDsHealth(RpsDataStoreHealthStatus.RED);
				break;
			case UNKNOWN:
				returnBackupInformationSummary.setDsHealth(RpsDataStoreHealthStatus.UNKNOWN);
				break;
			}

			DataStoreRunningState runningState = DataStoreRunningState.UNKNOWN;
			if(source!=null && source[0]!=null)
				runningState =	DataStoreRunningState.parseInt((int) source[0].getDataStoreStatus().getOverallStatus());
			else if(bNotExist)
				runningState = DataStoreRunningState.DELETED;
			
			returnBackupInformationSummary.setDsRunningState(runningState);				

			RPSInfo rpsinfo = new RPSInfo();
			rpsinfo.setRpsHostName(configuration.getBackupRpsDestSetting().getRpsHost().getRhostname());
			rpsinfo.setRpsPolicy(configuration.getBackupRpsDestSetting().getRPSPolicy());
			rpsinfo.setRpsDataStore(configuration.getBackupRpsDestSetting().getRPSDataStoreDisplayName());
			rpsinfo.setRpsDataStoreGuid(configuration.getBackupRpsDestSetting().getRPSDataStore());
			returnBackupInformationSummary.setRpsInfo(rpsinfo);
		}		
	}
	
	public String appendVMInfoIfNeeded(String destination, String vmInfo,
			String instanceUUID, boolean isLocalOrShareFolder) throws ServiceException {
		JObjRet<String> retObj;
		
		if(!StringUtil.isEmptyOrNull(instanceUUID)) {
			retObj = this.getNativeFacade().checkDestNeedVMInfo(
					destination, isLocalOrShareFolder?vmInfo:vmInfo+String.format("[%s]", instanceUUID), instanceUUID);
		} else {
			retObj = this.getNativeFacade().checkDestNeedVMInfo(
					destination, isLocalOrShareFolder?vmInfo:vmInfo, instanceUUID);
		}

		logger.debug("JObjRet<String> - hostName:" + retObj.getItem()
				+ ", retCode:" + retObj.getRetCode());

		if (retObj.getRetCode() == 0) {
			String hostName = retObj.getItem();
			if (hostName != null && hostName.trim().length() > 0) {
				if (destination.endsWith("\\") || destination.endsWith("/")) {
					destination += hostName;
				} else {
					destination += "\\" + hostName;
				}
			}
		}

		//logger.debug("dest" + destination);
		return destination;
	}
	
	public String filterVMName(String vmName){
		if(vmName == null || vmName.equals("")){
			return "";
		}
		return vmName.replaceAll(vmNameRegex, "");
	}
	
	public int handleErrorFromRPS(BaseJobArg arg) {
		String msg;
		if (arg.getErrorCode() == ERROR_RWLOCK_BETWEEN_DNJOBS_AND_OTHERJOBS) {
			String dataStoreName = "";
			try {
				BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
				dataStoreName = configuration.getBackupRpsDestSetting().getRPSDataStoreDisplayName();
			}catch(Exception e) {
				logger.error("Failed to get backup configuration " + e);
			}
			
		    msg = String.format(WebServiceMessages.getResource("RPSJobOnDataStoreSkippedBecausePurging")
				    , ServiceUtils.jobType2String(arg.getJobType()), dataStoreName);
	    
		    JActLogDetails logDetails = new JActLogDetails();
			logDetails.setProductType(1);
			logDetails.setJobID(0);
			logDetails.setJobType((int)arg.getJobType());
			logDetails.setJobMethod(0);
			logDetails.setLogLevel((int)Constants.AFRES_AFALOG_ERROR);
			logDetails.setIsVMInstance(arg.isVM());
			logDetails.setSvrNodeName(arg.getD2dServerName());
			logDetails.setSvrNodeID(arg.getD2dServerUUID());
			logDetails.setAgentNodeName(arg.getD2dServerName());
			logDetails.setAgentNodeID(arg.getD2dServerUUID());
			logDetails.setSourceRPSID("");
			logDetails.setTargetRPSID("");
			logDetails.setDSUUID("");
			logDetails.setTargetDSUUID("");
			
			nativeFacade.addLogActivityWithDetailsEx(logDetails, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg, "", "", "", "" });
		    return -1;
		}
		
		return 0;
	}
}

package com.ca.arcflash.webservice.scheduler;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jni.common.JJobHistory;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.RPSDataStoreInfo;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveJobScript;
import com.ca.arcflash.webservice.data.archive.RestoreArchiveJob;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.RetryPolicy;
import com.ca.arcflash.webservice.data.job.rps.ArchiveJobArg;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.DeleteArchiveService;
import com.ca.arcflash.webservice.service.MergeService;
import com.ca.arcflash.webservice.service.PurgeArchiveService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.internal.RSSItemXMLDAO;
import com.ca.arcflash.webservice.service.rps.JobService;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.RSSItem;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public abstract class BaseArchiveJob extends BaseJob{

	protected static String jobScriptName = "ArchiveJobSettings.Xml";
	private static final Logger logger = Logger.getLogger(BaseArchiveJob.class);

	public static final long LOCATION_AmazonS3 =0;
	public static final long LOCATION_WindowsAzure =1;
//	public static final long LOCATION_IronMountain =2;
//	public static final long LOCATION_I365 =3;
	public static final long LOCATION_FileSystem =4;
	public static final long LOCATION_Eucalyptus =5;
	public static final long LOCATION_CACould =6;
	
	public static int Job_Type_Archive = (int) JobType.JOBTYPE_FILECOPY_BACKUP;
	public static int Job_Type_ArchivePurge = (int) JobType.JOBTYPE_FILECOPY_PURGE;
	public static int Job_Type_ArchiveRestore = (int) JobType.JOBTYPE_FILECOPY_RESTORE;
	public static int Job_Type_ArchiveCatalogSync = (int) JobType.JOBTYPE_FILECOPY_CATALOGSYNC;
	public static int Job_Type_ArchiveSourceDelete = (int)JobType.JOBTYPE_FILECOPY_SOURCEDELETE;
	public static int Job_Type_ArchiveDelete = (int)JobType.JOBTYPE_FILECOPY_DELETE;

	public final static int JOBSTATUS_ACTIVE = 0;
	public final static int JOBSTATUS_FINISHED = 1;
	public final static int JOBSTATUS_CANCELLED = 2;
	public final static int JOBSTATUS_FAILED = 3;
	public final static int JOBSTATUS_INCOMPLETE = 4;
	public final static int JOBSTATUS_IDLE = 5;
	public final static int JOBSTATUS_WAITING = 6;
	public final static int JOBSTATUS_CRASH = 7;
	public final static int JOBSTATUS_MISSED = 10000;

	private long jobType;
	protected static JobExecutionContext missedPurgeJobContext;
	protected static Object missPurgeJobLock = new Object();
	
	protected static JobExecutionContext missedFCPurgeJobContext;
	protected static Object missFCPurgeJobLock = new Object();

	protected static JobExecutionContext missedFileCopyJobContextForPurge;
	protected static Object missedFileCopyJobLockForPurge = new Object();

	protected static JobExecutionContext missedFileCopyJobContextForRestore;
	protected static Object missedFileCopyJobLockForRestore = new Object();
	
	protected static JobExecutionContext missedFileArchiveJobContextForPurge;
	protected static Object missedFileArchiveJobLockForPurge = new Object();

	protected static JobExecutionContext missedFileArchiveJobContextForRestore;
	protected static Object missedFileArchiveJobLockForRestore = new Object();
	
	protected boolean isJobMonitorRun = false;
	public final long JobExitPhase = 0x20;
	protected int triggerTimes;

	//protected long shrmemid = 0;
	//protected long jobStatus = -1;
	//protected long jobPhase = 0;
	private volatile  boolean isJobRunning = false;
	//protected volatile  boolean isStopJM = false;

	//public Object phaseLock = new Object();

	private RSSItemXMLDAO rssItemXMLDAO = new RSSItemXMLDAO();

	public long getJobType(){
		return this.jobType;
	}

	public void setJobType(long jobType){
		this.jobType = jobType;
	}

	public void setJobStatus(long status){
		this.jobStatus = status;
	}
	public long getJobStatus(){
		return this.jobStatus;
	}
	
	protected boolean checkRpsPolicy4ArchiveJob(Boolean runNow,
			JobExecutionContext jobContext, ArchiveJobScript jobScript) {
		BackupRPSDestSetting setting = BackupService.getInstance().getRpsSetting();
//		RpsHost rpsHost = null;
		if(setting != null){
			rpsHost = setting.getRpsHost();
			rpsPolicyUUID = setting.getRPSPolicyUUID();
			rpsDataStoreUUID = setting.getRPSDataStore();
		}
		try {
			if(this.checkRPS4Job(runNow, rpsHost != null, rpsHost, rpsDataStoreUUID, jobContext, 
					JobType.JOBTYPE_CATALOG_FS, BackupService.getInstance().getNativeFacade())){
				ArchiveJobArg jobArg = this.getJobArg(jobContext.getJobDetail(), rpsPolicyUUID, setting.getRPSDataStore(), 
						this.getDefaultJobType(), jobScript);
				logger.debug("Submit job to rps");
				JobService.getInstance().submitArchive(jobArg, rpsHost);
				return true;
			}else {
				return false;
			}
		}catch(ServiceException se) {
			//TODO email alert
			return true;
		}
		
	}	

	protected ArchiveJobArg getJobArg(JobDetail jobDetail,
			String policyUUID,
			String datastoreUUID,
			long jobType,
			ArchiveJobScript jobScript){
		ArchiveJobArg jobArg = new ArchiveJobArg();
		this.getJobArg((JobDetailImpl)jobDetail, policyUUID, datastoreUUID, jobType, jobArg, 
				BackupService.getInstance().getDataStoreName());
		jobArg.setJobScript(jobScript);
		return jobArg;
	}
	
	protected boolean preprocess(ArchiveJobScript in_jobScript) {
		logger.debug("preprocess(JJobScript) - start");

		if (!getJobLock())
			return false;
		try {
			//shrmemid = CommonService.getInstance().getNativeFacade().getJobID();
			//shrmemid is initiated in BaseJob.initShrmemid, do not call JNI method to get job id here.
      // 
			long jobType = in_jobScript != null ? in_jobScript.getUsJobType() : this.getDefaultJobType();
			String planUUID = getPlanUUID("");
			
			JJobHistory jobHistory = new JJobHistory();
			jobHistory.setJobId(shrmemid);
			jobHistory.setJobType( (int)jobType );
			jobHistory.setDatastoreUUID(getRPSDatastoreUUID());
			
			RPSDataStoreInfo rpsDataStoreInfo = getRpsDataStoreInfo(getRPSDatastoreUUID());
			jobHistory.setDatastoreVersion(rpsDataStoreInfo.getVersion());
			jobHistory.setTargetUUID(rpsDataStoreInfo.getRpsServerId());
			
			jobHistory.setPlanUUID(isOnDemand ? "" : planUUID);
			ArchiveService.getInstance().getNativeFacade().updateJobHistory( jobHistory );
			
			logger.info("Processing jobid(shrmemid):" + shrmemid);
			in_jobScript.setUlShrMemID(shrmemid);
			// startJobMonitor();

			pauseMergeJob((int) jobType);
			logger.info("starting ArchiveJobMonitorThread");
			new Thread(new ArchiveJobMonitorThread(this)).start();
		} catch (RuntimeException e) {
			releaseJobLock();
			throw e;
		}

		logger.debug("preprocess(JJobScript) - end");
		return true;
	}

	/*protected void startJobMonitor(){
		new Thread(new ArchiveJobMonitorThread(this)).start();
		isJobMonitorRun = true;
	}

	public void stopJobMonitor(){
		isJobMonitorRun = false;
	}*/

	protected synchronized boolean getJobLock() {
		if(isJobRunning)
			return false;

		isJobRunning = true;
		return true;
	}

	protected synchronized void releaseJobLock()
	{
		isJobRunning = false;
	}

	static void makeupMissedPurge(){
		logger.debug("makeupMissedPurge() - begin");
		JobDetailImpl jobDetail = null;
		synchronized (missPurgeJobLock) {
			if (missedPurgeJobContext != null) {
				jobDetail = getMissedJobDetail(missedPurgeJobContext,
						BaseService.JOB_NAME_ARCHIVE_PURGE,
						BaseService.JOB_GROUP_ARCHIVE_PURGE_MAKEUP);
				SimpleTriggerImpl trigger = getMissedJobTrigger(
						BaseService.TRIGGER_NAME_ARCHIVE_PURGE,
						BaseService.TRIGGER_GROUP_ARCHIVE_PURGE_MAKEUP);
				try {
					PurgeArchiveService.getInstance().getScheduler().scheduleJob(jobDetail, trigger);
					PurgeArchiveService.getInstance().setMakeupJobs(jobDetail.getName(), false);
					missedPurgeJobContext = null;
				} catch (SchedulerException e) {
					logger.error("re-schedule purge job error: ", e);
				}
			}
		}
	}
	
	static void makeupMissedFCPurge(){
		logger.debug("makeupMissedFCPurge() - begin");
		JobDetailImpl jobDetail = null;
		synchronized (missFCPurgeJobLock) {
			if (missedFCPurgeJobContext != null) {
				jobDetail = getMissedJobDetail(missedFCPurgeJobContext,
						BaseService.JOB_NAME_ARCHIVE_PURGE_FOR_FC,
						BaseService.JOB_GROUP_ARCHIVE_PURGE_MAKEUP);
				SimpleTriggerImpl trigger = getMissedJobTrigger(
						BaseService.TRIGGER_NAME_ARCHIVE_PURGE_FOR_FC,
						BaseService.TRIGGER_GROUP_ARCHIVE_PURGE_MAKEUP);
				try {
					PurgeArchiveService.getInstance().getScheduler().scheduleJob(jobDetail, trigger);
					PurgeArchiveService.getInstance().setMakeupJobs(jobDetail.getName(), true);
					missedFCPurgeJobContext = null;
				} catch (SchedulerException e) {
					logger.error("re-schedule FC purge job error: ", e);
				}
			}
		}
	}
	
	static JobDetailImpl getMissedJobDetail(JobExecutionContext context, String jobName, String makeupJobGroup){
		JobDetailImpl jobDetail = (JobDetailImpl) context.getJobDetail();
		jobDetail.setName(jobName + "_Missed");
		jobDetail.setGroup(makeupJobGroup);
		return jobDetail;
	}

	static SimpleTriggerImpl getMissedJobTrigger(String name, String group){
		SimpleTriggerImpl trig = new SimpleTriggerImpl();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 30);
		Date startDate = cal.getTime();
		trig.setStartTime(startDate);
		trig.setRepeatCount(0);
		trig.setRepeatInterval(0);
		trig.setName(name);
		trig.setGroup(group);
		return trig;
	}

	static void makeupMissedFileCopy(){
		logger.debug("makeupMissedFileCopy() - begin");
		JobDetailImpl jobDetail = null;
		synchronized (missedFileCopyJobLockForPurge) {
			if (missedFileCopyJobContextForPurge != null) {
				jobDetail = getMissedJobDetail(
						missedFileCopyJobContextForPurge,
						BaseService.JOB_NAME_ARCHIVE_BACKUP,
						BaseService.JOB_GROUP_ARCHIVE_MAKEUP_NAME);
				SimpleTriggerImpl trigger = getMissedJobTrigger(
						BaseService.TRIGGER_NAME_ARCHIVE_BACKUP,
						BaseService.TRIGGER_GROUP_ARCHIVE_MAKEUP_NAME);
				try {
					ArchiveService.getInstance().getScheduler().scheduleJob(jobDetail, trigger);
					ArchiveService.getInstance().addMakeUpJobNames(jobDetail.getName());
					missedFileCopyJobContextForPurge = null;
				} catch (SchedulerException e) {
					logger.error("re-schedule file copy job error: ", e);
				}
				
			}
		}
		
		synchronized (missedFileCopyJobLockForRestore) {
			if (missedFileCopyJobContextForRestore != null) {
				jobDetail = getMissedJobDetail(
						missedFileCopyJobContextForRestore,
						BaseService.JOB_NAME_ARCHIVE_BACKUP,
						BaseService.JOB_GROUP_ARCHIVE_MAKEUP_NAME);
				SimpleTriggerImpl trigger = getMissedJobTrigger(
						BaseService.TRIGGER_NAME_ARCHIVE_BACKUP,
						BaseService.TRIGGER_GROUP_ARCHIVE_MAKEUP_NAME);
				try {
					ArchiveService.getInstance().deleteMakeupSchedules();
					ArchiveService.getInstance().getScheduler().scheduleJob(jobDetail, trigger);
					ArchiveService.getInstance().addMakeUpJobNames(jobDetail.getName());
					missedFileCopyJobLockForRestore = null;
				} catch (SchedulerException e) {
					logger.error("re-schedule file copy job error: ", e);
				}
			}
		}

	}
	
	static void makeupMissedFileArchive(){
		logger.debug("makeupMissedFileArchive() - begin");
		JobDetailImpl jobDetail = null;
		synchronized (missedFileArchiveJobLockForPurge) {
			if (missedFileArchiveJobContextForPurge != null) {
				jobDetail = getMissedJobDetail(
						missedFileArchiveJobContextForPurge,
						BaseService.JOB_NAME_ARCHIVE_SOURCEDELETE,
						BaseService.JOB_GROUP_ARCHIVE_SOURCEDELETE_MAKEUP);
				SimpleTriggerImpl trigger = getMissedJobTrigger(
						BaseService.TRIGGER_NAME_ARCHIVE_SOURCEDELETE,
						BaseService.TRIGGER_GROUP_ARCHIVE_SOURCEDELETE_MAKEUP);
				try {
					DeleteArchiveService.getInstance().deleteMakeupSchedule();
					DeleteArchiveService.getInstance().getScheduler().scheduleJob(jobDetail, trigger);
					DeleteArchiveService.getInstance().addDeleteMakeupJobName(jobDetail.getName());
					missedFileArchiveJobContextForPurge = null;
				} catch (SchedulerException e) {
					logger.error("re-schedule file archive job error: ", e);
				}
			}
		}
		
		synchronized (missedFileArchiveJobLockForRestore) {
			if (missedFileArchiveJobContextForRestore != null) {
				jobDetail = getMissedJobDetail(
						missedFileArchiveJobContextForRestore,
						BaseService.JOB_NAME_ARCHIVE_SOURCEDELETE,
						BaseService.JOB_GROUP_ARCHIVE_SOURCEDELETE_MAKEUP);
				SimpleTriggerImpl trigger = getMissedJobTrigger(
						BaseService.TRIGGER_NAME_ARCHIVE_SOURCEDELETE,
						BaseService.TRIGGER_GROUP_ARCHIVE_SOURCEDELETE_MAKEUP);
				try {
					DeleteArchiveService.getInstance().deleteMakeupSchedule();
					DeleteArchiveService.getInstance().getScheduler().scheduleJob(jobDetail, trigger);
					DeleteArchiveService.getInstance().addDeleteMakeupJobName(jobDetail.getName());
					missedFileArchiveJobLockForRestore = null;
				} catch (SchedulerException e) {
					logger.error("re-schedule file archive job error: ", e);
				}
			}
		}

	}

		protected void sendEmail(JJobMonitor jJM) {
			logger.debug("sendEmail - start");
			try{
				// BackupEmail email = BackupService.getInstance().getBackupConfiguration().getEmail();
				PreferencesConfiguration preferencesConfig = CommonService.getInstance().getPreferences();
				if (preferencesConfig == null) {
					return;
				}
				
				BackupEmail email = preferencesConfig.getEmailAlerts();
				if (email == null || !email.isEnableSettings())
					return;

				boolean sendEmail = false;
				if ((jJM.getUlJobStatus() == JOBSTATUS_FAILED ||
						jJM.getUlJobStatus() == JOBSTATUS_CRASH ||
						jJM.getUlJobStatus() == JOBSTATUS_CANCELLED) && email.isEnableEmail())
				{
					logger.debug("job is failed, cancelled or crashed => send email");
					sendEmail = true;
				}
				else if ((jJM.getUlJobStatus() == JOBSTATUS_FINISHED) && (email.isEnableEmailOnSuccess()))
				{
					logger.debug("job is successful => send email");
					sendEmail = true;
				}

				if (sendEmail){
					//try to get hostname
					String url = getSettingsURL();//getServerURL();

					EmailSender emailSender = new EmailSender();
					//add job status in subject to fix 18911769
					String jobStatus = EmailContentTemplate.jobStatus2String(jJM.getUlJobStatus(), this.shrmemid, jJM.getUlJobType());
					String emailJobStatus = WebServiceMessages.getResource("EmailJobStatus");
					String hostName = ServiceContext.getInstance().getLocalMachineName();
					//Adding Job Type to Email Subject
					long jobType = jJM.getUlJobType();
					String jobTypeString = null;
					if (jobType == Constants.AF_JOBTYPE_BACKUP)
					{
						jobTypeString = EmailContentTemplate.backupType2String(jJM.getUlJobMethod())+ " ";
					}
					else if(jobType == Constants.AF_JOBTYPE_VM_BACKUP ||
							jobType == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
							jobType == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP ||
							jobType == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP)
					{
						jobTypeString = WebServiceMessages.getResource("VSphereBackupJob") + "-" +EmailContentTemplate.backupType2String(jJM.getUlJobMethod())+ " ";						
					}
					else if (jobType == Constants.AF_JOBTYPE_RESTORE)
					{
						jobTypeString = WebServiceMessages.getResource("RestoreJob")+ " ";	
						
					}
					else if (jobType == Constants.AF_JOBTYPE_COPY)
					{
						jobTypeString = WebServiceMessages.getResource("CopyJob")+ " ";	
						
					}
					else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_BACKUP)
					{
						jobTypeString = WebServiceMessages.getResource("FileCopyJob")+ " ";
						
					}
					else if(jobType == Constants.AF_JOBTYPE_ARCHIVE_SOURCEDELETE)
					{
						jobTypeString = WebServiceMessages.getResource("FileArchiveJob")+ " ";
					}
					else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_RESTORE)
					{
						jobTypeString = WebServiceMessages.getResource("ArchiveRestoreJob")+ " ";
						
					}
					else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_PURGE)
					{
						jobTypeString = WebServiceMessages.getResource("ArchivePurgeJob")+ " ";
						
					}	
					else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_CATALOGSYNC)
					{
						jobTypeString = WebServiceMessages.getResource("ArchiveCatalogReSyncJob")+ " ";
						
					}	
					else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_SOURCEDELETE)
					{
						jobTypeString = WebServiceMessages.getResource("ArchiveSourceDeleteJob")+ " ";
						
					}					
					String emailSubject = email.getSubject() + "-"+ jobTypeString +emailJobStatus+jobStatus+"("+hostName+")";
					String archivesize = null;
					long archiveSizeinBytes = jJM.getUlXferBytesJob();
					archivesize = ServiceUtils.bytes2String(Long.parseLong(archiveSizeinBytes+""));
					emailSender.setSubject(emailSubject);

					Date startTime = new Date(jJM.getUlBackupStartTime());
					String startString = EmailContentTemplate.formatDate(startTime);

					/** for email alert PR */

					//Create the HTML template
					String strDest = getArchiveDestination();
					if (StringUtil.isEmptyOrNull(strDest)){
						 logger.error("invalid param: archive destination is null.");
						 return;
					}

					if (email.isEnableHTMLFormat())
					{
						if(archivesize != null)
						emailSender.setContent(EmailContentTemplate.getHtmlContent(jJM,shrmemid,startString,strDest,getActivityLogResult(shrmemid),url));
						else 
						emailSender.setContent(EmailContentTemplate.getHtmlContent(jJM.getUlJobStatus(), jJM.getUlJobType(),
							jJM.getUlJobMethod(), shrmemid, startString, null, strDest, getActivityLogResult(shrmemid),url));
					}
					else
					{
						/*emailSender.setContent(EmailContentTemplate.getPlainTextContent(jJM.getUlJobStatus(), jJM.getUlJobType(),
								jJM.getUlJobMethod(), shrmemid, startString, strDest, getActivityLogResult(shrmemid),url));*/
						emailSender.setContent(EmailContentTemplate.getPlainTextContent(jJM,shrmemid,startString,strDest,getActivityLogResult(shrmemid),url));
					}
					
					/** for promoting alert message  to edge server */
					emailSender.setJobStatus(jJM.getUlJobStatus());
					emailSender.setProductType(CommonEmailInformation.PRODUCT_TYPE.ARCFlash.getValue());

					if(jJM.getUlJobStatus() == JOBSTATUS_FAILED ||
							jJM.getUlJobStatus() == JOBSTATUS_CRASH ||
							jJM.getUlJobStatus() == Constants.JOBSTATUS_LICENSE_FAILED){
							emailSender.setHighPriority(true);
					}
					//"To log on the server to make changes or fix job settings - click here. You can also submit a backup now"
					/** Alert email ehan PR */
					emailSender.setUseSsl(email.isEnableSsl());
					emailSender.setSmptPort(email.getSmtpPort());
					emailSender.setMailPassword(email.getMailPassword());
					emailSender.setMailUser(email.getMailUser());
					emailSender.setUseTls(email.isEnableTls());
					emailSender.setProxyAuth(email.isProxyAuth());
					emailSender.setMailAuth(email.isMailAuth());

					emailSender.setFromAddress(email.getFromAddress());
					emailSender.setRecipients(email.getRecipientsAsArray());
					emailSender.setSMTP(email.getSmtp());
					emailSender.setEnableProxy(email.isEnableProxy());
					emailSender.setProxyAddress(email.getProxyAddress());
					emailSender.setProxyPort(email.getProxyPort());
					emailSender.setProxyUsername(email.getProxyUsername());
					emailSender.setProxyPassword(email.getProxyPassword());
					emailSender.setJobType(getDefaultJobType());
					emailSender.sendEmail(email.isEnableHTMLFormat());
				}
			}catch(Exception e){
				logger.error("Error in sending email", e);
			}

			

			logger.debug("sendEmail - end");
		}
		
		/**
		 * this method should be overwrited by subclass  
		 * @return
		 */
		protected String getArchiveDestination() {
			String strDest = null;
			try {
				ArchiveConfiguration configuration = ArchiveService.getInstance().getArchiveConfiguration();
				if (configuration == null)
					return null;
				if(configuration.isbArchiveToCloud())
					strDest = configuration.getCloudConfig().getcloudVendorURL() + "\\" + configuration.getCloudConfig().getcloudBucketName();
				else if(configuration.isbArchiveToDrive())
					strDest = configuration.getStrArchiveToDrivePath();
			} catch (ServiceException e) {
				logger.info(e);;
			}
			return strDest;
		}

		private void copyRssToTarget(String srcPath, String targetPath, String rssXML, String srcFile) throws IOException {
			StringUtil.copy(srcPath + rssXML, targetPath + rssXML);
			StringUtil.copy(srcPath + srcFile, targetPath + srcFile);

			File targFolder = new File(targetPath);
			File[] htmlFiles = targFolder.listFiles(new HTMLFileFilter());

			if (htmlFiles.length > Constants.FAILED_JOB_RSS_MAX) {
				File oldest = null;
				for (int i = 0; i < htmlFiles.length; i++) {
					if (i == 0 && htmlFiles[i] != null) {
						oldest = htmlFiles[i];
					} else if (oldest.lastModified() > htmlFiles[i].lastModified()) {
						oldest = htmlFiles[i];
					}
				}
				if (oldest.delete()) {
					logger.debug("Oldest RSS file = " + oldest.getName());
					logger.info("Successfully deleted oldest file");
				} else {
					logger.error("Failed to delete oldest failed job html file");
				}
			}
		}

		protected void sendEmailOnMissedJob(JobExecutionContext context, long jobMethod) {
			logger.debug("sendEmail - start");
			try {
				BackupConfiguration configuration = BackupService.getInstance()
						.getBackupConfiguration();
				if (configuration == null)
					return;

				//BackupEmail email = configuration.getEmail();
				PreferencesConfiguration preferencesConfig = CommonService.getInstance().getPreferences();
				if (preferencesConfig == null) {
					return;
				}
				
				BackupEmail email = preferencesConfig.getEmailAlerts();
				if (email == null || !email.isEnableSettings())
					return;

				if (email.isEnableEmailOnMissedJob()) {
					EmailSender emailSender = new EmailSender();
					String jobStatus = EmailContentTemplate.jobStatus2String(JOBSTATUS_MISSED);
					String emailJobStatus = WebServiceMessages
							.getResource("EmailJobStatus");
					String hostName = ServiceContext.getInstance()
							.getLocalMachineName();
					String jobTypeString = null;
					long jobType = jobMethod;
					if (jobType == Constants.AF_JOBTYPE_BACKUP)
					{
						jobTypeString = EmailContentTemplate.backupType2String(jobMethod)+ " ";
					}
					else if(jobType == Constants.AF_JOBTYPE_VM_BACKUP ||
							jobType == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP ||
							jobType == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
							jobType == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP)
					{
						jobTypeString = WebServiceMessages.getResource("VSphereBackupJob") + "-" +EmailContentTemplate.backupType2String(jobMethod)+ " ";						
					}
					else if (jobType == Constants.AF_JOBTYPE_RESTORE)
					{
						jobTypeString = WebServiceMessages.getResource("RestoreJob")+ " ";	
						
					}
					else if (jobType == Constants.AF_JOBTYPE_COPY)
					{
						jobTypeString = WebServiceMessages.getResource("CopyJob")+ " ";	
						
					}
					else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_BACKUP)
					{
						jobTypeString = WebServiceMessages.getResource("FileCopyJob")+ " ";
						
					}
					else if(jobType == Constants.AF_JOBTYPE_ARCHIVE_SOURCEDELETE)
					{
						jobTypeString = WebServiceMessages.getResource("FileArchiveJob")+ " ";
					}
					else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_RESTORE)
					{
						jobTypeString = WebServiceMessages.getResource("ArchiveRestoreJob")+ " ";
						
					}
					else if (jobType == Constants.AF_JOBTYPE_ARCHIVE_PURGE)
					{
						jobTypeString = WebServiceMessages.getResource("ArchivePurgeJob")+ " ";
						
					}
					String emailSubject = email.getSubject() + "-" +jobTypeString + emailJobStatus
							+ jobStatus + "(" + hostName + ")";

					emailSender.setSubject(emailSubject);

					emailSender.setContent(getMissedJobContent(email
							.isEnableHTMLFormat(), context, jobMethod));

					
					/** for promoting alert message  to edge server */
					emailSender.setJobStatus(JOBSTATUS_MISSED);
					emailSender.setProductType(CommonEmailInformation.PRODUCT_TYPE.ARCFlash.getValue());

					
					emailSender.setUseSsl(email.isEnableSsl());
					emailSender.setSmptPort(email.getSmtpPort());
					emailSender.setMailPassword(email.getMailPassword());
					emailSender.setMailUser(email.getMailUser());
					emailSender.setUseTls(email.isEnableTls());
					emailSender.setProxyAuth(email.isProxyAuth());
					emailSender.setMailAuth(email.isMailAuth());

					emailSender.setFromAddress(email.getFromAddress());
					emailSender.setRecipients(email.getRecipientsAsArray());
					emailSender.setSMTP(email.getSmtp());
					emailSender.setEnableProxy(email.isEnableProxy());
					emailSender.setProxyAddress(email.getProxyAddress());
					emailSender.setProxyPort(email.getProxyPort());
					emailSender.setProxyUsername(email.getProxyUsername());
					emailSender.setProxyPassword(email.getProxyPassword());

					emailSender.setJobType( getDefaultJobType() );
					emailSender.sendEmail(email.isEnableHTMLFormat());
				}
			} catch (Exception e) {
				logger.error("Error in sending email", e);
			}
		}

		protected void saveRSSFeed(JJobMonitor jJM) {
			//FAILED JOB RSS
			if (jJM.getUlJobStatus() == JOBSTATUS_FAILED ||
					jJM.getUlJobStatus() == JOBSTATUS_CRASH ||
					jJM.getUlJobStatus() == JOBSTATUS_CANCELLED)
			{
				try {
					String url = getServerURL();

					BackupConfiguration configuration;

						configuration = BackupService.getInstance().getBackupConfiguration();

					if (configuration == null)
						return;

					Date startTime = new Date(jJM.getUlBackupStartTime());
					String startString = EmailContentTemplate.formatDate(startTime);
					String html = EmailContentTemplate.getHtmlContent(jJM.getUlJobStatus(), jJM.getUlJobType(),
							jJM.getUlJobMethod(), shrmemid, startString, null, configuration.getDestination(), getActivityLogResult(shrmemid),url);

					//Save this string to a new html file
					try {
						String folderPath = ServiceContext.getInstance().getDataFolderPath();
						String filePath = folderPath + "\\job" + startTime.getTime() + ".html";
						String fileURL = url + "/job" + startTime.getTime()	+ ".html";
						String rssXML = folderPath + "\\jobfeed.xml";

						File file = new File(filePath);
						if (!file.exists()) {
							file.createNewFile();
						}

						FileWriter fw = null;
						try {
							fw = new FileWriter(file);
							fw.write(html);
							fw.flush();
							fw.close();
						}
						catch (Exception e)
						{
							logger.error("Error saving error content to file for RSS ", e);
						}

						//if there are more than X html files, delete the last one
						File folder = new File(folderPath);
						HTMLFileFilter filter = new HTMLFileFilter();
						File[] allFiles = folder.listFiles(filter);

						if (allFiles.length > Constants.FAILED_JOB_RSS_MAX)
						{
							File oldest = null;
							for (int i = 0; i < allFiles.length; i++)
							{
								if (i == 0 && allFiles[i] != null)
								{
									oldest = allFiles[i];
								}
								else if (oldest.lastModified() > allFiles[i].lastModified())
								{
									oldest = allFiles[i];
								}
							}
							if (oldest.delete())
							{
								logger.debug("Oldest RSS file = " + oldest.getName());
								rssItemXMLDAO.removeItem(rssXML, oldest.getName());
								logger.info("Successfully deleted oldest file");
							}
							else
							{
								logger.error("Failed to delete oldest failed job html file");
							}
						}

						RSSItem rssItem = new RSSItem();
						String jobStatus = EmailContentTemplate.jobStatus2String(jJM.getUlJobStatus());
						String jobTypeString;
						if (jJM.getUlJobType() == Constants.AF_JOBTYPE_ARCHIVE_BACKUP)
						{
							jobTypeString = WebServiceMessages.getResource("FileCopyJob");
						}
						else if(jJM.getUlJobType() == Constants.AF_JOBTYPE_ARCHIVE_SOURCEDELETE)
						{
							jobTypeString = WebServiceMessages.getResource("FileArchiveJob");
						}
						else if (jJM.getUlJobType() == Constants.AF_JOBTYPE_ARCHIVE_RESTORE)
						{
							jobTypeString = WebServiceMessages.getResource("ArchiveRestoreJob");
						}
						else
						{
							jobTypeString = WebServiceMessages.getResource("ArchivePurgeJob");
						}

						String emailJobStatus = WebServiceMessages.getResource("EmailJobStatus");
						String title = WebServiceMessages.getResource("EmailJobType") + jobTypeString + ", " +
							emailJobStatus+jobStatus+" " + " ("+startString+")";
						rssItem.setTitle(title);
						rssItem.setLink(fileURL);

						//Take the contents of the folder and create an RSS xml file
						rssItemXMLDAO.addRSSItem(rssXML, rssItem);

						// Copy to target TOMCAT\webapps\ROOT
						String targetPath = ServiceContext.getInstance().getTomcatFilePath();
						copyRssToTarget(folderPath, targetPath, "\\jobfeed.xml", "\\job"+ startTime.getTime() + ".html");
					}
					catch (Exception e)
					{
						logger.error("Error saving error content to file for RSS - ", e);
					}

				} catch (ServiceException e) {
					logger.error("Error in saving RSS feed - ", e);
				}

			}
		}
		
		private String getMissedJobContent(boolean isEnableHTMLFormat,
				JobExecutionContext context, long jobMethod) {
			String exeDate = BackupConverterUtil.dateToString(context.getFireTime());

			String dest = null;
			try {
				if(ArchiveService.getInstance().getArchiveConfiguration().isbArchiveToCloud())
					dest = ArchiveService.getInstance().getArchiveDestinationConfig().getCloudConfig().getcloudVendorURL();
				else
					dest = ArchiveService.getInstance().getArchiveDestinationConfig().getStrArchiveToDrivePath();
			} catch (ServiceException e) {
				logger.error("get archive destinatin error.", e);
				
				return null;
			}

			String url = getSettingsURL();			
			
			if(isEnableHTMLFormat){
				return 	EmailContentTemplate.getHtmlContent(JOBSTATUS_MISSED, jobMethod, jobMethod, -1, exeDate, null, dest, null, url);
			}else{
				return EmailContentTemplate.getPlainTextContent(JOBSTATUS_MISSED, jobMethod, jobMethod, -1, exeDate, null, dest, null, url);
			}
		}

		private class HTMLFileFilter implements FileFilter
		{
			@Override
			public boolean accept(File pathname) {
				return (pathname.getName().endsWith("html") && pathname.getName().startsWith("job"));
			}
		}

		private ActivityLogResult getActivityLogResult(long jobid) {
			try {
				logger.debug("getActivityLogResult - jobid = " + jobid);
				return CommonService.getInstance().getJobActivityLogs(jobid, 0, 512);
			} catch (Exception e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
			}
			return null;
		}

/*		protected void updateJobMonitor(JJobMonitor jJM) {
			logger.debug("updateJobMonitor(JJobMonitor) - start");

			JobMonitor jmon = ArchiveService.getInstance().getJobMonitorInternal();
			synchronized (jmon) {
				jmon.setId(shrmemid);
				jmon.setBackupStartTime(jJM.getUlBackupStartTime());
				jmon.setCurrentProcessDiskName(jJM.getWszDiskName());
				jmon.setEstimateBytesDisk(jJM.getUlEstBytesDisk());
				jmon.setEstimateBytesJob(jJM.getUlEstBytesJob());
				jmon.setFlags(jJM.getUlFlags());
				jmon.setJobMethod(jJM.getUlJobMethod());
				jmon.setJobPhase(jJM.getUlJobPhase());
				jmon.setJobStatus(jJM.getUlJobStatus());
				jmon.setJobType(jJM.getUlJobType());
				jmon.setSessionID(jJM.getUlSessionID());
				jmon.setTransferBytesDisk(jJM.getUlXferBytesDisk());
				jmon.setTransferBytesJob(jJM.getUlXferBytesJob());
				jmon.setElapsedTime(jJM.getUlElapsedTime());
				jmon.setVolMethod(jJM.getUlVolMethod());
			}

			logger.debug("updateJobMonitor(JJobMonitor) - end");
		}*/

		//protected abstract void runJob();
		protected abstract void afterComplete(JJobMonitor jJM);

		private class ArchiveJobMonitorThread extends JobMonitorThread {
			BaseArchiveJob archiveJob = null;
			public ArchiveJobMonitorThread(BaseArchiveJob job){
				super(job);
				archiveJob = job;
			}
			
			@Override
			protected void optionalUpdate(JJobMonitor jJM) {}
			
			@Override
			protected void afterComplete(JJobMonitor jJM) {
				synchronized (phaseLock) {
					 if(jJM!=null)
					 {						 
						 jobStatus = jJM.getUlJobStatus();						
					 }
					 jobPhase = JobExitPhase;
					 logger.info("notify for job end");
					 phaseLock.notifyAll();
					 MergeService.getInstance().jobEnd(job);
					 startMergeJob(jJM);
				 }
				try {
					if (jobMonitorHandle != 0) {
						CommonService.getInstance().getNativeFacade()
								.releaseJobMonitor(jobMonitorHandle);
					}
				} catch (Throwable e) {
					logger.error("Error when release job monitor", e);
				}
				
				releaseJobLock();
				archiveJob.afterComplete(jJM);
			}
			/**
			 * Logger for this class
			 */

	}
		
	protected String parseFileCopyLocation(RestoreArchiveJob job) {
		String fileCopyLocation = null;
		try {
			if (job.getArchiveDestType() == LOCATION_FileSystem) {
				fileCopyLocation = job.getArchiveDiskInfo()
						.getArchiveDiskDestPath();
			} else {
				fileCopyLocation = job.getArchiveCloudInfo()
						.getcloudVendorURL()
						+ "\\"
						+ job.getArchiveCloudInfo().getcloudBucketName();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return fileCopyLocation;
	}

	protected RetryPolicy getRetryPolicy(String jobTypeName){
		RetryPolicy retryPolicy = CommonService.getInstance().getRetryPolicy(jobTypeName);
		if(retryPolicy == null || !validateRetryPolicy(retryPolicy)){
			return getDefaultRetryPolicy(jobTypeName);
		}
		return retryPolicy;
	}
	
	private RetryPolicy getDefaultRetryPolicy(String jobTypeName){
		logger.debug("get default retry policy for " + jobTypeName);
		RetryPolicy retryPolicy = new RetryPolicy();
		retryPolicy.setMaxTimes(1);
		retryPolicy.setEnabled(true);
		retryPolicy.setFailedEnabled(true);
		retryPolicy.setImmediately(false);
		retryPolicy.setJobType(jobTypeName);
		retryPolicy.setTimeToWait(30);
		retryPolicy.setMissedEnabled(false);
		return retryPolicy;
	}
	
	private boolean validateRetryPolicy(RetryPolicy retryPolicy){
		if(retryPolicy == null)
			return false;
		if(retryPolicy.isEnabled()){
			if(retryPolicy.getTimeToWait() < 15){
				logger.warn("invalid param - TimeToWait should not be less than 15");
				return false;
			}
			if(retryPolicy.getMaxTimes() < 1){
				logger.warn("invalid param - MaxTimes should not be less than 1");
				return false;
			}
		}
		return true;
	}
}



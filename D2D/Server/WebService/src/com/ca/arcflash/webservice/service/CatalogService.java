package com.ca.arcflash.webservice.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import sun.misc.BASE64Encoder;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jni.common.JCatalogJobScriptInfo;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.service.common.CatalogQueueType;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.RetryPolicy;
import com.ca.arcflash.webservice.data.catalog.CatalogJobPara;
import com.ca.arcflash.webservice.data.job.rps.CatalogJobArg;
import com.ca.arcflash.webservice.data.job.rps.IJobDependency;
import com.ca.arcflash.webservice.data.job.rps.JobDependencySource;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.scheduler.BaseBackupJob;
import com.ca.arcflash.webservice.scheduler.BaseJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.CatalogJob;
import com.ca.arcflash.webservice.scheduler.OndemandCatalogJob;
import com.ca.arcflash.webservice.service.rps.JobService;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class CatalogService extends BaseService implements IJobDependency{
	
	private Scheduler scheduler;
	
	private static CatalogService instance = new CatalogService();
	
	public static final int FSCAT_DISABLED = 0x03;
	public static final int FSCAT_PENDING = 0x02;
	public static final int FSCAT_FINISH =  0x01;
	public static final int FSCAT_FAIL = 0x00;
	public static final int FSCAT_NOTCREATE =  -0x01;
	
	public static final String JOB_ID = "jobID";
	
	public static final String QUEUE_TYPE = "queueType";
	
	public static final String WAIT_TIME = "makeupWait";
	
	public static final String JOB_TYPE = "jobType";
	
	public static final String DESTINATION = "destination";
	public static final String USERNAME = "userName";
	public static final String PASSWORD = "password";
	public static final String RESUMED  = "resumed";	
	
	private CatalogJob makeupJob = null;
	
	private boolean submitCatalog = false;
	
	private Logger logger = Logger.getLogger(CatalogService.class);
	
	private CatalogService() {
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
	}
	
	public static CatalogService getInstance(){
		return instance;
	}
	
	/**
	 * schedule a catalog job
	 * @param jobID jobID: for common situations, it's -1, 
	 * 	      for resume job after restart it's the value get from backend
	 * @param startTime time to wait before launch the job in milliseconds
	 */
	public void startCatalogJob(long jobID, long startTime, long queueType, long jobType){
		startCatalogJob(jobID, startTime, queueType, null, null, null, jobType);
	}
	
	/**
	 * schedule a catalog job
	 * @param jobID jobID: for common situations, it's -1, 
	 * 	      for resume job after restart it's the value get from backend
	 * @param startTime time to wait before launch the job in milliseconds
	 */
	public void startCatalogJob(long jobID, long startTime, long queueType, String destination, 
			String userName, String password, long jobType){
		if (disabledPlan())
			return;
		
		if(CatalogJob.isJobRunning()){
			logger.info("Catalog job is running, no need to schedule it again");
			return;
		}
		
		try {
			JobDetailImpl jobDetail = new JobDetailImpl(JOB_NAME_CATALOG, JOB_GROUP_CATALOG_NAME, CatalogJob.class);
			jobDetail.getJobDataMap().put(CatalogService.JOB_ID, jobID);
			jobDetail.getJobDataMap().put(CatalogService.WAIT_TIME, startTime);
			jobDetail.getJobDataMap().put(CatalogService.QUEUE_TYPE, queueType);
			jobDetail.getJobDataMap().put(CatalogService.JOB_TYPE, jobType);
			jobDetail.getJobDataMap().put(DESTINATION, destination);
			jobDetail.getJobDataMap().put(USERNAME, userName);
			jobDetail.getJobDataMap().put(PASSWORD, password);
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			trigger.setName(JOB_NAME_CATALOG);
			scheduler.scheduleJob(jobDetail, trigger);
		}catch(SchedulerException e){
			logger.error("Failed to scheduler catalog job", e);
		}
	}
	
	/**
	 * schedule an on demand catalog job
	 * @param jobID jobID: for common situations, it's -1, 
	 * 		  for resume job after restart it's the value get from backend
	 */
	public void startOnDemandCatalogJob(long jobID, String destination, 
			String userName, String password){
		if (disabledPlan())
			return;
		
		if(OndemandCatalogJob.isJobRunning()){
			logger.info("On-demand Catalog job is running, no need to schedule it again");
			return;
		}else if(jobID <= 0 && IsCatalogAvaiable(CatalogQueueType.ONDEMAND_JOB, null) == 0){
			return;
		}
		
		try {
			JobDetail jobDetail = new JobDetailImpl(JOB_NAME_OD_CATALOG, JOB_GROUP_CATALOG_NAME, OndemandCatalogJob.class);
			jobDetail.getJobDataMap().put(DESTINATION, destination);
			jobDetail.getJobDataMap().put(USERNAME, userName);
			jobDetail.getJobDataMap().put(PASSWORD, password);
			jobDetail.getJobDataMap().put(CatalogService.JOB_ID, jobID);
			jobDetail.getJobDataMap().put(CatalogService.JOB_TYPE, JobType.JOBTYPE_CATALOG_GRT);
			jobDetail.getJobDataMap().put(CatalogService.QUEUE_TYPE, CatalogQueueType.ONDEMAND_JOB);
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			trigger.setName(JOB_NAME_OD_CATALOG);
			scheduler.scheduleJob(jobDetail, trigger);
		}catch(SchedulerException e){
			logger.error("Failed to scheduler on-demand catalog job", e);
		}
	}
	
	/**
	 * schedule an on demand catalog job
	 * @param jobID jobID: for common situations, it's -1, 
	 * 		  for resume job after restart it's the value get from backend
	 */
	public void startOnDemandCatalogJob(long jobID){
		startOnDemandCatalogJob(jobID, null, null, null);
	}
	
	/**
	 * add new grt catalog information
	 * @param backupDestination
	 * @param userName
	 * @param password
	 * @param sessionNumber
	 * @param subSessionNumber
	 * @param sessionPassword
	 * @return
	 * @throws ServiceException
	 */
	public synchronized long submitCatalogJob(CatalogJobPara catalogJobPara) throws ServiceException	{
		if (disabledPlan())
			throw generateAxisFault(FlashServiceErrorCode.Backup_BackupDisabled);
		
		logger.debug("submitCatalogJob() - start");
//		if(!CommonService.getInstance().checkLicense(CommonService.EX_GR_LIC)) {
//			String msg = WebServiceMessages.getResource("LicenseGRT");
//			throw new ServiceException(msg, FlashServiceErrorCode.Common_License_Failure);
//		}
		//String[] pass = this.getNativeFacade().getSessionPasswordBySessionGuid(new String[] {sessionGUID});
		//String sessionPassword = pass.length == 0 ? null : pass[0];		
		// CONN_INFO info = BackupService.getInstance().getCONN_INFO(BackupService.getInstance().getBackupConfiguration());
		//long ret = WSJNI.addGRTCatalogInfo(backupDestination, info.getUserName(), info.getPwd(), info.getDomain(), 
		//		sessionNumber, subSessionNumber, sessionPassword);
		
		if (catalogJobPara.getSessionNumber()<=0)
			throw new ServiceException(FlashServiceErrorCode.Common_Invalid_SessionNum);		
		
		if (catalogJobPara.getSubSessionNumber()<=0)
			throw new ServiceException(FlashServiceErrorCode.Common_Invalid_SubSessionNum);
		
		if(this.getNativeFacade().isCatalogJobInQueue(CatalogQueueType.REGULAR_JOB, catalogJobPara.getBackupDestination(), 
				catalogJobPara.getSessionNumber(), catalogJobPara.getSubSessionNumber(), null)){
			logger.debug("The job script is already in regular job queue, will start it directly");
			if(makeupJob != null)
				makeupJob.start();
			else
				startCatalogJob(-1, 0, CatalogQueueType.REGULAR_JOB, catalogJobPara.getBackupDestination(), 
						catalogJobPara.getUserName(), catalogJobPara.getPassword(), Constants.JOBTYPE_CATALOG_GRT);
			return 0;
		}else if(this.getNativeFacade().isCatalogJobInQueue(CatalogQueueType.ONDEMAND_JOB, catalogJobPara.getBackupDestination(), 
				catalogJobPara.getSessionNumber(), catalogJobPara.getSubSessionNumber(), null)){
			logger.debug("The job script is already in on-demand job queue, will start it directly");
			startOnDemandCatalogJob(-1, catalogJobPara.getBackupDestination(), 
					catalogJobPara.getUserName(), catalogJobPara.getPassword());
			return 0;
		}else {
			long ret = WSJNI.addGRTCatalogInfo(
					catalogJobPara.getBackupDestination(), 
					catalogJobPara.getUserName(), 
					catalogJobPara.getPassword(), 
					"",
					catalogJobPara.getSessionNumber(),
					catalogJobPara.getSubSessionNumber(),
					catalogJobPara.getEncryptionPassword(),
					catalogJobPara.getGrtEdbList(),
					null);
	
			//This API need a return value, if the return value is valid, then start catalog job
			if(ret == 0){
				logger.info("saved successfully, launch catalog job");
				checkForMergeRunning(ServiceUtils.jobType2String(Constants.JOBTYPE_CATALOG_GRT, 0));
				startOnDemandCatalogJob(-1, catalogJobPara.getBackupDestination(), 
						catalogJobPara.getUserName(), catalogJobPara.getPassword());
			}else 
				logger.warn("Save grt catalog information failed, error code " + ret);
			return ret;
		}
	}
	
	/**
	 * 
	 * @param backupDestination
	 * @param userName
	 * @param password
	 * @param sessionNumber
	 * @param subSessionNumber
	 * @param sessionPassword
	 * @return
	 * @throws ServiceException
	 */
	public synchronized long submitFSCatalogJob(CatalogJobPara catalogJobPara, String vmInstanceUUID) throws ServiceException	{
		if (disabledPlan())
			throw generateAxisFault(FlashServiceErrorCode.Backup_BackupDisabled);
		
		logger.debug("AFSaveJS4FSOndemand() - start");
		long ret = WSJNI.AFSaveJS4FSOndemand(
				catalogJobPara.getBackupDestination(), 
				catalogJobPara.getUserName(), 
				catalogJobPara.getPassword(), 
				"",
				catalogJobPara.getSessionNumber(),
				vmInstanceUUID,
				0,
				null);

		//This API need a return value, if the return value is valid, then start catalog job
		if(ret == 0){
			if(StringUtil.isEmptyOrNull(vmInstanceUUID)){
				//Start D2D backup job catalog job 
				logger.info("Successfully launch D2D catalog job");
				if(makeupJob != null)
					makeupJob.start();
				else
					startCatalogJob(-1, 0, CatalogQueueType.MAKEUP_JOB, catalogJobPara.getBackupDestination(), 
						catalogJobPara.getUserName(), catalogJobPara.getPassword(), JobType.JOBTYPE_CATALOG_FS);
			}
			else{
				logger.info("Successfully launch vsphere catalog job:"+vmInstanceUUID);
				VSPhereCatalogService.getInstance().startCatalogJob(-1, 0, CatalogQueueType.MAKEUP_JOB, 
						vmInstanceUUID, catalogJobPara.getBackupDestination(), 
						catalogJobPara.getUserName(), catalogJobPara.getPassword(), JobType.JOBTYPE_VM_CATALOG_FS);
			}
				
		}else 
			logger.warn("Faild to launch catalog job, error code " + ret);
		
		return ret;
	}
	
	public synchronized long submitFSOnDemandJobToRPS(CatalogJobPara para) {		
		RpsHost host = new RpsHost();

		host.setRhostname(para.getRpsServerName());
		host.setUsername(para.getRpsUserName());
		host.setPassword(para.getRpsPassword());
 
		if (para.getRpsPort() == 0)
			host.setPort(8014);
		else
			host.setPort(para.getRpsPort());
		
		host.setHttpProtocol(para.isRpsHttp());

		return JobService.getInstance().submitFSOnDemandCatalog(para, host);
	}
	
	public synchronized long submitFSOnDemandJobToLocal(CatalogJobPara para) throws ServiceException {
		if(para.getCurrentCatalogStatus() == FSCAT_DISABLED){
			try {
				int ret = BrowserService.getInstance().generateCatalogOnDemand(para.getSessionNumber(), 
						para.getBackupDestination(), para.getUserName(), para.getPassword(), null);
				if(ret == 1) {
					if(makeupJob != null){
						makeupJob.start();
					}else
						startCatalogJob(-1, 0, CatalogQueueType.MAKEUP_JOB, 
							para.getBackupDestination(), para.getUserName(), para.getPassword(), 
							JobType.JOBTYPE_CATALOG_FS_ONDEMAND);
				}
			}catch(Exception e) {
				logger.error("Failed to move catalog job script " + e.getMessage());
				return 1;
			}
		}else if(para.getCurrentCatalogStatus() == FSCAT_FAIL) {
			return this.submitFSCatalogJob(para, null);
		}else {
			logger.warn("Don't need to run ondemand catalog for catalog status " 
					+ para.getCurrentCatalogStatus());
		}
		return 0;
	}

	public long submitFSOnDemandJob(CatalogJobPara para)
			throws ServiceException {
		//Bug756025 also allowed to submit FSOnDemandJob on plan is disabled.
//		if (disabledPlan())
//			throw generateAxisFault(FlashServiceErrorCode.Backup_BackupDisabled);
		
		if (!StringUtil.isEmptyOrNull(para.getRpsServerName()))
			return submitFSOnDemandJobToRPS(para);
		else
			return submitFSOnDemandJobToLocal(para);
	}
	
	/**
	 * Call back end API to do real catalog
	 * @param id]
	 * @param type : catalog type: 1 for regular, 2 for ondemand
	 * @throws ServiceException
	 */
	public long launchCatalogJob(long id, long type) throws ServiceException {
		BackupConfiguration conf = BackupService.getInstance().getBackupConfiguration();
		if(conf == null){
			logger.error("No backup configuration, no need to start catalog job");
			return -1;
		}
			
		return this.getNativeFacade().lauchCatalogJob(id, type, null, 
				conf.getDestination(), conf.getUserName(), conf.getPassword());			
	}
	
	public long launchCatalogJob(long id, long type, String destination, 
			String userName, String password) throws ServiceException {
		if (disabledPlan())
			return -1;
		
		if(destination == null || destination.isEmpty()){
			return launchCatalogJob(id, type);
		}else
			return this.getNativeFacade().lauchCatalogJob(id, type, null, 
				destination, userName, password);
	}

	
//	@Override
//	public void update(Observable o, Object arg) {
//		JJobMonitor jJM = (JJobMonitor)arg;
//		if(jJM.getUlJobStatus() == Constants.JOBSTATUS_FINISHED){
//			startCatalogJob(-1, 0);
//		}
//	}
	
	/**
	 * We always first start makeup job.
	 * If catalog job success, we start another catalog job;
	 * else we start the makeup job in 15 minutes (the number is defined in RetryPolicy.xml)
	 */
	public synchronized void update(Observable o, Object arg) {
		if (disabledPlan())
			return;
		
        submitCatalog = false;
        //Before run catalog, reset submit file copy flag, in case catalog is disabled, 
       //then ArchiveService update will not be called at all.
//        ArchiveService.getInstance().resetSubmitFileCopy();
		try {
		if(o instanceof OndemandCatalogJob) {
			this.startOnDemandCatalogJob(-1);
		}else {
			JJobMonitor jJM = (JJobMonitor)arg;
			
			if(jJM.getUlJobType() == Constants.AF_JOBTYPE_BACKUP 
					&& jJM.getUlJobStatus() == BaseBackupJob.BackupJob_PROC_EXIT){
				if(makeupJob != null) {
					//start make up job
					makeupJob.start();
				}else {
					startRegularJob(true, "");
				}
			}else if(jJM.getUlJobType() == Constants.JOBTYPE_CATALOG_FS ||
					jJM.getUlJobType() == Constants.JOBTYPE_CATALOG_GRT ||
					jJM.getUlJobType() == Constants.JOBTYPE_CATALOG_FS_ONDEMAND) {
				if(jJM.getUlJobStatus() == Constants.JOBSTATUS_FINISHED)
					startRegularJob(true, "");
				else
					startRegularJob(false, "");
              }
			}
		}finally {
			if(!CopyService.getInstance().isSubmitCopy()
					&& !ArchiveService.getInstance().isSubmitFileCopy()
					&& !CatalogService.getInstance().isSubmitCatalog()) {
				try {
					MergeService.getInstance().resumeMerge(AbstractMergeService.MergeEvent.OTHER_JOB_END);
				}catch(Exception e) {
					logger.error("Failed to start merge job " + e.getMessage());
				}
			}else {
				logger.info("Cannot submit merge job, current job resume state: copy " + CopyService.getInstance().isSubmitCopy()
						+ ", file copy " + ArchiveService.getInstance().isSubmitFileCopy()
						+ ", catalog " + CatalogService.getInstance().isSubmitCatalog());
			}
		}
	}

	public boolean isSubmitCatalog() {
		return submitCatalog;
	}

	public void startRegularJob(boolean startImmediately, String serverIdentity) {
		long catJob = IsCatalogAvaiable(CatalogQueueType.MAKEUP_JOB, null, serverIdentity); 
		if(catJob != 0) {
			if(startImmediately) {
				startCatalogJob(-1, 0, CatalogQueueType.MAKEUP_JOB, catJob);
				submitCatalog = true;
			}else {
				makeupFailedCatalog();
			}	
		}else{ 
			catJob = IsCatalogAvaiable(CatalogQueueType.REGULAR_JOB, null, serverIdentity); 
			if(catJob != 0) {
				startCatalogJob(-1, 0, CatalogQueueType.REGULAR_JOB, catJob);
				submitCatalog = true;
			}
		}
	}
	
	public long IsCatalogAvaiable(long jobQueueType, String queueIdentity) {
		return WSJNI.AFIsCatalogAvailable(jobQueueType, queueIdentity, "");
	}
	
	private boolean makeupFailedCatalog() {
		RetryPolicy policy = BackupService.getInstance().getRetryPolicy(CommonService.RETRY_CATALOG);
		// condition 1, if enabled or not
		if(!(policy.isEnabled()))
		{
			logger.warn("makeup catalog - end with disabled retry policy for backup");
			return false;
		}
		
		startCatalogJob(-1, policy.getTimeToWait() * 60 * 1000, CatalogQueueType.MAKEUP_JOB, JobType.JOBTYPE_CATALOG_FS);
		return true;
	}
	
	public void setMakeupJob(CatalogJob job) {
		makeupJob = job;
	}
	
	public RetryPolicy defaultRetryPolicy() {
		RetryPolicy result = new RetryPolicy();
		//for makeup catalog, we need a default value.
		result.setEnabled(true);
		result.setTimeToWait(15);
		result.setJobType(CommonService.RETRY_CATALOG);
		return result;
	}

	public long runCatalogNow(CatalogJobArg jobArg) throws ServiceException {
		if (handleErrorFromRPS(jobArg) == -1)
			return -1;
		
		if (disabledPlan())
			return 0;
		
		if(CatalogJob.isJobRunning()){
			logger.info("Catalog job is running, no need to schedule it again");
			return 0;
		}
		String jobGroup = jobArg.getJobDetailGroup() + Constants.RUN_NOW;
		try {
			JobDetail jobDetail = new JobDetailImpl(jobArg.getJobDetailName(), 
					jobGroup, CatalogJob.class);
			jobDetail.getJobDataMap().put(CatalogService.WAIT_TIME, 0L);
			jobDetail.getJobDataMap().put(CatalogService.QUEUE_TYPE, jobArg.getQueueType());
			jobDetail.getJobDataMap().put(DESTINATION, jobArg.getDestination());
			jobDetail.getJobDataMap().put(USERNAME, jobArg.getUserName());
			jobDetail.getJobDataMap().put(PASSWORD, jobArg.getPassword());
			jobDetail.getJobDataMap().put(Constants.RUN_NOW, Boolean.TRUE);
			jobDetail.getJobDataMap().put(CatalogService.JOB_TYPE, jobArg.getJobType());
			jobDetail.getJobDataMap().put(JOB_ID, jobArg.getJobId());
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			trigger.setName(jobArg.getJobDetailName());
			trigger.setGroup(jobDetail.getKey().getGroup());
			scheduler.scheduleJob(jobDetail, trigger);
		} catch(ObjectAlreadyExistsException oe){
			logger.error("Failed to scheduler catalog job, will remove the job", oe);			
			try {
				scheduler.deleteJob(new JobKey(jobArg.getJobDetailName(), jobGroup));
			} catch (SchedulerException e) {}
		} catch(SchedulerException e){
			logger.error("Failed to scheduler catalog job", e);
		}
		return 0;
	}

	@Override
	public boolean needRun(JobDependencySource source) {
		if(source.getJobType() == JobType.JOBTYPE_BACKUP
				&& source.getJobStatus() == JobStatus.BackupJob_PROC_EXIT)
			if(this.IsCatalogAvaiable(CatalogQueueType.MAKEUP_JOB, null) != 0
				|| IsCatalogAvaiable(CatalogQueueType.REGULAR_JOB, null) != 0)
				return true;
			else
				return false;
		else if(source.getJobType() == JobType.JOBTYPE_CATALOG_FS
				|| source.getJobType() == JobType.JOBTYPE_CATALOG_GRT
				|| source.getJobType() == JobType.JOBTYPE_CATALOG_FS_ONDEMAND){
			if(source.getJobStatus() != JobStatus.JOBSTATUS_FINISHED)
				return false;
			
			if(this.IsCatalogAvaiable(CatalogQueueType.MAKEUP_JOB, null) != 0
					|| IsCatalogAvaiable(CatalogQueueType.REGULAR_JOB, null) != 0)
				return true;
		}
		return false;
	}

	public void resumeOnDemandCatalogJob(long jobID) {
		if (disabledPlan())
			return;
		
		if(OndemandCatalogJob.isJobRunning()){
			logger.info("On-demand Catalog job is running, no need to schedule it again");
			return;
		}else if(jobID <= 0 && IsCatalogAvaiable(CatalogQueueType.ONDEMAND_JOB, null) == 0){
			return;
		}
		
		try {
			JobDetail jobDetail = new JobDetailImpl(JOB_NAME_OD_CATALOG, JOB_GROUP_CATALOG_NAME, OndemandCatalogJob.class);
			jobDetail.getJobDataMap().put(DESTINATION, null);
			jobDetail.getJobDataMap().put(USERNAME, null);
			jobDetail.getJobDataMap().put(PASSWORD, null);
			jobDetail.getJobDataMap().put(CatalogService.JOB_ID, jobID);
			jobDetail.getJobDataMap().put(CatalogService.JOB_TYPE, JobType.JOBTYPE_CATALOG_GRT);
			jobDetail.getJobDataMap().put(CatalogService.QUEUE_TYPE, CatalogQueueType.ONDEMAND_JOB);
			jobDetail.getJobDataMap().put(RESUMED, Boolean.TRUE);
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			trigger.setName(JOB_NAME_OD_CATALOG);
			scheduler.scheduleJob(jobDetail, trigger);
		}catch(SchedulerException e){
			logger.error("Failed to scheduler on-demand catalog job", e);
		}
		
	}

	public void resumeCatalogJob(long jobID, long jobType) {
		if (disabledPlan())
			return;
		
		if(CatalogJob.isJobRunning()){
			logger.info("Catalog job is running, no need to schedule it again");
			return;
		}
		
		try {
			JobDetail jobDetail = new JobDetailImpl(JOB_NAME_CATALOG, JOB_GROUP_CATALOG_NAME, CatalogJob.class);
			jobDetail.getJobDataMap().put(CatalogService.JOB_ID, jobID);
			jobDetail.getJobDataMap().put(CatalogService.WAIT_TIME, 0L);
			jobDetail.getJobDataMap().put(CatalogService.QUEUE_TYPE, -1L);
			jobDetail.getJobDataMap().put(CatalogService.JOB_TYPE, jobType);
			jobDetail.getJobDataMap().put(DESTINATION, null);
			jobDetail.getJobDataMap().put(USERNAME, null);
			jobDetail.getJobDataMap().put(PASSWORD, null);
			jobDetail.getJobDataMap().put(RESUMED, Boolean.TRUE);
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			trigger.setName(JOB_NAME_CATALOG);
			scheduler.scheduleJob(jobDetail, trigger);
		}catch(SchedulerException e){
			logger.error("Failed to scheduler catalog job", e);
		}
		
	}
	
	public List<String> queryCatalogJobScript(String nodeName, String serverID,
			String clientID) {
		JCatalogJobScriptInfo info = new JCatalogJobScriptInfo();
		CommonNativeInstance.getICommonNative().queryJobQueue(
				CatalogQueueType.REGULAR_JOB, clientID, info, false, serverID);

		logger.debug("clinetID : " + clientID);
		logger.debug("serverID : " + serverID);

		logger.debug("info : " + info.getCatalogScriptPath());
		logger.debug("info jobscript: " + info.getCatalogJobScript().size());

		return info.getCatalogJobScript();
	}
	
	public String getRegularCatalogJobScript(String fileName) {

		byte[] buffer = readByte(fileName);
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(buffer);
	}

	private byte[] readByte(String fileName) {
		Path path = FileSystems.getDefault().getPath(fileName);
		if (!path.toFile().exists()) {
			logger.error("File -- " + fileName + " not exists");
			return null;
		}
		BufferedInputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(path.toFile()));

			out = new ByteArrayOutputStream();
			byte[] buffer = new byte[(int) path.toFile().length()];

			int size = 0;
			while ((size = in.read(buffer)) != -1) {
				out.write(buffer, 0, size);
			}
			return out.toByteArray();
			
		} catch (IOException e) {
			logger.error(e);

		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					// ignore
				}
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					// ignore
				}
		}
		return null;
	}
	public void deleteRegularCatalogJobScript(String fileName) {
		try {
			Path path = FileSystems.getDefault().getPath(fileName);

			File f = path.toFile();

			if (f.exists())
				f.delete();
		} catch (Throwable e) {
			logger.error(e);
		}
	}
	

	private long IsCatalogAvaiable(long jobQueueType, String queueIdentity, String rpsSvrIdentity) {
		return WSJNI.AFIsCatalogAvailable(jobQueueType, queueIdentity, rpsSvrIdentity);
	}
	
	private boolean disabledPlan() {
		try {
		    BackupConfiguration conf = BackupService.getInstance().getBackupConfiguration();
		    if (conf!=null && conf.isDisablePlan()) {
			    logger.info("The plan is disabled, do not run catalog job");
			    return true;
		    }
		    else 
		    	return false;
		}
		catch (ServiceException e) {
			logger.error("Can not get backup configuration");
		}
		
		return false;
	}
	
	public void resetSubmitCatalog(){
		this.submitCatalog = false;
	}
}

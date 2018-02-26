package com.ca.arcflash.webservice.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.EveryMonthSchedule;
import com.ca.arcflash.webservice.data.EveryWeekSchedule;
import com.ca.arcflash.webservice.data.MountSession;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.merge.MergeAPISource;
import com.ca.arcflash.webservice.data.merge.MergeJobMonitor;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.scheduler.AbstractMergeJob;
import com.ca.arcflash.webservice.scheduler.BaseJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.MergeJob;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class MergeService extends AbstractMergeService {
	
	private static final Logger logger = Logger.getLogger(MergeService.class);	

	private volatile MergeJob currentJob = null;
//	private int otherJobCount = 0;
	private Set<Object> otherJobs = new HashSet<Object>();
	private MergeStatus currentStatus = null;
	private static String statusPath = null; 
	private static MergeService INSTANCE = SingletonInstance.INSTANCE;
	
	private MergeService() {
		super();
		currentStatus = new MergeStatus();
		currentStatus.setUUID(CommonService.getInstance().getNodeUUID());
		currentStatus.setStatus(MergeStatus.Status.NOTRUNNING);
		statusPath = mergeStatusFolderPath + "\\d2dmergestatus";
		MergeStatus status = this.loadMergeStatus(statusPath);		
		this.clearMergeStatus(statusPath);
		if(status != null) {
			currentStatus = status;
		}
	}
	
	private static class SingletonInstance {
		static MergeService INSTANCE = new MergeService();
	}

	public synchronized static MergeService getInstance(){
		return INSTANCE;
	}
	
	public synchronized MergeStatus getMergeJobStatus() {
		currentStatus.setJobType(Constants.JOBTYPE_D2D_MERGE);
		return currentStatus;
	}
	
	public synchronized int pauseMerge(MergeAPISource source) throws ServiceException {
		logger.debug("Enter pause merge: manually");
		int ret = 0;
		
		if(source == null) {
			ret = pauseMerge(MergeEvent.WS_STOP, null, null);
		}else {
			switch(source) {
			case MANUALLY:
				ret = pauseMerge(MergeEvent.MANUAL_STOP, null, null);
				break;
			case ASBU_BACKUP:
				ret = pauseMerge(MergeEvent.WS_STOP, null, ASBU_JOB);
				break;
			default:
				ret = pauseMerge(MergeEvent.WS_STOP, null, null);
				break;
			}
		}
		
		logger.debug("End pause merge");
		return ret;
	}

	@Override
	protected void logWithID(long level, long jobId, String msg,
			String vmInstanceUUID) {
		if(jobId > 0)
			this.getNativeFacade().addLogActivityWithJobID(level, jobId, 
					Constants.AFRES_AFJWBS_GENERAL, new String[]{
					msg, "", "","",""});
		else
			this.getNativeFacade().addLogActivity(level, Constants.AFRES_AFJWBS_GENERAL, 
					new String[]{msg, "", "","",""});
	}

	public synchronized void mergeStart(MergeJob job) {
		currentJob = job;
	}
	
	public synchronized int checkForResume(MergeEvent event) throws ServiceException {
		logger.debug("Enter check for resume merge");
		int ret = this.checkForResume(event, currentStatus, 
				JobMonitorService.getInstance().getJobMonitorMap(), 
				otherJobs, currentJob, null);
		logger.debug("Check for resume complete");
		
		return ret;
	}
	
	@Override
	public boolean canStartMerge(String vmInstanceUUID) {
		return !isOtherJobRunning(JobMonitorService.getInstance().getJobMonitorMap(), 
				otherJobs);
	}	
	/**
	 * No matter to start new merge job or resume paused merge, we will generate
	 *  a new job id and start a new merge job  
	 * @param vmInstanceUUID
	 * @return
	 */
	public int resumeMerge(MergeAPISource source) throws ServiceException {
		// disable plan
		BackupConfiguration conf = BackupService.getInstance().getBackupConfiguration();
		if(conf.isDisablePlan()) {
			logger.info("The Merge job is paused.");
			throw generateAxisFault(FlashServiceErrorCode.Backup_BackupDisabled);
		}
		
		logger.debug("Enter resume merge " + source);

		int ret = 0;
		if(source == null) {
			ret = resumeMerge(MergeEvent.WS_RESUME);
		}else {
			switch(source) {
			case MANUALLY:
				ret = resumeMerge(MergeEvent.MANUAL_RESUME);
				break;
			case ASBU_BACKUP:
				jobEnd(ASBU_JOB);
				ret = resumeMerge(MergeEvent.WS_RESUME);
				break;
			default:
				ret = resumeMerge(MergeEvent.WS_RESUME);
				break;
			}
		}
		logger.debug("resumeMerge(source) " + source + " end");
		return ret;
	}

	public synchronized MergeJobMonitor getMergeJobMonitor() {
		return currentStatus.getJobMonitor();	
	}
	
	@Override
	public synchronized void updateMergeJobStatus(MergeStatus status) {
		status.setUpdateTime(System.currentTimeMillis());
		currentStatus = status;		
		logger.debug("Current status is " + currentStatus.getStatus());
		setChanged();
		status.setD2dServerName(ServiceContext.getInstance().getLocalMachineName());
//		reportMergeJobMonitor(status, null);
		notifyObservers(currentStatus);
	}
	
	@Override
	public synchronized long startMerge(AbstractMergeJob job) {
		long ret = -1;
		
		if(backupToRPS()){
			logger.debug("Backup to RPS, no need to start merge job");
			return 0;
		}
		
		try {
			this.logResumeActivity(job.getMergeEvent(), job.getJobId(), job.getVMInstanceUUID());
			
			ret = this.getNativeFacade().startMerge(job.getMergeJobScript());
			if(ret != 0) {
				logger.error("Failed to start merge with error code " + ret);
				logWithID(Constants.AFRES_AFALOG_ERROR,
						job.getJobId(),
						WebServiceMessages.getResource("startMergeFailed", String.valueOf(ret)),
						null);
				mergeDone(job);
			}
//			this.clearMergeStatus(statusPath);
		}catch(ServiceException se) {
			logger.error("Failed to start merge", se);
		}
		
		return ret;
	}
	
	@Override
	public synchronized void mergeDone(AbstractMergeJob mJM) {
		mergeDone(mJM, currentStatus);
		//need the persistent merge status to know whether it's paused manually after webservice restart
//		this.saveMergeStatus(currentStatus, statusPath);
		currentJob = null;
	}
	
	//wanqi06
	public void resumeJobAfterWSRestart(int jobID) {
		//
		if(currentJob != null){
			logger.info("There is job running, return");
			return;
		}
		MergeJob job = new MergeJob(jobID);
		mergeStart(job);
		BaseJob.pool.submit(job);
	}

	@Override
	public RetentionPolicy getRetentionPolicy(String vmInstanceUUID) {
		RetentionPolicy policy = null;
		try {
			BackupConfiguration backupConf 
				= BackupService.getInstance().getBackupConfiguration();
			if(backupConf != null) {
				policy = backupConf.getRetentionPolicy();
				if(policy == null) {
					policy = new RetentionPolicy();
					policy.setUseBackupSet(false);
					policy.setUseTimeRange(false);
					backupConf.setRetentionPolicy(policy);
				}
			}
				
		}catch(ServiceException e) {
			logger.error("Failed to get backup configuration");
		}
		
		return policy;
	}
	
	@Override
	public List<DailyScheduleDetailItem> getMergeSchedule(String vmInstanceUUID) {
		List<DailyScheduleDetailItem> dailyScheduleDetailItems = null;
		try {
			BackupConfiguration backupConf 
				= BackupService.getInstance().getBackupConfiguration();
			if(backupConf != null && backupConf.getBackupDataFormat() == 1 && backupConf.getAdvanceSchedule() != null) {
				dailyScheduleDetailItems = backupConf.getAdvanceSchedule().getDailyScheduleDetailItems();
			}		
		}catch(ServiceException e) {
			logger.error("Failed to get backup configuration");
		}
		
		return dailyScheduleDetailItems;
	}
	
	public void waitForJobEnd() {
		if(getCurrentJob() != null)
			currentJob.waitJobEnd();
	}
	
	public synchronized MergeJob getCurrentJob() {
		return currentJob;
	}
	
	public boolean isJobRunning() {
		return currentJob != null;
	}
	
	
	public void scheduleMergeJob(){
		logger.info("Schedule merge job");
		if(backupToRPS()){
			logger.debug("Backup to RPS, no need to start merge job");
			return ;
		}
		
		BackupConfiguration backupConf = null;
		try {
			backupConf = BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e1) {
			logger.error("Failed to get backup configuration");
		}
		
		if(backupConf != null && backupConf.isDisablePlan()) {
			unschedule(null);
			return;
		}
		
    	if(backupConf != null && backupConf.getBackupDataFormat() == 1) {
    		newScheduleMergeJob();
    	}
    	else {
    		oldScheduleMergeJob();
    	}
	}
	
	private void oldScheduleMergeJob() {
		unschedule(null);
		
		scheduleMergeJob(new MergeJobContext(getMergeJobName(null), MergeJob.class, null));
		
		if(currentJob == null && this.isInMergeTimeRange(null)) {
			try {
				resumeMerge(MergeEvent.SCHEDULE_BEGIN);
			}catch(Exception e) {
				logger.error("Failed to start merge job", e);
			}
		}
	}
	
	private void newScheduleMergeJob(){
		logger.info("Schedule merge job");
		
		unschedule(null);
		
		newScheduleMergeJob(new MergeJobContext(getMergeJobName(null), MergeJob.class, null));
		
		if(currentJob == null && this.isInMergeTimeRange(null)) {
			try {
				resumeMerge(MergeEvent.SCHEDULE_BEGIN);
			}catch(Exception e) {
				logger.error("Failed to start merge job", e);
			}
		}
		
	    return;
	}
	
	/////add event interface for webservice internal use
	////////////////////////////////////////////////////////////////////////////
	public int resumeMerge(MergeEvent event) throws ServiceException{
		logger.debug("Enter resume merge event is " + event);
		if(backupToRPS()){
			logger.info("Backup to RPS, no need to start merge job");
			return 0;
		}
		
		if(this.isJobRunning()){
			logger.info("Merge job is already running");
			return 0;
		}
		//check whether need to start merge job
		if(!this.isMergeJobAvailable(null)){
			if(event != MergeEvent.MANUAL_RESUME)
				return 0;
			else
				throw new ServiceException(WebServiceMessages.getResource("mergeNoNeedResume"),
						FlashServiceErrorCode.Common_General_Message);
		}
		
		if(this.checkForResume(event) != 0)
			//already running or torun.
			return 0;
		synchronized(this){
			int needResume = this.canResumeMerge(event, currentStatus);
			if(needResume == -1){
				//already running or torun.
				return 0;
			}
			
			startMergeJob(new MergeJobContext(getMergeJobName(null), MergeJob.class, event));
			currentStatus.setRecoverySet(isUseBackupSet());
			currentStatus.setCanResume(false);
			this.updateMergeJobStatus(currentStatus);
		}

		logger.debug("End resume merge, " + currentJob);
		return 0;
	}
	
	public int canResumeMerge(MergeEvent event) throws ServiceException {
		return this.canResumeMerge(event, currentStatus);
	}
	
	/**
	 * 
	 * @param event
	 * @param source: the job type or manually to pause the merge
	 * @param otherJob: the job
	 * @return
	 * @throws ServiceException
	 */
	public synchronized int pauseMerge(MergeEvent event, String source, Object otherJob) throws ServiceException {
		logger.debug("Enter pause merge");
		
		if(isUseBackupSet()){
			logger.info("Don't allow stop for backup set");
			return 0;
		}
		if(otherJob != null)
			newJobStart(otherJob);
		int ret = pauseMerge(currentStatus, currentJob, event, source);
		
		logger.debug("End pause merge");
		return ret;
	}
	
	///For other jobs run
	private void newJobStart(Object obj) {
		otherJobs.add(obj);
		logger.info("New job starts " + obj);
	}
	
	public synchronized void jobEnd(Object obj) {
		otherJobs.remove(obj);
		logger.info("Other job ends " + obj);
	}
	
	//For backup set
	public boolean isUseBackupSet() {
		RetentionPolicy policy = this.getRetentionPolicy(null);
		if(policy != null && policy.isUseBackupSet())
			return true;
		else
			return false;
	}
	
	
	@Override
	public void saveMergeStatus() {
		this.saveMergeStatus(currentStatus, statusPath);
		
		logger.info("Exit MergeService.saveMergeStatus");
	}	
	
	@Override
	protected long checkMergeJobAvailableForRecoveryPoints(String vmInstanceUUID) {
		try {
			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			if(configuration == null) return 1;			
			RetentionPolicy retentionPolicy = configuration.getRetentionPolicy();
			if(retentionPolicy.isUseBackupSet())
				return 1;
			
			return this.isMergeJobAvailable(vmInstanceUUID)? 0:1;
		}catch(Exception e){
			logger.error("Failed to check recovery point number " + e);
			return 2;
		}
	}

	public boolean isMergeJobAvailable(String vmInstanceUUID) {
		try {
			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			if(configuration == null) return false;
			
			int dailyCount = 0;
			int weeklyCount = 0;
			int monthlyCount = 0;
			if(configuration.getAdvanceSchedule() !=null &&configuration.getAdvanceSchedule().getPeriodSchedule() != null && configuration.getAdvanceSchedule().getPeriodSchedule().isEnabled()){
				
				EveryDaySchedule daySchedule = configuration.getAdvanceSchedule().getPeriodSchedule().getDaySchedule();					
				
				if(daySchedule != null && daySchedule.isEnabled()){
					dailyCount = daySchedule.getRetentionCount();							
				}
				
				EveryWeekSchedule weekSchedule  = configuration.getAdvanceSchedule().getPeriodSchedule().getWeekSchedule();
				if(weekSchedule != null && weekSchedule.isEnabled()){
					weeklyCount = weekSchedule.getRetentionCount();							
				}
				
				EveryMonthSchedule monthSchedule  = configuration.getAdvanceSchedule().getPeriodSchedule().getMonthSchedule();
				if(monthSchedule != null && monthSchedule.isEnabled()){
					monthlyCount = monthSchedule.getRetentionCount();							
				}			 
			}
			
			RetentionPolicy retentionPolicy = configuration.getRetentionPolicy();
			return this.isMergeJobAvailableEx(vmInstanceUUID, getMergeJobStatus(),
					configuration.getDestination(),
					configuration.getUserName(), configuration.getPassword(),
					retentionPolicy,
					configuration.getRetentionCount(), dailyCount,weeklyCount,monthlyCount);
			
		}catch(Exception e){
			logger.error("Failed to check recovery point number " + e);
			return false;
		}
	}

	public void fixMergeStatusAfterRestart(){
		fixMergeStatusAfterRestart(currentJob, currentStatus, null);
	}
	
	public MountSession[] getMountedSessionsToMerge() {
		if(backupToRPS()){
			logger.debug("Backup to RPS, no need to start merge job");
			return null;
		}
		try {
			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			if(configuration == null) return null;
			RetentionPolicy retentionPolicy = configuration.getRetentionPolicy();
			return this.getMountedSessionsToPurge(null, configuration.getDestination(), configuration.getUserName(), 
					configuration.getPassword(), retentionPolicy, configuration.getRetentionCount());
		}catch(ServiceException se) {
			logger.error("Failed to get backup configuration");
			return null;
		}
	}
	
	public String getMergeScheduleTime() {
		if(backupToRPS()){
			logger.debug("Backup to RPS, no need to start merge job");
			return null;
		}
		return this.getMergeScheduleTime(null, currentStatus, this.getRetentionPolicy(null));
	}
	
	@Override
	protected String getMergeJobName(String vmInstanceUUID) {
		return MERGE_JOB_NAME;
	}

	@Override
	public void startNewJobAfterDone(String vmInstanceUUID) {
		super.startNewJobAfterDone(vmInstanceUUID);
		try {
			MergeService.getInstance().resumeMerge(MergeEvent.SCHEDULE_BEGIN);
		}catch(ServiceException se) {
			logger.error("Failed to start merge job " + se.getMessage());
		}
		
	}
	@Override
	public void sendEmailOnMergePausedManually(String vmInstanceUUID) {
		try {
			logger.debug("Begin sendEmailOnMergePausedManually");
			PreferencesConfiguration preferencesConfig = CommonService.getInstance().getPreferences();
			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			if (preferencesConfig == null || configuration == null) {
				return;
			}
			
			BackupEmail email = preferencesConfig.getEmailAlerts();
			if (email == null || !email.isEnableSettings())
				return;
			
			//If we need to start merge job, while it's paused by manually, we will send a email
			if(email.isEnableEmailOnMergeFailure() 
					&& currentStatus.getStatus() == MergeStatus.Status.PAUSED_MANUALLY 
					&& this.isMergeJobAvailable(null)){
				EmailSender emailSender = new EmailSender();
				String emailJobStatus = WebServiceMessages.getResource("EmailJobStatus");
				String jobStatus = WebServiceMessages.getResource("mergeJobStatusSkipped");
				String hostName = ServiceContext.getInstance().getLocalMachineName();
				String subject = email.getSubject() + "-"+ WebServiceMessages.getResource("mergeJobString") 
					+ " " +emailJobStatus+jobStatus+"("+hostName+")";
				String content = WebServiceMessages.getResource("mergeJobPausedEmailAlert");
				emailSender.sendEmail(email, subject, content, Constants.JOBSTATUS_MISSED, true, 
						CommonEmailInformation.PRODUCT_TYPE.ARCFlash.getValue(),  JobType.JOBTYPE_MERGE, null );
			}
			logger.debug("End sendEmailOnMergePausedManually");
		}catch(Exception e) {
			logger.error("Failed to send email for manually pause");
		}
	}

	@Override
	public String getRPSPolicyUUID(String vmInstanceUUID) {
		return BackupService.getInstance().getRPSPolicyUUID();
	}

	@Override
	public String getDataStoreUUID(String vmInstanceUUID) {
		return BackupService.getInstance().getDataStoreUUID();
	}
	
	private boolean backupToRPS() {
		return BackupService.getInstance().isBackupToRPS();
	}
	
	protected ServiceException generateAxisFault(String errorCode){
		return new ServiceException("",errorCode);
	}
}

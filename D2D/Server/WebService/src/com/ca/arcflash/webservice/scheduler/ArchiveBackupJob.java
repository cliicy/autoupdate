package com.ca.arcflash.webservice.scheduler;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.JobDetailImpl;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.service.util.WebServiceMessages;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveJobScript;
import com.ca.arcflash.webservice.data.archive.ArchiveScheduleStatus;
import com.ca.arcflash.webservice.data.archive.JArchiveJob;
import com.ca.arcflash.webservice.data.backup.RetryPolicy;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.service.ArchiveCatalogSyncService;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.DeleteArchiveService;
import com.ca.arcflash.webservice.service.PurgeArchiveService;
import com.ca.arcflash.webservice.service.RestoreArchiveService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.internal.ArchiveJobConverter;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;

public class ArchiveBackupJob extends BaseArchiveJob{
		
	private static final Logger logger = Logger.getLogger(ArchiveBackupJob.class);
	
	protected ArchiveJobConverter jobConverter = new ArchiveJobConverter();
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("executing archive job");
		if(BackupService.getInstance().isBackupToRPS()){
			logger.debug("archive job will run on RPS");
			return;
		}
		
		Date date1 = context.getFireTime();
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		JobDetail jobDetail = context.getJobDetail();
		Object currentJobType = jobDetail.getJobDataMap().get(BaseService.JOB_TYPE);
		if(currentJobType == null){
			logger.error("current job type is null");
			return;
		}
		setJobType((long)currentJobType);
		setTriggerTimes(context, getJobType());
		String backupSessionPath = jobDetail.getJobDataMap().getString("BackupSessionPath");
		String backupSessionId = jobDetail.getJobDataMap().getString("BackupSessionId");
		String backupSessionGUID = jobDetail.getJobDataMap().getString("BackupSessionGUID");
		// job triggered by schedule need check whether it can be submitted or not
		if(StringUtil.isEmptyOrNull(backupSessionId) || StringUtil.isEmptyOrNull(backupSessionPath) || StringUtil.isEmptyOrNull(backupSessionGUID)){
			JArchiveJob archiveJobDetails = new JArchiveJob();
			if(getJobType() == JobType.JOBTYPE_FILECOPY_BACKUP){
				try {
					if(ArchiveService.getInstance().checkSubmitArchiveJob(archiveJobDetails)){
						backupSessionId = archiveJobDetails.getbackupSessionId();
						backupSessionPath = archiveJobDetails.getbackupSessionPath();
						backupSessionGUID = archiveJobDetails.getBackupSessionGUID();
						jobDetail.getJobDataMap().put("BackupSessionPath", backupSessionPath);
						jobDetail.getJobDataMap().put("BackupSessionId", backupSessionId);
						jobDetail.getJobDataMap().put("BackupSessionGUID", backupSessionGUID);
					}else{
						logger.debug("archive job can not submit");
						return;
					}
				} catch (ServiceException e) {
					logger.debug("check submit archive job failed with message : " + e.getMessage());
					return;
				}
			}
			if(getJobType() == JobType.JOBTYPE_FILECOPY_SOURCEDELETE){
				if(DeleteArchiveService.getInstance().getSessionInfo(archiveJobDetails)){
					backupSessionId = archiveJobDetails.getbackupSessionId();
					backupSessionPath = archiveJobDetails.getbackupSessionPath();
					backupSessionGUID = archiveJobDetails.getBackupSessionGUID();
					jobDetail.getJobDataMap().put("BackupSessionPath", backupSessionPath);
					jobDetail.getJobDataMap().put("BackupSessionId", backupSessionId);
					jobDetail.getJobDataMap().put("BackupSessionGUID", backupSessionGUID);
					logger.info("File Archive can be submitted for the session: " + backupSessionId);
				}else{
					logger.info("there is no backup session need to do archive");
					return;
				}
				
			}
		}
		Object jobID = jobDetail.getJobDataMap().get(BaseService.JOB_ID);
		if(jobID != null)
			this.shrmemid = (Long)jobID;
		boolean bRestoreJobRun = RestoreArchiveService.getInstance().isArchiveRestoreJobRunning();
		boolean bPurgeJobRun = PurgeArchiveService.getInstance().isPurgeJobRunning() ;
		boolean bArchiveJobRun = ArchiveService.getInstance().isArchiveBackupJobRunning(); 
		boolean bArchiveCatalogSyncJobRun = ArchiveCatalogSyncService.getInstance().isArchiveCatalogSyncJobRunning();
		boolean bFileArchiveJobRun = DeleteArchiveService.getInstance().isFileArchiveJobRunning();
		if(getJobType() == JobType.JOBTYPE_FILECOPY_BACKUP){
			if (bRestoreJobRun || bArchiveJobRun || bArchiveCatalogSyncJobRun || bPurgeJobRun){
				logger.info("There is another job running...");
				String name = ((JobDetailImpl)jobDetail).getName();

				String date = BackupConverterUtil.dateToString(date1);
				nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_JOB_SKIPPED,new String[]{name, date,"","",""});
				sendEmailOnMissedJob(context, getJobType());
				if(bRestoreJobRun){
					synchronized(missedFileCopyJobLockForRestore){
						missedFileCopyJobContextForRestore = context;
					}				
				}
				
				if(bPurgeJobRun){
					synchronized(missedFileCopyJobLockForPurge){
						missedFileCopyJobContextForPurge = context;
					}
				}
				
				ArchiveService.getInstance().resetSubmitFileCopy();
				return;
			}
		} else if(getJobType() == JobType.JOBTYPE_FILECOPY_SOURCEDELETE){
			if (bRestoreJobRun || bFileArchiveJobRun || bArchiveCatalogSyncJobRun || bPurgeJobRun){
				logger.info("There is another job running...");
				String name = ((JobDetailImpl)jobDetail).getName();

				String date = BackupConverterUtil.dateToString(date1);
				nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_JOB_SKIPPED,new String[]{name, date,"","",""});
				sendEmailOnMissedJob(context, getJobType());
				if(bRestoreJobRun){
					synchronized(missedFileArchiveJobLockForRestore){
						missedFileArchiveJobContextForRestore = context;
					}				
				}
				
				if(bPurgeJobRun){
					synchronized(missedFileArchiveJobLockForPurge){
						missedFileArchiveJobContextForPurge = context;
					}
				}
			}
		}
		
		try {
			ArchiveConfiguration configuration = null;
			if(shrmemid <= 0)
				initShrmemid();
			try {
				configuration = getConfiguration(getJobType());
				if(configuration == null) return;
			} catch (ServiceException e) {
				logger.error("get configuration error");
			}
			
			String backupSessionPwd = ArchiveService.getInstance().getSessionPwdByGUID(backupSessionGUID);
			ArchiveJobScript jobScript = generateJobScript(configuration, shrmemid,backupSessionPath,backupSessionId, backupSessionPwd);
			if(jobScript == null) return ;
			
			if(preprocess(jobScript)){
				if(isMakeupJob(getJobType())){
					ArchiveService.getInstance().getNativeFacade().addLogActivityWithJobID(Constants.AFRES_AFALOG_INFO,
							this.shrmemid, Constants.AFRES_AFJWBS_GENERAL,
							new String[] {WebServiceMessages.getResource("makeupJobDetails", String.valueOf(triggerTimes)),"","","",""});
				}
				if(0 != ArchiveService.getInstance().archive(jobScript)){
					logger.error("submit archive job error ");
					stopJobMonitor();
					setJobStatus(JOBSTATUS_FAILED);
					
					//sonle01: Inform sync Data to Edge
					EdgeDataSynchronization.SetSyncDataFlag();
					
					return;
				}
				
	            //we wait here to get the job's status. The lock is released in JobMonitorThread
				synchronized (phaseLock) 
				{
				       while ( jobPhase != JobExitPhase)
						try {
							phaseLock.wait();
						} catch (InterruptedException e) {
							logger.error(e.getMessage() == null ? e : e.getMessage());
						}
			    }
				
				retryJobWhenFailedOrCrashed(jobDetail);
				
				//sonle01: Inform sync Data to Edge
				EdgeDataSynchronization.SetSyncDataFlag();
			} 
		}finally {
			if(getJobType() == JobType.JOBTYPE_FILECOPY_BACKUP)
				ArchiveService.getInstance().resetSubmitFileCopy();
		}
	}
	
	public ArchiveJobScript generateJobScript(ArchiveConfiguration configuration, long shrmemid,
			String backupSessionPath, String backupSessionId, String backupSessionPwd) {
		try {
			ArchiveJobScript result = null;
			result = jobConverter.convert(configuration, (int)getJobType(), ServiceContext.getInstance().getLocalMachineName(), 
					shrmemid,backupSessionPath,backupSessionId, backupSessionPwd);
			return result;
		} catch (Exception e) {
			logger.error("Generate archive job script exception", e);
		}
		
		return null;
	}

	protected void retryJobWhenFailedOrCrashed(JobDetail in_archiveJobDetails) {
		if(this.getJobStatus() == JOBSTATUS_FAILED || this.getJobStatus() == JOBSTATUS_CRASH){
			logger.info("retryJobWhenFailedOrCrashed() - begin, jobid= " + this.shrmemid + ", retry time= " + this.triggerTimes);
			
			if(getJobType() == JobType.JOBTYPE_FILECOPY_BACKUP){
				RetryPolicy retryPolicy = getRetryPolicy(CommonService.RETRY_FILECOPY);
				String msg = "";
				if(retryPolicy.isEnabled()){
					if(ArchiveService.getInstance().isMakeupJob()){
						if(triggerTimes >= retryPolicy.getMaxTimes()){
							ArchiveService.getInstance().deleteMakeupSchedules();
							msg = WebServiceMessages.getResource("deleteMakeupJobMaxTime");
						}
					}else{
						ArchiveService.getInstance().createMakeupSchedule(retryPolicy);
						msg = WebServiceMessages.getResource("createMakeupJobAfterATime", String.valueOf(retryPolicy.getTimeToWait()));
					}
				}else{
					ArchiveService.getInstance().deleteMakeupSchedules();
					msg = WebServiceMessages.getResource("deleteMakeupJobDisabled");
				}
				logger.info(msg);
				ArchiveService.getInstance().getNativeFacade()
						.addLogActivityWithJobID(Constants.AFRES_AFALOG_INFO,
								this.shrmemid, Constants.AFRES_AFJWBS_GENERAL,
								new String[] {msg});
			} else if(getJobType() == JobType.JOBTYPE_FILECOPY_SOURCEDELETE){
				RetryPolicy retryPolicy = getRetryPolicy(CommonService.RETRY_FILEARCHIVE);
				String msg = "";
				if(retryPolicy.isEnabled()){
					if(DeleteArchiveService.getInstance().isMakeupJob()){
						if(triggerTimes >= retryPolicy.getMaxTimes()){
							DeleteArchiveService.getInstance().deleteMakeupSchedule();
							msg = WebServiceMessages.getResource("deleteMakeupJobMaxTime");
						}
					}else{
						DeleteArchiveService.getInstance().createMakeupSchedule(retryPolicy);
						msg = WebServiceMessages.getResource("createMakeupJobAfterATime", String.valueOf(retryPolicy.getTimeToWait()));
					}
				} else{
					DeleteArchiveService.getInstance().deleteMakeupSchedule();
					msg = WebServiceMessages.getResource("deleteMakeupJobDisabled");
				}
				logger.info(msg);
				ArchiveService.getInstance().getNativeFacade()
						.addLogActivityWithJobID(Constants.AFRES_AFALOG_INFO,
								this.shrmemid, Constants.AFRES_AFJWBS_GENERAL,
								new String[] {msg});
			}
			
			logger.info("retryJobWhenFailedOrCrashed() - end");
		}
	}

	@Override
	protected void afterComplete(JJobMonitor jJM) {
		if (jJM!=null/* && jJM.getUlJobPhase() == JobExitPhase*/) {
			
			//ArchiveService.getInstance().setArchiveJobsInformation(ArchiveService.getInstance().GetArchiveJobsInfo(archiveJob));
			synchronized (ArchiveService.getInstance().getObjectLock()){
				logger.debug("Entered synchronize block in archhive job");
				JArchiveJob archiveJob = new JArchiveJob();
				archiveJob.setScheduleType(ArchiveScheduleStatus.ScheduleAll);
				archiveJob.setbOnlyOneSession(false);
				//ArchiveService.getInstance().InsertJobInfoToGlobalList(ArchiveService.getInstance().GetArchiveJobsInfo(archiveJob));
				ArchiveService.getInstance().setArchiveJobsInformation(ArchiveService.getInstance().GetArchiveJobsInfo(archiveJob));
				logger.debug("Exiting synchronize block in archhive job");
			}
			
			makeupMissedPurge();
			makeupMissedFCPurge();
		    
			try{
				sendEmail(jJM);
				saveRSSFeed(jJM);
			}
			catch (Exception e)
			{
				
				logger.debug(e.getMessage());
			}
			
		}
		isStopJM = false;
		
		if(this.getJobStatus() != JOBSTATUS_CRASH && this.getJobStatus()!=JOBSTATUS_FAILED){
			if(getJobType() == JobType.JOBTYPE_FILECOPY_BACKUP){
				try{
					Thread.sleep(1000*60);
				}catch(InterruptedException e){
					logger.error("sleep error after one job");
				}
				logger.info("Archive Job is successful and so calling submitArchiveJob() ");
				ArchiveService.getInstance().submitArchiveJob();
				
			} else if(getJobType() == JobType.JOBTYPE_FILECOPY_SOURCEDELETE){
				if(DeleteArchiveService.getInstance().hasMakeupTriggers()){
					DeleteArchiveService.getInstance().deleteMakeupSchedule();
				}
//				DeleteArchiveService.getInstance().submitArchiveSourceDeleteJob();
			}
			
		}
	}

	@Override
	protected long getDefaultJobType() {
		return getJobType();
	}
	
	private ArchiveConfiguration getConfiguration(long jobType) throws ServiceException{
		if(jobType == JobType.JOBTYPE_FILECOPY_BACKUP)
			return ArchiveService.getInstance().getArchiveConfiguration();
		else if(jobType == JobType.JOBTYPE_FILECOPY_SOURCEDELETE)
			return DeleteArchiveService.getInstance().getArchiveDelConfiguration();
		else 
			return null;
	}
	
	private void setTriggerTimes(JobExecutionContext context, long jobType){
		if(isMakeupJob(jobType) && context.getTrigger() instanceof SimpleTrigger){
			SimpleTrigger trigger = (SimpleTrigger) context.getTrigger();
			triggerTimes = trigger.getTimesTriggered();
			logger.info("This is a makeup job, current trigger time is " + triggerTimes);
		}
	}
	
	private boolean isMakeupJob(long jobType){
		return jobType == JobType.JOBTYPE_FILECOPY_BACKUP && ArchiveService.getInstance().isMakeupJob()
				|| jobType == JobType.JOBTYPE_FILECOPY_SOURCEDELETE && DeleteArchiveService.getInstance().isMakeupJob();
	}
}



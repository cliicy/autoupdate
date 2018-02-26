package com.ca.arcflash.webservice.scheduler;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveJobScript;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.service.ArchiveCatalogSyncService;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.DeleteArchiveService;
import com.ca.arcflash.webservice.service.PurgeArchiveService;
import com.ca.arcflash.webservice.service.RestoreArchiveService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.internal.ArchiveJobConverter;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;


public class ArchivePurgeJob extends BaseArchiveJob{
	private static final Logger logger = Logger.getLogger(ArchivePurgeJob.class);
	protected ArchiveJobConverter jobConverter = new ArchiveJobConverter();
	private boolean purgeJob4FC = false;
				
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("purge job execute...");
		if(BackupService.getInstance().isBackupToRPS()){
			logger.info("archive purge job will run on RPS");
			return;
		}
		
		Date date1 = context.getFireTime();
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		JobDetail jobDetail = context.getJobDetail();
		Object jobID = jobDetail.getJobDataMap().get(BaseService.JOB_ID);
		if(jobID != null){
			this.shrmemid = (Long)jobID;
		}
		purgeJob4FC = false;
		if(jobDetail.getJobDataMap().containsKey("purgeJob4FC")){
			purgeJob4FC = (boolean) jobDetail.getJobDataMap().get("purgeJob4FC");
		}
		
		if(purgeJob4FC && !PurgeArchiveService.getInstance().isFCPurgeJobEnabled()){
			logger.warn("FC purge job is disabled - return");
			return;
		}
		if(ArchiveService.getInstance().isArchiveBackupJobRunning() 
				|| PurgeArchiveService.getInstance().isPurgeJobRunning()
				|| RestoreArchiveService.getInstance().isArchiveRestoreJobRunning()
				|| ArchiveCatalogSyncService.getInstance().isArchiveCatalogSyncJobRunning()
				|| DeleteArchiveService.getInstance().isFileArchiveJobRunning()){
			logger.info("There is another job running...");
			String name = ((JobDetailImpl)jobDetail).getName();
			String date = BackupConverterUtil.dateToString(date1);
			nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_JOB_SKIPPED,new String[]{name, date,"","",""});
			sendEmailOnMissedJob(context, Job_Type_ArchivePurge);
			if(purgeJob4FC){
				synchronized(missFCPurgeJobLock){
					missedFCPurgeJobContext = context;
				}
			} else {
				synchronized(missPurgeJobLock){
					missedPurgeJobContext = context;
				}
			}
			return;
		}
		
		ArchiveConfiguration configuration = null;
		try {
			if(purgeJob4FC){
				configuration = ArchiveService.getInstance().getArchiveConfiguration();
			} else {
				configuration = DeleteArchiveService.getInstance().getArchiveDelConfiguration();
			}
			
			if(configuration == null) {
				logger.error("get archive configuration error");
				return;
			}
		} catch (ServiceException e) {
			logger.error("ArchiveService.getInstance().getArchiveConfiguration() error");
			
		}
		ArchiveJobScript jobScript = generateJobScript(configuration, shrmemid, purgeJob4FC);
		if(jobScript == null) {
			logger.error("generateJobScript in purge job exec error");
			return ;
		}
		if(shrmemid <= 0)
			initShrmemid();
		
		try {
			if(preprocess(jobScript)){
				if(0 != PurgeArchiveService.getInstance().purge(jobScript)){
					logger.error("submit purge job error ");
					stopJobMonitor();
					setJobStatus(JOBSTATUS_FAILED);
					
					//sonle01: Inform sync Data to Edge
					EdgeDataSynchronization.SetSyncDataFlag();
					
					return;
				}
				
				setJobType(Constants.AF_JOBTYPE_ARCHIVE_PURGE);
			}
		} catch (ServiceException e) {
			stopJobMonitor();
			logger.error("ArchiveService.getInstance().purge error", e);
		}			
		
		//sonle01: Inform sync Data to Edge
		EdgeDataSynchronization.SetSyncDataFlag();
	}
	
	public ArchiveJobScript generateJobScript(ArchiveConfiguration configuration, long shrmemid, boolean purgeJob4FC) {
		try {
			ArchiveJobScript result = jobConverter.convert4PurgeJob(configuration, BaseArchiveJob.Job_Type_ArchivePurge, ServiceContext.getInstance().getLocalMachineName(), shrmemid,"","","", purgeJob4FC);
			return result;
		} catch (Exception e) {
			logger.error("Generate backup job script exception", e);
		}
		
		return null;
	}
	
	protected void retryJobWhenFailedOrCrashed() {
		if(this.getJobStatus() == JOBSTATUS_FAILED || this.getJobStatus() == JOBSTATUS_CRASH){
			logger.debug("retryJobWhenFailedOrCrashed() - begin, jobid= " + this.shrmemid);
			
			if(PurgeArchiveService.getInstance().isMakeupJob(purgeJob4FC)){
				PurgeArchiveService.getInstance().deleteMakeupJobs(purgeJob4FC);
				logger.info("The current job is makeup job - return");
				return;
			}
			
			SimpleTriggerImpl trig = new SimpleTriggerImpl();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 30);
			Date startDate = cal.getTime();					
			trig.setStartTime(startDate);
			trig.setRepeatCount(0);
			trig.setRepeatInterval(0);
			String jobname = "";
			if(purgeJob4FC){
				jobname = BaseService.JOB_NAME_ARCHIVE_PURGE_FOR_FC+"_"+ this.shrmemid +"_" + Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME;
				trig.setName(BaseService.TRIGGER_NAME_ARCHIVE_PURGE_FOR_FC);
			} else {
				jobname = BaseService.JOB_NAME_ARCHIVE_PURGE+"_"+ this.shrmemid +"_" + Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME;
				trig.setName(BaseService.TRIGGER_NAME_ARCHIVE_PURGE);
			}
			trig.setGroup(BaseService.TRIGGER_GROUP_ARCHIVE_PURGE_MAKEUP);
			PurgeArchiveService.getInstance().setMakeupJobs(jobname, purgeJob4FC);
			JobDetail jobDetail = new JobDetailImpl(jobname, BaseService.JOB_GROUP_ARCHIVE_PURGE_MAKEUP, ArchivePurgeJob.class);
			jobDetail.getJobDataMap().put("purgeJob4FC", purgeJob4FC);

			try {
				PurgeArchiveService.getInstance().getScheduler().scheduleJob(jobDetail, trig);
			} catch (SchedulerException e) {
				logger.error("re-schedule archive job error");
				return ;
			}
			logger.debug("retryJobWhenFailedOrCrashed() - end");
		}
	}

	@Override
	protected void afterComplete(JJobMonitor jJM) {
		if (jJM!=null && jJM.getUlJobPhase() == JobExitPhase) {
			
		    makeupMissedFileCopy();
		    makeupMissedFileArchive();
		    if(purgeJob4FC){
		    	makeupMissedPurge();
		    } else {
		    	makeupMissedFCPurge();
		    }
		    
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
		
		retryJobWhenFailedOrCrashed();
		
	}

	@Override
	protected long getDefaultJobType() {
		return JobType.JOBTYPE_FILECOPY_PURGE;
	}
}

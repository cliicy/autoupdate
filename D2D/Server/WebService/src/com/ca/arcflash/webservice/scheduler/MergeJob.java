package com.ca.arcflash.webservice.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.service.jni.model.MergeJobScript;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.EveryMonthSchedule;
import com.ca.arcflash.webservice.data.EveryWeekSchedule;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.merge.MergeJobMonitor;
import com.ca.arcflash.webservice.data.merge.MergeJobStatus;
import com.ca.arcflash.webservice.data.merge.MergeMethod;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.service.AbstractMergeService;
import com.ca.arcflash.webservice.service.AbstractMergeService.MergeEvent;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.MergeService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.WebServiceMessages;


public class MergeJob extends AbstractMergeJob {
	private static final Logger logger = Logger.getLogger(MergeJob.class);
	
	public MergeJob() {
		
	}
	
	public MergeJob(int jobId) {
		this.jobId = jobId;
	}
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		super.execute(context);
		runningNodeUUID = CommonService.getInstance().getNodeUUID();
		try {
			if(jobId <= 0){
				synchronized(MergeService.getInstance()) {
					if(event != MergeEvent.MANUAL_RESUME 
							&& !MergeService.getInstance().isMergeJobAvailable(null))
						return;
					if(MergeService.getInstance().checkForResume(event) != 0)
						return;
					
					if(MergeService.getInstance().canResumeMerge(event) == -1)
						return;
					((MergeService)getMergeService()).mergeStart(this);
				}
			}
		}catch(ServiceException se) {
			logger.warn("Cannot run merge job now");
			return;
		}
		updateMergeJobStatus(MergeStatus.Status.TORUNN, null);
		run();
	}	

	protected void updateMergeJobStatus(MergeStatus.Status status, 
			MergeJobMonitor jobMonitor){
		MergeStatus ms = MergeService.getInstance().getMergeJobStatus();
		updateMergeJobStatus(status, ms, jobMonitor);	
		if(jobMonitor != null){
			boolean finished = this.isJobDone(jobMonitor.getDwMergePhase(), 
					jobMonitor.getJobStatus());
			MergeService.getInstance().updateMergeJobStatus(status, ms,
					jobMonitor, jobId, finished, vmInstanceUUID);
		}
	}
	
	protected MergeJobScript generateMergeJobScript() {
		BackupConfiguration backupConf = null;
		MergeJobScript jobScript = null;
		try {
			backupConf = BackupService.getInstance().getBackupConfiguration();
		}catch(Exception e) {
			logger.error("Failed to get backup configuraiton for merge", e);
			return null;
		}
		
		if(backupConf == null) {
			logger.error("Failed to run merge job, no backup configuration");
			return null;
		}
		
		jobScript = this.generateMergeJobScript(backupConf.getDestination(), 
				backupConf.getUserName() != null ? backupConf.getUserName() : "", 
				backupConf.getPassword() != null ? backupConf.getPassword() : "", 
						backupConf.getRetentionCount(), 
						backupConf.getRetentionPolicy().isUseBackupSet(), 
						backupConf.getRetentionPolicy().getBackupSetCount());
		if(jobScript != null)
			jobScript.setDwSessionType(SESSIONTYPE_D2D);
		
		if(backupConf.getBackupDataFormat() >0){// new format
			AdvanceSchedule ad = backupConf.getAdvanceSchedule();
			if (ad != null) {
				PeriodSchedule ps = ad.getPeriodSchedule();
				if (ps != null) {					
					EveryDaySchedule day = ps.getDaySchedule();
					if (day != null && day.isEnabled()) {
						jobScript.setDwDailyCnt(day.getRetentionCount());
					}
					EveryWeekSchedule week = ps.getWeekSchedule();
					if (week != null && week.isEnabled()) {
						jobScript.setDwWeeklyCnt(week.getRetentionCount());
					}
					EveryMonthSchedule month = ps.getMonthSchedule();
					if (month != null && month.isEnabled()) {
						jobScript.setDwMonthlyCnt(month.getRetentionCount());
					}
				}
				
//				if(ad.isPeriodEnabled()){ // in oolong this is always use the new merge method.
					jobScript.setDwMergeMethod(MergeMethod.EMM_SESS_RANGES.getValue());
//				}
			}
		}	
		
		return jobScript;
	}
		
	protected AbstractMergeService getMergeService() {
		return MergeService.getInstance();
	}

	@Override
	protected boolean isOutScheduleTime() {
		RetentionPolicy policy = getMergeService().getRetentionPolicy(null); 
		if(policy == null || !policy.isStopMergeAfterSchedule() )
			return false;
		if(getMergeService().isInMergeTimeRange(null))
			return false;
		else {
			try {
				MergeService.getInstance().pauseMerge(
						AbstractMergeService.MergeEvent.NO_SCHEDULE, null, null);
			}catch(ServiceException e) {
				logger.error("Failed to pause merge", e);
			}
			return true;
		}
	}

	@Override
	protected void sendMail(MergeJobMonitor jJM) {
		logger.debug("Enter send mail");
		
		try {
			super.sendMail(jJM);
			PreferencesConfiguration preferencesConfig = CommonService.getInstance().getPreferences();
			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			if (preferencesConfig == null || configuration == null) {
				return;
			}
			
			BackupEmail email = preferencesConfig.getEmailAlerts();
			if (email == null || !email.isEnableSettings())
				return;
			
			if(email.isEnableEmailOnMergeFailure() 
					&& jJM.getJobStatus() != MergeJobStatus.EJS_JOB_FINISH.getValue()
				|| email.isEnableEmailOnMergeSuccess() 
				&& jJM.getJobStatus() == MergeJobStatus.EJS_JOB_FINISH.getValue()) {
				
				EmailSender emailSender = new EmailSender();
				String emailJobStatus = WebServiceMessages.getResource("EmailJobStatus");
				String jobStatus = jobStatus2String(jJM.getJobStatus());
				String hostName = ServiceContext.getInstance().getLocalMachineName();
				String subject = email.getSubject() + "-"+ WebServiceMessages.getResource("mergeJobString") 
					+ " " +emailJobStatus+jobStatus+"("+hostName+")";				
				boolean priority = !(jJM.getJobStatus() == MergeJobStatus.EJS_JOB_FINISH.getValue());
				String serverNameLabel = WebServiceMessages.getResource("EmailServerName", 
						ServiceContext.getInstance().getProductNameD2D());
				
				List<EmailContentTemplate.Content> contents = new ArrayList<EmailContentTemplate.Content>();
				String content = getEmailContent(contents, jobStatus, jJM.getUllStartTime(), 
						jJM.getJobStatus(), jJM.getDwJobID(), 
						email.isEnableHTMLFormat(), serverNameLabel, BaseJob.getBackupSettingsURL(), 
						configuration.getDestination());
				
				emailSender.sendEmail(email, subject, content, changeMergeStatusToBaseJobStatus (jJM.getJobStatus() ), priority, 
						CommonEmailInformation.PRODUCT_TYPE.ARCFlash.getValue(), JobType.JOBTYPE_MERGE, null  );
			}
		}catch(Exception se) {
			logger.error("Failed to send merge email " + se);
		}
		
		logger.debug("Send mail complete");
	}
	
	@Override
	protected ActivityLogResult getActivityLog(long jobId) throws Exception {
		return CommonService.getInstance().getJobActivityLogs(jobId, 0, 500);
	}

	@Override
	protected boolean isVHDMerge() {
		try {
			BackupConfiguration conf = BackupService.getInstance().getBackupConfiguration();
			if(conf != null)
				return conf.getCompressionLevel() == 0 && conf.getEncryptionAlgorithm() == 0;
		}catch(Exception se) {
			logger.error("Failed to get backup configuration" + se);
		}
		return false;
	}

	@Override
	protected String getVMName() {
		return null;
	}
	
/*	@Override
	public RetentionPolicy getRetentionPolicy() {
		return MergeService.getInstance().getRetentionPolicy();
	}*/
}

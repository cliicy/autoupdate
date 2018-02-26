package com.ca.arcflash.webservice.scheduler;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.JobDetailImpl;

import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.job.rps.CopyJobArg;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.CopyService;
import com.ca.arcflash.webservice.service.RestoreService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.internal.RecoveryPointConverter;
import com.ca.arcflash.webservice.service.rps.JobService;
import com.ca.arcflash.webservice.util.EmailContentTemplate;

public class CopyJob extends BaseJob {

	private static final Logger logger = Logger.getLogger(CopyJob.class);
	private RecoveryPointConverter recoveryPointConverter = new RecoveryPointConverter();
	private volatile static boolean isJobRunning = false;
	private volatile static boolean JobMonitorEdgeThreadStarted = false;
	private String copyDest = null;
	
	static final Observer[] observers = new Observer[]{};
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("execute(JobExecutionContext) - start");
		JobDetailImpl jobDetail = (JobDetailImpl)context.getJobDetail();
		Date date1 = context.getFireTime();
		rpsPolicyUUID = jobDetail.getJobDataMap().getString(BaseService.RPS_POLICY_UUID);
		rpsDataStoreUUID = jobDetail.getJobDataMap().getString(BaseService.RPS_DATASTORE_UUID);
		rpsDataStoreName = jobDetail.getJobDataMap().getString(BaseService.RPS_DATASTORE_DISPLAY_NAME);
		rpsHost = (RpsHost) jobDetail.getJobDataMap().get(BaseService.RPS_HOST);
		Boolean runNow = jobDetail.getJobDataMap().getBoolean(Constants.RUN_NOW);
		isOnDemand = jobDetail.getJobDataMap().getBoolean(BaseService.ON_DEMAND_JOB);
		Object jobID = jobDetail.getJobDataMap().get(BaseService.JOB_ID);
		if(jobID != null)
			this.shrmemid = (Long)jobID;
				
		NativeFacade nativeFacade2 = RestoreService.getInstance().getNativeFacade();
		if (nativeFacade2.checkJobExist()){
			//fix issue 18712980
			String name = BackupConverterUtil.backupIndicatorToName(jobDetail.getName());
			String date = BackupConverterUtil.dateToString(date1);
			nativeFacade2.addLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_JOB_SKIPPED,new String[]{name,date});
			logger.error("Other jobs are running, exist");
			return;
		}
		/*if(rpsHost == null){
		 *if it's null, we think it's copy from shared folder, so don't check rps data store and policy.
			BackupRPSDestSetting setting = BackupService.getInstance().getRpsSetting();
			if(setting != null){
				rpsHost = setting.getRpsHost();
				rpsPolicyUUID = setting.getRPSPolicyUUID();
				rpsDataStoreUUID = setting.getRPSDataStore();
			}
		}*/
		com.ca.arcflash.webservice.data.restore.CopyJob job = (com.ca.arcflash.webservice.data.restore.CopyJob)context.getJobDetail().getJobDataMap().get("Job");
		if(rpsHost!=null)
			job.setRpsHostname(rpsHost.getRhostname());
		NativeFacade nativeFacade = (NativeFacade)context.getJobDetail().getJobDataMap().get("NativeFacade");
		copyDest = job.getDestinationPath();
		
		try {
			if(shrmemid <= 0)
				initShrmemid();
			if(checkRPS4Job(runNow, rpsHost != null, rpsHost, 
				rpsDataStoreUUID, context, 
				JobType.JOBTYPE_COPY, nativeFacade)){
				//submit job to rps
				CopyJobArg jobArg = new CopyJobArg();
				getJobArgWithSrc(jobDetail, rpsPolicyUUID,
						rpsDataStoreUUID, JobType.JOBTYPE_COPY, 
						rpsHost,jobArg, rpsDataStoreName);
				
				jobArg.setJobScript(job);
				JobService.getInstance().submitCopyJob(jobArg, rpsHost);
				return;	
			}	
		} catch (ServiceException se) {
			//TODO send email
			return;
		}
		JJobScript targetJob;
		try {			
			targetJob = recoveryPointConverter.convert2JobScript(job, ServiceContext.getInstance().getLocalMachineName());
			js = targetJob;
			if(!preprocess(targetJob, observers))
			{	
				logger.debug("Job monitor is running, exist");
				return;
			}
			nativeFacade.copy(targetJob);
		} catch (UnknownHostException e) {
			logger.error("execute(JobExecutionContext)", e);

			
		} catch (Throwable e) {
			logger.error("execute(JobExecutionContext)", e);

		}
		
		//sonle01: Inform sync Data to Edge
		EdgeDataSynchronization.SetSyncDataFlag();

		logger.debug("execute(JobExecutionContext) - end");
	}

	protected String getMissedJobContent(boolean isEnableHTMLFormat,
			JobExecutionContext context, String dest, boolean causedByRPS) {
		String exeDate = BackupConverterUtil.dateToString(context.getFireTime());		
		String url = getBackupSettingsURL();
		ActivityLogResult result= null;
		if(causedByRPS) {
			try {
				result = CommonService.getInstance().getActivityLogs(0, 5);
			} catch (Exception e) {
				logger.error("Failed to get activity log", e);
			}
		}
		if (isEnableHTMLFormat) {
			return EmailContentTemplate.getHtmlContent(Constants.JOBSTATUS_MISSED,
					this.getDefaultJobType(), 0, 0, exeDate, dest, copyDest,
					result, url);
		} else {
			return EmailContentTemplate.getPlainTextContent(Constants.JOBSTATUS_MISSED,
					this.getDefaultJobType(), 0, 0, exeDate, dest, copyDest,
					result, url);
		}
	}
	
	/**
	 * Get the job lock.
	 * 
	 * @return true if successfully getting the lock.
	 */
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
	
	protected synchronized boolean getJobMonitorEdgeThreadLock(){
		if(JobMonitorEdgeThreadStarted)	
			return false;
		
		JobMonitorEdgeThreadStarted = true;
		return true;
	}
	
	protected synchronized void releaseJobMonitorEdgeThreadLock(){
		JobMonitorEdgeThreadStarted = false;
	}
	
	public static boolean isJobRunning() {
		return isJobRunning;
	}

	@Override
	protected long getDefaultJobType() {
		return JobType.JOBTYPE_COPY;
	}

	@Override
	protected void afterComplete() {
		CopyService.getInstance().resetSubmitCopy();
	}
	
}

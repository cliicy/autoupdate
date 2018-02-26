package com.ca.arcflash.webservice.scheduler;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.JobDetailImpl;

import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.job.rps.RestoreJobArg;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.RestoreService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.internal.RestoreJobConverter;
import com.ca.arcflash.webservice.service.rps.JobService;

public class RestoreJob extends BaseJob {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(RestoreJob.class);
	private volatile static boolean isJobRunning = false;
	private volatile static boolean JobMonitorEdgeThreadStarted = false;
	private static final boolean allowMultiple = true;
//	private String destination;

	private RestoreJobConverter restoreJobConverter = new RestoreJobConverter();
	static final Observer[] observers = new Observer[]{};
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug("execute(JobExecutionContext) - start");
		Date date1 = context.getFireTime();
		JobDetail jobDetail = context.getJobDetail();
		rpsPolicyUUID = jobDetail.getJobDataMap().getString(BaseService.RPS_POLICY_UUID);
		rpsDataStoreUUID = jobDetail.getJobDataMap().getString(BaseService.RPS_DATASTORE_UUID);
		rpsDataStoreName = jobDetail.getJobDataMap().getString(BaseService.RPS_DATASTORE_DISPLAY_NAME);
		rpsHost = (RpsHost) jobDetail.getJobDataMap().get(BaseService.RPS_HOST);
		Boolean runNow = jobDetail.getJobDataMap().getBoolean(Constants.RUN_NOW);
		isOnDemand = true;
		Object jobID = jobDetail.getJobDataMap().get(BaseService.JOB_ID);
		if(jobID != null)
			this.shrmemid = (Long)jobID;
		
		NativeFacade nativeFacade1 = RestoreService.getInstance().getNativeFacade();
		if (RestoreService.getInstance().isCheckJob()){
			//Because one job is running, a new job named "%s" at "%s" cannot be arranged.
			String name = BackupConverterUtil.backupIndicatorToName(((JobDetailImpl)jobDetail).getName());
			String date = BackupConverterUtil.dateToString(date1);
			//fix issue 18712980
			nativeFacade1.addLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_JOB_SKIPPED,new String[]{name,date});
			
			logger.debug("Other jobs are running, exist");
			return;
		}

		com.ca.arcflash.webservice.data.restore.RestoreJob job = (com.ca.arcflash.webservice.data.restore.RestoreJob)context.getJobDetail().getJobDataMap().get("Job");
		NativeFacade nativeFacade = (NativeFacade)context.getJobDetail().getJobDataMap().get("NativeFacade");
		if(shrmemid <= 0)
			initShrmemid();
		try {
			if(checkRPS4Job(runNow, rpsHost != null, rpsHost, 
				rpsDataStoreUUID, context, 
				JobType.JOBTYPE_RESTORE, nativeFacade1)){
				//submit job to rps
				RestoreJobArg jobArg = new RestoreJobArg();
				getJobArgWithSrc(jobDetail, rpsPolicyUUID,
						rpsDataStoreUUID, JobType.JOBTYPE_RESTORE, 
						rpsHost,jobArg, rpsDataStoreName);
				jobArg.setJobScript(job);
				JobService.getInstance().submitRestoreJob(jobArg, rpsHost);
				return;	
			}	
		} catch (ServiceException se) {
			//TODO send email
			return;
		}
		//for E15 cas feature
		if(job.getRestoreExchangeGRTOption()!=null){
			RestoreService.getInstance().setDefaultE15CAS(job.getRestoreExchangeGRTOption().getDefaultE15CAS());
		}
		
		JJobScript targetJob;
		try {			
			targetJob = restoreJobConverter.convert2JobScript(job, ServiceContext.getInstance().getLocalMachineName());
			js = targetJob;
//			this.destination=job.getSessionPath();
			if(!preprocess(targetJob, observers))
			{	
				logger.debug("Job monitor is running, exist");
				return;
			}
			nativeFacade.restore(targetJob);
		} catch (UnknownHostException e) {
			logger.error("execute(JobExecutionContext)", e);

		} catch (Throwable e) {
			logger.error("execute(JobExecutionContext)", e);

		}
		
		//sonle01: Inform sync Data to Edge
		EdgeDataSynchronization.SetSyncDataFlag();

		logger.debug("execute(JobExecutionContext) - end");
	}
	
	/**
	 * For restore job, we need to check whether job is done with phase 0xE. 
	 */
	@Override
	protected boolean isJobDone(long jobPhase, long jobStatus) {		
		if(jobPhase == Constants.RestoreJob_Phase_PROC_EXIT) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Get the job lock.
	 * 
	 * @return true if successfully getting the lock.
	 */
	protected synchronized boolean getJobLock() {
		if(isAllowMultiple()) return true;
		
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
	public String getDestination(){
//		return destination;
		return js.getPAFNodeList().get(0).getPwszSessPath();
	}

	@Override
	protected long getDefaultJobType() {
		return JobType.JOBTYPE_RESTORE;
	}

	public static boolean isAllowMultiple() {
		return allowMultiple;
	}
}

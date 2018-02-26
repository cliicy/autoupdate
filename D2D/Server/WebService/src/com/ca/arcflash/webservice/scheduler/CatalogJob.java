package com.ca.arcflash.webservice.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.JobDetailImpl;

import com.ca.arcflash.jni.common.JJobHistory;
import com.ca.arcflash.service.common.CatalogQueueType;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.RPSDataStoreInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveScheduleStatus;
import com.ca.arcflash.webservice.data.archive.JArchiveJob;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.job.rps.CatalogJobArg;
import com.ca.arcflash.webservice.data.job.rps.IJobDependency;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.CatalogService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.CopyService;
import com.ca.arcflash.webservice.service.MergeService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.rps.JobService;

public class CatalogJob extends BaseJob
{
	private static final Logger logger = Logger.getLogger(CatalogJob.class);
	private volatile static boolean isJobRunning = false;
	private static final long noJob = 0xa1000019;
	private static Object makeupLock = new Object();
	public boolean keepWait = true;
	public static final int JOB_TYPE_CATALOG = 100;//A common catalog job type
	protected long jobType = JobType.JOBTYPE_CATALOG_FS;
	
	static final Observer[] observers = new Observer[] {CatalogService.getInstance(),
			CopyService.getInstance()};
	
	public CatalogJob(){
		
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("Executing catalog job");
		JobDetail jobDetail = context.getJobDetail();
		long jobID = jobDetail.getJobDataMap().getLong(CatalogService.JOB_ID);
		long queueType = jobDetail.getJobDataMap().getLong(CatalogService.QUEUE_TYPE);
		long jobType = jobDetail.getJobDataMap().getLong(CatalogService.JOB_TYPE);
		Object waittTime = jobDetail.getJobDataMap().get(CatalogService.WAIT_TIME);
		String destination = jobDetail.getJobDataMap().getString(CatalogService.DESTINATION);
		String userName = jobDetail.getJobDataMap().getString(CatalogService.USERNAME);
		String password = jobDetail.getJobDataMap().getString(CatalogService.PASSWORD);
		Boolean runNow = jobDetail.getJobDataMap().getBoolean(Constants.RUN_NOW);
		Boolean resumed = jobDetail.getJobDataMap().getBoolean(CatalogService.RESUMED);
		
		if(jobType > 0)
			this.jobType = jobType;
		
		long waitTime = 0;
		boolean isJobDisabled = false;
		if(waittTime != null)
			waitTime = (Long)waittTime;
	
		if(!getJobLock())
			return;
		jobPhase = 0;
		try {
			if(waitTime != 0) {
				CatalogService.getInstance().setMakeupJob(this);
				synchronized(makeupLock) {
					while(keepWait){
						makeupLock.wait(waitTime);
						keepWait = false;
					}
				}
			}
			
			if(resumed == null || !resumed) {
				Object ojobID = jobDetail.getJobDataMap().get(BaseService.JOB_ID);
				if(ojobID != null)
					this.shrmemid = (Long)ojobID;
				if(shrmemid <= 0)
					initShrmemid();
				logger.debug("Check rps information");
				BackupRPSDestSetting setting = BackupService.getInstance().getRpsSetting();
//				RpsHost rpsHost = null;
				if(setting != null){
					rpsHost = setting.getRpsHost();
					rpsPolicyUUID = setting.getRPSPolicyUUID();
					rpsDataStoreUUID = setting.getRPSDataStore();
					rpsDataStoreName = setting.getRPSDataStoreDisplayName();
				}
				try {
					if(this.checkRPS4Job(runNow, rpsHost != null, rpsHost, rpsDataStoreUUID, context, 
							this.jobType, BackupService.getInstance().getNativeFacade())){
						CatalogJobArg jobArg = new CatalogJobArg();
						this.getJobArg((JobDetailImpl)jobDetail, rpsPolicyUUID, 
								setting.getRPSDataStore(), this.jobType, jobArg, rpsDataStoreName);
						jobArg.setQueueType(queueType);
						jobArg.setDestination(destination);
						jobArg.setUserName(userName);
						jobArg.setPassword(password);
						addJobDependency(jobArg);
						releaseJobLock();
						logger.debug("Submit job to rps");
						JobService.getInstance().submitCatalog(jobArg, rpsHost);
						return;
					}
				}catch(Exception se) {
					releaseJobLock();
					//TODO email alert
					return;
				}
				logger.debug("Run catalog job");
				preprocess(null, observers);
				long ret = CatalogService.getInstance().launchCatalogJob(shrmemid, queueType, 
						destination, userName, password);
				if(ret != 0){
					stopJobMonitor();
					if(ret == 4)
					{
						isJobDisabled = true;
					}
				} 
			}else {
				//it's resume job after restart
				resumeAfterRestarted(jobID, observers);

			}
				
			synchronized(phaseLock){
				while(jobPhase != Constants.CatalogDone){
					this.phaseLock.wait();
			}
		}
				//after 5 seconds, try to launch another catalog job.
//				Thread.sleep(5 * 1000);
		}catch(ServiceException e){
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}catch(InterruptedException ie){
			logger.error(ie.getMessage() == null ? ie : ie.getMessage());
		} catch(Throwable t){
			logger.error(t.getMessage());
		}finally {
			//after 5 seconds, try to launch another catalog job.
			stopJobMonitor();
			if(!isJobDisabled)
				CatalogService.getInstance().setMakeupJob(null);
		}

		releaseJobLock();
		
		//sonle01: Inform sync Data to Edge
		EdgeDataSynchronization.SetSyncDataFlag();
	}
	
	private void addJobDependency(CatalogJobArg jobArg) {
		List<String> jobDependencies = new ArrayList<String>();
		jobDependencies.add(IJobDependency.CATALOG_JOB);
		jobDependencies.add(IJobDependency.COPY_JOB);
		jobArg.setJobDependencies(jobDependencies.toArray(new String[0]));
	}
	
	protected class CatalogJobMonitorThread extends JobMonitorThread {
		public CatalogJobMonitorThread(BaseJob job) {
			super(job);
		}
		
		@Override
		protected void optionalUpdate(JJobMonitor jJM) {}

		@Override
		protected void afterComplete(JJobMonitor jJM) {
			logger.info("catalog job completed " + job.shrmemid);
			synchronized(job.phaseLock){
				job.jobPhase = Constants.CatalogDone;
				job.phaseLock.notify();
				if(jJM != null)
					job.jobStatus = jJM.getUlJobStatus();
				
				MergeService.getInstance().jobEnd(job);
				if(jJM.getUlJobType() == JobType.JOBTYPE_CATALOG_FS
						|| jJM.getUlJobType() == JobType.JOBTYPE_CATALOG_FS_ONDEMAND)//type JOBTYPE_CATALOG_FS = 11
				{
					
					synchronized (ArchiveService.getInstance().getObjectLock()){
						logger.debug("Entered synchronize block in catalog job");
						JArchiveJob archiveJob = new JArchiveJob();
						archiveJob.setScheduleType(ArchiveScheduleStatus.ScheduleReady);
						archiveJob.setbOnlyOneSession(true);
					
						ArchiveService.getInstance().InsertJobInfoToGlobalList(ArchiveService.getInstance().GetArchiveJobsInfo(archiveJob));
						//ArchiveService.getInstance().setArchiveJobsInformation(ArchiveService.getInstance().GetArchiveJobsInfo(archiveJob));
						
						logger.debug("Exiting synchronize block in catalog job");
					}
				}else {
					startMergeJob(jJM);
				}
			}

			if (jJM!=null && (jJM.getUlJobPhase() == Constants.JobExitPhase 
					|| jJM.getUlJobPhase() == Constants.CatalogDone)) {
				sendEmail(jJM);
				saveRSSFeed(jJM);
			}
			
			if(jJM.getUlJobType() == 0) {			
				jJM.setUlJobType(Constants.JOBTYPE_CATALOG_FS);
			}
			job.afterComplete();
		}
	}
	protected boolean getJobLock(){
		if(isJobRunning)
			return false;
		
		isJobRunning = true;
		logger.info("Get catalog job lock.");
		return true;
	}
	
	protected void releaseJobLock() {
		isJobRunning = false;
		logger.info("Release catalog job lock.");
	};
	
	protected long launchJob(long id) throws ServiceException {
		return CatalogService.getInstance().launchCatalogJob(id, CatalogQueueType.REGULAR_JOB);
	}
	
	@Override
	protected void resumeAfterRestarted(long jobID, Observer[] observers) {
		try {
			shrmemid = jobID;
			logger.info("resume jobid(shrmemid):" + shrmemid);

			if(observers != null){
				for(Observer observer : observers){
		        	addObserver(observer);
		        }
			}
			this.pauseMergeJob(JOB_TYPE_CATALOG);
			isStopJM = false;
	        new Thread(new CatalogJobMonitorThread(this)).start();
		}catch (RuntimeException e) {
			releaseJobLock();
			throw e;
		}

		logger.debug("preprocess(JJobScript) - end");
	}

	protected boolean preprocess(JJobScript js, Observer[] observers) {	

		try {
			JJobHistory jobHistory = new JJobHistory();
			jobHistory.setJobId(shrmemid);
			jobHistory.setJobType( (int)getDefaultJobType());
			jobHistory.setDatastoreUUID( getRPSDatastoreUUID() );
			RPSDataStoreInfo rpsDataStoreInfo = getRpsDataStoreInfo(getRPSDatastoreUUID());
	        jobHistory.setDatastoreVersion(rpsDataStoreInfo.getVersion());
	        jobHistory.setTargetUUID(rpsDataStoreInfo.getRpsServerId());
			jobHistory.setPlanUUID(isOnDemand ? "" : getPlanUUID(null));			
			CommonService.getInstance().getNativeFacade().updateJobHistory( jobHistory );
			
			logger.info("Processing jobid(shrmemid):" + shrmemid);

			if(observers != null){
				for(Observer observer : observers){
		        	addObserver(observer);
		        }
			}
			isStopJM = false;
			pauseMergeJob(JOB_TYPE_CATALOG);
			pool.submit(new CatalogJobMonitorThread(this));
//	        new Thread(new CatalogJobMonitorThread(this)).start();
		}catch (RuntimeException e) {
			releaseJobLock();
			throw e;
		}

		logger.debug("preprocess(JJobScript) - end");
		return true;
	}
	
	public static boolean isJobRunning() {
		return isJobRunning;
	}
	
	public void start() {
		keepWait = false;
		synchronized(makeupLock) {
			makeupLock.notify();
		}
	}

	@Override
	protected long getDefaultJobType() {
		return jobType;
	}
	
	public boolean isGRTCatalogJob() {
		if(jobType == JobType.JOBTYPE_CATALOG_GRT)
			return true;
		
		return false;
	}

	@Override
	protected void afterComplete() {
		CatalogService.getInstance().resetSubmitCatalog();
	}
	
	
}

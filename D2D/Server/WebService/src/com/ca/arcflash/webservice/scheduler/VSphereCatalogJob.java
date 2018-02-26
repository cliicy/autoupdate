package com.ca.arcflash.webservice.scheduler;


import java.util.HashSet;
import java.util.Observer;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.JobDetailImpl;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jni.common.JJobHistory;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.RPSDataStoreInfo;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.job.rps.CatalogJobArg;
import com.ca.arcflash.webservice.data.job.rps.IJobDependency;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.service.CatalogService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSPhereCatalogService;
import com.ca.arcflash.webservice.service.VSphereMergeService;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.VSphereJobContext;
import com.ca.arcflash.webservice.service.rps.JobService;

public class VSphereCatalogJob extends BaseVSphereJob {

	private static final Logger logger = Logger.getLogger(VSphereCatalogJob.class);
	private static final Observer[] vmObservers = new Observer[]{VSPhereCatalogService.getInstance()};
	private Object makeupLock = new Object();
	private static final Set<String> runningJobs = new HashSet<String>();
	protected long jobType = JobType.JOBTYPE_VM_CATALOG_FS;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetailImpl jobDetail = (JobDetailImpl)context.getJobDetail();
		String instanceUUID = jobDetail.getJobDataMap().getString("vmInstanceUUID");
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
		if(waittTime != null)
			waitTime = (Long)waittTime;
		
		this.shrmemid = jobID;
		
		if(this.shrmemid <= 0)
			shrmemid = CommonService.getInstance().getNativeFacade().getJobID();
						logger.info("Processing jobid(shrmemid):" + shrmemid);
		
		logger.info( StringUtil.enFormat("jobID[%d], jobType[%d] waitTime[%d] vmInstanceUUID[%s] jobName[%s]",
				shrmemid, queueType, waitTime,instanceUUID, jobDetail.getName()));
		if(!getJobLock(instanceUUID) || isJobRunningFromJobMonitor(instanceUUID, jobType)){
			logger.info("other vsphere catalog is running");
			return;
		}
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(instanceUUID);
		VMBackupConfiguration conf = null;
		try {
			conf = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if (isConfigurationForRemoteNode(conf)) {
				logger.info("The configuration is for remote nodes.");
				return;
			}
		}catch(ServiceException se) {
			logger.error("Failed to get vm backup configuration", se);
		}
		
		VSphereJobContext jobContext = new VSphereJobContext();
		jobContext.setExecuterInstanceUUID(instanceUUID);
		jobContext.setJobType(Constants.AF_JOBTYPE_VM_CATALOG_FS);
		jobContext.setJobLauncher(VSphereJobContext.JOB_LAUNCHER_VSPHERE);
		jobContext.setLauncherInstanceUUID(instanceUUID);
		if(conf != null && conf.getBackupVM() != null)
			jobContext.setVmName(conf.getBackupVM().getVmName());
		this.setJobContext(jobContext);
		
		jobPhase = 0;

		try {
			if (waitTime != 0) {
				VSPhereCatalogService.getInstance().setMakeupJob(this,instanceUUID);
				synchronized (makeupLock) {
					String info = StringUtil.enFormat("vpshere makeup catalog job with wating time[%d], jobName[%s]", waitTime, jobDetail.getName());
					logger.info("start " + info);
					makeupLock.wait(waitTime);
					logger.info("end "+ info);
				}
			}

			if(resumed == null || !resumed){
				if(conf == null || conf.getBackupVM() == null){
					logger.error("No vm backup configuration found");
				}else{
					if(checkRPSDataStore(runNow, context, destination, userName, password, 
							queueType, instanceUUID, conf.getBackupVM().getVmName(), 
							conf.getBackupVM().getEsxServerName(), 
							conf.getBackupRpsDestSetting().getRPSDataStoreDisplayName())){
						return;
					}
				}	
				
				preprocess(null, vmObservers);
				long ret = VSPhereCatalogService.getInstance().launchVSphereCatalogJob(this.shrmemid, queueType,
						instanceUUID, destination, userName, password);
				if (ret != 0) {
					stopJobMonitor();
				}
			}
			else{
				resumeAfterRestarted(shrmemid, vmObservers);
			}

			synchronized (phaseLock) {
				while (jobPhase != Constants.CatalogDone) {
					this.phaseLock.wait();
				}
			}

		} catch (ServiceException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		} catch (InterruptedException ie) {
			logger.error(ie.getMessage() == null ? ie : ie.getMessage());
		} catch (Throwable t) {
			logger.error(t.getMessage());
		} finally {
			// after 5 seconds, try to launch another catalog job.
			stopJobMonitor();
			VSPhereCatalogService.getInstance().setMakeupJob(null,instanceUUID);
			
		}
		//sonle01: Inform sync Data to Edge
		EdgeDataSynchronization.SetSyncDataFlag();
		releaseJobLock(instanceUUID);

	}
	
	public void start() {
		synchronized(makeupLock) {
			makeupLock.notify();
		}
	}
	
	protected class VSphereCatalogJobMonitorThread extends VSphereJobMonitorThread {
		public VSphereCatalogJobMonitorThread(BaseVSphereJob job,VSphereJobContext jobContext) {
			super(job,jobContext);
		}
		
		@Override
		protected void optionalUpdate(JJobMonitor jJM) {}

		@Override
		protected void afterComplete(JJobMonitor jJM) {
			logger.info("vsphere catalog job completed " + job.shrmemid);
			VSphereMergeService.getInstance().jobEnd(
					jobContext.getExecuterInstanceUUID(), job);
			synchronized(job.phaseLock){
				job.jobPhase = Constants.CatalogDone;
				job.phaseLock.notify();
				if(jJM != null)
					job.jobStatus = jJM.getUlJobStatus();	
//				startMergeJob(jJM);
			}
			
			if(jJM != null)
				sendEmail(jJM,jobContext.getExecuterInstanceUUID());
		}
	}
	
	protected boolean preprocess(JJobScript js, Observer[] observers) {		

		try {
			
			JJobHistory jobHistory = new JJobHistory();
			jobHistory.setJobId(shrmemid);
			jobHistory.setJobType((int) getDefaultJobType());
			jobHistory.setJobRunningNodeUUID(getJobContext()
					.getLauncherInstanceUUID());
			jobHistory.setJobDisposeNodeUUID(getJobContext()
					.getExecuterInstanceUUID());
			jobHistory.setJobDisposeNode(getJobContext().getVmName());
			jobHistory.setDatastoreUUID(getRPSDatastoreUUID());
			RPSDataStoreInfo rpsDataStoreInfo = getRpsDataStoreInfo(getRPSDatastoreUUID());
			jobHistory.setDatastoreVersion(rpsDataStoreInfo.getVersion());
			jobHistory.setTargetUUID(rpsDataStoreInfo.getRpsServerId());
			jobHistory.setPlanUUID(getPlanUUID(getJobContext().getExecuterInstanceUUID()));
			VSphereService.getInstance().getNativeFacade()
					.updateJobHistory(jobHistory);

			if (observers != null) {
				for(Observer observer : observers){
		        	addObserver(observer);
		        }
			}
			isStopJM = false;
			pauseMergeJob(CatalogJob.JOB_TYPE_CATALOG);
	        new Thread(new VSphereCatalogJobMonitorThread(this,this.getJobContext())).start();
		}catch (RuntimeException e) {
			releaseJobLock(getJobContext().getExecuterInstanceUUID());
			throw e;
		}

		logger.debug("preprocess(JJobScript) - end");
		return true;
	}
	
	
	@Override
	protected void resumeAfterRestarted(long jobID, Observer[] observers) {
		try {
			shrmemid = jobID;
			logger.info("resume jobid(shrmemid):" + shrmemid);
			//pauseMergejob
			pauseMergeJob(CatalogJob.JOB_TYPE_CATALOG);
			
			if(observers != null){
				for(Observer observer : observers){
		        	addObserver(observer);
		        }
			}
			isStopJM = false;
	        new Thread(new VSphereCatalogJobMonitorThread(this, this.getJobContext())).start();
		}catch (RuntimeException e) {
			releaseJobLock(getJobContext().getExecuterInstanceUUID());
			throw e;
		}

		logger.debug("preprocess(JJobScript) - end");
	}
	
	public boolean getJobLock(String vmInstanceUUID){
		synchronized(VSphereCatalogJob.class)
		{
			if(vmInstanceUUID == null){
				logger.error("Null UUID ");
				return false;
			}
			if(runningJobs.contains(vmInstanceUUID)){	
				return false;
			}else{
				runningJobs.add(vmInstanceUUID);
				return true;
			}
		}		
	}
	
	public static synchronized boolean isJobRunning(String vmInstanceUUID){
		if(vmInstanceUUID == null){
			logger.error("Null UUID ");
			return false;
		}
		return runningJobs.contains(vmInstanceUUID);
	}
	
	public void releaseJobLock(String vmInstanceUUID) {
		synchronized(VSphereCatalogJob.class)
		{
			if(vmInstanceUUID == null){
				logger.error("Null UUID ");
				return;
			}
			runningJobs.remove(vmInstanceUUID);
		}
		
	}

	@Override
	protected long getDefaultJobType() {		
		return jobType;
	}
	
	private boolean checkRPSDataStore(Boolean runNow, JobExecutionContext context, String destination,
			String userName, String password, long queueType, String instanceUUID, String vmName, 
			String esxName, String dataStoreName){
		logger.debug("Check rps information");
		BackupRPSDestSetting setting = VSphereService.getInstance().getRpsSetting(instanceUUID);
		RpsHost rpsHost = null;
		if(setting != null){
			rpsHost = setting.getRpsHost();
			rpsPolicyUUID = setting.getRPSPolicyUUID();
			rpsDataStoreUUID = setting.getRPSDataStore();
		}
		try {
			if(this.checkRPS4Job(runNow, rpsHost != null, rpsHost, rpsDataStoreUUID, context, 
					getDefaultJobType(), VSphereService.getInstance().getNativeFacade())){
				CatalogJobArg jobArg = new CatalogJobArg();
				this.getJobArg((JobDetailImpl)context.getJobDetail(), rpsPolicyUUID, 
						setting.getRPSDataStore(), getDefaultJobType(), jobArg, 
						instanceUUID, vmName, esxName, dataStoreName);
				jobArg.setQueueType(queueType);
				jobArg.setDestination(destination);
				jobArg.setUserName(userName);
				jobArg.setPassword(password);		
				jobArg.setJobDependencies(new String[]{IJobDependency.VSPHERE_CATALOG_JOB});
				releaseJobLock(instanceUUID);
				logger.debug("Submit job to rps");
				JobService.getInstance().submitCatalog(jobArg, rpsHost);
				return true;
			}
		}catch(Exception se) {
			releaseJobLock(instanceUUID);
			return true;
		}
		
		return false;
		
		}
		
		public boolean isJobRunningFromJobMonitor(String instanceUUID, long jobType) {
			
			if( BaseVSphereJob.isJobRunning(instanceUUID,String.valueOf(Constants.AF_JOBTYPE_VM_CATALOG_FS))					
					|| BaseVSphereJob.isJobRunning(instanceUUID,String.valueOf(Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND)))
			{	
				logger.info("Another job is running!");
				return true;
			}	
			else
				return false;
			
		}
	
}

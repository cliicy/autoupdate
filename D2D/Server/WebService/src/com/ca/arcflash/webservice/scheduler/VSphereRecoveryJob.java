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
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.job.rps.BaseJobArgWithSourceInfo;
import com.ca.arcflash.webservice.data.job.rps.RestoreJobArg;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.RestoreService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.internal.RestoreJobConverter;
import com.ca.arcflash.webservice.service.internal.VSphereJobContext;
import com.ca.arcflash.webservice.service.internal.VSphereRestartRestoreJobTask;
import com.ca.arcflash.webservice.service.internal.VSphereRestoreJobQueue;
import com.ca.arcflash.webservice.service.rps.JobService;

public class VSphereRecoveryJob extends BaseVSphereJob{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(VSphereRecoveryJob.class);

	private RestoreJobConverter restoreJobConverter = new RestoreJobConverter();

	private com.ca.arcflash.webservice.data.restore.RestoreJob restoreJob;
	
	@Override
	protected boolean resumeMonitorAfterRestart(VSphereJobContext context,
			Observer[] observers, int jobType) {
		try {
			VSphereRestartRestoreJobTask restartJobRunnable = new VSphereRestartRestoreJobTask(context, this);
			VSphereRestoreJobQueue.getInstance().addJobToWaitingQueue(restartJobRunnable);

			shrmemid = context.getJobId();
			logger.info("resume jobid(shrmemid):" + shrmemid);
			if (observers != null) {
				for (Observer observer : observers) {
					addObserver(observer);
				}
			}
			resumeSerialization();
			pauseMergeJob(jobType);
			vmpool.submit(new VSphereJobMonitorThread(this, context));
		} catch (RuntimeException e) {
			
			throw e;
		}

		logger.debug("resumeMonitorAfterRestart(JJobScript) - end");
		return true;
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug("execute(JobExecutionContext) - start");
		Date date1 = context.getFireTime();
		JobDetailImpl jobDetail = (JobDetailImpl)context.getJobDetail();
		restoreJob = (com.ca.arcflash.webservice.data.restore.RestoreJob)context.getJobDetail().getJobDataMap().get("Job");
		rpsPolicyUUID = jobDetail.getJobDataMap().getString(BaseService.RPS_POLICY_UUID);
		rpsDataStoreUUID = jobDetail.getJobDataMap().getString(BaseService.RPS_DATASTORE_UUID);
		rpsDataStoreName = jobDetail.getJobDataMap().getString(BaseService.RPS_DATASTORE_DISPLAY_NAME);
		rpsHost = (RpsHost) jobDetail.getJobDataMap().get(BaseService.RPS_HOST);
		Boolean runNow = jobDetail.getJobDataMap().getBoolean(Constants.RUN_NOW);
		isOnDemand = true;
		Object jobID = jobDetail.getJobDataMap().get(VSphereService.JOB_ID);
		if(jobID != null)
			this.shrmemid = (Long)jobID;
		
		NativeFacade nativeFacade1 = RestoreService.getInstance().getNativeFacade();
		if (BaseVSphereJob.isJobRunning(restoreJob.getRecoverVMOption().getVmInstanceUUID(),String.valueOf(Constants.AF_JOBTYPE_VM_RECOVERY))){
			//Because one job is running, a new job named "%s" at "%s" cannot be arranged.
			String name = BackupConverterUtil.backupIndicatorToName(jobDetail.getName());
			String date = BackupConverterUtil.dateToString(date1);
			//fix issue 18712980
			nativeFacade1.addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_JOB_SKIPPED,new String[]{name,date},restoreJob.getRecoverVMOption().getVmInstanceUUID());
			
			logger.debug("Other jobs are running, exist");
			return;
		}
		
		NativeFacade nativeFacade = (NativeFacade)context.getJobDetail().getJobDataMap().get("NativeFacade");
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(restoreJob.getRecoverVMOption().getVmInstanceUUID());
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
		jobContext.setExecuterInstanceUUID(restoreJob.getRecoverVMOption().getVmInstanceUUID());
		jobContext.setJobType(Constants.AF_JOBTYPE_VM_RECOVERY);
		jobContext.setJobLauncher((int) restoreJob.getJobLauncher());
		//if it's vApp child VM, we do not show its job monitor in the vApp home page
		if(restoreJob.getMasterJobId() > 0) {
			jobContext.setLauncherInstanceUUID(restoreJob.getRecoverVMOption().getVmInstanceUUID());
		} else {
			jobContext.setLauncherInstanceUUID(restoreJob.getVmInstanceUUID());
		}
		if(conf != null && conf.getBackupVM() != null) {
			jobContext.setVmName(conf.getBackupVM().getVmName());
		}
		this.setJobContext(jobContext);
		
		if(this.shrmemid <= 0)
			initShrmemid();
		try {
			if(checkRPS4Job(runNow, rpsHost != null, rpsHost, 
				rpsDataStoreUUID, context, 
				getDefaultJobType(), nativeFacade1)){
				//submit job to rps
				RestoreJobArg jobArg = new RestoreJobArg();					
				getJobArgWithSrc(jobDetail, rpsPolicyUUID,
						rpsDataStoreUUID, getDefaultJobType(), rpsHost,jobArg,
						restoreJob.getJobLauncher(), restoreJob.getRecoverVMOption().getVmInstanceUUID(),
						rpsDataStoreName, conf);
				jobArg.setJobScript(restoreJob);
				JobService.getInstance().submitRestoreJob(jobArg, rpsHost);
				return;	
			}	
		} catch (ServiceException se) {
			return;
		}
		
		//CommonService.getInstance().addVMJobMonitorMap(restoreJob.getRecoverVMOption().getVmInstanceUUID(),String.valueOf(Constants.AF_JOBTYPE_VM_RECOVERY));
		JJobScript targetJob;
		try {			
			targetJob = restoreJobConverter.convert2JobScript(restoreJob, ServiceContext.getInstance().getLocalMachineName());
			if(!preprocess(targetJob,null))
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
	
	private BackupRPSDestSetting getBackupRpsSettings(long jobLauncher, String vmInstanceUUID){
		if(jobLauncher == VSphereJobContext.JOB_LAUNCHER_D2D){
			return BackupService.getInstance().getRpsSetting();
		}else {
			return VSphereService.getInstance().getRpsSetting(vmInstanceUUID);
		}
	}
	
	private void getJobArgWithSrc(JobDetail jobDetail, String policyUUID,
			String datastoreUUID, long jobType, RpsHost rpsHost,
			BaseJobArgWithSourceInfo jobArg, long jobLauncher, String vmInstanceUUID,
			String dataStoreName, VMBackupConfiguration conf) {
		if(jobLauncher == VSphereJobContext.JOB_LAUNCHER_D2D){
			super.getJobArgWithSrc(jobDetail, policyUUID, datastoreUUID, jobType, rpsHost,
				jobArg, CommonService.getInstance().getNodeUUID(), 
				ServiceContext.getInstance().getLocalMachineName(), null, dataStoreName);
			jobArg.setVM(false);
		} else {
			if(conf != null && conf.getBackupVM() != null)
				super.getJobArgWithSrc(jobDetail, policyUUID, datastoreUUID, jobType, rpsHost,
					jobArg, vmInstanceUUID, conf.getBackupVM().getVmName(), 
					conf.getBackupVM().getEsxServerName(), dataStoreName);
		}
	}



	@Override
	protected long getDefaultJobType() {
		return JobType.JOBTYPE_VM_RECOVERY;
	}
	
	public String getDestination(){
//		return destination;
		logger.info("Inside vsphererecovery job, calling getDestination: " + restoreJob.getSessionPath());
		return restoreJob.getSessionPath();
	}
}

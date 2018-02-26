package com.ca.arcflash.webservice.scheduler;

import java.net.UnknownHostException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.JobDetailImpl;

import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.job.rps.RestoreJobArg;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.RestoreService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.internal.RestoreJobConverter;
import com.ca.arcflash.webservice.service.internal.VSphereJobContext;
import com.ca.arcflash.webservice.service.internal.VSphereRestoreJobQueue;
import com.ca.arcflash.webservice.service.internal.VSphereRestoreJobTask;
import com.ca.arcflash.webservice.service.rps.JobService;

public class VSphereRestoreJob extends BaseVSphereJob{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(VSphereRestoreJob.class);
	private RestoreJobConverter restoreJobConverter = new RestoreJobConverter();
	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		VSphereRestoreJobTask task = new VSphereRestoreJobTask(this, context);
		//Commented for issue fix: 753582
		/*if (task.getVirtualMachine() != null &&
			task.getVMInstanceUUID() != null &&	
			VSphereService.getInstance().isPlanDisabled(task.getVMInstanceUUID())) {
			logger.info("Do not execute the VM restore job since the plan is paused.");
			return;
		}*/
		VSphereRestoreJobQueue.getInstance().addJobToWaitingQueue(task);
	}
	
	public void executeRestore(JobExecutionContext context) throws JobExecutionException {
		Date date1 = context.getFireTime();
		JobDetailImpl jobDetail = (JobDetailImpl)context.getJobDetail();
		rpsPolicyUUID = jobDetail.getJobDataMap().getString(BaseService.RPS_POLICY_UUID);
		rpsDataStoreUUID = jobDetail.getJobDataMap().getString(BaseService.RPS_DATASTORE_UUID);
		rpsDataStoreName = jobDetail.getJobDataMap().getString(BaseService.RPS_DATASTORE_DISPLAY_NAME);
		rpsHost = (RpsHost) jobDetail.getJobDataMap().get(BaseService.RPS_HOST);
		Boolean runNow = jobDetail.getJobDataMap().getBoolean(Constants.RUN_NOW);
		isOnDemand = true;
		Object jobID = jobDetail.getJobDataMap().get(VSphereService.JOB_ID);
		if(jobID != null)
			this.shrmemid = (Long)jobID;
		
		com.ca.arcflash.webservice.data.restore.RestoreJob job = (com.ca.arcflash.webservice.data.restore.RestoreJob)context.getJobDetail().getJobDataMap().get("Job");
		NativeFacade nativeFacade1 = RestoreService.getInstance().getNativeFacade();
		if (BaseVSphereJob.isJobRunning(job.getVmInstanceUUID(),String.valueOf(Constants.AF_JOBTYPE_RESTORE))){
			//Because one job is running, a new job named "%s" at "%s" cannot be arranged.
			String name = BackupConverterUtil.backupIndicatorToName(jobDetail.getName());
			String date = BackupConverterUtil.dateToString(date1);
			//fix issue 18712980
			nativeFacade1.addLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_JOB_SKIPPED,new String[]{name,date});
			
			logger.debug("Other jobs are running, exist");
			return;
		}
		
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(job.getVmInstanceUUID());
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
		NativeFacade nativeFacade = (NativeFacade)context.getJobDetail().getJobDataMap().get("NativeFacade");
		VSphereJobContext jobContext = new VSphereJobContext();
		jobContext.setExecuterInstanceUUID(job.getVmInstanceUUID());
		jobContext.setJobType(Constants.AF_JOBTYPE_RESTORE);
		jobContext.setJobLauncher(VSphereJobContext.JOB_LAUNCHER_VSPHERE);
		jobContext.setLauncherInstanceUUID(job.getVmInstanceUUID());
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
				String vmName = null;
				String esxName = null;
				if(conf != null && conf.getBackupVM() != null){
					vmName = conf.getBackupVM().getVmName();
					esxName = conf.getBackupVM().getEsxServerName();
				}
					
				getJobArgWithSrc(jobDetail, rpsPolicyUUID,
						rpsDataStoreUUID, getDefaultJobType(), rpsHost,jobArg,
						job.getVmInstanceUUID(), vmName,
						esxName, rpsDataStoreName);
				jobArg.setJobScript(job);
				JobService.getInstance().submitRestoreJob(jobArg, rpsHost);
				return;	
			}	
		} catch (ServiceException se) {
			return;
		}
		//CommonService.getInstance().addVMJobMonitorMap(job.getVmInstanceUUID(),String.valueOf(Constants.AF_JOBTYPE_RESTORE));
		JJobScript targetJob;
		try {			
			targetJob = restoreJobConverter.convert2JobScript(job, ServiceContext.getInstance().getLocalMachineName());
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
	}
	
	public long getJobPhase() {
		return jobPhase;
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

	@Override
	protected long getDefaultJobType() {
		return JobType.JOBTYPE_RESTORE;
	}
}

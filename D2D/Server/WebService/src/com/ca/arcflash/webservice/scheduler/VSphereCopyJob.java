package com.ca.arcflash.webservice.scheduler;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.JobDetailImpl;

import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.job.rps.CopyJobArg;
import com.ca.arcflash.webservice.data.restore.RecoveryPointItem;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.RestoreService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VMCopyService;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.internal.RecoveryPointConverter;
import com.ca.arcflash.webservice.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.webservice.service.internal.VSphereJobContext;
import com.ca.arcflash.webservice.service.rps.JobService;

public class VSphereCopyJob extends BaseVSphereJob implements Job{
	
	private static final Logger logger = Logger.getLogger(VSphereCopyJob.class);
	private RecoveryPointConverter recoveryPointConverter = new RecoveryPointConverter();
	private com.ca.arcflash.webservice.data.restore.CopyJob job;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug("execute(JobExecutionContext) - start");
		JobDetailImpl jobDetail = (JobDetailImpl)context.getJobDetail();
		Date date1 = context.getFireTime();
		rpsPolicyUUID = jobDetail.getJobDataMap().getString(BaseService.RPS_POLICY_UUID);
		rpsDataStoreUUID = jobDetail.getJobDataMap().getString(BaseService.RPS_DATASTORE_UUID);
		rpsDataStoreName = jobDetail.getJobDataMap().getString(BaseService.RPS_DATASTORE_DISPLAY_NAME);
		rpsHost = (RpsHost) jobDetail.getJobDataMap().get(BaseService.RPS_HOST);
		Boolean runNow = jobDetail.getJobDataMap().getBoolean(Constants.RUN_NOW);
		isOnDemand = jobDetail.getJobDataMap().getBoolean(BaseService.ON_DEMAND_JOB);
		Object jobID = jobDetail.getJobDataMap().get(VSphereService.JOB_ID);
		if(jobID != null)
			this.shrmemid = (Long)jobID;
		
		NativeFacade nativeFacade2 = RestoreService.getInstance().getNativeFacade();
		job = (com.ca.arcflash.webservice.data.restore.CopyJob)context.getJobDetail().getJobDataMap().get("Job");
		if (BaseVSphereJob.isJobRunning(job.getVmInstanceUUID(),String.valueOf(Constants.AF_JOBTYPE_COPY))){
			//fix issue 18712980
			String name = BackupConverterUtil.backupIndicatorToName(jobDetail.getName());
			String date = BackupConverterUtil.dateToString(date1);
			nativeFacade2.addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_JOB_SKIPPED,new String[]{name,date},job.getVmInstanceUUID());
			logger.debug("Other jobs are running, exist");
			return;
		}
		
		if(rpsHost!=null)
			job.setRpsHostname(rpsHost.getRhostname());
		
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(job.getVmInstanceUUID());
		String vmName = null;
		VMBackupConfiguration configuration = null;
		try{
			configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if (isConfigurationForRemoteNode(configuration)) {
				logger.info("The configuration is for remote nodes.");
				return;
			}
			vmName = configuration.getBackupVM().getVmName();
		    int isReachable = VSphereService.getInstance().isServerReachable(configuration);
			if(isReachable != 0){
				nativeFacade2.addVMLogActivity(Constants.AFRES_AFALOG_ERROR,Constants.AFRES_AFJWBS_JOB_VSPHERE_HOST_NOT_FOUND,
						new String[]{jobDetail.getName(), BackupConverterUtil.dateToString(date1),configuration.getBackupVM().getEsxServerName(),"",""},vm.getVmInstanceUUID());
				return;
			}
			
		}catch(Exception e){
			logger.debug(e);
			return;
		}	
		
		VSphereJobContext jobContext = new VSphereJobContext();
		jobContext.setExecuterInstanceUUID(job.getVmInstanceUUID());
		jobContext.setJobType(Constants.AF_JOBTYPE_COPY);
		jobContext.setJobLauncher(VSphereJobContext.JOB_LAUNCHER_VSPHERE);
		jobContext.setLauncherInstanceUUID(job.getVmInstanceUUID());
		jobContext.setVmName(vmName);
		this.setJobContext(jobContext);
		
		//check whether continue job
		if(IsContinueJob() == false) {
			logger.debug("Exit this job since it cannot continue!!");
			return;
		}
		
		int sessimNumber = job.getSessionNumber();
		try {
			String domain = "";
			String pwd = "";
			String fullUserName = job.getUserName();
			String userName = fullUserName;
			if (fullUserName != null) {
				int index = fullUserName.indexOf('\\');
				if (index > 0) {
					domain = fullUserName.substring(0, index);
					userName = fullUserName.substring(index + 1);
				}
				pwd = job.getPassword();
			}
			String rootPath = job.getSessionPath();
			
			Lock lock = null;
			String fullSessionPath = null;
			RecoveryPointItem[] rpItems = null;
			try {
				lock = RemoteFolderConnCache.getInstance().getLockByPath(rootPath);
				if(lock != null) {
					lock.lock();
				}
				nativeFacade2.NetConn(userName, pwd, rootPath);
				fullSessionPath = WSJNI.getSessPathByNo(rootPath, sessimNumber);
				
				if (rootPath.contains("/")) {
					rootPath = rootPath.replaceAll("/", "\\\\");
				}
				if (rootPath.endsWith("\\")) {
					rootPath = rootPath.substring(0, rootPath.length() - 1);
				}
				if (fullSessionPath.contains("/")) {
					fullSessionPath = fullSessionPath.replaceAll("/", "\\\\");
				}
				if (fullSessionPath.endsWith("\\")) {
					fullSessionPath = fullSessionPath.substring(0, fullSessionPath.length() - 1);
				}
				String subPath = fullSessionPath.substring(rootPath.length() + "\\VStore\\".length());
				rpItems = RestoreService.getInstance().getRecoveryPointItems(rootPath, domain, userName, pwd, subPath);
			} finally {
				if(lock != null){
					lock.unlock();
				}
				
				try {
					nativeFacade2.disconnectRemotePath(fullSessionPath, "", userName, pwd, false);
				} catch (Exception e){
					logger.warn("Disconnect " + fullSessionPath + " failed");
				}
			}
			
			boolean isHasVolumn = false;
			if (rpItems != null && rpItems.length > 0) {
				for (RecoveryPointItem item : rpItems) {
					if ("Volume".equals(item.getVolumeOrAppType())) {
						isHasVolumn = true;
						break;
					}
				}
			} 
			
			/*if (!isHasVolumn ) {
				nativeFacade2.addVMLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_VSPHERE_FAILED_COPY_JOB_NO_VOLUMN,
						new String[]{vmName, sessimNumber + "", "", "", ""}, vm.getVmInstanceUUID());
				return;
			}*/
		} catch (Exception e1) {
			logger.warn("Failed to check volumn information");
			nativeFacade2.addVMLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_VSPHERE_FAILED_COPY_JOB_NO_VOLUMN,
					new String[]{vmName, sessimNumber + "", "", "", ""}, vm.getVmInstanceUUID());
			return;
		}
		
		/*if(rpsHost == null){
			BackupRPSDestSetting setting = VSphereService.getInstance().getRpsSetting(job.getVmInstanceUUID());
			if(setting != null){
				rpsHost = setting.getRpsHost();
				rpsPolicyUUID = setting.getRPSPolicyUUID();
				rpsDataStoreUUID = setting.getRPSDataStore();
			}
		}*/
		if(this.shrmemid <= 0)
			initShrmemid();
		try {
			BackupVM backupVM = configuration.getBackupVM();
			if(checkRPS4Job(runNow, rpsHost != null, rpsHost, 
				rpsDataStoreUUID, context, 
				JobType.JOBTYPE_COPY, nativeFacade2)){
				//submit job to rps
				CopyJobArg jobArg = new CopyJobArg();
				String esxName = null;
				
				if(backupVM != null){					
					esxName = backupVM.getEsxServerName();
				}
				getJobArgWithSrc(jobDetail, rpsPolicyUUID,
						rpsDataStoreUUID, JobType.JOBTYPE_COPY, rpsHost,jobArg,
						job.getVmInstanceUUID(), vmName,
						esxName, rpsDataStoreName);
				jobArg.setJobScript(job);
				JobService.getInstance().submitCopyJob(jobArg, rpsHost);
				return;	
			}	
		} catch (ServiceException se) {
			//TODO send email
			return;
		}
		
		NativeFacade nativeFacade = (NativeFacade)context.getJobDetail().getJobDataMap().get("NativeFacade");
		//CommonService.getInstance().addVMJobMonitorMap(job.getVmInstanceUUID(),String.valueOf(Constants.AF_JOBTYPE_COPY));
		JJobScript targetJob;
		try {			
			targetJob = recoveryPointConverter.convert2JobScript(job, ServiceContext.getInstance().getLocalMachineName());
			js = targetJob;
			if(!preprocess(targetJob,null))
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
		VMCopyService.getInstance().resetSubmitCopy();
		logger.debug("execute(JobExecutionContext) - end");
	}

	@Override
	protected long getDefaultJobType() {
		return JobType.JOBTYPE_COPY;
	}
	public String getDestination(){
		logger.info("Inside vspherecopy job, calling getDestination: " + job.getDestinationPath());
		return job.getDestinationPath();
	}
}

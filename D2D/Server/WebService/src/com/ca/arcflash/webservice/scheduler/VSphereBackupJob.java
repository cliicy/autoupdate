package com.ca.arcflash.webservice.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observer;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jni.common.JJobHistory;
import com.ca.arcflash.service.data.PeriodRetentionValue;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.FlashServiceImpl;
import com.ca.arcflash.webservice.common.LicenseCheckException;
import com.ca.arcflash.webservice.common.LicenseCheckManager;
import com.ca.arcflash.webservice.common.VSphereLicenseCheck;
import com.ca.arcflash.webservice.common.VSphereLicenseCheck.HyperVisorInfo;
import com.ca.arcflash.webservice.common.VSphereLicenseCheck.LicensePrepareThread;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.NextScheduleEvent;
import com.ca.arcflash.webservice.data.RPSDataStoreInfo;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.backup.RetryPolicy;
import com.ca.arcflash.webservice.data.backup.RpsPolicy4D2D;
import com.ca.arcflash.webservice.data.job.rps.BackupJobArg;
import com.ca.arcflash.webservice.data.job.rps.IJobDependency;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.edge.license.LICENSEDSTATUS;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.jni.model.JJobScriptRestoreVolApp;
import com.ca.arcflash.webservice.jni.model.JJobScriptVSphereNode;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VMCopyService;
import com.ca.arcflash.webservice.service.VSPhereCatalogService;
import com.ca.arcflash.webservice.service.VSphereBackupSetService;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.internal.BackupJobConverter;
import com.ca.arcflash.webservice.service.internal.VSphereBackupJobTask;
import com.ca.arcflash.webservice.service.internal.VSphereJobContext;
import com.ca.arcflash.webservice.service.internal.VSphereJobQueue;
import com.ca.arcflash.webservice.service.internal.VSphereRestartBackupJobTask;
import com.ca.arcflash.webservice.service.rps.JobService;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class VSphereBackupJob extends BaseVSphereJob implements Job{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(VSphereBackupJob.class);
	protected static BackupJobConverter jobConverter = new BackupJobConverter();

	private volatile static boolean processingDestChanged;
	private volatile boolean ownerOfDestChanged;
	
	static final Observer[] vmObservers = new Observer[]{VSPhereCatalogService.getInstance(), HAService.getInstance(), VMCopyService.getInstance()};
	
	// Creates a map for each VM using vm UUID as its key.
	// It only shared among different Backup jobs, so we define in this class rather that it's parent base class.
	private final static Map<String, JobExecutionContext> vmMissedBackupJobContext = new HashMap<String, JobExecutionContext>();
	
	private static final Set<String> runningJobs = new HashSet<String>();

	protected boolean isDaily = false;
	protected boolean isWeekly = false;
	protected boolean isMonthly = false;
	
	// move it to Base class
	//protected int scheduletype=0;//ADD
	
	protected final static int missedJob = 0;
	protected final static int skippedJob = 1;
	
	@Deprecated protected static JobExecutionContext currentJob = null;
	protected static Object currentJobLock = new Object();
	
	// use this to replace currentJob, otherwise currentJob will be shared by different VMs
	private static Map<String, JobExecutionContext> currentJobMap = new HashMap<String, JobExecutionContext>();
	
	private IComputeMissedJob calMissedJob = CalMissedJob.getInstance();

	private static NativeFacade nativeFacade = VSphereService.getInstance().getNativeFacade();
	/**
	 * For backup job, we need to check whether job is done with phase 0xE. 
	 */
	
	@Override
	protected boolean isJobDone(long jobPhase, long jobStatus) {		
		if(jobPhase == Constants.BackupJob_Phase_PROC_EXIT) {
			return true;
		}else {
			return false;
		}
	}
	
	public JJobScript generateVSphereBackupJobScript(VMBackupConfiguration configuration,int backupType,
			VirtualMachine vm, RpsPolicy4D2D rpsPolicy, boolean isRPSCatalogEnable){
		try {
			return jobConverter.convert(configuration, backupType, 
					ServiceContext.getInstance().getLocalMachineName(),vm, isDaily, isWeekly, isMonthly, rpsPolicy, isRPSCatalogEnable);
		} catch (Exception e) {
			logger.error("Generate backup job script exception", e);
		}
		
		return null;
	}

	@Override
	protected boolean resumeMonitorAfterRestart(VSphereJobContext context, Observer[] observers, int jobType) {
		try {
			VSphereRestartBackupJobTask restartJobRunnable = new VSphereRestartBackupJobTask(context, this);
			VSphereJobQueue.getInstance().addJobToWaitingQueue(restartJobRunnable);

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
			releaseJobLock(context.getExecuterInstanceUUID());
			throw e;
		}

		logger.debug("resumeMonitorAfterRestart(JJobScript) - end");
		return true;
	}
	
	public long getJobPhase(){
		return jobPhase;
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		VSphereBackupJobTask task = new VSphereBackupJobTask(this, context);
		boolean ismanual=false;
		if(context.getJobDetail().getJobDataMap().containsKey("manualFlag"))
			ismanual=context.getJobDetail().getJobDataMap().getBoolean("manualFlag");
		if (VSphereService.getInstance().isPlanDisabled(task.getVMInstanceUUID())&&!ismanual) {
			logger.info("Do not execute the VM backup job [instanceUUID=" + task.getVMInstanceUUID() + ", jobName=" + 
					task.getJobName() + ", jobType=" + task.getJobType() + "], since the plan is paused.");
			return;
		}
		VSphereJobQueue.getInstance().addJobToWaitingQueue(task);
	}
	
	private RpsPolicy4D2D checkRPS4Backup(VMBackupConfiguration configuration, 
			String vmInstanceUUID, int jobMethod) {
		String message = null;
		logger.info("Check Rps for backup start");
		try {
			RpsPolicy4D2D policy = VSphereService.getInstance().checkRPS4Backup(configuration);
			rpsDataStoreUUID = policy.getDataStoreName();
			rpsPolicyUUID = policy.getPolicyUUID();
			logger.info("Check Rps for backup end");
			return policy;
		}catch(ServiceException se) {
			logger.error("Check RPS for backup got exception ", se);
			message = se.getMessage();
			if(StringUtil.isEmptyOrNull(message)) {
				message = WebServiceMessages.getResource("backupNotRunPolicy");
			}else {
				message = WebServiceMessages.getResource("backupNotRunRPSError", message);
			}
		}catch(Throwable t) {
			message = WebServiceMessages.getResource("backupNotRunPolicy");
			logger.error("Check RPS for backup got exception ", t);
		}
		
		JJobHistory jobHistory = new JJobHistory();
        jobHistory.setJobId(shrmemid);
        jobHistory.setJobType((int)JobType.JOBTYPE_VM_BACKUP );
        jobHistory.setJobMethod(jobMethod);
        jobHistory.setJobStatus(JobStatus.JOBSTATUS_MISSED);
        jobHistory.setJobRunningNodeUUID(vmInstanceUUID);
        jobHistory.setJobDisposeNodeUUID(vmInstanceUUID);
        jobHistory.setJobName( name );
        jobHistory.setDatastoreUUID(configuration.getBackupRpsDestSetting().getRPSDataStore());
        jobHistory.setTargetUUID(getRPSServerID());
        jobHistory.setPlanUUID(isOnDemand ? "" : getPlanUUID(vmInstanceUUID));
        
		long version = VSphereService.getInstance().getRPSDatastoreVersion(jobHistory.getDatastoreUUID(), vmInstanceUUID);
		jobHistory.setDatastoreVersion(version);
		
		nativeFacade.addMissedJobHistory(jobHistory );
		
		if(!StringUtil.isEmptyOrNull(message)){
			nativeFacade.addVMLogActivityWithJobID(Constants.AFRES_AFALOG_ERROR,
					shrmemid,
					Constants.AFRES_AFJWBS_GENERAL, 
					new String[]{message, "","","",""}, vmInstanceUUID);
		}
		return null;
	}
	
	private boolean isBackupJobMissed(JobExecutionContext context) throws ServiceException {
		JobDetailImpl jobDetail = (JobDetailImpl)context.getJobDetail();
		VirtualMachine vm = (VirtualMachine)jobDetail.getJobDataMap().get("vm");
		String instanceUUID = vm.getVmInstanceUUID();
		logger.info("Job ID : " + shrmemid + ", Job Name : " + jobDetail.getName() + ", Job Priority : " + context.getTrigger().getPriority());
		synchronized (this.getClass()) {
			if (!getJobLock(instanceUUID)) {
				name = jobDetail.getName();
				if (name != null && name.endsWith(Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME)) {
					name = jobDetail.getJobDataMap().getString("jobName");
				} else {
					name = BackupConverterUtil.backupIndicatorToName(jobDetail.getName());
				}
				String date = BackupConverterUtil.dateToString(context.getFireTime());
				String msg = WebServiceMessages
						.getResource("skippedJobInformation", new Object[] { WebServiceMessages.getResource(Constants.regular), name, date });

				if (jobDetail.getName().contains("daily")) {
					msg = WebServiceMessages.getResource("skippedJobInformation", new Object[] { WebServiceMessages.getResource(Constants.daily), name, date });
				}
				if (jobDetail.getName().contains("weekly")) {
					msg = WebServiceMessages.getResource("skippedJobInformation", new Object[] { WebServiceMessages.getResource(Constants.weekly), name, date });
				}
				if (jobDetail.getName().contains("monthly")) {
					msg = WebServiceMessages.getResource("skippedJobInformation", new Object[] { WebServiceMessages.getResource(Constants.monthly), name, date });
				}
	
				nativeFacade.addVMLogActivityWithJobID(
								Constants.AFRES_AFALOG_WARNING,
								shrmemid,
								Constants.AFRES_AFJWBS_GENERAL,
								new String[] {
										msg,"","","",""}, vm.getVmInstanceUUID());

				logger.info("Job is missed due to another job for this vm is Running, missed: "
						+ jobDetail.getName()
						+ ", running vm instance uuid:"
						+ instanceUUID);

				synchronized (missVMJobLock) {
					calMissedJob.initJobContext(vmMissedBackupJobContext.get(instanceUUID), currentJobMap.get(instanceUUID));
					vmMissedBackupJobContext.put(instanceUUID, calMissedJob.computeForHBBU(context));
				}
				
				try {
					logger.info("Other jobs are running, exit, last remembered missed job : "
								+ (vmMissedBackupJobContext == null ? "null"
								: ((JobDetailImpl)vmMissedBackupJobContext.get(vm.getVmInstanceUUID()).getJobDetail()).getFullName()));
				} catch (Exception e) {
					
				}
				return true;
			} else {
				synchronized(currentJobLock) {
					currentJobMap.put(vm.getVmInstanceUUID(), context);
				}
				logger.info("current job:" + ((JobDetailImpl)currentJobMap.get(vm.getVmInstanceUUID()).getJobDetail()).getFullName());
			}
		}
		return false;
	}
	
	public void executeBackupJob(JobExecutionContext context) throws JobExecutionException {
		logger.info("execute(JobExecutionContext) - start");
		boolean isWMwareVM = false;
		Date date1 = context.getFireTime();
		JobDetailImpl jobDetail = (JobDetailImpl)context.getJobDetail();
		Integer jobType = jobDetail.getJobDataMap().getInt("jobType");
		Object jobID = jobDetail.getJobDataMap().get(VSphereService.JOB_ID);
		if(jobID != null)
			this.shrmemid = (Long)jobID;
		VirtualMachine vm = (VirtualMachine)jobDetail.getJobDataMap().get("vm");
		String jobName = jobDetail.getJobDataMap().getString("jobName");
		String instanceUUID = vm.getVmInstanceUUID();
		String generatedDestinationPath = jobDetail.getJobDataMap().getString("generatedDestinationPath");
		boolean toRPSJobQueue = false;//whether this request will goto RPS job queue
		VMBackupConfiguration configuration;
		boolean bMissed = false;
		this.scheduletype=this.getScheduleType(jobDetail);
		
		String log = String.format("executing job: name %s, inst uuid %s, vm name %s, scheduletype %d, job detail name %s ",
						jobName, instanceUUID, vm.getVmName(), scheduletype, jobDetail.getName());
		logger.info(log);
		// construct it firstly for retry job
		VSphereJobContext jobContext = new VSphereJobContext();
		jobContext.setExecuterInstanceUUID(vm.getVmInstanceUUID());
		try{
			if(this.shrmemid <= 0)
				initShrmemid();
			
			jobDetail.getJobDataMap().put(VSphereService.JOB_ID, this.shrmemid); // add the jobID, refer to the places that get this value
			
			configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if (isConfigurationForRemoteNode(configuration)) {
				logger.info("The job script is for remote nodes.");
				return;
			}
			
			//check free space of the volume where agent is installed, and print warning message in activity log if free space is less than a threshold value (default is 1024 MB) 
			VSphereService.getInstance().agentInstalledVolumeFreeSpaceCheck(configuration);
			
			// Prepare license info is slow, try to do it concurrently, and start the thread as early as we can
			logger.info("Start to prepare the license check info");
			LicensePrepareThread licensePrepareThread = VSphereLicenseCheck.getInstance().startPrepare(configuration);
			
			bMissed = isBackupJobMissed(context); 
			if(bMissed) {
				sendEmailOnMissedJob(context,vm, jobType);
				return;
			}
			
			//check whether continue job
			if(IsContinueJob() == false) {
				logger.info("Exit this job since it cannot continue!!");
				return;
			}
			
			//fix issue 18712980
			name = jobDetail.getName();
			if(name != null && name.endsWith(Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME)){
				name = jobDetail.getJobDataMap().getString("jobName");
			}else{
				name = BackupConverterUtil.backupIndicatorToName(jobDetail.getName());
			}
			
			if(jobDetail.getJobDataMap().containsKey("isDaily")){
				isDaily =  jobDetail.getJobDataMap().getBoolean("isDaily");		
				logger.info("isDaily:" + isDaily);
			}
			if(jobDetail.getJobDataMap().containsKey("isWeekly")){
				isWeekly =  jobDetail.getJobDataMap().getBoolean("isWeekly");
				logger.info("isWeekly:" + isWeekly);
			}
			if(jobDetail.getJobDataMap().containsKey("isMonthly")){
				isMonthly =  jobDetail.getJobDataMap().getBoolean("isMonthly");
				logger.info("isMonthly:" + isMonthly);
			}
			
			//if it's launched for the vApp child VM, the backup destination is generated from back-end
			if(!StringUtil.isEmptyOrNull(generatedDestinationPath)) {
				configuration.getBackupVM().setDestination(generatedDestinationPath);
			}
			
			RpsPolicy4D2D rpsPolicy = null;
			if(!configuration.isD2dOrRPSDestType()) {
				rpsHost = configuration.getBackupRpsDestSetting().getRpsHost();
				//check whether rps policy exist
				rpsPolicy = checkRPS4Backup(configuration, instanceUUID, jobType);
				if(rpsPolicy == null){
					sendEmailOnMissedJob(context,vm, jobType);
					releaseJobLock(instanceUUID);
					return;
				}
			}

			//Check whether need to schedule a job for DST skipped daily backup
			VSphereService.getInstance().makeupForDSTSkippedDaily(date1, instanceUUID);
			
			
			
			if(jobDetail.getJobDataMap().containsKey("periodRetentionFlag")){
				int periodRetentionFlag = jobDetail.getJobDataMap().getInt("periodRetentionFlag");					
				isDaily  = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Daily) > 0;
				isWeekly  = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Weekly) > 0;
				isMonthly  = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Monthly) > 0;
				
				logger.info("periodRetentionFlag:" + periodRetentionFlag);
			}		
			
//			// prepare license check info (fix TFS bug 750596, move this block before submit to RPS. to avoid the this block use more than 3 minutes and cause RPS think the job is end)
//			logger.info("Prepare HyperVisor info for license check begin");
//			long errorCode = 0;
//			String[] errorParameters = null;
//			String hyperVServerName = null;
//			HyperVisorInfo hyperVisorInfo = null;
//			try {
//				if (configuration.getBackupVM().getVmType() == BackupVM.Type.VMware.ordinal() ||
//					configuration.getBackupVM().getVmType() == BackupVM.Type.VMware_VApp.ordinal())
//					hyperVServerName = VSphereLicenseCheck.getNormalizedESXHostName(configuration);
//				else{
//					hyperVServerName = WSJNI.AFGetHyperVPhysicalName(configuration.getBackupVM().getEsxServerName(), configuration.getBackupVM().getEsxUsername(), 
//							configuration.getBackupVM().getEsxPassword(), configuration.getBackupVM().getInstanceUUID());
//					if (hyperVServerName==null || hyperVServerName.isEmpty())
//						throw new ServiceException(FlashServiceErrorCode.Common_General_Message);
//				}
//							
//				hyperVisorInfo = VSphereLicenseCheck.getHyperVisorInfo(configuration, hyperVServerName);
//							
//			} catch (ServiceException e) {
//				String errorLogs = String.format("Failed to get the vm[%s,%s] in the esx host[%s]", 
//						configuration.getBackupVM().getVmName(), instanceUUID, configuration.getBackupVM().getEsxServerName());
//				logger.error(errorLogs);
//				String activeLogs = null;
//							
//				if (configuration.getBackupVM().getVmType() == BackupVM.Type.VMware.ordinal()) {
//					if (e.getErrorCode() == "00000000")
//						activeLogs = String.format(WebServiceMessages.getResource("vsphereBackupJobLicenseFailedSearchVM"), 
//							configuration.getBackupVM().getVmName(), configuration.getBackupVM().getInstanceUUID(), configuration.getBackupVM().getEsxServerName());
//					else
//						activeLogs = String.format(WebServiceMessages.getResource("vsphereBackupJobLicenseFailedConnect"), 
//								configuration.getBackupVM().getEsxServerName());
//				}
//				else
//					activeLogs = String.format(WebServiceMessages.getResource("failConnectHyperServerToGetName"), configuration.getBackupVM().getEsxServerName());
//							
//				errorParameters = new String[] { activeLogs,"", "", "", "" };
//				errorCode = Constants.AFRES_AFJWBS_GENERAL;
//			}
//			logger.info("Prepare HyperVisor info for license check end: hyperVServerName = " + hyperVServerName + " errorCode = " + errorCode);
			
			boolean ismanual=false;
			if(context.getJobDetail().getJobDataMap().containsKey("manualFlag"))
				ismanual=context.getJobDetail().getJobDataMap().getBoolean("manualFlag");
			
			if(!configuration.isD2dOrRPSDestType() && isNotBackupNowJob(jobDetail)&& configuration.isDisablePlan()&&!ismanual){
				logger.info("As the plan is disabled, not going to submit job to RPS for the vm: " + configuration.getBackupVM().getInstanceUUID());
				return;
			}
			if(configuration.isD2dOrRPSDestType()&& configuration.isDisablePlan()&&!ismanual){
				logger.info("As the plan is disabled, not going to submit job to Proxy for the vm: " + configuration.getBackupVM().getInstanceUUID());
				return;
			}

			// submit to RPS 
			if(!configuration.isD2dOrRPSDestType() && isNotBackupNowJob(jobDetail)){
				logger.info("Submit backup to RPS server");
				BackupJobArg arg = getBackupJobArg(jobDetail, configuration, this.shrmemid);
				
				// 2015-07-23 fix TFS Bug 416690:[211815] HBBU catalog job doesn't start occasionally when HBBU job queue is smaller than data store job queue
				if (VMRPSJobSubmitter.keepRPSJobInProxyRunningQueue())
				{
					BackupJobArg returnedArg = null;
					try
					{
						VSphereJobQueue.getInstance().addJobToWaitingAtRPS(arg.getD2dServerUUID(), configuration);
						returnedArg = VMRPSJobSubmitter.getInstance().submitBackupToRpsAndWait(arg, configuration.getBackupRpsDestSetting().getRpsHost());	
					}
					catch(Exception e)
					{
						logger.error("Exception happened when the job is at RPS", e);
					}
					finally
					{
						VSphereJobQueue.getInstance().removeJobFromWaitingAtRPS(arg.getD2dServerUUID());
					}
					
					if (returnedArg == null)
					{
						logger.error("Failed to submit backup to RPS and wait till the job is submitted back.");
						return;
					}
					
					// get some info from the returned Arg (refer to the old code in VSphereService.backupNow)
					updateJobDetailByJobArg(jobDetail, returnedArg, false);	
					
					// RPS may return a different job for the VM, replace some info to the current thread (refer to the executeBackupJob)
					// if (returnedArg.getJobId() != shrmemid)
					{
						logger.info("RPS returned a job, replace setting and continue " + VMRPSJobSubmitter.arg2String(returnedArg));
						
						// job id
						this.shrmemid = returnedArg.getJobId();
						
						// job type
						jobType = jobDetail.getJobDataMap().getInt("jobType");
						
						// job name
						name = jobDetail.getName();
						if (name != null && name.endsWith(Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME))
						{
							name = jobDetail.getJobDataMap().getString("jobName");
						}
						else
						{
							name = BackupConverterUtil.backupIndicatorToName(jobDetail.getName());
						}
						
						// period retention settings
						this.scheduletype=this.getScheduleType(jobDetail);
						
						PeriodRetentionSetting periodRetentionSetting = getPeriodRetentionSetting(jobDetail);
						this.isMonthly = periodRetentionSetting.isMonthly;
						this.isWeekly = periodRetentionSetting.isWeekly;
						this.isDaily = periodRetentionSetting.isDaily;	
					}
				}
				else
				{
					toRPSJobQueue = true;
					JobService.getInstance().submitBackup(arg, configuration.getBackupRpsDestSetting().getRpsHost());
					logger.info("Submit backup to RPS server succeed");
					return;
				}
			}
			
			if(!configuration.isD2dOrRPSDestType()){
				//check whether RPS policy is modified
				configuration = VSphereService.getInstance().rpsPolicyUpdated(rpsPolicy, false, vm.getVmInstanceUUID());
			}
			
			// check license
			long errorCode = 0;
			String[] errorParameters = null;
			HyperVisorInfo hyperVisorInfo = null;
			
			logger.info("wait the result from license prepare thread");
			if (VSphereLicenseCheck.getInstance().waitPrepare(licensePrepareThread))
			{
				errorCode = licensePrepareThread.getErrorCode();
				errorParameters = licensePrepareThread.getErrorParameters();
				hyperVisorInfo = licensePrepareThread.getHyperVisorInfo();
			}			
			
			LICENSEDSTATUS licenseValue = null;
			if (errorCode == 0){
				try {
					licenseValue = LicenseCheckManager.getInstance().checkVSphereBackupLicense(hyperVisorInfo);
				} catch (LicenseCheckException e) {
					logger.error(e.getMessage() + ". Error code: " + e.getErrorCode() );
					if(e.getErrorCode().equals(LicenseCheckException.FAIL_CONNECT_EDGE)){
						errorParameters = new String[]{e.getMessage(), e.getMessage(),"","",""};
						errorCode = Constants.AFRES_AFJWBS_VSPHERE_LICENSE_FAILED_CANNOT_CONNECT;
					}
				}
			}
		
			if (errorCode == 0){
				if(licenseValue ==  LICENSEDSTATUS.EXPIRED){
					String activeLogs = WebServiceMessages.getResource("vsphereBackupJobLicenseExpired");
					errorParameters = new String[] { activeLogs,"", "", "", "" };
					errorCode = Constants.AFRES_AFJWBS_GENERAL;
				}
			}
			
			if (errorCode == 0){
				if(licenseValue != LICENSEDSTATUS.VALID && licenseValue != LICENSEDSTATUS.TRIAL){
					errorParameters = new String[]{jobDetail.getName(), BackupConverterUtil.dateToString(date1),configuration.getBackupVM().getEsxServerName(),"",""};
					errorCode = Constants.AFRES_AFJWBS_JOB_VSPHERE_LICENSE_FAILED;
				}
			}

			if (configuration.getBackupVM().getVmType() == BackupVM.Type.VMware.ordinal()) {
				jobContext.setJobType(Constants.AF_JOBTYPE_VM_BACKUP);
				isWMwareVM = true;
			} else if (configuration.getBackupVM().getVmType() == BackupVM.Type.VMware_VApp.ordinal()){
				jobContext.setJobType(Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP);
				isWMwareVM = true;
			} else if(configuration.getBackupVM().getVmType() == BackupVM.Type.HyperV_Cluster.ordinal()){
				jobContext.setJobType(Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP);
			} else
				jobContext.setJobType(Constants.AF_JOBTYPE_HYPERV_VM_BACKUP);
			
			jobContext.setJobLauncher(VSphereJobContext.JOB_LAUNCHER_VSPHERE);
			jobContext.setLauncherInstanceUUID(vm.getVmInstanceUUID());
			jobContext.setVmName(configuration.getBackupVM().getVmName());
			this.setJobContext(jobContext);
		
			Boolean isRPSCatalogEnable = jobDetail.getJobDataMap().getBoolean(BaseService.RPS_CATALOG_GENERATION);
			
			//Step 1: generate job script
			logger.info("Generate job script");
			
			JJobScript backupJob = generateVSphereBackupJobScript(configuration,jobType,vm, rpsPolicy, isRPSCatalogEnable);
			
			if (!StringUtil.isEmptyOrNull(jobName) && !(isDaily || isWeekly || isMonthly)){
				backupJob.setPwszComments(jobName);
			}
			
			if (errorCode!=0){
				JJobScriptVSphereNode node = backupJob.getpVSphereNodeList().get(0);
				node.setFOptions((int)errorCode);
				node.setNVolumeApp(1);
				JJobScriptRestoreVolApp vol = new JJobScriptRestoreVolApp();
				node.getpRestoreVolumeAppList().add(vol);
				vol.setPwszPath("");
				for (String error:errorParameters){
					vol.setPwszPath(vol.getPwszPath()+"--@@##$$--"+error);
				}
			}
			
			//After the backup destination changed, jobs launched by user manually are not changed
			//to the job type specified in the backup setting dialog.
			if (jobDetail.getName() != null) {
				synchronized (BaseBackupJob.class) {
					if (!processingDestChanged) {
						if (configuration.isChangedBackupDest()) {
							processingDestChanged = true;
							ownerOfDestChanged = true;
						}
					}
				}
			}
			
			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertObject2String(backupJob));
			
			//Step2: execute backup job
			long result = -1;
			//result is 0 meaning successful, -2 means AFBackup result non zero
			try{
				if(!preprocess(backupJob,vmObservers)) {
					logger.debug("Job monitor is running, exist");
					return;
				}
				addBackupSetStartFlag(configuration, jobType, backupJob, vm, jobDetail.getName());
				
				result = BackupService.getInstance().backup(backupJob);
				if (result != 0) {
					stopJobMonitor();
					this.setJobStatus(Constants.JOBSTATUS_FAILED);
					logger.info("backup result:" + result);
					//sonle01: Inform sync Data to Edge
					EdgeDataSynchronization.SetSyncDataFlag();
					return;
				}
				
                //we wait here to get the job's status. The lock is released in JobMonitorThread
				synchronized (phaseLock) 
				 {
					while (jobPhase != Constants.JobExitPhase)
						phaseLock.wait();
			     }
				logger.debug("Backup Result:"+result);
			}catch(Throwable e){
				logger.error("backup execute with execption", e);
			}
			
			if(ownerOfDestChanged && result == 0)
			{
				configuration.setChangedBackupDest(false);
				configuration.setChangedBackupDestType(BackupType.Unknown);
			}
			
		}catch(Exception e){
			logger.error("base backup job", e);
		}finally{
			if(ownerOfDestChanged)
			{
				synchronized (BaseBackupJob.class) {
					processingDestChanged = false;
				}
				ownerOfDestChanged = false;
			}
			
			if(!bMissed)
				releaseJobLock(vm.getVmInstanceUUID());

			// Move PFC triggering logic to the end of backup because, if it is at the begining of backup, occasionally PFC fails to login VM to perform data consistency check (might be cause that VM is not responding when snapshot is being taken)
			// Asynchronously connect to console and trigger PFC
			final String fInstanceUUID = instanceUUID;
			Thread triggerPFC = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					triggerPFC(fInstanceUUID);
				}
			}, "asynchronously-trigger-PFC");
			triggerPFC.start();	
						
			//if failed
			if(toRPSJobQueue)
				return;
			retryJobWhenFailedOrCrashed(jobDetail);
			
		}
		
		//VSphereService.getInstance().startVSphereCatalogJob(vm.getVmInstanceUUID());
		//sonle01: Inform sync Data to Edge
		EdgeDataSynchronization.SetSyncDataFlag();
		
/* move the logic into the new method updateConsoleWithInfoGotFromBackupJob()
		if (isWMwareVM) {
			VSphereService.getInstance().updateApplicationStatus2Edge(vm.getVmInstanceUUID());
		}
*/
		//added by Liang.Shu for enhancement to update OS column in console after each backup
		VSphereService.getInstance().updateConsoleWithInfoGotFromBackupJob(vm);
		
		logger.debug("execute(JobExecutionContext) - end");
	}

	public static BackupJobArg getBackupJobArg(JobDetailImpl jobDetail, VMBackupConfiguration configuration, long jobId) {
		BackupJobArg arg = new BackupJobArg();
		arg.setJobType(JobType.JOBTYPE_VM_BACKUP);
		arg.setJobDetailName(jobDetail.getName());
		arg.setJobDetailGroup(jobDetail.getGroup());
		arg.setPolicyUUID(configuration.getBackupRpsDestSetting().getRPSPolicyUUID());
		arg.setDataStoreUUID(configuration.getBackupRpsDestSetting().getRPSDataStore());
		arg.setDataStoreName(configuration.getBackupRpsDestSetting().getRPSDataStoreDisplayName());
		
		Object retriedTimes = jobDetail.getJobDataMap().get("retriedTimes");
		if (retriedTimes != null){
			arg.setRetryTimes((int)retriedTimes);
		}else{
			arg.setRetryTimes(0);
		}
		
		Object oriJobType = jobDetail.getJobDataMap().get("oriJobType");
		if (oriJobType != null){
			arg.setOriJobType((int)oriJobType);
		}
		
		Object oriJobID = jobDetail.getJobDataMap().get("oriJobID");
		if (oriJobID != null){
			arg.setOriJobID((long)oriJobID);
		}
		
		Object jobName = jobDetail.getJobDataMap().get("jobName");
		if(jobName != null){
			arg.setJobName((String)jobName);
		}
		
		Integer jobMethod = jobDetail.getJobDataMap().getInt("jobType");
		arg.setJobMethod(jobMethod);
		arg.setVM(true);
		arg.setJobId(jobId);
		arg.setD2dServerName(configuration.getBackupVM().getVmName()  + 
				"@" + configuration.getBackupVM().getEsxServerName());
		arg.setD2dServerUUID(configuration.getBackupVM().getInstanceUUID());
		
		PeriodRetentionSetting periodRetentionSetting = getPeriodRetentionSetting(jobDetail);		
		arg.setPeriodRetentionFlag(getPeriodRetentionFlag(periodRetentionSetting.isMonthly, periodRetentionSetting.isWeekly, periodRetentionSetting.isDaily));
		
		if(configuration.isGenerateCatalog())
			arg.setJobDependencies(new String[]{IJobDependency.VSPHERE_CATALOG_JOB});
		else {
			arg.setJobDependencies(new String[]{IJobDependency.VSPHERE_COPY_JOB});
		}
		
		return arg;
	}
	
	public static int getPeriodRetentionFlag(boolean isMonthly, boolean isWeekly, boolean isDaily) {
		int periodRetentionFlag = 0;
		if(isMonthly) periodRetentionFlag |= PeriodRetentionValue.QJDTO_B_Backup_Monthly;
		if(isWeekly) periodRetentionFlag |= PeriodRetentionValue.QJDTO_B_Backup_Weekly;		
		if(isDaily) periodRetentionFlag |= PeriodRetentionValue.QJDTO_B_Backup_Daily;		
		return periodRetentionFlag;
	}

	public static class PeriodRetentionSetting
	{
		public boolean isMonthly = false;
		public boolean isWeekly = false;
		public boolean isDaily = false;
		//public int periodRetentionFlag = 0;
	}
	
	public static PeriodRetentionSetting getPeriodRetentionSetting(JobDetail jobDetail)
	{
		PeriodRetentionSetting result = new PeriodRetentionSetting();
		
		if (jobDetail != null)
		{
			if(jobDetail.getJobDataMap().containsKey("isDaily")){
				result.isDaily =  jobDetail.getJobDataMap().getBoolean("isDaily");		
				logger.info("isDaily:" + result.isDaily);
			}
			if(jobDetail.getJobDataMap().containsKey("isWeekly")){
				result.isWeekly =  jobDetail.getJobDataMap().getBoolean("isWeekly");
				logger.info("isWeekly:" + result.isWeekly);
			}
			if(jobDetail.getJobDataMap().containsKey("isMonthly")){
				result.isMonthly =  jobDetail.getJobDataMap().getBoolean("isMonthly");
				logger.info("isMonthly:" + result.isMonthly);
			}
			
			if(jobDetail.getJobDataMap().containsKey("periodRetentionFlag")){
				int periodRetentionFlag = jobDetail.getJobDataMap().getInt("periodRetentionFlag");					
				result.isDaily  = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Daily) > 0;
				result.isWeekly  = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Weekly) > 0;
				result.isMonthly  = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Monthly) > 0;
				
				logger.info("periodRetentionFlag:" + periodRetentionFlag);
			}		
		}
		
		return result;
	}

	private void addBackupSetStartFlag(VMBackupConfiguration configuration,
			int jobType, JJobScript result, VirtualMachine vm, String jobDetailName) {
		if (configuration == null || result == null || vm == null) {
			return;
		}

		if (VSphereBackupSetService.getInstance().isCurrentBackupSetStart(
				configuration, jobType, jobDetailName, vm.getVmInstanceUUID())) {
			// set the backup set flag and convert current job to full.
			int foption = result.getFOptions();
			result.setFOptions(BackupJobConverter.BACKUP_SET_START | foption);
			if (result.getUsJobMethod() != BackupType.Full) {
				result.setUsJobMethod(BackupType.Full);
				String message = WebServiceMessages.getResource("convertJobToFullForBackupSetStart");
				nativeFacade.addVMLogActivityWithJobID(Constants.AFRES_AFALOG_WARNING,
								result.getUlJobID(),
								Constants.AFRES_AFJWBS_GENERAL,
								new String[] { message, "", "", "", "" }, vm.getVmInstanceUUID());
			}
		}
	}
	
	private void retryJobWhenFailedOrCrashed(JobDetailImpl jobDetail) {
		String vmInstanceUUID = this.getJobContext().getExecuterInstanceUUID();
		
		if (VSphereService.getInstance().isPlanDisabled(vmInstanceUUID)) {
			logger.info("Do not execute the makeup job for the VM  [instanceUUID=" + vmInstanceUUID + ", jobName=" + 
					jobDetail.getName() + "], since the plan is paused.");
			return;
		}
		else
		{
			logger.info("prepare the makeup job for the VM  [instanceUUID=" + vmInstanceUUID + ", jobName=" + jobDetail.getName() + "]" + " Job Status=" + this.getJobStatus());
		}
		
		if(this.getJobStatus() == Constants.JOBSTATUS_FAILED || this.getJobStatus() == Constants.JOBSTATUS_CRASH){
			//we exclude the now job, and retry job itself, just include the scheduled jobs
			if (jobDetail.getName() != null
					 && !jobDetail.getName().endsWith(BaseService.JOB_NAME_BACKUP_NOW_SUFFIX)){
				String name = jobDetail.getName();
				String group = jobDetail.getGroup();
				int type = BackupType.Unknown;
				if(name!=null && group!=null)
				{
					if(group.startsWith(BaseService.JOB_GROUP_BACKUP_NAME))
					{
						if (name.endsWith(Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME))
						{  
							int retriedType = jobDetail.getJobDataMap().getInt("oriJobType");
							int times = jobDetail.getJobDataMap().getInt("retriedTimes");
							long jobID = jobDetail.getJobDataMap().getLong("oriJobID");
							retryFailedBackup(retriedType,times+1,jobID,jobDetail);

						} else if (name.startsWith(VSphereService.JOB_NAME_BACKUP_FULL))
						{
							type = BackupType.Full;
							retryFailedBackup(type,1,this.shrmemid ,jobDetail);
						}else if (name.startsWith(VSphereService.JOB_NAME_BACKUP_RESYNC))
						{
							type = BackupType.Resync;
							retryFailedBackup(type,1,this.shrmemid ,jobDetail);
						}else if (name.startsWith(VSphereService.JOB_NAME_BACKUP_INCREMENTAL))
						{
							type = BackupType.Incremental;
							retryFailedBackup(type,1,this.shrmemid ,jobDetail);
						}
					}
				}
			}
		}
		else 
		{ //succeed, if it is one of retry job, we need to delete the job
			String jobName = jobDetail.getName();
			if(jobName!=null && jobName.endsWith(Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME))
			{
				try {
					VSphereService.getInstance().getBackupSchedule().deleteJob(new JobKey(jobName, ScheduleUtils.getBackupJobGroupName(vmInstanceUUID)));
				} catch (SchedulerException e1) {
					logger.error("execute - end "+ e1.getMessage(), e1);
				}
			}
			
		}
	}
	
/**
 * 
 * @param type the original backup type
 * @param times the times to setup a failed job
 * @param self_jobDetail the current job, used for delete
 */
	private void retryFailedBackup(int type,int times, long jobID, JobDetail self_jobDetail) {
			logger.info("retryFailedBackup() - begin" + " type = " + type + " times = " + times + " jobID = " + jobID);
			JobDetail jobDetail;
			String jobname = "";
			String jobDisplayName = "";
			VirtualMachine vm = new VirtualMachine();
			String vmInstanceUUID = this.getJobContext().getExecuterInstanceUUID();
			vm.setVmInstanceUUID(vmInstanceUUID);
			if (type == BackupType.Full)
			{
				//FullBackupJob_1_1_RETRY 
				jobname = VSphereService.JOB_NAME_BACKUP_FULL+this.getJobContext().getExecuterInstanceUUID()+"_"+jobID +"_" + times + Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME;
				jobDetail = new JobDetailImpl(jobname, ScheduleUtils.getBackupJobGroupName(vmInstanceUUID), VSphereBackupJob.class);
				jobDetail.getJobDataMap().put("jobType", type);
				jobDetail.getJobDataMap().put("vm", vm);
			}
			else if (type == BackupType.Incremental)
			{
				jobname = VSphereService.JOB_NAME_BACKUP_INCREMENTAL+this.getJobContext().getExecuterInstanceUUID()+"_"+jobID +"_" + times +Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME;
				jobDetail = new JobDetailImpl(jobname, ScheduleUtils.getBackupJobGroupName(vmInstanceUUID), VSphereBackupJob.class);
				jobDetail.getJobDataMap().put("jobType", type);
				jobDetail.getJobDataMap().put("vm", vm);
			}
			else if (type == BackupType.Resync)
			{
				jobname = VSphereService.JOB_NAME_BACKUP_RESYNC+this.getJobContext().getExecuterInstanceUUID()+"_"+jobID +"_" + times + Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME ;
				jobDetail = new JobDetailImpl(jobname, ScheduleUtils.getBackupJobGroupName(vmInstanceUUID), VSphereBackupJob.class);
				jobDetail.getJobDataMap().put("jobType", type);
				jobDetail.getJobDataMap().put("vm", vm);
			}
			else
			{
				logger.error("retryFailedBackup() - end with error type");
				return;
			}

			try{
				// condition 1, if policy enabled
				RetryPolicy retryPolicy = BackupService.getInstance().getRetryPolicy(CommonService.RETRY_BACKUP);
				if(!(retryPolicy.isEnabled() && retryPolicy.isFailedEnabled()))
				{
					logger.info("retryFailedBackup() - end with disabled retry policy for failed backup");
					nativeFacade.addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_JOB_RETRY,new String[]{WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_FAILED_DISABLED),"","","",""},vm.getVmInstanceUUID());
					return;
				}

				//quartz use default timezone,
				Calendar startCalendar = java.util.Calendar.getInstance();
				startCalendar.add(java.util.Calendar.MINUTE, retryPolicy.getTimeToWait());
				Date startDate = startCalendar.getTime();
				{// condition 2, if now is within specified minutes toward to next backup event
					if(!retryPolicy.isImmediately())
					{
						java.util.Calendar cal = new FlashServiceImpl().getServerCalendar();
						//time to wait
						cal.add(java.util.Calendar.MINUTE, retryPolicy.getTimeToWait());
						NextScheduleEvent nextScheduleEvent;
						
						try {
							nextScheduleEvent = VSphereService.getInstance().getNextScheduleEvent(vm);
							if(nextScheduleEvent!=null)
							{
								Date nextDate = nextScheduleEvent.getDate();
								java.util.Calendar serverCalendar = new FlashServiceImpl().getServerCalendar();
								serverCalendar.setTimeInMillis(nextDate.getTime());
	
								cal.add(java.util.Calendar.MINUTE, retryPolicy.getNearToNextEvent());
								if (cal.after(serverCalendar))
								{
									logger.info("retryFailedBackup() - end with too near to next event");
									String time = BackupConverterUtil.dateToString(nextDate);
									nativeFacade.addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_JOB_RETRY,new String[]{WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_FAILED_SKIPPED_NEXT, time),"","","",""},vm.getVmInstanceUUID());
									return;
								}
							}
						} catch (Exception e) {
							logger.error("retryFailedBackup - end "+ e.getMessage(), e);
						}
					}
				}
				// condition 3, times
				if(times > retryPolicy.getMaxTimes())
				{
					logger.info("retryFailedBackup() - end with too much times");
					nativeFacade.addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_JOB_RETRY,new String[]{WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_FAILED_EXCEED_MAXTIMES,""+jobID,""+retryPolicy.getMaxTimes()),"","","",""},vm.getVmInstanceUUID());
					return;	
				}
				//we omit the times and jobID to support i18n
				jobDisplayName = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_JOBNAME);
				//jobname is used for display in recent panel
				jobDetail.getJobDataMap().put("jobName", jobDisplayName);
				jobDetail.getJobDataMap().put("oriJobType", type);
				jobDetail.getJobDataMap().put("oriJobID", jobID);
				jobDetail.getJobDataMap().put("retriedTimes", times);
				jobDetail.getJobDataMap().put("periodRetentionFlag", self_jobDetail.getJobDataMap().getInt("periodRetentionFlag"));
				
				VSphereService.getInstance().modifyPeriodJobDetail(jobDetail, self_jobDetail);
				
		        SimpleTriggerImpl trig = new SimpleTriggerImpl();
		        if(retryPolicy.isImmediately()) 
		        {
		        	Calendar cal = Calendar.getInstance();
					cal.add(Calendar.MINUTE, 1);
					startDate = cal.getTime();		
		        }
		        trig.setStartTime( startDate);
		        trig.setRepeatCount(0);
		        trig.setRepeatInterval(0);
		        trig.setName(((JobDetailImpl)jobDetail).getName()+"Trigger");
				
				try {
					VSphereService.getInstance().getBackupSchedule().scheduleJob(jobDetail, trig);
					String date = BackupConverterUtil.dateToString(startDate);
					logger.info("retry job for job = " + jobID + " vmUuid = " + vm.getVmInstanceUUID() + " is scheduled at " + date );
				    nativeFacade.addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_JOB_RETRY,new String[]{WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_FAILED_SCHEDULED, date, ""+jobID),"","","",""},vm.getVmInstanceUUID());
				} catch (Exception e) {
					logger.error("retryFailedBackup - end "+ e.getMessage(), e);
				}
			}
			catch(Exception e)
			{
				logger.error("failed when schedule retry job", e);
			}
			finally
			{
				if(((JobDetailImpl)self_jobDetail).getName().endsWith(Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME))
				{
					try {
						VSphereService.getInstance().getBackupSchedule().deleteJob(new JobKey(((JobDetailImpl)self_jobDetail).getName(),VSphereService.JOB_GROUP_BACKUP_NAME));
					} catch (Exception e1) {
						logger.error("retryFailedBackup - end "+ e1.getMessage(), e1);
					}
				}
			}
			logger.debug("retryFailedBackup() - end");
			return ;
		
		
	}

	private void sendEmailOnMissedJob(JobExecutionContext context,VirtualMachine vm, int jobMethod) {
		logger.debug("sendEmail - start");
		try {
			VMBackupConfiguration configuration = VSphereService.getInstance()
					.getVMBackupConfiguration(vm);
			if (configuration == null)
				return;
			
			if (isConfigurationForRemoteNode(configuration)) {
				logger.info("The configuration is for remote nodes.");
				return;
			}

			BackupEmail email = configuration.getEmail();
			if (email == null)
				return;

			if (email.isEnableEmailOnMissedJob()) {
				EmailSender emailSender = new EmailSender();
				String jobStatus = EmailContentTemplate.jobStatus2String(Constants.JOBSTATUS_MISSED);
				String emailJobStatus = WebServiceMessages
						.getResource("EmailJobStatus");
				String vmName = configuration.getBackupVM().getVmName();
				String nodeName = configuration.getBackupVM().getVmHostName();
				if(nodeName==null||nodeName.isEmpty()){
					nodeName = WebServiceMessages.getResource("EmailNodeNameUnknown");
				}
				String emailSubject = WebServiceMessages.getResource("EmailSubject",
							email.getSubject(), emailJobStatus+jobStatus ,vmName, nodeName);

				/** for promoting alert message  to edge server */
				emailSender.setJobStatus(Constants.JOBSTATUS_MISSED);
				emailSender.setProductType(CommonEmailInformation.PRODUCT_TYPE.VSPHERE.getValue());
				
				
				emailSender.setSubject(emailSubject);

				emailSender.setContent(getMissedJobContent(email
						.isEnableHTMLFormat(), context, configuration, jobMethod));

				emailSender.setUseSsl(email.isEnableSsl());
				emailSender.setSmptPort(email.getSmtpPort());
				emailSender.setMailPassword(email.getMailPassword());
				emailSender.setMailUser(email.getMailUser());
				emailSender.setUseTls(email.isEnableTls());
				emailSender.setProxyAuth(email.isProxyAuth());
				emailSender.setMailAuth(email.isMailAuth());

				emailSender.setFromAddress(email.getFromAddress());
				emailSender.setRecipients(email.getRecipientsAsArray());
				emailSender.setSMTP(email.getSmtp());
				emailSender.setEnableProxy(email.isEnableProxy());
				emailSender.setProxyAddress(email.getProxyAddress());
				emailSender.setProxyPort(email.getProxyPort());
				emailSender.setProxyUsername(email.getProxyUsername());
				emailSender.setProxyPassword(email.getProxyPassword());

				emailSender.setJobType(this.getDefaultJobType() );
				emailSender.setProtectedNode(vmName);
				
				emailSender.sendEmail(email.isEnableHTMLFormat());
			}
		} catch (Exception e) {
			logger.error("Error in sending email", e);
		}

	}

	private String getMissedJobContent(boolean isEnableHTMLFormat,
			JobExecutionContext context, VMBackupConfiguration configuration, int jobMethod) {
		String exeDate = BackupConverterUtil.dateToString(context.getFireTime());	
		int scheduletype=getScheduleType(context.getJobDetail());
		String url = getEdgeUrl();
		if(isEnableHTMLFormat){
			return 	EmailContentTemplate.getVSphereHtmlContent(Constants.JOBSTATUS_MISSED, 
							Constants.AF_JOBTYPE_VM_BACKUP, jobMethod, scheduletype, -1, configuration.getDestination(),
							exeDate, configuration, null, url, 0);
		}else{
			return EmailContentTemplate.getVSpherePlainTextContent(Constants.JOBSTATUS_MISSED, 
							Constants.AF_JOBTYPE_VM_BACKUP, scheduletype, jobMethod, 
							-1, configuration.getDestination(),exeDate, configuration, null, url, 0);
		}
	}
	
	public int getScheduleType(){
		return this.scheduletype;
	}
	
	private int getScheduleType(JobDetail jobDetail){
		int scheduletype=0;
		if (this.isNotBackupNowJob(jobDetail))
		{
			PeriodRetentionSetting periodRetentionSetting = getPeriodRetentionSetting(jobDetail);
			scheduletype = getPeriodRetentionFlag(periodRetentionSetting.isMonthly, periodRetentionSetting.isWeekly, periodRetentionSetting.isDaily);
		}
		else
			scheduletype=-1;
		return scheduletype;
	}
	
	private static void makeupJob(VirtualMachine vm, int jobStatus, JobDetailImpl jobDetail) {
		if (jobDetail.getName() != null	&& jobDetail.getName().endsWith(BaseService.JOB_NAME_BACKUP_NOW_SUFFIX)) {
			logger.debug("we exclude the now job, and retry job, just include the scheduled jobs");
			return;
		}
		logger.info("try to makeup job:" + jobDetail.getFullName() + ", status:" + jobStatus);

		String oldName = jobDetail.getFullName();

		RetryPolicy policy = BackupService.getInstance().getRetryPolicy(CommonService.RETRY_BACKUP);
		
		// condition 1, if enabled or not
		if (!(policy.isEnabled() && policy.isMissedEnabled())) {
			logger.debug("makeupBackup() - end with disabled retry policy for missed(s) backup " + jobStatus);
			return;
		}

		// condition 2, if now is within 15 minutes toward to next
		// backup event
		try {
			NextScheduleEvent nextScheduleEvent = VSphereService.getInstance().getNextScheduleEvent(vm);
			if (nextScheduleEvent != null) {
				Date nextDate = nextScheduleEvent.getDate();
				java.util.Calendar serverCalendar = new FlashServiceImpl().getServerCalendar();
				serverCalendar.setTimeInMillis(nextDate.getTime());

				java.util.Calendar cal = new FlashServiceImpl().getServerCalendar();
				cal.add(java.util.Calendar.MINUTE, policy.getNearToNextEvent());
				if (cal.after(serverCalendar)) {
					logger.debug("makeupMissedBackup() - end with too near to next event");
					return;
				}
			}

		} catch (ServiceException e) {
			logger.error("makeupMissedBackup() - end", e);
		}
		// condition 3, if schedule backup is missed
		scheduleMissedScheduleJob(jobStatus, jobDetail, oldName, vm.getVmInstanceUUID());
	}
	
	public static void makeupSkippedBackup(String vmIndentification) {
		logger.debug("makeupSkippedBackup() - begin");
		JobDetail jobDetail = null;
		synchronized (currentJobLock) {
			jobDetail = currentJobMap.get(vmIndentification).getJobDetail();
		}
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vmIndentification);
		
		makeupJob(vm, skippedJob, (JobDetailImpl)jobDetail);

		logger.debug("makeupSkippedBackup() - end");
	}
	
	private static void makeupPeriodJob(VirtualMachine vm, int jobStatus, JobDetailImpl jobDetail) {
		if (jobDetail.getName() != null && jobDetail.getName().endsWith(BaseService.JOB_NAME_BACKUP_NOW_SUFFIX)) {
			logger.debug("we exclude the now job, and retry job, just include the scheduled jobs");
			return;
		}
		logger.info("try to makeup Period job:" + jobDetail.getFullName() + ", status:" + jobStatus);

		String oldName = jobDetail.getFullName();
		try {
			RetryPolicy policy = VSphereService.getInstance().getRetryPolicy(CommonService.RETRY_BACKUP);
			if(policy != null && !(policy.isEnabled() && policy.isMissedEnabled())) {
				int jobId = 0;
				Object objJobID = jobDetail.getJobDataMap().get(VSphereService.JOB_ID);
				if(objJobID != null) {
					jobId = (int) jobDetail.getJobDataMap().get(VSphereService.JOB_ID);
				}
			// condition 1, if enabled or not
				nativeFacade.addVMLogActivityWithJobID(
								Constants.AFRES_AFALOG_WARNING,
								jobId,
								Constants.AFRES_AFJWBS_JOB_RETRY,
								new String[] {
										WebServiceMessages
												.getResource(Constants.RETRYPOLICY_FOR_MISSED_DISABLED),
										"", "", "", "" }, vm.getVmInstanceUUID());
				logger.info("makeupBackup() - end with disabled retry policy for missed(s) backup " + jobStatus);
				return;
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		// condition 2, if schedule backup is missed
		scheduleMissedScheduleJob(jobStatus, jobDetail, oldName, vm.getVmInstanceUUID());
	}
	
	private static void scheduleMissedScheduleJob(int jobStatus, JobDetailImpl jobDetail, String oldName, String vmInstanceUUID) {
		if (VSphereService.getInstance().isPlanDisabled(vmInstanceUUID)) {
			logger.info("Do not execute the Makeup job for the VM  [instanceUUID=" + vmInstanceUUID + ", jobName=" + 
					jobDetail.getName() + "], since the plan is paused.");
			return;
		}
		
		try {
			String jobTypeName = jobDetail.getName();
			
			// liuwe05 2011-5-25 fix Issue: 20300004    Title: JOB TYPE ERROR-MAIL ALERT
			// should not use display name as the job name, otherwise we cannot identify the job method from job name. see method getJobMethodFromJobDetail()
			String status = getRetryJob(jobStatus);
			if(!jobTypeName.startsWith(status)) {
				jobDetail.setName(status +" "+ jobTypeName);
			} else {
				jobTypeName = jobTypeName.substring(status.length() + 1);
			}
			SimpleTriggerImpl trig = new SimpleTriggerImpl();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 1);
			Date startDate = cal.getTime();
			trig.setStartTime(startDate);
			trig.setRepeatCount(0);
			trig.setRepeatInterval(0);
			trig.setName(jobDetail.getName() + "_" +vmInstanceUUID + "_" + "Trigger"); // 2015-07-15 fix Bug 371733:[223983] if conflict occurs for scheduled backups having two vms in plan
			VSphereService.getInstance().getBackupSchedule().scheduleJob(jobDetail, trig);
			
			logger.info("makeup for Missed Backup job is scheduled, origName: " +oldName + ", new Name:" +  jobDetail.getName()+", jobTypeName:" + jobTypeName);
			long jobId = 0;
			Object objJobID = jobDetail.getJobDataMap().get(VSphereService.JOB_ID);
			if (objJobID != null)
			{
				jobId = (long) objJobID;
			}
			nativeFacade.addVMLogActivityWithJobID(
							Constants.AFRES_AFALOG_WARNING,
							jobId,
							Constants.AFRES_AFJWBS_JOB_RETRY,
							new String[] {WebServiceMessages.getResource(
									Constants.RETRYPOLICY_FOR_MISSED_SCHEDULED,
									getRetryJob(jobStatus),
									BackupConverterUtil.backupIndicatorToName(jobTypeName)),
									"", "", "", "" }, vmInstanceUUID);
		} catch (Exception e) {
			logger.error("makeupMissedBackup() - end " + e.getMessage(), e);
		}
		
		logger.info("makeup for missed backup job complete");
	}
	
	public static void makeupMissedBackup(String vmIndentification) {
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vmIndentification);
		logger.debug("makeupMissedBackup() - begin");
		JobDetailImpl jobDetail = null;
		synchronized (missVMJobLock) {			
			if (vmMissedBackupJobContext.get(vmIndentification) == null) {
				logger.debug("no missed job");
				return;
			} else {
				jobDetail = (JobDetailImpl)vmMissedBackupJobContext.get(vmIndentification).getJobDetail();			
				vmMissedBackupJobContext.remove(vmIndentification);
			}
		}
		
		if (jobDetail.getName() != null	&& jobDetail.getName().endsWith(BaseService.JOB_NAME_BACKUP_NOW_SUFFIX)) {
			logger.debug("we exclude the now job, and retry job, just include the scheduled jobs");
			return;
		}

		if(isPeriodSchedule(jobDetail)){
			logger.info("makeupPeriodJob : " + vm.getVmName() + " - " + vm.getVmInstanceUUID());
			makeupPeriodJob(vm, missedJob, (JobDetailImpl)jobDetail);
		}else{
			logger.info("makeupJob : " + vm.getVmName() + " - " + vm.getVmInstanceUUID());
			makeupJob(vm, missedJob, (JobDetailImpl)jobDetail);
		}
		
		logger.debug("makeupMissedBackup() - end");
	}
	
	
	private static boolean isPeriodSchedule(JobDetail jobDetail){
		if (jobDetail == null) return false;
		
		boolean isDaily=false, isWeekly=false, isMonthly=false;
		
		if(jobDetail.getJobDataMap().containsKey("isDaily")){
			isDaily =  jobDetail.getJobDataMap().getBoolean("isDaily");					
		}
		if(jobDetail.getJobDataMap().containsKey("isWeekly")){
			isWeekly =  jobDetail.getJobDataMap().getBoolean("isWeekly");			
		}
		if(jobDetail.getJobDataMap().containsKey("isMonthly")){
			isMonthly =  jobDetail.getJobDataMap().getBoolean("isMonthly");			
		}
		return isDaily || isWeekly || isMonthly;
	}

	
	private static String getRetryJob(int jobStatus) {
		String jobName = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED);
		switch(jobStatus) {
		case missedJob:
			jobName = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED);
			break;
		case skippedJob:
			jobName = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_SKIPPED);
			break;
		default:
			break;
		}
		 
		return jobName;
	}
	
	private void sendMailIfNeed(JobExecutionContext context,VMBackupConfiguration configuration,int eventType){
		try{
			
			if (configuration == null)
				return;
			
			BackupEmail email = configuration.getEmail();
			if (email == null)
				return;
			
			
			boolean isSend = false;
			if(eventType == JOBSTATUS_VSPHERE_LICENSE_FAIL && configuration.getEmail().isEnableEmailOnLicensefailure()){
				isSend = true;
			}
			if(eventType == JOBSTATUS_HOST_NOT_FOUND && configuration.getEmail().isEnableEmailOnHostNotFound()){
				isSend = true;
			}
			if(eventType == Constants.JOBSTATUS_FAILED && configuration.getEmail().isEnableEmailOnHostNotFound()){
				isSend = true;
			}
			/*if(eventType == JOBSTATUS_DATASTROE_NOT_ENOUGH && configuration.getEmail().isEnableEmailOnDataStoreNotEnough()){
				isSend = true;
			}*/
			if(isSend){
				EmailSender emailSender = new EmailSender();
				//fix issue:106497 fanda03
				if( eventType == JOBSTATUS_VSPHERE_LICENSE_FAIL || eventType == JOBSTATUS_HOST_NOT_FOUND || eventType == Constants.JOBSTATUS_FAILED ) {
					emailSender.setHighPriority(true);
				}
				//end fix
				String jobStatus = EmailContentTemplate.jobStatus2String(Constants.JOBSTATUS_FAILED);
				String emailJobStatus = WebServiceMessages
						.getResource("EmailJobStatus");
				String vmName = configuration.getBackupVM().getVmName();
				String nodeName = configuration.getBackupVM().getVmHostName();
				if(nodeName==null||nodeName.isEmpty()){
					nodeName = WebServiceMessages.getResource("EmailNodeNameUnknown");
				}
				String emailSubject = WebServiceMessages.getResource("EmailSubject",
							email.getSubject(), emailJobStatus+jobStatus ,vmName, nodeName);
	
				/** for promoting alert message  to edge server */
				emailSender.setJobStatus(eventType);
				emailSender.setProductType(CommonEmailInformation.PRODUCT_TYPE.VSPHERE.getValue());
				
				
				emailSender.setSubject(emailSubject);
	
				emailSender.setContent(getEmailContent(eventType,email
						.isEnableHTMLFormat(), context, configuration));
	
				emailSender.setUseSsl(email.isEnableSsl());
				emailSender.setSmptPort(email.getSmtpPort());
				emailSender.setMailPassword(email.getMailPassword());
				emailSender.setMailUser(email.getMailUser());
				emailSender.setUseTls(email.isEnableTls());
				emailSender.setProxyAuth(email.isProxyAuth());
				emailSender.setMailAuth(email.isMailAuth());
	
				emailSender.setFromAddress(email.getFromAddress());
				emailSender.setRecipients(email.getRecipientsAsArray());
				emailSender.setSMTP(email.getSmtp());
				emailSender.setEnableProxy(email.isEnableProxy());
				emailSender.setProxyAddress(email.getProxyAddress());
				emailSender.setProxyPort(email.getProxyPort());
				emailSender.setProxyUsername(email.getProxyUsername());
				emailSender.setProxyPassword(email.getProxyPassword());
	
				emailSender.setJobType(this.getDefaultJobType() );
				emailSender.setProtectedNode(  vmName );
				
				emailSender.sendEmail(email.isEnableHTMLFormat());
			}
		}catch(Exception e){
			logger.error("Error in sendMailIfNeed", e);
		}
	}
	
	private String getEmailContent(long jobStatus,boolean isEnableHTMLFormat,
			JobExecutionContext context, VMBackupConfiguration configuration) {
		JobDetail jobDetail = context.getJobDetail();
		String exeDate = BackupConverterUtil.dateToString(context.getFireTime());		
		long jobMethod = jobDetail.getJobDataMap().getInt("jobType");
		String vmUUID = ((VirtualMachine)jobDetail.getJobDataMap().get("vm")).getVmInstanceUUID();
		String url = getEdgeUrl();
		int scheduletype=getScheduleType(context.getJobDetail());
		if(isEnableHTMLFormat){
			return EmailContentTemplate.getVSphereHtmlContent(jobStatus, Constants.AF_JOBTYPE_VM_BACKUP, 
							jobMethod, scheduletype,  shrmemid, configuration.getDestination(), exeDate, configuration, 
					        getVMActivityLogResult(shrmemid, vmUUID), url, 0);
		}else{
			return EmailContentTemplate.getVSpherePlainTextContent(jobStatus, Constants.AF_JOBTYPE_VM_BACKUP,
							jobMethod, scheduletype, shrmemid,configuration.getDestination(), exeDate, configuration, 
					        getVMActivityLogResult(shrmemid, vmUUID), url, 0);
		}
		
	}

	private ActivityLogResult getVMActivityLogResult(long jobid, String vmUUID) {
		if(jobid<0)
			return null;
		try {
			logger.debug("getActivityLogResult - jobid = " + jobid);
			return CommonService.getInstance().getVMJobActivityLogs(jobid, 0, 512, vmUUID);
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		return null;
	}

	@Override
	protected long getDefaultJobType() {
		return JobType.JOBTYPE_VM_BACKUP;
	}
	
	public static synchronized boolean getJobLock(String vmInstanceUUID){
		synchronized(runningJobs) {
			if(vmInstanceUUID == null){
				logger.error("Null UUID ");
				return false;
			}
			if(runningJobs.contains(vmInstanceUUID)){	
				logger.info("getJobLock already contains " + vmInstanceUUID);
				return false;
			}else{
				logger.info("getJobLock add uuid " + vmInstanceUUID);
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
	
	public static synchronized void releaseJobLock(String vmInstanceUUID) {
		synchronized(runningJobs) {
			logger.info("releaseJobLock - " + vmInstanceUUID);
			if(vmInstanceUUID == null){
				logger.error("Null UUID ");
				return;
			}
			runningJobs.remove(vmInstanceUUID);
		}
	}
	
	private boolean isNotBackupNowJob(JobDetail jobDetail) {
		Boolean backupNow = (Boolean) jobDetail.getJobDataMap().get("backupNow");
		logger.info("isNotBackupNowJob " + backupNow);
		return backupNow == null || !backupNow;
	}
	

	protected boolean preprocess(JJobScript js,Observer[] observers) {
		logger.debug("preprocess(JJobScript) - start");

		if(!getJobLock())
			return false;
		try {
          js.setUlJobID(shrmemid);
          JJobHistory jobHistory = new JJobHistory();
          jobHistory.setJobId(shrmemid);
          jobHistory.setJobMethod(js.getUsJobMethod());
          jobHistory.setJobType((int)getDefaultJobType());
          jobHistory.setJobRunningNodeUUID(getJobContext().getLauncherInstanceUUID());
          jobHistory.setJobDisposeNodeUUID(getJobContext().getExecuterInstanceUUID());
          jobHistory.setJobDisposeNode(getJobContext().getVmName());
          jobHistory.setDatastoreUUID(getRPSDatastoreUUID());
          RPSDataStoreInfo rpsDataStoreInfo = getRpsDataStoreInfo(getRPSDatastoreUUID());
          jobHistory.setDatastoreVersion(rpsDataStoreInfo.getVersion());
          jobHistory.setTargetUUID(rpsDataStoreInfo.getRpsServerId());
          String planUUID = getPlanUUID(getJobContext().getExecuterInstanceUUID());
          if(StringUtil.isEmptyOrNull(planUUID)) {
        	  VirtualMachine vm = new VirtualMachine();
        	  vm.setVmInstanceUUID(getJobContext().getExecuterInstanceUUID());
        	  VMBackupConfiguration conf = VSphereService.getInstance().getVMBackupConfiguration(vm);
        	  if(conf != null) {
        		  String groupInstanceUUID = conf.getBackupVM().getGroupInstanceUUID();
        		  if(!StringUtil.isEmptyOrNull(groupInstanceUUID)) {
        			  planUUID = getPlanUUID(groupInstanceUUID);
        		  }
        	  }
          }
          jobHistory.setPlanUUID(planUUID);
          nativeFacade.updateJobHistory(jobHistory);
          
          if(observers != null){
			for(Observer observer : observers){
	        	addObserver(observer);
	        }
          }
          pauseMergeJob(js != null ? js.getUsJobType() : 0);
          vmpool.submit(new VSphereJobMonitorThread(this,getJobContext()));
		}
		catch (RuntimeException e) {
			releaseJobLock(getJobContext().getExecuterInstanceUUID());
			throw e;
		} catch (ServiceException e1) {
			
		}

		logger.debug("preprocess(JJobScript) - end");
		return true;
	}
	
	public static void updateJobDetailByJobArg(JobDetail jobDetail, BackupJobArg arg, boolean backupNow)
	{
		if (jobDetail != null && arg != null)
		{
			try
			{
				VirtualMachine vm = new VirtualMachine();
				vm.setVmInstanceUUID(arg.getD2dServerUUID());
				VMBackupConfiguration vmBackupConfig;
				vmBackupConfig = VSphereService.getInstance().getVMBackupConfiguration(vm);
				
				jobDetail.getJobDataMap().put(BaseService.RPS_CATALOG_GENERATION, arg.isEnableCatalog());
				jobDetail.getJobDataMap().put("jobName", arg.getJobName());
				jobDetail.getJobDataMap().put("jobType", arg.getJobMethod());
				jobDetail.getJobDataMap().put(VSphereService.JOB_ID, arg.getJobId());
				if (backupNow)
				    jobDetail.getJobDataMap().put("backupNow", Boolean.TRUE);
				jobDetail.getJobDataMap().put("vm", vm);
				jobDetail.getJobDataMap().put("periodRetentionFlag", arg.getPeriodRetentionFlag());
				jobDetail.getJobDataMap().put("jobQueuePriority", vmBackupConfig.getBackupQueuePriority());
				jobDetail.getJobDataMap().put("retriedTimes", arg.getRetryTimes());
				jobDetail.getJobDataMap().put("oriJobType", arg.getOriJobType());
				jobDetail.getJobDataMap().put("oriJobID", arg.getOriJobID());
				jobDetail.getJobDataMap().put("manualFlag", arg.isManual());
			}
			catch (Exception e)
			{
				logger.error(e);
			}
		}
	}
	
	private void triggerPFC(String VMInstanceUUID){
		logger.info("Start to trigger PFC asynchronously for VM " + VMInstanceUUID);
		EdgeRegInfo info = null;
		try {
			D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();

			info = edgeRegInfo.getEdgeRegInfo(ApplicationType.vShpereManager);

			if(info == null) 
			{
				logger.error("Failed to trigger PFC for VM " + VMInstanceUUID);
				return;
			}
		}
		catch(Exception ex) {
			logger.error("Unexpected exception occurs. Failed to trigger PFC for VM " + VMInstanceUUID + ". " + ex.getMessage(), ex);
			return;
		}
		
		IEdgeCM4D2D edgeService = null;
		try {
			edgeService = WebServiceFactory.getEdgeService(info.getEdgeWSDL(),IEdgeCM4D2D.class);
			edgeService.validateUserByUUID(info.getEdgeUUID());
		}
		catch(Exception ex) {
			logger.error("Unexpected exception occurs when connecting to Edge. " +
					"Failed to trigger PFC for VM " + VMInstanceUUID + ". " + ex.getMessage(), ex);
			return;
		}
		
		try {
			List<String> VMInstanceUUIDs = new ArrayList<String>();
			VMInstanceUUIDs.add(VMInstanceUUID);
			
			edgeService.verifyVMsByInstanceUUID(VMInstanceUUIDs);
			logger.info("Finished to trigger PFC asynchronously for VM " + VMInstanceUUID);
		} 
		catch(Exception ex) {
			logger.error("Internal error occurs in Edge side. Failed to trigger PFC for VM " + VMInstanceUUID + ". " 
					+ ex.getMessage(), ex);
			return;
		}
		return;

	}
}

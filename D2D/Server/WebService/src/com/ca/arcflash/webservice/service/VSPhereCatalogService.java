package com.ca.arcflash.webservice.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.service.common.CatalogQueueType;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.backup.RetryPolicy;
import com.ca.arcflash.webservice.data.catalog.CatalogJobPara;
import com.ca.arcflash.webservice.data.job.rps.CatalogJobArg;
import com.ca.arcflash.webservice.data.job.rps.IJobDependency;
import com.ca.arcflash.webservice.data.job.rps.JobDependencySource;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.jni.model.JBackupVM;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.jni.model.JVMJobMonitorDetail;
import com.ca.arcflash.webservice.scheduler.BaseBackupJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.BaseVSphereJob;
import com.ca.arcflash.webservice.scheduler.OndemandCatalogJob;
import com.ca.arcflash.webservice.scheduler.VSphereCatalogJob;
import com.ca.arcflash.webservice.scheduler.VSphereOndemandCatalogJob;
import com.ca.arcflash.webservice.service.internal.VSphereJobContext;
import com.ca.arcflash.webservice.service.rps.JobService;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;
import com.ca.arcflash.webservice.util.ScheduleUtils;


public class VSPhereCatalogService extends BaseService implements IJobDependency{
	private Logger logger = Logger.getLogger(VSPhereCatalogService.class);
	private Map<String, VSphereCatalogJob> makeupJobs = new HashMap<String, VSphereCatalogJob>();
	
	private static VSPhereCatalogService instance = new VSPhereCatalogService();
	
	private VSPhereCatalogService(){}
	
	private Scheduler getCatalogScheduler(){
		return VSphereService.getInstance().getCatalogScheduler();
	}
	
	public static VSPhereCatalogService getInstance(){
		return instance;
	}
	
	private VSphereCatalogJob getMakeupJob(String vmInstanceUUID){
		return makeupJobs.get(vmInstanceUUID);
	}
	
	private void startMakeupJob(String vmInstanceUUID){
		VSphereCatalogJob catalogJob = getMakeupJob(vmInstanceUUID);
		if(catalogJob!=null){
			catalogJob.start();
		}
	}
	
	public void setMakeupJob(VSphereCatalogJob job,String vmInstanceUUID) {
		makeupJobs.put(vmInstanceUUID, job);
	}
	/**
	 * We always first start makeup job.
	 * If catalog job success, we start another catalog job;
	 * else we start the makeup job in 15 minutes (the number is defined in RetryPolicy.xml)
	 */
	public synchronized void update(Observable o, Object arg) {
		if(arg instanceof JVMJobMonitorDetail) {
			JVMJobMonitorDetail jobMonitorDetail = (JVMJobMonitorDetail)arg;
			JJobMonitor jJM = jobMonitorDetail.getjJobMonitor();
			VSphereJobContext jobContext = jobMonitorDetail.getJobContext();
			String vmInstanceUUID = jobContext.getExecuterInstanceUUID();
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(vmInstanceUUID);
			// disable plan
			try {
				VMBackupConfiguration conf = VSphereService.getInstance().getVMBackupConfiguration(vm);;
				if (conf.isDisablePlan()) {
					logger.info("The plan is disabled, do not run catalog job");
					return;
				}
			} catch (ServiceException e) {
				logger.error("Can not get backup configuration");
			}
			
			if(o instanceof VSphereOndemandCatalogJob)
			{
				this.startOnDemandCatalogJob(-1, null, null, null, vmInstanceUUID);				
			}
			else if((jJM.getUlJobType() == Constants.AF_JOBTYPE_VM_BACKUP ||
				jJM.getUlJobType() == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
				jJM.getUlJobType() == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP ||
				jJM.getUlJobType() == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP)
					&& jJM.getUlJobStatus() == BaseBackupJob.BackupJob_PROC_EXIT){
				if(getMakeupJob(vmInstanceUUID) != null) {
					//start make up job
					startMakeupJob(vmInstanceUUID);
				}else {
					startRegularJob(true, vmInstanceUUID);
				}
			}else if(jJM.getUlJobType() == Constants.AF_JOBTYPE_VM_CATALOG_FS		
					|| jJM.getUlJobType() == Constants.JOBTYPE_CATALOG_GRT
					|| jJM.getUlJobType() == Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND) {
				if(jJM.getUlJobStatus() == Constants.JOBSTATUS_FINISHED)
					startRegularJob(true, vmInstanceUUID);					
				else
					startRegularJob(false, vmInstanceUUID);
			}else {
				startMergeJobForNoCatalog(vmInstanceUUID);
			}
		}
	}
		
	
	public void startRegularJob(boolean startImmediately, String vmInstanceUUID) {
		long catJob = IsCatalogAvaiable(CatalogQueueType.MAKEUP_JOB, vmInstanceUUID); 
		if( catJob != 0) {
			if(startImmediately) {
				startCatalogJob(-1, 0, CatalogQueueType.MAKEUP_JOB, vmInstanceUUID, catJob);
			}else {
				makeupFailedCatalog(vmInstanceUUID, catJob);
				startMergeJobForNoCatalog(vmInstanceUUID);
			}	
		}else{
				catJob = IsCatalogAvaiable(CatalogQueueType.REGULAR_JOB, vmInstanceUUID);
				if(catJob != 0) {
					startCatalogJob(-1, 0, CatalogQueueType.REGULAR_JOB, vmInstanceUUID, catJob);
				}else {
					//no need to start catalog, start merge job
					startMergeJobForNoCatalog(vmInstanceUUID);
				}
			}
	}
	
	public long IsCatalogAvaiable(long jobQueueType, String queueIdentity) {
		return WSJNI.AFIsCatalogAvailable(jobQueueType, queueIdentity, "");
	}
	
	private boolean makeupFailedCatalog(String vmInstanceUUID, long catJob) {
		RetryPolicy policy = BackupService.getInstance().getRetryPolicy(CommonService.RETRY_CATALOG);
		// condition 1, if enabled or not
		if(!(policy.isEnabled()))
		{
			logger.warn("makeup catalog - end with disabled retry policy for backup");
			return false;
		}
		
		startCatalogJob(-1, policy.getTimeToWait() * 60 * 1000, CatalogQueueType.MAKEUP_JOB, vmInstanceUUID, catJob);
		return true;
	}
	
	
	/**
	 * schedule a catalog job
	 * @param jobID jobID: for common situations, it's -1, 
	 * 	      for resume job after restart it's the value get from backend
	 * @param startTime time to wait before launch the job in milliseconds
	 */
	public void startCatalogJob(long jobID, long waitTime, long queueType, String vmInstanceUUID, long jobType){
		startCatalogJob(jobID, waitTime, queueType, vmInstanceUUID, null, null, null, jobType);
	}
	
	/**
	 * schedule a catalog job
	 * @param jobID jobID: for common situations, it's -1, 
	 * 	      for resume job after restart it's the value get from backend
	 * @param startTime time to wait before launch the job in milliseconds
	 */
	public void startCatalogJob(long jobID, long waitTime, long queueType, String vmInstanceUUID, 
			String destination, String userName, String password, long jobType){
		if(VSphereCatalogJob.isJobRunning(vmInstanceUUID)){
			logger.info("Catalog job for " + vmInstanceUUID + " is already scheduled");
			return;
		}
		
		if(BaseVSphereJob.isJobRunning(vmInstanceUUID,String.valueOf(Constants.AF_JOBTYPE_VM_CATALOG_FS))
				|| BaseVSphereJob.isJobRunning(vmInstanceUUID,String.valueOf(Constants.JOBTYPE_CATALOG_GRT))
				|| BaseVSphereJob.isJobRunning(vmInstanceUUID,String.valueOf(Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND))){
			logger.info("Vsphere Catalog job is running, no need to schedule it again:"+vmInstanceUUID);
			return;
		}
		
		try {
			String jobName = JOB_NAME_CATALOG+ vmInstanceUUID + System.currentTimeMillis();
			JobDetail jobDetail = new JobDetailImpl(jobName, JOB_GROUP_CATALOG_NAME, VSphereCatalogJob.class);
			jobDetail.getJobDataMap().put(CatalogService.JOB_ID, jobID);
			jobDetail.getJobDataMap().put(CatalogService.WAIT_TIME, waitTime);
			jobDetail.getJobDataMap().put(CatalogService.QUEUE_TYPE, queueType);
			jobDetail.getJobDataMap().put(CatalogService.JOB_TYPE, jobType);
			jobDetail.getJobDataMap().put(CatalogService.DESTINATION, destination);
			jobDetail.getJobDataMap().put(CatalogService.USERNAME, userName);
			jobDetail.getJobDataMap().put(CatalogService.PASSWORD, password);
			jobDetail.getJobDataMap().put("vmInstanceUUID", vmInstanceUUID);
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			trigger.setName(jobName);
			getCatalogScheduler().scheduleJob(jobDetail, trigger);
		}catch(SchedulerException e){
			logger.error("Failed to scheduler Vsphere catalog jobs", e);
		}
	}
	
	/**
	 * Call back end API to do real catalog
	 * @param id]
	 * @param type : catalog type: 1 for regular, 2 for ondemand
	 * @throws ServiceException 
	 * 
	 */
	public long launchVSphereCatalogJob(long jobId, long type,String vmInstanceUUID) throws ServiceException {
		try {
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(vmInstanceUUID);			
			VMBackupConfiguration vmConf = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if(vmConf == null) {
				logger.error("No vm backup cofniguration for " + vmInstanceUUID + ", no need to start catalog");
				return -1;
			}
			BackupVM backupVM = vmConf.getBackupVM();
			return this.getNativeFacade().lauchCatalogJob(jobId, type, vmInstanceUUID, 
					backupVM.getDestination(), backupVM.getDesUsername(), backupVM.getDesPassword());			
		}catch(Exception se){
			throw generateInternalErrorAxisFault();
		}
	}
	
	public long launchVSphereCatalogJob(long id, long type, String vmInstanceUUID, String destination, 
			String userName, String password) throws ServiceException {
		if(destination == null || destination.isEmpty()){
			return launchVSphereCatalogJob(id, type, vmInstanceUUID);
		}else
			return this.getNativeFacade().lauchCatalogJob(id, type, vmInstanceUUID, 
				destination, userName, password);
	}
	
	public void startCatalogJob(long jobId, String vmInstanceUUID, long jobType){
		startCatalogJob(jobId, 0, -1, vmInstanceUUID, jobType);
	}
	
	public synchronized long submitFSOnDemandJob(CatalogJobPara para, String vmInstanceUUID) throws ServiceException {
		vmInstanceUUID=checkVMInstanceUUID(para.getBackupDestination(), para.getUserName(), para.getPassword(), vmInstanceUUID);
		
		
		if (!StringUtil.isEmptyOrNull(para.getRpsServerName())){
			para.setCurrentAgentUUID(vmInstanceUUID);
			return submitFSOnDemandJobToRPS(para);
		} else{
			if(para.getCurrentCatalogStatus() == CatalogService.FSCAT_DISABLED){
				try {
					int ret = BrowserService.getInstance().generateCatalogOnDemand(para.getSessionNumber(), 
							para.getBackupDestination(), para.getUserName(), para.getPassword(), vmInstanceUUID);
					if(ret == 1) {
						this.startCatalogJob(-1, 0, CatalogQueueType.MAKEUP_JOB, vmInstanceUUID, 
								para.getBackupDestination(), para.getUserName(), para.getPassword(), JobType.JOBTYPE_VM_CATALOG_FS_ONDEMAND);
					}
				}catch(Exception e) {
					logger.error("Failed to move catalog job script " + e.getMessage());
					return 1;
				}
			}else if(para.getCurrentCatalogStatus() == CatalogService.FSCAT_FAIL) {
				return CatalogService.getInstance().submitFSCatalogJob(para, vmInstanceUUID);
			}else {
				logger.warn("Don't need to run ondemand catalog for catalog status " 
						+ para.getCurrentCatalogStatus());
			}
		}
		return 0;
	}
	
	public synchronized long submitFSOnDemandJobToRPS(CatalogJobPara para) {
		RpsHost host = new RpsHost();

		host.setRhostname(para.getRpsServerName());
		host.setUsername(para.getRpsUserName());
		host.setPassword(para.getRpsPassword());
 
		if (para.getRpsPort() == 0)
			host.setPort(8014);
		else
			host.setPort(para.getRpsPort());
		
		host.setHttpProtocol(para.isRpsHttp());

		return JobService.getInstance().submitFSOnDemandCatalog(para, host);
	}
	
	private void startMergeJobForNoCatalog(String vmInstanceUUID){
		//no need to start catalog, start merge job
		logger.debug("no need to start catalog, start merge job");
		try {
			VSphereMergeService.getInstance()
			.resumeVMMerge(AbstractMergeService.MergeEvent.OTHER_JOB_END, 
					vmInstanceUUID);
		}catch(Exception e) {
			logger.error("Failed to start merge job for " + vmInstanceUUID);
		}
	}

	/**
	 * check if UUID is vmInstanceUUID. If it is vmUUID, replace it with vmInstanceUUID
	 * @param backupDest
	 * @param username
	 * @param password
	 * @param vmInstanceUUID
	 * @return
	 */
	private String checkVMInstanceUUID(String backupDest,String username, String password, String vmInstanceUUID) {
		String domain = "";
		if(username!=null){
			int indx = username.indexOf('\\');
			if (indx > 0) {
				domain = username.substring(0, indx);
				username = username.substring(indx + 1);
			}
		}
		try {
			JBackupVM vm=getNativeFacade().getBackupVM(backupDest, domain, username, password);
			if(!vm.getInstanceUUID().equals(vmInstanceUUID)){
				logger.info("vmInstanceUUID is different to VMInfo.xml, so replace it with "+vm.getInstanceUUID());
			}
			return vm.getInstanceUUID();
		} catch (ServiceException e) {
			logger.error("fail to get backuVM: "+backupDest,e);
		}

		return vmInstanceUUID;
	}
	
	public long runCatalogNow(CatalogJobArg jobArg) throws ServiceException {
		if (handleErrorFromRPS(jobArg) == -1)
			return -1;
		
		String vmInstanceUUID = jobArg.getD2dServerUUID();
		if(VSphereCatalogJob.isJobRunning(vmInstanceUUID)){
			logger.info("Catalog job for " + vmInstanceUUID + " is already scheduled");
			return -1;
		}
		
		if(BaseVSphereJob.isJobRunning(vmInstanceUUID,String.valueOf(Constants.AF_JOBTYPE_VM_CATALOG_FS))
				|| BaseVSphereJob.isJobRunning(vmInstanceUUID,String.valueOf(Constants.JOBTYPE_CATALOG_GRT))
				|| BaseVSphereJob.isJobRunning(vmInstanceUUID,String.valueOf(Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND))){
			logger.info("Vsphere Catalog job is running, no need to schedule it again:"+vmInstanceUUID);
			return -1;
		}
		
		try {
			JobDetail jobDetail = new JobDetailImpl(jobArg.getJobDetailName() + "Now", JOB_GROUP_CATALOG_NAME, VSphereCatalogJob.class);
			jobDetail.getJobDataMap().put(JOB_ID, jobArg.getJobId());
			jobDetail.getJobDataMap().put(CatalogService.WAIT_TIME, 0L);
			jobDetail.getJobDataMap().put(CatalogService.QUEUE_TYPE, jobArg.getQueueType());
			jobDetail.getJobDataMap().put(CatalogService.JOB_TYPE, jobArg.getJobType());
			jobDetail.getJobDataMap().put(CatalogService.DESTINATION, jobArg.getDestination());
			jobDetail.getJobDataMap().put(CatalogService.USERNAME, jobArg.getUserName());
			jobDetail.getJobDataMap().put(CatalogService.PASSWORD, jobArg.getPassword());
			jobDetail.getJobDataMap().put("vmInstanceUUID", vmInstanceUUID);
			jobDetail.getJobDataMap().put(Constants.RUN_NOW, Boolean.TRUE);
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			trigger.setName(jobArg.getJobDetailName() + "Now");
			getCatalogScheduler().scheduleJob(jobDetail, trigger);
			return 0;
		}catch(SchedulerException e){
			logger.error("Failed to scheduler Vsphere catalog jobs", e);
			return -1;
		}
	}

	@Override
	public boolean needRun(JobDependencySource source) {
		if(source.getJobType() == JobType.JOBTYPE_VM_BACKUP
				&& source.getJobStatus() == JobStatus.BackupJob_PROC_EXIT)
			if(this.IsCatalogAvaiable(CatalogQueueType.MAKEUP_JOB, source.getClientUUID()) != 0
				|| IsCatalogAvaiable(CatalogQueueType.REGULAR_JOB, source.getClientUUID()) != 0)
				return true;
		else if(source.getJobType() == JobType.JOBTYPE_VM_CATALOG_FS
				|| source.getJobType() == JobType.JOBTYPE_VM_CATALOG_FS_ONDEMAND){
			if(source.getJobStatus() != JobStatus.JOBSTATUS_FINISHED)
				return false;
			
			if(this.IsCatalogAvaiable(CatalogQueueType.MAKEUP_JOB, source.getClientUUID()) != 0
					|| IsCatalogAvaiable(CatalogQueueType.REGULAR_JOB, source.getClientUUID()) != 0)
				return true;
		}
		return false;
	}
	
	public void resumeCatalogJob(long jobId, String vmInstanceUUID){
		if(VSphereCatalogJob.isJobRunning(vmInstanceUUID)){
			logger.info("Catalog job for " + vmInstanceUUID + " is already scheduled");
			return;
		}
		
		if(BaseVSphereJob.isJobRunning(vmInstanceUUID,String.valueOf(Constants.AF_JOBTYPE_VM_CATALOG_FS))
				|| BaseVSphereJob.isJobRunning(vmInstanceUUID,String.valueOf(Constants.JOBTYPE_CATALOG_GRT))
				|| BaseVSphereJob.isJobRunning(vmInstanceUUID,String.valueOf(Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND))){
			logger.info("Vsphere Catalog job is running, no need to schedule it again:"+vmInstanceUUID);
			return;
		}
		
		try {
			String jobName = JOB_NAME_CATALOG+ vmInstanceUUID + System.currentTimeMillis();
			JobDetail jobDetail = new JobDetailImpl(jobName, JOB_GROUP_CATALOG_NAME, VSphereCatalogJob.class);
			jobDetail.getJobDataMap().put(CatalogService.JOB_ID, jobId);
			jobDetail.getJobDataMap().put(CatalogService.WAIT_TIME, 0L);
			jobDetail.getJobDataMap().put(CatalogService.QUEUE_TYPE, -1L);
			jobDetail.getJobDataMap().put(CatalogService.DESTINATION, null);
			jobDetail.getJobDataMap().put(CatalogService.USERNAME, null);
			jobDetail.getJobDataMap().put(CatalogService.PASSWORD, null);
			jobDetail.getJobDataMap().put("vmInstanceUUID", vmInstanceUUID);
			jobDetail.getJobDataMap().put(CatalogService.RESUMED, Boolean.TRUE);
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			trigger.setName(jobName);
			getCatalogScheduler().scheduleJob(jobDetail, trigger);
		}catch(SchedulerException e){
			logger.error("Failed to scheduler Vsphere catalog jobs", e);
		}
	}
	
	
	/**
	 * add new grt catalog information
	 * @param backupDestination
	 * @param userName
	 * @param password
	 * @param sessionNumber
	 * @param subSessionNumber
	 * @param sessionPassword
	 * @return
	 * @throws ServiceException
	 */
	public synchronized long submitCatalogJob(CatalogJobPara catalogJobPara) throws ServiceException	{
		if (disabledPlan(catalogJobPara.getVmInstanceUUID()))
			throw generateAxisFault(FlashServiceErrorCode.Backup_BackupDisabled);
		
		logger.debug("submitCatalogJob() - start");
//		if(!CommonService.getInstance().checkLicense(CommonService.EX_GR_LIC)) {
//			String msg = WebServiceMessages.getResource("LicenseGRT");
//			throw new ServiceException(msg, FlashServiceErrorCode.Common_License_Failure);
//		}
		//String[] pass = this.getNativeFacade().getSessionPasswordBySessionGuid(new String[] {sessionGUID});
		//String sessionPassword = pass.length == 0 ? null : pass[0];		
		// CONN_INFO info = BackupService.getInstance().getCONN_INFO(BackupService.getInstance().getBackupConfiguration());
		//long ret = WSJNI.addGRTCatalogInfo(backupDestination, info.getUserName(), info.getPwd(), info.getDomain(), 
		//		sessionNumber, subSessionNumber, sessionPassword);
		
		if (catalogJobPara.getSessionNumber()<=0)
			throw new ServiceException(FlashServiceErrorCode.Common_Invalid_SessionNum);		
		
		if (catalogJobPara.getSubSessionNumber()<=0)
			throw new ServiceException(FlashServiceErrorCode.Common_Invalid_SubSessionNum);
		
		if(CommonService.isCatalogJobInQueue(CatalogQueueType.REGULAR_JOB,  
				catalogJobPara.getSessionNumber(), catalogJobPara.getVmInstanceUUID())){
			logger.info("The job script is already in regular job queue, will start it directly");
			VSphereCatalogJob makeupJob = getMakeupJob(catalogJobPara.getVmInstanceUUID());
			if(makeupJob != null)
				makeupJob.start();
			else
				startCatalogJob(-1, 0, CatalogQueueType.REGULAR_JOB, catalogJobPara.getVmInstanceUUID(), catalogJobPara.getBackupDestination(), 
						catalogJobPara.getUserName(), catalogJobPara.getPassword(), Constants.JOBTYPE_CATALOG_GRT);
			return 0;
		}else if(CommonService.isCatalogJobInQueue(CatalogQueueType.ONDEMAND_JOB, 
				catalogJobPara.getSessionNumber(), catalogJobPara.getVmInstanceUUID())){
			logger.info("The job script is already in on-demand job queue, will start it directly");
			startOnDemandCatalogJob(-1, catalogJobPara.getBackupDestination(), 
					catalogJobPara.getUserName(), catalogJobPara.getPassword(), catalogJobPara.getVmInstanceUUID());
			return 0;
		}else {
			long ret = WSJNI.addGRTCatalogInfo(
					catalogJobPara.getBackupDestination(), 
					catalogJobPara.getUserName(), 
					catalogJobPara.getPassword(), 
					"",
					catalogJobPara.getSessionNumber(),
					catalogJobPara.getSubSessionNumber(),
					catalogJobPara.getEncryptionPassword(),
					catalogJobPara.getGrtEdbList(),
					catalogJobPara.getVmInstanceUUID());
	
			//This API need a return value, if the return value is valid, then start catalog job
			if(ret == 0){
				logger.info("saved successfully, launch catalog job");
				checkForMergeRunning(ServiceUtils.jobType2String(Constants.JOBTYPE_CATALOG_GRT, 0));
				startOnDemandCatalogJob(-1, catalogJobPara.getBackupDestination(), 
						catalogJobPara.getUserName(), catalogJobPara.getPassword(), catalogJobPara.getVmInstanceUUID());
			}else 
				logger.warn("Save grt catalog information failed, error code " + ret);
			return ret;
		}
	}
	
	private boolean disabledPlan(String vmInstanceUUID) {
		try {
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(vmInstanceUUID);
			VMBackupConfiguration vmConf = VSphereService.getInstance().getVMBackupConfiguration(vm);
		    if (vmConf!=null && vmConf.isDisablePlan()) {
			    logger.info("The plan is disabled, do not run catalog job");
			    return true;
		    }
		    else 
		    	return false;
		}
		catch (ServiceException e) {
			logger.error("Can not get vm backup configuration");
		}
		
		return false;
	}
	
	
	/**
	 * schedule an on demand catalog job
	 * @param jobID jobID: for common situations, it's -1, 
	 * 		  for resume job after restart it's the value get from backend
	 */
	public void startOnDemandCatalogJob(long jobID, String destination, 
			String userName, String password, String vmInstanceUUID){
		if (disabledPlan(vmInstanceUUID))
			return;
		
		if(VSphereOndemandCatalogJob.isJobRunning(vmInstanceUUID)){
			logger.info("On-demand Catalog job is running, no need to schedule it again");
			return;
		}else if(jobID <= 0 && IsCatalogAvaiable(CatalogQueueType.ONDEMAND_JOB, vmInstanceUUID) == 0){
			return;
		}
		
		try {
			JobDetail jobDetail = new JobDetailImpl(JOB_NAME_OD_CATALOG, JOB_GROUP_CATALOG_NAME, VSphereOndemandCatalogJob.class);
			jobDetail.getJobDataMap().put(CatalogService.DESTINATION, destination);
			jobDetail.getJobDataMap().put(CatalogService.USERNAME, userName);
			jobDetail.getJobDataMap().put(CatalogService.PASSWORD, password);
			jobDetail.getJobDataMap().put(CatalogService.JOB_ID, jobID);
			jobDetail.getJobDataMap().put(CatalogService.JOB_TYPE, JobType.JOBTYPE_CATALOG_GRT);
			jobDetail.getJobDataMap().put(CatalogService.QUEUE_TYPE, CatalogQueueType.ONDEMAND_JOB);
			jobDetail.getJobDataMap().put("vmInstanceUUID", vmInstanceUUID);			
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			trigger.setName(JOB_NAME_OD_CATALOG);
			getCatalogScheduler().scheduleJob(jobDetail, trigger);
		}catch(SchedulerException e){
			logger.error("Failed to scheduler on-demand catalog job", e);
		}
	}
}

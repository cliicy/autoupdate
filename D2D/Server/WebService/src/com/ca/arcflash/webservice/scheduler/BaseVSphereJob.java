package com.ca.arcflash.webservice.scheduler;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.impl.JobDetailImpl;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jni.common.JJobHistory;
import com.ca.arcflash.jobscript.base.GenerateType;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.service.common.FlashSyncher;
import com.ca.arcflash.service.jni.model.JRestorePoint;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.RPSDataStoreInfo;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.job.rps.BaseJobArg;
import com.ca.arcflash.webservice.data.job.rps.BaseJobArgWithSourceInfo;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.datasync.job.VSphereBackupJobSyncMonitor;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation.EVENT_TYPE;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyCheckStatus;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyQueryStatus;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;
import com.ca.arcflash.webservice.jni.model.JBackupInfoSummary;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.jni.model.JVMJobMonitorDetail;
import com.ca.arcflash.webservice.service.AbstractMergeService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.JobMonitorService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSPhereCatalogService;
import com.ca.arcflash.webservice.service.VSphereBackupSetService;
import com.ca.arcflash.webservice.service.VSphereMergeService;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.RSSItemXMLDAO;
import com.ca.arcflash.webservice.service.internal.VSphereJobContext;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.RSSItem;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public abstract class BaseVSphereJob extends BaseJob{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(BaseVSphereJob.class);
	
	//in log.h  
	/*
	 * #define AFINFO		0
	   #define AFWARNING	1
	   #define AFERROR		2
	*/
	//fix issue 18712980
	
	protected int scheduletype=0;//ADD
	
	public final static int JOBSTATUS_VSPHERE_LICENSE_FAIL = (int) EVENT_TYPE.VSPHERE_LICENSE_FAIL.getValue();
	public final static int JOBSTATUS_HOST_NOT_FOUND = (int) EVENT_TYPE.VSPHERE_HOST_NOT_FOUND.getValue();
	public final static int JOBSTATUS_DATASTROE_NOT_ENOUGH = (int) EVENT_TYPE.VSPHERE_DATASTROE_NOT_ENOUGH.getValue();
	
	private RSSItemXMLDAO rssItemXMLDAO = new RSSItemXMLDAO();
	
	private VSphereJobContext jobContext;
	
//	protected final static ExecutorService vmpool = Executors.newFixedThreadPool(Integer.MAX_VALUE);
	protected static ExecutorService vmpool = null;
	private final static int  multisize = 5;
	protected static Object missVMJobLock = new Object();
	
	static {
		int maxJob = ServiceContext.getInstance().getvSphereMaxJobNum();
		if(maxJob != -1 && maxJob > 0 && multisize * maxJob > 50)
			//As we test, there may be about 3 thread to run a job
			vmpool = Executors.newFixedThreadPool(multisize * maxJob);
		else
			vmpool = Executors.newFixedThreadPool(50);		
	}
	
	@Override
	protected String getSettingsURL() {
		return getServerURL();
	}
	
	public VSphereJobContext getJobContext() {
		return jobContext;
	}


	public void setJobContext(VSphereJobContext jobContext) {
		this.jobContext = jobContext;
	}
	
	public static void shutDownThreadPool() {
		vmpool.shutdownNow();
	}

	@Override
	protected void addMissedJobHistory(long jobType, String dataStoreUUID, long datastoreVersion, long jobMethod, int periodRetentionFlag) {
		JJobHistory jobHistory = new JJobHistory();
		jobHistory.setJobId(shrmemid);
		jobHistory.setJobType((int)jobType);
		jobHistory.setJobStatus(JobStatus.JOBSTATUS_MISSED);
		jobHistory.setJobRunningNodeUUID(jobContext.getLauncherInstanceUUID());		
		jobHistory.setJobDisposeNodeUUID(jobContext.getExecuterInstanceUUID());
		jobHistory.setJobDisposeNode(jobContext.getVmName());
		jobHistory.setDatastoreUUID(dataStoreUUID);
		jobHistory.setDatastoreVersion(datastoreVersion);
		jobHistory.setTargetUUID(getRPSServerID());
		jobHistory.setPlanUUID(isOnDemand ? "" : getPlanUUID(jobContext
				.getExecuterInstanceUUID()));
		jobHistory.setPeriodRetentionFlag(periodRetentionFlag);
		VSphereService.getInstance().getNativeFacade().addMissedJobHistory( jobHistory );
		
	}

	protected void getJobArg(JobDetailImpl jobDetail,
			String policyUUID,
			String datastoreUUID,
			long jobType, BaseJobArg jobArg, String vmInstanceUUID,
			String vmName, String esxName, String dataStoreName) {
		jobArg.setJobDetailName(jobDetail.getName());
		jobArg.setJobDetailGroup(jobDetail.getGroup());
		jobArg.setPolicyUUID(policyUUID);
		jobArg.setDataStoreUUID(datastoreUUID);
		Object jobName = jobDetail.getJobDataMap().get("jobName");
		if(jobName != null){
			jobArg.setJobName((String)jobName);
		}
		jobArg.setVM(true);
		if(!StringUtil.isEmptyOrNull(esxName))
			jobArg.setD2dServerName(vmName + "@" + esxName);
		else
			jobArg.setD2dServerName(vmName);
		jobArg.setD2dServerUUID(vmInstanceUUID);
		jobArg.setDataStoreName(dataStoreName);
		jobArg.setJobType(jobType);		
		jobArg.setJobId(shrmemid);
	}
	
	protected void getJobArgWithSrc(JobDetail jobDetail,
			String policyUUID,
			String datastoreUUID,
			long jobType, RpsHost rpsHost, BaseJobArgWithSourceInfo jobArg,
			String vmInstanceUUID,String vmName, String esxName, String dataStoreName){
		getJobArg((JobDetailImpl)jobDetail, policyUUID, datastoreUUID, jobType, jobArg,
				vmInstanceUUID, vmName, esxName, dataStoreName);
		jobArg.setD2dLoginUUID(CommonService.getInstance().getLoginUUID());
		jobArg.setLocalD2DName(ServiceContext.getInstance().getLocalMachineName());
		jobArg.setLocalD2DPort(CommonService.getInstance().getServerPort());
		jobArg.setLocalD2DProtocol(CommonService.getInstance().getServerProtocol());
		jobArg.setSrcRps(rpsHost);
		jobArg.setIpList(CommonService.getInstance().getNativeFacade().getD2DIPList());
		jobArg.setOnDemand(isOnDemand);
	}
	
	@Override
	protected void logErrorAct(NativeFacade nativeFacade, String message) {
		if(!StringUtil.isEmptyOrNull(message)){
			nativeFacade.addVMLogActivityWithJobID(Constants.AFRES_AFALOG_ERROR,
					shrmemid,
					Constants.AFRES_AFJWBS_GENERAL, 
					new String[]{message, "","","",""}, jobContext.getExecuterInstanceUUID());
		}
	}
	
	private int CheckBackupConfigSettingWithEdge(String uuid) throws ServiceException{
		// wait at most about (times*millis/1000) seconds
		int times=20;
		long millis=30000;
		while(times-->0){
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
			}
			EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.vShpereManager);
			if(edgeRegInfo==null)
				return PolicyQueryStatus.OK;
			String policyUuid=edgeRegInfo.getPolicyUuids().get(uuid);
			if(policyUuid==null || policyUuid.isEmpty())
				return PolicyQueryStatus.OK;
			else{
				policyUuid = policyUuid.split(":")[0];
			}

			int status = VSphereService.getInstance().checkPolicyFromEdge(uuid, policyUuid, true);
			
			switch(status){
			case PolicyCheckStatus.UNKNOWN:
			case PolicyCheckStatus.SAMEPOLICY:
				return PolicyQueryStatus.OK;
			case PolicyCheckStatus.NOPOLICY:
			case PolicyCheckStatus.POLICYFAILED:
				return PolicyQueryStatus.FAIL;
			case PolicyCheckStatus.DIFFERENTPOLICY:
			case PolicyCheckStatus.POLICYDEPLOYING:
				continue;
			}
		}
		// allow continue even timeout
		return PolicyQueryStatus.TIMEOUT;
	}

	protected boolean IsContinueJob(){
		boolean continueFlag = false;
		
		//Refresh VM management status with Edge VSPhere
		VSphereService svc = VSphereService.getInstance();
		try {
			List<VirtualMachine> ManagedVMs = svc.RefreshBackupConfigSettingWithEdge();
			Iterator<VirtualMachine> iter = ManagedVMs.iterator();
			while(iter.hasNext()) {
				VirtualMachine vm = iter.next();
				if(vm.getVmInstanceUUID().equals(jobContext.getExecuterInstanceUUID())) {
					EdgeRegInfo info = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.vShpereManager);
					String policyUuid = "UNKNOWNPOLICY";
					if(info.getPolicyUuids().containsKey(vm.getVmInstanceUUID())){
						policyUuid = info.getPolicyUuids().get(vm.getVmInstanceUUID());
					}
					if(policyUuid!=null && !policyUuid.isEmpty()){
						policyUuid = policyUuid.split(":")[0];
					}
					int state=svc.checkPolicyFromEdge(vm.getVmInstanceUUID(), policyUuid, false);
					switch(state){
					case PolicyCheckStatus.UNKNOWN:
					case PolicyCheckStatus.SAMEPOLICY:
						continueFlag = true;
						break;
					case PolicyCheckStatus.NOPOLICY:
						VSphereService.getInstance().getNativeFacade().addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_GENERAL,
								new String[]{WebServiceMessages.getResource("autoUnassignPolicy"), "","","",""},vm.getVmInstanceUUID());
						logger.warn(WebServiceMessages.getResource("autoUnassignPolicy"));
						svc.detachVSpherePolicy(new VirtualMachine[]{vm});
						continueFlag = false;
						break;
					case PolicyCheckStatus.DIFFERENTPOLICY:
						VSphereService.getInstance().getNativeFacade().addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_GENERAL,
								new String[]{WebServiceMessages.getResource("autoRedeployPolicy"), "","","",""},vm.getVmInstanceUUID());
						logger.warn(WebServiceMessages.getResource("autoRedeployPolicy"));
						int r=CheckBackupConfigSettingWithEdge(vm.getVmInstanceUUID());
						if(r==PolicyQueryStatus.FAIL){
							VSphereService.getInstance().getNativeFacade().addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_GENERAL,
									new String[]{WebServiceMessages.getResource("autoRedeployPolicySkip"), "","","",""},vm.getVmInstanceUUID());
							logger.warn(WebServiceMessages.getResource("autoRedeployPolicySkip"));
						}
						continueFlag = true;
						break;
					case PolicyCheckStatus.POLICYDEPLOYING:
						r=CheckBackupConfigSettingWithEdge(vm.getVmInstanceUUID());
						continueFlag = true;
						break;
					case PolicyCheckStatus.POLICYFAILED:
						VSphereService.getInstance().getNativeFacade().addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_GENERAL,
								new String[]{WebServiceMessages.getResource("autoRedeployFailedPolicy"), "","","",""},vm.getVmInstanceUUID());
						logger.warn(WebServiceMessages.getResource("autoRedeployFailedPolicy"));
						r=CheckBackupConfigSettingWithEdge(vm.getVmInstanceUUID());
						if(r==PolicyQueryStatus.FAIL){
							VSphereService.getInstance().getNativeFacade().addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_GENERAL,
									new String[]{WebServiceMessages.getResource("autoRedeployFailedPolicySkip"), "","","",""},vm.getVmInstanceUUID());
							logger.warn(WebServiceMessages.getResource("autoRedeployFailedPolicySkip"));
						}
						continueFlag = true;
						break;
					}
					break;
				}
			}
		} catch (ServiceException e1) {
			logger.debug(e1);
			continueFlag = true;
		} catch (Exception e2) {
			logger.debug(e2);
			continueFlag = true;
		}
		
		if(continueFlag == false) {
			logger.debug("This job on VM instance " + jobContext.getExecuterInstanceUUID() + 
					" cannot continue since it has not been protected under this D2D proxy!");
		}
		
		return continueFlag;
	}

	public static void resumeJobAfterRestart(VSphereJobContext context) {
		logger.debug("resumeJobAfterRestart - start, type is " + 
				context.getJobType() + ", Id is " + context.getJobId());
		switch(new Long(context.getJobType()).intValue()) {
		case Constants.AF_JOBTYPE_VM_BACKUP:
		case Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP:
			new VSphereBackupJob().resumeMonitorAfterRestart(context, null, Constants.AF_JOBTYPE_VM_BACKUP);
			break;
		case Constants.AF_JOBTYPE_HYPERV_VM_BACKUP:
		//case Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP:
			new VSphereBackupJob().resumeMonitorAfterRestart(context, null, Constants.AF_JOBTYPE_HYPERV_VM_BACKUP);
			break;
		case Constants.AF_JOBTYPE_VM_RECOVERY:
		case Constants.AF_JOBTYPE_VMWARE_VAPP_RECOVERY:
		case Constants.AF_JOBTYPE_HYPERV_CLUSTER_RECOVERY:
			new VSphereRecoveryJob().resumeMonitorAfterRestart(context, null, Constants.AF_JOBTYPE_VM_RECOVERY);
			break;
		case Constants.AF_JOBTYPE_RESTORE:
			if(context.getJobLauncher() == VSphereJobContext.JOB_LAUNCHER_VSPHERE){
				new VSphereRestoreJob().resumeMonitorAfterRestart(context, null, Constants.AF_JOBTYPE_RESTORE);
			}
			break;
		case Constants.AF_JOBTYPE_COPY:
			if(context.getJobLauncher() == VSphereJobContext.JOB_LAUNCHER_VSPHERE){
				new VSphereCopyJob().resumeMonitorAfterRestart(context, null, Constants.AF_JOBTYPE_COPY);
			}
			break;
		case Constants.AF_JOBTYPE_VM_CATALOG_FS:
		case Constants.JOBTYPE_CATALOG_GRT:
		case Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND:
			if(context.getJobLauncher() == VSphereJobContext.JOB_LAUNCHER_VSPHERE){
				VSPhereCatalogService.getInstance().resumeCatalogJob(context.getJobId(),context.getExecuterInstanceUUID());
			}
			break;
		default:
			logger.warn("vSphere don't support resume job other job" + context.getJobType());
			break;
		}
		
		logger.debug("resumeJobAfterRestart(JJobScript) - end");
	}
	
	/**
	 * resume monitor after restart
	 * 
	 * 
	 */
	protected boolean resumeMonitorAfterRestart(VSphereJobContext context,Observer[] observers, int jobType) {
		logger.debug("resumeMonitorAfterRestart(JJobScript) - start");

		try {
		  shrmemid = context.getJobId();
		  logger.info("resume jobid(shrmemid):" + shrmemid);		  
          this.jobContext = context;
          if(observers != null){
			for(Observer observer : observers){
	        	addObserver(observer);
	        }
          }
          pauseMergeJob(jobType);
          vmpool.submit(new VSphereJobMonitorThread(this,this.jobContext));
		}
		catch (RuntimeException e) {
			releaseJobLock();
			throw e;
		}

		logger.debug("resumeMonitorAfterRestart(JJobScript) - end");
		return true;
	}
	
	
	/**
	 * Start job monitor thread.
	 * 
	 * @param true no other job is running.
	 */
	protected boolean preprocess(JJobScript js,Observer[] observers) {
		logger.debug("preprocess(JJobScript) - start");

		if(!getJobLock())
			return false;
		try {
          js.setUlJobID(shrmemid);
          
          JJobHistory jobHistory = new JJobHistory();
          jobHistory.setJobId(shrmemid);
          jobHistory.setJobType( (int)getDefaultJobType() );
          jobHistory.setJobRunningNodeUUID(jobContext.getLauncherInstanceUUID());
          jobHistory.setJobDisposeNodeUUID(jobContext.getExecuterInstanceUUID());
          jobHistory.setJobDisposeNode(jobContext.getVmName());
          jobHistory.setDatastoreUUID(getRPSDatastoreUUID());
          RPSDataStoreInfo rpsDataStoreInfo = getRpsDataStoreInfo(getRPSDatastoreUUID());
          jobHistory.setDatastoreVersion(rpsDataStoreInfo.getVersion());
          jobHistory.setTargetUUID(rpsDataStoreInfo.getRpsServerId());
          jobHistory.setPlanUUID(isOnDemand ? "" : getPlanUUID(jobContext.getExecuterInstanceUUID()));          
          VSphereService.getInstance().getNativeFacade().updateJobHistory(jobHistory);
          
          if(observers != null){
			for(Observer observer : observers){
	        	addObserver(observer);
	        }
          }
          pauseMergeJob(js != null ? js.getUsJobType() : 0);
          vmpool.submit(new VSphereJobMonitorThread(this,this.jobContext));
		}
		catch (RuntimeException e) {
			releaseJobLock();
			throw e;
		}

		logger.debug("preprocess(JJobScript) - end");
		return true;
	}

	@Override
	protected void pauseMergeJob(int jobType) {
		try {
//			VSphereMergeService.getInstance().pauseMerge(false, 
//		      		  jobContext.getExecuterInstanceUUID(), false);
			String strType = ServiceUtils.jobType2String(jobType);
			VSphereMergeService.getInstance().pauseMerge(
					AbstractMergeService.MergeEvent.OTHER_JOB_START, 
		      		  jobContext.getExecuterInstanceUUID(),
		      		  strType,
		      		  this);
			VSphereMergeService.getInstance()
				.waitForJobEnd(jobContext.getExecuterInstanceUUID());
		}catch(Exception e) {
			logger.error("Failed to pause merge job", e);
		}
	}
   	public static synchronized boolean isJobRunning(String vmIndentification,String jobType)
	{
		Map<Long,JobMonitor> jobMonitorMap = JobMonitorService.getInstance().getVMJobMonitorMapByJobType(vmIndentification,jobType);
		if(jobMonitorMap == null || jobMonitorMap.isEmpty()){
			return false;
		}else{
			return true;
		}
	}
   	
   	public static String getEdgeUrl(){
		D2DEdgeRegistration registration = new D2DEdgeRegistration();
		EdgeRegInfo regInfo = registration.getEdgeRegInfo(ApplicationType.CentralManagement);
		String url = "";
		if(regInfo !=null){
			url = regInfo.getConsoleUrl();
//			String edgeWSDL = regInfo.getEdgeWSDL();
//			if(edgeWSDL!=null){
//				url = edgeWSDL.split("services")[0];
//			}
		}
		return url;
	}
    
	protected class VSphereJobMonitorThread extends JobMonitorThread {
		/**
		 * Logger for this class
		 */
		private final Logger logger = Logger.getLogger(JobMonitorThread.class);
		
		VSphereJobContext jobContext;
		
		public VSphereJobMonitorThread(BaseVSphereJob job,VSphereJobContext jobContext){
			super(job);
			this.jobContext = jobContext;			
		}
		
		protected void optionalUpdate(JJobMonitor jJM){
			VSphereBackupJobSyncMonitor.getInstance().addSyncData(jobContext.getExecuterInstanceUUID(), jJM, shrmemid);
		}
		
		protected void getRPSInfo(){
			BackupRPSDestSetting policy = 
					VSphereService.getInstance().getRpsSetting(jobContext.getExecuterInstanceUUID());
			if(policy == null)
				return;
			job.dedupeEnabled = policy.isDedupe();
			
			if (job.js != null) {
				switch (job.js.getUsJobType()) {
				case Constants.AF_JOBTYPE_COPY:
					job.dedupeEnabled = false;
					break;
				default:
					break;
				}
			}	
			
			/*if(!StringUtil.isEmptyOrNull(rpsPolicyUUID) 
					&& !StringUtil.isEmptyOrNull(rpsDataStoreUUID))
				return;
			
			if(StringUtil.isEmptyOrNull(rpsPolicyUUID))
				rpsPolicyUUID = policy.getRPSPolicyUUID();
			if(StringUtil.isEmptyOrNull(rpsDataStoreUUID))
				rpsDataStoreUUID = policy.getRPSDataStore();*/
		}
		
		protected JJobMonitor startMonitor(){
			logger.info("Start vSphere backup job monitor.");
			JJobMonitor jJM = null;
			startTime = System.currentTimeMillis();
			int optionalUpdateCount = 0;
			getRPSInfo();
			while (true) {
				try{
					if (isStopJM) {
						break;
					}

					if((jJM = getJobMonitor(jJM)) == null){
						break;
					}
					if (jJM != null){
						updateJobMonitor(jJM,jobContext);
						if (jJM.getUlJobType() == Constants.AF_JOBTYPE_VM_BACKUP || 
							jJM.getUlJobType() == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
							jJM.getUlJobType() == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP ||
							jJM.getUlJobType() == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP) {
							optionalUpdate(jJM);
							++optionalUpdateCount;
						}
						if (job.isJobDone(jJM.getUlJobPhase(),jJM.getUlJobStatus())) {
							Thread.sleep(5 * 1000);
							logger.info("Remove job monitor for " + jJM.getUlJobType() + " id is " 
									+ job.shrmemid + " JOBPHASE = " + jJM.getUlJobPhase()
									+ " Job status is: " + jJM.getUlJobStatus());
							break;
						}
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						logger.error("run()", e);
						
					}
				}catch(Throwable e){
					logger.error("get job monitor", e);
				}
			}
			optionalUpdate(null);
			
			logger.info("End of vSphere backup job monitor, update count = " + optionalUpdateCount);
			
			return jJM;
		}
		
		protected void fixRecoverySet(JJobMonitor jJM) {
			//if current job is full backup, we may need to update reovery set.
			if(jJM != null 
					&& (jJM.getUlJobType() == Constants.AF_JOBTYPE_BACKUP || 
					jJM.getUlJobType() == Constants.AF_JOBTYPE_VM_BACKUP || 
					jJM.getUlJobType() == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
					jJM.getUlJobType() == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP ||
					jJM.getUlJobType() == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP) &&
					jJM.getUlJobMethod() == BackupType.Full	&&
					jJM.getUlJobStatus() == Constants.BackupJob_PROC_EXIT){
				VSphereBackupSetService.getInstance().fixRecoverySetForNewFull(
						jJM.getUlBackupStartTime(), 
						jobContext.getLauncherInstanceUUID());
			}
		}
		
		protected void afterComplete(JJobMonitor jJM) {
			synchronized (job.phaseLock) {
				 if(jJM!=null) //&& jJM.getUlJobPhase() == JobExitPhase)
				 {
					 job.jobStatus = jJM.getUlJobStatus();
				 }
				 //we must set the jobPhase to JobExitPhase, otherwise, no other place will info backup thread exits.
				 job.jobPhase = Constants.JobExitPhase;
				 job.phaseLock.notifyAll();
			 }
			
			releaseJobLock();			
			
			fixRecoverySet(jJM);
			
			startMergeJob(jJM);

			if (jJM!=null &&
			    (jJM.getUlJobPhase() == Constants.JobExitPhase ||
				 jJM.getUlJobPhase() == Constants.BackupJob_Phase_PROC_EXIT ||
				 isJobFinishedWithStatus((int)jJM.getUlJobStatus())) ||
				 jJM.getUlJobStatus() == Constants.JOBSTATUS_CANCELLED)
			{
				if((jJM.getUlJobType() == Constants.AF_JOBTYPE_VM_BACKUP || 
					jJM.getUlJobType() == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
					jJM.getUlJobType() == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP ||
					jJM.getUlJobType() == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP) 
						&& jJM.getUlJobStatus() == Constants.JOBSTATUS_SKIPPED)
					VSphereBackupJob.makeupSkippedBackup(jobContext.getExecuterInstanceUUID());
				else 
					VSphereBackupJob.makeupMissedBackup(jobContext.getExecuterInstanceUUID());
				
				try{
					sendEmail(jJM,jobContext.getExecuterInstanceUUID());
				}
				catch (Exception e)
				{	
					logger.error(e.getMessage());
				}

			}
			isStopJM = false;
		}
		
		@Override
		public void run() {
			logger.debug("run() - start");
			JJobMonitor jJM = null;
			try {
				job.serializeToDisk();
				jJM = startMonitor();
			}
			finally {
				job.removeJobSerializationFromDisk();
				cleanup(jJM);
				afterComplete(jJM);
				notify(jJM);
			}
			logger.debug("run() - end");
		}
		
		protected void cleanup(JJobMonitor jJM) {
			if(jJM != null) {
				JobMonitorService.getInstance().removeVMJobMonitor(jobContext.getExecuterInstanceUUID(),String.valueOf(jJM.getUlJobType()),shrmemid);
				if(jJM.getUlJobType() == Constants.AF_JOBTYPE_VM_RECOVERY){
					if(jobContext.getJobLauncher() == VSphereJobContext.JOB_LAUNCHER_D2D){
						JobMonitorService.getInstance().removeJobMonitor(String.valueOf(jJM.getUlJobType()),shrmemid);
					}else{
						JobMonitorService.getInstance().removeVMJobMonitor(jobContext.getLauncherInstanceUUID(),String.valueOf(jJM.getUlJobType()),shrmemid);
					}
				}else if (jJM.getUlJobType() == Constants.AF_JOBTYPE_RESTORE){
					if(jobContext.getJobLauncher() == VSphereJobContext.JOB_LAUNCHER_VSPHERE){
						JobMonitorService.getInstance().removeJobMonitor(String.valueOf(jJM.getUlJobType()),shrmemid);
					}
				}
			}
			
			try {
				if (jobMonitorHandle != 0) {
					CommonService.getInstance().getNativeFacade()
							.releaseJobMonitor(jobMonitorHandle);
					jobMonitorHandle = 0;
				}
			} catch (Throwable e) {
				logger.error("Error when release job monitor", e);
			}

			isStopJM = false;

			/*logger.info("Try to notify vsphere backup observers");
			if(job != null && jJM != null){
				try {
					Thread.sleep(4 * 1000);
					job.notifyObservers(jJM);
				}catch(Throwable e){
					logger.error("failed to notify vsphere backup observers", e);
				}

			}*/
		}
		 protected void notify(JJobMonitor jJM){
			try {
				logger.info("Try to notify observers");
				boolean isValid = isValidJobMonitor(jJM);
				if (job != null && isValid) {
					Thread.sleep(3 * 1000);
					JVMJobMonitorDetail jobMonitorDetail = new JVMJobMonitorDetail(jJM, jobContext);
					job.notifyObservers(jobMonitorDetail);
				} else {
					logger.warn("No need to notify, job/job monitor is invalid");
				}
			} catch (Throwable e) {
				logger.error("failed to notify observers", e);
			}
		}
		 
		protected void sendEmail(JJobMonitor jJM,String vmIdentification) {
			logger.debug("sendEmail - start");
			VMBackupConfiguration configuration = null;
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(vmIdentification);

			if(jJM.getUlJobType()==Constants.AF_JOBTYPE_VM_BACKUP)
				scheduletype= ((VSphereBackupJob)job).getScheduleType();
			try{
				
				configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
				if (configuration == null)
					return;
				
				BackupEmail email = configuration.getEmail();
				if (email == null)
					return;
				
				boolean sendEmail = false;
				
				if ((jJM.getUlJobStatus() == Constants.JOBSTATUS_FAILED ||
						jJM.getUlJobStatus() == Constants.JOBSTATUS_CRASH ||
						jJM.getUlJobStatus() == Constants.JOBSTATUS_CANCELLED ||
						jJM.getUlJobStatus() == Constants.JOBSTATUS_LICENSE_FAILED) && email.isEnableEmail())
				{
					logger.debug("job is failed, canceled, check recover point faild or crashed => send email");
					sendEmail = true;
				}
				else if ((jJM.getUlJobStatus() == Constants.JOBSTATUS_FINISHED || jJM.getUlJobStatus() == Constants.BackupJob_PROC_EXIT) && 
						 email.isEnableEmailOnSuccess() &&
						 (!email.isEnableEmailOnRecoveryPointCheckFailure() || 
			              email.isEnableEmailOnRecoveryPointCheckFailure() && jJM.getJobSubStatus() != Constants.JOB_SUB_STATUS_SJS_CHECK_RP_FAILED))
				{
					logger.debug("job is successful => send email");
					sendEmail = true;
				}
				else if (jJM.getJobSubStatus() == Constants.JOB_SUB_STATUS_SJS_CHECK_RP_FAILED
						 && email.isEnableEmailOnRecoveryPointCheckFailure())
				{
					logger.debug("check recover point failed, sending mail");
					sendEmail = true;
				}

				else if((jJM.getUlJobStatus() == Constants.JOBSTATUS_SKIPPED || jJM.getUlJobStatus() == Constants.JOBSTATUS_MISSED)
						&& (email.isEnableEmailOnMissedJob()))
				{
					logger.info("job is missed => send email");
					sendEmail = true;
				}
				
				if (sendEmail){
					//try to get edge url
					String url = getEdgeUrl();
					
					EmailSender emailSender = new EmailSender();
					String strJobType = EmailContentTemplate.jobType2String( getDefaultJobType(), jJM.getUlJobMethod());
					//add job status in subject to fix 18911769 
					String jobStatus = EmailContentTemplate.jobStatus2String(jJM.getUlJobStatus());
					
					if (email.isEnableEmailOnRecoveryPointCheckFailure() && 
						(jJM.getUlJobStatus() == Constants.JOBSTATUS_FINISHED || 
						 jJM.getUlJobStatus() == Constants.BackupJob_PROC_EXIT))
					    jobStatus = EmailContentTemplate.jobStatus2StringWithCRP(jJM.getUlJobStatus(), jJM.getJobSubStatus());
					String emailJobStatus = WebServiceMessages.getResource("EmailJobStatus");
					String vmName = configuration.getBackupVM().getVmName();
					String nodeName = configuration.getBackupVM().getVmHostName();
					if(nodeName==null||nodeName.isEmpty()){
						nodeName = WebServiceMessages.getResource("EmailNodeNameUnknown");
					}
					String emailSubject = WebServiceMessages.getResource("EmailSubject",
								email.getSubject(), strJobType+emailJobStatus+jobStatus ,vmName, nodeName);
					
					emailSender.setSubject(emailSubject);
					
					Date startTime = new Date();
					if(jJM.getUlBackupStartTime() != 0){
						startTime = new Date(jJM.getUlBackupStartTime());
					}
					String startString = EmailContentTemplate.formatDate(startTime);
					
					/** for email alert PR */
					
					String jobDestination = job.getDestination();
					
					if((jJM.getUlJobType() == Constants.AF_JOBTYPE_VM_BACKUP || 
							jJM.getUlJobType() == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
							jJM.getUlJobType() == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP ||
							jJM.getUlJobType() == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP || 
							jJM.getUlJobType() == Constants.AF_JOBTYPE_VM_CATALOG_FS || jJM.getUlJobType() == Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND))
						jobDestination = configuration.getDestination();
					
					
					logger.info("sendEmail scheduletype is " + scheduletype);
					
					//Create the HTML template
					if (email.isEnableHTMLFormat())
					{
						emailSender.setContent(EmailContentTemplate.getVSphereHtmlContent(jJM.getUlJobStatus(), getDefaultJobType(),
							jJM.getUlJobMethod(), scheduletype, shrmemid,  jobDestination,startString, configuration,
							getActivityLogResult(shrmemid,vm),url, jJM.getJobSubStatus()));
					}
					else
					{
						emailSender.setContent(EmailContentTemplate.getVSpherePlainTextContent(jJM.getUlJobStatus(), getDefaultJobType(),
								jJM.getUlJobMethod(), scheduletype, shrmemid,  jobDestination ,startString, configuration,
								getActivityLogResult(shrmemid,vm),url, jJM.getJobSubStatus()));
					}
					
					if(jJM.getUlJobStatus() == Constants.JOBSTATUS_FAILED ||
							jJM.getUlJobStatus() == Constants.JOBSTATUS_CRASH ||
							jJM.getUlJobStatus() == Constants.JOBSTATUS_LICENSE_FAILED ||
							jJM.getJobSubStatus() == Constants.JOB_SUB_STATUS_SJS_CHECK_RP_FAILED){
							emailSender.setHighPriority(true);
					}
					
					/** for promoting alert message  to edge server */
					emailSender.setJobStatus(jJM.getUlJobStatus());
					emailSender.setProductType(CommonEmailInformation.PRODUCT_TYPE.VSPHERE.getValue());
					
					
					//"To log on the server to make changes or fix job settings - click here. You can also submit a backup now"
					/** Alert email ehan PR */
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
					// why not use getJobType() from JJobMonitor but use getDefaultJobType(); see function updateJobMonitor(); it generate JobMonitor.jobType using getDefaultJobType(),and the JobMonitor is send to cpm using Listener;
					///this function send email and alert to CPM; so all attributions should same as JobMonitor: so the parameter of the sendEmail() function should be JobMOnitor not JJobMonitor!!; but we have no resource to change it; so we just set the difference manually
					emailSender.setJobType( getDefaultJobType() );
					emailSender.setProtectedNode( vmName );
					
					emailSender.sendEmail(email.isEnableHTMLFormat());
				}
			}catch(Exception e){
				logger.error("Error in sending email", e);
			}
			
			//FAILED JOB RSS
			if (jJM.getUlJobStatus() == Constants.JOBSTATUS_FAILED ||
					jJM.getUlJobStatus() == Constants.JOBSTATUS_CRASH ||
					jJM.getUlJobStatus() == Constants.JOBSTATUS_CANCELLED)
			{
				try {
					String url = getEdgeUrl();
					
					Date startTime = new Date(jJM.getUlBackupStartTime());	
					String startString = EmailContentTemplate.formatDate(startTime);
					String html = EmailContentTemplate.getVSphereHtmlContent(jJM.getUlJobStatus(), jJM.getUlJobType(),
							jJM.getUlJobMethod(), scheduletype, shrmemid, job.getDestination(), startString, 
							configuration, getActivityLogResult(shrmemid,vm),url, jJM.getJobSubStatus());
					
					//Save this string to a new html file					
					try {
						String folderPath = ServiceContext.getInstance().getDataFolderPath();								
						String filePath = folderPath + "\\job" + startTime.getTime() + ".html";
						String fileURL = url + "/job" + startTime.getTime()	+ ".html";
						String rssXML = folderPath + "\\jobfeed.xml";

						File file = new File(filePath);
						if (!file.exists()) {
							file.createNewFile();
						}

						FileWriter fw = null;
						try {
							fw = new FileWriter(file);
							fw.write(html);
							fw.flush();
							fw.close();							
						}
						catch (Exception e)
						{
							logger.error("Error saving error content to file for RSS ", e);
						}
						
						//if there are more than X html files, delete the last one
						File folder = new File(folderPath);
						HTMLFileFilter filter = new HTMLFileFilter();
						File[] allFiles = folder.listFiles(filter);
						
						if (allFiles.length > Constants.FAILED_JOB_RSS_MAX)
						{
							File oldest = null; 
							for (int i = 0; i < allFiles.length; i++)
							{
								if (i == 0 && allFiles[i] != null)
								{
									oldest = allFiles[i];
								}
								else if (oldest.lastModified() > allFiles[i].lastModified())
								{
									oldest = allFiles[i];
								}
							}
							if (oldest.delete())
							{
								logger.debug("Oldest RSS file = " + oldest.getName());
								rssItemXMLDAO.removeItem(rssXML, oldest.getName());
								logger.info("Successfully deleted oldest file");
							}
							else
							{
								logger.error("Failed to delete oldest failed job html file");
							}
						}
												
						RSSItem rssItem = new RSSItem();
						String jobStatus = EmailContentTemplate.jobStatus2String(jJM.getUlJobStatus());
						String jobTypeString;
						if (jJM.getUlJobType() == Constants.AF_JOBTYPE_VM_BACKUP ||
							jJM.getUlJobType() == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
							jJM.getUlJobType() == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP ||
							jJM.getUlJobType() == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP)
						{
							jobTypeString = EmailContentTemplate.backupType2String(jJM.getUlJobMethod());
						}
						else if (jJM.getUlJobType() == Constants.AF_JOBTYPE_RESTORE)
						{
							jobTypeString = WebServiceMessages.getResource("RestoreJob");	
						}
						else 
						{
							jobTypeString = WebServiceMessages.getResource("CopyJob");	
						}
						
						String emailJobStatus = WebServiceMessages.getResource("EmailJobStatus");
						String title = WebServiceMessages.getResource("EmailJobType") + jobTypeString + ", " + 
							emailJobStatus+jobStatus+" " + " ("+startString+")";
						rssItem.setTitle(title);												
						rssItem.setLink(fileURL);
						
						//Take the contents of the folder and create an RSS xml file
						rssItemXMLDAO.addRSSItem(rssXML, rssItem);						
						
						// Copy to target TOMCAT\webapps\ROOT
						String targetPath = ServiceContext.getInstance().getTomcatFilePath();
						copyRssToTarget(folderPath, targetPath, "\\jobfeed.xml", "\\job"+ startTime.getTime() + ".html");
					}
					catch (Exception e)
					{
						logger.error("Error saving error content to file for RSS - ", e);
					}					
					
				} catch (Exception e) {
					logger.error("Error in saving RSS feed - ", e);
				}
				
			}
			
			logger.debug("sendEmail - end");
		}

		
		
		private void copyRssToTarget(String srcPath, String targetPath, String rssXML, String srcFile) throws IOException {									
			StringUtil.copy(srcPath + rssXML, targetPath + rssXML);
			StringUtil.copy(srcPath + srcFile, targetPath + srcFile);
			
			File targFolder = new File(targetPath);
			File[] htmlFiles = targFolder.listFiles(new HTMLFileFilter());

			if (htmlFiles.length > Constants.FAILED_JOB_RSS_MAX) {
				File oldest = null;
				for (int i = 0; i < htmlFiles.length; i++) {
					if (i == 0 && htmlFiles[i] != null) {
						oldest = htmlFiles[i];
					} else if (oldest.lastModified() > htmlFiles[i].lastModified()) {
						oldest = htmlFiles[i];
					}
				}
				if (oldest.delete()) {
					logger.debug("Oldest RSS file = " + oldest.getName());							
					logger.info("Successfully deleted oldest file");
				} else {
					logger.error("Failed to delete oldest failed job html file");
				}
			}
		}
		

		private ActivityLogResult getActivityLogResult(long jobid,VirtualMachine vm) {
			try {
				logger.debug("getActivityLogResult - jobid = " + jobid);
				return VSphereService.getInstance().getVMJobActivityLogs(jobid, 0, 512,vm);
			} catch (Exception e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
			}
			return null;
		}
		
		protected String getD2DServerName4JobMonitor(VSphereJobContext jobContext){
			if(jobContext.getJobLauncher() == VSphereJobContext.JOB_LAUNCHER_D2D)
				return ServiceContext.getInstance().getLocalMachineName();
			else
				return jobContext.getVmName();
		}

		private void updateJobMonitor(JJobMonitor jJM,VSphereJobContext jobContext) {
			logger.debug("updateJobMonitor(JJobMonitor) - start");
			long jobType = getDefaultJobType();
			JobMonitor jmon = JobMonitorService.getInstance().getVMJobMonitorByJobTypeAndJobIdInternal(jobContext,String.valueOf(jobType),shrmemid);
			
			if(!isValidJobMonitor(jJM))
				return;
			synchronized (jmon) {
				jmon.setJobId(shrmemid);
				jmon.setBackupStartTime(jJM.getUlBackupStartTime());
				jmon.setCurrentProcessDiskName(jJM.getWszDiskName());
				jmon.setEstimateBytesDisk(jJM.getUlEstBytesDisk());
				jmon.setEstimateBytesJob(jJM.getUlEstBytesJob());
				jmon.setFlags(jJM.getUlFlags());
				jmon.setJobMethod(jJM.getUlJobMethod());
				jmon.setJobPhase(jJM.getUlJobPhase());
				jmon.setJobStatus(jJM.getUlJobStatus());
				jmon.setJobType(jobType);
//				jmon.setJobType(jJM.getUlJobType());
				jmon.setSessionID(jJM.getUlSessionID());
				jmon.setTransferBytesDisk(jJM.getUlXferBytesDisk());
				jmon.setTransferBytesJob(jJM.getUlXferBytesJob());
				jmon.setElapsedTime(jJM.getUlElapsedTime());
				if(jmon.getJobType() == Constants.AF_JOBTYPE_VM_CATALOG_FS
						|| jmon.getJobType() == Constants.JOBTYPE_CATALOG_GRT
						|| jmon.getJobType() == Constants.JOBTYPE_VM_CATALOG_FS_ONDEMAND){
					jmon.setElapsedTime(new Date().getTime() - new Date(jJM.getUlBackupStartTime()).getTime());
				}
				jmon.setVolMethod(jJM.getUlVolMethod());
				
				jmon.setnProgramCPU(jJM.getnProgramCPU());
				jmon.setnSystemCPU(jJM.getnSystemCPU());
				jmon.setnReadSpeed(jJM.getnReadSpeed());
				jmon.setnWriteSpeed(jJM.getnWriteSpeed());
				jmon.setnSystemReadSpeed(jJM.getnSystemReadSpeed());
				jmon.setnSystemWriteSpeed(jJM.getnSystemWriteSpeed());
				
				jmon.setThrottling(jJM.getUlThrottling());
				jmon.setEncInfoStatus(jJM.getUlEncInfoStatus());
				jmon.setTotalSizeRead(jJM.getUlTotalSizeRead());
				jmon.setTotalSizeWritten(jJM.getUlTotalSizeWritten());
				jmon.setCurVolMntPoint(jJM.getWzCurVolMntPoint());
				jmon.setTransferMode(jJM.getTransferMode());
				jmon.setCompressLevel(jJM.getUlCompressLevel());
				
				jmon.setCtBKJobName(jJM.getCtBKJobName());
				jmon.setCtBKStartTime(jJM.getCtBKStartTime());
				jmon.setCtCurCatVol(jJM.getCtCurCatVol());
				jmon.setCtDWBKJobID(jJM.getCtDWBKJobID());

				jmon.setWszEDB(jJM.getWszEDB());
				jmon.setWszMailFolder(jJM.getWszMailFolder());
				jmon.setUlProcessedFolder(jJM.getUlProcessedFolder());
				jmon.setUlTotalFolder(jJM.getUlTotalFolder());

				jmon.setDwBKSessNum(jJM.getDwBKSessNum());
				jmon.setWzBKBackupDest(jJM.getWzBKBackupDest());
				jmon.setWzBKDestPassword(jJM.getWzBKDestPassword());
				jmon.setWzBKDestUsrName(jJM.getWzBKDestUsrName());
				jmon.setD2dServerName(getD2DServerName4JobMonitor(jobContext));
				jmon.setUniqueData(jJM.getUlUniqueData());
				jmon.setEnableDedupe(dedupeEnabled);
				jmon.setOnDemand(isOnDemand);
				if(StringUtil.isEmptyOrNull(jmon.getRpsPolicyUUID()))
					jmon.setRpsPolicyUUID(rpsPolicyUUID);
				if(StringUtil.isEmptyOrNull(jmon.getDataStoreUUID()))
					jmon.setDataStoreUUID(rpsDataStoreUUID);
				if(StringUtil.isEmptyOrNull(jmon.getPlanUUID()))
					jmon.setPlanUUID(isOnDemand ? null : planUUID);
				if(job.isJobDone(jmon.getJobPhase(), jmon.getJobStatus()))
					jmon.setFinished(true);

				if (StringUtil.isEmptyOrNull(jmon.getAgentNodeName()))
					jmon.setAgentNodeName(getD2DServerName4JobMonitor(jobContext));
				if (StringUtil.isEmptyOrNull(jmon.getServerNodeName())) {
					jmon.setServerNodeName(ServiceContext.getInstance().getLocalMachineName());
				}
				
				jmon.setUlTotalVMJobCount(jJM.getUlTotalVMJobCount());
				jmon.setUlFinishedVMJobCount(jJM.getUlFinishedVMJobCount());
				jmon.setUlCanceledVMJobCount(jJM.getUlCanceledVMJobCount());
				jmon.setUlFailedVMJobCount(jJM.getUlFailedVMJobCount());
				
				jmon.setJobSubStatus(jJM.getJobSubStatus());
				
				if (job != null && job.rpsHost != null) {
					jmon.setRpsServerName(job.rpsHost.getRhostname());
				}
			}
			
			logger.debug("updateJobMonitor(JJobMonitor) - end");
		}
		
		private class HTMLFileFilter implements FileFilter
		{
			@Override
			public boolean accept(File pathname) {
				return (pathname.getName().endsWith("html") && pathname.getName().startsWith("job"));
			}
		}		
		
		protected void startMergeJob(JJobMonitor jJM) {
			try {
				VSphereMergeService.getInstance().jobEnd(jobContext.getExecuterInstanceUUID(), job);
				logger.info("Start merge job for " + jobContext.getExecuterInstanceUUID());
				if(jJM != null){
					if(jJM.getUlJobType() != Constants.AF_JOBTYPE_VM_BACKUP &&
							jJM.getUlJobType() != Constants.AF_JOBTYPE_HYPERV_VM_BACKUP &&
							jJM.getUlJobType() != Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP &&
							jJM.getUlJobType() != Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP
							) {
						//after backup, a catalog is always started, 
						//so we don't start merge job after backup. 
						VSphereMergeService.getInstance()
						.resumeVMMerge(AbstractMergeService.MergeEvent.OTHER_JOB_END, 
								jobContext.getExecuterInstanceUUID());
					}
				}else {
					VSphereMergeService.getInstance().resumeVMMerge(AbstractMergeService.MergeEvent.OTHER_JOB_END, jobContext.getExecuterInstanceUUID());
				}		
			}catch(Exception e) {
				logger.error("Failed to start merge job", e);
			}
		}
		
		@Override
		@Deprecated
		protected void reportJobMonitor(JobMonitor jobMonitor)
		{
			if(jobMonitor == null)
				logger.debug("Input JobMonitor values null.");
			
			//Update job status to RPS by Listener.
//			FlashJobMonitor fJM = convertToFlashJobMonitor(jobMonitor);
			
			FlashSyncher flashSyn = FlashSyncher.getInstance();
			String flashServerUuid = CommonService.getInstance().getNodeUUID();
			if(job.isJobDone(jobMonitor.getJobPhase(), jobMonitor.getJobStatus())){
				jobMonitor.setFinished(true);
			}
			jobMonitor.setDataStoreUUID(VSphereService.getInstance()
					.getRpsDataStoreUUID(jobContext.getExecuterInstanceUUID()));
			if(flashSyn.reportJobMonitor(jobMonitor, jobContext.getExecuterInstanceUUID(), 
					VSphereService.getInstance().getRpsPolicyUUID(jobContext.getExecuterInstanceUUID()),
					flashServerUuid) != 0)
			{
				logger.error("Failed to update job status to RPS by Listener.");
			}
		}
	}

	public static String getHyperVServerName(VMBackupConfiguration vmBackup) throws ServiceException{
		if (vmBackup == null) {
			logger.error("The VM backup configuration is null.");
			return null;
		}
		if(vmBackup.getBackupVM() == null) {
			logger.error("Invalid VM backup configuration, the VM info is null.");
			return null;
		}
		
		String hyperVServerName = vmBackup.getBackupVM().getEsxServerName();
		if (StringUtil.isEmptyOrNull(hyperVServerName)) {
			logger.error("The ESX host name in VM backup configuration is null or empty.");
		}
		
		return hyperVServerName;
		
	}
	
	protected boolean isConfigurationForRemoteNode(VMBackupConfiguration configuration) {
		return configuration != null && configuration.getGenerateType() == GenerateType.MSPManualConversion;
	}
}

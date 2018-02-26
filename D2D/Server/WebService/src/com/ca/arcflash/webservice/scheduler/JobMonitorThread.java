package com.ca.arcflash.webservice.scheduler;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.service.common.FlashSyncher;
import com.ca.arcflash.service.jni.model.JRestorePoint;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.edge.datasync.job.D2DBackupJobSyncMonitor;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;
import com.ca.arcflash.webservice.jni.model.JBackupInfoSummary;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.jni.wrapper.WebClientWrapper;
import com.ca.arcflash.webservice.service.AbstractMergeService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BackupSetService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.JobMonitorService;
import com.ca.arcflash.webservice.service.MergeService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.RSSItemXMLDAO;
import com.ca.arcflash.webservice.util.EmailContentContext;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.RSSItem;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;

class JobMonitorThread implements Runnable {
	protected BaseJob job = null;

	private final Logger logger = Logger.getLogger(JobMonitorThread.class);

	long jobMonitorHandle = -1;

	long startTime;

	//public JobMonitorThread() {	}

	public JobMonitorThread(BaseJob job){
		this.job = job;
	    this.job.jobPhase = 0;
		getJobMonitorHandle(job.shrmemid);
	}
	
	protected void getJobMonitorHandle(long id){
		if(id == -1){
			job.shrmemid = CommonService.getInstance().getNativeFacade().getJobID();
		}
		jobMonitorHandle = CommonService.getInstance().getNativeFacade().createJobMonitor(id);
		logger.debug("Job monitor handle:"+jobMonitorHandle);
	}

	protected JJobMonitor getJobMonitor(JJobMonitor jJM){
		jJM = CommonService.getInstance().getNativeFacade().GetJobMonitor(jobMonitorHandle);
//		if (jJM.getPlanUUID() == null || jJM.getPlanUUID().length() == 0)
//		{
//			jJM.setPlanUUID(VSphereService.getInstance().getPlanUUIDByVMInstanceUUID(jJM.getVmInstanceUUID()));
//		}
		long current = System.currentTimeMillis();
		long duration = (current - startTime)/1000/60;
		
		if(duration > 10L && (jJM == null || jJM.getUlJobPhase() == 0))// read for 10 min and still no job. break it.
		{
			logger.info(duration + " min passed for job:" + job.shrmemid);
			logger.debug("Clean up and notify for timeout job monitor");
			cleanup(jJM);
			return null;
		}else
			return jJM;
	}
	
	
	protected void optionalUpdate(JJobMonitor jJM){
		//logger.debug("Inform to update job status to Edge");
		D2DBackupJobSyncMonitor.getInstance().addSyncData(job.jobStatusSyncUuid, jJM, job.shrmemid);
	}
	
	@Deprecated
	protected void reportJobMonitor(JobMonitor jobMonitor){
		if(jobMonitor == null)
			logger.debug("Input JobMonitor values null.");
		
		//Update job status to RPS by Listener. 
		//No need to covert the object, otherwise some attributes will be missed
//		FlashJobMonitor fJM = convertToFlashJobMonitor(jobMonitor);
		
		FlashSyncher flashSyn = FlashSyncher.getInstance();
		String flashServerUuid = CommonService.getInstance().getNodeUUID(); 
		jobMonitor.setDataStoreUUID(BackupService.getInstance().getDataStoreUUID());
		if(flashSyn.reportJobMonitor(jobMonitor, jobMonitor.getVmInstanceUUID(), 
				BackupService.getInstance().getRPSPolicyUUID(), flashServerUuid) != 0)
		{
			logger.error("Failed to update job status to RPS by Listener.");
		}
		if(job.isJobDone(jobMonitor.getJobPhase(), jobMonitor.getJobStatus()))
			jobMonitor.setFinished(true);
	}
	
	protected FlashJobMonitor convertToFlashJobMonitor(JobMonitor jobMonitor)
	{
		if(jobMonitor == null)
			return null;
		
		FlashJobMonitor fJM = new FlashJobMonitor();
		fJM.setJobId(jobMonitor.getJobId());
		fJM.setJobType(jobMonitor.getJobType());
		fJM.setJobStatus(jobMonitor.getJobStatus());
		fJM.setStartTime(jobMonitor.getStartTime());
		fJM.setRemainTime(jobMonitor.getRemainTime());
		fJM.setElapsedTime(jobMonitor.getElapsedTime());
		fJM.setProgress(jobMonitor.getProgress());
		fJM.setProcessing(jobMonitor.getProcessing());
		fJM.setJobMethod(jobMonitor.getJobMethod());
		fJM.setJobPhase(jobMonitor.getJobPhase());
		
		return fJM;
	}

	protected void getRPSInfo(){
		BackupRPSDestSetting policy = BackupService.getInstance().getRpsSetting();			
		if(policy == null)
			return;
		job.dedupeEnabled = policy.isDedupe();
		
		if (job.js != null) {
			switch (job.js.getUsJobType()) {
			case Constants.AF_JOBTYPE_COPY:
				// for CRP job, because the destination cannot be dedupe we
				// should disable dedupe flag
				// all the time. for Oolong v1.0, 2/18/2014
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
		logger.info("Start backup job monitor.");
		JJobMonitor jJM = null;
		if(startTime == 0){
			startTime = System.currentTimeMillis();
		}
		
		int optionalUpdateCount = 0;
		getRPSInfo();
		while (true) {
			try{
				if (job.isStopJM || ServiceContext.getInstance().isServiceStoped()) {
					break;
				}

				if((jJM = getJobMonitor(jJM)) == null){
					break;
				}
				if (jJM != null){
					updateJobMonitor(jJM);
					optionalUpdate(jJM);
					++optionalUpdateCount;
					if(job.isJobDone(jJM.getUlJobPhase(),jJM.getUlJobStatus())){
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
					if(ServiceContext.getInstance().isServiceStoped()) break;
					logger.error("run()", e);
					
				}
			}catch(Throwable e){
				logger.error("get job monitor", e);
			}
		}


		optionalUpdate(null); //20110603 inform job status sync thread to exit
		
		logger.info("End of backup job monitor, update count = " + optionalUpdateCount);
		
		return jJM;
	}

	protected void fixRecoverySet(JJobMonitor jJM) {
		//if current job is full backup, we may need to update reovery set.
		String emptyVMInstanceUUID = "";
		if(jJM != null 
				&& (jJM.getUlJobType() == Constants.AF_JOBTYPE_BACKUP || 
					jJM.getUlJobType() == Constants.AF_JOBTYPE_VM_BACKUP ||
							jJM.getUlJobType() == Constants.AF_JOBTYPE_HYPERV_VM_BACKUP ||
					jJM.getUlJobType() == Constants.AF_JOBTYPE_VMWARE_VAPP_BACKUP ||
					jJM.getUlJobType() == Constants.AF_JOBTYPE_HYPERV_CLUSTER_BACKUP) 
				&& jJM.getUlJobMethod() == BackupType.Full 
				&& jJM.getUlJobStatus() == Constants.BackupJob_PROC_EXIT){
			BackupSetService.getInstance().fixRecoverySetForNewFull(
					jJM.getUlBackupStartTime(), emptyVMInstanceUUID);
		}
	}
	
	protected void afterComplete(JJobMonitor jJM) {
		synchronized (job.phaseLock) {
			 if(jJM != null)// && jJM.getUlJobPhase() == JobExitPhase)
			 {	
				 job.jobStatus = jJM.getUlJobStatus();
			 }
			 //we must set the jobPhase to JobExitPhase, otherwise, no other place will info backup thread exits.
			 logger.info("notify for job end");
			 job.jobPhase = Constants.JobExitPhase;
			 
			 job.phaseLock.notifyAll();
		 }
		try{
			logger.info("After job complete...");
			job.releaseJobLock();			
			fixRecoverySet(jJM);
		
			MergeService.getInstance().jobEnd(job);
			startMergeJob(jJM);

			if (jJM != null) {
				if (jJM.getUlJobPhase() == Constants.JobExitPhase
						|| jJM.getUlJobPhase() == BaseBackupJob.BackupJob_Phase_PROC_EXIT) {
					if(jJM.getUlJobType() == Constants.AF_JOBTYPE_BACKUP 
							&& jJM.getUlJobStatus() == Constants.JOBSTATUS_SKIPPED) {
						logger.debug("Backup is skipped, will retry it");
						BaseBackupJob.makeupSkippedBackup();
					}else{
						BaseBackupJob.makeupMissedBackup();
					}
					saveRSSFeed(jJM);
				}
				
				sendEmail(jJM);	
			}
		} catch (Throwable e) {
	
			logger.error("Error in afterComplete", e);
		}
		job.isStopJM = false;
		job.afterComplete();
	}

	protected void startMergeJob(JJobMonitor jJM) {
		try {
			
			if(jJM != null) {
				if(jJM.getUlJobType() == Constants.AF_JOBTYPE_BACKUP){
					if(jJM.getUlJobStatus() != BaseBackupJob.BackupJob_PROC_EXIT){
						//if backup job failed, then don't need to consider the following 
						//catalog and file copy, copy, we can start merge immediately
//						MergeService.getInstance().resumeMerge(false, true);
						//remove start merge job as catalog job will try to launch merge
						//MergeService.getInstance().resumeMerge(AbstractMergeService.MergeEvent.OTHER_JOB_END);
					}else {
						logger.info("Backup job complete successfully, waiting for " +
								"catalog job done, then we start merge job");
					}
				}else {
					logger.info("Start merge after other job");
//					MergeService.getInstance().resumeMerge(false, true);
					MergeService.getInstance().resumeMerge(AbstractMergeService.MergeEvent.OTHER_JOB_END);
				}
			}else {
				logger.info("Start merge, and the job monitor is null");
//				MergeService.getInstance().resumeMerge(false, true);
				MergeService.getInstance().resumeMerge(AbstractMergeService.MergeEvent.OTHER_JOB_END);
			}
		}catch(Exception e) {
			logger.error("Failed to start merge job " + e.getMessage());
		}
		
	}
	
	protected boolean isValidJobMonitor(JJobMonitor jJM) {
//		if((jJM == null || jJM.getUlJobPhase() <= 0	&& (jJM.getWszEDB() == null || jJM.getWszEDB().isEmpty()))){
//			logger.debug("jJM is not valid");
//			return false;
//		}
		if(jJM == null){
			logger.debug("jJM is not valid");
			return false;
		}
		//For catalog, catalog don't know job type before it read the job script
		if(jJM.getUlJobType() == 0 && (jJM.getUlJobPhase() == Constants.CATPROC_PHASE_VALIDATE_CATALOG_SCRIPT
				|| jJM.getUlJobPhase() == Constants.CATPROC_PHASE_PARSE_CATALOG_SCRIPT)){
			logger.debug("jJM is not valid");
			return false;
		}
		return true;
	}

	protected void updateJobMonitor(JJobMonitor jJM) {
		logger.debug("updateJobMonitor(JJobMonitor) - start");

		if(jJM.getWszEDB() != null && !jJM.getWszEDB().isEmpty())
			jJM.setUlJobType(Constants.JOBTYPE_CATALOG_GRT);
		//JobMonitor jmon = CommonService.getInstance().getJobMonitorInternal(String.valueOf(jJM.getUlJobType()),shrmemid, true);
//		JobMonitor jmon = CommonService.getInstance().getJobMonitorInternal();
		long jobType = jJM.getUlJobType() > 0 ? jJM.getUlJobType() : job.getDefaultJobType();
		JobMonitor jmon = JobMonitorService.getInstance().getJobMonitorInternal(String.valueOf(jobType),job.shrmemid,
				jJM.getProductType(),jJM.getVmInstanceUUID(), true, startTime);
		
		if(!isValidJobMonitor(jJM))
			return;
		
		synchronized (jmon) {
			jmon.setJobId(job.shrmemid);
			if(jJM.getUlBackupStartTime() > 0){
				jmon.setBackupStartTime(jJM.getUlBackupStartTime());
			}
			jmon.setCurrentProcessDiskName(jJM.getWszDiskName());
			jmon.setEstimateBytesDisk(jJM.getUlEstBytesDisk());
			jmon.setEstimateBytesJob(jJM.getUlEstBytesJob());
			jmon.setFlags(jJM.getUlFlags());
			jmon.setJobMethod(jJM.getUlJobMethod());
			jmon.setJobPhase(jJM.getUlJobPhase());
			jmon.setJobStatus(jJM.getUlJobStatus());
			jmon.setJobType(jobType);
			jmon.setSessionID(jJM.getUlSessionID());
			jmon.setTransferBytesDisk(jJM.getUlXferBytesDisk());
			jmon.setTransferBytesJob(jJM.getUlXferBytesJob());
			jmon.setElapsedTime(jJM.getUlElapsedTime());
			if(jmon.getElapsedTime() <= 0 && jJM.getUlBackupStartTime() > 0){
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

			jmon.setUlMergedSessions(jJM.getUlMergedSession());
			jmon.setUlTotalMegedSessions(jJM.getUlTotalMergedSessions());
			
			jmon.setProductType(jJM.getProductType());
			jmon.setVmInstanceUUID(jJM.getVmInstanceUUID());
			jmon.setD2dServerName(ServiceContext.getInstance().getLocalMachineName());
			jmon.setUniqueData(jJM.getUlUniqueData());
			jmon.setEnableDedupe(job.dedupeEnabled);
			jmon.setOnDemand(job.isOnDemand);
			if(StringUtil.isEmptyOrNull(jmon.getRpsPolicyUUID()))
				jmon.setRpsPolicyUUID(job.rpsPolicyUUID);
			if(StringUtil.isEmptyOrNull(jmon.getDataStoreUUID()))
				jmon.setDataStoreUUID(job.rpsDataStoreUUID);
			if(StringUtil.isEmptyOrNull(jmon.getPlanUUID()))
				jmon.setPlanUUID(job.isOnDemand ? null : job.planUUID);
			
			if (StringUtil.isEmptyOrNull(jmon.getAgentNodeName()))
				jmon.setAgentNodeName(jmon.getD2dServerName());
			if (StringUtil.isEmptyOrNull(jmon.getServerNodeName())) {
				jmon.setServerNodeName(jmon.getD2dServerName());
			}
			
			if (job != null && job.rpsHost != null) {
				jmon.setRpsServerName(job.rpsHost.getRhostname());
			}

			CommonService.getInstance().addJobmonitorHistory(jmon);				
			if(job.isJobDone(jmon.getJobPhase(), jmon.getJobStatus()))
				jmon.setFinished(true);
		}
		
//		reportJobMonitor(jmon);
		logger.debug("updateJobMonitor(JJobMonitor) - end");
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
			logger.info("Clean job monitor from cache");
			JobMonitorService.getInstance().removeJobMonitor(String.valueOf(jJM.getUlJobType()), job.shrmemid);
			CommonService.getInstance().clearJobmonitorHistory();
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

		job.isStopJM = false;
   }
	
   protected void notify(JJobMonitor jJM){
		try {
			logger.info("Try to notify observers");
			boolean isValid = isValidJobMonitor(jJM);				
			if(job != null && isValid){
				Thread.sleep(3 * 1000);
				job.notifyObservers(jJM);
			}else {
				logger.warn("No need to notify, job/job monitor is invalid");
			}
		}catch(Throwable e){
			logger.error("failed to notify observers", e);
		}
	}
	
	protected void sendEmail(JJobMonitor jJM) {
		logger.debug("sendEmail - start");
		try{
			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			if(configuration==null)
				return;

			// BackupEmail email = configuration.getEmail();
			PreferencesConfiguration preferencesConfig = CommonService.getInstance().getPreferences();
			if (preferencesConfig == null) {
				return;
			}
			
			BackupEmail email = preferencesConfig.getEmailAlerts();
			if (email == null || !email.isEnableSettings())
				return;

			boolean sendEmail = false;
			if ((jJM.getUlJobStatus() == Constants.JOBSTATUS_FAILED ||
					jJM.getUlJobStatus() == Constants.JOBSTATUS_CRASH ||
					jJM.getUlJobStatus() == Constants.JOBSTATUS_CANCELLED ||
					jJM.getUlJobStatus() == Constants.JOBSTATUS_LICENSE_FAILED) && email.isEnableEmail())
			{
				logger.debug("job is failed, cancelled or crashed => send email");
				sendEmail = true;
			}
			else if ((jJM.getUlJobStatus() == Constants.JOBSTATUS_FINISHED || jJM.getUlJobStatus() == Constants.BackupJob_PROC_EXIT) 
					&& (email.isEnableEmailOnSuccess()))
			{
				logger.debug("job is successful => send email");
				sendEmail = true;
			}

			if (sendEmail){
				EmailContentContext eContext = new EmailContentContext(); 
				//try to get hostname
				String url = job.getSettingsURL();

				EmailSender emailSender = new EmailSender();
				//add job status in subject to fix 18911769
				String jobStatus = EmailContentTemplate.jobStatus2String(jJM.getUlJobStatus(), job.shrmemid, jJM.getUlJobType());
				String emailJobStatus = WebServiceMessages.getResource("EmailJobStatus");
				String hostName = ServiceContext.getInstance().getLocalMachineName();
				String jobType = "";
                boolean isLink = true;
				String emailDest = job.getDestination();
                if (job.rpsDataStoreUUID != null && !job.rpsDataStoreUUID.equals("")) {
					emailDest = job.getDataStoreDisplayName();
					isLink = false;
				}
                if (job.getRPSName() != null)
					eContext.rpsName = job.getRPSName();
				String emailSource = null;
				long ulJobType = jJM.getUlJobType();
				
				if(jJM.getUlJobStatus() == Constants.JOBSTATUS_FAILED ||
					jJM.getUlJobStatus() == Constants.JOBSTATUS_CRASH ||
					jJM.getUlJobStatus() == Constants.JOBSTATUS_LICENSE_FAILED){
					emailSender.setHighPriority(true);
				}
				
				emailSender.setSubject(getEmailSubject(email.getSubject(), (int)ulJobType,
						emailJobStatus, jobStatus, hostName));

				if(ulJobType == Constants.AF_JOBTYPE_COPY) {
					emailDest = job.js.getPwszDestPath();
					if(job.js.getPAFNodeList().size() >= 1)
					{
						emailSource = job.js.getPAFNodeList().get(0).getPwszSessPath();
					}
					if(job.rpsDataStoreName != null && !job.rpsDataStoreName.equals(""))
						emailSource = job.rpsDataStoreName;
				}	
				Date startTime = new Date();
				if(jJM.getUlBackupStartTime() != 0){
					startTime = new Date(jJM.getUlBackupStartTime());
				}
				String startString = EmailContentTemplate.formatDate(startTime);

				/** for promoting alert message  to edge server */
				emailSender.setJobStatus(jJM.getUlJobStatus());
				emailSender.setProductType(CommonEmailInformation.PRODUCT_TYPE.ARCFlash.getValue());

				/** for email alert PR */
				String size = this.getEMailSize(jJM);
				eContext.backupSize = size;
				eContext.destination = emailDest;
                eContext.isLink = isLink;
				eContext.executionTime = startString;
				eContext.jobID = job.shrmemid;
				eContext.URL = url;
				eContext.jobMethod = jJM.getUlJobMethod();
				eContext.jobStatus = jJM.getUlJobStatus();
				eContext.jobType = jJM.getUlJobType();
				eContext.result = getActivityLogResult(job.shrmemid);
				eContext.source = emailSource;
				eContext.backupStartTime = jJM.getCtBKStartTime();
				eContext.enableHtml = email.isEnableHTMLFormat();
				if(jJM.getUlJobType() == Constants.AF_JOBTYPE_BACKUP){
					eContext.jobScheduleType=((BaseBackupJob)job).getScheduleType();
				}			
				emailSender.setContent(EmailContentTemplate.getContent(eContext));
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
				// why not directly use getJobType() from JJobMonitor but this statement; see function updateJobMonitor(); it generate JobMonitor.jobType using this statement,and the JobMonitor is send to cpm using Listener;
				long jobTypeForEmailSend = jJM.getUlJobType() > 0 ? jJM.getUlJobType() : job.getDefaultJobType();
				///the sendEmail() send email and alert to CPM; so all attributions should same as JobMonitor: so the parameter of the sendEmail() function should be JobMonitor not JJobMonitor!!; but we have no resource to change it; so we just set the difference manually
				emailSender.setJobType( jobTypeForEmailSend );
				emailSender.sendEmail(email.isEnableHTMLFormat());
			}
		}catch(Throwable e){
			logger.error("Error in sending email", e);
		}

		logger.debug("sendEmail - end");
	}
	
	private String getEmailSubject(String baseSubject, int ulJobType, String emailJobStatus, 
							String jobStatus, String hostName) {
		String jobType = "";
		switch(ulJobType) {
		case Constants.AF_JOBTYPE_BACKUP:
			jobType = WebServiceMessages.getResource("BackupJob") + " ";
			break;
		case Constants.AF_JOBTYPE_COPY:
			jobType = WebServiceMessages.getResource("CopyJob") + " ";
			break;
		case Constants.AF_JOBTYPE_RESTORE:
			jobType = WebServiceMessages.getResource("RestoreJob") + " ";
			break;
		case Constants.JOBTYPE_CATALOG_FS:
        case Constants.JOBTYPE_CATALOG_FS_ONDEMAND:
			jobType = WebServiceMessages.getResource("FSCatalogJob") + " ";
			break;
		case Constants.JOBTYPE_CATALOG_GRT:
			jobType = WebServiceMessages.getResource("GRTCatalogJob") + " ";
			break;
			default:
				break;
		}
		
		return baseSubject + "-"+ jobType +emailJobStatus+jobStatus+"("+hostName+")";

	}
	
	protected void saveRSSFeed(JJobMonitor jJM) {
		RSSItemXMLDAO rssItemXMLDAO = new RSSItemXMLDAO();
		//FAILED JOB RSS
		if (jJM.getUlJobStatus() == Constants.JOBSTATUS_FAILED ||
				jJM.getUlJobStatus() == Constants.JOBSTATUS_CRASH ||
				jJM.getUlJobStatus() == Constants.JOBSTATUS_CANCELLED ||
				jJM.getUlJobStatus() == Constants.JOBSTATUS_LICENSE_FAILED)
		{
			try {
				String url = BaseJob.getServerURL();

				BackupConfiguration configuration;

					configuration = BackupService.getInstance().getBackupConfiguration();

				if (configuration == null)
					return;

				Date startTime = new Date(jJM.getUlBackupStartTime());
				String startString = EmailContentTemplate.formatDate(startTime);
				String html = EmailContentTemplate.getHtmlContent(jJM.getUlJobStatus(), jJM.getUlJobType(),
						jJM.getUlJobMethod(), job.shrmemid, startString, null, job.getDestination(), getActivityLogResult(job.shrmemid),url);

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
					String jobStatus = EmailContentTemplate.jobStatus2String(jJM.getUlJobStatus(), job.shrmemid, jJM.getUlJobType());
					String jobTypeString;
					if (jJM.getUlJobType() == Constants.AF_JOBTYPE_BACKUP)
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

			} catch (ServiceException e) {
				logger.error("Error in saving RSS feed - ", e);
			}

		}
	}
	
	private String getEMailSize(JJobMonitor jJM) {
		String size = null;
		try {
			NativeFacade facade = new NativeFacadeImpl();
			if(jJM.getUlJobType() == Constants.AF_JOBTYPE_BACKUP){
				JBackupInfoSummary summary = facade.GetBackupInfoSummary(
						job.js.getPwszDestPath(),null,job.js.getPwszUserName(),job.js.getPwszPassword(), false);
				//issue  20764421, the summary is sorted by date now, the first item is the newest.
				size = summary.getBackupInfoList().get(0).getSize();
				size = ServiceUtils.bytes2String(Long.parseLong(size));
			}else if(jJM.getUlJobType() == Constants.AF_JOBTYPE_COPY){
				if(jJM.getUlJobStatus() != Constants.JOBSTATUS_FINISHED)
					size = "0";
				else {
						Date MinDate = new Date(0);  //1970-1-1
						Date MaxDate = new Date(8099, 11, 31);  //9999-12-31
						JRestorePoint[] RestorePoints = facade.getRestorePoints(job.js.getPwszDestPath(), null, job.js.getPwszUserName_2(),
								job.js.getPwszPassword_2(), MinDate, MaxDate, false);
						if(RestorePoints.length >= 1)
						{
							size = RestorePoints[0].getDataSize();
						}
						else
						{
							size = "0";
						}
				}
				
				size = ServiceUtils.bytes2String(Long.parseLong(size));
			}
		}catch(Throwable t) {
			logger.error(t.getMessage() == null ? t : t.getMessage());
		}
		
		return size;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	//private methods
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


	private ActivityLogResult getActivityLogResult(long jobid) {
		try {
			logger.debug("getActivityLogResult - jobid = " + jobid);
			return CommonService.getInstance().getJobActivityLogs(jobid, 0, 512);
		} catch (Exception e) {

			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		return null;
	}

	private class HTMLFileFilter implements FileFilter
	{
		@Override
		public boolean accept(File pathname) {
			return (pathname.getName().endsWith("html") && pathname.getName().startsWith("job"));
		}
	}
}


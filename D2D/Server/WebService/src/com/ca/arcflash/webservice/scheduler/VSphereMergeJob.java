package com.ca.arcflash.webservice.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.jobscript.base.GenerateType;
import com.ca.arcflash.service.jni.model.MergeJobScript;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.EveryMonthSchedule;
import com.ca.arcflash.webservice.data.EveryWeekSchedule;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.merge.MergeJobMonitor;
import com.ca.arcflash.webservice.data.merge.MergeJobStatus;
import com.ca.arcflash.webservice.data.merge.MergeMethod;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.service.AbstractMergeService;
import com.ca.arcflash.webservice.service.AbstractMergeService.MergeEvent;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereMergeService;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class VSphereMergeJob extends AbstractMergeJob {
	private static final Logger logger = Logger.getLogger(VSphereMergeJob.class);	
	public static final ExecutorService pool = Executors.newCachedThreadPool();
	private String vmName;
	
	public VSphereMergeJob() {
		
	}
	
	public VSphereMergeJob(String vmInstanceUUID, int jobId) {
		this.jobId = jobId;
		this.vmInstanceUUID = vmInstanceUUID;
		vmName = VSphereService.getInstance().getVMName(vmInstanceUUID);
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {		
		super.execute(context);		
		runningNodeUUID = vmInstanceUUID;
		try {
			if(jobId <= 0){
				synchronized(VSphereMergeService.getInstance()) {
					if(event != MergeEvent.MANUAL_RESUME &&
							!VSphereMergeService.getInstance().isMergeJobAvailable(vmInstanceUUID))
						return;
					if(((VSphereMergeService)getMergeService()).checkForResume(event, vmInstanceUUID) != 0)
						return ;
					
					if(VSphereMergeService.getInstance().canResumeMerge(event, vmInstanceUUID) == -1)
						return ;
					((VSphereMergeService)getMergeService()).mergeStart(this);
				}
			}
		}catch(ServiceException se) {
			logger.warn("Merge job cannot run now");
			return;
		}
		vmName = VSphereService.getInstance().getVMName(vmInstanceUUID);
		updateMergeJobStatus(MergeStatus.Status.TORUNN, null);
		pool.submit(this);
	}
	
	protected void updateMergeJobStatus(MergeStatus.Status status, 
			MergeJobMonitor jobMonitor){
		if(jobMonitor != null)
			jobMonitor.setVmInstanceUUID(vmInstanceUUID);
		MergeStatus ms = VSphereMergeService.getInstance().getMergeJobStatus(vmInstanceUUID);
		ms.setD2dServerName(vmName);
		if(jobMonitor != null){
			boolean finished = this.isJobDone(jobMonitor.getDwMergePhase(), 
					jobMonitor.getJobStatus());
			VSphereMergeService.getInstance().updateMergeJobStatus(status, ms,
					jobMonitor, jobId, finished, vmInstanceUUID);
		}
	}
	
	public String getVMInstanceUUID() {
		return vmInstanceUUID;
	}

	@Override
    protected MergeJobScript generateMergeJobScript() {
		MergeJobScript jobScript = null;
		VMBackupConfiguration backupConf = null;
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vmInstanceUUID);
		try {
			backupConf = VSphereService.getInstance()
				.getVMBackupConfiguration(vm);
		}catch(Exception e) {
			logger.error("Failed to get vm backup configuraiton", e);
			return null;
		}
		
		if(backupConf == null)
			return null;
		
		if (backupConf.getGenerateType() == GenerateType.MSPManualConversion) {
			logger.info("The configuration is for remote nodes.");
			return null;
		}
	    BackupVM backupVM = backupConf.getBackupVM();
	    if(backupVM == null)
	    	return null;

	    jobScript = this.generateMergeJobScript(backupVM.getDestination(),
	    		backupConf.getUserName() != null ? backupConf.getUserName() : "", 
	    				backupConf.getPassword() != null ? backupConf.getPassword() : "", 
	    						backupConf.getRetentionCount(), 
	    						backupConf.getRetentionPolicy().isUseBackupSet(), 
	    						backupConf.getRetentionPolicy().getBackupSetCount());
	    if(jobScript != null){
	    	jobScript.setDwSessionType(SESSIONTYPE_VSPHERE);
	    	jobScript.setWsVMGUID(vmInstanceUUID);
	    }
	    
	    if(backupConf.getBackupDataFormat() >0){// new format
			AdvanceSchedule ad = backupConf.getAdvanceSchedule();
			if (ad != null) {
				PeriodSchedule ps = ad.getPeriodSchedule();
				if (ps != null) {					
					EveryDaySchedule day = ps.getDaySchedule();
					if (day != null && day.isEnabled()) {
						jobScript.setDwDailyCnt(day.getRetentionCount());
					}
					EveryWeekSchedule week = ps.getWeekSchedule();
					if (week != null && week.isEnabled()) {
						jobScript.setDwWeeklyCnt(week.getRetentionCount());
					}
					EveryMonthSchedule month = ps.getMonthSchedule();
					if (month != null && month.isEnabled()) {
						jobScript.setDwMonthlyCnt(month.getRetentionCount());
					}
				}
				
//				if(ad.isPeriodEnabled()){
					jobScript.setDwMergeMethod(MergeMethod.EMM_SESS_RANGES.getValue());
//				}
			}
		}	
	    
	    return jobScript;
    }
	
	@Override
	protected AbstractMergeService getMergeService() {
		return VSphereMergeService.getInstance();
	}

	@Override
	protected boolean isOutScheduleTime() {
		RetentionPolicy policy = getMergeService().getRetentionPolicy(vmInstanceUUID); 
		if(policy == null || !policy.isStopMergeAfterSchedule() )
			return false;
		
		if(getMergeService().isInMergeTimeRange(vmInstanceUUID))
			return false;
		else {
			try {
//				VSphereMergeService.getInstance().pauseMerge(false, vmInstanceUUID, true);
				VSphereMergeService.getInstance().pauseMerge(
						AbstractMergeService.MergeEvent.NO_SCHEDULE, 
			      		  vmInstanceUUID,
			      		  null,
			      		  null);
			}catch(ServiceException e) {
				logger.error("Failed to pause merge", e);
			}
			return true;
		}
	}	

	@Override
	protected void sendMail(MergeJobMonitor jJM) {
		logger.debug("Enter send mail");
		try{
			super.sendMail(jJM);
			
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(vmInstanceUUID);
			
			VMBackupConfiguration configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if (configuration == null)
				return;
			
			if (configuration.getGenerateType() == GenerateType.MSPManualConversion) {
				logger.info("The configuration is for remote nodes.");
				return;
			}
			
			BackupEmail email = configuration.getEmail();
			if (email == null)
				return;
			if(email.isEnableEmailOnMergeFailure() 
					&& jJM.getJobStatus() != MergeJobStatus.EJS_JOB_FINISH.getValue()
				|| email.isEnableEmailOnMergeSuccess() 
					&& jJM.getJobStatus() == MergeJobStatus.EJS_JOB_FINISH.getValue()){
				EmailSender emailSender = new EmailSender();				
				String emailJobStatus = WebServiceMessages.getResource("EmailJobStatus");
				String jobStatus = jobStatus2String(jJM.getJobStatus());
				
				String url = BaseVSphereJob.getEdgeUrl();
				String vmName = configuration.getBackupVM().getVmName();
				String nodeName = configuration.getBackupVM().getVmHostName();
				if(nodeName==null||nodeName.isEmpty()){
					nodeName = WebServiceMessages.getResource("EmailNodeNameUnknown");
				}
				String emailSubject = WebServiceMessages.getResource("EmailSubject",
							email.getSubject(), WebServiceMessages.getResource("mergeJobString") 
						 + " " + emailJobStatus+jobStatus ,vmName, nodeName);
				boolean priority = !(jJM.getJobStatus() == MergeJobStatus.EJS_JOB_FINISH.getValue());
				
				List<EmailContentTemplate.Content> contents = new ArrayList<EmailContentTemplate.Content>();
				contents.add(new EmailContentTemplate.Content(
						WebServiceMessages.getResource("EmailVMName"), vmName, false));
				contents.add(new EmailContentTemplate.Content(
						WebServiceMessages.getResource("EmailNodeName"), nodeName, false));				
				String content = getEmailContent(contents, jobStatus, jJM.getUllStartTime(), 
						jJM.getJobStatus(), jJM.getDwJobID(), email.isEnableHTMLFormat(), 
						WebServiceMessages.getResource("VSphereEmailServerName"), url, 
						configuration.getBackupVM().getDestination());
				
				emailSender.sendEmail(email, emailSubject, content, changeMergeStatusToBaseJobStatus (jJM.getJobStatus()), priority, 
						CommonEmailInformation.PRODUCT_TYPE.VSPHERE.getValue(),JobType.JOBTYPE_MERGE, vmName  );
			}
		}catch(Exception e) {
				logger.error("Failed to send mail " + e);
		}
		logger.debug("Send mail complete");
	}
	
	@Override
	protected ActivityLogResult getActivityLog(long jobId) throws Exception{
		return CommonService.getInstance().getVMJobActivityLogs(jobId, 0, 500, vmInstanceUUID);
	}

	@Override
	protected boolean isVHDMerge() {
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vmInstanceUUID);
		try {
			VMBackupConfiguration configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if(configuration == null) return false;
			if (configuration.getGenerateType() == GenerateType.MSPManualConversion) {
				logger.info("The configuration is for remote nodes.");
				return false;
			}
			return configuration.getCompressionLevel() == 0 && configuration.getEncryptionAlgorithm() == 0;
		}catch(Exception e) {
			logger.error("Failed to get backup configuration " + e);
		}
		
		return false;
	}

	@Override
	protected String getVMName() {
		return vmName;
	}
	
/*	@Override
	public RetentionPolicy getRetentionPolicy() {
		return VSphereMergeService.getInstance().getRetentionPolicy(vmInstanceUUID);
	}*/
}

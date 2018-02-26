package com.ca.arcflash.webservice.scheduler;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.jni.common.JJobHistory;
import com.ca.arcflash.service.data.PeriodRetentionValue;
import com.ca.arcflash.service.jni.model.MergeJobScript;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveConfig;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveSourceItem;
import com.ca.arcflash.webservice.data.merge.MergeJobMonitor;
import com.ca.arcflash.webservice.data.merge.MergeJobPhase;
import com.ca.arcflash.webservice.data.merge.MergeJobStatus;
import com.ca.arcflash.webservice.data.merge.MergeMethod;
import com.ca.arcflash.webservice.data.merge.MergeOption;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.service.AbstractMergeService;
import com.ca.arcflash.webservice.service.AbstractMergeService.MergeEvent;
import com.ca.arcflash.webservice.service.ArchiveToTapeService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.util.AsyncTaskRunner;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public abstract class AbstractMergeJob implements Job, Runnable {
	
	private static final Logger logger = Logger.getLogger(AbstractMergeJob.class);
	
	protected int jobId;	
	private long startTime;
	
	protected MergeMethod method;
	protected MergeOption option;
	protected MergeEvent event;
	protected String vmInstanceUUID;
	protected String runningNodeUUID;
	
	
	private MergeJobScript jobScript;
	private volatile boolean mergeDone = false;
	private volatile Object mergeJobLock = new Object();
	private volatile boolean stopRun = false;	
	private AbstractMergeService.MergeActionContext pauseContext;

	private String planUUID = null;
	protected static final int SESSIONTYPE_D2D = 1;
	protected static final int SESSIONTYPE_VSPHERE = 2;
	
	private class StartMergeThread extends Thread {
		private String vmInstanceUUID;
		
		public StartMergeThread(String vmInstanceUUID) {
			this.vmInstanceUUID = vmInstanceUUID;
		}
		
		public void run() {
			startNewJobAfterDone(vmInstanceUUID);
		}
	}
	
	public long getJobId() {
		return jobId;
	}
	
	public String getVMInstanceUUID() {
		return vmInstanceUUID;
	}
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		method = (MergeMethod) dataMap.get("mergetMethod");
		option = (MergeOption) dataMap.get("mergetOption");
		jobId = (Integer)dataMap.get("jobId");
		event = (MergeEvent)dataMap.get("mergeEvent");
		vmInstanceUUID = dataMap.getString("vmInstanceUUID");
	}

	public MergeEvent getMergeEvent() {
		return event;
	}

	public void run() {	
		boolean canRun = true;
		if(jobId > 0) {
			//resume after webservice restart
			long jobMonitorHandle = getJobMonitorHandle(jobId);
			mergeRunning(jobMonitorHandle);
		}else{
			if(canRun && stopRun) {			
				canRun = false;
			}
			
			long jobMonitorHandle = 0;
			synchronized(getMergeService()) {
				jobId = (int) getMergeService().getNativeFacade().getJobID();
				planUUID = BackupService.getInstance().getPlanUUID(
						vmInstanceUUID);
				logger.info("Get job id for merge " + jobId);
				
				JJobHistory jobHistory = new JJobHistory();
				jobHistory.setJobId(jobId);
				jobHistory.setJobType( (int)JobType.JOBTYPE_MERGE );
				jobHistory.setJobDisposeNodeUUID(vmInstanceUUID);
				jobHistory.setDatastoreUUID(getRPSDataStoreUUID());
				jobHistory.setJobRunningNodeUUID(runningNodeUUID);
				jobHistory.setDatastoreVersion(BackupService.getInstance().getRPSDatastoreVersion(getRPSDataStoreUUID()));
				
				jobHistory.setPlanUUID(planUUID);
				getMergeService().getNativeFacade().updateJobHistory( jobHistory );
				
				jobScript = generateMergeJobScript();
				if(jobScript == null) {
					logger.error("Merge job cannot run, jobScript is null");
					canRun = false;
				}
				//check whether there is other job running again
				if(canRun)
					canRun = this.checkForJobRunning();
				if(canRun && !stopRun){
					jobMonitorHandle = getJobMonitorHandle(jobId);
					if(getMergeService().startMerge(this) != 0){
						this.releaseJobMonitor(jobMonitorHandle);
						jobMonitorHandle = 0;
					}
				}
			}	
			if(jobMonitorHandle > 0){
				//get and update job monitor				
				mergeRunning(jobMonitorHandle);
			}			
		}
		
		if(!canRun || stopRun) {
			//ask service to clean the job and job monitor cache
			mergeJobExit(null);
		}
	}	
	
	private String getRPSDataStoreUUID() {
		// TODO Auto-generated method stub
		return null;
	}

	private void mergeJobExit(MergeJobMonitor jJM) {
		this.getMergeService().mergeDone(this);		
		synchronized(mergeJobLock) {
			this.setMergeDone(true);
			//notify other jobs to run
			mergeJobLock.notifyAll();
		}
		
		
	}
	
	protected boolean isJobDone(long jobPhase, int jobStatus) {
		logger.debug("Current job phase is " + jobPhase + " jobStatus is " 
				+ jobStatus + " the end phase should be " + MergeJobPhase.EJP_PROC_EXIT);
		if(jobPhase == MergeJobPhase.EJP_END_OF_JOB.ordinal()
				|| jobPhase == MergeJobPhase.EJP_PROC_EXIT.ordinal()){
			logger.info("Merge job completed");
			return true;
		}
		else
			return false;
	}

	
	protected void mergeRunning(long jobMonitorHandle) {
		startTime = System.currentTimeMillis();
		
		MergeJobMonitor jJM = new MergeJobMonitor();
		logger.info("Start running merge job " + jobId);
		startTime = System.currentTimeMillis();
		while (true) {
			try{
				isOutScheduleTime();
				
				if((jJM = getJobMonitor(jJM, jobMonitorHandle)) == null){
					break;
				}
				jJM.setDwJobID(jobId);
				if (jJM != null && jJM.getDwMergePhase() > 0){
					if(logger.isDebugEnabled())
						logger.debug(jJM);
					
					updateProgress(jJM);
					
					updateMergeJobStatus(MergeStatus.Status.RUNNING, jJM);
					
					if(isJobDone(jJM.getDwMergePhase(),jJM.getJobStatus())){
						this.setMergeDone(true);
						Thread.sleep(3 * 1000);
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
		//Merge complete
		mergeJobExit(jJM);
		try {
			Method sendMail = AbstractMergeJob.class.getDeclaredMethod("sendMail", MergeJobMonitor.class);
			sendMail.setAccessible(true);
			AsyncTaskRunner.submit(this, sendMail, jJM);
		}catch(NoSuchMethodException e) {
			logger.error("Failed to get send mail method.");
		}
		
//		sendMail(jJM);
		releaseJobMonitor(jobMonitorHandle);
		
		logger.info("Remove job monitor for merge, id is " 
				+ jobId + " JOBPHASE = " + jJM.getDwMergePhase()
				+ " Job status is: " + jJM.getJobStatus() + " merge job is " + this);
		if(jJM != null && jJM.getJobStatus() == MergeJobStatus.EJS_JOB_FINISH.getValue()) {
			logger.info("Try to start a new merge job after merge job complete in case " +
					"there are more sessions need to merge");
			new StartMergeThread(vmInstanceUUID).start();
		}
	}

	
	protected String getEmailContent(List<EmailContentTemplate.Content> contents,
			String jobStatus, long startTime, 
			int status, long id, boolean html, 
			String serverNameLabel, String url, String destination) {
		String jobStatusString = jobStatus;
		String jobTypeString = WebServiceMessages.getResource("mergeJobString");
		
		contents.add(new EmailContentTemplate.Content(serverNameLabel, 
				ServiceContext.getInstance().getLocalMachineName(), false));
		contents.add(new EmailContentTemplate.Content(WebServiceMessages.getResource("EmailJobStatus"), 
				jobStatusString, false));
		contents.add(new EmailContentTemplate.Content(WebServiceMessages.getResource("EmailJobType"), 
				jobTypeString, false));
		contents.add(new EmailContentTemplate.Content(WebServiceMessages.getResource("EmailExecutionTime"), 
				BackupConverterUtil.dateToString(new Date(startTime)), false));
		contents.add(new EmailContentTemplate.Content(WebServiceMessages.getResource("EmailDestination"), 
				destination, true));
		
		String activityLogHeader = "";
		String activityLog = "";
		if (status == MergeJobStatus.EJS_JOB_CRASH.getValue() 
				|| status == MergeJobStatus.EJS_JOB_FAILED.getValue()
				|| status == MergeJobStatus.EJS_JOB_SKIPPED.getValue()
				|| status == MergeJobStatus.EJS_JOB_STOPPED.getValue())
		{
			activityLogHeader = WebServiceMessages.getResource("ActivityLog");
			try {
				ActivityLogResult log = this.getActivityLog(id);
				if(html)
					activityLog = EmailContentTemplate.convertLogToHtml(log, id);
				else
					activityLog = EmailContentTemplate.convertLogToPlainText(log, id);
			}catch(Exception e) {
				logger.error("Failed to get activity log " + e);
			}
		}
		
		return EmailContentTemplate.formatContent(contents, activityLog, activityLogHeader, 
				url, html);
	}
	
	
	
	private void releaseJobMonitor(long handle) {
		if (handle != 0) {
			CommonService.getInstance().getNativeFacade()
					.releaseMergeJobMonitor(handle);
		}
	}
	
	protected MergeJobScript generateMergeJobScript(String destination, String userName, 
		String password, int retentionCount, boolean useBackupSet, int backupSetCount){
		MergeJobScript jobScript = new MergeJobScript();
		jobScript.setDwJobID(jobId);
		jobScript.setWsDomainName("");
		jobScript.setWsFolderPath(destination);
		jobScript.setWsUserName(userName);
		jobScript.setWsUserPWD(password);
		jobScript.setProductType(1);
		if(option != null)
			jobScript.setDwMergeOpt(option.getValue());
		if(!useBackupSet){
			/*if(this.isVHDMerge())
				jobScript.setDwMergeMethod(MergeMethod.EMM_1_SESS.getValue());
			else: backend will check whether it's VHD or not.
			*/
				jobScript.setDwMergeMethod(MergeMethod.EMM_INC_2_FUL.getValue());
			jobScript.setDwRetentionCnt(retentionCount);
		}else {
			jobScript.setDwRetentionCnt(backupSetCount);
			method = MergeMethod.EMM_RMV_SESS;
			jobScript.setDwMergeMethod(MergeMethod.EMM_RMV_SESS.getValue());
			jobScript.setDwMergeOpt(MergeOption.EMO_MERGE_BKSET.getValue());
			int endSessionNum = this.getMergeService().getMergeBackupSetEndSessionNum();
			
			if(endSessionNum > 0)
				jobScript.setDwEndSess(endSessionNum);
			else
				return null;
		}
		long[] archivToTapeForMerge = getArchivToTapeForMerge();
		jobScript.setArchiveConfigTime(archivToTapeForMerge[0]);
		jobScript.setArchiveSourceSelection(archivToTapeForMerge[1]);		
		return jobScript;
	}
	
	private long[] getArchivToTapeForMerge(){
		ArchiveConfig config = null;
		try{
			config = ArchiveToTapeService.getInstance().getArchiveToTapeConfig();
		}catch(Exception e){
			logger.error("Faild to get archive config",e);
		}
		
		long archiveConfigTime = 0;
		long archiveSourceSelection = 0;
		
		if(config != null && config.getSource() != null && config.getSource().getSourceItems() !=null && config.getSource().getSourceItems().size() >0){
			ArchiveSourceItem item  = config.getSource().getSourceItems().get(0);
			archiveConfigTime = item.getConfigTime();
			
			if(item.getDailyItem()!=null && item.getDailyItem().isEnabled()){
				archiveSourceSelection |= PeriodRetentionValue.QJDTO_B_Backup_Daily;
			}
			if(item.getWeeklyItem()!=null && item.getWeeklyItem().isEnabled()){
				archiveSourceSelection |= PeriodRetentionValue.QJDTO_B_Backup_Weekly;
			}
			if(item.getMonthlyItem()!=null && item.getMonthlyItem().isEnabled()){
				archiveSourceSelection |= PeriodRetentionValue.QJDTO_B_Backup_Monthly;
			}
		}
		
		return new long[]{archiveConfigTime, archiveSourceSelection};
	}
	/*
	 * this status string converter should merge with EmailContentTemplate's converter. but the skip status is a problem. basejob convert skip to miss status,
	 *  but merger job developer refuse this modification. so defer this 
	 */
	
	protected String jobStatus2String(int jobStatus) {
		if(jobStatus == MergeJobStatus.EJS_JOB_CRASH.getValue())
			return WebServiceMessages.getResource("BackupStatusCrashed");
		else if(jobStatus == MergeJobStatus.EJS_JOB_FAILED.getValue()) {
			return WebServiceMessages.getResource("BackupStatusFailed");
		}else if(jobStatus == MergeJobStatus.EJS_JOB_FINISH.getValue()) {
			return WebServiceMessages.getResource("BackupStatusFinished");
		}else if(jobStatus == MergeJobStatus.EJS_JOB_SKIPPED.getValue()) {
			return WebServiceMessages.getResource("mergeJobStatusSkipped");
		}else if(jobStatus == MergeJobStatus.EJS_JOB_STOPPED.getValue()) {
			return WebServiceMessages.getResource("mergeJobStatusStopped");
		}else {
			return WebServiceMessages.getResource("BackupStatusUnknown");
		}
	}
	
	protected long getJobMonitorHandle(int id){
		jobId = id;
		
		if(id == -1){
			jobId = (int) CommonService.getInstance().getNativeFacade().getJobID();
		}
		long jobMonitorHandle = CommonService.getInstance().getNativeFacade().createMergeJobMonitor(id);
		logger.debug("Job monitor handle:"+jobMonitorHandle);
		return jobMonitorHandle;
	}

	protected MergeJobMonitor getJobMonitor(MergeJobMonitor jJM, long jobMonitorHandle){
		//Only for test
		jJM.setDwJobID(jobId);
		//test end
		jJM = CommonService.getInstance().getNativeFacade().getMergeJobMonitor(jJM, jobMonitorHandle);
		long current = System.currentTimeMillis();
		long duration = (current - startTime)/1000/60;
		// read for 10 min and still no job. break it.
		if(duration > 10L && (jJM == null || jJM.getDwMergePhase() == 0))
		{
			logger.info(duration + " min passed for job:" + jobId);
			logger.debug("Clean up and notify for timeout job monitor");
			return null;
		}else
			return jJM;
	}

	
	public MergeJobScript getMergeJobScript() {
		return jobScript;
	}

	
	public boolean isMergeJobEnd() {
		return mergeDone;
	}
	
	public void setMergeDone(boolean done) {
		mergeDone = done;
	}
	
	public void pauseMerge(AbstractMergeService.MergeActionContext context) {
		pauseContext = context;
		
	}
	
	protected boolean validateJobMonitor(MergeJobMonitor mjm) {
		if(mjm == null || mjm.getDwMergePhase() <= 0 || mjm.getUllStartTime() <= 0) {
			return false;
		}else {
			return true;
		}
	}
	
	public AbstractMergeService.MergeActionContext getPauseContext() {
		return pauseContext;
	}
	
	public synchronized int stopRun() {
		if(jobId > 0 && startTime > 0){
			logger.info("Merge job has started, we need to wait backend's end phase");
			return jobId;
		}else{
			stopRun =  true;
			return 0;
		}
	}
	
	public void waitJobEnd() {
		try {
			synchronized(mergeJobLock) {
				while(!mergeDone) {
					logger.info("wait for merge job " + jobId + " to end");
					mergeJobLock.wait();
				}
			}
		}catch(Exception e) {
			logger.error("wait lock exception " + e);
		}
	}

	private void updateProgress(MergeJobMonitor jJM) {
		jJM.setVHDMerge(this.isVHDMerge());
		//set elapsed time
		if(jJM.getUllStartTime() > 0) {
			jJM.setUllElapsedTime(new Date().getTime() - jJM.getUllStartTime());
			
			if(jJM.getfMergePercentage() > 0)
			{
				jJM.setfMergePercentage(jJM.getfMergePercentage()/100);
			}
		}	
		
		if(jJM.getUllTotalBytesMerged() > 0 && jJM.getUllElapsedTime() > 0 && jJM.getfMergePercentage()>0){
			
			long reMainTime = (long) (((1-jJM.getfMergePercentage())*jJM.getUllElapsedTime())/jJM.getfMergePercentage());
			jJM.setTimeRemain(reMainTime);
		}
		
		logger.debug(jJM);
	}
	
	//fix issue 128415
	protected int changeMergeStatusToBaseJobStatus( int mergeStatus) {
		if(mergeStatus == MergeJobStatus.EJS_JOB_CRASH.getValue())
			return Constants.JOBSTATUS_CRASH;
		else if(mergeStatus == MergeJobStatus.EJS_JOB_FAILED.getValue()) {
			return Constants.JOBSTATUS_FAILED;
		}
		else if(mergeStatus == MergeJobStatus.EJS_JOB_FINISH.getValue()) {
			return Constants.JOBSTATUS_FINISHED;
		}
		//this two transform only used in Edge Report;
		else if(mergeStatus == MergeJobStatus.EJS_JOB_SKIPPED.getValue()) {
			return Constants.JOBSTATUS_MISSED;
		}
		else if(mergeStatus == MergeJobStatus.EJS_JOB_STOPPED.getValue()) {
			return Constants.JOBSTATUS_STOP;
		}
		else {
			return Constants.JOBSTATUS_FAILED;
		}
	}
	
	protected void sendMail(MergeJobMonitor jJM){
		if(!validateJobMonitor(jJM)) {
			throw new RuntimeException("Merge job monitor is invalid");
		}
	}
	
	protected void updateMergeJobStatus(MergeStatus.Status status, MergeStatus jobStatus, MergeJobMonitor jobMonitor){
		if(jobStatus.getStatus() != MergeStatus.Status.PAUSING)
			jobStatus.setStatus(status);
		jobStatus.setPlanUUID(planUUID);
		jobStatus.setJobMonitor(jobMonitor);
		if(jobMonitor != null){
			jobStatus.setJobId(jobId);
			jobStatus.setJobPhase(jobMonitor.getDwMergePhase());
			if(jobStatus.getStatus() == MergeStatus.Status.PAUSING)
				jobStatus.setJobStatus(JobStatus.JOBSTATUS_CANCELLED);
			else
				jobStatus.setJobStatus(jobMonitor.getJobStatus());
			jobStatus.setProgress(jobMonitor.getfMergePercentage());
			jobStatus.setStartTime(jobMonitor.getUllStartTime());
			jobStatus.setElapsedTime(jobMonitor.getUllElapsedTime());
			jobStatus.setRemainTime(jobMonitor.getTimeRemain());
			if(this.isJobDone(jobMonitor.getDwMergePhase(), jobMonitor.getJobStatus())){
				jobStatus.setFinished(true);
			}else {
				jobStatus.setFinished(false);
			}
		}
//		jobStatus.setDataStoreUUID(getMergeService().getDataStoreUUID(vmInstanceUUID));
		this.getMergeService().updateMergeJobStatus(jobStatus);
		
//		this.getMergeService().reportMergeJobMonitor(jobStatus, vmInstanceUUID);
	}
	
	//abstract methods
	protected abstract void updateMergeJobStatus(MergeStatus.Status status, 
			MergeJobMonitor jobMonitor);	
	
	protected abstract AbstractMergeService getMergeService();
	
	protected boolean checkForJobRunning(){
		return getMergeService().canStartMerge(vmInstanceUUID);
	}
	
	protected abstract boolean isOutScheduleTime();
	
	protected abstract MergeJobScript generateMergeJobScript();	
	
	protected abstract ActivityLogResult getActivityLog(long jobId) throws Exception;
		
	protected abstract boolean isVHDMerge();
	
	protected abstract String getVMName();
	
	protected void startNewJobAfterDone(String vmInstanceUUID){
		this.getMergeService().startNewJobAfterDone(vmInstanceUUID);
	};
//	public abstract RetentionPolicy getRetentionPolicy();
}

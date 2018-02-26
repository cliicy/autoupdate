package com.ca.arcflash.webservice.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jobscript.base.GenerateType;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.EveryMonthSchedule;
import com.ca.arcflash.webservice.data.EveryWeekSchedule;
import com.ca.arcflash.webservice.data.MergeDetailItem;
import com.ca.arcflash.webservice.data.MountSession;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.merge.MergeAPISource;
import com.ca.arcflash.webservice.data.merge.MergeJobMonitor;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.scheduler.AbstractMergeJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.VSphereMergeJob;
import com.ca.arcflash.webservice.service.internal.VSphereBackupConfigurationXMLDAO;
import com.ca.arcflash.webservice.service.internal.VSphereConverter;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class VSphereMergeService extends AbstractMergeService {
	private static final Logger logger = Logger.getLogger(VSphereMergeService.class);
	
	private Map<String, MergeStatus> currentStatus 
		= new Hashtable<String, MergeStatus>();
	private Map<String, VSphereMergeJob> currentJobs 
		= new Hashtable<String, VSphereMergeJob>();
	private Map<String, Set<Object>> otherJobs = new Hashtable<String, Set<Object>>();
	
	private VSphereBackupConfigurationXMLDAO vspherebackupConfigurationXMLDAO = new VSphereBackupConfigurationXMLDAO();

	private VSphereMergeService() {
		scheduler = VSphereService.getInstance().getCatalogScheduler();
		initialize();
	}
	
	private static class SingletonInstance {
		static VSphereMergeService INSTANCE = new VSphereMergeService();
	}

	public static VSphereMergeService getInstance(){
		return SingletonInstance.INSTANCE;
	}
	
	public String getMergeScheduleTime(String vmInstanceUUID) {
		if(backupToRPS(vmInstanceUUID))
			return null;
		
		return getMergeScheduleTime(vmInstanceUUID, 
				this.getMergeJobStatus(vmInstanceUUID), 
				this.getRetentionPolicy(vmInstanceUUID));
	}
	
	@Override
	protected String getMergeJobName(String vmInstanceUUID) {
		return VSPHERE_MERGE_JOB_NAME + "_" + vmInstanceUUID;
	}

	@Override
	public synchronized void updateMergeJobStatus(MergeStatus status) {
		String vmInstanceUUID = status.getUUID();
		MergeStatus ms = currentStatus.get(vmInstanceUUID);
		ms = status;
		ms.setJobType(Constants.JOBTYPE_VM_MERGE);
		ms.setUpdateTime(System.currentTimeMillis());
		currentStatus.put(vmInstanceUUID, ms);
		setChanged();
//		this.reportMergeJobMonitor(status, vmInstanceUUID);
		this.notifyObservers(currentStatus.get(vmInstanceUUID));
	}

	public synchronized MergeStatus getMergeJobStatus(String vmInstanceUUID) {
		return currentStatus.get(vmInstanceUUID);
	}
	
	public synchronized MergeStatus[] getMergeStatusList() {
		return currentStatus.values().toArray(new MergeStatus[0]);
	}
	
	public synchronized int pauseMerge(MergeAPISource source, String vmInstanceUUID)
			throws ServiceException {
		logger.debug("Enter pause merge: source " + source
				+ "vmInstanceUUID " + vmInstanceUUID);
		int ret = 0;
		if(source == null) {
			ret = pauseMerge(MergeEvent.WS_STOP, vmInstanceUUID, null, null);
		}else {
			switch(source) {
			case MANUALLY:
				ret = pauseMerge(MergeEvent.MANUAL_STOP, vmInstanceUUID, null, null);
				break;
			case ASBU_BACKUP:
				ret = pauseMerge(MergeEvent.WS_STOP, vmInstanceUUID, null, ASBU_JOB);
				break;
			default:
				ret = pauseMerge(MergeEvent.WS_STOP, vmInstanceUUID, null, null);
				break;
			}
		}

		logger.debug("End pause merge " + vmInstanceUUID + " source  is " + source);
		return ret;
	}
	
	//all other job should first call pause merge, then call this method to wait for job end.
	public void waitForJobEnd(String vmInstanceUUID) {
		VSphereMergeJob job = getMergeJob(vmInstanceUUID);
		if(job == null) {
			logger.warn("No merge job for VM " + vmInstanceUUID);
		}else {
			job.waitJobEnd();
		}
	}

	public int checkForResume(MergeEvent event, String vmInstanceUUID) throws ServiceException {
		if(getMergeJob(vmInstanceUUID) != null){
			logger.warn("Merge is running no need to start");
			return -1;
		}
		logger.debug("Enter check resume VM merge " + vmInstanceUUID);
		int ret = this.checkForResume(event, currentStatus.get(vmInstanceUUID), 
				JobMonitorService.getInstance().getVMJobMonitorMap(vmInstanceUUID), 
				otherJobs.get(vmInstanceUUID), getMergeJob(vmInstanceUUID), vmInstanceUUID);		
		logger.debug("Exit check resume VM merge " + vmInstanceUUID);
		return ret;
	}
	
	@Override
	public boolean canStartMerge(String vmInstanceUUID) {
		return !isOtherJobRunning(JobMonitorService.getInstance().getVMJobMonitorMap(vmInstanceUUID), 
				otherJobs.get(vmInstanceUUID));
	}
	
	/**
	 * No matter to start new merge job or resume paused merge, we will generate
	 *  a new job id and start a new merge job  
	 * @param vmInstanceUUID
	 * @return
	 */
	public synchronized int resumeVMMerge(MergeAPISource source, String vmInstanceUUID) 
		throws ServiceException {
		logger.debug("Enter resume merge: vmInstanceUUID " + vmInstanceUUID + " source " + source);
		if(backupToRPS(vmInstanceUUID))
			return 0;
		
		int ret = 0;
		if(source == null) {
			ret = resumeVMMerge(MergeEvent.WS_RESUME, vmInstanceUUID);
		}else {
			switch(source) {
			case MANUALLY:
				ret = resumeVMMerge(MergeEvent.MANUAL_RESUME, vmInstanceUUID);
				break;
			case ASBU_BACKUP:
				jobEnd(vmInstanceUUID, ASBU_JOB);
				ret = resumeVMMerge(MergeEvent.WS_RESUME, vmInstanceUUID);
				break;
			default:
				ret = resumeVMMerge(MergeEvent.WS_RESUME, vmInstanceUUID);
				break;
			}
		}
		
		logger.debug("End resume merge");
		return ret;
	}
	
	@Override
	public boolean isInMergeTimeRange(String vmInstanceUUID) {
		Calendar currentTime = Calendar.getInstance();
		int dayofWeek = currentTime.get(Calendar.DAY_OF_WEEK);

		List<DailyScheduleDetailItem> mergeSchedules = this.getMergeSchedule(vmInstanceUUID);
		// If no schedule, allow merge at any time
		if(mergeSchedules == null || mergeSchedules.size() == 0) {	
			return true;
		}
		else {
			boolean hasSchedule = false;
			for (DailyScheduleDetailItem mergeSchedule : mergeSchedules) {
				ArrayList<MergeDetailItem> mergeDetailItems = mergeSchedule.getMergeDetailItems();
				if (mergeDetailItems != null && mergeDetailItems.size() != 0)
					hasSchedule = true;
			}
			if (!hasSchedule)
				return true;
		}

		boolean isInTimeRange = false;
		loop : for (DailyScheduleDetailItem mergeSchedule : mergeSchedules) {
			if (mergeSchedule.getDayofWeek() == dayofWeek) {
				ArrayList<MergeDetailItem> mergeDetailItems = mergeSchedule.getMergeDetailItems();
				if (mergeDetailItems != null) {
				    for (MergeDetailItem mergeDetailItem : mergeDetailItems) {
				    	isInTimeRange = isInDSTTimeRange(mergeDetailItem.getStartTime().getHour(), mergeDetailItem.getStartTime().getMinute(),
				    			mergeDetailItem.getEndTime().getHour(), mergeDetailItem.getEndTime().getMinute());
				    	
				    	if (isInTimeRange)
				    		break loop;
				    }
				}
			}
		}
	    return isInTimeRange;
	}
	
	public MergeJobMonitor getMergeJobMonitor(String vmInstanceUUID) {		
		return currentStatus.get(vmInstanceUUID).getJobMonitor();	
	}	
	
	public MergeJobMonitor[] getMergeJobMonitorList() {
		List<MergeJobMonitor> jobMonitors = new ArrayList<MergeJobMonitor>();
		for(MergeStatus status : currentStatus.values()){
			if(status.getJobMonitor() != null)
				jobMonitors.add(status.getJobMonitor());
		}
		return jobMonitors.toArray(new MergeJobMonitor[0]);
	}
	
	public MountSession[] getMountedSessionsToMerge(String vmInstanceUUID) {
		if(backupToRPS(vmInstanceUUID))
			return null;
		
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vmInstanceUUID);
		try {
			VMBackupConfiguration configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if(configuration == null) return null;
			RetentionPolicy retentionPolicy = configuration.getRetentionPolicy();
			return this.getMountedSessionsToPurge(vmInstanceUUID, configuration.getBackupVM().getDestination(), 
					configuration.getUserName(), configuration.getPassword(), retentionPolicy, configuration.getRetentionCount());
		}catch(ServiceException e){
			logger.error("Failed to get vm backup configuration for " + vmInstanceUUID);
			return null;
		}
	}
	
	@Override
	protected void logWithID(long level, long jobId, String msg,
			String vmInstanceUUID) {
		if(jobId > 0)
			this.getNativeFacade().addVMLogActivityWithJobID(level, jobId, 
					Constants.AFRES_AFJWBS_GENERAL, new String[]{
					msg, "", "","",""},
					vmInstanceUUID);
		else 
			this.getNativeFacade().addVMLogActivity(level, Constants.AFRES_AFJWBS_GENERAL, 
					new String[]{msg, "", "","",""}, vmInstanceUUID);
	}

	public void mergeStart(VSphereMergeJob job) {
		currentJobs.put(job.getVMInstanceUUID(), job);
	}
	
	@Override
    public synchronized void mergeDone(AbstractMergeJob job) {		
		String vmInstanceUUID = ((VSphereMergeJob)job).getVMInstanceUUID();
		mergeDone(job, getMergeJobStatus(vmInstanceUUID));
//		saveMergeStatus(getMergeJobStatus(vmInstanceUUID), getStatusPath(vmInstanceUUID));
		currentJobs.remove(vmInstanceUUID);
    }
	
	//wanqi06
	public void resumeJobAfterWSRestart(int jobID, String vmUUID){
		/*try {
			startMergeJob(null, null, jobID, vmUUID, VSPHERE_MERGE_JOB_NAME + "_" + vmUUID, 
					VSphereMergeJob.class);
		}catch(ServiceException e) {
			logger.error("Fail to start VSphere merge job. " + e);
		}*/
		VSphereMergeJob job = new VSphereMergeJob(vmUUID, jobID);
		this.mergeStart(job);
		VSphereMergeJob.pool.submit(job);
	}
	
	public void scheduleMergeJob(BackupVM backupVM ){
		if(backupToRPS(backupVM.getInstanceUUID()))
			return ;
		VirtualMachine vm = new VSphereConverter().ConvertToVirtuaMachine(backupVM);
		
		VSphereBackupConfiguration backupConf = null;

		VMBackupConfiguration vmBackupConf = null;
		try {
			vmBackupConf = VSphereService.getInstance().getVMBackupConfiguration(vm);
		} catch (ServiceException e) {
			logger.error("Failed to get VM backup configruation", e);
		}
		if(vmBackupConf != null) {
			if (vmBackupConf.isDisablePlan()) {
				unschedule(backupVM.getInstanceUUID());
				return;
			}
			
			if (vmBackupConf.getGenerateType() == GenerateType.MSPManualConversion) {
				logger.info("The configuration is for remote nodes.");
				return;
			}
			
			backupConf = vspherebackupConfigurationXMLDAO.VMConfigToVSphereConfig(vmBackupConf);
					
	    	if(backupConf != null && backupConf.getAdvanceSchedule() != null && backupConf.getAdvanceSchedule().getDailyScheduleDetailItems() != null) {
	    		newScheduleMergeJob(backupVM);
	    	}
	    	else {
	    		oldScheduleMergeJob(backupVM);
	    	}
		}
	}
	
	private void oldScheduleMergeJob(BackupVM backupVM) {
		unschedule(backupVM.getInstanceUUID());
		
		scheduleMergeJob(new MergeJobContext(getMergeJobName(backupVM.getInstanceUUID()), VSphereMergeJob.class, null, backupVM.getInstanceUUID()));
		
		if(this.isInMergeTimeRange(backupVM.getInstanceUUID())) {
			try {
				resumeVMMerge(MergeEvent.SCHEDULE_BEGIN, backupVM.getInstanceUUID());
			}catch(Exception e) {
				logger.error("Failed to start merge job", e);
			}
		}
	}
	
	private void newScheduleMergeJob(BackupVM backupVM){
		logger.info("Schedule merge job");
		
		unschedule(backupVM.getInstanceUUID());
		
		newScheduleMergeJob(new MergeJobContext(getMergeJobName(backupVM.getInstanceUUID()), VSphereMergeJob.class, null, backupVM.getInstanceUUID()));
		
		if(this.isInMergeTimeRange(backupVM.getInstanceUUID())) {
			try {
				resumeVMMerge(MergeEvent.SCHEDULE_BEGIN, backupVM.getInstanceUUID());
			}catch(Exception e) {
				logger.error("Failed to start merge job", e);
			}
		}
		
	    return;
	}
	
	public void fixMergeStatusAfterRestart() {
		if (!StringUtil.isExistingPath(ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath())) {
			return;
		}
		String configurationPath = ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath();
		File file = new File(configurationPath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		
		for (File one : files) {
			BackupVM tempVM = new BackupVM();
			String filename = one.getName();
			String instanceUUID = new String();
			instanceUUID = filename.substring(0, filename.lastIndexOf('.'));
			tempVM.setInstanceUUID(instanceUUID);
			this.fixMergeStatusAfterRestart(this.getMergeJob(instanceUUID),
					this.getMergeJobStatus(instanceUUID), instanceUUID);
		}
	}	
	
	@Override
	public void startNewJobAfterDone(String vmInstanceUUID) {
		if(backupToRPS(vmInstanceUUID))
			return ;
		
		super.startNewJobAfterDone(vmInstanceUUID);
		try {
			VSphereMergeService.getInstance().resumeVMMerge(MergeEvent.SCHEDULE_BEGIN, vmInstanceUUID);
		}catch(ServiceException se) {
			logger.error("Failed to start merge job for " 
					+ vmInstanceUUID + " error is " + se.getMessage() );
		}
	}

	@Override
    public synchronized long startMerge(AbstractMergeJob job) {
		if(backupToRPS(job.getVMInstanceUUID())){
			mergeDone(job);
			return 0;
		}
		
		long ret = -1;
		try {
			this.logResumeActivity(job.getMergeEvent(), job.getJobId(), job.getVMInstanceUUID());
			ret = getNativeFacade().startMerge(job.getMergeJobScript());
			if(ret != 0) {
				logger.error("Failed to start merge with error code " + ret);
				this.logWithID(Constants.AFRES_AFALOG_ERROR, 
						job.getJobId(), 
						WebServiceMessages.getResource("startMergeFailed", String.valueOf(ret)), 
						job.getVMInstanceUUID());
				mergeDone(job);
			}
//			this.clearMergeStatus(getStatusPath(job.getVMInstanceUUID()));
		}catch(ServiceException se) {
			logger.error("Failed to start merge", se);
		}
		
		return ret;
    }

	public synchronized VSphereMergeJob getMergeJob(String vmInstanceUUID) {
		return currentJobs.get(vmInstanceUUID);
	}
	
	public boolean isJobRunning(String vmInstanceUUID) {
		return getMergeJob(vmInstanceUUID) != null;
	}

	@Override
	public RetentionPolicy getRetentionPolicy(String vmInstanceUUID) {
		RetentionPolicy policy = null;
		try {
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(vmInstanceUUID);
			VMBackupConfiguration vmBackupConf = VSphereService.getInstance()
				.getVMBackupConfiguration(vm);
			if(vmBackupConf != null) {
				policy = vmBackupConf.getRetentionPolicy();
				if(policy == null){
					policy = new RetentionPolicy();
					policy.setUseBackupSet(false);
					policy.setUseTimeRange(false);
					vmBackupConf.setRetentionPolicy(policy);
				}
			}
		}catch(Exception e) {
			logger.error("Failed to get retention policy");
		}
		return policy;
	}
	

	@Override
	public List<DailyScheduleDetailItem> getMergeSchedule(String vmInstanceUUID) {
		List<DailyScheduleDetailItem> dailyScheduleDetailItems = null;
		VirtualMachine virtualMachine = new VirtualMachine();
		virtualMachine.setVmInstanceUUID(vmInstanceUUID);
		VMBackupConfiguration configuration = null;
		try {
			configuration = vspherebackupConfigurationXMLDAO.get(ServiceContext.getInstance().getVsphereBackupConfigurationFolderPath(), virtualMachine);
		} catch (Exception e) {
			logger.error("Failed to get vsphere backup configuration");
		}
		
		if(configuration != null && configuration.getAdvanceSchedule() != null) {
			dailyScheduleDetailItems = configuration.getAdvanceSchedule().getDailyScheduleDetailItems();
		}
		
		return dailyScheduleDetailItems;
	}
	
	public void scheduleAllVMMergeJob() {
		logger.debug("configJobSchedule() - start");
		// BackupVM[] backupVMArray = configuration.getBackupVMList();
		// for(BackupVM backupVM : backupVMArray){
		if (!StringUtil.isExistingPath(ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath())) {
			return;
		}
		String configurationPath = ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath();
		File file = new File(configurationPath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		
		for (File one : files) {
			BackupVM tempVM = new BackupVM();
			String filename = one.getName();
			String instanceUUID = new String();
			instanceUUID = filename.substring(0, filename.lastIndexOf('.'));
			if(instanceUUID != null && instanceUUID.startsWith(VSphereService.VMCONFIG_PREFIX))
				continue;
			tempVM.setInstanceUUID(instanceUUID);
			this.scheduleMergeJob(tempVM);
		}
	}

	public synchronized int pauseMerge(MergeEvent event, String vmInstanceUUID, String source, Object otherJob) 
		throws ServiceException {
		logger.debug("Enter pause merge:"  
				+ "vmInstanceUUID " + vmInstanceUUID);
		if(useBackupSet(vmInstanceUUID)){
			logger.warn("Don't allow stop for backup set");
			return 0;
		}
		if(otherJob != null){
			newJobStart(vmInstanceUUID, otherJob);
		}
			
		int ret = pauseMerge(getMergeJobStatus(vmInstanceUUID), 
				getMergeJob(vmInstanceUUID), event, source);
		
		logger.debug("End pause merge");
		return ret;
	}
	
	
	
	public synchronized int resumeVMMerge(MergeEvent event, String vmInstanceUUID) 
		throws ServiceException {
		logger.debug("Enter resume merge: vmInstanceUUID " + vmInstanceUUID);
		if(backupToRPS(vmInstanceUUID))
			return 0;
		
		if (event != MergeEvent.MANUAL_RESUME && isPlanDisabled(vmInstanceUUID)) {
			logger.info("Merge job for " + vmInstanceUUID + " is skipped because the plan is disabled");
			return 0;
		}
		
		if(this.isJobRunning(vmInstanceUUID)){
			logger.info("Merge job is already running for " + vmInstanceUUID);
			return 0;
		}
		//check whether need to start merge job
		if(!this.isMergeJobAvailable(vmInstanceUUID)){
			if(event != MergeEvent.MANUAL_RESUME)
				return 0;
			else{
				throw new ServiceException(WebServiceMessages.getResource("mergeNoNeedResume"),
						FlashServiceErrorCode.Common_General_Message);
			}
		}
		
		if(checkForResume(event, vmInstanceUUID) != 0)
			return 0;
		
		MergeStatus status = getMergeJobStatus(vmInstanceUUID);
		int needResume = this.canResumeMerge(event, status);
		if(needResume == -1){
			//already running or torun.
			return 0;
		}
		
		startMergeJob(new MergeJobContext(getMergeJobName(vmInstanceUUID),
				VSphereMergeJob.class, event, vmInstanceUUID));
		
		status.setCanResume(false);
		status.setRecoverySet(useBackupSet(vmInstanceUUID));
		this.updateMergeJobStatus(status);
		logger.debug("End resume merge");
		return 0;
	}
	
	public int canResumeMerge(MergeEvent event, String vmInstanceUUID) throws ServiceException {
		MergeStatus status = getMergeJobStatus(vmInstanceUUID);
		return this.canResumeMerge(event, status);
	}
	
	private void newJobStart(String vmInstanceUUID, Object obj) {
		if(vmInstanceUUID == null || vmInstanceUUID.isEmpty())
			return;

		Set<Object> jobs = otherJobs.get(vmInstanceUUID);
		if(jobs == null)
			jobs = new HashSet<Object>();
		jobs.add(obj);
		otherJobs.put(vmInstanceUUID, jobs);
	}
	
	public synchronized void jobEnd(String vmInstanceUUID, Object obj) {
		if(vmInstanceUUID == null || vmInstanceUUID.isEmpty())
			return;

		Set<Object> jobs = otherJobs.get(vmInstanceUUID);
		if(jobs == null)
			return;
		jobs.remove(obj);
		otherJobs.put(vmInstanceUUID, jobs);
	}
	
	@Override
	protected long checkMergeJobAvailableForRecoveryPoints(String vmInstanceUUID) {
		try {
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(vmInstanceUUID);
			VMBackupConfiguration configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if(configuration == null) return 1;
			RetentionPolicy retentionPolicy = configuration.getRetentionPolicy();
			if(retentionPolicy.isUseBackupSet()) 
				return 1;
			return this.isMergeJobAvailable(vmInstanceUUID)? 0:1;
		}catch(Exception e) {
			logger.error("Failed to check recovery point number " + e);
			return 2;
		}
	}

	public boolean isMergeJobAvailable(String vmInstanceUUID) {
		try {
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(vmInstanceUUID);
			VMBackupConfiguration configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if(configuration == null) return false;
			if (configuration.getGenerateType() == GenerateType.MSPManualConversion) {
				if (logger.isInfoEnabled()) {
					logger.info("The vm:" + vmInstanceUUID + " is remote node, so skip merge job for remote ndoe.");
				}
				return false;
			}
			
			int dailyCount = 0;
			int weeklyCount = 0;
			int monthlyCount = 0;
			if(configuration.getAdvanceSchedule() !=null &&configuration.getAdvanceSchedule().getPeriodSchedule() != null && configuration.getAdvanceSchedule().getPeriodSchedule().isEnabled()){
				
				EveryDaySchedule daySchedule = configuration.getAdvanceSchedule().getPeriodSchedule().getDaySchedule();					
				
				if(daySchedule != null && daySchedule.isEnabled()){
					dailyCount = daySchedule.getRetentionCount();							
				}
				
				EveryWeekSchedule weekSchedule  = configuration.getAdvanceSchedule().getPeriodSchedule().getWeekSchedule();
				if(weekSchedule != null && weekSchedule.isEnabled()){
					weeklyCount = weekSchedule.getRetentionCount();							
				}
				
				EveryMonthSchedule monthSchedule  = configuration.getAdvanceSchedule().getPeriodSchedule().getMonthSchedule();
				if(monthSchedule != null && monthSchedule.isEnabled()){
					monthlyCount = monthSchedule.getRetentionCount();							
				}			 
			}
			
			RetentionPolicy retentionPolicy = configuration.getRetentionPolicy();
			return this.isMergeJobAvailableEx(vmInstanceUUID, getMergeJobStatus(vmInstanceUUID),
					configuration.getBackupVM().getDestination(),
					configuration.getUserName(), configuration.getPassword(),
					retentionPolicy,
					configuration.getRetentionCount(), dailyCount,weeklyCount,monthlyCount);
		}catch(Exception e) {
			logger.error("Failed to check recovery point number " + e);
			return false;
		}
	}
	
	private boolean useBackupSet(String vmInstanceUUID) {
		RetentionPolicy policy = this.getRetentionPolicy(vmInstanceUUID);
		if(policy != null && policy.isUseBackupSet())
			return true;
		else
			return false;
	}
	
	private void initialize( ){
		if (!StringUtil.isExistingPath(ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath())) {
			return;
		}
		String configurationPath = ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath();
		File file = new File(configurationPath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		
		for (File one : files) {
			String filename = one.getName();
			String instanceUUID = new String();
			instanceUUID = filename.substring(0, filename.lastIndexOf('.'));
			if(instanceUUID != null && instanceUUID.startsWith(VSphereService.VMCONFIG_PREFIX))
				continue;
			MergeStatus status = new MergeStatus();
			status.setUUID(instanceUUID);
			status.setVmInstanceUUID(instanceUUID);
			status.setJobType(Constants.JOBTYPE_VM_MERGE);
			currentStatus.put(instanceUUID, status);
			MergeStatus lstatus = this.loadMergeStatus(getStatusPath(instanceUUID));
			this.clearMergeStatus(getStatusPath(instanceUUID));
			if(lstatus != null){
				currentStatus.put(instanceUUID, lstatus);
			}
			Set<Object> jobs = new HashSet<Object>();
			otherJobs.put(instanceUUID, jobs);
		}
	}
	
	private synchronized String getStatusPath(String uuid) {
		StringBuilder sb = new StringBuilder(mergeStatusFolderPath);
		sb.append("\\");
		sb.append(uuid);
		sb.append("_mergestatus");
		return sb.toString();
	}
	
	public void initializeMergeStatus(String vmInstanceUUID) {
		MergeStatus status = new MergeStatus();
		status.setUUID(vmInstanceUUID);
		status.setVmInstanceUUID(vmInstanceUUID);
		status.setJobType(JobType.JOBTYPE_VM_MERGE);
		currentStatus.put(vmInstanceUUID, status);
	}
	
	public void removeVMStatus(String vmInstanceUUID) {
		currentStatus.remove(vmInstanceUUID);
	}

	@Override
	public void saveMergeStatus() {
		for(Map.Entry<String, MergeStatus> entry : currentStatus.entrySet()) {
			saveMergeStatus(entry.getValue(), getStatusPath(entry.getKey()));
		}
		
		logger.info("Exit VSphereMergeService.saveMergeStatus");
	}

	@Override
	public void sendEmailOnMergePausedManually(String vmInstanceUUID) {
		logger.debug("Begin sendEmailOnMergePausedManually " + vmInstanceUUID);
		if(vmInstanceUUID == null || vmInstanceUUID.isEmpty()){
			logger.warn("VMInstanceUUID is invalid, return");
			return;
		}
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vmInstanceUUID);
		try {
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
			
			//If we need to start merge job, while it's paused by manually, we will send a email
			MergeStatus currentStatus = this.getMergeJobStatus(vmInstanceUUID);
			if(email.isEnableEmailOnMergeFailure() &&
					currentStatus != null && currentStatus.getStatus() == MergeStatus.Status.PAUSED_MANUALLY 
					&& this.isMergeJobAvailable(vmInstanceUUID)){
				EmailSender emailSender = new EmailSender();				
				String emailJobStatus = WebServiceMessages.getResource("EmailJobStatus");
				String jobStatus = WebServiceMessages.getResource("mergeJobStatusSkipped");
				
				String vmName = configuration.getBackupVM().getVmName();
				String nodeName = configuration.getBackupVM().getVmHostName();
				if(nodeName==null||nodeName.isEmpty()){
					nodeName = WebServiceMessages.getResource("EmailNodeNameUnknown");
				}
				String emailSubject = WebServiceMessages.getResource("EmailSubject",
							email.getSubject(), WebServiceMessages.getResource("mergeJobString") 
						 + " " + emailJobStatus+jobStatus ,vmName, nodeName);				
								
				String content = WebServiceMessages.getResource("mergeJobPausedEmailAlert");
				
				emailSender.sendEmail(email, emailSubject, content, Constants.JOBSTATUS_SKIPPED, true, 
						CommonEmailInformation.PRODUCT_TYPE.VSPHERE.getValue(), JobType.JOBTYPE_VM_MERGE, vmName );
			}
		}catch(Exception e) {
			logger.error("Failed to send email for manually pause");
		}
		logger.debug("End sendEmailOnMergePausedManually " + vmInstanceUUID);
	}

	@Override
	public String getRPSPolicyUUID(String vmInstanceUUID) {
		return VSphereService.getInstance().getRpsPolicyUUID(vmInstanceUUID);
	}

	@Override
	public String getDataStoreUUID(String vmInstanceUUID) {
		return VSphereService.getInstance().getRpsDataStoreUUID(vmInstanceUUID);
	}
	
	private boolean backupToRPS(String vmInstanceUUID) {
		return VSphereService.getInstance().isBackupToRPS(vmInstanceUUID);
	}
	
	public void unSchduleAllVM() {
		if (!StringUtil.isExistingPath(ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath())) {
			return;
		}
		String configurationPath = ServiceContext.getInstance()
				.getVsphereBackupConfigurationFolderPath();
		File file = new File(configurationPath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		
		for (File one : files) {
			String filename = one.getName();
			String instanceUUID = new String();
			instanceUUID = filename.substring(0, filename.lastIndexOf('.'));
			if(instanceUUID != null && instanceUUID.startsWith(VSphereService.VMCONFIG_PREFIX))
				continue;
			unschedule(instanceUUID);
		}
	}
	
	protected boolean isPlanDisabled(String vmInstanceUUID) {
		return VSphereService.getInstance().isPlanDisabled(vmInstanceUUID);
	}
}

package com.ca.arcflash.webservice.service;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.AbstractTrigger;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.service.jni.model.JRestorePoint;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.EveryMonthSchedule;
import com.ca.arcflash.webservice.data.EveryWeekSchedule;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveJobScript;
import com.ca.arcflash.webservice.data.archive.JArchiveJob;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.RetryPolicy;
import com.ca.arcflash.webservice.data.job.rps.ArchiveJobArg;
import com.ca.arcflash.webservice.data.job.rps.IJobDependency;
import com.ca.arcflash.webservice.data.job.rps.JobDependencySource;
import com.ca.arcflash.webservice.scheduler.AdvancedScheduleTrigger;
import com.ca.arcflash.webservice.scheduler.ArchiveBackupJob;
import com.ca.arcflash.webservice.scheduler.ArchiveSourceDeleteJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.internal.ArchiveJobConverter;
import com.ca.arcflash.webservice.service.internal.ArchiveSourceDeleteConfigurationXMLDAO;
import com.ca.arcflash.webservice.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.webservice.service.validator.ArchiveConfigurationValidator;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.ServiceUtils;

public class DeleteArchiveService extends BaseService implements IJobDependency {

	private static final Logger logger = Logger.getLogger(DeleteArchiveService.class);
	private static final DeleteArchiveService delArchiveInstance = new DeleteArchiveService();
	private ArchiveJobConverter jobConverter = new ArchiveJobConverter();
	private ArchiveSourceDeleteConfigurationXMLDAO archiveDelConfigurationXMLDAO = new ArchiveSourceDeleteConfigurationXMLDAO();
	private ArchiveConfigurationValidator archiveConfigurationValidator = new ArchiveConfigurationValidator();
	private Scheduler scheduler;
	private Set<String> jobNames4Adv = new HashSet<String>();
	private Set<String> jobNames4Makeup = new HashSet<String>();
	private Set<String> deleteMakeupJobNames = new HashSet<String>();
	private Object lock = new Object();
	private ArchiveConfiguration archiveDelConfiguration;
	
	public synchronized void addDeleteMakeupJobName(String name){
		deleteMakeupJobNames.add(name);
	}
	
	public synchronized void cleanDeleteMakeupJobSchedule(){
		try {
			for(String name : deleteMakeupJobNames){
				scheduler.deleteJob(new JobKey(name, BaseService.JOB_GROUP_ARCHIVE_DELETE_MAKEUP));
			}
			deleteMakeupJobNames.clear();
		} catch (SchedulerException e) {
			logger.error("Failed to delete archive source delete schedule "
					+ e.getMessage());
		}
	}
	
	public boolean isDeleteMakeupJob(){
		return !deleteMakeupJobNames.isEmpty();
	}
	
	public synchronized void addJobName(String name){
		jobNames4Adv.add(name);
	}
	
	public synchronized void cleanAdvanceSchedule(){
		try {
			for(String name : jobNames4Adv){
				scheduler.deleteJob(new JobKey(name, BaseService.JOB_GROUP_ARCHIVE_SOURCEDELETE));
			}
			jobNames4Adv.clear();
		} catch (SchedulerException e) {
			logger.error("Failed to delete File Archive schedule "
					+ e.getMessage());
		}
	}
	
	public synchronized void addMakeupJobName(String name){
		jobNames4Makeup.add(name);
	}
	
	public synchronized void deleteMakeupSchedule(){
		try {
			for (String name : jobNames4Makeup) {
				scheduler.deleteJob(new JobKey(name,
						BaseService.JOB_GROUP_ARCHIVE_SOURCEDELETE_MAKEUP));
			}
			jobNames4Makeup.clear();
		} catch (SchedulerException e) {
			logger.error("Failed to delete File Archive makeup schedule "
					+ e.getMessage());
		}
	}
	
	public List<Trigger> getMakeupTriggers(){
		List<Trigger> triggerList = new ArrayList<Trigger>();
		try {
			String[] triggerGroupNames = new String[]{BaseService.TRIGGER_GROUP_ARCHIVE_SOURCEDELETE_MAKEUP};
			for(String triggerGroupName : triggerGroupNames){
				String[] triggerNames = ScheduleUtils.getTriggerNames(scheduler, triggerGroupName);
				if(triggerNames == null)
					continue;
				for(String triggerName : triggerNames){
					Trigger tirgger = scheduler.getTrigger(new TriggerKey(triggerName, triggerGroupName));
					triggerList.add(tirgger);
				}
			}
		} catch (Exception e) {
			logger.error("get makeup triggers error " + e.getMessage());
		}
		return triggerList;
	}
	
	@Override
	public boolean needRun(JobDependencySource source) {
		return false;
	}

	private DeleteArchiveService(){
		try {			
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
	}
	
	public Scheduler getScheduler(){
		return scheduler;
	}
	
	public ArchiveConfiguration getArchiveDelConfiguration() throws ServiceException{
		logger.debug("get file archive configuration - start");
		
		if(BackupService.getInstance().getBackupConfiguration() == null){
			logger.error("There is no backup configuration, return error code");
			throw generateAxisFault(FlashServiceErrorCode.Backup_NoBackupConfiguration);
		}
		try {
			synchronized(lock){
				WindowsRegistry registry = new WindowsRegistry();
				int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
				String sAuto = registry.getValue(handle, "Auto");
				registry.closeKey(handle);
				registry = null;
				boolean bAutoEnabled = false;
				if(sAuto != null)
				{ 
					if(sAuto.equalsIgnoreCase("0") || sAuto.equalsIgnoreCase("1"))				
					bAutoEnabled = Integer.parseInt(sAuto) == 1 ? true : false;			
					else
					bAutoEnabled = false;
				}
				if(archiveDelConfiguration == null || bAutoEnabled){
					if(StringUtil.isExistingPath(ServiceContext.getInstance().getArchiveSourceDeleteConfigurationFilePath())){
						archiveDelConfiguration = archiveDelConfigurationXMLDAO.load(ServiceContext.getInstance().getArchiveSourceDeleteConfigurationFilePath());
					}
				}
			}
			
			logger.debug("get file archive configuration - end");
			return archiveDelConfiguration;
		} catch (Throwable e) {
			logger.error("get file archive configuration failed: ", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	public void setArchiveDelConfigurationFromUpgrade(ArchiveConfiguration archiveDelConfig){
		archiveDelConfiguration = archiveDelConfig;
	}
	
	public static DeleteArchiveService getInstance() {
		return delArchiveInstance;
	}
	//this is for manually submitting file archive job 
	public long submitArchiveSrcDeleteJob(){
		logger.debug("check submit archive source copy job manually");
		
		SimpleTriggerImpl trigger = new SimpleTriggerImpl();
		Calendar cal = Calendar.getInstance();
		Date startDate = cal.getTime();
		trigger.setStartTime(startDate);
		trigger.setRepeatCount(0);
		trigger.setRepeatInterval(0);
		trigger.setName(TRIGGER_NAME_ARCHIVE_SOURCEDELETE);
		trigger.setGroup(TRIGGER_GROUP_ARCHIVE_SOURCEDELETE);
		
		String jobName = JOB_NAME_ARCHIVE_SOURCEDELETE + "_" + Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME;
		JobDetail jobDetail = new JobDetailImpl(jobName, JOB_GROUP_ARCHIVE_SOURCEDELETE, ArchiveBackupJob.class);
		addMakeupJobName(jobName);
		jobDetail.getJobDataMap().put(BaseService.JOB_TYPE, JobType.JOBTYPE_FILECOPY_SOURCEDELETE);
		
		try {
			scheduler.scheduleJob(jobDetail, trigger);
			logger.info("manual schedule file archive source copy job success");
			return 0;
		} catch (SchedulerException e) {
			logger.error("re-schedule file archive source copy job error" + e.getMessage());
		}
		return 0;
	}
		
		
	public long submitArchiveSourceDeleteJob(){
		logger.debug("check submit archive source delete job");
		long ret = -1;
		try {
			JArchiveJob out_archiveJob = new JArchiveJob();
			if(checkSubmitArchiveSourceDeleteJob(out_archiveJob)){
				logger.debug("generate archive job arg");
				ArchiveJobArg jobArg = generateArchiveJobArg(out_archiveJob);
				return submitArchiveSourceDeleteJob(jobArg);
			}
			logger.info("there is no archive source delete job available to submit");
		} catch (ServiceException e) {
			logger.error("failed to submit archive source delete job from D2D : " + e.getMessage());
		}
		return ret;		
	}

	public long submitArchiveSourceDeleteJob(ArchiveJobArg jobArg) throws ServiceException{
		logger.debug("archive sourcedelete() - start");
		
		long ret = -1;
		
		if (scheduler == null){
			logger.error("Failed to find the Scheduler to submit the source delete job.");
			return ret;
		}

		try {
			JobDetailImpl jobDetail = new JobDetailImpl(JOB_NAME_ARCHIVE_DELETE, JOB_GROUP_ARCHIVE_DELETE, ArchiveSourceDeleteJob.class);
			jobDetail.getJobDataMap().put("JobScript", jobArg.getJobScript());
			jobDetail.getJobDataMap().put(BaseService.JOB_TYPE, jobArg.getJobType());
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0,0);
			trigger.setName(TRIGGER_NAME_ARCHIVE_DELETE);
			trigger.setGroup(TRIGGER_GROUP_ARCHIVE_DELETE);
			scheduler.scheduleJob(jobDetail, trigger);
			
			checkForMergeRunning(ServiceUtils.jobType2String(Constants.AF_JOBTYPE_ARCHIVE_SOURCEDELETE, 0));
			
			ret = 0;//success
			logger.info("submit Archive Source Delete job - end");
		} catch(ServiceException se) {
			throw se;
		}catch(org.quartz.ObjectAlreadyExistsException alreadyExistsEx){
			logger.info("Another job is runnign, throwing org.quartz.ObjectAlreadyExistsException in submitArchiveSourceDeleteJob");
			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
		}
		catch (Throwable e) {
			logger.error("archive submitArchiveSourceDeleteJob()", e);
			throw generateInternalErrorAxisFault();
		}
		return ret;
	}

	public boolean isArchiveSourceDeleteJobRunning() {
		boolean bRunning = false;
		logger.debug("checking whether another delete job is running");
		try {
			logger.debug("calling native facade");
			bRunning = getNativeFacade().isArchiveSourceDeleteJobRunning();
			logger.debug("another Source Delete job status" + bRunning);
		} catch (Throwable e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		logger.debug("another source delete job status" + bRunning);
		return bRunning;
	}
	
	public long saveArchiveDelConfiguration(ArchiveConfiguration archiveDelConfig) throws Exception{
		logger.info("saveArchiveDelConfiguration - start");
		if(BackupService.getInstance().isBackupToRPS()){
			logger.info("is backup to RPS - return");
			return 0;
		}
		
		if(archiveDelConfig.isbArchiveAfterBackup()){
			if(!hasAdvanceSchedule(archiveDelConfig.getAdvanceSchedule())){
				logger.warn("There is no advanced schedule");
				return -1;
			}
			if(archiveDelConfig.isbArchiveAfterBackup()){
				checkCatalogPath(archiveDelConfig);
				archiveConfigurationValidator.validateAdvanceSchedule(archiveDelConfig.getAdvanceSchedule(), false);
				ArchiveService.getInstance().validateArchiveConfiguration(archiveDelConfig);//reuse
				archiveDelConfigurationXMLDAO.save(archiveDelConfig, ServiceContext.getInstance().getArchiveSourceDeleteConfigurationFilePath());
				archiveDelConfiguration = archiveDelConfig;
				createAdvanceSchedule(archiveDelConfig.getAdvanceSchedule());
				PurgeArchiveService.getInstance().configJobSchedule();
			}
		}
		logger.info("saveArchiveDelConfiguration - end");
		return 0;
	}
	
	public long removeArchiveDelConfiguration() {
		logger.info("removeArchiveDelConfiguration - start, at " + new Date());
		long ret = 0;
		String configFilePath = ServiceContext.getInstance()
				.getArchiveSourceDeleteConfigurationFilePath();
		File configFile = new File(configFilePath);
		if (configFile.exists()) {
			boolean removed=false;
			try{
				removed=CommonUtil.tryDeleteFile(configFile);
			}
			catch(Exception e){
				logger.error("Error occur on removing configFile",e);
			}
			
			if (removed) {
				clearCachedConfiguration();
				cleanAdvanceSchedule();
				cleanDeleteMakeupJobSchedule();
			} else {
				logger.error("fail to remove ArchiveDelConfiguration file");
				ret = -1;
			}
		}
		logger.info("removeArchiveDelConfiguration end");
		return ret;
	}
	
	public void configSchedule(){
		try {
			ArchiveConfiguration archiveDelConfig = getArchiveDelConfiguration();
			if(archiveDelConfig == null){
				logger.info("no need to configure schedule, because archive delete configuration is null");
				return;
			}
			if(hasAdvanceSchedule(archiveDelConfig.getAdvanceSchedule()))
				createAdvanceSchedule(archiveDelConfig.getAdvanceSchedule());
		} catch (ServiceException e) {
			logger.error("Failed to initialize archive source delete job, message : " + e.getMessage());
		}
	}
	
	public void createAdvanceSchedule(AdvanceSchedule advanceSchedule){
		logger.info("create advanceSchedule - start");
		if(advanceSchedule == null){
			logger.info("No advanced schedule");
			return;
		}
		
		cleanAdvanceSchedule();
		try {
		JobDetailImpl jobDetail = new JobDetailImpl("regular_" + JOB_NAME_ARCHIVE_SOURCEDELETE,
				JOB_GROUP_ARCHIVE_SOURCEDELETE, ArchiveBackupJob.class);
		jobDetail.setDurability(true);
		jobDetail.getJobDataMap().put(BaseService.JOB_TYPE, JobType.JOBTYPE_FILECOPY_SOURCEDELETE);
		addJobName(jobDetail.getName());
		
		scheduler.addJob(jobDetail, false);
		
		Date startTime = advanceSchedule.getScheduleStartTime() > 0 ? new Date(advanceSchedule.getScheduleStartTime()) : new Date();
		
		List<DailyScheduleDetailItem> dailySchedules = advanceSchedule.getDailyScheduleDetailItems();
		if(dailySchedules != null && dailySchedules.size() > 0){
			int triggerNumber = 1;
			for (DailyScheduleDetailItem dailySchedule : dailySchedules) {
				ArrayList<ScheduleDetailItem> scheduleDetailItems = dailySchedule.getScheduleDetailItems();
				if (scheduleDetailItems != null) {
					for (ScheduleDetailItem scheduleDetailItem : scheduleDetailItems) {
						AbstractTrigger trigger = new AdvancedScheduleTrigger(TRIGGER_NAME_ARCHIVE_SOURCEDELETE + triggerNumber,
								TRIGGER_GROUP_ARCHIVE_SOURCEDELETE, startTime,scheduleDetailItem, dailySchedule.getDayofWeek());
						triggerNumber++;
						trigger.setJobName(jobDetail.getName());
						trigger.setJobGroup(jobDetail.getGroup());
						trigger.setMisfireInstruction(AdvancedScheduleTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
						scheduler.scheduleJob(trigger);
					}
				}
			}
			logger.debug("custom schedule is created");
		}
		
		PeriodSchedule periodSchedule = advanceSchedule.getPeriodSchedule();
		if(periodSchedule != null){
			processPeriodSchedule(jobDetail,periodSchedule);
			logger.debug("period schedule is created");
		}
		} catch (SchedulerException se) {
			logger.error("Failed to schedule file archive job for quartz error : " + se.getMessage());
		} catch (Exception e) {
			logger.error("Failed to schedule file archive job : " + e.getMessage());
		}
		
	}
	
	public void processPeriodSchedule(JobDetailImpl archiveJobDetail, PeriodSchedule periodSchedule) throws SchedulerException, ParseException{
		EveryDaySchedule daySchedule = periodSchedule.getDaySchedule();		
		if (daySchedule != null && daySchedule.isEnabled()) {
			String triggerName = "daily_" + TRIGGER_NAME_ARCHIVE_SOURCEDELETE;
			processDaySchedule(archiveJobDetail, triggerName, TRIGGER_GROUP_ARCHIVE_SOURCEDELETE, daySchedule);
		}
		
		EveryWeekSchedule weekSchedule = periodSchedule.getWeekSchedule();
		if (weekSchedule != null && weekSchedule.isEnabled()) {
			String triggerName = "weekly_" + TRIGGER_NAME_ARCHIVE_SOURCEDELETE;
			processWeekSchedule(archiveJobDetail, triggerName, TRIGGER_GROUP_ARCHIVE_SOURCEDELETE, weekSchedule);
		}
		
		EveryMonthSchedule monthSchedule = periodSchedule.getMonthSchedule();
		if (monthSchedule != null && monthSchedule.isEnabled()) {
			String triggerName = "monthly" + TRIGGER_NAME_ARCHIVE_SOURCEDELETE;
			processMonthSchedule(archiveJobDetail, triggerName, TRIGGER_GROUP_ARCHIVE_SOURCEDELETE, monthSchedule);
		}
	}
	
	private void processDaySchedule(JobDetailImpl archiveJobDetail, String triggerName, String triggerGroup, EveryDaySchedule daySchedule) throws SchedulerException, ParseException{
		CronTriggerImpl dailyTrigger = null;
		
		String whichDays = "";
		boolean flag = true;
		String cronExpFormat = "";
		
		if(daySchedule.getDayEnabled()!=null && daySchedule.getDayEnabled().length==7){
			if(daySchedule.getDayEnabled()[0])
				whichDays = whichDays.concat(SUNDAY_FOR_DAILY_SCHEDULE);
			else
				flag = false;
			if(daySchedule.getDayEnabled()[1])
				whichDays = whichDays.concat(MONDAY_FOR_DAILY_SCHEDULE);
			else
				flag = false;
			if(daySchedule.getDayEnabled()[2])
				whichDays = whichDays.concat(TUESDAY_FOR_DAILY_SCHEDULE);
			else
				flag = false;
			if(daySchedule.getDayEnabled()[3])
				whichDays = whichDays.concat(WEDNESDAY_FOR_DAILY_SCHEDULE);
			else
				flag = false;
			if(daySchedule.getDayEnabled()[4])
				whichDays = whichDays.concat(THURSDAY_FOR_DAILY_SCHEDULE);
			else
				flag = false;
			if(daySchedule.getDayEnabled()[5])
				whichDays = whichDays.concat(FRIDAY_FOR_DAILY_SCHEDULE);
			else
				flag = false;
			if(daySchedule.getDayEnabled()[6])
				whichDays = whichDays.concat(SATURDAY_FOR_DAILY_SCHEDULE);
			else
				flag = false;
			
			if(whichDays.length()>0 && whichDays.charAt(whichDays.length() - 1) == ',')
				whichDays = whichDays.substring(0, whichDays.length() - 1);							
			if(flag)
				cronExpFormat = "0 %d %d * * ?";
			else
				cronExpFormat = "0 %d %d ? * " + whichDays;
		}					
		else
			cronExpFormat = "0 %d %d * * ?";

		String cronExp = String.format(cronExpFormat, daySchedule.getDayTime().getMinute(), daySchedule.getDayTime().getHour());
		dailyTrigger = new CronTriggerImpl(triggerName,triggerGroup,cronExp);
		dailyTrigger.setJobName(archiveJobDetail.getName());
		dailyTrigger.setJobGroup(archiveJobDetail.getGroup());
		scheduler.scheduleJob(dailyTrigger);
		addJobName(archiveJobDetail.getName());
	}
	
	private void processWeekSchedule(JobDetailImpl archiveJobDetail, String triggerName, String triggerGroup, EveryWeekSchedule weekSchedule) throws SchedulerException, ParseException{
		CronTriggerImpl weeklyTrigger = null;
		
		String cronExpFormat = "0 %d %d ? * %d";
		String cronExp = String.format(cronExpFormat, weekSchedule.getDayTime().getMinute(), weekSchedule.getDayTime().getHour(),
				weekSchedule.getDayOfWeek());
		weeklyTrigger = new CronTriggerImpl(triggerName,triggerGroup,cronExp);
		weeklyTrigger.setJobName(archiveJobDetail.getName());
		weeklyTrigger.setJobGroup(archiveJobDetail.getGroup());
		scheduler.scheduleJob(weeklyTrigger);
		addJobName(archiveJobDetail.getName());
	}

	private void processMonthSchedule(JobDetailImpl archiveJobDetail, String triggerName, String triggerGroup, EveryMonthSchedule monthSchedule) throws SchedulerException, ParseException{
		CronTriggerImpl monthlyTrigger = null;
		
		String cronExp = "";
		String cronExpFormat = "";
		if (monthSchedule.isDayOfMonthEnabled()) {
			if (monthSchedule.getDayOfMonth() == 32) {
				cronExpFormat = "0 %d %d L * ?";
				cronExp = String.format(cronExpFormat, monthSchedule.getDayTime().getMinute(), monthSchedule.getDayTime().getHour());
			} else {
				cronExpFormat = "0 %d %d %d * ?";
				cronExp = String.format(cronExpFormat, monthSchedule.getDayTime().getMinute(), monthSchedule.getDayTime().getHour(), 
						monthSchedule.getDayOfMonth());
			}
		} else {
			if (monthSchedule.getWeekNumOfMonth() == 0) { // last
				cronExpFormat = "0 %d %d ? * %dL";
				cronExp = String.format(cronExpFormat, monthSchedule.getDayTime().getMinute(), monthSchedule.getDayTime().getHour(), 
						monthSchedule.getWeekDayOfMonth());
			} else {
				cronExpFormat = "0 %d %d ? * %d#%d";
				cronExp = String.format(cronExpFormat, monthSchedule.getDayTime().getMinute(), monthSchedule.getDayTime().getHour(),
						monthSchedule.getWeekDayOfMonth(), monthSchedule.getWeekNumOfMonth());
			}
		}
		monthlyTrigger = new CronTriggerImpl(triggerName,triggerGroup,cronExp);
		monthlyTrigger.setJobName(archiveJobDetail.getName());
		monthlyTrigger.setJobGroup(archiveJobDetail.getGroup());
		scheduler.scheduleJob(monthlyTrigger);
		addJobName(archiveJobDetail.getName());
	}
	
	public boolean hasAdvanceSchedule(AdvanceSchedule advanceSchedule){
		if(advanceSchedule == null)
			return false;
		
		return advanceSchedule.getPeriodSchedule().getDaySchedule().isEnabled()
				|| advanceSchedule.getPeriodSchedule().getMonthSchedule().isEnabled();
	}
	
	public long archiveSourceDelete(ArchiveJobScript jobScript){
		try {
			return getNativeFacade().archiveSourceDelete(jobScript);
		} catch (Throwable e) {
			logger.error("getNativeFacade().archiveSourceDelete(jobScript) error");
			return 0;
		}
	}
	
	public void createMakeupSchedule(RetryPolicy retryPolicy){
		if(hasMakeupTriggers()){
			deleteMakeupSchedule();
		}
		SimpleTriggerImpl trigger = new SimpleTriggerImpl();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, retryPolicy.getTimeToWait());
		Date startDate = cal.getTime();
		trigger.setStartTime(startDate);
		trigger.setRepeatCount(retryPolicy.getMaxTimes() - 1);
		trigger.setRepeatInterval(retryPolicy.getTimeToWait()*ScheduleUtils.MILLISECONDS_IN_MINUTE);
		trigger.setName(TRIGGER_NAME_ARCHIVE_SOURCEDELETE);
		trigger.setGroup(TRIGGER_GROUP_ARCHIVE_SOURCEDELETE_MAKEUP);
		
		String jobName = JOB_NAME_ARCHIVE_SOURCEDELETE + "_" + Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME;
		JobDetail jobDetail = new JobDetailImpl(jobName, JOB_GROUP_ARCHIVE_SOURCEDELETE_MAKEUP, ArchiveBackupJob.class);
		addMakeupJobName(jobName);
		jobDetail.getJobDataMap().put(BaseService.JOB_TYPE, JobType.JOBTYPE_FILECOPY_SOURCEDELETE);
		
		try {
			scheduler.scheduleJob(jobDetail, trigger);
			logger.info("re-schedule file archive job success");
		} catch (SchedulerException e) {
			logger.error("re-schedule file archive job error" + e.getMessage());
		}
	}
	
	public void createDeleteMakeupSchedule(JobDetail jobDetail){
		SimpleTriggerImpl trigger = new SimpleTriggerImpl();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 30);
		Date startDate = cal.getTime();
		trigger.setStartTime(startDate);
		trigger.setRepeatCount(0);
		trigger.setRepeatInterval(0);
		trigger.setName(TRIGGER_NAME_ARCHIVE_DELETE);
		trigger.setGroup(TRIGGER_GROUP_ARCHIVE_DELETE_MAKEUP);
		
		String jobName = JOB_NAME_ARCHIVE_DELETE + "_" + Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME;
		JobDetail jobDetailNew = new JobDetailImpl(jobName, JOB_GROUP_ARCHIVE_DELETE_MAKEUP, ArchiveSourceDeleteJob.class);
		addMakeupJobName(jobName);
		jobDetailNew.getJobDataMap().put(BaseService.JOB_TYPE, JobType.JOBTYPE_FILECOPY_DELETE);
		jobDetailNew.getJobDataMap().put("JobScript", jobDetail.getJobDataMap().get("JobScript"));
		
		try {
			scheduler.scheduleJob(jobDetail, trigger);
			logger.debug("re-schedule archive source delete job success");
		} catch (SchedulerException e) {
			logger.error("re-schedule archive source delete job error" + e.getMessage());
		}
	}
	
	public boolean checkSubmitArchiveSourceDeleteJob(JArchiveJob out_archiveJob){
		Lock lock = null;
		
		try {
			logger.info("checking to submit archive source delete job");
			archiveDelConfiguration = getArchiveDelConfiguration();
			if(archiveDelConfiguration == null){
				logger.debug("Failed to check. Please configure the file archive settings.");
				return false;
			}
		
			if(!archiveDelConfiguration.isbArchiveAfterBackup())
				return false;

			BackupConfiguration backupConfig = BackupService.getInstance().getBackupConfiguration();
			if(backupConfig == null){
				logger.debug("Failed to check. Please configure the backup settings.");
				return false;
			}
			lock = RemoteFolderConnCache.getInstance().getLockByPath(backupConfig.getDestination());
			if(lock != null){
				lock.lock();
			}
			out_archiveJob.setbackupDestination(backupConfig.getDestination());
			out_archiveJob.setbackupDestinationDomain(getDomainNameFromUserTextField(backupConfig.getUserName()));
			out_archiveJob.setbackupDestinationUsername(GetUserNameFromUserTextField(backupConfig.getUserName()));
			out_archiveJob.setbackupDestinationPassword(backupConfig.getPassword() == null ? "" : backupConfig.getPassword());
			out_archiveJob.setScheduleType(10);// FIXME hard code for schedule type
			out_archiveJob.setbOnlyOneSession(true);
			out_archiveJob.setD2dHostName(ServiceContext.getInstance().getLocalMachineName());
			out_archiveJob.setJobType(JobType.JOBTYPE_FILECOPY_SOURCEDELETE);

			getNativeFacade().CanArchiveSourceDeleteJobBeSubmitted(out_archiveJob);

			if(out_archiveJob == null) return false;

			if(out_archiveJob.issubmitArchive()){
				logger.info("Archive source delete job can be submitted");
				return true;
			}

		}
		catch(ArrayIndexOutOfBoundsException ARRAYEx){
			logger.debug(ARRAYEx.getMessage());
		}
		catch (Throwable e) {
			logger.debug(e.getMessage());
		}finally {
			if(lock != null){
				lock.unlock();
			}
		}
		return false;
	}
	
	private ArchiveJobArg generateArchiveJobArg(JArchiveJob jArchiveJob){
		ArchiveJobArg jobArg = new ArchiveJobArg();
		jobArg.setJobType(JobType.JOBTYPE_FILECOPY_DELETE);
		jobArg.setJobScript(generateJobScript(jArchiveJob));
		
		return jobArg;
	}
	
	private ArchiveJobScript generateJobScript(JArchiveJob jArchiveJob){
		ArchiveJobScript jobScript = jobConverter.convert(archiveDelConfiguration, (int)JobType.JOBTYPE_FILECOPY_DELETE, ServiceContext.getInstance().getLocalMachineName(), 
				-1, jArchiveJob.getbackupSessionPath(), jArchiveJob.getbackupSessionId(), "");
		return jobScript;
	}
	
	private String getCatalogPath(){
		return this.getNativeFacade().getFileCopyCatalogPath(ServiceContext.getInstance().getLocalMachineName(), 0);
	}
	
	public void checkCatalogPath(ArchiveConfiguration archiveDelConfig){
		if(this.archiveDelConfiguration != null){
			if(!StringUtil.isEmptyOrNull(this.archiveDelConfiguration.getStrCatalogPath())){
				archiveDelConfig.setStrCatalogPath(this.archiveDelConfiguration.getStrCatalogPath());
			}
		}else{
			archiveDelConfig.setStrCatalogPath(getCatalogPath());
		}
	}
	
	public boolean getSessionInfo(JArchiveJob out_archiveJob) {
		try {
			BackupConfiguration backupConfig = BackupService.getInstance().getBackupConfiguration();
			if(backupConfig == null){
				logger.debug("Failed to the backup settings.");
				return false;
			}
			
			Calendar beginDate = Calendar.getInstance();
			beginDate.set(1970, 0, 1);
			Calendar endDate = Calendar.getInstance();
			endDate.set(2999, 11, 31);
			JRestorePoint[] recoverPoints = getNativeFacade().getRestorePoints(
					backupConfig.getDestination(),
					getDomainNameFromUserTextField(backupConfig.getUserName()),
					GetUserNameFromUserTextField(backupConfig.getUserName()),
					backupConfig.getPassword(), beginDate.getTime(),
					endDate.getTime(), false);
			if(recoverPoints != null && recoverPoints.length > 0){
				JRestorePoint recoverPoint = recoverPoints[0];
				out_archiveJob.setBackupSessionGUID(recoverPoint.getSessionGuid());
				out_archiveJob.setbackupSessionId(recoverPoint.getSessionID());
				out_archiveJob.setbackupSessionPath(backupConfig.getDestination() + "\\" + recoverPoint.getPath());
				return true;
			}
		} catch (Throwable e) {
			logger.error("get recover points error : ", e);			
		}
		return false;
	}
	
	public String getDomainNameFromUserTextField (String strUserInput){
          String strDomain = "";
          logger.debug("Start getDomainNameFromUserTextField");
          if (strUserInput == null || strUserInput.isEmpty())
                return strDomain;

          int pos = strUserInput.indexOf("\\"); // ex) tant-a01\kimwo01
          if (pos == -1) // If not exist.
          {
                // Normal user input without domain field.
          }
          else
          {
                // Extract domain part
                strDomain = strUserInput.substring(0, pos);
          }
          logger.debug("End getDomainNameFromUserTextField :" + strDomain);
          return strDomain;
    }
	
	public String GetUserNameFromUserTextField (String strUserInput){
    	logger.debug("Start GetUserNameFromUserTextField");
          String strUser = "";

          if (strUserInput == null || strUserInput.isEmpty())
                return strUser;

          int pos = strUserInput.indexOf("\\"); // ex) tant-a01\kimwo01
          if (pos == -1) // If not exist.
          {
                // Normal user input without domain field.
                strUser = strUserInput;
          }
          else
          {
                // Extract user name part
                strUser = strUserInput.substring(pos+1);
          }
          logger.debug("End GetUserNameFromUserTextField: " + strUser);
          return strUser;
    }
	public void clearCachedConfiguration() {
		this.archiveDelConfiguration=null;	
	}
	
	public boolean hasMakeupTriggers(){
		return getMakeupTriggers() != null && getMakeupTriggers().size() > 0;
	}
	
	public boolean isMakeupJob(){
		return hasMakeupTriggers();
	}
	
	public boolean isFileArchiveJobRunning(){
		boolean bRunning = false;
		try {
			bRunning = getNativeFacade().IsFileArchiveJobRunning();
		} catch (Throwable e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		return bRunning;
	}
}

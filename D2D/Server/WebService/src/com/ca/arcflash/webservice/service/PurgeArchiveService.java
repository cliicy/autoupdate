package com.ca.arcflash.webservice.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.DateBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.NextArchiveScheduleEvent;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfigurationConstants;
import com.ca.arcflash.webservice.data.archive.ArchiveJobScript;
import com.ca.arcflash.webservice.data.job.rps.ArchiveJobArg;
import com.ca.arcflash.webservice.scheduler.ArchivePurgeJob;
import com.ca.arcflash.webservice.scheduler.BaseArchiveJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.util.ScheduleUtils;

public class PurgeArchiveService extends BaseService {
	private static final Logger logger = Logger.getLogger(BrowserService.class);
	private static final PurgeArchiveService instance = new PurgeArchiveService();
	private static final JobMonitor archivePurgeJobMonitor = new JobMonitor(-1);

	// private ArchiveConfiguration archiveConfiguration;
	private Scheduler scheduler;
	private String makeupJobName = "";
	private String makeupJobName4FC = "";
	
	private PurgeArchiveService(){
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
	}
	
	public static PurgeArchiveService getInstance(){
		return instance;
	}
	
	public void deleteMakeupJobs(boolean isFCPurgeJob){	
		try {
			if(isFCPurgeJob){
				if(!StringUtil.isEmptyOrNull(makeupJobName4FC)){
					scheduler.deleteJob(new JobKey(makeupJobName, JOB_GROUP_ARCHIVE_PURGE_MAKEUP));
					makeupJobName4FC = "";
				}
			} else {
				if(!StringUtil.isEmptyOrNull(makeupJobName)){
					scheduler.deleteJob(new JobKey(makeupJobName, JOB_GROUP_ARCHIVE_PURGE_MAKEUP));
					makeupJobName = "";
				}
			}
		} catch (SchedulerException e) {
			logger.error("Failed to delete archive purge job : ", e);
		}
	}
	
	public void setMakeupJobs(String name, boolean isFCPurgeJob){
		if(isFCPurgeJob){
			makeupJobName4FC = name;
		} else {
			makeupJobName = name;
		}
	}
	
	public boolean isMakeupJob(boolean isFCPurgeJob){
		if(isFCPurgeJob){
			return !StringUtil.isEmptyOrNull(makeupJobName4FC);
		} else {
			return !StringUtil.isEmptyOrNull(makeupJobName);
		}
	}
	
	private ArchiveConfiguration getArchiveDelConfiguration() {
		try {			
			ArchiveConfiguration archiveDelConfiguration = DeleteArchiveService.getInstance().getArchiveDelConfiguration();
			return archiveDelConfiguration;
		} catch (ServiceException e) {
			
			if(e.getErrorCode().equalsIgnoreCase("21474836483"))//handling the backup empty exception.
			{
				logger.debug("backup configuration is found empty. Not able to set archive purge schedule. Returning false");
				return null;
			}
			
			logger.error("getArchiveConfiguration error in configJobSchedule of archive");
			return null;
		}
	}
	
	public boolean configJobSchedule()throws ServiceException{
		logger.debug("ArchiveService::configJobSchedule() - start");
		try {
			clearArchivePurgeSchedule();

			ArchiveConfiguration archiveDelConfiguration = getArchiveDelConfiguration();
			
			if(archiveDelConfiguration == null || !archiveDelConfiguration.isbArchiveAfterBackup()) {
				logger.error("File Archive configuration is null or disabled");
				return false;
			}
			
			if (!CanArchivePurgeJobBeSubmitted()) {
				logger.info("Archive job cannot be submitted. returning");
				return false;
			}
			
			Date purgeStartTime;
			
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(Long.toString(archiveDelConfiguration.getlPurgeStartTime())));
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 5);
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
			purgeStartTime = c.getTime();
			
			Trigger purgeTrigger = generatePurgeJobQuartzTrigger(TRIGGER_NAME_ARCHIVE_PURGE, purgeStartTime, archiveDelConfiguration);
			JobDetail jobDetail = new JobDetailImpl(JOB_NAME_ARCHIVE_PURGE,JOB_GROUP_ARCHIVE_PURGE_NAME,ArchivePurgeJob.class);
			
			scheduler.scheduleJob(jobDetail, purgeTrigger);
			
			configJobSchedule4FileCopyDest(archiveDelConfiguration);
			
		} catch (SchedulerException e) {
			logger.error("Configure archiver Scheduler", e);
				throw new ServiceException("exception : Configure archiver Scheduler error");
		}
		
		logger.debug("ArchiveService::configJobSchedule() - end");
		return true;
	}

	private boolean CanArchivePurgeJobBeSubmitted() {
		ArchiveConfiguration archiveDelConfiguration = getArchiveDelConfiguration();
		if(archiveDelConfiguration == null)
			return false;
		
		if(!archiveDelConfiguration.isbArchiveAfterBackup())
			return false;
		
		if(archiveDelConfiguration.isbArchiveToDrive())
			if(archiveDelConfiguration.getStrArchiveToDrivePath() == null || archiveDelConfiguration.getStrArchiveToDrivePath().length() == 0)
				return false;
		else if(archiveDelConfiguration.isbArchiveToCloud())
			if(archiveDelConfiguration.getCloudConfig().getcloudBucketName() == null || archiveDelConfiguration.getCloudConfig().getcloudBucketName().length() == 0)
				return false;
		
		return true;
	}

	private Trigger generatePurgeJobQuartzTrigger(String name, Date startTime, final ArchiveConfiguration archiveConfig){
		SimpleTriggerImpl trigger = null;
		int purgeAfterDays = archiveConfig.getiPurgeAfterDays() > 0 ? archiveConfig.getiPurgeAfterDays() : 1;
		trigger = ScheduleUtils.makeHourlyTrigger(purgeAfterDays * 24);
		trigger.setGroup(TRIGGER_GROUP_ARCHIVE_PURGE);
		trigger.setStartTime(startTime);
		trigger.setRepeatCount(-1);
		trigger.setName(name);
		
		return trigger;
	}

	public NextArchiveScheduleEvent getNextPurgeScheduleEvent() throws ServiceException{
		logger.debug("getNextScheduleEvent() - start");

		try{
			if (scheduler == null)
				return null;
			
			Trigger purgeTrigger = scheduler.getTrigger(new TriggerKey(TRIGGER_NAME_ARCHIVE_PURGE, JOB_GROUP_ARCHIVE_PURGE_NAME));
			Date nextPurge = purgeTrigger == null? null : purgeTrigger.getNextFireTime();			
			//NextArchiveScheduleEvent nextEvent= new NextArchiveScheduleEvent(nextPurge, BaseArchiveJob.Job_Type_Archive);
			NextArchiveScheduleEvent nextEvent = new NextArchiveScheduleEvent();
			nextEvent.setDate(nextPurge);
			nextEvent.setJobType(BaseArchiveJob.Job_Type_ArchivePurge);
			logger.debug("getNextPurgeScheduleEvent() - end");
			return nextEvent;
		}catch(Throwable e){
			logger.error("getNextPurgeScheduleEvent()", e);
			throw generateInternalErrorAxisFault();
		}		
	}

	public JobMonitor getJobMonitorInternal() {
		return archivePurgeJobMonitor;
	}
	
	public long purge(ArchiveJobScript jobScript) throws ServiceException{
		if (jobScript == null)	return 0;
		
		try {
			logger.debug("Triggering the purge job");
			return getNativeFacade().purge(jobScript);
		} catch (Throwable e) {
			logger.error("getNativeFacade().purge(jobScript) error");
			return 0;
		}
	}

	public JobMonitor getArchivePurgeJobMonitor() {
		synchronized (archivePurgeJobMonitor) {
			if ((archivePurgeJobMonitor == null) || (archivePurgeJobMonitor.getJobId() == -1L))
				return null;

			return archivePurgeJobMonitor;
		}
	}
	
	public boolean isPurgeJobRunning(){
		boolean bRunning = false;
		try {
			bRunning = getNativeFacade().IsArchivePurgeJobRunning();
		} catch (Throwable e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		return bRunning;
	}
	
	public Scheduler getScheduler(){
		return scheduler;
	}

	public long archivePurgeNow(ArchiveJobArg jobArg) throws ServiceException {
		logger.debug("Launch archive purge job from rps");
		
		if(isPurgeJobRunning()) {
			logger.error("Archive purge job is running");
			return -2;
		}
		SimpleTriggerImpl trig = ScheduleUtils.makeImmediateTrigger(0, 0);
		trig.setName(ArchiveService.TRIGGER_NAME_ARCHIVE_BACKUP);
		trig.setGroup(ArchiveService.JOB_GROUP_ARCHIVE_BACKUP_NAME);

		JobDetail jobDetail = new JobDetailImpl(jobArg.getJobDetailName(), jobArg.getJobDetailGroup() + Constants.RUN_NOW,
				ArchivePurgeJob.class);
		jobDetail.getJobDataMap().put(Constants.RUN_NOW, Boolean.TRUE);
		jobDetail.getJobDataMap().put(JOB_ID, jobArg.getJobId());

		try {
			getScheduler().scheduleJob(jobDetail, trig);
			return 0;
		} catch (SchedulerException e) {
			logger.error("re-schedule archive job error");
			return -1;
		}		
	}
	
	private void clearArchivePurgeSchedule(){
		try {
			scheduler.deleteJob(new JobKey(JOB_NAME_ARCHIVE_PURGE, JOB_GROUP_ARCHIVE_PURGE_NAME));
			scheduler.deleteJob(new JobKey(JOB_NAME_ARCHIVE_PURGE_FOR_FC, JOB_GROUP_ARCHIVE_PURGE_NAME));
		} catch (SchedulerException e) {
			logger.error("Error occurs when removing archive purge jobs from scheduler"
					+ e.getMessage());
		}
	}
	
	private void configJobSchedule4FileCopyDest(ArchiveConfiguration archiveDelConfiguration){
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String purgeJobFlag = registry.getValue(handle, ArchiveConfigurationConstants.ENABLE_FC_PURGE_JOB_KEY);
			String purgeJobStartTime = registry.getValue(handle, ArchiveConfigurationConstants.FC_PURGE_JOB_START_TIME);
			if(!StringUtil.isEmptyOrNull(purgeJobFlag) && "true".equalsIgnoreCase(purgeJobFlag)){
				logger.info("FC purge job is enabled.");
				int startHour = Integer.parseInt(ArchiveConfigurationConstants.DEFAULT_FC_PURGE_JOB_START_TIME);
				try {
					startHour = Integer.parseInt(purgeJobStartTime);
					if(0 > startHour || startHour > 23){
						startHour = Integer.parseInt(ArchiveConfigurationConstants.DEFAULT_FC_PURGE_JOB_START_TIME);
					}
				} catch (Exception e) {
					logger.warn("get FC purge job start time fail: " + e.getMessage() + ", use default time");
				}
				Date startDate;
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.set(Calendar.HOUR_OF_DAY, startHour);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 5);
				c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
				startDate = c.getTime();
				Trigger purgeTrigger = generatePurgeJobQuartzTrigger(TRIGGER_NAME_ARCHIVE_PURGE_FOR_FC, startDate, archiveDelConfiguration);
				JobDetail jobDetail = new JobDetailImpl(JOB_NAME_ARCHIVE_PURGE_FOR_FC,JOB_GROUP_ARCHIVE_PURGE_NAME,ArchivePurgeJob.class);
				jobDetail.getJobDataMap().put("purgeJob4FC", true);
				scheduler.scheduleJob(jobDetail, purgeTrigger);
			}
		} catch (Exception e) {
			logger.error("Read registry for key: "
					+ ArchiveConfigurationConstants.ENABLE_FC_PURGE_JOB_KEY
					+ "or "
					+ ArchiveConfigurationConstants.FC_PURGE_JOB_START_TIME
					+ " failed.", e);
		} finally {
			if (handle != 0) {
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
					logger.error("Close registry key failed.", e);
				}
			}
		}
	}
	
	public boolean isFCPurgeJobEnabled(){
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String purgeJobFlag = registry.getValue(handle, ArchiveConfigurationConstants.ENABLE_FC_PURGE_JOB_KEY);
			if(!StringUtil.isEmptyOrNull(purgeJobFlag) && "true".equalsIgnoreCase(purgeJobFlag)){
				return true;
			}
		} catch (Exception e) {
			logger.error("Read registry for key: "
					+ ArchiveConfigurationConstants.ENABLE_FC_PURGE_JOB_KEY	+ " failed.", e);
		} finally {
			if (handle != 0) {
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
					logger.error("Close registry key failed.", e);
				}
			}
		}
		return false;
	}

}

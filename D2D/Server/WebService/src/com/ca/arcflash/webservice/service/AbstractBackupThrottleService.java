package com.ca.arcflash.webservice.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.ThrottleItem;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.scheduler.BackupThrottleJob;
import com.ca.arcflash.webservice.scheduler.DSTWeeklyTrigger;
import com.ca.arcflash.webservice.util.ScheduleUtils;


public abstract class AbstractBackupThrottleService{

	private static final Logger logger = Logger.getLogger(AbstractBackupThrottleService.class);	
	
	protected static final String BACKUP_THROTTLE_JOB_NAME = "D2DBackupThrottleJob";
	protected static final String VSPHERE_BACKUP_THROTTLE_JOB_NAME = "VSphereBackupThrottleJob";
	protected static final String BACKUP_THROTTLE_JOB_GROUP_NAME = "BackupThrottleJobGroup";
	protected Scheduler scheduler = null;
	BackupConfiguration backupConf = null;

	public AbstractBackupThrottleService() {
	}

	
	protected void schedule() {	
		JobDetailImpl jd = new JobDetailImpl(BACKUP_THROTTLE_JOB_NAME, BACKUP_THROTTLE_JOB_GROUP_NAME, BackupThrottleJob.class);
        jd.setDurability(true);
        try {
			scheduler.addJob(jd, false);
			
			SimpleTriggerImpl immediateTrigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			immediateTrigger.setName(jd.getName() + "immediateTrigger");
			immediateTrigger.setGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
			immediateTrigger.setJobName(jd.getName());
			immediateTrigger.setJobGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
			scheduler.scheduleJob(immediateTrigger);		
		} catch (SchedulerException e1) {
			logger.error("Failed to schedule backup throttle job for quartz error" + e1.getMessage());
		}
        
        List<DailyScheduleDetailItem> dailySchedules = this.getDailySchedule();
		if (!hasSchedule(dailySchedules)) {
			return;
		}

		try {
			int triggerNumber = 1;
			for (DailyScheduleDetailItem dailySchedule : dailySchedules) {
				ArrayList<ThrottleItem> throttleItems = dailySchedule.getThrottleItems();
				if (throttleItems != null) {
					for (ThrottleItem throttleItem : throttleItems) {
						logger.debug("throttleItem StartTime: "+throttleItem.getStartTime());
						logger.debug("throttleItem EndTime: "+throttleItem.getEndTime());
						CronTriggerImpl startTrigger = new DSTWeeklyTrigger(throttleItem.getStartTime().getHour(), 
								throttleItem.getStartTime().getMinute(), throttleItem.getEndTime().getHour(),
								throttleItem.getEndTime().getMinute(), dailySchedule.getDayofWeek());
						startTrigger.setName(jd.getName() + "trigger" + triggerNumber);
						triggerNumber++;
						startTrigger.setGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
						startTrigger.setJobName(jd.getName());
						startTrigger.setJobGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
						logger.info("Throttle startTrigger DayofWeek: "+dailySchedule.getDayofWeek()+" ["+throttleItem.getStartTime()+" - "+throttleItem.getEndTime()+"]");
						scheduler.scheduleJob(startTrigger);
						
						if (!endIsStart(dailySchedules, dailySchedule.getDayofWeek(), throttleItem.getEndTime())) {
							DayTime nextTime = getNextDayTime(dailySchedules, dailySchedule.getDayofWeek(), throttleItem.getEndTime());
							CronTriggerImpl endTrigger = new DSTWeeklyTrigger(throttleItem.getEndTime().getHour(), 
									throttleItem.getEndTime().getMinute(), nextTime.getHour(),
									nextTime.getMinute(), dailySchedule.getDayofWeek());
							endTrigger.setName(jd.getName() + "trigger" + triggerNumber);
							triggerNumber++;
							endTrigger.setGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
							endTrigger.setJobName(jd.getName());
							endTrigger.setJobGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
							logger.info("Throttle endTrigger DayofWeek: "+dailySchedule.getDayofWeek()+" ["+throttleItem.getStartTime()+" - "+throttleItem.getEndTime()+"]");
							scheduler.scheduleJob(endTrigger);
						}
						
						if (throttleItem.getStartTime().getHour() == 0
								&& throttleItem.getStartTime().getMinute() == 0
								&& throttleItem.getEndTime().getHour() == 0
								&& throttleItem.getEndTime().getMinute() == 0) {
							//throttle 00:00-24:00
							Calendar currentTime = Calendar.getInstance();
							currentTime.set(Calendar.DAY_OF_WEEK, dailySchedule.getDayofWeek());
							currentTime.add(Calendar.DAY_OF_WEEK, 1);
							int dayofWeek = currentTime.get(Calendar.DAY_OF_WEEK);
							CronTriggerImpl endTrigger = new DSTWeeklyTrigger(0, 0, 0, 0, dayofWeek);
							endTrigger.setName(jd.getName() + "trigger" + triggerNumber);
							triggerNumber++;
							endTrigger.setGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
							endTrigger.setJobName(jd.getName());
							endTrigger.setJobGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
							logger.info("Throttle endTrigger DayofWeek: "+dayofWeek+" [0:0 - 0:0]");
							scheduler.scheduleJob(endTrigger);
						}
					}
				}
			}
		}catch(SchedulerException se) {
			logger.error("Failed to schedule backup throttle job for quartz error" + se.getMessage());
		}
	}
	
	protected boolean hasSchedule(List<DailyScheduleDetailItem> dailySchedules) {
		boolean hasSchedule = false;
		if(dailySchedules == null || dailySchedules.size() == 0) {
			logger.warn("No backup throttle settings.");	
		}
		else {
			for (DailyScheduleDetailItem dailySchedule : dailySchedules) {
				ArrayList<ThrottleItem> throttleItems = dailySchedule.getThrottleItems();
				if (throttleItems != null && throttleItems.size() != 0)
					hasSchedule = true;
			}
		}
		
		return hasSchedule;
	}
	
	protected List<DailyScheduleDetailItem> getDailySchedule() {
		try {
			backupConf = BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e1) {
			logger.error("Failed to get backup configuration");
		}
		
		List<DailyScheduleDetailItem> dailyScheduleDetailItems = null;
		
        if(backupConf != null && backupConf.getBackupDataFormat() == 1 && backupConf.getAdvanceSchedule() != null) {
			dailyScheduleDetailItems = backupConf.getAdvanceSchedule().getDailyScheduleDetailItems();
		}		
			
		return dailyScheduleDetailItems;
	}
	
	protected boolean endIsStart(List<DailyScheduleDetailItem> dailySchedules, int day, DayTime endTime) {
		for (DailyScheduleDetailItem dailySchedule : dailySchedules) {
			if (dailySchedule.getDayofWeek() == day) {
				for (ThrottleItem throttleItem : dailySchedule.getThrottleItems()) {
					if (throttleItem.getStartTime().equals(endTime))
						return true;
				}
			}
		}
		return false;
	}

	protected DayTime getNextDayTime(List<DailyScheduleDetailItem> dailySchedules, int day, DayTime endTime) {
		DayTime dayTime = new DayTime(23, 59);
		for (DailyScheduleDetailItem dailySchedule : dailySchedules) {
			if (dailySchedule.getDayofWeek() == day) {
				for (ThrottleItem throttleItem : dailySchedule.getThrottleItems()) {
					if (throttleItem.getStartTime().after(endTime) && throttleItem.getStartTime().before(dayTime))
						dayTime = throttleItem.getStartTime();
				}
			}
		}
		return dayTime;
	}
	
	public void startImmediateTrigger() {
		try {
			backupConf = BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e1) {
			logger.error("Failed to get backup configuration");
		}

    	if(backupConf != null && backupConf.getBackupDataFormat() == 1) {
    		JobDetailImpl jd = new JobDetailImpl(BACKUP_THROTTLE_JOB_NAME, BACKUP_THROTTLE_JOB_GROUP_NAME, BackupThrottleJob.class);
            jd.setDurability(true);
            try {
    			scheduler.addJob(jd, false);
    			
    			SimpleTriggerImpl immediateTrigger = ScheduleUtils.makeImmediateTrigger(0, 0);
    			immediateTrigger.setName(jd.getName() + "immediateTrigger");
    			immediateTrigger.setGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
    			immediateTrigger.setJobName(jd.getName());
    			immediateTrigger.setJobGroup(BACKUP_THROTTLE_JOB_GROUP_NAME);
    			scheduler.scheduleJob(immediateTrigger);		
    		} catch (SchedulerException e1) {
    			logger.error("Failed to schedule backup throttle job for quartz error" + e1.getMessage());
    		}
    	}
	}
	
	public void unschedule(String vmInstanceUUID) {
		try {
			scheduler.pauseTrigger(new TriggerKey(BACKUP_THROTTLE_JOB_NAME, BACKUP_THROTTLE_JOB_GROUP_NAME));
			scheduler.unscheduleJob(new TriggerKey(BACKUP_THROTTLE_JOB_NAME, BACKUP_THROTTLE_JOB_GROUP_NAME));
			scheduler.deleteJob(new JobKey(BACKUP_THROTTLE_JOB_NAME, BACKUP_THROTTLE_JOB_GROUP_NAME));
		}catch(SchedulerException e){
			logger.debug("Failed to delete job");
		}
	}
	
	protected abstract String getThrottleJobName(String vmInstanceUUID);
}

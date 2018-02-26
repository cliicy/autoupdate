package com.ca.arcflash.webservice.util;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;
import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.calendar.DailyCalendar;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.scheduler.AdvancedScheduleTrigger;
import com.ca.arcflash.webservice.scheduler.FullBackupJob;
import com.ca.arcflash.webservice.scheduler.IncrementalBackupJob;
import com.ca.arcflash.webservice.scheduler.MakeUp;
import com.ca.arcflash.webservice.scheduler.ResyncBackupJob;
import com.ca.arcflash.webservice.scheduler.VSphereBackupJob;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.ServiceContext;

public class ScheduleUtils {
	public static final int SCHEDULE_FULLBACKUP_PRIORITY = 100;
	public static final int SCHEDULE_RESYNC_PRIORITY = 50;
	public static final int SCHEDULE_INCREMENTAL_PRIORITY = 25;
	public static final int DAY_PRIORITY = ScheduleUtils.SCHEDULE_FULLBACKUP_PRIORITY + 50;
	public static final int WEEK_PRIORITY = DAY_PRIORITY + 50;
	public static final int MONTH_PRIORITY = WEEK_PRIORITY + 50;
	
	private static MakeUp makeup = null;
	
	public static String makeupFilePath = "";

	private static Scheduler scheduler;
	private static final Logger logger = Logger.getLogger(ScheduleUtils.class);

	// For triggers
	public static final int REPEAT_INDEFINITELY = -1;
	public static final long MILLISECONDS_IN_MINUTE = 60l * 1000l;
	public static final long MILLISECONDS_IN_HOUR = 60l * 60l * 1000l;
	public static final long SECONDS_IN_DAY = 24l * 60l * 60L;
	public static final long MILLISECONDS_IN_DAY = SECONDS_IN_DAY * 1000l;

	public static Scheduler getBackupSchedule() throws SchedulerException {
		if (scheduler == null) {
			int maxJob = ServiceContext.getInstance().getvSphereMaxJobNum();
			int defaultThreadCount = 20;
			if (maxJob > 10) {
				defaultThreadCount = maxJob * 2;
			}

			Properties properties = new Properties();
			properties.setProperty("org.quartz.scheduler.instanceName", "DefaultQuartzScheduler");
			properties.setProperty("org.quartz.scheduler.rmi.export", "false");
			properties.setProperty("org.quartz.scheduler.rmi.proxy", "false");
			properties.setProperty("org.quartz.scheduler.wrapJobExecutionInUserTransaction", "false");
			properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
			properties.setProperty("org.quartz.threadPool.threadCount", String.valueOf(defaultThreadCount));
			properties.setProperty("org.quartz.threadPool.threadPriority", "5");
			properties.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
			properties
					.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
			properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.BackupRAMJobStore");

			scheduler = new StdSchedulerFactory(properties).getScheduler();
			scheduler.start();
		}
		return scheduler;
	}

	public static AdvancedScheduleTrigger newAdvancedTrigger(String vmInstanceUUID, int jobtype, Date startTime, String jobID,
			ScheduleDetailItem item, int dayOfWeek, Trigger... triggers) {
		int priority = 5;
		String triggerGroup = "";
		String triggername = BaseService.BACKUP_TRIGGER_NAME + jobID;

		switch (jobtype) {
		case BackupType.Full:
			priority = ScheduleUtils.SCHEDULE_FULLBACKUP_PRIORITY;
			triggerGroup = getFullBackupTriggerGroupName(vmInstanceUUID);
			break;
		case BackupType.Incremental:
			priority = ScheduleUtils.SCHEDULE_INCREMENTAL_PRIORITY;
			triggerGroup = getIncBackupTriggerGroupName(vmInstanceUUID);
			break;
		case BackupType.Resync:
			priority = ScheduleUtils.SCHEDULE_RESYNC_PRIORITY;
			triggerGroup = getResyncBackupTriggerGroupName(vmInstanceUUID);
			break;
		}

		AdvancedScheduleTrigger trigger = new AdvancedScheduleTrigger(triggername, triggerGroup, startTime, item,
				dayOfWeek, triggers);
		trigger.setPriority(priority);
		trigger.setMisfireInstruction(AdvancedScheduleTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
		return trigger;
	}

	public static PeriodTrigger getPeriodTrigger(String vmInstanceUUID, int bkptype, Date startTime, String jobID, String cronExpression,
			int priority, Trigger... triggers) {

		String triggerGroup = "";
		switch (bkptype) {
		case BackupType.Full:
			triggerGroup = getFullBackupTriggerGroupName(vmInstanceUUID);
			break;
		case BackupType.Incremental:
			triggerGroup = getIncBackupTriggerGroupName(vmInstanceUUID);
			break;
		case BackupType.Resync:
			triggerGroup = getResyncBackupTriggerGroupName(vmInstanceUUID);
			break;
		}

		String triggername = BaseService.BACKUP_TRIGGER_NAME + jobID;
		if (!StringUtil.isEmptyOrNull(vmInstanceUUID)) {
			triggername = BaseService.BACKUP_TRIGGER_NAME + vmInstanceUUID + jobID;
		}

		PeriodTrigger trigger = null;
		try {
			trigger = new PeriodTrigger(triggername, triggerGroup, cronExpression, triggers);
			trigger.setStartTime(startTime);
			trigger.setPriority(priority);
			trigger.setMisfireInstruction(AdvancedScheduleTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}

		return trigger;
	}

	public static JobDetailImpl newJobDetail(String vmInstanceUUID, int jobtype, String jobID) {
		String jobName = "";
		Class backupClass = null;
		JobDetailImpl jd = null;

		switch (jobtype) {
		case BackupType.Full:
			jobName = BaseService.JOB_NAME_BACKUP_FULL;
			backupClass = FullBackupJob.class;
			break;
		case BackupType.Incremental:
			jobName = BaseService.JOB_NAME_BACKUP_INCREMENTAL;
			backupClass = IncrementalBackupJob.class;
			break;
		case BackupType.Resync:
			jobName = BaseService.JOB_NAME_BACKUP_RESYNC;
			backupClass = ResyncBackupJob.class;
			break;
		}
		
		if (backupClass != null) {
			if (!StringUtil.isEmptyOrNull(vmInstanceUUID)) {
				backupClass = VSphereBackupJob.class;
				jobName += "_" + vmInstanceUUID;//for vm backup, append this guid
			}
			jobName += "_" + jobID;
			jd = new JobDetailImpl(jobName, getBackupJobGroupName(vmInstanceUUID), backupClass);
		}

		return jd;
	}

	public static DailyCalendar newCalendar(DayTime startTime, DayTime endTime) {
		DailyCalendar calendar = null;
		if (endTime.getMinute() > 0) {
			calendar = new DailyCalendar(startTime.getHour(), startTime.getMinute(), 0, 0, endTime.getHour(),
					endTime.getMinute() - 1, 0, 0);
		} else {
			calendar = new DailyCalendar(startTime.getHour(), startTime.getMinute(), 0, 0, endTime.getHour() - 1, 59,
					0, 0);
		}

		calendar.setInvertTimeRange(true);
		return calendar;
	}

	public static void scheduleJob(ScheduleDetailItem scheduleDetail, String jobID, Scheduler scheduler,
			JobDetail jobDetail, AdvancedScheduleTrigger trigger) throws SchedulerException, ParseException {

		if (jobDetail == null) {
			return;
		}
		if (trigger == null)
			return;
		DayTime endTime = scheduleDetail.getEndTime();
		DayTime startTime = scheduleDetail.getStartTime();

		// 2. set calendar to set the start and endtime of the trigger
		// DailyCalendar calendar = newCalendar(startTime, endTime);
		// scheduler.addCalendar(BaseService.BACKUP_SCHEDULE_CALENDAR_NAME +
		// jobID, calendar, true, false);

		// 4. add trigger and job
		// trigger.setCalendarName(BaseService.BACKUP_SCHEDULE_CALENDAR_NAME +
		// jobID);
		scheduler.scheduleJob(jobDetail, trigger);
	}

	public static String generateJobID(int index, ScheduleDetailItem sd) {
		StringBuilder sb = new StringBuilder();
		sb.append(index);
		sb.append(sd.getJobType());
		sb.append(sd.getInterval());
		sb.append(sd.getIntervalUnit());
		sb.append(sd.getStartTime());
		sb.append(sd.getEndTime());
		return sb.toString();
	}

	public static AdvancedScheduleTrigger newAdvancedTrigger(int jobtype, Date startTime, String jobID,
			ScheduleDetailItem item, int dayOfWeek, String vmInstanceUUID) {
		int priority = 5;
		String triggerGroup = "";
		String triggername = BaseService.BACKUP_TRIGGER_NAME + jobID;

		switch (jobtype) {
		case BackupType.Full:
			priority = ScheduleUtils.SCHEDULE_FULLBACKUP_PRIORITY;
			triggerGroup = getFullBackupTriggerGroupName(vmInstanceUUID);
			break;
		case BackupType.Incremental:
			priority = ScheduleUtils.SCHEDULE_INCREMENTAL_PRIORITY;
			triggerGroup = getIncBackupTriggerGroupName(vmInstanceUUID);
			break;
		case BackupType.Resync:
			priority = ScheduleUtils.SCHEDULE_RESYNC_PRIORITY;
			triggerGroup = getResyncBackupTriggerGroupName(vmInstanceUUID);
			break;
		}

		AdvancedScheduleTrigger trigger = new AdvancedScheduleTrigger(triggername, triggerGroup, startTime, item,
				dayOfWeek);
		trigger.setPriority(priority);
		trigger.setMisfireInstruction(AdvancedScheduleTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
		return trigger;
	}

	public static String getFullBackupTriggerGroupName(String vmInstanceUUID) {
		String temp = vmInstanceUUID;
		if (StringUtil.isEmptyOrNull(vmInstanceUUID)) {
			temp = "";
		}
		return BaseService.TRIGGER_GROUP_BACKUP_NAME_FULL + temp;
	}

	public static String getIncBackupTriggerGroupName(String vmInstanceUUID) {
		String temp = vmInstanceUUID;
		if (StringUtil.isEmptyOrNull(vmInstanceUUID)) {
			temp = "";
		}
		return BaseService.TRIGGER_GROUP_BACKUP_NAME_INCREMENTAL + temp;
	}

	public static String getResyncBackupTriggerGroupName(String vmInstanceUUID) {
		String temp = vmInstanceUUID;
		if (StringUtil.isEmptyOrNull(vmInstanceUUID)) {
			temp = "";
		}
		return BaseService.TRIGGER_GROUP_BACKUP_NAME_RESYNC + temp;
	}

	public static String getBackupJobGroupName(String vmInstanceUUID) {
		String temp = vmInstanceUUID;
		if (StringUtil.isEmptyOrNull(vmInstanceUUID)) {
			temp = "";
		}
		return BaseService.JOB_GROUP_BACKUP_NAME + temp;
	}

	public static String generateJobID(int index, ScheduleDetailItem sd, String vmInstanceUUID) {
		StringBuilder sb = new StringBuilder();
		sb.append(index);
		sb.append(sd.getJobType());
		sb.append(sd.getInterval());
		sb.append(sd.getIntervalUnit());
		sb.append(sd.getStartTime());
		sb.append(sd.getEndTime());
		if (!StringUtil.isEmptyOrNull(vmInstanceUUID)) {
			sb.append("_").append(vmInstanceUUID);
		}
		return sb.toString();
	}

	public static class PeriodTrigger extends CronTriggerImpl {
		private static final long serialVersionUID = 1L;
		private Trigger[] triggers = null;

		public PeriodTrigger(String name, String group, String cronExpression, Trigger... triggers)
				throws ParseException {
			super(name, group, cronExpression);
			this.triggers = triggers;
		}

		public Date getTimeAfter(Date afterTime) {
			return super.getTimeAfter(afterTime);
		}
		
		private Date getTriggeredNextTime(){			
			try {
				Trigger tg = BackupService.getInstance().getBackupSchedule().getTrigger(new TriggerKey(this.getName(),this.getGroup()));
				if(tg != null){
					Date date = ((PeriodTrigger)tg).getStoredNextTime();
					return date;
				}
			} catch (Exception e) {
				logger.error("Failed to get scheduled trigger", e);
			}
			return null;
		}
		
		private Date getStoredNextTime(){
			return super.getNextFireTime();
		}

		@Override
	    public Date computeFirstFireTime(Calendar calendar) {
	        Date date = super.computeFirstFireTime(calendar); 
	        if (triggers != null && triggers.length > 0) {
	            List<Date> list = new ArrayList<>();
	            for (Trigger t : triggers) {
	                if (t != null) {
	                    list.add(t.getFireTimeAfter(t.getStartTime()));
	                }
	            }
	            if (!list.isEmpty()) {
	                Collections.sort(list);
	                for (Date hd : list) {
	                    if (hd.getTime() == date.getTime()) {
	                    	logger.debug("Conflicted detected at date:" + date +",conflict with this trigger:" + this.getFullName());	
	                    	date = getFireTimeAfter(date);
	                    	logger.debug("Conflicted detected, current trigger will be skipped and delay to:" + date +","+ this.getFullName());
	                    }
	                }
	            }
	            
	        }
	        logger.debug("The Fisrt time for this trigger is "+date);
	        return date;
	    }
		
	
		
		
		@Override
		public Date getNextFireTime() {
			Date date = getTriggeredNextTime();
			
			if(date == null){
				date = super.getNextFireTime();
			}
			
			 boolean isconflict = false;
		     if (triggers != null && triggers.length > 0) {
      
		          List<Date> list = new ArrayList<Date>();
		            for (Trigger t : triggers) {
		                if (t != null) {
		                    list.add(t.getFireTimeAfter(new Date()));
		                }
		            }
		            if (!list.isEmpty()) {
		                Collections.sort(list);
		                for (Date hd : list) {
		                    if (Math.abs(hd.getTime()-date.getTime())<=5000) {
		                    	logger.debug("Conflicted detected at date:" + date +",conflict with this trigger:" + this.getFullName());	
		                    	date = getFireTimeAfter(date);
		                    	logger.debug("Conflicted detected, current trigger will be skipped and delay to:" + date +","+ this.getFullName());
		                        isconflict = true;
		                    }
		                }
		            }
		            if (isconflict) {
		            	
		                super.setNextFireTime(date);
		            }
		    }
			return date;
		}
		

//		@Override
//		public Date getNextFireTime() {
//			Date date = getTriggeredNextTime();
//			
//			if(date == null){
//				date = super.getNextFireTime();
//			}
//			
//			boolean hasConflict = false;
//
//			if (triggers != null && triggers.length > 0) {
//
//				List<Trigger> list = new ArrayList<Trigger>();
//				for (Trigger t : triggers) {
//					if (t != null)
//						list.add(t);
//				}
//				if (!list.isEmpty()) {
//					Collections.sort(list);
//
//					for (Trigger t : list) {
//						Date hd = t.getNextFireTime();
//						if (hd.getTime() == date.getTime()) {
//							logger.info("Conflicted detected, higher periority:" + t + ", date:" + date +",conflict with this trigger:" + this.getFullName());						
//							date = getFireTimeAfter(date);
//							logger.info("Conflicted detected, current trigger will be skipped and delay to:" + date +","+ this.getFullName());
//							hasConflict = true;
//						}
//					}
//				}
//			}
//			if (hasConflict) {
//				super.setNextFireTime(date);
//			}
//			return date;
//		}
		
		
	/*	public Date getNextEventTime() {
			Date date = getTriggeredNextTime();
			
			if(date == null){
				date = super.getNextFireTime();
			}
			
			boolean hasConflict = false;

			if (triggers != null && triggers.length > 0) {

				List<Trigger> list = new ArrayList<Trigger>();
				for (Trigger t : triggers) {
					if (t != null)
						list.add(t);
				}
				if (!list.isEmpty()) {
					Collections.sort(list);

					for (Trigger t : list) {
						Date hd = t.getNextFireTime();
						if (hd.getTime() == date.getTime()) {
							logger.info("Conflicted detected, higher periority:" + t + ", date:" + date +",conflict with this trigger:" + this.getFullName());						
							date = getFireTimeAfter(date);
							logger.info("Conflicted detected, current trigger will be skipped and delay to:" + date +","+ this.getFullName());
							hasConflict = true;
						}
					}
				}
			}
			if (hasConflict) {
				super.setNextFireTime(date);
			}
			return date;
		}*/
	}
	
	public static void saveMakeUp(MakeUp mp){
		JAXB.marshal(mp, new File(ScheduleUtils.makeupFilePath));
		ScheduleUtils.setMakeup(mp);
	}
	
	public static void loadMakeUp(){
		MakeUp makeup = JAXB.<MakeUp>unmarshal(new File(ScheduleUtils.makeupFilePath), MakeUp.class);
		ScheduleUtils.setMakeup(makeup);
	}

	// ///////////////////////----For triggers-------------------------------

	public static SimpleTriggerImpl makeImmediateTrigger(int repeatCount, long repeatInterval) {
		SimpleTriggerImpl trig = new SimpleTriggerImpl();
		trig.setStartTime(new Date());
		trig.setRepeatCount(repeatCount);
		trig.setRepeatInterval(repeatInterval);
		return trig;
	}

	public static SimpleTriggerImpl makeSecondlyTrigger(int intervalInSeconds){
		return makeSecondlyTrigger(intervalInSeconds, REPEAT_INDEFINITELY);
	}
	
	public static SimpleTriggerImpl makeSecondlyTrigger(int intervalInSeconds, int repeatCount) {
		SimpleTriggerImpl trig = new SimpleTriggerImpl();
		trig.setRepeatInterval(intervalInSeconds * 1000l);
		trig.setRepeatCount(repeatCount);
		trig.setStartTime(new Date());

		return trig;
	}

	public static SimpleTriggerImpl makeMinutelyTrigger(int intervalInMinutes) {
		return makeMinutelyTrigger(intervalInMinutes, REPEAT_INDEFINITELY);
	}

	public static SimpleTriggerImpl makeMinutelyTrigger(int intervalInMinutes, int repeatCount) {
		SimpleTriggerImpl trig = new SimpleTriggerImpl();
		trig.setRepeatInterval(intervalInMinutes * MILLISECONDS_IN_MINUTE);
		trig.setRepeatCount(repeatCount);
		trig.setStartTime(new Date());
		return trig;
	}

	public static SimpleTriggerImpl makeHourlyTrigger(int intervalInHours) {
		return makeHourlyTrigger(intervalInHours, REPEAT_INDEFINITELY);
	}

	public static SimpleTriggerImpl makeHourlyTrigger(int intervalInHours, int repeatCount) {
		SimpleTriggerImpl trig = new SimpleTriggerImpl();
		trig.setRepeatInterval(intervalInHours * MILLISECONDS_IN_HOUR);
		trig.setRepeatCount(repeatCount);
		trig.setStartTime(new Date());
		return trig;
	}

	public static CronTriggerImpl makeDailyTrigger(int hour, int minute) {
		CronTriggerImpl trig = new CronTriggerImpl();
		try {
			trig.setCronExpression("0 " + minute + " " + hour + " ? * *");
		} catch (Exception ignore) {
			return null;
		}
		trig.setStartTime(new Date());
		return trig;
	}

	public static CronTriggerImpl makeDailyTrigger(String trigName, int hour, int minute) {
		CronTriggerImpl trig = makeDailyTrigger(hour, minute);
		trig.setName(trigName);
		return trig;
	}

	public static CronTriggerImpl makeWeeklyTrigger(int dayOfWeek, int hour, int minute) {
		CronTriggerImpl trig = new CronTriggerImpl();
		try {
			trig.setCronExpression("0 " + minute + " " + hour + " ? * " + dayOfWeek);
		} catch (Exception ignore) {
			return null;
		}

		trig.setStartTime(new Date());
		return trig;
	}
	
	
	public static String[] getTriggerNames(Scheduler scheduler, String tirggerGroupname) throws SchedulerException{
		List<String> triggerNameList = new ArrayList<String>();
		Set<TriggerKey> sets = scheduler.getTriggerKeys(GroupMatcher.<TriggerKey>groupEquals(tirggerGroupname));

		if (sets != null)
			for (TriggerKey key : sets) {
				Trigger trigger = scheduler.getTrigger(key);
				triggerNameList.add(trigger.getKey().getName());
			}
		
		return triggerNameList.toArray(new String[0]);
		
//		return scheduler.getTriggerNames(tirggerGroupname);
	}

	public static MakeUp getMakeup() {
		return makeup;
	}

	public static void setMakeup(MakeUp makeup) {
		ScheduleUtils.makeup = makeup;
	}

	// ///////////////////////-----------------------------------
}

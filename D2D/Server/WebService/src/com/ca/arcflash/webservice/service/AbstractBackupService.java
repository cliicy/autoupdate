package com.ca.arcflash.webservice.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.AbstractTrigger;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jobscript.base.GenerateType;
import com.ca.arcflash.service.data.PeriodRetentionValue;
import com.ca.arcflash.service.internal.ProtectionInformationConverter;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.D2DTime;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.EveryMonthSchedule;
import com.ca.arcflash.webservice.data.EveryWeekSchedule;
import com.ca.arcflash.webservice.data.NextScheduleEvent;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupSchedule;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.scheduler.AdvancedScheduleTrigger;
import com.ca.arcflash.webservice.scheduler.BackupScheduleUtil;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.DSTSimpleTrigger;
import com.ca.arcflash.webservice.service.internal.BackupSummaryConverter;
import com.ca.arcflash.webservice.service.internal.RecoveryPointConverter;
import com.ca.arcflash.webservice.service.validator.BackupConfigurationValidator;
import com.ca.arcflash.webservice.util.DSTUtils;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;
import com.ca.arcflash.webservice.util.ScheduleUtils.PeriodTrigger;

public abstract class AbstractBackupService extends BaseService {
	private static final Logger logger = Logger.getLogger(AbstractBackupService.class);

	protected Scheduler otherScheduler;
	protected Scheduler bkpScheduler;
	protected Object lock = new Object();
	protected BackupSummaryConverter backupSummaryConverter = new BackupSummaryConverter();
	protected ProtectionInformationConverter protectionInformationConverter = new ProtectionInformationConverter();
	protected RecoveryPointConverter recoveryPointConverter = new RecoveryPointConverter();
	protected BackupConfigurationValidator backupConfigurationValidator = new BackupConfigurationValidator();
	
	abstract protected void removeAdvScheduleJob(String vmInstanceUUID);
	
	abstract protected JobDetail generateJobDetail(String vmInstanceUUID, int backupType);
	
	abstract public BackupConfiguration getBackupConfiguration(String vmInstanceUUID) throws ServiceException;
	
	abstract protected void fillJobNamesMap(String vmInstanceUUID, String jobName);
	
	
	public static class CONN_INFO {
		String domain = "";
		String userName = "";
		String pwd = "";

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getPwd() {
			return pwd;
		}

		public void setPwd(String pwd) {
			this.pwd = pwd;
		}
	}
	
	public Scheduler getBackupSchedule() {
		return bkpScheduler;
	}
	
	public Scheduler getOtherScheduler() {
		return otherScheduler;
	}
	
	public void configSchedule(String vmInstanceUUID) {
		try {
			cleanSchedule(vmInstanceUUID);
			
			BackupConfiguration conf = getBackupConfiguration(vmInstanceUUID);

			if (conf == null) {
				logger.debug("No configuration, return");
				return;
			}
			
			if (conf instanceof VMBackupConfiguration) {
				VMBackupConfiguration vmConf = (VMBackupConfiguration) conf;
				if (vmConf.getGenerateType() == GenerateType.MSPManualConversion) {
					logger.info("This configuration is for Remote Nodes imported from RHA, skip configure schedule.");
					return;
				}
			}

			if (conf.isDisablePlan()) {
				String msg = "The plan is disabled";
				if (!StringUtil.isEmptyOrNull(vmInstanceUUID)) {
					msg += " for VM[instanceUUID=" + vmInstanceUUID;
				}
				logger.info(msg);
				return;
			}

			if (conf.getBackupDataFormat() == 0) {
				this.configJobSchedule(vmInstanceUUID);
			} else {
				configAdvancedSchedule(vmInstanceUUID);
			}

		} catch (Exception e) {
			logger.error("Failed to configure backup schedule", e);
		}

	}
	
	protected void configJobSchedule(String vmInstaceUUID) {
		logger.debug("configJobSchedule() - start");

		try {
			removeBackupJobFromScheduler(vmInstaceUUID);

			BackupConfiguration configuration = getBackupConfiguration(vmInstaceUUID);
			if (configuration == null) {
				logger.debug("Backup configuration is null, return");
				return;
			}

			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertObject2String(configuration));
			}

			Trigger fullTrigger = null;
			Trigger incremTrigger = null;
			Trigger syncTrigger = null;
			Date startTime;
			if (configuration.getBackupStartTime() > 0) {
				startTime = new Date(configuration.getBackupStartTime());
			} else {
				startTime = new Date();
			}

			BackupSchedule fullBackupSchedule = configuration.getFullBackupSchedule();
			if (fullBackupSchedule != null && fullBackupSchedule.isEnabled()) {
				if (logger.isDebugEnabled()) {
					logger.debug(StringUtil.convertObject2String(fullBackupSchedule));
				}

				JobDetail jobDetail = generateJobDetail(vmInstaceUUID, BackupType.Full);
				fullTrigger = BackupScheduleUtil.generateQuartzTrigger(TRIGGER_NAME_BACKUP_FULL, getFullBackupTriggerGroupName(vmInstaceUUID), startTime, fullBackupSchedule, null, null, null, null);
				getBackupSchedule().scheduleJob(jobDetail, fullTrigger);
			}

			BackupSchedule resyncSchedule = configuration.getResyncBackupSchedule();
			if (resyncSchedule != null && resyncSchedule.isEnabled()) {
				if (logger.isDebugEnabled()) {
					logger.debug(StringUtil.convertObject2String(resyncSchedule));
				}

				JobDetail jobDetail = generateJobDetail(vmInstaceUUID, BackupType.Resync);
				syncTrigger = BackupScheduleUtil.generateQuartzTrigger(TIGGER_NAME_BACKUP_RESYNC, getResyncBackupTriggerGroupName(vmInstaceUUID), startTime, resyncSchedule, fullBackupSchedule, fullTrigger,
						null, null);
				try {
					getBackupSchedule().scheduleJob(jobDetail, syncTrigger);
				} catch (SchedulerException e) {
					logger.debug("Expected exception if following error message is \"... the given trigger will never fire.\""
							+ " Thrown if the ResyncBackupJob does not have chance to be scheduled. \n Error message: " + e.getMessage());
				}
			}

			BackupSchedule incrementalBackupSchedule = configuration.getIncrementalBackupSchedule();
			if (incrementalBackupSchedule != null && incrementalBackupSchedule.isEnabled()) {
				if (logger.isDebugEnabled()) {
					logger.debug(StringUtil.convertObject2String(incrementalBackupSchedule));
				}

				JobDetail jobDetail = generateJobDetail(vmInstaceUUID, BackupType.Incremental);
				if (fullTrigger != null) {
					incremTrigger = BackupScheduleUtil.generateQuartzTrigger(TRIGGER_NAME_BACKUP_INCREMENTAL, getIncBackupTriggerGroupName(vmInstaceUUID), startTime, incrementalBackupSchedule,
							fullBackupSchedule, fullTrigger, resyncSchedule, syncTrigger);
				} else {
					incremTrigger = BackupScheduleUtil.generateQuartzTrigger(TRIGGER_NAME_BACKUP_INCREMENTAL, getIncBackupTriggerGroupName(vmInstaceUUID), startTime, incrementalBackupSchedule,
							resyncSchedule, syncTrigger, null, null);
				}
				try {
					getBackupSchedule().scheduleJob(jobDetail, incremTrigger);
				} catch (SchedulerException e) {
					logger.debug("Expected exception if following error message is \"... the given trigger will never fire.\""
							+ " Thrown if the incremental backup job does not have chance to be scheduled. \n Error message: " + e.getMessage());
				}
			}

		} catch (Exception e) {
			logger.error("Configure Scheduler", e);
		}
		logger.debug("configJobSchedule() - end");
	}
	
	protected void configAdvancedSchedule(String vmInstaceUUID) {
		logger.debug("configAdvancedSchedule() - start");
		
		try {
			removeAdvScheduleJob(vmInstaceUUID);
			
			BackupConfiguration conf = getBackupConfiguration(vmInstaceUUID);
			if (conf == null || conf.getAdvanceSchedule() == null) {
				logger.debug("No configuration, return");
				return;
			}
			Date startTime = null;

			AdvanceSchedule schedule = conf.getAdvanceSchedule();
			if (schedule.getScheduleStartTime() > 0)
				startTime = new Date(schedule.getScheduleStartTime());
			else
				startTime = new Date();

			List<Trigger> peridTriggers = configPeriodSchedule(vmInstaceUUID, schedule.getPeriodSchedule(), startTime);

			for (DailyScheduleDetailItem item : schedule.getDailyScheduleDetailItems()) {
				if (item.getScheduleDetailItems() != null) {
					List<Trigger> list = new ArrayList<Trigger>();
					list.addAll(peridTriggers);
					// start the full backup schedule
					for (ScheduleDetailItem detail : item.getScheduleDetailItems()) {
						if (detail.getJobType() == BackupType.Full) {
							Trigger t = scheduleAdvJob(vmInstaceUUID, item.getDayofWeek(), detail, startTime, peridTriggers.toArray(new Trigger[0]));
							list.add(t);
						}
					}

					Trigger[] highT = list.toArray(new Trigger[0]);
					// start the Verify backup schedule
					for (ScheduleDetailItem detail : item.getScheduleDetailItems()) {
						if (detail.getJobType() == BackupType.Resync) {
							Trigger t = scheduleAdvJob(vmInstaceUUID, item.getDayofWeek(), detail, startTime, highT);
							list.add(t);
						}
					}
					// start the Increment backup schedule
					for (ScheduleDetailItem detail : item.getScheduleDetailItems()) {
						if (detail.getJobType() == BackupType.Incremental)
							scheduleAdvJob(vmInstaceUUID, item.getDayofWeek(), detail, startTime, list.toArray(new Trigger[0]));
					}
				}

			}

		} catch (Exception e) {
			logger.error("Failed to configure backup schedule", e);
		}
		
		logger.debug("configAdvancedSchedule() - end");
	}
	
	protected List<Trigger> configPeriodSchedule(String vmInstanceUUID, PeriodSchedule periodSchedule, Date startTime) throws SchedulerException {
		Trigger monthTrigger = wrap(processMonthSchedule(vmInstanceUUID, periodSchedule, startTime));
		Trigger weekTrigger = wrap(processWeekSchedule(vmInstanceUUID, periodSchedule, startTime, monthTrigger));
		Trigger dailyTrigger = wrap(processDaySchedule(vmInstanceUUID, periodSchedule, startTime, monthTrigger, weekTrigger));

		List<Trigger> triggers = new ArrayList<Trigger>();
		if (monthTrigger != null) {
			triggers.add(monthTrigger);
		}
		if (weekTrigger != null) {
			triggers.add(weekTrigger);
		}
		if (dailyTrigger != null) {
			triggers.add(dailyTrigger);
		}

		return triggers;
	}
	
	protected void fillJobDetailDataMap(JobDetailImpl jd, String vmInstanceUUID, int backupType, boolean isGenerateCatalog) {
		//VsphereService will override it; but currently, BackupService does NOT need it
	}
	
	protected AbstractTrigger processMonthSchedule(String vmInstanceUUID, PeriodSchedule periodSchedule, Date startTime) throws SchedulerException {
		PeriodTrigger monthlyTrigger = null;
		EveryMonthSchedule monthSchedule = periodSchedule.getMonthSchedule();
		if (monthSchedule != null && monthSchedule.isEnabled()) {
			String jobId = "monthly_" + monthSchedule.getBkpType() + "_" + monthSchedule.getDayTime() + "_" + monthSchedule.getRetentionCount();
			JobDetailImpl monthlyJobDetail = ScheduleUtils.newJobDetail(vmInstanceUUID, monthSchedule.getBkpType(), jobId);
			monthlyJobDetail.getJobDataMap().put("isMonthly", Boolean.TRUE);
			fillJobNamesMap(vmInstanceUUID, monthlyJobDetail.getName());
			
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

			monthlyTrigger = ScheduleUtils.getPeriodTrigger(vmInstanceUUID, monthSchedule.getBkpType(), startTime, jobId, cronExp,
					ScheduleUtils.MONTH_PRIORITY);
			monthlyTrigger.getJobDataMap().put("periodRetentionFlag", PeriodRetentionValue.QJDTO_B_Backup_Monthly);
			fillJobDetailDataMap(monthlyJobDetail, vmInstanceUUID, monthSchedule.getBkpType(), monthSchedule.isGenerateCatalog());

			getBackupSchedule().scheduleJob(monthlyJobDetail, monthlyTrigger);
		}

		return monthlyTrigger;
	}

	protected AbstractTrigger processWeekSchedule(String vmInstanceUUID, PeriodSchedule periodSchedule, Date startTime, Trigger monthTrigger) throws SchedulerException {
		PeriodTrigger weeklyTrigger = null;
		EveryWeekSchedule weekSchedule = periodSchedule.getWeekSchedule();
		if (weekSchedule != null && weekSchedule.isEnabled()) {
			String jobId = "weekly_" + weekSchedule.getBkpType() + "_" + weekSchedule.getDayTime() + "_" + weekSchedule.getRetentionCount();
			JobDetailImpl weeklyJobDetail = ScheduleUtils.newJobDetail(vmInstanceUUID, weekSchedule.getBkpType(), jobId);
			weeklyJobDetail.getJobDataMap().put("isWeekly", Boolean.TRUE);
			fillJobNamesMap(vmInstanceUUID, weeklyJobDetail.getName());
			
			String cronExpFormat = "0 %d %d ? * %d";
			String cronExp = String.format(cronExpFormat, weekSchedule.getDayTime().getMinute(), weekSchedule.getDayTime().getHour(),
					weekSchedule.getDayOfWeek());
			weeklyTrigger = ScheduleUtils.getPeriodTrigger(vmInstanceUUID, weekSchedule.getBkpType(), startTime, jobId, cronExp, 
					ScheduleUtils.WEEK_PRIORITY, monthTrigger);
			weeklyTrigger.getJobDataMap().put("periodRetentionFlag", PeriodRetentionValue.QJDTO_B_Backup_Weekly);
			fillJobDetailDataMap(weeklyJobDetail, vmInstanceUUID, weekSchedule.getBkpType(), weekSchedule.isGenerateCatalog());
			
			getBackupSchedule().scheduleJob(weeklyJobDetail, weeklyTrigger);
		}
		return weeklyTrigger;
	}

//	protected AbstractTrigger processDaySchedule(String vmInstanceUUID, PeriodSchedule periodSchedule, Date startTime, Trigger... triggers) throws SchedulerException {
//		PeriodTrigger dailyTrigger = null;
//		EveryDaySchedule daySchedule = periodSchedule.getDaySchedule();
//		if (daySchedule != null && daySchedule.isEnabled()) {
//			String jobId = "daily_" + daySchedule.getBkpType() + "_" + daySchedule.getDayTime() + "_" + daySchedule.getRetentionCount();
//			JobDetailImpl dailyJobDetail = ScheduleUtils.newJobDetail(vmInstanceUUID, daySchedule.getBkpType(), jobId);
//			dailyJobDetail.getJobDataMap().put("isDaily", Boolean.TRUE);
//			fillJobNamesMap(vmInstanceUUID, dailyJobDetail.getName());
//			
//			String cronExpFormat = "0 %d %d * * ?";
//			String cronExp = String.format(cronExpFormat, daySchedule.getDayTime().getMinute(), daySchedule.getDayTime().getHour());
//			dailyTrigger = ScheduleUtils.getPeriodTrigger(vmInstanceUUID, daySchedule.getBkpType(), startTime, jobId, cronExp, ScheduleUtils.DAY_PRIORITY, triggers);
//			dailyTrigger.getJobDataMap().put("periodRetentionFlag", PeriodRetentionValue.QJDTO_B_Backup_Daily);
//			fillJobDetailDataMap(dailyJobDetail, vmInstanceUUID, daySchedule.getBkpType(), daySchedule.isGenerateCatalog());
//			
//			getBackupSchedule().scheduleJob(dailyJobDetail, dailyTrigger);
//		}
//		return dailyTrigger;
//	}
	
	protected AbstractTrigger processDaySchedule(String vmInstanceUUID, PeriodSchedule periodSchedule, Date startTime, Trigger... triggers) throws SchedulerException {
		PeriodTrigger dailyTrigger = null;
		EveryDaySchedule daySchedule = periodSchedule.getDaySchedule();		
		if (daySchedule != null && daySchedule.isEnabled()) {
			String jobId = "daily_" + daySchedule.getBkpType() + "_" + daySchedule.getDayTime() + "_" + daySchedule.getRetentionCount();
			JobDetailImpl dailyJobDetail = ScheduleUtils.newJobDetail(vmInstanceUUID, daySchedule.getBkpType(), jobId);
			dailyJobDetail.getJobDataMap().put("isDaily", Boolean.TRUE);
			fillJobNamesMap(vmInstanceUUID, dailyJobDetail.getName());
			
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
			dailyTrigger = ScheduleUtils.getPeriodTrigger(vmInstanceUUID, daySchedule.getBkpType(), startTime, jobId, cronExp, ScheduleUtils.DAY_PRIORITY, triggers);
			dailyJobDetail.getJobDataMap().put("isDaily", Boolean.TRUE);
			dailyTrigger.getJobDataMap().put("periodRetentionFlag", PeriodRetentionValue.QJDTO_B_Backup_Daily);
			fillJobDetailDataMap(dailyJobDetail, vmInstanceUUID, daySchedule.getBkpType(), daySchedule.isGenerateCatalog());
			getBackupSchedule().scheduleJob(dailyJobDetail, dailyTrigger);
		}
		return dailyTrigger;
	}
	
	private Trigger scheduleAdvJob(String vmInstanceUUID, int dayOfWeek, ScheduleDetailItem scheduleDetail, Date triggerStart, Trigger... triggers) throws Exception {
		int backupType = scheduleDetail.getJobType();
		String id = ScheduleUtils.generateJobID(dayOfWeek, scheduleDetail, vmInstanceUUID);
		JobDetailImpl jd = ScheduleUtils.newJobDetail(vmInstanceUUID, backupType, id);
		return scheduleWeekDayJob(vmInstanceUUID, dayOfWeek, scheduleDetail, triggerStart, jd, backupType, id, triggers);
	}

	private Trigger scheduleWeekDayJob(String vmInstanceUUID, int dayOfWeek, ScheduleDetailItem scheduleDetail, Date triggerStart, JobDetail jd, int backupType, String id,
			Trigger... triggers) throws Exception {
		AdvancedScheduleTrigger trigger = ScheduleUtils.newAdvancedTrigger(vmInstanceUUID, backupType, triggerStart, id, scheduleDetail, dayOfWeek, triggers);
		if (jd != null) {
			fillJobNamesMap(vmInstanceUUID, ((JobDetailImpl)jd).getName());
			
			boolean isGenerateCatalog = false;
			BackupConfiguration conf = getBackupConfiguration(vmInstanceUUID);
			if (conf != null) {
				isGenerateCatalog = conf.isGenerateCatalog();
			}
			fillJobDetailDataMap((JobDetailImpl)jd, vmInstanceUUID, backupType, isGenerateCatalog);
		}
		ScheduleUtils.scheduleJob(scheduleDetail, id, getBackupSchedule(), jd, trigger);
		return trigger;
	}

	private Trigger wrap(AbstractTrigger trigger) {
		if (trigger == null)
			return null;
		try {
			return getBackupSchedule().getTrigger(new TriggerKey(trigger.getName(), trigger.getGroup()));
		} catch (SchedulerException e) {
			logger.error("failed to get wrapper", e);
		}
		return null;
	}
	
	protected long getCalTimeInMillies(D2DTime time) {
		Calendar cal = Calendar.getInstance();
		cal.set(time.getYear(), time.getMonth(), time.getDay(), time.getHourOfday(), time.getMinute(), 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}
	
	protected void cleanSchedule(String vmInstanceUUID) {
		removeBackupJobFromScheduler(vmInstanceUUID);
		removeAdvScheduleJob(vmInstanceUUID);
	}
	
	protected void removeBackupJobFromScheduler(String vmInstanceUUID) {
		if(vmInstanceUUID == null || vmInstanceUUID.trim().isEmpty() ) {
			vmInstanceUUID = "";
		}
		try {
			getBackupSchedule().deleteJob(new JobKey(JOB_NAME_BACKUP_FULL, getBackupJobGroupName(vmInstanceUUID)));
			getBackupSchedule().deleteJob(new JobKey(JOB_NAME_BACKUP_INCREMENTAL, getBackupJobGroupName(vmInstanceUUID)));
			getBackupSchedule().deleteJob(new JobKey(JOB_NAME_BACKUP_RESYNC, getBackupJobGroupName(vmInstanceUUID)));
		} catch (Exception e) {
			logger.warn("Error occurs when removing backup jobs from scheduler");
		}
	}
	
	/**
	 * For DST saving, the backups configured in the one hour interval of DST
	 * start time will be skipped, we need to make up one job for backups
	 * interval larger than one day.
	 * 
	 * @param trigger
	 * @param time
	 * @return
	 */
	protected boolean dailyMissed(DSTSimpleTrigger trigger, Date time) {
		// 21967017-1 koyto01  RunNow job doesn't need makeup job for DSTskipped.
		Date prevFire = trigger.getPreviousFireTime();
		if(prevFire == null)
		{
			return false;
		}
		Date nextfire = trigger.getNextFireTime();
		// -----------
//		Date nextfire = trigger.getNextFireTime();
//		Date prevFire = trigger.getPreviousFireTime();
		long interval = trigger.getRepeatInterval();

		if (interval >= 24 * 3600 * 1000) {
			if ((time.getTime() + interval + 15 * 60 * 1000) < nextfire.getTime()) {
				// if DST ends, there will be 25 hours on that day
				if (prevFire != null && prevFire.getTimezoneOffset() < nextfire.getTimezoneOffset()) {
					logger.info("DST ends, no need to makeup the missed job");
					return false;
				} else {
					logger.info("DST starts, there is one day no backup, so makeup one");
					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * For DST saving, the backups configured in the one hour interval of DST
	 * start time will be skipped, we need to make up one job for backups
	 * interval larger than one day.
	 * 
	 * @param currentTime
	 * @return
	 */
	public boolean makeupForDSTSkippedDaily(Date currentTime, String vmInstanceUUID) {
		logger.debug("makeupForDSTSkippedDaily() - start");
		String fullTriggerName = "", incrementalTriggerName = "", resyncTriggerName = "";
		try {
			if (getBackupSchedule() == null)
				return false;
			
			if (!StringUtil.isEmptyOrNull(vmInstanceUUID)) {
				fullTriggerName = getFullBackupTriggerGroupName(vmInstanceUUID);
				incrementalTriggerName = getIncBackupTriggerGroupName(vmInstanceUUID);
				resyncTriggerName = getResyncBackupTriggerGroupName(vmInstanceUUID);
			} else {
				fullTriggerName = TRIGGER_GROUP_BACKUP_NAME;
				incrementalTriggerName = TRIGGER_GROUP_BACKUP_NAME;
				resyncTriggerName = TRIGGER_GROUP_BACKUP_NAME;
			}
			DSTSimpleTrigger fullTrigger = (DSTSimpleTrigger) getBackupSchedule().getTrigger(new TriggerKey(TRIGGER_NAME_BACKUP_FULL, fullTriggerName));
			DSTSimpleTrigger incrementalTrigger = (DSTSimpleTrigger) getBackupSchedule().getTrigger(new TriggerKey(TRIGGER_NAME_BACKUP_INCREMENTAL, incrementalTriggerName));
			DSTSimpleTrigger resyncTrigger = (DSTSimpleTrigger) getBackupSchedule().getTrigger(new TriggerKey(TIGGER_NAME_BACKUP_RESYNC, resyncTriggerName));

			DSTSimpleTrigger runTrigger = null;

			boolean missed = true;

			if (fullTrigger != null) {
				missed &= dailyMissed(fullTrigger, currentTime);
				if (missed)
					runTrigger = fullTrigger;
			}

			if (resyncTrigger != null) {
				missed &= dailyMissed(resyncTrigger, currentTime);
				if (missed)
					runTrigger = resyncTrigger;
			}

			if (incrementalTrigger != null) {
				missed &= dailyMissed(incrementalTrigger, currentTime);
				if (missed)
					runTrigger = incrementalTrigger;
			}

			if (missed && runTrigger != null) {
				JobDetailImpl jobDetail = (JobDetailImpl)getBackupSchedule().getJobDetail(new JobKey(runTrigger.getJobName(), runTrigger.getJobGroup()));
				jobDetail.setName(runTrigger.getJobName() + "_DSTMakeup");
				DSTSimpleTrigger trigger = new DSTSimpleTrigger();
				trigger.setStartTime(new Date(currentTime.getTime() + runTrigger.getRepeatInterval()));
				trigger.setName(runTrigger.getName() + "_DSTMakeup");
				trigger.setGroup(TRIGGER_GROUP_BACKUP_NAME);
				trigger.setRepeatCount(0);
				trigger.setRepeatInterval(0);
				getBackupSchedule().scheduleJob(jobDetail, trigger);

				String dstStart = "";
				java.util.Calendar dstTime = getDSTStartTime(runTrigger.getPreviousFireTime(), runTrigger.getNextFireTime());
				SimpleDateFormat dateFormat = new SimpleDateFormat(CommonNativeInstance.getICommonNative().getDateTimeFormat().getTimeDateFormat(),
						DataFormatUtil.getDateFormatLocale());
				if (dstTime != null) {
					java.util.Calendar dstDate = java.util.Calendar.getInstance();
					dstDate.setTime(dstTime.getTime());
					dstDate.add(dstDate.DATE, -1);
					// The date and time should be formated seperately, or you
					// will get wrong time.
					dstStart = new SimpleDateFormat(CommonNativeInstance.getICommonNative().getDateTimeFormat().getDateFormat(),
							DataFormatUtil.getDateFormatLocale()).format(dstDate.getTime())
							+ " "
							+ new SimpleDateFormat(CommonNativeInstance.getICommonNative().getDateTimeFormat().getTimeFormat(),
									DataFormatUtil.getDateFormatLocale()).format(dstTime.getTime());
				}

				getNativeFacade().addLogActivity(
						Constants.AFRES_AFALOG_WARNING,
						Constants.AFRES_AFJWBS_JOB_RETRY,
						new String[] {
								WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_DST_SKIPPED, dstStart,
										dateFormat.format(trigger.getStartTime())), "", "", "", "" });
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return true;
	}
	
	protected String getFullBackupTriggerGroupName(String vmInstanceUUID){
		return ScheduleUtils.getFullBackupTriggerGroupName(vmInstanceUUID);
	}
	
	protected String getIncBackupTriggerGroupName(String vmInstanceUUID){
		return ScheduleUtils.getIncBackupTriggerGroupName(vmInstanceUUID);
	}
	
	protected String getResyncBackupTriggerGroupName(String vmInstanceUUID){
		return ScheduleUtils.getResyncBackupTriggerGroupName(vmInstanceUUID);
	}
	
	protected String getBackupJobGroupName(String vmInstanceUUID){
		return ScheduleUtils.getBackupJobGroupName(vmInstanceUUID);
	}
	
	private java.util.Calendar getDSTStartTime(Date prevFireTime, Date nextFireTime) {
		Date ret = null;
		for (long time = prevFireTime.getTime(); time <= nextFireTime.getTime(); time += 3600 * 1000) {
			ret = new Date(time);
			ret.setSeconds(0);
			if (ret.getHours() >= 1) {
				Date lastHour = new Date(ret.getYear(), ret.getMonth(), ret.getDate(), ret.getHours() - 1, ret.getMinutes());
				if (ret.getTime() == lastHour.getTime())
					break;
			}
		}
		if (ret != null) {
			java.util.Calendar cal = java.util.Calendar.getInstance();
			cal.setTime(ret);
			cal.add(cal.DATE, 1);
			cal.set(cal.HOUR_OF_DAY, cal.get(cal.HOUR_OF_DAY) - 1);
			cal.set(cal.MINUTE, 0);
			cal.set(cal.SECOND, 0);
			return cal;
		} else {
			return null;
		}
	}
	
	public CONN_INFO getCONN_INFO(BackupConfiguration config) {

		CONN_INFO info = new CONN_INFO();
		if (config != null) {
			String domain = "";
			String userName = getUserNameFromConfiguration(config);
			String pwd = "";
			if (userName != null && userName.trim().length() > 0) {
				userName = userName.trim();
				int index = userName.indexOf("\\");
				if (index > 0) {
					domain = userName.substring(0, index);
					userName = userName.substring(index + 1);
				}
				pwd = getPasswordFromConfiguration(config);
			}
			info.setDomain(domain);
			info.setUserName(userName);
			info.setPwd(pwd);
		}

		if (info.getDomain() == null)
			info.setDomain("");
		if (info.getUserName() == null)
			info.setUserName("");
		if (info.getPwd() == null)
			info.setPwd("");

		return info;
	}
	
	protected String getUserNameFromConfiguration(BackupConfiguration config) {
		return config.getUserName();
	}
	
	protected String getPasswordFromConfiguration(BackupConfiguration config) {
		return config.getPassword();
	}
	
	protected List<Trigger> getBackupTriggers(String vmInstanceUUID) {
		List<Trigger> triggerList = new ArrayList<Trigger>();

		try {
			String[] triggerGroupNames = new String[] { getFullBackupTriggerGroupName(vmInstanceUUID), getResyncBackupTriggerGroupName(vmInstanceUUID),
					getIncBackupTriggerGroupName(vmInstanceUUID) };
			for (String tirggerGroupname : triggerGroupNames) {
				
				Set<TriggerKey> sets = getBackupSchedule().getTriggerKeys(GroupMatcher.<TriggerKey>groupEquals(tirggerGroupname));
				if (sets == null)
					continue;

				for (TriggerKey key : sets) {
					Trigger trigger = getBackupSchedule().getTrigger(key);
					triggerList.add(trigger);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return triggerList;
	}
		
	public boolean isScheduledFullJob(int date, String vmInstanceUUID) {
		try {
			List<Trigger> triggers = this.getBackupTriggers(vmInstanceUUID);
			for(Trigger trigger : triggers){
				if(((AbstractTrigger)trigger).getGroup().equals(getFullBackupTriggerGroupName(vmInstanceUUID))){
					Date nextEvent = trigger.getNextFireTime();
					if(nextEvent.getDate() == date){
						logger.info("Current date is " + date + " next scheduled full is " + trigger.getNextFireTime());
						return true;
					}
				}
			}
			
			return false;
		}catch(Exception e) {
			logger.error("Failed to get full trigger");
		}
		
		return false;
	}
	
	protected int getBkpType(String vmInstanceUUID, Trigger resultTrigger) {
		int backupType = BackupType.Unknown;
		if (resultTrigger.getKey().getGroup().equals(getFullBackupTriggerGroupName(vmInstanceUUID))) {
			backupType = BackupType.Full;
		} else if (resultTrigger.getKey().getGroup().equals(getResyncBackupTriggerGroupName(vmInstanceUUID))) {
			backupType = BackupType.Resync;
		} else if (resultTrigger.getKey().getGroup().equals(getIncBackupTriggerGroupName(vmInstanceUUID))) {
			backupType = BackupType.Incremental;
		}
		return backupType;
	}

	protected NextScheduleEvent getNewNextEvent(String vmInstanceUUID) throws ServiceException {
		List<Trigger> triggerList = getBackupTriggers(vmInstanceUUID);
		Trigger resultTrigger = null;
		for (Trigger trigger : triggerList) {
			if (resultTrigger == null || resultTrigger.getNextFireTime() == null) {
				resultTrigger = trigger;
			} else {
				if (trigger == null || trigger.getNextFireTime() == null) {
					continue;
				}
				
				int compareResult = resultTrigger.getNextFireTime().compareTo(trigger.getNextFireTime());
				if (compareResult == 0) {
					compareResult = trigger.getPriority() - resultTrigger.getPriority();
				}
				if (compareResult > 0) {
					resultTrigger = trigger;
				}
			}
		}

		if (resultTrigger != null) {
			NextScheduleEvent result = new NextScheduleEvent();
			Date nextEvent = resultTrigger.getNextFireTime();
			int backupType = getBkpType(vmInstanceUUID, resultTrigger);
			result.setBackupType(backupType);
			result.setDate(nextEvent);
			result.setTimeZoneOffset(DSTUtils.getTimezoneOffset(nextEvent));

			if (resultTrigger.getJobDataMap().containsKey("periodRetentionFlag")) {
				int periodRetentionFlag = resultTrigger.getJobDataMap().getIntValue("periodRetentionFlag");
				result.setPeriodRetentionFlag(periodRetentionFlag);
			}
			return result;
		}

		return null;
	}
}

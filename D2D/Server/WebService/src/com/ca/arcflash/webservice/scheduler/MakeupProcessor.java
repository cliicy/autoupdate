package com.ca.arcflash.webservice.scheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.AbstractTrigger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jni.common.JJobHistoryFilterCol;
import com.ca.arcflash.jni.common.JJobHistoryResult;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.service.data.PeriodRetentionValue;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.service.jni.model.JRestorePoint;
import com.ca.arcflash.service.util.JobHistoryConverter;
import com.ca.arcflash.webservice.FlashServiceImpl;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.ConnectionInfo;
import com.ca.arcflash.webservice.data.NextScheduleEvent;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupSchedule;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.backup.RetryPolicy;
import com.ca.arcflash.webservice.data.backup.RpsPolicy4D2D;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.service.AbstractBackupService.CONN_INFO;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.RestoreService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.internal.RecoveryPointConverter;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.TheadPoolManager;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class MakeupProcessor {
	private static final Logger logger = Logger.getLogger(MakeupProcessor.class);

	private RecoveryPointConverter recoveryPointConverter = new RecoveryPointConverter();

	public boolean triggerMakeup(Date startCheckTime, Date[] range, boolean isMonthly, boolean isWeekly, boolean isDaily) throws SchedulerException {
		StringBuilder backupName = new StringBuilder();
		AbstractTrigger makeupTrigger = (AbstractTrigger)getMakeupTrigger(backupName, startCheckTime, range, isMonthly, isWeekly, isDaily);

		if (makeupTrigger != null) {
			if (isMonthly || isWeekly || isDaily || checkRetryPolicy4Next()) {
				doMakeup(backupName, makeupTrigger, isMonthly, isWeekly, isDaily);
			}
			return true;
		}
		return false;
	}

	private boolean checkRetryPolicy4Next() {
		// if now is within NearToNextEvent (e.g. 15 minutes by default in
		// RetryPolicy.xml file) against next backup event
		try {
			RetryPolicy policy = BackupService.getInstance().getRetryPolicy(CommonService.RETRY_BACKUP);
			NextScheduleEvent nextScheduleEvent = BackupService.getInstance().getNextScheduleEvent();
			if (nextScheduleEvent != null) {
				Date nextDate = nextScheduleEvent.getDate();
				java.util.Calendar incomingBackup = getUTCNow();
				incomingBackup.setTimeInMillis(nextDate.getTime());

				java.util.Calendar now = getUTCNow();
				now.add(java.util.Calendar.MINUTE, policy.getNearToNextEvent());

				if (now.after(incomingBackup)) {
					logger.debug("dealWithMissedBackup() - end with too near to next event");
					String time = BackupConverterUtil.dateToString(nextDate);
					BackupService
							.getInstance()
							.getNativeFacade()
							.addLogActivity(
									Constants.AFRES_AFALOG_WARNING,
									Constants.AFRES_AFJWBS_JOB_RETRY,
									new String[] {
											WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SKIPPED_NEXT, time,
													WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED)), "", "", "", "" });
					return false;
				}
			}

		} catch (ServiceException e) {
			logger.error("dealWithMissedBackup() - end", e);
		}
		return true;
	}

	protected java.util.Calendar getUTCNow() {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		java.util.Calendar cal = java.util.Calendar.getInstance(timeZone);
		cal.setTimeInMillis(System.currentTimeMillis());
		return cal;
	}

	private Trigger getMakeupTrigger(StringBuilder retBackupName, Date startCheckTime, Date[] range, boolean isMonthly, boolean isWeekly, boolean isDaily)
			throws SchedulerException {
		Trigger makeupTrigger = null;

		String[] backupNames = new String[] { BaseService.JOB_NAME_BACKUP_FULL, BaseService.JOB_NAME_BACKUP_RESYNC, BaseService.JOB_NAME_BACKUP_INCREMENTAL };
		String[] triggerGroupNames = new String[] { BaseService.TRIGGER_GROUP_BACKUP_NAME_FULL, BaseService.TRIGGER_GROUP_BACKUP_NAME_RESYNC,
				BaseService.TRIGGER_GROUP_BACKUP_NAME_INCREMENTAL };
		int[] backupTypes = new int[] { BackupType.Full, BackupType.Resync, BackupType.Incremental };

		java.util.Calendar cal = java.util.Calendar.getInstance();
		Date currentTime = cal.getTime();

		for (int index = 0; index < backupNames.length; index++) {
			String triggerGroupName = triggerGroupNames[index];
			String[] triggerNames = ScheduleUtils.getTriggerNames(BackupService.getInstance().getBackupSchedule(), triggerGroupName);
			if (triggerNames == null || triggerNames.length <= 0)
				continue;

			Trigger trigger = null;
			Date makeupStartDate = null;
			Date nextFiredTime = null;
			for (String triggerName : triggerNames) {
				Trigger curTrigger = BackupService.getInstance().getBackupSchedule().getTrigger(new TriggerKey(triggerName, triggerGroupName));
				boolean isPeriod = isMonthly || isWeekly || isDaily;
				if (curTrigger != null) {
					if ((!isPeriod && isPeriodTrigger(curTrigger))
							|| (isMonthly && curTrigger.getPriority() != ScheduleUtils.MONTH_PRIORITY)
							|| (isWeekly && curTrigger.getPriority() != ScheduleUtils.WEEK_PRIORITY)
							|| (isDaily && curTrigger.getPriority() != ScheduleUtils.DAY_PRIORITY)
						 ) {
						continue;
					}			

					Date lastFiredTime = startCheckTime;
					nextFiredTime = null;
					// if (isPeriod) {
					// nextFiredTime = ((PeriodTrigger)
					// curTrigger).getTimeAfter(lastFiredTime);
					// } else
					{
						nextFiredTime = curTrigger.getFireTimeAfter(lastFiredTime);
					}

					if (range == null) {// for the past missed time
						if ((nextFiredTime == null) || (nextFiredTime.compareTo(currentTime) >= 0))
							continue;
					}

					// find the latest missed backup
					int counter = 0;
					while ((nextFiredTime != null) && (nextFiredTime.compareTo(currentTime) < 0) && counter < 365*24*4) {
						counter++;
						lastFiredTime = nextFiredTime;
						// if (isPeriod) {
						// nextFiredTime = ((PeriodTrigger)
						// curTrigger).getTimeAfter(lastFiredTime);
						// } else
						{
							nextFiredTime = curTrigger.getFireTimeAfter(lastFiredTime);
						}
					}

					if (makeupStartDate == null) {
						makeupStartDate = lastFiredTime;
						trigger = curTrigger;
					} else if (lastFiredTime.compareTo(makeupStartDate) > 0) {
						makeupStartDate = lastFiredTime;
						trigger = curTrigger;
					}

				}
			}

			if (makeupStartDate != null) {
				if (hasConflict(makeupStartDate, isMonthly, isWeekly, isDaily))
					return null;
				if (range == null) {
					if (!(isBacked(backupTypes[index], makeupStartDate, currentTime))) {
						retBackupName.append(backupNames[index]);
						makeupTrigger = trigger;
						break;
					}
				} else {// check if makeup time is in, and next backup time is
						// out of range
					// if (makeupStartDate.compareTo(range[0]) >=0 &&
					// makeupStartDate.compareTo(range[1]) <=0 &&
					// (nextFiredTime ==null || nextFiredTime.after(range[1])))

					if ((nextFiredTime == null || nextFiredTime.after(range[1]))
							&& (nextFiredTime.getTime() >= startCheckTime.getTime() + range[1].getTime() - range[0].getTime())) {
						retBackupName.append(backupNames[index]);
						makeupTrigger = trigger;
						break;
					}

					// //iF the start time is set to over the schedule start
					// time. then ingore this.
					// if(nextFiredTime.getTime() < startCheckTime.getTime() +
					// range[1].getTime() - range[0].getTime()){
					// return null;
					// }
				}
			}
		}

		return makeupTrigger;
	}

	protected boolean isPeriodTrigger(Trigger curTrigger) {
		return curTrigger.getPriority() == ScheduleUtils.MONTH_PRIORITY
				||  curTrigger.getPriority() == ScheduleUtils.WEEK_PRIORITY
				||  curTrigger.getPriority() == ScheduleUtils.DAY_PRIORITY;
	}

	protected boolean hasConflict(Date makeupStartDate, boolean isMonthly, boolean isWeekly, boolean isDaily) {
		HashMap<Long, ConfilctData> map = ScheduleUtils.getMakeup().getConflictTimeAndFlag();
		long makeupTime = makeupStartDate.getTime();
		for (Long conflictTime : map.keySet()) {
			if (makeupTime == conflictTime) {
				ConfilctData data = map.get(conflictTime);
				int current = data.getCurrent();
				int conflict = data.getFlag();
				boolean isforthisType = false;

				if (isWeekly) {
					isforthisType = (conflict & PeriodRetentionValue.QJDTO_B_Backup_Weekly) > 0;					
				}

				if (isDaily) {
					isforthisType = (conflict & PeriodRetentionValue.QJDTO_B_Backup_Daily) > 0;					
				}
				
				if(!isforthisType) break;
				
				boolean isExist = isConflictRecoveryPointExist(conflictTime, current, isWeekly, isDaily);
				
				if(isExist) return true;				
			}
		}

		return false;
	}

	private boolean isConflictRecoveryPointExist(long conflictTime, int current, boolean isWeekly, boolean isDaily) {
		if(isWeekly){
			// check monthly RP exist
			return isBacked(conflictTime, PeriodRetentionValue.QJDTO_B_Backup_Monthly);
			
		}else if(isDaily){
			if((current & PeriodRetentionValue.QJDTO_B_Backup_Weekly) > 0){
				// conflict with Weekly, check weekly RP exist
				return isBacked(conflictTime, PeriodRetentionValue.QJDTO_B_Backup_Weekly);
			}else if((current & PeriodRetentionValue.QJDTO_B_Backup_Monthly) > 0){
				// conflict with Monthly, check monthly RP  exist
				return isBacked(conflictTime, PeriodRetentionValue.QJDTO_B_Backup_Monthly);
			}
		}
		return false;
	}
	
	private boolean isBacked(long conflictTime, int periodRetentionFlag) {
		BackupConfiguration configuration;
		try {
			configuration = BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e) {
			logger.error("getBackupConfiguration failed", e);
			return false;
		}
		long schStartTime = configuration.getAdvanceSchedule().getScheduleStartTime();
		java.util.Calendar cal = java.util.Calendar.getInstance();
		Date scheduleStartDateTime = cal.getTime();
		scheduleStartDateTime.setTime(schStartTime);	
		
		RecoveryPoint[]  recoveryPoints = getRecoveryPoints(configuration, scheduleStartDateTime, new Date());	
		if(recoveryPoints == null){
			logger.warn("fail to get recovery points' infomation, so we assume there is no missed backup job.");
			return true;
		}
		for (RecoveryPoint rp : recoveryPoints) {
			if (rp.getBackupStatus() == 1 || rp.getBackupStatus() == 4) {
				if (rp.getPeriodRetentionFlag() == periodRetentionFlag) {
					long timediff = rp.getTime().getTime() > conflictTime? (rp.getTime().getTime()- conflictTime) : (conflictTime - rp.getTime().getTime()); 						 
					long allowDiff = 15*60*1000; // within 15 minutes
					if( timediff < allowDiff)  
						return true;
				}
			}
		}
		return false;
	}
	
/*	private boolean isBacked(long conflictTime, int periodRetentionFlag) {
		BackupConfiguration configuration;
		try {
			configuration = BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e) {
			logger.error("getBackupConfiguration failed", e);
			return false;
		}
		CONN_INFO info = BackupService.getInstance().getCONN_INFO(configuration);
		
		ConnectionInfo connInfo = new ConnectionInfo();
		connInfo.setDestination(configuration.getDestination());
		connInfo.setDomain(info.getDomain());
		connInfo.setUserName(info.getUserName());
		connInfo.setPassword(info.getPwd());
		RecoveryPoint[]  recoveryPoints = null;
		long schStartTime = configuration.getAdvanceSchedule().getScheduleStartTime();
		java.util.Calendar cal = java.util.Calendar.getInstance();
		Date scheduleStartDateTime = cal.getTime();
		scheduleStartDateTime.setTime(schStartTime);		
		try {	
			recoveryPoints = RestoreService.getInstance().getRecoveryPoints(connInfo.getDestination(), connInfo.getDomain(), connInfo.getUserName(), connInfo.getPassword(), scheduleStartDateTime, new Date(), false);			
		} catch (Throwable e) {
			logger.error("getRecoveryPoints failed", e);
			return false;
		}
		
		if ((recoveryPoints != null) && (recoveryPoints.length > 0)) {
			for (RecoveryPoint rp : recoveryPoints) {
				if (rp.getBackupStatus() == 1 || rp.getBackupStatus() == 4) {
					if (rp.getPeriodRetentionFlag() == periodRetentionFlag) {
						long timediff = rp.getTime().getTime() > conflictTime? (rp.getTime().getTime()- conflictTime) : (conflictTime - rp.getTime().getTime()); 						 
						long allowDiff = 15*60*1000; // within 15 minutes
						if( timediff < allowDiff)  
							return true;
					}
				}
			}
		}
		
		return false;
	}*/

	/**
	 * This method can only decide on the current destination.
	 * 
	 * @param backupType
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private boolean isBacked(int backupType, Date startDate, Date endDate) {
		logger.debug("isBacked() - start");
		if (logger.isDebugEnabled()) {
			logger.debug("backupType:" + backupType + " StartDate:" + startDate + " endDate:" + endDate);
		}
		
		BackupConfiguration configuration;
		try {
			configuration = BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e) {
			logger.error("getBackupConfiguration failed", e);
			return false;
		}
		
		RecoveryPoint[]  recoveryPoints = getRecoveryPoints(configuration, startDate, endDate);	
		if(recoveryPoints == null){
			logger.warn("fail to get recovery points' infomation, so we assume there is no missed backup job.");
			return true;
		}

		for (int i = 0; i < recoveryPoints.length; i++) {
			RecoveryPoint jRestorePoint = recoveryPoints[i];
			int type = jRestorePoint.getBackupType();
			// if there is full backup, we don't need other backups
			if (type == BackupType.Full)
				return true;
			else if (backupType == BackupType.Incremental && type == BackupType.Resync)
				return true;
			else if (backupType == type) {
				logger.debug("isBacked - end with true during period");
				return true;
			}
		}
		logger.debug("isBacked - end with false due to no such backups during period");
		return false;
	}
	
/*	private boolean isBacked(int backupType, Date startDate, Date endDate) {
		logger.debug("isBacked() - start");
		if (logger.isDebugEnabled()) {
			logger.debug("backupType:" + backupType + " StartDate:" + startDate + " endDate:" + endDate);
		}
		try {
			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			if (configuration == null) {
				logger.debug("isBacked - end with false due to null configuration");
				return false;
			}
			CONN_INFO connInfo = BackupService.getInstance().getCONN_INFO(configuration);

		//	JBackupInfo[] recoveryPoints = BackupService.getInstance().getNativeFacade()
		//			.getRecoveryPoints(configuration.getDestination(), info.getDomain(), info.getUserName(), info.getPwd(), startDate, endDate, -1);
			RecoveryPoint[] recoveryPoints = RestoreService.getInstance().getRecoveryPoints(configuration.getDestination(), connInfo.getDomain(), connInfo.getUserName(), connInfo.getPwd(), startDate, endDate, false);	
			if (recoveryPoints == null) {
				logger.debug("isBacked - end with false due to empty backups during period");
				return false;
			}
			for (int i = 0; i < recoveryPoints.length; i++) {
				RecoveryPoint jRestorePoint = recoveryPoints[i];
				int type = jRestorePoint.getBackupType();
				// if there is full backup, we don't need other backups
				if (type == BackupType.Full)
					return true;
				else if (backupType == BackupType.Incremental && type == BackupType.Resync)
					return true;
				else if (backupType == type) {
					logger.debug("isBacked - end with true during period");
					return true;
				}

			}
			logger.debug("isBacked - end with false due to no such backups during period");
			return false;
		} catch (Throwable e) {
			logger.error("isBacked() - end", e);
		}
		return false;
	}*/

	private void doMakeup(StringBuilder backupName, AbstractTrigger makeupTrigger, boolean isMonthly, boolean isWeekly, boolean isDaily) throws SchedulerException {
		String tempFullJobName = makeupTrigger.getFullJobName();
		int end = tempFullJobName.indexOf('.');
		String tempJobGroupName = tempFullJobName.substring(0, end);
		String tempJobName = tempFullJobName.substring(end + 1);

		String jobName = BackupConverterUtil.backupIndicatorToName(backupName.toString());
		String jobKind = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED);

		String actmsg = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SCHEDULETYPE_SCHEDULED, jobKind,
				WebServiceMessages.getResource(Constants.regular), jobName);

		if (isDaily) {
			logger.info("isDaily makeup:" + isDaily);
			actmsg = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SCHEDULETYPE_SCHEDULED, jobKind,
					WebServiceMessages.getResource(Constants.daily), jobName);
		}

		if (isWeekly) {
			logger.info("isWeekly makeup:" + isWeekly);
			actmsg = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SCHEDULETYPE_SCHEDULED, jobKind,
					WebServiceMessages.getResource(Constants.weekly), jobName);
		}

		if (isMonthly) {
			logger.info("isMonthly makeup:" + isMonthly);
			actmsg = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SCHEDULETYPE_SCHEDULED, jobKind,
					WebServiceMessages.getResource(Constants.monthly), jobName);
		}

		logger.info("make up message:" + actmsg);

		BackupService.getInstance().getNativeFacade()
				.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY, new String[] { actmsg, "", "", "", "" });

		try {
			if (BackupService.getInstance().getBackupConfiguration().isD2dOrRPSDestType()) {
				BackupService.getInstance().getBackupSchedule().triggerJob(new JobKey(tempJobName, tempJobGroupName));
			} else {
				boolean isSameHost = false;
				try {
					RpsHost rpsHost = BackupService.getInstance().getBackupConfiguration().getBackupRpsDestSetting().getRpsHost();

					String rpsHostName = rpsHost.getRhostname();
					if (!StringUtil.isEmptyOrNull(rpsHostName)
							&& (rpsHostName.equalsIgnoreCase("localhost") || rpsHostName.equalsIgnoreCase(ServiceContext.getInstance().getLocalMachineName()))) {
						isSameHost = true;
					}
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				final boolean isSameM = isSameHost;
				final String jN = tempJobName, jG = tempJobGroupName;

				Callable<Boolean> task = new Callable<Boolean>() {

					private void wait4RPS() throws Exception {
						int cnt = 0;
						do {
							try {
								// waiting for one minute
								// for the RPS start up.

								RpsPolicy4D2D policy = BackupService.getInstance().checkRPS4Backup();
								if (policy != null)
									break;
							} catch (Exception e) {
								logger.error("error", e);
							}

							Thread.sleep(3000);

						} while (cnt++ < 20);
					}

					@Override
					public Boolean call() throws Exception {
						// if (isSameM)
						{
							wait4RPS();
						}
						BackupService.getInstance().getBackupSchedule().triggerJob(new JobKey(jN, jG));
						return true;
					}
				};

				ExecutorService es = TheadPoolManager.getThreadPool(TheadPoolManager.UtilTheadPool);
				es.submit(task);

			}
		} catch (Exception e) {
			logger.error("Schedule Makeup failed", e);
		}

		String msg = String.format("trigger a missed backup job, triggerName[%s] backupType[%s]", tempFullJobName, backupName);
		logger.info(msg);
	}	

	public void dealWithMissedBackupAdvancedFormat() {
		logger.debug("dealWithMissedBackupAdvancedFormat() - begin");

		try {
			if (!checkAdvScheduleEnabled() || !checkRetryPolicyAllowed() || checkJobRunning())
				return;

			// if certain kind of backup type is missed
			if (BackupService.getInstance().getBackupConfiguration().getAdvanceSchedule().getPeriodSchedule().isEnabled()) {
				if (processPeriodSchedule())
					return;
			}

			processAdvRepeatSchedule();

		} catch (Exception e) {
			logger.error("dealWithMissedBackupAdvancedFormat failed,", e);
		}

		logger.debug("dealWithMissedBackupAdvancedFormat() - end");
	}

	private boolean processPeriodSchedule() {
		boolean isTrigged = false;
		try {

			PeriodSchedule ps = BackupService.getInstance().getBackupConfiguration().getAdvanceSchedule().getPeriodSchedule();

			if (ps.getMonthSchedule() != null && ps.getMonthSchedule().isEnabled()) {
				Date[] range = getMonthlyRange();
				if (!isMonthlyBackuped(PeriodRetentionValue.QJDTO_B_Backup_Monthly, range[0], range[1])) {
					Date startPointOfTime = getStartTime(PeriodRetentionValue.QJDTO_B_Backup_Monthly);
					isTrigged = triggerMakeup(startPointOfTime, range, true, false, false);
				}
			}
			logger.info("Monthly makeup:" + isTrigged);

			if (ps.getWeekSchedule() != null && ps.getWeekSchedule().isEnabled()) {
				Date[] range = getWeeklyRange();
				if (!isTrigged && !isWeeklyBackuped(PeriodRetentionValue.QJDTO_B_Backup_Weekly, range[0], range[1])) {
					Date startPointOfTime = getStartTime(PeriodRetentionValue.QJDTO_B_Backup_Weekly);
					isTrigged = triggerMakeup(startPointOfTime, range, false, true, false);
				}
			}

			logger.info("Weekly makeup:" + isTrigged);
			if (ps.getDaySchedule() != null && ps.getDaySchedule().isEnabled()) {
				Date[] range = getDailyRange();
				Date startPointOfTime = getStartTime(PeriodRetentionValue.QJDTO_B_Backup_Daily);
				java.util.Calendar cal = java.util.Calendar.getInstance();
				Date currentTime = cal.getTime();
				
				Date lastFiredTime = getLastDailyFiredTime(startPointOfTime,currentTime);
				if (!isTrigged && !isDailyBackuped(PeriodRetentionValue.QJDTO_B_Backup_Daily, lastFiredTime, range[1])) {
					isTrigged = triggerMakeup(startPointOfTime, range, false, false, true);
				}
			}
			logger.info("Daily makeup:" + isTrigged);
		} catch (SchedulerException e) {
			logger.error("processPeriodSchedule() - end", e);
		} catch (Throwable e) {
			logger.error("processPeriodSchedule() - end with error:", e);
		}
		return isTrigged;
	}
	
	private Date getLastDailyFiredTime(Date start, Date end) throws SchedulerException{
		String[] backupNames = new String[] { BaseService.JOB_NAME_BACKUP_FULL, BaseService.JOB_NAME_BACKUP_RESYNC, BaseService.JOB_NAME_BACKUP_INCREMENTAL };
		String[] triggerGroupNames = new String[] { BaseService.TRIGGER_GROUP_BACKUP_NAME_FULL, BaseService.TRIGGER_GROUP_BACKUP_NAME_RESYNC,
				BaseService.TRIGGER_GROUP_BACKUP_NAME_INCREMENTAL };
		Date lastFiredTime = start;
		for (int index = 0; index < backupNames.length; index++){
			String triggerGroupName = triggerGroupNames[index];
			String[] triggerNames = ScheduleUtils.getTriggerNames(BackupService.getInstance().getBackupSchedule(), triggerGroupName);
			if (triggerNames == null || triggerNames.length <= 0)
				continue;
			Date nextFiredTime = null;
			for (String triggerName : triggerNames) {
				Trigger curTrigger = BackupService.getInstance().getBackupSchedule().getTrigger(new TriggerKey(triggerName, triggerGroupName));
				if(curTrigger!=null){
					if( curTrigger.getPriority() != ScheduleUtils.DAY_PRIORITY){
						continue;
					}					
					nextFiredTime = curTrigger.getFireTimeAfter(lastFiredTime);					
					while ((nextFiredTime != null) && (nextFiredTime.compareTo(end) < 0)) {
						lastFiredTime = nextFiredTime;													
						nextFiredTime = curTrigger.getFireTimeAfter(lastFiredTime);					
					}
				}					
			}
		}
		logger.info("The last fire time is:" + lastFiredTime.getDate()+":"+lastFiredTime.getHours()+":"+lastFiredTime.getMinutes());

		return lastFiredTime;
	}
	
	protected Date[] getMonthlyRange() {
		Date[] dates = new Date[2];
		Date begin, end;
		java.util.Calendar beginOfthisMonth = java.util.Calendar.getInstance();
		beginOfthisMonth.set(java.util.Calendar.DAY_OF_MONTH, 1);
		beginOfthisMonth.set(java.util.Calendar.HOUR_OF_DAY, 0);
		beginOfthisMonth.set(java.util.Calendar.MINUTE, 0);
		beginOfthisMonth.set(java.util.Calendar.SECOND, 0);
		beginOfthisMonth.set(java.util.Calendar.MILLISECOND, 0);
		begin = beginOfthisMonth.getTime();

		java.util.Calendar lastSecondsOfThisMonth = java.util.Calendar.getInstance();
		lastSecondsOfThisMonth.add(java.util.Calendar.MONTH, 1);
		lastSecondsOfThisMonth.set(java.util.Calendar.DAY_OF_MONTH, 1);
		lastSecondsOfThisMonth.set(java.util.Calendar.HOUR_OF_DAY, 0);
		lastSecondsOfThisMonth.set(java.util.Calendar.MINUTE, 0);
		lastSecondsOfThisMonth.set(java.util.Calendar.SECOND, 0);
		lastSecondsOfThisMonth.set(java.util.Calendar.MILLISECOND, 0);
		lastSecondsOfThisMonth.setTimeInMillis(lastSecondsOfThisMonth.getTimeInMillis() - 1);
		end = lastSecondsOfThisMonth.getTime();

		dates[0] = begin;
		dates[1] = end;
		return dates;
	}

	private boolean isMonthlyBackuped(int backupMonthlyFlag, Date begin, Date end) {
		return isPeriodBackuped(begin, end, backupMonthlyFlag);
	}

	protected Date[] getWeeklyRange() {
		Date[] dates = new Date[2];
		Date begin, end;
		java.util.Calendar beginOfthisWeek = java.util.Calendar.getInstance();
		int dayOfWeek = beginOfthisWeek.get(java.util.Calendar.DAY_OF_WEEK);
		beginOfthisWeek.add(java.util.Calendar.DAY_OF_MONTH, 1 - dayOfWeek);
		beginOfthisWeek.set(java.util.Calendar.HOUR_OF_DAY, 0);
		beginOfthisWeek.set(java.util.Calendar.MINUTE, 0);
		beginOfthisWeek.set(java.util.Calendar.SECOND, 0);
		beginOfthisWeek.set(java.util.Calendar.MILLISECOND, 0);
		begin = beginOfthisWeek.getTime();

		java.util.Calendar lastSecondsOfThisWeek = java.util.Calendar.getInstance();
		dayOfWeek = lastSecondsOfThisWeek.get(java.util.Calendar.DAY_OF_WEEK);
		lastSecondsOfThisWeek.add(java.util.Calendar.DAY_OF_MONTH, 8 - dayOfWeek);
		lastSecondsOfThisWeek.set(java.util.Calendar.HOUR_OF_DAY, 0);
		lastSecondsOfThisWeek.set(java.util.Calendar.MINUTE, 0);
		lastSecondsOfThisWeek.set(java.util.Calendar.SECOND, 0);
		lastSecondsOfThisWeek.set(java.util.Calendar.MILLISECOND, 0);
		lastSecondsOfThisWeek.setTimeInMillis(lastSecondsOfThisWeek.getTimeInMillis() - 1);
		end = lastSecondsOfThisWeek.getTime();

		dates[0] = begin;
		dates[1] = end;
		return dates;
	}

	private boolean isWeeklyBackuped(int backupWeeklyFlag, Date begin, Date end) {
		return isPeriodBackuped(begin, end, backupWeeklyFlag);
	}

	protected Date[] getDailyRange() {
		Date[] dates = new Date[2];
		Date begin, end;
		java.util.Calendar beginOfToday = java.util.Calendar.getInstance();
		beginOfToday.set(java.util.Calendar.HOUR_OF_DAY, 0);
		beginOfToday.set(java.util.Calendar.MINUTE, 0);
		beginOfToday.set(java.util.Calendar.SECOND, 0);
		beginOfToday.set(java.util.Calendar.MILLISECOND, 0);
		begin = beginOfToday.getTime();

		java.util.Calendar lastSecondsOfToday = java.util.Calendar.getInstance();
		lastSecondsOfToday.add(java.util.Calendar.DAY_OF_MONTH, 1);
		lastSecondsOfToday.set(java.util.Calendar.HOUR_OF_DAY, 0);
		lastSecondsOfToday.set(java.util.Calendar.MINUTE, 0);
		lastSecondsOfToday.set(java.util.Calendar.SECOND, 0);
		lastSecondsOfToday.set(java.util.Calendar.MILLISECOND, 0);
		lastSecondsOfToday.setTimeInMillis(lastSecondsOfToday.getTimeInMillis() - 1);
		end = lastSecondsOfToday.getTime();

		dates[0] = begin;
		dates[1] = end;
		return dates;
	}

	private boolean isDailyBackuped(int backupDailyFlag, Date begin, Date end) {
		return isPeriodBackuped(begin, end, backupDailyFlag);
	}

	private boolean isPeriodBackuped(Date startDate, Date endDate, int advflag) {
		logger.debug("StartDate:" + startDate + " endDate:" + endDate + "advflag:" + advflag);
		try {
			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			if (configuration == null) {
				logger.debug("isPeriodBackuped - end with false due to null configuration");
				return false;
			}

			CONN_INFO info = BackupService.getInstance().getCONN_INFO(configuration);

			JRestorePoint[] restorePoints = BackupService.getInstance().getNativeFacade()
					.getRestorePoints(configuration.getDestination(), info.getDomain(), info.getUserName(), info.getPwd(), startDate, endDate, false);

			for (JRestorePoint restorePoint : restorePoints) {
				if ((restorePoint.getDwBKAdvSchFlag() & advflag) > 0) {
					return true;
				}
			}

		} catch (Throwable e) {
			logger.error("isPeriodBackuped - end", e);
		}

		logger.debug("isPeriodBackuped:false - no such backups during period");
		return false;
	}

	private void processAdvRepeatSchedule() {
		try {
			Date startCheckTime = getStartTime(0);

			triggerMakeup(startCheckTime, null, false, false, false);		
		} catch (Throwable e) {			
			logger.error("processAdvRepeatSchedule failed", e);
		}
	}

	private Date getStartTime(int periodRetentionFlag) throws ServiceException {
		BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
		java.util.Calendar cal = java.util.Calendar.getInstance();
		Date currentTime = cal.getTime();
		RecoveryPoint[]  recoveryPoints = getRecoveryPoints(configuration, new Date(0), currentTime);	

		Date startCheckTime = null;
		long schStartTime = configuration.getAdvanceSchedule().getScheduleStartTime();
		// the start point of time is beginning from the latest one of the last
		// backup and the scheduled start time configured in the settings.
		// e.g. If the last backup happened after the schedule time. The start
		// time should be the last backup time,
		// otherwise if the start time is after the last successful the same
		// schedule type (daily/weekly...), the start time should be the
		// schedule start time,
		// that must probably be user reconfigured the schedule settings.
		if(recoveryPoints == null){
			logger.warn("fail to get recovery points' infomation, so we assume there is no missed backup job.");
			startCheckTime = currentTime;
		}else if(recoveryPoints.length == 0){
			startCheckTime = new Date(schStartTime);
		}else{
			for (RecoveryPoint rp : recoveryPoints) {
				if (rp.getBackupStatus() == 1 || rp.getBackupStatus() == 4) {
					if (rp.getPeriodRetentionFlag() == periodRetentionFlag) {
						startCheckTime = rp.getTime();
					}
				}
			}

			if (startCheckTime == null || startCheckTime.getTime() < schStartTime) {
				startCheckTime = new Date(schStartTime);
			}
		}
		return startCheckTime;
	}
	
/*	private Date getStartTime(int periodRetentionFlag) throws ServiceException {
		BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
		CONN_INFO connInfo = BackupService.getInstance().getCONN_INFO(configuration);
		java.util.Calendar cal = java.util.Calendar.getInstance();
		Date currentTime = cal.getTime();
		RecoveryPoint[] recoveryPoints = null;
		try {
			// restorePoints =
			// BackupService.getInstance().getNativeFacade().getRecentBackupsByServerTime(configuration.getDestination(),
			// info.getDomain(), info.getUserName(), info.getPwd(),
			// BackupType.All, BackupStatus.All, new Date(0), currentTime,
			// null);
			//restorePoints = BackupService.getInstance().getNativeFacade()
			//		.getRecoveryPoints(configuration.getDestination(), info.getDomain(), info.getUserName(), info.getPwd(), new Date(0), currentTime, -1);
			recoveryPoints = RestoreService.getInstance().getRecoveryPoints(configuration.getDestination(),  connInfo.getDomain(), connInfo.getUserName(), connInfo.getPwd(),  new Date(0), currentTime, false);	
		} catch (Throwable e) {
			logger.error("getStartTime failed", e);
		}
		//RecoveryPoint[] result = recoveryPointConverter.convert2RecoveryPointsFromBackupInfo(restorePoints);

		Date startCheckTime = null;
		long schStartTime = configuration.getAdvanceSchedule().getScheduleStartTime();
		// the start point of time is beginning from the latest one of the last
		// backup and the scheduled start time configured in the settings.
		// e.g. If the last backup happened after the schedule time. The start
		// time should be the last backup time,
		// otherwise if the start time is after the last successful the same
		// schedule type (daily/weekly...), the start time should be the
		// schedule start time,
		// that must probably be user reconfigured the schedule settings.

		if ((recoveryPoints != null) && (recoveryPoints.length > 0)) {
			for (RecoveryPoint rp : recoveryPoints) {
				if (rp.getBackupStatus() == 1 || rp.getBackupStatus() == 4) {
					if (rp.getPeriodRetentionFlag() == periodRetentionFlag) {
						startCheckTime = rp.getTime();
					}
				}
			}

			if (startCheckTime == null || startCheckTime.getTime() < schStartTime) {
				startCheckTime = new Date(schStartTime);
			}
		} else {
			startCheckTime = new Date(schStartTime);
		}
		return startCheckTime;
	}*/

	private boolean checkJobRunning() {
		// if a running already job
		if (BackupService.getInstance().getNativeFacade().checkJobExist()) {
			logger.debug("dealWithMissedBackup() - end with running job");
			BackupService
					.getInstance()
					.getNativeFacade()
					.addLogActivity(
							Constants.AFRES_AFALOG_WARNING,
							Constants.AFRES_AFJWBS_JOB_RETRY,
							new String[] {
									WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SKIPPED_RUNNING,
											WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED)), "", "", "", "" });
			return true;
		}
		return false;
	}

	private boolean checkAdvScheduleEnabled() {
		try {
			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			if (configuration == null) {
				logger.debug("dealWithMissedBackup() - end with null configuration");
				return false;
			}
			AdvanceSchedule advanceSchedule = configuration.getAdvanceSchedule();
			if (advanceSchedule == null) {
				logger.debug("dealWithMissedBackup() - end with disable the advance backup scheduled in configuration");
				return false;
			}

		} catch (ServiceException e1) {
			logger.error("dealWithMissedBackup() - end", e1);
			return false;
		}
		return true;
	}

	private boolean checkRetryPolicyAllowed() {
		RetryPolicy policy = BackupService.getInstance().getRetryPolicy(CommonService.RETRY_BACKUP);

		if (!policy.isEnabled() || !policy.isMissedEnabled()) {
			logger.debug("dealWithMissedBackup() - end with disabled retry policy for missed(s) backup");
			BackupService
					.getInstance()
					.getNativeFacade()
					.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY,
							new String[] { WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_DISABLED), "", "", "", "" });
			return false;
		}
		// if now is within NearToNextEvent (e.g. 15 minutes by default in
		// RetryPolicy.xml file) against next backup event
		// try {
		// NextScheduleEvent nextScheduleEvent = this.getNextScheduleEvent();
		// if (nextScheduleEvent != null) {
		// Date nextDate = nextScheduleEvent.getDate();
		// java.util.Calendar incomingBackup = getUTCNow();
		// incomingBackup.setTimeInMillis(nextDate.getTime());
		//
		// java.util.Calendar now = getUTCNow();
		// now.add(java.util.Calendar.MINUTE, policy.getNearToNextEvent());
		//
		// if (now.after(incomingBackup)) {
		// logger.debug("dealWithMissedBackup() - end with too near to next event");
		// String time = BackupConverterUtil.dateToString(nextDate);
		// getNativeFacade().addLogActivity(
		// Constants.AFRES_AFALOG_WARNING,
		// Constants.AFRES_AFJWBS_JOB_RETRY,
		// new String[] {
		// WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SKIPPED_NEXT,
		// time,
		// WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED)),
		// "", "",
		// "", "" });
		// return false;
		// }
		// }
		//
		// } catch (ServiceException e) {
		// logger.error("dealWithMissedBackup() - end", e);
		// }
		return true;
	}

	public void dealWithMissedBackupStandardFormat() {
		logger.debug("dealWithMissedBackup() - begin");
		boolean fullScheduled = false;
		boolean increScheduled = false;
		boolean resyncScheduled = false;
		RetryPolicy policy = BackupService.getInstance().getRetryPolicy(CommonService.RETRY_BACKUP);
		BackupConfiguration configuration = null;

		try {
			configuration = BackupService.getInstance().getBackupConfiguration();
			if (configuration == null) {
				logger.debug("dealWithMissedBackup() - end with null configuration");
				return;
			}
			BackupSchedule backupSchedule = configuration.getFullBackupSchedule();
			if (backupSchedule != null) {
				fullScheduled = backupSchedule.isEnabled();
			}
			backupSchedule = configuration.getIncrementalBackupSchedule();
			if (backupSchedule != null) {
				increScheduled = backupSchedule.isEnabled();
			}

			backupSchedule = configuration.getResyncBackupSchedule();
			if (backupSchedule != null) {
				resyncScheduled = backupSchedule.isEnabled();
			}
			if (!(fullScheduled || increScheduled || resyncScheduled)) {
				logger.debug("dealWithMissedBackup() - end with no backup scheduled in configuration");
				return;
			}
		} catch (ServiceException e1) {
			logger.error("dealWithMissedBackup() - end", e1);
			return;
		}
		NextScheduleEvent nextScheduleEvent;
		java.util.Calendar cal = new FlashServiceImpl().getServerCalendar();
		Date currentTime = cal.getTime();
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		// condition 1, if enabled or not
		if (!(policy.isEnabled() && policy.isMissedEnabled())) {
			logger.debug("dealWithMissedBackup() - end with disabled retry policy for missed(s) backup");
			nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY,
					new String[] { WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_DISABLED), "", "", "", "" });
			return;
		}

		// condition 2, if now is within 15 minutes toward to next backup event
		try {
			nextScheduleEvent = BackupService.getInstance().getNextScheduleEvent();
			if (nextScheduleEvent != null) {
				Date nextDate = nextScheduleEvent.getDate();
				java.util.Calendar serverCalendar = new FlashServiceImpl().getServerCalendar();
				serverCalendar.setTimeInMillis(nextDate.getTime());
				cal.add(java.util.Calendar.MINUTE, policy.getNearToNextEvent());
				if (cal.after(serverCalendar)) {
					logger.debug("dealWithMissedBackup() - end with too near to next event");
					String time = BackupConverterUtil.dateToString(nextDate);
					nativeFacade.addLogActivity(
							Constants.AFRES_AFALOG_WARNING,
							Constants.AFRES_AFJWBS_JOB_RETRY,
							new String[] {
									WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SKIPPED_NEXT, time,
											WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED)), "", "", "", "" });
					return;
				}
				cal.add(java.util.Calendar.MINUTE, 0 - policy.getNearToNextEvent());
			}

		} catch (ServiceException e) {
			logger.error("dealWithMissedBackup() - end", e);
		}
		// condition 3, if no running job
		if (nativeFacade.checkJobExist()) {
			logger.debug("dealWithMissedBackup() - end with running job");
			nativeFacade.addLogActivity(
					Constants.AFRES_AFALOG_WARNING,
					Constants.AFRES_AFJWBS_JOB_RETRY,
					new String[] {
							WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SKIPPED_RUNNING,
									WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED)), "", "", "", "" });
			return;
		}
		// condition 4, if correspondent backup type is missed
		try {

			Trigger fullTrigger = BackupService.getInstance().getBackupSchedule()
					.getTrigger(new TriggerKey(BaseService.TRIGGER_NAME_BACKUP_FULL, BaseService.TRIGGER_GROUP_BACKUP_NAME));
			Trigger incrementalTrigger = BackupService.getInstance().getBackupSchedule()
					.getTrigger(new TriggerKey(BaseService.TRIGGER_NAME_BACKUP_INCREMENTAL, BaseService.TRIGGER_GROUP_BACKUP_NAME));
			Trigger resyncTrigger = BackupService.getInstance().getBackupSchedule()
					.getTrigger(new TriggerKey(BaseService.TIGGER_NAME_BACKUP_RESYNC, BaseService.TRIGGER_GROUP_BACKUP_NAME));

			if (fullScheduled && fullTrigger != null && fullTrigger instanceof DSTSimpleTrigger) {
				if (isJobJustTriggered(fullTrigger, currentTime, policy.getNearToNextEvent()))
					return;

				Date fireTimeBefore = ((DSTSimpleTrigger) fullTrigger).getFireTimeBefore(new Date(configuration.getBackupStartTime()), currentTime);
				if (fireTimeBefore == null) {
					logger.debug("dealWithMissedBackup() - previous fullTrigger fire time is null");
				} else if (fireTimeBefore.after(currentTime)) {
					logger.debug("dealWithMissedBackup() - previous fullTrigger fire time is after current time");
				} else {
					boolean isbacked = isBacked(BackupType.Full, fireTimeBefore, currentTime);
					if (!isbacked) // we get a missed full backup
					{
						nativeFacade.addLogActivity(
								Constants.AFRES_AFALOG_WARNING,
								Constants.AFRES_AFJWBS_JOB_RETRY,
								new String[] {
										WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SCHEDULED,
												WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED),
												BackupConverterUtil.backupIndicatorToName(BaseService.JOB_NAME_BACKUP_FULL)), "", "", "", "" });
						BackupService.getInstance().getBackupSchedule().triggerJob(new JobKey(BaseService.JOB_NAME_BACKUP_FULL, BaseService.JOB_GROUP_BACKUP_NAME));
						logger.debug("dealWithMissedBackup() - end trigger a missed full backup");
						return;
					}
				}
			}

			if (resyncScheduled && resyncTrigger != null && resyncTrigger instanceof DSTSimpleTrigger) {
				if (isJobJustTriggered(resyncTrigger, currentTime, policy.getNearToNextEvent()))
					return;
				// we should compute the missed backup from the real starttime,
				// instead of
				// the changed starttime for DST.
				Date fireTimeBefore = ((DSTSimpleTrigger) resyncTrigger).getFireTimeBefore(new Date(configuration.getBackupStartTime()), currentTime);
				if (fireTimeBefore == null) {
					logger.debug("dealWithMissedBackup() - previous resyncTrigger fire time is null");
				} else if (fireTimeBefore.after(currentTime)) {
					logger.debug("dealWithMissedBackup() - previous resyncTrigger fire time is after current time");
				} else {
					boolean isbacked = isBacked(BackupType.Resync, fireTimeBefore, currentTime);
					if (!isbacked) // we get a missed resync backup
					{
						nativeFacade.addLogActivity(
								Constants.AFRES_AFALOG_WARNING,
								Constants.AFRES_AFJWBS_JOB_RETRY,
								new String[] {
										WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SCHEDULED,
												WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED),
												BackupConverterUtil.backupIndicatorToName(BaseService.JOB_NAME_BACKUP_RESYNC)), "", "", "", "" });
						BackupService.getInstance().getBackupSchedule().triggerJob(new JobKey(BaseService.JOB_NAME_BACKUP_RESYNC, BaseService.JOB_GROUP_BACKUP_NAME));
						logger.debug("dealWithMissedBackup() - end trigger a missed resync backup");
						return;
					}
				}
			}

			if (increScheduled && incrementalTrigger != null && incrementalTrigger instanceof DSTSimpleTrigger) {
				if (isJobJustTriggered(incrementalTrigger, currentTime, policy.getNearToNextEvent()))
					return;

				Date fireTimeBefore = ((DSTSimpleTrigger) incrementalTrigger).getFireTimeBefore(new Date(configuration.getBackupStartTime()), currentTime);

				if (fireTimeBefore == null) {
					logger.debug("dealWithMissedBackup() - previous incrementalTrigger fire time is null");
				} else if (fireTimeBefore.after(currentTime)) {
					logger.debug("dealWithMissedBackup() - previous  incrementalTrigger fire time is after current time");
				} else {
					boolean isbacked = isBacked(BackupType.Incremental, fireTimeBefore, currentTime);
					if (!isbacked)// we get a missed incrementalTrigger backup
					{
						nativeFacade.addLogActivity(
								Constants.AFRES_AFALOG_WARNING,
								Constants.AFRES_AFJWBS_JOB_RETRY,
								new String[] {
										WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SCHEDULED,
												WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED),
												BackupConverterUtil.backupIndicatorToName(BaseService.JOB_NAME_BACKUP_INCREMENTAL)), "", "", "", "" });
						BackupService.getInstance().getBackupSchedule().triggerJob(new JobKey(BaseService.JOB_NAME_BACKUP_INCREMENTAL, BaseService.JOB_GROUP_BACKUP_NAME));
						logger.debug("dealWithMissedBackup() - end trigger a missed incremental backup");
						return;
					}
				}
			}

		} catch (SchedulerException e) {
			logger.error("dealWithMissedBackup() - end", e);
		}

		logger.debug("dealWithMissedBackup() - end");
	}

	/**
	 * If the job is just triggered, there may not be this job in the backup
	 * history, while we should not makeup this job again.
	 * 
	 * @param trigger
	 * @param currentTime
	 * @param nearEventInMinutes
	 * @return
	 */
	protected boolean isJobJustTriggered(Trigger trigger, Date currentTime, int nearEventInMinutes) {
		java.util.Calendar cal = new FlashServiceImpl().getServerCalendar();
		Date prevTime = trigger.getPreviousFireTime();
		if (prevTime != null) {
			java.util.Calendar serverCalendar = new FlashServiceImpl().getServerCalendar();
			serverCalendar.setTimeInMillis(prevTime.getTime());
			cal.add(java.util.Calendar.MINUTE, nearEventInMinutes);
			if (cal.getTime().after(currentTime)) {
				return true;
			}
		}

		return false;
	}
	
	private RecoveryPoint[] getRecoveryPoints(BackupConfiguration configuration, Date beginDate, Date endDate) {
		if(configuration==null){
			return null;
		}
		RecoveryPoint[]  recoveryPoints = null;
		CONN_INFO info = BackupService.getInstance().getCONN_INFO(configuration);
		ConnectionInfo connInfo = new ConnectionInfo();
		connInfo.setDestination(configuration.getDestination());
		connInfo.setDomain(info.getDomain());
		connInfo.setUserName(info.getUserName());
		connInfo.setPassword(info.getPwd());
		try {	
			recoveryPoints = RestoreService.getInstance().getRecoveryPoints(connInfo.getDestination(), connInfo.getDomain(), connInfo.getUserName(), connInfo.getPassword(), beginDate, endDate, false);			
		} catch (ServiceException exception){
			logger.error(exception.getErrorCode());
			logger.error(CommonService.getInstance().getServiceError(exception.getErrorCode(), exception.getMultipleArguments()));
		} catch (Throwable e) {
			logger.error("getRecoveryPoints failed", e);
		}
		if(recoveryPoints == null){
			logger.warn("fail to get recovery points from destination, so search from job history...");
			recoveryPoints = getRecoveryPointsFromJobHistory(beginDate, endDate);
		}
		return recoveryPoints;
	}
	
	private RecoveryPoint[] getRecoveryPointsFromJobHistory(Date beginDate, Date endDate){
		
		JJobHistoryFilterCol filter = new JJobHistoryFilterCol();
		filter.setJobType(JobType.JOBTYPE_BACKUP);
		filter.setJobStatus(JobStatus.JOBSTATUS_FINISHED);
//		filter.setStartTimeValue(beginDate.getTime());
//		filter.setEndTimeValue(endDate.getTime());
		filter.setEndTimeValue(beginDate.getTime());// mean from beginDate to current time
		JJobHistoryResult ret_finish = CommonNativeInstance.getICommonNative().getJobHistory(0, 10000, filter);
		List<RecoveryPoint> finishList = JobHistoryConverter.convertJResultToRecoveryPoint(ret_finish);
		
		filter.setJobStatus(JobStatus.JOBSTATUS_INCOMPLETE);
		JJobHistoryResult ret_incomplete = CommonNativeInstance.getICommonNative().getJobHistory(0, 10000, filter);
		List<RecoveryPoint> incompleteList = JobHistoryConverter.convertJResultToRecoveryPoint(ret_incomplete);
		if(finishList.size()==0&&incompleteList.size()==0){
			return null;
		}else{
			finishList.addAll(incompleteList);
			logger.debug("getRecoveryPointsFromJobHistory: "+finishList.size());
			return finishList.toArray(new RecoveryPoint[0]);
		}
	}
}

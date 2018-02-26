package com.ca.arcflash.webservice.scheduler;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.AbstractTrigger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.service.data.PeriodRetentionValue;
import com.ca.arcflash.service.jni.model.JRestorePoint;
import com.ca.arcflash.webservice.FlashServiceImpl;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.NextScheduleEvent;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.backup.BackupSchedule;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.backup.RetryPolicy;
import com.ca.arcflash.webservice.data.backup.RpsPolicy4D2D;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.RestoreService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.internal.VSphereJobQueue;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.TheadPoolManager;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class VMMakeupProcessor extends MakeupProcessor{
	private static final Logger logger = Logger.getLogger(MakeupProcessor.class);
	
	public void dealWithMissedBackupAdvancedFormat(VirtualMachine vm) {
		logger.info("begin: vmUuid = " + vm.getVmInstanceUUID());

		try {
			if (!checkAdvScheduleEnabled(vm) || !checkRetryPolicyAllowed() || checkJobRunning(vm))
				return;

			// if certain kind of backup type is missed
			if (VSphereService.getInstance().getVMBackupConfiguration(vm).getAdvanceSchedule().getPeriodSchedule().isEnabled()) {
				if (processPeriodSchedule(vm))
					return;
			}

			processAdvRepeatSchedule(vm);

		} catch (Exception e) {
			logger.error("dealWithMissedBackupAdvancedFormat failed,", e);
		}

		logger.debug("end");
	}
	
	private void processAdvRepeatSchedule(VirtualMachine vm) {
		try {
			Date startCheckTime = getStartTime(0, vm);

			triggerMakeup(startCheckTime, null, false, false, false, vm);		
		} catch (Throwable e) {			
			logger.error("processAdvRepeatSchedule failed", e);
		}
	}
	
	public boolean triggerMakeup(Date startCheckTime, Date[] range, boolean isMonthly, boolean isWeekly, boolean isDaily, VirtualMachine vm) throws SchedulerException {
		StringBuilder backupName = new StringBuilder();
		AbstractTrigger makeupTrigger = (AbstractTrigger)getMakeupTrigger(backupName, startCheckTime, range, isMonthly, isWeekly, isDaily, vm);

		if (makeupTrigger != null) {
			if (isMonthly || isWeekly || isDaily || checkRetryPolicy4Next(vm)) {
				doMakeup(backupName, makeupTrigger, isMonthly, isWeekly, isDaily,vm);
			}
			return true;
		}
		return false;
	}
	
	private void doMakeup(StringBuilder backupName, AbstractTrigger makeupTrigger, boolean isMonthly, boolean isWeekly, boolean isDaily, final VirtualMachine vm) throws SchedulerException {
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

		VSphereService.getInstance().getNativeFacade()
				.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY, new String[] { actmsg, "", "", "", "" });

		try {
			if (VSphereService.getInstance().getVMBackupConfiguration(vm).isD2dOrRPSDestType()) {
				VSphereService.getInstance().getBackupSchedule().triggerJob(new JobKey(tempJobName, tempJobGroupName));
			} else {
				boolean isSameHost = false;
				try {
					RpsHost rpsHost = VSphereService.getInstance().getVMBackupConfiguration(vm).getBackupRpsDestSetting().getRpsHost();

					String rpsHostName = rpsHost.getRhostname();
					if (!StringUtil.isEmptyOrNull(rpsHostName)
							&& (rpsHostName.equalsIgnoreCase("localhost") || rpsHostName.equalsIgnoreCase(ServiceContext.getInstance().getLocalMachineName()))) {
						isSameHost = true;
					}
				} catch (ServiceException e) {
					e.printStackTrace();
				}

				final String jN = tempJobName, jG = tempJobGroupName;

				Callable<Boolean> task = new Callable<Boolean>() {

					private void wait4RPS() throws Exception {
						int cnt = 0;
						do {
							try {
								// waiting for one minute
								// for the RPS start up.

								RpsPolicy4D2D policy = VSphereService.getInstance().checkRPS4Backup(VSphereService.getInstance().getVMBackupConfiguration(vm));
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
						VSphereService.getInstance().getBackupSchedule().triggerJob(new JobKey(jN, jG));
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
	
	private boolean checkRetryPolicy4Next(VirtualMachine vm) {
		// if now is within NearToNextEvent (e.g. 15 minutes by default in
		// RetryPolicy.xml file) against next backup event
		try {
			RetryPolicy policy = VSphereService.getInstance().getRetryPolicy(CommonService.RETRY_BACKUP);
			NextScheduleEvent nextScheduleEvent = VSphereService.getInstance().getNextScheduleEvent(vm);
			if (nextScheduleEvent != null) {
				Date nextDate = nextScheduleEvent.getDate();
				java.util.Calendar incomingBackup = getUTCNow();
				incomingBackup.setTimeInMillis(nextDate.getTime());

				java.util.Calendar now = getUTCNow();
				now.add(java.util.Calendar.MINUTE, policy.getNearToNextEvent());

				if (now.after(incomingBackup)) {
					logger.info("end with too near to next event");
					String time = BackupConverterUtil.dateToString(nextDate);
					VSphereService
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
			logger.error("end", e);
		}
		return true;
	}
	
	private Trigger getMakeupTrigger(StringBuilder retBackupName, Date startCheckTime, Date[] range, boolean isMonthly, boolean isWeekly, boolean isDaily, VirtualMachine vm)
			throws SchedulerException {
		Trigger makeupTrigger = null;

		String[] backupNames = new String[] { BaseService.JOB_NAME_BACKUP_FULL, BaseService.JOB_NAME_BACKUP_RESYNC, BaseService.JOB_NAME_BACKUP_INCREMENTAL };
		String[] triggerGroupNames = new String[] { ScheduleUtils.getFullBackupTriggerGroupName(vm.getVmInstanceUUID()), 
				ScheduleUtils.getResyncBackupTriggerGroupName(vm.getVmInstanceUUID()),
				ScheduleUtils.getIncBackupTriggerGroupName(vm.getVmInstanceUUID()) };
		int[] backupTypes = new int[] { BackupType.Full, BackupType.Resync, BackupType.Incremental };

		java.util.Calendar cal = java.util.Calendar.getInstance();
		Date currentTime = cal.getTime();

		for (int index = 0; index < backupNames.length; index++) {
			String triggerGroupName = triggerGroupNames[index];
			String[] triggerNames = ScheduleUtils.getTriggerNames(VSphereService.getInstance().getBackupSchedule(), triggerGroupName);
			if (triggerNames == null || triggerNames.length <= 0)
				continue;

			Trigger trigger = null;
			Date makeupStartDate = null;
			Date nextFiredTime = null;
			for (String triggerName : triggerNames) {
				Trigger curTrigger = VSphereService.getInstance().getBackupSchedule().getTrigger(new TriggerKey(triggerName, triggerGroupName));
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
					if (!(isBacked(backupTypes[index], makeupStartDate, currentTime, vm))) {
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
	
	private boolean processPeriodSchedule(VirtualMachine vm) {
		boolean isTrigged = false;
		try {

			PeriodSchedule ps = VSphereService.getInstance().getVMBackupConfiguration(vm).getAdvanceSchedule().getPeriodSchedule();

			if (ps.getMonthSchedule() != null && ps.getMonthSchedule().isEnabled()) {
				Date[] range = getMonthlyRange();
				if (!isMonthlyBackuped(PeriodRetentionValue.QJDTO_B_Backup_Monthly, range[0], range[1], vm)) {
					Date startPointOfTime = getStartTime(PeriodRetentionValue.QJDTO_B_Backup_Monthly, vm);
					isTrigged = triggerMakeup(startPointOfTime, range, true, false, false, vm);
				}
			}
			logger.info("Monthly makeup:" + isTrigged);

			if (ps.getWeekSchedule() != null && ps.getWeekSchedule().isEnabled()) {
				Date[] range = getWeeklyRange();
				if (!isTrigged && !isWeeklyBackuped(PeriodRetentionValue.QJDTO_B_Backup_Weekly, range[0], range[1], vm)) {
					Date startPointOfTime = getStartTime(PeriodRetentionValue.QJDTO_B_Backup_Weekly, vm);
					isTrigged = triggerMakeup(startPointOfTime, range, false, true, false, vm);
				}
			}

			logger.info("Weekly makeup:" + isTrigged);
			if (ps.getDaySchedule() != null && ps.getDaySchedule().isEnabled()) {
				Date[] range = getDailyRange();
				Date startPointOfTime = getStartTime(PeriodRetentionValue.QJDTO_B_Backup_Daily, vm);
				java.util.Calendar cal = java.util.Calendar.getInstance();
				Date currentTime = cal.getTime();
				
				Date lastFiredTime = getLastDailyFiredTime(startPointOfTime,currentTime, vm);
				if (!isTrigged && !isDailyBackuped(PeriodRetentionValue.QJDTO_B_Backup_Daily, lastFiredTime, range[1], vm)) {
					isTrigged = triggerMakeup(startPointOfTime, range, false, false, true, vm);
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
	
	private boolean isDailyBackuped(int backupDailyFlag, Date begin, Date end, VirtualMachine vm) {
		return isPeriodBackuped(begin, end, backupDailyFlag, vm);
	}
	
	private Date getLastDailyFiredTime(Date start, Date end, VirtualMachine vm) throws SchedulerException{
		String[] backupNames = new String[] { BaseService.JOB_NAME_BACKUP_FULL, BaseService.JOB_NAME_BACKUP_RESYNC, BaseService.JOB_NAME_BACKUP_INCREMENTAL };
		String[] triggerGroupNames = new String[] { ScheduleUtils.getFullBackupTriggerGroupName(vm.getVmInstanceUUID()), 
				 ScheduleUtils.getResyncBackupTriggerGroupName(vm.getVmInstanceUUID()),
				 ScheduleUtils.getIncBackupTriggerGroupName(vm.getVmInstanceUUID()) };
		Date lastFiredTime = start;
		for (int index = 0; index < backupNames.length; index++){
			String triggerGroupName = triggerGroupNames[index];
			String[] triggerNames = ScheduleUtils.getTriggerNames(VSphereService.getInstance().getBackupSchedule(), triggerGroupName);
			if (triggerNames == null || triggerNames.length <= 0)
				continue;
			Date nextFiredTime = null;
			for (String triggerName : triggerNames) {
				Trigger curTrigger = VSphereService.getInstance().getBackupSchedule().getTrigger(new TriggerKey(triggerName, triggerGroupName));
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
	
	private boolean isWeeklyBackuped(int backupWeeklyFlag, Date begin, Date end, VirtualMachine vm) {
		return isPeriodBackuped(begin, end, backupWeeklyFlag, vm);
	}
	
	private Date getStartTime(int periodRetentionFlag, VirtualMachine vm) throws ServiceException {
		VMBackupConfiguration configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);

		java.util.Calendar cal = java.util.Calendar.getInstance();
		Date currentTime = cal.getTime();
		RecoveryPoint[] recoveryPoints = null;
		try {
			recoveryPoints = RestoreService.getInstance().getRecoveryPoints(configuration.getBackupVM().getDestination(), "", configuration.getBackupVM().getDesUsername(), configuration.getBackupVM().getDesPassword(),  new Date(0), currentTime, false);	
		} catch (Throwable e) {
			logger.error("getStartTime failed", e);
		}

		Date startCheckTime = null;
		long schStartTime = configuration.getAdvanceSchedule().getScheduleStartTime();
		
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
	}
	
	private boolean isMonthlyBackuped(int backupMonthlyFlag, Date begin, Date end, VirtualMachine vm) {
		return isPeriodBackuped(begin, end, backupMonthlyFlag, vm);
	}
	
	private boolean isPeriodBackuped(Date startDate, Date endDate, int advflag, VirtualMachine vm) {
		logger.debug("StartDate:" + startDate + " endDate:" + endDate + "advflag:" + advflag);
		try {
			VMBackupConfiguration configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if (configuration == null) {
				logger.debug("isPeriodBackuped - end with false due to null configuration");
				return false;
			}

			JRestorePoint[] restorePoints = VSphereService.getInstance().getNativeFacade()
					.getRestorePoints(configuration.getBackupVM().getDestination(), "", configuration.getBackupVM().getDesUsername(), configuration.getBackupVM().getDesPassword(), startDate, endDate, false);

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
	
	private boolean checkJobRunning(VirtualMachine vm) {
		// if a running already job
		if (VSphereJobQueue.getInstance().isJobRunning(vm.getVmInstanceUUID(),String.valueOf(Constants.AF_JOBTYPE_VM_BACKUP))) {
			logger.info("end with running job");
			VSphereService
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
	
	private boolean checkRetryPolicyAllowed() {
		RetryPolicy policy = VSphereService.getInstance().getRetryPolicy(CommonService.RETRY_BACKUP);

		if (!policy.isEnabled() || !policy.isMissedEnabled()) {
			logger.info("end with disabled retry policy for missed(s) backup");
			VSphereService
					.getInstance()
					.getNativeFacade()
					.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY,
							new String[] { WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_DISABLED), "", "", "", "" });
			return false;
		}
		
		return true;
	}
	
	private boolean checkAdvScheduleEnabled(VirtualMachine vm) {
		try {
			VMBackupConfiguration configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if (configuration == null) {
				logger.debug("end with null configuration");
				return false;
			}
			AdvanceSchedule advanceSchedule = configuration.getAdvanceSchedule();
			if (advanceSchedule == null) {
				logger.debug("end with disable the advance backup scheduled in configuration");
				return false;
			}

		} catch (ServiceException e1) {
			logger.error("end", e1);
			return false;
		}
		return true;
	}
	
	public void dealWithMissedBackupStandardFormat(VirtualMachine vm) {
		logger.info("begin: vmUuid = " + vm.getVmInstanceUUID());
		boolean fullScheduled = false;
		boolean increScheduled = false;
		boolean resyncScheduled = false;
		RetryPolicy policy = VSphereService.getInstance().getRetryPolicy(CommonService.RETRY_BACKUP);
		VMBackupConfiguration configuration = null;

		try {
			configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
			if (configuration == null) {
				logger.debug("end with null configuration");
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
				logger.debug("end with no backup scheduled in configuration");
				return;
			}
		} catch (ServiceException e1) {
			logger.error("end", e1);
			return;
		}
		NextScheduleEvent nextScheduleEvent;
		java.util.Calendar cal = new FlashServiceImpl().getServerCalendar();
		Date currentTime = cal.getTime();
		NativeFacade nativeFacade = VSphereService.getInstance().getNativeFacade();
		// condition 1, if enabled or not
		if (!(policy.isEnabled() && policy.isMissedEnabled())) {
			logger.info("end with disabled retry policy for missed(s) backup");
			nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY,
					new String[] { WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_DISABLED), "", "", "", "" });
			return;
		}

		// condition 2, if now is within 15 minutes toward to next backup event
		try {
			nextScheduleEvent = VSphereService.getInstance().getNextScheduleEvent(vm);
			if (nextScheduleEvent != null) {
				Date nextDate = nextScheduleEvent.getDate();
				java.util.Calendar serverCalendar = new FlashServiceImpl().getServerCalendar();
				serverCalendar.setTimeInMillis(nextDate.getTime());
				cal.add(java.util.Calendar.MINUTE, policy.getNearToNextEvent());
				if (cal.after(serverCalendar)) {
					logger.info("end with too near to next event");
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
			logger.error("end", e);
		}
		// condition 3, if no running job
		if (VSphereJobQueue.getInstance().isJobRunning(vm.getVmInstanceUUID(),String.valueOf(Constants.AF_JOBTYPE_VM_BACKUP))) {
			logger.info("end with running job");
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
			Trigger fullTrigger = VSphereService.getInstance().getBackupSchedule()
					.getTrigger(new TriggerKey(BaseService.TRIGGER_NAME_BACKUP_FULL, ScheduleUtils.getFullBackupTriggerGroupName(vm.getVmInstanceUUID())));
			Trigger incrementalTrigger = VSphereService.getInstance().getBackupSchedule()
					.getTrigger(new TriggerKey(BaseService.TRIGGER_NAME_BACKUP_INCREMENTAL, ScheduleUtils.getIncBackupTriggerGroupName(vm.getVmInstanceUUID())));
			Trigger resyncTrigger = VSphereService.getInstance().getBackupSchedule()
					.getTrigger(new TriggerKey(BaseService.TIGGER_NAME_BACKUP_RESYNC, ScheduleUtils.getIncBackupTriggerGroupName(vm.getVmInstanceUUID())));

			if (fullScheduled && fullTrigger != null && fullTrigger instanceof DSTSimpleTrigger) {
				if (isJobJustTriggered(fullTrigger, currentTime, policy.getNearToNextEvent()))
				{
					logger.info("job is just triggered, stop makeup for full");
					return;
				}

				Date fireTimeBefore = ((DSTSimpleTrigger) fullTrigger).getFireTimeBefore(new Date(configuration.getBackupStartTime()), currentTime);
				if (fireTimeBefore == null) {
					logger.debug("previous fullTrigger fire time is null");
				} else if (fireTimeBefore.after(currentTime)) {
					logger.debug("previous fullTrigger fire time is after current time");
				} else {
					boolean isbacked = isBacked(BackupType.Full, fireTimeBefore, currentTime, vm);
					if (!isbacked) // we get a missed full backup
					{
						nativeFacade.addLogActivity(
								Constants.AFRES_AFALOG_WARNING,
								Constants.AFRES_AFJWBS_JOB_RETRY,
								new String[] {
										WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SCHEDULED,
												WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED),
												BackupConverterUtil.backupIndicatorToName(BaseService.JOB_NAME_BACKUP_FULL)), "", "", "", "" });
						VSphereService.getInstance().getBackupSchedule().triggerJob(new JobKey(BaseService.JOB_NAME_BACKUP_FULL, ScheduleUtils.getBackupJobGroupName(vm.getVmInstanceUUID())));
						logger.info("end trigger a missed full backup");
						return;
					}
				}
			}

			if (resyncScheduled && resyncTrigger != null && resyncTrigger instanceof DSTSimpleTrigger) {
				if (isJobJustTriggered(resyncTrigger, currentTime, policy.getNearToNextEvent()))
				{
					logger.info("job is just triggered, stop makeup for resync");
					return;
				}
				// we should compute the missed backup from the real starttime,
				// instead of
				// the changed starttime for DST.
				Date fireTimeBefore = ((DSTSimpleTrigger) resyncTrigger).getFireTimeBefore(new Date(configuration.getBackupStartTime()), currentTime);
				if (fireTimeBefore == null) {
					logger.debug("previous resyncTrigger fire time is null");
				} else if (fireTimeBefore.after(currentTime)) {
					logger.debug("previous resyncTrigger fire time is after current time");
				} else {
					boolean isbacked = isBacked(BackupType.Resync, fireTimeBefore, currentTime, vm);
					if (!isbacked) // we get a missed resync backup
					{
						nativeFacade.addLogActivity(
								Constants.AFRES_AFALOG_WARNING,
								Constants.AFRES_AFJWBS_JOB_RETRY,
								new String[] {
										WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SCHEDULED,
												WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED),
												BackupConverterUtil.backupIndicatorToName(BaseService.JOB_NAME_BACKUP_RESYNC)), "", "", "", "" });
						VSphereService.getInstance().getBackupSchedule().triggerJob(new JobKey(BaseService.JOB_NAME_BACKUP_RESYNC, ScheduleUtils.getBackupJobGroupName(vm.getVmInstanceUUID())));
						logger.info("end trigger a missed resync backup");
						return;
					}
				}
			}

			if (increScheduled && incrementalTrigger != null && incrementalTrigger instanceof DSTSimpleTrigger) {
				if (isJobJustTriggered(incrementalTrigger, currentTime, policy.getNearToNextEvent()))
				{
					logger.info("job is just triggered, stop makeup for incremental");
					return;
				}

				Date fireTimeBefore = ((DSTSimpleTrigger) incrementalTrigger).getFireTimeBefore(new Date(configuration.getBackupStartTime()), currentTime);

				if (fireTimeBefore == null) {
					logger.debug("previous incrementalTrigger fire time is null");
				} else if (fireTimeBefore.after(currentTime)) {
					logger.debug("previous  incrementalTrigger fire time is after current time");
				} else {
					boolean isbacked = isBacked(BackupType.Incremental, fireTimeBefore, currentTime, vm);
					if (!isbacked)// we get a missed incrementalTrigger backup
					{
						nativeFacade.addLogActivity(
								Constants.AFRES_AFALOG_WARNING,
								Constants.AFRES_AFJWBS_JOB_RETRY,
								new String[] {
										WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SCHEDULED,
												WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED),
												BackupConverterUtil.backupIndicatorToName(BaseService.JOB_NAME_BACKUP_INCREMENTAL)), "", "", "", "" });
						VSphereService.getInstance().getBackupSchedule().triggerJob(new JobKey(BaseService.JOB_NAME_BACKUP_INCREMENTAL, ScheduleUtils.getBackupJobGroupName(vm.getVmInstanceUUID())));
						logger.info("end trigger a missed incremental backup");
						return;
					}
				}
			}

		} catch (SchedulerException e) {
			logger.error("end", e);
		}

		logger.debug("end");
	}
	
	/**
	 * This method can only decide on the current destination.
	 * 
	 * @param backupType
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private boolean isBacked(int backupType, Date startDate, Date endDate, VirtualMachine vm) {
		logger.info("start - backupType = " + backupType + " StartDate = " + startDate + " endDate = " + endDate + " vmUuid = " + vm.getVmInstanceUUID());

		boolean result = false;
		
		do
		{
			try
			{
				VMBackupConfiguration configuration = VSphereService.getInstance().getVMBackupConfiguration(vm);
				if (configuration == null)
				{
					logger.debug("isBacked - end with false due to null configuration");
					break;
				}

				//	JBackupInfo[] recoveryPoints = BackupService.getInstance().getNativeFacade()
				//			.getRecoveryPoints(configuration.getDestination(), info.getDomain(), info.getUserName(), info.getPwd(), startDate, endDate, -1);
				RecoveryPoint[] recoveryPoints = RestoreService.getInstance().getRecoveryPoints(configuration.getBackupVM().getDestination(), "",
						configuration.getBackupVM().getDesUsername(), configuration.getBackupVM().getDesPassword(), startDate, endDate, false);
				if (recoveryPoints == null)
				{
					logger.debug("isBacked - end with false due to empty backups during period");
					break;
				}

				for (int i = 0; i < recoveryPoints.length; i++)
				{
					RecoveryPoint jRestorePoint = recoveryPoints[i];
					int type = jRestorePoint.getBackupType();
					// if there is full backup, we don't need other backups
					if (type == BackupType.Full)
					{
						result = true;
						break;
					}
					else if (backupType == BackupType.Incremental && type == BackupType.Resync)
					{
						result = true;
						break;
					}
					else if (backupType == type)
					{
						logger.debug("isBacked - end with true during period");
						result = true;
						break;
					}
				}
			}
			catch (Throwable e)
			{
				logger.error(e);
			}
		}while(false);		
		
		logger.info("result = " + result);
		return result;
	}
}

package com.ca.arcflash.webservice.service;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.service.data.PeriodRetentionValue;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.service.jni.model.JMergeData;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.MergeDetailItem;
import com.ca.arcflash.webservice.data.MountSession;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveConfig;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveSourceItem;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.merge.BackupSetInfo;
import com.ca.arcflash.webservice.data.merge.MergeJobMonitor;
import com.ca.arcflash.webservice.data.merge.MergeJobStatus;
import com.ca.arcflash.webservice.data.merge.MergeMethod;
import com.ca.arcflash.webservice.data.merge.MergeOption;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;
import com.ca.arcflash.webservice.scheduler.AbstractMergeJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.DSTDailyTrigger;
import com.ca.arcflash.webservice.scheduler.DSTWeeklyTrigger;
import com.ca.arcflash.webservice.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public abstract class AbstractMergeService extends Observable {
	private static final Logger logger = Logger
			.getLogger(AbstractMergeService.class);

	protected static final String MERGE_JOB_NAME = "D2DMergeJob";
	protected static final String VSPHERE_MERGE_JOB_NAME = "vSphereMergeJob";
	protected static final String MERGE_JOB_GROUP_NAME = "MergeJobGroup";
	protected static final String ASBU_JOB = "ASBU_JOB";
	protected static final long NO_SESSION_MERGE = 2012;

	public static final String MERGE_EMAIL_JOB_NAME = "EmalAlertForManualMerge";

	protected NativeFacade nativeFacade = null;

	protected String mergeStatusFolderPath = null;
	protected int backupSetEndSessionNum = 0;

	protected static class MergeJobContext {
		private MergeMethod method;
		private MergeOption option;
		private int jobId;
		private String vmInstanceUUID;
		private String jobName;
		private Class<? extends AbstractMergeJob> job;
		private MergeEvent event;

		public MergeJobContext(String jobName,
				Class<? extends AbstractMergeJob> job, MergeEvent event) {
			this(jobName, job, event, null);
		}

		public MergeJobContext(String jobName,
				Class<? extends AbstractMergeJob> job, MergeEvent event,
				String vmInstanceUUID) {
			this.jobName = jobName;
			this.job = job;
			this.event = event;
			this.vmInstanceUUID = vmInstanceUUID;
		}

		public MergeMethod getMethod() {
			return method;
		}

		public void setMethod(MergeMethod method) {
			this.method = method;
		}

		public MergeOption getOption() {
			return option;
		}

		public void setOption(MergeOption option) {
			this.option = option;
		}

		public int getJobId() {
			return jobId;
		}

		public void setJobId(int jobId) {
			this.jobId = jobId;
		}

		public String getVmInstanceUUID() {
			return vmInstanceUUID;
		}

		public void setVmInstanceUUID(String vmInstanceUUID) {
			this.vmInstanceUUID = vmInstanceUUID;
		}

		public String getJobName() {
			return jobName;
		}

		public void setJobName(String jobName) {
			this.jobName = jobName;
		}

		public Class<? extends AbstractMergeJob> getJob() {
			return job;
		}

		public void setJob(Class<? extends AbstractMergeJob> job) {
			this.job = job;
		}

		public MergeEvent getEvent() {
			return event;
		}

		public void setEvent(MergeEvent event) {
			this.event = event;
		}
	}

	protected AbstractMergeService() {
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();

			nativeFacade = new NativeFacadeImpl();

			mergeStatusFolderPath = ServiceContext.getInstance()
					.getDataFolderPath() + "\\MergeJobStatus";
			File file = new File(mergeStatusFolderPath);
			if (!file.exists()) {
				file.mkdir();
			}

		} catch (SchedulerException se) {
			logger.error("Failed to get quartz scheduler", se);
		}
	}

	protected Scheduler scheduler = null;

	public NativeFacade getNativeFacade() {
		if (nativeFacade == null)
			nativeFacade = new NativeFacadeImpl();
		return nativeFacade;

	}

	protected boolean isOtherJobRunning(
			Map<String, Map<Long, JobMonitor>> jobMonitorMap,
			Set<Object> otherJobs) {
		if (jobMonitorMap != null && !jobMonitorMap.isEmpty()
				|| otherJobs != null && !otherJobs.isEmpty()) {
			logger.error("Another job is running, cannot run merge job now, "
					+ "current running job are: " + otherJobs);
			return true;
		} else {
			return false;
		}
	}

	protected int checkForResume(MergeEvent event, MergeStatus currentStatus,
			Map<String, Map<Long, JobMonitor>> jobMonitorMap,
			Set<Object> otherJobs, AbstractMergeJob currentJob,
			String vmInstanceUUID) throws ServiceException {
		if (currentJob != null) {
			logger.warn("Merge is running no need to start");
			return -1;
		}

		if (jobMonitorMap != null && !jobMonitorMap.isEmpty()
				|| otherJobs != null && !otherJobs.isEmpty()) {
			logger.error("Another job is running, cannot run merge job now, "
					+ "current running job are: " + otherJobs);
			throw new ServiceException(
					WebServiceMessages.getResource("submitMergeFailOtherJob"),
					FlashServiceErrorCode.Common_General_Message);
		}

		if (event != MergeEvent.MANUAL_RESUME
				&& !this.isInMergeTimeRange(vmInstanceUUID)) {
			if (currentStatus.getStatus() == MergeStatus.Status.PAUSED_MANUALLY) {
				currentStatus.setCanResume(true);
			}
			currentStatus.setInSchedule(false);
			updateMergeJobStatus(currentStatus);
			// log activity
			RetentionPolicy policy = this.getRetentionPolicy(vmInstanceUUID);
			String time = getScheduleStartTime(vmInstanceUUID);
			String message = WebServiceMessages.getResource(
					"mergeJobNotInSchedule", time);

			logWithID(Constants.AFRES_AFALOG_WARNING, -1, message,
					vmInstanceUUID);
			throw new ServiceException(message,
					FlashServiceErrorCode.Common_General_Message);
		} else {
			currentStatus.setInSchedule(true);
			updateMergeJobStatus(currentStatus);
		}

		return 0;
	}

	protected String getScheduleStartTime(String vmInstanceUUID) {
		/* in new schedule, there are multiple triggers for the merge job. */
		try {
			List<? extends Trigger> triggers = scheduler
					.getTriggersOfJob(new JobKey(
							getMergeJobName(vmInstanceUUID),
							MERGE_JOB_GROUP_NAME));
			if (triggers == null || triggers.size() == 0) {
				return null;
			}
			Date nextTime = triggers.get(0).getNextFireTime();
			for (Trigger trigger : triggers) {
				Date temp = trigger.getNextFireTime();
				if (temp.before(nextTime)) {
					nextTime = temp;
				}
			}
			SimpleDateFormat timeFormat = new SimpleDateFormat(
					CommonNativeInstance.getICommonNative().getDateTimeFormat()
							.getTimeDateFormat(),
					DataFormatUtil.getDateFormatLocale());
			return timeFormat.format(nextTime);
		} catch (SchedulerException se) {
			logger.error("Failed to get merge schedule start time ", se);
			return null;
		}
	}

	public static final String MANUAL_MERGE_STRING = "showManualMerge";

	/**
	 * The API is only to show the warning on SummaryPanel. If there are
	 * sessions need to merge, and the status is not paused_manually, we will
	 * return the above message to let UI show the StartManualMerge panel, else
	 * return null.
	 * 
	 * @param status
	 * @param policy
	 * @return
	 */
	protected String getMergeScheduleTime(String vmInstanceUUID,
			MergeStatus status, RetentionPolicy policy) {
		if (policy == null || policy.isUseBackupSet()/*
													 * ||
													 * !policy.isUseTimeRange()
													 */)
			return null;
		if (status != null
				&& status.getStatus() == MergeStatus.Status.PAUSED_MANUALLY) {
			return null;
		}
		if (this.isMergeJobAvailable(vmInstanceUUID)) {
			// return this string is to minimize code change for time range
			// check.
			if (!this.isInMergeTimeRange(vmInstanceUUID)) {
				if (isPlanDisabled(vmInstanceUUID)) {
					return MANUAL_MERGE_STRING;
				}
				return getScheduleStartTime(vmInstanceUUID);
			}
			return MANUAL_MERGE_STRING;
		} else {
			return null;
		}
		// The method is changed to always allow user to trigger a manual merge
		// for issue 140511
		/*
		 * if(!this.isInMergeTimeRange(vmInstanceUUID) &&
		 * this.isMergeJobAvailable(vmInstanceUUID)) { return
		 * getScheduleStartTime(vmInstanceUUID); // return
		 * getScheduleStartTime(policy); } return null;
		 */
	}

	protected JobDetailImpl getJobDetail(MergeJobContext context) {
		JobDetailImpl jd = new JobDetailImpl(context.getJobName(),
				MERGE_JOB_GROUP_NAME, context.getJob());
		JobDataMap dataMap = jd.getJobDataMap();
		MergeMethod method = context.getMethod() != null ? context.getMethod()
				: MergeMethod.EMM_INC_2_FUL;
		MergeOption option = context.getOption() != null ? context.getOption()
				: MergeOption.EMO_START;
		dataMap.put("mergetMethod", method);
		dataMap.put("mergetOption", option);
		dataMap.put("jobId", context.getJobId());
		dataMap.put("vmInstanceUUID", context.getVmInstanceUUID());
		dataMap.put("mergeEvent", context.getEvent());
		return jd;
	}

	protected boolean scheduleMergeJob(MergeJobContext context) {
		JobDetailImpl jd = getJobDetail(context);

		RetentionPolicy policy = this.getRetentionPolicy(context
				.getVmInstanceUUID());
		if (policy == null || policy.isUseBackupSet()
				|| !policy.isUseTimeRange()) {
			logger.warn("No retention policy or use backup set or not user time range, "
					+ "no need to schedule merge job");
			return false;
		}

		// Trigger trigger =
		// TriggerUtils.makeDailyTrigger(context.getJob().toString(),
		// policy.getStartHour(), policy.getStartMinutes());

		// since merge job has the lowest priority, and it will be started after
		// any job complete,
		// so set it as low priority.
		CronTriggerImpl trigger = new DSTDailyTrigger(policy.getStartHour(),
				policy.getStartMinutes(), policy.getEndHour(),
				policy.getEndMinutes());
		trigger.setPriority(1);

		try {
			trigger.setName(jd.getName() + "trigger");
			trigger.setGroup(MERGE_JOB_GROUP_NAME);
			scheduler.scheduleJob(jd, trigger);
			return true;
		} catch (SchedulerException se) {
			logger.error("Failed to schedule merge job for quartz error"
					+ se.getMessage());
		} catch (Exception e) {
			logger.error("Failed to schedule merge job" + e.getMessage());
		}
		return false;
	}

	protected boolean newScheduleMergeJob(MergeJobContext context) {
		List<DailyScheduleDetailItem> mergeSchedules = this
				.getMergeSchedule(context.getVmInstanceUUID());
		if (mergeSchedules == null || mergeSchedules.size() == 0) {
			logger.warn("No merge time range, "
					+ "no need to schedule merge job");
			return false;
		}

		JobDetailImpl jd = getJobDetail(context);
		jd.setDurability(true);

		try {
			scheduler.addJob(jd, false);
			int triggerNumber = 1;
			for (DailyScheduleDetailItem mergeSchedule : mergeSchedules) {
				ArrayList<MergeDetailItem> mergeDetailItems = mergeSchedule
						.getMergeDetailItems();
				if (mergeDetailItems != null) {
					for (MergeDetailItem mergeDetailItem : mergeDetailItems) {
						CronTriggerImpl trigger = new DSTWeeklyTrigger(
								mergeDetailItem.getStartTime().getHour(),
								mergeDetailItem.getStartTime().getMinute(),
								mergeDetailItem.getEndTime().getHour(),
								mergeDetailItem.getEndTime().getMinute(),
								mergeSchedule.getDayofWeek());
						trigger.setName(jd.getName() + "trigger"
								+ triggerNumber);
						triggerNumber++;
						trigger.setGroup(MERGE_JOB_GROUP_NAME);
						trigger.setJobName(jd.getName());
						trigger.setJobGroup(MERGE_JOB_GROUP_NAME);
						scheduler.scheduleJob(trigger);
					}
				}
			}

			return true;
		} catch (SchedulerException se) {
			logger.error("Failed to schedule merge job for quartz error"
					+ se.getMessage());
		} catch (Exception e) {
			logger.error("Failed to schedule merge job" + e.getMessage());
		}
		return false;
	}

	public void startNewJobAfterDone(String vmInstanceUUID) {
		try {
			String onDemandName = this.getMergeJobName(vmInstanceUUID)
					+ "OnDemandJob";
			while (scheduler.getJobDetail(new JobKey(onDemandName,
					MERGE_JOB_GROUP_NAME)) != null) {
				Thread.sleep(10);
			}
		} catch (Exception e) {
			logger.error("Failed to check exist job");
		}
	}

	protected boolean startMergeJob(MergeJobContext context)
			throws ServiceException {
		if (!isInMergeTimeRange("")
				&& context.getEvent() != MergeEvent.MANUAL_RESUME)
			return false;

		String jobName = context.getJobName() + "OnDemandJob";
		context.setJobName(jobName);
		JobDetailImpl jd = getJobDetail(context);

		SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
		// since merge job has the lowest priority, and it will be started after
		// any job complete,
		// so set it as low priority.
		trigger.setPriority(1);

		try {
			trigger.setName(jd.getName() + "trigger");
			scheduler.scheduleJob(jd, trigger);
			return true;
		} catch (SchedulerException se) {
			logger.error("Failed to schedule merge job for quartz error: " + se);
		} catch (Exception e) {
			logger.error("Failed to schedule merge job: " + e);
		}
		return false;
	}

	protected boolean isMergeJobAvailable(String vmInstanceUUID,
			MergeStatus status, String dest, String destUserName,
			String destPassword, RetentionPolicy retentionPolicy,
			int retentionCount) {
		if (retentionPolicy != null) {
			boolean isAvailable = false;
			if (!retentionPolicy.isUseBackupSet()) {
				isAvailable = getNativeFacade().isMergeJobAvailable(
						retentionCount, dest, vmInstanceUUID, destUserName,
						destPassword) == 0;
			} else {
				int count = retentionPolicy.getBackupSetCount();
				this.backupSetEndSessionNum = getBackupSetEndSessionNum(dest,
						destUserName, destPassword, count);
				isAvailable = this.backupSetEndSessionNum > 0;
			}

			updateMergeStatus(isAvailable, status);
			return isAvailable;
		} else {
			return false;
		}
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

	protected boolean isMergeJobAvailableEx(String vmInstanceUUID, MergeStatus status, String dest,
			String destUserName, String destPassword, RetentionPolicy retentionPolicy, int retentionCount, int dailyCount, int weeklyCount, int monthlyCount) {
		if(retentionPolicy != null){
			boolean isAvailable = false;
			if(!retentionPolicy.isUseBackupSet()) {
				//isAvailable = getNativeFacade().isMergeJobAvailableEx(dest,retentionCount,dailyCount,weeklyCount,monthlyCount,vmInstanceUUID, destUserName, destPassword) == 0;
				long[] archiveInfo = getArchivToTapeForMerge();
				
				JMergeData mergeInfo = new JMergeData();
				mergeInfo.setArchiveConfigTime(archiveInfo[0]);
				mergeInfo.setArchiveSourceSelection(archiveInfo[1]);
				mergeInfo.setBackupDest(dest);				
				mergeInfo.setBackupUser(destUserName);
				mergeInfo.setBackupPassword(destPassword);
				mergeInfo.setCustomRetentionCnt(retentionCount);
				mergeInfo.setDailyCnt(dailyCount);
				mergeInfo.setWeeklyCnt(weeklyCount);
				mergeInfo.setMonthlyCnt(monthlyCount);
				mergeInfo.setVmGUID(vmInstanceUUID);
				isAvailable = getNativeFacade().isMergeJobAvailable(mergeInfo) == 0;
			}
			else {
				int count = retentionPolicy.getBackupSetCount();
				this.backupSetEndSessionNum = getBackupSetEndSessionNum(dest, destUserName, destPassword, count);
				isAvailable = this.backupSetEndSessionNum > 0;
			}
			
			updateMergeStatus(isAvailable, status);
			return isAvailable;
		}else {
			return false;
		}
	}

	private synchronized void updateMergeStatus(boolean mergeAvaiable,
			MergeStatus status) {
		if (!mergeAvaiable
				&& status.getStatus() != MergeStatus.Status.NOTRUNNING
				&& status.getStatus() != MergeStatus.Status.RUNNING) {
			// the merge job maybe paused manually, but the merge job is really
			// finished.
			// so we may get a wrong merge status as PAUSED_MANUALLY which in
			// fact should be NOTRUNNING.
			logger.info("Current merge job status is: " + status.getStatus()
					+ ". Reset merge job monitor.");
			status.setCanResume(false);
			status.setJobMonitor(null);
			status.setStatus(MergeStatus.Status.NOTRUNNING);
			this.updateMergeJobStatus(status);
		}
	}

	protected MountSession[] getMountedSessionsToPurge(String vmInstanceUUID,
			String dest, String destUserName, String destPassword,
			RetentionPolicy retentionPolicy, int retentionCount) {
		List<MountSession> toPurgeMountedSession = new ArrayList<MountSession>();
		if (retentionPolicy == null)
			return new MountSession[0];
		Lock lock = null;
		try {
			// get lock and create connection
			lock = RemoteFolderConnCache.getInstance().getLockByPath(dest);
			if (lock != null) {
				lock.lock();
			}
			logger.debug("Lock to destination " + dest);
			long ret = -1;
			for (int i = 0; i < 5; i++) {
				try {
					if (destUserName == null)
						destUserName = "";
					if (destPassword == null)
						destPassword = "";
					ret = this.nativeFacade.NetConn(destUserName, destPassword,
							dest);
					break;
				} catch (ServiceException se) {
					logger.debug("Net connetion error " + se.getMessage());
				}
			}
			if (ret != 0) {
				logger.error("Failed to connect to backup destination.");
				return new MountSession[0];
			}
			// get mounted sessions
			MountSession[] mountedSessions = CommonService.getInstance()
					.getMountedSessions(dest);
			if (mountedSessions == null || mountedSessions.length == 0)
				return toPurgeMountedSession.toArray(new MountSession[0]);
			// get to merge sessions
			if (!retentionPolicy.isUseBackupSet()) {
				List<Integer> toPurgeSessions = getNativeFacade()
						.GetSessNumListForNextMerge(retentionCount, dest);
				if (toPurgeSessions.isEmpty())
					return toPurgeMountedSession.toArray(new MountSession[0]);

				try {
					Map<Long, Long> mountedSessionFull = new HashMap<Long, Long>();
					for (MountSession session : mountedSessions) {
						mountedSessionFull.put(
								new Long(session.getSessionNum()),
								new Long(nativeFacade.getFullSessNumber4Incre(
										session.getSessionNum(), dest)));

					}
					for (Integer sid : toPurgeSessions) {
						long fullSid = nativeFacade.getFullSessNumber4Incre(
								sid.longValue(), dest);
						for (MountSession session : mountedSessions) {
							long mfullSid = mountedSessionFull.get(new Long(
									session.getSessionNum()));
							if (mfullSid == fullSid
									&& !toPurgeMountedSession.contains(session)) {
								toPurgeMountedSession.add(session);
							}
						}
					}
				} catch (Exception e) {
					logger.error("Failed to get to purge mounted sessions");
				}

			} else {
				int count = retentionPolicy.getBackupSetCount();
				int backupSetEndSessionNum = getBackupSetEndSessionNum(dest,
						destUserName, destPassword, count);
				for (MountSession session : mountedSessions) {
					if (session.getSessionNum() < backupSetEndSessionNum) {
						toPurgeMountedSession.add(session);
					}
				}
			}
		} finally {
			if (lock != null) {
				lock.unlock();
				logger.debug("unlock destination " + dest);
			}
			try {
				nativeFacade.disconnectRemotePath(dest, "", destUserName,
						destPassword, false);
			} catch (Exception e) {
				logger.debug("Failed to cut connection to " + dest);
			}
		}

		return toPurgeMountedSession.toArray(new MountSession[0]);
	}

	private int getBackupSetEndSessionNum(String destination, String userName,
			String password, int backupSetCount) {
		try {
			List<BackupSetInfo> info = BaseBackupSetService
					.getAllBackupSetInfo(destination, "", userName, password);
			if (info.size() <= (backupSetCount + 1))
				return 0;
			else {
				BackupSetInfo set = info.get(backupSetCount);
				logger.info("Found the end session number to purge backup set: "
						+ set.getStartRecoveryPoint().getSessionID());
				return (int) set.getStartRecoveryPoint().getSessionID();
			}
		} catch (Exception e) {
			logger.error("Failed to get backup set information"
					+ e.getMessage());
		}

		return 0;
	}

	public void clearMergeStatus(String path) {
		File file = new File(path);
		file.delete();
	}

	public int getMergeBackupSetEndSessionNum() {
		return this.backupSetEndSessionNum;
	}

	/**
	 * we only save the merge status for user manually paused or the paused
	 * times larger than 0.
	 * 
	 * @param status
	 * @param path
	 */
	protected synchronized void saveMergeStatus(MergeStatus status, String path) {
		logger.debug("Enter save merge status");
		if (status == null)
			return;
		if (status.getStatus() == MergeStatus.Status.PAUSED_MANUALLY
				|| status.getStatus() == MergeStatus.Status.PAUSING
				&& status.isManualPause() || status.getStopTimes() > 0) {
			FileOutputStream fos = null;
			try {
				File file = new File(path);
				if (!file.exists()) {
					if (!file.createNewFile()) {
						logger.warn("Failed to create file for save merge status to "
								+ path);
						return;
					}
				}
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(path));
				oos.writeObject(status);
				oos.close();
			} catch (IOException ioe) {
				logger.error("Failed to save merge status " + ioe.getMessage());
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException ioe) {
					}
				}
			}
		}

		logger.debug("Exit save merge status");
	}

	/**
	 * We only get the status for manually paused, for other case, we think
	 * there is no merge running, only get the stopped times.
	 * 
	 * @param path
	 * @return
	 */
	protected MergeStatus loadMergeStatus(String path) {
		logger.debug("Enter load merge status");
		MergeStatus status = null;
		FileInputStream fis = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				return status;
			}
			fis = new FileInputStream(file);
			ObjectInputStream oos = new ObjectInputStream(fis);
			Object obj = oos.readObject();
			if (obj != null)
				status = (MergeStatus) obj;
			oos.close();
		} catch (IOException ioe) {
			logger.error("Failed to save merge status " + ioe.getMessage());
		} catch (ClassNotFoundException e) {
			logger.error("Failed to save merge status " + e.getMessage());
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ioe) {
				}
			}
		}

		if (status != null) {
			if (status.getStatus() == MergeStatus.Status.PAUSED_MANUALLY
					|| status.getStatus() == MergeStatus.Status.PAUSING
					&& status.isManualPause()) {
				return status;
			} else {
				status.setStatus(MergeStatus.Status.NOTRUNNING);
				status.setCanResume(false);
				return status;
			}
		}
		logger.debug("Exit load merge status");
		return status;
	}

	public boolean isInMergeTimeRange(String vmInstanceUUID) {
		BackupConfiguration backupConf = null;
		try {
			backupConf = BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e1) {
			// TODO Auto-generated catch block
			logger.error("Failed to get backup configuration");
		}

		// determine by old format or new format
		if (backupConf != null && backupConf.getBackupDataFormat() == 1) {
			Calendar currentTime = Calendar.getInstance();
			int dayofWeek = currentTime.get(Calendar.DAY_OF_WEEK);

			List<DailyScheduleDetailItem> mergeSchedules = this
					.getMergeSchedule(vmInstanceUUID);
			// If no schedule, allow merge at any time
			if (mergeSchedules == null || mergeSchedules.size() == 0) {
				return true;
			} else {
				boolean hasSchedule = false;
				for (DailyScheduleDetailItem mergeSchedule : mergeSchedules) {
					ArrayList<MergeDetailItem> mergeDetailItems = mergeSchedule
							.getMergeDetailItems();
					if (mergeDetailItems != null
							&& mergeDetailItems.size() != 0)
						hasSchedule = true;
				}
				if (!hasSchedule)
					return true;
			}

			boolean isInTimeRange = false;
			loop: for (DailyScheduleDetailItem mergeSchedule : mergeSchedules) {
				if (mergeSchedule.getDayofWeek() == dayofWeek) {
					ArrayList<MergeDetailItem> mergeDetailItems = mergeSchedule
							.getMergeDetailItems();
					if (mergeDetailItems != null) {
						for (MergeDetailItem mergeDetailItem : mergeDetailItems) {
							isInTimeRange = isInDSTTimeRange(mergeDetailItem
									.getStartTime().getHour(), mergeDetailItem
									.getStartTime().getMinute(),
									mergeDetailItem.getEndTime().getHour(),
									mergeDetailItem.getEndTime().getMinute());

							if (isInTimeRange)
								break loop;
						}
					}
				}
			}
			return isInTimeRange;
		} else {
			RetentionPolicy policy = getRetentionPolicy(vmInstanceUUID);
			if (policy == null)
				return false;
			if (policy.isUseBackupSet() || !policy.isUseTimeRange())
				return true;
			else {
				return isInDSTTimeRange(policy.getStartHour(),
						policy.getStartMinutes(), policy.getEndHour(),
						policy.getEndMinutes());
			}
		}
	}

	protected boolean isInDSTTimeRange(int startHour, int startMinutes,
			int endHour, int endMinutes) {
		Calendar currentTime = Calendar.getInstance();
		int hour = currentTime.get(Calendar.HOUR_OF_DAY);
		int minutes = currentTime.get(Calendar.MINUTE);

		Calendar startTime = Calendar.getInstance();
		startTime.set(HOUR_OF_DAY, startHour);
		startTime.set(MINUTE, startMinutes);
		startTime.set(SECOND, 0);
		startTime.set(MILLISECOND, 0);
		Calendar endTime = Calendar.getInstance();
		endTime.set(HOUR_OF_DAY, endHour);
		endTime.set(MINUTE, endMinutes);
		endTime.set(SECOND, 0);
		endTime.set(MILLISECOND, 0);
		if (!ServiceUtils.isTimeBefore(startHour, startMinutes, endHour,
				endMinutes)) {
			endTime.add(DAY_OF_MONTH, 1);

		}

		if (CommonService.getInstance().isTimeInDSTEndInterval(
				currentTime.get(YEAR), currentTime.get(MONTH),
				currentTime.get(DAY_OF_MONTH), startHour, startMinutes)
				|| ServiceUtils.isTimeBefore(startHour, startMinutes, endHour,
						endMinutes)
				&& CommonService.getInstance().isTimeInDSTEndInterval(
						currentTime.get(YEAR), currentTime.get(MONTH),
						currentTime.get(DAY_OF_MONTH), endHour, endMinutes)) {
			// start/end time in DST end time range, like 1:00-2:00am in UTC-5,
			// then compare the milliseconds
			// we need to compare the real milliseconds of the time, since the
			// clock will revert back to 1:00am after first reach 1:59am.
			if (currentTime.getTimeInMillis() >= startTime.getTimeInMillis()
					&& currentTime.getTimeInMillis() < endTime
							.getTimeInMillis()) {
				return true;
			} else {
				return false;
			}
		} else if (!ServiceUtils.isTimeBefore(startHour, startMinutes, endHour,
				endMinutes)
				&& CommonService.getInstance().isTimeInDSTEndInterval(
						currentTime.get(YEAR), currentTime.get(MONTH),
						currentTime.get(DAY_OF_MONTH), endHour, endMinutes)) {
			if (currentTime.getTimeInMillis() >= startTime.getTimeInMillis()
					|| currentTime.getTimeInMillis() < endTime
							.getTimeInMillis()) {
				return true;
			} else {
				return false;
			}
		} else {
			// end time is larger than start time, then check whether current
			// time
			// is in this time range (larger than start time and smaller than
			// end time)
			if (ServiceUtils.isTimeBefore(startHour, startMinutes, endHour,
					endMinutes)) {
				if (ServiceUtils.isTimeBeforeOrEqual(startHour, startMinutes,
						hour, minutes)
						&& ServiceUtils.isTimeBefore(hour, minutes, endHour,
								endMinutes)) {
					return true;
				}
			} else {
				// we check whether current time is after start time before
				// 23:59,
				// or before end time and after 0:00.
				if (ServiceUtils.isTimeBeforeOrEqual(startHour, startMinutes,
						hour, minutes)
						|| ServiceUtils.isTimeBefore(hour, minutes, endHour,
								endMinutes)) {
					return true;
				}
			}
			return false;
		}
	}

	protected void mergeDone(AbstractMergeJob job, MergeStatus mergeStatus) {
		MergeStatus.Status status = MergeStatus.Status.NOTRUNNING;
		MergeStatus.Status currentStatus = mergeStatus.getStatus();
		MergeJobMonitor jobMonitor = mergeStatus.getJobMonitor();

		if (job.getPauseContext() != null) {
			if (jobMonitor != null
					&& jobMonitor.getJobStatus() == MergeJobStatus.EJS_JOB_FINISH
							.getValue()) {
				// if job has complete successfully
				status = MergeStatus.Status.NOTRUNNING;
				// clear the stopped times for finished job
				mergeStatus.setStopTimes(0);
			} else if (jobMonitor != null
					&& jobMonitor.getJobStatus() == MergeJobStatus.EJS_JOB_STOPPED
							.getValue()) {
				// merge is paused
				status = action(job.getPauseContext().event,
						job.getPauseContext().action, currentStatus);
				if (status == MergeStatus.Status.PAUSED_MANUALLY) {
					mergeStatus.setCanResume(true);
					mergeStatus.setJobMonitor(null);
					mergeStatus.setStatus(MergeStatus.Status.PAUSED_MANUALLY);
					mergeStatus.setFinished(false);
					this.updateMergeJobStatus(mergeStatus);
					return;
				} else {
					// log the merge paused times by other job.
					int stopTimes = mergeStatus.getStopTimes() + 1;
					mergeStatus.setStopTimes(stopTimes);
					logStoppedActivity(jobMonitor.getDwJobID(),
							job.getVMInstanceUUID(), stopTimes);
				}
			} else {
				status = MergeStatus.Status.NOTRUNNING;
			}
		} else if (jobMonitor != null
				&& jobMonitor.getJobStatus() != MergeJobStatus.EJS_JOB_STOPPED
						.getValue()) {
			if (jobMonitor.getJobStatus() == MergeJobStatus.EJS_JOB_FINISH
					.getValue()) {
				// clear the stopped times for finished job
				mergeStatus.setStopTimes(0);
			}
			status = MergeStatus.Status.NOTRUNNING;
		}

		mergeStatus.setCanResume(false);
		mergeStatus.setJobMonitor(null);
		mergeStatus.setStatus(status);
		this.updateMergeJobStatus(mergeStatus);
	}

	private MergeStatus.Status startRun() {
		return setMergeStatus(MergeEvent.MANUAL_RESUME);
	}

	// Change to event interface
	// /////////////////////////////////////////////////////////////////////////
	protected MergeStatus.Status action(MergeEvent event, MergeAction action,
			MergeStatus.Status currentStatus) {
		MergeStatus.Status status = MergeStatus.Status.NOTRUNNING;
		switch (action) {
		case STOP:
			status = stopRun(event, currentStatus);
			break;
		case RESUME:
			status = startRun();
			break;
		}
		return status;
	}

	private void logPauseActivity(MergeEvent event, String source, long jobId,
			String vmInstanceUUID) {
		String msg = null;
		switch (event) {
		case MANUAL_STOP:
			msg = WebServiceMessages.getResource("pauseMergeManually");
			break;
		case WS_STOP:
			msg = WebServiceMessages.getResource("pauseMergeWSCall");
			break;
		case OTHER_JOB_START:
			if (source != null)
				msg = WebServiceMessages.getResource("pauseMergeOtherJobRun",
						source);
			break;
		}

		if (msg != null)
			this.logWithID(Constants.AFRES_AFALOG_INFO, jobId, msg,
					vmInstanceUUID);
	}

	private void logNotPauseActivity(MergeEvent event, String source,
			long jobId, String vmInstanceUUID) {
		String msg = null;
		switch (event) {
		case MANUAL_STOP:
		case WS_STOP:
			msg = WebServiceMessages.getResource("mergeCannotPauseManually");
			break;
		case OTHER_JOB_START:
			if (source != null)
				msg = WebServiceMessages.getResource(
						"mergeCannotPauseOtherJob", source);
			break;
		}

		if (msg != null)
			this.logWithID(Constants.AFRES_AFALOG_INFO, jobId, msg,
					vmInstanceUUID);
	}

	protected void logResumeActivity(MergeEvent event, long jobId,
			String vmInstanceUUID) {
		String msg = WebServiceMessages.getResource("startMergeJob");
		if (event != null) {
			switch (event) {
			case MANUAL_RESUME:
				msg = WebServiceMessages.getResource("resumeMergeManully");
				break;
			case WS_RESUME:
			case OTHER_JOB_END:
				msg = WebServiceMessages.getResource("resumeMergeOtherJob");
				break;
			default:
				msg = WebServiceMessages.getResource("startMergeJob");
			}
		}
		if (msg != null)
			this.logWithID(Constants.AFRES_AFALOG_INFO, jobId, msg,
					vmInstanceUUID);
	}

	protected void logStoppedActivity(long jobId, String vmInstanceUUID,
			int stoppedTimes) {
		String msg = WebServiceMessages.getResource("mergeStoppedLog",
				new Object[] { stoppedTimes });
		logWithID(Constants.AFRES_AFALOG_WARNING, jobId, msg, vmInstanceUUID);
	}

	private int stopMerge(MergeStatus mergeStatus, AbstractMergeJob currentJob,
			MergeEvent event, String eventSource, long jobId)
			throws ServiceException {
		try {
			MergeJobMonitor currentJobMonitor = mergeStatus.getJobMonitor();
			logPauseActivity(event, eventSource, jobId,
					currentJob.getVMInstanceUUID());

			int ret = (int) this.getNativeFacade().stopMerge(jobId);
			// after this call, backend side should set merge status to stopped
			if (ret != 0) {
				logger.error("Failed to stop merge with return " + ret);
				return ret;
			}

			currentJob.pauseMerge(new MergeActionContext(event,
					MergeAction.STOP));
			mergeStatus.setStatus(MergeStatus.Status.PAUSING);
			mergeStatus.setJobStatus(JobStatus.JOBSTATUS_CANCELLED);
			if (event == MergeEvent.MANUAL_STOP) {
				mergeStatus.setManualPause(true);
			} else {
				mergeStatus.setManualPause(false);
			}
			mergeStatus.setJobMonitor(currentJobMonitor);
			updateMergeJobStatus(mergeStatus);
			logger.info("Merge job paused, id is " + jobId);
		} catch (ServiceException e) {
			logger.error("Pause Merge job failed", e);
			throw e;
		} catch (Throwable t) {
			logger.error("Pause Merge job failed", t);
			// TODO
			throw new ServiceException("", "Failed to pause merge job");
		}
		return 0;
	}

	protected int pauseMerge(MergeStatus mergeStatus,
			AbstractMergeJob currentJob, MergeEvent event, String eventSource)
			throws ServiceException {
		MergeStatus.Status currentStatus = mergeStatus.getStatus();
		MergeJobMonitor currentJobMonitor = mergeStatus.getJobMonitor();
		logger.debug("Current status is " + currentStatus);

		if (event != MergeEvent.MANUAL_STOP)
			mergeStatus.setCanResume(false);

		if (currentStatus != MergeStatus.Status.RUNNING) {
			if (currentStatus == MergeStatus.Status.PAUSING)
				return 0;
			if (currentJob != null) {
				if (currentJob.stopRun() != 0) {
					return stopMerge(mergeStatus, currentJob, event,
							eventSource, currentJob.getJobId());
				}
			}
			logger.info("No merge job running now, no need to pause");
			currentStatus = action(event, MergeAction.STOP, currentStatus);
		} else {
			if (currentJob.isMergeJobEnd()) {
				logger.info("Merge job has completed, no need to pause");
				return 0;
			}
			long jobId = currentJobMonitor.getDwJobID();
			if (!currentJobMonitor.isVHDMerge()
					&& currentJobMonitor.getUllTotalBytes2Merge() > 0
					&& currentJobMonitor.getUllTotalBytesMerged() == currentJobMonitor
							.getUllTotalBytes2Merge()) {
				logNotPauseActivity(event, eventSource, jobId,
						currentJob.getVMInstanceUUID());
				logger.info("Merge has completed data merge, cannot pause it");
				return 0;
			} else {
				return this.stopMerge(mergeStatus, currentJob, event,
						eventSource, jobId);
			}
		}

		return 0;
	}

	private MergeStatus.Status stopRun(MergeEvent event,
			MergeStatus.Status currentStatus) {
		if (currentStatus == MergeStatus.Status.PAUSED_MANUALLY)
			return currentStatus;

		switch (event) {
		case MANUAL_STOP:
			return MergeStatus.Status.PAUSED_MANUALLY;
		case WS_STOP:
		case OTHER_JOB_START:
			return MergeStatus.Status.PAUSED;
		case NO_SCHEDULE:
			return MergeStatus.Status.PAUSED_NO_SCHEDULE;
		default:
			return currentStatus;
		}
	}

	/**
	 * We assume when there is other job running, the resume button on UI is
	 * disabled so don't need to consider if status is paused, and the event is
	 * {@code MANUAL_RESUME}
	 * 
	 * @param event
	 * @param currentStatus
	 * @return
	 */
	protected int canResumeMerge(MergeEvent event, MergeStatus currentStatus)
			throws ServiceException {
		if (currentStatus.getStatus() == MergeStatus.Status.NOTRUNNING)
			return 0;
		else if (currentStatus.getStatus() == MergeStatus.Status.PAUSED_MANUALLY) {
			if (event == MergeEvent.MANUAL_RESUME)
				return 0;
			else {
				currentStatus.setCanResume(true);
				throw new ServiceException(
						WebServiceMessages
								.getResource("resumeMergeStatusWrong"),
						FlashServiceErrorCode.Common_General_Message);
			}
		} else if (currentStatus.getStatus() == MergeStatus.Status.RUNNING
				|| currentStatus.getStatus() == MergeStatus.Status.TORUNN) {
			logger.info("Current job status is " + currentStatus.getStatus()
					+ " no need to start job");
			return -1;
		} else {
			logger.info("Current job status is " + currentStatus.getStatus()
					+ "; event is " + event + " can start merge");
			return 0;
		}

	}

	/**
	 * The merge may the running, then the webservice stopped and merge finished
	 * before webservice restart. Or there may other cases cause the merge job
	 * status is inconsistent with the one before webservice stopped, we need to
	 * fix it after webservice restart.
	 * 
	 * @param currentJob
	 * @param currentStatus
	 * @param vmInstanceUUID
	 */
	protected void fixMergeStatusAfterRestart(AbstractMergeJob currentJob,
			MergeStatus currentStatus, String vmInstanceUUID) {
		if (currentJob != null)
			return;
		long available = checkMergeJobAvailableForRecoveryPoints(vmInstanceUUID);
		if (available == 0) {
			if (currentStatus.getStatus() == MergeStatus.Status.PAUSING) {
				if (currentStatus.isManualPause()) {
					currentStatus.setStatus(MergeStatus.Status.PAUSED_MANUALLY);
					currentStatus.setCanResume(true);
				} else {
					currentStatus.setStatus(MergeStatus.Status.NOTRUNNING);
					currentStatus.setCanResume(false);
				}

				currentStatus.setJobMonitor(null);
			}
		} else if (available == NO_SESSION_MERGE) {
			// the merge job finished when webservice in stopped state, we need
			// to clear the stop times.
			currentStatus.setStopTimes(0);
		}
	}

	public synchronized void updateMergeJobStatus(MergeStatus.Status status,
			MergeStatus jobStatus, MergeJobMonitor jobMonitor, long jobId,
			boolean finished, String vmInstanceUUID) {
		if (jobStatus.getStatus() != MergeStatus.Status.PAUSING)
			jobStatus.setStatus(status);
		jobStatus.setJobMonitor(jobMonitor);
		if (jobMonitor != null) {
			jobStatus.setJobId(jobId);
			jobStatus.setJobPhase(jobMonitor.getDwMergePhase());
			if (jobStatus.getStatus() == MergeStatus.Status.PAUSING)
				jobStatus.setJobStatus(JobStatus.JOBSTATUS_CANCELLED);
			else
				jobStatus.setJobStatus(jobMonitor.getJobStatus());
			jobStatus.setProgress(jobMonitor.getfMergePercentage());
			jobStatus.setStartTime(jobMonitor.getUllStartTime());
			jobStatus.setElapsedTime(jobMonitor.getUllElapsedTime());
			jobStatus.setRemainTime(jobMonitor.getTimeRemain());
			jobStatus.setFinished(finished);
		}
		// jobStatus.setDataStoreUUID(getDataStoreUUID(vmInstanceUUID));
		updateMergeJobStatus(jobStatus);
	}

	protected void unschedule(String vmInstanceUUID) {
		try {
			scheduler.pauseTrigger(new TriggerKey(
					getMergeJobName(vmInstanceUUID), MERGE_JOB_GROUP_NAME));
			scheduler.unscheduleJob(new TriggerKey(
					getMergeJobName(vmInstanceUUID), MERGE_JOB_GROUP_NAME));
			scheduler.deleteJob(new JobKey(getMergeJobName(vmInstanceUUID),
					MERGE_JOB_GROUP_NAME));
		} catch (SchedulerException e) {
			logger.debug("Failed to delete job");
		}
	}

	/*
	 * public void reportMergeJobMonitor(MergeStatus jobStatus, String
	 * vmInstanceUUID){ // Update job status to RPS by Listener. FlashSyncher
	 * flashSyn = FlashSyncher.getInstance(); String flashServerUuid =
	 * WebClientWrapper.retrieveCurrentUUID();
	 * jobStatus.setDataStoreUUID(getDataStoreUUID(vmInstanceUUID)); if
	 * (flashSyn.reportJobMonitor(jobStatus, vmInstanceUUID,
	 * getRPSPolicyUUID(vmInstanceUUID), flashServerUuid) != 0) {
	 * logger.error("Failed to update job status to RPS by Listener."); } }
	 */

	protected boolean isPlanDisabled(String vmInstanceUUID) {
		return false;
	}

	// abstract method
	protected abstract long checkMergeJobAvailableForRecoveryPoints(
			String vmInstanceUUID);

	protected abstract String getMergeJobName(String vmInstanceUUID);

	protected abstract void logWithID(long level, long jobId, String msg,
			String vmInstanceUUID);

	public abstract long startMerge(AbstractMergeJob job);

	public abstract void mergeDone(AbstractMergeJob job);

	public abstract RetentionPolicy getRetentionPolicy(String vmInstanceUUID);

	public abstract List<DailyScheduleDetailItem> getMergeSchedule(
			String vmInstanceUUID);

	public abstract void updateMergeJobStatus(MergeStatus status);

	public abstract boolean isMergeJobAvailable(String vmInstanceUUID);

	public abstract void saveMergeStatus();

	public abstract boolean canStartMerge(String vmInstanceUUID);

	protected abstract void sendEmailOnMergePausedManually(String vmInstanceUUID);

	public abstract String getRPSPolicyUUID(String vmInstanceUUID);

	public abstract String getDataStoreUUID(String vmInstanceUUID);

	// /////////////////////////////////////////////////////////////////////////

	private MergeStatus.Status setMergeStatus(MergeEvent event) {
		switch (event) {
		case MANUAL_RESUME:
		case WS_RESUME:
		case OTHER_JOB_END:
		case WS_RESTART:
		case SCHEDULE_BEGIN:
			return MergeStatus.Status.RUNNING;
		case MANUAL_STOP:
			return MergeStatus.Status.PAUSED_MANUALLY;
		case WS_STOP:
		case OTHER_JOB_START:
			return MergeStatus.Status.PAUSED;
		case MERGE_COMPLETE:
			return MergeStatus.Status.NOTRUNNING;
		case NO_SCHEDULE:
			return MergeStatus.Status.PAUSED_NO_SCHEDULE;
		}
		return MergeStatus.Status.NOTRUNNING;
	}

	public static enum MergeEvent {
		MANUAL_RESUME, // resume the job from UI
		MANUAL_STOP, // stop the job from UI
		WS_RESUME, // resume the job by webservice call
		WS_STOP, // stop the job by webservice call
		OTHER_JOB_START, // other jobs like backup start.
		OTHER_JOB_END, // other jobs like backup end
		WS_RESTART, // webservice restart
		MERGE_COMPLETE, // merge job complete
		NO_SCHEDULE, // merge job is out of schedule
		SCHEDULE_BEGIN// merge job is goto schedule for schedule changing or
						// time reaches
	}

	protected enum MergeAction {
		STOP, RESUME
	}

	public static class MergeActionContext {
		public boolean manually;
		public boolean internal;
		public MergeAction action;
		public MergeEvent event;

		public MergeActionContext(boolean m, boolean i, MergeAction a) {
			manually = m;
			internal = i;
			action = a;
		}

		public MergeActionContext(MergeEvent event, MergeAction a) {
			this.event = event;
			action = a;
		}
	}
}

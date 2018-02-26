package com.ca.arcflash.webservice.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jni.common.JJobHistory;
import com.ca.arcflash.service.data.PeriodRetentionValue;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.FlashServiceImpl;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.NextScheduleEvent;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.activitylog.ActivityLogResult;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.backup.RetryPolicy;
import com.ca.arcflash.webservice.data.backup.RpsPolicy4D2D;
import com.ca.arcflash.webservice.data.edge.datasync.d2d.D2DStatus;
import com.ca.arcflash.webservice.data.job.rps.BackupJobArg;
import com.ca.arcflash.webservice.data.job.rps.IJobDependency;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegStatusRefresh;
import com.ca.arcflash.webservice.edge.d2dreg.PlanUtil;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyCheckStatus;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyQueryStatus;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.replication.BaseReplicationCommand;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BackupSetService;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.CatalogService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.CopyService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.internal.BackupJobConverter;
import com.ca.arcflash.webservice.service.rps.JobService;
import com.ca.arcflash.webservice.service.rps.SettingsService;
import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.TheadPoolManager;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class BaseBackupJob extends BaseJob {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(BaseBackupJob.class);
	protected BackupJobConverter jobConverter = new BackupJobConverter();

	private volatile static boolean processingDestChanged;
	private volatile boolean ownerOfDestChanged;

	private static final String VCM_CONVERT_TO_FULL_BACKUP = "VCM_CONVERT_TO_FULL_BACKUP";

	private volatile static boolean isJobRunning = false;
	private volatile static boolean JobMonitorEdgeThreadStarted = false;
	public final static long BackupJob_PROC_EXIT = 10;
	public final static long BackupJob_Phase_PROC_EXIT = 0xE;
	// private int counterForExit = 0;
	protected static JobExecutionContext currentJob = null;;

	public static JobExecutionContext getCurrentJob() {
		return currentJob;
	}

	public static void setCurrentJob(JobExecutionContext currentJob) {
		BaseBackupJob.currentJob = currentJob;
	}

	protected static Object currentJobLock = new Object();
	protected final static int missedJob = 0;
	protected final static int skippedJob = 1;

	protected boolean isDaily = false;
	protected boolean isWeekly = false;
	protected boolean isMonthly = false;
	protected int scheduletype=0;//ADD
	protected boolean bCatalogGenerate = false;

	static final Observer[] observers = new Observer[] { CatalogService.getInstance(), HAService.getInstance(), CopyService.getInstance(), ArchiveService.getInstance()};

	// private volatile static boolean isJobRunning = false;

	// private static Object recfgSCHLock = new Object();

	public static void resumeMonitorAfterRestart() {
		logger.debug("resumeMonitorAfterRestart() - start");

		BaseBackupJob job = new BaseBackupJob();
		logger.info("begin to call jni getCurrentJobID");// getCurrentJobID some
															// times cause the
															// web service
															// reboot, this log
															// is for the issue
															// reproduce.
		job.shrmemid = CommonService.getInstance().getNativeFacade().getCurrentJobID();
		logger.info("finish call jni getCurrentJobID");
		logger.info("jobId: " + job.shrmemid);
		if (job.shrmemid > 0) {
			if (job.getJobLock()) {
				logger.info("RestartJobMonitor");
				new Thread(new JobMonitorThread(job)).start();
			}
		}

		logger.debug("resumeMonitorAfterRestart() - end");
	}

	public JJobScript generateBackupJobScript(BackupConfiguration configuration, ArchiveConfiguration archiveConfig) throws Exception {
		return null;
	}
	
	public int getScheduleType(){
		return this.scheduletype;
	}

	protected BackupJobArg getBackupJobArg(JobDetailImpl jobDetail, BackupConfiguration configuration){
		BackupJobArg arg = BackupService.getInstance().getBackupJobArg(jobDetail, configuration);
		arg.setJobType(JobType.JOBTYPE_BACKUP);
		arg.setJobId(shrmemid);
		arg.setPeriodRetentionFlag(getPeriodRetentionFlag());
		arg.setManual(isMaunal());
		List<String> jobDependencies = new ArrayList<String>();
		if (configuration.isGenerateCatalog())
			jobDependencies.add(IJobDependency.CATALOG_JOB);
		else {
			if (configuration.getExchangeGRTSetting() == 1)
				jobDependencies.add(IJobDependency.CATALOG_JOB);
			jobDependencies.add(IJobDependency.COPY_JOB);
		}

		// set variable retention catalog job.
		if (configuration.getAdvanceSchedule() != null) {
			PeriodSchedule p = configuration.getAdvanceSchedule().getPeriodSchedule();
			if (p != null && p.isEnabled()) {
				if (p.getDaySchedule() != null && isDaily)
					if (p.getDaySchedule().isGenerateCatalog())
						jobDependencies.add(IJobDependency.CATALOG_JOB);

				if (p.getWeekSchedule() != null && isWeekly)
					if (p.getWeekSchedule().isGenerateCatalog())
						jobDependencies.add(IJobDependency.CATALOG_JOB);

				if (p.getMonthSchedule() != null && isMonthly)
					if (p.getMonthSchedule().isGenerateCatalog())
						jobDependencies.add(IJobDependency.CATALOG_JOB);
			}
		}
		try {
			ArchiveConfiguration archiveConfig = ArchiveService.getInstance().getArchiveConfiguration();
			if(archiveConfig != null && archiveConfig.isbArchiveAfterBackup()
					&& !ArchiveService.getInstance().hasEnabledAdvSchedule(archiveConfig.getAdvanceSchedule()))
				jobDependencies.add(IJobDependency.FILECOPY_JOB);
		} catch (ServiceException e) {
			logger.equals(e);
		}
		arg.setJobDependencies(jobDependencies.toArray(new String[0]));
		return arg;
	}

	private int getPeriodRetentionFlag() {
		int periodRetentionFlag = 0;
		if (this.isMonthly)
			periodRetentionFlag |= PeriodRetentionValue.QJDTO_B_Backup_Monthly;
		if (this.isWeekly)
			periodRetentionFlag |= PeriodRetentionValue.QJDTO_B_Backup_Weekly;
		if (this.isDaily)
			periodRetentionFlag |= PeriodRetentionValue.QJDTO_B_Backup_Daily;
		return periodRetentionFlag;
	}

	private RpsPolicy4D2D checkRPS4Backup(NativeFacade nativeFacade, int periodRetentionFlag) {
		String message = null;
		logger.info("Check Rps for backup start");
		try {
			RpsPolicy4D2D policy = BackupService.getInstance().checkRPS4Backup();
			rpsPolicyUUID = policy.getPolicyUUID();
			rpsDataStoreUUID = policy.getDataStoreName();
			logger.info("Check Rps for backup end");
			return policy;
		} catch (ServiceException se) {
			// fix issue 106771
			if (se.getErrorCode().equals(FlashServiceErrorCode.Backup_RPS_Datastore_stopped)) {
				RpsPolicy4D2D policy = SettingsService.instance().getTempPolicy();
				rpsDataStoreUUID = policy.getDataStoreName();
			}

			logger.error("Check RPS for backup got exception ", se);
			message = se.getMessage();
			if (StringUtil.isEmptyOrNull(message)) {
				message = WebServiceMessages.getResource("backupNotRunPolicy");
			} else {
				message = WebServiceMessages.getResource("backupNotRunRPSError", message);
			}
		} catch (Throwable t) {
			message = WebServiceMessages.getResource("backupNotRunPolicy");
			logger.error("Check RPS for backup got exception ", t);
		}
		long datastoreVersion = BackupService.getInstance().getRPSDatastoreVersion(rpsDataStoreUUID);
		this.addMissedJobHistory(JobType.JOBTYPE_BACKUP, rpsDataStoreUUID, datastoreVersion, getJobMethod(), periodRetentionFlag);
		if (!StringUtil.isEmptyOrNull(message)) {
			nativeFacade.addLogActivityWithJobID(Constants.AFRES_AFALOG_ERROR, shrmemid, Constants.AFRES_AFJWBS_GENERAL,
					new String[] { message, "", "", "", "" });
		}
		return null;
	}

	private IComputeMissedJob calMissedJob = CalMissedJob.getInstance();

	// @Deprecated
	// private boolean IsManagedByEdgeCM(){
	// boolean IsManagedByEdge = false;
	// D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
	// EdgeRegInfo edgeRegInfo =
	// edgeReg.getEdgeRegInfo(ApplicationType.CentralManagement);
	// if(edgeRegInfo == null || edgeRegInfo.getEdgeWSDL() == null ||
	// edgeRegInfo.getEdgeWSDL().isEmpty()) {
	// logger.debug("There is no local registration information for CentralManagement");
	// return false;
	// }
	//
	// IEdgeCM4D2D proxy =
	// WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(),IEdgeCM4D2D.class);
	//
	// if(proxy == null){
	// logger.error("Failed to get Edge proxy handle!");
	// //throw new ServiceException("Cannot get Edge proxy handle",
	// "EdgeConnectError");
	// }
	//
	// String UUID = CommonService.getInstance().getUUID();
	//
	// try {
	// proxy.validateUserByUUID(edgeRegInfo.getEdgeUUID());
	// }catch(EdgeServiceFault e) {
	// logger.error("Failed to establish connection to Edge Server(login failed)");
	// //throw new ServiceException(e.getMessage(), "EdgeLoginError");
	// }
	//
	// try {
	// IsManagedByEdge = proxy.isManagedByEdge(UUID);
	// } catch (Exception e) {
	// logger.debug(e);
	// //throw new ServiceException(e.getMessage(),"EdgeConnectError");
	// }
	//
	// return IsManagedByEdge;
	// }

	/**
	 * If agent is deleted by CPM or no plan assigned to agent, return false.
	 * Otherwise, return true
	 */
	private boolean checkD2DStatus() {
		try {
			logger.debug("Refresh Edge Registration status for CentralManagement.");
			D2DStatus status = CommonService.getInstance().checkD2DStatusFromEdgeCM();
			logger.info("D2DStatus is " + status.name());
			switch (status) {
			case NodeDeleted:
				logger.info("clean registration info ...");
				new D2DEdgeRegStatusRefresh().cleanRegInfo4VSphereOrVCMIfNeed();
				PlanUtil.cleanRegInfo4CM();
				return false;
			case NoPolicy:
				logger.info("clean plan info ...");
				PlanUtil.cleanPlanInfo4CM();
				return false;
			case Ok:
			case PolicyChanged:
			case StandAlone:
			default:
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
			return true;
		}
	}

	class JobArg {
		int periodRetentionFlag = 0;
		JJobScript backupJob = null;
		boolean toRPSJobQueue = false;
	}

	static AtomicBoolean isJobSubmittedToExecute = new AtomicBoolean(false);

	static ReentrantLock lock = new ReentrantLock();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("execute(JobExecutionContext) - start:" + context);
		JobDetail jobDetail = context.getJobDetail();
		Object jobID = jobDetail.getJobDataMap().get(BaseService.JOB_ID);
		this.scheduletype=this.getScheduleType(jobDetail);
		try {
			lock.lock();
			
			if (jobID != null)
				this.shrmemid = (Long) jobID;

			if (!handleConflict(context)) {
				logger.info("job exit due to schedule confict:" + context);
				return;
			}

			final JobExecutionContext currentContext = context;
			TheadPoolManager.getThreadPool(TheadPoolManager.UtilTheadPool).submit(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(1500); // waiting for 1.5 seconds.
					} catch (InterruptedException e) {
						logger.info("job is failed to run", e);
						return;
					}

					logger.info("Job is going to run:" + currentContext);
					doJob(currentContext);
					isJobSubmittedToExecute.getAndSet(false);
					logger.info("Job done!");
					logger.info("reset Job Submitted To Execute to :" + isJobSubmittedToExecute);
				}
			});

			isJobSubmittedToExecute.getAndSet(true);
			logger.info("Set Job Submitted To Execute to:" + isJobSubmittedToExecute);
			// Thread.yield();
			currentJob = currentContext;
		} finally {
			logger.info("execute(JobExecutionContext) - end:" + context);
			lock.unlock();
		}
	}

	private void doJob(JobExecutionContext context) {
		JobDetailImpl jobDetail = (JobDetailImpl)context.getJobDetail();
		JobArg jobArg = new JobArg();
		try {
			long result = -1;
			NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
			synchronized (BaseBackupJob.class) {
				try {			
					lock.lock();				
					if (!prepareJob(context, jobArg)) {
						return;
					}
				} finally {
					isJobSubmittedToExecute.getAndSet(false);
					logger.info("reset Job Submitted To Execute:" + isJobSubmittedToExecute);
					lock.unlock();
				}
			}

			try {
				if (BackupService.getInstance().isBackupToRPS()) {
					// check whether rps policy exist
					RpsPolicy4D2D rpsPolicy = checkRPS4Backup(nativeFacade, jobArg.periodRetentionFlag);
					if (rpsPolicy == null) {
						stopJobMonitor();
						sendEmailOnMissedJob(context, true);
						return;
					}					
				}

				addBackupSetStartFlag(BackupService.getInstance().getBackupConfiguration(), jobArg.backupJob.getUsJobMethod(), jobArg.backupJob,
						jobDetail.getName());
				// put the backup job script, for later catalog job use
				jobDetail.getJobDataMap().put("jobScript", jobArg.backupJob);

				result = BackupService.getInstance().backup(jobArg.backupJob);
				if (result != 0) {
					stopJobMonitor();
					this.setJobStatus(Constants.JOBSTATUS_FAILED);
					logger.info("backup result:" + result);

					// sonle01: Inform sync Data to Edge
					EdgeDataSynchronization.SetSyncDataFlag();

					return;
				}

				// we wait here to get the job's status. The lock is released in
				// JobMonitorThread
				synchronized (phaseLock) {
					while (jobPhase != Constants.JobExitPhase)
						phaseLock.wait();
				}
				logger.debug("Backup Result:" + result);
			} catch (Throwable e) {
				logger.error("backup execute with execption", e);
				stopJobMonitor();
				this.setJobStatus(Constants.JOBSTATUS_FAILED);
				EdgeDataSynchronization.SetSyncDataFlag();
				return;
			}

			if (ownerOfDestChanged && result == 0) {
				BackupService.getInstance().getBackupConfiguration().setChangedBackupDest(false);
				BackupService.getInstance().getBackupConfiguration().setChangedBackupDestType(BackupType.Unknown);
				BackupConfiguration currentConf = BackupService.getInstance().getBackupConfiguration();
				// if user has changed the settings, should not update
				if (currentConf != null && currentConf.getDestination().equals(BackupService.getInstance().getBackupConfiguration().getDestination())) {
					BackupService.getInstance().updateBackupConfiguration(BackupService.getInstance().getBackupConfiguration());
				}
			}
		} catch (Exception e) {
			logger.error("base backup job", e);
		} finally {
			if (ownerOfDestChanged) {
				synchronized (BaseBackupJob.class) {
					processingDestChanged = false;
				}
				ownerOfDestChanged = false;
			}
			if (jobArg.toRPSJobQueue)
				return;
			// if failed
			retryJobWhenFailedOrCrashed(jobDetail);
			BackupService.getInstance().regenerateWriterMetadata();
			// HAService.getInstance().notifyBackupEnds(getJobStatus());
		}

		// sonle01: Inform sync Data to Edge
		EdgeDataSynchronization.SetSyncDataFlag();
	}

	private boolean handleConflict(JobExecutionContext context) {
		Date date1 = context.getFireTime();
		JobDetailImpl jobDetail = (JobDetailImpl)context.getJobDetail();
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		name = jobDetail.getName();
		if (name != null && name.endsWith(Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME)) {
			name = jobDetail.getJobDataMap().getString("jobName");
		} else {
			name = BackupConverterUtil.backupIndicatorToName(jobDetail.getName());
		}

		if (nativeFacade.checkJobExist() || isJobRunning || isJobSubmittedToExecute.get()) {
			String date = BackupConverterUtil.dateToString(date1);
			String msg = WebServiceMessages
					.getResource("skippedJobInformation", new Object[] { WebServiceMessages.getResource(Constants.regular), name, date });

			if (jobDetail.getName().contains("daily")) {
				msg = WebServiceMessages.getResource("skippedJobInformation", new Object[] { WebServiceMessages.getResource(Constants.daily), name, date });
			}
			if (jobDetail.getName().contains("weekly")) {
				msg = WebServiceMessages.getResource("skippedJobInformation", new Object[] { WebServiceMessages.getResource(Constants.weekly), name, date });
			}
			if (jobDetail.getName().contains("monthly")) {
				msg = WebServiceMessages.getResource("skippedJobInformation", new Object[] { WebServiceMessages.getResource(Constants.monthly), name, date });
			}

			logger.info("skipped job message:" + msg);

			nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_GENERAL, new String[] { msg, "", "", "", "" });
			String currentJobName = "";
			if (currentJob != null && currentJob.getJobDetail() != null) {
				currentJobName = ((JobDetailImpl)currentJob.getJobDetail()).getName();
			}

			logger.info("Job is missed due to another job is Running, missed: " + jobDetail.getName() + ", running:" + currentJobName);
			sendEmailOnMissedJob(context, false);

			synchronized (missJobLock) {
				calMissedJob.initJobContext(missedJobContext, currentJob);
				missedJobContext = calMissedJob.compute(context);
			}

			if (missedJobContext == null) {
				Date missedDate = context.getScheduledFireTime();
				if (ScheduleUtils.getMakeup().getConflictTimeAndFlag().containsKey(missedDate.getTime())) {
					ConfilctData data = ScheduleUtils.getMakeup().getConflictTimeAndFlag().get(missedDate.getTime());
					data.setCurrent(getPeriodFlag(currentJob.getJobDetail()));
					data.setFlag(data.getFlag() | getPeriodFlag(jobDetail));
					ScheduleUtils.getMakeup().getConflictTimeAndFlag().put(missedDate.getTime(), data);
				} else {
					ConfilctData data = new ConfilctData();
					data.setCurrent(getPeriodFlag(currentJob.getJobDetail()));
					data.setFlag(getPeriodFlag(jobDetail));
					ScheduleUtils.getMakeup().getConflictTimeAndFlag().put(missedDate.getTime(), data);
				}
				ScheduleUtils.saveMakeUp(ScheduleUtils.getMakeup());
			}

			logger.info("Other jobs are running, exit, last remembered missed job : "
					+ (missedJobContext == null ? "null" : ((JobDetailImpl)missedJobContext.getJobDetail()).getFullName()));
			return false;
		}
		return true;
	}

	public int getPeriodFlag(JobDetail jobDetail) {
		if (jobDetail == null)
			return 0;

		boolean isDaily = false, isWeekly = false, isMonthly = false;

		if (jobDetail.getJobDataMap().containsKey("periodRetentionFlag")) {
			int periodRetentionFlag = jobDetail.getJobDataMap().getInt("periodRetentionFlag");
			isDaily = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Daily) > 0;
			isWeekly = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Weekly) > 0;
			isMonthly = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Monthly) > 0;
		} else {
			if (jobDetail.getJobDataMap().containsKey("isDaily")) {
				isDaily = jobDetail.getJobDataMap().getBoolean("isDaily");
			}

			if (jobDetail.getJobDataMap().containsKey("isWeekly")) {
				isWeekly = jobDetail.getJobDataMap().getBoolean("isWeekly");
			}

			if (jobDetail.getJobDataMap().containsKey("isMonthly")) {
				isMonthly = jobDetail.getJobDataMap().getBoolean("isMonthly");
			}
		}

		if (isDaily) {
			return PeriodRetentionValue.QJDTO_B_Backup_Daily;
		} else if (isWeekly) {
			return PeriodRetentionValue.QJDTO_B_Backup_Weekly;
		} else if (isMonthly) {
			return PeriodRetentionValue.QJDTO_B_Backup_Monthly;
		}

		return 0;
	}

	private boolean prepareJob(JobExecutionContext context, JobArg jobArg) throws Exception {
		Date date1 = context.getFireTime();
		JobDetailImpl jobDetail = (JobDetailImpl)context.getJobDetail();
		RpsPolicy4D2D rpsPolicy = null;
		jobArg.backupJob = null;
		BackupConfiguration configuration = null;
		jobArg.periodRetentionFlag = 0;
		if(jobDetail.getJobDataMap().containsKey("manualFlag")){
			isMaunal = jobDetail.getJobDataMap().getBoolean("manualFlag");
		}

		if (!isContinueJob()) {
			logger.warn("Job is skipped, because policy is changed");

			// if(!IsManagedByEdgeCM()){
			// D2DEdgeRegistration.cleanRegInfo4CM();
			// }

			sendEmailOnMissedJob(context, false);
			return false;
		}

		if (!checkD2DStatus()) {
			logger.info("CPM delete the agent or not assign a plan, stop the job.");
			return false;
		}
		

		BackupService.getInstance().reloadBackupConfiguration();
		configuration = BackupService.getInstance().getBackupConfiguration();
		
		if(!isMaunal() && isPlanPaused()) {
			logger.info("backup job will not start because plan was paused");
			return false;
		}
		
		if (shrmemid <= 0)
			initShrmemid();

		if (jobDetail.getJobDataMap().containsKey("isDaily")) {
			isDaily = jobDetail.getJobDataMap().getBoolean("isDaily");
			logger.info("isDaily:" + isDaily + ", scheduled at:" + context.getScheduledFireTime());

			jobArg.periodRetentionFlag = PeriodRetentionValue.QJDTO_B_Backup_Daily;
		}
		if (jobDetail.getJobDataMap().containsKey("isWeekly")) {
			isWeekly = jobDetail.getJobDataMap().getBoolean("isWeekly");
			logger.info("isWeekly:" + isWeekly + ", scheduled at:" + context.getScheduledFireTime());
			jobArg.periodRetentionFlag = PeriodRetentionValue.QJDTO_B_Backup_Weekly;
			
		}
		if (jobDetail.getJobDataMap().containsKey("isMonthly")) {
			isMonthly = jobDetail.getJobDataMap().getBoolean("isMonthly");
			logger.info("isMonthly:" + isMonthly + ", scheduled at:" + context.getScheduledFireTime());
			jobArg.periodRetentionFlag = PeriodRetentionValue.QJDTO_B_Backup_Monthly;
			
		}

		if (BackupService.getInstance().isBackupToRPS()) {
			rpsHost = configuration.getBackupRpsDestSetting().getRpsHost();
			// check whether rps policy exist
			rpsPolicy = checkRPS4Backup(BackupService.getInstance().getNativeFacade(), jobArg.periodRetentionFlag);
			if (rpsPolicy == null) {
				sendEmailOnMissedJob(context, true);
				return false;
			}

			if (isNotBackupNowJob(jobDetail)) {
				jobArg.toRPSJobQueue = true;
				logger.info("Submit backup to RPS server");
				BackupJobArg arg = this.getBackupJobArg(jobDetail, configuration);
				getJobArgWithSrc(jobDetail, rpsPolicyUUID, rpsDataStoreUUID, JobType.JOBTYPE_BACKUP, rpsHost, arg, rpsDataStoreName);
				arg.setScheduledTime(context.getScheduledFireTime().getTime());
				JobService.getInstance().submitBackup(arg, configuration.getBackupRpsDestSetting().getRpsHost());
				logger.info("Submit backup to RPS server succeed");
				return false;
			} else {
				// check whether RPS policy is modified
				configuration = BackupService.getInstance().rpsPolicyUpdated(rpsPolicy, false);
			}
		}

		// Check whether need to schedule a job for DST skipped daily backup
		BackupService.getInstance().makeupForDSTSkippedDaily(date1, null);

		// Step 1: generate job script
		logger.info("Generate job script");

		if (jobDetail.getJobDataMap().containsKey("periodRetentionFlag")) {
			int periodRetentionFlag = jobDetail.getJobDataMap().getInt("periodRetentionFlag");
			isDaily = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Daily) > 0;
			isWeekly = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Weekly) > 0;
			isMonthly = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Monthly) > 0;

			if (periodRetentionFlag > 0)
				logger.info("periodRetentionFlag:" + periodRetentionFlag);

			logger.debug("periodRetentionFlag:" + periodRetentionFlag);
		}

		bCatalogGenerate = jobDetail.getJobDataMap().getBoolean(BaseService.RPS_CATALOG_GENERATION);
		
		ArchiveConfiguration archiveConfig = null;
		if(BackupService.getInstance().isBackupToRPS()){
			try {
				archiveConfig = (ArchiveConfiguration) jobDetail.getJobDataMap().get("archiveConfiguration");
			} catch (Exception e) {
				archiveConfig = null;
			}			
		}else{
			archiveConfig = ArchiveService.getInstance().getArchiveConfiguration();
		}

		try {
			jobArg.backupJob = generateBackupJobScript(configuration, archiveConfig);
			if (jobDetail.getJobDataMap().containsKey("scheduledTime")) {
				jobArg.backupJob.setUllScheduledTime(jobDetail.getJobDataMap().getLong("scheduledTime"));
			} else {
				jobArg.backupJob.setUllScheduledTime(context.getScheduledFireTime().getTime());
			}
		} catch (ServiceException e) {
			sendEmailOnMissedJob(context, true);
		}

		js = jobArg.backupJob;
		if (jobArg.backupJob == null) {
			logger.debug("backup job script is null, return from scheduler");
			return false;
		}

		synchronized (currentJobLock) {
			currentJob = context;
		}
		logger.info("current job:" + ((JobDetailImpl)currentJob.getJobDetail()).getFullName());

		String jobName = jobDetail.getJobDataMap().getString("jobName");
		if (!StringUtil.isEmptyOrNull(jobName)) {
			jobArg.backupJob.setPwszComments(jobName);
		}

		// After the backup destination changed, jobs launched by user manually
		// are not changed
		// to the job type specified in the backup setting dialog.
		if (jobDetail.getName() != null) {
			synchronized (BaseBackupJob.class) {
				if (!processingDestChanged) {
					if (configuration.isChangedBackupDest()) {
						processingDestChanged = true;
						ownerOfDestChanged = true;
						// give up this case.
						// backupType = configuration
						// .getChangedBackupDestType();
						// backupJob.setUsJobMethod(backupType);
					}
				}
			}
		}

		// Before execute job, check whether the FirstVirtualConversionSetting
		// registry key
		// is configured, in this case, we need change job type to FULL in some
		// cases
		logger.info("Check for VCM");
		if (CommonUtil.getFirstVCMSettingFlag() == 1) {
			if (jobArg.backupJob.getUsJobMethod() != BackupType.Full) {
				if (BaseReplicationCommand.checkSessionsOKForVCM() == false) {
					jobArg.backupJob.setUsJobMethod(BackupType.Full);
					logger.info("execute() - convert backup job to full one for Virtual Conversion.");
					BackupService
							.getInstance()
							.getNativeFacade()
							.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_GENERAL,
									new String[] { WebServiceMessages.getResource(BaseBackupJob.VCM_CONVERT_TO_FULL_BACKUP), "", "", "", "" });
				}
			}
			CommonUtil.setFirstVCMSettingFlag(false); // Clean this flag
		}

		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertObject2String(jobArg.backupJob));

		// Step2: execute backup job
		// result = -1;
		// result is 0 meaning successful, -2 means AFBackup result non zero

		try {
			if (!preprocess(jobArg.backupJob, observers)) {
				logger.debug("Job monitor is running, exist");
				return false;
			}
		} catch (Throwable e) {
			logger.error("backup execute with execption", e);
			stopJobMonitor();
			this.setJobStatus(Constants.JOBSTATUS_FAILED);
			EdgeDataSynchronization.SetSyncDataFlag();
			return false;
		}

		return true;

	}

	private boolean isNotBackupNowJob(JobDetail jobDetail) {
		Boolean backupNow = (Boolean) jobDetail.getJobDataMap().get("backupNow");
		return backupNow == null || !backupNow;
	}

	private boolean isContinueJob() {
		int status = BackupService.getInstance().refreshBackupConfigSettingWithEdge();
		switch (status) {
		case PolicyCheckStatus.UNKNOWN:
			// check may failed
		case PolicyCheckStatus.SAMEPOLICY:
			// check ok
			return true;
		case PolicyCheckStatus.NOPOLICY:
			// unassigned policy, keep setting and continue job
			return false;
		case PolicyCheckStatus.POLICYDEPLOYING:
			// policy deploying
			int r = BackupService.getInstance().CheckBackupConfigSettingWithEdge();
			return true;
		case PolicyCheckStatus.DIFFERENTPOLICY:
			// policy changed
			r = BackupService.getInstance().CheckBackupConfigSettingWithEdge();
			if (r == PolicyQueryStatus.FAIL)
				BackupService
						.getInstance()
						.getNativeFacade()
						.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_GENERAL,
								new String[] { WebServiceMessages.getResource("autoRedeployPolicySkip"), "", "", "", "" });
			return true;
		case PolicyCheckStatus.POLICYFAILED:
			// policy failed before
			r = BackupService.getInstance().CheckBackupConfigSettingWithEdge();
			if (r == PolicyQueryStatus.FAIL)
				BackupService
						.getInstance()
						.getNativeFacade()
						.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_GENERAL,
								new String[] { WebServiceMessages.getResource("autoRedeployFailedPolicySkip"), "", "", "", "" });
			return true;
		}

		return true;
	}

	/**
	 * For backup job, we need to check whether job is done with phase 0xE.
	 */
	@Override
	protected boolean isJobDone(long jobPhase, long jobStatus) {
		if (jobPhase == BackupJob_Phase_PROC_EXIT) {
			/*
			 * if(jobStatus == Constants.JOBSTATUS_FINISHED) { counterForExit
			 * ++; //Job monitor thread sleep 1 second for each loop //if there
			 * is 10 minutes before process exit and after get exit phase //we
			 * log an warning to track it. if(counterForExit >= 10 * 60) {
			 * logger.warn("Backup " + this.shrmemid + " has exited for more " +
			 * "than 10 minutes, while the process is still alive"); } return
			 * false; }else { logger.debug("Backup job done with status: " +
			 * jobStatus); return true; }
			 */
			return true;
		} else {
			return false;
		}
	}

	private void addBackupSetStartFlag(BackupConfiguration configuration, int jobType, JJobScript result, String jobDetailName) {
		if (configuration == null || result == null) {
			return;
		}

		if (BackupSetService.getInstance().isCurrentBackupSetStart(configuration, jobType, jobDetailName, "")) {
			// set the backup set flag and convert current job to full.
			int foption = result.getFOptions();
			result.setFOptions(BackupJobConverter.BACKUP_SET_START | foption);
			if (result.getUsJobMethod() != BackupType.Full) {
				result.setUsJobMethod(BackupType.Full);
				String message = WebServiceMessages.getResource("convertJobToFullForBackupSetStart");
				BackupService
						.getInstance()
						.getNativeFacade()
						.addLogActivityWithJobID(Constants.AFRES_AFALOG_WARNING, result.getUlJobID(), Constants.AFRES_AFJWBS_GENERAL,
								new String[] { message, "", "", "", "" });
			}
		}
	}

	private boolean isScheduledJob(String jobName) {
		if (jobName != null && !jobName.endsWith(BaseService.JOB_NAME_BACKUP_NOW_SUFFIX))
			return true;
		else
			return false;
	}

	private void retryJobWhenFailedOrCrashed(JobDetailImpl jobDetail) {
		if (this.getJobStatus() == Constants.JOBSTATUS_FAILED || this.getJobStatus() == Constants.JOBSTATUS_CRASH) {
			// we exclude the now job, and retry job itself, just include the
			// scheduled jobs
			if (isScheduledJob(jobDetail.getName())) {
				String name = jobDetail.getName();
				String group = jobDetail.getGroup();
				int type = BackupType.Unknown;
				if (name != null && group != null) {
					if (group.equals(BackupService.JOB_GROUP_BACKUP_NAME) || group.equals(BackupService.JOB_GROUP_BACKUP_NAME + "Now")) {
						if (name.endsWith(Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME)) { //
							int retriedType = jobDetail.getJobDataMap().getInt("oriJobType");
							int times = jobDetail.getJobDataMap().getInt("retriedTimes");
							long jobID = jobDetail.getJobDataMap().getLong("oriJobID");
							retryFailedBackup(retriedType, times + 1, jobID, jobDetail);

						} else if (name.startsWith(BackupService.JOB_NAME_BACKUP_FULL)) {
							type = BackupType.Full;
							retryFailedBackup(type, 1, this.shrmemid, jobDetail);
						} else if (name.startsWith(BackupService.JOB_NAME_BACKUP_RESYNC)) {
							type = BackupType.Resync;
							retryFailedBackup(type, 1, this.shrmemid, jobDetail);
						} else if (name.startsWith(BackupService.JOB_NAME_BACKUP_INCREMENTAL)) {
							type = BackupType.Incremental;
							retryFailedBackup(type, 1, this.shrmemid, jobDetail);
						}

					}

				}
			}
		}

		else { // succeed, if it is one of retry job, we need to delete the job
			String jobName = jobDetail.getName();
			if (jobName != null && jobName.endsWith(Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME)) {
				try {
					BackupService.getInstance().getBackupSchedule().deleteJob(new JobKey(jobName, jobDetail.getGroup()));
				} catch (SchedulerException e1) {
					logger.error("execute - end " + e1.getMessage(), e1);
				}
			}

		}
	}

	/**
	 * 
	 * @param type
	 *            the original backup type
	 * @param times
	 *            the times to setup a failed job
	 * @param self_jobDetail
	 *            the current job, used for delete
	 */
	private void retryFailedBackup(int type, int times, long jobID, JobDetailImpl self_jobDetail) {
		logger.debug("retryFailedBackup() - begin");
		JobDetail jobDetail;
		String jobname = "";
		String jobDisplayName = "";
		// String oriJobName = "";
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		if (type == BackupType.Full) {
			// oriJobName = BackupService.JOB_NAME_BACKUP_FULL;
			// FullBackupJob_1_1_RETRY
			jobname = BackupService.JOB_NAME_BACKUP_FULL + "_" + jobID + "_" + times + Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME;
			// FullBackupJob_1_Retry_1
			// jobDisplayName = BackupService.JOB_NAME_BACKUP_FULL+"_"+jobID +
			// Constants.RETRYPOLICY_FOR_FAILED_DISPLAY_NAME+times;

			jobDetail = new JobDetailImpl(jobname, BackupService.JOB_GROUP_BACKUP_NAME, FullBackupJob.class);
		} else if (type == BackupType.Incremental) {
			// oriJobName = BackupService.JOB_NAME_BACKUP_INCREMENTAL;
			jobname = BackupService.JOB_NAME_BACKUP_INCREMENTAL + "_" + jobID + "_" + times + Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME;
			// jobDisplayName =
			// BackupService.JOB_NAME_BACKUP_INCREMENTAL+"_"+jobID +
			// Constants.RETRYPOLICY_FOR_FAILED_DISPLAY_NAME+times;

			jobDetail = new JobDetailImpl(jobname, BackupService.JOB_GROUP_BACKUP_NAME, IncrementalBackupJob.class);
		} else if (type == BackupType.Resync) {
			// oriJobName = BackupService.JOB_NAME_BACKUP_RESYNC;
			jobname = BackupService.JOB_NAME_BACKUP_RESYNC + "_" + jobID + "_" + times + Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME;
			// jobDisplayName = BackupService.JOB_NAME_BACKUP_RESYNC+"_"+jobID +
			// Constants.RETRYPOLICY_FOR_FAILED_DISPLAY_NAME+times;

			jobDetail = new JobDetailImpl(jobname, BackupService.JOB_GROUP_BACKUP_NAME, ResyncBackupJob.class);
		} else {
			logger.debug("retryFailedBackup() - end with error type");
			return;
		}

		try {
			// condition 1, if policy enabled
			RetryPolicy retryPolicy = BackupService.getInstance().getRetryPolicy(CommonService.RETRY_BACKUP);
			if (!(retryPolicy.isEnabled() && retryPolicy.isFailedEnabled())) {
				logger.debug("retryFailedBackup() - end with disabled retry policy for failed backup");
				nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY,
						new String[] { WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_FAILED_DISABLED), "", "", "", "" });
				return;
			}

			// quartz use default timezone,
			Calendar startCalendar = java.util.Calendar.getInstance();
			startCalendar.add(java.util.Calendar.MINUTE, retryPolicy.getTimeToWait());
			Date startDate = startCalendar.getTime();
			{// condition 2, if now is within specified minutes toward to next
				// backup event
				if (!retryPolicy.isImmediately()) {
					java.util.Calendar cal = new FlashServiceImpl().getServerCalendar();
					// time to wait
					cal.add(java.util.Calendar.MINUTE, retryPolicy.getTimeToWait());

					{
						NextScheduleEvent nextScheduleEvent;

						try {
							nextScheduleEvent = BackupService.getInstance().getNextScheduleEvent();
							if (nextScheduleEvent != null) {
								Date nextDate = nextScheduleEvent.getDate();
								java.util.Calendar serverCalendar = new FlashServiceImpl().getServerCalendar();
								serverCalendar.setTimeInMillis(nextDate.getTime());

								cal.add(java.util.Calendar.MINUTE, retryPolicy.getNearToNextEvent());
								if (cal.after(serverCalendar)) {
									logger.debug("retryFailedBackup() - end with too near to next event");
									String time = BackupConverterUtil.dateToString(nextDate);
									nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY, new String[] {
											WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_FAILED_SKIPPED_NEXT, time), "", "", "", "" });
									return;
								}

							}

						} catch (ServiceException e) {
							logger.error("retryFailedBackup - end " + e.getMessage(), e);
						}
					}
				}
			}
			// condition 3, times
			if (times > retryPolicy.getMaxTimes()) {
				logger.debug("retryFailedBackup() - end with too much times");
				nativeFacade.addLogActivity(
						Constants.AFRES_AFALOG_WARNING,
						Constants.AFRES_AFJWBS_JOB_RETRY,
						new String[] {
								WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_FAILED_EXCEED_MAXTIMES, "" + jobID, "" + retryPolicy.getMaxTimes()),
								"", "", "", "" });
				return;
			}
			// we omit the times and jobID to support i18n
			jobDisplayName = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_JOBNAME);
			// jobname is used for display in recent panel
			jobDetail.getJobDataMap().put("jobName", jobDisplayName);
			jobDetail.getJobDataMap().put("oriJobType", type);
			jobDetail.getJobDataMap().put("oriJobID", jobID);
			jobDetail.getJobDataMap().put("retriedTimes", times);

			BackupService.getInstance().modifyPeriodJObDetail(jobDetail, self_jobDetail);

			SimpleTriggerImpl trig = new SimpleTriggerImpl();
			if (retryPolicy.isImmediately()) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, 1);
				startDate = cal.getTime();
			}
			trig.setStartTime(startDate);
			trig.setRepeatCount(0);
			trig.setRepeatInterval(0);
			trig.setName(((JobDetailImpl)jobDetail).getName() + "Trigger");

			try {
				BackupService.getInstance().getBackupSchedule().scheduleJob(jobDetail, trig);
				String date = BackupConverterUtil.dateToString(startDate);

				String msg = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_FAILED_SCHEDULED, date, "" + jobID);
				nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY, msg);
				logger.info("activity log msg:" + msg);
			} catch (SchedulerException e) {
				logger.error("retryFailedBackup - end " + e.getMessage(), e);
			}
		} finally {
			if (self_jobDetail.getName().endsWith(Constants.RETRYPOLICY_FOR_FAILED_SUFFIXNAME)) {
				try {
					BackupService.getInstance().getBackupSchedule().deleteJob(new JobKey(self_jobDetail.getName(), BackupService.JOB_GROUP_BACKUP_NAME));
				} catch (SchedulerException e1) {
					logger.error("retryFailedBackup - end " + e1.getMessage(), e1);
				}
			}
		}
		logger.debug("retryFailedBackup() - end");
		return;

	}

	private void sendEmailOnMissedJob(JobExecutionContext context, boolean causedByRPSPolicy) {
		logger.debug("sendEmail - start");
		try {
			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			if (configuration == null)
				return;

			// BackupEmail email = configuration.getEmail();
			PreferencesConfiguration preferencesConfig = CommonService.getInstance().getPreferences();
			if (preferencesConfig == null) {
				return;
			}

			BackupEmail email = preferencesConfig.getEmailAlerts();
			if (email == null || !email.isEnableSettings())
				return;

			sendMissedJobEmail(email, context, configuration.getDestination(), causedByRPSPolicy);
		} catch (Exception e) {
			logger.error("Failed to get configuration", e);
		}
	}

	protected String getMissedJobContent(boolean isEnableHTMLFormat, JobExecutionContext context, String dest, boolean causedByRPS) {
		String exeDate = BackupConverterUtil.dateToString(context.getFireTime());
		String url = getBackupSettingsURL();
		ActivityLogResult result = null;
		if (causedByRPS) {
			try {
				if (this.shrmemid > 0)
					result = CommonService.getInstance().getJobActivityLogs(this.shrmemid, 0, 5);
				else
					result = CommonService.getInstance().getActivityLogs(0, 5);
			} catch (Exception e) {
				logger.error("Failed to get activity log", e);
			}
		}
	
		int scheduletype=getScheduleType(context.getJobDetail());
		
		
		if (isEnableHTMLFormat) {
			if (causedByRPS) {
				String rpsName = "";
			    if (rpsHost != null)
				     rpsName = rpsHost.getRhostname();
				return EmailContentTemplate.getHtmlContentCausedByRPS(Constants.JOBSTATUS_MISSED, Constants.AF_JOBTYPE_BACKUP, getJobMethod(), scheduletype,shrmemid > 0 ? shrmemid : 0,
						exeDate, result, url, rpsName, rpsDataStoreName);
			}
			else
			    return EmailContentTemplate.getBackUpHtmlContent(Constants.JOBSTATUS_MISSED, Constants.AF_JOBTYPE_BACKUP, getJobMethod(), scheduletype,shrmemid > 0 ? shrmemid : 0,
			    		exeDate, null, dest, result, url);
		} else {
			return EmailContentTemplate.getBackUpPlainTextContent(Constants.JOBSTATUS_MISSED, Constants.AF_JOBTYPE_BACKUP, getJobMethod(), scheduletype,shrmemid > 0 ? shrmemid: 0, exeDate, null, dest, result, url);
		}
	}
	
	private int getScheduleType(JobDetail jobDetail){
		int scheduletype=0;
		if(this.isNotBackupNowJob(jobDetail)){
			
				scheduletype=this.getPeriodFlag(jobDetail);
			
		}else 
			scheduletype=-1;
		return scheduletype;
	}
	

	private static boolean isPeriod(JobDetail jobDetail) {
		if (jobDetail == null)
			return false;

		boolean isDaily = false, isWeekly = false, isMonthly = false;

		if (jobDetail.getJobDataMap().containsKey("isDaily")) {
			isDaily = jobDetail.getJobDataMap().getBoolean("isDaily");
		}
		if (jobDetail.getJobDataMap().containsKey("isWeekly")) {
			isWeekly = jobDetail.getJobDataMap().getBoolean("isWeekly");
		}
		if (jobDetail.getJobDataMap().containsKey("isMonthly")) {
			isMonthly = jobDetail.getJobDataMap().getBoolean("isMonthly");
		}
		return isDaily || isWeekly || isMonthly;
	}

	private static void makeupJob(int jobStatus, JobDetailImpl jobDetail) {
		if (jobDetail.getName() != null && jobDetail.getName().endsWith(BaseService.JOB_NAME_BACKUP_NOW_SUFFIX)) {
			logger.debug("we exclude the now job, and retry job, just include the scheduled jobs");
			return;
		}
		logger.info("try to makeup job:" + jobDetail.getFullName() + ", status:" + jobStatus);

		String oldName = jobDetail.getFullName();

		RetryPolicy policy = BackupService.getInstance().getRetryPolicy(CommonService.RETRY_BACKUP);
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		// condition 1, if enabled or not
		if (!(policy.isEnabled() && policy.isMissedEnabled())) {

			nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY,
					new String[] { WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_DISABLED), "", "", "", "" });
			logger.debug("makeupBackup() - end with disabled retry policy for missed(s) backup " + jobStatus);
			return;
		}

		// condition 2, if now is within 15 minutes toward to next
		// backup event
		try {
			NextScheduleEvent nextScheduleEvent = BackupService.getInstance().getNextScheduleEvent();
			if (nextScheduleEvent != null) {
				Date nextDate = nextScheduleEvent.getDate();
				java.util.Calendar serverCalendar = new FlashServiceImpl().getServerCalendar();
				serverCalendar.setTimeInMillis(nextDate.getTime());

				java.util.Calendar cal = new FlashServiceImpl().getServerCalendar();
				cal.add(java.util.Calendar.MINUTE, policy.getNearToNextEvent());
				if (cal.after(serverCalendar)) {

					String time = BackupConverterUtil.dateToString(nextDate);
					nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY,
							new String[] { WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SKIPPED_NEXT, time, getRetryJob(jobStatus)), "", "",
									"", "" });
					logger.debug("makeupMissedBackup() - end with too near to next event");
					return;
				}
			}

		} catch (ServiceException e) {
			logger.error("makeupMissedBackup() - end", e);
		}
		// condition 3, if no running job
		if (nativeFacade.checkJobExist()) {
			logger.debug("makeupMissedBackup() - end with running job");
			nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY,
					new String[] { WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SKIPPED_RUNNING, getRetryJob(jobStatus)), "", "", "", "" });
			return;
		}
		// condition 4, if schedule backup is missed
		scheduleMissedScheduleJob(jobStatus, jobDetail, oldName);
	}

	// private static String backupIndicatorToName(String backupIndicator) {
	// if(BaseService.JOB_NAME_BACKUP_FULL.equals(backupIndicator)
	// || backupIndicator.endsWith(BaseService.JOB_NAME_BACKUP_FULL))
	// return WebServiceMessages.getResource("BackupTypeFull");
	// else if(BaseService.JOB_NAME_BACKUP_INCREMENTAL.equals(backupIndicator)
	// || backupIndicator.endsWith(BaseService.JOB_NAME_BACKUP_INCREMENTAL))
	// return WebServiceMessages.getResource("BackupTypeIncremental");
	// else if(BaseService.JOB_NAME_BACKUP_RESYNC.equals(backupIndicator)
	// || backupIndicator.endsWith(BaseService.JOB_NAME_BACKUP_RESYNC))
	// return WebServiceMessages.getResource("BackupTypeResync");
	//
	// return backupIndicator;
	// }

	private static String getRetryJob(int jobStatus) {
		String jobName = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED);
		switch (jobStatus) {
		case missedJob:
			jobName = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED);
			break;
		case skippedJob:
			jobName = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_SKIPPED);
			break;
		default:
			break;
		}

		return jobName;
	}

	public static void makeupSkippedBackup() {
		logger.debug("makeupSkippedBackup() - begin");
		JobDetail jobDetail = null;
		synchronized (currentJobLock) {
			jobDetail = currentJob.getJobDetail();
		}

		makeupJob(skippedJob, (JobDetailImpl)jobDetail);

		logger.debug("makeupSkippedBackup() - end");
	}

	public static void makeupMissedBackup() {
		if (isPlanPaused()) {
			logger.debug("Do not makeup missed backup job because plan is paused.");
			return;
		}

		logger.debug("makeupMissedBackup() - begin");
		JobDetailImpl jobDetail = null;
		synchronized (missJobLock) {
			if (missedJobContext == null) {
				logger.debug("no missed job");
				return;
			} else {
				jobDetail = (JobDetailImpl)missedJobContext.getJobDetail();
				missedJobContext = null;
			}
		}

		if (isPeriod(jobDetail)) {
			makeupPeriodJob(missedJob, jobDetail);
		} else {
			makeupJob(missedJob, jobDetail);
		}

		logger.debug("makeupMissedBackup() - end");
	}

	private static void makeupPeriodJob(int jobStatus, JobDetailImpl jobDetail) {
		if (jobDetail.getName() != null && jobDetail.getName().endsWith(BaseService.JOB_NAME_BACKUP_NOW_SUFFIX)) {
			logger.debug("we exclude the now job, and retry job, just include the scheduled jobs");
			return;
		}
		logger.info("try to makeup Period job:" + jobDetail.getFullName() + ", status:" + jobStatus);

		String oldName = jobDetail.getFullName();

		RetryPolicy policy = BackupService.getInstance().getRetryPolicy(CommonService.RETRY_BACKUP);

		// condition 1, if enabled or not
		if (!(policy.isEnabled() && policy.isMissedEnabled())) {

			BackupService
					.getInstance()
					.getNativeFacade()
					.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY,
							new String[] { WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_DISABLED), "", "", "", "" });
			logger.debug("makeupBackup() - end with disabled retry policy for missed(s) backup " + jobStatus);
			return;
		}
		// condition 2, if schedule backup is missed
		scheduleMissedScheduleJob(jobStatus, jobDetail, oldName);
	}

	private static void scheduleMissedScheduleJob(int jobStatus, JobDetailImpl jobDetail, String oldName) {
		try {
			String jobTypeName = jobDetail.getName();

			// liuwe05 2011-5-25 fix Issue: 20300004 Title: JOB TYPE ERROR-MAIL
			// ALERT
			// should not use display name as the job name, otherwise we cannot
			// identify the job method from job name. see method
			// getJobMethodFromJobDetail()
			// jobDetail.setName(WebServiceMessages.getResource("BackupStatusMissed")
			// +" "+ BackupConverterUtil.backupIndicatorToName(jobTypeName));
			// jobDetail.setName(WebServiceMessages.getResource("BackupStatusMissed")
			// +" "+ jobTypeName);
			String status = getRetryJob(jobStatus);
			if (!jobTypeName.startsWith(status)) {
				jobDetail.setName(status + " " + jobTypeName);
			} else {
				jobTypeName = jobTypeName.substring(status.length() + 1);
			}
			SimpleTriggerImpl trig = new SimpleTriggerImpl();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 1);
			Date startDate = cal.getTime();
			trig.setStartTime(startDate);
			trig.setRepeatCount(0);
			trig.setRepeatInterval(0);
			trig.setName(jobDetail.getName() + "Trigger");
			BackupService.getInstance().getBackupSchedule().scheduleJob(jobDetail, trig);

			logger.info("makeup for Missed Backup job is scheduled, origName: " + oldName + ", new Name:" + jobDetail.getName() + ", jobTypeName:"
					+ jobTypeName);

			String msg = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SCHEDULETYPE_SCHEDULED, getRetryJob(jobStatus),
					WebServiceMessages.getResource(Constants.regular), BackupConverterUtil.backupIndicatorToName(jobTypeName));

			boolean isDaily = false;
			if (jobDetail.getJobDataMap().containsKey("isDaily")) {
				isDaily = jobDetail.getJobDataMap().getBoolean("isDaily");
				logger.info("isDaily makeup:" + isDaily);
				msg = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SCHEDULETYPE_SCHEDULED, getRetryJob(jobStatus),
						WebServiceMessages.getResource(Constants.daily), BackupConverterUtil.backupIndicatorToName(jobTypeName));
			}

			boolean isWeekly = false;
			if (jobDetail.getJobDataMap().containsKey("isWeekly")) {
				isWeekly = jobDetail.getJobDataMap().getBoolean("isWeekly");
				logger.info("isWeekly makeup:" + isWeekly);
				msg = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SCHEDULETYPE_SCHEDULED, getRetryJob(jobStatus),
						WebServiceMessages.getResource(Constants.weekly), BackupConverterUtil.backupIndicatorToName(jobTypeName));
			}

			boolean isMonthly = false;
			if (jobDetail.getJobDataMap().containsKey("isMonthly")) {
				isMonthly = jobDetail.getJobDataMap().getBoolean("isMonthly");
				logger.info("isMonthly makeup:" + isMonthly);
				msg = WebServiceMessages.getResource(Constants.RETRYPOLICY_FOR_MISSED_SCHEDULETYPE_SCHEDULED, getRetryJob(jobStatus),
						WebServiceMessages.getResource(Constants.monthly), BackupConverterUtil.backupIndicatorToName(jobTypeName));
			}

			logger.info("make up message:" + msg);

			BackupService.getInstance().getNativeFacade()
					.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY, new String[] { msg, "", "", "", "" });

		} catch (SchedulerException e) {
			logger.error("makeupMissedBackup() - end " + e.getMessage(), e);
		}
	}

	public static boolean isJobRunning() {
		return isJobRunning;
	}

	/**
	 * Get the job lock.
	 * 
	 * @return true if successfully getting the lock.
	 */
	protected boolean getJobLock() {
		// we need to lock the class here, since for any type backup job, we
		// only allow one.
		synchronized (BaseBackupJob.class) {
			if (isJobRunning)
				return false;

			isJobRunning = true;
			return true;
		}
	}

	protected void releaseJobLock() {
		synchronized (BaseBackupJob.class) {
			isJobRunning = false;
		}
	}

	protected synchronized boolean getJobMonitorEdgeThreadLock() {
		if (JobMonitorEdgeThreadStarted)
			return false;

		JobMonitorEdgeThreadStarted = true;
		return true;
	}

	protected synchronized void releaseJobMonitorEdgeThreadLock() {
		JobMonitorEdgeThreadStarted = false;
	}

	@Override
	protected long getDefaultJobType() {
		return JobType.JOBTYPE_BACKUP;
	}

	protected int getJobMethod() {
		return BackupType.Full;
	}

	@Override
	protected JJobHistory getJobHistory(int jobType) {
		JJobHistory jobHistory = super.getJobHistory(jobType);
		jobHistory.setJobMethod(getJobMethod());
		return jobHistory;
	}

	private static boolean isPlanPaused() {
		BackupConfiguration conf = null;
		try {
			conf = BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e) {
			logger.error("Can not get backup configuration.");
		}
		if (conf != null && conf.isDisablePlan())
			return true;

		return false;
	}
}
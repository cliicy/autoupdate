package com.ca.arcflash.webservice.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.AbstractTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4D2D;
import com.ca.arcflash.service.common.WebServiceErrorMessages;
import com.ca.arcflash.service.data.PeriodRetentionValue;
import com.ca.arcflash.service.jni.model.JNetConnInfo;
import com.ca.arcflash.service.jni.model.JProtectionInfo;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.common.ConfigRPSInD2DService;
import com.ca.arcflash.webservice.common.VolumnMapAdapter;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.BackupInformationSummary;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.DestinationCapacity;
import com.ca.arcflash.webservice.data.LicInfo;
import com.ca.arcflash.webservice.data.NextScheduleEvent;
import com.ca.arcflash.webservice.data.ProtectionInformation;
import com.ca.arcflash.webservice.data.RPSDataStoreInfo;
import com.ca.arcflash.webservice.data.RecoveryInfoStatistics;
import com.ca.arcflash.webservice.data.RecoveryPointSummary;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveJobInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveScheduleStatus;
import com.ca.arcflash.webservice.data.archive.JArchiveJob;
import com.ca.arcflash.webservice.data.backup.Account;
import com.ca.arcflash.webservice.data.backup.ApplicationComponent;
import com.ca.arcflash.webservice.data.backup.ApplicationWriter;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.BackupSchedule;
import com.ca.arcflash.webservice.data.backup.BackupStatus;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.backup.BackupVolumes;
import com.ca.arcflash.webservice.data.backup.RetryPolicy;
import com.ca.arcflash.webservice.data.backup.RpsPolicy4D2D;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.job.rps.BackupJobArg;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.d2dstatus.SyncD2DStatusService;
import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyApplyerFactory;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyCheckStatus;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyQueryStatus;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.jni.model.JApplicationComponect;
import com.ca.arcflash.webservice.jni.model.JApplicationWriter;
import com.ca.arcflash.webservice.jni.model.JBackupDestinationInfo;
import com.ca.arcflash.webservice.jni.model.JBackupInfo;
import com.ca.arcflash.webservice.jni.model.JBackupInfoSummary;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.jni.model.JObjRet;
import com.ca.arcflash.webservice.scheduler.BaseBackupJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.FullBackupJob;
import com.ca.arcflash.webservice.scheduler.IncrementalBackupJob;
import com.ca.arcflash.webservice.scheduler.MakeupProcessor;
import com.ca.arcflash.webservice.scheduler.ResyncBackupJob;
import com.ca.arcflash.webservice.service.internal.BackupConfigurationXMLDAO;
import com.ca.arcflash.webservice.service.internal.DestinationInformationConverter;
import com.ca.arcflash.webservice.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.webservice.service.internal.RetryPolicyXMLDAO;
import com.ca.arcflash.webservice.service.rps.RPSServiceProxyManager;
import com.ca.arcflash.webservice.service.rps.SettingsService;
import com.ca.arcflash.webservice.service.validator.ArchiveConfigurationValidator;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcflash.webservice.util.ArchiveUtil;
import com.ca.arcflash.webservice.util.AsyncTaskRunner;
import com.ca.arcflash.webservice.util.DSTUtils;
import com.ca.arcflash.webservice.util.EmailSender;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

public final class BackupService extends AbstractBackupService {
	private static final Logger logger = Logger.getLogger(BackupService.class);
	private static final BackupService instance = new BackupService();
	private BackupConfigurationXMLDAO backupConfigurationXMLDAO = new BackupConfigurationXMLDAO();
	private RetryPolicyXMLDAO retryPolicyXMLDAO = new RetryPolicyXMLDAO();
	private ArchiveConfigurationValidator archiveConfigurationValidator = new ArchiveConfigurationValidator();
	private DestinationInformationConverter destInfoConverter = new DestinationInformationConverter();
	private BackupConfiguration backupConfiguration;
	private BackupConfiguration protectConfiguration;
	private JNetConnInfo baseConnection = null;
	private boolean updateConnectionPrev = true;
	private RpsPolicy4D2D currentRPSPolicy;
	private final String D2D_VM_ISNTANCEUUID = null;
	private Set<String> jobNames = new HashSet<String>();

	private long throttling = 0;
	
	public class DisableVolumeInfo {
		private String volumeName;
		private int subVolumeStatus;

		public String getVolumeName() {
			return volumeName;
		}

		public void setVolumeName(String volumeName) {
			this.volumeName = volumeName;
		}

		public int getSubVolumeStatus() {
			return subVolumeStatus;
		}

		public void setSubVolumeStatus(int subVolumeStatus) {
			this.subVolumeStatus = subVolumeStatus;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof DisableVolumeInfo) {
				return ((DisableVolumeInfo) o).getVolumeName().equals(
						volumeName);
			}
			return false;
		}
		
		@Override
		public String toString() {
			return volumeName;
		}
	}

	private BackupService() {
		try {
			otherScheduler = StdSchedulerFactory.getDefaultScheduler();
			otherScheduler.start();
		} catch (SchedulerException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}

		getBackupSchedule();
	}

	public static BackupService getInstance() {
		return instance;
	}

	private void saveBackupStartTimeByD2DTime(BackupConfiguration configuration) {
		if (configuration.getStartTime() != null && configuration.getStartTime().getYear() > 1900) {
			configuration.setBackupStartTime(getCalTimeInMillies(configuration.getStartTime()));
		}
	}

	public long saveBackupConfiguration(BackupConfiguration configuration) throws ServiceException {
		return saveBackupConfiguration(configuration, false, null);
	}

	private void checkAdminAccount(BackupConfiguration configuration) throws ServiceException {
		boolean hasNewAccountInfo = ((configuration.getAdminUserName() != null) && !configuration.getAdminUserName().trim().isEmpty());

		if (hasNewAccountInfo) {
			Account account = new Account();
			account.setUserName(configuration.getAdminUserName() == null ? "" : configuration.getAdminUserName());
			account.setPassword(configuration.getAdminPassword() == null ? "" : configuration.getAdminPassword());
			getNativeFacade().validateAdminAccount(account);
			getNativeFacade().saveAdminAccount(account);
		} else // use old account
		{
			Account account = getNativeFacade().getAdminAccount();
			configuration.setAdminUserName(account.getUserName());
			configuration.setAdminPassword(account.getPassword());
		}
	}

	public List<DisableVolumeInfo> getVolumesCannotBackup(
			BackupConfiguration configuration) throws ServiceException {
		List<String> volumes = new ArrayList<String>();
		if (configuration == null)
			return new ArrayList<DisableVolumeInfo>();
		else {
			VolumnMapAdapter.convertBackupVolumes(configuration);
			BackupVolumes bkpVolumes = configuration.getBackupVolumes();
			if (bkpVolumes != null && !bkpVolumes.isFullMachine()) {
				volumes = bkpVolumes.getVolumes();
			}
			Volume[] localVolumes = BrowserService.getInstance().getVolumes(
					true, null, null, null);
			List<DisableVolumeInfo> disablelist = new ArrayList<DisableVolumeInfo>();
			for (String volname : volumes) {
				Volume findVolume = null;
				for (Volume lv : localVolumes) {
					String lvname = lv.getName();
					if (lvname.endsWith("\\")) {
						lvname = lvname.substring(0, lvname.length() - 1);
					}
					if (volname.equalsIgnoreCase(lvname)) {
						findVolume = lv;
						break;
					}
				}
				if (findVolume == null) {
					DisableVolumeInfo disableVolumeInfo = new DisableVolumeInfo();
					disableVolumeInfo.setVolumeName(volname);
					disablelist.add(disableVolumeInfo);
				} else if (!findVolume.isCanBackup()) {
					DisableVolumeInfo disableVolumeInfo = new DisableVolumeInfo();
					disableVolumeInfo.setVolumeName(volname);
					disableVolumeInfo.setSubVolumeStatus(findVolume
							.getSubStatus());
					disablelist.add(disableVolumeInfo);
				}
			}

			return disablelist;
		}
	}
	/**
	 * 
	 * @param configuration
	 * @param forUpgrade
	 *            : true only for upgrade from build before 16.5
	 * @return
	 * @throws ServiceException
	 */
	private long saveBackupConfiguration(BackupConfiguration configuration, boolean forUpgrade, RpsPolicy4D2D policy) throws ServiceException {
		logger.info("saveBackupConfiguration(BackupConfiguration) - start, at " + new Date());
		if (logger.isDebugEnabled()) {
			logger.debug(StringUtil.convertObject2String(configuration));
			if (configuration != null) {
				logger.debug(StringUtil.convertObject2String(configuration.getEmail()));
				logger.debug(StringUtil.convertObject2String(configuration.getFullBackupSchedule()));
				logger.debug(StringUtil.convertObject2String(configuration.getIncrementalBackupSchedule()));
				logger.debug(StringUtil.convertObject2String(configuration.getResyncBackupSchedule()));
				logger.debug(StringUtil.convertObject2String(configuration.getBackupRpsDestSetting()));
			}
		}

		// validate
		if (policy == null)
			policy = backupConfigurationValidator.validateRpsDestSetting(configuration);
		if (policy != null) {
			this.updateBackupConfiguration4RPSPolicy(policy, configuration);
			BackupConfiguration oldConfiguration = this.getBackupConfiguration();
			this.updateBackupDestChange4RPS(oldConfiguration, configuration);
		}
		checkAdminAccount(configuration);

		int pathMaxWithoutHostName = backupConfigurationValidator.validate(configuration);

		// verify destination threshold value
		
		verifyDestThresholdValue(configuration);

		// check source volume
		VolumnMapAdapter.checkSourceVolumes(configuration);

		// Save backup starttime milliseconds by passed D2D time
		// to avoid potential errors of UI time covertion to server time
		saveBackupStartTimeByD2DTime(configuration);
		boolean isManagedByD2D = configuration.isD2dOrRPSDestType();
		if (isManagedByD2D) {
			makeupRetentionPolicy(configuration);
		}
		// wanqi06
		VolumnMapAdapter.convertBackupVolumes(configuration);

		try {
			synchronized (lock) {
				try {
					String originalDest = configuration.getDestination();
					JObjRet<String> result = appendHostNameAndSIDIfNeeded(
							originalDest, null, configuration.getUserName(),
							configuration.getPassword(),
							configuration.getChangedBackupDestType(),
							!isManagedByD2D);
					if (result.getRetCode() == BackupServiceErrorCode.WARN_FolderWithSIDExist) {
						String msg = String.format(WebServiceMessages
								.getResource("backupConfigFolderWithSIDEixst"),
								result.getItem());
						getNativeFacade().addLogActivity(
								Constants.AFRES_AFALOG_WARNING,
								Constants.AFRES_AFJWBS_GENERAL,
								new String[] { msg, "", "",			"", "" });
					}
					
					String dest = result.getItem();
					if (!StringUtil.isEmptyOrNull(dest) && !StringUtil.isEmptyOrNull(originalDest) && dest.length() > originalDest.length()) {
						// int maxLength = pathMaxWithoutHostName -
						// BackupConfigurationValidator.WINDOWS_HOST_NAME_MAX_LENGTH;
						int backslash = 1;
						if (originalDest.endsWith("\\") || originalDest.endsWith("/")) {
							backslash = 0;
						}
						if (dest.length() > (pathMaxWithoutHostName + backslash))
							backupConfigurationValidator.generatePathExeedLimitException(pathMaxWithoutHostName);
					}

					// if d2d is manged by itself, validate changed retention
					// policy
					if (isManagedByD2D) {
						this.validateChangedRetentionPolicy(configuration, dest);
					}
					// validating whether same destination is used for archive
					ArchiveConfiguration archiveconfig = ArchiveService.getInstance().getArchiveConfigurationAlreadyDefined();
					String strArchivedest = archiveconfig != null ? archiveconfig.getStrArchiveToDrivePath() : null;
					if (strArchivedest != null) {
						if (dest.compareToIgnoreCase(strArchivedest) == 0) {
							throw new ServiceException(FlashServiceErrorCode.BackupConfig_ERR_DEST_SAME_ARCHIVEDEST);
						}
					}
					// validating done
					configuration.setDestination(dest);

					long ret = 0;

					BackupConfiguration oldConfiguration = this.getBackupConfiguration();
					
					/**
					 * We need unregister old RPS before register to new RPS
					 * When old RPS and new RPS point to the same machine, unregister will remove register information
					 */
					unRegistryD2DIfNeed(configuration, oldConfiguration);
					
					if (!isManagedByD2D) {
						// save rps setting
						saveRPSSetting(configuration);
					}
					
					try {
						if (oldConfiguration == null) {
							CONN_INFO newConnection = getCONN_INFO(configuration);
							CONN_INFO connection = new CONN_INFO();
							ret = getNativeFacade().initBackupDestination(configuration.getDestination(), newConnection.getDomain(),
									newConnection.getUserName(), newConnection.getPwd(), null, connection.getDomain(), connection.getUserName(),
									connection.getPwd(), configuration.getChangedBackupDestType());
						} else {
							String newRPSPolicyUUID = configuration.getBackupRpsDestSetting().getRPSPolicyUUID();
							String oldRPSPolicyUUID = oldConfiguration.getBackupRpsDestSetting().getRPSPolicyUUID();
							boolean isRPSPolicyChanged = !StringUtil.isEmptyOrNull(newRPSPolicyUUID) 
														&& !StringUtil.isEmptyOrNull(oldRPSPolicyUUID)
														&& !newRPSPolicyUUID.equalsIgnoreCase(oldRPSPolicyUUID);
							if(configuration.isChangedBackupDest() || isRPSPolicyChanged){
								String domainName = "";
								String userName = "";
								String password = "";
								if(oldConfiguration.isD2dOrRPSDestType()){
									domainName = ArchiveUtil.getDomainName(oldConfiguration.getUserName());
									userName = ArchiveUtil.getUserName(oldConfiguration.getUserName());
									password = oldConfiguration.getPassword() == null ? "" : oldConfiguration.getPassword();
								} else {
									RpsHost rps = oldConfiguration.getBackupRpsDestSetting().getRpsHost();
									domainName = ArchiveUtil.getDomainName(rps.getUsername());
									userName = ArchiveUtil.getUserName(rps.getUsername());
									password = rps.getPassword() == null ? "" : rps.getPassword();
								}
								ArchiveService.getInstance().deleteAllPendingFileCopyJobs(oldConfiguration.getDestination(), 
										domainName, userName, password);
							}
							
							String oldDest = oldConfiguration.getDestination();
							if (oldDest.endsWith("\\") || oldDest.endsWith("/")) {
								oldDest = oldDest.substring(0, oldDest.length() - 1);
							}

							if (dest.endsWith("\\") || dest.endsWith("/")) {
								dest = dest.substring(0, dest.length() - 1);
							}
							if (oldDest.equalsIgnoreCase(dest)) {
								// logger.debug("new dest:" + dest);
								// logger.debug("old dest:" + oldDest);
								logger.debug("Is Dest Chagned: false");
								// do nothing.
							} else {
								// logger.debug("Is Dest Chagned:"
								// + configuration.isChangedBackupDest());
								if (configuration.isChangedBackupDest()) {
									CONN_INFO newConnection = getCONN_INFO(configuration);
									CONN_INFO connection = getCONN_INFO(oldConfiguration);
									ret = this.getNativeFacade().initBackupDestination(configuration.getDestination(), newConnection.getDomain(),
											newConnection.getUserName(), newConnection.getPwd(), oldConfiguration.getDestination(), connection.getDomain(),
											connection.getUserName(), connection.getPwd(), configuration.getChangedBackupDestType());
								}
							}
						}
					} catch (ServiceException e) {
						if (!isManagedByD2D)
							unregistryD2D2RPS(configuration);
						throw e;
					}

					if (ret != 0) {
						return ret;
					}
				} catch (ServiceException ex) {
					logger.error(ex.getMessage(), ex);
					throw ex;
				}

				if (backupConfiguration != null) {
					configuration.setHasSendEmail(backupConfiguration.isHasSendEmail());
					VersionInfo versionInfo = CommonService.getInstance().getVersionInfo();
					if (versionInfo != null) {
						configuration.setMajorVersion(versionInfo.getMajorVersion());
						configuration.setMinorVersion(versionInfo.getMinorVersion());
					}
				}
				// banar02: Updating APMSettings.Ini file
				// selfUpdateConfiguration.Write(ServiceContext.getInstance()
				// .getApmSettingsIniFilePath(),
				// configuration.getUpdateSettings());
				//
				// wanqi06
				AdvanceSchedule advanceSchedule = configuration.getAdvanceSchedule();
				if (advanceSchedule != null && advanceSchedule.getScheduleStartTime() <= 0) {
					advanceSchedule.setScheduleStartTime(System.currentTimeMillis());
					// shaji02
					backupConfigurationValidator.validateAdvanceSchedule(advanceSchedule);
				}

				backupConfigurationXMLDAO.save(ServiceContext.getInstance().getBackupConfigurationFilePath(), configuration);
				// fanda03 fix 102889; the preallocation parameter is passed to
				// backend from job script , not set into afstore.ini
				// getNativeFacade().setPreAllocSpacePercent(
				// configuration.getPreAllocationBackupSpace());
				backupConfiguration = configuration;
				protectConfiguration = configuration;

				// Save threshold
				saveThreshhold();

				// Create the desktop.ini file after the gui select the
				// destination path.
				createDesktopINI();

				// this.configJobSchedule();
				this.configSchedule(D2D_VM_ISNTANCEUUID);

				AsyncTaskRunner.submit(BackupThrottleService.getInstance(), BackupThrottleService.class.getMethod("scheduleBackupThrottleJob"));

				AsyncTaskRunner.submit(VSphereBackupThrottleService.getInstance(), VSphereBackupThrottleService.class.getMethod("startImmediateTrigger"));

				if (!forUpgrade) {
					if (configuration.getRetentionPolicy() != null && configuration.getRetentionPolicy().isUseBackupSet()) {
						AsyncTaskRunner.submit(BackupSetService.getInstance(),
								BackupSetService.class.getMethod("markBackupSetFlag", BackupConfiguration.class), configuration);
					} else
						AsyncTaskRunner.submit(MergeService.getInstance(), MergeService.class.getMethod("scheduleMergeJob"));
				}
				// sync d2d status after destination changed
				SyncD2DStatusService.getInstance().syncD2DStatus2Edge(CommonService.getInstance().getNodeUUID());
			}
			logger.info("saveBackupConfiguration(BackupConfiguration) - end");
			return 0;
		} catch (Exception e) {
			logger.error("saveBackupConfiguration()", e);
			if (e instanceof ServiceException) {
				throw (ServiceException) e;
			} else {
				throw generateInternalErrorAxisFault();
			}
		}

	}

	public long updateBackupConfiguration(BackupConfiguration configuration) throws ServiceException {
		synchronized (lock) {
			if (backupConfiguration == null)
				return -1;

			return saveBackupConfiguration(configuration);
		}
	}

	private void makeupRetentionPolicy(BackupConfiguration backupConfig) {
		if (backupConfig.getRetentionPolicy() == null) {
			RetentionPolicy policy = new RetentionPolicy();
			backupConfig.setRetentionPolicy(policy);
		}
	}

	public long validateBackupConfiguration(BackupConfiguration configuration) throws ServiceException {
		logger.debug("validateBackupConfiguration(BackupConfiguration) - start");
		if (logger.isDebugEnabled()) {
			logger.debug(StringUtil.convertObject2String(configuration));
			if (configuration != null) {
				logger.debug(StringUtil.convertObject2String(configuration.getEmail()));
				logger.debug(StringUtil.convertObject2String(configuration.getFullBackupSchedule()));
				logger.debug(StringUtil.convertObject2String(configuration.getIncrementalBackupSchedule()));
				logger.debug(StringUtil.convertObject2String(configuration.getResyncBackupSchedule()));
			}
		}
		RpsPolicy4D2D policy = backupConfigurationValidator.validateRpsDestSetting(configuration);
		if (policy != null) {
			this.updateBackupConfiguration4RPSPolicy(policy, configuration);
		}
		checkAdminAccount(configuration);
		// validate
		int pathMaxWithoutHostName = backupConfigurationValidator.validate(configuration);
		// verify destination threshold value
		verifyDestThresholdValue(configuration);

		CommonService.getInstance().validateBackupStartTime(configuration.getStartTime());
		makeupRetentionPolicy(configuration);

		try {
			synchronized (lock) {

				try {

					String originalDest = configuration.getDestination();
					boolean isRPS = !configuration.isD2dOrRPSDestType();

					JObjRet<String> result = appendHostNameAndSIDIfNeeded(
							originalDest, null, configuration.getUserName(),
							configuration.getPassword(),
							configuration.getChangedBackupDestType(), isRPS);
					String dest = result.getItem();
					if (!StringUtil.isEmptyOrNull(dest)
							&& !StringUtil.isEmptyOrNull(originalDest)
							&& dest.length() > originalDest.length()) {
						// int maxLength = pathMaxWithoutHostName -
						// BackupConfigurationValidator.WINDOWS_HOST_NAME_MAX_LENGTH;
						int backslash = 1;
						if (originalDest.endsWith("\\")
								|| originalDest.endsWith("/")) {
							backslash = 0;
						}
						if (dest.length() > (pathMaxWithoutHostName + backslash))
							backupConfigurationValidator
									.generatePathExeedLimitException(pathMaxWithoutHostName);
					}
					this.validateChangedRetentionPolicy(configuration, dest);

					// validating whether same destination is used for archive
					ArchiveConfiguration archiveconfig = ArchiveService.getInstance().getArchiveConfigurationAlreadyDefined();
					String strArchivedest = archiveconfig != null ? archiveconfig.getStrArchiveToDrivePath() : null;
					if (strArchivedest != null) {
						if (dest.compareToIgnoreCase(strArchivedest) == 0) {
							throw new ServiceException(FlashServiceErrorCode.BackupConfig_ERR_DEST_SAME_ARCHIVEDEST);
						}
					}
					// validating done

					// configuration.setDestination(dest);

					/*
					 * boolean hasNewAccountInfo =
					 * ((configuration.getAdminUserName() != null) &&
					 * !configuration.getAdminUserName().trim().isEmpty());
					 * 
					 * if (hasNewAccountInfo) { Account account = new Account();
					 * account.setUserName(configuration.getAdminUserName() ==
					 * null ? "" : configuration.getAdminUserName());
					 * account.setPassword(configuration.getAdminPassword() ==
					 * null ? "" : configuration.getAdminPassword());
					 * getNativeFacade().validateAdminAccount(account);
					 * //getNativeFacade().saveAdminAccount(account); }
					 */

				} catch (ServiceException ex) {
					logger.error(ex.getMessage(), ex);
					throw ex;
				}

			}

			logger.debug("validateBackupConfiguration(BackupConfiguration) - end");
			return 0;
		} catch (Exception e) {
			logger.error("validateBackupConfiguration()", e);
			if (e instanceof ServiceException) {
				throw (ServiceException) e;
			} else {
				throw generateInternalErrorAxisFault();
			}
		}

	}

	private void validateChangedRetentionPolicy(BackupConfiguration configuration, String newdest) throws ServiceException {
		if (this.backupConfiguration == null)
			return;
		if (backupConfiguration.getRetentionPolicy() != null) {
			if(!configuration.isD2dOrRPSDestType()){// destination is a data store
				return;
			}else if (backupConfiguration.getRetentionPolicy().isUseBackupSet() && configuration.getRetentionPolicy().isUseBackupSet()
					|| !backupConfiguration.getRetentionPolicy().isUseBackupSet() && !configuration.getRetentionPolicy().isUseBackupSet()) {
				return;
			} else {
				if (!backupConfigurationValidator.isCleanDestination(newdest, configuration.getUserName(), configuration.getPassword())) {
					throw new ServiceException(WebServiceMessages.getResource("backupConfRetentionChangeOldDest"),
							FlashServiceErrorCode.BackupConfig_ERR_RETENTION_CHANGE);
				} else if (configuration.getChangedBackupDestType() != BackupType.Full) {
					configuration.setChangedBackupDestType(BackupType.Full);
				}
			}
		} else if (configuration.getRetentionPolicy() != null && configuration.getRetentionPolicy().isUseBackupSet()) {
			if (!backupConfigurationValidator.isCleanDestination(newdest, configuration.getUserName(), configuration.getPassword())) {
				throw new ServiceException(WebServiceMessages.getResource("backupConfRetentionChangeOldDest"),
						FlashServiceErrorCode.BackupConfig_ERR_RETENTION_CHANGE);
			} else if (configuration.getChangedBackupDestType() != BackupType.Full) {
				configuration.setChangedBackupDestType(BackupType.Full);
			}
		}
	}

	private void saveThreshhold() {
		try {
			if (!backupConfiguration.isEnableSpaceNotification()) {
				this.getNativeFacade().saveThreshold(0L);
				return;
			}

			if ("MB".equals(backupConfiguration.getSpaceMeasureUnit())) {
				this.getNativeFacade().saveThreshold((long) backupConfiguration.getSpaceMeasureNum());
			} else {
				DestinationCapacity diskCapacity = BackupService.getInstance().getBackupInformationSummary().getDestinationCapacity();
				long totalValumeSize = diskCapacity.getTotalVolumeSize();
				long threshold = (long) (totalValumeSize * backupConfiguration.getSpaceMeasureNum()) / (100 * 1024 * 1024);
				this.getNativeFacade().saveThreshold(threshold);

				logger.debug("totalValumeSize:" + totalValumeSize);
				// logger.debug("threshold:"+threshold);
				// logger.debug(totalValumeSize*
				// backupConfiguration.getSpaceMeasureNum());
			}

		} catch (Throwable e) {
			logger.error("saveBackupConfiguration()", e);
		}
	}

	public String appendHostNameIfNeeded(String destination, String serverName, String userName, String password, int changedBackupType)
			throws ServiceException {
		JObjRet<String> retObj = this.getNativeFacade().checkDestNeedHostName(destination, serverName, null, userName, password, true);

		logger.debug("JObjRet<String> - hostName:" + retObj.getItem() + ", retCode:" + retObj.getRetCode());

		if (retObj.getRetCode() == 0
				|| retObj.getRetCode() == BackupServiceErrorCode.WARN_FolderWithSIDExist) {
			String hostName = retObj.getItem();
			if (hostName != null && hostName.trim().length() > 0) {
				if (destination.endsWith("\\") || destination.endsWith("/")) {
					destination += hostName;
				} else {
					destination += "\\" + hostName;
				}
			}
		}
		// logger.debug("dest" + destination);
		return destination;
	}

	private JObjRet<String> appendHostNameAndSIDIfNeeded(String destination,
			String serverName, String userName, String password,
			int changedBackupType, boolean isRPS) throws ServiceException {

		JObjRet<String> retObj = this.getNativeFacade().checkDestNeedHostName(
				destination, serverName,
				isRPS ? CommonService.getInstance().getServerSID() : null,
				userName, password, true);

		logger.debug("JObjRet<String> - hostName:" + retObj.getItem()
				+ ", retCode:" + retObj.getRetCode());

		if (retObj.getRetCode() == 0
				|| retObj.getRetCode() == BackupServiceErrorCode.WARN_FolderWithSIDExist) {
			String hostName = retObj.getItem();
			if (hostName != null && hostName.trim().length() > 0) {
				if (destination.endsWith("\\") || destination.endsWith("/")) {
					destination += hostName;
				} else {
					destination += "\\" + hostName;
				}
			}
		}

		destination = appendSID4RPSToSupportMSP(destination, isRPS);

		retObj.setItem(destination);
		// logger.debug("dest" + destination);
		return retObj;
	}

	private String appendSID4RPSToSupportMSP(String dest, boolean isToRPS) throws ServiceException {
		boolean isBkpToRPSDataStore = isToRPS;

		if (isBkpToRPSDataStore) {
			String tmpDest = dest;

			if (tmpDest.endsWith("\\")) {
				tmpDest = tmpDest.substring(0, dest.length() - 1);
			}

			String d2dID = String.format("[%s]", CommonService.getInstance().getServerSID());
			if (!tmpDest.endsWith(d2dID)) {
				dest = tmpDest + d2dID;
			}
		}
		return dest;
	}

	public BackupConfiguration getBackupConfiguration() throws ServiceException {
		return getBackupConfiguration(D2D_VM_ISNTANCEUUID);
	}
	
	@Override
	public BackupConfiguration getBackupConfiguration(String vmInstanceUUID) throws ServiceException {
		logger.debug("getBackupConfiguration - start");

		try {
			synchronized (lock) {

				if (backupConfiguration == null) {
					if (!StringUtil.isExistingPath(ServiceContext.getInstance().getBackupConfigurationFilePath()))
						return null;

					backupConfiguration = backupConfigurationXMLDAO.get(ServiceContext.getInstance().getBackupConfigurationFilePath());
					try {
						if (backupConfiguration != null) {
							Account adminAccount = getNativeFacade().getAdminAccount();
							backupConfiguration.setAdminUserName(adminAccount.getUserName());
							backupConfiguration.setAdminPassword(adminAccount.getPassword());
						}
						// to avoid service break up.
					} catch (Throwable e) {
						logger.error("Can not get Administrator account:" + e.getMessage(), e);
					}
				}
				// Currently only verify usb hard disk
				verifyDestination();

				if (backupConfiguration != null) {
					// /fanda03 enhance 102889' we don't use afstore.ini
					// backupConfiguration.setPreAllocationBackupSpace(getPreAllocationValue());
					if (backupConfiguration.getRetentionPolicy() == null) {
						RetentionPolicy policy = new RetentionPolicy();
						backupConfiguration.setRetentionPolicy(policy);
					}
				}
			}

			logger.debug("getBackupConfiguration - end");
			return backupConfiguration;
		} catch (ServiceException ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		} catch (Exception e) {
			logger.error("getBackupConfiguration()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public boolean checkBackupConfigurationValid() {
		return backupConfiguration != null ? true : false;
	}

	public void reloadBackupConfiguration() throws ServiceException {
		logger.debug("reloadBackupConfiguration - start");

		try {
			synchronized (lock) {

				backupConfiguration = backupConfigurationXMLDAO.get(ServiceContext.getInstance().getBackupConfigurationFilePath());
				try {
					if (backupConfiguration != null) {
						Account adminAccount = getNativeFacade().getAdminAccount();
						backupConfiguration.setAdminUserName(adminAccount.getUserName());
						backupConfiguration.setAdminPassword(adminAccount.getPassword());
					}
					// to avoid service break up.
				} catch (Throwable e) {
					logger.error("Can not get Administrator account:" + e.getMessage(), e);
				}

			}

			logger.debug("reloadBackupConfiguration - end");
		} catch (ServiceException ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		} catch (Exception e) {
			logger.error("reloadBackupConfiguration()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public void cleanBackupConfiguration() {
		logger.debug("cleanBackupConfiguration - start");
		
		synchronized (lock) {
			this.unregistryD2D2RPS(backupConfiguration);
			backupConfiguration = null;
			cleanSchedule(D2D_VM_ISNTANCEUUID);
			unscheduleTriggers();
		}

		logger.debug("cleanBackupConfiguration - end");
	}
	
	@Override
	protected JobDetail generateJobDetail(String vmInstanceUUID, int backupType) {
		String jobName = JOB_NAME_BACKUP_FULL;
		Class backupClass = FullBackupJob.class;
		if (backupType == BackupType.Full) {
			jobName = JOB_NAME_BACKUP_FULL;
			backupClass = FullBackupJob.class;
		} else if (backupType == BackupType.Resync) {
			jobName = JOB_NAME_BACKUP_RESYNC;
			backupClass = ResyncBackupJob.class;
		} else if (backupType == BackupType.Incremental) {
			jobName = JOB_NAME_BACKUP_INCREMENTAL;
			backupClass = IncrementalBackupJob.class;
		}
		
		return new JobDetailImpl(jobName, JOB_GROUP_BACKUP_NAME, backupClass);
	}

	protected String getBackupJobGroupName(String vmInstanceUUID){
		return JOB_GROUP_BACKUP_NAME;
	}
	
	private void checkForBackupSetStart() throws ServiceException {
		if (BackupSetService.getInstance().isManuallyBackupSetStart(D2D_VM_ISNTANCEUUID)) {
			throw generateAxisFault(WebServiceMessages.getResource("covertManualJobToBackupSetWarning"), FlashServiceErrorCode.MERGE_CONVERT_MANUAL_JOB_FULL);
		}
	}

	public long backup(int type, String name, boolean convertForBackupSet) throws ServiceException{
		return backup(type,name,convertForBackupSet,0);
	}
	
	public long backup(int type, String name, boolean convertForBackupSet, int scheduletype) throws ServiceException {
		logger.debug("backup() - start");
		logger.debug("backup type:" + type);
		logger.debug("backup name:" + name);
		logger.debug("scheduletype:" + scheduletype);
		BackupConfiguration conf = this.getBackupConfiguration();

//		if (null != conf && conf.isDisablePlan()) {
//			logger.info("The backup job is disabled.");
//			throw generateAxisFault(FlashServiceErrorCode.Backup_BackupDisabled);
//		}

		if (StringUtil.isEmptyOrNull(name)) {
			logger.debug("no backup name, return error code");
			throw generateAxisFault(FlashServiceErrorCode.Backup_NoBackupName);
		}

		if (name.length() > 128) {
			logger.debug("backup name exceeds max length, return error code");
			throw generateAxisFault(FlashServiceErrorCode.Backup_BackupNameExceedMaxLength);
		}

		if (conf == null) {
			logger.debug("There is no backup configuration, return error code");
			throw generateAxisFault(FlashServiceErrorCode.Backup_NoBackupConfiguration);
		}

		// check whether there is running jobs
		if (this.getNativeFacade().checkJobExist() || BaseBackupJob.isJobRunning()) {
			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
		}

		// if it's not full backup, then we need to check whether need to
		// convert this
		// job to full and mark it as backup set start, if yes, we will ask user
		// whether he
		// want to do that
		// if it's full backup, we will mark it as backup set start as needed
		if (convertForBackupSet && type != BackupType.Full) {
			checkForBackupSetStart();
		}

		// check for RPS server and datastore status
		checkRPS4Backup();

		try {			
			String jobName = "";
			Class<? extends Job> jobClass = null;
			if (type == BackupType.Full)
				jobClass = FullBackupJob.class;
			else if (type == BackupType.Incremental)
				jobClass = IncrementalBackupJob.class;
			else if (type == BackupType.Resync)
				jobClass = ResyncBackupJob.class;
			else
				throw generateAxisFault(FlashServiceErrorCode.Backup_InvalidBackupType);

			try {
				jobName = jobClass.getSimpleName() + JOB_NAME_BACKUP_NOW_SUFFIX;
				JobDetailImpl jobDetail = new JobDetailImpl(jobName, null, jobClass);
				jobDetail.getJobDataMap().put("jobName", name);
				//if(manualstart){
					jobDetail.getJobDataMap().put("periodRetentionFlag", scheduletype);
					jobDetail.getJobDataMap().put("manualFlag", true);
//					jobDetail.getJobDataMap().put("periodRetentionFlag", 4);
//					jobDetail.getJobDataMap().put("isMonthly", true);
					switch(scheduletype){
						case PeriodRetentionValue.QJDTO_B_Backup_Daily:{jobDetail.getJobDataMap().put("isDaily", true);break;}
						case PeriodRetentionValue.QJDTO_B_Backup_Weekly:{jobDetail.getJobDataMap().put("isWeekly", true);break;}
						case PeriodRetentionValue.QJDTO_B_Backup_Monthly:{jobDetail.getJobDataMap().put("isMonthly", true);break;}
						default:break;
					}

				//}
				SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
				trigger.setName(jobDetail.getName() + "Trigger");
				// delete previous saved run once job.
				deleteJob(jobDetail);
				getBackupSchedule().scheduleJob(jobDetail, trigger);
			} catch (ObjectAlreadyExistsException e) {
				logger.info("There is already a scheduled immediate job existing", e);
				throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
			}

			checkForMergeRunning(ServiceUtils.jobType2String(Constants.AF_JOBTYPE_BACKUP, type));

			logger.debug("backup() - end");
			return 0;
		} catch (ServiceException se) {
			throw se;
		} catch (Throwable e) {
			logger.error("backup(int type, String name, boolean convertForBackupSet):" + type + "," + name + "," + convertForBackupSet, e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	

	

	private void deleteJob(JobDetailImpl jobDetail) {
		try {
			JobDetail jd = getBackupSchedule().getJobDetail(new JobKey(jobDetail.getName(), jobDetail.getGroup()));
			if (jd != null) {
				logger.info("Find an immediate job:" + jd.toString());
				getBackupSchedule().deleteJob(new JobKey(jobDetail.getName(), jobDetail.getGroup()));
			}
		} catch (Exception e) {
			logger.warn("Error occurs to delete immediate job from scheduler", e);
		}
	}

	public long backup(int type, String name) throws ServiceException {
		return backup(type, name, true);
	}

	public long backup(JJobScript backupJob) throws ServiceException {
		try {
			if (backupJob == null)
				return 0;
			return getNativeFacade().backup(backupJob);
		} catch (Throwable e) {
			logger.error("validateUser()", e);
			throw generateInternalErrorAxisFault();
		}
	}


	public BackupInformationSummary getBackupInformationSummary() throws ServiceException {
		logger.debug("getBackupInformationSummary() - start");

		try {
			BackupConfiguration configuration = this.getBackupConfiguration();
			if (configuration == null)
				return null;

			CONN_INFO info = getCONN_INFO(configuration);
			JBackupInfoSummary summary = this.getNativeFacade().GetBackupInfoSummary(configuration.getDestination(), info.getDomain(), info.getUserName(),
					info.getPwd(), false);

			BackupInformationSummary returnBackupInformationSummary = backupSummaryConverter.convert(summary);

			returnBackupInformationSummary.setAdvanced(configuration.getBackupDataFormat() > 0);
			returnBackupInformationSummary.setAdvanceSchedule(configuration.getAdvanceSchedule());
			if (configuration.getAdvanceSchedule() != null) {
				returnBackupInformationSummary.setPeriodEnabled(configuration.getAdvanceSchedule().isPeriodEnabled());
			}

			setRPSInfo(configuration, returnBackupInformationSummary);

			String mergeScheduleTime = MergeService.getInstance().getMergeScheduleTime();
			returnBackupInformationSummary.setMergeJobScheduleTime(mergeScheduleTime);
			if (!configuration.getRetentionPolicy().isUseBackupSet()) {
				returnBackupInformationSummary.setRetentionCount(configuration.getRetentionCount());
				if (mergeScheduleTime != null) {
					if (mergeScheduleTime.equals(AbstractMergeService.MANUAL_MERGE_STRING)) {
						returnBackupInformationSummary.setInSchedule(true);
					} else {
						returnBackupInformationSummary.setInSchedule(false);
					}
				}
			} else {
				returnBackupInformationSummary.setRetentionCount(configuration.getRetentionPolicy().getBackupSetCount());
			}
			returnBackupInformationSummary.setBackupDestination(configuration.getDestination());
			returnBackupInformationSummary.setSpaceMeasureNum(configuration.getSpaceMeasureNum());
			returnBackupInformationSummary.setSpaceMeasureUnit(configuration.getSpaceMeasureUnit());
			// wanqi06
			returnBackupInformationSummary.setBackupSet(configuration.getRetentionPolicy().isUseBackupSet());
			//

			// LicInfo licInfo = this.getNativeFacade().getLicInfo();
			// returnBackupInformationSummary.setLicInfo(licInfo);
			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertObject2String(returnBackupInformationSummary));
				if (returnBackupInformationSummary != null) {
					logger.debug(StringUtil.convertObject2String(returnBackupInformationSummary.getDestinationCapacity()));
					logger.debug(StringUtil.convertObject2String(returnBackupInformationSummary.getRecentFullBackup()));
					logger.debug(StringUtil.convertObject2String(returnBackupInformationSummary.getRecentIncrementalBackup()));
					logger.debug(StringUtil.convertObject2String(returnBackupInformationSummary.getRecentResyncBackup()));
					// logger
					// .debug(StringUtil
					// .convertObject2String(returnBackupInformationSummary
					// .getLicInfo()));
				}
			}
			logger.debug("getBackupInformationSummary() - end");
			return returnBackupInformationSummary;
		} catch (Throwable e) {
			processWebCallException(e);
			logger.error("getBackupInformationSummary()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	private void processWebCallException(Throwable e) throws ServiceException {
		if (e instanceof ServerSOAPFaultException) {
			ServerSOAPFaultException exception = (ServerSOAPFaultException) e;
			if (exception.getFault() != null) {
				String errorCode = exception.getFault().getFaultCodeAsQName().getLocalPart();
				String errorMsg = exception.getFault().getFaultString();
				ServiceException sex = new ServiceException(errorMsg, errorCode);
				if (errorMsg != null) {
					String msg = WebServiceErrorMessages.getServiceError(errorCode, null);
					if (msg != null && msg.indexOf("}") == msg.indexOf("{") + 2) {
						sex.setWebServiceCause(exception);
					}
				}
				throw sex;
			}
		}
	}

	public BackupInformationSummary getBackupInformationSummaryWithLicInfo() throws ServiceException {
		logger.debug("getBackupInformationSummaryWithLicInfo() - start");

		try {
			BackupConfiguration configuration = this.getBackupConfiguration();
			PreferencesConfiguration preferencesSettings = CommonService.getInstance().getPreferences();
			BackupInformationSummary returnBackupInformationSummary = null;
			if (configuration == null) {
				if (preferencesSettings != null) {
					if (preferencesSettings.getupdateSettings() != null) {
						preferencesSettings.getupdateSettings().setBackupsConfigured(false);
					}
					returnBackupInformationSummary = new BackupInformationSummary();
					returnBackupInformationSummary.setUpdateSettings(preferencesSettings.getupdateSettings() != null ? preferencesSettings.getupdateSettings()
							: null);
				}
				return returnBackupInformationSummary;
			}
			CONN_INFO info = getCONN_INFO(configuration);
			JBackupInfoSummary summary = this.getNativeFacade().GetBackupInfoSummary(configuration.getDestination(), info.getDomain(), info.getUserName(),
					info.getPwd(), false);

			returnBackupInformationSummary = backupSummaryConverter.convert(summary);

			returnBackupInformationSummary.setAdvanced(configuration.getBackupDataFormat() > 0);
			returnBackupInformationSummary.setAdvanceSchedule(configuration.getAdvanceSchedule());
			if (configuration.getAdvanceSchedule() != null) {
				returnBackupInformationSummary.setPeriodEnabled(configuration.getAdvanceSchedule().isPeriodEnabled());
			}

			setRPSInfo(configuration, returnBackupInformationSummary);

			String mergeScheduleTime = MergeService.getInstance().getMergeScheduleTime();
			returnBackupInformationSummary.setMergeJobScheduleTime(mergeScheduleTime);
			if (!configuration.getRetentionPolicy().isUseBackupSet()) {
				returnBackupInformationSummary.setRetentionCount(configuration.getRetentionCount());
				if (mergeScheduleTime != null) {
					if (mergeScheduleTime.equals(AbstractMergeService.MANUAL_MERGE_STRING)) {
						returnBackupInformationSummary.setInSchedule(true);
					} else {
						returnBackupInformationSummary.setInSchedule(false);
					}
				}
			} else {
				returnBackupInformationSummary.setRetentionCount(configuration.getRetentionPolicy().getBackupSetCount());
			}
			returnBackupInformationSummary.setBackupSet(configuration.getRetentionPolicy().isUseBackupSet());
			returnBackupInformationSummary.setBackupDestination(configuration.getDestination());
			returnBackupInformationSummary.setSpaceMeasureNum(configuration.getSpaceMeasureNum());
			returnBackupInformationSummary.setSpaceMeasureUnit(configuration.getSpaceMeasureUnit());

			LicInfo licInfo = CommonService.getInstance().getLicInfo();
			returnBackupInformationSummary.setLicInfo(licInfo);

			
			if (preferencesSettings != null) {
				preferencesSettings.getupdateSettings().setBackupsConfigured(true);
				returnBackupInformationSummary.setUpdateSettings(preferencesSettings.getupdateSettings() != null ? preferencesSettings.getupdateSettings()
						: null);
			}

			// retrieving last archive job info
			JArchiveJob archiveJob = new JArchiveJob();
			archiveJob.setScheduleType(ArchiveScheduleStatus.LastJobDetails);
			archiveJob.setbOnlyOneSession(true);
			logger.debug("reading archive job info in summary lic info");
			List<ArchiveJobInfo> archiveFinishedJobsInfoList = ArchiveService.getInstance().GetArchiveJobsInfo(archiveJob);

			/*
			 * synchronized (ArchiveService.getInstance().getObjectLock()) {
			 * ArchiveService
			 * .getInstance().InsertJobInfoToGlobalList(archiveFinishedJobsInfoList
			 * ); }
			 */

			ArchiveJobInfo archiveInfo = null;
			if (archiveFinishedJobsInfoList != null) {
				logger.debug("archive job info found");
				archiveInfo = new ArchiveJobInfo();
				archiveInfo.setarchiveJobStatus(archiveFinishedJobsInfoList.get(0).getarchiveJobStatus());
				archiveInfo.setArchiveDataSize(archiveFinishedJobsInfoList.get(0).getArchiveDataSize());
				archiveInfo.setCopyDataSize(archiveFinishedJobsInfoList.get(0).getCopyDataSize());
				archiveInfo.setHour(archiveFinishedJobsInfoList.get(0).getHour());
				archiveInfo.setMin(archiveFinishedJobsInfoList.get(0).getMin());
				archiveInfo.setSec(archiveFinishedJobsInfoList.get(0).getSec());
				archiveInfo.setDay(archiveFinishedJobsInfoList.get(0).getDay());
				archiveInfo.setMonth(archiveFinishedJobsInfoList.get(0).getMonth());
				archiveInfo.setYear(archiveFinishedJobsInfoList.get(0).getYear());

				/*
				 * String strDay = Long.toString(archiveInfo.getDay()); String
				 * strMonth = Long.toString(archiveInfo.getMonth()); String
				 * strYear = Long.toString(archiveInfo.getYear()); String
				 * strHour = archiveInfo.getHour() > 12 ?
				 * Long.toString(archiveInfo.getHour() - 12) :
				 * Long.toString(archiveInfo.getHour()); String strMin =
				 * Long.toString(archiveInfo.getMin()); String strSec =
				 * Long.toString(archiveInfo.getSec());
				 * 
				 * String strDateTime = strMonth + "/" + strDay + "/" + strYear
				 * + " " + strHour + ":" + strMin + ":" + strSec;
				 * 
				 * if(archiveInfo.getHour() < 12) strDateTime += " AM"; else
				 * strDateTime += " PM";
				 */ 
				logger.info("File Archive LastJob Year " +archiveFinishedJobsInfoList.get(0).getYear());
				logger.info("File Archive LastJob Month " +archiveFinishedJobsInfoList.get(0).getMonth());
				if(archiveFinishedJobsInfoList.get(0).getYear() == 0 && archiveFinishedJobsInfoList.get(0).getMonth() == 0)
				{
					archiveInfo.setlastArchiveDateTime("N/A");	
					logger.info("Setting File Archive LastJob Date as N/A");
				}
				else
				{
					int iDay = Integer.parseInt(Long.toString(archiveFinishedJobsInfoList.get(0).getDay()));
					int iMonth = Integer.parseInt(Long.toString(archiveFinishedJobsInfoList.get(0).getMonth())) - 1;
					int iYear = Integer.parseInt(Long.toString(archiveFinishedJobsInfoList.get(0).getYear()));
					int iHour = Integer.parseInt(Long.toString(archiveFinishedJobsInfoList.get(0).getHour()));
					int iMin = Integer.parseInt(Long.toString(archiveFinishedJobsInfoList.get(0).getMin()));
					int iSec = Integer.parseInt(Long.toString(archiveFinishedJobsInfoList.get(0).getSec()));
	
					TimeZone timeZone = TimeZone.getDefault();
					java.util.Calendar cal = java.util.Calendar.getInstance(timeZone);
					cal.set(iYear, iMonth, iDay, iHour, iMin, iSec);
					// Date serverDate =
					// BackupConverterUtil.string2Date(strDateTime1);
					// String serverDateString =
					// BackupConverterUtil.dateToString(cal.getTime());
					String serverDateString = StringUtil.date2String(cal.getTime());
	
					archiveInfo.setlastArchiveDateTime(serverDateString);
				}
				/*
				 * java.util.Calendar cd = java.util.Calendar.getInstance();
				 * cd.set(java.util.Calendar.HOUR,
				 * Integer.parseInt(Long.toString(archiveInfo.getHour())));
				 * cd.set(java.util.Calendar.MINUTE,
				 * Integer.parseInt(Long.toString(archiveInfo.getMin())));
				 * cd.set(java.util.Calendar.SECOND,
				 * Integer.parseInt(Long.toString(archiveInfo.getSec())));
				 * cd.set(java.util.Calendar.YEAR,
				 * Integer.parseInt(Long.toString(archiveInfo.getYear())));
				 * cd.set(java.util.Calendar.DAY_OF_MONTH,
				 * Integer.parseInt(Long.toString(archiveInfo.getDay())));
				 * cd.set(java.util.Calendar.MONTH,
				 * Integer.parseInt(Long.toString(archiveInfo.getMonth())));
				 * cd.set(java.util.Calendar.MILLISECOND,0);
				 */
				// int year, int month, int date, int hour, int minute
				// java.util.Calendar cd = new
				// java.util.GregorianCalendar(Integer.parseInt(Long.toString(archiveInfo.getYear()),Long.toString(archiveInfo.getMonth()),Long.toString(archiveInfo.getDay()),Long.toString(archiveInfo.getHour()),Long.toString(archiveInfo.getMin()),Long.toString(archiveInfo.getSec()));
				// java.util.Calendar cd = new
				// java.util.GregorianCalendar((int)archiveInfo.getYear(),(int)archiveInfo.getMonth()-1,(int)archiveInfo.getDay(),(int)archiveInfo.getHour(),(int)archiveInfo.getMin(),(int)archiveInfo.getSec());

				archiveInfo.setbackupSessionPath(archiveFinishedJobsInfoList.get(0).getbackupSessionPath());
				archiveInfo.setbackupSessionId(archiveFinishedJobsInfoList.get(0).getbackupSessionId());
			}
			returnBackupInformationSummary.setArchiveJobInfo(archiveInfo);
			// /

			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertObject2String(returnBackupInformationSummary));
				if (returnBackupInformationSummary != null) {
					logger.debug(StringUtil.convertObject2String(returnBackupInformationSummary.getDestinationCapacity()));
					logger.debug(StringUtil.convertObject2String(returnBackupInformationSummary.getRecentFullBackup()));
					logger.debug(StringUtil.convertObject2String(returnBackupInformationSummary.getRecentIncrementalBackup()));
					logger.debug(StringUtil.convertObject2String(returnBackupInformationSummary.getRecentResyncBackup()));
					logger.debug(StringUtil.convertObject2String(returnBackupInformationSummary.getLicInfo()));
				}
			}
			logger.debug("getBackupInformationSummaryWithLicInfo() - end");
			return returnBackupInformationSummary;
		} catch (Throwable e) {
			processWebCallException(e);
			logger.error("getBackupInformationSummaryWithLicInfo()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public DestinationCapacity getDestSizeInformation(BackupConfiguration configuration) throws ServiceException {
		logger.debug("getDestSizeInformation---------start");
		// logger.debug("destination: " + configuration);

		if (configuration == null) {
			return null;
		}
		logger.debug(configuration.getDestination());
		logger.debug(configuration.getUserName());
		checkDestinationLength(configuration.getDestination());
		DestinationCapacity destCapacity = null;

		try {

			CONN_INFO info = getCONN_INFO(configuration);
			JBackupDestinationInfo destInformation = this.getNativeFacade().GetDestSizeInformation(configuration.getDestination(), info.getDomain(),
					info.getUserName(), info.getPwd());

			destCapacity = destInfoConverter.convert(destInformation);

		} catch (Throwable e) {
			logger.error("getDestSizeInformation()", e);
			throw generateInternalErrorAxisFault();
		}

		return destCapacity;

	}

	private void getOldNextRun4Protection(BackupConfiguration configuration, List<ProtectionInformation> returnProtectionInformationList) throws Exception {
		Date now = new Date();
		for (ProtectionInformation protectionInfo : returnProtectionInformationList) {
			Trigger trigger = null;
			BackupSchedule schedule = null;
			if (protectionInfo.getBackupType() == BackupType.Full) {
				trigger = getBackupSchedule().getTrigger(new TriggerKey(TRIGGER_NAME_BACKUP_FULL, TRIGGER_GROUP_BACKUP_NAME));
				schedule = configuration.getFullBackupSchedule();
			} else if (protectionInfo.getBackupType() == BackupType.Incremental) {
				trigger = getBackupSchedule().getTrigger(new TriggerKey(TRIGGER_NAME_BACKUP_INCREMENTAL, TRIGGER_GROUP_BACKUP_NAME));
				schedule = configuration.getIncrementalBackupSchedule();
			} else if (protectionInfo.getBackupType() == BackupType.Resync) {
				trigger = getBackupSchedule().getTrigger(new TriggerKey(TIGGER_NAME_BACKUP_RESYNC, TRIGGER_GROUP_BACKUP_NAME));
				schedule = configuration.getResyncBackupSchedule();
			}

			if (trigger != null) {
				Date date = trigger.getFireTimeAfter(now);
				protectionInfo.setNextRunTime(date);
				protectionInfo.setNextTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(date));
			}
			protectionInfo.setShedule(schedule);
		}
	}

	private void getNewNextRun4Protection(List<ProtectionInformation> returnProtectionInformationList) {
		Date[] nextRunTimeResult = getNextRunTime();
		for (ProtectionInformation protectionInfo : returnProtectionInformationList) {
			Date nextRunTime = null;
			if (protectionInfo.getBackupType() == BackupType.Full) {
				nextRunTime = nextRunTimeResult[0];
			} else if (protectionInfo.getBackupType() == BackupType.Incremental) {
				nextRunTime = nextRunTimeResult[2];
			} else if (protectionInfo.getBackupType() == BackupType.Resync) {
				nextRunTime = nextRunTimeResult[1];
			}

			if (nextRunTime != null) {
				protectionInfo.setNextRunTime(nextRunTime);
				protectionInfo.setNextTimeZoneOffset(DSTUtils.getTimezoneOffset(nextRunTime));
			}
		}
	}

	public ProtectionInformation[] getProtectionInformation() throws ServiceException {
		logger.debug("getProtectionInformation() - start");

		try {
			BackupConfiguration configuration = this.getBackupConfiguration();
			if (configuration == null)
				return null;

			boolean isDedupe = false;
			if (!configuration.isD2dOrRPSDestType() && configuration.getBackupRpsDestSetting() != null)
				isDedupe = configuration.getBackupRpsDestSetting().isDedupe();

			CONN_INFO info = getCONN_INFO(configuration);

			JProtectionInfo[] sources = this.getNativeFacade().GetProtectionInformation(configuration.getDestination(), info.getDomain(), info.getUserName(),
					info.getPwd());

			List<ProtectionInformation> returnProtectionInformationList = protectionInformationConverter.convert(sources, isDedupe);

			if (getBackupSchedule() != null) {
				if (configuration.getBackupDataFormat() == 0) {
					getOldNextRun4Protection(configuration, returnProtectionInformationList);
				} else {
					getNewNextRun4Protection(returnProtectionInformationList);
				}
			}

			List<ArchiveJobInfo> archiveJobsInfoList = null;
			synchronized (ArchiveService.getInstance().getObjectLock()) {
				// retrieving archive backups information
				JArchiveJob archiveJob = new JArchiveJob();
				archiveJob.setScheduleType(ArchiveScheduleStatus.ScheduleFinished);
				archiveJob.setbOnlyOneSession(false);
				archiveJobsInfoList = ArchiveService.getInstance().GetArchiveJobsInfo(archiveJob);
			}
			ProtectionInformation archiveProtectionInfo = new ProtectionInformation();
			archiveProtectionInfo.setBackupType(BackupType.Archive);// 4 to
																	// represent
																	// archive's
																	// in ui for
																	// protection
																	// summary
			archiveProtectionInfo.setCount(archiveJobsInfoList != null ? archiveJobsInfoList.size() : 0);
			logger.debug("archive jobs count" + archiveProtectionInfo.getCount());

			if (archiveJobsInfoList != null) {
				logger.debug("archive jobs info found.");
				ArchiveJobInfo lastSuccessJobInfo = archiveJobsInfoList.get(0);

				int iDay = Integer.parseInt(Long.toString(lastSuccessJobInfo.getDay()));
				int iMonth = Integer.parseInt(Long.toString(lastSuccessJobInfo.getMonth())) - 1;
				int iYear = Integer.parseInt(Long.toString(lastSuccessJobInfo.getYear()));
				int iHour = Integer.parseInt(Long.toString(lastSuccessJobInfo.getHour()));
				int iMin = Integer.parseInt(Long.toString(lastSuccessJobInfo.getMin()));
				int iSec = Integer.parseInt(Long.toString(lastSuccessJobInfo.getSec()));

				TimeZone timeZone = TimeZone.getDefault();
				java.util.Calendar cal = java.util.Calendar.getInstance(timeZone);
				cal.set(iYear, iMonth, iDay, iHour, iMin, iSec);
				// String serverDateString =
				// BackupConverterUtil.dateToString(cal.getTime());
				String serverDateString = StringUtil.date2String(cal.getTime());

				/*
				 * if(lastSuccessJobInfo.getHour() < 12) strDateTime += " AM";
				 * else strDateTime += " PM";
				 */

				archiveProtectionInfo.setLastBackupTime(serverDateString);
			}
			BackupSchedule archiveSchedule = null;
			archiveSchedule = ConvertToArchiveSchedule(ArchiveService.getInstance().getInternalArchiveConfiguration());
			archiveProtectionInfo.setShedule(archiveSchedule);
			// calculating total size
			long lSize = 0;
			if (archiveJobsInfoList != null) {
				for (ArchiveJobInfo archiveJobInfo : archiveJobsInfoList) {
					lSize = lSize + Long.parseLong(archiveJobInfo.getArchiveDataSize()) + Long.parseLong(archiveJobInfo.getCopyDataSize());
				}
			}
			archiveProtectionInfo.setSize(lSize);

			if (getBackupSchedule() != null) {
				Date now = new Date();

				Trigger trigger = getBackupSchedule().getTrigger(new TriggerKey(TRIGGER_GROUP_ARCHIVE_BACKUP, TRIGGER_GROUP_ARCHIVE_BACKUP));
				if (trigger != null) {
					archiveProtectionInfo.setNextRunTime(trigger.getFireTimeAfter(now));
				}
			}

			returnProtectionInformationList.add(archiveProtectionInfo);
			returnProtectionInformationList.add(CopyService.getInstance().getScheduledExportProtectionInfo());

			ProtectionInformation[] returnProtectionInformationArray = returnProtectionInformationList.toArray(new ProtectionInformation[0]);

			if (logger.isDebugEnabled()) {
				System.out.println(StringUtil.convertArray2String(returnProtectionInformationArray));
			}
			logger.debug("getProtectionInformation() - end");
			return returnProtectionInformationArray;
		} catch (Throwable e) {
			logger.error("getProtectionInformation()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	private BackupSchedule ConvertToArchiveSchedule(ArchiveConfiguration archiveConfiguration) {

		BackupSchedule archiveSchedule = new BackupSchedule();
		if (archiveConfiguration == null) {
			archiveSchedule.setEnabled(false);
			return archiveSchedule;
		}

		archiveSchedule.setEnabled(archiveConfiguration.isbArchiveAfterBackup());
		archiveSchedule.setInterval(archiveConfiguration.getiArchiveAfterNBackups());
		archiveSchedule.setIntervalUnit(3);// unit 3 resembles "no of backups"
											// in ui.
		return archiveSchedule;
	}

	private void updateOldProtectionInfo(ProtectionInformation[] result) throws Exception {
		// Date now = new Date();
		for (ProtectionInformation protectionInfo : result) {
			Trigger trigger = null;
			BackupSchedule schedule = null;
			if (protectionInfo.getBackupType() == BackupType.Full) {
				trigger = getBackupSchedule().getTrigger(new TriggerKey(TRIGGER_NAME_BACKUP_FULL, TRIGGER_GROUP_BACKUP_NAME));
				schedule = protectConfiguration.getFullBackupSchedule();
			} else if (protectionInfo.getBackupType() == BackupType.Incremental) {
				trigger = getBackupSchedule().getTrigger(new TriggerKey(TRIGGER_NAME_BACKUP_INCREMENTAL, TRIGGER_GROUP_BACKUP_NAME));
				schedule = protectConfiguration.getIncrementalBackupSchedule();
			} else if (protectionInfo.getBackupType() == BackupType.Resync) {
				trigger = getBackupSchedule().getTrigger(new TriggerKey(TIGGER_NAME_BACKUP_RESYNC, TRIGGER_GROUP_BACKUP_NAME));
				schedule = protectConfiguration.getResyncBackupSchedule();
			} else if (protectionInfo.getBackupType() == BackupType.Archive) {
				trigger = getBackupSchedule().getTrigger(new TriggerKey(TRIGGER_NAME_ARCHIVE_BACKUP, TRIGGER_GROUP_ARCHIVE_BACKUP));

				schedule = ConvertToArchiveSchedule(ArchiveService.getInstance().getInternalArchiveConfiguration());
			} else if (protectionInfo.getBackupType() == BackupType.Copy) {
				schedule = CopyService.getInstance().convertToBackupSchedule(CopyService.getInstance().getScheduledExportConfiguration());
			}

			if (trigger != null) {
				Date date = trigger.getNextFireTime();
				protectionInfo.setNextRunTime(date);
				protectionInfo.setNextTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(date));
			}
			protectionInfo.setShedule(schedule);
		}
	}

	private void updateNewProtectionInfo(ProtectionInformation[] result) throws Exception {
		Date[] nextRunTimeResult = getNextRunTime();
		for (ProtectionInformation protectionInfo : result) {
			BackupSchedule schedule = null;
			if (protectionInfo.getBackupType() == BackupType.Full) {
				Date nextRunTime = nextRunTimeResult[0];
				protectionInfo.setNextRunTime(nextRunTime);
				protectionInfo.setNextTimeZoneOffset(DSTUtils.getTimezoneOffset(nextRunTime));
				schedule = protectConfiguration.getFullBackupSchedule();
			} else if (protectionInfo.getBackupType() == BackupType.Incremental) {
				Date nextRunTime = nextRunTimeResult[2];
				protectionInfo.setNextRunTime(nextRunTime);
				protectionInfo.setNextTimeZoneOffset(DSTUtils.getTimezoneOffset(nextRunTime));
				schedule = protectConfiguration.getIncrementalBackupSchedule();
			} else if (protectionInfo.getBackupType() == BackupType.Resync) {
				Date nextRunTime = nextRunTimeResult[1];
				protectionInfo.setNextRunTime(nextRunTime);
				protectionInfo.setNextTimeZoneOffset(DSTUtils.getTimezoneOffset(nextRunTime));
				schedule = protectConfiguration.getResyncBackupSchedule();
			} else if (protectionInfo.getBackupType() == BackupType.Archive) {
				Trigger trigger = getBackupSchedule().getTrigger(new TriggerKey(TRIGGER_NAME_ARCHIVE_BACKUP, TRIGGER_GROUP_ARCHIVE_BACKUP));
				if (trigger != null) {
					protectionInfo.setNextRunTime(trigger.getNextFireTime());
					protectionInfo.setNextTimeZoneOffset(DSTUtils.getTimezoneOffset(trigger.getNextFireTime()));
				}
				schedule = ConvertToArchiveSchedule(ArchiveService.getInstance().getInternalArchiveConfiguration());
			} else if (protectionInfo.getBackupType() == BackupType.Copy) {
				schedule = CopyService.getInstance().convertToBackupSchedule(CopyService.getInstance().getScheduledExportConfiguration());
			}

			protectionInfo.setShedule(schedule);
		}
	}

	public ProtectionInformation[] updateProtectionInformation() throws ServiceException {

		logger.debug("updateProtectionInformation() - start");

		try {
			if (protectConfiguration == null) {
				synchronized (lock) {
					if (protectConfiguration == null) {
						protectConfiguration = backupConfigurationXMLDAO.get(ServiceContext.getInstance().getBackupConfigurationFilePath());
					}
				}
			}

			if (backupConfiguration == null || protectConfiguration == null || getBackupSchedule() == null)
				return null;

			ProtectionInformation[] result = new ProtectionInformation[5];
			ProtectionInformation info = new ProtectionInformation();
			info.setBackupType(BackupType.Full);
			result[0] = info;

			info = new ProtectionInformation();
			info.setBackupType(BackupType.Incremental);
			result[1] = info;

			info = new ProtectionInformation();
			info.setBackupType(BackupType.Resync);
			result[2] = info;

			// archive schedule information
			info = new ProtectionInformation();
			info.setBackupType(BackupType.Archive);
			result[3] = info;

			// scheduled export information
			info = new ProtectionInformation();
			info.setBackupType(BackupType.Copy);
			result[4] = info;

			if (getBackupSchedule() != null) {
				if (backupConfiguration.getBackupDataFormat() == 0) {
					this.updateOldProtectionInfo(result);
				} else {
					this.updateNewProtectionInfo(result);
				}
			}

			boolean isDedupe = false;
			if (!backupConfiguration.isD2dOrRPSDestType() && backupConfiguration.getBackupRpsDestSetting() != null)
				isDedupe = backupConfiguration.getBackupRpsDestSetting().isDedupe();
			for (int i = 0; i < 5; i++)
				result[i].setDedupe(isDedupe);

			logger.debug(StringUtil.convertArray2String(result));
			logger.debug("updateProtectionInformation() - end");

			return result;

		} catch (Throwable e) {
			logger.error("updateProtectionInformation()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public RecoveryPoint[] getMostRecentRecoveryPoints(int backupType, int backupStatus, int top) throws ServiceException {
		logger.debug("getMostRecentRecoveryPoints(int) - start");
		logger.debug("top:" + top);

		try {
			BackupConfiguration configuration = this.getBackupConfiguration();
			if (configuration == null)
				return null;
			CONN_INFO info = getCONN_INFO(configuration);

			JBackupInfo[] restorePoints = getNativeFacade().getMostRecentRecoveryPoints(configuration.getDestination(), info.getDomain(), info.getUserName(),
					info.getPwd(), backupType, backupStatus, top, null);
			RecoveryPoint[] result = recoveryPointConverter.convert2RecoveryPointsFromBackupInfo(restorePoints);

			// updating archive jobs status
			ArchiveConfiguration archiveConfig = ArchiveService.getInstance().getArchiveConfigurationAlreadyDefined();
			if (archiveConfig != null) {
				long lActiveArchiveJobSessionId = -1;
				if ((ArchiveService.getInstance().getArchiveBackupJobMonitor() != null)
						&& (ArchiveService.getInstance().getArchiveBackupJobMonitor().getJobId() != -1)) {
					lActiveArchiveJobSessionId = ArchiveService.getInstance().getArchiveBackupJobMonitor().getJobId();
				}
				List<ArchiveJobInfo> archiveJobsInfoList = null;
				synchronized (ArchiveService.getInstance().getObjectLock()) {
					logger.debug("Entered synchronize block in most recent panel");
					archiveJobsInfoList = ArchiveService.getInstance().getArchiveJobsInformation();
					// if((archiveJobsInfoList == null) &&
					// (!ArchiveService.getInstance().bRequestedArchiveJobsInfo))
					JArchiveJob archiveJob = new JArchiveJob();
					if (archiveJobsInfoList == null || (result.length > archiveJobsInfoList.size())) {
						logger.debug("reading archive info in recent backup info");
						archiveJob.setScheduleType(ArchiveScheduleStatus.ScheduleAll);
						archiveJob.setbOnlyOneSession(false);
						archiveJobsInfoList = ArchiveService.getInstance().GetArchiveJobsInfo(archiveJob);
						ArchiveService.getInstance().setArchiveJobsInformation(archiveJobsInfoList);
						// ArchiveService.getInstance().bRequestedArchiveJobsInfo
						// = true;
					} else {
						archiveJob.setScheduleType(ArchiveScheduleStatus.LastJobDetails);
						archiveJob.setbOnlyOneSession(true);
						logger.debug("reading archive job info in recent backup info");
						List<ArchiveJobInfo> archiveFinishedJobsInfoList = ArchiveService.getInstance().GetArchiveJobsInfo(archiveJob);

						// synchronized
						// (ArchiveService.getInstance().getObjectLock()) {
						ArchiveService.getInstance().InsertJobInfoToGlobalList(archiveFinishedJobsInfoList);
						// }
					}

					logger.debug("Exiting synchronize block in most recent panel");
				}
				// logger.info("reading archive info finished");
				if ((archiveJobsInfoList != null) && (result != null)) {
					logger.debug("populating archive jobs information in recent backups panel");
					int iBackupJobsCount = result.length > top ? top : result.length;
					for (int iIndex = 0; iIndex < iBackupJobsCount; iIndex++) {
						long iSessionId = result[iIndex].getSessionID();
						// logger.info("backup session id " + iSessionId);
						// int iArchiveCount = archiveJobsInfoList.size() > top
						// ? top : archiveJobsInfoList.size();
						// int iArchiveJobsIndex = archiveJobsInfoList.size() -
						// 1;
						for (int iArchiveJobsIndex = archiveJobsInfoList.size() - 1; iArchiveJobsIndex >= 0; iArchiveJobsIndex--) {
							// logger.info("archive job data found for backup session id "
							// +
							// archiveJobsInfoList.get(iArchiveJobIndex).getbackupSessionId());
							if ((iSessionId == Long.parseLong(archiveJobsInfoList.get(iArchiveJobsIndex).getbackupSessionId()))
									&& (result[iIndex].getBackupStatus() == BackupStatus.Finished)) {
								// logger.info("setting archive status for " +
								// iSessionId+". archive status" +
								// archiveJobsInfoList.get(iArchiveJobIndex).getarchiveJobStatus());
								logger.debug("archive job status found for archive job " + archiveJobsInfoList.get(iArchiveJobsIndex).getarchiveJobId());
								result[iIndex].setArchiveJobStatus(archiveJobsInfoList.get(iArchiveJobsIndex).getarchiveJobStatus());
								int iArchiveStatus = archiveJobsInfoList.get(iArchiveJobsIndex).getarchiveJobStatus();
								if ((iArchiveStatus == ArchiveScheduleStatus.ScheduleFinished) || (iArchiveStatus == ArchiveScheduleStatus.ScheduleCancel)
										|| (iArchiveStatus == ArchiveScheduleStatus.ScheduleJobFailed))
									break;
							} else if (iSessionId == lActiveArchiveJobSessionId) {
								// logger.info("archive job running");
								result[iIndex].setArchiveJobStatus(ArchiveScheduleStatus.ScheduleRunning);
							}
						}
					}
				}
			}
			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertArray2String(restorePoints));
			logger.debug("getRecoveryPoints - end");
			return result;
		} catch (Throwable e) {
			logger.error("getRecoveryPoints()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public RecoveryPoint[] getRecentBackupsByServerTime(int backupType, int backupStatus, Date beginDate, Date endDate) throws ServiceException {
		logger.debug("getRecentBackupsByServerTime(int) - start");

		try {
			BackupConfiguration configuration = this.getBackupConfiguration();
			if (configuration == null)
				return null;
			CONN_INFO info = getCONN_INFO(configuration);

			JBackupInfo[] restorePoints = getNativeFacade().getRecentBackupsByServerTime(configuration.getDestination(), info.getDomain(), info.getUserName(),
					info.getPwd(), backupType, backupStatus, beginDate, endDate, null);
			RecoveryPoint[] result = recoveryPointConverter.convert2RecoveryPointsFromBackupInfo(restorePoints, configuration.getRetentionPolicy()
					.isUseBackupSet());

			// updating archive jobs status
			ArchiveConfiguration archiveConfig = ArchiveService.getInstance().getArchiveConfigurationAlreadyDefined();
			if (archiveConfig != null) {
				long lActiveArchiveJobSessionId = -1;
				if ((ArchiveService.getInstance().getArchiveBackupJobMonitor() != null)
						&& (ArchiveService.getInstance().getArchiveBackupJobMonitor().getJobId() != -1)) {
					lActiveArchiveJobSessionId = ArchiveService.getInstance().getArchiveBackupJobMonitor().getSessionID();
				}
				List<ArchiveJobInfo> archiveJobsInfoList = null;
				synchronized (ArchiveService.getInstance().getObjectLock()) {
					logger.debug("Entered synchronize block in most recent panel");
					// archiveJobsInfoList =
					// ArchiveService.getInstance().getArchiveJobsInformation();
					// if((archiveJobsInfoList == null) &&
					// (!ArchiveService.getInstance().bRequestedArchiveJobsInfo))

					JArchiveJob archiveJob = new JArchiveJob();
					archiveJob.setScheduleType(ArchiveScheduleStatus.ScheduleAll);
					archiveJob.setbOnlyOneSession(false);
					archiveJobsInfoList = ArchiveService.getInstance().GetArchiveJobsInfo(archiveJob);
					// If backup destination changes the back end gives NULL
					// then get the list from cache
					if (archiveJobsInfoList == null)
						archiveJobsInfoList = ArchiveService.getInstance().getArchiveJobsInformation();
					else
						ArchiveService.getInstance().setArchiveJobsInformation(archiveJobsInfoList);

					logger.debug("Exiting synchronize block in most recent panel");
				}
				// logger.info("reading archive info finished");
				if ((archiveJobsInfoList != null) && (result != null)) {
					logger.debug("populating archive jobs information in recent backups panel");
					int iBackupJobsCount = /* result.length > top ? top : */result.length;
					for (int iIndex = 0; iIndex < iBackupJobsCount; iIndex++) {
						long iSessionId = result[iIndex].getSessionID();
						// logger.info("backup session id " + iSessionId);
						// int iArchiveCount = archiveJobsInfoList.size() > top
						// ? top : archiveJobsInfoList.size();
						// int iArchiveJobsIndex = archiveJobsInfoList.size() -
						// 1;
						for (int iArchiveJobsIndex = archiveJobsInfoList.size() - 1; iArchiveJobsIndex >= 0; iArchiveJobsIndex--) {
							if (iSessionId == lActiveArchiveJobSessionId && (result[iIndex].getBackupStatus() == BackupStatus.Finished)
									&& backupConfiguration != null && result[iIndex].getBackupDest().equals(backupConfiguration.getDestination())) {
								// if Archive Job is running
								result[iIndex].setArchiveJobStatus(ArchiveScheduleStatus.ScheduleRunning);
								break;
							} else if ((iSessionId == Long.parseLong(archiveJobsInfoList.get(iArchiveJobsIndex).getbackupSessionId()))
									&& (result[iIndex].getBackupStatus() == BackupStatus.Finished)
									&& archiveJobsInfoList.get(iArchiveJobsIndex).getbackupSessionPath().startsWith(result[iIndex].getBackupDest())) {
								// logger.info("setting archive status for " +
								// iSessionId+". archive status" +
								// archiveJobsInfoList.get(iArchiveJobIndex).getarchiveJobStatus());
								logger.debug("archive job status found for archive job " + archiveJobsInfoList.get(iArchiveJobsIndex).getarchiveJobId());
								result[iIndex].setArchiveJobStatus(archiveJobsInfoList.get(iArchiveJobsIndex).getarchiveJobStatus());
								break;

							}
						}
					}
				}
			}
			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertArray2String(restorePoints));
			logger.debug("getRecoveryPoints - end");
			return result;
		} catch (Throwable e) {
			logger.error("getRecoveryPoints()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	// wanqi06

	private Date[] getNextRunTime() {
		Date result[] = new Date[3];
		try {
			String[] triggerGroupNames = new String[] { BaseService.TRIGGER_GROUP_BACKUP_NAME_FULL, BaseService.TRIGGER_GROUP_BACKUP_NAME_RESYNC,
					BaseService.TRIGGER_GROUP_BACKUP_NAME_INCREMENTAL };
			for (int i = 0; i < triggerGroupNames.length; i++) {
				String[] triggerNames = ScheduleUtils.getTriggerNames(getBackupSchedule(),triggerGroupNames[i]);
				if ((triggerNames == null) || (triggerNames.length == 0)) {
					result[i] = null;
				}
			}

			NextScheduleEvent event = getNextScheduleEvent();
			if (event == null) {
				return result;
			}
			if (event.getBackupType() == BackupType.Full) {
				result[0] = event.getDate();
			} else if (event.getBackupType() == BackupType.Resync) {
				result[1] = event.getDate();
			} else {
				result[2] = event.getDate();
			}

			Date beginDate = event.getDate();
			java.util.Calendar endCal = java.util.Calendar.getInstance();
			endCal.setTime(beginDate);
			endCal.add(java.util.Calendar.DATE, 32);
			Date endDate = endCal.getTime();
			List<Trigger> backupTriggers = getBackupTriggers(null);

			while ((beginDate.compareTo(endDate) < 0) && (backupTriggers.size() > 0)) {
				Trigger resultTrigger = null;
				Date resultDate = null;
				for (Trigger trigger : backupTriggers) {
					if (resultTrigger == null) {
						resultTrigger = trigger;
						resultDate = trigger.getFireTimeAfter(beginDate);
					} else {
						Date currentDate = trigger.getFireTimeAfter(beginDate);
						int compareResult = resultDate.compareTo(currentDate);
						if (compareResult == 0) {
							compareResult = trigger.getPriority() - resultTrigger.getPriority();
						}
						if (compareResult > 0) {
							resultTrigger = trigger;
							resultDate = currentDate;
						}
					}
				}

				if (((AbstractTrigger)resultTrigger).getGroup().equals(BaseService.TRIGGER_GROUP_BACKUP_NAME_FULL) && result[0] == null) {
					result[0] = resultDate;
				} else if (((AbstractTrigger)resultTrigger).getGroup().equals(BaseService.TRIGGER_GROUP_BACKUP_NAME_RESYNC) && result[1] == null) {
					result[1] = resultDate;
				} else if (((AbstractTrigger)resultTrigger).getGroup().equals(BaseService.TRIGGER_GROUP_BACKUP_NAME_INCREMENTAL) && result[2] == null) {
					result[2] = resultDate;
				}

				beginDate = resultDate;
				if ((result[0] != null) && (result[1] != null) && (result[2] != null))
					return result;

			}
			return result;

		} catch (Exception e) {
			logger.error(e);
		}
		return result;
	}
	
	/*
	private NextScheduleEvent getOldNextEvent() throws ServiceException {
		try {
			Trigger fullTrigger = getBackupSchedule().getTrigger(new TriggerKey(TRIGGER_NAME_BACKUP_FULL, getFullBackupTriggerGroupName(D2D_VM_ISNTANCEUUID))); //TRIGGER_GROUP_BACKUP_NAME));
			Trigger incrementalTrigger = getBackupSchedule().getTrigger(new TriggerKey(TRIGGER_NAME_BACKUP_INCREMENTAL, getIncBackupTriggerGroupName(D2D_VM_ISNTANCEUUID))); //TRIGGER_GROUP_BACKUP_NAME));
			Trigger resyncTrigger = getBackupSchedule().getTrigger(new TriggerKey(TIGGER_NAME_BACKUP_RESYNC, getResyncBackupTriggerGroupName(D2D_VM_ISNTANCEUUID))); //TRIGGER_GROUP_BACKUP_NAME));

			int backupType = BackupType.Unknown;
			Date nextEvent = null;
			Date nextFull = fullTrigger == null ? null : fullTrigger.getNextFireTime();
			Date nextIncremental = incrementalTrigger == null ? null : incrementalTrigger.getNextFireTime();
			Date nextResync = resyncTrigger == null ? null : resyncTrigger.getNextFireTime();

			if (nextFull != null) {
				if (nextIncremental == null || nextFull.compareTo(nextIncremental) < 0) {
					nextEvent = nextFull;
					backupType = BackupType.Full;
				} else {
					nextEvent = nextIncremental;
					backupType = BackupType.Incremental;
				}
			} else if (nextIncremental != null) {
				nextEvent = nextIncremental;
				backupType = BackupType.Incremental;
			}

			if (nextResync != null) {
				if (nextEvent == null || nextResync.compareTo(nextEvent) < 0) {
					nextEvent = nextResync;
					backupType = BackupType.Resync;
				}
			}

			if (nextEvent == null)
				return null;

			NextScheduleEvent result = new NextScheduleEvent();
			result.setBackupType(backupType);
			result.setDate(nextEvent);
			result.setTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(nextEvent));
			logger.debug("getNextScheduleEvent() - end");
			return result;
		} catch (Throwable e) {
			logger.error("getNextScheduleEvent()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	*/

	public NextScheduleEvent getNextScheduleEvent() throws ServiceException {
		logger.debug("getNextScheduleEvent() - start");

		try {
			if (getBackupSchedule() == null)
				return null;

			if (backupConfiguration == null)
				return null;

//since we unified the trigger for both format, so getOldNextEvent is not needed
//			if (backupConfiguration.getBackupDataFormat() == 0) {
//				return getOldNextEvent();
//			} else {
				return getNewNextEvent(D2D_VM_ISNTANCEUUID);
//			}
		} catch (Throwable e) {
			logger.error("getNextScheduleEvent()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	@Override
	public Scheduler getBackupSchedule() {
		if (bkpScheduler != null)
			return bkpScheduler;
		try {

			InputStream is = this.getClass().getResourceAsStream("/com/ca/arcflash/webservice/scheduler/agt-bkp-sched-quartz-4bkp.properties");
			Properties properties = new Properties();
			properties.load(is);
			bkpScheduler = new StdSchedulerFactory(properties).getScheduler();
			bkpScheduler.start();
		} catch (SchedulerException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		} catch (IOException e) {
			logger.error("getBackupSchedule failed", e);
		}

		return bkpScheduler;
	}

	public boolean isBackupCompressionLevelChanged() throws ServiceException {
		logger.debug("isBackupCompressionLevelChanged() - start");

		try {

			int compressionLevel = this.getBackupConfiguration().getCompressionLevel();
			return isBackupCompressionLevelChanged(compressionLevel);

		} catch (Throwable e) {
			logger.error("isBackupCompressionLevelChanged()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public boolean isBackupCompressionLevelChanged(int compressionLevel) throws ServiceException {
		logger.debug("isBackupCompressionLevelChanged() - start");

		try {
			BackupConfiguration configuration = this.getBackupConfiguration();

			if (configuration == null) {
				logger.debug("backup configuration is null, return false");
				return false;
			}

			CONN_INFO info = getCONN_INFO(configuration);

			boolean returnboolean = this.getNativeFacade().isCompressionLevelChanged(configuration.getDestination(), info.getDomain(), info.getUserName(),
					info.getPwd(), compressionLevel);

			logger.debug("return value:" + returnboolean);
			logger.debug("isBackupCompressionLevelChanged() - end");
			return returnboolean;
		} catch (Throwable e) {
			logger.error("isBackupCompressionLevelChanged()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public boolean isBackupEncryptionAlgorithmAndKeyChanged() throws ServiceException {
		logger.debug("isBackupEncryptionAlgorithmAndKeyChanged() - start");

		try {
			BackupConfiguration configuration = this.getBackupConfiguration();
			if (configuration == null) {
				logger.debug("backup configuration is null, return false");
				return false;
			}

			int encryptionAlgorithm = configuration.getEncryptionAlgorithm();
			String encryptionKey = configuration.getEncryptionKey();

			boolean returnboolean = isBackupEncryptionAlgorithmAndKeyChanged(encryptionAlgorithm, encryptionKey);

			logger.debug("return value:" + returnboolean);
			logger.debug("isBackupEncryptionAlgorithmAndKeyChanged() - end");
			return returnboolean;
		} catch (Throwable e) {
			logger.error("isBackupEncryptionAlgorithmAndKeyChanged()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public boolean isBackupEncryptionAlgorithmAndKeyChanged(int encryptionAlgorithm, String encryptionKey) throws ServiceException {
		logger.debug("isBackupEncryptionAlgorithmAndKeyChanged() - start");

		try {
			BackupConfiguration configuration = this.getBackupConfiguration();
			if (configuration == null) {
				logger.debug("backup configuration is null, return false");
				return false;
			}

			CONN_INFO info = getCONN_INFO(configuration);

			boolean returnboolean = this.getNativeFacade().isEncryptionAlgorithmAndKeyChanged(configuration.getDestination(), info.getDomain(),
					info.getUserName(), info.getPwd(), encryptionAlgorithm, encryptionKey);

			logger.debug("return value:" + returnboolean);
			logger.debug("isBackupEncryptionAlgorithmAndKeyChanged() - end");
			return returnboolean;
		} catch (Throwable e) {
			logger.error("isBackupEncryptionAlgorithmAndKeyChanged()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public void checkDestinationLength(String destination) throws ServiceException {
		long maxLength = getPathMaxLength();
		if (destination.length() > maxLength + 1) {
			backupConfigurationValidator.generatePathExeedLimitException(maxLength);
		}
	}

	public void checkDestinationLengthForArchive(String destination) throws ServiceException {
		long maxLength = getPathMaxLength();
		if (destination.length() > maxLength + 1) {
			archiveConfigurationValidator.generatePathExeedLimitException(maxLength);
		}
	}

	public long getPathMaxLength() throws ServiceException {
		logger.debug("getPathMaxLength - started");
		long maxLength = -1;
		try {
			maxLength = this.getNativeFacade().getPathMaxLength();
		} catch (Throwable e) {
			throw generateInternalErrorAxisFault();
		}
		return maxLength;
	}

	public long checkDestinationValid(String path) throws ServiceException {
		logger.debug("checkDestinationValid - start");

		try {
			long ret = this.getNativeFacade().checkDestinationValid(path);
			logger.debug("checkDestinationValid - ret = " + ret);
			return ret;
		} catch (Throwable e) {
			throw generateInternalErrorAxisFault();
		}
	}

	public void validateAdminAccount(Account account) throws ServiceException {
		logger.debug("validateAdminAccount - start");
		this.getNativeFacade().validateAdminAccount(account);
		logger.debug("validateAdminAccount - end");
	}

	public Account getAdminAccount() throws ServiceException {
		logger.debug("getAdminAccount - start");
		Account account = getNativeFacade().getAdminAccount();
		logger.debug("getAdminAccount - ret UserName: " + account.getUserName());
		return account;
	}

	public void saveAdminAccount(Account account) throws ServiceException {
		logger.debug("saveAdminAccount - start");
		getNativeFacade().saveAdminAccount(account);
		logger.debug("saveAdminAccount - end ");
	}

	public long getDestDriveType(String path) throws ServiceException {
		logger.debug("getDestDriveType - start");
		if (StringUtil.isEmptyOrNull(path))
			return -1;
		if(path.startsWith("\\\\") && !path.substring(2).contains("\\")){
			throw generateAxisFault(path, FlashServiceErrorCode.Browser_Invalid_Share_Folder_Path);
		}
		if (path.length() > BrowserService.BROWSE_LENGTH)
			throw generateAxisFault(BrowserService.BROWSE_LENGTH + "", FlashServiceErrorCode.Browser_Source_Path_Exceeds_Max);
		long ret = getNativeFacade().getDestDriveType(path);
		logger.debug("return value:" + ret);
		logger.debug("getDestDriveType - end ");
		return ret;
	}

	public long getDestDriveType(String path, int mode) throws ServiceException {
		logger.debug("getDestDriveType - start");
		// if(path.length() > BrowserService.BROWSE_LENGTH)
		// throw
		// generateAxisFault(BrowserService.BROWSE_LENGTH+"",FlashServiceErrorCode.Browser_Source_Path_Exceeds_Max);
		if (path != null) {
			if(path.startsWith("\\\\") && !path.substring(2).contains("\\")){
				throw generateAxisFault(path, FlashServiceErrorCode.Browser_Invalid_Share_Folder_Path);
			}
			
			if (mode == BrowserService.ARCHIVE_DEST_MODE) {
				BackupService.getInstance().checkDestinationLengthForArchive(path);

			} else if (mode == BrowserService.BACKUP_MODE) {
				BackupService.getInstance().checkDestinationLength(path);

			} else {
				if (path.length() > BrowserService.BROWSE_LENGTH) {
					throw generateAxisFault(BrowserService.BROWSE_LENGTH + "", FlashServiceErrorCode.Browser_Source_Path_Exceeds_Max);
				}
			}
		}
		long ret = getNativeFacade().getDestDriveType(path);
		logger.debug("return value:" + ret);
		logger.debug("getDestDriveType - end ");
		return ret;
	}

	public void saveHasSendEmail(boolean hasSendEmail, boolean allowSendEmail) throws Exception {
		synchronized (lock) {
			this.backupConfiguration.setAllowSendEmail(allowSendEmail);
			this.backupConfiguration.setHasSendEmail(hasSendEmail);
			backupConfigurationXMLDAO.save(ServiceContext.getInstance().getBackupConfigurationFilePath(), backupConfiguration);
		}
	}

	private void verifyDestination() {
		try {
			// This code segment is for usb hard disk identification. Issue
			// Number:18686454
			// If backup destination is usb hard disk.Drive letter may be
			// changed to another one if replug in.
			// If changed, return the new destination. If unchanged,do not
			// change.
			String newDestination = getNativeFacade().findNewDestination(backupConfiguration.getDestination());
			if (newDestination != null && !newDestination.equals(backupConfiguration.getDestination())) {
				backupConfiguration.setDestination(newDestination);
				saveBackupConfiguration(backupConfiguration);
			}
		} catch (Throwable e) {
			logger.error("Fail in findNewDestination:" + e.getMessage(), e);
		}
	}

	/**
	 * to compensate the missed backup, if full backup is missed, start a full
	 * backup, else if resync backup is missed, start a resync, else if
	 * incremental is missed, start a incremental.
	 */

	public void dealWithMissedBackup() {
		try {
			BackupConfiguration configuration = this.getBackupConfiguration();
			if (configuration == null) {
				logger.debug("dealWithMissedBackup() - end with null configuration");
				return;
			}
			if (configuration.getBackupDataFormat() == 0) {
				mp.dealWithMissedBackupStandardFormat();
			} else {
				mp.dealWithMissedBackupAdvancedFormat();
			}
		} catch (Exception e) {

		}
	}

	private MakeupProcessor mp = new MakeupProcessor();

	public void modifyPeriodJObDetail(JobDetail jobDetail, JobDetail oriJobDetail) {
		if (jobDetail == null || oriJobDetail == null)
			return;

		boolean isDaily = false, isWeekly = false, isMonthly = false;

		if (oriJobDetail.getJobDataMap().containsKey("periodRetentionFlag")) {
			int periodRetentionFlag = oriJobDetail.getJobDataMap().getInt("periodRetentionFlag");
			isDaily = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Daily) > 0;
			isWeekly = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Weekly) > 0;
			isMonthly = (periodRetentionFlag & PeriodRetentionValue.QJDTO_B_Backup_Monthly) > 0;
		} else {
			if (oriJobDetail.getJobDataMap().containsKey("isDaily")) {
				isDaily = oriJobDetail.getJobDataMap().getBoolean("isDaily");
			}

			if (oriJobDetail.getJobDataMap().containsKey("isWeekly")) {
				isWeekly = oriJobDetail.getJobDataMap().getBoolean("isWeekly");
			}

			if (oriJobDetail.getJobDataMap().containsKey("isMonthly")) {
				isMonthly = oriJobDetail.getJobDataMap().getBoolean("isMonthly");
			}
		}

		if (isDaily) {
			jobDetail.getJobDataMap().put("isDaily", new Boolean(isDaily));
		}

		if (isWeekly) {
			jobDetail.getJobDataMap().put("isWeekly", new Boolean(isWeekly));
		}

		if (isMonthly) {
			jobDetail.getJobDataMap().put("isMonthly", new Boolean(isMonthly));
		}
	}

	public byte[] signalStart = new byte[0];

	/**
	 * 
	 * @return cannot be null
	 */
	public RetryPolicy getRetryPolicy(String jobTypeName) {
		logger.debug("getRetryPolicy - start");
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		RetryPolicy result = null;
		boolean error = false;
		String msg = "";
		try {
			result = CommonService.getInstance().getRetryPolicy(jobTypeName);
			if (result == null) {
				if (jobTypeName != null && jobTypeName.equals(CommonService.RETRY_CATALOG)) {
					result = CatalogService.getInstance().defaultRetryPolicy();
				} else {
					result = new RetryPolicy();
				}
			}
		} catch (Exception e) {
			error = true;
			msg = e.getMessage();
			nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY,
					new String[] { WebServiceMessages.getResource(Constants.RETRYPOLICY_READ_ERROR), "", "", "", "" });
			logger.error("getRetryPolicy - " + e.getMessage(), e);
		}
		if (!error) {
			if (result.isEnabled() && result.isFailedEnabled()) {
				if (result.getMaxTimes() <= 0) {
					error = true;
					msg = "invalid MaxTimes attribute";
				} else if (!result.isImmediately() && result.getTimeToWait() <= 0) {
					error = true;
					msg = "invalid TimeToWait attribute";
				}
			}
			if (!error && result.isEnabled() && (result.isFailedEnabled() || result.isMissedEnabled())) {
				if (result.getNearToNextEvent() <= 0) {
					error = true;
					msg = "invalid NearToNextEvent attribute";
				}
			}
			if (error) {
				nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY,
						new String[] { WebServiceMessages.getResource(Constants.RETRYPOLICY_READ_ERROR), "", "", "", "" });
				logger.debug("getRetryPolicy - " + msg);
				result = new RetryPolicy();
			}
		}

		logger.debug("getRetryPolicy - end");
		return result;
	}

	/**
	 * 
	 * @return cannot be null
	 */
	public void saveRetryPolicy(RetryPolicy retryPolicy) {
		logger.debug("saveRetryPolicy - start");
		if (StringUtil.isExistingPath(ServiceContext.getInstance().getRetryPolicyFilePath())) {
			logger.debug("getRetryPolicy - end with exist retry policy file");

		} else {
			boolean error = false;
			try {
				retryPolicyXMLDAO.save(ServiceContext.getInstance().getRetryPolicyFilePath(), retryPolicy);
			} catch (Exception e) {
				error = true;
				logger.error(e.getMessage(), e);
			}
			if (error) {
				NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
				nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_JOB_RETRY,
						new String[] { WebServiceMessages.getResource(Constants.RETRYPOLICY_SAVE_ERROR), "", "", "", "" });
			}
		}
		logger.debug("saveRetryPolicy - end");
		return;
	}

	public static void main(String[] args) {
		RetryPolicy retryPolicy = new RetryPolicy();
		BackupService bs = new BackupService();
		ServiceContext.getInstance().setRetryPolicyFilePath("e:\\temp2\\RetryPolicy.xml");
		bs.saveRetryPolicy(retryPolicy);
		RetryPolicy policy = bs.getRetryPolicy(CommonService.RETRY_BACKUP);
		System.out.println("dfadf" + policy.getMaxTimes());
	}

	public boolean checkBLILic() {
		return getNativeFacade().checkBLILic();
	}

	private void verifyDestThresholdValue(BackupConfiguration configuration) throws ServiceException {
		if(configuration.isEnableSpaceNotification()==false){
			logger.debug("Won't check destination threshold as the SpaceNotification is false");
			return;
		}
		
		try {
//			if (configuration.isEnableSpaceNotification() && "MB".equals(configuration.getSpaceMeasureUnit())) {
//				CONN_INFO info = getCONN_INFO(configuration);
//				JBackupInfoSummary summary = this.getNativeFacade().GetBackupInfoSummary(configuration.getDestination(), info.getDomain(), info.getUserName(),
//						info.getPwd(), true);
//				double configFreeSpace = configuration.getSpaceMeasureNum();
//				long actualFreeSpace = StringUtil.string2Long(summary.getDestinationInfo().getTotalFreeSize(), 0) >> 20;
//				if (configFreeSpace >= actualFreeSpace) {
//					// logger.debug("configFreeSpace: " + configFreeSpace);
//					// logger.debug("actualFreeSpace" + actualFreeSpace);
//					throw new ServiceException(FlashServiceErrorCode.BackupConfig_ERR_ThresholdIseTooBig);
//				}
//			}
			
			CONN_INFO info = getCONN_INFO(configuration);
			JBackupInfoSummary summary = this.getNativeFacade()
					.GetBackupInfoSummary(configuration.getDestination(),
							info.getDomain(), info.getUserName(),
							info.getPwd(), true);
			
			double configFreeSpace = 0;
			long actualFreeSpace = StringUtil.string2Long(summary.getDestinationInfo().getTotalFreeSize(), 0) >> 20;
			long actualTotalSpace = StringUtil.string2Long(summary.getDestinationInfo().getTotalSize(), 0) >> 20;
			
			if ("%".equals(configuration.getSpaceMeasureUnit())) {
				configFreeSpace = (actualTotalSpace * configuration.getSpaceMeasureNum())/100;
			}else if ("MB".equals(configuration.getSpaceMeasureUnit())){
				configFreeSpace = configuration.getSpaceMeasureNum();
			}


			if (configFreeSpace >= actualFreeSpace) {
				logger.debug("configFreeSpace: " + configFreeSpace);
				logger.debug("actualFreeSpace" + actualFreeSpace);
				throw new ServiceException(
						FlashServiceErrorCode.BackupConfig_ERR_ThresholdIseTooBig);
			}
			
		} catch (ServiceException se) {
			if (se instanceof ServiceException) {
				throw se;
			}
		} catch (Throwable e) {
			logger.error("destination threshold is bigger than actural free space", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public ApplicationWriter[] getExcludedAppComponents(List<String> volumeList) throws ServiceException {
		logger.debug("getExcludedAppComponents - start");
		List<JApplicationWriter> appWriter = getNativeFacade().getExcludedAppComponents(volumeList);
		ApplicationWriter[] apps = convert2ApplicationData(appWriter);
		logger.debug("resulted app arr: " + StringUtil.convertArray2String(apps));
		logger.debug("getExcludedAppComponents - end");
		return apps;
	}

	private ApplicationWriter[] convert2ApplicationData(List<JApplicationWriter> excludedAppList) {
		ApplicationWriter[] apps = new ApplicationWriter[excludedAppList.size()];
		for (int i = 0, count = excludedAppList.size(); i < count; i++) {
			JApplicationWriter app = excludedAppList.get(i);
			apps[i] = new ApplicationWriter();
			apps[i].setAppName(app.getAppName());
			List<String> affectedMnt = app.getAffectedMnt();
			if (affectedMnt != null)
				apps[i].setAffectedMnt(affectedMnt.toArray(new String[0]));

			List<JApplicationComponect> componentList = app.getComponentList();
			ApplicationComponent[] appComps = null;
			if (componentList != null) {
				appComps = new ApplicationComponent[componentList.size()];
				for (int j = 0, compCount = componentList.size(); j < compCount; j++) {
					JApplicationComponect appComp = componentList.get(j);
					appComps[j] = new ApplicationComponent();
					appComps[j].setName(appComp.getName());
					List<String> compAffectedMnt = appComp.getAffectedMnt();
					if (compAffectedMnt != null)
						appComps[j].setAffectedMnt(compAffectedMnt.toArray(new String[0]));
					List<String> fileList = appComp.getFileList();
					if (fileList != null)
						appComps[j].setFileList(fileList.toArray(new String[0]));
				}
			}
			apps[i].setComponentList(appComps);
		}
		return apps;
	}

	public DestinationCapacity getDestCapacity(String destination, String domain, String userName, String pwd) throws ServiceException {
		try {
			logger.debug("getDestCapacity(String, String, String, String) - start");
			logger.debug("destination:" + destination);
			logger.debug("domain:" + domain);
			logger.debug("userName:" + userName);
			checkDestinationLength(destination);

			if (StringUtil.isEmptyOrNull(domain) && userName != null && userName.trim().length() > 0) {
				userName = userName.trim();
				int index = userName.indexOf("\\");
				if (index > 0) {
					domain = userName.substring(0, index);
					userName = userName.substring(index + 1);
				}
			}

			JBackupInfoSummary summary = this.getNativeFacade().GetBackupInfoSummary(destination, domain, userName, pwd, true);
			JBackupDestinationInfo capacity = summary.getDestinationInfo();

			// Perhaps the sub folder does not exist
			if (StringUtil.isEmptyOrNull(capacity.getTotalSize()) && !StringUtil.isEmptyOrNull(destination)) {
				String rootPath = getRootPath(destination);
				if (rootPath != null) {
					summary = this.getNativeFacade().GetBackupInfoSummary(rootPath, domain, userName, pwd, true);
					capacity = summary.getDestinationInfo();
				}
			}

			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertObject2String(capacity));

			DestinationCapacity dset = transformJDestCapacity(summary);
			logger.debug("getDestCapacity(String, String, String, String) - end");
			return dset;
		} catch (ServiceException se) {
			throw se;
		} catch (Throwable e) {
			logger.debug(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	/**
	 * Returns root path if the path contains sub path. Otherwise returns null.
	 * 
	 * @param destination
	 * @return
	 */
	private String getRootPath(String destination) {
		if (destination.charAt(1) == ':') {
			if (destination.charAt(2) == '\\' && destination.length() > 3)
				return destination.substring(0, 3);
		} else {
			int indexOfHost = destination.indexOf("\\", 2);
			if (indexOfHost > 0 && indexOfHost < destination.length() - 1) {
				int indexOfFirstPath = destination.indexOf("\\", indexOfHost + 1);
				if (indexOfFirstPath > 0 && indexOfFirstPath < destination.length() - 1)
					return destination.substring(0, indexOfFirstPath);
			}
		}

		return null;
	}

	private DestinationCapacity transformJDestCapacity(JBackupInfoSummary summary) {
		if (summary != null) {
			return backupSummaryConverter.getDestCapacity(summary);
		}
		return null;
	}

	public void regenerateWriterMetadata() {
		try {
			logger.debug("regenerateWriterMetadata() - start");
			getNativeFacade().regenerateWriterMetadata();
			logger.debug("regenerateWriterMetadata() - end");
		} catch (Exception e) {
			logger.error("Error occur when regenerating writer meta data: " + e.getMessage(), e);
		}
	}

	public String[] getLocalDestVolumes(String destPath) throws ServiceException {
		List<String> volumesList = getNativeFacade().getLocalDestVolumes(destPath);
		return volumesList.toArray(new String[0]);
	}

	public boolean ValidateServerName(String in_ServerName) {
		return backupConfigurationValidator.validateServerName(in_ServerName);
	}

	private void createDesktopINI() {
		String backupDestination = backupConfiguration.getDestination();

		try {
			this.getNativeFacade().createDesktopINI(backupDestination);
		} catch (Throwable e) {
			logger.error("createDesktopINI()", e);
		}
	}

	public void stopAllBackJobs() throws ServiceException {
		logger.info("stopAllBackJobs - start");

		try {
			synchronized (lock) {

				if (!StringUtil.isExistingPath(ServiceContext.getInstance().getBackupConfigurationFilePath()))
					return;

				BackupConfiguration backupConfig = backupConfigurationXMLDAO.get(ServiceContext.getInstance().getBackupConfigurationFilePath());

				disableBackupSchedule(backupConfig.getFullBackupSchedule());
				disableBackupSchedule(backupConfig.getIncrementalBackupSchedule());
				disableBackupSchedule(backupConfig.getResyncBackupSchedule());

				backupConfigurationXMLDAO.save(ServiceContext.getInstance().getBackupConfigurationFilePath(), backupConfig);

				if (backupConfiguration != null)
					reloadBackupConfiguration();
			}

			logger.info("stopAllBackJobs - end");
		} catch (Exception e) {
			logger.error("stopAllBackJobs()", e);
		}
	}

	private void disableBackupSchedule(BackupSchedule fullSchedule) {
		if (fullSchedule != null && fullSchedule.isEnabled()) {
			fullSchedule.setEnabled(false);
			fullSchedule.setInterval(0);
			fullSchedule.setIntervalUnit(3);

		}
	}

	public synchronized String checkDestChainAccess() throws ServiceException {
		BackupConfiguration conf = this.getBackupConfiguration();
		if (conf == null)
			return "";
		this.getNativeFacade().cutAllRemoteConnections();
		connectionWithLock(conf.getDestination(), conf.getUserName(), conf.getPassword());

		JNetConnInfo currentConn = new JNetConnInfo();
		currentConn.setSzDir(conf.getDestination());
		currentConn.setSzDomain("");
		currentConn.setSzUsr(conf.getUserName());
		currentConn.setSzPwd(conf.getPassword());

		JNetConnInfo baseDest = new JNetConnInfo();
		JNetConnInfo errDest = new JNetConnInfo();
		if (!WSJNI.AFCheckDestChainAccess(currentConn, baseDest, errDest, this.updateConnectionPrev)) {
			baseConnection = baseDest;
			return errDest.getSzDir();
		} else {
			if (updateConnectionPrev) {
				updateConnectionPrev = false;
				return checkDestChainAccess();
			} else {
				updateConnectionPrev = true;
				return "";
			}
		}
	}

	public synchronized String updateDestConnection(String dest, String user, String pass, String domain) throws ServiceException {
		// liuyu07 2011-5-11 fix Issue: 20273286
		if (backupConfiguration.getDestination().equals(dest)) {// current
																// destiantion
			logger.info("update current connection : " + dest);
			try {
				connectionWithLock(dest, user, pass);
			} catch (Exception e1) {
				logger.error("update current connection error : " + e1);
				return dest;
			}
			// save to file
			String oldUser = backupConfiguration.getUserName();
			String oldPass = backupConfiguration.getPassword();
			try {
				backupConfiguration.setUserName(user);
				backupConfiguration.setPassword(pass);
				backupConfigurationXMLDAO.save(ServiceContext.getInstance().getBackupConfigurationFilePath(), backupConfiguration);
			} catch (Exception e) {
				logger.error("update destination connection error: " + e);
				logger.error("rollback the old user and password ");
				// rollback
				backupConfiguration.setUserName(oldUser);
				backupConfiguration.setPassword(oldPass);
				throw new ServiceException(FlashServiceErrorCode.BackupConfig_ERR_UPDATE_CREN, new Object[] { dest });
			}
			return checkDestChainAccess();
		}

		JNetConnInfo newConn = new JNetConnInfo();
		newConn.setSzDir(dest);
		newConn.setSzDomain(domain);
		newConn.setSzPwd(pass);
		newConn.setSzUsr(user);

		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(dest);
		try {
			if (lock != null) {
				lock.lock();
			}
			this.getNativeFacade().cutAllRemoteConnections();

			long ret = WSJNI.AFCheckUpdateNetConn(baseConnection, newConn, updateConnectionPrev);
			if (ret != 0) {
				throw new ServiceException(FlashServiceErrorCode.BackupConfig_ERR_UPDATE_CREN, new Object[] { dest });
			} else {
				return checkDestChainAccess();
			}
		} finally {
			if (lock != null)
				lock.unlock();
		}

	}

	public void connectionWithLock(String dest, String userName, String pwd) throws ServiceException {
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(dest);
		try {
			if (lock != null) {
				lock.lock();
				this.getNativeFacade().NetConn(userName, pwd, dest);
				RemoteFolderConnCache.getInstance().addConnections(dest, "", userName, pwd, true);
			} else {
				JNetConnInfo conn = new JNetConnInfo();
				conn.setSzDir(dest);
				conn.setSzUsr(userName);
				conn.setSzPwd(pwd);
				if (WSJNI.AFCreateConnection(conn) != 0)
					logger.error("Connect failed " + dest);
			}
		} finally {
			if (lock != null)
				lock.unlock();
		}
	}

	public int CheckBackupConfigSettingWithEdge() {
		// wait at most about (times*millis/1000) seconds
		int times = 20;
		long millis = 30000;
		while (times-- > 0) {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
			}
			EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.CentralManagement);
			if (edgeRegInfo == null)
				return PolicyQueryStatus.OK;
			String policyUuid = edgeRegInfo.getPolicyUuids().get("Default");
			if (policyUuid == null || policyUuid.isEmpty())
				return PolicyQueryStatus.OK;
			else {
				policyUuid = policyUuid.split(":")[0];
			}
			String d2duuid = CommonService.getInstance().getNodeUUID();

			int status = checkPolicyFromEdge(edgeRegInfo, policyUuid, d2duuid, true);

			switch (status) {
			case PolicyCheckStatus.UNKNOWN:
			case PolicyCheckStatus.SAMEPOLICY:
				return PolicyQueryStatus.OK;
			case PolicyCheckStatus.NOPOLICY:
			case PolicyCheckStatus.POLICYFAILED:
				return PolicyQueryStatus.FAIL;
			case PolicyCheckStatus.DIFFERENTPOLICY:
			case PolicyCheckStatus.POLICYDEPLOYING:
				continue;
			}
		}
		return PolicyQueryStatus.TIMEOUT;
	}

	public int refreshBackupConfigSettingWithEdge() {
		EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.CentralManagement);
		if (edgeRegInfo == null)
			return PolicyCheckStatus.UNKNOWN;
		String policyUuid = edgeRegInfo.getPolicyUuids().get("Default");
		if (policyUuid == null || policyUuid.isEmpty())
			return PolicyCheckStatus.UNKNOWN;
		else {
			policyUuid = policyUuid.split(":")[0];
		}
		String d2duuid = CommonService.getInstance().getNodeUUID();

		int status = checkPolicyFromEdge(edgeRegInfo, policyUuid, d2duuid, false);

		if (status == PolicyCheckStatus.NOPOLICY) {
			List<PolicyDeploymentError> errorList = new ArrayList<PolicyDeploymentError>();

			PolicyApplyerFactory.createPolicyApplyer(ID2DPolicyManagementService.PolicyTypes.BackupAndArchiving).unapplyPolicy(errorList, false, "");

			BackupService
					.getInstance()
					.getNativeFacade()
					.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_GENERAL,
							new String[] { WebServiceMessages.getResource("autoUnassignPolicy"), "", "", "", "" });

		} else if (status == PolicyCheckStatus.DIFFERENTPOLICY) {
			BackupService
					.getInstance()
					.getNativeFacade()
					.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_GENERAL,
							new String[] { WebServiceMessages.getResource("autoRedeployPolicy"), "", "", "", "" });
		} else if (status == PolicyCheckStatus.POLICYFAILED) {
			BackupService
					.getInstance()
					.getNativeFacade()
					.addLogActivity(Constants.AFRES_AFALOG_WARNING, Constants.AFRES_AFJWBS_GENERAL,
							new String[] { WebServiceMessages.getResource("autoRedeployFailedPolicy"), "", "", "", "" });
		}

		return status;
	}

	private int checkPolicyFromEdge(EdgeRegInfo edgeRegInfo, String policyUuid, String d2duuid, boolean justcheck) {
		if (edgeRegInfo == null || edgeRegInfo.getEdgeWSDL() == null || edgeRegInfo.getEdgeWSDL().isEmpty()) {
			return PolicyCheckStatus.UNKNOWN;
		}

		try {
			IEdgeCM4D2D service = WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(), IEdgeCM4D2D.class);

			try {
				service.validateUserByUUID(edgeRegInfo.getEdgeUUID());
			} catch (EdgeServiceFault e) {
				logger.error("D2DSync - Failed to establish connection to Edge Server(login failed)\n");
				return PolicyCheckStatus.UNKNOWN;
			}

			try {
				int state = service.checkPolicyStatus(d2duuid, policyUuid, justcheck);
				return state;
			} catch (EdgeServiceFault e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
				return PolicyCheckStatus.UNKNOWN;
			}
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
			return PolicyCheckStatus.UNKNOWN;
		}
	}

	public DestinationCapacity getDestSizeInformationForArchiveMode(BackupConfiguration configuration) throws ServiceException {
		logger.debug("getDestSizeInformation---------start");
		// logger.debug("destination: " + configuration);

		if (configuration == null) {
			return null;
		}
		logger.debug(configuration.getDestination());
		logger.debug(configuration.getUserName());
		checkDestinationLengthForArchive(configuration.getDestination());
		DestinationCapacity destCapacity = null;

		try {

			CONN_INFO info = getCONN_INFO(configuration);
			JBackupDestinationInfo destInformation = this.getNativeFacade().GetDestSizeInformation(configuration.getDestination(), info.getDomain(),
					info.getUserName(), info.getPwd());

			destCapacity = destInfoConverter.convert(destInformation);

		} catch (Throwable e) {
			logger.error("getDestSizeInformation()", e);
			throw generateInternalErrorAxisFault();
		}

		return destCapacity;

	}

	public boolean testEmailSettings(BackupEmail mailConf) throws ServiceException {
		if (mailConf == null)
			return false;
		mailConf.setEnableSettings(true);
		if (backupConfigurationValidator.ValidateEmailSettings(mailConf) == 0) {
			EmailSender sender = new EmailSender();
			return sender.sendTestMail(mailConf);
		} else {
			return false;
		}
	}

	public int getPreAllocationValue() throws ServiceException {
		return (int) this.getNativeFacade().getPreAllocSpacePercent();
	}

	public boolean isScheduledFullJob(int date) {
		return isScheduledFullJob(date, D2D_VM_ISNTANCEUUID);
	}

	public boolean existScheduledBackup(int date) {
		try {
			NextScheduleEvent event = this.getNextScheduleEvent();
			if (event != null) {
				logger.info("Current date is " + date + " Next schedule backup will run at " + event.getDate());
			}
			if (event == null || event.getDate().getDate() != date)
				return false;
			else {
				return true;
			}
		} catch (ServiceException se) {
			logger.error("Failed to get next scheduled event " + se);
		}
		return false;

	}

	public void checkIfUpgradeFromOldBuild() {
		try {
			BackupConfiguration configuration = getBackupConfiguration();
			if (configuration == null)
				return;
			ArchiveConfiguration archiveConfiguration = ArchiveService.getInstance().getArchiveConfiguration();
			if (archiveConfiguration == null)
				return;
			// upgrade from 16 as we add version information from 16.5
			if (configuration.getMajorVersion() == null || configuration.getMajorVersion().equals("")) {
				if (archiveConfiguration.isbArchiveAfterBackup()) {
					configuration.setGenerateCatalog(true);
					saveBackupConfiguration(configuration, true, null);
				}
			}
		} catch (ServiceException e) {
			logger.error("Failed to checkIfUpgradeFromOldBuild", e);
		}
	}

	public String getRPSPolicyUUID() {
		try {
			BackupConfiguration configuration = instance.getBackupConfiguration();
			if (configuration == null)
				return null;
			if (!configuration.isD2dOrRPSDestType() && configuration.getBackupRpsDestSetting() != null)
				return configuration.getBackupRpsDestSetting().getRPSPolicyUUID();
		} catch (ServiceException e) {
			logger.error("Error in geting BackupConfiguration", e);
		}
		return null;
	}

	public String getDataStoreUUID() {
		try {
			BackupConfiguration configuration = instance.getBackupConfiguration();
			if (configuration == null)
				return null;
			if (!configuration.isD2dOrRPSDestType() && configuration.getBackupRpsDestSetting() != null)
				return configuration.getBackupRpsDestSetting().getRPSDataStore();
		} catch (ServiceException e) {
			logger.error("Error in geting BackupConfiguration", e);
		}
		return null;
	}

	public String getDataStoreName() {
		try {
			BackupConfiguration configuration = instance.getBackupConfiguration();
			if (configuration == null)
				return null;
			if (!configuration.isD2dOrRPSDestType() && configuration.getBackupRpsDestSetting() != null)
				return configuration.getBackupRpsDestSetting().getRPSDataStoreDisplayName();
		} catch (ServiceException e) {
			logger.error("Error in geting BackupConfiguration", e);
		}
		return null;
	}

	private void saveRPSSetting(BackupConfiguration configuration) throws ServiceException {
		registryD2D2PPS(configuration);
		assignPolicy2D2D(configuration);
	}

	private void registryD2D2PPS(BackupConfiguration newConfiguration) throws ServiceException {
		if (newConfiguration == null || newConfiguration.getBackupRpsDestSetting() == null) {
			logger.error("Invalid parameter");
			return;
		}
		RpsHost rpsHost = newConfiguration.getBackupRpsDestSetting().getRpsHost();
		if (rpsHost == null) {
			logger.error("Invalid parameter");
			return;
		}
		ConfigRPSInD2DService.getInstance().registryD2D2PPS(rpsHost, newConfiguration.getDestination());
		ConfigRPSInD2DService.getInstance().addVMToFlashListener(rpsHost, null);
	}

	private void assignPolicy2D2D(BackupConfiguration newConfiguration) throws ServiceException {
		if (newConfiguration == null || newConfiguration.getBackupRpsDestSetting() == null) {
			logger.error("Invalid parameter");
			return;
		}
		RpsHost rpsHost = newConfiguration.getBackupRpsDestSetting().getRpsHost();
		if (rpsHost == null) {
			logger.error("Invalid parameter");
			return;
		}
		ConfigRPSInD2DService.getInstance().callAssignPolicyToD2D(rpsHost, newConfiguration.getBackupRpsDestSetting().getRPSPolicyUUID());
	}

	private void unregistryD2D2RPS(BackupConfiguration newConfiguration) {
		if (newConfiguration == null || newConfiguration.getBackupRpsDestSetting() == null) {
			logger.error("Invalid parameter");
			return;
		}
		RpsHost rpsHost = newConfiguration.getBackupRpsDestSetting().getRpsHost();
		if (rpsHost == null || StringUtil.isEmptyOrNull(rpsHost.getRhostname())) {
			logger.error("Invalid parameter");
			return;
		}
		ConfigRPSInD2DService.getInstance().unRegisterD2DToRPSServer(rpsHost);
	}

	private void unRegistryD2DIfNeed(BackupConfiguration newConfiguration, BackupConfiguration oldConfiguration) {
		if (oldConfiguration != null && !oldConfiguration.isD2dOrRPSDestType()) {
			if (!newConfiguration.isD2dOrRPSDestType()) {
				String newRpsHostName = newConfiguration.getBackupRpsDestSetting().getRpsHost().getRhostname();
				String oldRpsHostName = oldConfiguration.getBackupRpsDestSetting().getRpsHost().getRhostname();
				if (newRpsHostName == null || !newRpsHostName.equalsIgnoreCase(oldRpsHostName)) {
					unregistryD2D2RPS(oldConfiguration);
				}
			} else {
				unregistryD2D2RPS(oldConfiguration);
			}
		}
	}

	public synchronized boolean isBackupToRPS() {
		if (backupConfiguration != null) {
			return !backupConfiguration.isD2dOrRPSDestType();
		} else {
			return false;
		}
	}

	public BackupJobArg getBackupJobArg(JobDetailImpl jobDetail, BackupConfiguration configuration) {
		BackupJobArg arg = new BackupJobArg();
		arg.setJobDetailName(jobDetail.getName());
		arg.setJobDetailGroup(jobDetail.getGroup());
		arg.setPolicyUUID(BackupService.getInstance().getRPSPolicyUUID());
		arg.setDataStoreUUID(BackupService.getInstance().getDataStoreUUID());
		Object jobName = jobDetail.getJobDataMap().get("jobName");
		if (jobName != null) {
			arg.setJobName((String) jobName);
		}
		arg.setD2dServerName(ServiceContext.getInstance().getLocalMachineName());
		arg.setD2dServerUUID(CommonService.getInstance().getNodeUUID());
		arg.setDataStoreName(BackupService.getInstance().getDataStoreName());
		return arg;
	}

	/**
	 * Run a backup job immediately without goes into RPS job queue.
	 * 
	 * @param arg
	 * @return
	 * @throws ServiceException
	 */
	public long backupNow(BackupJobArg arg) throws ServiceException {
	    if (handleErrorFromRPS(arg) == -1)
	    	return -1;;
			
		if (this.getBackupConfiguration() == null) {
			logger.debug("There is no backup configuration, return error code");
			throw generateAxisFault(FlashServiceErrorCode.Backup_NoBackupConfiguration);
		}

		// check whether there is running jobs
		if (this.getNativeFacade().checkJobExist() || BaseBackupJob.isJobRunning()) {
			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
		}

		try {
			JobDetailImpl jobDetail;
			long type = arg.getJobMethod();
			if (type == BackupType.Full)
				jobDetail = new JobDetailImpl(arg.getJobDetailName(), arg.getJobDetailGroup() + "Now", FullBackupJob.class);
			else if (type == BackupType.Incremental)
				jobDetail = new JobDetailImpl(arg.getJobDetailName(), arg.getJobDetailGroup() + "Now", IncrementalBackupJob.class);
			else if (type == BackupType.Resync)
				jobDetail = new JobDetailImpl(arg.getJobDetailName(), arg.getJobDetailGroup() + "Now", ResyncBackupJob.class);
			else
				throw generateAxisFault(FlashServiceErrorCode.Backup_InvalidBackupType);

			jobDetail.getJobDataMap().put("jobName", arg.getJobName());
			jobDetail.getJobDataMap().put("backupNow", Boolean.TRUE);
			jobDetail.getJobDataMap().put(JOB_ID, arg.getJobId());
			jobDetail.getJobDataMap().put(RPS_CATALOG_GENERATION, arg.isEnableCatalog());
			jobDetail.getJobDataMap().put("periodRetentionFlag", arg.getPeriodRetentionFlag());
			jobDetail.getJobDataMap().put("scheduledTime", arg.getScheduledTime());
			jobDetail.getJobDataMap().put("archiveConfiguration", arg.getArchiveConfig());
			jobDetail.getJobDataMap().put("manualFlag", arg.isManual());
			
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0, 0);
			trigger.setName(jobDetail.getName() + "NowTrigger");
			getBackupSchedule().scheduleJob(jobDetail, trigger);
			logger.debug("backup() - end");
			return 0;
		} catch (ServiceException se) {
			throw se;
		} catch (SchedulerException e) {
			logger.error("Scheduler exception, delete the job", e);
			try {
				getBackupSchedule().deleteJob(new JobKey(arg.getJobDetailName(), arg.getJobDetailGroup() + "Now"));
			} catch (Throwable t) {
				logger.debug("Ignore", t);
			}
			throw generateInternalErrorAxisFault();
		} catch (Throwable e) {
			logger.error("validateUser()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	// wanqi06
	public BackupConfiguration getBackupConfigurationFromCache() {
		return backupConfiguration;
	}
	
	public void configSchedule() {
		configSchedule(D2D_VM_ISNTANCEUUID);
	}

	@Override
	protected void removeAdvScheduleJob(String vmInstanceUUID) {
		for (String jobName : jobNames) {
			try {
				getBackupSchedule().deleteJob(new JobKey(jobName, BaseService.JOB_GROUP_BACKUP_NAME));
			} catch (SchedulerException e) {
				logger.error("Failed to remove job " + jobName);
			}
		}
	}
	
	@Override
	protected void fillJobNamesMap(String vmInstanceUUID, String jobName) {
		jobNames.add(jobName);
	}

	public void initDefaultSchedule(BackupConfiguration configuration, long serverTime) {
		AdvanceSchedule advanceSchedule = new AdvanceSchedule();
		// long serverTime = getServerTime().getTime();
		// set backup start time plus 15 minutes
		Date tempStartTime = new Date(serverTime);
		int addMins = 15 - tempStartTime.getMinutes() % 15;
		Date scheduleStartTime = new Date(tempStartTime.getTime() + addMins * 60 * 1000);

		advanceSchedule.setScheduleStartTime(serverTime);
		// advanceSchedule.setEnabled(true);

		Date start = new Date(scheduleStartTime.getTime());
		Date end = new Date(scheduleStartTime.getTime() + 15 * 60 * 1000);
		if (start.getDate() != end.getDate()) {
			start = new Date(end.getYear(), end.getMonth(), end.getDate());
			end = new Date(start.getTime() + 15 * 60 * 1000);
		}

		ArrayList<DailyScheduleDetailItem> daylyScheduleItems = new ArrayList<DailyScheduleDetailItem>();
		for (int i = 1; i <= 7; i++) {
			ScheduleDetailItem item = new ScheduleDetailItem();
			DayTime startTime = new DayTime(start.getHours(), start.getMinutes());
			item.setStartTime(startTime);
			DayTime endTime = new DayTime(end.getHours(), end.getMinutes());
			item.setEndTime(endTime);
			item.setInterval(15);
			item.setIntervalUnit(0);
			item.setJobType(BackupType.Incremental);
			item.setRepeatEnabled(true);

			ArrayList<ScheduleDetailItem> itemLists = new ArrayList<ScheduleDetailItem>();
			itemLists.add(item);

			DailyScheduleDetailItem dailyItem = new DailyScheduleDetailItem();
			dailyItem.setDayofWeek(i);
			dailyItem.setScheduleDetailItems(itemLists);

			daylyScheduleItems.add(dailyItem);
		}
		advanceSchedule.setDailyScheduleDetailItems(daylyScheduleItems);
		configuration.setAdvanceSchedule(advanceSchedule);

	}

	public BackupConfiguration rpsPolicyUpdated(RpsPolicy4D2D policy, boolean encrypted) throws ServiceException {
		logger.info("Check whether need to update backup configuration for rps policy");
		if (policy == null) {
			logger.error("The input policy is null");
		}
		currentRPSPolicy = policy;
		BackupConfiguration configuration = getBackupConfiguration();
		if (configuration == null)
			return null;
		if (configuration.isD2dOrRPSDestType() || configuration.getBackupRpsDestSetting() == null) {
			logger.warn("D2D does not use rps policy");
			return null;
		}
		boolean changed = false;

		BackupRPSDestSetting setting = configuration.getBackupRpsDestSetting();

		if (!setting.getRPSDataStore().equals(policy.getDataStoreName())) {
			setting.setRPSDataStore(policy.getDataStoreName());
			changed = true;
		}

		if (setting.getRPSDataStoreDisplayName() == null || !setting.getRPSDataStoreDisplayName().equals(policy.getDataStoreDisplayName())) {
			setting.setRPSDataStoreDisplayName(policy.getDataStoreDisplayName());
			changed = true;
		}
		String destination = configuration.getDestination();
		int index = destination.lastIndexOf("//");
		int index2 = destination.lastIndexOf("\\");
		index = Math.max(index, index2);
		String host = destination.substring(index + 1);
		String hostName = ServiceContext.getInstance().getLocalMachineName();
		String sid = CommonService.getInstance().getServerSID();
		if (hostName.equalsIgnoreCase(host) || (hostName + "[" + sid + "]").equalsIgnoreCase(host)) {
			destination = destination.substring(0, index);
		}
		/*
		 * String storePath = policy.getStorePath(); if(storePath != null &&
		 * !storePath.startsWith("\\\\")){ storePath = formatPolicyDestination(
		 * configuration.getBackupRpsDestSetting().getRpsHost().getRhostname(),
		 * policy.getStorePath()); }
		 */
		String storePath = policy.getDataStoreSharedPath();
		if (!destination.equals(storePath)) {
			configuration.setDestination(storePath);
			changed = true;
		}
		if (!StringUtil.isEmptyOrNull(policy.getStoreUserName()) && !StringUtil.equals(configuration.getUserName(), policy.getStoreUserName())) {
			configuration.setUserName(policy.getStoreUserName());
			changed = true;
		}
		if (!StringUtil.isEmptyOrNull(policy.getStorePassword()) && !StringUtil.isEmptyOrNull(policy.getStorePassword())) {
			String decryptedPass = policy.getStorePassword();
			if (encrypted) {
				decryptedPass = WSJNI.AFDecryptStringEx(policy.getStorePassword());
				policy.setStorePassword(decryptedPass);
			}
			if (!decryptedPass.equals(configuration.getPassword())) {
				configuration.setPassword(decryptedPass);
				changed = true;
			}
		}

		if (configuration.getRetentionCount() != policy.getRetentionCount()) {
			changed = true;
			configuration.setRetentionCount(policy.getRetentionCount());
		}
		if (changed) {
			configuration.setBackupRpsDestSetting(setting);
			saveBackupConfiguration(configuration, false, policy);
		}
		logger.info("Check whether need to update backup configuration for rps policy end, changed:" + changed);
		return configuration;
	}

	public boolean rpsPolicyNotExist(BackupConfiguration configuration) {
		try {
			BackupRPSDestSetting setting = configuration.getBackupRpsDestSetting();
			RpsHost host = setting.getRpsHost();
			String protocol = host.isHttpProtocol() ? "http:" : "https:";
			return SettingsService.instance().getRPSPolicy(host.getRhostname(), host.getUsername(), host.getPassword(), host.getPort(), protocol,
					setting.getRPSPolicyUUID(), host.getUuid(), false) == null;
		} catch (ServiceException se) {
			logger.error(se);
			return true;
		}
	}

	private void updateBackupConfiguration4RPSPolicy(RpsPolicy4D2D policy, BackupConfiguration configuration) {
		configuration.setDestination(policy.getDataStoreSharedPath());
		if (StringUtil.isEmptyOrNull(policy.getStoreUserName())) {
			String username = configuration.getBackupRpsDestSetting().getRpsHost().getUsername();
			if (!username.contains("\\")) {
				username = configuration.getBackupRpsDestSetting().getRpsHost().getRhostname() + "\\" + username;
			}
			configuration.setUserName(username);
			configuration.setPassword(configuration.getBackupRpsDestSetting().getRpsHost().getPassword());
		} else {
			configuration.setUserName(policy.getStoreUserName());
			configuration.setPassword(policy.getStorePassword());
		}
		configuration.setRetentionCount(policy.getRetentionCount());
		configuration.setCompressionLevel(policy.getCompressionMethod());
		configuration.setEnableEncryption(policy.isEnableEncryption());
		if (policy.isEnableEncryption() && policy.getEncryptionMethod() > 0)
			configuration.setEncryptionAlgorithm(1 << 16 | policy.getEncryptionMethod());
		else
			configuration.setEncryptionAlgorithm(0);
		configuration.getBackupRpsDestSetting().setDedupe(policy.isEnableGDD());
	}

	public RpsPolicy4D2D getRPSPolicy() {
		return currentRPSPolicy;
	}

	public BackupRPSDestSetting getRpsSetting() {
		try {
			BackupConfiguration configuration = instance.getBackupConfiguration();
			if (configuration == null)
				return null;
			if (!configuration.isD2dOrRPSDestType())
				return configuration.getBackupRpsDestSetting();
		} catch (ServiceException e) {
			logger.error("Error in geting BackupConfiguration", e);
		}
		return null;
	}

	/**
	 * If we change backup destination from shared folder to datastore or change
	 * back, or we change the datastore, we will always start with full backup.
	 * 
	 * @param oldConfiguration
	 * @param newConfiguration
	 */
	private void updateBackupDestChange4RPS(BackupConfiguration oldConfiguration, BackupConfiguration newConfiguration) {
		if (oldConfiguration == null)
			return;
		boolean changed = false;
		if (oldConfiguration.isD2dOrRPSDestType() && !newConfiguration.isD2dOrRPSDestType())
			changed = true;
		else if (!oldConfiguration.isD2dOrRPSDestType() && newConfiguration.isD2dOrRPSDestType())
			changed = true;
		else if (!oldConfiguration.isD2dOrRPSDestType() && !newConfiguration.isD2dOrRPSDestType()) {
			if (oldConfiguration.getBackupRpsDestSetting() != null && oldConfiguration.getBackupRpsDestSetting().getRPSDataStore() != null
					&& !oldConfiguration.getBackupRpsDestSetting().getRPSDataStore().equals(newConfiguration.getBackupRpsDestSetting().getRPSDataStore())) {
				changed = true;
			}
		}

		if (changed) {
			newConfiguration.setChangedBackupDest(true);
			newConfiguration.setChangedBackupDestType(BackupType.Full);
		}
	}

	public RpsPolicy4D2D validateRpsDestSetting(BackupConfiguration backupConfiguration) throws ServiceException {
		return backupConfigurationValidator.validateRpsDestSetting(backupConfiguration);
	}

	// there are several cases backup job cannot run if backup to RPS
	// 1. Cannot connect to RPS webservice.
	// 2. RPS policy not exist.
	// 3. RPS datastore service stopped.
	// 4. RPS datastore not running.
	public RpsPolicy4D2D checkRPS4Backup() throws ServiceException {
		logger.debug("Check RPS server and datastore status for backup job");
		if (!this.isBackupToRPS())
			return null;

		BackupConfiguration configuration = this.getBackupConfiguration();
		BackupRPSDestSetting setting = null;

		if (configuration == null || (setting = configuration.getBackupRpsDestSetting()) == null)
			return null;

//		if (StringUtil.isJustEmptyOrNull(backupConfiguration.getEncryptionKey())) {
//			throw new ServiceException(FlashServiceErrorCode.BackupConfig_ERR_Empty_Session_Pwd);
//		}

		return SettingsService.instance().checkRPS4Backup(setting);
	}

	public void validateRPSHost(String hostName, String userName, String password, int port) throws ServiceException {
		this.backupConfigurationValidator.validateRPSHost(hostName, userName, password, port);
	}

	public boolean isOnlyFullBackup() throws ServiceException {
		try {
			BackupConfiguration backupConf = this.getBackupConfiguration();
			if (backupConf == null)
				return false;

			if (this.isBackupToRPS()) {
				return this.isBackupCompressionLevelChanged();
			} else {
				return this.isBackupCompressionLevelChanged() || this.isBackupEncryptionAlgorithmAndKeyChanged();
			}
		} catch (ServiceException se) {
			throw se;
		}
	}

	public long getRPSDatastoreVersion(String dataStoreUUID) {
		long version = 0;
		try {
			if (!StringUtil.isEmptyOrNull(dataStoreUUID)) {
				RPSDataStoreInfo rpsDataStoreInfo = BackupService.getInstance().getDataStoreInformation(dataStoreUUID);
				version = rpsDataStoreInfo.getVersion();
			}
			BackupConfiguration configuration = getBackupConfiguration();
			RpsHost rpsHost = configuration.getBackupRpsDestSetting().getRpsHost();
			return SettingsService.instance().getRPSDataStoreVersion(rpsHost, dataStoreUUID);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

		return version;
	}

	public RPSDataStoreInfo getDataStoreInformation(String dataStoreUUID) throws ServiceException {
		BackupConfiguration configuration = null;
		try {
			configuration = this.getBackupConfiguration();
		} catch (ServiceException ex) {
			throw ex;
		} catch (Exception e) {
			throw new ServiceException(FlashServiceErrorCode.RPS_PLAN_UNASSIGN);
		}

		RpsHost tempRPSHost = configuration.getBackupRpsDestSetting().getRpsHost();
		IRPSService4D2D client = RPSServiceProxyManager.getRPSServiceClient(tempRPSHost.getRhostname(), tempRPSHost.getUsername(), tempRPSHost.getPassword(),
				tempRPSHost.getPort(), tempRPSHost.isHttpProtocol() ? "http" : "https", tempRPSHost.getUuid());
		RPSDataStoreInfo rpsDSInfo = null;
		if (client != null)
			rpsDSInfo = backupSummaryConverter.convert(client.getDataStoreStatus(dataStoreUUID));
		return rpsDSInfo;
	}

	public long getThrottling() {
		return this.throttling;
	}

	public void setThrottling(long throttling) {
		this.throttling = throttling;
	}

	private void unscheduleTriggers() {
		unscheduleBackupThrottleJob();
		unscheduleMergeJob();
	}

	private void unscheduleBackupThrottleJob() {
		BackupThrottleService.getInstance().unschedule(null);
		VSphereBackupThrottleService.getInstance().unScheduleAllVM();
	}

	private void unscheduleMergeJob() {
		MergeService.getInstance().unschedule(null);
		VSphereMergeService.getInstance().unSchduleAllVM();
	}

	public String getPlanUUID(String vmInstanceUUID) {

		String planUUID = null;
		if (StringUtil.isEmptyOrNull(vmInstanceUUID)) {
			EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.CentralManagement);
			if (edgeRegInfo == null)
				return null;
			planUUID = edgeRegInfo.getPolicyUuids().get("Default");
		} else {
			EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.vShpereManager);
			if (edgeRegInfo == null)
				return null;
			String key_vm = vmInstanceUUID;
			planUUID = edgeRegInfo.getPolicyUuids().get(key_vm);
		}

		if (!StringUtil.isEmptyOrNull(planUUID)) {
			String[] s = planUUID.split(":");
			if (s != null && s.length > 0)
				planUUID = s[0];
		}

		return planUUID;
	}

	public void destory() {
		try {
			if (bkpScheduler != null && bkpScheduler.isStarted()) {
				bkpScheduler.shutdown();
			}
		} catch (Exception e) {
			logger.warn("destory error, Fail to stop Quartz Scheduler: " + e.getMessage());
		}

	}

	public RecoveryPointSummary getRecoveryPointSummary() throws ServiceException {

		logger.debug("getAgentRecoveryPointSummary() - start");

		RecoveryPointSummary rpSummary = new RecoveryPointSummary();

		try {
			String nodeId = CommonService.getInstance().getNodeUUID();
			rpSummary.setNodeId(nodeId);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

		java.util.Calendar beginDate = java.util.Calendar.getInstance();
		beginDate.set(1970, 0, 1);
		java.util.Calendar endDate = java.util.Calendar.getInstance();
		endDate.set(2999, 11, 31);
		BackupConfiguration conf = this.getBackupConfiguration();
		if (!conf.isD2dOrRPSDestType()){
			logger.info("The destination is Datastore, get for RSP instead, ignore here");
			return null;
		}
		 RecoveryPoint[] recoveryPoints = RestoreService.getInstance().getRecoveryPoints(conf.getDestination(), "", conf.getUserName(), conf.getPassword(), beginDate.getTime(), endDate.getTime(), false);		 

		if (recoveryPoints != null) {		
			String backupDest = null;
			RecoveryInfoStatistics data = null;

			for (RecoveryPoint pi : recoveryPoints) {
				if (backupDest == null || !backupDest.equalsIgnoreCase(pi.getBackupDest())) {											
					data = new RecoveryInfoStatistics();
					data.setDestination(pi.getBackupDest());
					rpSummary.getStatisData().add(data);
					backupDest = pi.getBackupDest();
				}

				if (pi.getBackupType() == BackupType.Full || pi.getBackupType() == BackupType.Incremental || pi.getBackupType() == BackupType.Resync) {
					data.setDataSize(data.getDataSize() +  pi.getDataSize());
					data.setRawdataSize(data.getRawdataSize() +  pi.getLogicalSize());					
				}
			}
		}
		

		logger.debug("getAgentRecoveryPointSummary() - end");
		return rpSummary;
	}
	
	public List<String> getBackupDatastoreVolumes(BackupConfiguration configuration){
		return VolumnMapAdapter.getBackupDatastoreVolumes(configuration);
	}
}

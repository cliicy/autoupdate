package com.ca.arcflash.ui.client;

import java.util.Date;

import com.google.gwt.i18n.client.Messages;

public interface FlashUIMessages extends Messages {
	//Restore
	String restoreDestination(String destination);
	String restoreAlterPathError(String dbNames);
	String restoreAlterLongPathError(String dbName, int length);
	String restoreAlterPathSysError(String dbName);
	String restoreAlterDBNameError(String dbNames);
	String restoreAlterUserDBSysNameError(String dbNames);
	String restoreRSGNote(String publicFolders);
	String restoreExchangeGRTSummaryCount(int total, int edb, int mailbox, int folder, int mail);
	String restoreExchangeGRTSummaryCountDatabase(int count);
	String restoreExchangeGRTSummaryCountMailbox(int count);
	String restoreExchangeGRTSummaryCountFolder(int count);
	String restoreExchangeGRTSummaryCountMail(int count);
	String restoreExchangeGRTSummaryCountContact(int count);
	String restoreExchangeGRTSummaryCountContactGroup(int count);
	String restoreExchangeGRTSummaryCountCalendar(int count);				
	String restoreExchangeGRTSelectedMailsTooltip(int selectedMails);
	String restoreExchangeGRTSelectedMailboxesTooltip(int selectedMailboxes);
	String restoreExchangeGRTArchiveMailboxToOriginalLocation(String mailboxName);
	String restoreBrowseExchangeDataDescription(String link);
	String restoreSQLWriterToRemoteDisk(String productName);
	String restoreExchangeWriterToRemoteDisk(String productName);
	String restoreFailedStartFSCatalogJob(String sessionName);
	String restoreFSCatalogNotReady(String sessionName);
	String restoreFSCatalogDisabled(String sessionName);
	String restoreSearchFindInSession(String fileName, int sessionCount, String startTime, String endTime);
	String restoreSearchFindAll(String fileName);
	String restoreSearchFindFileCopyLocation(String fileName, String location);
	String restoreSourceIsNTFSDedupVolumeError(String volumeName,String osName);
	String restoreNtfsDedupVolumeToNonSysteomNonEmptyVolume(String srouceVolumeName,String destinationVolumeName);
	String restoreNtfsDedupVolumeToSystemOrRefsOrFat(String srouceVolumeName,String destinationVolumeName,String destinationVolumeType);
	String restoreNtfsDedupVolumeToNonSysteomEmptyVolume(String srouceVolumeName,String destinationVolumeName);
	String restoreNtfsDedupVolumeToNonSysteomNonEmptyVolumeWin8(String srouceVolumeName,String destinationVolumeName);
	String restoreVolumeSourceIsNTFSDedupVolumeError(String volumeName);
	String restoreFileSourceIsNTFSDedupVolumeError(String volumeName);
	String restoreSourceIsRefsVolumeCannotMount(String volume,String osName);
	String restoreSearchSourceIsRefsAndNotWin8(String osName);
	String restoreSearchSourceIsNtfsDedupAndNotWin8(String osName);
	String restoreSearchSourceIsNtfsDedupAndDedupNotInstall();
	String restoreOriginalWithoutOverwriteWarning(String checkBoxLabel);
	
	/*Retention policy*/
	String retentionCountToolTip(String productName);
	String retentionCountTotalCountWarning(int count);
	String retentionCountWeeklyWarning(String weekDay);
	String retentionCountMonthlyWarning(String weekDay);
	String retentionDailyWarning(String weekDay);
	String scheduleMaxItem(int maxCount);
	String scheduleMaxItemEx(int maxCount, String dayOfWeek);
	String scheduleReplicationMaxItemEx(int maxCount, String dayOfWeek);
	String scheduleThrottleMaxItem(int maxCount);
	String scheduleThrottleMaxItemEx(int maxCount, String dayOfWeek);
	String scheduleMergeMaxItem(int maxCount);
	String scheduleMergeMaxItemEx(int maxCount, String dayOfWeek);
	String scheduleMaxRepeatValue(int maxRepeatValue, String repeatUnit);
	String scheduleItemOverLap(String backupType, String startTime, String endTime);	
	String scheduleItemOverLapWithNoRepeat(String backupType, String startTime1, String startTime2);
	String scheduleItemNoRepeatSameStartTime(String backupType, String startTime);
	String scheduleThrottleItemOverLap(String startTime, String endTime);
	String scheduleMergeItemOverLap(String startTime, String endTime);
	String scheduleReplicationItemOverLap(String startTime, String endTime);
	String retentionCountNONNull(String type);
	String scheduleNoDailyBackupAfterDelete(String weekDay, String scheduledJobTypeNames);
	String scheduleStartTimeNoLaterThan(String time);

	String scheduleRpsNoDailyBackupAfterDelete(String weekDay);
	
	/* Backup Schedule */
	String scheduleLabelNeverTooltip(String backupType);
	String scheduleLabelRepeatTooltip(String backupType);
	String backupSettingsNodifications(int num);
	String backupSettingsNodificationsError(int num);
	String backupSettingsNodificationsWithError(int warning, int error);
	String ArchiveEnableLable();
	String fileArchiveEnableLable();
	String ArchiveSettingsNodifications();
	String ArchiveFileSourceNotification();
	String fileArchiveFileSourceNotification();
	String backupSettingsDestinationIsOnLocalDist(String volume);
	String backupSettingsDestChainOneOnLocal(String volume);
	String backupSettingsDestinationIsOnSystemVolume(String volume);
	String backupSettingsDestinationIsOnBootVolume(String volume);
	String backupSettingsOnSystemVolumeNotSelect(String volume);
	String refsVolumesSelect(String selectedRefsList);
	String backupSettingsOnBootVolumeNotSelect(String volume);
	String backupSettingsBootVolumeSelectESP(String volume);
	String backupSettingsEstimatedBakupSize(String size);
	String backupSettingsUsedSize(String size);
	String backupSettingsSelectVolumesSize(String size);
	String backupSettingsNotBackedAppComponents(String volume, String appComps);
	String backupSettingsDataStoreVolume(String rpsProductName, String volume);
	String notificationMessage(String symbolicPath, String actualPath);
	String notificationMessageGeneric();
	String archiveSystemVolumeNotification();
	String backupSettingsSpaceUsedByBackups(String size);
	String destinationNoDiskEnoughSpaceAndEstiCount(String size, long num);
	String backupSettingsVolumeCannotBackedUp(String volumesType);
	String backupSettingsVolume2TBType(String volumesType, String produceName);
	String backupSettingsVolume2TBTypeFullMachineBackup(String volumesType);
	String backupSettingsRemotePathInValid(String type);
	String settingsUpdateConnection(String dest);
	String settingDSTStartTime(String startTime, String interval);
	String settingDSTEndTime(String startTime, String interval, String productName);
	
	String refsVolumeName();
	String ntfsDedupeName();
	String refsFileCopyDescription(String volumes);
	
	/* Backup Settings */
	String backupSettingsWindowWithTap(String tapName);
	String destinationRemoteConnectExist(String remotePath);
	
	/* Homepage log out*/
	String homepageServerName(String serverName);
	String homepageServerNameForEdge(String serverName);
	
	//wanqi06
	String homepageSummaryRecoverySets(int count, int totalcount);
	String homepageSummaryRecoverySetWarningTooltip(int current, int total);
	
	
	/* Homepage - Summary */
	String homepageSummaryMostRecentBackupLabel(String backupType);
	String homepageSummaryRecoveryPoints(int count, int totalcount);
	String homepageSummaryDestinationCapacity(String freeSpace);
	String homepageSummaryDestThresholdReached(String freeSpace);
	String homepageSummaryDestFreeSizeLow(String freeSpace, int num);
	String homepageSummaryDestFreeSizeLowEx(String freeSpace);
	String homepageSummaryLegendBackup(String message);
	String homepageSummaryLegendFull(String message);
	String homepageSummaryLegendIncremental(String message);
	String homepageSummaryLegendFree(String message);
	String homepageSummaryLegendOthers(String message);
	String homepageNextScheduledEvent(String time, String backupType);
	String homepageSummaryDestinationVolume(String volume);
	String homepageSummaryDestinationPath(String path);
	String homepageSummaryDestinationTooltip(int percent, String freeSpace);
	String homepageSummaryDestinationDataStoreInfoTooltip();
	String homepageSummaryDestinationDataStoreWarnTooltip();
	String homepageSummaryDestinationDataStoreErrorTooltip();
	String homepageSummaryDestinationDataStoreUnknownTooltip();
	String homepageSummaryMostBackupStatus(String status);
	String homepageSummaryMostFullBackupStatus(String status);
	String homepageSummaryMostIncrementalBackupStatus(String status);
	String homepageSummaryRecoveryPointWarningTooltip(int current, int total);
	String homepageSummaryRecoveryPointsLarger(String sessionPath);
	String homepageSummaryRPSServerName(String name);
	String homepageSummaryRPSDataStoreName(String name);
	String homepageSummaryLicenseFailurefor(String comp);
	
	String homepageSummaryMostRecentArchiveLabel();
	String homepageSummaryMostRecentArchiveStatusLabel(String status);

	String homepageRecentBackupColumnToolTip(String size);
	String homepageRecentBackupLogicalSizeColumnToolTip(String size);
	
	String homepageDataProtectedColumnToolTip();
	String homepageSpaceOccupiedColumnToolTip();
	
	/*homepage - pie chart */
	String homepagePieChartInstallFlash(String posi);
	
	String fileRestoreOptionOverwrite(String message);
	String fileRestoreOptionReplaceActive(String message);
	String fileRestoreOptionBaseFolderWillNotBeCreated(String message);
	String trustHostSwitchServer(String server);
	String fileRestoreOptionRename(String message);
	String fileRestoreOptionSkip(String message);
	
	/* Job Monitor */
	String jobMonitorProgressBarLabel(int percentage, String processedSize, String totalSize);
	String jobMonitorSeconds(long seconds);
	String jobMonitorMinutesSeconds(long minutes, long seconds);
	String jobMonitorThroughout(String throughput);
	String jobMonitorThroughoutMbps(String throughput);
	String jobMonitorThroughoutKbps(String throughput);
	String jobMonitorThroughoutGPerMin(String throughput);
	String jobMonitorThroughoutGBPerMin(String throughput);
	String jobMonitorEncryptionAlgorithm(String algorithm);
	String jobMonitorPhaseBackupMergeSessions(long mergedSession, long totalMergedSessions);
	String jobMonitorReducedRatioDetail(String totalSize,String percentage, String actualSize,String reason);
	String jobMonitorFurtherReducedRatioDetail(String totalSize,String percentage, String actualSize,String reason);
	String jobMonitorOverallReducedDetail(String percentage);
	String jobMonitorVAppProgressBarLabel(int percentage, String processedVMs, String totalVMs);
	/* About Window */
	String aboutWindowReleaseNumber(String majorVersion, String minorVersion, String buildNumber);
	String aboutWindowUpdateNumber(String updateNumber);
	String aboutWindowUpdateBuildNumber(String updateNubmer,String updateBuildNumber);
	String aboutWindowReleaseDisplayVersion(String displayVersion);
	String aboutWindowBuildNumber(String majorVersion, String minorVersion, String buildNumber);
	
	/*home page login window*/
	String versionAndBuild(String majorVersion, String minorVersion, String buildNumber);
	String updateNumber(String updateNumber);
	String updateBuildNumber(String updateNumber,String updateBuildNumber);
	
	/* Network Path Connect Window */
	String connectToNetworkPath(String path);
	
	String isNotAllowed(String str);
	
	String FileCopyDestEmptyMessage();
	
	String restoreSearchmaxAllowShowItems(String count);
	
	String browseWindowCreateAFolderUnder(String foldername);
	
	String trustedHostWindowDeleteAlert(String server);
	String browse(String folderName);
	String backupSettingsErrorDaysTooLargeForNoLic(int days);
	String backupSettingsErrorHoursTooLargeForNoLic(int hours);
	String backupSettingsErrorMinutesTooLargeForNoLic(int minutes);
	String backupSettingsErrorHoursTooSmallForNoLic(int hours);
	String backupSettingsErrorMinutesTooSmallForNoLic(int minutes);
	
	String gettingStartedRemoteURL(String productName, String url);
	String upgradeWarningMessage(String build);
	String upgradeWaringTitle();
	
	String bytes(String num);
	String KB(String num);
	String MB(String num);
	String GB(String num);
	String settingsRetentionCountExceedMax(long max);
	String percentage(String num);
	
	//wanqi06
	String settingsBackupSetCountExceedMax(long max);
	//
	String moniteeStatusChange(String server);
	String moniteeSwitching(String server);

	String virtualConversionMonitorTitle(int convertingNum, int totalNum,  String productName);
	String virtualConversionProcessingAndTotal(int convertingNum, int totalNum);
	String virtualConversionSessionCreatedAt(String sessionName);
	String virtualConversionMonitorProcessing(String time);
	String replicaJobDetailWindowTitle(String productName);
	String virtualConversionMostRecent(String productName);
	String coldStandbyTaskEnableAutoOfflineCopy(String productName);
	String coldStandbyTaskDisableAutoOfflineCopy(String productName);
	String coldStandbyTaskSettings(String productName);
	String coldStandbyOfflineCopyCommandNowResult(String productName);
	String coldStandbyenEnableAutoOfflieCopyResult(String productName);
	String coldStandbyDisableAutoOfflieCopyResult(String productName);
	String coldStandbySettingEmailAlertReplicationError(String productName);
	String coldStandbySettingEmailAlertConversionSuccess(String productName);
	String homepageTaskVirtualConversion(String productName);
	String destinationColdstandbySettingMsg(String productName);
	
	String virtualConversionSummary(String server, String productName);
	String virtualConversionSummaryAvailablePoints(int num, String total);
	String virtualConversionSummaryDestFreeSpace(String dest, String freeSpace);
	String virtualConversionSummaryDestTitle(String dest);
	String virtualConversionTaskPanelConnectFails(String hostName);
	String virtualConversionTaskPanelReconnectFails(String hostName);
	String virtualConversionTaskPanelSwithServer(String hostName);
	String virtualConversionTaskPanelReconnectServer(String hostName);
	String virtualConvesionSnapshotPowerOnAnother(String onSnapshotBackupedTime, String toPowerOn);
	String coldStandbySettingTitle(String subTitle);
	String heartBeatInstructionText(String seconds);
	String testMonitorConnectionError(String server, int port, String protocol);
	String testMonitorConnectionESXError(String server, int port, String protocol);
	String coldStandbyInvalidHyperVPath(String path);
	String coldStandbyMaxMemorysize(long maxMemorysize);
	String coldStandbyVMNameExist(String vmName, String serverName);
	String coldStandbyConnectionConfirmMsg(String msg);
	String coldStandbyReplicaJobConnecting(String machine);
	String coldStandbyDatastoreSize(String datastoreName, long GB);
	String coldStandbyCheckResourcePool(String resourcePoolName, String resourcePoolRef, String esxHost);
	String coldStandbyUnSupportHyperVPath(String path);
	String testMonitorConnectionUnkownHost(String hyperVHost);
	String testMonitorConnectionFail(String productNameD2D, String hyperVHost);
	String coldStandbyMaxRecoveryPointCount(int maxRecoveryPointCount);
	
	String vSphereTestProxyWarning(String productName, String proxyName);
	String vSphereTestProxyHostNotFoundError(String proxyName,String proxyName1);
	String vSphereTestProxyWarningConfirmMsg(String warningMsg);
	String vSphereTestProxyLowerVersion(String proxyName);
	String vSphereDataStoreNotEnough(String datastore);
	String vSphereDatastoreFreeSize(String datastoreName,String freeSize);
	String vSphereVMToolNotInstall(String vmName);
	String vSphereVMToolOutOfDate(String vmName);
	String vSphereVixNotInstallWarning(String proxyName);
	String vSphereVixOutOfDateWarning(String proxyName);
	String vSphereChangeToOtherVM(String vmName);
	String vSphereVMProtectedByProxy(String proxyName);
	String vSphereVCCannotConnect(String vcName);
	String vSphereVCCredentialWrong(String vcName);
	String vSphereVCloudDirectorCannotConnect(String vCloudDirectorName);
	String vSphereVMPowerOff(String vmName);
	String vSphereVMSuspended(String vmName);
	String vSphereVMNotPowerOn(String vmName);
	String vSphereNetWorkNotConfig(String adapter);
	
	String vSphereHyperVHostDiskSizeNotEnough(String drive);
	String vSphereHyperVHostDriveNotExist();
	
	String arcFlashNodeRepPerText(String per);
	String arcFlashNodeRepToRepText(int toRep);
	
	String vSphereGetFlashResourceSizeFailed(String server);
	String vSphereGetFlashResourceSizeZero(String server);
	String vSphereGetFlashResourceSizeLsReadCache(String targetSize, String requiredSize, String server);
	
	//D2D Auto Update Messages
	String LoadingStatusWindowtitle();
	String D2DAutoUpdateMessageBoxTitle();
	String D2DAutoBIUpdateMessageBoxTitle();//added by cliicy.luo
	String D2DAutoUpdateFailedToGetStatusError(String strErrorMessage);
	String D2DAutoBIUpdateFailedToGetStatusError(String strErrorMessage);//added by cliicy.luo
	String SelectStagingServerToDelete();
	String D2DAutoUpdateIsdown(String productName);
	String InstallFailedAsD2DAutoUpdateIsdown(String productName);
	String D2DAutoUpdateBusyWithOtherRequest();
	String D2DAutoBIUpdateBusyWithOtherRequest();//added by cliicy.luo
	String D2DUpdateInstallSuccess();
	String D2DAutoUpdateInstallFailed();
	String D2DAutoUpdateInstallFailedDueToActiveJobs();
	
	String D2DAutoUpdateInstallFailedDueToD2DMissing(String productName);
	String D2DAutoUpdateInstallFailedDueToNotApplicable(String productName);
	String D2DAutoUpdateInstallFailedDueToInCompatiable(String productName);
	String D2DAutoUpdateInstallFailedDueToAlreadyInstalled();
	String D2DAutoUpdateInstallFailedDueToLatestVersionInstalled();
	String D2DAutoUpdateInstallFailedDueToInsfficientDiskSpace();
	String D2DAutoUpdateInstallFailedDueToUncompressFailed();
	
	
	String EnterValidProxyServerNameMessage();
	String EnterValidPortMessage(String strServerType);
	String EnterValidProxyUserNameMessage();
	String EnterValidProxyPasswordMessage();
	String D2DAutoUpdateStagingServerCannotBeLocalMachineMessage();
	String EnterValidStagingServerName();
	String D2DErroredInstallingUpdate(String strErrorMsg);
	String InValidCharactersServerNameFoundMessage(String strServerType);
	
	//Preferences
	String preferencesWindowWithTap(String tapName);
	String failedToSavePreferences();
	String D2DUnableToConnectDownloadServer(String productName);
	String D2DUnableToConnectDownloadBIServer(String productName);//added by cliicy.luo
	String SelectStagingServerToEdit();
	String SelectOneStagingServerToEdit();
	
	String AllUpdatesAppliedMessage();
	String D2DAutoUpdateNotConfigured();
	String D2DAutoUpdateNotEnabled();
	String NewD2DUpdatesareAvailableMessage();
	String AutoUpdatesAreEnabledMessage();
	
	String NewD2DBIUpdatesareAvailableMessage();//added by cliicy.luo
	
	String setSavingPreferencesMaskMessage();
	String setLoadingPreferencesMaskMessage();
	String CheckingForUpdatesMessage();
	String DownloadingUpdateMessage();
	String InstallingUpdateMessage();
	String CheckingDownloadStatus();
	String InstallingDownloadedUpdate();
	String SelectedServerAlreadyAddedMessage();
	
	String DownloadServerConnectionVerificationFailureError(String strMessage);
	String DownloadExceptionMessage(String strMessage);
	
	String SelectDownloadServerMessage();
	String SelectStagingServersMessage();
	String SelectDayForAutoUpdatesToRunMessage();
	String SelectHourForAutoUpdatesToRunMessage();
	String SelectAPMPMForAutoUpdatesToRunMessage();
	String MaximumStagingServerMessage();
	String DownloadServerTestWaitMessage();
	String EnterProxyServerNameMessage();
	String EnterProxyUserNameMessage();
	String EnterProxyPasswordMessage();
	String EnterPortMessage();
	String FailedToValidateProxyServerName();
	String ConnectionWithCAServeAvailable(String companyName);
	String ConnectionWithCAServerIsNotAvailable(String companyName);
	String FailedToValidateStagingServerName();
	String D2DIsUptoDate(String productName);
	String FailedToSubmitRequestToUpdateManager();
	String FailedToReadResponse();
	String FailedToGetUpdates();
	String CheckSummaryPanelNewUpdatesMessage(String productName);
	String D2DBackupsNotconfiguredMessage(String productName);
	String LoadingSummaryMessage(String productName);
	
	String archiveSettingsWindowWithTap(String in_strMessage);
	
	String EnterValidArchiveDestination();
	String EnterValidUniqueArchiveDestination();
	String EnterValidPurgeScheduleMessage();
	String NoPoliciesSelectedWarning();
	String homepageNextArchiveScheduledEvent(String strArchiveType,String time, String backupType);
	
	String ArchiveSourceLabel(String strType);
	String ArchiveFiltersDescription();
	String ArchiveSourceCannotbeBackupDestination();
	String ArchiveRefsSourceCannotbeBackupDestination();
	String ArchiveDedupeSourceCannotbeBackupDestination();
	
	String SelectArchiveOrFileCopyErrorMessage();
	String SelectArchiveSourceErrorMessage();
	String SelectCopyAndArchiveSourceErrorMessage();
	String SelectLocalArchiveSourceErrorMessage();
	String SelectValidFilterMessage();
	String SelectValidFilterTypeMessage();
	String SelectValidFilterValueMessage();
	String SelectValidFolderValueMessage();
	String SelectValidFilterLenghtMessage();
	String SelectValidFolderFilterValueMessage();
	String PleaseselectthefilterMessage();
	String SelectValidFileAccessTimeMessage();
	String SelectValidFileAccessTypeMessage();
	String SelectValidFileModifiedTimeMessage();
	String SelectValidFileModifiedTypeMessage();
	String SelectValidFileCreationTimeMessage();
	String SelectValidFileCreationTypeMessage();
	String SelectFilterToDeleteMessage();
	String SelectValidFileSizeMessage();
	String SelectValidFileSizeLowerMessage();
	String SelectValidFileSizeLowerUnitMessage();
	String SelectValidFileSizeHigherMessage();
	String SelectValidFileSizeLowerAndHigher();
	String SelectValidFileSizeHigherUnitMessage();
	String EnterValidNumberOfBackupsMessage(long lMaxBackups);
	String EnterValidNumberOfBackupsRangeMessage(String lMaxBackups);
	String enterValidBackupSchedule();
	String MinimumRetentionTimeForFilesAtDestinationMessage(long lMinRentionTime);
	String MinimumNumberFileVersionsMessage(long lMaxFileVersions);
	String MaximumNumberFileVersionsMessage(long lMaxFileVersions);
	String EnterEncryptionPasswordMessage();
	String EnterConfirmEncryptionPasswordMessage();
	String EncryptionPasswordNotverifiedMessage();
	String SelectDestinationToArchiveSourceMessage();
	String ConfigureCouldErrorMessage();
	String ArchiveDeletedFilesFoldersMessage();
	String FilterAlreadyAddedMessage();
	String EnterValidArchiveDestinationMessage();
	String EnterValidCredentialsMessage();
	String EnterValidCloudConfigMessage();
	String ArchiveSourceContainsRefsOrDedup(String sources);
	
	// scheduled export settings
	String settingsExportIntervalExceedMax(long max);
	String keepRecoveryPointsCountExceedMax(long max);
	
	//sync job
	String CatalogSyncJob(String date);
	String catalogDataForSelectedDestination();
	
	//srm pki
	String srmErrorParameterInvalid(int min, int max);
	
	//Add for change product name
	String windowTitle(String productName);
	
	String loginTitle(String productName);
	String loginButtonTooltip(String productName);
	
	String messageBoxTitleError(String productName);
	String messageBoxTitleWarning(String productName);
	String messageBoxTitleInformation(String productName);
	
	String scheduleLabelIncrementalDescription(String productName);
	String scheduleLabelFullDescription(String productName);
	String scheduleLabelResyncDescription(String productName);
	
	String settingsLabelRetentionDescription(String productName);
	String settingsMailServerTooltip(String productName);
	String settingsSubjectTooltip(String productName);
	String settingsProxyServerTooltip(String productName);
	
	String advancedDefaultEmailSubject(String productName);
	String restoreToOriginalLocationForVSphere(String productName);
	String restoreResolvingConflictsDescription(String productName);
	String restoreToFileSystemOverwriteDesc(String productName);
	String restoreCatalogJobIsRunning(String productName);
	
	String volumeGreyNotSupported(String productName);
	String homepageSupportFAQDescription(String productName);
	String homepageSupportSendFeedbackSubject(String productName);
	String remoteDeployPanelDeployNowTooltip(String productName);
//	String remoteDeployMsgInstallPathInvalid(String productName,String productName1);
	String remoteDeployMsgInstallPathInvalid(String productName);
	String remoteDeployMsgServerNameInvalid(String productName);
	String remoteDeployNotex86ToCompletePackage(String productName,String productName1);
	String remoteDeployNotex64ToCompletePackage(String productName,String productName1);
	
	String aboutWindowTitle(String productName);
	String aboutWindowProductName(String productName);
	String settingsFromTooltip(String productName);
	String gettingStartedTitle(String productName);
	String allFeedsSubscribe(String productName);
	String selectVideoSourceDescription(String productName);
	String resolvingConflictsDescription();
	
	String settingsTestMailSubject(String productName);
	String settingsTestMailContent(String productName);
	
	String useEdgePolicyAndCannotSaveSettings(String productName,String productName1);
	
	/*Add for customized product name and company name*/
	String arcserveD2DHelp(String productName);
	String aboutARCserveD2D(String productName);
	String settingsCompreesionAlert(String productName);
	String restoreValidateUserError5(String productName);
	String restoreToFileSystemOverwriteTooltip(String productName);
	String restoreToFileSystemRenameTooltip(String productName);
	String D2DUpdateRestartServiceDescription(String productName, String productName2);
//	String D2DAutoUpdateMessageBoxTitle(String productName);
	String updateScheduleDescription(String productName);
	String coldStandbySettingHyperVHostTip(String productName);
	String coldStandbySettingHyperVPortTip(String productName);
	String coldStandbySettingMonitorHostTip(String productName);
	String ArchiveAfterBackupLabel(String productName);
	String NumberOfBackupsTooltip(String productName);
	String scheduledExportToolTipRecoveryPoint(String productName);
	String installDriverStep(String productName);
	String homepageSupportCASupportDescription(String productName, String companyName);
	String D2DBackups(String productName);
	String coldStandbySettingConfigureBackupSysBoot(String productName);
	String coldStandbySettingConfigureBackupFullMachine(String productName);
	String notRestartAfterInstall(String productName);
	
	String DownloadFromCAServerLabel(String companyName);
	String aboutWindowLicense(String companyName);
	String aboutWindowCopyRight(String companyName);	
	String selectVideoCASupport(String companyName);
	String selectVideoCASupportDescription(String companyName);
	String PatchDetailsDialogDescription(String productName);
	String D2DUpdateLinkText(String companyName);
	String preferencesDownloadServerDescription(String companyName);
	String preferencesDownloadFromCAServerLabel(String companyName);
	String UseCASupportVideos(String companyName);
	
	String coldStandbyWelcomeSecondVideoTitle(String productName);
	String ArchiveappliesExcludebeforeIncludepoliciesMessage(String productName);
	String ArchiveappliesANDforCriteriaMessage(String productName);
	String notInstallDriver(String productName);
	String ToolTipBrowserProxySettings(String productName);
	String ArchiveSourceisNotBackupVolumeMessage(String productName);
	String ArchiveSymbolicSourceisNotBackupVolumeMessage(String symbolicPath,String actualPath,String productName);
	String SourcePathNotExistMessage(String sourcePath);
	String ArchiveSourceisNotRemote(String in_ArchiveSource);
	
	String coldStandbyWelcomeFirstVideoDescription(String productNameD2D);
	String coldStandbyWelcomeSecondVideoDescription(String productNameD2D);
	String coldStandbySettingHyperVProtocolTip(String productNameD2D);
	String coldStandbySettingVirtualizationFailedConnectESXMonitor(String productNameD2D);
	String coldStandbySettingVirtualizationFailedConnectHyperVMonitor(String productNameD2D);
	
	String addServerNotSameType(String productName);

	//D2D Mount tool
	String mountVolText(String mountedPath);
	String mountVolDestinationNotAccessible(String destPath);
	
	//Merge
	String mergejobPanelPauseButtonWarning();
	String mergeJobTimeIntervalLimit(int timeInterval);
	String mergeNotInScheduleSummary(String time);
	String mergeVHDProgress(int merged, int total);
	

	String cloudNoBucketsRetreived(String hostName, String hostNameForNewPrefix);
	String cloudNoContainersRetreived(String hostName, String hostNameForNewPrefix);
	
	//RPS
	String rpsHomepageServerName(String hostName, String rpsProductName);
	String rpsMessageFailLoadPolicy(String policyName, String rpsProductName);
	String rpsPolicyNotExist(String policyName,String rpsServerName, String rpsProductName);
	String rpsDataStoreNotExist(String storeName,String rpsServerName, String rpsProductName);
	String rpsPolicyNameChanged(String oldName,String newName);
	String rpsDataStoreNameChanged(String oldName,String newName);
	String rpsSettingsBasicFileStoreLimit(String message);
	String rpsMessageSettingsFailConnect(String rpsServerName, String rpsProductName);
	String rpsJobMonitorCancelAlert(String jobId);
	String loadingInfo(String rpsProductName);
	String loadingDataStore(String rpsProductName);
	String InputRightInfo(String rpsProductName);
	String loadingRpsHostInfo(String rpsProductName);
	String failToGetRpsServerList(String rpsProductName);
	String hostnameCannotBeBlank(String rpsProductName);
	String rpsServerSetting(String rpsProductName);
	String restoreLocationRPS(String rpsProductName);
	String restoreNoRPSServerSelected(String rpsProductName);
	String restoreChangeRPSServer(String rpsProductName);
	String restoreD2DSelectionFailToGetD2D(String rpsProductName, String d2dProductName);
	String restoreD2DSelectionNoD2D(String d2dProductName, String rpsProductName);
	String rpsInstNavigatorName(String rpsProductName);
	String rpsHomepageTask(String rpsProductName);
	String rpsTrustHostServerLabel(String rpsProductName);
	String rpsSettingsWindow(String rpsProductName);
	String rpsSettingsPathLocation(String rpsProductName);
	String rpsPolicyUpdatingPolicy(String rpsProductName);
	String UseRPS(String rpsProductName);
	String rpsTrustHostServerTooltip(String rpsProductName);
	String rpsSettingBrowseLocation(String rpsProductName);
	String rpsSettingsFailConnect(String rpsProductName);
	String rpsCheckingManagedStautsNotManaged(String rpsProductName);
	String sessionPwdNull(String productName);
	String backupRPSDestHostNameIsNull(String productName);
	String backupRPSDestUserNameIsNull(String productName);
	String backupRPSDestPwdIsNull(String productName);
	String backupRPSPolicyCannotBeBlank(String productName);
	String restoreRPSDataStoreCannotBeBlank(String productName);
	String noRPSPolicy(String productName, String rpsHost);
	//RPS Dedup
	String rpsMaximumMemorySize(String maxMemorySize);
	String rpsMessageFailLoadDedupStore(String dedupStoreName, String rpsProductName);
	String rpsDatastoreHashEstimate(String uniqueData, int dedupRatio, String totalData );
	//restore
	String selectRPS(String rpsProductName);
	String selectBackupD2D(String d2dProductName);
	String d2dEmpty(String d2dProductName);
	String rpsChangeTooltip(String rpsProductName);
	
	
	String scheduleSummaryRepeatBackup(int count);
	String scheduleSummaryRepeatBackupRepeat(String interval, String from, String to, String backupType);
	String scheduleSummaryThrottling(int count);
	String scheduleSummaryThrottlingLimit(Long speed, String from, String to);
	String scheduleSummaryRepeatBackupMerge(int count);
	String scheduleSummaryRepeatBackupMergeAllow(String from, String to);
	String scheduleSummaryRepeatBackupRention(int backups);	
	String scheduleSummaryDailyRentention(int days);
	String scheduleSummaryWeeklyRentention(int weeks);
	String scheduleSummaryMonthlyRentention(int months);
	String scheduleSummaryRetentionLastBackups(int count);
	String scheduleSummaryDailyBackupSchedule(String bkpTye, String time);
	String scheduleSummaryWeeklyBackupSchedule(String bkpTye, String time, String weekday);
	String scheduleSummaryMonthlyBackupScheduleWeekOfMonth(String bkpTye, String time, String weekNum, String weekday);
	String scheduleSummaryMonthlyBackupScheduleLastDay(String bkpTye, String time);
	String scheduleSummaryMonthlyBackupScheduleDayOfMonth(String bkpTye, String time, int day);
	
	// Virtual Standby Pai Chart
	String virtualStandbySize(String size);
	
	String repeatEvery(int interval, String unit);
	
	String hyperVNetwork(int number);
	String hyperVLegacyNetwork(int number);
	
	String unknown_vm(String vmName);
	String repeatRecoveryPointsStatus(int count, int totalcount); 
	String dailyRecoveryPointsStatus(int count, int totalcount);
	String weeklyRecoveryPointsStatus(int count, int totalcount);
	String monthlyRecoveryPointsStatus(int count, int totalcount);
	
	String sampleTextMessage(String message);
	
	String scheduleDescriptionBackup(String type, String repeatString);
	String scheduleDescriptionBackupFull(String repeatString);
	String scheduleDescriptionBackupVerify(String repeatString);
	String scheduleDescriptionBackupNonRepeat(String type, String time);
	String scheduleDescriptionThrottle(Long value,String throttleUnit);
	String scheduleTimeRange(String startTime, String endTime);
	String scheduleDescriptionDailyBackup(String type);
	String scheduleDescriptionWeeklyBackup(String type);
	String scheduleDescriptionMonthlyBackup(String type);
	String scheduleMergeItemOverLapEx(String startTime, String endTime, String day);
	String scheduleThrottleItemOverLapEx(String startTime, String endTime, String day);
	String scheduleItemOverLapWithDay(String backupType, String startTime, String endTime, String day);	
	
	String HBBUTruncateLogNote(String functionName);
	String HBBURunCommandNote(String functionName);
	
	String ADNodePath(String path);
	String selectedNumberAndTotalNumber(int count, int total);
	String ADNodeFullySelectedTooltip(int count);
	String ADAttributeSelectedTooltip(int count);
	String restoreOptionADRenamedObjects(String message);
	String restoreOptionADMovedObjects(String message);
	String restoreOptionADDeletedObjects(String message);
	String purchaseLicDescription(String purchaseLic);
	
	String vAppRestoreNoVDCNetworks(String vDCName);
	String vAppRestoreChildVMNodeName(String vmName);
	String vAppRestoreDuplicatedVMNames(String vmName, String nodeNames);
	String vAppRestoreDataStoreNotEnough(String dataStoreName, String freeSize, String totalDiskSize);
	String vAppRestorePolicyNotEnough(String policyName, String freeSize, String totalDiskSize);
	String vAppRestoreMaxMemorySize(Long size);
	String vAppRestoreVMAllDisks(int count);
	String vAppRestoreVMAllAdapters(int size);
	String vAppRestoreVMAdapterName(int index);
	String vAppRestoreVMDataStoreNotEnough(String dataStoreName, String freeSize, String totalDiskSize, String nodeName);
	String vAppRestoreVMPolicyNotEnough(String policyName, String freeSize, String totalDiskSize, String nodeName);
	String vAppRestoreVMNetworkNotConfig(String adpaterNames, String nodeName);
	String vAppRestoreMemorySizeLargerMax(Long maximum, Long size);
	String vAppRestoreMemorySizeLargeVDCLimit(String vDCName, Long limitedSize, Long size);
	String vAppRestoreTotalMemorySizeNotEnough(String vDCName, Long limitedSize, Long size);
	String vAppRestoreVAppNetworkNotConfig(String vAppAdapters);
	
	String vAppRestoreVCloudTreeConnectError(String vcName);
	String vAppRestoreVCloudTreeVerifyDescription(String vcName);
	String vAppRestoreVCloudTreeVerifiedDescription(String vcName);
	String vAppRestoreVCloudTreeFailedToVerify(String vcName);
	
	//File Copy
	String dailyScheduleNotConfiguredInBackup();
	String weeklyScheduleNotConfiguredInBackup();
	String monthlyScheduleNotConfiguredInBackup();
	
	//ASBU
	String loadingMediaGroupDetails();
	String loadingMediaGroupList();
	String deletingArcserveBackupServer();
	
	//linux job monitor
	String linuxJobMonitorBackupVolume(String volume);
	String linuxJobMonitorRestoreVolume(String volume);
	
	//aerp notifications
	String messages(int msgCount);
	String messagesUserNotRegisteredNotification();
	String messagesUserNotActivatedNotificationForEmail(String emailID);
	String messagesUserNotActivatedNotification();
	String messagesSendNewRegistrationEmail();
	String messagesRegister();
	String getRegisterPolicyLabel(String url);
    String getEUModuleClauseLabel(String url);
    

	
	String scheduleCatalogExchNotRequired(String link);
	String scheduleGRTUtilityCatalogExchNotNecessary(String link);
	
	//test connection
	String testConnectionSuccess();
	
	//restore
	String restoreFailureForRPS(String hostName);
	String restoreFailureForNode(String hostName);
	
	String vmWareVMExists(String esxName);
	
	//jvm out of memory message
	String jvmOutOfMemoryGuide();
	
	String troubleShootingLink(String link);
}
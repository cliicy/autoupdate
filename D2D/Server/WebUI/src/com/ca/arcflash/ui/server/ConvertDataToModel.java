package com.ca.arcflash.ui.server;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.ui.client.backup.schedule.AdvanceScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.DailyScheduleDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryDayScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryMonthScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryWeekScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.MergeDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.PeriodScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.ThrottleModel;
import com.ca.arcflash.ui.client.model.BackupScheduleModel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupVolumeModel;
import com.ca.arcflash.ui.client.model.D2DTimeModel;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.ca.arcflash.ui.client.model.FileModel;
import com.ca.arcflash.ui.client.model.MergeJobMonitorModel;
import com.ca.arcflash.ui.client.model.MergeStatusModel;
import com.ca.arcflash.ui.client.model.PatchInfoModel;
import com.ca.arcflash.ui.client.model.ProxySettingsModel;
import com.ca.arcflash.ui.client.model.RetentionPolicyModel;
import com.ca.arcflash.ui.client.model.StagingServerModel;
import com.ca.arcflash.ui.client.model.UpdateSettingsModel;
import com.ca.arcflash.ui.client.model.VolumeModel;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.D2DTime;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.MergeDetailItem;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcflash.webservice.data.ThrottleItem;
import com.ca.arcflash.webservice.data.PM.AutoUpdateSettings;
import com.ca.arcflash.webservice.data.PM.PatchInfo;
import com.ca.arcflash.webservice.data.PM.ProxySettings;
import com.ca.arcflash.webservice.data.PM.StagingServerSettings;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupSchedule;
import com.ca.arcflash.webservice.data.backup.BackupVolumes;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.merge.MergeJobMonitor;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;

public class ConvertDataToModel {

	public static BackupSettingsModel ConvertBackupConfigToModel(BackupConfiguration bc) {
		BackupSettingsModel model = new BackupSettingsModel();
		//Convert to Model Object and Return
		model.setDestination(bc.getDestination());
		model.setDestUserName(bc.getUserName());
		model.setDestPassword(bc.getPassword());
		model.setCommandAfterBackup(bc.getCommandAfterBackup());
		model.setCommandBeforeBackup(bc.getCommandBeforeBackup());
		model.setCommandAfterSnapshot(bc.getCommandAfterSnapshot());
		model.setChangedBackupDest(bc.isChangedBackupDest());
		model.setChangedBackupDestType(bc.getChangedBackupDestType());
		model.setBackupStartTime(bc.getBackupStartTime());
		model.startTime = convertToTimeModel(bc.getStartTime());
		model.retentionPolicy = convertToRetentionPolicyModel(bc.getRetentionPolicy());
		if(model.retentionPolicy != null) {
			model.retentionPolicy.setRetentionCount(bc.getRetentionCount());
		}
		
		model.setActionsUserName(bc.getPrePostUserName());
		model.setActionsPassword(bc.getPrePostPassword());
		
		model.setRetentionCount(bc.getRetentionCount());		
		model.setCompressionLevel(bc.getCompressionLevel());
		model.setEnableEncryption(bc.isEnableEncryption());
		model.setPreExitCode(bc.getPreExitCode());
		model.setSkipJob(bc.isSkipJob());
		model.setEnablePreExitCode(bc.isEnablePreExitCode());
		
		model.setPurgeSQLLogDays(bc.getPurgeSQLLogDays());
		model.setPurgeExchangeLogDays(bc.getPurgeExchangeLogDays());
		
		model.setAdminUserName(bc.getAdminUserName());
		model.setAdminPassword(bc.getAdminPassword());
		
		model.setEnableSpaceNotification(bc.isEnableSpaceNotification());
		model.setSpaceMeasureNum(bc.getSpaceMeasureNum());
		model.setSpaceMeasureUnit(bc.getSpaceMeasureUnit());
		model.setSpaceSavedAfterCompression(bc.getSpaceSavedAfterCompression());
		model.setGrowthRate(bc.getGrowthRate());
		if (bc.getEmail() != null)
		{		
			model.setContent(bc.getEmail().getContent());
			model.setEnableEmailOnMissedJob(bc.getEmail().isEnableEmailOnMissedJob());
			model.setEnableEmail(bc.getEmail().isEnableEmail());
			model.setEnableEmailOnSuccess(bc.getEmail().isEnableEmailOnSuccess());
			model.setEnableEmailOnMergeFailure(bc.getEmail().isEnableEmailOnMergeFailure());
			model.setEnableEmailOnMergeSuccess(bc.getEmail().isEnableEmailOnMergeSuccess());
			model.setEnableProxy(bc.getEmail().isEnableProxy());
			model.setFromAddress(bc.getEmail().getFromAddress());
			model.setProxyAddress(bc.getEmail().getProxyAddress());
			model.setProxyPassword(bc.getEmail().getProxyPassword());
			model.setProxyPort(bc.getEmail().getProxyPort());
			model.setProxyUsername(bc.getEmail().getProxyUsername());
			model.setSubject(bc.getEmail().getSubject());		
			model.setSMTP(bc.getEmail().getSmtp());
			model.setEnableHTMLFormat(bc.getEmail().isEnableHTMLFormat());
			/** alert email PR */
			model.setMailPwd(bc.getEmail().getMailPassword());
			model.setMailService(bc.getEmail().getMailServiceName());
			model.setEnableSsl(bc.getEmail().isEnableSsl());
			model.setEnableTls(bc.getEmail().isEnableTls());
			model.setMailUser(bc.getEmail().getMailUser());
			model.setSmtpPort(bc.getEmail().getSmtpPort());
			model.setEnableMailAuth(bc.getEmail().isMailAuth());
			model.setEnableProxyAuth(bc.getEmail().isProxyAuth());

			
			model.Recipients = new ArrayList<String>();
			if (bc.getEmail().getRecipients() != null)
			{

					model.Recipients.addAll(bc.getEmail().getRecipients());
				
			}
		}
				
		if (bc.getIncrementalBackupSchedule() != null)
		{
			model.incrementalSchedule =
				ConvertToBackupSettingsScheduleModel(bc.getIncrementalBackupSchedule());
		}		
		if (bc.getFullBackupSchedule() != null)
		{
			model.fullSchedule =
				ConvertToBackupSettingsScheduleModel(bc.getFullBackupSchedule());
		}
		if (bc.getResyncBackupSchedule() != null)
		{
			model.resyncSchedule =
				ConvertToBackupSettingsScheduleModel(bc.getResyncBackupSchedule());
		}
		if(bc.getBackupVolumes() != null)
		{
			model.backupVolumes = ConvertToBackupSettingsVolumeModel(bc.getBackupVolumes());
		}
		return model;
	}

	public static BackupVolumeModel ConvertToBackupSettingsVolumeModel(
			BackupVolumes backupVolumes) {
		BackupVolumeModel model = new BackupVolumeModel();
		model.setIsFullMachine(backupVolumes.isFullMachine());
		
		String[] volumes = backupVolumes.getVolumes().toArray(new String[0]);
		if(volumes != null && volumes.length > 0) {
			model.selectedVolumesList = java.util.Arrays.asList(volumes);
		}
		return model;
	}
	
	public static BackupScheduleModel ConvertToBackupSettingsScheduleModel(
			BackupSchedule schedule) {
		BackupScheduleModel model = new BackupScheduleModel();
		
		model.setEnabled(schedule.isEnabled());
		if (schedule.isEnabled())
		{
			model.setInterval(schedule.getInterval());
			model.setIntervalUnit(schedule.getIntervalUnit());
		}
		
		return model;
	}
	
	public static FileModel ConvertToVolumeModel(Volume vol) {
		VolumeModel m = new VolumeModel();

		m.setGUID(vol.getGuid());
		m.setName(vol.getName());
		m.setPath(vol.getName());
		m.setType(vol.getType());
		m.setTotalSize(vol.getSize());
		m.setFreeSize(vol.getFreeSize());
		m.setLayout(vol.getLayOut());
		m.setFileSysType(vol.getFsType());
		m.setStatus(vol.getStatus());
		m.setSubStatus(vol.getSubStatus());
		m.setIsShow(vol.getIsShow());
		m.setMsgID(vol.getMsgID());
		m.setDisplayName(vol.getDisplayName());
		
		return m;		
	}
	
	public static UpdateSettingsModel convertToUpdateSettingsModel(
			AutoUpdateSettings in_updateSettings) {

		UpdateSettingsModel updateModel = null;

		if(in_updateSettings != null)
		{
			updateModel = new UpdateSettingsModel();
			updateModel.setDownloadServerType(in_updateSettings.getServerType());
			if(in_updateSettings.getServerType() == 1)
			{
				StagingServerSettings[] stagingServers = in_updateSettings.getStagingServers();
				int istagingServersCount = stagingServers.length;

				StagingServerModel[] serverModels = new StagingServerModel[istagingServersCount];
				for(int iIndex = 0;iIndex < istagingServersCount;iIndex++)
				{
					StagingServerModel serverModel = new StagingServerModel();
					serverModel.setStagingServer(stagingServers[iIndex].getStagingServer());
					serverModel.setStagingServerPort(stagingServers[iIndex].getStagingServerPort());
					serverModel.setStagingServerStatus(stagingServers[iIndex].getStagingServerStatus());
					serverModel.setStagingServerId(stagingServers[iIndex].getServerId());
					serverModels[iIndex] = serverModel;
				}
				updateModel.setStagingServers(serverModels);
				updateModel.setCAServerStatus(in_updateSettings.getiCAServerStatus());
			}
			else
			{
				updateModel.setCAServerStatus(in_updateSettings.getiCAServerStatus());
			}

			updateModel.setAutoCheckupdate(in_updateSettings.isScheduleType());
			if(in_updateSettings.isScheduleType())
			{
				updateModel.setScheduledWeekDay(in_updateSettings.getScheduledWeekDay());
				updateModel.setScheduledHour(in_updateSettings.getScheduledHour());
			}

			updateModel.setD2DBackupsConfigured(in_updateSettings.isBackupsConfigured());

			ProxySettingsModel proxyModel = new ProxySettingsModel();
			ProxySettings proxyConfig = in_updateSettings.getproxySettings();
			proxyModel.setUseProxy(proxyConfig.isUseProxy());
			if(proxyModel.getUseProxy())
			{
				proxyModel.setProxyServerName(proxyConfig.getProxyServerName());
				proxyModel.setProxyPort(proxyConfig.getProxyServerPort());

				proxyModel.setProxyRequiresAuth(proxyConfig.isProxyRequiresAuth());
				if(proxyConfig.isProxyRequiresAuth())
				{
					proxyModel.setProxyUserName(proxyConfig.getProxyUserName());
					proxyModel.setProxyPassword(proxyConfig.getProxyPassword());
				}
			}

			updateModel.setproxySettings(proxyModel);
		}
		return updateModel;
	}
	
	public static AutoUpdateSettings convertToData(
			UpdateSettingsModel updateSettingsModel) {
		if(updateSettingsModel == null)
			return null;
		AutoUpdateSettings AutoUpdateConfig = new AutoUpdateSettings();
		
		AutoUpdateConfig.setServerType(updateSettingsModel.getDownloadServerType());
		//if(updateSettingsModel.getDownloadServerType() == 1)
		//{
		AutoUpdateConfig.setStagingServers(convertToData(updateSettingsModel.getStagingServers()));
		//}

		boolean bAutoCheckUpdate = updateSettingsModel.getAutoCheckupdate() != null ? updateSettingsModel.getAutoCheckupdate() : false;
		AutoUpdateConfig.setScheduleType(bAutoCheckUpdate);
		if(bAutoCheckUpdate)
		{
			AutoUpdateConfig.setScheduledWeekDay(updateSettingsModel.getScheduledWeekDay() != null ? updateSettingsModel.getScheduledWeekDay() : -1);

			int iScheduledHour = updateSettingsModel.getScheduledHour() != null ? updateSettingsModel.getScheduledHour() : -1;
			AutoUpdateConfig.setScheduledHour(iScheduledHour);
		}

		AutoUpdateConfig.setproxySettings(convertToData(updateSettingsModel.getproxySettings()));

		return AutoUpdateConfig;
	}
	
	private static StagingServerSettings[] convertToData(
			StagingServerModel[] in_stagingServers) {
		StagingServerSettings[] stagingServersList = null;
		if(in_stagingServers != null)
		{
			int istagingServersCount = in_stagingServers.length;

			if(istagingServersCount > 0)
			{
				stagingServersList = new StagingServerSettings[istagingServersCount];

				for(int istagingServerIndex = 0;istagingServerIndex < istagingServersCount;istagingServerIndex++)
				{
					StagingServerModel serverModel = in_stagingServers[istagingServerIndex];

					stagingServersList[istagingServerIndex] = new StagingServerSettings();
					stagingServersList[istagingServerIndex].setStagingServer(serverModel.getStagingServer());
					stagingServersList[istagingServerIndex].setStagingServerPort(serverModel.getStagingServerPort());
					stagingServersList[istagingServerIndex].setServerId(serverModel.getStagingServerId());
				}
			}
		}
		return stagingServersList;
	}


	private static ProxySettings convertToData(
			ProxySettingsModel proxySettingsModel) {

		ProxySettings proxyConfig = new ProxySettings();

		if(proxySettingsModel == null)
		{
			proxyConfig.setUseProxy(false);
		}
		else
		{
			proxyConfig.setUseProxy(proxySettingsModel.getUseProxy());
			if(proxySettingsModel.getUseProxy())
			{
				proxyConfig.setProxyServerName(proxySettingsModel.getProxyServerName());
				proxyConfig.setProxyServerPort(proxySettingsModel.getProxyPort());

				proxyConfig.setProxyRequiresAuth(proxySettingsModel.getProxyRequiresAuth());
				if(proxySettingsModel.getProxyRequiresAuth())
				{
					proxyConfig.setProxyUserName(proxySettingsModel.getProxyUserName());
					proxyConfig.setProxyPassword(proxySettingsModel.getProxyPassword());
				}
			}
		}
		return proxyConfig;
	}
	
	public static PatchInfoModel ConvertPatchInfoToModel(PatchInfo in_patchInfo)
	{
		PatchInfoModel patchInfoModel = new PatchInfoModel();

		//product information
		patchInfoModel.setMajorversion(in_patchInfo.getMajorversion());
		patchInfoModel.setMinorVersion(in_patchInfo.getMinorVersion());
		patchInfoModel.setServicePack(in_patchInfo.getServicePack());

		////patch information
		patchInfoModel.setPackageID(in_patchInfo.getPackageID());
		patchInfoModel.setPublishedDate(in_patchInfo.getPublishedDate());
		patchInfoModel.setDescription(in_patchInfo.getDescription());
		patchInfoModel.setPatchDownloadLocation(in_patchInfo.getPatchDownloadLocation());
		patchInfoModel.setPatchURL(in_patchInfo.getPatchURL());
		patchInfoModel.setRebootRequired(in_patchInfo.getRebootRequired());
		patchInfoModel.setSize(in_patchInfo.getSize());
		patchInfoModel.setPatchVersionNumber(in_patchInfo.getPatchVersionNumber());
		patchInfoModel.setAvailableStatus(in_patchInfo.getAvailableStatus());
		patchInfoModel.setDownloadStatus(in_patchInfo.getDownloadStatus());
		patchInfoModel.setInstallStatus(in_patchInfo.getInstallStatus());
		patchInfoModel.setErrorMessage(in_patchInfo.getErrorMessage());
		patchInfoModel.setError_Status(in_patchInfo.getError_Status());

		return patchInfoModel;
	}
	
	public static PatchInfo convertToData(PatchInfoModel in_PatchinfoModel)
	{
		PatchInfo patchInfo = new PatchInfo();

		//product information
		patchInfo.setMajorversion(in_PatchinfoModel.getMajorversion());
		patchInfo.setMinorVersion(in_PatchinfoModel.getMinorVersion());
		patchInfo.setServicePack(in_PatchinfoModel.getServicePack());

		//patch information
		patchInfo.setPackageID(in_PatchinfoModel.getPackageID());
		patchInfo.setPublishedDate(in_PatchinfoModel.getPublishedDate());
		patchInfo.setDescription(in_PatchinfoModel.getDescription());
		patchInfo.setPatchDownloadLocation(in_PatchinfoModel.getPatchDownloadLocation());
		patchInfo.setPatchURL(in_PatchinfoModel.getPatchURL());
		patchInfo.setRebootRequired(in_PatchinfoModel.getRebootRequired());
		patchInfo.setSize(in_PatchinfoModel.getSize());
		patchInfo.setPatchVersionNumber(in_PatchinfoModel.getPatchVersionNumber());
		patchInfo.setAvailableStatus(in_PatchinfoModel.getAvailableStatus());
		patchInfo.setDownloadStatus(in_PatchinfoModel.getDownloadStatus());
		patchInfo.setInstallStatus(in_PatchinfoModel.getInstallStatus());
		patchInfo.setErrorMessage(in_PatchinfoModel.getErrorMessage());
		patchInfo.setError_Status(in_PatchinfoModel.getError_Status());

		return patchInfo;
	}
	private static D2DTimeModel convertToTimeModel(D2DTime time) {
		if(time == null)
			return null;
		D2DTimeModel model = new D2DTimeModel();
		model.setYear(time.getYear());
		model.setMonth(time.getMonth());
		model.setDay(time.getDay());
		model.setHour(time.getHour());
		model.setMinute(time.getMinute());
		model.setAMPM(time.getAmPM());
		model.setHourOfDay(time.getHourOfday());
		return model;
	}
	
	private static RetentionPolicyModel convertToRetentionPolicyModel(RetentionPolicy policy) {
		if(policy == null)
			return null;
		RetentionPolicyModel model = new RetentionPolicyModel();
		model.setUseBackupSet(policy.isUseBackupSet());
		if(policy.isUseBackupSet()) {
			
		}else {
			model.setUseTimeRange(policy.isUseTimeRange());
			if(policy.isUseTimeRange()) {
				model.setEndTimeHour(policy.getEndHour());
				model.setEndTimeMinutes(policy.getEndMinutes());
				model.setStartTimeHour(policy.getStartHour());
				model.setStartTimeMinutes(policy.getStartMinutes());
			}
		}
		return model;
	}
	
	public static MergeStatusModel mergeStatus2MergeStatusModel(MergeStatus status) {
		MergeStatusModel model = new MergeStatusModel();
		if(status == null)
			return model;
		switch(status.getStatus()) {
		case FAILED:
			model.setStatus(MergeStatusModel.FAILED);
			break;
		case NOTRUNNING:
			model.setStatus(MergeStatusModel.NOTRUNNING);
			break;
		case TORUNN:
			model.setStatus(MergeStatusModel.TO_RUN);
			break;
		case PAUSED:			
			model.setStatus(MergeStatusModel.PAUSED);
			break;
		case PAUSED_MANUALLY:
			model.setStatus(MergeStatusModel.PAUSED_MANUALLY);
			break;	
		case RUNNING:
			model.setStatus(MergeStatusModel.RUNNING);
			break;
		case PAUSING:
			model.setStatus(MergeStatusModel.PAUSING);
			break;
		case PAUSED_NO_SCHEDULE:
			model.setStatus(MergeStatusModel.PAUSED_NO_SCHEDULE);
			break;
		}
		
		if(status.getJobMonitor() != null)
			model.jobMonitor = convertToMergeJobMonitorModel(status.getJobMonitor());
		model.setCanResume(status.isCanResume());
		model.setInSchedule(status.isInSchedule());
		model.setUUID(status.getUUID());
		model.setRecoverySet(status.isRecoverySet());
		model.setUpdateTime(status.getUpdateTime());
		model.setJobType(status.getJobType());
		return model;
	}
	
	public static MergeJobMonitorModel convertToMergeJobMonitorModel(MergeJobMonitor jm) {
		if(jm == null)
			return null;
		MergeJobMonitorModel model = new MergeJobMonitorModel();
		model.setJobId(jm.getDwJobID());
		model.setJobPhase(jm.getDwMergePhase());
		model.setJobStatus(jm.getJobStatus());
		model.setCurDiskSig2Merge(jm.getDwCurDiskSig2Merge());
		model.setCurSess2Merge(jm.getDwCurSess2Merge());
		model.setDiskBytes2Merge(jm.getUllDiskBytes2Merge());
		model.setDiskBytesMerged(jm.getUllDiskBytesMerged());
		model.setDiskCnt2Merge(jm.getDwDiskCnt2Merge());
		model.setDiskCntMerged(jm.getDwDiskCntMerged());
		model.setEndStart(jm.getDwEndStart());
		model.setMergeMethod(jm.getDwMergeMethod());
		model.setMergeOpt(jm.getDwMergeOpt());
		model.setMergePercentage(jm.getfMergePercentage());
		model.setRetentionCnt(jm.getDwRetentionCnt());
		model.setSessBytes2Merge(jm.getUllSessBytes2Merge());
		model.setSessBytesMerged(jm.getUllSessBytesMerged());
		model.setSessCnt2Merge(jm.getDwSessCnt2Merge());
		model.setSessCntMerged(jm.getDwSessCntMerged());
		model.setSessStart(jm.getDwSessStart());
		model.setElapsedTime(jm.getUllElapsedTime());
		model.setStartTime(jm.getUllStartTime());
		model.setTotalBytes2Merge(jm.getUllTotalBytes2Merge());
		model.setTotalBytesMerged(jm.getUllTotalBytesMerged());
		model.setVmInstanceUUID(jm.getVmInstanceUUID());
		model.setTimeRemain(jm.getTimeRemain());
		model.setVHDMerge(jm.isVHDMerge());		
		model.setSessRangeCnt(jm.getDwSessRangeCnt());
		model.setCurrentMergeRangeStart(jm.getCurrentMergeRangeStart());
		model.setCurrentMergeRangeEnd(jm.getCurrentMergeRangeEnd());
		return model;
	}
	
	// zhazh06. retention policy
	private static DayTimeModel ConvertToDayTimeModel(DayTime time) {
		DayTimeModel model = new DayTimeModel();
		model.setHour(time.getHour());
		model.setMinute(time.getMinute());
		return model;
	}
/*	// zhazh06. retention policy
	public static RetentionModel ConvertToRetentionModel(RetentionSetting setting) {
		RetentionModel model = new RetentionModel();
		if(setting == null){
			return model;
		}
	
		if (setting.getDailyBackupTime() != null)
			model.dailyBackupTime = ConvertToDayTimeModel(setting.getDailyBackupTime());

		model.setDailyUseLastBackup(setting.isDailyUseLastBackup());
		model.setWeeklyBackupTime(setting.getWeeklyBackup());

		model.setMonthlyBackupTime(setting.getMonthlyBackup());

		model.setMonthlyUseLastBackup(setting.isMonthlyUseLastBackup());
		
		return model;
	}*/
	
	//wanqi06
	public static AdvanceScheduleModel convertToAdvanceScheduleModel(AdvanceSchedule schedule){
		AdvanceScheduleModel model = null;
		if(schedule!=null){
			model = new AdvanceScheduleModel();
			//model.setIsEnableBackup(schedule.isEnabled());
			ArrayList<DailyScheduleDetailItemModel> daylyModels = new ArrayList<DailyScheduleDetailItemModel>();			
			List<DailyScheduleDetailItem> daylyLists = schedule.getDailyScheduleDetailItems();
			if(daylyLists!=null){
				for (DailyScheduleDetailItem daylyItem : daylyLists) {					
					daylyModels.add(convertToDaylyScheduleDetailItemModel(daylyItem));
				}
			}
			
			model.daylyScheduleDetailItemModel = daylyModels;
			
			if(schedule.getPeriodSchedule() != null){	
				model.periodScheduleModel = convertToPeriodScheduleModel(schedule.getPeriodSchedule());				
			}
			
			model.setBackupStartTime(schedule.getScheduleStartTime());
		}
		
		return model;
	}
	
	private static PeriodScheduleModel convertToPeriodScheduleModel(PeriodSchedule periodSchedule) {
		
		PeriodScheduleModel periodScheduleModel  = new PeriodScheduleModel();
		if(periodSchedule.getDaySchedule() != null){
			periodScheduleModel.dayScheduleModel = new EveryDayScheduleModel();
			periodScheduleModel.dayScheduleModel.setBkpType(periodSchedule.getDaySchedule().getBkpType());		
			periodScheduleModel.dayScheduleModel.setDayTime(ConvertToDayTimeModel(periodSchedule.getDaySchedule().getDayTime()));
			periodScheduleModel.dayScheduleModel.setEnabled(periodSchedule.getDaySchedule().isEnabled());
			periodScheduleModel.dayScheduleModel.setGenerateCatalog(periodSchedule.getDaySchedule().isGenerateCatalog());
			periodScheduleModel.dayScheduleModel.setRetentionCount(periodSchedule.getDaySchedule().getRetentionCount());
			periodScheduleModel.dayScheduleModel.setCheckRecoveryPoint(periodSchedule.getDaySchedule().isCheckRecoveryPoint());
		}

		if(periodSchedule.getWeekSchedule() != null){
			periodScheduleModel.weekScheduleModel = new EveryWeekScheduleModel();		
			periodScheduleModel.weekScheduleModel.setBkpType(periodSchedule.getWeekSchedule().getBkpType());		
			periodScheduleModel.weekScheduleModel.setDayTime(ConvertToDayTimeModel(periodSchedule.getWeekSchedule().getDayTime()));
			periodScheduleModel.weekScheduleModel.setEnabled(periodSchedule.getWeekSchedule().isEnabled());
			periodScheduleModel.weekScheduleModel.setGenerateCatalog(periodSchedule.getWeekSchedule().isGenerateCatalog());
			periodScheduleModel.weekScheduleModel.setRetentionCount(periodSchedule.getWeekSchedule().getRetentionCount());
			periodScheduleModel.weekScheduleModel.setDayOfWeek(periodSchedule.getWeekSchedule().getDayOfWeek());
			periodScheduleModel.weekScheduleModel.setCheckRecoveryPoint(periodSchedule.getWeekSchedule().isCheckRecoveryPoint());
		}
		
		if(periodSchedule.getMonthSchedule() != null){
			periodScheduleModel.monthScheduleModel = new EveryMonthScheduleModel();
			periodScheduleModel.monthScheduleModel.setBkpType(periodSchedule.getMonthSchedule().getBkpType());		
			periodScheduleModel.monthScheduleModel.setDayTime(ConvertToDayTimeModel(periodSchedule.getMonthSchedule().getDayTime()));
			periodScheduleModel.monthScheduleModel.setEnabled(periodSchedule.getMonthSchedule().isEnabled());
			periodScheduleModel.monthScheduleModel.setGenerateCatalog(periodSchedule.getMonthSchedule().isGenerateCatalog());
			periodScheduleModel.monthScheduleModel.setRetentionCount(periodSchedule.getMonthSchedule().getRetentionCount());
			periodScheduleModel.monthScheduleModel.setDayOfMonth(periodSchedule.getMonthSchedule().getDayOfMonth());
			periodScheduleModel.monthScheduleModel.setWeekDayOfMonth(periodSchedule.getMonthSchedule().getWeekDayOfMonth());
			periodScheduleModel.monthScheduleModel.setWeekNumOfMonth(periodSchedule.getMonthSchedule().getWeekNumOfMonth());
			periodScheduleModel.monthScheduleModel.setWeekOfMonthEnabled(periodSchedule.getMonthSchedule().isWeekOfMonthEnabled());
			periodScheduleModel.monthScheduleModel.setDayOfMonthEnabled(periodSchedule.getMonthSchedule().isDayOfMonthEnabled());
			periodScheduleModel.monthScheduleModel.setCheckRecoveryPoint(periodSchedule.getMonthSchedule().isCheckRecoveryPoint());
		}
		return periodScheduleModel;
	}

	private static DailyScheduleDetailItemModel convertToDaylyScheduleDetailItemModel(DailyScheduleDetailItem daylyItem){
		
		DailyScheduleDetailItemModel daylyModel = new DailyScheduleDetailItemModel();
		daylyModel.dayOfweek = daylyItem.getDayofWeek();
		
		ArrayList<ScheduleDetailItemModel> itemModels = new ArrayList<ScheduleDetailItemModel>();
		ArrayList<ThrottleModel> throttleModels = new ArrayList<ThrottleModel>();
		ArrayList<MergeDetailItemModel> mergeModels = new ArrayList<MergeDetailItemModel>();
		
		ArrayList<ScheduleDetailItem> itemsLists = daylyItem.getScheduleDetailItems();
		if(itemsLists!=null){
			for (ScheduleDetailItem item : itemsLists) {
				itemModels.add(convertToScheduleDetailItemModel(item));
			}
		}
		
		ArrayList<ThrottleItem> throttleItems = daylyItem.getThrottleItems();
		if(throttleItems!=null){
			for (ThrottleItem item : throttleItems) {
				throttleModels.add(convertToThrottleItemModel(item));
			}
		}
		
		ArrayList<MergeDetailItem> mergeItems = daylyItem.getMergeDetailItems();
		if(mergeItems!=null){
			for (MergeDetailItem item : mergeItems) {
				mergeModels.add(convertToMergeDetailItemModel(item));
			}
		}
		daylyModel.scheduleDetailItemModels = itemModels;
		daylyModel.throttleModels = throttleModels;
		daylyModel.mergeModels = mergeModels;
		return daylyModel;
	}
	
	private static ScheduleDetailItemModel convertToScheduleDetailItemModel(ScheduleDetailItem item){
		ScheduleDetailItemModel model = new ScheduleDetailItemModel();
		model.setJobType(item.getJobType());
		model.startTimeModel = ConvertToDayTimeModel(item.getStartTime());
		model.endTimeModel = ConvertToDayTimeModel(item.getEndTime());
		model.setInterval(item.getInterval());
		model.setIntervalUnit(item.getIntervalUnit());
		model.setRepeatEnabled(item.isRepeatEnabled());
		
		return model;
	}
	
	private static ThrottleModel convertToThrottleItemModel(ThrottleItem item){
		ThrottleModel model = new ThrottleModel();
		model.startTimeModel = ConvertToDayTimeModel(item.getStartTime());
		model.endTimeModel = ConvertToDayTimeModel(item.getEndTime());
		model.setThrottleValue(item.getThrottleValue());
		model.setUnit(item.getUnit());
		return model;
	}
	
	private static MergeDetailItemModel convertToMergeDetailItemModel(MergeDetailItem item){
		MergeDetailItemModel model = new MergeDetailItemModel();
		model.startTimeModel = ConvertToDayTimeModel(item.getStartTime());
		model.endTimeModel = ConvertToDayTimeModel(item.getEndTime());
		return model;
	}
	
	public static RpsHost convertToData(RpsHostModel model){
		if(model == null)
			return null;
		RpsHost host = new RpsHost();
		host.setHttpProtocol(model.getIsHttpProtocol());
		host.setPassword(model.getPassword());
		host.setPort(model.getPort());
		host.setRhostname(model.getHostName());
		host.setUsername(model.getUserName());
		return host;
	}
}

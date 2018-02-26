package com.ca.arcflash.webservice.util;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.BackupInformationSummary;
import com.ca.arcflash.webservice.data.D2DServerInfo;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.PM.PreferencesConfigurationOEM;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfigurationOEM;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupConfigurationOEM;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfigurationOEM;
import com.ca.arcflash.webservice.data.job.BackupSummary;
import com.ca.arcflash.webservice.data.job.D2DJobMonitor;
import com.ca.arcflash.webservice.data.merge.MergeJobMonitor;
import com.ca.arcflash.webservice.service.JobMonitorService;

public class OEMAdapter {
	
	public static PreferencesConfiguration convertPreferenceConfiguration(
			PreferencesConfigurationOEM pcOEM) {
		// TODO Auto-generated method stub
		PreferencesConfiguration pc = new PreferencesConfiguration();
		pc.setEmailAlerts(pcOEM.getEmailAlerts());
		return pc;
	}

	public static ArchiveConfiguration convertToArchiveConfiguration(
			ArchiveConfigurationOEM acOEM) {
		// TODO Auto-generated method stub
		ArchiveConfiguration ac = new ArchiveConfiguration();
		ac.setArchiveSources(acOEM.getArchiveSources());
		ac.setbackupDestination(acOEM.getbackupDestination());
		ac.setBackupVolumes(acOEM.getBackupVolumes());
		ac.setbArchiveAfterBackup(acOEM.isbArchiveAfterBackup());
		ac.setbArchiveExcludeAppFiles(acOEM.isbArchiveExcludeAppFiles());
		ac.setbArchiveExcludeSystemFiles(acOEM.isbArchiveExcludeSystemFiles());
		ac.setbArchiveToCloud(acOEM.isbArchiveToCloud());
		ac.setbArchiveToDrive(acOEM.isbArchiveToDrive());
		ac.setbEncryption(acOEM.isbEncryption());
		ac.setbPurgeArchiveItems(acOEM.isbPurgeArchiveItems());
		ac.setbPurgeScheduleAvailable(acOEM.isbPurgeScheduleAvailable());
		ac.setCloudConfig(acOEM.getCloudConfig());
		ac.setCompressionLevel(acOEM.getCompressionLevel());
		ac.setEncryptionPassword(acOEM.getEncryptionPassword());
		ac.setFileVersionRetentionCount(acOEM.getFileVersionRetentionCount());
		ac.setiArchiveAfterNBackups(acOEM.getiArchiveAfterNBackups());
		ac.setiPurgeAfterDays(acOEM.getiPurgeAfterDays());
		ac.setiSpaceUtilization(acOEM.getiSpaceUtilization());
		ac.setlPurgeStartTime(acOEM.getlPurgeStartTime());
		ac.setRetentiontime(acOEM.getRetentiontime());
		ac.setRRSFlag(acOEM.getRRSFlag());
		ac.setStrArchiveDestinationPassword(acOEM.getStrArchiveDestinationPassword());
		ac.setStrArchiveDestinationUserName(acOEM.getStrArchiveDestinationUserName());
		ac.setStrArchiveToDrivePath(acOEM.getStrArchiveToDrivePath());
		
		return ac;
	}

	public static ScheduledExportConfiguration convertToExportConfiguration(
			ScheduledExportConfigurationOEM seOEM) {
		// TODO Auto-generated method stub
		ScheduledExportConfiguration se = new ScheduledExportConfiguration();
		se.setCompressionLevel(seOEM.getCompressionLevel());			
		se.setDestination(seOEM.getDestination());
		se.setDestPassword(seOEM.getDestPassword());
		se.setDestUserName(seOEM.getDestUserName());
		se.setEnableScheduledExport(seOEM.isEnableScheduledExport());
		se.setEncryptionAlgorithm(seOEM.getEncryptionAlgorithm());
		se.setEncryptionKey(seOEM.getEncryptionKey());
		se.setExportInterval(seOEM.getExportInterval());
		se.setKeepRecoveryPoints(seOEM.getKeepRecoveryPoints());		
		return se;
	}

	public static BackupConfiguration convertToBackupConfiguration(
			BackupConfigurationOEM bcOEM) {
		BackupConfiguration bc = new BackupConfiguration();
		bc.setAdminPassword(bcOEM.getAdminPassword());
		bc.setAdminUserName(bcOEM.getAdminUserName());
		bc.setBackupStartTime(bcOEM.getBackupStartTime());
		bc.setBackupVolumes(bcOEM.getBackupVolumes());
		bc.setChangedBackupDest(bcOEM.isChangedBackupDest());
		bc.setChangedBackupDestType(bcOEM.getChangedBackupDestType());
		bc.setCommandAfterBackup(bcOEM.getCommandAfterBackup());
		bc.setCommandAfterSnapshot(bcOEM.getCommandAfterSnapshot());
		bc.setCommandBeforeBackup(bcOEM.getCommandBeforeBackup());
		bc.setCompressionLevel(bcOEM.getCompressionLevel());
		bc.setDestination(bcOEM.getDestination());
		bc.setEnableEncryption(bcOEM.isEnableEncryption());
		bc.setEnablePreExitCode(bcOEM.isEnablePreExitCode());
		bc.setEnableSpaceNotification(bcOEM.isEnableSpaceNotification());
		bc.setEncryptionAlgorithm(bcOEM.getEncryptionAlgorithm());
		bc.setEncryptionKey(bcOEM.getEncryptionKey());
		bc.setExchangeGRTSetting(bcOEM.getExchangeGRTSetting());
		bc.setFullBackupSchedule(bcOEM.getFullBackupSchedule());
		bc.setGenerateCatalog(bcOEM.isGenerateCatalog());
		bc.setGrowthRate(bcOEM.getGrowthRate());
		bc.setIncrementalBackupSchedule(bcOEM.getIncrementalBackupSchedule());
		bc.setMajorVersion(bcOEM.getMajorVersion());
		bc.setMinorVersion(bcOEM.getMinorVersion());
		bc.setPassword(bcOEM.getPassword());
		bc.setPreAllocationBackupSpace(bcOEM.getPreAllocationBackupSpace());
		bc.setPreExitCode(bcOEM.getPreExitCode());
		bc.setPrePostPassword(bcOEM.getPrePostPassword());
		bc.setPrePostUserName(bcOEM.getPrePostUserName());
		bc.setPurgeExchangeLogDays(bcOEM.getPurgeExchangeLogDays());
		bc.setResyncBackupSchedule(bcOEM.getResyncBackupSchedule());
		bc.setRetentionCount(bcOEM.getRetentionCount());
		bc.setRetentionPolicy(bcOEM.getRetentionPolicy());
		bc.setSharePointGRTSetting(bcOEM.getSharePointGRTSetting());
		bc.setSkipJob(bcOEM.isSkipJob());
		bc.setSpaceMeasureNum(bcOEM.getSpaceMeasureNum());
		bc.setSpaceMeasureUnit(bcOEM.getSpaceMeasureUnit());
		bc.setSpaceSavedAfterCompression(bcOEM.getSpaceSavedAfterCompression());
		bc.setStartTime(bcOEM.getStartTime());
		bc.setThrottling(bcOEM.getThrottling());
		bc.setUserName(bcOEM.getUserName());
		return bc;
	}

	
	
	public static PreferencesConfigurationOEM convertToPreferenceOEM(
			PreferencesConfiguration pc) {
		// TODO Auto-generated method stub
		PreferencesConfigurationOEM pcOEM = new PreferencesConfigurationOEM();
		pcOEM.setEmailAlerts(pc.getEmailAlerts());
		return pcOEM;
	}

	public static ArchiveConfigurationOEM convertToArchiveOEM(ArchiveConfiguration ac) {
		// TODO Auto-generated method stub
		ArchiveConfigurationOEM acOEM = new ArchiveConfigurationOEM();
		acOEM.setArchiveSources(ac.getArchiveSources());
		acOEM.setbackupDestination(ac.getbackupDestination());
		acOEM.setBackupVolumes(ac.getBackupVolumes());
		acOEM.setbArchiveAfterBackup(ac.isbArchiveAfterBackup());
		acOEM.setbArchiveExcludeAppFiles(ac.isbArchiveExcludeAppFiles());
		acOEM.setbArchiveExcludeSystemFiles(ac.isbArchiveExcludeSystemFiles());
		acOEM.setbArchiveToCloud(ac.isbArchiveToCloud());
		acOEM.setbArchiveToDrive(ac.isbArchiveToDrive());
		acOEM.setbEncryption(ac.isbEncryption());
		acOEM.setbPurgeArchiveItems(ac.isbPurgeArchiveItems());
		acOEM.setbPurgeScheduleAvailable(ac.isbPurgeScheduleAvailable());
		acOEM.setCloudConfig(ac.getCloudConfig());
		acOEM.setCompressionLevel(ac.getCompressionLevel());
		acOEM.setEncryptionPassword(ac.getEncryptionPassword());
		acOEM.setFileVersionRetentionCount(ac.getFileVersionRetentionCount());
		acOEM.setiArchiveAfterNBackups(ac.getiArchiveAfterNBackups());
		acOEM.setiPurgeAfterDays(ac.getiPurgeAfterDays());
		acOEM.setiSpaceUtilization(ac.getiSpaceUtilization());
		acOEM.setlPurgeStartTime(ac.getlPurgeStartTime());
		acOEM.setRetentiontime(ac.getRetentiontime());
		acOEM.setRRSFlag(ac.getRRSFlag());
		acOEM.setStrArchiveDestinationPassword(ac.getStrArchiveDestinationPassword());
		acOEM.setStrArchiveDestinationUserName(ac.getStrArchiveDestinationUserName());
		acOEM.setStrArchiveToDrivePath(ac.getStrArchiveToDrivePath());
		return acOEM;
	}

	public static ScheduledExportConfigurationOEM convertToExportOEM(
			ScheduledExportConfiguration se) {
		// TODO Auto-generated method stub
		ScheduledExportConfigurationOEM scOEM = new ScheduledExportConfigurationOEM();
		scOEM.setCompressionLevel(se.getCompressionLevel());
		scOEM.setDestination(se.getDestination());
		scOEM.setDestPassword(se.getDestPassword());
		scOEM.setDestUserName(se.getDestUserName());
		scOEM.setEnableScheduledExport(se.isEnableScheduledExport());
		scOEM.setEncryptionAlgorithm(se.getEncryptionAlgorithm());
		scOEM.setEncryptionKey(se.getEncryptionKey());
		scOEM.setExportInterval(se.getExportInterval());
		scOEM.setKeepRecoveryPoints(se.getKeepRecoveryPoints());
		return scOEM;
	}

	public static BackupConfigurationOEM convertToBackupOEM(BackupConfiguration bc) {
		// TODO Auto-generated method stub
		BackupConfigurationOEM bcOEM = new BackupConfigurationOEM();
		bcOEM.setAdminPassword(bc.getAdminPassword());
		bcOEM.setAdminUserName(bc.getAdminUserName());
		bcOEM.setBackupStartTime(bc.getBackupStartTime());
		bcOEM.setBackupVolumes(bc.getBackupVolumes());
		bcOEM.setChangedBackupDest(bc.isChangedBackupDest());
		bcOEM.setChangedBackupDestType(bc.getChangedBackupDestType());
		bcOEM.setCommandAfterBackup(bc.getCommandAfterBackup());
		bcOEM.setCommandAfterSnapshot(bc.getCommandAfterSnapshot());
		bcOEM.setCommandBeforeBackup(bc.getCommandBeforeBackup());
		bcOEM.setCompressionLevel(bc.getCompressionLevel());
		bcOEM.setDestination(bc.getDestination());
		bcOEM.setEnableEncryption(bc.isEnableEncryption());
		bcOEM.setEnablePreExitCode(bc.isEnablePreExitCode());
		bcOEM.setEnableSpaceNotification(bc.isEnableSpaceNotification());
		bcOEM.setEncryptionAlgorithm(bc.getEncryptionAlgorithm());
		bcOEM.setEncryptionKey(bc.getEncryptionKey());
		bcOEM.setExchangeGRTSetting(bc.getExchangeGRTSetting());
		bcOEM.setFullBackupSchedule(bc.getFullBackupSchedule());
		bcOEM.setGenerateCatalog(bc.isGenerateCatalog());
		bcOEM.setGrowthRate(bc.getGrowthRate());
		bcOEM.setIncrementalBackupSchedule(bc.getIncrementalBackupSchedule());
		bcOEM.setMajorVersion(bc.getMajorVersion());
		bcOEM.setMinorVersion(bc.getMinorVersion());
		bcOEM.setPassword(bc.getPassword());
		bcOEM.setPreAllocationBackupSpace(bc.getPreAllocationBackupSpace());
		bcOEM.setPreExitCode(bc.getPreExitCode());
		bcOEM.setPrePostPassword(bc.getPrePostPassword());
		bcOEM.setPrePostUserName(bc.getPrePostUserName());
		bcOEM.setPurgeExchangeLogDays(bc.getPurgeExchangeLogDays());
		bcOEM.setResyncBackupSchedule(bc.getResyncBackupSchedule());
		bcOEM.setRetentionCount(bc.getRetentionCount());
		bcOEM.setRetentionPolicy(bc.getRetentionPolicy());
		bcOEM.setSharePointGRTSetting(bc.getSharePointGRTSetting());
		bcOEM.setSkipJob(bc.isSkipJob());
		bcOEM.setSpaceMeasureNum(bc.getSpaceMeasureNum());
		bcOEM.setSpaceMeasureUnit(bc.getSpaceMeasureUnit());
		bcOEM.setSpaceSavedAfterCompression(bc.getSpaceSavedAfterCompression());
		bcOEM.setStartTime(bc.getStartTime());
		bcOEM.setThrottling(bc.getThrottling());
		bcOEM.setUserName(bc.getUserName());
		return bcOEM;
	}
	
	public static D2DJobMonitor convertToJobMonitorOEM(JobMonitor jm){
		D2DJobMonitor D2Djm = new D2DJobMonitor();
		D2Djm.setJobId(jm.getJobId());
		D2Djm.setJobType(jm.getJobType());
		D2Djm.setJobStatus(jm.getJobStatus());
		D2Djm.setStartTime(jm.getBackupStartTime());
		D2Djm.setJobPhase(jm.getJobPhase());
		D2Djm.setJobMethod(jm.getJobMethod());
				
		long totalSize = jm.getEstimateBytesJob();
		long processedSize = jm.getTransferBytesJob();
		if(totalSize > 0) {
			float percent = ((float)processedSize)/totalSize;
			if(percent < 1) {
				D2Djm.setProgress(percent);
			} else {
				D2Djm.setProgress(1);
			}						
		} else {
			D2Djm.setProgress(0);
			}
		
		if(jm.getElapsedTime() > 1000) {
			if(totalSize > 0) {
				if(processedSize > totalSize) {
					totalSize = processedSize;
				}
				long remainSize = totalSize - processedSize;
				long temp = (processedSize / jm.getElapsedTime());
				long remainTime = 0;
				if(temp == 0) {
					D2Djm.setRemainTime(0);
				}
				else {
					remainTime = remainSize / temp;
					D2Djm.setRemainTime(remainTime);
				}
			} else {
				D2Djm.setRemainTime(0);
			}
		} else {
			D2Djm.setRemainTime(0);
		}
		return D2Djm;
	}
	
	public static D2DJobMonitor[] convertToJobMonitorOEM(
			JobMonitor[] jms) {
		if(jms == null || jms.length < 0)
			return null;
		List<D2DJobMonitor> d2dJms = new ArrayList<D2DJobMonitor>();
		for(int i = 0; i < jms.length; i ++){
			if(JobMonitorService.getInstance().isValidJobMonitor(jms[i]))
				d2dJms.add(convertToJobMonitorOEM(jms[i]));
		}
				
		return d2dJms.toArray(new D2DJobMonitor[0]);
	}
	
	public static BackupSummary convertToBackupSummary(BackupInformationSummary sum) {
		BackupSummary summary = new BackupSummary();
		summary.setBackupDestination(sum.getBackupDestination());
		summary.setDestinationCapacity(sum.getDestinationCapacity());
		summary.setErrorCode(sum.getErrorCode());
		summary.setRecentFullBackup(sum.getRecentFullBackup());
		summary.setRecentIncrementalBackup(sum.getRecentIncrementalBackup());
		summary.setRecentResyncBackup(sum.getRecentResyncBackup());
		summary.setRecoveryPointCount(sum.getRecoveryPointCount());
		summary.setRetentionCount(sum.getRetentionCount());
		summary.setSpaceMeasureNum(sum.getSpaceMeasureNum());
		summary.setSpaceMeasureUnit(sum.getSpaceMeasureUnit());
		summary.setTotalCanceledCount(sum.getTotalCanceledCount());
		summary.setTotalCrashedCount(sum.getTotalCrashedCount());
		summary.setTotalFailedCount(sum.getTotalFailedCount());
		summary.setTotalSuccessfulCount(sum.getTotalSuccessfulCount());
		
		return summary;
	}
	
	public static D2DServerInfo convertToD2DServerInfo(VersionInfo info) {
		D2DServerInfo serverInfo = new D2DServerInfo();
		serverInfo.setBuildNumber(info.getBuildNumber());
		serverInfo.setMajorVersion(info.getMajorVersion());
		serverInfo.setMinorVersion(info.getMinorVersion());
		serverInfo.setTimeZoneID(info.getTimeZoneID());
		serverInfo.setTimeZoneOffset(info.getTimeZoneOffset());
		serverInfo.setUefiFirmware(info.isUefiFirmware());
		serverInfo.setUpdateNumber(info.getUpdateNumber());		
		return serverInfo;
	}
	
	public static D2DJobMonitor convertToD2DJobMonitor(MergeJobMonitor mjm){
		if(mjm == null)
			return null;
		
		D2DJobMonitor jm = new D2DJobMonitor();
		jm.setJobId(mjm.getDwJobID());
		jm.setJobPhase(mjm.getDwMergePhase());
		jm.setJobStatus(mjm.getJobStatus());
		jm.setJobType(JobType.JOBTYPE_MERGE);
		jm.setProgress(mjm.getfMergePercentage());
		jm.setRemainTime(mjm.getTimeRemain());
		jm.setStartTime(mjm.getUllStartTime());
		//TODO jm.setProcessing(processing);//
		return jm;
	}
	
}

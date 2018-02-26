package com.ca.arcflash.webservice.util;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.ha.vcloudmanager.utilities.StringUtils;
import com.ca.arcflash.service.jni.model.JNetConnInfo;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.EveryMonthSchedule;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfigurationConstants;
import com.ca.arcflash.webservice.data.archive.ArchiveSourceInfoConfiguration;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.service.DeleteArchiveService;

public class ArchiveConfigUtils {
	private static final Logger logger = Logger.getLogger(ArchiveConfigUtils.class);
	
	private int archivePolicyCount;
	private int filecopyPolicyCount;

	public void separateConfiguration(ArchiveConfiguration archiveConfig){
		initPolicyCount(archiveConfig.getArchiveSources());
		if(hasArchivePolicy()){
			setArchiveDelConfiguration(archiveConfig);
		}
		if(hasFilecopyPolicy()){
			ArchiveSourceInfoConfiguration[] arrFilecopyPolicy = new ArchiveSourceInfoConfiguration[filecopyPolicyCount];
			int index = 0;
			for(int i=0;i<archiveConfig.getArchiveSources().length;i++){
				if(archiveConfig.getArchiveSources()[i].isbCopyFiles()){
					arrFilecopyPolicy[index] = archiveConfig.getArchiveSources()[i];
					index++;
				}
			}
			archiveConfig.setArchiveSources(arrFilecopyPolicy);
			archiveConfig.setAdvanceSchedule(null);
		} else {
			archiveConfig.setbArchiveAfterBackup(false);
		}
		archiveConfig.setbPurgeArchiveItems(false);
		archiveConfig.setbPurgeScheduleAvailable(false);
		setFCPurgeJobFlag();
	}
	
	private void initPolicyCount(ArchiveSourceInfoConfiguration[] archiveSources){
		archivePolicyCount = 0;
		filecopyPolicyCount = 0;
		for(ArchiveSourceInfoConfiguration archiveSource : archiveSources){
			if(archiveSource.isbArchiveFiles())
				archivePolicyCount++;
			if(archiveSource.isbCopyFiles())
				filecopyPolicyCount++;
		}
		logger.debug("archive policy count=" + archivePolicyCount + ", filecopy policy count=" + filecopyPolicyCount);
	}
	
	private boolean hasArchivePolicy(){
		return archivePolicyCount > 0;
	}
	
	private boolean hasFilecopyPolicy(){
		return filecopyPolicyCount > 0;
	}
	
	private void setArchiveDelConfiguration(ArchiveConfiguration archiveConfig) {
		ArchiveConfiguration archiveDelConfig = new ArchiveConfiguration();
		ArchiveSourceInfoConfiguration[] arrArchivePolicy = new ArchiveSourceInfoConfiguration[archivePolicyCount];
		int index = 0;
		for(int i=0;i<archiveConfig.getArchiveSources().length;i++){
			if(archiveConfig.getArchiveSources()[i].isbArchiveFiles()){
				arrArchivePolicy[index] = archiveConfig.getArchiveSources()[i];
				index++;
			}
		}
		archiveDelConfig.setArchiveSources(arrArchivePolicy);
		archiveDelConfig.setbackupDestination(archiveConfig.getbackupDestination());
		archiveDelConfig.setBackupVolumes(archiveConfig.getBackupVolumes());
		archiveDelConfig.setbArchiveAfterBackup(true);
		archiveDelConfig.setbArchiveExcludeAppFiles(archiveConfig.isbArchiveExcludeAppFiles());
		archiveDelConfig.setbArchiveExcludeSystemFiles(archiveConfig.isbArchiveExcludeSystemFiles());
		archiveDelConfig.setbArchiveToCloud(archiveConfig.isbArchiveToCloud());
		archiveDelConfig.setbArchiveToDrive(archiveConfig.isbArchiveToDrive());
		archiveDelConfig.setbDailyBackup(false);
		archiveDelConfig.setbEncryption(archiveConfig.isbEncryption());
		archiveDelConfig.setbMonthlyBackup(false);
		archiveDelConfig.setbPurgeArchiveItems(true);
		archiveDelConfig.setbPurgeScheduleAvailable(true);
		archiveDelConfig.setbWeeklyBackup(false);
		if(hasFilecopyPolicy() && archiveConfig.isbArchiveToCloud()){
			archiveDelConfig.setCloudConfig(getSeparateCloudInfo(archiveConfig.getCloudConfig()));
		}else{
			archiveDelConfig.setCloudConfig(archiveConfig.getCloudConfig());
		}
		archiveDelConfig.setCompressionLevel(archiveConfig.getCompressionLevel());
		archiveDelConfig.setEncryptionPassword(archiveConfig.getEncryptionPassword());
		archiveDelConfig.setFilesRetentionTime(archiveConfig.getFilesRetentionTime());
		archiveDelConfig.setFileVersionRetentionCount(archiveConfig.getFileVersionRetentionCount());
		archiveDelConfig.setiArchiveAfterNBackups(archiveConfig.getiArchiveAfterNBackups());
		archiveDelConfig.setiPurgeAfterDays(archiveConfig.getiPurgeAfterDays());
		archiveDelConfig.setiSpaceUtilization(archiveConfig.getiSpaceUtilization());
		archiveDelConfig.setlPurgeStartTime(archiveConfig.getlPurgeStartTime());
		archiveDelConfig.setRetentiontime(archiveConfig.getRetentiontime());
		archiveDelConfig.setRRSFlag(archiveConfig.getRRSFlag());
		archiveDelConfig.setSelectedSourceId(archiveConfig.getSelectedSourceId());
		archiveDelConfig.setStrArchiveDestinationPassword(archiveConfig.getStrArchiveDestinationPassword());
		archiveDelConfig.setStrArchiveDestinationUserName(archiveConfig.getStrArchiveDestinationUserName());
		if(hasFilecopyPolicy() && archiveConfig.isbArchiveToDrive()){
			archiveDelConfig.setStrArchiveToDrivePath(getSeparateDrivePath(archiveConfig.getStrArchiveToDrivePath(), 
					archiveConfig.getStrArchiveDestinationUserName(), archiveConfig.getStrArchiveDestinationPassword()));
		}else{
			archiveDelConfig.setStrArchiveToDrivePath(archiveConfig.getStrArchiveToDrivePath());
		}
		archiveDelConfig.setStrCatalogPath(null);
		archiveDelConfig.setStrScheduleMode(null);
		archiveDelConfig.setAdvanceSchedule(getDefaultArchiveDelAdvanceSchedule());
		DeleteArchiveService.getInstance().setArchiveDelConfigurationFromUpgrade(archiveDelConfig);
	}
	
	private AdvanceSchedule getDefaultArchiveDelAdvanceSchedule(){
		AdvanceSchedule advSchedule = new AdvanceSchedule();
		long now = System.currentTimeMillis();
		DayTime startTime = new DayTime(22,0);
		EveryDaySchedule daySchedule = new EveryDaySchedule();
		daySchedule.setDayEnabled(new Boolean[]{true,true,true,true,true,true,true});
		daySchedule.setEnabled(true);
		daySchedule.setDayTime(startTime);
		advSchedule.setScheduleStartTime(now);
		EveryMonthSchedule monthSchedule = new EveryMonthSchedule();
		monthSchedule.setEnabled(false);
		PeriodSchedule periodSchedule = new PeriodSchedule();
		periodSchedule.setDaySchedule(daySchedule);
		periodSchedule.setMonthSchedule(monthSchedule);
		advSchedule.setPeriodSchedule(periodSchedule);
		return advSchedule;
	}
	
	private ArchiveCloudDestInfo getSeparateCloudInfo(ArchiveCloudDestInfo cloudInfo){
		ArchiveCloudDestInfo retCloudInfo = new ArchiveCloudDestInfo();
		retCloudInfo.setAccountName(cloudInfo.getAccountName());
		retCloudInfo.setcloudBucketName(cloudInfo.getcloudBucketName() + "-" + ArchiveConfigurationConstants.FA_SUFFIX_CLOUD);
		retCloudInfo.setcloudBucketRegionName(cloudInfo.getcloudBucketRegionName());
		retCloudInfo.setcloudCertificatePassword(cloudInfo.getcloudCertificatePassword());
		retCloudInfo.setcloudCertificatePath(cloudInfo.getcloudCertificatePath());
		retCloudInfo.setcloudProxyPassword(cloudInfo.getcloudProxyPassword());
		retCloudInfo.setcloudProxyPort(cloudInfo.getcloudProxyPort());
		retCloudInfo.setcloudProxyRequireAuth(cloudInfo.iscloudProxyRequireAuth());
		retCloudInfo.setcloudProxyServerName(cloudInfo.getcloudProxyServerName());
		retCloudInfo.setcloudProxyUserName(cloudInfo.getcloudProxyUserName());
		retCloudInfo.setCloudSubVendorType(cloudInfo.getCloudSubVendorType());
		retCloudInfo.setcloudUseProxy(cloudInfo.iscloudUseProxy());
		retCloudInfo.setcloudVendorHostName(cloudInfo.getcloudVendorHostName());
		retCloudInfo.setcloudVendorPassword(cloudInfo.getcloudVendorPassword());
		retCloudInfo.setcloudVendorPort(cloudInfo.getcloudVendorPort());
		retCloudInfo.setcloudVendorType(cloudInfo.getcloudVendorType());
		retCloudInfo.setcloudVendorURL(cloudInfo.getcloudVendorURL());
		retCloudInfo.setcloudVendorUserName(cloudInfo.getcloudVendorUserName());
		retCloudInfo.setEncodedCloudBucketName(cloudInfo.getEncodedCloudBucketName());
		retCloudInfo.setId(cloudInfo.getId());
		retCloudInfo.setRRSFlag(cloudInfo.getRRSFlag());
		return retCloudInfo;
	}
	
	private String getSeparateDrivePath(String path, String fullName, String password){
		String hostName = path.substring(path.lastIndexOf("\\")+1);
		String destination = path.substring(0, path.lastIndexOf("\\"));
		createSubFolder(destination, ArchiveConfigurationConstants.FA_SUFFIX_SHARE_FOLDER, hostName,
				ArchiveUtil.getDomainName(fullName), ArchiveUtil.getUserName(fullName), password);
		String retPath = destination + "\\" + ArchiveConfigurationConstants.FA_SUFFIX_SHARE_FOLDER + "\\" + hostName;
		return retPath;
	}
	
	private boolean createSubFolder(String path, String subFolder, String hostName, String domainName, String userName, String password){
		JNetConnInfo connInfo = new JNetConnInfo();
		connInfo.setSzDir(path);
		connInfo.setSzDomain(domainName);
		connInfo.setSzUsr(userName);
		connInfo.setSzPwd(WSJNI.AFDecryptStringEx(password));
		long ret = -1;
		ret = WSJNI.AFCreateConnection(connInfo);
		if(ret != 0){
			logger.error("Failed to connect to filecopy destination");
			return false;
		}
		ret = WSJNI.AFCreateDir(path, subFolder);
		ret = WSJNI.AFCreateDir(path + "\\" + subFolder, hostName);
		WSJNI.AFCutConnection(connInfo, true);
		if(ret != 0){
			logger.error("Failed to create sub folder for file archive job");
			return false;
		}
		return true;
	}
	
	private void setFCPurgeJobFlag(){
		if(hasArchivePolicy() && hasFilecopyPolicy()){
			logger.info("Both File copy and File archive are enabled, need to set purge job flag");
			WindowsRegistry registry = new WindowsRegistry();
			int handle = 0;
			try {
				handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
				String purgeJobFlag = registry.getValue(handle, ArchiveConfigurationConstants.ENABLE_FC_PURGE_JOB_KEY);
				if(StringUtil.isEmptyOrNull(purgeJobFlag)){
					registry.setValue(handle, ArchiveConfigurationConstants.ENABLE_FC_PURGE_JOB_KEY, "true");
				}
				String purgeJobStartTime = registry.getValue(handle, ArchiveConfigurationConstants.FC_PURGE_JOB_START_TIME);
				if(StringUtil.isEmptyOrNull(purgeJobStartTime)){
					registry.setValue(handle, ArchiveConfigurationConstants.FC_PURGE_JOB_START_TIME, ArchiveConfigurationConstants.DEFAULT_FC_PURGE_JOB_START_TIME);
				}
				logger.info("Set FC purge job registry entry successfully. "
						+ "key=" + CommonRegistryKey.getD2DRegistryRoot() + "\\" + ArchiveConfigurationConstants.ENABLE_FC_PURGE_JOB_KEY + " value=true, "
						+ "key=" + CommonRegistryKey.getD2DRegistryRoot() + "\\" + ArchiveConfigurationConstants.FC_PURGE_JOB_START_TIME + " value=" 
						+ ArchiveConfigurationConstants.DEFAULT_FC_PURGE_JOB_START_TIME);
			} catch (Exception e) {
				logger.error("Read/Write registry for key: "
						+ ArchiveConfigurationConstants.ENABLE_FC_PURGE_JOB_KEY
						+ "or "
						+ ArchiveConfigurationConstants.FC_PURGE_JOB_START_TIME
						+ " failed.", e);
			} finally {
				if (handle != 0) {
					try {
						registry.closeKey(handle);
					} catch (Exception e) {
						logger.error("Close registry key failed.", e);
					}
				}
			}
		}
	}
}

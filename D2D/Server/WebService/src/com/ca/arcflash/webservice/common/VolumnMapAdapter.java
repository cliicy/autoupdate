package com.ca.arcflash.webservice.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupSources;
import com.ca.arcflash.webservice.data.backup.BackupVolumes;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.ServiceException;

public class VolumnMapAdapter {
	private static final Logger logger = Logger.getLogger(VolumnMapAdapter.class);
	private static final int EVSS_SYSTEM = 0x00010000;
	private static final int EVSS_BOOT = 0x00020000;
	private static final int EVSS_RECOVERYVOLUME = 0x00800000;
	
	public static void convertBackupVolumes(BackupConfiguration backupConfig) throws ServiceException {
		if (backupConfig == null || backupConfig.getBackupVolumes() == null) {
			return;
		}
		
		List<String> volumeNameList = backupConfig.getBackupVolumes().getVolumes();
		if (volumeNameList == null) {
			return;
		}
				
		boolean backupSystemVolume = volumeNameList.contains(BackupSources.SystemVolume);
		boolean backupBootVolume = volumeNameList.contains(BackupSources.BootVolume);
		boolean backupRecoveryVolume = volumeNameList.contains(BackupSources.RecoveryVolume);
		if ( !backupSystemVolume && !backupBootVolume&& !backupRecoveryVolume ) {
			return;
		}
		
		Volume[] volumeArray = BrowserService.getInstance().getVolumes(true, 
				backupConfig.getDestination(),
				backupConfig.getUserName(),
				backupConfig.getPassword());
		
		if (backupSystemVolume) {
			volumeNameList.remove(BackupSources.SystemVolume);
			String systemVolumeName = getSystemVolumeName(volumeArray);
			if (systemVolumeName !=null && !volumeNameList.contains(systemVolumeName)) {
				volumeNameList.add(systemVolumeName);
			}
		}
		
		if (backupBootVolume) {
			volumeNameList.remove(BackupSources.BootVolume);
			String bootVolumeName = getBootVolumeName(volumeArray);
			if (bootVolumeName!=null && !volumeNameList.contains(bootVolumeName)) {
				volumeNameList.add(bootVolumeName);
			}
		}
		if( backupRecoveryVolume ) {
			volumeNameList.remove(BackupSources.RecoveryVolume);
			String recoveryVolName = getRecoveryVolumeName(volumeArray);
			if( recoveryVolName!=null &&!volumeNameList.contains( recoveryVolName ) ){
				volumeNameList.add(recoveryVolName);
			}
		}
		
	}
	//fanda03 143288
	private static String getRecoveryVolumeName(Volume[] volumeArray ) {
		if (volumeArray == null) {
			return null;
		}
		for (Volume volume : volumeArray) {
			if ( ( volume.getSubStatus() & EVSS_RECOVERYVOLUME ) > 0) {
				return removeEndSlash(volume.getName());
			}
		}
		return null;
	}
	///
	private static String getBootVolumeName(Volume[] volumeArray) {
		if (volumeArray == null) {
			return null;
		}
		
		for (Volume volume : volumeArray) {
			if ((volume.getSubStatus() & EVSS_BOOT) > 0) {
				return removeEndSlash(volume.getName());
			}
		}
		
		return null;
	}
	
	private static String removeEndSlash(String name) {
		if(name != null && (name.endsWith("\\") || name.endsWith("/")))
			name = name.substring(0, name.length() - 1);
		return name;
	}

	private static String getSystemVolumeName(Volume[] volumeArray) {
		if (volumeArray == null) {
			return null;
		}
		
		for (Volume volume : volumeArray) {
			if ((volume.getSubStatus() & EVSS_SYSTEM) > 0) {
				return removeEndSlash(volume.getName());
			}
		}
		
		return null;
	}
	
	public static void checkSourceVolumes(BackupConfiguration configuration) throws ServiceException{		
		BackupVolumes bv = configuration.getBackupVolumes();
		
		if(bv != null && bv.isFullMachine())
			return;
		
		List<String> volumeNameList = bv == null? null: bv.getVolumes();
		if(volumeNameList == null || volumeNameList.size()==0){
			throw new ServiceException(FlashServiceErrorCode.BackupConfig_SOURCE_VOLUME_EMPTY);
		}
		
		StringBuffer buffer = new StringBuffer();
		//check if source volumes exist or are mounted
		for(String sourceVolume:volumeNameList){
			if(sourceVolume!=null){
				boolean isMountedOrNotExist = BrowserService.getInstance().getNativeFacade().isVolumeMounted(sourceVolume);
				if(isMountedOrNotExist){
					buffer.append(sourceVolume+",");
				}
			}
		}
		
		if(buffer.length()>0){
			throw new ServiceException(FlashServiceErrorCode.BackupConfig_SOURCE_VOLUME_CAN_NOT_BACKUP,new Object[]{buffer.substring(0, buffer.length()-1)});
		}
		
		
	}
	
	public static List<String> getBackupDatastoreVolumes(BackupConfiguration backupConfig) {
		List<String> dsVolumes= getAllDatastoreVolumes(backupConfig);
		if(dsVolumes.size()==0){
			return null;
		}
		BackupVolumes bv = backupConfig.getBackupVolumes(); // bv not null after checkSourceVolumes()
		if(bv.isFullMachine())
			return dsVolumes;
		
		List<String> result = new ArrayList<String>();
		List<String> backupVolumes = bv.getVolumes();
		for(String backupVolume : backupVolumes){
			if(dsVolumes.contains(backupVolume)){
				result.add(backupVolume);
			}
		}
		return result;
	}
	
	private static List<String> getAllDatastoreVolumes(BackupConfiguration backupConfig) {
		List<String> result = new ArrayList<String>();
		try {
			Volume[] volumeArray = BrowserService.getInstance().getVolumes(true, backupConfig.getDestination(), backupConfig.getUserName(), backupConfig.getPassword());
			if (volumeArray == null) {
				return result;
			}
			
			for (Volume volume : volumeArray) {
				if (!StringUtil.isEmptyOrNull(volume.getDatastore())) {
					result.add(removeEndSlash(volume.getName()));
				}
			}
		} catch (ServiceException e) {
			logger.error(e);
		}
		return result;
	}
			
	public static boolean isSystemVolue(int subStatus) {
		if ((subStatus & EVSS_SYSTEM) > 0)
			return true;
		return false;
	}
}

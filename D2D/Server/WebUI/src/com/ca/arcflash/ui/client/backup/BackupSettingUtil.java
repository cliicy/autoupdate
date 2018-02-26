package com.ca.arcflash.ui.client.backup;

public class BackupSettingUtil {
	private static final BackupSettingUtil INSTANCE = new BackupSettingUtil();
	public BackupSettingsContent settingContent = null;
	
	private BackupSettingUtil() {}
	
	void setBackupSetting(BackupSettingsContent setting) {
		settingContent = setting;
	}
	
	public String getBackupDestination() {
		if(settingContent == null)
			return null;
		else 
			return settingContent.getDestination().getBackupDestination();
	}
	
	public static BackupSettingUtil getInstance() {
		return INSTANCE;
	}
}

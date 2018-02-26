package com.ca.arcflash.ui.client.model;

import java.io.Serializable;

public class D2DSettingModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6917028457287054600L;
	
	public BackupSettingsModel getBackupSettingsModel() {
		return backupSettingsModel;
	}
	public void setBackupSettingsModel(BackupSettingsModel backupSettingsModel) {
		this.backupSettingsModel = backupSettingsModel;
	}
	public PreferencesModel getPreferencesModel() {
		return preferencesModel;
	}
	public void setPreferencesModel(PreferencesModel preferencesModel) {
		this.preferencesModel = preferencesModel;
	}
	private BackupSettingsModel backupSettingsModel;
	private PreferencesModel preferencesModel;
	private ArchiveSettingsModel archiveSettingsModel;
	private ArchiveSettingsModel fileArchiveSettingsModel;
	private ScheduledExportSettingsModel scheduledExportSettingsModel;

	public ArchiveSettingsModel getArchiveSettingsModel() {
		return archiveSettingsModel;
	}
	public void setArchiveSettingsModel(ArchiveSettingsModel archiveSettingsModel) {
		this.archiveSettingsModel = archiveSettingsModel;
	}
	public ArchiveSettingsModel getFileArchiveSettingsModel() {
		return fileArchiveSettingsModel;
	}
	public void setFileArchiveSettingsModel(
			ArchiveSettingsModel fileArchiveSettingsModel) {
		this.fileArchiveSettingsModel = fileArchiveSettingsModel;
	}
	public ScheduledExportSettingsModel getScheduledExportSettingsModel() {
		return scheduledExportSettingsModel;
	}
	public void setScheduledExportSettingsModel(
			ScheduledExportSettingsModel scheduledExportSettingsModel) {
		this.scheduledExportSettingsModel = scheduledExportSettingsModel;
	}
	
	
}

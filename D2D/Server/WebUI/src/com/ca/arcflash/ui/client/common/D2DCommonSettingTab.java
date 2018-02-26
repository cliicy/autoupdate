package com.ca.arcflash.ui.client.common;

import java.util.HashMap;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.ArchiveSettingsContent;
import com.ca.arcflash.ui.client.backup.BackupSettingsContent;
import com.ca.arcflash.ui.client.backup.BackupSettingsContentForEdge;
import com.ca.arcflash.ui.client.export.ScheduledExportSettingsContent;
import com.ca.arcflash.ui.client.homepage.PreferencesSettingsContent;
import com.ca.arcflash.ui.client.model.CustomizationModel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class D2DCommonSettingTab extends BaseCommonSettingTab {
	//private boolean d2dUsingEdgePolicy = false;
	private boolean isD2DSaved = false;

	public D2DCommonSettingTab(SettingsGroupType settingsGroupType, boolean isForEdge,
			ISettingsContentHost contentHost) {
		super(settingsGroupType, isForEdge, contentHost);
	}

	@Override
	protected String initSettingsContentList() {
		String hostTitle = UIContext.Constants.homepageTasksBackupSettingLabel();
		ISettingsContent d2dSettingsContent = new BackupSettingsContent();
		if(isForEdge) {
			d2dSettingsContent = new BackupSettingsContentForEdge();
		}
		((BackupSettingsContent)d2dSettingsContent).setD2D(true);
		String d2dSettingTab = UIContext.Constants.backupSettingsWindow();
		settingsContentList.add(new SettingsContentEntry(d2dBackupSettingID,
				d2dSettingTab, d2dSettingsContent, d2dBackupSettingID,
				AbstractImagePrototype.create(UIContext.IconBundle.d2d_backup_settings())));

		CustomizationModel customizedModel = UIContext.customizedModel;
		Boolean isFileCopyEnabled = customizedModel.get("FileCopy");
		Boolean isFileArchiveEnabled = customizedModel.get("FileArchive");
		if(isFileCopyEnabled)
		{

			ISettingsContent archiveSettingContent = new ArchiveSettingsContent(
					(BackupSettingsContent) d2dSettingsContent, false);
			String archiveSettingTab = UIContext.Constants
					.homepageTasksArchiveSettingLabel();
			settingsContentList.add(new SettingsContentEntry(archiveSettingID,
					archiveSettingTab, archiveSettingContent, archiveSettingID,
					AbstractImagePrototype.create(UIContext.IconBundle.d2d_filecopy_settings())));
			
		}
		if(isFileArchiveEnabled){
			ISettingsContent fileArchiveSettingContent = new ArchiveSettingsContent(
					(BackupSettingsContent) d2dSettingsContent, true);
			String fileArchiveSettingTab = UIContext.Constants
					.homepageTasksArchiveSettingLabel();
			settingsContentList.add(new SettingsContentEntry(fileArchiveSettingID,
					fileArchiveSettingTab, fileArchiveSettingContent, fileArchiveSettingID,
					AbstractImagePrototype.create(UIContext.IconBundle.d2d_filecopy_settings())));
			
		}

		ISettingsContent scheduledExportSettingsContent = new ScheduledExportSettingsContent();
		String scheduledExportSettingsTab = UIContext.Constants
				.scheduledExportSettings();
		settingsContentList.add(new SettingsContentEntry(
				scheduledExportSettingsID, scheduledExportSettingsTab,
				scheduledExportSettingsContent, scheduledExportSettingsID,
				AbstractImagePrototype.create(UIContext.IconBundle.d2d_reconverypoints_settings())));

		ISettingsContent preferenceconContent = new PreferencesSettingsContent();
		String d2dPrefrenceTab = UIContext.Constants.preferences();
		settingsContentList.add(new SettingsContentEntry(
				d2dPreferenceSettingID, d2dPrefrenceTab, preferenceconContent,
				d2dPreferenceSettingID, AbstractImagePrototype.create(UIContext.IconBundle
						.d2d_preference_settings())));
			
		Utils.connectionCache = new HashMap<String, String[]>();
		return hostTitle;
	}

	@Override
	public void loadSetting() {
//		contentHost.increaseBusyCount(UIContext.Constants
//				.settingsLoadingConfigMaskText());
//		
//		Broker.loginService.getD2DConfiguration(new BaseAsyncCallback<D2DSettingModel>() {
//			public void onFailure(Throwable caught) {
//				contentHost.decreaseBusyCount();
//				onLoadingCompleted(false);
//				super.onFailure(caught);
//
//			}
//			@Override
//			public void onSuccess(D2DSettingModel result) {
//				d2dSettings = result;
//				Broker.loginService.isUsingEdgePolicySettings(
//						SettingsTypesForUI.BackupSettings,
//						new AsyncCallback<Boolean>() {
//							@Override
//							public void onFailure(Throwable caught) {
//								contentHost.decreaseBusyCount();
//								initD2DSettings();
//							}
//
//							@Override
//							public void onSuccess(Boolean result) {
//								contentHost.decreaseBusyCount();
//								d2dUsingEdgePolicy = result;
//								initD2DSettings();
//							}
//						});
//
//			}
//		});
		
		settingPresenter.loadSetting();

	}
	
	@Override
	protected void saveSetting() {
		isD2DSaved  = false;
		saveQueue.clear();
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			ISettingsContent settingsContent = contentEntry.getContentObject();
			this.saveQueue.add(settingsContent);
		}
		
		this.saveItem();
	}
	
	@Override
	protected void onSaveQueueEmpty() {
		if(!isD2DSaved) {
			saveD2DConfiguration();
		}else {
			super.onSaveQueueEmpty();
		}
	}
	
	private BackupSettingsContent getBackupSettingContent() {
		BackupSettingsContent backupSettingsContent = null;
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			if(contentEntry.getId() == d2dBackupSettingID) {
				backupSettingsContent = (BackupSettingsContent)contentEntry.getContentObject();
				break;
			}
		}
		return backupSettingsContent;
	}
	
	@Override
	protected void enableEdit(int settingsId, ISettingsContent content) {
		CustomizationModel customizedModel = UIContext.customizedModel;
		if(d2dUsingEdgePolicy && !this.isForEdge)
			switch(settingsId){
			case archiveSettingID:
				Boolean isFileCopyEnabled = customizedModel.get("FileCopy");
				
				if(isFileCopyEnabled)
				{	
					((ArchiveSettingsContent)content).enableEditing(false);
				}
				break;
			case fileArchiveSettingID:
				Boolean isFileArchiveEnabled = customizedModel.get("FileArchive");

				if (isFileArchiveEnabled) {
					((ArchiveSettingsContent) content).enableEditing(false);
				}
				break;
			case d2dBackupSettingID:
				((BackupSettingsContent)content).enableEditing(false);
				break;
			case d2dPreferenceSettingID:
				((PreferencesSettingsContent)content).enableEditing(false);
				break;
			case scheduledExportSettingsID:
				((ScheduledExportSettingsContent)content).enableEditing(false);
				break;
				
				default:
					break;
			}
	}
	
	private void saveD2DConfiguration() {
//		increaseBusyCount(UIContext.Constants.settingsMaskText());
//		final BackupSettingsContent backupSetting = getBackupSettingContent();
		isD2DSaved = true;
//		Broker.loginService.saveD2DConfiguration(d2dSettings, new BaseAsyncCallback<Long>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				if (isForEdge) {
//					decreaseBusyCount();
//					onSavingCompleted(false);
//				} else {
//					if (caught instanceof BusinessLogicException
//							&& backupSetting.ERR_REMOTE_DEST_WINSYSMSG
//									.equals(((BusinessLogicException) caught)
//											.getErrorCode())) {
//						backupSetting.checkDestDriverType(caught, true);
//					} else {
//						decreaseBusyCount();
//						super.onFailure(caught);
//						onSavingCompleted(false);
//					}
//				}
//			}
//
//			@Override
//			public void onSuccess(Long result) {
//				decreaseBusyCount();
//				if (isForEdge) {
//					onSavingCompleted(true);
//				} else {
//					backupSetting.launchFirstBackupJobifNeeded();
//					contentHost.close();
//					// refresh preference settings
//					if (UIContext.d2dHomepagePanel != null)
//						UIContext.d2dHomepagePanel.refresh(null,
//								IRefreshable.CS_CONFIG_CHANGED);
//					else if (UIContext.hostPage != null) {
//						UIContext.hostPage.refresh(null);
//					}
//					// refresh archive settings
//					UIContext.d2dHomepagePanel.refreshProtectionSummary(null);
//				}
//			}
//		});
		
		settingPresenter.saveSetting(getBackupSettingContent());
	}

//	@Override
//	public boolean isForCreate() {
//		// TODO Auto-generated method stub
//		return false;
//	}
	
}

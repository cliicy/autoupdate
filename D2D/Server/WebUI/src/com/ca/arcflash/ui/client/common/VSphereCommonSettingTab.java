package com.ca.arcflash.ui.client.common;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.vsphere.setting.VSphereBackupSettingContent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class VSphereCommonSettingTab extends BaseCommonSettingTab {

	public VSphereCommonSettingTab(SettingsGroupType settingsGroupType, boolean isForEdge,
			ISettingsContentHost contentHost) {
		super(settingsGroupType, isForEdge, contentHost);
	}

	@Override
	protected String initSettingsContentList() {
		String hostTitle = UIContext.Constants
				.homepageTasksBackupSettingLabel();
		ISettingsContent vsphereSettingsContent = new VSphereBackupSettingContent(settingsGroupType);
		String vsphereTab = UIContext.Constants.backupSettingsWindow();
		settingsContentList.add(new SettingsContentEntry(vsphereSettingID,
				vsphereTab, vsphereSettingsContent, vsphereSettingID,
				AbstractImagePrototype.create(UIContext.IconBundle.vsphere_backup_settings())));

		ISettingsContent vspherePreference = new CommonPreferenceSettings(
				SettingsGroupType.VMBackupSettings);
		String vspherePrefrenceTab = UIContext.Constants.preferences();
		settingsContentList.add(new SettingsContentEntry(vspherePreferenceID,
				vspherePrefrenceTab, vspherePreference, vspherePreferenceID,
				AbstractImagePrototype.create(UIContext.IconBundle.vsphere_preference_settings())));
		return hostTitle;
	}

	@Override
	protected void saveSetting() {
		saveVsphereSettings();
	}	
	
	@Override
	protected void enableEdit(int settingsId, ISettingsContent content) {
		if(content instanceof VSphereBackupSettingContent)
			((VSphereBackupSettingContent)content).enableEditing(this.isForEdge);
		
	}
	
	private void saveVsphereSettings() {
		saveQueue.clear();
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			if(contentEntry.getId() == vsphereSettingID) {
				ISettingsContent vcmSettingsContent = contentEntry.getContentObject();
				saveQueue.add(vcmSettingsContent);
				break;
			}
			
		}
		
		saveItem();
	}

//	@Override
//	public boolean isForCreate() {
//		// TODO Auto-generated method stub
//		return false;
//	}
}

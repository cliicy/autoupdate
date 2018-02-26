package com.ca.arcflash.ui.client.common;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.VCMMessages;
import com.ca.arcflash.ui.client.coldstandby.edge.setting.VCMSettingsContent;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class VCMCommonSettingTab extends BaseCommonSettingTab {

	public VCMCommonSettingTab(SettingsGroupType settingsGroupType, boolean isForEdge,
			ISettingsContentHost contentHost) {
		super(settingsGroupType, isForEdge, contentHost);
	}

	@Override
	protected String initSettingsContentList() {
		String hostTitle = VCMMessages.coldStandbyTaskSettings();
		ISettingsContent vcmSettingContent = new VCMSettingsContent( this.settingsGroupType == SettingsGroupType.RemoteVCMSettings );
		String virtualConversionTab = UIContext.Constants
				.virtualStandyNameTranslate();
		settingsContentList.add(new SettingsContentEntry(vcmSettingID,
				virtualConversionTab, vcmSettingContent, vcmSettingID,
				AbstractImagePrototype.create(UIContext.IconBundle.vcm_virtualstandby_settings())));

		ISettingsContent vcmPreference = new CommonPreferenceSettings( this.settingsGroupType );
		String prefrenceTab = UIContext.Constants.preferences();
		settingsContentList.add(new SettingsContentEntry(vcmPreferenceID,
				prefrenceTab, vcmPreference, vcmPreferenceID,
				AbstractImagePrototype.create(UIContext.IconBundle.vcm_preference_settings())));
		return hostTitle;
	}

	@Override
	protected void saveSetting() {
		saveVCMSettings();
	}
	
	private void saveVCMSettings() {
		saveQueue.clear();
		for (SettingsContentEntry contentEntry : this.settingsContentList)
		{
			if(contentEntry.getId() == vcmSettingID) {
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

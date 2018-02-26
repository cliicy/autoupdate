package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class PreferencesModel extends BaseModelData{
	private static final long serialVersionUID = -1190005990510526621L;
	
	private GeneralSettingsModel generalSettings;
	private EmailAlertsModel emailAlerts;
	private UpdateSettingsModel updateSettings;
	private DestinationCapacityModel destCapacityModel;
	
	public GeneralSettingsModel getGeneralSettings()
	{
		return generalSettings;
	}
	
	public void setGeneralSettings(GeneralSettingsModel generalSettings)
	{
		this.generalSettings = generalSettings;
	}
	
	public EmailAlertsModel getEmailAlerts()
	{
		return emailAlerts;
	}
	
	public void setEmailAlerts(EmailAlertsModel emailAlerts)
	{
		this.emailAlerts = emailAlerts;
	}
	public UpdateSettingsModel getupdateSettings()
	{
		return updateSettings;
	}
	
	public void setupdateSettings(UpdateSettingsModel updateSettings)
	{
		this.updateSettings = updateSettings;
	}
	
	public DestinationCapacityModel getdestCapacityModel()
	{
		return destCapacityModel;
	}
	
	public void setdestCapacityModel(DestinationCapacityModel destCapacityModel)
	{
		this.destCapacityModel = destCapacityModel;
	}
}

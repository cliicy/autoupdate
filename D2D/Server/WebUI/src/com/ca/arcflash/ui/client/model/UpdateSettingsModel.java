package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class UpdateSettingsModel extends BaseModelData{	
	
	private static final long serialVersionUID = 3562335603716840621L;

	private StagingServerModel[] StagingServers;
	private ProxySettingsModel proxySettings;
	
	//Download server information
	public Integer getDownloadServerType()
	{
		return (Integer)get("DownloadServerType");
	}
	
	public void setDownloadServerType(Integer iServerType)
	{
		set("DownloadServerType",iServerType);
	}
	
	public StagingServerModel[] getStagingServers()
	{
		return StagingServers;
	}
	
	public void setStagingServers(StagingServerModel[] in_StagingServers)
	{
		this.StagingServers = in_StagingServers;
	}

	public ProxySettingsModel getproxySettings()
	{
		return proxySettings;
	}
	
	public void setproxySettings(ProxySettingsModel in_StagingServers)
	{
		this.proxySettings = in_StagingServers;
	}	
	
	// Update Schedule Information
	public Boolean getAutoCheckupdate()
	{
		return (Boolean)get("AutoCheckUpdate");
	}
	
	public void setAutoCheckupdate(Boolean bAutoCheckUpdate)
	{
		set("AutoCheckUpdate",bAutoCheckUpdate);
	}
	

	public Integer getScheduledWeekDay()
	{
		return (Integer)get("ScheduledWeekDay");
	}
	
	public void setScheduledWeekDay(Integer in_iScheduledWeekDay)
	{
		set("ScheduledWeekDay",in_iScheduledWeekDay);
	}
	
	public Integer getScheduledHour()
	{
		return (Integer)get("ScheduledHour");
	}
	
	public void setScheduledHour(Integer in_iScheduledHour)
	{
		set("ScheduledHour",in_iScheduledHour);
	}
	
	public int getCAServerStatus()
	{
		return (Integer)get("CAServerStatus");
	}
	
	public void setCAServerStatus(int in_iCAServerStatus)
	{
		set("CAServerStatus",in_iCAServerStatus);
	}
	
	public Boolean getD2DBackupsConfigured()
	{
		return (Boolean)get("D2DBackupsConfigured");
	}
	
	public void setD2DBackupsConfigured(Boolean in_bD2DBackupsConfigured)
	{
		set("D2DBackupsConfigured",in_bD2DBackupsConfigured);
	}
	
}

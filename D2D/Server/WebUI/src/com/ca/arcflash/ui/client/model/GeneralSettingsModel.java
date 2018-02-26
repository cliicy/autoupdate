package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GeneralSettingsModel extends BaseModelData{	

	private static final long serialVersionUID = 3562335603716840621L;

	//News Feed
	public Boolean getNewsFeed()
	{
		return (Boolean)get("NewsFeed");
	}
	public void setNewsFeed(Boolean bNewsFeed)
	{
		set("NewsFeed",bNewsFeed);
	}

	//Social networking
	public Boolean getSocialNetworking()
	{
		return (Boolean)get("SocialNetworking");
	}
	public void setSocialNetworking(Boolean bSocialNetworking)
	{
		set("SocialNetworking",bSocialNetworking);
	}
	
	// Tray Notifications
	public Integer getTrayNotificationType()
	{
		return (Integer)get("TrayNotificationType");
	}
	public void setTrayNotificationType(Integer iTrayNotificationType)
	{
		set("TrayNotificationType",iTrayNotificationType);
	}
	
	// Help
	public Integer getUseVideos()
	{
		return (Integer)get("UseVideos");
	}
	public void setUseVideos(Integer iUseVideos)
	{
		set("UseVideos",iUseVideos);
	}
}

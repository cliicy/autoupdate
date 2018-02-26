package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class HostInfo extends BaseModelData{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1190005990510526622L;
	
	public String getName() {
		return get("Name");
	}
	public void setName(String name) {
		set("Name", name);
	}
	public String getUsername() {
		return get("Username");
	}
	public void setUsername(String username) {
		set("Username", username);
	}
	public Date getLastVisit() {
		return get("LastVisit");
	}
	public void setLastVisit(Date lastVisit) {
		set("LastVisit", lastVisit);
	} 
	
	
}

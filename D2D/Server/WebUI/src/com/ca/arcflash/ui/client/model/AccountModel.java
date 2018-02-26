package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class AccountModel extends BaseModelData{
	
	private static final long serialVersionUID = 3266208800408483529L;
	
	public String getUserName() {
		return (String)get("userName");
	}
	public void setUserName(String userName) {
		set("userName", userName);		
	}
	
	public void setPassword(String password) {
		set("password", password);
	}
	public String getPassword() {
		return (String)get("password");
	}
}

package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ExchangeGRTOptionModel extends BaseModelData {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 697168484393428095L;
	
	public Long getOption() {
		return get("option");
	}
	public void setOption(Long option) {
		set("option", option);
	}
	public Long getServerVersion() {
		return get("serverVersion");
	}
	public void setServerVersion(Long serverVersion) {
		set("serverVersion", serverVersion);
	}
	public String getFolder() {
		return get("folder");
	}
	public void setFolder(String folder) {
		set("folder", folder);
	}
	public String getAlternateServer() {
		return get("alternateServer");
	}
	public void setAlternateServer(String alternateServer) {
		set("alternateServer", alternateServer);
	}
	public String getUserName() {
		return get("userName");
	}
	public void setUserName(String userName) {
		set("userName", userName);
	}
	public String getPassword() {
		return get("password");
	}
	public void setPassword(String password) {
		set("password", password);
	}
	public String getDefaultE15CAS() {
		return get("defaultE15CAS");
	}
	public void setDefaultE15CAS(String defaultE15CAS) {
		set("defaultE15CAS", defaultE15CAS);
	}
}

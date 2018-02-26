package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VirtualCenterModel extends BaseModelData {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2937113605367794459L;

	public String getVcName() {
		return (String)get("vcName");
	}

	public void setVcName(String vcName) {
		set("vcName",vcName);
	}

	public String getUsername() {
		return (String)get("username");
	}

	public void setUsername(String username) {
		set("username",username);
	}

	public String getPassword() {
		return (String)get("password");
	}

	public void setPassword(String password) {
		set("password",password);
	}

	public String getProtocol() {
		return (String)get("protocol");
	}

	public void setProtocol(String protocol) {
		set("protocol",protocol);
	}

	public int getPort() {
		return (Integer)get("port");
	}

	public void setPort(int port) {
		set("port",port);
	}	
	
	public String getId() {
		return (String) get("id");
	}
	
	public void setId(String id) {
		set("id", id);
	}
	
	public String getVersion() {
		return (String) get("version");
	}
	
	public void setVersion(String version) {
		set("version", version);
	}
}

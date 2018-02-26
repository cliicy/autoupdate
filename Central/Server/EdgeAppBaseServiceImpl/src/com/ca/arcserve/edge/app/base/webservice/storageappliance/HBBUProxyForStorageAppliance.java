package com.ca.arcserve.edge.app.base.webservice.storageappliance;

import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class HBBUProxyForStorageAppliance {
	public int id;
	public String hostname;
	public String username;
	public String password;
	public int protocol;
	public int port;
	public String uuid;
	public String authUuid;
	// Dec sprint
	public int majorversion;
	
	
	public int getMajorversion() {
		return majorversion;
	}
	public void setMajorversion(int majorversion) {
		this.majorversion = majorversion;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@EncryptSave
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getProtocol() {
		return protocol;
	}
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	@EncryptSave
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	@EncryptSave
	public String getAuthUuid() {
		return authUuid;
	}
	public void setAuthUuid(String authUuid) {
		this.authUuid = authUuid;
	}

}

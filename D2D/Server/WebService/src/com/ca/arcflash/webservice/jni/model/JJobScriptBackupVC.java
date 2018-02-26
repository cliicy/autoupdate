package com.ca.arcflash.webservice.jni.model;

public class JJobScriptBackupVC {
	
	private String vcName;
	private String username;
	private String password;
	private String protocol;
	private int port;
	private int ignoreCertificate;

	public String getVcName() {
		return vcName;
	}

	public void setVcName(String vcName) {
		this.vcName = vcName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getIgnoreCertificate() {
		return ignoreCertificate;
	}

	public void setIgnoreCertificate(int ignoreCertificate) {
		this.ignoreCertificate = ignoreCertificate;
	}
	

}

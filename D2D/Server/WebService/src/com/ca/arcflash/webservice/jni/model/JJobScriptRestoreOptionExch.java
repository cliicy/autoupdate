package com.ca.arcflash.webservice.jni.model;

public class JJobScriptRestoreOptionExch {
	private long ulOptions;
	private long ulServerVersion;
	private String pwszFolder;
	private String pwszAlternateServer;
	private String pwszUser;
	private String pwszUserPW;
	
	public long getUlOptions() {
		return ulOptions;
	}
	public void setUlOptions(long ulOptions) {
		this.ulOptions = ulOptions;
	}
	public long getUlServerVersion() {
		return ulServerVersion;
	}
	public void setUlServerVersion(long ulServerVersion) {
		this.ulServerVersion = ulServerVersion;
	}
	public String getPwszFolder() {
		return pwszFolder;
	}
	public void setPwszFolder(String pwszFolder) {
		this.pwszFolder = pwszFolder;
	}
	public String getPwszAlternateServer() {
		return pwszAlternateServer;
	}
	public void setPwszAlternateServer(String pwszAlternateServer) {
		this.pwszAlternateServer = pwszAlternateServer;
	}
	public String getPwszUser() {
		return pwszUser;
	}
	public void setPwszUser(String pwszUser) {
		this.pwszUser = pwszUser;
	}
	public String getPwszUserPW() {
		return pwszUserPW;
	}
	public void setPwszUserPW(String pwszUserPW) {
		this.pwszUserPW = pwszUserPW;
	}
	
}

package com.ca.arcserve.edge.app.base.webservice.contract.node;

public class LinuxD2DServerRegistrationResponse {

	private String d2dServerUUID;
	
	private String d2dServerAuthKey;
	
	private String osName;
	
	private int d2dServerTimezone;
	
	private String versionNumber;
	
	private String buildNumber;

	public String getD2dServerUUID() {
		return d2dServerUUID;
	}

	public void setD2dServerUUID(String d2dServerUUID) {
		this.d2dServerUUID = d2dServerUUID;
	}

	public String getD2dServerAuthKey() {
		return d2dServerAuthKey;
	}

	public void setD2dServerAuthKey(String d2dServerAuthKey) {
		this.d2dServerAuthKey = d2dServerAuthKey;
	}
	
	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}

	public int getD2dServerTimezone() {
		return d2dServerTimezone;
	}

	public void setD2dServerTimezone(int d2dServerTimezone) {
		this.d2dServerTimezone = d2dServerTimezone;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}
	
}

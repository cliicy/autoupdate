package com.ca.arcflash.webservice.jni.model;

public class JArchiveDestinationConfig {
	public boolean bArchiveToDrive;
	public String strArchiveToDrivePath;
	public String StrArchiveDestinationUserName;
	public String StrArchiveDestinationPassword;
	
	public boolean bArchiveToCloud;
	
	public JCloudConfig JcloudConfig;
	
	public void setbArchiveToDrive(boolean bArchiveToDrive) {
		this.bArchiveToDrive = bArchiveToDrive;
	}

	public boolean isbArchiveToDrive() {
		return bArchiveToDrive;
	}

	public void setStrArchiveToDrivePath(String strArchiveToDrivePath) {
		this.strArchiveToDrivePath = strArchiveToDrivePath;
	}

	public String getStrArchiveToDrivePath() {
		return strArchiveToDrivePath;
	}
	
	public void setStrArchiveDestinationUserName(String in_StrArchiveDestinationUserName) {
		this.StrArchiveDestinationUserName = in_StrArchiveDestinationUserName;
	}

	public String getStrArchiveDestinationUserName() {
		return StrArchiveDestinationUserName;
	}

	public void setStrArchiveDestinationPassword(String in_StrArchiveDestinationPassword) {
		this.StrArchiveDestinationPassword = in_StrArchiveDestinationPassword;
	}

	public String getStrArchiveDestinationPassword() {
		return StrArchiveDestinationPassword;
	}
	
	public void setbArchiveToCloud(boolean bArchiveToCloud) {
		this.bArchiveToCloud = bArchiveToCloud;
	}

	public boolean isbArchiveToCloud() {
		return bArchiveToCloud;
	}

	public void setJcloudConfig(JCloudConfig in_JcloudConfig) {
		this.JcloudConfig = in_JcloudConfig;
	}

	public JCloudConfig getJcloudConfig() {
		return JcloudConfig;
	}
}

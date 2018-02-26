package com.ca.arcflash.webservice.jni.model;

public class JMountSession {
	private long sessionNum;
	private String sessionPath;
	public long getSessionNum() {
		return sessionNum;
	}
	public void setSessionNum(long sessionNum) {
		this.sessionNum = sessionNum;
	}
	public String getSessionPath() {
		return sessionPath;
	}
	public void setSessionPath(String sessionPath) {
		this.sessionPath = sessionPath;
	}
}

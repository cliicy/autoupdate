package com.ca.arcflash.webservice.jni.model;

public class JWindowsServiceModel {
	private boolean isExist = false;
	private boolean started = false;
	private long processId = 0;
	private String startMode = "";
	private String state = "";
	public boolean isExist() {
		return isExist;
	}
	public void setExist(boolean isExist) {
		this.isExist = isExist;
	}
	public boolean isStarted() {
		return started;
	}
	public void setStarted(boolean started) {
		this.started = started;
	}
	public long getProcessId() {
		return processId;
	}
	public void setProcessId(long processId) {
		this.processId = processId;
	}
	public String getStartMode() {
		return startMode;
	}
	public void setStartMode(String startMode) {
		this.startMode = startMode;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
}

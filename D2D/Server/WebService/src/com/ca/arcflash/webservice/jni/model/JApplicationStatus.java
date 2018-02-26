package com.ca.arcflash.webservice.jni.model;

public class JApplicationStatus {
	
	private boolean sqlInstalled;
	private boolean exchangeInstalled;
	private boolean d2dInstalled;
	private boolean arcserveInstalled;
	private String OSVersion; //Liang.Shu - add OS version which can be used by console to update its database by the same API setVMApplicationStatus
	
	public boolean isSqlInstalled() {
		return sqlInstalled;
	}
	public void setSqlInstalled(boolean sqlInstalled) {
		this.sqlInstalled = sqlInstalled;
	}
	public boolean isExchangeInstalled() {
		return exchangeInstalled;
	}
	public void setExchangeInstalled(boolean exchangeInstalled) {
		this.exchangeInstalled = exchangeInstalled;
	}
	public boolean isD2dInstalled() {
		return d2dInstalled;
	}
	public void setD2dInstalled(boolean d2dInstalled) {
		this.d2dInstalled = d2dInstalled;
	}
	public boolean isArcserveInstalled() {
		return arcserveInstalled;
	}
	public void setArcserveInstalled(boolean arcserveInstalled) {
		this.arcserveInstalled = arcserveInstalled;
	}
	
	//add methods for OSVersion
	public void setOSVersion(String OSVersion) {
		this.OSVersion = OSVersion;
	}
	public String getOSVersion() {
		return OSVersion;
	}
}

package com.ca.arcflash.webservice.jni.model;

public class JPFCVMInfo {

	private String vmOSVersion;
	private boolean sqlserverInstalled;
	private boolean exchangeInstalled;
	private boolean hasDynamicDisk;
	private boolean hasStorageSpaces;

	public String getVmOSVersion() {
		return vmOSVersion;
	}
	public void setVmOSVersion(String vmOSVersion) {
		this.vmOSVersion = vmOSVersion;
	}
	public boolean isSqlserverInstalled() {
		return sqlserverInstalled;
	}
	public void setSqlserverInstalled(boolean sqlserverInstalled) {
		this.sqlserverInstalled = sqlserverInstalled;
	}
	public boolean isExchangeInstalled() {
		return exchangeInstalled;
	}
	public void setExchangeInstalled(boolean exchangeInstalled) {
		this.exchangeInstalled = exchangeInstalled;
	}
	public boolean isHasDynamicDisk() {
		return hasDynamicDisk;
	}
	public void setHasDynamicDisk(boolean hasDynamicDisk) {
		this.hasDynamicDisk = hasDynamicDisk;
	}
	public boolean isHasStorageSpaces() {
		return hasStorageSpaces;
	}
	public void setHasStorageSpaces(boolean hasStorageSpaces) {
		this.hasStorageSpaces = hasStorageSpaces;
	}
	
}

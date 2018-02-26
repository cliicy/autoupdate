package com.ca.arcflash.webservice.jni.model;

public class JBackupDestinationInfo {
	private String fullBackupSize;
	private String incrementalBackupSize;
	private String resyncBackupSize;
	private String totalSize;
	private String totalFreeSize;
	private String catalogSize;
	
	public String getCatalogSize() {
		return catalogSize;
	}
	public void setCatalogSize(String catalogSize) {
		this.catalogSize = catalogSize;
	}
	public String getFullBackupSize() {
		return fullBackupSize;
	}
	public void setFullBackupSize(String fullBackupSize) {
		this.fullBackupSize = fullBackupSize;
	}
	public String getIncrementalBackupSize() {
		return incrementalBackupSize;
	}
	public void setIncrementalBackupSize(String incrementalBackupSize) {
		this.incrementalBackupSize = incrementalBackupSize;
	}
	public String getResyncBackupSize() {
		return resyncBackupSize;
	}
	public void setResyncBackupSize(String resyncBackupSize) {
		this.resyncBackupSize = resyncBackupSize;
	}
	public String getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(String totalSize) {
		this.totalSize = totalSize;
	}
	public String getTotalFreeSize() {
		return totalFreeSize;
	}
	public void setTotalFreeSize(String totalFreeSize) {
		this.totalFreeSize = totalFreeSize;
	}
}

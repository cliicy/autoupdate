package com.ca.arcflash.webservice.jni.model;

public class JBackupInfo {
	private String name;
	private String status;
	private String type;
	private String date;
	private String time;
	private String logicalSize;
	private String size;
	private String backupSessionID;
	private long   catalogFlag; //1 has catalog, 0 has no catalog.
	private int backupSetFlag;
	private String backupDest;
	private int periodRetentionFlag;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getLogicalSize() {
		return logicalSize;
	}
	public void setLogicalSize(String logicalSize) {
		this.logicalSize = logicalSize;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
	public String getbackupSessionID() {
		return backupSessionID;
	}
	public void setbackupSessionID(String in_backupSessionID) {
		this.backupSessionID = in_backupSessionID;
	}
	public long getCatalogFlag()
	{
		return catalogFlag;
	}
	public void setCatalogFlag(long catalogFlag)
	{
		this.catalogFlag = catalogFlag;
	}
	public int getBackupSetFlag() {
		return backupSetFlag;
	}
	public void setBackupSetFlag(int backupSetFlag) {
		this.backupSetFlag = backupSetFlag;
	}
	public String getBackupDest() {
		return backupDest;
	}
	public void setBackupDest(String backupDest) {
		this.backupDest = backupDest;
	}
	public int getPeriodRetentionFlag() {
		return periodRetentionFlag;
	}
	public void setPeriodRetentionFlag(int periodRetentionFlag) {
		this.periodRetentionFlag = periodRetentionFlag;
	}
}

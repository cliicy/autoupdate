package com.ca.arcflash.webservice.jni.model;

import java.util.List;

public class JBackupInfoSummary {
	private int recoveryPointCount;
	private int recoveryPointCount4Repeat;
	private int recoveryPointCount4Day;
	private int recoveryPointCount4Week;
	private int recoveryPointCount4Month;
	
	private int reocverySetCount;
	private List<JBackupInfo> backupInfoList;
	private JBackupDestinationInfo destinationInfo;
	private int errorCode;
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public int getRecoveryPointCount() {
		return recoveryPointCount;
	}
	public void setRecoveryPointCount(int recoveryPointCount) {
		this.recoveryPointCount = recoveryPointCount;
	}
	public List<JBackupInfo> getBackupInfoList() {
		return backupInfoList;
	}
	public void setBackupInfoList(List<JBackupInfo> backupInfoList) {
		this.backupInfoList = backupInfoList;
	}
	public JBackupDestinationInfo getDestinationInfo() {
		return destinationInfo;
	}
	public void setDestinationInfo(JBackupDestinationInfo destinationInfo) {
		this.destinationInfo = destinationInfo;
	}
	public int getReocverySetCount() {
		return reocverySetCount;
	}
	public void setReocverySetCount(int reocverySetCount) {
		this.reocverySetCount = reocverySetCount;
	}
	
	public int getRecoveryPointCount4Day() {
		return recoveryPointCount4Day;
	}
	public void setRecoveryPointCount4Day(int recoveryPointCount4Day) {
		this.recoveryPointCount4Day = recoveryPointCount4Day;
	}
	public int getRecoveryPointCount4Week() {
		return recoveryPointCount4Week;
	}
	public void setRecoveryPointCount4Week(int recoveryPointCount4Week) {
		this.recoveryPointCount4Week = recoveryPointCount4Week;
	}
	public int getRecoveryPointCount4Month() {
		return recoveryPointCount4Month;
	}
	public void setRecoveryPointCount4Month(int recoveryPointCount4Month) {
		this.recoveryPointCount4Month = recoveryPointCount4Month;
	}
	public int getRecoveryPointCount4Repeat() {
		return recoveryPointCount4Repeat;
	}
	public void setRecoveryPointCount4Repeat(int recoveryPointCount4Repeat) {
		this.recoveryPointCount4Repeat = recoveryPointCount4Repeat;
	}
}

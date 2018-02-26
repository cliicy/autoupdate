package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModel;

public class EncrypedRecoveryPoint extends BaseModel{
	
	public enum VerifyStatus{
		FAIL_VERIFIED, SUCCESS_VERIFIED, VERIFYING
	}
	
	private static final long serialVersionUID = -1110618755788291187L;
	
	public Integer getSessionNumber() {
		return (Integer)get("sessionNumber");
	}
	public void setSessionNumber(Integer sessionNumber) {
		set("sessionNumber", sessionNumber);
	}
	public String getBackupDestination() {
		return get("backupDestination");
	}
	public void setBackupDestination(String backupDestination) {
		set("backupDestination", backupDestination);
	}
	public Date getBackupDate() {
		return (Date)get("backupDate");
	}
	public void setBackupDate(Date backupDate) {
		set("backupDate", backupDate);
	}
	public Long getBackupTimeZoneOffset() {
		return (Long)get("BKTimeZoneOffset");
	}
	public void setBackupTimeZoneOffset(Long offset) {
		set("BKTimeZoneOffset", offset);
	}
	public String getBackupJobName() {
		return get("backupJobName");
	}
	public void setBackupJobName(String backupJobName) {
		set("backupJobName", backupJobName);
	}
	public String getPassword() {
		return get("password");
	}
	public void setPassword(String password) {
		set("password", password);
	}
	public VerifyStatus getPasswordVerified() {
		return (VerifyStatus)get("passwordVerified");
	}
	public void setPasswordVerified(VerifyStatus passwordVerified) {
		set("passwordVerified", passwordVerified);
	}
	public String getPasswordHash() {
		return get("passwordHash");
	}
	public void setPasswordHash(String passwordHash) {
		set("passwordHash", passwordHash);
	}
	public String getSessionGuid(){
		return get("sessionGuid");
	}
	public void setSessionGuid(String sessionGuid){
		set("sessionGuid", sessionGuid);
	}
	
	public String getFullSessionGuid(){
		return get("fullSessionGuid");
	}
	public void setFullSessionGuid(String fullSessionGuid){
		set("fullSessionGuid", fullSessionGuid);
	}
}

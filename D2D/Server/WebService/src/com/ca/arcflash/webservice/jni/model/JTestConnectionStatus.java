package com.ca.arcflash.webservice.jni.model;

public class JTestConnectionStatus {
	public String ErrorMsg;
	public String FinalStatus;
	public long LastErrorCode;
	public int ServerStatus;
	
	public String LastModifiedDate;
	public String LastModifiedTime;
	
	public int getServerStatus(){
		return ServerStatus;
	}
	
	public void setServerStatus(int in_ServerStatus)
	{
		this.ServerStatus = in_ServerStatus;
	}
	
	public long getLastErrorCode() {
		return LastErrorCode;
	}
	public void setLastErrorCode(long in_LastErrorCode) {
		this.LastErrorCode = in_LastErrorCode;
	}

	public String getErrorMsg() {
		return ErrorMsg;
	}
	public void setErrorMsg(String in_ErrorMsg) {
		this.ErrorMsg = in_ErrorMsg;
	}
	
	public String getFinalStatus() {
		return FinalStatus;
	}
	public void setFinalStatus(String in_FinalStatus) {
		this.FinalStatus = in_FinalStatus;
	}
	
	public String getLastModifiedDate() {
		return LastModifiedDate;
	}
	public void setLastModifiedDate(String in_LastModifiedDate) {
		this.LastModifiedDate = in_LastModifiedDate;
	}
	
	public String getLastModifiedTime() {
		return LastModifiedTime;
	}
	public void setLastModifiedTime(String in_LastModifiedTime) {
		this.LastModifiedTime = in_LastModifiedTime;
	}
}

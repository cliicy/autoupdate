package com.ca.arcflash.webservice.replication;

import com.ca.arcflash.common.StringUtil;

public class SessionInfo {
	String sessionGuid="";
	String sessionType="";
	String sessionFolder="";
	String sessionName="";
	int 	sessionCompressType;
	boolean sessionFullMachine;		// chefr03: UPDATE_BACKUP_INFO
	long 	sessionDataSize;
	long 	sessionCatalogSize;
	long backupTime;
	private String encryptType;
	private String encryptPasswordHash;
	private String backupID;
	
	public long getBackupTime() {
		return backupTime;
	}
	public void setBackupTime(long backupTime) {
		this.backupTime = backupTime;
	}
	public int getSessionCompressType() {
		return sessionCompressType;
	}
	public void setSessionCompressType(int sessionCompressType) {
		this.sessionCompressType = sessionCompressType;
	}
	public String getSessionGuid() {
		return sessionGuid;
	}
	public void setSessionGuid(String sessionGuid) {
		this.sessionGuid = sessionGuid;
	}
	public String getSessionType() {
		return sessionType;
	}
	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}
	public long getSessionDataSize() {
		return sessionDataSize;
	}

	public void setSessionDataSize(long DataSize) {
		this.sessionDataSize = DataSize;
	}

	public long getSessionCatalogSize() {
		return sessionCatalogSize;
	}

	public void setSessionCatalogSize(long CatalogSize) {
		this.sessionCatalogSize = CatalogSize;
	}
	
		// chefr03: UPDATE_BACKUP_INFO
	public boolean getSessionFullMachineFlag() {
		return sessionFullMachine;
	}
	public void setSessionFullMachineFlag(boolean bIsFullMachine) {
		this.sessionFullMachine = bIsFullMachine;
	}
	// chefr03: UPDATE_BACKUP_INFO

	/**
	 * the folder is without ending \
	 * @return
	 */
	public String getSessionFolder() {
		
		if(sessionFolder.endsWith("\\")){
			int len = sessionFolder.length();
			if(len==1) return "";
			else return sessionFolder.substring(0,len-1);
		}
		else return sessionFolder;
	}
	public void setSessionFolder(String sessionFolder) {
		this.sessionFolder = sessionFolder;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public String getSessionName() {
		return sessionName;
	}
	@Override
	public String toString() {
		return "SessionInfo [sessionGuid=" + sessionGuid + ", sessionName="
				+ sessionName + ", sessionFolder=" + sessionFolder
				+ ", sessionType=" + sessionType
				+ ", isFullMachine=" + sessionFullMachine
				+ ", encryptType=" + encryptType + ", encryptPasswordHash=" + encryptPasswordHash + "]";
	}

	public boolean ifFull(){
		return getSessionType().equals("Full");
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null || !this.getClass().equals(obj.getClass()))
			return false;

		SessionInfo tmp = (SessionInfo)obj;
		return this.sessionGuid.equals(tmp.sessionGuid);
	}

	public String getEncryptType() {
		return encryptType;
	}

	public void setEncryptType(String encryptType) {
		this.encryptType = encryptType;
	}

	public String getEncryptPasswordHash() {
		return encryptPasswordHash;
	}

	public void setEncryptPasswordHash(String encryptPasswordHash) {
		this.encryptPasswordHash = encryptPasswordHash;
	}
	
	public boolean isSessionEncrypted() {
		return !StringUtil.isEmptyOrNull(encryptType) && !StringUtil.isEmptyOrNull(encryptPasswordHash);
	}

	public String getBackupID() {
		return backupID;
	}

	public void setBackupID(String backupID) {
		this.backupID = backupID;
	}

}

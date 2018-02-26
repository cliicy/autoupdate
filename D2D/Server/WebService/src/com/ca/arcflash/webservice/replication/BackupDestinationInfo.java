package com.ca.arcflash.webservice.replication;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.service.BackupService;

public class BackupDestinationInfo {
	String backupDestination = "";
	BackupService.CONN_INFO connInfo;

	public String getDomain() {
		return connInfo.getDomain();
	}
	public String getUserName() {
		return connInfo.getUserName();
	}
	public String getNetConnUserName() {
		String userName = connInfo.getUserName();
		if (StringUtil.isEmptyOrNull(userName))
			return null;
		
		if (!StringUtil.isEmptyOrNull(connInfo.getDomain())) {
			String domain = connInfo.getDomain();
			if (!domain.trim().endsWith("\\"))
				domain += "\\";
			userName = domain + userName;
		}
		
		return userName;
	}
	public String getNetConnPwd() {
		if (StringUtil.isEmptyOrNull(connInfo.getPwd()))
			return null;
		
		return connInfo.getPwd();
	}
	public String getPwd() {
		return connInfo.getPwd();
	}

	public BackupService.CONN_INFO getConnInfo() {
		return connInfo;
	}
	public void setConnInfo(BackupService.CONN_INFO connInfo) {
		this.connInfo = connInfo;
	}
	
	public String getBackupDestination() {
		return backupDestination;
	}
	public void setBackupDestination(String backupDestination) {
		this.backupDestination = backupDestination;
	}
}

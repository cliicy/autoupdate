package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;

public class LinuxBackupLocationInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4775220095246276899L;

	private String backupDestLocation;
	private String backupDestUser;
	private String backupDestPasswd;
	private int type;
	public String getBackupDestLocation() {
		return backupDestLocation;
	}
	public void setBackupDestLocation(String backupDestLocation) {
		this.backupDestLocation = backupDestLocation;
	}
	public String getBackupDestUser() {
		return backupDestUser;
	}
	public void setBackupDestUser(String backupDestUser) {
		this.backupDestUser = backupDestUser;
	}
	public String getBackupDestPasswd() {
		return backupDestPasswd;
	}
	public void setBackupDestPasswd(String backupDestPasswd) {
		this.backupDestPasswd = backupDestPasswd;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}

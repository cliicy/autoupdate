package com.ca.arcserve.edge.app.base.webservice.contract.action;

public class BackupNowTaskParameter extends ActionTaskParameter<Integer>{
	private static final long serialVersionUID = 1L;
	private int backupType;
	private String jobName;
	public int getBackupType() {
		return backupType;
	}
	public void setBackupType(int backupType) {
		this.backupType = backupType;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
}

package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;

public class LinuxBackupSettings implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1253577013805388081L;

	private LinuxBackupLocationInfo backupLocationInfo;
	
	private int linuxD2DServerId;
	@NonPlanContent
	private String linuxD2DServerName;
	private boolean isExclude=true;
	private String excludeVolumes;
	private String excludeFiles;
	private String serverScriptBeforeJob;
	private String serverScriptAfterJob;
	private String targetScriptBeforeJob;
	private String targetScriptAfterJob; 
	private String targetScriptBeforeSnapshot;
	private String targetScriptAfterSnapshot;
	public LinuxBackupLocationInfo getBackupLocationInfo() {
		return backupLocationInfo;
	}
	public void setBackupLocationInfo(LinuxBackupLocationInfo backupLocationInfo) {
		this.backupLocationInfo = backupLocationInfo;
	}
	public int getLinuxD2DServerId() {
		return linuxD2DServerId;
	}
	public void setLinuxD2DServerId(int linuxD2DServerId) {
		this.linuxD2DServerId = linuxD2DServerId;
	}
	public String getExcludeVolumes() {
		return excludeVolumes;
	}
	public void setExcludeVolumes(String excludeVolumes) {
		this.excludeVolumes = excludeVolumes;
	}
	public String getServerScriptBeforeJob() {
		return serverScriptBeforeJob;
	}
	public void setServerScriptBeforeJob(String serverScriptBeforeJob) {
		this.serverScriptBeforeJob = serverScriptBeforeJob;
	}
	public String getServerScriptAfterJob() {
		return serverScriptAfterJob;
	}
	public void setServerScriptAfterJob(String serverScriptAfterJob) {
		this.serverScriptAfterJob = serverScriptAfterJob;
	}
	public String getTargetScriptBeforeJob() {
		return targetScriptBeforeJob;
	}
	public void setTargetScriptBeforeJob(String targetScriptBeforeJob) {
		this.targetScriptBeforeJob = targetScriptBeforeJob;
	}
	public String getTargetScriptAfterJob() {
		return targetScriptAfterJob;
	}
	public void setTargetScriptAfterJob(String targetScriptAfterJob) {
		this.targetScriptAfterJob = targetScriptAfterJob;
	}
	public String getTargetScriptBeforeSnapshot() {
		return targetScriptBeforeSnapshot;
	}
	public void setTargetScriptBeforeSnapshot(String targetScriptBeforeSnapshot) {
		this.targetScriptBeforeSnapshot = targetScriptBeforeSnapshot;
	}
	public String getTargetScriptAfterSnapshot() {
		return targetScriptAfterSnapshot;
	}
	public void setTargetScriptAfterSnapshot(String targetScriptAfterSnapshot) {
		this.targetScriptAfterSnapshot = targetScriptAfterSnapshot;
	}
	public String getLinuxD2DServerName() {
		return linuxD2DServerName;
	}
	public void setLinuxD2DServerName(String linuxD2DServerName) {
		this.linuxD2DServerName = linuxD2DServerName;
	}
	public String getExcludeFiles() {
		return excludeFiles;
	}
	public void setExcludeFiles(String excludeFiles) {
		this.excludeFiles = excludeFiles;
	}
	public boolean isExclude() {
		return isExclude;
	}
	public void setExclude(boolean isExclude) {
		this.isExclude = isExclude;
	}	
}

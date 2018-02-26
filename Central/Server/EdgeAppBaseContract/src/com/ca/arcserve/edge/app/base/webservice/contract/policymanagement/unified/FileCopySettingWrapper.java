package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;

import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;

@SuppressWarnings("serial")
public class FileCopySettingWrapper implements Serializable{
	public static enum FileCopySourceType {BackUp, RPS}
	private ArchiveConfiguration archiveConfiguration;
	private FileCopySourceType fileCopySourceType;
	private String taskId;
	
	public ArchiveConfiguration getArchiveConfiguration() {
		return archiveConfiguration;
	}
	public void setArchiveConfiguration(ArchiveConfiguration archiveConfiguration) {
		this.archiveConfiguration = archiveConfiguration;
	}
	public FileCopySourceType getFileCopySourceType() {
		return fileCopySourceType;
	}
	public void setFileCopySourceType(FileCopySourceType fileCopySourceType) {
		this.fileCopySourceType = fileCopySourceType;
	}
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	

}

package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;

/**
 * @author Neo.Li
 *
 */
public class ArchiveToTapeSettingsWrapper implements Serializable{

	private static final long serialVersionUID = -3785907760629733047L;

	public static enum ArchiveToTapeSourceType {BackUp, RPS}
	private ArchiveToTapeSettings archiveToTapeSettings = new ArchiveToTapeSettings();
	private String taskId;
	private String archiveToTapeUUID;
	private ArchiveToTapeSourceType archiveToTapeSourceType;

	public ArchiveToTapeSettings getArchiveToTapeSettings() {
		return archiveToTapeSettings;
	}

	public void setArchiveToTapeSettings(ArchiveToTapeSettings archiveToTapeSettings) {
		this.archiveToTapeSettings = archiveToTapeSettings;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getArchiveToTapeUUID() {
		return archiveToTapeUUID;
	}

	public void setArchiveToTapeUUID(String archiveToTapeUUID) {
		this.archiveToTapeUUID = archiveToTapeUUID;
	}

	public ArchiveToTapeSourceType getArchiveToTapeSourceType() {
		return archiveToTapeSourceType;
	}

	public void setArchiveToTapeSourceType(ArchiveToTapeSourceType archiveToTapeSourceType) {
		this.archiveToTapeSourceType = archiveToTapeSourceType;
	}
	
	
	
	
}

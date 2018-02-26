package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;

public class ArchiveToTapeSource implements Serializable{
	private static final long serialVersionUID = 7485195370198938101L;
	private String taskId;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
}

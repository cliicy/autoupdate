package com.ca.arcserve.edge.app.base.webservice.contract.log;

import java.io.Serializable;

public class LogAddEntity implements Serializable {

	private static final long serialVersionUID = -8844785516456290125L;
	
	private Severity severity = Severity.Information;
	private int targetHostId;
	private long jobId;
	private String message = "";
	
	public static LogAddEntity create(Severity severity, String message) {
		return create(severity, 0, message);
	}
	
	public static LogAddEntity create(Severity severity, int targetHostId, String message) {
		LogAddEntity entity = new LogAddEntity();
		
		entity.setSeverity(severity);
		entity.setTargetHostId(targetHostId);
		entity.setMessage(message);
		
		return entity;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public int getTargetHostId() {
		return targetHostId;
	}

	public void setTargetHostId(int targetHostId) {
		this.targetHostId = targetHostId;
	}

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

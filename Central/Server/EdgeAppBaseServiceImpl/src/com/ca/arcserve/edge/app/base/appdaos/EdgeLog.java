package com.ca.arcserve.edge.app.base.appdaos;

import java.util.Date;

public class EdgeLog {
	private long id;
	private int severity;
	private int productType;
	private Date logUtcTime;
	private String nodeName;
	private String targetNodeName;
	private String targetVMName;
	private long jobId;
	private int jobType;
	private String messageText;
	// BUG 755011 
	// add start
	private String siteName;
    // add end
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getJobType() {
		return jobType;
	}

	public void setJobType(int jobType) {
		this.jobType = jobType;
	}

	public int getSeverity() {
		return severity;
	}
	
	public void setSeverity(int severity) {
		this.severity = severity;
	}
	
	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public Date getLogUtcTime() {
		return logUtcTime;
	}

	public void setLogUtcTime(Date logUtcTime) {
		this.logUtcTime = logUtcTime;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	public String getTargetNodeName() {
		return targetNodeName;
	}

	public void setTargetNodeName(String targetNodeName) {
		this.targetNodeName = targetNodeName;
	}

	public String getTargetVMName() {
		return targetVMName;
	}

	public void setTargetVMName(String targetVMName) {
		this.targetVMName = targetVMName;
	}

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	
	// BUG 755011
	// ADD START
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	// ADD END

}

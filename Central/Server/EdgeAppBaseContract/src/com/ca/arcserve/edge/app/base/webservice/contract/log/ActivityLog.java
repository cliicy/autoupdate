package com.ca.arcserve.edge.app.base.webservice.contract.log;

import java.io.Serializable;
import java.util.Date;

public class ActivityLog implements Serializable {

	private static final long serialVersionUID = -3695293040171146547L;
	private long id;
	private Severity severity;
	private Date time;
	private Module module;
	private String nodeName;
	private String targetNodeName;
	private String targetVMName;
	private long jobId;
	private int jobType;
	private String message;
	private LogProductType productType;
	private int hostId;
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
	public LogProductType getProductType() {
		return productType;
	}
	public void setProductType(LogProductType productType) {
		this.productType = productType;
	}
	public int getJobType() {
		return jobType;
	}
	public void setJobType(int jobType) {
		this.jobType = jobType;
	}
	public Severity getSeverity() {
		return severity;
	}
	public void setSeverity(Severity severity) {
		this.severity = severity;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Module getModule() {
		return module;
	}
	public void setModule(Module module) {
		this.module = module;
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	// BUG 755011 
	// add start
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	// add end
}

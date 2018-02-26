package com.ca.arcserve.edge.app.base.webservice.contract.jobhistory;

import java.io.Serializable;

public class CancelJobParameter  implements Serializable{
	
	private static final long serialVersionUID = 3837074713732788982L;
	
	private long jobId;
	private long jobType;
	private int serverId;
	private int nodeId;
	
	public long getJobId() {
		return jobId;
	}
	public void setJobId(long jobId) {
		this.jobId = jobId;
	}
	public long getJobType() {
		return jobType;
	}
	public void setJobType(long jobType) {
		this.jobType = jobType;
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	

}

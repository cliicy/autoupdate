package com.ca.arcserve.edge.app.base.appdaos;

import java.util.Date;

public class EdgeJobHistory {
	private long id;
	private long jobId;
	private long jobType;
	private long jobStatus;
	private Date jobUTCStartTime;
	private Date jobLocalStartTime;
	
	private String version;
	private int productType;
	private long jobMethod;
	private Date jobUTCEndTime;
	private Date jobLocalEndTime;
	private String serverId;
	private String agentId;
	private String sourceRPSId;
	private String targetRPSId;
	private String sourceDataStoreUUID;
	private String targetDataStoreUUID;
	
	private String name;
	private int planId;
	private String planUuid;
	private int nodeId;
	private String agentName;
	private String serverName;
	private String jobUUID;
	private String agentNodeName;
	private String serverNodeName;
	
	private String agentUUID;
	
    public String getAgentUUID() {
		return agentUUID;
	}

	public void setAgentUUID(String agentUUID) {
		this.agentUUID = agentUUID;
	}

	public String getJobUUID() {
		return jobUUID;
	}
	public void setJobUUID(String jobUUID) {
		this.jobUUID = jobUUID;
	}
	public String getAgentNodeName() {
		return agentNodeName;
	}
	public void setAgentNodeName(String agentNodeName) {
		if( agentNodeName != null && (!agentNodeName.isEmpty())){
			if(agentNodeName.contains("@")){
				agentNodeName = agentNodeName.substring(0, agentNodeName.indexOf("@"));
			}
		}
		this.agentNodeName = agentNodeName;
	}
	public String getServerNodeName() {
		return serverNodeName;
	}
	public void setServerNodeName(String serverNodeName) {
		this.serverNodeName = serverNodeName;
	}
	public String getAgentName() {
		return agentName;
//		if (agentName != null && !"".equals(agentName))
//			return agentName;
//		else 
//			return agentNodeName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getPlanId() {
		return planId;
	}

	public void setPlanId(int planId) {
		this.planId = planId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public long getJobMethod() {
		return jobMethod;
	}

	public void setJobMethod(long jobMethod) {
		this.jobMethod = jobMethod;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getSourceRPSId() {
		return sourceRPSId;
	}

	public void setSourceRPSId(String sourceRPSId) {
		this.sourceRPSId = sourceRPSId;
	}

	public String getTargetRPSId() {
		return targetRPSId;
	}

	public void setTargetRPSId(String targetRPSId) {
		this.targetRPSId = targetRPSId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
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
	
	public long getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(long jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Date getJobUTCStartTime() {
		return jobUTCStartTime;
	}

	public void setJobUTCStartTime(Date jobUTCStartTime) {
		this.jobUTCStartTime = jobUTCStartTime;
	}

	public Date getJobLocalStartTime() {
		return jobLocalStartTime;
	}

	public void setJobLocalStartTime(Date jobLocalStartTime) {
		this.jobLocalStartTime = jobLocalStartTime;
	}

	public Date getJobUTCEndTime() {
		return jobUTCEndTime;
	}

	public void setJobUTCEndTime(Date jobUTCEndTime) {
		this.jobUTCEndTime = jobUTCEndTime;
	}

	public Date getJobLocalEndTime() {
		return jobLocalEndTime;
	}

	public void setJobLocalEndTime(Date jobLocalEndTime) {
		this.jobLocalEndTime = jobLocalEndTime;
	}

	public String getSourceDataStoreUUID() {
		return sourceDataStoreUUID;
	}

	public void setSourceDataStoreUUID(String sourceDataStoreUUID) {
		this.sourceDataStoreUUID = sourceDataStoreUUID;
	}

	public String getTargetDataStoreUUID() {
		return targetDataStoreUUID;
	}

	public void setTargetDataStoreUUID(String targetDataStoreUUID) {
		this.targetDataStoreUUID = targetDataStoreUUID;
	}

	public String getPlanUuid() {
		return planUuid;
	}

	public void setPlanUuid(String planUuid) {
		this.planUuid = planUuid;
	}
	
}

package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;


public class MergeStatusModel extends BaseModelData {
	private static final long serialVersionUID = 8323211930367867704L;
	public static final int NOTRUNNING = 0;
	public static final int PAUSED = 1; 
	public static final int PAUSED_MANUALLY = 2;   
	public static final int RUNNING = 3;  
	public static final int FAILED = 4;
	public static final int PAUSED_NO_SCHEDULE = 5;
	public static final int TO_RUN = 6;
	public static final int PAUSING = 7;
	
	public MergeJobMonitorModel jobMonitor;
	
	public Integer getStatus() {
		return (Integer)get("Status");
	}
	
	public void setStatus(Integer status) {
		set("Status", status);
	}
	
	public Boolean canResume() {
		Boolean canResume = (Boolean)get("canResume");
		if(canResume == null)
			return Boolean.FALSE;
		else
			return canResume;
	}
	
	public void setCanResume(Boolean canResume) {
		set("canResume", canResume);
	}
	
	public Boolean isInSchedule() {
		Boolean inSchedule = (Boolean)get("inSchedule");
		if(inSchedule == null)
			return Boolean.FALSE;
		else
			return inSchedule;
	}
	
	public void setInSchedule(Boolean inSchedule) {
		set("inSchedule", inSchedule);
	}
	
	public String getUUID() {
		return (String)get("uuid");
	}
	
	public void setUUID(String uuid){
		set("uuid", uuid);
	}
	
	public Boolean isRecoverySet() {
		return (Boolean)get("UseRecoverySet");
	}
	
	public void setRecoverySet(Boolean useRecoverySet) {
		set("UseRecoverySet", useRecoverySet);
	}
	public Long getUpdateTime() {
		return (Long)get("UpdateTime");
	}
	public void setUpdateTime(Long time) {
		set("UpdateTime", time);
	}
	public long getJobType() {
		return (Long)get("JobType");
	}

	public void setJobType(long jobType) {
		set("JobType", jobType);
	}
	
	public String getJobMonitorId() {
		return get("jobMonitorId");
	}

	public void setJobMonitorId(String jobMonitorId) {
		set("jobMonitorId", jobMonitorId);
	}
	
	public String getD2dUuid() {
		return get("d2dUuid");
	}

	public void setD2dUuid(String d2dUuid) {
		set("d2dUuid",d2dUuid);
	}
	public String getd2dServerName() {
		return get("d2dServerName");
	}

	public void setd2dServerName(String d2dServerName) {
		set("d2dServerName", d2dServerName);
	}
	
	public String getVmInstanceUUID() {
		return get("vmInstanceUUID");
	}
	
	public void setVmInstanceUUID(String vmInstanceUUID) {
		set("vmInstanceUUID", vmInstanceUUID);
	}
	
	public Integer getRunningServerId() {
		return (Integer)get("runningServerId");
	}

	public void setRunningServerId(Integer runningServerId) {
		set("runningServerId", runningServerId);
	}

	public Integer getNodeId() {
		return (Integer)get("nodeId");
	}

	public void setNodeId(Integer nodeId) {
		set("nodeId", nodeId);
	}
	public String getAgentNodeName() {
		return (String)get("agentNodeName");
	}

	public void setAgentNodeName(String agentNodeName) {
		set("agentNodeName", agentNodeName);
	}

	public String getServerNodeName() {
		return (String)get("serverNodeName");
	}

	public void setServerNodeName(String serverNodeName) {
		set("serverNodeName", serverNodeName);
	}
	public Integer getHistoryProductType() {
		return (Integer)get("historyProductType");
	}

	public void setHistoryProductType(Integer historyProductType) {
		set("historyProductType", historyProductType);
	}
	
	public Boolean isRunningOnRPS() {
		return (Boolean)get("isRunningOnRPS");
	}

	public void setRunningOnRPS(Boolean isRunningOnRPS) {
		set("isRunningOnRPS", isRunningOnRPS);
	}
	
	public Long getStartTime() {
		return (Long)get("startTime");
	}

	public void setStartTime(Long startTime) {
		set("startTime", startTime);
	}
	
	public long getJobId() {
		return (Long)get("jobId");
	}

	public void setJobId(long jobId) {
		set("jobId", jobId);
	}
	
	public String getVmHostName() {
		return (String)get("vmHostName");
	}

	public void setVmHostName(String vmHostName) {
		set("vmHostName", vmHostName);
	}
}

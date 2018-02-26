package com.ca.arcserve.edge.app.base.webservice.monitor.model;

import java.util.List;

public class JobMonitorResultModel {
	private String PlanGlobalID;
	private String PlanVersionID;
	private String NodeUUID;
	private String NodeName;
	private String ScheduleType;
	private String JobID;
	private String KBPerSec;
	private String JobPercent;
	private String ElapsedTime;
	private String RemainTime;
	private String KBProcessed;
	private String KBEstimated;
	private String Status;
	private String StartTime;
	private String EndTime;
	private String ChildJobID;
	private List<Integer> BackupSession;
	private List<Integer> CurrentSession;
	private String Encrypt;
	private String Compress;
	private String RPSUUID;
	private String DataStoreUUID;
	private String GroupName;
	private List<String> RelatedNodes;
	public String getPlanGlobalID() {
		return PlanGlobalID;
	}
	public void setPlanGlobalID(String planGlobalID) {
		PlanGlobalID = planGlobalID;
	}
	public String getPlanVersionID() {
		return PlanVersionID;
	}
	public void setPlanVersionID(String planVersionID) {
		PlanVersionID = planVersionID;
	}
	
	public String getNodeUUID() {
		return NodeUUID;
	}
	public void setNodeUUID(String nodeUUID) {
		NodeUUID = nodeUUID;
	}
	public String getNodeName() {
		return NodeName;
	}
	public void setNodeName(String nodeName) {
		NodeName = nodeName;
	}
	public String getScheduleType() {
		return ScheduleType;
	}
	public void setScheduleType(String scheduleType) {
		ScheduleType = scheduleType;
	}
	public String getJobID() {
		return JobID;
	}
	public void setJobID(String jobID) {
		JobID = jobID;
	}
	public String getKBPerSec() {
		return KBPerSec;
	}
	public void setKBPerSec(String kBPerSec) {
		KBPerSec = kBPerSec;
	}
	public String getJobPercent() {
		return JobPercent;
	}
	public void setJobPercent(String jobPercent) {
		JobPercent = jobPercent;
	}
	public String getElapsedTime() {
		return ElapsedTime;
	}
	public void setElapsedTime(String elapsedTime) {
		ElapsedTime = elapsedTime;
	}
	public String getRemainTime() {
		return RemainTime;
	}
	public void setRemainTime(String remainTime) {
		RemainTime = remainTime;
	}
	public String getKBProcessed() {
		return KBProcessed;
	}
	public void setKBProcessed(String kBProcessed) {
		KBProcessed = kBProcessed;
	}
	public String getKBEstimated() {
		return KBEstimated;
	}
	public void setKBEstimated(String kBEstimated) {
		KBEstimated = kBEstimated;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getStartTime() {
		return StartTime;
	}
	public void setStartTime(String startTime) {
		StartTime = startTime;
	}
	public String getEndTime() {
		return EndTime;
	}
	public void setEndTime(String endTime) {
		EndTime = endTime;
	}
	public String getChildJobID() {
		return ChildJobID;
	}
	public void setChildJobID(String childJobID) {
		ChildJobID = childJobID;
	}
	public List<Integer> getBackupSession() {
		return BackupSession;
	}
	public void setBackupSession(List<Integer> backupSession) {
		BackupSession = backupSession;
	}
	public List<Integer> getCurrentSession() {
		return CurrentSession;
	}
	public void setCurrentSession(List<Integer> currentSession) {
		CurrentSession = currentSession;
	}
	public String getEncrypt() {
		return Encrypt;
	}
	public void setEncrypt(String encrypt) {
		Encrypt = encrypt;
	}
	public String getCompress() {
		return Compress;
	}
	public void setCompress(String compress) {
		Compress = compress;
	}
	public String getRPSUUID() {
		return RPSUUID;
	}
	public void setRPSUUID(String rPSUUID) {
		RPSUUID = rPSUUID;
	}
	public String getDataStoreUUID() {
		return DataStoreUUID;
	}
	public void setDataStoreUUID(String dataStoreUUID) {
		DataStoreUUID = dataStoreUUID;
	}
	public String getGroupName() {
		return GroupName;
	}
	public void setGroupName(String groupName) {
		GroupName = groupName;
	}
	public List<String> getRelatedNodes() {
		return RelatedNodes;
	}
	public void setRelatedNodes(List<String> relatedNodes) {
		RelatedNodes = relatedNodes;
	}
	
}

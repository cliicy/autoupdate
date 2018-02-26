package com.ca.arcserve.edge.app.base.webservice.contract.asbumonitor;

import java.io.Serializable;
import java.util.List;

import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;


public abstract class JobMonitorModel extends FlashJobMonitor implements Serializable{
	private static final long serialVersionUID = 7265440889502627340L;
	private int productType;
	private String jobMonitorId;
	private int serverId;
	private String planVersionUUID;
	private String nodeName;
	private String scheduleType;
	private long kbPerSec;
	private String jobPercent;
	private long kbProcessed;
	private long kbEstimated;
	private String status;
	private long endTime;
	private String nodeUUID;
	private List<Integer> backupSession;
	private List<Integer> currentSession;
	private String encrypt;
	private String compress;
	private String rpsUUID;
	private String dataStoreUUID;
	private String groupName;
	private List<String> relatedNodes;
	private List<String> nodeUUIDs;
	public int getProductType() {
		return productType;
	}
	public void setProductType(int productType) {
		this.productType = productType;
	}
	public String getJobMonitorId() {
		return jobMonitorId;
	}
	public void setJobMonitorId(String jobMonitorId) {
		this.jobMonitorId = jobMonitorId;
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public String getPlanVersionUUID() {
		return planVersionUUID;
	}
	public void setPlanVersionUUID(String planVersionUUID) {
		this.planVersionUUID = planVersionUUID;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getScheduleType() {
		return scheduleType;
	}
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}
	public long getKbPerSec() {
		return kbPerSec;
	}
	public void setKbPerSec(long kbPerSec) {
		this.kbPerSec = kbPerSec;
	}
	public String getJobPercent() {
		return jobPercent;
	}
	public void setJobPercent(String jobPercent) {
		this.jobPercent = jobPercent;
	}
	public long getKbProcessed() {
		return kbProcessed;
	}
	public void setKbProcessed(long kbProcessed) {
		this.kbProcessed = kbProcessed;
	}
	public long getKbEstimated() {
		return kbEstimated;
	}
	public void setKbEstimated(long kbEstimated) {
		this.kbEstimated = kbEstimated;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	@EncryptSave
	public String getNodeUUID() {
		return nodeUUID;
	}
	public void setNodeUUID(String nodeUUID) {
		this.nodeUUID = nodeUUID;
	}
	public List<String> getNodeUUIDs() {
		return nodeUUIDs;
	}
	public void setNodeUUIDs(List<String> nodeUUIDs) {
		this.nodeUUIDs = nodeUUIDs;
	}
	public List<Integer> getBackupSession() {
		return backupSession;
	}
	public void setBackupSession(List<Integer> backupSession) {
		this.backupSession = backupSession;
	}
	public List<Integer> getCurrentSession() {
		return currentSession;
	}
	public void setCurrentSession(List<Integer> currentSession) {
		this.currentSession = currentSession;
	}
	public String getEncrypt() {
		return encrypt;
	}
	public void setEncrypt(String encrypt) {
		this.encrypt = encrypt;
	}
	public String getCompress() {
		return compress;
	}
	public void setCompress(String compress) {
		this.compress = compress;
	}
	public String getRpsUUID() {
		return rpsUUID;
	}
	public void setRpsUUID(String rpsUUID) {
		this.rpsUUID = rpsUUID;
	}
	public String getDataStoreUUID() {
		return dataStoreUUID;
	}
	public void setDataStoreUUID(String dataStoreUUID) {
		this.dataStoreUUID = dataStoreUUID;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public List<String> getRelatedNodes() {
		return relatedNodes;
	}
	public void setRelatedNodes(List<String> relatedNodes) {
		this.relatedNodes = relatedNodes;
	}
	
}

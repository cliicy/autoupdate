package com.ca.arcserve.edge.app.base.webservice.monitor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

import com.ca.arcflash.listener.service.event.FlashEvent.Source;
@XmlType(name="monitor")
public class JobMonitor implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String jobId;
	
	private long jobType;
	
	private long jobMethod;
	
	private long jobStatus;
	
	private int productType;
	
	private int serverId;
	
	private String planUUID;
	
	private Date startTime;
	
	private JobPhase phase;
	
	private Source source;
	
	private String planGlobalUUID;
	
	private String nodeUUID;
	private String nodeName;
	private String scheduleType;
	private String kbPerSec;
	private String jobPercent;
	private String elapsedTime;
	private String remainTime;
	private String kbProcessed;
	private String kbEstimated;
	private String status;
	private Date endTime;
	private List<Integer> backupSession;
	private List<Integer> currentSession;
	private String encrypt;
	private String compress;
	private String rpsUUID;
	private String dataStoreUUID;
	private String groupName;
	private List<String> relatedNodes;
	private String destinationType;
	private String destinationPath;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	
	public long getJobType() {
		return jobType;
	}

	public void setJobType(long jobType) {
		this.jobType = jobType;
	}

	public long getJobMethod() {
		return jobMethod;
	}

	public void setJobMethod(long jobMethod) {
		this.jobMethod = jobMethod;
	}
	
	public long getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(long jobStatus) {
		this.jobStatus = jobStatus;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getPlanUUID() {
		return planUUID;
	}

	public void setPlanUUID(String planUUID) {
		this.planUUID = planUUID;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public JobPhase getPhase() {
		return phase;
	}

	public void setPhase(JobPhase phase) {
		this.phase = phase;
	}
	
	public void clear(){
		jobId = null;
		phase = JobPhase.CLEAR;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public String getPlanGlobalUUID() {
		return planGlobalUUID;
	}

	public void setPlanGlobalUUID(String planGlobalUUID) {
		this.planGlobalUUID = planGlobalUUID;
	}

	public String getNodeUUID() {
		return nodeUUID;
	}

	public void setNodeUUID(String nodeUUID) {
		this.nodeUUID = nodeUUID;
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

	public String getKbPerSec() {
		return kbPerSec;
	}

	public void setKbPerSec(String kbPerSec) {
		this.kbPerSec = kbPerSec;
	}

	public String getJobPercent() {
		return jobPercent;
	}

	public void setJobPercent(String jobPercent) {
		this.jobPercent = jobPercent;
	}

	public String getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public String getRemainTime() {
		return remainTime;
	}

	public void setRemainTime(String remainTime) {
		this.remainTime = remainTime;
	}

	public String getKbProcessed() {
		return kbProcessed;
	}

	public void setKbProcessed(String kbProcessed) {
		this.kbProcessed = kbProcessed;
	}

	public String getKbEstimated() {
		return kbEstimated;
	}

	public void setKbEstimated(String kbEstimated) {
		this.kbEstimated = kbEstimated;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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
	
	public String getDestinationType() {
		return destinationType;
	}

	public void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}
}

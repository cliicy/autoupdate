package com.ca.arcserve.edge.app.rps.webservice.contract.rps.job.filecopyJob;


import java.util.List;

import com.ca.arcflash.rps.webservice.replication.ManualReplicationItem;

public class ManualFilecopyParam implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<String> nodeUuidList;
	private List<Integer> nodeIdList;
	private int srcRpsHostId;
	private String srcRpsHostName;
	private String srcDataStoreUUID;
	private String srcDataStoreName;
	private String srcPolicyUUID;
	private boolean bArchiveToDrive;
	private String strArchiveToDrivePath;
	private String strCloudBucket;	
	private String currentTaskName;
	private String parentTaskName;
	private String currentTaskId;
	private String parentTaskId;
	
		
	public List<String> getNodeUuidList() {
		return nodeUuidList;
	}
	public void setNodeUuidList(List<String> nodeUuidList) {
		this.nodeUuidList = nodeUuidList;
	}
	public int getSrcRpsHostId() {
		return srcRpsHostId;
	}
	public void setSrcRpsHostId(int srcRpsHostId) {
		this.srcRpsHostId = srcRpsHostId;
	}
	public String getSrcRpsHostName() {
		return srcRpsHostName;
	}
	public void setSrcRpsHostName(String srcRpsHostName) {
		this.srcRpsHostName = srcRpsHostName;
	}
	public String getSrcDataStoreUUID() {
		return srcDataStoreUUID;
	}
	public void setSrcDataStoreUUID(String srcDataStoreUUID) {
		this.srcDataStoreUUID = srcDataStoreUUID;
	}
	public String getSrcDataStoreName() {
		return srcDataStoreName;
	}
	public void setSrcDataStoreName(String srcDataStoreName) {
		this.srcDataStoreName = srcDataStoreName;
	}
	
	public boolean isbArchiveToDrive() {
		return bArchiveToDrive;
	}
	public void setbArchiveToDrive(boolean bArchiveToDrive) {
		this.bArchiveToDrive = bArchiveToDrive;
	}
	public String getStrArchiveToDrivePath() {
		return strArchiveToDrivePath;
	}
	public void setStrArchiveToDrivePath(String strArchiveToDrivePath) {
		this.strArchiveToDrivePath = strArchiveToDrivePath;
	}
	public String getStrCloudBucket() {
		return strCloudBucket;
	}
	public void setStrCloudBucket(String strCloudBucket) {
		this.strCloudBucket = strCloudBucket;
	}
	public String getSrcPolicyUUID() {
		return srcPolicyUUID;
	}
	public void setSrcPolicyUUID(String srcPolicyUUID) {
		this.srcPolicyUUID = srcPolicyUUID;
	}
	public List<Integer> getNodeIdList() {
		return nodeIdList;
	}
	public void setNodeIdList(List<Integer> nodeIdList) {
		this.nodeIdList = nodeIdList;
	}
	
	public String getCurrentTaskName() {
		return currentTaskName;
	}
	public void setCurrentTaskName(String currentTaskName) {
		this.currentTaskName = currentTaskName;
	}
	public String getParentTaskName() {
		return parentTaskName;
	}
	public void setParentTaskName(String parentTaskName) {
		this.parentTaskName = parentTaskName;
	}
	public String getCurrentTaskId() {
		return currentTaskId;
	}
	public void setCurrentTaskId(String currentTaskId) {
		this.currentTaskId = currentTaskId;
	}
	public String getParentTaskId() {
		return parentTaskId;
	}
	public void setParentTaskId(String parentTaskId) {
		this.parentTaskId = parentTaskId;
	}
	
	@Override
	public String toString(){
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("ManualFilecopyParam[");
		sBuffer.append(" srcRpsHostId="+srcRpsHostId + ", srcRpsHostName="+srcRpsHostName);
		sBuffer.append(", srcDataStoreUUID="+srcDataStoreUUID + ", srcDataStoreName="+srcDataStoreName);
		sBuffer.append(", srcPolicyUUID=" + srcPolicyUUID);
		sBuffer.append(", bArchiveToDrive=" + bArchiveToDrive);
		sBuffer.append(", strArchiveToDrivePath="+strArchiveToDrivePath);
		sBuffer.append(", strCloudBucket="+strCloudBucket);
		sBuffer.append(", parentTaskId=" + parentTaskId + ", parentTaskName=" + parentTaskName);
		sBuffer.append(", currentTaskId=" + currentTaskId + ", currentTaskName=" + currentTaskName);
		sBuffer.append("]");
		return sBuffer.toString();
	}
}
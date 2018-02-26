package com.ca.arcserve.edge.app.rps.webservice.contract.rps.node;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ProtectedNodeInDestination implements Serializable{
	private static final long serialVersionUID = 1L;
	private String nodeUuid;
	private String nodeName;
	private Date mostRecentRecoveryPoint;
	private long recoveryCount;
	private String planUuid;
	private String planName;
	private int nodeId;
	private String destination;
	private String password;
	private String username;
	private boolean haveSessions = true;
	private String encryptPasswordHash;
	private Date firstRecoveryPoint;
	private int parentRpsNodeId;
	private String parentDataStoreUUID;
	private String parentDataStoreName;
	//Bug 761350: remove "replicate now" from browse recovery point page
	//private boolean canReplication = false;
	//private String replicationRPSPolicy;
	private Boolean validated = null;
	private List<String> sessionGuidList;
	private String sfUserName;
	private String sfPassword;
	private String sfFullPath;
	private boolean isIntegral;
	private boolean linux = false;
	
	public List<String> getSessionGuidList() {
		return sessionGuidList;
	}
	public void setSessionGuidList(List<String> sessionGuidList) {
		this.sessionGuidList = sessionGuidList;
	}
	public String getSfUserName() {
		return sfUserName;
	}
	public void setSfUserName(String sfUserName) {
		this.sfUserName = sfUserName;
	}
	public String getSfPassword() {
		return sfPassword;
	}
	public void setSfPassword(String sfPassword) {
		this.sfPassword = sfPassword;
	}
	public String getSfFullPath() {
		return sfFullPath;
	}
	public void setSfFullPath(String sfFullPath) {
		this.sfFullPath = sfFullPath;
	}
	public boolean isIntegral() {
		return isIntegral;
	}
	public void setIntegral(boolean isIntegral) {
		this.isIntegral = isIntegral;
	}
	public Boolean isValidated() {
		return validated;
	}
	public void setValidated(boolean validated) {
		this.validated = validated;
	}
	public Date getFirstRecoveryPoint() {
		return firstRecoveryPoint;
	}
	public void setFirstRecoveryPoint(Date firstRecoveryPoint) {
		this.firstRecoveryPoint = firstRecoveryPoint;
	}
	public String getEncryptPasswordHash() {
		return encryptPasswordHash;
	}
	public void setEncryptPasswordHash(String encryptPasswordHash) {
		this.encryptPasswordHash = encryptPasswordHash;
	}
	public String getNodeUuid() {
		return nodeUuid;
	}
	public void setNodeUuid(String nodeUuid) {
		this.nodeUuid = nodeUuid;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public Date getMostRecentRecoveryPoint() {
		return mostRecentRecoveryPoint;
	}
	public void setMostRecentRecoveryPoint(Date mostRecentRecoveryPoint) {
		this.mostRecentRecoveryPoint = mostRecentRecoveryPoint;
	}
	public long getRecoveryCount() {
		return recoveryCount;
	}
	public void setRecoveryCount(long recoveryCount) {
		this.recoveryCount = recoveryCount;
	}
	public String getPlanUuid() {
		return planUuid;
	}
	public void setPlanUuid(String planUuid) {
		this.planUuid = planUuid;
	}
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public boolean isHaveSessions() {
		return haveSessions;
	}
	public void setHaveSessions(boolean haveSessions) {
		this.haveSessions = haveSessions;
	}
	public int getParentRpsNodeId() {
		return parentRpsNodeId;
	}
	public void setParentRpsNodeId(int parentRpsNodeId) {
		this.parentRpsNodeId = parentRpsNodeId;
	}	
	public String getParentDataStoreUUID() {
		return parentDataStoreUUID;
	}
	public void setParentDataStoreUUID(String parentDataStoreUUID) {
		this.parentDataStoreUUID = parentDataStoreUUID;
	}	
	public String getParentDataStoreName() {
		return parentDataStoreName;
	}
	public void setParentDataStoreName(String parentDataStoreName) {
		this.parentDataStoreName = parentDataStoreName;
	}
	/*public boolean isCanReplication() {
		return canReplication;
	}
	public void setCanReplication(boolean canReplication) {
		this.canReplication = canReplication;
	}
	public String getReplicationRPSPolicy() {
		return replicationRPSPolicy;
	}
	public void setReplicationRPSPolicy(String replicationRPSPolicy) {
		this.replicationRPSPolicy = replicationRPSPolicy;
	}*/
	public boolean isLinux() {
		return linux;
	}
	public void setLinux(boolean linux) {
		this.linux = linux;
	}
}

package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;

public class StartInstantVHDOperation implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String proxyNameOrIP;
	public String getProxyNameOrIP() {
		return proxyNameOrIP;
	}
	public void setProxyNameOrIP(String proxyNameOrIP) {
		this.proxyNameOrIP = proxyNameOrIP;
	}
	
	private String nodeName;
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	private String nodeUUID;
	public String getNodeUUID() {
		return nodeUUID;
	}
	public void setNodeUUID(String nodeUUID) {
		this.nodeUUID = nodeUUID;
	}

	private String backupDestination;
	public String getBackupDestination() {
		return backupDestination;
	}
	public void setBackupDestination(String backupDestination) {
		this.backupDestination = backupDestination;
	}

	private int sessionNum;
	public int getSessionNum() {
		return sessionNum;
	}
	public void setSessionNum(int sessionNum) {
		this.sessionNum = sessionNum;
	}

	private String sessionPassword;
	public String getSessionPassword() {
		return sessionPassword;
	}
	public void setSessionPassword(String sessionPassword) {
		this.sessionPassword = sessionPassword;
	}

	private String userName;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	private String userPassword;
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	
	private int timeout;
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	private String vhdPath;
	public String getVhdPath() {
		return vhdPath;
	}
	public void setVhdPath(String vhdPath) {
		this.vhdPath = vhdPath;
	}
	
	private int diskType;
	public int getDiskType() {
		return diskType;
	}
	public void setDiskType(int diskType) {
		this.diskType = diskType;
	}
	
	// TODO Add more parameters
}

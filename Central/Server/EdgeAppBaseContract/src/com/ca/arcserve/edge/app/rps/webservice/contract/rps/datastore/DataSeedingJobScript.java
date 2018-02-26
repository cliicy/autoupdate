package com.ca.arcserve.edge.app.rps.webservice.contract.rps.datastore;

import java.io.Serializable;
import java.util.List;

import com.ca.arcflash.webservice.data.PM.ProxySettings;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;

public class DataSeedingJobScript implements Serializable {

	private static final long serialVersionUID = 8292345163322338725L;
	
	private int sourceRpsNodeId;
	private String sourceDataStoreUuid;
	private List<ProtectedNodeInDestination> seedingNodes;
	private int targetRpsNodeId;
	private String targetDataStoreUuid;
	private ProxySettings proxy;
	private boolean isFromShareFolder = false;
	private String srcShareFolderPath;
	private String srcShareFolderUserName;
	private String srcShareFolderPassword;
	private List<String> sessionPasswordPool;
	
	public boolean isFromShareFolder() {
		return isFromShareFolder;
	}
	public void setFromShareFolder(boolean isFromShareFolder) {
		this.isFromShareFolder = isFromShareFolder;
	}
	public String getSrcShareFolderPath() {
		return srcShareFolderPath;
	}
	public void setSrcShareFolderPath(String srcShareFolderPath) {
		this.srcShareFolderPath = srcShareFolderPath;
	}
	public String getSrcShareFolderUserName() {
		return srcShareFolderUserName;
	}
	public void setSrcShareFolderUserName(String srcShareFolderUserName) {
		this.srcShareFolderUserName = srcShareFolderUserName;
	}
	public String getSrcShareFolderPassword() {
		return srcShareFolderPassword;
	}
	public void setSrcShareFolderPassword(String srcShareFolderPassword) {
		this.srcShareFolderPassword = srcShareFolderPassword;
	}
	public List<String> getSessionPasswordPool() {
		return sessionPasswordPool;
	}
	public void setSessionPasswordPool(List<String> sessionPasswordPool) {
		this.sessionPasswordPool = sessionPasswordPool;
	}
	public int getSourceRpsNodeId() {
		return sourceRpsNodeId;
	}
	public void setSourceRpsNodeId(int sourceRpsNodeId) {
		this.sourceRpsNodeId = sourceRpsNodeId;
	}
	public String getSourceDataStoreUuid() {
		return sourceDataStoreUuid;
	}
	public void setSourceDataStoreUuid(String sourceDataStoreUuid) {
		this.sourceDataStoreUuid = sourceDataStoreUuid;
	}
	public List<ProtectedNodeInDestination> getSeedingNodes() {
		return seedingNodes;
	}
	public void setSeedingNodes(List<ProtectedNodeInDestination> seedingNodes) {
		this.seedingNodes = seedingNodes;
	}
	public int getTargetRpsNodeId() {
		return targetRpsNodeId;
	}
	public void setTargetRpsNodeId(int targetRpsNodeId) {
		this.targetRpsNodeId = targetRpsNodeId;
	}
	public String getTargetDataStoreUuid() {
		return targetDataStoreUuid;
	}
	public void setTargetDataStoreUuid(String targetDataStoreUuid) {
		this.targetDataStoreUuid = targetDataStoreUuid;
	}
	public ProxySettings getProxy() {
		return proxy;
	}
	public void setProxy(ProxySettings proxy) {
		this.proxy = proxy;
	}
	
}

package com.ca.arcserve.edge.app.base.appdaos;

public class EdgeInstantVM {
	private String uuid;
	private String name;
	private int recoveryServerId;
	private String recoveryServerName;
	private int rpsServerId;
	private String rpsServerName;
	private String dataStoreUuid;
	private String dataStoreName;
	private int sharedFolderId;
	private String shareFolderPath;
	private int gatewayId;
	private String xmlContent;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRecoveryServerId() {
		return recoveryServerId;
	}
	public void setRecoveryServerId(int recoveryServerId) {
		this.recoveryServerId = recoveryServerId;
	}
	public String getRecoveryServerName() {
		return recoveryServerName;
	}
	public void setRecoveryServerName(String recoveryServerName) {
		this.recoveryServerName = recoveryServerName;
	}
	public int getRpsServerId() {
		return rpsServerId;
	}
	public void setRpsServerId(int rpsServerId) {
		this.rpsServerId = rpsServerId;
	}
	public String getRpsServerName() {
		return rpsServerName;
	}
	public void setRpsServerName(String rpsServerName) {
		this.rpsServerName = rpsServerName;
	}
	public String getDataStoreUuid() {
		return dataStoreUuid;
	}
	public void setDataStoreUuid(String dataStoreUuid) {
		this.dataStoreUuid = dataStoreUuid;
	}
	public String getDataStoreName() {
		return dataStoreName;
	}
	public void setDataStoreName(String dataStoreName) {
		this.dataStoreName = dataStoreName;
	}
	public int getSharedFolderId() {
		return sharedFolderId;
	}
	public void setSharedFolderId(int sharedFolderId) {
		this.sharedFolderId = sharedFolderId;
	}
	public String getShareFolderPath() {
		return shareFolderPath;
	}
	public void setShareFolderPath(String shareFolderPath) {
		this.shareFolderPath = shareFolderPath;
	}
	public int getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(int gatewayId) {
		this.gatewayId = gatewayId;
	}
	public String getXmlContent() {
		return xmlContent;
	}
	public void setXmlContent(String xmlContent) {
		this.xmlContent = xmlContent;
	}
	
}

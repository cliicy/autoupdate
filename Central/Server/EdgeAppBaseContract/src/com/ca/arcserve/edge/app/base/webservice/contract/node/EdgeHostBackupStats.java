package com.ca.arcserve.edge.app.base.webservice.contract.node;

public class EdgeHostBackupStats {
	private int nodeId;
	private int serverId;
	private String serverName;
	private String nodeName;
	private long RawDataSizeByte;
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public long getRawDataSizeByte() {
		return RawDataSizeByte;
	}
	public void setRawDataSizeByte(long rawDataSizeByte) {
		RawDataSizeByte = rawDataSizeByte;
	}
	
}

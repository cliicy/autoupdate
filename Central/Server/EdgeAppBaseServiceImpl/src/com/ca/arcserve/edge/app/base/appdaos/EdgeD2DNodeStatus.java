package com.ca.arcserve.edge.app.base.appdaos;

public class EdgeD2DNodeStatus {
	private int 	rhostid;
	private String 	rhostname;
	private int		protocol;
	private int		port;
	private String  uuid;
	private int		status;
	
	public int getRhostid() {
		return rhostid;
	}
	public void setRhostid(int rhostid) {
		this.rhostid = rhostid;
	}
	public String getRhostname() {
		return rhostname;
	}
	public void setRhostname(String rhostname) {
		this.rhostname = rhostname;
	}
	public int getProtocol() {
		return protocol;
	}
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}

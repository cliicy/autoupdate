package com.ca.arcserve.edge.app.base.appdaos;

import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EdgeD2DHost {
	private int rhostid;
	private String rhostname;
	private String uuid;
	private int protocol;
	private int port;
	private int appStatus;
	private int type;
	private int managed;
	private int isVisible;
	
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
	@EncryptSave
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
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
	public int getAppStatus() {
		return appStatus;
	}
	public void setAppStatus(int appStatus) {
		this.appStatus = appStatus;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setManaged(int managed) {
		this.managed = managed;
	}
	public int getManaged() {
		return managed;
	}
	public int getIsVisible() {
		return isVisible;
	}
	public void setIsVisible(int isVisible) {
		this.isVisible = isVisible;
	}
}

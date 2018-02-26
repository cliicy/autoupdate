package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class ServerInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 790680340890017830L;
	private String uuid;
	private String serverName;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
}

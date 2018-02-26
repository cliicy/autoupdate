package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

public class ITManagementInfo implements Serializable{
	
	private static final long serialVersionUID = -7071515621410L;	
	
	private String serverName;
	private String status;
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	


}

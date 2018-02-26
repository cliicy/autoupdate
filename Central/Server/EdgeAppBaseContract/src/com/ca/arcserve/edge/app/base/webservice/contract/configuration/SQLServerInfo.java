package com.ca.arcserve.edge.app.base.webservice.contract.configuration;

import java.io.Serializable;
import java.util.List;

public class SQLServerInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8295834073115229894L;

	private String serverName;
	private List<String> instances;
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public List<String> getInstances() {
		return instances;
	}
	public void setInstances(List<String> instances) {
		this.instances = instances;
	}
	
}

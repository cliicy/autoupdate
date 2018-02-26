package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class DiscoveredVM implements Serializable {

	private static final long serialVersionUID = 633066184242331497L;
	
	private int id;
	private String hostname;
	private String vmName;
	private String hyperVisor;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getVmName() {
		return vmName;
	}
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	public String getHyperVisor() {
		return hyperVisor;
	}
	public void setHyperVisor(String hyperVisor) {
		this.hyperVisor = hyperVisor;
	}

}

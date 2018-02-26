package com.ca.arcserve.edge.app.base.appdaos;

public class EdgeEsxHost {
	
	private int rhostid;
	private String rhostname;
	private String vmName;
	private String hostname;	// ESX server host name
	private String esxHost;
	private int servertype;
	
	public int getRhostid() {
		return rhostid;
	}
	public void setRhostid(int rhostid) {
		this.rhostid = rhostid;
	}
	public String getEsxHost() {
		return esxHost;
	}
	public void setEsxHost(String esxHost) {
		this.esxHost = esxHost;
	}
	public int getServertype() {
		return servertype;
	}
	public void setServertype(int servertype) {
		this.servertype = servertype;
	}
	public String getRhostname() {
		return rhostname;
	}
	public void setRhostname(String rhostname) {
		this.rhostname = rhostname;
	}
	public String getVmName() {
		return vmName;
	}
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

}

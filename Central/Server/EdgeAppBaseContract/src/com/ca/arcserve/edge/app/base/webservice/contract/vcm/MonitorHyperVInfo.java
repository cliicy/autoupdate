package com.ca.arcserve.edge.app.base.webservice.contract.vcm;

import java.io.Serializable;

import com.ca.arcflash.ha.model.ESXServerInfo;

public class MonitorHyperVInfo implements Serializable {

	private static final long serialVersionUID = 1849027742996348516L;
	
	private boolean installed;
	private boolean validOS;
	private String[] networks;
	private String[] networkAdapterTypes;
	private ESXServerInfo serverInfo;
	
	public boolean isInstalled() {
		return installed;
	}
	public void setInstalled(boolean installed) {
		this.installed = installed;
	}
	public boolean isValidOS() {
		return validOS;
	}
	public void setValidOS(boolean validOS) {
		this.validOS = validOS;
	}
	public String[] getNetworks() {
		return networks;
	}
	public void setNetworks(String[] networks) {
		this.networks = networks;
	}
	public String[] getNetworkAdapterTypes() {
		return networkAdapterTypes;
	}
	public void setNetworkAdapterTypes(String[] networkAdapterTypes) {
		this.networkAdapterTypes = networkAdapterTypes;
	}
	public ESXServerInfo getServerInfo() {
		return serverInfo;
	}
	public void setServerInfo(ESXServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

}

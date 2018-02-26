package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.jobscript.failover.Virtualization;

public class NetworkInfo {
	
	private boolean isConfiguredSameNetwork;
	private String networkType = "";
	private String networkName = "";
	private Virtualization virtualizationInfo;
	private List<NetworkDiffInfo> networkDiffList = new ArrayList<NetworkDiffInfo>();
	
	
	public Virtualization getVirtualizationInfo() {
		return virtualizationInfo;
	}
	public void setVirtualizationInfo(Virtualization virtualizationInfo) {
		this.virtualizationInfo = virtualizationInfo;
	}
	public List<NetworkDiffInfo> getNetworkDiffList() {
		return networkDiffList;
	}
	public void setNetworkDiffList(List<NetworkDiffInfo> networkDiffList) {
		this.networkDiffList = networkDiffList;
	}
	public String getNetworkType() {
		return networkType;
	}
	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public boolean isConfiguredSameNetwork() {
		return isConfiguredSameNetwork;
	}
	public void setConfiguredSameNetwork(boolean isConfiguredSameNetwork) {
		this.isConfiguredSameNetwork = isConfiguredSameNetwork;
	}
}

package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class AdapterForInstantVM implements Serializable {

	private static final long serialVersionUID = 1L;
	private String adapterName;
	private String adapterDesc;
	private String displayName;
	private String adapterType;
	private String networkType;
	private String networkID;
	private boolean isVDS; 
	private String macAddress;
	private boolean dhcp;
	private boolean isAutodns;
	private boolean isAutowins;
	private List<IPForInstantVM> ipInfos = new ArrayList<IPForInstantVM>();
	private List<String> gateWayList = new ArrayList<String>();
	private List<String> dnsList = new ArrayList<String>();
	private List<String> winsList = new ArrayList<String>();
	
	private boolean defaultConnect = false;
	
	public boolean isDefaultConnect() {
		return defaultConnect;
	}
	public void setDefaultConnect(boolean defaultConnect) {
		this.defaultConnect = defaultConnect;
	}
	public String getAdapterType() {
		return adapterType;
	}
	public void setAdapterType(String adapterType) {
		this.adapterType = adapterType;
	}

	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public boolean isDhcp() {
		return dhcp;
	}
	public void setDhcp(boolean dhcp) {
		this.dhcp = dhcp;
	}
	public List<IPForInstantVM> getIpInfos() {
		return ipInfos;
	}
	public void setIpInfos(List<IPForInstantVM> ipInfos) {
		this.ipInfos = ipInfos;
	}
	public String getNetworkType() {
		return networkType;
	}
	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}
	public String getNetworkID() {
		return networkID;
	}
	public void setNetworkID(String networkID) {
		this.networkID = networkID;
	}
	public boolean isVDS() {
		return isVDS;
	}
	public void setVDS(boolean isVDS) {
		this.isVDS = isVDS;
	}
	public String getAdapterName() {
		return adapterName;
	}
	public void setAdapterName(String adapterName) {
		this.adapterName = adapterName;
	}
	public String getAdapterDesc() {
		return adapterDesc;
	}
	public void setAdapterDesc(String adapterDesc) {
		this.adapterDesc = adapterDesc;
	}
	public List<String> getGateWayList() {
		return gateWayList;
	}
	public void setGateWayList(List<String> gateWayList) {
		this.gateWayList = gateWayList;
	}
	public List<String> getDnsList() {
		return dnsList;
	}
	public void setDnsList(List<String> dnsList) {
		this.dnsList = dnsList;
	}
	public List<String> getWinsList() {
		return winsList;
	}
	public void setWinsList(List<String> winsList) {
		this.winsList = winsList;
	}
	public boolean isAutodns() {
		return isAutodns;
	}
	public void setAutodns(boolean isAutodns) {
		this.isAutodns = isAutodns;
	}
	public boolean isAutowins() {
		return isAutowins;
	}
	public void setAutowins(boolean isAutowins) {
		this.isAutowins = isAutowins;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
}

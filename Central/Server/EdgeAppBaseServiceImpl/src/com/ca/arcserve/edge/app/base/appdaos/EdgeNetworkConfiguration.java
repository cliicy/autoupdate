package com.ca.arcserve.edge.app.base.appdaos;

public class EdgeNetworkConfiguration {
	
	private int id;
	private int hostId;
	private String adapterDesc;
	private String macAddress;
	private String virtualNetworkName;
	private int isVirtualNameFromPolicy;
	private String nicTypeName;
	private int isNICTypeFromPolicy;
	private int isDHCP;
	private int isKeepWithBackup;
	private String ipStr;
	private String gatewayStr;
	private String dnsStr;
	private String winsStr;
	
	
	public int getIsKeepWithBackup() {
		return isKeepWithBackup;
	}
	public void setIsKeepWithBackup(int isKeepWithBackup) {
		this.isKeepWithBackup = isKeepWithBackup;
	}
	public String getAdapterDesc() {
		return adapterDesc;
	}
	public void setAdapterDesc(String adapterDesc) {
		this.adapterDesc = adapterDesc;
	}
	public String getIpStr() {
		return ipStr;
	}
	public void setIpStr(String ipStr) {
		this.ipStr = ipStr;
	}
	public String getGatewayStr() {
		return gatewayStr;
	}
	public void setGatewayStr(String gatewayStr) {
		this.gatewayStr = gatewayStr;
	}
	public String getDnsStr() {
		return dnsStr;
	}
	public void setDnsStr(String dnsStr) {
		this.dnsStr = dnsStr;
	}
	public String getWinsStr() {
		return winsStr;
	}
	public void setWinsStr(String winsStr) {
		this.winsStr = winsStr;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getHostId() {
		return hostId;
	}
	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getVirtualNetworkName() {
		return virtualNetworkName;
	}
	public void setVirtualNetworkName(String virtualNetworkName) {
		this.virtualNetworkName = virtualNetworkName;
	}
	public int getIsVirtualNameFromPolicy() {
		return isVirtualNameFromPolicy;
	}
	public void setIsVirtualNameFromPolicy(int isVirtualNameFromPolicy) {
		this.isVirtualNameFromPolicy = isVirtualNameFromPolicy;
	}
	public String getNicTypeName() {
		return nicTypeName;
	}
	public void setNicTypeName(String nicTypeName) {
		this.nicTypeName = nicTypeName;
	}
	public int getIsNICTypeFromPolicy() {
		return isNICTypeFromPolicy;
	}
	public void setIsNICTypeFromPolicy(int isNICTypeFromPolicy) {
		this.isNICTypeFromPolicy = isNICTypeFromPolicy;
	}
	public int getIsDHCP() {
		return isDHCP;
	}
	public void setIsDHCP(int isDHCP) {
		this.isDHCP = isDHCP;
	}
}

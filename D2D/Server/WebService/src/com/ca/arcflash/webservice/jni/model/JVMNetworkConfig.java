package com.ca.arcflash.webservice.jni.model;

public class JVMNetworkConfig {
	private int adapterType;
	private String switchUUID;
	private String adapterFriendlyName;
	private boolean staticMAC;
	private String macAddress;
	private int id;
	
	public int getAdapterType() {
		return adapterType;
	}
	public void setAdapterType(int adapterType) {
		this.adapterType = adapterType;
	}
	public String getSwitchUUID() {
		return switchUUID;
	}
	public void setSwitchUUID(String switchUUID) {
		this.switchUUID = switchUUID;
	}
	public String getAdapterFriendlyName() {
		return adapterFriendlyName;
	}
	public void setAdapterFriendlyName(String adapterFriendlyName) {
		this.adapterFriendlyName = adapterFriendlyName;
	}
	public boolean isStaticMAC() {
		return staticMAC;
	}
	public void setStaticMAC(boolean staticMAC) {
		this.staticMAC = staticMAC;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}

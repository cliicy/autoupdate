package com.ca.arcflash.webservice.jni.model;

import java.util.Vector;

public class JHostNetworkConfig {
	private String					networkAdapterName;
	private boolean					isDHCPEnabled;
	private Vector<String> 			vecIP = new Vector<String>();
	private Vector<String> 			vecMask = new Vector<String>();
	private Vector<String>			vecGateway = new Vector<String>();
	private boolean					isAutoDnsEnabled;
	private Vector<String>			vecDnsServer = new Vector<String>();
	private String					macAddress;
	
	public String getNetworkAdapterName() {
		return networkAdapterName;
	}
	public void setNetworkAdapterName(String networkAdapterName) {
		this.networkAdapterName = networkAdapterName;
	}
	public boolean isDHCPEnabled() {
		return isDHCPEnabled;
	}
	public void setIsDHCPEnabled(boolean isDHCPEnabled) {
		this.isDHCPEnabled = isDHCPEnabled;
	}
	public Vector<String> getGateway() {
		return vecGateway;
	}
	public void addGateway(String gateway) {
		vecGateway.add(gateway);
	}
	public boolean isAutoDnsEnabled() {
		return isAutoDnsEnabled;
	}
	public void setIsAutoDnsEnabled(boolean isAutoDnsEnabled) {
		this.isAutoDnsEnabled = isAutoDnsEnabled;
	}
	public Vector<String> getVecIP() {
		return vecIP;
	}
	
	public void addIP(String ip){
		vecIP.add(ip);
	}
	
	public Vector<String> getVecMask() {
		return vecMask;
	}
	
	public void addMask(String mask){
		vecMask.add(mask);
	}
	
	public Vector<String> getVecDnsServer() {
		return vecDnsServer;
	}
	
	public void addDnsServer(String dnsServer){
		vecDnsServer.add(dnsServer);
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getMacAddress() {
		return macAddress;
	}
}

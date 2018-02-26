package com.ca.arcflash.webservice.jni.model;

import java.util.ArrayList;
import java.util.List;

public class IPSettingDetail {
	
	boolean dhcp;
	String virtualNetwork;
	String nicType;
	String winsPrimary;
	String winsSecond;
	List<String> dns = new ArrayList<String>();
	List<String> gateways = new ArrayList<String>();
	List<String> ips = new ArrayList<String>();
	List<String> subnets = new ArrayList<String>();
	
	public boolean isDhcp() {
		return dhcp;
	}
	public void setDhcp(boolean dhcp) {
		this.dhcp = dhcp;
	}
	public String getVirtualNetwork() {
		return virtualNetwork;
	}
	public void setVirtualNetwork(String virtualNetwork) {
		this.virtualNetwork = virtualNetwork;
	}
	public String getNicType() {
		return nicType;
	}
	public void setNicType(String nicType) {
		this.nicType = nicType;
	}
	public String getWinsPrimary() {
		return winsPrimary;
	}
	public void setWinsPrimary(String winsPrimary) {
		this.winsPrimary = winsPrimary;
	}
	public String getWinsSecond() {
		return winsSecond;
	}
	public void setWinsSecond(String winsSecond) {
		this.winsSecond = winsSecond;
	}
	public List<String> getDns() {
		return dns;
	}
	public void setDns(List<String> dns) {
		this.dns = dns;
	}
	public List<String> getGateways() {
		return gateways;
	}
	public void setGateways(List<String> gateways) {
		this.gateways = gateways;
	}
	public List<String> getIps() {
		return ips;
	}
	public void setIps(List<String> ips) {
		this.ips = ips;
	}
	public List<String> getSubnets() {
		return subnets;
	}
	public void setSubnets(List<String> subnets) {
		this.subnets = subnets;
	}
	
}

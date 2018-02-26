package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IPSettingForADR implements Serializable, Comparable<IPSettingForADR> {

	private static final long serialVersionUID = -1916160585632588334L;
	String version;
	boolean dhcp;
	List<DNSForADR> dnses = new ArrayList<DNSForADR>();
	List<GatewayForADR> gateways = new ArrayList<GatewayForADR>();
	List<WinsForADR> wins = new ArrayList<WinsForADR>();
	List<IPAddressInfoForADR> ips = new ArrayList<IPAddressInfoForADR>();

	public IPSettingForADR() {
		
	}
	
	
	public List<IPAddressInfoForADR> getIps() {
		return ips;
	}


	public void setIps(List<IPAddressInfoForADR> ips) {
		this.ips = ips;
	}


	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isDhcp() {
		return dhcp;
	}

	public void setDhcp(boolean dhcp) {
		this.dhcp = dhcp;
	}

	@Override
	public int compareTo(IPSettingForADR o) {

		return this.getVersion().compareTo(o.getVersion());
	}

	public IPSettingForADR(String version, boolean dhcp) {
		super();
		this.version = version;
		this.dhcp = dhcp;
	}
	
	public List<DNSForADR> getDnses() {
		return dnses;
	}


	public void setDnses(List<DNSForADR> dnses) {
		this.dnses = dnses;
	}


	public List<WinsForADR> getWins() {
		return wins;
	}


	public void setWins(List<WinsForADR> wins) {
		this.wins = wins;
	}


	public List<GatewayForADR> getGateways() {
		return gateways;
	}

	public void setGateways(List<GatewayForADR> gateways) {
		this.gateways = gateways;
	}

	public String getIPListToString() {
		StringBuilder sb = new StringBuilder();
		int ipSize = ips.size();
		for (int i=0;i<ipSize;i++) {
			IPAddressInfoForADR ip = ips.get(i);
			sb.append(ip.getIp());
			if (i<ipSize-1) 
				sb.append(";");
		}
		return sb.toString();
	}
}

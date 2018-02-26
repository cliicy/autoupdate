package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class StandbyVMNetworkInfo  implements Serializable{
	
	private static final long serialVersionUID = -1246608707824267228L;
	private List<SourceMachineNetworkAdapterInfo> sourceMachineNetworkAdapterInfo = new ArrayList<SourceMachineNetworkAdapterInfo>();
	private int ttl;
	private int dnsServerType;
	private String dnsUsername;
	private String dnsPassword;
	private String keyFile;

	public String getKeyFile() {
		return keyFile;
	}
	public void setKeyFile(String keyFile) {
		this.keyFile = keyFile;
	}
	public List<SourceMachineNetworkAdapterInfo> getSourceMachineNetworkAdapterInfo() {
		return sourceMachineNetworkAdapterInfo;
	}
	public void setSourceMachineNetworkAdapterInfo(
			List<SourceMachineNetworkAdapterInfo> sourceMachineNetworkAdapterInfo) {
		this.sourceMachineNetworkAdapterInfo = sourceMachineNetworkAdapterInfo;
	}
	public int getTtl() {
		return ttl;
	}
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	public int getDnsServerType() {
		return dnsServerType;
	}
	public void setDnsServerType(int dnsServerType) {
		this.dnsServerType = dnsServerType;
	}
	public String getDnsUsername() {
		return dnsUsername;
	}
	public void setDnsUsername(String dnsUsername) {
		this.dnsUsername = dnsUsername;
	}

	@EncryptSave
	public String getDnsPassword() {
		return dnsPassword;
	}

	public void setDnsPassword(String dnsPassword) {
		this.dnsPassword = dnsPassword;
	}
	
	public StandbyVMNetworkInfo clone() {
		StandbyVMNetworkInfo temp = new StandbyVMNetworkInfo();
		temp.setDnsPassword(this.dnsPassword);
		temp.setDnsServerType(this.dnsServerType);
		temp.setDnsUsername(this.dnsUsername);
		temp.setTtl(this.ttl);
		List<SourceMachineNetworkAdapterInfo> tempSourceMachineNetworkAdapterInfo = new ArrayList<SourceMachineNetworkAdapterInfo>();
		for (SourceMachineNetworkAdapterInfo sourceInfo : this.sourceMachineNetworkAdapterInfo) {
			SourceMachineNetworkAdapterInfo tempSourceInfo = new SourceMachineNetworkAdapterInfo();
			tempSourceInfo.setAdapterDescription(sourceInfo.getAdapterDescription());
			tempSourceInfo.setDefaultNetworkType(sourceInfo.getDefaultNetworkType());
			tempSourceInfo.setNICTypeFromPolicy(sourceInfo.isNICTypeFromPolicy());
			tempSourceInfo.setVirtualNameFromPolicy(sourceInfo.isVirtualNameFromPolicy());
			tempSourceInfo.setDefaultVirtualNetwork(sourceInfo.getDefaultVirtualNetwork());
			tempSourceInfo.setKeepWithBackup(sourceInfo.isKeepWithBackup());
			tempSourceInfo.setMacAddress(sourceInfo.getMacAddress());
			List<IPSettingForADR> tempSettings = new ArrayList<IPSettingForADR>();
			for (IPSettingForADR ipSettings : sourceInfo.getSavedIpSettings()) {
				IPSettingForADR tempIPSettings = new IPSettingForADR();
				tempIPSettings.setDhcp(ipSettings.isDhcp());
				List<IPAddressInfoForADR> ips = new ArrayList<IPAddressInfoForADR>();
				for (IPAddressInfoForADR ip : ipSettings.getIps()) {
					IPAddressInfoForADR tempIP = new IPAddressInfoForADR();
					tempIP.setIp(ip.getIp());
					tempIP.setSubnet(ip.getSubnet());
					tempIP.setKey(ip.getKey());
					ips.add(tempIP);
				}
				tempIPSettings.setIps(ips);
				List<GatewayForADR> gateways = new ArrayList<GatewayForADR>();
				for (GatewayForADR gateway : ipSettings.getGateways()) {
					GatewayForADR tempGateway = new GatewayForADR();
					tempGateway.setGatewayAddress(gateway.getGatewayAddress());
					tempGateway.setGatewayMetric(gateway.getGatewayMetric());
					tempGateway.setKey(gateway.getKey());
					gateways.add(tempGateway);
				}
				tempIPSettings.setGateways(gateways);
				List<DNSForADR> dnses = new ArrayList<DNSForADR>();
				for (DNSForADR dns : ipSettings.getDnses()) {
					DNSForADR tempDNS = new DNSForADR();
					tempDNS.setDns(dns.getDns());
					tempDNS.setKey(dns.getKey());
					dnses.add(tempDNS);
				}
				tempIPSettings.setDnses(dnses);
				List<WinsForADR> wins = new ArrayList<WinsForADR>();
				for (WinsForADR win : ipSettings.getWins()) {
					WinsForADR tempWin = new WinsForADR();
					tempWin.setWins(win.getWins());
					tempWin.setKey(win.getKey());
					wins.add(tempWin);
				}
				tempIPSettings.setWins(wins);
				tempSettings.add(tempIPSettings);
			}
			tempSourceInfo.setSavedIpSettings(tempSettings);
			tempSourceMachineNetworkAdapterInfo.add(tempSourceInfo);
		}
		temp.setSourceMachineNetworkAdapterInfo(tempSourceMachineNetworkAdapterInfo);
		return temp;
	}
	
}

package com.ca.arcserve.edge.app.base.appdaos;

import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EdgeStandbyVMNetworkInfo {
	private int id;
	private int hostId;
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

	
}

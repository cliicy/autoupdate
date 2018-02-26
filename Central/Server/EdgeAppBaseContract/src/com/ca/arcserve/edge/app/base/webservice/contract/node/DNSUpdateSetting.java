package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class DNSUpdateSetting  implements Serializable, BeanModelTag {

	private static final long serialVersionUID = -8366228056882116941L;
	
//	private String key;
	private String dnsAddress;
	private String sourceIPAddresses;
	private String ipAddresses;
	
	
//	public String getKey() {
//		return key;
//	}
//	public void setKey(String key) {
//		this.key = key;
//	}
	public String getSourceIPAddresses() {
		return sourceIPAddresses;
	}
	public void setSourceIPAddresses(String sourceIPAddresses) {
		this.sourceIPAddresses = sourceIPAddresses;
	}
	public String getDnsAddress() {
		return dnsAddress;
	}
	public void setDnsAddress(String dnsAddress) {
		this.dnsAddress = dnsAddress;
	}
	public String getIpAddresses() {
		return ipAddresses;
	}
	public void setIpAddresses(String ipAddresses) {
		this.ipAddresses = ipAddresses;
	}
	
}

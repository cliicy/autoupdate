package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class DNSForADR implements Serializable, BeanModelTag {

	private static final long serialVersionUID = -2425365047303848832L;

	private String key;
	private String dns = "";

	public DNSForADR() {
		key = "_" + System.currentTimeMillis();
	}

	public String getDns() {
		return dns;
	}

	public void setDns(String dns) {
		this.dns = dns;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DNSForADR that = (DNSForADR) obj;
		return (key == that.key || (key != null && key.equals(that.key)))
				&& (dns == that.dns || (dns != null && dns.equals(that.dns)));
	}
}

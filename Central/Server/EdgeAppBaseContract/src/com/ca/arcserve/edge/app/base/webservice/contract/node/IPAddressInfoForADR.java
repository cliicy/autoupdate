package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class IPAddressInfoForADR implements Serializable, BeanModelTag {

	private static final long serialVersionUID = -502490791785011435L;
	private String key;
	private String ip = "";
	private String subnet = "";

	public IPAddressInfoForADR() {
		key = "_" + System.currentTimeMillis();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getSubnet() {
		return subnet;
	}

	public void setSubnet(String subnet) {
		this.subnet = subnet;
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
		IPAddressInfoForADR that = (IPAddressInfoForADR) obj;
		return (key == that.key || (key != null && key.equals(that.key)))
				&& (ip == that.ip || (ip != null && ip.equals(that.ip)))
				&& (subnet == that.subnet || (subnet != null && subnet.equals(that.subnet)));
	}

}

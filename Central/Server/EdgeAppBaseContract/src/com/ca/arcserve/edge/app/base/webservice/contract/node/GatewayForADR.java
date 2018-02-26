package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class GatewayForADR implements Serializable, BeanModelTag {

	private static final long serialVersionUID = -7329154102373043071L;
	private String key;
	String gatewayAddress = "";
	String gatewayMetric = "";

	public GatewayForADR() {
		key = "_" + System.currentTimeMillis();
	}

	public String getGatewayAddress() {
		return gatewayAddress;
	}

	public void setGatewayAddress(String gatewayAddress) {
		this.gatewayAddress = gatewayAddress;
	}

	public String getGatewayMetric() {
		return gatewayMetric;
	}

	public void setGatewayMetric(String gatewayMetric) {
		this.gatewayMetric = gatewayMetric;
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
		GatewayForADR that = (GatewayForADR) obj;
		return (key == that.key || (key != null && key.equals(that.key)))
				&& (gatewayAddress == that.gatewayAddress || (gatewayAddress != null && gatewayAddress
						.equals(that.gatewayAddress)))
				&& (gatewayMetric == that.gatewayMetric || (gatewayMetric != null && gatewayMetric
						.equals(that.gatewayMetric)));
	}
}

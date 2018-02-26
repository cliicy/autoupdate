package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;

public class InstantVMFilter implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private int gatewayId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(int gatewayId) {
		this.gatewayId = gatewayId;
	}

}

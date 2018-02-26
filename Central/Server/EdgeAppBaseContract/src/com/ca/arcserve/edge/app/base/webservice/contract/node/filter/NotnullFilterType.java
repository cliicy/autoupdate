package com.ca.arcserve.edge.app.base.webservice.contract.node.filter;

import com.ca.arcserve.edge.app.base.webservice.contract.common.IBit;

public enum NotnullFilterType implements IBit {
	
	Hostname(0x01),
	Username(0x02);
	
	private int value;
	
	private NotnullFilterType(int value) {
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}
	
}

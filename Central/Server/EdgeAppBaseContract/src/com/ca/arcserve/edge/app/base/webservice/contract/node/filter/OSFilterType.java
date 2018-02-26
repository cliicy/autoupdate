package com.ca.arcserve.edge.app.base.webservice.contract.node.filter;

import com.ca.arcserve.edge.app.base.webservice.contract.common.IBit;

public enum OSFilterType implements IBit {
	
	Windows(0x01),
	Linux(0x02),
	Others(0x04);
	
	private int value;
	
	private OSFilterType(int value) {
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}
	
}

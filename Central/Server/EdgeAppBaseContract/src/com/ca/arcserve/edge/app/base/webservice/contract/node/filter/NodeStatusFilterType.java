package com.ca.arcserve.edge.app.base.webservice.contract.node.filter;

import com.ca.arcserve.edge.app.base.webservice.contract.common.IBit;

public enum NodeStatusFilterType implements IBit {
	
	Unprotected(0x01),
	Protected(0x02);
	
	private int value;
	
	private NodeStatusFilterType(int value) {
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}

}

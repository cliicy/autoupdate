package com.ca.arcserve.edge.app.base.webservice.contract.node.filter;

import com.ca.arcserve.edge.app.base.webservice.contract.common.IBit;

public enum LastBackupFilterType implements IBit {

	Successful(0x01),
	Failed(0x02),
	Cancel(0x04),
	Missed(0x08),
	Others(0x10);
	
	private int value;
	
	private LastBackupFilterType(int value) {
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}
}

package com.ca.arcserve.edge.app.base.webservice.contract.node.filter;

import com.ca.arcserve.edge.app.base.webservice.contract.common.IBit;

public enum DeployStatusFilterType implements IBit {
	NotInstall(0x01), OldVersion(0x02), Normal(0x04), DeployFail(0x08), DeploySuccess(0x10) ;
	
	private int value;
	
	private DeployStatusFilterType(int value) {
		this.value = value;
	} 

	@Override
	public int getValue() {
		return value;
	}
}

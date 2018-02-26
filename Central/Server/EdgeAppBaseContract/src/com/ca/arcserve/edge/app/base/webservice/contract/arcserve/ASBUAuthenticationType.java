package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

public enum ASBUAuthenticationType {
	ARCSERVE_BACKUP(0x00), WINDOWS(0x10);

	private int value;

	ASBUAuthenticationType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}

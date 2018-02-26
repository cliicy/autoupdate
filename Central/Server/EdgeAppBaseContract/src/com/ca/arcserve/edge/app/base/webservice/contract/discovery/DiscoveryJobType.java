package com.ca.arcserve.edge.app.base.webservice.contract.discovery;

public enum DiscoveryJobType {
	UNKNOW(-1),MANUAL(0), SCHEDULE(1);
	
	private int value;
	
	private DiscoveryJobType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static DiscoveryJobType parse(int value) {
		switch (value) {
		case 0:
			return MANUAL;
		case 1:
			return SCHEDULE;
		default:
			return UNKNOW;
		}
	}
}

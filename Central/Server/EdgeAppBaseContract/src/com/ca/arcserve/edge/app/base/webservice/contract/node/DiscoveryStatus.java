package com.ca.arcserve.edge.app.base.webservice.contract.node;

public enum DiscoveryStatus {
	DISCOVERY_STATUS_IDLE(0),
	DISCOVERY_STATUS_ACTIVE(1),
	DISCOVERY_STATUS_FINISHED(2),
	DISCOVERY_STATUS_CANCELLED(3),
	DISCOVERY_STATUS_FAILED(4),
	DISCOVERY_STATUS_INCOMPLETE(5),
	DISCOVERY_STATUS_PENDING(6);
	
	private int value;
	
	private DiscoveryStatus(int value) {
		this.value = value;
	}
	
	public int getDiscoveryStatus() {
		return value;
	}
	
	public static DiscoveryStatus parse(int value) {
		switch (value) {
		case 0:
			return DISCOVERY_STATUS_IDLE;
		case 1:
			return DISCOVERY_STATUS_ACTIVE;
		case 2:
			return DISCOVERY_STATUS_FINISHED;
		case 3:
			return DISCOVERY_STATUS_CANCELLED;
		case 4:
			return DISCOVERY_STATUS_FAILED;
		case 5:
			return DISCOVERY_STATUS_INCOMPLETE;
		case 6:
			return DISCOVERY_STATUS_PENDING;
		default:
			return DISCOVERY_STATUS_IDLE;
		}
	}
}

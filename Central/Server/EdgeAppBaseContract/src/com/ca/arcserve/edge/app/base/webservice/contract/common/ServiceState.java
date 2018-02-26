package com.ca.arcserve.edge.app.base.webservice.contract.common;

public enum ServiceState {
	UnKnown(0),
	NotExist(1),
	Running(2),
	NotRunning(3);
	
	private final int value;
	
	ServiceState(int value){
		this.value = value;
	}
	
	public int value() {
		return value;
	}

	public static ServiceState valueOf(int value) {
		for (ServiceState status : ServiceState.values()) {
			if (status.value() == value) {
				return status;
			}
		}
		return null;
	}
}

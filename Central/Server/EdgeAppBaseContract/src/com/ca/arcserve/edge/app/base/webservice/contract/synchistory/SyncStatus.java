package com.ca.arcserve.edge.app.base.webservice.contract.synchistory;

import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;

public enum SyncStatus {
	/**
	 * 0
	 */
	FINISHED(0),
	/**
	 * 1
	 */
	FAILED(1),
	/**
	 * 2
	 */
	RUNNING(2);
	
	private int value;
	
	private SyncStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public static SyncStatus parse(int status) {
		switch (status) {
		case 0:
			return FINISHED;
		case 1:
			return FAILED;
		case 2:
			return RUNNING;
		default:
			return null;
		}
	}
}

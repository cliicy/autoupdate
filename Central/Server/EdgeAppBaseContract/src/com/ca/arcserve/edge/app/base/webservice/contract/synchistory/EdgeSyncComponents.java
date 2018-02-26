package com.ca.arcserve.edge.app.base.webservice.contract.synchistory;

public enum EdgeSyncComponents {
	ARCserve_Backup(1),
	ARCserve_D2D(2);
	
	private int value;
	
	private EdgeSyncComponents(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}

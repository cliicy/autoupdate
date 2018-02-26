package com.ca.arcserve.edge.app.base.webservice.contract.dashboard;

public enum DashboardFilterType {
	All(-1),
	JobsInProgress(0),
	JobsCompleted(1),
	JobsFailed(2),
	JobsScheduled(3),
	JobsCanceled(4);
	
	private int value;
	
	private DashboardFilterType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}

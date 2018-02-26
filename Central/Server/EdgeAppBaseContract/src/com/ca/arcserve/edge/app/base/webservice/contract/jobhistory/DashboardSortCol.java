package com.ca.arcserve.edge.app.base.webservice.contract.jobhistory;


public enum DashboardSortCol {
	
	jobStatus("jobStatus"), jobType("jobType"), nodeName("agentName"), jobUTCStartTime("jobUTCStartTime"), planName("name");
	
	private final String value;
	
	DashboardSortCol(String value){
		this.value = value;
	}
	
	public String value() {
		return value;
	}

	public static DashboardSortCol fromValue(String v) {
		for (DashboardSortCol c : DashboardSortCol.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}

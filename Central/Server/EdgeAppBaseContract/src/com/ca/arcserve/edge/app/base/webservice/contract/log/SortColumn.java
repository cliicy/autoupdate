package com.ca.arcserve.edge.app.base.webservice.contract.log;

public enum SortColumn {
	
	None(-1),
	JobId(1),
	ModuleId(2),
	Severity(3),
	NodeName(4),
	Time(5),
	TargetNodeName(6),
	TargetVMName(7),
	JobType(8),
	ServerNodeName(9),
	// BUG 755011 ADD
	SiteName(10);

	private int value;
	
	private SortColumn(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
}

package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

public enum PlanEnableStatus {
	Enable(0),	
	Disable(1);
	
	private int value;
	
	private PlanEnableStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}

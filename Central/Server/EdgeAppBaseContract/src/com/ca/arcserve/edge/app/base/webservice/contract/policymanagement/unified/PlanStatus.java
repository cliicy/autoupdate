package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

public enum PlanStatus {
	
	CreateSuccess(0, false),
	
	Deploying(1, true),
	DeploySuccess(2, false),
	DeployFailed(3, false),
	
	Deleting(4, true),
	DeleteFailed(5, false),
	
	Modifying(6, true),
	ModifySucess(7, false),
	ModifyFailed(8, false),
	
	Pending(9, true),
	
	DeployingRpsPolicy(10, true),
	DeployRpsPolicyFailed(11, false),
	
	DeployingAsbuPolicy(12, true),
	DeployAsbuPolicyFailed(13, false);
	
	private int value;
	private boolean inProgress;
	
	private PlanStatus(int value, boolean inProgress) {
		this.value = value;
		this.inProgress = inProgress;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public boolean isInProgress() {
		return inProgress;
	}
	
}

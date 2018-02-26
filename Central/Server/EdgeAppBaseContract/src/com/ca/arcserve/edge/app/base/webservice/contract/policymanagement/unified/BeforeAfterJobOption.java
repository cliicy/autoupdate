package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

public enum BeforeAfterJobOption {
	RUN_JOB(1), 
	SKIP_JOB_ON_PRECMD_EXIT(2), 
	SKIP_POSTCMD_ON_PRECMD_EXIT(3), 
	DISABLE_POSTCMD_ON_JOB_FAIL(4), 
	DISABLE_POSTCMD_ON_JOB_INCOMPLETE(5), 
	DISABLE_POSTCMD_ON_JOB_COMPLETE(6);

	private int value;

	private BeforeAfterJobOption(int val) {
		this.value = val;
	}

	public int getValue() {
		return this.value;
	}
}

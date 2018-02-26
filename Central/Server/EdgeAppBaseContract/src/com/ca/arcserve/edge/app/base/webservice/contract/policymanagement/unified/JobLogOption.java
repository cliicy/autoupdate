package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

public enum JobLogOption {
	LOG_ALL_ACTIVITY(1), LOG_SUMMARY_ONLY(2), LOG_DISABLED(3);

	private int value;

	private JobLogOption(int val) {
		this.value = val;
	}

	public int getValue() {
		return this.value;
	}
	
	public static JobLogOption fromValue(int value){
		switch(value){
			case 1 : return LOG_ALL_ACTIVITY;
			case 2 : return LOG_SUMMARY_ONLY;
			case 3 : return LOG_DISABLED;
			default : return LOG_ALL_ACTIVITY;
		}
	}
}

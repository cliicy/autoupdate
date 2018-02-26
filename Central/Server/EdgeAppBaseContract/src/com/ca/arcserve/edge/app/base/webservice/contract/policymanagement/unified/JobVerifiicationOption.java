package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

public enum JobVerifiicationOption {
	NONE(1), SCAN(2), COMPARE(3), CACULATE(4);

	private int value;

	private JobVerifiicationOption(int val) {
		this.value = val;
	}

	public int getValue() {
		return this.value;
	}
	public static JobVerifiicationOption fromValue(int value){
		switch(value){
			case 1 : return NONE;
			case 2 : return SCAN;
			case 3 : return COMPARE;
			case 4 : return CACULATE;
			default : return NONE;
		}
	}
}

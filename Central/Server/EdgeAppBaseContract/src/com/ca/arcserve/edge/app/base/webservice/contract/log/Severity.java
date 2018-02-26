package com.ca.arcserve.edge.app.base.webservice.contract.log;

public enum Severity {
	
	All(-1),
	Information(1),
	Warning(2),
	Error(4),
	ErrorAndWarning(6);
	
	private int value;
	
	private Severity(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}

	public static Severity parse(int severity) {
		switch (severity) {
		case 1:
			return Information;
		case 2:
			return Warning;
		case 4:
			return Error;
		default:
			return null;
		}
	}

}

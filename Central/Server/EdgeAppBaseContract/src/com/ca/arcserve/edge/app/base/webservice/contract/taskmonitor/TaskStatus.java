package com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor;

public enum TaskStatus{
	Unknown(0),
	Pending(1),
	InProcess(2),
	OK(3),
	Warning(4),
	Error(5),
	WarnningCanContinue(6);
	
	private int value;
	private TaskStatus(int value) {
		this.value = value;
	}
	public int getValue() {
		return this.value;
	}
	
	public static TaskStatus parseTaskStatus( Integer value ) {
		switch( value  ){
		case 0:
			return Unknown;
		case 1:
			return Pending;
		case 2:
			return InProcess;
		case 3:
			return OK;
		case 4:
			return Warning;
		case 5:
			return Error;
		case 6:
			return WarnningCanContinue;
		default:
			return Unknown;
		}
	}
}
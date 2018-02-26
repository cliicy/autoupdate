package com.ca.arcserve.edge.app.base.webservice.contract.log;


public enum LogProductType {
	
	CPM(0),
	D2D(1),
	RPS(3),
	LinuxD2D(5),
	ASBU(4);
	
	private int value;
	
	private LogProductType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static LogProductType parse(int value) {
		switch (value) {
		case 1: return D2D;
		case 3: return RPS;
		case 5: return LinuxD2D;
		default: return CPM;
		}
	}

}

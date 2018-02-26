package com.ca.arcserve.edge.app.base.webservice.contract.common;

public enum D2DRole {
	
	Unknown(0),
	WindowsProxy(1),
	WindowsMonitor(2),
	LinuxD2D(3);

	private int value;
	
	private D2DRole(int value){
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public static D2DRole parseInt(int value) {
		switch (value) {
		case 0:
			return Unknown;
		case 0x1:
			return WindowsProxy;
		case 0x2:
			return WindowsMonitor;
		case 0x3:
			return LinuxD2D;		
		default:
			 return Unknown;
		}
	}	
}

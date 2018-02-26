package com.ca.arcserve.edge.app.base.webservice.contract.node;

public enum D2DBackupType {
	
	None(-1),
	Disk(2),
	VM(3);
	
	private int value;
	
	private D2DBackupType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static D2DBackupType parse(int value) {
		switch (value) {
		case 2: return Disk;
		case 3: return VM;
		default: return None;
		}
	}

}

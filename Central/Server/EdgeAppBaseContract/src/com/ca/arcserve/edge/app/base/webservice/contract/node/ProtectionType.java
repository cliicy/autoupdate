package com.ca.arcserve.edge.app.base.webservice.contract.node;

import com.ca.arcserve.edge.app.base.webservice.contract.common.IBit;

public enum ProtectionType implements IBit {
	
	WIN_D2D(0x00000001),
	BAB(0x00000002),
	RPS(0x00000004),
	Restore(0x00000008),
	Conversion(0x00000010),
	Replication(0x00000020),
	Unprotected(0x00000040),
	LINUX_D2D_SERVER(0x00000080),
	ASBUServer(0x00000100); // ASBU Backup Server as destination
	
	private final int value;
	
	private ProtectionType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
}

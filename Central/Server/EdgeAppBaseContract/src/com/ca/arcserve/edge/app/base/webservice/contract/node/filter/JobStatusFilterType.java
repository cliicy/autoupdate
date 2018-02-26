package com.ca.arcserve.edge.app.base.webservice.contract.node.filter;

import com.ca.arcserve.edge.app.base.webservice.contract.common.IBit;

public enum JobStatusFilterType implements IBit {
	
	BackupFailure(0x01),
	RestoreFailure(0x02),
	MergeFailure(0x04),
	CatalogFailure(0x08),
	ReplicationFailure(0x10),
	VirtualStandbyFailure(0x20),
	CopyRecoveryPointsFailure(0x40),
	FileCopyFailure(0x80),
	CopyToTapeFailure(0x100);
	
	private int value;
	
	private JobStatusFilterType(int value) {
		this.value = value;
	}
	
	@Override
	public int getValue() {
		return value;
	}
	
	

}

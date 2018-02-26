package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import com.ca.arcserve.edge.app.base.webservice.contract.common.IBit;

public enum PlanTaskType implements IBit {
	
	WindowsD2DBackup(0x00000001),
	WindowsVMBackup(0x00000002),
	LinuxBackup(0x00000004),
	LocalConversion(0x00000008),
	Replication(0x00000010),
	RemoteConversion(0x00000020),
	MspServerReplication(0x00000040),
	MspClientReplication(0x00000080),
	FileCopy(0x00000100),
	CopyRecoveryPoints(0x00000200),
	
	FileSystemCatalog(0x00000400),
	GRTCatalog(0x00000800),
	FileArchive(0x00001000),
	Archive2Tape(0x00002000);
	
	private int value;
	
	private PlanTaskType(int value) {
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}

	public static PlanTaskType parse(int value) {
		for (PlanTaskType type : values()) {
			if (type.getValue() == value) {
				return type;
			}
		}
		return null;
	}
}

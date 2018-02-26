package com.ca.arcserve.edge.app.base.webservice.contract.log;

public enum LogJobType {
	
	All(-1),
	Backup(1),
	Replication(2),
	Restore(3),
	Merge(4),
	VSB(5),
	Catalog(6),
	CopyRecoveryPoint(7),
	ArchiveToTape(8),
	InstantVM(9),
//	InstantVHD(10),
	FileCopy(11),
	FileArchive(12),
	Purge(13),
	Other(20);
	
	private int value;
	
	private LogJobType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}

	public static LogJobType parse(int jobType) {
		switch (jobType) {
		case 0:
		case 3:
			return Backup;
		case 22:
		case 24:
			return Replication;
		case 11:
		case 12:
		case 13:
		case 15:
		case 16:
		case 20:
			return Catalog;
		case 30:
		case 31:
		case 32:
			return Merge;
		case 1:
		case 5:
		case 10:
		case 17:
		// BUG 764821 2016/1/13
		// add
		case 42:
		// end
			return Restore;		
		case 40:
		case 41:
			return VSB;
		case 2:
			return CopyRecoveryPoint;
		case 71:
			return ArchiveToTape;
		case 8:
			return FileCopy;
		case 70: 
			return FileArchive;
		case 9:
		case 14:
		// BUG 764821 2016/1/13
		// delete
//		case 42:
		// end
		case 19:
			return Other;
		case 60:
		case 61:
			return InstantVM;
//		case 63:
//		case 64:
//			return InstantVHD;
		case 51:
			return Purge;
		default:
			return All;
		}
	}

}

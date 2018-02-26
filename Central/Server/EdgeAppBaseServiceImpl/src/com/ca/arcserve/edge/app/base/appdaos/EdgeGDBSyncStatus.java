package com.ca.arcserve.edge.app.base.appdaos;

public enum EdgeGDBSyncStatus {
	GDB_Full_Sync_Succeed,
	GDB_Full_Sync_Failed;
	
	public static int toInt(EdgeGDBSyncStatus type) {
		return type.ordinal();
	}
	
	public static EdgeGDBSyncStatus fromInt(int num) {
		switch(num) {
		case 0: return GDB_Full_Sync_Succeed;
		case 1: return GDB_Full_Sync_Failed;
		default: return null;
		}
	}
	
	
}

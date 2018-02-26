package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import com.ca.arcserve.edge.app.base.appdaos.EdgeGDBSyncStatus;

public class BranchSyncTaskFactory {
	
	public static BranchSyncTask Create(int syncType) {
		EdgeGDBSyncStatus type = EdgeGDBSyncStatus.fromInt(syncType);
		BranchSyncTask task = null;
		
		switch (type) {
		case GDB_Full_Sync_Succeed:
			task = new BranchIncSyncTask(); 
			break;
			
		case GDB_Full_Sync_Failed:
			task = new BranchFullSyncTask();
			break;
			
		default:
			return null;
		}
		

		return task;
	}
}

package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.EdgeSyncStatus;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeD2DSyncDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.ChangeStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.EdgeSyncComponents;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.SyncStatus;

public class CheckSyncProperties {


	public Integer[] CheckSyncStatus(int[] rhostId) throws EdgeServiceFault {

		ArrayList<Integer> invalidHost = new ArrayList<Integer>();
		
		for (int i = 0; i < rhostId.length; i++) {
			if(rhostId[i] == 0)
				continue;
			if (!CheckStatus(rhostId[i]))
				invalidHost.add(rhostId[i]);
		}
		
		return invalidHost.toArray(new Integer[0]);
		
	}
	
	public Boolean CheckStatus(int branchid) {
		EdgeSyncStatus status = GetSyncStatus(branchid);
		if(status == null)
			return false;
		ChangeStatus serverStatus = ChangeStatus.parse(status.getChange_status());
		SyncStatus syncStatus = SyncStatus.parse(status.getStatus());
		
		if(serverStatus == ChangeStatus.NORMAL)
		{	
			if (status.getLast_cache_id() != 0)
				return true;

			if (syncStatus == SyncStatus.FINISHED)
				return true;
		}

		return false;

	}
	
	private EdgeSyncStatus GetSyncStatus(int branchid) {
		List<EdgeSyncStatus> syncStatusLst = new ArrayList<EdgeSyncStatus>();
		
		IEdgeD2DSyncDao iDao = DaoFactory.getDao(IEdgeD2DSyncDao.class);
		int ret = iDao.as_edge_Get_Sync_Status(
				EdgeSyncComponents.ARCserve_Backup.getValue(), branchid,
				syncStatusLst);
		if (ret != 0)
			return null;

		EdgeSyncStatus syncStatus = null;
		Iterator<EdgeSyncStatus> iter = syncStatusLst.iterator();
		if (iter.hasNext()) {
			syncStatus = iter.next();
			if(syncStatus != null)
				return syncStatus;
		} 

		return null;
	}
}

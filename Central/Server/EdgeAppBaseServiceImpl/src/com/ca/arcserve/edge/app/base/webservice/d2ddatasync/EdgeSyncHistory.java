package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeSyncStatus;
import com.ca.arcserve.edge.app.base.appdaos.EdgeSyncStatusEx;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeD2DSyncDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.EdgeSyncComponents;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.SyncStatus;

public class EdgeSyncHistory {
	private static final Logger logger = Logger.getLogger(EdgeSyncHistory.class);
	private static IEdgeD2DSyncDao syncDao = null;
	
	private void getSyncDao() {
		if(syncDao != null)
			return;
		
		syncDao = DaoFactory.getDao(IEdgeD2DSyncDao.class);
	}
	
	/*
	 * return code: 0 succeeded   others failed
	 */
	public int UpdateSyncHistoryStatus(long lastCacheId, SyncStatus status, EdgeSyncComponents componentid, int branchid)
	{
		getSyncDao();
		
		return syncDao.as_edge_Update_Sync_Status(lastCacheId, status.getValue(), componentid.getValue(), branchid);
	}

	/*
	 * Return code: 0 succeeded 1 SQL error  100 not found
	 */
	public int GetSyncHistoryStatus(EdgeSyncComponents componentid, int branchid, long[] lastCacheId, SyncStatus[] status)
	{	
		Date[] lastUpdate = new Date[1];
		return GetSyncHistoryStatusEx(componentid, branchid, lastCacheId, status, lastUpdate);
	}
	/*
	 * Return code: 0 succeeded 1 SQL error  100 not found
	 */
	public int GetSyncHistoryStatusEx(EdgeSyncComponents componentid, int branchid, long[] lastCacheId, SyncStatus[] status, Date[] lastUpdate)
	{	
		getSyncDao();
		
		List<EdgeSyncStatusEx> syncStatusLst = new ArrayList<EdgeSyncStatusEx>();
		
		int ret = syncDao.as_edge_Get_Sync_Status_ex(componentid.getValue(), branchid, syncStatusLst);
		if(ret != 0)
			return -1;
		
		EdgeSyncStatusEx syncStatus = null;
		Iterator<EdgeSyncStatusEx> iter = syncStatusLst.iterator();
		if (iter.hasNext()) {
			syncStatus = iter.next();
			status[0] = SyncStatus.parse(syncStatus.getStatus());
			lastCacheId[0] = syncStatus.getLast_cache_id();
			lastUpdate[0] = syncStatus.getLast_update();
		} else {
			logger.debug("cannot get sync status(componentid:"
					+ componentid.getValue() + ";branchid:" + branchid);
			return 100;
		}
		
		return ret;
	}
	
	/*
	 * return code: 0 succeeded   others failed
	 */
	public int InsertSyncHistory(long lastCacheId, SyncStatus status, EdgeSyncComponents componentid, int branchid)
	{
		getSyncDao();
		
		return syncDao.as_edge_Insert_Sync_History( lastCacheId,  status.getValue(), componentid.getValue(), branchid);
	}
}

package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeD2DSyncDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.ChangeStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.EdgeSyncComponents;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.SyncStatus;

public class ASBUSyncUtil {

	private int rhostid;
	
	public static ASBUSyncUtil getASBUSyncUtil(int rhostid)
	{
		ASBUSyncUtil util = new ASBUSyncUtil();
		util.rhostid = rhostid;
		return util;
	}
	
	public void UpdateFullSyncStatus(SyncStatus status) {
		UpdateSyncStatus(0,rhostid, status);
		if(status == SyncStatus.FINISHED)
			ClearChangeStatus(rhostid);
	}

	private void ClearChangeStatus(int id) {
		IEdgeD2DSyncDao iDao = DaoFactory.getDao(IEdgeD2DSyncDao.class);
		iDao.as_edge_update_change_status(EdgeSyncComponents.ARCserve_Backup
				.getValue(), id, ChangeStatus.NORMAL
				.getValue());
	}
	private void UpdateSyncStatus(int lastcachid, int id, SyncStatus status) {
		IEdgeD2DSyncDao iDao = DaoFactory.getDao(IEdgeD2DSyncDao.class);
		iDao.as_edge_Update_Sync_Status(lastcachid, status.getValue(),
				EdgeSyncComponents.ARCserve_Backup.getValue(), id);
	}
}

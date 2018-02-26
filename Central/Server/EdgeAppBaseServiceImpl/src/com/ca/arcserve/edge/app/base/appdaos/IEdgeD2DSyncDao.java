package com.ca.arcserve.edge.app.base.appdaos;

import java.util.List;

import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;

public interface IEdgeD2DSyncDao {
	@StoredProcedure(name = "dbo.as_edge_Get_Sync_Status")
	int as_edge_Get_Sync_Status(int componentid, int branchid, @ResultSet List<EdgeSyncStatus> syncStatus);
	
	@StoredProcedure(name = "dbo.as_edge_Get_Sync_Status_ex")
	int as_edge_Get_Sync_Status_ex(int componentid, int branchid, @ResultSet List<EdgeSyncStatusEx> syncStatus);

	@StoredProcedure(name = "dbo.as_edge_Insert_Sync_History")
	int as_edge_Insert_Sync_History(long lastCacheId, int status, int componentid, int branchid);
	
	@StoredProcedure(name = "dbo.as_edge_Update_Sync_Status")
	int as_edge_Update_Sync_Status(long lastCacheId, int status, int componentid, int branchid);
	
	@StoredProcedure(name = "dbo.as_edge_update_change_status")
	int as_edge_update_change_status(int componentid, int branchid, int changeStatus);	
}

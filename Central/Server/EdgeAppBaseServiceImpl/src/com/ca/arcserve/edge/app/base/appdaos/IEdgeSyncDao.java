package com.ca.arcserve.edge.app.base.appdaos;

import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;

public interface IEdgeSyncDao {
	@StoredProcedure(name = "as_edge_sync_delete_branch")
	void as_edge_sync_delete_branch(int branchid);
	
	/*
	 * This is used by ARCserve sync import data into database, it used internally
	 * please don't expose it to webservice, because it has injection risk.
	 */
	@StoredProcedure(name = "as_edge_sync_data")
	void as_edge_sync_data(String tableName, String dataFilePath, String formatFilePath);
	
	@StoredProcedure(name = "as_edge_sync_get_unique_column")
	int as_edge_sync_get_unique_column(String objname, @Out String[] keys );
}

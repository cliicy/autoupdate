package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.WildcardConversion;

public interface IEdgeLogDao {
	
	@StoredProcedure(name = "as_edge_log_add")
	void as_edge_log_add(String version, int productType, Date logUtcTime, Date logLocalTime, int severity,
			long jobId, int jobType, int serverHostId, int targetHostId, 
			int sourceRpsHostId, int targetRpsHostId, @In(jdbcType = Types.VARCHAR)String sourceDataStoreUUID, @In(jdbcType = Types.VARCHAR)String targetDataStoreUUID, 
			@In(jdbcType = Types.VARCHAR)String planUUID, @In(jdbcType = Types.VARCHAR)String targetPlanUUID, String messageText, @Out long[] output_logId );
	
	/**
	 * Get paging activity logs.
	 * @param productType ignored when < 0.
	 * @param nodeId ignored when < 0.
	 * @param nodeName node name filter.
	 * @param severity ignored when < 0. Refer to {@code Severity}.
	 * @param jobId ignored when < 0.
	 * @param sortField ignored when < 0. Refer to {@code SortColumn}
	 * @param sortAsc order by ASC if true, else DESC.
	 * @param startIndex start from 0.
	 * @param count paging count
	 * @param totalCount total logs
	 * @param logs paging logs
	 */
	@StoredProcedure(name = "as_edge_log_getPagingList")
	void as_edge_log_getPagingList(int productType, int nodeId, 
			@WildcardConversion String nodeName,
			@WildcardConversion String vmName,
			String dataStoreUUID,
			int severity, int jobId, int jobType, int targetRPSId, @WildcardConversion String message, Date datetime,
			int sortField, boolean sortAsc, int startIndex, int count, 
			@Out(jdbcType = Types.INTEGER) int[] totalCount,
			@ResultSet List<EdgeLog> logs);
	
	@StoredProcedure(name = "as_edge_log_deleteAll")
	void as_edge_log_deleteAll(int productType, int nodeId);
	
	@StoredProcedure(name = "as_edge_log_deleteOld")
	void as_edge_log_deleteOld(int productType, int nodeId, Date logUtcTime);
	
	@StoredProcedure(name = "as_edge_log_getPagingListById")
	void as_edge_log_getPagingListById(String activityLogids, int startIndex,  int count, @Out(jdbcType = Types.INTEGER) int[] totalCount, @ResultSet List<EdgeLog> logs);

	
}

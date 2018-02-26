package com.ca.arcserve.edge.app.rps.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsNode;

public interface IRpsNodeDao {

	@StoredProcedure(name = "dbo.as_edge_rps_node_list_bygroupid")
	void as_edge_rps_node_list_bygroupid(int gateway, int groupid,
			@In(jdbcType = Types.VARCHAR) String nodePattern,
			@ResultSet List<EdgeRpsNode> hosts);

	@StoredProcedure(name = "dbo.as_edge_rps_node_list")
	void as_edge_rps_node_list(int id, @ResultSet List<EdgeRpsNode> hosts);

	@StoredProcedure(name = "dbo.as_edge_rps_node_update")
	void as_edge_rps_node_update(int gatewayid, int nodeId,
			@In(jdbcType = Types.VARCHAR) String nodeName,
			@In(jdbcType = Types.VARCHAR) String nodeDescription,
			@In(jdbcType = Types.VARCHAR) String ipAddress,
			int appstatus,
			@In(jdbcType = Types.VARCHAR) String fqdnName,
			@Out(jdbcType = Types.INTEGER) int[] id);

	@StoredProcedure(name = "dbo.as_edge_rps_node_delete")
	void as_edge_rps_node_delete(int nodeId);
	
	@StoredProcedure(name = "dbo.as_edge_rps_node_getIdByHostnameIp")
	void as_edge_rps_node_getIdByHostnameIp(int gatewayid, @In(jdbcType = Types.VARCHAR) String hostname,
			@In(jdbcType = Types.VARCHAR) String ipaddress,
			@Out(jdbcType = Types.INTEGER) int[]id);
	
	@StoredProcedure(name = "dbo.as_edge_rps_node_getIdByFqdnName")
	void as_edge_rps_node_getIdByFqdnName(int gatewayid, @In(jdbcType = Types.VARCHAR) String fqdnName,
			@Out(jdbcType = Types.INTEGER) int[]hostId);
	
	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_node_list_by_policy")
	void as_edge_rps_node_list_by_policy(int policyId, @ResultSet List<EdgeRpsNode> hosts);

	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_node_list_by_datastore")
	void as_edge_rps_node_list_by_dedup(int policyId, @ResultSet List<EdgeRpsNode> hosts);
	
	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_unassign_node_list_by_policy")
	void as_edge_rps_unassign_node_list_by_policy(int policyId, @ResultSet List<EdgeRpsNode> hosts);

	@Deprecated
	@StoredProcedure(name = "dbo.as_edge_rps_unassign_node_list_by_datastore")
	void as_edge_rps_unassign_node_list_by_dedup(int policyId, @ResultSet List<EdgeRpsNode> hosts);
}

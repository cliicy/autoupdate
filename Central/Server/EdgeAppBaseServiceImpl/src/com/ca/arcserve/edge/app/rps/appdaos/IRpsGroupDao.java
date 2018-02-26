package com.ca.arcserve.edge.app.rps.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsGroup;

public interface IRpsGroupDao {
	
	@StoredProcedure(name = "dbo.as_edge_rps_group_list")
	void as_edge_rps_group_list(@ResultSet List<EdgeRpsGroup> groups);
	
	@StoredProcedure(name = "dbo.as_edge_rps_group_isexisted_byname")
	void as_edge_rps_group_isexisted(@In(jdbcType = Types.VARCHAR) String groupname,
			@Out(jdbcType = Types.INTEGER) int[]isexisted);
	
	@StoredProcedure(name = "dbo.as_edge_rps_group_update")
	void as_edge_rps_group_update(int groupid,
			@In(jdbcType = Types.VARCHAR) String name,
			@In(jdbcType = Types.VARCHAR) String description,
			@Out(jdbcType = Types.INTEGER) int[] id);
	
	@StoredProcedure(name = "dbo.as_edge_rps_group_assign")
	void as_edge_rps_group_assign(int groupid, int hostid);
	
	@StoredProcedure(name = "dbo.as_edge_rps_group_isexisted_byidname")
	void as_edge_rps_group_isexisted(int groupid, @In(jdbcType = Types.VARCHAR) String groupname,
			@Out(jdbcType = Types.INTEGER) int[]isexisted);
	
	@StoredProcedure(name = "dbo.as_edge_rps_group_unassignall")
	void as_edge_rps_group_unassignall(int groupid);
	
	@StoredProcedure(name = "dbo.as_edge_rps_group_unassign")
	void as_edge_rps_group_unassign(int groupid, int nodeid);
	
	@StoredProcedure(name = "dbo.as_edge_rps_group_remove")
	void as_edge_rps_group_remove(int groupid);
	
}

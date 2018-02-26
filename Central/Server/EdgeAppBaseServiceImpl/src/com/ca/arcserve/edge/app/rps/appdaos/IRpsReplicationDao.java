package com.ca.arcserve.edge.app.rps.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsReplication;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsReplicationUsed;

public interface IRpsReplicationDao {

	@StoredProcedure(name = "dbo.as_edge_rps_replication_update")
	void as_edge_rps_replication_update(int id,
			@In(jdbcType = Types.VARCHAR) String replicationName,
			@In(jdbcType = Types.VARCHAR) String replicationSetting,
			@Out(jdbcType = Types.INTEGER) int[] replicationId);

	@StoredProcedure(name = "dbo.as_edge_rps_replication_delete")
	void as_edge_rps_replication_delete(int replicationId);

	@StoredProcedure(name = "dbo.as_edge_rps_replication_list")
	void as_edge_rps_replication_list(int replicationId,
			@ResultSet List<EdgeRpsReplication> repList);

	@StoredProcedure(name = "dbo.as_edge_rps_replication_used_info")
	void as_edge_rps_replication_used_info(int replicationId,
			@ResultSet List<EdgeRpsReplicationUsed> repList);

}

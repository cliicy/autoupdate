package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;

public interface IEdgeInstantVMDao {
	@StoredProcedure(name = "as_edge_instantVM_update")
	void as_edge_instantVM_update(@In(jdbcType = Types.VARCHAR)String uuid, @In(jdbcType = Types.VARCHAR)String name,
			int nodeId, int recoveryServerId, int rpsServerId, @In(jdbcType = Types.VARCHAR)String dataStoreUuid, 
			int sharedFolderId, int gatewayId, @In(jdbcType = Types.VARCHAR)String xmlContent);
	
	@StoredProcedure(name = "as_edge_instantVM_getVMList")
	void as_edge_instantVM_getVMList(@ResultSet List<EdgeInstantVM> vms);
	
	@StoredProcedure(name = "as_edge_instantVM_delete")
	void as_edge_instantVM_delete(@In(jdbcType = Types.VARCHAR)String uuid);
	
}

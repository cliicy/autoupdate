package com.ca.arcserve.edge.app.rps.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.AuthUuidWrapper;
import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsConnectionInfo;

public interface IRpsConnectionInfoDao {

	@StoredProcedure(name = "dbo.as_edge_rps_connection_info_update")
	void as_edge_rps_connection_info_update(int nodeId, int protocol, int port,
			@In(jdbcType = Types.VARCHAR) String username,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String password, 
			@In(jdbcType = Types.VARCHAR) String majorVersion,
			@In(jdbcType = Types.VARCHAR) String minorVersion,
			@In(jdbcType = Types.VARCHAR) String buildNumber, 
			@In(jdbcType = Types.VARCHAR) String updateNumber,int manage, 
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid);

	@StoredProcedure(name = "dbo.as_edge_rps_connection_info_list")
	void as_edge_rps_connection_info_list(int nodeId,
			@ResultSet List<EdgeRpsConnectionInfo> conInfo);
	
	@StoredProcedure(name = "dbo.as_edge_rps_connection_info_delete")
	void as_edge_rps_connection_info_delete(int nodeId);
	
	@StoredProcedure(name = "dbo.as_edge_rps_connection_info_manage_update")
	void as_edge_rps_connection_info_manage_update(int nodeId, int manage);
	
	@StoredProcedure(name = "dbo.as_edge_rps_GetConnInfoByUUID")
	int as_edge_rps_GetConnInfoByUUID(
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid,
			@Out int[] rhostid,
			@Out(jdbcType = Types.VARCHAR) String[] hostname,
			@Out int[] protcol,
			@Out int[] port);
	
	@StoredProcedure(name = "dbo.as_edge_rps_connection_info_setAuthUuid")
	void as_edge_rps_connection_info_setAuthUuid(
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String authUuid);
	
	@StoredProcedure(name = "dbo.as_edge_rps_connection_info_getAuthUuid")
	void as_edge_rps_connection_info_getAuthUuid(
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid,
			@ResultSet List<AuthUuidWrapper> authUuids);
	
	@StoredProcedure(name = "dbo.as_edge_rps_connection_info_updateUuid")
	void as_edge_rps_connection_info_updateUuid(
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String newNodeUuid,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String newAuthUuid);

}

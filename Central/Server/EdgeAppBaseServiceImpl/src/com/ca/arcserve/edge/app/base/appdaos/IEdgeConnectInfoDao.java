package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public interface IEdgeConnectInfoDao {
	@StoredProcedure(name = "dbo.as_edge_d2dhost_list")
	void as_edge_d2dhost_list(@ResultSet List<EdgeD2DHost> hosts);

	@StoredProcedure(name = "dbo.as_edge_connect_info_list")
	void as_edge_connect_info_list(int hostid,
			@ResultSet List<EdgeConnectInfo> infos);

	@StoredProcedure(name = "dbo.as_edge_arcserve_connect_info_list")
	void as_edge_arcserve_connect_info_list(int hostid,
			@ResultSet List<EdgeArcserveConnectInfo> infos);

	@StoredProcedure(name = "dbo.as_edge_arcserve_connect_info_list_byschedid")
	void as_edge_arcserve_connect_info_list_byschedid(int schedid,
			@ResultSet List<EdgeArcserveConnectInfo> infos);

	/**
	 * 
	 * @param hostid
	 * @param username
	 * @param password
	 * @param uuid
	 * @param protocol
	 * @param port
	 * @param type
	 * @param majorversion
	 * @param minorversion
	 * @param updateversionnumber
	 * @param buildnumber
	 * @param managed
	 */
	@StoredProcedure(name = "dbo.as_edge_connect_info_update")
	void as_edge_connect_info_update(int hostid,
			@In(jdbcType = Types.VARCHAR) String username,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String password,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid,
			int protocol, int port, int type,
			@In(jdbcType = Types.VARCHAR) String majorversion,
			@In(jdbcType = Types.VARCHAR) String minorversion,
			@In(jdbcType = Types.VARCHAR) String updateversionnumber,
			@In(jdbcType = Types.VARCHAR) String buildnumber,
			int managed);
	
	@StoredProcedure(name = "as_edge_connect_info_update_credential")
	void as_edge_connect_info_update_credential(int hostid, 
			@In(jdbcType = Types.VARCHAR) String username,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String password);
	
	/**
	 *
	 * @param uuid
	 * @param protocol
	 * @return the count of rows that are updated. In general, it should be 1
	 */
	@StoredProcedure(name = "dbo.as_edge_connect_info_update_protocol")
	int as_edge_connect_info_update_protocol(
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid,
			int protocol);
	@StoredProcedure(name = "dbo.as_edge_arcserve_connect_info_update")
	void as_edge_arcserve_connect_info_update(int hostid,
			@In(jdbcType = Types.VARCHAR) String causer,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String capasswd,
			int authmode, int protocol, int port, int type, @In(jdbcType = Types.VARCHAR) String version, int managed);

	@StoredProcedure(name = "dbo.as_edge_arcserve_connect_info_update_gdb")
	void as_edge_arcserve_connect_info_update_gdb(int hostid,
			@In(jdbcType = Types.VARCHAR) String causer,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String capasswd,
			int authmode, int protocol, int port, int type, @In(jdbcType = Types.VARCHAR) String version,
			int gdb_branchid, int managed);
	
	@StoredProcedure(name = "dbo.as_edge_arcserve_connect_info_insert_or_update")
	void insertOrUpdateArcserveConnectInfo(int hostid,
								   @In(jdbcType = Types.VARCHAR) String causer,
								   @EncryptSave @In(jdbcType = Types.VARCHAR) String capasswd,
								   int authmode, 
								   int protocol, 
								   int port, 
								   int type, 
								   @In(jdbcType = Types.VARCHAR) String version,
								   int managed, 
								   @In(jdbcType = Types.VARCHAR) String uuid);

	@StoredProcedure(name = "dbo.as_edge_arcserve_connInfo_list_by_gdbbranchid")
	void as_edge_arcserve_connInfo_list_by_gdbbranchid(int gdb_branchid,
			@ResultSet List<EdgeArcserveConnectInfo> infos);

	@StoredProcedure(name = "dbo.as_edge_GetConnInfoByUUID")
	int as_edge_GetConnInfoByUUID(
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid,
			@Out int[] rhostid,
			@Out(jdbcType = Types.VARCHAR) String[] hostname,
			@Out int[] protcol,
			@Out int[] port);

	@StoredProcedure(name = "dbo.as_edge_arcserve_connect_update_managedStatus")
	void as_edge_arcserve_connect_update_managedStatus(int hostid, int managed, 
			@In(jdbcType = Types.VARCHAR)String uuid);

	@StoredProcedure(name = "dbo.as_edge_connect_update_managedStatus")
	void as_edge_connect_update_managedStatus(int hostid, int managed);

	@StoredProcedure(name = "dbo.as_edge_connect_rps_update_managedStatus")
	void as_edge_connect_rps_update_managedStatus(int hostid, int managed);

	@StoredProcedure(name = "dbo.as_edge_connect_remove")
	void as_edge_connect_remove(int hostid);

	@StoredProcedure(name = "dbo.as_edge_arcserve_connect_remove")
	void as_edge_arcserve_connect_remove(int hostid);

	@StoredProcedure(name = "dbo.as_edge_GetD2DHostGroupListByUuid")
	void as_edge_GetD2DHostGroupListByUuid(
			@EncryptSave @In(jdbcType = Types.NVARCHAR) String uuid,
			@ResultSet List<EdgeGroup> groupList);

	@StoredProcedure(name = "as_edge_arcserve_gdb_update_branch_flags")
	void as_edge_arcserve_gdb_update_branch_flags(int hostid, int gdb_branchid, int flags);

	@StoredProcedure(name = "as_edge_arcserve_gdb_can_branch_become_validate")
	int as_edge_arcserve_gdb_can_branch_become_validate(int hostid, int gdb_branchid);
	
	@StoredProcedure(name = "as_edge_arcserve_connect_info_list_get_by_guid")
	int as_edge_arcserve_connect_info_list_get_by_guid(
			@In(jdbcType = Types.VARCHAR)String guid,
			@ResultSet List<EdgeArcserveConnectInfo> infos);
	
	@StoredProcedure(name = "dbo.as_edge_CheckConnInfoByUUID")
	int as_edge_CheckConnInfoByUUID(@EncryptSave @In(jdbcType = Types.VARCHAR)String uuid, @Out int[] count);

	@StoredProcedure(name = "dbo.as_edge_CheckProxyConnInfoByUUID")
	int as_edge_CheckProxyConnInfoByUUID(@EncryptSave @In(jdbcType = Types.VARCHAR)String uuid, @Out int[] count);

	@StoredProcedure(name = "dbo.as_edge_connect_info_update_version")
	int as_edge_connect_info_update_version(
			int hostId,
			@In(jdbcType = Types.VARCHAR) String majorversion, 
			@In(jdbcType = Types.VARCHAR) String minorversion,
			@In(jdbcType = Types.VARCHAR) String updateversionnumber, 
			@In(jdbcType = Types.VARCHAR) String buildnumber);
	
	@StoredProcedure(name = "dbo.as_edge_proxy_by_policyid")
	void as_edge_proxy_by_policyid(int policyId, @ResultSet List<EdgeConnectInfo> proxyInfo);
	
	@StoredProcedure(name = "dbo.as_edge_linux_node_by_policyid")
	void as_edge_linux_node_by_policyid(int policyId, @ResultSet List<EdgeConnectInfo> linuxNodeInfo);
	
	@StoredProcedure(name = "dbo.as_edge_linux_d2d_server_by_policyid")
	void as_edge_linux_d2d_server_by_policyid(int policyId, @ResultSet List<EdgeConnectInfo> linuxD2DInfo);
	
	@StoredProcedure(name = "dbo.as_edge_linux_d2d_server_by_hostid")
	void as_edge_linux_d2d_server_by_hostid(int hostId, @ResultSet List<EdgeConnectInfo> linuxD2DInfo);
	
	@StoredProcedure(name = "dbo.as_edge_linux_node_by_ids")
	void as_edge_linux_node_by_ids(String ids, @ResultSet List<EdgeConnectInfo> linuxNodeInfo);
	
	@StoredProcedure(name = "dbo.as_edge_connect_info_setAuthUuid")
	void as_edge_connect_info_setAuthUuid(
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String authUuid);
	
	@StoredProcedure(name = "dbo.as_edge_connect_info_getAuthUuid")
	void as_edge_connect_info_getAuthUuid(
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid,
			@ResultSet List<AuthUuidWrapper> authUuids);
	
	@StoredProcedure(name = "dbo.as_edge_connect_info_updateUuid")
	void as_edge_connect_info_updateUuid(
			@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String newNodeUuid,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String newAuthUuid);
	
	@StoredProcedure(name = "dbo.as_edge_connect_info_updateUuidByHostId")
	void as_edge_connect_info_updateUuidByHostId(int hostId,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String nodeUuid,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String authUuid);
	
	@StoredProcedure(name = "dbo.as_edge_connect_info_update_uuid")
	void as_edge_connect_info_update_uuid(int hostId, @EncryptSave @In(jdbcType = Types.VARCHAR) String uuid);

}

package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.annotations.WildcardConversion;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHyperVHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport.HypervEntity;

public interface IEdgeHyperVDao {
	
	public static final int HYPERV_HOST_STATUS_VISIBLE = 1;
	public static final int HYPERV_HOST_STATUS_INVISIBLE = 2;
	
	@StoredProcedure(name = "dbo.as_edge_hyperv_getById")
	void as_edge_hyperv_getById(int id, @ResultSet List<EdgeHyperV> hypervList);
	
	@StoredProcedure(name = "dbo.as_edge_hyperv_get_auto_discovery")
	void as_edge_hyperv_get_auto_discovery(@ResultSet List<EdgeHyperV> hypervList);
	
	@StoredProcedure(name = "dbo.as_edge_hyperv_getByName")
	void as_edge_hyperv_getByName(int gatewayid, String hostname, @ResultSet List<EdgeHyperV> hypervList);
	
	@StoredProcedure(name = "dbo.as_edge_hyperv_update")
	void as_edge_hyperv_update(int id,
			@In(jdbcType = Types.VARCHAR) String hostname,
			@In(jdbcType = Types.NVARCHAR) String username,
			@EncryptSave @In(jdbcType = Types.NVARCHAR) String password,
			int protocol, int port, int visible,int type,
			@Out(jdbcType = Types.INTEGER) int[] newId);
	
	@StoredProcedure(name = "dbo.as_edge_hyperv_delete")
	void as_edge_hyperv_delete(int id);
	
	@StoredProcedure(name = "dbo.as_edge_hyperv_update_auto_discovery_flag")
	void as_edge_hyperv_update_auto_discovery_flag(int id, int isAutoDiscovery);
	
	@StoredProcedure(name = "dbo.as_edge_hyperv_host_map_add")
	void as_edge_hyperv_host_map_add(int hypervId, int hostId, int status,
			@In(jdbcType = Types.NVARCHAR) String vmName,
			@In(jdbcType = Types.NVARCHAR) String vmUuid,
			@In(jdbcType = Types.NVARCHAR) String vmInstanceUuid,
			@In(jdbcType = Types.VARCHAR) String hypervHost,
			@In(jdbcType = Types.VARCHAR) String vmGuestOS);
	
	@StoredProcedure(name = "dbo.as_edge_hyperv_host_map_delete")
	void as_edge_hyperv_host_map_delete(int hypervId, int hostId, int status);
	
	@StoredProcedure(name = "dbo.as_edge_hyperv_host_map_isExist")
	void as_edge_hyperv_host_map_isExist(int hypervId, int hostId,
			@Out(jdbcType = Types.INTEGER) int[] isExist);
	
	@StoredProcedure(name = "dbo.as_edge_hyperv_host_map_updateStatus")
	void as_edge_hyperv_host_map_updateStatus(int hypervId, int hostId, int status);
	
	@StoredProcedure(name = "dbo.as_edge_host_getListFromHypervDiscoveryResult")
	void as_edge_host_getListFromHypervDiscoveryResult(
			@WildcardConversion @In(jdbcType = Types.NVARCHAR) String hostnamePattern,
			int visible, int status, int startIndex, int count,
			int sortColumn, boolean isASC,
			@Out(jdbcType = Types.INTEGER) int[] totalCount,
			@ResultSet List<EdgeHyperVHost> hostList);

    @StoredProcedure(name = "dbo.as_edge_hyperv_host_map_getById")
	void as_edge_hyperv_host_map_getById(int id, @ResultSet List<EdgeHyperVHostMapInfo> hostMapInfo);
    
    @StoredProcedure(name = "dbo.as_edge_hyperv_host_map_isExistByVMInstanceUuid")
	void as_edge_hyperv_host_map_isExistByVMInstanceUuid(int gatewayid, 
			String instanceUuid, @Out(jdbcType = Types.INTEGER) int[] hostId);
    
	@StoredProcedure(name = "dbo.as_edge_hyperv_host_map_update")
	void as_edge_hyperv_host_map_update(int hostId,
			@In(jdbcType = Types.NVARCHAR) String vmName,
			@In(jdbcType = Types.NVARCHAR) String vmUuid,
			@In(jdbcType = Types.NVARCHAR) String vmInstanceUuid,
			@In(jdbcType = Types.VARCHAR) String hypervHost,
			@In(jdbcType = Types.VARCHAR) String vmGuestOS);
	
	@StoredProcedure(name = "dbo.as_edge_hyperv_host_map_updateHyperVIDByVMUUID")
	void as_edge_hyperv_host_map_updateHyperVIDByVMUUID(@In(jdbcType = Types.NVARCHAR) String vmInstanceUuid, int hypervId);
	
	@StoredProcedure(name = "dbo.as_edge_hyperv_host_map_updateHyperVIDByID")
	void as_edge_hyperv_host_map_updateHyperVIDByID(int id, int hypervId, String hypervisorHost);
	
    @StoredProcedure(name = "dbo.as_edge_hyperv_host_map_list")
	void as_edge_hyperv_host_map_list(@ResultSet List<EdgeHyperVHostMapInfo> hostMapInfo);
    //fanda03 fix 143246
    @StoredProcedure(name = "dbo.as_edge_hyperv_host_map_list_by_hypervid")
	void as_edge_hyperv_host_map_list_by_hypervid(Integer hypervId, @ResultSet List<EdgeHyperVHostMapInfo> hostMapInfo);
    
    @StoredProcedure(name = "dbo.as_edge_hyperv_verify_status_update")
	void as_edge_hyperv_verify_status_update(int nodeID, int status, String detail);
    
    @StoredProcedure(name = "dbo.as_edge_hyperv_verify_status_getById")
	void as_edge_hyperv_verify_status_getById(int nodeID, @ResultSet List<EdgeHyperVVerifyStatus> detail);
    
    @StoredProcedure(name = "dbo.as_edge_hyperv_updateLicenseInfo")
    void as_edge_hyperv_updateLicenseInfo(int hostid, int socketCount);
    
    @StoredProcedure(name = "dbo.as_edge_hyperv_getLicenseInfo")
    void as_edge_hyperv_getLicenseInfo(int hostId, 
    		@Out(jdbcType = Types.INTEGER) int[] hypervId,
    		@Out(jdbcType = Types.VARCHAR) String[] hyperVHost,
    		@Out(jdbcType = Types.INTEGER) int[] socketCount);
    
    /**
     * added by tonyzhai
     * list hyper-v vm and its hypervisor information
     * 
     * @param hostIds
     * @param hosts
     */
    @StoredProcedure(name = "as_edge_hyperv_vm_list_by_ids")
    void as_edge_hyperv_vm_list_by_ids(@In(jdbcType = Types.VARCHAR) String hostIds, @ResultSet List<EdgeHost> hosts);
    
    @StoredProcedure(name = "dbo.as_edge_hyperv_getByNodeId")
    void as_edge_hyperv_getByNodeId(int hostId, @ResultSet List<EdgeHyperV> hypervList);
    
    @StoredProcedure(name = "dbo.as_edge_getHypervsByIds")
	void as_edge_getHypervsByIds(String ids,
			@ResultSet List<HypervEntity> hypervs);
}

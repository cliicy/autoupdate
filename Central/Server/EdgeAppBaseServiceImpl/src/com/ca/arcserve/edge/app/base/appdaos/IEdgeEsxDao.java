package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.annotations.WildcardConversion;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport.EsxEntity;

public interface IEdgeEsxDao {
	
	public static final int ESX_HOST_STATUS_VISIBLE = 1;
	public static final int ESX_HOST_STATUS_INVISIBLE = 2;
	
	@StoredProcedure(name = "dbo.as_edge_esx_getById")
	void as_edge_esx_getById(int id, @ResultSet List<EdgeEsx> esxList);
	
	@StoredProcedure(name = "dbo.as_edge_esx_get_auto_discovery")
	void as_edge_esx_get_auto_discovery(@ResultSet List<EdgeEsx> esxList);
	
	@StoredProcedure(name = "dbo.as_edge_esx_getByName")
	void as_edge_esx_getByName(int gatewayid, String hostname, @ResultSet List<EdgeEsx> esxList);
	
	@StoredProcedure(name = "dbo.as_edge_esx_getVsphereEntityByUuidAndName")
	void as_edge_esx_getVsphereEntityByUuidAndName(int gatewayid, @In(jdbcType = Types.NVARCHAR) String uuid
			,@In(jdbcType = Types.NVARCHAR) String hostname, @ResultSet List<EdgeEsx> vsphereEntityList);
	
	@StoredProcedure(name = "dbo.as_edge_esx_update")
	void as_edge_esx_update(int id,
			@In(jdbcType = Types.VARCHAR) String hostname,
			@In(jdbcType = Types.NVARCHAR) String username,
			@EncryptSave @In(jdbcType = Types.NVARCHAR) String password,
			int protocol, int port,int serverType,int visible,
			@In(jdbcType = Types.NVARCHAR) String description,
			@In(jdbcType = Types.NVARCHAR) String uuid,
			@Out(jdbcType = Types.INTEGER) int[] newId);
	
	@StoredProcedure(name = "dbo.as_edge_esx_delete")
	void as_edge_esx_delete(int id);
	
	@StoredProcedure(name = "dbo.as_edge_esx_update_auto_discovery_flag")
	void as_edge_esx_update_auto_discovery_flag(int id, int isAutoDiscovery);
	
	@StoredProcedure(name = "dbo.as_edge_esx_getESXIdByVMUUID")
	void as_edge_esx_getESXIdByVMUUID(String instanceUuid, @Out(jdbcType = Types.INTEGER) int[] esxId);
	
	@StoredProcedure(name = "dbo.as_edge_esx_updateESXIDByVMUUID")
	void as_edge_esx_updateESXIDByVMUUID(@In(jdbcType = Types.NVARCHAR) String vmInstanceUuid, int esxId);
	
	@StoredProcedure(name = "dbo.as_edge_host_getListFromEsxDiscoveryResult")
	void as_edge_host_getListFromEsxDiscoveryResult(
			@WildcardConversion @In(jdbcType = Types.NVARCHAR) String hostnamePattern,
			int visible, int status, int startIndex, int count,
			int sortColumn, boolean isASC,
			@Out(jdbcType = Types.INTEGER) int[] totalCount,
			@ResultSet List<EdgeEsxHost> hostList);
	
	@StoredProcedure(name = "dbo.as_edge_esx_update_type")
	void as_edge_esx_update_type(int id, int type);
	
	@StoredProcedure(name = "dbo.as_edge_esx_getServerType")
	int as_edge_esx_getServerType(int esxId);
    
    @StoredProcedure(name = "dbo.as_edge_esx_verify_status_update")
	void as_edge_esx_verify_status_update(int nodeID, int status, String detail);
    
    @StoredProcedure(name = "dbo.as_edge_esx_verify_status_getById")
	void as_edge_esx_verify_status_getById(int nodeID, @ResultSet List<EdgeEsxVerifyStatus> detail);
    
    @StoredProcedure(name = "dbo.as_edge_esx_updateLicenseInfo")
    void as_edge_esx_updateLicenseInfo(int hostid, int essential, int socketCount);
    
    @StoredProcedure(name = "dbo.as_edge_esx_getLicenseInfo")
    void as_edge_esx_getLicenseInfo(int hostId, 
    		@Out(jdbcType = Types.INTEGER) int[] exsId,
    		@Out(jdbcType = Types.VARCHAR) String[] esxHost,
    		@Out(jdbcType = Types.INTEGER) int[] essential,
    		@Out(jdbcType = Types.INTEGER) int[] socketCount);
    
    /**
     * added by tonyzhai
     * list esx vm and its hypervisor information
     * 
     * @param hostIds
     * @param hosts
     */
    @StoredProcedure(name = "as_edge_esx_vm_list_by_ids")
    void as_edge_esx_vm_list_by_ids(@In(jdbcType = Types.VARCHAR) String hostIds, @ResultSet List<EdgeHost> hosts);
    
    @StoredProcedure(name = "as_edge_vsphere_entity_map_insert")
    void as_edge_vsphere_entity_map_insert(int childId, int parentId, int relationType);
    
    @StoredProcedure(name = "as_edge_vsphere_entity_map_update")
    void as_edge_vsphere_entity_map_update(int childId, int parentId, int relationType);
    
    @StoredProcedure(name = "as_edge_esx_getEntityByHostId")
    void as_edge_esx_getEntityByHostId(int hostId, @ResultSet List<EdgeEsx> esxList);
    
    @StoredProcedure(name = "as_edge_esx_getHypervisorByHostId")
    void as_edge_esx_getHypervisorByHostId(int hostId,@ResultSet List<EdgeEsx> esxList);
    
    @StoredProcedure(name = "as_edge_vsphere_entity_map_getVcentersByvApp")
    void as_edge_vsphere_entity_map_getVcentersByvApp(int vappEsxId, @ResultSet List<EdgeEsx> esxList);
    
    @StoredProcedure(name = "as_edge_vsphere_entity_map_getVappHostList_By_EsxGroup")
    void as_edge_vsphere_entity_map_getVappHostList_By_EsxGroup(int esxGroupId, @ResultSet List<EdgeHost> vappList);
    
    @StoredProcedure(name = "as_edge_vsphere_vm_detail_update")
    void as_edge_vsphere_vm_detail_update(int entityId,int status,
			@In(jdbcType = Types.NVARCHAR) String vmName,
			@In(jdbcType = Types.NVARCHAR) String vmUuid,
			@In(jdbcType = Types.VARCHAR) String esxHost,
			@In(jdbcType = Types.VARCHAR) String vmXPath,
			@In(jdbcType = Types.VARCHAR) String vmGuestOS);
    
    @StoredProcedure(name = "as_edge_vsphere_vm_detail_getVMByvApp")
    void as_edge_vsphere_vm_detail_getVMByvApp(int vappEsxId, @ResultSet List<EdgeEsxHostMapInfo> vmList);
    
    @StoredProcedure(name = "as_edge_vsphere_vm_detail_getVMByVmHostId")
    void as_edge_vsphere_vm_detail_getVMByVmHostId(int vmHostId, @ResultSet List<EdgeEsxVmInfo> vmList);
    
    @StoredProcedure(name = "as_edge_vsphere_vm_detail_getVMByEsxServerId")
    void as_edge_vsphere_vm_detail_getVMByEsxServerId(int esxServerId, @ResultSet List<EdgeEsxVmInfo> vmList);
    
    @StoredProcedure(name = "as_edge_vsphere_vm_detail_updateEsxHost")
    void as_edge_vsphere_vm_detail_updateEsxHost(@In(jdbcType = Types.NVARCHAR) String instanceUUID,
			@In(jdbcType = Types.NVARCHAR) String esxHost);
    
	@StoredProcedure(name = "dbo.as_edge_vsphere_vm_detail_updateStatus")
	void as_edge_vsphere_vm_detail_updateStatus(int hostId, int status);
    
    @StoredProcedure(name = "as_edge_vsphere_entity_host_map_update")
    void as_edge_vsphere_entity_host_map_update(int hostId, int entityId);
    
    @StoredProcedure(name = "as_edge_host_getHostByInstanceUUID")
    void as_edge_host_getHostByInstanceUUID(int gatewayid, String uuid, @Out(jdbcType = Types.INTEGER) int[] hostIds);
    
    @StoredProcedure(name = "as_edge_host_getVisibleHostByInstanceUUID")
    void as_edge_host_getVisibleHostByInstanceUUID(int gatewayid, String uuid, @Out(jdbcType = Types.INTEGER) int[] hostIds);
    
    @StoredProcedure(name = "as_edge_vsphere_entity_host_map_delete")
    void as_edge_vsphere_entity_host_map_delete(int hostId, int entityId);
    
    @StoredProcedure(name = "dbo.as_edge_getEsxsByIds")
   	void as_edge_getEsxsByIds(String ids,
   			@ResultSet List<EsxEntity> esxs);
}

package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.annotations.WildcardConversion;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.ArcserveInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.ConverterSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.D2DInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.GatewaySummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.JobSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.LinuxD2DInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeVcloudSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.PlanSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.ProxyInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.RemoteDeployInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.VmInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport.NodeExportEntity;

public interface IEdgeHostMgrDao {
	@StoredProcedure(name = "dbo.as_edge_group_isexisted")
	void as_edge_group_isexistedByGroupName(int gatewayId,
			@In(jdbcType = Types.VARCHAR) String groupname,
			@Out(jdbcType = Types.INTEGER) int[]isexisted);
	
	@StoredProcedure(name = "dbo.as_edge_group_isexisted_withID")
	void as_edge_group_isexisted(int groupid, @In(jdbcType = Types.VARCHAR) String groupname,
			@Out(jdbcType = Types.INTEGER) int[]isexisted);
	
	@StoredProcedure(name = "dbo.as_edge_group_list")
	void as_edge_group_list(int gatewayId, @ResultSet List<EdgeSourceGroup> groups);
	
	@StoredProcedure(name = "dbo.as_edge_hyperv_group_list")
	void as_edge_hyperv_group_list(int gatewayId, @ResultSet List<EdgeSourceGroup> groups);
	
	@StoredProcedure(name = "dbo.as_edge_winProxy_group_list")
	void as_edge_winProxy_group_list(int gatewayId, @ResultSet List<EdgeSourceGroup> groups);
	
	@StoredProcedure(name = "dbo.as_edge_linuxServer_group_list")
	void as_edge_linuxServer_group_list(int gatewayId,@ResultSet List<EdgeSourceGroup> groups);
	
	@StoredProcedure(name = "dbo.as_edge_gdb_group_list")
	void as_edge_gdb_group_list(@ResultSet List<EdgeSourceGroup> groups);
	
	@StoredProcedure(name = "dbo.as_edge_group_assign")
	void as_edge_group_assign(int groupid, int hostid);

	@StoredProcedure(name = "dbo.as_edge_group_unassign")
	void as_edge_group_unassign(int groupid, int hostid);

	@StoredProcedure(name = "dbo.as_edge_group_unassignall")
	void as_edge_group_unassignall(int groupid);

	@StoredProcedure(name = "dbo.as_edge_group_update")
	void as_edge_group_update(int groupid,
			@In(jdbcType = Types.VARCHAR) String name,
			@In(jdbcType = Types.VARCHAR) String comments, int isvisible,
			@Out(jdbcType = Types.INTEGER) int[] id);

	@StoredProcedure(name = "dbo.as_edge_group_remove")
	void as_edge_group_remove(int id);

	@StoredProcedure(name = "dbo.as_edge_group_esx_remove")
	void as_edge_group_esx_remove(int id,int type);
	
	@StoredProcedure(name = "dbo.as_edge_group_hyperv_remove")
	void as_edge_group_hyperv_remove(int id,int type);
	
	@StoredProcedure(name = "dbo.as_edge_group_get_all_list")
	void as_edge_group_get_all_list(int gatewayId, @ResultSet List<EdgeSourceGroup> groups);
	
	@StoredProcedure(name = "dbo.as_edge_group_get_type_list")
	void as_edge_group_get_type_list(int gatewayid, @ResultSet List<EdgeIntegerValue> groupTypes);
	
	@StoredProcedure(name = "dbo.as_edge_host_list_bytype")
	void as_edge_host_list_bytype(int type,
			@ResultSet List<EdgeHost> hosts);
			
	@StoredProcedure(name = "dbo.as_edge_host_list_bygroupid")
	void as_edge_host_list_bygroupid(int groupid,
			@ResultSet List<EdgeHost> hosts);

	@StoredProcedure(name = "dbo.as_edge_host_list_paged")
	void as_edge_host_list_paged(int startpos, int count, int IsVisible,
			@Out(jdbcType = Types.INTEGER) int[]totalcount,
			@ResultSet List<EdgeHost> hosts);

	@StoredProcedure(name = "dbo.as_edge_host_list_byFilter_paged")
	void as_edge_host_list_byFilter_paged(@In(jdbcType = Types.VARCHAR) String host, @In(jdbcType = Types.VARCHAR) String osdesc, int app, int startpos, int count, int IsVisible,
			@Out(jdbcType = Types.INTEGER) int[]totalcount,
			@ResultSet List<EdgeHost> hosts);
	
	@StoredProcedure(name = "dbo.as_edge_host_list")
	void as_edge_host_list(int id, int IsVisible,
			@ResultSet List<EdgeHost> hosts);

	@StoredProcedure(name = "dbo.as_edge_deletedhost_list")
	void as_edge_deletedhost_list(@ResultSet List<EdgeHost> hosts);	
	
	@StoredProcedure(name = "as_edge_purge_d2d")
	void as_edge_purge_d2d(int hostID);
	
	@StoredProcedure(name = "dbo.as_edge_hosts_list")
	void as_edge_hosts_list(@In(jdbcType = Types.VARCHAR) String ids,
			@ResultSet List<EdgeHost> hosts);

	// check hostid > 0 for exists
//	@StoredProcedure(name = "dbo.as_edge_host_isexisted")
//	void as_edge_host_isexisted(@In(jdbcType = Types.VARCHAR) String hostname,
//			@In(jdbcType = Types.VARCHAR) String ipaddress,
//			@Out(jdbcType = Types.INTEGER) int[]hostId);
	
	// check hostid > 0 for exists
	@StoredProcedure(name = "dbo.as_edge_host_linux_node_isexisted")
	void as_edge_host_linux_node_isexisted(@In(jdbcType = Types.VARCHAR) String hostname,
			@In(jdbcType = Types.VARCHAR) String ipaddress,
			int nodetype,
			@Out(jdbcType = Types.INTEGER) int[]hostId);
	
	@StoredProcedure(name = "dbo.as_edge_host_getIdByHostnameIp")
	void as_edge_host_getIdByHostnameIp(int gatewayid, @In(jdbcType = Types.VARCHAR) String hostname,
			@In(jdbcType = Types.VARCHAR) String ipaddress,
			int isVisible,
			@Out(jdbcType = Types.INTEGER) int[]hostId);
	
	@StoredProcedure(name = "dbo.as_edge_host_getIdByFqdnName")
	void as_edge_host_getIdByFqdnName(int gatewayid, @In(jdbcType = Types.VARCHAR) String fqdnName,
			@Out(jdbcType = Types.INTEGER) int[]hostId);
	
	@StoredProcedure(name = "dbo.as_edge_host_getIdByHostnameOrIp")
	void as_edge_host_getIdByHostnameOrIp(@In(jdbcType = Types.VARCHAR) String hostname,
			@In(jdbcType = Types.VARCHAR) String ipaddress,
			@Out(jdbcType = Types.INTEGER) int[]hostId);
	
	
	@StoredProcedure(name = "dbo.as_edge_host_getIdByHostname")
	void as_edge_host_getIdByHostname(@In(jdbcType = Types.VARCHAR) String hostname,
			@Out(jdbcType = Types.INTEGER) int[]hostId);
	
	/**
	 * Get arcserve backup domain server id by domain name and protection type
	 * 
	 * @param hostname
	 * @param protectionType
	 * @param hostId
	 */
	@StoredProcedure(name = "dbo.as_edge_host_getIdByHostnameAndProtectionType")
	void getIdByHostnameAndProtectionType(@In(jdbcType = Types.VARCHAR) String hostname, int protectionType, @Out(jdbcType = Types.INTEGER) int[]hostId);
	
	@StoredProcedure(name = "dbo.as_edge_host_getIdByHostnameForLinux")
	void as_edge_host_getIdByHostnameForLinux(@In(jdbcType = Types.VARCHAR) String hostname,@In(jdbcType = Types.VARCHAR) String ip,
			int nodetype,@Out(jdbcType = Types.INTEGER) int[]hostId);
	
	@StoredProcedure(name = "dbo.as_edge_host_set_managed")
	void as_edge_host_set_managed(@In(jdbcType = Types.VARCHAR) String IDs,
			int IsVisible);

	@StoredProcedure(name = "dbo.as_edge_host_update_status")
	void as_edge_host_update_status(
			@In(jdbcType = Types.VARCHAR) String rhostname,
			int appStatus);
	
	@StoredProcedure(name = "dbo.as_edge_host_update_timezone_by_id")
	void as_edge_host_update_timezone_by_id(int rhostid, int rawoffset);
	
	@StoredProcedure(name = "dbo.as_edge_host_get_timezone_by_id")
	void as_edge_host_get_timezone_by_id(int rhostid, @Out(jdbcType = Types.INTEGER) int[] rawoffset);
	
	@StoredProcedure(name = "dbo.as_edge_host_update")
	void as_edge_host_update(int rhostid, 
			@In(jdbcType = Types.TIMESTAMP) Date lastupdated,
			@In(jdbcType = Types.VARCHAR) String rhostname,
			@In(jdbcType = Types.VARCHAR) String nodeDescription,
			@In(jdbcType = Types.VARCHAR) String ipaddress,
			@In(jdbcType = Types.VARCHAR) String osdesc,
			@In(jdbcType = Types.VARCHAR) String ostype,
			int IsVisible, int appStatus,
			@In(jdbcType = Types.VARCHAR) String ServerPrincipalName,
			int rhostType,
			int protectionType,
			@In(jdbcType = Types.VARCHAR) String fqdnName,
			@Out(jdbcType = Types.INTEGER) int[] id);
	
	// This is used when import VMs from HBBU into VCM
	// Only has Derby implementation, no store procedure for SQL Server
	@StoredProcedure(name = "dbo.as_edge_host_update_ImportVSphereVM")
	void as_edge_host_update_ImportVSphereVM(
			String hypervisor, String vmInstanceUuid,
			int rhostid, 
			@In(jdbcType = Types.TIMESTAMP) Date lastupdated,
			@In(jdbcType = Types.VARCHAR) String rhostname,
			@In(jdbcType = Types.VARCHAR) String nodeDescription,
			@In(jdbcType = Types.VARCHAR) String ipaddress,
			@In(jdbcType = Types.VARCHAR) String osdesc,
			int IsVisible, int appStatus,
			@In(jdbcType = Types.VARCHAR) String ServerPrincipalName,
			int rhostType,
			@Out(jdbcType = Types.INTEGER) int[] id);

	@StoredProcedure(name = "dbo.as_edge_host_update_bydiscovery")
	void as_edge_host_update_bydiscovery(
			@In(jdbcType = Types.TIMESTAMP) Date lastupdated,
			@In(jdbcType = Types.VARCHAR) String rhostname,
			@In(jdbcType = Types.VARCHAR) String domainname,
			@In(jdbcType = Types.VARCHAR) String ipaddress,
			@In(jdbcType = Types.VARCHAR) String osdesc,
			int appStatus,
			@In(jdbcType = Types.VARCHAR) String ServerPrincipalName,
			@Out(jdbcType = Types.INTEGER) int[] id);

	@StoredProcedure(name = "dbo.as_edge_host_remove")
	void as_edge_host_remove(int id);
	@StoredProcedure(name = "dbo.as_edge_host_remove_history_and_logs")
	void removeHistoryAndJobs(int id);
	
	@StoredProcedure( name = "dbo.as_edge_host_get_node_groups" )
	void as_edge_host_get_node_groups(
		int nodeId,
		@ResultSet List<EdgeIntegerValue> groupIds );
	
	@StoredProcedure( name = "dbo.as_edge_host_get_node_esx_groups" )
	void as_edge_host_get_node_esx_groups(
		int nodeId,
		@ResultSet List<EdgeIntegerValue> groupIds );
	
	@StoredProcedure( name = "dbo.as_edge_host_get_node_hyperv_groups" )
	void as_edge_host_get_node_hyperv_groups(
		int nodeId,
		@ResultSet List<EdgeIntegerValue> groupIds );
	
	@StoredProcedure( name = "dbo.as_edge_host_list_by_gdb_id")
	void as_edge_host_list_by_gdb_id(
		int hostId,
		@ResultSet List<EdgeHost> hosts);
	
	// added by Weiping Li on 2010-10-21
	@StoredProcedure( name = "dbo.as_edge_host_list_display_info_by_gdb_id")
	void as_edge_host_list_display_info_by_gdb_id(
		int hostId,
		@ResultSet List<EdgeHost> hosts);

	// end of adding by Weiping Li
	@StoredProcedure(name = "dbo.as_edge_host_list_display_info_by_gdb_id_paging")
	void as_edge_host_list_display_info_by_gdb_id_paging(int hostid,
			int policytype,
			@WildcardConversion @In(jdbcType = Types.VARCHAR) String nodepara,
			int startpos, int pagesize,
			@In(jdbcType = Types.VARCHAR) String sortorder,
			@In(jdbcType = Types.VARCHAR) String sortcol,
			@In(jdbcType = Types.VARCHAR) String sortcol2,
			@Out(jdbcType = Types.INTEGER) int[] totalcount,
			@ResultSet List<EdgeHost> hosts);
	
	@StoredProcedure( name = "dbo.as_edge_host_list_by_group_type_appstatus_prodType")
	void as_edge_host_list_by_group_type_appstatus_prodType(
		int groupid, 
		int type,
		int appstatus,
		@ResultSet List<EdgeHost> hosts);
	
	@StoredProcedure( name = "dbo.as_edge_host_esx_list_by_group_type_appstatus_prodType")
	void as_edge_host_esx_list_by_group_type_appstatus_prodType(
		int groupid, 
		int type,
		int appstatus,
		int grouptype,
		int minvisible,
		@ResultSet List<EdgeHost> hosts);
	
	@StoredProcedure(name = "dbo.as_edge_GetFilteredPagingNodeList")
	void as_edge_GetFilteredPagingNodeList(
			int groupId, int groupType, int hostTypes,int visibleLevel,int vappId,
			@WildcardConversion @In(jdbcType = Types.VARCHAR) String nodeNamePattern,
			int gateway, int appBitmap, int jobStatusBitmap, int protectionTypeBitmap, 
			int nodeStatusBitmap, int osBitmap, int deployStatusBitmap, 
			int lastBackupStatusBitmap,String deployStatusParameter,
			int startPos, int pageSize, int notnullfieldBitmap,
			@In(jdbcType = Types.VARCHAR) String sortOrder,
			@In(jdbcType = Types.VARCHAR) String sortCol,
			@In(jdbcType = Types.VARCHAR) String sortCol2,
			@Out(jdbcType = Types.INTEGER) int[] totalCount,
			@ResultSet List<EdgeHost> hosts);
	
	@StoredProcedure(name = "dbo.as_edge_enum_D2D_node")
	void as_edge_enum_D2D_node(@ResultSet List<EdgeD2DNodeStatus> d2dNodes);
	
	@StoredProcedure(name = "dbo.as_edge_enum_D2D_node_by_id")
	void as_edge_enum_D2D_node_by_id(int hostid, @ResultSet List<EdgeD2DNodeStatus> d2dNodes);
	
	@StoredProcedure(name = "dbo.as_edge_update_D2D_status")
	void as_edge_update_D2D_status(int rhostid, int status, long destFreeSpace);
	
	// result[0] is the result, 0 is false, 1 is true
	@StoredProcedure(name = "dbo.as_edge_host_isVSphereVMExisted")
	void isVSphereVMExisted(
		String hypervisor,
		String instanceUuid,
		@Out(jdbcType = Types.INTEGER) int[] isExisted );

	@StoredProcedure(name = "dbo.as_edge_host_update_hostInfo")
	void as_edge_host_update_hostInfo(
			int rhostid,
			@In(jdbcType = Types.VARCHAR) String rhostname,
			@In(jdbcType = Types.VARCHAR) String ipaddress
			//@In(jdbcType = Types.VARCHAR) String osdesc
			);
	
	@StoredProcedure(name = "dbo.as_edge_host_setD2DStatusInfo")
	void setD2DStatusInfo(
		int hostId,
		int overallStatus,
		@In(jdbcType = Types.TIMESTAMP) Date lastBackupStartTime,
		int lastBackupType,
		int lastBackupJobStatus,
		int lastBackupStatus,
		int recPointRetentionCount,
		int recPointCount,
		@In(jdbcType = Types.VARCHAR) String recPointMounted,
		int recPointStatus,
		boolean isUseBackupSets,
		@In(jdbcType = Types.VARCHAR) String destPath,
		int isDestAccessible,
		long destFreeSpace,
		int destEstimatedBackupCount,
		int destStatus,
		int isDriverInstalled,
		int isRestarted,
		int estimatedValue,
		int isBackupConfiged
		);
	
	// only implemented for java DB, no SQL server store procedure
	@StoredProcedure(name = "dbo.as_edge_host_insertVCMStorage")
	void insertVCMStorage(
		int hostId,
		int no,
		@In(jdbcType = Types.VARCHAR) String storageName,
		long freeSize,
		long coldStandySize,
		long totalSize,
		long otherSize
		);
	
	// only implemented for java DB, no SQL server store procedure
	@StoredProcedure(name = "dbo.as_edge_host_clearVCMStorages")
	void clearVCMStorages(
		int hostId
		);
	
	@StoredProcedure(name = "dbo.as_edge_host_getVCMStorages")
	void getVCMStorages(
		int hostId,
		@ResultSet List<EdgeVCMStorage> storageList
		);
	
	@StoredProcedure(name = "dbo.as_edge_host_markHostAsRemoved")
	void markHostAsRemoved(@In(jdbcType = Types.NVARCHAR) String nodeIDs );
	
	@StoredProcedure(name = "dbo.as_edge_host_update_rhosttype_by_id")
	void as_edge_host_update_rhosttype_by_id(int rhostid, int rhosttype);
	
	@StoredProcedure(name = "dbo.as_edge_host_deleteD2DStatusInfo")
	void as_edge_host_deleteD2DStatusInfo(int rhostid);

	///deploy
	@StoredProcedure(name = "dbo.as_edge_deploy_target_delete")
	void as_edge_deploy_target_delete();
	
	@StoredProcedure(name = "dbo.as_edge_deploy_target_update_credential")
	void as_edge_deploy_target_update_credential(@In(jdbcType = Types.VARCHAR) String serverName,
			@In(jdbcType = Types.VARCHAR) String username,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String password);

	@StoredProcedure(name = "dbo.as_edge_deploy_target_update_nodeID")
	void as_edge_deploy_target_update_nodeID(String serverName, int hostID);
	
	@StoredProcedure(name = "dbo.as_edge_deploy_target_update_planIds")
	void as_edge_deploy_target_update_planIds(int targetId ,
			@In(jdbcType = Types.VARCHAR) String planIds);
//end deploy
	
	@StoredProcedure(name = "dbo.as_edge_host_get_groups_by_hostId")
	void as_edge_host_get_groups_by_hostId(int id,@ResultSet List<NodeGroup> groups);

	@StoredProcedure(name = "dbo.as_edge_policy_get_nodes_for_policy_assign")
	void as_edge_policy_get_nodes_for_policy_assign(
			int groupID, 
			int groupType,
			int policyType, 
			@WildcardConversion @In(jdbcType = Types.VARCHAR) String nodeName, 
			int startpos, 
			int pagesize,
			@In(jdbcType = Types.VARCHAR) String sortType, 
			@In(jdbcType = Types.VARCHAR) String sortColumn, 
			@In(jdbcType = Types.VARCHAR) String sortColumn2,
			@Out(jdbcType = Types.INTEGER) int[] totalCount,
			@ResultSet List<EdgeHost> hosts);

	@StoredProcedure(name = "dbo.as_edge_host_update_ImportFromRHA")
	void as_edge_host_update_ImportFromRHA(
			int rhostid, 
			Date lastupdated,
			@In(jdbcType = Types.VARCHAR) String rhostname,
			int isVisible, int appStatus, 
			int rhostType, 
			int protectionType,
			@Out(jdbcType = Types.INTEGER) int[] newHostId,
			@Out(jdbcType = Types.INTEGER) int[] insert);
	
//	@StoredProcedure(name = "dbo.as_edge_host_getOffsiteVCMConverters")
//	void getOffsiteVCMConverters(
//		int id,
//		@ResultSet List<EdgeOffsiteVCMConverterInfo> converters );
	
//	@StoredProcedure(name = "dbo.as_edge_host_udpateOffsiteVCMConverter")
//	void udpateOffsiteVCMConverter(
//		int id,
//		@In(jdbcType = Types.VARCHAR) String hostname,
//		int port,
//		int protocol,
//		@In(jdbcType = Types.VARCHAR) String username,
//		@EncryptSave @In(jdbcType = Types.VARCHAR) String password,
//		@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid );
	
//	@StoredProcedure(name = "dbo.as_edge_host_saveConverterUsage")
//	void saveConverterUsage(
//		int hostId,
//		int converterId );
	
//	@StoredProcedure(name = "dbo.as_edge_host_removeConverterUsage")
//	void removeConverterUsage(
//		int hostId,
//		int converterId );
	
//	@StoredProcedure(name = "dbo.as_edge_host_getConverterUsageCount")
//	void getConverterUsageCount(
//		@In(jdbcType = Types.VARCHAR) String converterUuid,
//		@Out(jdbcType = Types.INTEGER) int[] result );
	
//	@StoredProcedure(name = "dbo.as_edge_host_getConvertersUsingByHost")
//	void getConvertersUsingByHost(
//		int hostId,
//		@ResultSet List<EdgeIntegerValue> converterIdList );
		
//	@StoredProcedure(name = "dbo.as_edge_host_OffsiteVCMConverters_insert")
//	void as_edge_host_OffsiteVCMConverters_insert(
//			int id, 
//			@In(jdbcType = Types.VARCHAR) String hostName,
//			@EncryptSave String password,
//			@EncryptSave String uuid,
//			int protocol,
//			@Out(jdbcType = Types.INTEGER) int[] newConverterId,
//			@Out(jdbcType = Types.INTEGER) int[] insertConverter);
	
//	void as_edge_host_OffsiteVCMConverters_insertOrUpdate(
//			int id, 
//			@In(jdbcType = Types.VARCHAR) String hostName,
//			@In(jdbcType = Types.VARCHAR) String userName,
//			@In(jdbcType = Types.VARCHAR) String password , 
//			@In(jdbcType = Types.INTEGER) int port ,
//			@In(jdbcType = Types.INTEGER) Protocol protocal,
//			@In(jdbcType = Types.VARCHAR) String uuid, 
//			@Out(jdbcType = Types.INTEGER) int[] result);
	
	@StoredProcedure(name = "as_edge_vcm_networkConfiguration_saveOrUpdate")
	void as_edge_vcm_networkConfiguration_saveOrUpdate (
			int hostId,
			@In(jdbcType = Types.VARCHAR) String macAddress,
			@In(jdbcType = Types.VARCHAR) String virtualNetworkName,
			int isVirtualNameFromPolicy,
			@In(jdbcType = Types.VARCHAR) String nicTypeName,
			int isNICTypeFromPolicy,
			int isDHCP,
			int isKeepWithBackup,
			@In(jdbcType = Types.VARCHAR) String ipStr,
			@In(jdbcType = Types.VARCHAR) String gatewayStr,
			@In(jdbcType = Types.VARCHAR) String dnsStr,
			@In(jdbcType = Types.VARCHAR) String winsStr
			);

	@StoredProcedure(name = "as_edge_vcm_networkConfiguration_selectById")
	void as_edge_vcm_networkConfiguration_selectById (
			int hostId,
			@ResultSet List<EdgeNetworkConfiguration> networkConfigurationList
			);

	@StoredProcedure(name = "as_edge_vcm_networkConfiguration_deleteByHostId")
	void as_edge_vcm_networkConfiguration_deleteByHostId(int hostId);
	
	@StoredProcedure(name = "dbo.as_edge_host_getHostIdByUuid")
	void as_edge_host_getHostIdByUuid(@EncryptSave @In(jdbcType = Types.VARCHAR) String uuid, @In int protectiontype, @Out(jdbcType = Types.INTEGER) int[] hostId);
	
	@StoredProcedure(name = "as_edge_vcm_sourceMachineAdapter_deleteByHostId")
	void as_edge_vcm_sourceMachineAdapter_deleteByHostId (int hostId);

	@StoredProcedure(name = "as_edge_vcm_sourceMachineAdapter_insert")
	void as_edge_vcm_sourceMachineAdapter_insert (int hostId,
			@In(jdbcType = Types.VARCHAR) String adapterDesc,
			@In(jdbcType = Types.VARCHAR) String macAddress,
			int isDHCP,
			@In(jdbcType = Types.VARCHAR) String ipStr,
			@In(jdbcType = Types.VARCHAR) String gatewayStr,
			@In(jdbcType = Types.VARCHAR) String dnsStr,
			@In(jdbcType = Types.VARCHAR) String winsStr
			);

	@StoredProcedure(name = "dbo.as_edge_vcm_sourceMachineAdapter_selectById")
	void as_edge_vcm_sourceMachineAdapter_selectById (
			int hostId,
			@ResultSet List<EdgeNetworkConfiguration> networkConfigurationList
			);

	@StoredProcedure(name = "dbo.as_edge_vcm_dnsRedirectionSetting_selectById")
	void as_edge_vcm_dnsRedirectionSetting_selectById (
			int hostId,
			@ResultSet List<EdgeStandbyVMNetworkInfo> edgeStandbyVMNetworkInfoList
			);

	@StoredProcedure(name = "as_edge_vcm_dnsRedirectionSetting_save")
	void as_edge_vcm_dnsRedirectionSetting_save (
			int hostId,
			int ttl,
			int dnsServerType,
			@In(jdbcType = Types.VARCHAR) String dnsUsername,
			@EncryptSave @In(jdbcType = Types.VARCHAR) String dnsPassword,
			@In(jdbcType = Types.VARCHAR) String keyFile
			);
	
	@StoredProcedure(name = "dbo.as_edge_host_set_visible")
	void as_edge_host_set_visible(int hostId, int IsVisible);
	
	@StoredProcedure(name = "dbo.as_edge_host_list_for_srm")
	void as_edge_host_list_for_srm(int id, @ResultSet List<EdgeHost> hosts);
	

	@StoredProcedure(name = "dbo.as_edge_rps_host_list")
	void as_edge_rps_host_list(@ResultSet List<EdgeConnectInfo> rpsHosts);
	
	@StoredProcedure(name = "dbo.as_edge_host_list_node_under_proxy")
	void as_edge_host_list_node_under_proxy(int proxyId,
			@ResultSet List<EdgeHost> hosts);
	
	@StoredProcedure(name = "dbo.as_edge_host_list_proxy")
	void as_edge_host_list_proxy(@ResultSet List<EdgeHost> hosts);
	
	@StoredProcedure(name = "dbo.as_edge_proxy_by_vmhostid")
	void as_edge_proxy_by_vmhostid(int policyId, @ResultSet List<EdgeConnectInfo> proxyInfo);
	
	@StoredProcedure(name = "dbo.as_edge_proxy_by_proxyhostid")
	void as_edge_proxy_by_proxyhostid(int proxyhostid, @ResultSet List<EdgeConnectInfo> proxyInfo);
	
	@StoredProcedure(name = "dbo.as_edge_node_filter_exist")
	void as_edge_node_filter_exist(int id, String name, @Out int[] exist);
	
	@StoredProcedure(name = "dbo.as_edge_node_filter_update")
	void as_edge_node_filter_update(int id, String name, String filterXml, @Out int[] newFilterId);
	
	@StoredProcedure(name = "dbo.as_edge_filter_update")
	void as_edge_filter_update(int id, String filterXml, int type, @Out int[] newFilterId);
	
	@StoredProcedure(name = "dbo.as_edge_filter_select")
	void as_edge_filter_select(int type, @ResultSet List<EdgeFilter> filters);
	
	@StoredProcedure(name = "dbo.as_edge_filter_delete")
	void as_edge_filter_delete(int id);

	@StoredProcedure(name = "dbo.as_edge_node_latest_job_status")
	void as_edge_node_latest_job_status(int nodeId, @ResultSet List<EdgeJobHistory> jobStatusResult);
	
	@StoredProcedure(name = "dbo.as_edge_host_list_by_ids")
	void as_edge_host_list_by_ids(
			@In(jdbcType = Types.VARCHAR) String ids,
			@ResultSet List<EdgeHost> hosts);
	
	@StoredProcedure(name = "dbo.as_edge_host_list_by_ids_paging")
	void as_edge_host_list_by_ids_paging(
			@In(jdbcType = Types.VARCHAR) String ids,
			int startPos, int pageSize,
			@Out(jdbcType = Types.INTEGER) int[] totalCount,
			@ResultSet List<EdgeHost> hosts);
	
	@StoredProcedure(name = "dbo.as_edge_host_pyhsical_list_byHostname")
	void as_edge_host_pyhsical_list_byHostname(@In(jdbcType = Types.VARCHAR) String hostname,
			@ResultSet List<EdgeHost> hosts);
	
	@StoredProcedure(name = "dbo.as_edge_host_vm_by_instanceUUID")
	void as_edge_host_vm_by_instanceUUID(@In(jdbcType = Types.VARCHAR) String instanceUUID,	@Out(jdbcType = Types.INTEGER) int[] hostId);
	
	@StoredProcedure(name = "dbo.as_edge_host_list_vms_by_samehyervisor")
	void as_edge_host_list_vms_by_samehyervisor(int id, @Out(jdbcType = Types.INTEGER) int[] totalCount);
	
	@StoredProcedure(name = "dbo.as_edge_esx_list_hostnames")
	void as_edge_esx_list_hostnames(int id, @ResultSet List<EdgeStringValue> esxhost);
	
	@StoredProcedure(name = "dbo.as_edge_host_updateMachineType")
	void as_edge_host_updateMachineType(int hostId, int machineType);
	
	@StoredProcedure(name = "dbo.as_edge_protected_resource_list_by_ids")
	void as_edge_protected_resource_list_by_ids(
			@In(jdbcType = Types.VARCHAR) String nodeIds,
			@In(jdbcType = Types.VARCHAR) String groupIds,
			@ResultSet List<EdgeProtectedResource> resources);
	
	@StoredProcedure(name = "dbo.as_edge_host_getNodeSummaries")
	void as_edge_host_getNodeSummaries(String ids, @ResultSet List<NodeSummary> hosts);
	
	@StoredProcedure(name = "dbo.as_edge_host_getVmInfoSummaries")
	void as_edge_host_getVmInfoSummaries(String ids, @ResultSet List<VmInfoSummary> vmInfos);
	
	@StoredProcedure(name = "dbo.as_edge_host_getProxyInfoSummaries")
	void as_edge_host_getProxyInfoSummaries(String ids, @ResultSet List<ProxyInfoSummary> vmInfos);
	
	@StoredProcedure(name = "dbo.as_edge_host_getLinuxD2DInfoSummaries")
	void as_edge_host_getLinuxD2DInfoSummaries(String ids, @ResultSet List<LinuxD2DInfoSummary> vmInfos);
	
	@StoredProcedure(name = "dbo.as_edge_host_getArcserveInfoSummaries")
	void as_edge_host_getArcserveInfoSummaries(String ids, @ResultSet List<ArcserveInfoSummary> arcInfos);
	
	@StoredProcedure(name = "dbo.as_edge_host_getD2DInfoSummaries")
	void as_edge_host_getD2DInfoSummaries(String ids, @ResultSet List<D2DInfoSummary> d2dInfos);
	
	@StoredProcedure(name = "dbo.as_edge_host_getPlanSummaries")
	void as_edge_host_getPlanSummaries(String ids, @ResultSet List<PlanSummary> planInfos);
	
	@StoredProcedure(name = "dbo.as_edge_host_getJobSummaries")
	void as_edge_host_getJobSummaries(String ids, @ResultSet List<JobSummary> jobInfos);
	
	@StoredProcedure(name = "dbo.as_edge_host_getRemoteDeployInfoSummaries")
	void as_edge_host_getRemoteDeployInfoSummaries(String ids, @ResultSet List<RemoteDeployInfoSummary> agentDeployInfos);
	
	@StoredProcedure(name = "dbo.as_edge_host_getVcloudSummaries")
	void as_edge_host_getVcloudSummaries(String nodeIds, @ResultSet List<NodeVcloudSummary> vCloudProperties);
	
	@StoredProcedure(name = "dbo.as_edge_host_getConverterSummaries")
	void as_edge_host_getConverterSummaries(String nodeIds, @ResultSet List<ConverterSummary> converterSummaries);
	
	@StoredProcedure(name = "dbo.as_edge_host_getGatewaySummaries")
	void as_edge_host_getGatewaySummaries(String nodeIds, @ResultSet List<GatewaySummary> gatewaySummaries);
	
	@StoredProcedure(name = "dbo.as_edge_host_getVsbStatus")
	void as_edge_host_getVsbStatus(String nodeIds, @ResultSet List<D2DStatusInfo> vsbStatusInfos);
	
	// ============================== node sorting start ===========================================
	
	@StoredProcedure(name = "dbo.as_edge_host_getSortedIds")
	void as_edge_host_getSortedIds(String sortColumn, boolean isAsc, @ResultSet List<IntegerId> ids);
	
	@StoredProcedure(name = "dbo.as_edge_host_getSortedIds_by_nodename")
	void as_edge_host_getSortedIds_by_nodename(boolean isAsc, @ResultSet List<IntegerId> ids);
	
	@StoredProcedure(name = "dbo.as_edge_host_getSortedIds_by_hypervisor")
	void as_edge_host_getSortedIds_by_hypervisor(boolean isAsc, @ResultSet List<IntegerId> ids);
	
	@StoredProcedure(name = "dbo.as_edge_host_getSortedIds_by_vmname")
	void as_edge_host_getSortedIds_by_vmname(boolean isAsc, @ResultSet List<IntegerId> ids);
	
	// ============================== node sorting end ===========================================
	
	// ============================== node filters start ===========================================
	
	@StoredProcedure(name = "dbo.as_edge_host_getIdsByFilter")
	void as_edge_host_getIdsByFilter(@WildcardConversion String nodeNamePattern, int appBitmap, int osBitmap, int hostTypeBitmap, @ResultSet List<IntegerId> ids);
	
	@StoredProcedure(name = "dbo.as_edge_host_getIdsByJobStatus")
	void as_edge_host_getIdsByJobStatus(int jobStatusBitmap, @ResultSet List<IntegerId> ids);
	
	@StoredProcedure(name = "dbo.as_edge_host_getIdsByProtectionType")
	void as_edge_host_getIdsByProtectionType(int protectionTypeBitmap, @ResultSet List<IntegerId> ids);
	
	@StoredProcedure(name = "dbo.as_edge_host_getIdsByNodeStatus")
	void as_edge_host_getIdsByNodeStatus(int nodeStatusBitmap, @ResultSet List<IntegerId> ids);
	
	@StoredProcedure(name = "dbo.as_edge_host_getIdsByRemoteDeployStatus")
	void as_edge_host_getIdsByRemoteDeployStatus(int deployStatusBitmap, String deployStatusParameter, @ResultSet List<IntegerId> ids);
	
	@StoredProcedure(name = "dbo.as_edge_host_getIdsByNotNullField")
	void as_edge_host_getIdsByNotNullField(int notNullFieldBitmap, @ResultSet List<IntegerId> ids);
	
	@StoredProcedure(name = "dbo.as_edge_host_getIdsByLastBackupStatus")
	void as_edge_host_getIdsByLastBackupStatus(int lastBackupStatusBitmap, @ResultSet List<IntegerId> ids);
	
	@StoredProcedure(name = "dbo.as_edge_host_getIdsBygateway")
	void as_edge_host_getIdsBygateway(int gatewayId, @ResultSet List<IntegerId> ids);
	
	
	// ============================== node filters end ===========================================
	
	// ============================== node group start ===========================================
	
	@StoredProcedure(name = "dbo.as_edge_host_getIdsByGroup")
	void as_edge_host_getIdsByGroup(int groupType, int groupId, @ResultSet List<IntegerId> ids);
	
	// ============================== node group end ===========================================

	@StoredProcedure(name = "dbo.as_edge_node_linux_exist")
	void as_edge_node_linux_exist(@Out int[] exist);
	
	@StoredProcedure(name = "as_edge_deploy_target_update_reboot_status")
	void as_edge_deploy_target_update_reboot_status(@In(jdbcType = Types.VARCHAR) String hostname, 
			@In(jdbcType = Types.VARCHAR) String ipAddress, int status, @ResultSet List<DeployTargetDetail> targets );
	
	@StoredProcedure(name = "dbo.as_edge_host_GetHostByUUID")
	void getHostByUUID(
		@EncryptSave @In(jdbcType = Types.VARCHAR) String d2dUuid,
		@ResultSet List<EdgeD2DHost> hostList );
	
	@StoredProcedure(name = "dbo.as_edge_deploy_target_list_by_nodeId")
	void as_edge_deploy_target_list_by_nodeId(
		@In(jdbcType = Types.INTEGER) int nodeId,
		@ResultSet List<DeployTargetDetail> targets);
	
	@StoredProcedure(name = "dbo.as_edge_deploy_target_delete_by_id")
	void as_edge_deploy_target_delete_by_id(int hostID);
	
	@StoredProcedure(name = "dbo.as_edge_cancel_deployment_update_status")
	void as_edge_cancel_deployment_update_status(int hostId, String errorMessage);
	
	@StoredProcedure(name = "dbo.as_edge_getVmHostId_ByVmNameAndEsxName")
	void as_edge_getVmHostId_ByVmNameAndEsxName(@In(jdbcType = Types.VARCHAR) String vmname, @In(jdbcType = Types.VARCHAR) String esxname, @Out(jdbcType = Types.INTEGER) int[] hostId);
	
	@StoredProcedure(name = "dbo.as_edge_host_getNodeExportEntity")
	void as_edge_host_getNodeExportEntity(String ids, @ResultSet List<NodeExportEntity> hosts);
	
	@StoredProcedure(name = "dbo.as_edge_host_getIdsByHostName")
	void as_edge_host_getIdsByHostName(String hostName, @ResultSet List<IntegerId> ids);
}

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
import com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport.AdEntity;

public interface IEdgeAdDao {
	
	public static final int AD_HOST_STATUS_VISIBLE = 1;
	public static final int AD_HOST_STATUS_INVISIBLE = 2;
	
	@StoredProcedure(name = "dbo.as_edge_ad_getById")
	void as_edge_ad_getById(int id, @ResultSet List<EdgeAD> adList);
	
	@StoredProcedure(name = "dbo.as_edge_ad_getByGatewayId")
	void as_edge_ad_getByGatewayId(int gatewayId, @ResultSet List<EdgeAD> adList);
	
	@StoredProcedure(name = "dbo.as_edge_ad_get_auto_discovery")
	void as_edge_ad_get_auto_discovery(@ResultSet List<EdgeAD> adList);
	
	@StoredProcedure(name = "dbo.as_edge_ad_isExist")
	void as_edge_ad_isExist(String username, String filter, 
			@Out(jdbcType = Types.INTEGER) int[] isExist);
	
	@StoredProcedure(name = "dbo.as_edge_ad_update")
	void as_edge_ad_update(int id,
			@In(jdbcType = Types.NVARCHAR) String username,
			@EncryptSave @In(jdbcType = Types.NVARCHAR) String password,
			@In(jdbcType = Types.NVARCHAR) String filter,
			@In(jdbcType = Types.NVARCHAR) String domainControler,
			@Out(jdbcType = Types.INTEGER) int[] newId);
	
	@StoredProcedure(name = "dbo.as_edge_ad_delete")
	void as_edge_ad_delete(int id);
	
	@StoredProcedure(name = "dbo.as_edge_ad_update_auto_discovery_flag")
	void as_edge_ad_update_auto_discovery_flag(int id, int isAutoDiscovery);
	
	@StoredProcedure(name = "dbo.as_edge_ad_host_map_add")
	void as_edge_ad_host_map_add(int adId, int hostId, int status);
	
	@StoredProcedure(name = "dbo.as_edge_ad_host_map_delete")
	void as_edge_ad_host_map_delete(int adId, int hostId, int status);
	
	@StoredProcedure(name = "dbo.as_edge_ad_host_map_isExist")
	void as_edge_ad_host_map_isExist(int adId, int hostId,
			@Out(jdbcType = Types.INTEGER) int[] isExist);
	
	@StoredProcedure(name = "dbo.as_edge_ad_host_map_updateStatus")
	void as_edge_ad_host_map_updateStatus(int adId, int hostId, int status);
	
	@StoredProcedure(name = "dbo.as_edge_host_getListFromADDiscoveryResult")
	void as_edge_host_getListFromADDiscoveryResult(
			@WildcardConversion @In(jdbcType = Types.VARCHAR) String hostnamePattern,
			@WildcardConversion @In(jdbcType = Types.VARCHAR) String domainPattern,
			int visible, int status, int startIndex, int count,
			int sortColumn, boolean isASC,
			@Out(jdbcType = Types.INTEGER) int[] totalCount,
			@ResultSet List<EdgeADHost> hostList);

	@StoredProcedure(name = "dbo.as_edge_host_update_For_AD")
	void as_edge_host_update_For_AD(
			int adId,
			@In(jdbcType = Types.TIMESTAMP) Date lastupdated,
			@In(jdbcType = Types.VARCHAR) String rhostname,
			@In(jdbcType = Types.VARCHAR) String domainname,
			@In(jdbcType = Types.VARCHAR) String ipaddress,
			@In(jdbcType = Types.VARCHAR) String osdesc,
			int appStatus,
			@In(jdbcType = Types.VARCHAR) String ServerPrincipalName,
			@Out(jdbcType = Types.INTEGER) int[] isExist,
			@Out(jdbcType = Types.INTEGER) int[] hostId);
	@StoredProcedure(name = "dbo.as_edge_save_ad_discovery_result")
	void as_edge_save_ad_discovery_result(
			int relatedId,
			int discoveryType,
			int jobStatus,
			@In(jdbcType = Types.TIMESTAMP) Date startTime,
			@In(jdbcType = Types.TIMESTAMP) Date endTime,
			int result);
	@StoredProcedure(name = "dbo.as_edge_discovery_list")
	void as_edge_discovery_list(@ResultSet List<EdgeDiscoveryItem> list);
	@StoredProcedure(name = "dbo.as_edge_truncate_ad_result")
	void as_edge_truncate_ad_result();
	@StoredProcedure(name = "dbo.as_edge_insert_ad_result")
	void as_edge_insert_ad_result(int nodeId);
	@StoredProcedure(name = "dbo.as_edge_getDiscoveryADResult")
	void as_edge_getDiscoveryADResult(
			@WildcardConversion @In(jdbcType = Types.VARCHAR) String hostnamePattern,
			@WildcardConversion @In(jdbcType = Types.VARCHAR) String domainPattern,
			int visible, int status, int startIndex, int count,
			int sortColumn, boolean isASC,
			@Out(jdbcType = Types.INTEGER) int[] totalCount,
			@ResultSet List<EdgeADHost> hostList);
	@StoredProcedure(name = "dbo.as_edge_getADSByIds")
	void as_edge_getADSByIds(String ids,
			@ResultSet List<AdEntity> ads);
	
	@StoredProcedure(name = "dbo.as_edge_ad_host_map_getNodeIdsbyadId")
	void as_edge_ad_host_map_getNodeIdsbyadId(int adId, @ResultSet List<IntegerId> nodeIds);
	
	@StoredProcedure(name = "dbo.as_edge_host_delete_unvisible_node")
	void as_edge_host_delete_unvisible_node(int nodeId, @Out(jdbcType = Types.INTEGER) int[] isVisible);
}

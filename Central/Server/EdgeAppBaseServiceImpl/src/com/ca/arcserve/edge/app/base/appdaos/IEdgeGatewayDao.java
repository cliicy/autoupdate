package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport.SiteEntity;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;

public interface IEdgeGatewayDao {
	
	@StoredProcedure(name = "as_edge_gateway_add")
	void as_edge_gateway_add(String name, String description, String uuid, @Out(jdbcType = Types.INTEGER) int[] newGatewayId);
	
	@StoredProcedure(name = "as_edge_gateway_update")
	void as_edge_gateway_update(int id, String name, String description, String uuid);
	
	@StoredProcedure(name = "as_edge_gateway_delete")
	void as_edge_gateway_delete(int id);
	
	@StoredProcedure(name = "as_edge_gateway_getById")
	void as_edge_gateway_getById(int id, @ResultSet List<EdgeGatewayEntity> gateways);
	
	@StoredProcedure(name = "as_edge_gateway_entity_map_addOrUpdate")
	void as_edge_gateway_entity_map_addOrUpdate(int gatewayId, int entityId, EntityType entityType);
	
	@StoredProcedure(name = "as_edge_gateway_entity_map_delete")
	void as_edge_gateway_entity_map_delete(int entityId, EntityType entityType);
	
	@StoredProcedure(name = "as_edge_gateway_entity_map_getGatewayId")
	void as_edge_gateway_entity_map_getGatewayId(int entityId, EntityType entityType, @ResultSet List<IntegerId> gatewayIds);
	
	@StoredProcedure(name = "as_edge_gateway_CreateSite")
	void createSite(
		String name,
		String description,
		String gatewayUuid,
		int isLocal,
		String address,
		String email,
		int heartbeatInterval,
		@Out(jdbcType = Types.INTEGER) int[] newSiteId );
	
	@StoredProcedure(name = "as_edge_gateway_UpdateSite")
	void updateSite(
		int id,
		String name,
		String description,
		String address,
		String email,
		String consoleHostName,
		int consoleProtocol,
		int consoleProt,
		int gatewayProtocol,
		int gatewayProt,
		String gatewayUsername,
		@EncryptSave @In(jdbcType = Types.VARCHAR) String gatewayPassword,
		String registrationText,
		int heartbearInterval);
	
	@StoredProcedure(name = "as_edge_gateway_DeleteSite")
	void deleteSite(
		int id );
	
	@StoredProcedure(name = "as_edge_gateway_GetSiteInfo")
	void getSiteInfo(
		int id,
		@ResultSet List<EdgeSiteInfo> siteInfoList );
	
	@StoredProcedure(name = "as_edge_gateway_QuerySites")
	void querySites(
		String namePattern,
		@ResultSet List<EdgeSiteInfo> siteInfoList );
	
	@StoredProcedure(name = "as_edge_gateway_PageQuerySites")
	void pageQuerySites(
		int isValid, 
		String namePattern,
		int startPos, 
		int pageSize, 
		@In(jdbcType = Types.VARCHAR) String sortOrder,
		@In(jdbcType = Types.VARCHAR) String sortColumn,
		@Out(jdbcType = Types.INTEGER) int[] totalCount,
		@ResultSet List<EdgeSiteInfo> siteInfoList );
	
	@StoredProcedure(name = "as_edge_gateway_GetSiteByGatewayId")
	void getSiteByGatewayId(
		int gatewayId,
		@ResultSet List<IntegerId> siteIdList );
	
	@StoredProcedure(name = "as_edge_gateway_GetGatewayReferenceCount")
	void getGatewayReferenceCount(
			int id,
			@Out(jdbcType = Types.INTEGER) int[] refCount );

	@StoredProcedure(name = "as_edge_gateway_GetGatewayEntitytypeReferenceCount")
	void getGatewayEntityTypeReferenceCount(
			int id,int type,
			@Out(jdbcType = Types.INTEGER) int[] refCount );
	
	@StoredProcedure(name = "as_edge_gateway_GetGatewayByUuid")
	void getGatewayByUuid(
		String gatewayUuid,
		@ResultSet List<EdgeGatewayEntity> gatewayList );
	
	@StoredProcedure(name = "as_edge_gateway_GetLocalGateway")
	void getLocalGateway(
		@ResultSet List<EdgeGatewayEntity> gatewayList
		);
	
	@StoredProcedure(name = "as_edge_gateway_RegisterGatewayHost")
	void registerGatewayHost(
		int id,
		String hostName,
		String hostUuid );
	
	@StoredProcedure(name = "as_edge_gateway_UpdateGatewayHostLastContactTime")
	void updateGatewayHostLastContactTime(
		int gatewayId,
		@In(jdbcType = Types.TIMESTAMP) Date lastHeartbeatTime );
	
	@StoredProcedure(name = "as_edge_gateway_UpdateGatewayHostVersion")
	void updateGatewayHostVersion(
		int gatewayId,
		String hostVersion );
	
	@StoredProcedure(name = "as_edge_gateway_GetEntitiesDidntBindToGateway")
	void getEntitiesDidntBindToGateway(
		int entityType,
		@ResultSet List<IntegerId> idList );

	@StoredProcedure(name = "as_edge_gateway_GetEntitiesDidntExisting")
	void getEntitiesDidntExisting(
			int entityType,
			int gatewayId,
			@ResultSet List<IntegerId> idList );

	@StoredProcedure(name = "as_edge_gateway_GetVisibleEntities")
	void getVisibleEntities(
			int entityType,
			int gatewayId,
			@ResultSet List<IntegerId> idList );

	@StoredProcedure(name = "as_edge_gateway_GetGateWayAndSite")
	void as_edge_gateway_GetGateWayAndSite(
		String ids,
		@ResultSet List<SiteEntity> sites );
	
	@StoredProcedure(name = "as_edge_gateway_SetGatewayUpdateStatus")
	void setGatewayUpdateStatus(
		int gatewayId,
		int updateStatus,
		String detailedMessage,
		@In(jdbcType = Types.TIMESTAMP) Date updateStartTime,
		int hasUpdateStartTime, // 0: false, 1: true
		@In(jdbcType = Types.TIMESTAMP) Date lastReportStatusTime
		);
	
	@StoredProcedure(name = "as_edge_gateway_GetGatewayUpdateStatus")
	void getGatewayUpdateStatus(
		int gatewayId,
		@ResultSet List<EdgeGatewayUpdateStatus> statusList );
	
	@StoredProcedure(name = "as_edge_gateway_GetAllGatewayUpdateStatus")
	void getAllGatewayUpdateStatus(
		@ResultSet List<EdgeGatewayUpdateStatus> statusList );
	
	@StoredProcedure(name = "as_edge_gateway_DeleteGatewayUpdateStatus")
	void deleteGatewayUpdateStatus(
		int gatewayId );
}

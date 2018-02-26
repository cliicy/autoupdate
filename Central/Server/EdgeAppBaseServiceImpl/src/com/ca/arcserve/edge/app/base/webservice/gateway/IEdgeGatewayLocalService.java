package com.ca.arcserve.edge.app.base.webservice.gateway;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.IEdgeGatewayService;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteId;

public interface IEdgeGatewayLocalService extends IEdgeGatewayService {
	
	void addNode(GatewayId gatewayId, int hostId) throws EdgeServiceFault;
	
	void bindEntity(GatewayId gatewayId, int entityId, EntityType entityType);
	
	void unbindEntity(int entityId, EntityType entityType) throws EdgeServiceFault;
	
	GatewayEntity getGatewayByHostId(int hostId) throws EdgeServiceFault;
	
	GatewayEntity getGatewayByEntityId(int entityId, EntityType entityType) throws EdgeServiceFault;
	
	GatewayEntity getLocalGateway();
	
	//GatewayEntity getDefaultGateway();
	
	void ensureLocalSite() throws Exception;
	
	boolean validateMessageServiceCredential( String username, String password );
	
	String getMqBrokerUsername();
	
	String getMqBrokerPassword();
	
	boolean isGatewayRegistered( GatewayEntity gateway );
	
	void updateLastContactTime( GatewayId gatewayId );
	
	SiteId getSiteIdByGatewayId( GatewayId gatewayId );
	
	void checkFirstRuns();
	
	void checkGatewayVersions();
}

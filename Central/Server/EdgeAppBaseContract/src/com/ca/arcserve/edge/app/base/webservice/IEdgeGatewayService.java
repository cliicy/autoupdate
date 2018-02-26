package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;

import javax.jws.WebService;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Version;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayHostHeartbeatParam;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayHostHeartbeatResponse2;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayLoginInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayRegistrationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayUnregistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayUpdateStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayUpdatesInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GenerateGatewayRegStrParam;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteId;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SitePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SitePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailServerSetting;

@WebService(targetNamespace="http://webservice.edge.arcserve.ca.com/")
public interface IEdgeGatewayService {
	
	/**
	 * If gateway ID in entity is invalid, try to add new gateway. If successful, the
	 * gateway ID in result is for the new gateway, otherwise, the it's invalid.
	 * <p>
	 * If the gateway ID in entity is valid, try to update the gateway. The gateway
	 * ID in result will always be invalid.
	 * <p>
	 * The result will always contains the UUID of the console.
	 * 
	 * @param	entity	The gateway entity object contains the gateway information.
	 * @return	
	 * @throws	EdgeServiceFault
	 */
	GatewayRegistrationResult addOrUpdateGateway(GatewayEntity entity) throws EdgeServiceFault;
	
	void deleteGateway(List<Integer> gatewayIds) throws EdgeServiceFault;
	
	PagingResult<GatewayEntity> getPagingGateway(PagingConfig config) throws EdgeServiceFault;
	List<GatewayEntity> getAllGateways() throws EdgeServiceFault;
	List<GatewayEntity> getAllValidGateways() throws EdgeServiceFault;
	GatewayEntity getGatewayById(GatewayId gatewayId) throws EdgeServiceFault;
	GatewayEntity getLocalGateway() throws EdgeServiceFault;
	
	SiteId createSite( SiteInfo siteInfo ) throws EdgeServiceFault;
	void updateSite( SiteId siteId, SiteInfo siteInfo ) throws EdgeServiceFault;
	void deleteSite( SiteId siteId ) throws EdgeServiceFault;
	SiteInfo getSite( SiteId siteId ) throws EdgeServiceFault;
	List<SiteInfo> querySites( SiteFilter filter ) throws EdgeServiceFault;
	SitePagingResult pageQuerySites( SiteFilter filter, SitePagingConfig loadConfig ) throws EdgeServiceFault;
	
	String generateGatewayRegistrationString(
		GenerateGatewayRegStrParam param
		) throws EdgeServiceFault;
	Boolean sendRegistrationEmail( SiteInfo siteInfo, EmailServerSetting setting) throws EdgeServiceFault;
	Integer sendRegistrationEmails(List<SiteId> siteParams, String consoleURL)throws EdgeServiceFault;
	/**
	 * Bind a machine as a gateway role.
	 * 
	 * @param	regInfo
	 * @return
	 * @throws	EdgeServiceFault
	 */
	GatewayRegistrationResult registerGatewayHost(
		GatewayRegistrationInfo regInfo
		) throws EdgeServiceFault;
	
	void unregisterGatewayHost(
		GatewayUnregistrationInfo unregInfo
		) throws EdgeServiceFault;
	
	GatewayConnectInfo gatewayLogin(
		GatewayLoginInfo loginInfo
		) throws EdgeServiceFault;
	
	/**
	 * This API is only for the gateway host. After the gateway tool
	 * successfully registered the gateway host to console, it will
	 * invoke this API to notify the service.
	 * 
	 * @param	regResult
	 * @throws	EdgeServiceFault
	 */
	void onGatewayHostRegistered(
		GatewayRegistrationResult regResult
		) throws EdgeServiceFault;
	
	/**
	 * This API is used by a gateway host to send heartbeat.
	 * 
	 * @param	param
	 * 			The parameters for the heartbeat, including gateway UUID and
	 * 			the UUID of the gateway host.
	 * @return	
	 * @throws	EdgeServiceFault
	 */
	GatewayHostHeartbeatResponse2 gatewayHostHeartbeat(
		GatewayHostHeartbeatParam param
		) throws EdgeServiceFault;
	
	/**
	 * After gateway host complete initialization and become ready, it should
	 * invoke this API to notify console it's ready.
	 * 
	 * @throws EdgeServiceFault
	 */
	void announceGatewayHostReady(
		String gatewayUuid,
		String gatewayHostUuid
		) throws EdgeServiceFault;
	
	void doGatewayUpdate(
		List<GatewayId> gatewayIdList
		) throws EdgeServiceFault;
	
	GatewayUpdatesInfo getGatewayUpdateInfo(
		Version gatewayVersion
		) throws EdgeServiceFault;
	
	void reportGatewayUpdateStatus(
		GatewayId gatewayId,
		GatewayUpdateStatus statusInfo
		) throws EdgeServiceFault;
	
	GatewayId getGatewayIdByUuid(
		String gatewayUuid
		) throws EdgeServiceFault;
	
	void updateRegInfoOfExistingNode( GatewayId gatewayId, int nodeId ) throws EdgeServiceFault;
}

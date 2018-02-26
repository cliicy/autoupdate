package com.ca.arcserve.edge.app.base.common.connection;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.AuthUuidWrapper;
import com.ca.arcserve.edge.app.base.appdaos.EdgeD2DHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVCMDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;

public class VMProxyConnectionContextProvider implements IConnectionContextProvider {
	
	private IEdgeVCMDao vsphereDao = DaoFactory.getDao(IEdgeVCMDao.class);
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	
	private EdgeD2DHost proxy;
	
	public VMProxyConnectionContextProvider(EdgeD2DHost proxy) {
		this.proxy = proxy;
	}

	@Override
	public ConnectionContext create() throws EdgeServiceFault {
		List<AuthUuidWrapper> wrappers = new ArrayList<AuthUuidWrapper>();
		vsphereDao.as_edge_proxy_connect_info_getAuthUuid(proxy.getUuid(), wrappers);
		
		String protocol = proxy.getProtocol() == 2 ? "https" : "http";
		ConnectionContext context = new ConnectionContext(protocol, proxy.getRhostname(), proxy.getPort());
		
		if (!wrappers.isEmpty()) {
			context.buildCredential(wrappers.get(0).getUsername(), wrappers.get(0).getPassword(), "");
			context.buildAuthUuid(wrappers.get(0).getAuthUuid());
		}
		
		GatewayEntity gateway = gatewayService.getGatewayByHostId(proxy.getRhostid());
		context.setGateway(gateway);
		
		return context;
	}

	@Override
	public void updateUuid(String nodeUuid, String authUuid) {
	}

}

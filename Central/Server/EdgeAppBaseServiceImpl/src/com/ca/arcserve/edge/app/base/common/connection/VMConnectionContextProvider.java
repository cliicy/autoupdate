package com.ca.arcserve.edge.app.base.common.connection;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.EdgeVSphereProxyInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVCMDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;

public class VMConnectionContextProvider implements IConnectionContextProvider {
	
	private IEdgeVCMDao vcmDao = DaoFactory.getDao(IEdgeVCMDao.class);
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	
	private int vmHostId;
	
	public VMConnectionContextProvider(int vmHostId) {
		this.vmHostId = vmHostId;
	}

	@Override
	public ConnectionContext create() throws EdgeServiceFault {
		List<EdgeVSphereProxyInfo> proxyList = new ArrayList<EdgeVSphereProxyInfo>();
		vcmDao.as_edge_vsphere_proxy_getByHostId(vmHostId, proxyList);
		
		if (proxyList.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Backup_ProxyNotFound, "Cannot find the backup proxy information for VM, host id = " + vmHostId);
		}
		
		EdgeVSphereProxyInfo proxy = proxyList.get(0);
		
		String protocol = proxy.getProtocol() == Protocol.Https.ordinal() ? "https" : "http";
		ConnectionContext context = new ConnectionContext(protocol, proxy.getHostname(), proxy.getPort());
		context.buildCredential(proxy.getUsername(), proxy.getPassword(), "");
		context.buildAuthUuid(proxy.getUuid());
		
		GatewayEntity gateway = gatewayService.getGatewayByEntityId(proxy.getId(), EntityType.Node);
		context.setGateway(gateway);
		
		return context;
	}

	@Override
	public void updateUuid(String nodeUuid, String authUuid) {
	}

}

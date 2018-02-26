package com.ca.arcserve.edge.app.base.common.connection;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.INodeService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;

public class MonitorConnectionContextProvider implements IConnectionContextProvider {
	private int hostId;
	private INodeService nodeService;
	
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	
	public MonitorConnectionContextProvider(int hostId) {
		this(hostId, new NodeServiceImpl());
	}
	
	public MonitorConnectionContextProvider(int hostId, INodeService nodeService) {
		this.hostId = hostId;
		this.nodeService = nodeService;
	}

	@Override
	public ConnectionContext create() throws EdgeServiceFault {
		HostConnectInfo info = nodeService.getMonitorConnectInfoByHostId(hostId);
		
		String protocol = info.getProtocol() == Protocol.Https ? "https" : "http";
		ConnectionContext context = new ConnectionContext(protocol, info.getHostName(), info.getPort());
		context.buildCredential(info.getUserName(), info.getPassword(), "");
		context.buildAuthUuid(info.getAuthUuid());
		
		GatewayEntity gateway = gatewayService.getGatewayById(info.getGatewayId());
		context.setGateway(gateway);
		
		return context;
	}

	@Override
	public void updateUuid(String nodeUuid, String authUuid) {
	}
}

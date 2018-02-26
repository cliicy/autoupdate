package com.ca.arcserve.edge.app.base.webservice.node;

import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public class DefaultRemoteNodeServiceFactory implements IRemoteNodeServiceFactory
{
	@Override
	public IRemoteNodeService createRemoteNodeService( GatewayId gatewayId )
	{
		return new RemoteNodeServiceImpl();
	}
}

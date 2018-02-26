package com.ca.arcserve.edge.app.base.webservice.node;

import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public interface IRemoteNodeServiceFactory
{
	IRemoteNodeService createRemoteNodeService( GatewayId gatewayId );
}

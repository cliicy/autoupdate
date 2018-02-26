package com.ca.arcserve.edge.app.base.webservice.gateway;

import com.ca.arcserve.edge.app.base.webservice.IGatewayHostService;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public interface IGatewayHostServiceFactory
{
	IGatewayHostService createGatewayHostService( GatewayId gatewayId );
}

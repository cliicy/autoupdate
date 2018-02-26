package com.ca.arcserve.edge.app.base.common.eventcenter.events;

import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;

public class GatewayHostRegisteredEventForConsole extends BaseEdgeEvent
{
	private GatewayEntity gateway;
	
	public GatewayHostRegisteredEventForConsole( GatewayEntity gateway )
	{
		if (gateway == null)
			gateway = new GatewayEntity();
		this.gateway = gateway;
	}

	public GatewayEntity getGateway()
	{
		return gateway;
	}

}

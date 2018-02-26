package com.ca.arcserve.edge.app.base.common.eventcenter.events;

public class GatewayHostRegisteredEvent extends BaseEdgeEvent
{
	private String consoleUuid = "";
	private String gatewayUuid = "";
	
	public GatewayHostRegisteredEvent( String consoleUuid, String gatewayUuid )
	{
		this.consoleUuid = consoleUuid;
		this.gatewayUuid = gatewayUuid;
	}
	
	public String getConsoleUuid()
	{
		return consoleUuid;
	}
	
	public String getGatewayUuid()
	{
		return gatewayUuid;
	}
}

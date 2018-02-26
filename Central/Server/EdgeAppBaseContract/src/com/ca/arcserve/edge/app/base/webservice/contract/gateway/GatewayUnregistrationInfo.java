package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import java.io.Serializable;

public class GatewayUnregistrationInfo implements Serializable
{
	private static final long serialVersionUID = 267781484079683148L;

	private String consoleUuid;
	private String gatewayUuid;
	private String hostUuid;

	public String getConsoleUuid()
	{
		return consoleUuid;
	}

	public void setConsoleUuid( String consoleUuid )
	{
		this.consoleUuid = consoleUuid;
	}

	public String getGatewayUuid()
	{
		return gatewayUuid;
	}

	public void setGatewayUuid( String gatewayUuid )
	{
		this.gatewayUuid = gatewayUuid;
	}

	public String getHostUuid()
	{
		return hostUuid;
	}

	public void setHostUuid( String hostUuid )
	{
		this.hostUuid = hostUuid;
	}
}

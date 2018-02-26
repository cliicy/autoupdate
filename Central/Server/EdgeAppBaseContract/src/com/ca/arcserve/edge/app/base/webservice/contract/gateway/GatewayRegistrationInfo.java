package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import java.io.Serializable;

public class GatewayRegistrationInfo implements Serializable
{
	private static final long serialVersionUID = 5500848373735141368L;
	
	private String consoleUuid;
	private String gatewayUuid;
	private String hostName;
	private String hostUuid;
	private boolean overwriteOld = false;

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

	public String getHostName()
	{
		return hostName;
	}

	public void setHostName( String hostName )
	{
		this.hostName = hostName;
	}

	public String getHostUuid()
	{
		return hostUuid;
	}

	public void setHostUuid( String hostUuid )
	{
		this.hostUuid = hostUuid;
	}

	public boolean isOverwriteOld()
	{
		return overwriteOld;
	}

	public void setOverwriteOld( boolean overwriteOld )
	{
		this.overwriteOld = overwriteOld;
	}
}

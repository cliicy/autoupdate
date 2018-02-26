package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSimpleVersion;

public class GatewayHostHeartbeatResponse2 implements Serializable
{
	private static final long serialVersionUID = -4324178868892051931L;

	private boolean needToUpdate;
	private EdgeSimpleVersion consoleVersion;
	
	public boolean isNeedToUpdate()
	{
		return needToUpdate;
	}
	
	public void setNeedToUpdate( boolean needToUpdate )
	{
		this.needToUpdate = needToUpdate;
	}
	
	public EdgeSimpleVersion getConsoleVersion()
	{
		return consoleVersion;
	}
	
	public void setConsoleVersion( EdgeSimpleVersion consoleVersion )
	{
		this.consoleVersion = consoleVersion;
	}
}

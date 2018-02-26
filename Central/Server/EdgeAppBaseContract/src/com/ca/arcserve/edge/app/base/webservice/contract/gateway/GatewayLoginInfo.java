package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSimpleVersion;

public class GatewayLoginInfo implements Serializable
{
	private static final long serialVersionUID = 3462158526559082329L;

	private String consoleUuid;
	private String gatewayUuid;
	private String hostUuid;
	private EdgeSimpleVersion hostVersion;
	private boolean hasRebootFlag;
	
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

	public EdgeSimpleVersion getHostVersion()
	{
		return hostVersion;
	}

	public void setHostVersion( EdgeSimpleVersion hostVersion )
	{
		this.hostVersion = hostVersion;
	}
	
	private String getPrintableString( String string )
	{
		if (string == null)
			return "null";
		
		return "'" + string + "'";
	}
	
	public boolean isHasRebootFlag()
	{
		return hasRebootFlag;
	}

	public void setHasRebootFlag( boolean hasRebootFlag )
	{
		this.hasRebootFlag = hasRebootFlag;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( this.getClass().getSimpleName() + " { " );
		sb.append( "consoleUuid = " + getPrintableString( consoleUuid ) );
		sb.append( ", gatewayUuid = " + getPrintableString( gatewayUuid ) );
		sb.append( ", hostUuid = " + getPrintableString( hostUuid ) );
		sb.append( ", hostVersion = '" + ((hostVersion == null) ? "null" : hostVersion.toVersionString()) + "'" );
		sb.append( ", hasRebootFlag = " + hasRebootFlag );
		sb.append( " }" );
		return sb.toString();
	}
}

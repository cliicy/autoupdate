package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSimpleVersion;

public class GatewayHostHeartbeatParam implements Serializable
{
	private static final long serialVersionUID = 4428014622823278372L;
	
	private String gatewayUuid;
	private String hostUuid;
	private EdgeSimpleVersion hostVersion;
	
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

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( this.getClass().getSimpleName() + " { " );
		sb.append( "gatewayUuid = " + gatewayUuid );
		sb.append( ", hostUuid = " + hostUuid );
		sb.append( " }" );
		return sb.toString();
	}
}

package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public class OffsiteVCMConverterInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int Id;
	private String hostname;
	private int port;
	private Protocol protocol;
	private String username;
	private String password;
	private String uuid;
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;
	
	public int getId()
	{
		return Id;
	}
	
	public void setId( int id )
	{
		Id = id;
	}
	
	public String getHostname()
	{
		return hostname;
	}
	
	public void setHostname( String hostname )
	{
		this.hostname = hostname;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort( int port )
	{
		this.port = port;
	}
	
	public Protocol getProtocol()
	{
		return protocol;
	}
	
	public void setProtocol( Protocol protocol )
	{
		this.protocol = protocol;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername( String username )
	{
		this.username = username;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setPassword( String password )
	{
		this.password = password;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public GatewayId getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(GatewayId gatewayId) {
		this.gatewayId = gatewayId;
	}
	
}

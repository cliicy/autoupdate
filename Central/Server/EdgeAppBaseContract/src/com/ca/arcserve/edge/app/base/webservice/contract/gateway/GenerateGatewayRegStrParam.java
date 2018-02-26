package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import java.io.Serializable;

public class GenerateGatewayRegStrParam implements Serializable
{
	private static final long serialVersionUID = 258134056605315179L;

	private String regSvrHostName;
	private int regSvrPort;
	private String regSvrProtocol;
	private GatewayId gatewayId;
	private String gatewayProtocol;
	private int gatewayPort;
	private String gatewayUsername;
	private String gatewayPassword;

	public String getRegSvrHostName()
	{
		return regSvrHostName;
	}

	public void setRegSvrHostName( String regSvrHostName )
	{
		this.regSvrHostName = regSvrHostName;
	}

	public int getRegSvrPort()
	{
		return regSvrPort;
	}

	public void setRegSvrPort( int regSvrPort )
	{
		this.regSvrPort = regSvrPort;
	}

	public String getRegSvrProtocol()
	{
		return regSvrProtocol;
	}

	public void setRegSvrProtocol( String regSvrProtocol )
	{
		this.regSvrProtocol = regSvrProtocol;
	}

	public GatewayId getGatewayId()
	{
		return gatewayId;
	}

	public void setGatewayId( GatewayId gatewayId )
	{
		this.gatewayId = gatewayId;
	}

	public String getGatewayProtocol()
	{
		return gatewayProtocol;
	}

	public void setGatewayProtocol( String gatewayProtocol )
	{
		this.gatewayProtocol = gatewayProtocol;
	}

	public int getGatewayPort()
	{
		return gatewayPort;
	}

	public void setGatewayPort( int gatewayPort )
	{
		this.gatewayPort = gatewayPort;
	}

	public String getGatewayUsername()
	{
		return gatewayUsername;
	}

	public void setGatewayUsername( String gatewayUsername )
	{
		this.gatewayUsername = gatewayUsername;
	}

	public String getGatewayPassword()
	{
		return gatewayPassword;
	}

	public void setGatewayPassword( String gatewayPassword )
	{
		this.gatewayPassword = gatewayPassword;
	}
}

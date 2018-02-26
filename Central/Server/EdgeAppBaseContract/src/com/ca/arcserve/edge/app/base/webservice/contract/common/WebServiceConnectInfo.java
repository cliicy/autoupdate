package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;

public class WebServiceConnectInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String hostName;
	private Protocol protocol;
	private int port;
	
	public WebServiceConnectInfo()
	{
		this( "", Protocol.Http, 0 );
	}
	
	public WebServiceConnectInfo( String hostName, Protocol protocol, int port )
	{
		this.hostName = hostName;
		this.protocol = protocol;
		this.port = port;
	}

	public String getHostName()
	{
		return hostName;
	}

	public void setHostName( String hostName )
	{
		this.hostName = hostName;
	}

	public Protocol getProtocol()
	{
		return protocol;
	}

	public void setProtocol( Protocol protocol )
	{
		this.protocol = protocol;
	}
	
	public int getPort()
	{
		return port;
	}

	public void setPort( int port )
	{
		this.port = port;
	}

	@Override
	public String toString()
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append( "WebServiceConnectInfo [" );
		strBuilder.append( "HostName: \"" + this.hostName + "\"" );
		strBuilder.append( "Protocol: " + this.protocol );
		strBuilder.append( "Port:" + this.port );
		strBuilder.append( "]" );
		return strBuilder.toString();
	}
	
	public boolean isValid()
	{
		return ((this.hostName != null) && !this.hostName.trim().isEmpty() &&
			(port > 0));
	}
}

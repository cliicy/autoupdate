package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSimpleVersion;

public class GatewayConnectInfo implements Serializable
{
	private static final long serialVersionUID = -1750739559323403465L;
	
	private String mqBrokerUrl;
	private String mqBrokerHost;
	private int mqBrokerPort;
	private String mqBrokerUsername;
	private String mqBrokerPassword;
	private String fromConsoleRequestQueue;
	private String fromConsoleResponseQueue;
	private String toConsoleRequestQueue;
	private String toConsoleResponseQueue;
	private int heartbeatInterval;
	private String siteName;
	private boolean needToUpdate;
	private EdgeSimpleVersion consoleVersion;
	
	public String getMqBrokerUrl()
	{
		return mqBrokerUrl;
	}

	public void setMqBrokerUrl( String mqBrokerUrl )
	{
		this.mqBrokerUrl = mqBrokerUrl;
	}

	public String getMqBrokerHost()
	{
		return mqBrokerHost;
	}

	public void setMqBrokerHost( String mqBrokerHost )
	{
		this.mqBrokerHost = mqBrokerHost;
	}

	public int getMqBrokerPort()
	{
		return mqBrokerPort;
	}

	public void setMqBrokerPort( int mqBrokerPort )
	{
		this.mqBrokerPort = mqBrokerPort;
	}

	public String getMqBrokerUsername()
	{
		return mqBrokerUsername;
	}

	public void setMqBrokerUsername( String mqBrokerUsername )
	{
		this.mqBrokerUsername = mqBrokerUsername;
	}

	public String getMqBrokerPassword()
	{
		return mqBrokerPassword;
	}

	public void setMqBrokerPassword( String mqBrokerPassword )
	{
		this.mqBrokerPassword = mqBrokerPassword;
	}

	public String getFromConsoleRequestQueue()
	{
		return fromConsoleRequestQueue;
	}
	
	public void setFromConsoleRequestQueue( String fromConsoleRequestQueue )
	{
		this.fromConsoleRequestQueue = fromConsoleRequestQueue;
	}
	
	public String getFromConsoleResponseQueue()
	{
		return fromConsoleResponseQueue;
	}
	
	public void setFromConsoleResponseQueue( String fromConsoleResponseQueue )
	{
		this.fromConsoleResponseQueue = fromConsoleResponseQueue;
	}
	
	public String getToConsoleRequestQueue()
	{
		return toConsoleRequestQueue;
	}
	
	public void setToConsoleRequestQueue( String toConsoleRequestQueue )
	{
		this.toConsoleRequestQueue = toConsoleRequestQueue;
	}
	
	public String getToConsoleResponseQueue()
	{
		return toConsoleResponseQueue;
	}
	
	public void setToConsoleResponseQueue( String toConsoleResponseQueue )
	{
		this.toConsoleResponseQueue = toConsoleResponseQueue;
	}
	
	public int getHeartbeatInterval()
	{
		return heartbeatInterval;
	}

	public void setHeartbeatInterval( int heartbeatInterval )
	{
		this.heartbeatInterval = heartbeatInterval;
	}

	public String getSiteName()
	{
		return siteName;
	}

	public void setSiteName( String siteName )
	{
		this.siteName = siteName;
	}

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

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( this.getClass().getSimpleName() + " { " );
		sb.append( "mqBrokerPort = " + mqBrokerPort );
		sb.append( ", mqBrokerUsername = " + mqBrokerUsername );
		sb.append( ", fromConsoleRequestQueue = " + fromConsoleRequestQueue );
		sb.append( ", fromConsoleResponseQueue = " + fromConsoleResponseQueue );
		sb.append( ", toConsoleRequestQueue = " + toConsoleRequestQueue );
		sb.append( ", toConsoleResponseQueue = " + toConsoleResponseQueue );
		sb.append( ", heartbeatInterval = " + heartbeatInterval );
		sb.append( ", needToUpdate = " + needToUpdate );
		sb.append( ", consoleVersion = " + consoleVersion );
		sb.append( " }" );
		return sb.toString();
	}
}

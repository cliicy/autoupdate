package com.ca.arcserve.edge.app.base.appdaos;

public class EdgeASBUServerExtInfo
{
	private int serverId;
	private String hostName;
	private int serverClass;
	private int domainId;

	public int getServerId()
	{
		return serverId;
	}

	public void setServerId( int serverId )
	{
		this.serverId = serverId;
	}

	public String getHostName()
	{
		return hostName;
	}

	public void setHostName( String hostName )
	{
		this.hostName = hostName;
	}

	public int getServerClass()
	{
		return serverClass;
	}

	public void setServerClass( int serverClass )
	{
		this.serverClass = serverClass;
	}

	public int getDomainId()
	{
		return domainId;
	}

	public void setDomainId( int domainId )
	{
		this.domainId = domainId;
	}
}

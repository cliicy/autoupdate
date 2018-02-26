package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

import java.io.Serializable;

public class BasicHostInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int hostId;
	private String hostName;
	
	public int getHostId()
	{
		return hostId;
	}
	
	public void setHostId( int hostId )
	{
		this.hostId = hostId;
	}
	
	public String getHostName()
	{
		return hostName;
	}
	
	public void setHostName( String hostName )
	{
		this.hostName = hostName;
	}
	
}

package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ASBUDomainTopology implements Serializable
{
	private static final long serialVersionUID = 3529569580588157214L;

	private String domainName;
	private List<ASBUServerInfo> serverList;
	
	public ASBUDomainTopology()
	{
		this.domainName = "";
		this.serverList = new ArrayList<ASBUServerInfo>();
	}
	
	public String getDomainName()
	{
		return domainName;
	}
	
	public void setDomainName( String domainName )
	{
		this.domainName = domainName;
	}
	
	public List<ASBUServerInfo> getServerList()
	{
		return serverList;
	}
	
	public void setServerList( List<ASBUServerInfo> serverList )
	{
		this.serverList = serverList;
	}

	@Override
	public String toString() {
		return "domain name is "+ domainName + " server list size is " + (serverList!=null?serverList.size():0);
	}
}

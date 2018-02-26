package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import java.io.Serializable;

public class AddASBUBackupServerParameter implements Serializable
{
	private static final long serialVersionUID = -2220807231819662424L;

	private String domainName;
	private ASBUServerInfo serverInfo;

	public String getDomainName()
	{
		return domainName;
	}

	public void setDomainName( String domainName )
	{
		this.domainName = domainName;
	}

	public ASBUServerInfo getServerInfo()
	{
		return serverInfo;
	}

	public void setServerInfo( ASBUServerInfo serverInfo )
	{
		this.serverInfo = serverInfo;
	}

}

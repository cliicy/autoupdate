package com.ca.arcserve.edge.app.base.webservice.contract.vcm;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;

public class ImportVSphereVMParameters implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String hostname;
	private int port;
	private String protocol;
	private String domain;
	private String username;
	private @NotPrintAttribute String password;
	
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
	
	public String getProtocol()
	{
		return protocol;
	}
	
	public void setProtocol( String protocol )
	{
		this.protocol = protocol;
	}
	
	public String getDomain()
	{
		return domain;
	}
	
	public void setDomain( String domain )
	{
		this.domain = domain;
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
}

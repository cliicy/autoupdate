package com.ca.arcserve.edge.app.base.appdaos;

import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EdgeOffsiteVCMConverterInfo
{
	private int Id;
	private String hostname;
	private int port;
	private int protocol;
	private String username;
	private String password;
	private String uuid;
	
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
	
	public int getProtocol()
	{
		return protocol;
	}
	
	public void setProtocol( int protocol )
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
	
	@EncryptSave
	public String getPassword()
	{
		return password;
	}
	
	public void setPassword( String password )
	{
		this.password = password;
	}

	@EncryptSave
	public String getUuid()
	{
		return uuid;
	}

	public void setUuid( String uuid )
	{
		this.uuid = uuid;
	}
	
}

package com.ca.arcserve.edge.app.base.appdaos;

import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EdgeVSphereProxyInfo
{
	String hostname;
	String username;
	String password;
	int protocol;
	int port;
	String uuid;
	int id;
	private String vmInstanceUuid;

	public String getVmInstanceUuid() {
		return vmInstanceUuid;
	}

	public void setVmInstanceUuid(String vmInstanceUuid) {
		this.vmInstanceUuid = vmInstanceUuid;
	}

	public String getHostname()
	{
		return hostname;
	}
	
	public void setHostname( String hostname )
	{
		this.hostname = hostname;
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

	public int getProtocol()
	{
		return protocol;
	}
	
	public void setProtocol( int protocol )
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
	
	@EncryptSave
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}

package com.ca.arcserve.edge.app.base.webservice.contract.vSphere;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EsxVSphere implements Serializable {
	private static final long serialVersionUID = 3552002692116609557L;
	private int id;
	private String hostname;
	private String username;
	private @NotPrintAttribute String password;
	private int protocol;
	private int port;
	private int isAutoDiscovery;
	
	public void setId(int id)
	{
		this.id = id;	
	}
	public int getId()
	{
		return this.id;	
	}
	
	public void setHostname(String hostname)
	{
		this.hostname = hostname;	
	}
	public String getHostname()
	{
		return this.hostname;	
	}
	
	public void setUsername(String username)
	{
		this.username = username;	
	}
	public String getUsername()
	{
		return this.username;	
	}
	
	public void setPassword(String password)
	{
		this.password = password;	
	}
	@EncryptSave
	public String getPassword()
	{
		return this.password;	
	}
	
	public void setProtocol(int protocol)
	{
		this.protocol = protocol;	
	}
	public int getProtocol()
	{
		return this.protocol;	
	}
	
	public void setPort(int port)
	{
		this.port = port;	
	}
	public int getPort()
	{
		return this.port;	
	}
	public int getIsAutoDiscovery() {
		return isAutoDiscovery;
	}
	public void setIsAutoDiscovery(int isAutoDiscovery) {
		this.isAutoDiscovery = isAutoDiscovery;
	}	
}

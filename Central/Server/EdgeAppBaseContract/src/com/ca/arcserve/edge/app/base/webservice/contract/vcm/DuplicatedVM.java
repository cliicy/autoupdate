package com.ca.arcserve.edge.app.base.webservice.contract.vcm;

import java.io.Serializable;

public class DuplicatedVM implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int hostId;
	private String hostname;
	private String hypervisor;
	private String vmName;
	private String vmInstanceUUID;
	
	public int getHostId()
	{
		return hostId;
	}

	public void setHostId( int hostId )
	{
		this.hostId = hostId;
	}

	public String getHostname()
	{
		return hostname;
	}
	
	public void setHostname( String hostname )
	{
		this.hostname = hostname;
	}
	
	public String getHypervisor()
	{
		return hypervisor;
	}
	
	public void setHypervisor( String hypervisor )
	{
		this.hypervisor = hypervisor;
	}
	
	public String getVMName()
	{
		return vmName;
	}
	
	public void setVMName( String vmName )
	{
		this.vmName = vmName;
	}
	
	public String getVMInstanceUUID()
	{
		return vmInstanceUUID;
	}
	
	public void setVMInstanceUUID( String vmInstanceUUID )
	{
		this.vmInstanceUUID = vmInstanceUUID;
	}
}

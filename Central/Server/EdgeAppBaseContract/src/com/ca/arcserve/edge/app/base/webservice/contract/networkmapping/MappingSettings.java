package com.ca.arcserve.edge.app.base.webservice.contract.networkmapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MappingSettings implements Serializable
{
	private static final long serialVersionUID = 1L;

	private List<HostMappings> hostMappingsList;
	
	public MappingSettings()
	{
		this.hostMappingsList = new ArrayList<HostMappings>();
	}

	public List<HostMappings> getHostMappingsList()
	{
		return hostMappingsList;
	}
}

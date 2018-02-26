package com.ca.arcserve.edge.app.base.webservice.contract.networkmapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HostMappings implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String hostName;
	private List<AdapterMapping> mappingList;
	private List<AdapterInfo> availableAdapters;
	private AdapterInfo defaultAdapter;
	
	public HostMappings()
	{
		this.hostName			= "";
		this.mappingList		= new ArrayList<AdapterMapping>();
		this.availableAdapters	= new ArrayList<AdapterInfo>();
		this.defaultAdapter		= null;
	}

	public String getHostName()
	{
		return hostName;
	}

	public void setHostName( String hostName )
	{
		this.hostName = hostName;
	}

	public List<AdapterMapping> getMappingList()
	{
		return mappingList;
	}

	public List<AdapterInfo> getAvailableAdapters()
	{
		return availableAdapters;
	}

	public AdapterInfo getDefaultAdapter()
	{
		return defaultAdapter;
	}

	public void setDefaultAdapter( AdapterInfo defaultAdapter )
	{
		this.defaultAdapter = defaultAdapter;
	}
}

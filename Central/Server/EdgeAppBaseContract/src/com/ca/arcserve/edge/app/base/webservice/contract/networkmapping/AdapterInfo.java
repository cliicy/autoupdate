package com.ca.arcserve.edge.app.base.webservice.contract.networkmapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdapterInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String adapterId;
	private String adapterName;
	private boolean isDhcpEnabled;
	private boolean isDhcpEnabledForDns;
	private String macAddress;
	private List<IPv4Address> ipAddresses;
	private List<IPv4Address> subnetMasks;
	private List<IPv4Address> gateways;
	private List<IPv4Address> dnsServers;
	private List<IPv4Address> winsServers;
	private boolean isEmptyAdapter;
	
	public AdapterInfo()
	{
		this.adapterId				= "";
		this.adapterName			= "";
		this.isDhcpEnabled			= true;
		this.isDhcpEnabledForDns	= true;
		this.macAddress				= "";
		this.ipAddresses			= new ArrayList<IPv4Address>();
		this.subnetMasks			= new ArrayList<IPv4Address>();
		this.gateways				= new ArrayList<IPv4Address>();
		this.dnsServers				= new ArrayList<IPv4Address>();
		this.winsServers			= new ArrayList<IPv4Address>();
		this.isEmptyAdapter			= false;
	}
	
	public AdapterInfo clone()
	{
		AdapterInfo newOne = new AdapterInfo();
		newOne.deepCopy( this );
		return newOne;
	}
	
	public void shallowCopy( AdapterInfo another )
	{
		this.adapterId				= another.adapterId;
		this.adapterName			= another.adapterName;
		this.isDhcpEnabled			= another.isDhcpEnabled;
		this.isDhcpEnabledForDns	= another.isDhcpEnabledForDns;
		this.macAddress				= another.macAddress;
		this.ipAddresses			= another.ipAddresses;
		this.subnetMasks			= another.subnetMasks;
		this.gateways				= another.gateways;
		this.dnsServers				= another.dnsServers;
		this.winsServers			= another.winsServers;
		this.isEmptyAdapter			= another.isEmptyAdapter;
	}
	
	public void deepCopy( AdapterInfo another )
	{
		this.adapterId				= another.adapterId;
		this.adapterName			= another.adapterName;
		this.isDhcpEnabled			= another.isDhcpEnabled;
		this.isDhcpEnabledForDns	= another.isDhcpEnabledForDns;
		this.macAddress				= another.macAddress;
		this.isEmptyAdapter			= another.isEmptyAdapter;
		
		this.ipAddresses.clear();
		this.ipAddresses.addAll( another.ipAddresses );
		
		this.subnetMasks.clear();
		this.subnetMasks.addAll( another.subnetMasks );
		
		this.gateways.clear();
		this.gateways.addAll( another.gateways );
		
		this.dnsServers.clear();
		this.dnsServers.addAll( another.dnsServers );
		
		this.winsServers.clear();
		this.winsServers.addAll( another.winsServers );
	}

	public String getAdapterId()
	{
		return adapterId;
	}

	public void setAdapterId( String adapterId )
	{
		this.adapterId = adapterId;
	}

	public String getAdapterName()
	{
		return adapterName;
	}

	public void setAdapterName( String name )
	{
		this.adapterName = name;
	}

	public boolean isDhcpEnabled()
	{
		return isDhcpEnabled;
	}

	public boolean isDhcpEnabledForDns()
	{
		return isDhcpEnabledForDns;
	}

	public void setDhcpEnabledForDns( boolean isDhcpEnabledForDns )
	{
		this.isDhcpEnabledForDns = isDhcpEnabledForDns;
	}

	public String getMacAddress()
	{
		return macAddress;
	}

	public void setMacAddress( String macAddress )
	{
		this.macAddress = macAddress;
	}

	public void setDhcpEnabled( boolean isDhcpEnabled )
	{
		this.isDhcpEnabled = isDhcpEnabled;
	}

	public List<IPv4Address> getIpAddresses()
	{
		return ipAddresses;
	}

	public List<IPv4Address> getSubnetMasks()
	{
		return subnetMasks;
	}

	public List<IPv4Address> getGateways()
	{
		return gateways;
	}

	public List<IPv4Address> getDnsServers()
	{
		return dnsServers;
	}

	public List<IPv4Address> getWinsServers()
	{
		return winsServers;
	}

	public boolean isEmptyAdapter()
	{
		return isEmptyAdapter;
	}

	public void setEmptyAdapter( boolean isEmptyAdapter )
	{
		this.isEmptyAdapter = isEmptyAdapter;
	}
}

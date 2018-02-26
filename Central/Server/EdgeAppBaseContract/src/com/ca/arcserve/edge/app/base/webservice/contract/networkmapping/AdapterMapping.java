package com.ca.arcserve.edge.app.base.webservice.contract.networkmapping;

import java.io.Serializable;

public class AdapterMapping implements Serializable
{
	private static final long serialVersionUID = 1L;

	private AdapterInfo srcAdapterSettings;
	private AdapterInfo destAdapterSettings;
	private boolean isMapped;
	private boolean useSourceSettings;
	
	public AdapterMapping()
	{
		this.srcAdapterSettings		= new AdapterInfo();
		this.destAdapterSettings	= new AdapterInfo();
		this.isMapped				= false;
		this.useSourceSettings		= true;
	}
	
	public AdapterMapping clone()
	{
		AdapterMapping newOne = new AdapterMapping();
		newOne.srcAdapterSettings	= this.srcAdapterSettings.clone();
		newOne.destAdapterSettings	= this.destAdapterSettings.clone();
		newOne.isMapped				= this.isMapped;
		newOne.useSourceSettings	= this.useSourceSettings;
		return newOne;
	}

	public AdapterInfo getSrcAdapterSettings()
	{
		return srcAdapterSettings;
	}

	public AdapterInfo getDestAdapterSettings()
	{
		return destAdapterSettings;
	}

	public void setDestAdapterSettings( AdapterInfo destAdapterSettings )
	{
		this.destAdapterSettings = destAdapterSettings;
	}

	public boolean isMapped()
	{
		return isMapped;
	}

	public void setMapped( boolean isMapped )
	{
		this.isMapped = isMapped;
	}

	public boolean isUseSourceSettings()
	{
		return useSourceSettings;
	}

	public void setUseSourceSettings( boolean useSourceSettings )
	{
		this.useSourceSettings = useSourceSettings;
	}
}

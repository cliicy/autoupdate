package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class OffsiteVCMConverterSavingStatus implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int converterId;
	private OffsiteVCMConverterEditingStatus status;
	
	public int getConverterId()
	{
		return converterId;
	}
	
	public void setConverterId( int converterId )
	{
		this.converterId = converterId;
	}
	
	public OffsiteVCMConverterEditingStatus getStatus()
	{
		return status;
	}
	
	public void setStatus( OffsiteVCMConverterEditingStatus status )
	{
		this.status = status;
	}
}

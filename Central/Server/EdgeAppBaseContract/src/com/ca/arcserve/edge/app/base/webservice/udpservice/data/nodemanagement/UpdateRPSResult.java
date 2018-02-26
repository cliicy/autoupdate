package com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement;

import java.io.Serializable;

public class UpdateRPSResult implements Serializable
{
	private static final long serialVersionUID = -4932078841231124762L;

	private boolean isSuccessful;
	private String errorCode;

	public boolean isSuccessful()
	{
		return isSuccessful;
	}

	public void setSuccessful( boolean isSuccessful )
	{
		this.isSuccessful = isSuccessful;
	}

	public String getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode( String errorCode )
	{
		this.errorCode = errorCode;
	}
	
}

package com.ca.arcserve.edge.app.base.webservice.udpservice.fault;

public class UDPServiceFaultInfo
{
	private String errorCode;
	private String errorMessage;
	private String addedData; // Additional information, will be encoded so that customer cannot
							  // understand. This may include detailed info to debug, such as
							  // stack trace.
	
	public UDPServiceFaultInfo( String errorCode, String errorMessage )
	{
		this.setErrorCode( errorCode );
		this.setErrorMessage( errorMessage );
	}

	public String getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode( String errorCode )
	{
		this.errorCode = errorCode;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage( String errorMessage )
	{
		this.errorMessage = errorMessage;
	}

	public String getAddedData()
	{
		return addedData;
	}

	public void setAddedData( String addedData )
	{
		this.addedData = addedData;
	}
	
}

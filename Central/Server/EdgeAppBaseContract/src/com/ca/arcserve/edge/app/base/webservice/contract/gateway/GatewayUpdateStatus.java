package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import com.ca.arcserve.edge.app.base.webservice.contract.common.Version;

public class GatewayUpdateStatus
{
	private GatewayUpdateStatusCode statusCode;
	private int errorCode;
	private GatewayUpdateMessageType messageType;
	private String detailedMessage;
	private Version gatewayVersion; // this will be used to report new gateway version when upgrading succeed
	
	public GatewayUpdateStatusCode getStatusCode()
	{
		return statusCode;
	}
	
	public void setStatusCode( GatewayUpdateStatusCode statusCode )
	{
		this.statusCode = statusCode;
	}
	
	public int getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode( int errorCode )
	{
		this.errorCode = errorCode;
	}

	public GatewayUpdateMessageType getMessageType()
	{
		return messageType;
	}

	public void setMessageType( GatewayUpdateMessageType messageType )
	{
		this.messageType = messageType;
	}

	public String getDetailedMessage()
	{
		return detailedMessage;
	}
	
	public void setDetailedMessage( String detailedMessage )
	{
		this.detailedMessage = detailedMessage;
	}
	
	public Version getGatewayVersion()
	{
		return gatewayVersion;
	}

	public void setGatewayVersion( Version gatewayVersion )
	{
		this.gatewayVersion = gatewayVersion;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "GatewayUpdateStatus { " );
		sb.append( "statusCode = " + statusCode );
		sb.append( ", errorCode = " + errorCode );
		sb.append( ", messageType = " + messageType );
		sb.append( ", detailedMessage = '" + detailedMessage + "'" );
		sb.append( ", gatewayVersion = " + gatewayVersion );
		sb.append( " }" );
		return sb.toString();
	}
}

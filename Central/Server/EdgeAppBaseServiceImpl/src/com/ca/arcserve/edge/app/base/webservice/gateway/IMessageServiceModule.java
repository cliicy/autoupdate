package com.ca.arcserve.edge.app.base.webservice.gateway;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public interface IMessageServiceModule
{
	boolean isMessageServiceException( Throwable t );
	String getLocalizedMessageOfException( Throwable t );
	EdgeServiceFault convertExceptionToEdgeServiceFault( Throwable t );
	boolean isConnectionTimeoutException( Throwable t );
}

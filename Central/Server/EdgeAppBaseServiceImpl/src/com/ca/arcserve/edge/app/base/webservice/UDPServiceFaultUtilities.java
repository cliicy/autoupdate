package com.ca.arcserve.edge.app.base.webservice;

import com.ca.arcserve.edge.app.base.resources.messages.WebServiceFaultMessageRetriever;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.webservice.udpservice.fault.UDPServiceFault;
import com.ca.arcserve.edge.app.base.webservice.udpservice.fault.UDPServiceFaultInfo;

public class UDPServiceFaultUtilities
{
	public static UDPServiceFault edgeServiceFault2UdpServiceFault( EdgeServiceFault edgeServiceFault )
	{
		assert edgeServiceFault != null : "edgeServiceFault is null";
		
		EdgeServiceFaultBean edgeServiceFaultInfo = edgeServiceFault.getFaultInfo();
		
		long longErrorCode = Long.parseLong( edgeServiceFaultInfo.getCode() );
		
		StringBuilder errorCodeBuilder = new StringBuilder();
		errorCodeBuilder.append( edgeServiceFaultInfo.getFaultType().getShortName() );
		errorCodeBuilder.append( "-" );
		errorCodeBuilder.append( Long.toHexString( longErrorCode ).toUpperCase() );
		
		UDPServiceFaultInfo udpServiceFaultInfo = new UDPServiceFaultInfo(
			errorCodeBuilder.toString(),
			WebServiceFaultMessageRetriever.getErrorMessage( null, edgeServiceFaultInfo ) );
		
		return new UDPServiceFault( edgeServiceFault.getMessage(), udpServiceFaultInfo );
	}
	
	public static UDPServiceFault handleException( Exception e )
	{
		if (e instanceof UDPServiceFault)
		{
			return (UDPServiceFault) e;
		}
		if (e instanceof EdgeServiceFault)
		{
			return edgeServiceFault2UdpServiceFault( (EdgeServiceFault) e );
		}
		else // unexpected exception
		{
			return getServiceFaultForUnexpectedException( e );
		}
	}
	
	public static UDPServiceFault getServiceFaultForUnexpectedException( Exception e )
	{
		assert e != null : "e is null";
		
		UDPServiceFaultInfo udpServiceFaultInfo = new UDPServiceFaultInfo(
			EdgeServiceErrorCode.Common_Service_Unexcepted_Execption,
			"Unexpected exception" );
		udpServiceFaultInfo.setAddedData( e.toString() );
		
		return new UDPServiceFault( "", udpServiceFaultInfo );
	}
	
	public static UDPServiceFault createUDPServiceFault( String edgeErrorCode, String message )
	{
		return createUDPServiceFault( edgeErrorCode, null, message );
	}
	
	public static UDPServiceFault createUDPServiceFault(
		String edgeErrorCode, Object[] errorParams, String message )
	{
		EdgeServiceFault edgeServiceFault = EdgeServiceFault.getFault(
			edgeErrorCode, errorParams, message );
		return edgeServiceFault2UdpServiceFault( edgeServiceFault );
	}
}

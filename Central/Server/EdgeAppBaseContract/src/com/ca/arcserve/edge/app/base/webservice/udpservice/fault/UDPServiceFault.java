package com.ca.arcserve.edge.app.base.webservice.udpservice.fault;

import javax.xml.ws.WebFault;

@WebFault( name = "UDPServiceFaultInfo", targetNamespace = "http://webservice.edge.arcserve.ca.com/" )
public class UDPServiceFault extends Exception
{
	private static final long serialVersionUID = 4722755147714559431L;

	private UDPServiceFaultInfo faultInfo;
	
	public UDPServiceFault( String message, UDPServiceFaultInfo faultInfo )
	{
		super( message );
		this.faultInfo = faultInfo;
	}
	
	public UDPServiceFault( String message, UDPServiceFaultInfo faultInfo, Throwable cause )
	{
		super( message, cause );
		this.faultInfo = faultInfo;
	}
	
	public UDPServiceFaultInfo getFaultInfo()
	{
		return this.faultInfo;
	}
}

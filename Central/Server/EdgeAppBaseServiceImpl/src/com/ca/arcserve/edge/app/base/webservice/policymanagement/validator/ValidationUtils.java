package com.ca.arcserve.edge.app.base.webservice.policymanagement.validator;

public class ValidationUtils
{
	public static boolean isStringNullOrEmpty( String string )
	{
		if (string == null)
			return true;
		
		if (string.trim().isEmpty())
			return true;
		
		return false;
	}
	
	public static boolean isHostInfoValid( String hostName, String username )
	{
		return !isStringNullOrEmpty( hostName ) && !isStringNullOrEmpty( username );
	}
	
	
}

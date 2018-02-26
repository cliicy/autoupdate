package com.ca.arcserve.edge.app.base.util;

import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;

public class LinuxD2DWebServiceErrorMessages {

	public static String getMessage(String errorCode){
		return getResource("ServiceError_" + errorCode);
	}
	
	public static String getMessage(String errorCode, Object... arguments) {
		String message = null;
		
		if (errorCode != null && !errorCode.isEmpty())
		{
			message = getResource("ServiceError_" + errorCode);
			
			if (arguments != null && arguments.length > 0)
			{
				message = Utils.getMessage(message, arguments);
			}
		}
		return message;		
	}
	
	private static String getResource(String key)
	{
		return MessageReader.getMessage("com.ca.arcserve.linuximaging.common.properties.resources", key);
	}
}

package com.ca.arcserve.edge.app.base.util;

import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;

public class D2DWebServiceErrorMessages {
	
	public static String getMessage(String errorCode){
		errorCode = mapErrorCode(errorCode);
		return getResource("ServiceError_" + errorCode);
	}
	
	public static String getMessage(String errorCode, Object... arguments) {
		String message = null;
		// may convert error code
		errorCode = mapErrorCode(errorCode);
		
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
		return MessageReader.getMessage("com.ca.arcflash.common.properties.resources", key);
	}
	
	private static String mapErrorCode(String errorCode)
	{
		if (errorCode == null || errorCode.isEmpty())
		{
			return null;
		}
		
		String result = errorCode;
		
		if (errorCode.equals(FlashServiceErrorCode.Common_License_Failure_Encryption))
		{
			result = FlashServiceErrorCode.Common_License_Failure;
		}
		
		return result;
	}
}

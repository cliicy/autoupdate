package com.ca.arcserve.edge.app.base.resources.messages;

import java.util.Locale;

import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean.FaultType;
import com.ca.arcserve.edge.app.base.util.D2DWebServiceErrorMessages;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.rps.webservice.i18n.RpsMessageReader;

public class WebServiceFaultMessageRetriever
{
	public static String getErrorMessage( Locale locale, EdgeServiceFaultBean serviceFaultInfo )
	{
		String message = "";
		
		if (locale == null)
			locale = new Locale( "en" );
		FaultType faultType = serviceFaultInfo.getFaultType();

		if (faultType == null) {
			faultType = FaultType.Edge;
		}

		switch (faultType) {
		case Edge:
			message = getErrorMessageForEdgeFault( locale, serviceFaultInfo );
			break;
			
		case D2D:
			message = getErrorMessageForD2DFault( locale, serviceFaultInfo );
			break;
			
		case RPS:
		case RPSRemote:
			message = getErrorMessageForRPSFault( locale, serviceFaultInfo );
			break;
			
		case LinuxD2D:
			message = getErrorMessageForLinuxD2DFault( locale, serviceFaultInfo );
			break;
			
		case ASBU:
			message = getErrorMessageForASBUFault(locale, serviceFaultInfo);
			break;
		}
		return message;
	}
	
	private static String getErrorMessageForEdgeFault( Locale locale, EdgeServiceFaultBean faultInfo )
	{
		String message = "";
		
		message = MessageReader.getErrorMessage(faultInfo.getCode(), locale);
		
		if (message != null &&
			faultInfo.getMessageParameters() != null &&
			faultInfo.getMessageParameters().length > 0) {
			message = Utils.getMessage(message, faultInfo.getMessageParameters());
		}
		
		return message;
	}
	
	private static String getErrorMessageForD2DFault( Locale locale, EdgeServiceFaultBean faultInfo )
	{
		String message = "";
		
		if (FlashServiceErrorCode.Login_WrongUUID.equals(faultInfo.getCode())
			|| FlashServiceErrorCode.Login_WrongCredential.equals(faultInfo.getCode()))
			message = EdgeCMWebServiceMessages.getResource("failedtoConnectToD2DError");
		else
	//		result.setDisplayMessage(D2DWebServiceErrorMessages.getMessage(exception.getFaultInfo().getCode(), exception.getFaultInfo().getMessageParameters()));
			message = faultInfo.getMessage();
		
		if (isStringNullOrEmpty(message)) {
			message = MessageReader.getErrorMessage(EdgeServiceErrorCode.Common_Service_General);
		}
		
		return message;
	}
	
	private static String getErrorMessageForASBUFault(Locale locale, EdgeServiceFaultBean faultInfo){
		//String message = MessageReader.getASBUErrorMessage(faultInfo.getCode());
		//first use asbu's message, then use console's message.
		String message = faultInfo.getMessage();
		if(!isStringNullOrEmpty(message) && message.indexOf("@") > 0){
			message = message.substring(0, message.lastIndexOf("@"));
		}
		if (isStringNullOrEmpty(message)) {
			message = MessageReader.getASBUErrorMessage(faultInfo.getCode(), faultInfo.getMessageParameters());
			if (isStringNullOrEmpty(message)) {
				message = MessageReader.getErrorMessage(EdgeServiceErrorCode.Common_Service_General);
			}
		}
		return message;
	}
	
	private static String getErrorMessageForRPSFault( Locale locale, EdgeServiceFaultBean faultInfo )
	{
		String message = "";
		
		if (faultInfo.getFaultType() == FaultType.RPSRemote) {
//			result.setDisplayMessage(MessageReader.getRpsErrorMessage(errorCode, errorMessageParameters));
			message = faultInfo.getMessage();
		} else {
			message = RpsMessageReader.getErrorMessage(faultInfo.getCode(), faultInfo.getMessageParameters());
		}
		
		if (isStringNullOrEmpty(message)) {
			message = D2DWebServiceErrorMessages.getMessage(faultInfo.getCode(), faultInfo.getMessageParameters());
		}
		
		if (isStringNullOrEmpty(message)) {
			message = MessageReader.getErrorMessage(EdgeServiceErrorCode.Common_Service_General);
		}
		
		return message;
	}
	
	private static String getErrorMessageForLinuxD2DFault( Locale locale, EdgeServiceFaultBean faultInfo )
	{
		String message = "";
		
		if (FlashServiceErrorCode.Login_WrongUUID.equals(faultInfo.getCode())
			|| FlashServiceErrorCode.Login_WrongCredential.equals(faultInfo.getCode()))
			message = EdgeCMWebServiceMessages.getResource("failedtoConnectToD2DError");
		else
	//		result.setDisplayMessage(D2DWebServiceErrorMessages.getMessage(exception.getFaultInfo().getCode(), exception.getFaultInfo().getMessageParameters()));
			message = faultInfo.getMessage();
		
		if (isStringNullOrEmpty(message)) {
			message = MessageReader.getErrorMessage(EdgeServiceErrorCode.Common_Service_General);
		}
		
		return message;
	}
	
	private static boolean isStringNullOrEmpty( String string )
	{
		return ((string == null) || string.trim().isEmpty());
	}
}

package com.ca.arcserve.edge.app.base.webservice.exception;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.service.exception.ServiceException;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean.FaultType;

public class WebServiceExceptionHandler extends ExceptionHandler {
	
	private static Logger logger = Logger.getLogger(WebServiceExceptionHandler.class);
	
	private FaultType faultType;
	private String connectionFailErrorCode;
	private final String METHOD_NOT_DEFINED_MESSAGE = "Cannot find dispatch method";
	private final String NO_CORRESPONDING_WSDL_OPERATION = "Method .*. is exposed as WebMethod, but there is no corresponding wsdl operation with name .*";
	
	public WebServiceExceptionHandler(FaultType faultType, String connectionFailErrorCode) {
		this(faultType, connectionFailErrorCode, null);
	}
	
	public WebServiceExceptionHandler(FaultType faultType, String connectionFailErrorCode, ExceptionHandler nextHandler) {
		super(nextHandler);
		
		this.faultType = faultType;
		this.connectionFailErrorCode = connectionFailErrorCode;
	}

	@Override
	protected boolean handleTargetException(Throwable exception) throws EdgeServiceFault {
		if (exception instanceof SOAPFaultException) {
			SOAPFaultException soapException = (SOAPFaultException) exception;
			if(soapException.getFault() ==null || soapException.getFault().getFaultCodeAsQName() == null){
				throw (SOAPFaultException) exception;
			}
			throw convert((SOAPFaultException) exception);
		}
		
		if (exception instanceof WebServiceException) {
			throw convert((WebServiceException) exception);
		}
		
		if (exception instanceof ServiceException) {
			throw convert((ServiceException) exception);
		}
		
		if (exception instanceof EdgeServiceFault) {
			throw convert((EdgeServiceFault) exception);
		}
		
		return false;
	}
	
	private EdgeServiceFault createEdgeServiceFault(String errorCode, String errorMessage, FaultType type) {
		EdgeServiceFault fault = EdgeServiceFault.getFault(errorCode, errorMessage);
		fault.getFaultInfo().setFaultType(type);
		return fault;
	}

	protected EdgeServiceFault convert(SOAPFaultException exception) {
		String errorCode = exception.getFault().getFaultCodeAsQName().getLocalPart();
		String errorMessage = exception.getFault().getFaultString();
		logger.info("[" + faultType + "] WebServiceExceptionHandler - SOAPFaultException occurred, error code = " + errorCode + ", error message = " + errorMessage);
		logger.debug(errorMessage, exception); 
		
		if (errorMessage != null && errorMessage.startsWith(METHOD_NOT_DEFINED_MESSAGE)) {
			logger.info("WebServiceExceptionHandler - version low!");
			String versionLowErrorCode = getVersionLowErrorCode();
			if(!versionLowErrorCode.equalsIgnoreCase("")){
				return new EdgeServiceFault("", new EdgeServiceFaultBean(versionLowErrorCode , ""));
			}
		}
		return createEdgeServiceFault(errorCode, errorMessage, faultType);
	}
	
	private EdgeServiceFault convert(WebServiceException exception) throws EdgeServiceFault {
		String errorMessage = exception.getMessage();
		logger.info("[" + faultType + "] WebServiceExceptionHandler - connect to web service failed, error message = " + errorMessage);
		logger.debug(errorMessage, exception); 
		if(errorMessage != null && errorMessage.matches(NO_CORRESPONDING_WSDL_OPERATION)){
			logger.info("WebServiceExceptionHandler - version low!");
			String errorCode = getVersionLowErrorCode();
			if(!errorCode.equalsIgnoreCase("")){
				return new EdgeServiceFault("", new EdgeServiceFaultBean(errorCode , ""));
			}
		}
		return createEdgeServiceFault(connectionFailErrorCode, exception.getMessage(), FaultType.Edge);
	}
	
	private EdgeServiceFault convert(ServiceException exception) throws EdgeServiceFault {
		logger.info("[" + faultType + "] WebServiceExceptionHandler - ServiceException occurred, error code = " + exception.getErrorCode() + ", error message = " + exception.getMessage());
		logger.debug(exception.getMessage(), exception);  
		return createEdgeServiceFault(exception.getErrorCode(), exception.getMessage(), faultType);
	}
	
	private EdgeServiceFault convert(EdgeServiceFault exception) throws EdgeServiceFault {
		String errorMessage = exception.getMessage();
		logger.debug(errorMessage, exception); 
		if(errorMessage != null && errorMessage.matches(NO_CORRESPONDING_WSDL_OPERATION)){
			logger.info("WebServiceExceptionHandler - check EdgeServiceFault - version low!");
			if(exception.getFaultInfo() == null )
				return exception;
			String errorCode = getVersionLowErrorCode(exception.getFaultInfo().getFaultType());
			if(!errorCode.equalsIgnoreCase("")){
				return new EdgeServiceFault("", new EdgeServiceFaultBean(errorCode , ""));
			}
		}
		return exception;
	}
	
	private String getVersionLowErrorCode(){
		return getVersionLowErrorCode(faultType);
	}
	
	private String getVersionLowErrorCode (FaultType faultType){
		String errorCode = "";
		if(faultType == null){
			return errorCode;
		}
		if(faultType == FaultType.RPSRemote){
			errorCode = EdgeServiceErrorCode.RPS_Server_Version_Not_Match;
		}else if(faultType == FaultType.LinuxD2D){
			errorCode = EdgeServiceErrorCode.Node_Linux_D2D_Server_Version_Not_Match;
		}else if(faultType == FaultType.Edge){
			errorCode = EdgeServiceErrorCode.RemoteConsole_Version_Not_Match;
		}else if(faultType == FaultType.ASBU){
			errorCode = EdgeServiceErrorCode.ASBU_Version_Not_Match;
		}else if(faultType == FaultType.D2D){
			errorCode = EdgeServiceErrorCode.Node_D2D_Server_Version_Not_Match;
		}
		return errorCode;
	}
	
}

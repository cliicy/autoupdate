package com.ca.arcserve.edge.app.base.webservice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceClientProxy;
import com.ca.arcserve.edge.app.base.webservice.client.ISubWebServiceFactory;

public class WebServiceFactory implements ISubWebServiceFactory{
	private static Logger logger = Logger.getLogger( WebServiceFactory.class );

	private static String CONTEXT_PATH = "/management";
	private static String endPointName = "EdgeServiceImpl";
	private static String gatewayContextPath="/gateway";

    /**
     * be care to use this method. It is used by context listener only.
     * @param cONTEXTPATH
     */
	public static void setCONTEXT_PATH(String cONTEXTPATH) {
		if(cONTEXTPATH.isEmpty())
			cONTEXTPATH = "";
		CONTEXT_PATH = cONTEXTPATH;
	}
	
	/**
     * be care to use this method. It is used by context listener only.
     * @param endPointName
     */
	public static void setEndPointName(String endPointName) {
		WebServiceFactory.endPointName = endPointName;
	}
/**
 * be careful to use this method. This method should be used by Edge inner code
 * @param hostName
 * @param Port
 * @param Protocol
 * @return
 */
	public static String getEdgeWSDL(String hostName, int Port, String Protocol)
	{
		String wsdlLocation = "";

		if (Protocol != null && !Protocol.endsWith(":"))
			Protocol = Protocol + ":";

		wsdlLocation = Protocol + "//" + hostName + ":" + Port + CONTEXT_PATH
				+ "/services/" + endPointName + "?wsdl";

		return wsdlLocation;
	}
	
	public static String getGateWayWSDL(String hostName, int Port, String Protocol)
	{
		String wsdlLocation = "";

		if (Protocol != null && !Protocol.endsWith(":"))
			Protocol = Protocol + ":";

		wsdlLocation = Protocol + "//" + hostName + ":" + Port + gatewayContextPath
				+ "/services/" + endPointName + "?wsdl";

		return wsdlLocation;
	}

	@Override
	public BaseWebServiceClientProxy getService(String protocol, String host,
			int port) {
		Object proxy = WebServiceUtility.getEdgeServiceProxy(
			CONTEXT_PATH, endPointName, protocol, host, port, IEdgeService.class );
		proxy=injectExpceptionHandler(proxy);
		WebServiceClientProxy proxy2 = new WebServiceClientProxy(port,
			protocol, proxy,host);
		return proxy2;
	}
	
	private Object injectExpceptionHandler(final Object interfaceImpl) {
		return Proxy.newProxyInstance(interfaceImpl.getClass().getClassLoader(), interfaceImpl.getClass().getInterfaces(), new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				try {
					return method.invoke(interfaceImpl, args);
				} catch (InvocationTargetException e) {
					if (e.getTargetException() instanceof SOAPFaultException) {
						throw handleException((SOAPFaultException) e.getTargetException());
					} else if (e.getTargetException() instanceof WebServiceException) {
						throw handleException((WebServiceException) e.getTargetException());
					} else {
						throw handleTargetException(e.getTargetException());
					}
				} catch (Exception e) {
					throw handleUnexpectedException(e, EdgeServiceErrorCode.Common_Service_General);
				}
			}
			
			
			private EdgeServiceFault handleException(SOAPFaultException e) {
				String errorCode = e.getFault().getFaultCodeAsQName().getLocalPart();
				String errorMessage = e.getFault().getFaultString();
				return EdgeServiceFault.getFault(errorCode, errorMessage);
			}

			private EdgeServiceFault handleException(WebServiceException e) {
				return EdgeServiceFault.getFault(EdgeServiceErrorCode.MSP_CannotConnectMSP , e.getMessage());
			}
			
			private EdgeServiceFault handleTargetException(Throwable targetException) {
				if (targetException instanceof EdgeServiceFault) {
					return (EdgeServiceFault) targetException;
				} else if (targetException instanceof DaoException) {
					logger.error("[WebServiceFactory] DB failed", targetException);
					return handleUnexpectedException(targetException, EdgeServiceErrorCode.Common_Service_Dao_Execption);
				} else {
					return handleUnexpectedException(targetException, EdgeServiceErrorCode.Common_Service_General);
				}
			}
			
			private EdgeServiceFault handleUnexpectedException(Throwable t, String edgeServiceErrorCode) {
				String message = "EdgeWebServiceProxyFactory.handleUnexpectedException - " + t.getClass().getSimpleName() + " occurred, error message = " + t.getMessage();
				return EdgeServiceFault.getFault(edgeServiceErrorCode, message);
			}
		});

	}

}

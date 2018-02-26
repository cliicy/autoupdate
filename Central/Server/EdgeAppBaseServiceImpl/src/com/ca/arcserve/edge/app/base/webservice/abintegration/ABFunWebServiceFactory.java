package com.ca.arcserve.edge.app.base.webservice.abintegration;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
/**
 * http://chech24-w8s-18:9999/ABFuncService/metadata
 */
public class ABFunWebServiceFactory implements Serializable{
	private static final long serialVersionUID = 3919121316672490298L;
	private static final String WSDL_URI = "http://%s:%d/ABFuncService/metadata?wsdl";
	private static final String TARGET_NAMESPACE = "http://tempuri.org/";
	private static final String CONNECT_TIMEOUT = "com.sun.xml.ws.connect.timeout";
	private static final String REQUEST_TIMEOUT = "com.sun.xml.ws.request.timeout";
	private static final int TIME_OUT_VALUE = 180000;
	
	public static ABFunWebServiceClientProxy getOldASBUService(String host, int port) throws SOAPFaultException {
		Service service = null;
		try {
			String strURI = String.format(WSDL_URI, host, port);
			service = Service.create(new URL(strURI), new QName(
					TARGET_NAMESPACE, "ABFuncServiceImpl"));
		} catch (MalformedURLException e) {
			throw new WebServiceException(e.getMessage(), e);
		}
		
		IABFuncService proxy = null;
		try {

			proxy = (IABFuncService) service.getPort(new QName(
					TARGET_NAMESPACE, "BasicHttpBinding_IABFuncService"),
					IABFuncService.class);

		} catch (Error error) {
			throw new WebServiceException(error.getMessage(), error);
		}
		
		Map<String, Object> rc = ((BindingProvider) proxy).getRequestContext();
		rc.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, Boolean.TRUE);
		rc.put(CONNECT_TIMEOUT, TIME_OUT_VALUE);
		rc.put(REQUEST_TIMEOUT, TIME_OUT_VALUE);	

		ABFunWebServiceClientProxy proxy2 = new ABFunWebServiceClientProxy(port,
				"http", proxy);
		return proxy2;
	}
}

package com.ca.arcserve.edge.app.base.webservice;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfoConstants;
import com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceFactory;
import com.ca.arcserve.edge.app.base.webservice.client.IWebServiceFactory;

public class WebServiceUtility
{

	public static Object getEdgeServiceProxy(
		String contextPath, String endPointName, String protocol, String host, int port,
		Class<?> serviceEndpointInterface )
	{
		if (protocol != null && !protocol.endsWith(":"))
			protocol = protocol + ":";
		String wsHost = BaseWebServiceFactory.replaceIfLocalHost(host);
		String wsdlLocation = protocol + "//" + wsHost + ":" + port + contextPath
				+ "/services/" + endPointName + "?wsdl";
		Service service = null;
		try {
			service = Service.create(new URL(wsdlLocation), new QName(
					ServiceInfoConstants.SERVICE_EDGE_PROPER_NAMESPACE, endPointName));
		} catch (MalformedURLException e) {
			throw new WebServiceException(e.getMessage(), e);
		}

//		IEdgeService proxy = null;
//		// java.lang.Error: Undefined operation name
//		try {
//			String portName = endPointName + "HttpSoap11Endpoint";
//			proxy = (IEdgeService) service.getPort(
//				new QName(ServiceInfoConstants.SERVICE_EDGE_PROPER_NAMESPACE, portName),
//				IEdgeService.class);
//
//		} catch (Error error) {
//			throw new WebServiceException(error.getMessage(), error);
//		}

		Object proxy = null;
		// java.lang.Error: Undefined operation name
		try {
			String portName = endPointName + "HttpSoap11Endpoint";
			proxy = service.getPort(
				new QName(ServiceInfoConstants.SERVICE_EDGE_PROPER_NAMESPACE, portName),
				serviceEndpointInterface);

		} catch (Error error) {
			throw new WebServiceException(error.getMessage(), error);
		}

		Map<String, Object> rc = ((BindingProvider) proxy).getRequestContext();
		rc.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, Boolean.TRUE);

		rc.put(IWebServiceFactory.CONNECT_TIMEOUT, IWebServiceFactory.TIME_OUT_VALUE);
		rc.put(IWebServiceFactory.REQUEST_TIMEOUT, IWebServiceFactory.TIME_OUT_VALUE);
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, makeEndpointAddress(wsdlLocation));
		
		return proxy;
	}
	
	private static String makeEndpointAddress(String wsdl) {
		String serviceURL = "";
		int idx = 0;
		if (wsdl != null && (idx = wsdl.indexOf("?")) > 0) {
			serviceURL = wsdl.substring(0, idx);
		} else {
			serviceURL = wsdl;
		}
		return serviceURL;
	}
}

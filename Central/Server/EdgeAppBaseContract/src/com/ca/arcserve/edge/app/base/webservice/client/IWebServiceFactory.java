package com.ca.arcserve.edge.app.base.webservice.client;

import javax.xml.ws.soap.SOAPFaultException;

import com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayProxyInfo;


public interface IWebServiceFactory {
	public static final String CONNECT_TIMEOUT = "com.sun.xml.ws.connect.timeout";

	public static final String REQUEST_TIMEOUT = "com.sun.xml.ws.request.timeout";

	public static final int TIME_OUT_VALUE = 180000;


	/**
	 * get service proxy with given serviceID and service info. invoke {@link #getServiceInfoList(String, String, int)} first.
	 *
	 * @param protocol
	 * @param host
	 * @param port
	 * @param serviceID
	 * @param serviceInfo
	 * @return
	 * @throws SOAPFaultException
	 */
	public abstract <T> BaseWebServiceClientProxy getWebService(String protocol,
			String host, int port, String serviceID, ServiceInfo serviceInfo,Class<T> serviceInterfaceClass)
			throws SOAPFaultException;
	public abstract <T> BaseWebServiceClientProxy getWebService(String protocol,
			String host, int port, String serviceID, ServiceInfo serviceInfo,Class<T> serviceInterfaceClass,int timeOut)
			throws SOAPFaultException;
	public abstract <T> BaseWebServiceClientProxy getEdgeWebService(String protocol,
			String host, int port, Class<T> serviceInterfaceClass)
			throws SOAPFaultException;
	public abstract <T> BaseWebServiceClientProxy getWebServiceByProxy(String protocol,
			String host, int port, String serviceID, ServiceInfo serviceInfo,
			GatewayProxyInfo proxyInfo, Class<T> serviceInterfaceClass)
			throws SOAPFaultException;
}
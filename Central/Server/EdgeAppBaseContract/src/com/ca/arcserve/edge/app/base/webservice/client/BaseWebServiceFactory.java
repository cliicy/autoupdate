package com.ca.arcserve.edge.app.base.webservice.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfo;
import com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfoConstants;
import com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfoList;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayProxyInfo;



public class BaseWebServiceFactory  implements IWebServiceFactory{
	/**
	 *
	 * @param protocol
	 * @param host
	 * @param port
	 * @param contextPath
	 * @return
	 */
	@Deprecated
	public static BaseWebServiceClientProxy getService(String protocol,String host, int port){
		if(subFactory == null)  throw new WebServiceException("unknow sub factory");
		return subFactory.getService(protocol, host, port);
	}

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
	@Deprecated
	public final  <T>  BaseWebServiceClientProxy getWebService(String protocol,
			String host, int port, String serviceID, ServiceInfo serviceInfo, Class<T> serviceInterface, int timeOut)
			throws SOAPFaultException {
		if (protocol != null && !protocol.endsWith(":"))
			protocol = protocol + ":";

		String wsdlLocation = serviceInfo.getWsdlURL();
		Service service = null;
		 InputStream in= null;
		 URLConnection conn = null;
		try {
			URL url = new URL(wsdlLocation);
			conn = url.openConnection();
			// setting these timeouts ensures the client does not deadlock
			// indefinitely
			// when the server has problems.
			conn.setConnectTimeout(timeOut);
			conn.setReadTimeout(timeOut);
			in = conn.getInputStream();
			byte[] bs = new byte[12];
			in.read(bs);
			
		} catch (Exception e) {
			throw new WebServiceException(e.getMessage(), e);
		} finally{
			if(in!=null){
				try{ in.close(); in =null;}catch(Exception e1){};
			}

		}
		try {
			service = Service.create(new URL(wsdlLocation), new QName(
					serviceInfo.getNamespace(), serviceInfo.getServiceName()));
		} catch (MalformedURLException e) {
			throw new WebServiceException(e.getMessage(), e);
		}

		T proxy = createProxy(protocol, serviceID, serviceInfo, service,serviceInterface,timeOut);
		
		Map<String, Object> rc = ((BindingProvider) proxy).getRequestContext();
		
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, makeEndpointAddress(wsdlLocation));
		

		BaseWebServiceClientProxy proxy2 = createClientProxy(protocol, host,
				port, proxy);
		return proxy2;
	}


	protected  BaseWebServiceClientProxy createClientProxy(
			String protocol, String host, int port, Object proxy) {
		BaseWebServiceClientProxy proxy2 = new BaseWebServiceClientProxy(port,
				protocol, proxy,host);
		return proxy2;
	}



	private <T> T createProxy(String protocol, String serviceID,
			ServiceInfo serviceInfo, Service service, Class<T> serviceInterface,int timeOut) {
		T proxy = null;

		try{

			proxy = service.getPort(new QName(serviceInfo.getNamespace(), serviceInfo.getPortName()),serviceInterface);

		}catch(Error error){
			throw  new WebServiceException(error.getMessage(), error);
		}

	    Map<String, Object> rc = ((BindingProvider) proxy).getRequestContext();
	    rc.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, Boolean.TRUE);
	    if (Boolean.getBoolean("wsmonitor") && protocol.equalsIgnoreCase("http:")) {
	        String address = (String)((BindingProvider) proxy).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
	        int indexOf = address.indexOf("/", "http://1".length());
	        String temp = address.substring(0,indexOf+1);
	        address = address.replaceFirst(temp, protocol+"//localhost:4040/");
	        ((BindingProvider) proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
	    }
	    rc.put(CONNECT_TIMEOUT, timeOut);
	    rc.put(REQUEST_TIMEOUT,timeOut);
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
	

	public static ServiceInfo getFeatureServiceInfo(String serviceID,
			ServiceInfoList serviceInfo) {
		List<ServiceInfo> services = serviceInfo.getServices();
		for (ServiceInfo service : services) {
			List<String> serviceIDList = service.getServiceIDList();
			for (String temp : serviceIDList) {
				if (temp.equals(serviceID))
					return service;
			}
		}
		return null;

	}



/**
 * get Service Info list from host
 * @param protocol
 * @param host
 * @param port
 * @param serviceInfoPath
 * @return
 */
	public  ServiceInfoList getServiceInfoList(String protocol,
			String host, int port,String serviceInfoPath) {
		if (protocol != null && !protocol.endsWith(":"))
			protocol = protocol + ":";

		String wsHost = replaceIfLocalHost(host);
		String listPath = protocol + "//" + wsHost + ":" + port
				+ serviceInfoPath;
		HttpURLConnection connection = null;
		BufferedReader rd = null;
		try {
			URL url = new URL(listPath);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			connection.setReadTimeout(TIME_OUT_VALUE);
			connection.connect();
			if (200 != connection.getResponseCode()) {
				throw new WebServiceException(
						EdgeServiceErrorCode.Common_Service_FAIL_TO_GETLIST);
			}

			rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line + '\n');
			}
			return CommonUtil.unmarshal(sb.toString(), ServiceInfoList.class);
		} catch (MalformedURLException e) {
			throw new WebServiceException(e.getMessage(), e);
		} catch (IOException e) {
			throw new WebServiceException(e.getMessage(), e);
		} catch (JAXBException e) {
			throw new WebServiceException(e.getMessage(), e);
		} finally {
			try {
				if (rd != null)
					rd.close();
				
				if (connection != null)
					connection.disconnect();
				connection = null;
			} catch (Throwable t) {
			}
		}

	}



	private static ArrayList<String> localArr = null;

	private static void initLocalArr() {
		localArr = new ArrayList<String>();
		localArr.add("localhost");
		localArr.add("127.0.0.1");

		try {
			InetAddress localHost = InetAddress.getLocalHost();
			localArr.add(localHost.getHostAddress());
			localArr.add(localHost.getHostName());
			String fQDN = localHost.getCanonicalHostName();
			int indx = fQDN.indexOf('.');
			if (indx > 0) {
				localArr.add("localhost" + fQDN.substring(indx));
				localArr.add(fQDN);
			}
		} catch (UnknownHostException e) {
		}

		try {
			Enumeration<NetworkInterface> ni = NetworkInterface
					.getNetworkInterfaces();

			while (ni.hasMoreElements()) {
				NetworkInterface element = ni.nextElement();
				Enumeration<InetAddress> ips = element.getInetAddresses();
				while (ips.hasMoreElements()) {
					InetAddress ip = ips.nextElement();
					if (!localArr.contains(ip.getHostAddress())) {
						localArr.add(ip.getHostAddress());
					}
				}
			}
		} catch (SocketException e) {
		}
	}

	public static String replaceIfLocalHost(String host) {
		if (localArr == null || localArr.isEmpty()) {
			initLocalArr();
		}

		if (host == null || host.length() == 0) {
			host = "localhost";
		}

		boolean isLocal = false;

		for (String h : localArr) {
			if (h.equalsIgnoreCase(host)) {
				isLocal = true;
				break;
			}
		}
		if (isLocal) {
			host = "localhost";
		}

		if(host!=null) host = host.toLowerCase();
		return host;
	}
	private static ISubWebServiceFactory subFactory;
	public static void setSubStaticWebServiceFactory(ISubWebServiceFactory sub) {
		subFactory = sub;

	}

	@Deprecated
	@Override
	public <T> BaseWebServiceClientProxy getWebService(String protocol,
			String host, int port, String serviceID, ServiceInfo serviceInfo,
			Class<T> serviceInterfaceClass)
			throws SOAPFaultException {
		return this.getWebService(protocol, host, port, serviceID, serviceInfo, serviceInterfaceClass,TIME_OUT_VALUE);
	}
	
	@Override
	public <T> BaseWebServiceClientProxy getEdgeWebService(String protocol,
			String host, int port, Class<T> serviceInterfaceClass)
			throws SOAPFaultException{
		ServiceInfo serviceInfo = new ServiceInfo();
		serviceInfo.setBindingType(ServiceInfoConstants.SERVICE_BINDING_SOAP11);
		serviceInfo.setNamespace(ServiceInfoConstants.SERVICE_EDGE_PROPER_NAMESPACE);
		//serviceInfo.setPortName(ServiceInfoConstants.SERVICE_EDGE_PROPER_PORT_NAME);
		serviceInfo.setPortName(ServiceInfoConstants.SERVICE_EDGE_CONSOLE_PROPER_PORT_NAME);
		
		//serviceInfo.setServiceName(ServiceInfoConstants.SERVICE_EDGE_PROPER_SERVICE_NAME);
		serviceInfo.setServiceName(ServiceInfoConstants.SERVICE_EDGE_CONSOLE_PROPER_SERVICE_NAME);
		if (!protocol.endsWith(":")) {
			protocol = protocol + ":";
		}
		// is host is uppercase, webService.getService()).getDatabaseConfiguration() will fail with "Not Login"
		host = host.toLowerCase();
		String wsdlURL = protocol + "//" + host + ":" + port
				+ CommonUtil.CENTRAL_MANAGER_CONTEXT_PATH
				+ "/services/EdgeServiceConsoleImpl?wsdl";
		serviceInfo.setWsdlURL(wsdlURL);
		
		return getWebService(protocol, host, port, ServiceInfoConstants.SERVICE_EDGE_CONSOLE_PROPER_SERVICE_NAME, serviceInfo, serviceInterfaceClass);
	}
	
	@Override
	public <T> BaseWebServiceClientProxy getWebServiceByProxy(String protocol,
			String host, int port, String serviceID, ServiceInfo serviceInfo, GatewayProxyInfo proxyInfo,
			Class<T> serviceInterfaceClass)
			throws SOAPFaultException {
		return this.getWebServiceByProxy(protocol, host, port, serviceID, serviceInfo, serviceInterfaceClass,TIME_OUT_VALUE, proxyInfo);
	}
	
	private final  <T>  BaseWebServiceClientProxy getWebServiceByProxy(String protocol,
			String host, int port, String serviceID, ServiceInfo serviceInfo, Class<T> serviceInterface, int timeOut, GatewayProxyInfo proxyInfo)
			throws SOAPFaultException {
		if (protocol != null && !protocol.endsWith(":"))
			protocol = protocol + ":";

		String wsdlLocation = serviceInfo.getWsdlURL();
		Service service = getEdgeLocalService(getEdgeConsoleLocalWSDL(), new QName(
				serviceInfo.getNamespace(), serviceInfo.getServiceName()));

		T proxy = createProxy(protocol, serviceID, serviceInfo, service,serviceInterface,timeOut);
		
		Map<String, Object> rc = ((BindingProvider) proxy).getRequestContext();
		
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, makeEndpointAddress(wsdlLocation));
		if(proxyInfo != null && !StringUtil.isEmptyOrNull(proxyInfo.getUsername())){
			 sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
			 String encodedUserPwd = encoder.encode((proxyInfo.getUsername() + ":" + proxyInfo.getPassword()).getBytes());
			 Map<String, List<String>> headers = new HashMap<String, List<String>>();
		      headers.put("Proxy-Authorization", Collections.singletonList("Basic " + encodedUserPwd));
		      rc.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
		 }
		BaseWebServiceClientProxy proxy2 = createClientProxy(protocol, host,
				port, proxy);
		return proxy2;
	}
	
	private  Service getEdgeLocalService(URL localWsdl, QName qname) throws SOAPFaultException {
		return Service.create(localWsdl, qname);
	}
	
	private static URL getEdgeConsoleLocalWSDL() {
		return BaseWebServiceFactory.class.getResource("EdgeServiceConsoleImpl.wsdl");
	}
}

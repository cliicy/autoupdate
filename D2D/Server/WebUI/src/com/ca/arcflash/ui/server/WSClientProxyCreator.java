package com.ca.arcflash.ui.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.serviceinfo.ServiceInfo;
import com.ca.arcflash.serviceinfo.ServiceInfoConstants;
import com.ca.arcflash.serviceinfo.ServiceInfoList;
import com.ca.arcflash.webservice.AxisFault;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.IFlashCommonService;
import com.ca.arcflash.webservice.WebServiceClientProxy;

public class WSClientProxyCreator {

	public static WebServiceClientProxy getFlassService(String protocol, String host, int port, String serviceID,
			ServiceInfo serviceInfo, int connectTimeout, int requestTimeout) throws SOAPFaultException {

		return getFlassService(protocol, host, port, serviceID, connectTimeout, requestTimeout);

	}

	public static WebServiceClientProxy getFlassService(String protocol, String host, int port, String serviceID,
			ServiceInfo serviceInfo) throws SOAPFaultException {

		return getFlassService(protocol, host, port, serviceID, -1, -1);

	}

	public static WebServiceClientProxy getFlassService(String protocol, String host, int port, String serviceID)
			throws SOAPFaultException {

		return getFlassService(protocol, host, port, serviceID, -1, -1);

	}

	public static WebServiceClientProxy getFlashVGRTService(String protocol, String host, int port)
			throws SOAPFaultException {

		return getFlassService(protocol, host, port, ServiceInfoConstants.SERVICE_ID_D2D_VGRT, -1, -1);

	}

	public static WebServiceClientProxy getFlassService(String protocol, String host, int port)
			throws SOAPFaultException {

		return getFlassService(protocol, host, port, ServiceInfoConstants.SERVICE_ID_D2D_PROPER, -1, -1);

	}

	public static WebServiceClientProxy getFlashServiceV2(String protocol, String host, int port)
			throws SOAPFaultException {

		return getFlassService(protocol, host, port, ServiceInfoConstants.SERVICE_ID_D2D_V2, -1, -1);

	}

	public static WebServiceClientProxy getFlashServiceV3(String protocol, String host, int port)
			throws SOAPFaultException {

		return getFlassService(protocol, host, port, ServiceInfoConstants.SERVICE_ID_D2D_V3, -1, -1);

	}

	public static WebServiceClientProxy getFlashServiceV2(String protocol, String host, int port, int connectTimeout,
			int requestTimeout) throws SOAPFaultException {

		return getFlassService(protocol, host, port, ServiceInfoConstants.SERVICE_ID_D2D_V2, connectTimeout,
				requestTimeout);

	}

	public static WebServiceClientProxy getFlassService(String protocol, String host, int port, String serviceID,
			int connectTimeout, int requestTimeout) throws SOAPFaultException {
		int defaultTimeout = getWebServiceTimeout();
		if (connectTimeout <= 0) {
			connectTimeout = defaultTimeout;
		}
		if (requestTimeout <= 0) {
			requestTimeout = defaultTimeout;
		}
		String wsHost = replaceIfLocalHost(host);

		URL wsdlLocation = getD2DWSDL();

		Service service = Service.create(wsdlLocation, new QName(Constants.Namespace, Constants.ServiceName));

		Object proxy = null;
		// java.lang.Error: Undefined operation name
		Class serviceInterfaceClass = ServiceInfoConstants.getServiceInterfaceClass(serviceID);
		if (serviceInterfaceClass == null) {
			throw new WebServiceException("Service with ID:" + serviceID + " is not supported");
		}

		try {

			proxy = service.getPort(new QName(Constants.Namespace, Constants.PortName), serviceInterfaceClass);

		} catch (Error error) {
			throw AxisFault.fromAxisFault(error.getMessage(), error);
		}

		if (protocol != null && !protocol.endsWith(":")) {
			protocol = protocol + ":";
		}

		String endpointAddress = makeEndpointAddress(protocol, wsHost, port, Constants.SERVICE_PART);

		Map<String, Object> rc = ((BindingProvider) proxy).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
		rc.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, Boolean.TRUE);
		rc.put(Constants.CONNECT_TIMEOUT, (connectTimeout <= 0) ? Constants.TIME_OUT_VALUE : connectTimeout);
		rc.put(Constants.REQUEST_TIMEOUT, (requestTimeout <= 0) ? Constants.TIME_OUT_VALUE : requestTimeout);

		// if (Boolean.getBoolean("wsmonitor") &&
		// protocol.equalsIgnoreCase("http:")) {
		// String address = (String)((BindingProvider)
		// proxy).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
		// int indexOf = address.indexOf("/", "http://1".length());
		// String temp = address.substring(0,indexOf+1);
		// address = address.replaceFirst(temp, protocol+"//localhost:4040/");
		// ((BindingProvider)
		// proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
		// address);
		// }

		WebServiceClientProxy proxy2 = new WebServiceClientProxy(port, protocol, proxy);

		return proxy2;

	}

	public static <T> T createWebService(String wsdl, QName serviceName, QName portName,
			Class<T> serviceEndpointInterface) throws SOAPFaultException {
		T proxy = null;

		try {
			Service service = Service.create(new URL(wsdl), serviceName);
			proxy = service.getPort(portName, serviceEndpointInterface);
		} catch (MalformedURLException e) {
			throw AxisFault.fromAxisFault(e.getMessage(), e);
		} catch (Error error) {
			throw AxisFault.fromAxisFault(error.getMessage(), error);
		}

		Map<String, Object> rc = ((BindingProvider) proxy).getRequestContext();
		rc.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, Boolean.TRUE);
		int defaultTimeout = getWebServiceTimeout();
		rc.put(Constants.CONNECT_TIMEOUT, defaultTimeout);
		rc.put(Constants.REQUEST_TIMEOUT, defaultTimeout);

		return proxy;
	}

	public static ServiceInfoList getServiceInfoList(String protocol, String host, int port) {
		if (protocol != null && !protocol.endsWith(":"))
			protocol = protocol + ":";

		String wsHost = replaceIfLocalHost(host);
		String listPath = protocol + "//" + wsHost + ":" + port + ServiceInfoConstants.SERVICE_LIST_PATH;
		HttpURLConnection connection = null;
		BufferedReader rd = null;
		try {
			URL url = new URL(listPath);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			connection.setReadTimeout(Constants.TIME_OUT_VALUE);
			connection.connect();
			if (200 != connection.getResponseCode()) {
				throw new WebServiceException(FlashServiceErrorCode.Common_Service_FAIL_TO_GETLIST);
			}

			rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line + '\n');
			}
			return unmarshal(sb.toString(), ServiceInfoList.class);
		} catch (MalformedURLException e) {
			throw new WebServiceException(e.getMessage(), e);
		} catch (IOException e) {
			throw new WebServiceException(e.getMessage(), e);
		} catch (JAXBException e) {
			throw new WebServiceException(e.getMessage(), e);
		} finally {
			try {
				if (connection != null)
					connection.disconnect();
				connection = null;
				if (rd != null)
					rd.close();
			} catch (Throwable t) {
			}
		}

	}

	public static <T> T unmarshal(String source, Class<T> type) throws JAXBException {
		return JAXB.unmarshal(new StringReader(source), type);
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
			Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();

			while (ni.hasMoreElements()) {
				NetworkInterface element = ni.nextElement();
				Enumeration<InetAddress> ips = element.getInetAddresses();
				while (ips.hasMoreElements()) {
					InetAddress ip = ips.nextElement();
					if (ip.getHostAddress() != null && !localArr.contains(ip.getHostAddress())) {
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
			return "localhost";
		}

		boolean isLocal = false;

		for (String h : localArr) {
			if (host.equalsIgnoreCase(h)) {
				isLocal = true;
				break;
			}
		}
		if (isLocal) {
			return "localhost";
		}

		if (host != null) {
			return host.toLowerCase();
		}

		return host;
	}

	public static ServiceInfo getFeatureServiceInfo(String serviceID, ServiceInfoList serviceInfo) {
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

	private static URL getD2DWSDL() {
		URL wsdlLocaltion = WebServiceClientProxy.class.getResource(Constants.WSDL_NAME);
		return wsdlLocaltion;
	}

	private static String makeEndpointAddress(String protocol, String host, int port, String servicePart) {

		if (servicePart != null && !servicePart.startsWith("/")) {
			servicePart = "/" + servicePart;
		}
		String endpointAddress = protocol + "//" + host + ":" + port + servicePart;
		return endpointAddress;

	}

	/**
	 * The default timeout value for D2D webservice request is 5 minutes, after
	 * that, there will be "Operation time out error..", if some operations like
	 * MountRecoveryPoint takes longer time than 5 minutes, user can add this
	 * registry key to change the timeout value.
	 * 
	 * @return the time out value.
	 */
	private static int getWebServiceTimeout() {
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(Constants.REGISTRY_WEBSERVICE);
			String value = registry.getValue(handle, Constants.REG_REQUEST_TIMEOUT);
			if (value == null || value.isEmpty()) {
				return Constants.TIME_OUT_VALUE;
			} else {
				int timeout = Integer.parseInt(value);
				if (timeout > 25)
					timeout = 25;
				if (timeout * 60 * 1000 < Constants.TIME_OUT_VALUE) {
					return Constants.TIME_OUT_VALUE;
				} else {
					return timeout * 60 * 1000;
				}
			}
		} catch (Exception e) {
			return Constants.TIME_OUT_VALUE;
		} finally {
			try {
				registry.closeKey(handle);
			} catch (Exception e) {

			}

		}
	}

	public static Service getRawD2DService() {
		URL wsdlLocation = getD2DWSDL();
		Service service = Service.create(wsdlLocation, new QName(Constants.Namespace, Constants.ServiceName));
		return service;
	}

	public static <T extends IFlashCommonService> WebServiceClientProxy getD2DServiceProxy1(Service service,
			String protocol, String hostName, int port, Class<T> sei) {

		Object proxy = null;

		try {
			proxy = service.getPort(new QName(Constants.Namespace, Constants.PortName), sei);
		} catch (Error error) {
			throw AxisFault.fromAxisFault(error.getMessage(), error);
		}

		int defaultTimeout = getWebServiceTimeout();
		int connectTimeout = defaultTimeout;
		int requestTimeout = defaultTimeout;

		String wsHost = replaceIfLocalHost(hostName);
		if (protocol != null && !protocol.endsWith(":")) {
			protocol = protocol + ":";
		}

		String endpointAddress = makeEndpointAddress(protocol, wsHost, port, Constants.SERVICE_PART);

		Map<String, Object> rc = ((BindingProvider) proxy).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
		rc.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, Boolean.TRUE);
		rc.put(Constants.CONNECT_TIMEOUT, (connectTimeout <= 0) ? Constants.TIME_OUT_VALUE : connectTimeout);
		rc.put(Constants.REQUEST_TIMEOUT, (requestTimeout <= 0) ? Constants.TIME_OUT_VALUE : requestTimeout);

		WebServiceClientProxy proxy2 = new WebServiceClientProxy(port, protocol, proxy);

		return proxy2;
	}
}

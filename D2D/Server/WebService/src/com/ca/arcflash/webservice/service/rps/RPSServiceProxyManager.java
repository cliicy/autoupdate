package com.ca.arcflash.webservice.service.rps;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.rps.webservice.RPSWebServiceClientProxy;
import com.ca.arcflash.rps.webservice.RPSWebServiceFactory;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4D2D;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class RPSServiceProxyManager {
	//clients cached mapped by RPS protocolhostName:port
	private static Map<String, IRPSService4D2D> clients = 
			new Hashtable<String, IRPSService4D2D>();
	private static final Logger logger = Logger.getLogger(RPSServiceProxyManager.class);
	
	public static IRPSService4D2D getRPSServiceClient(String hostName, String userName, 
				String password, int port, String protocol, String loginUUID) throws ServiceException {
		IRPSService4D2D client = clients.get(getKey(hostName, protocol, port));
		if(client == null){
			client = getNewClient(hostName, userName, password, port, protocol, loginUUID);
			return client;
		} else {
			try {
				client.validateUser(userName, password, "");
			} catch (WebServiceException e) {
				if (validateByUUID4LocalHost(client, e, hostName)) {
					return client;
				} else if (!StringUtil.isEmptyOrNull(loginUUID)
						&& validateRpsByUUID(client, e, hostName, loginUUID)) {
					return client;
				} else {
					String showMesg = "";
					if (e instanceof SOAPFaultException) {
						SOAPFaultException fault = (SOAPFaultException) e;
						String m = fault.getFault().getFaultString();
						logger.debug(m);
						String mesg = m.replace(
								"Client received SOAP Fault from server: ", "");
						showMesg = mesg
								.replace(
										" Please see the server log to find more detail regarding exact cause of the failure.",
										"");
						showMesg = hostName + ": \"" + showMesg + "\"";
					} else {
						showMesg = e.getMessage();
						showMesg = hostName + ": \"" + showMesg + "\"";
						if (showMesg.contains("HTTP transport error")) {
							showMesg = hostName;
							logger.error(e.getCause() + ", " + e.getMessage());
						}
					}
					showMesg = String.format(WebServiceMessages
							.getResource("cannotConnectService"),
							new Object[] { showMesg });
					throw new ServiceException(showMesg, FlashServiceErrorCode.Backup_RPS_Get_Policy_Failed);	
					//throw e;
				}
			} catch (Exception e) {
				client = getNewClient(hostName, userName, password, port,
						protocol, loginUUID);
			}
			return client;
		}
	}
	
	private static IRPSService4D2D getNewClient(String hostName, String userName, 
			String password, int port, String protocol, String loginUUID) throws ServiceException {
		if(logger.isDebugEnabled())
			logger.debug("get new client for " + hostName + 
					" protocol is " + protocol
					+ " port is " + port);
		
		RPSWebServiceClientProxy proxy = null;
		try {
			proxy = RPSWebServiceFactory.getRPSService4D2D(
					protocol, hostName, port);
		} catch(WebServiceException e) {
			logger.error("get new client for " + hostName + 
					" protocol is " + protocol
					+ " port is " + port
					+ " failed", e);
			ServiceUtils.processWebServiceException(e, hostName);
			throw new ServiceException(
					WebServiceMessages.getResource("cannotConnectService", new Object[]{hostName}),
						FlashServiceErrorCode.Common_CannotConnectRPSService);
		} catch(Throwable t) {
			logger.error("get new client for " + hostName + 
					" protocol is " + protocol
					+ " port is " + port
					+ " failed", t);
			throw new WebServiceException("Failed to get webservice client", t);
		}
		
		IRPSService4D2D client = proxy.getServiceForD2D();
	
		try {
			client.validateUser(userName, password, "");
		}catch(WebServiceException e) {
			if (validateByUUID4LocalHost(client, e, hostName)) {
				return client;
			} else if (!StringUtil.isEmptyOrNull(loginUUID)
					&& validateRpsByUUID(client, e, hostName, loginUUID)) {
				return client;
			} else {
				String showMesg = "";
				if (e instanceof SOAPFaultException) {
					SOAPFaultException fault = (SOAPFaultException) e;
					String m = fault.getFault().getFaultString();
					logger.debug(m);
					String mesg = m.replace(
							"Client received SOAP Fault from server: ", "");
					showMesg = mesg
							.replace(
									" Please see the server log to find more detail regarding exact cause of the failure.",
									"");
					showMesg = hostName + ": \"" + showMesg + "\"";
				} else {
					showMesg = e.getMessage();
					showMesg = hostName + ": \"" + showMesg + "\"";
					if (showMesg.contains("HTTP transport error")) {
						showMesg = hostName;
						logger.error(e.getCause() + ", " + e.getMessage());
					}
				}
				showMesg = String.format(
						WebServiceMessages.getResource("cannotConnectService"),
						new Object[] { showMesg });
				throw new ServiceException(showMesg,
						FlashServiceErrorCode.Backup_RPS_Get_Policy_Failed);

			}
		}
		
		clients.put(getKey(hostName, protocol, port), client);
		logger.debug("get new webservice client for " + hostName);
		return client;
	}
	
	public static String makeRPSServiceURL(String hostName, String protocol, int port, String wsdl){
		StringBuilder sb = new StringBuilder(protocol);
		if(!protocol.endsWith(":"))
			sb.append(":");
		sb.append("//");
		sb.append(hostName);
		sb.append(":");
		sb.append(port);
		sb.append(wsdl);
		return sb.toString();
	}
	
	private static String getKey(String hostName, String protocol, int port){
		if(protocol != null && !protocol.endsWith(":")){
			protocol += ":";
		}
		StringBuffer sb = new StringBuffer();
		sb.append(protocol);
		sb.append(hostName);
		sb.append(":");
		sb.append(port);
		return sb.toString();
	}
	
	private static boolean validateRpsByUUID(IRPSService4D2D client, WebServiceException e, String host, String uuid) {
		logger.debug("Call validateUserByUUID if it's local host " + host);
		if(e instanceof SOAPFaultException) {
			String errorCode = ((SOAPFaultException)e).getFault().getFaultCodeAsQName().getLocalPart();
			if(errorCode != null && errorCode.endsWith(FlashServiceErrorCode.Login_WrongCredential)){
				try {
					return client.validateUserByUUID(uuid) == 0;
				}catch(Exception ee){
					logger.error("Failed  to validate user by uuid", ee);
					return false;
				}
			}
		}
		return false;
	}
	
	private static boolean validateByUUID4LocalHost(IRPSService4D2D client,
			WebServiceException e, String host) {
		logger.debug("Call validateUserByUUID if it's local host " + host);
		if (isLocalHost(host)) {
			CommonService commonService = CommonService.getInstance();
			return validateRpsByUUID(client, e, host, commonService
					.getNativeFacade().decrypt(commonService.getLoginUUID()));
		}
		return false;
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
					if (ip.getHostAddress() != null && !localArr.contains(ip.getHostAddress())) {
						localArr.add(ip.getHostAddress());
					}
				}
			}
		} catch (SocketException e) {
		}
	}

	private static boolean isLocalHost(String host) {
		if (localArr == null || localArr.isEmpty()) {
			initLocalArr();
		}

		if (host == null || host.length() == 0) {
			return true;
		}
		
		for (String h : localArr) {
			if (host.equalsIgnoreCase(h)) {
				return true;
			}
		}
		return false;
	}

	public static IRPSService4D2D getServiceByHost(RpsHost host)
			throws ServiceException {
		String rpsHostName = host.getRhostname();
		String rpsUserName = host.getUsername();
		String rpsPassword = host.getPassword();
		int rpsPort = host.getPort();
		String protocol = host.isHttpProtocol() ? "http:" : "https:";
		return getRPSServiceClient(rpsHostName, rpsUserName, rpsPassword,
				rpsPort, protocol, host.getUuid());
	}
	
	public static IRPSService4D2D getRPSServiceClient(String hostName, String userName, String password, int port, String protocol) throws ServiceException {
		IRPSService4D2D client = clients.get(getKey(hostName, protocol, port));
		try {
			if(client == null){
				RPSWebServiceClientProxy proxy = RPSWebServiceFactory.getRPSService4D2D(protocol, hostName, port);
				client = proxy.getServiceForD2D();
			} 
			client.validateUser(userName, password, "");
		} catch (WebServiceException e) {
			logger.error(e);
			ServiceUtils.processWebServiceException(e, hostName);
			throw e;
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		clients.put(getKey(hostName, protocol, port), client);
		return client;
	}

}

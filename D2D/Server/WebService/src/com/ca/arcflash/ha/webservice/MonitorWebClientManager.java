package com.ca.arcflash.ha.webservice;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.ha.utils.LRUHashMap;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.serviceinfo.ServiceInfoConstants;
import com.ca.arcflash.webservice.FlashServiceImpl;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.WebServiceFactory;
import com.ca.arcflash.webservice.replication.ManualConversionUtility;
import com.ca.arcflash.webservice.service.HAService;

/*
 * This manager will manage client used to connect Monitor in VCM
 */

public class MonitorWebClientManager {
	
	private static final Logger logger = Logger.getLogger(MonitorWebClientManager.class);
	
	private static Map<String, WebServiceClientProxy> clientProxy = new LRUHashMap<String, WebServiceClientProxy>(5);
	private static Map<String, String> monitorUUID = new LRUHashMap<String, String>(5);
	
	private static final int CONNECT_TIMEOUT = 600*1000; //connection timeout is 10 mins
	private static final int REQUEST_TIMEOUT = 1800*1000; //request timeout is 30 mins
	
	private static final String REQUEST_TIMEOUT_KEY = "com.sun.xml.ws.request.timeout";
	
	public static boolean setRequestTimeout(WebServiceClientProxy client, int requestTimeout) {
		try {
			requestTimeout = (requestTimeout <= 0) ? REQUEST_TIMEOUT : requestTimeout;
			BindingProvider bindingProvider = client.getFlashService(BindingProvider.class);
			bindingProvider.getRequestContext().put(REQUEST_TIMEOUT_KEY, requestTimeout);
		} catch (Exception e) {
			logger.info("Since the WebService is a local service, it's not neccessary to set the client request timeout.");
			return false;
		}
		
		logger.info(String.format("Set SOAP request timeout of WebServiceClientProxy to %d minutes successfully.",
						requestTimeout / 1000 / 60));
		return true;
	}
	
	public static boolean isRemoteService(WebServiceClientProxy client)
	{
		try {
			return null != client.getFlashService(BindingProvider.class);
		} catch (Exception e) {
			logger.info("WebService is a local service.");
		}
		return false;
	}
	
	public static void resetRequestTimeout(WebServiceClientProxy client) {
		try {
			BindingProvider bindingProvider = client.getService(BindingProvider.class);
			bindingProvider.getRequestContext().put(REQUEST_TIMEOUT_KEY, REQUEST_TIMEOUT);
		} catch (Exception e) {
			logger.error("Failed to reset the request timeout of WebServiceClientProxy.");
		}
	}

	public static WebServiceClientProxy getMonitorWebClientProxy(HeartBeatJobScript jobScript){
		
		if(jobScript == null){
			logger.error("heartbeatjobscript is null.");
			return null;
		}
		
		String key = getKey4Client(jobScript);
		
		WebServiceClientProxy proxy = null;
		
		synchronized (clientProxy) {
			if (clientProxy.containsKey(key)) {
				try {
					proxy = clientProxy.get(key);
					proxy.getServiceV2().validateUserByUUID(monitorUUID.get(key));
				} catch (Exception e) {
					// If d2d on monitor is reinstalled, uuid will change
					// user username and password to validate again
					proxy = validateByUser(jobScript);
				}
			} else {
				proxy = validateByUser(jobScript);
			}
		}
		return proxy;
	}
	
	public static WebServiceClientProxy getMonitorWebClientProxy(String afGuid){
		
		if(afGuid == null){
			logger.error("afguid is null.");
			return null;
		}
		
		HeartBeatJobScript jobScript = HAService.getInstance().getHeartBeatJobScript(afGuid);
		
		if(jobScript == null){
			logger.error("heartbeatjobscript is null. guid = " + afGuid);
			return null;
		}
		
		return getMonitorWebClientProxy(jobScript);

	}
	
	public static WebServiceClientProxy getMonitorWebClientProxy4HeartBeat(HeartBeatJobScript jobScript){
		
		WebServiceClientProxy proxy = null;
		
		String key = getKey4Client(jobScript);
		synchronized (clientProxy) {
			if (clientProxy.containsKey(key)) {
				proxy = clientProxy.get(key);
			} else {
				proxy = validateByUser(jobScript);
			}
		}
		return proxy;
		
	}
	
	private synchronized static WebServiceClientProxy validateByUser(HeartBeatJobScript jobScript){
		WebServiceClientProxy proxy = null;
		String key = getKey4Client(jobScript);
		
		String currentUUID = HAService.getInstance().retrieveCurrentAuthUUID(true);
		if (ManualConversionUtility.isVSBWithoutHASupport(jobScript)) {
			if (jobScript.getVirtualType() == VirtualizationType.VMwareESX
					|| jobScript.getVirtualType() == VirtualizationType.VMwareVirtualCenter
					|| StringUtil.isEmptyOrNull(jobScript.getHeartBeatMonitorHostName())) {
				proxy = getLocalService();
				clientProxy.put(key, proxy);
				monitorUUID.put(key, currentUUID);
				return proxy;
			}
		}
		
		String protocol = jobScript.getHeartBeatMonitorProtocol();
		if(!protocol.endsWith(":")){
			protocol += ":";
		}
		String host = jobScript.getHeartBeatMonitorHostName();
		int port = jobScript.getHeartBeatMonitorPort();
		
		proxy = WebServiceFactory.getFlassService(protocol,host,port,ServiceInfoConstants.SERVICE_ID_D2D_R16_5,CONNECT_TIMEOUT,REQUEST_TIMEOUT);
		
		String domainUsername = jobScript.getHeartBeatMonitorUserName();
		String username = HACommon.getUserFromUsername(domainUsername);
		String domain = HACommon.getDomainFromUsername(domainUsername);
		domain = (domain==""?host:domain);
		String password = jobScript.getHeartBeatMonitorPassword();

		String uuid = proxy.getFlashServiceR16_5().EstablishTrust(username, password, domain);
		uuid = HAService.getInstance().getNativeFacade().decrypt(uuid);
		if (currentUUID != null && currentUUID.equals(uuid)) {
			proxy = getLocalService();
		}
		clientProxy.put(key, proxy);
		monitorUUID.put(key, uuid);
		
		return proxy;
		
	}
	
	
	public static String getMonitorUUID(HeartBeatJobScript jobScript){
		
		String key = getKey4Client(jobScript);
		return monitorUUID.get(key);
	}
	
	public static String getKey4Client(HeartBeatJobScript jobScript){
		
		String protocol = jobScript.getHeartBeatMonitorProtocol();
		if(!protocol.endsWith(":")){
			protocol += ":";
		}
		String host = jobScript.getHeartBeatMonitorHostName();
		int port = jobScript.getHeartBeatMonitorPort();
		
		String key = protocol + "-" + host + "-" + port;
		
		return key;
		
	}

	private static WebServiceClientProxy getLocalService() {
		FlashServiceImpl service = new FlashServiceImpl();
		service.setLocalCheckSession(true);
		return new WebServiceClientProxy(service);
	}
	
	public static boolean isRVCMMonitorLocalhost(HeartBeatJobScript heartBeatJobScript) {
		boolean rvcmMonitorIsLocalhost = false;
		if (heartBeatJobScript != null && ManualConversionUtility.isVSBWithoutHASupport(heartBeatJobScript)) {
			String key = getKey4Client(heartBeatJobScript);
			if (!clientProxy.containsKey(key)) {
				validateByUser(heartBeatJobScript);
			}
			String monitorUUID = getMonitorUUID(heartBeatJobScript);
			String currentUUID = HAService.getInstance().retrieveCurrentAuthUUID(true);
			if (currentUUID != null && monitorUUID != null) {
				rvcmMonitorIsLocalhost = currentUUID.equals(monitorUUID);
			}
		}
		return rvcmMonitorIsLocalhost;
	}
}

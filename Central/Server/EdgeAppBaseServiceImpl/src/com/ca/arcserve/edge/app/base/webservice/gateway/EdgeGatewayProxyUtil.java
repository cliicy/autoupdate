package com.ca.arcserve.edge.app.base.webservice.gateway;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcserve.edge.app.base.jni.BaseWSJNI;
import com.ca.arcserve.edge.app.base.util.WindowsRegistry;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayProxyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayProxyType;
import com.ca.arcserve.edge.webservice.jni.model.HttpProxySettings;

public class EdgeGatewayProxyUtil {

	private static Logger logger = Logger.getLogger( EdgeGatewayProxyUtil.class );
	private static final String BROKER_HKEY_LOCAL_MACHINE = "HKEY_LOCAL_MACHINE";
	
	public static String getBrokerProxyParam(){
		StringBuffer brokerParam = new StringBuffer();
		String proxyType = getProxyType();
		if(proxyType != null && proxyType.equalsIgnoreCase("1")){// user proxy
			String proxyHost = getProxyHost();
			String proxyPort = getProxyPort();
			if(StringUtil.isEmptyOrNull(proxyHost) || StringUtil.isEmptyOrNull(proxyPort))
				return "";
			brokerParam.append("&proxyHost=").append(proxyHost).append("&proxyPort=").append(proxyPort);
			String requireAccount = getProxyRequireAccount();
			if(requireAccount != null && requireAccount.equalsIgnoreCase("1")){
				String proxyUserName = getProxyUserName();
				String proxyPassword = getProxyPassword();
				if(!StringUtil.isEmptyOrNull(proxyUserName))
					brokerParam.append("&proxyUser=").append(proxyUserName).append("&proxyPassword=").append(proxyPassword);
			}
		}else if(proxyType != null && proxyType.equalsIgnoreCase("0")){// Use browser proxy settings (for IE and Chrome only) 
			String proxy[] = getIEProxy(null, null);
			if(proxy != null && proxy.length > 1){
				String proxyHost = proxy[0];
				String proxyPort = proxy[1];
				if(StringUtil.isEmptyOrNull(proxyHost) || StringUtil.isEmptyOrNull(proxyPort))
					return "";
				brokerParam.append("&proxyHost=").append(proxyHost).append("&proxyPort=").append(proxyPort);
			}
		}
		return brokerParam.toString();
	}
	
	public static GatewayProxyInfo getRegistyProxyInfo(){
		GatewayProxyInfo proxyInfo = new GatewayProxyInfo();
		String proxyType = getProxyType();
		if(proxyType == null)// local gateway
			return null;
		if(proxyType.equalsIgnoreCase("1")){// remote gateway user proxy
			String proxyHost = getProxyHost();
			String proxyPort = getProxyPort();
			if(StringUtil.isEmptyOrNull(proxyHost) || StringUtil.isEmptyOrNull(proxyPort))
				return null;
			proxyInfo.setServer(proxyHost);
			proxyInfo.setPort(Integer.valueOf(proxyPort));
			String requireAccount = getProxyRequireAccount();
			if(requireAccount != null && requireAccount.equalsIgnoreCase("1")){
				String proxyUserName = getProxyUserName();
				if(!StringUtil.isEmptyOrNull(proxyUserName)){
					proxyInfo.setUsername(proxyUserName);
					String proxyPassword = getProxyPassword();
					proxyInfo.setPassword(proxyPassword);
				}
			}
		}else if(proxyType != null && proxyType.equalsIgnoreCase("0")){// remote gateway IE proxy
			String proxy[] = getIEProxy(null, null);
			if(proxy != null && proxy.length > 1){
				String proxyHost = proxy[0];
				String proxyPort = proxy[1];
				if(StringUtil.isEmptyOrNull(proxyHost) || StringUtil.isEmptyOrNull(proxyPort))
					return null;
				proxyInfo.setServer(proxyHost);
				proxyInfo.setPort(Integer.valueOf(proxyPort));
			}
		}
		return proxyInfo;
	}
	
	public static GatewayProxyInfo getIEProxyInfo(String gatewayAdmin, String gatewayPS){
		GatewayProxyInfo proxyInfo = new GatewayProxyInfo();
		proxyInfo.setProxyType( GatewayProxyType.IEProxy );
		String proxy[] = getIEProxy(gatewayAdmin, gatewayPS);
		if(proxy != null && proxy.length > 1){
			String proxyHost = proxy[0];
			String proxyPort = proxy[1];
			if(!StringUtil.isEmptyOrNull(proxyHost) && !StringUtil.isEmptyOrNull(proxyPort)){
				proxyInfo.setServer(proxyHost);
				proxyInfo.setPort(Integer.valueOf(proxyPort));
			}	
		}
		return proxyInfo;
	}
	
	private static String getProxyType(){
		return getRegistyInfo(makeFilePath(BROKER_HKEY_LOCAL_MACHINE, WindowsRegistry.KEY_NAME_ROOT_PROXY), WindowsRegistry.VALUE_PROXY_TYPE);
	}
	
	private static String getProxyHost(){
		return getRegistyInfo(makeFilePath(BROKER_HKEY_LOCAL_MACHINE, WindowsRegistry.KEY_NAME_ROOT_PROXY), WindowsRegistry.VALUE_PROXY_SERVER);
	}
	
	private static String getProxyPort(){
		return getRegistyInfo(makeFilePath(BROKER_HKEY_LOCAL_MACHINE, WindowsRegistry.KEY_NAME_ROOT_PROXY), WindowsRegistry.VALUE_PROXY_PORT);
	}
	
	private static String getProxyUserName(){
		return getRegistyInfo(makeFilePath(BROKER_HKEY_LOCAL_MACHINE, WindowsRegistry.KEY_NAME_ROOT_PROXY), WindowsRegistry.VALUE_PROXY_USERNAME);
	}
	
	private static String getProxyPassword(){
		try{
			byte[] value = WSJNI.GetRegBinaryValue(WindowsRegistry.KEY_NAME_ROOT_PROXY, WindowsRegistry.VALUE_PROXY_PASSWORD);
			if(value != null){
				byte[] db = WSJNI.AFDecryptBinary(value);
				if(db == null)
					return "";
				String ds = new String(db, "UTF-16LE");
				return ds.trim();
			} 
		}catch (Exception e) {
			logger.error("EdgeBrokerKeyStoreUtils getProxyPassword failed," + e);
		}
		return "";
	}
	
	private static String getProxyRequireAccount(){
		return getRegistyInfo(makeFilePath(BROKER_HKEY_LOCAL_MACHINE, WindowsRegistry.KEY_NAME_ROOT_PROXY), WindowsRegistry.VALUE_PROXY_REQUIREACCOUNT);
	}
	
	private static String getRegistyInfo(String key, String name){
		try {
			Process p = Runtime.getRuntime().exec("reg QUERY \"" + key + "\" /v \"" + name + "\"");
			BufferedReader in = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
			String out = "";
			while((out = in.readLine()) != null){
				if(out.matches("(.*)\\s+REG_(.*)"))
					break;
			}
			in.close();
			p.destroy();
			if(out == null)
				return null;
			String r[] = out.split("    ");
			if(r == null || r.length < 4)
				return null;
			return registyValueToString(r[2], r[3]);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	private static String registyValueToString(String type, String value){
		if(type.equalsIgnoreCase("REG_SZ")){
			return value;
		}
		if(type.equalsIgnoreCase("REG_QWORD") || type.equalsIgnoreCase("REG_DWORD")){
			long result = 0;
	    	char c[] = value.toCharArray();
	    	for(int i = 2; i < c.length; i++){
	    		result = result * 16 + Character.digit( c[i], 16 );
	    	}
	    	return String.valueOf(result);
		}
		return null;
	}
	
	private static String[] getIEProxy(String gatewayAdmin, String gatewayPS){
		try {
			HttpProxySettings IEProxySetting = new HttpProxySettings();
			BaseWSJNI.getIEProxySettings(gatewayAdmin, gatewayPS, IEProxySetting);
			if(IEProxySetting != null){
				logger.info("IE proxy:" + IEProxySetting);
				if(StringUtil.isEmptyOrNull(IEProxySetting.getServer()) || IEProxySetting.getPort() == 0)
					return getIEProxyByJVM();
				return new String[]{IEProxySetting.getServer(), String.valueOf(IEProxySetting.getPort())};
			}
		}catch(Exception e){
			logger.error("get IE proxy failed. " + e);
		}
		return null;
    
	}
	
	private static String[] getIEProxyByJVM(){
		logger.info("get IE proxy by JVM into");
		System.setProperty("java.net.useSystemProxies","true");
		try {
			List<Proxy> proxys = ProxySelector.getDefault().select(new URI("http://www.yahoo.com/"));
			Iterator<Proxy> p = proxys.iterator();
			while(p.hasNext()){
				Proxy proxy = p.next();
				InetSocketAddress addr = (InetSocketAddress) proxy.address();
				if(addr == null){
					logger.info("No proxy by JVM");
				}else{
					String hostname = addr.getHostName();
					int port = addr.getPort();
					logger.info("IE proxy by JVM, hostname: " + hostname + ", port: " + port);
					return new String[]{hostname, String.valueOf(port)};
				}
			}
		} catch (Exception e) {
			logger.error("get IE proxy by JVM failed. " + e);
		}
		return null;
	}
	
	private static String makeFilePath( String folderPath, String fileName )
	{
		if (!folderPath.endsWith( "\\" ))
			folderPath += "\\";
		return folderPath + fileName;
	}
}

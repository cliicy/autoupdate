package com.ca.arcserve.edge.app.base.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.prefs.Preferences;

import com.ca.arcserve.edge.app.base.dllloader.DllLoader;
import com.ca.arcserve.edge.app.base.jni.BaseWSJNI;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;

public class WindowsRegistry {
	
	private static final WindowsRegistryNames REGISTRY_NAMES = MessageReader.loadMessages(WindowsRegistryNames.class);
	
	//NEW
	public static final String KEY_NAME_UDP_ROOT			=	REGISTRY_NAMES.keyUdpRoot();
	public static final String KEY_NAME_ROOT				=	REGISTRY_NAMES.keyRoot();
	public static final String KEY_NAME_ROOT_CM				=	REGISTRY_NAMES.keyCPMRoot();
	public static final String KEY_NAME_ROOT_REPORT			=	REGISTRY_NAMES.keyReportRoot();
	public static final String KEY_NAME_ROOT_VCM			=	REGISTRY_NAMES.keyVCMRoot();
	public static final String KEY_NAME_ROOT_VSPHERE		=	REGISTRY_NAMES.keyHBBURoot();
	public static final String KEY_NAME_ROOT_WEBSERVER		=	REGISTRY_NAMES.keyWebServerRoot();
	public static final String KEY_NAME_ROOT_MESSAGESERVICE =	REGISTRY_NAMES.keyMessageServiceRoot();
	public static final String KEY_NAME_ROOT_PROXY          =	REGISTRY_NAMES.keyProxyRoot();
	public static final String KEY_NAME_ROOT_AGENT_WS	    =	REGISTRY_NAMES.keyAgentWebServiceRoot();
	
	// D2D and ARCserve Backup synchronize frequency register key by lijwe02
	public static final String D2D_UPDATE_FREQUENCY_KEY		=	REGISTRY_NAMES.d2dUpdateFrequency();
	public static final String ASBU_UPDATE_FREQUENCY_KEY	=	REGISTRY_NAMES.asbuUpdateFrequency();
	// ARCserve Node register root key
	public final static String ARCSERVE_NODE_REGISTRY_ROOTKEY	=	REGISTRY_NAMES.keyARCserveNode();
	
	public static final String VALUE_UDP_HOME_PATH			=	REGISTRY_NAMES.udpHomePath();
	public static final String VALUE_NAME_PATH				=	REGISTRY_NAMES.installationPath();
	public static final String VALUE_NAME_ADMIN_USER		=	REGISTRY_NAMES.adminUser();
	public static final String VALUE_NAME_ADMIN_PASSWORD	=	REGISTRY_NAMES.adminPassword();
	
	public static final String CONSOLE_FQDN_NAME = REGISTRY_NAMES.consoleFqdnName();
	
	public static final String VALUE_NAME_WEBSERVER_VERSION	=	REGISTRY_NAMES.webServerVersion();
	public static final String VALUE_NAME_WEBSERVER_PATH	=	REGISTRY_NAMES.webServerPath();
	public static final String VALUE_NAME_WEBSERVER_JREPATH	=	REGISTRY_NAMES.webServerJREPath();
	public static final String VALUE_NAME_WEBSERVER_PORT	=	REGISTRY_NAMES.webServerPort();
	public static final String VALUE_NAME_WEBSERVER_URL		=	REGISTRY_NAMES.webServerUrl();
	public static final String VALUE_NAME_WEBSERVER_UPDATE	=	REGISTRY_NAMES.WebServerUpdate();
	
	public static final String VALUE_NAME_APP_VERSION		=	REGISTRY_NAMES.appVersion();
	public static final String VALUE_NAME_APP_PATH			=	REGISTRY_NAMES.appPath();
	public static final String VALUE_NAME_APP_UPDATE		=	REGISTRY_NAMES.appUpdate();
	public static final String VALUE_NAME_UpdateExitCode	=	REGISTRY_NAMES.updateExitCode();
	public static final String VALUE_NAME_RestartServiceAfterPatch = REGISTRY_NAMES.restartServiceAfterPatch();
	public static final String VALUE_NAME_APP_NEWSFEED      =   REGISTRY_NAMES.appNewsFeed();
	public static final String VALUE_NAME_APP_SOCIALNETWORKING      =   REGISTRY_NAMES.appSocialNetworking();
	public static final String VALUE_NAME_APP_VIDEO      =   REGISTRY_NAMES.appVideo();
	public static final String VALUE_NAME_UPDATE_VERSION_NUMBER 	= REGISTRY_NAMES.updateVersionNumber();
	public static final String VALUE_NAME_UPDATE_BUILD_NUMBER 	= REGISTRY_NAMES.updateBuildNumber();
	public static final String VALUE_NAME_DEPLOY_AGREE_FLAG         = REGISTRY_NAMES.deployAgreeFlag();
	public static final String VALUE_NAME_DEPLOY_SHOWLICENSE_FLAG   = REGISTRY_NAMES.deployShowLicenseFlag();
	public static final String VALUE_NAME_DEPLOY_MAX_THREAD_COUNT   = REGISTRY_NAMES.deployMaxThreadCount();
	public static final String VALUE_NAME_POLICY_AGREE_FLAG         = REGISTRY_NAMES.policyAgreeFlag();
	public static final String VALUE_NAME_POLICY_SHOWLICENSE_FLAG   = REGISTRY_NAMES.policyShowLicenseFlag();
	
	public static final String VALUE_NAME_D2D_SYNCJOB_INTERVAL		=	REGISTRY_NAMES.d2dSyncJobInterval();
	public static final String VALUE_NAME_D2D_SYNCJOB_DISABLE		=	REGISTRY_NAMES.d2dSyncJobDisable();
	public static final String VALUE_NAME_D2D_SYNCJOB_CONCURRENT	=	REGISTRY_NAMES.d2dSyncJobConcurrent();
	public static final String VALUE_NAME_GUID						=	REGISTRY_NAMES.guid();
	
	public static final String VALUE_NAME_ARCserveSyncPath			=	REGISTRY_NAMES.arcserveSyncPath();
	
	public static final String VALUE_NAME_ShowDeleteNodeUI			=	REGISTRY_NAMES.showDeleteNodeUI();
	
	public static final String VALUE_NAME_UpdateMaximumThreadCount  =   REGISTRY_NAMES.updateMaximumThreadCount();
	
	public static final String ENABLE_IMPORT_REMOTE_NODES_FROM_FILE = REGISTRY_NAMES.enableImportRemoteNodesFromFile();
	
	public static final String KEY_NAME_APM_D2D_VERSION		=	REGISTRY_NAMES.keyAPMD2DVersion();
	public static final String KEY_NAME_APM_EDGE_VERSION	=	REGISTRY_NAMES.keyAPMEdgeVersion();
	public static final String VALUE_NAME_APM_D2D_MAJOR_VERSION	=	REGISTRY_NAMES.apmD2DMajorVersion();
	public static final String VALUE_NAME_APM_D2D_MINOR_VERSION	=	REGISTRY_NAMES.apmD2DMinorVersion();
	
	public static final String VALUE_AUTO_ADDED_LOCAL_AGENT = REGISTRY_NAMES.autoAddedLocalAgentFlag();
	public static final String VALUE_AUTO_ADDED_LOCAL_RPS = REGISTRY_NAMES.autoAddedLocalRpsFlag();
	
	public static final String VALUE_ASBU_REGULARGROUP_FSD = REGISTRY_NAMES.asbuRegularGroupFSDFlag();
	
	public static final String VALUE_BROKER_PROTOCOL = REGISTRY_NAMES.brokerProtocol();
	public static final String VALUE_MESSAGESERVICE_PORT = REGISTRY_NAMES.messageServicePort();
	
	public static final String VALUE_PROXY_SERVER = REGISTRY_NAMES.proxyServer();
	public static final String VALUE_PROXY_PORT = REGISTRY_NAMES.proxyPort();
	public static final String VALUE_PROXY_TYPE = REGISTRY_NAMES.proxyType();
	public static final String VALUE_PROXY_USERNAME = REGISTRY_NAMES.proxyUsername();
	public static final String VALUE_PROXY_PASSWORD = REGISTRY_NAMES.proxyPassword();
	public static final String VALUE_PROXY_REQUIREACCOUNT = REGISTRY_NAMES.proxyRequireAccount();
	
	public static final String KEY_NAME_HA_ROOT						= KEY_NAME_ROOT + REGISTRY_NAMES.keyHARoot();
	public static final String VALUE_NAME_CS_CONNECT_TIMEOUT		= REGISTRY_NAMES.valueCSConnectTimeout();
	public static final String VALUE_NAME_CS_REQUEST_TIMEOUT		= REGISTRY_NAMES.valueCSRequestTimeout();
	public static final String VALUE_NAME_CSDATA_COLLECT_INTERVAL	= REGISTRY_NAMES.valueCSDataCollectInterval();
	public static final String VALUE_NAME_CSDATA_PURGE_INTERVAL		= REGISTRY_NAMES.valueCSDataPurgeInterval();
	public static final String VALUE_NAME_CSDATA_RETENTION_DAYS		= REGISTRY_NAMES.valueCSDataRetentionDays();
	public static final String VALUE_PAGE_SIZE      =   REGISTRY_NAMES.pageSize();
	
	public static final String VALUE_NAME_GATEWAYHOST_HEARTBEAT_INTERVAL = REGISTRY_NAMES.gatewayHostHeartbeatInterval();
	public static final String VALUE_NAME_GATEWAY_UPGRADE_TIMEOUT = REGISTRY_NAMES.gatewayUpgradeTimeout();
	
	private static final Preferences systemRoot = Preferences.systemRoot();
	
	public static final int HKEY_LOCAL_MACHINE = 0x80000002;
	public static final int KEY_ALL_ACCESS = 0xf003f;
	
	private static Method windowsRegOpenKey = null;
	private static Method windowsRegCloseKey = null;
	private static Method windowsRegQueryValueEx = null;
	private static Method windowsRegCreateKeyEx = null;
	private static Method windowsRegSetValueEx = null;
	private static Method windowsRegDeleteKey = null;

	
	static{
		try{
			DllLoader.loadAsNative();
			Class<?> systemClass = systemRoot.getClass();
			
			windowsRegOpenKey = systemClass.getDeclaredMethod(
					"WindowsRegOpenKey", new Class[] { int.class, byte[].class,
							int.class });
			windowsRegOpenKey.setAccessible(true);
	
			windowsRegCloseKey = systemClass.getDeclaredMethod(
					"WindowsRegCloseKey", new Class[] { int.class });
			windowsRegCloseKey.setAccessible(true);
			//BaseWSJNI.class
			windowsRegQueryValueEx = BaseWSJNI.class.getDeclaredMethod(
					"WindowsRegQueryValueEx", new Class[] { int.class,
							byte[].class });
			windowsRegQueryValueEx.setAccessible(true);
			
			windowsRegCreateKeyEx = systemClass.getDeclaredMethod(
					"WindowsRegCreateKeyEx", new Class[] { int.class,
							byte[].class });
			windowsRegCreateKeyEx.setAccessible(true);
			
			windowsRegSetValueEx = systemClass.getDeclaredMethod(
					"WindowsRegSetValueEx", new Class[] { int.class,
							byte[].class, byte[].class });
			windowsRegSetValueEx.setAccessible(true);
			
			windowsRegDeleteKey = systemClass.getDeclaredMethod(  
			          "WindowsRegDeleteKey", new Class[] { int.class,  
			              byte[].class });  
			windowsRegDeleteKey.setAccessible(true); 

		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
	}
	
	public int openKey(String key) throws Exception{
		int[] result= (int[]) windowsRegOpenKey.invoke(systemRoot, new Object[] {
				new Integer(HKEY_LOCAL_MACHINE), stringToByteArray(key), new Integer(KEY_ALL_ACCESS) });
		return result[0];
	}
	
	public int createKey(String keyName) throws Exception{
		int result[] = (int[])windowsRegCreateKeyEx.invoke(null, new Object[] {new Integer(HKEY_LOCAL_MACHINE), stringToByteArray(keyName)});
		if (result[1]!=0 || result[0]==0)
			throw new Exception("Find create specified Key");
		return result[0];
	}
	
	public String getValue(int handle, String key) throws Exception{
		byte[] result = (byte[]) windowsRegQueryValueEx.invoke(systemRoot,
				new Object[] { new Integer(handle), stringToByteArray(key) });
		if (result == null)
			return null;
		String re = BaseWSJNI.JByteArrayToJString(result);
		return re;
	}
	
	/**
	 * get the key value by provide two key path, added by lijwe02 on 2010-11-02
	 * 
	 * @param rootKey
	 *            the value key's parent
	 * @param valueKey
	 *            the value key
	 * @return the value of the specify key
	 * @throws Exception
	 *             error on get the key value
	 */
	public String getValue(String rootKey, String valueKey) throws Exception {
		int handle = openKey(rootKey);
		String keyValue = getValue(handle, valueKey);
		closeKey(handle);
		return keyValue;
	}
	
	public void deleteKey(int handle, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		windowsRegDeleteKey.invoke(systemRoot, new Object[] { new Integer(handle), stringToByteArray(key) });
	}
	
	public void setValue(int handle, String name,  String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		windowsRegSetValueEx.invoke(systemRoot, new Object[] { 
		          new Integer(handle), stringToByteArray(name), stringToByteArray(value)}); 

	}
	
	public void closeKey(int handle) throws Exception{
		windowsRegCloseKey.invoke(systemRoot,
				new Object[] { new Integer(handle) });
	}
	
	private byte[] stringToByteArray(String str)
	{
		byte[] result = new byte[str.length() + 1];
		for (int i = 0; i < str.length(); i++)
		{
			result[i] = (byte) str.charAt(i);
		}
		result[str.length()] = 0;
		return result;
	}
	
	public int getIntValue( int handle, String valueName, int defaultValue )
	{
		try
		{
			String valueString = null;
			
			try
			{
				valueString = this.getValue( handle, valueName );
			}
			catch (Exception e)
			{
				//logger.error( "Error get value from registry. Value name: " + valueName, e );
			}
			
			return (valueString == null) ? defaultValue : Integer.parseInt( valueString );
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}
}
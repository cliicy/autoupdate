package com.ca.arcserve.edge.app.base.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcserve.edge.app.base.dao.IEncrypt;
import com.ca.arcserve.edge.app.base.db.Configuration;
import com.ca.arcserve.edge.app.base.db.GDBCConnection;
import com.ca.arcserve.edge.app.base.db.IConfiguration;
import com.ca.arcserve.edge.app.base.db.ImpersonateException;
import com.ca.arcserve.edge.app.base.jni.BaseWSJNI;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeRegistryInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ServerDate;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceClientProxy;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceFactory;

public class CommonUtil implements IEncrypt{

	public static String udpHome = "C:\\Program Files\\Arcserve\\Unified Data Protection\\";
	public static String BaseEdgeInstallPath = "C:\\Program Files\\Arcserve\\Unified Data Protection\\Management\\";
	public static final String BaseEdgeCONFIGURATION_DIR = "Configuration\\";
	public static final String BaseEdgeBIN_DIR = "BIN\\";
	public static final String BaseDeploymentInstallPath = "Deployment\\D2D\\Install\\";
	public static final String BaseEdgeLog_DIR = "Logs\\";
	public static final String ReportName = "Report";
	public static final String VCMName = "VCM";
	public static final String VSphereName = "VSphere";
	public static String EdgeDefaultUser = null;


	public static final String STRING_SESSION_USERNAME = "com.ca.arcflash.webservice.EdgeServiceImpl.UserName";
	public static final String STRING_SESSION_UUID 	= "com.ca.arcflash.webservice.EdgeServiceImpl.UUID";
	public static final String STRING_SESSION_PASSWORD = "com.ca.arcflash.webservice.EdgeServiceImpl.Password";
	public static final String STRING_SESSION_DOMAIN = "com.ca.arcflash.webservice.EdgeServiceImpl.Domain";
	public static final String STRING_SESSION_IMPORTVSPHEREVMS_PARAMETERS = "com.ca.arcflash.webservice.EdgeServiceImpl.ImportVSphereVMs.Parameters";
	public static final String STRING_SESSION_IMPORTVSPHEREVMS_VMDATA = "com.ca.arcflash.webservice.EdgeServiceImpl.ImportVSphereVMs.VMData";
	public static final String STRING_SESSION_IMPORTVSPHEREVMS_HOSTESXMAP = "com.ca.arcflash.webservice.EdgeServiceImpl.ImportVSphereVMs.HostEsxMap";
	public static final String STRING_SESSION_IMPORTVSPHEREVMS_DUPLICATEDVMS = "com.ca.arcflash.webservice.EdgeServiceImpl.ImportVSphereVMs.DuplicatedVMs";
	public static final String STRING_SESSION_POLICYEDITSESSION = "com.ca.arcflash.webservice.EdgeServiceImpl.PolicyEditSession";
	public static final String STRING_SESSION_CONVERTERUPDATESESSION = "com.ca.arcflash.webservice.EdgeServiceImpl.ConverterUpdateSession";
	public static final String STRING_SESSION_EDGERHASESSION = "com.ca.arcflash.webservice.EdgeRHAServiceImpl.EdgeRHASession";
	public static final String STRING_SESSION_TEMP_LOGEXPORTMARKER ="com.ca.arcserve.webservice.log.LOGExportController.identifier";
	
	public static final String CENTRAL_MANAGER_CONTEXT_PATH = "/management";
	public static final String REPORT_CONTEXT_PATH = "/reporting";
	public static final String VSphere_CONTEXT_PATH = "/hostbasedvmbackup";
	public static final String VCM_CONTEXT_PATH = "/virtualstandby";
	//This file is defined in base impl project, used by vSphereContextListener and VCMContextListener
	public static String daofile = "/com/ca/arcserve/edge/app/base/appdaos/javadb_dao.properties";
	public static final String SERVER_IP_FILE = "server_ip.ini";
	private static Cipher encryptCipher = null;
	private static Cipher decryptCipher = null;

	private static final int DEFAULT_D2D_UPDATE_FREQUENCY = 2;
	private static final int DEFAULT_ASBU_UPDATE_FREQUENCY = 2;
	
	private static boolean hasVersionInfoInited = false;
	private static String versionString = "";
	private static int majorVersion = 0;
	private static int minorVersion = 0;
	private static int buildNumber = 0;
	private static int requiredD2DMajorVersion = 0;
	private static int requiredD2DMinorVersion = 0;
	private static int requiredD2DBuildNumber = 0;
	private static Double requireD2DVersion = 0.0;

	private static final boolean DEFAULT_ENABLE_IMPORT_REMOTE_NODES_FROM_FILE = false;
	
	private static String REPORT_FILE_TEMP_DIR = null;
	
	private static final Logger logger = Logger.getLogger(CommonUtil.class);
	
	static {

		getAllRegistryItems_();

		try {
			
			initVersionInfo();
			
			String seed = "1234567890!@#$%^&*()";
			SecureRandom random = new SecureRandom(seed.getBytes());
			KeyGenerator generator = KeyGenerator.getInstance("DES");
			generator.init(56, random);
			SecretKey key = generator.generateKey();
			encryptCipher = Cipher.getInstance("DES");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);

			decryptCipher = Cipher.getInstance("DES");
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
		}
	}
	
	public static void initVersionInfo()
	{
		if (hasVersionInfoInited)
			return;
		
		versionString = getApplicationRegistryInfo().getAppVersion();
		if (versionString == null) {
			return;
		}
		
		String[] versionFields = versionString.split( "\\." );
		majorVersion = (versionFields.length > 0) ? Integer.parseInt( versionFields[0] ) : 0;
		minorVersion = (versionFields.length > 1) ? Integer.parseInt( versionFields[1] ) : 0;
		buildNumber = (versionFields.length > 2) ? Integer.parseInt( versionFields[2] ) : 0;
		
		requiredD2DMajorVersion = majorVersion;
		requiredD2DMinorVersion = minorVersion;
		requiredD2DBuildNumber = buildNumber;
		requireD2DVersion = Double.parseDouble( requiredD2DMajorVersion + "." + requiredD2DMinorVersion );
		
		hasVersionInfoInited = true;
	}
	
	public static double getRequiredD2DUpdateVersionNumber(){
		String updateVersionString = getApplicationUpdateVersionNumber();
		if (updateVersionString == null)
			updateVersionString = "0";
		double requireD2DUpdateVersionNumber = Double.parseDouble(updateVersionString);
		return requireD2DUpdateVersionNumber;
	}
	
	public static String getVersionString()
	{
		return versionString;
	}

	public static int getMajorVersion()
	{
		return majorVersion;
	}

	public static int getMinorVersion()
	{
		return minorVersion;
	}

	public static int getBuildNumber()
	{
		return buildNumber;
	}

	public static int getRequiredD2DMajorVersion()
	{
		return requiredD2DMajorVersion;
	}

	public static int getRequiredD2DMinorVersion()
	{
		return requiredD2DMinorVersion;
	}

	public static Double getRequireD2DVersion()
	{
		return requireD2DVersion;
	}
	
	public static int getRequiredD2DBuildNumber() {
		return requiredD2DBuildNumber;
	}

	public static void setRequiredD2DBuildNumber(int requiredD2DBuildNumber) {
		CommonUtil.requiredD2DBuildNumber = requiredD2DBuildNumber;
	}

	public static String getRequiredD2DVersionString()
	{
		return requiredD2DMajorVersion + "." + requiredD2DMinorVersion;
	}

	/**
	 *
	 * @param edgeAppType
	 * @return  for example C:\\Program Files\\CA\\ARCserve Edge\\Configuration\\
	 */
	public static String getConfigurationFolder(EdgeApplicationType  edgeAppType){
		switch(edgeAppType ){
		case CentralManagement:
			return BaseEdgeInstallPath+BaseEdgeCONFIGURATION_DIR;
		case Report:
			return BaseEdgeInstallPath+ReportName+"\\"+BaseEdgeCONFIGURATION_DIR;
		case VirtualConversionManager:
			return BaseEdgeInstallPath+VCMName+"\\"+BaseEdgeCONFIGURATION_DIR;
		case vShpereManager:
			return BaseEdgeInstallPath+VSphereName+"\\"+BaseEdgeCONFIGURATION_DIR;
		}
		return BaseEdgeInstallPath+BaseEdgeCONFIGURATION_DIR;
	}
	/**
	 *
	 * @param edgeAppType
	 * @return  for example C:\\Program Files\\CA\\ARCserve Edge\\Logs\\
	 */
	public static String getLogFolder(EdgeApplicationType  edgeAppType){
		switch(edgeAppType ){
		case CentralManagement:
			return BaseEdgeInstallPath+BaseEdgeLog_DIR;
		case Report:
			return BaseEdgeInstallPath+ReportName+"\\"+BaseEdgeLog_DIR;
		case VirtualConversionManager:
			return BaseEdgeInstallPath+VCMName+"\\"+BaseEdgeLog_DIR;
		case vShpereManager:
			return BaseEdgeInstallPath+VSphereName+"\\"+BaseEdgeLog_DIR;
		}
		return BaseEdgeInstallPath+BaseEdgeLog_DIR;
	}
	private static void getAllRegistryItems_() {
		{
			String installPath = "";
			try {

				installPath = CommonUtil.getApplicationRegistryInfo().getHomePath();

				if (installPath != null && !installPath.isEmpty()) {
					int len = installPath.length();
					if (installPath.charAt(len - 1) != '\\')
						installPath += "\\";
					BaseEdgeInstallPath = installPath;
				}
				
				udpHome = CommonUtil.getApplicationRegistryInfo().getUdpHomePath();

			} catch (Exception e) {
				System.out.println("Failed to get Edge install path in CommonUtil!");
			}

			String defaultUser = null;
			try {
				defaultUser = CommonUtil.getApplicationRegistryInfo().getAdminUser();

			} catch (Exception e) {
				System.out.println("Failed to get Edge administrator user in CommonUtil!");
			}
			if(defaultUser == null || defaultUser.isEmpty()) {
				defaultUser = "";
			}
			EdgeDefaultUser = defaultUser;

		}

	}
	public static <T> T unmarshal(String source, Class<T> type)
			throws JAXBException {
		if (StringUtil.isEmptyOrNull(source))
			return null;
		return JAXB.unmarshal(new StringReader(source), type);
	}

	public static <T> T unmarshal(InputStream source, Class<T> type)
			throws JAXBException {
		if (source == null)
			return null;
		return JAXB.unmarshal(source, type);
	}

	public static <T> T unmarshal(File source, Class<T> type)
			throws JAXBException {
		return JAXB.unmarshal(source, type);
	}

	public static String marshal(Object script) throws JAXBException {
		if (script == null)
			return null;
		StringWriter buffer = new StringWriter();
		JAXB.marshal(script, buffer);
		return buffer.toString();
	}

	public static String readFileAsString(String filePath) throws Exception {
		byte[] buffer = new byte[(int) new File(filePath).length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(filePath));
			f.read(buffer);
		} finally {

			if (f != null) {
				try {
					f.close();
				} catch (Exception e) {
				}
			}
		}

		return new String(buffer, "utf-8");
	}

	public static void saveStringToFile(String source, String filePath)
			throws Exception {
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(filePath));
			bos.write(source.getBytes("utf-8"));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (bos != null)
				bos.close();
		}

	}

	/* This function will write string to utf8 file with BOM */
	public static void saveStringToFileWithUTF8BOM(String source, String filePath)
			throws Exception {
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(filePath));
			bos.write(new byte[] {(byte)0xEF , (byte)0xBB , (byte)0xBF});
			bos.write(source.getBytes("utf-8"));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (bos != null)
				bos.close();
		}

	}
	
	public static void saveFile(InputStream in, String filepath)
			throws IOException {
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(filepath));
			byte[] buffer = new byte[1024];
			int read = 0;
			read = in.read(buffer);
			while (-1 != read) {
				bos.write(buffer, 0, read);
				read = in.read(buffer);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (bos != null)
				bos.close();
		}
	}
	public static void prepareTrustAllSSLEnv() throws NoSuchAlgorithmException,
			KeyManagementException, KeyStoreException {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
				.getInstance("SSL");

		sc.init(new javax.net.ssl.X509KeyManager[] {},
				new TrustManager[] { new SelfSignTrustManager(null) },
				new java.security.SecureRandom());

		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
				.getSocketFactory());

	}

	public static String encrypt(String text) {
		if(text==null) return null;
		byte[] doFinal = null;
		try {
			doFinal = text.getBytes("utf-8");
		} catch (UnsupportedEncodingException e2) {
			logger.error(e2.getMessage(), e2);
			return text;
		}
		try {
			doFinal = encryptCipher.doFinal(doFinal);
		} catch (IllegalBlockSizeException e1) {
			logger.error(e1.getMessage(), e1);
			return text;
		} catch (BadPaddingException e1) {
			logger.error(e1.getMessage(), e1);
			return text;
		}
		try {
			StringBuilder sb = new StringBuilder();
			for (Byte b : doFinal) {
				int i = 0xff & b;
				String sub = Integer.toHexString(i);
				if (sub.length() == 0)
					sub = "00";
				else if (sub.length() == 1)
					sub = "0" + sub;
				else if (sub.length() > 2)
					sub = sub.substring(sub.length() - 2);
				sb.append(sub);
			}
			return sb.toString();
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
		return "";
	}

	public static String decrypt(String cypher) {
		if(cypher==null) return null;
		byte[] bytes = new byte[cypher.length() / 2];
		try {
			for (int i = 0; i < bytes.length; i++) {
				String s = cypher.substring(i * 2, i * 2 + 2);
				int temp = Integer.valueOf(s, 16);
				bytes[i] = (byte) (temp & 0xff);
			}
			bytes = decryptCipher.doFinal(bytes);
		} catch (IllegalBlockSizeException e) {
			logger.error(e.getMessage(), e);
			return cypher;
		} catch (BadPaddingException e) {
			logger.error(e.getMessage(), e);
			return cypher;
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
		try {
			return new String(bytes, "utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
			return cypher;
		}
	}

	@Override
	public String decryptString(String value) {
		return decrypt(value);
	}

	@Override
	public String encryptString(String value) {
		return encrypt(value);
	}

	/**
	 * Get the ARCserve Backup's synchronization frequency
	 *
	 * @author lijwe02
	 * @return the frequency got from the registry, default value is
	 *         DEFAULT_ASBU_UPDATE_FREQUENCY
	 */
	public static int getASBUSyncFrequency() {
		String keyValue = null;
		try {
			keyValue = getApplicationExtentionKey(getAppRootKey(EdgeWebServiceContext.getApplicationType())+WindowsRegistry.ARCSERVE_NODE_REGISTRY_ROOTKEY, WindowsRegistry.D2D_UPDATE_FREQUENCY_KEY);
		} catch (Exception e) {
			keyValue = null;
			logger.error(e.getMessage(), e);
		}
		if (keyValue != null && keyValue.trim().length() > 0) {
			return Integer.valueOf(keyValue);
		}
		return DEFAULT_ASBU_UPDATE_FREQUENCY;
	}

	/**
	 * Get the ARCserve D2D's synchronization frequency
	 *
	 * @author lijwe02
	 * @return the frequency got from the registry, default value is
	 *         DEFAULT_D2D_UPDATE_FREQUENCY
	 */
	public static int getD2DSyncFrequency() {
		String keyValue = null;
		try {
			keyValue = getApplicationExtentionKey( getAppRootKey(EdgeWebServiceContext.getApplicationType())+WindowsRegistry.ARCSERVE_NODE_REGISTRY_ROOTKEY, WindowsRegistry.ASBU_UPDATE_FREQUENCY_KEY);
		} catch (Exception e) {
			keyValue = null;
			logger.error(e.getMessage(), e);
		}
		if (keyValue != null && keyValue.trim().length() > 0) {
			return Integer.valueOf(keyValue);
		}
		return DEFAULT_D2D_UPDATE_FREQUENCY;
	}

	public static EdgeRegistryInfo getApplicationRegistryInfo(EdgeApplicationType type){
		EdgeRegistryInfo result = new EdgeRegistryInfo();

		try{
			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(WindowsRegistry.KEY_NAME_ROOT);
			result.setHomePath(registry.getValue(handle, WindowsRegistry.VALUE_NAME_PATH));
			result.setAdminUser(registry.getValue(handle, WindowsRegistry.VALUE_NAME_ADMIN_USER));
			result.setAdminPassword(registry.getValue(handle, WindowsRegistry.VALUE_NAME_ADMIN_PASSWORD));
			registry.closeKey(handle);
			
			handle = registry.openKey(WindowsRegistry.KEY_NAME_UDP_ROOT);
			result.setUdpHomePath(registry.getValue(handle, WindowsRegistry.VALUE_UDP_HOME_PATH));
			registry.closeKey(handle);

			handle = registry.openKey(WindowsRegistry.KEY_NAME_ROOT_WEBSERVER);
			result.setWebServerVersion(registry.getValue(handle, WindowsRegistry.VALUE_NAME_WEBSERVER_VERSION));
			result.setWebServerPath(registry.getValue(handle, WindowsRegistry.VALUE_NAME_WEBSERVER_PATH));
			result.setWebServerJREPath(registry.getValue(handle, WindowsRegistry.VALUE_NAME_WEBSERVER_JREPATH));
			result.setConsoleUrl(registry.getValue(handle, WindowsRegistry.VALUE_NAME_WEBSERVER_URL));
			//result.setWebServerPort(registry.getValue(handle, WindowsRegistry.VALUE_NAME_WEBSERVER_PORT));
			result.setWebServerUpdateWhenStartup(string2Boolean(registry.getValue(handle, WindowsRegistry.VALUE_NAME_WEBSERVER_UPDATE)));
			String webServerUrl = registry.getValue(handle, WindowsRegistry.VALUE_NAME_WEBSERVER_URL);
			setWebServerPortAndProtocol( result, webServerUrl );
			registry.closeKey(handle);

			String rootKey = type==null? getAppRootKey(EdgeWebServiceContext.getApplicationType()):getAppRootKey(type);
			if (rootKey != null){
				handle = registry.openKey(rootKey);
				result.setAppVersion(registry.getValue(handle, WindowsRegistry.VALUE_NAME_APP_VERSION));
				result.setAppPath(registry.getValue(handle, WindowsRegistry.VALUE_NAME_APP_PATH));
				result.setAppUpdateWhenEdgeStartup(string2Boolean(registry.getValue(handle, WindowsRegistry.VALUE_NAME_APP_UPDATE)));
				registry.closeKey(handle);
			}
			
			//get Rps webservice port
			handle = registry.openKey(WindowsRegistry.KEY_NAME_ROOT_AGENT_WS);
			String agentUrl = registry.getValue(handle, WindowsRegistry.VALUE_NAME_WEBSERVER_URL);
			result.setAgentUrl(agentUrl);
			registry.closeKey(handle);
			
		} catch(Throwable e){
			System.out.println(e.getMessage());
		}

		return result;
	}
	
	private static void setWebServerPortAndProtocol( EdgeRegistryInfo edgeInfo, String webServerUrl )
	{
		int index = webServerUrl.indexOf( ":" );
		String protocol = webServerUrl.substring( 0, index );
		
		index = webServerUrl.lastIndexOf( ":" );
		String portStr = webServerUrl.substring( index + 1, webServerUrl.length() );
		
		edgeInfo.setWebServerPort( Integer.parseInt( portStr ) );
		edgeInfo.setWebServerProtocol( protocol );
	}

	public static EdgeRegistryInfo getApplicationRegistryInfo(){
		return getApplicationRegistryInfo(EdgeWebServiceContext.getApplicationType());
	}

	public static String getApplicationExtentionKey(String name){
		WindowsRegistry registry = new WindowsRegistry();
		String rootKey = getAppRootKey(EdgeWebServiceContext.getApplicationType());
		if (rootKey != null){
			try {
				int handle = registry.openKey(rootKey);
				String value=  registry.getValue(handle, name);
				registry.closeKey(handle);
				return value;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		return null;
	}
	
	public static String getApplicationUpdateVersionNumber() {
		WindowsRegistry registry = new WindowsRegistry();
		String rootKey = getAppRootKey(EdgeWebServiceContext.getApplicationType());
		if (rootKey != null){
			try {
				int handle = registry.openKey(rootKey);
				String value = registry.getValue(handle, WindowsRegistry.VALUE_NAME_UPDATE_VERSION_NUMBER);
				registry.closeKey(handle);
				return value;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		return null;
	}
	
	public static String getApplicationUpdateBuildNumber() {
		WindowsRegistry registry = new WindowsRegistry();
		String rootKey = getAppRootKey(EdgeWebServiceContext.getApplicationType());
		if (rootKey != null){
			try {
				int handle = registry.openKey(rootKey);
				String value = registry.getValue(handle, WindowsRegistry.VALUE_NAME_UPDATE_BUILD_NUMBER);
				registry.closeKey(handle);
				return value;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		return null;
	}
	
	public static String getApplicationExtentionKey(EdgeApplicationType app, String name){
		WindowsRegistry registry = new WindowsRegistry();
		String rootKey = getAppRootKey(app);
		if (rootKey != null){
			try {
				int handle = registry.openKey(rootKey);
				String value=  registry.getValue(handle, name);
				registry.closeKey(handle);
				return value;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		return null;
	}

	public static String getApplicationExtentionKey(String key, String name){
		WindowsRegistry registry = new WindowsRegistry();
		String rootKey = key;
		if (rootKey != null){
			try {
				int handle = registry.openKey(rootKey);
				String val = registry.getValue(handle, name);
				registry.closeKey(handle);
				return val;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		return null;
	}

	private static boolean string2Boolean(String source){
		boolean result = false;
		try{
			result = Boolean.parseBoolean(source);
		}catch(Exception e){

		}
		return result;
	}

	public static String getAppRootKey(EdgeApplicationType type){
		if (type == EdgeApplicationType.CentralManagement)
			return WindowsRegistry.KEY_NAME_ROOT_CM;
		else if (type == EdgeApplicationType.Report)
			return WindowsRegistry.KEY_NAME_ROOT_REPORT;
		else if (type == EdgeApplicationType.VirtualConversionManager)
			return WindowsRegistry.KEY_NAME_ROOT_VCM;
		else if (type == EdgeApplicationType.vShpereManager)
			return WindowsRegistry.KEY_NAME_ROOT_VSPHERE;

		return null;
	}
	
	public static BaseWebServiceClientProxy getLinuxD2DForEdgeService(
			String protocol,String hostname,int port) {
		return getLinuxD2DForEdgeService(protocol,hostname,port,0,0);
	}
	
	public static BaseWebServiceClientProxy getLinuxD2DForEdgeService(String protocol,
			String hostname, int port, int connectTimeout, int requestTimeout) {
		hostname = hostname.toLowerCase();
		BaseWebServiceFactory proxy = new BaseWebServiceFactory();
		if ((connectTimeout == 0) && (requestTimeout == 0))
			return proxy.getLinuxImagingWebServiceWithLocalWSDL(protocol, hostname, port);
		else
			return proxy.getLinuxImagingWebServiceWithLocalWSDL(protocol, hostname, port,connectTimeout,requestTimeout);
	}
	
	
	public static boolean isAppInstalled(EdgeApplicationType type){
		EdgeRegistryInfo regInfo = CommonUtil.getApplicationRegistryInfo(type);
		if(regInfo == null)
			return false;
		if( StringUtil.isEmptyOrNull(regInfo.getAppVersion()) )
			return false;
		return true;
	}

	public static void generateUUIDForNecessary(){
		System.out.println(retrieveCurrentAppUUID());
	}

	public static String retrieveCurrentAppUUID() {
		return retrieveCurrentAppUUIDWithDecrypt(true);
	}
	public static String retrieveCurrentAppUUIDWithDecrypt(boolean isNeedDecrypt) {
		WindowsRegistry registry = new WindowsRegistry();
		String uuid = null;
		try {
			String rootKey = CommonUtil.getAppRootKey(EdgeWebServiceContext.getApplicationType());
			int handle = registry.openKey(rootKey);
			if (handle == 0)
				handle = registry.createKey(rootKey);
			registry.closeKey(handle);
			handle = registry.openKey(rootKey);

			uuid = registry.getValue(handle, WindowsRegistry.VALUE_NAME_GUID);
			if (StringUtil.isEmptyOrNull(uuid)){
				uuid = UUID.randomUUID().toString();
				registry.setValue(handle, WindowsRegistry.VALUE_NAME_GUID, BaseWSJNI.AFEncryptString(uuid));
			}
			if (isNeedDecrypt) {					
				uuid = BaseWSJNI.AFDecryptString(uuid);
			}
			registry.closeKey(handle);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return uuid;
	}

	public static ApplicationType getApplicationTypeForD2D(){
		EdgeApplicationType type = EdgeWebServiceContext.getApplicationType();

		if (type == EdgeApplicationType.CentralManagement)
			return ApplicationType.CentralManagement;
		else if (type == EdgeApplicationType.Report)
			return ApplicationType.Report;
		else if (type == EdgeApplicationType.VirtualConversionManager)
			return ApplicationType.VirtualConversionManager;
		else if (type == EdgeApplicationType.vShpereManager)
			return ApplicationType.vShpereManager;

		return null;
	}
	
	public static String getApplicationTitle(EdgeApplicationType type){
		if (type == EdgeApplicationType.CentralManagement)
			return EdgeCMWebServiceMessages.getResource("productNameUPM");
		else if (type == EdgeApplicationType.Report)
			return EdgeCMWebServiceMessages.getResource("productNameUR");
		else if (type == EdgeApplicationType.VirtualConversionManager)
			return EdgeCMWebServiceMessages.getResource("productNameUVS");
		else if (type == EdgeApplicationType.vShpereManager)
			return EdgeCMWebServiceMessages.getResource("productNameUHBVB");
		
		return "";
	}
	
	public static String getApplicationTitle(){
		EdgeApplicationType type = EdgeWebServiceContext.getApplicationType();
		return getApplicationTitle(type);
	}
	
	public static boolean setApplicationExtentionKey(String valueName, String value){
		WindowsRegistry registry = new WindowsRegistry();
		EdgeApplicationType type = EdgeWebServiceContext.getApplicationType();
		String rootKey = getAppRootKey(type);
		int handle = 0;
		
		try{
			handle = registry.openKey(rootKey);
			registry.setValue( handle, valueName, value);
			return true;
		}catch (Exception e) {
			System.out.println( e.getMessage() );
			return false;
		}finally{
			try{
				if( handle != 0 )
					registry.closeKey(handle);
			}catch (Exception e) {
				System.out.println( e.getMessage() );
			}
		}
		
	}
	
	public static void setApplicationExtentionKey(String keyPath, String valueName, String value){
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		
		try{
			handle = registry.openKey(keyPath);
			registry.setValue( handle, valueName, value);
		}catch (Exception e) {
			System.out.println( e.getMessage() );
		}finally{
			try{
				if( handle != 0 )
					registry.closeKey(handle);
			}catch (Exception e) {
				System.out.println( e.getMessage() );
			}
		}
		
	}
	
	public static String getApplicationContextPath(EdgeApplicationType edgeAppType){
		switch(edgeAppType ){
		case CentralManagement:
			return CENTRAL_MANAGER_CONTEXT_PATH;
		case Report:
			return REPORT_CONTEXT_PATH;
		case VirtualConversionManager:
			return VCM_CONTEXT_PATH;
		case vShpereManager:
			return VSphere_CONTEXT_PATH;
		}
		return "";
	}

	//Please use EdgeCommonUtil.getLocalFqdnName()
	//Or IcommonService.getConsoleHost()
//	/**
//	 * This method will return the FQDN name of console
//	 * Firstly get the FQDN name from registry which is set by user
//	 * Then use java API get FQDN name if user not set.
//	 * 
//	 * @return FQDN name
//	 */
//	public static String getLocalHost(){
//		try {
//			String fqdnNameBeSet = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT,WindowsRegistry.CONSOLE_FQDN_NAME);
//			if(!StringUtil.isEmptyOrNull(fqdnNameBeSet))
//				return fqdnNameBeSet;
//			
//			InetAddress addr = InetAddress.getLocalHost();
//			String fQDN = addr.getCanonicalHostName();
//			if (fQDN != null) {
//				int indx = fQDN.indexOf('.');
//				if (indx > 0) {
//					return fQDN;
//				}
//			}
//			return addr.getHostName();
//		} catch (Exception e) {
//			return "";
//		}
//	}
	
	public static int[] convertIntegerList2Array(List<Integer> list){
		int[] array = new int[list.size()];
		
		for (int i=0;i<list.size();i++)
			array[i] = list.get(i);
			
		return array;
	}
	
	/**
	 * Get enable import remote nodes from file flag from registry
	 * 
	 * @author lijwe02
	 * @return We will hide the import rempote nodes from file menu item, if user configure this value to 1, then we
	 *         will show the menu item
	 */
	public static boolean isEnableImportRemoteNodesFromFile() {
		String keyValue = null;
		try {
			keyValue = getApplicationExtentionKey(getAppRootKey(EdgeWebServiceContext.getApplicationType()),
					WindowsRegistry.ENABLE_IMPORT_REMOTE_NODES_FROM_FILE);
		} catch (Exception e) {
			keyValue = null;
			logger.error(e.getMessage(), e);
		}
		if (keyValue != null && keyValue.trim().length() > 0) {
			try {
				return Integer.valueOf(keyValue) == 1;
			} catch (NumberFormatException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return DEFAULT_ENABLE_IMPORT_REMOTE_NODES_FROM_FILE;
	}
	
	/**
	 * Get the directory for export report
	 * 
	 * @return The directory path for export report
	 */
	private static Object reportFileLock=new Object();
	public static String getReportFileTempDir() {
		synchronized (reportFileLock) {
		if (REPORT_FILE_TEMP_DIR == null) {
			REPORT_FILE_TEMP_DIR = BaseEdgeInstallPath + ReportName + File.separator + "Temp" + File.separator;
			File dir = new File(REPORT_FILE_TEMP_DIR);
			System.out.println("CommonUtil.getReportFileTempDir(): filePath = " + REPORT_FILE_TEMP_DIR);
			if (!dir.exists() && !dir.mkdirs()) {
				System.out.println("CommonUtil.getReportFileTempDir(): Create dir: " + REPORT_FILE_TEMP_DIR
						+ " failed.");
				REPORT_FILE_TEMP_DIR = "C:\\Windows\\Temp\\";
			}
		}
		}
		return REPORT_FILE_TEMP_DIR;
	}
	
	 public static void recursiveDelFolder( String folderName ){
		 
		 File file = new File( folderName );
		 if( file.exists() ){
			File[] childrenFiles = file.listFiles();
			if( childrenFiles ==null ){ //file
				file.delete();
			}
			else if( childrenFiles.length ==0 ){ //empty folder
				file.delete();
			}
			else {
				for( File childFile : childrenFiles  ){
					recursiveDelFolder( childFile.getAbsolutePath() );
				}
				file.delete();
			}
		}		
	 }
	/**
	 * Get the directory of the system font file
	 * 
	 * @return The directory of the system font file
	 */
	public static String getSystemFontDir() {
		String winDir = System.getenv("WINDIR");
		if (StringUtil.isEmptyOrNull(winDir)) {
			winDir = "C:/Windows";
		}
		File fontDir = new File(winDir, "Fonts");
		if (fontDir.exists()) {
			return fontDir.getAbsolutePath();
		}
		return null;
	}
	
	public static Date toDate(ServerDate serverDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		
		calendar.set(serverDate.getYear(), serverDate.getMonth(), serverDate.getDate(), 
				serverDate.getHour(), serverDate.getMinute(), serverDate.getSecond());
		
		return calendar.getTime();
	}
	
	public static ServerDate fromServerDate(Date date) {
		ServerDate serverDate = new ServerDate();
		serverDate.setYear(date.getYear());
		serverDate.setMonth(date.getMonth());
		serverDate.setDate(date.getDate());
		serverDate.setHour(date.getHours());
		serverDate.setMinute(date.getMinutes());
		serverDate.setSecond(date.getSeconds());
		return serverDate;
	}
	
	public static Date getLastMinutes(int minutes) {
		return getLastDate(Calendar.MINUTE, minutes);
	}
	
	public static Date getLastHours(int hours) {
		return getLastDate(Calendar.HOUR, hours);
	}
	
	public static Date getLastDays(int days) {
		return getLastDate(Calendar.DATE, days);
	}
	
	public static Date getLastDate(int unit, int num) {
		Calendar cal = Calendar.getInstance(); 
		cal.add(unit, -num); 
		return cal.getTime();
	}
	
	public static Date getSomeDate(int year, int month, int date) {
		Calendar cal = Calendar.getInstance(); 
		cal.clear();
		cal.set(year, month, date);
		return cal.getTime();
	}
	
	public static String getServerIpFromFile(){
		String ip = "";
		String ipFile = CommonUtil.getConfigurationFolder(EdgeApplicationType.CentralManagement) + SERVER_IP_FILE;
		
		File file = new File(ipFile);
		if(file.exists()){
			try {
				ip = readFileAsString(ipFile);
			} catch (Exception e) {
				
			}
		}
		return ip;
	}
	
	public static boolean isLocalHost(String hostname) {
		if (hostname == null || hostname.isEmpty()) {
			return false;
		}
		
		hostname = hostname.trim();
		
		List<String> localhostIPList = getLocalhostIPList();

		for (String h : localhostIPList) {
			if (hostname.equalsIgnoreCase(h)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static List<String> getLocalhostIPList() {
		List<String> localhostIPList = new ArrayList<String>();
		localhostIPList.add("localhost");
		localhostIPList.add("127.0.0.1");
		
		InetAddress localhostAddress = null;
		
		try {
			localhostAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			return localhostIPList;
		}
		
		localhostIPList.add(localhostAddress.getHostAddress());
		localhostIPList.add(localhostAddress.getHostName());

		String fQDN = localhostAddress.getCanonicalHostName();

		if (fQDN != null) {
			int indx = fQDN.indexOf('.');
			if (indx > 0) {
				localhostIPList.add("localhost" + fQDN.substring(indx));
				localhostIPList.add(fQDN);
			}
		}
		
		Enumeration<NetworkInterface> ni;
		
		try {
			ni = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			return localhostIPList;
		}
		
		while (ni.hasMoreElements()) {
			NetworkInterface element = ni.nextElement();
			Enumeration<InetAddress> ips = element.getInetAddresses();
			while (ips.hasMoreElements()) {
				InetAddress ip = ips.nextElement();
				localhostIPList.add(ip.getHostAddress());
			}
		}
		
		String fqdnNameBeSet = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT,WindowsRegistry.CONSOLE_FQDN_NAME);
		if(!StringUtil.isEmptyOrNull(fqdnNameBeSet) && !localhostIPList.contains(fqdnNameBeSet)){
			localhostIPList.add(fqdnNameBeSet);
		}
		
		return localhostIPList;
	}
	
	//Not contains localhost and 127.0.0.1
	//Contains ipv4 list and hostname
	public static List<String> getConnectNameList() {
		List<String> connectNameList = new ArrayList<String>();
		Enumeration<NetworkInterface> ni = null;
		try {
			ni = NetworkInterface.getNetworkInterfaces();
			while (ni.hasMoreElements()) {
				NetworkInterface element = ni.nextElement();
				Enumeration<InetAddress> ips = element.getInetAddresses();
				while (ips.hasMoreElements()) {
					InetAddress ip = ips.nextElement();
					if(ip != null 
							&&(ip instanceof Inet4Address)
							&&!ip.getHostAddress().equals("127.0.0.1")){
						connectNameList.add(ip.getHostAddress());
					}	
				}
			}
		} catch (SocketException e) {
			//do nohing
		}
			
		InetAddress localhostAddress = null;
		try {
			localhostAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			return connectNameList;
		}
			
		String fQDN = localhostAddress.getCanonicalHostName();
		if (fQDN != null) {
			int indx = fQDN.indexOf('.');
			if (indx > 0) {
				connectNameList.add(fQDN);
			}
		}
		connectNameList.add(localhostAddress.getHostName());
		
		String fqdnNameBeSet = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT,WindowsRegistry.CONSOLE_FQDN_NAME);
		if(!StringUtil.isEmptyOrNull(fqdnNameBeSet) && !connectNameList.contains(fqdnNameBeSet)){
			connectNameList.add(fqdnNameBeSet);
		}
		
		return connectNameList;
	}
		
	
	public static String getFQDN(){
		String fqdnNameBeSet = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT,WindowsRegistry.CONSOLE_FQDN_NAME);
		if(!StringUtil.isEmptyOrNull(fqdnNameBeSet)){
			return fqdnNameBeSet;
		}
		
		String fqdn = "localhost";
		InetAddress localhostAddress = null;
		try {
			localhostAddress = InetAddress.getLocalHost();
			fqdn = localhostAddress.getCanonicalHostName();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
		}
		return fqdn;
	}
	
	public static String getHostName(){
		String hostname = "localhost";
		InetAddress localhostAddress = null;
		try {
			localhostAddress = InetAddress.getLocalHost();
			hostname = localhostAddress.getHostName();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
		}
		return hostname;
	}
	
	public static String getIpAddress(){
		String ip = "127.0.0.1";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
		}
		return ip;
	}
	
	public static List<String> getFqdnNamebyHostNameOrIp(String hostnameOrIp){
		if(StringUtil.isEmptyOrNull(hostnameOrIp))
			return null;
		List<String> fqdnNameList = new ArrayList<String>();
		try {
			InetAddress addr = InetAddress.getByName(hostnameOrIp);
			String hostnameCanonical = addr.getCanonicalHostName();
			if (hostnameCanonical != null
					&& hostnameCanonical.indexOf('.') > 0) {
				fqdnNameList.add(hostnameCanonical);
			}

		} catch (Exception e) {
			// do nothing
		}
		return fqdnNameList;
	}
	
	public static String getHostNameByIp(String hostnameOrIp){//return short name
		if(StringUtil.isEmptyOrNull(hostnameOrIp))
			return null;
		String hostname = "";
		try {
			InetAddress addr = InetAddress.getByName(hostnameOrIp);
			hostname = addr.getHostName();
			if(hostname!=null && hostname.contains(".")){
				String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
			    //String ipv6Pattern = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";
				if(!hostname.matches(ipv4Pattern))
					return hostname.substring(0,hostname.indexOf("."));
			}
		} catch (Exception e) {
			// do nothing
		}
		return hostname;
	}
	
	public static boolean checkSession( HttpServletRequest request )
	{
		HttpSession session = request.getSession();
		if ((session == null) ||
			((session.getAttribute( CommonUtil.STRING_SESSION_USERNAME ) == null) &&
			 (session.getAttribute( CommonUtil.STRING_SESSION_UUID ) == null)))
			return false;
		
		return true;
	}
	
	public static boolean checkDBAvaliable() throws ImpersonateException {
		boolean isDBAvailable = false;
  		IConfiguration configuration = CommonUtil.getConConfiguration();
  		GDBCConnection con = null;
  		try {
  			con = GDBCConnection.getGDBCConnection(configuration.getDbURI().substring(configuration.getDbURI().indexOf("://") + 3, configuration.getDbURI().indexOf(";")), configuration.getDbUser(), configuration.getDbPassword());
  			isDBAvailable = true;
  		} catch(ImpersonateException e) {
  			throw e;
  		} catch(Exception e) {
  			isDBAvailable = false;
  			logger.error("database is not avaliable", e);
  		} finally{
  			if(con!=null) con.close();
  		}
  		return isDBAvailable;
	}
	
	public static IConfiguration getConConfiguration(){
		try {
			return Configuration.getInstance(getConfigurationFolder(EdgeApplicationType.CentralManagement) + Configuration.DBCONFIGURATION_FILE);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static String listToCommaString(List<String> strList){
		if(strList==null || strList.isEmpty())
			return "";
		StringBuilder commaString = new StringBuilder();
		for (String str : strList) {
			commaString.append(",");
			commaString.append(str);
		}
		commaString.append(",");
		return commaString.toString();
	}
	
	public static boolean isSameHost(String host1, String host2){
		if(host1==null && host2==null){
			return true;
		}
		if(host1==null && host2 != null){
			return false;
		}
		if(host1 != null && host2 == null){
			return false;
		}
		if(host1.contains(".") && !host2.contains(".")){
			String shotName = host1.substring(0,host1.indexOf("."));
			return shotName.equalsIgnoreCase(host2);
		}
		if(!host1.contains(".") && host2.contains(".")){
			String shotName = host2.substring(0,host2.indexOf("."));
			return shotName.equalsIgnoreCase(host1);
		}
		return host1.equalsIgnoreCase(host2);
	}
	
	public static String encryptByJNI(String text){
		return BaseWSJNI.AFEncryptString(text);
	}
	
	public static List<String> getAllIps(){
		List<String> ips = new ArrayList<String>();
		try {
			InetAddress[] allMyIps = InetAddress.getAllByName(CommonUtil.getHostName());
			if (allMyIps != null && allMyIps.length > 0) {
				for (int i = 0; i < allMyIps.length; i++) {
					if (!allMyIps[i].getHostAddress().contains(":")) {
						logger.info("current server has ip: " + allMyIps[i].getHostAddress());
						ips.add(allMyIps[i].getHostAddress());
					}
				}
			}
		} catch (UnknownHostException e) {
			logger.error("error retrieving server ips", e);
		}
		return ips;
	}
}

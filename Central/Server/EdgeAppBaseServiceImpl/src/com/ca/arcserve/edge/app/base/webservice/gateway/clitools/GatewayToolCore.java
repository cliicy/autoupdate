package com.ca.arcserve.edge.app.base.webservice.gateway.clitools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.jni.BaseWSJNI;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.SelfSignTrustManager;
import com.ca.arcserve.edge.app.base.webservice.IEdgeGatewayService;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayProxyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayProxyType;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayRegistrationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayUnregistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.gateway.EdgeGatewayBean;
import com.ca.arcserve.edge.app.base.webservice.gateway.GatewayRegistrationString;
import com.ca.arcserve.edge.app.base.webservice.gateway.clitools.GatewayToolCore.FailedToRestartGatewayServiceErrorParam.RestartServicePhase;
import com.ca.arcserve.edge.app.base.webservice.gateway.settings.GatewayMessageServiceSettings;
import com.ca.arcserve.edge.app.base.webservice.jni.WSJNI;
import com.ca.arcserve.edge.webservice.jni.model.EdgeAccount;

public class GatewayToolCore
{
	public static enum GatewayToolError
	{
		UnknownError						( -1,	"gatewayTool_UnknownError" ),
		InvalidRegistrationString			( -2,	"gatewayTool_InvalidRegistrationString" ),
		FailedToGetLocalHostName			( -3,	"gatewayTool_FailedToGetLocalHostName" ),
		FailedToConnectConsole				( -4,	"gatewayTool_FailedToConnectConsole" ),
		FailedToLoginConsole				( -5,	"gatewayTool_FailedToLoginConsole" ),
		InvalidRegistrationInfo				( -6,	"gatewayTool_InvalidRegistrationInfo" ),
		SpecifiedGatewayNotFound			( -7,	"gatewayTool_SpecifiedGatewayNotFound" ),
		CannotRegisterToLocalGateway		( -8,	"gatewayTool_CannotRegisterToLocalGateway" ),
		GatewayRegisteredToAnotherHost		( -9,	"gatewayTool_GatewayRegisteredToAnotherHost" ),
		GatewayHostNotRegistered			( -10,	"gatewayTool_GatewayHostNotRegistered" ),
		FailedToOperateDatabase				( -11,	"gatewayTool_FailedToOperateDatabase" ),
		FailedToWriteGatewaySettingsFile	( -12,	"gatewayTool_FailedToWriteGatewaySettingsFile" ),
		FailedToDeleteGatewaySettingsFile	( -13,	"gatewayTool_FailedToDeleteGatewaySettingsFile" ),
		FailedToConnectToGatewayHost		( -14,	"gatewayTool_FailedToConnectToGatewayHost" ),
		FailedToNotifyGatewayHost			( -15,	"gatewayTool_FailedToNotifyGatewayHost" ),
		InvalidUsername						( -16,	"gatewayTool_InvalidUsername" ),
		InvalidPassword						( -17,	"gatewayTool_InvalidPassword" ),
		WrongCredential						( -16,	"gatewayTool_WrongCredential" ),
		NotAdministrator					( -17,	"gatewayTool_NotAdministrator" ),
		InvalidProxyInfo					( -18,	"gatewayTool_InvalidProxyInfo" ),
		FailedToSaveProxyInfo				( -19,	"gatewayTool_FailedToSaveProxyInfo" ),
		FailedToRestartGatewayService		( -20,	"gatewayTool_FailedToRestartGatewayService" ),
		CannotRegisterWhenUpgrading			( -21,	"gatewayTool_CannotRegisterWhenUpgrading" ),
		UnknownConsoleError					( -22,	"gatewayTool_UnknownConsoleError" );
		
		private int errorCode;
		private String errorMessage;
		
		GatewayToolError( int errorCode, String errorMessage )
		{
			this.errorCode = errorCode;
			this.errorMessage = errorMessage;
		}

		public int getErrorCode()
		{
			return errorCode;
		}

		public String getErrorMessage()
		{
			return errorMessage;
		}
	}
	
	public static abstract class GatewayToolErrorParam
	{
		public abstract Object[] getErrorMessageParam();
	}
	
	public static class FailedToConnectConsoleErrorParam extends GatewayToolErrorParam
	{
		private String consoleHostName;
		private int consolePort;
		private String consoleProtocol;
		
		public FailedToConnectConsoleErrorParam(
			String consoleHostName, int consolePort, String consoleProtocol )
		{
			this.consoleHostName = consoleHostName;
			this.consolePort = consolePort;
			this.consoleProtocol = consoleProtocol;
		}

		public String getConsoleHostName()
		{
			return consoleHostName;
		}

		public int getConsolePort()
		{
			return consolePort;
		}

		public String getConsoleProtocol()
		{
			return consoleProtocol;
		}

		@Override
		public Object[] getErrorMessageParam()
		{
			return new Object[] { consoleHostName, consolePort, consoleProtocol };
		}
	}
	
	public static class GatewayRegisteredToAnotherHostErrorParam extends GatewayToolErrorParam
	{
		private String occupierHostName;
		private String siteName;
		private String localHostName;
		
		public GatewayRegisteredToAnotherHostErrorParam(
			String occupierHostName, String siteName, String localHostName )
		{
			this.occupierHostName = occupierHostName;
			this.siteName = siteName;
			this.localHostName = localHostName;
		}

		public String getOccupierHostName()
		{
			return occupierHostName;
		}

		public String getSiteName()
		{
			return siteName;
		}

		public String getLocalHostName()
		{
			return localHostName;
		}

		@Override
		public Object[] getErrorMessageParam()
		{
			return new Object[] { occupierHostName, siteName, localHostName };
		}
	}
	
	public static class CannotRegisterWhenUpgradingErrorParam extends GatewayToolErrorParam
	{
		private String siteName;
		private String currentHostName;
		
		public CannotRegisterWhenUpgradingErrorParam( String siteName, String currentHostName )
		{
			this.siteName = siteName;
			this.currentHostName = currentHostName;
		}

		public String getSiteName()
		{
			return siteName;
		}

		public String getCurrentHostName()
		{
			return currentHostName;
		}

		@Override
		public Object[] getErrorMessageParam()
		{
			return new Object[] { siteName, currentHostName };
		}
	}
	
	public static class FailedToSaveProxyInfoErrorParam extends GatewayToolErrorParam
	{
		private String attributeName;
		
		public FailedToSaveProxyInfoErrorParam( String attributeName )
		{
			this.attributeName = attributeName;
		}

		public String getAttributeName()
		{
			return attributeName;
		}

		@Override
		public Object[] getErrorMessageParam()
		{
			return new Object[] { attributeName };
		}
	}
	
	public static class FailedToRestartGatewayServiceErrorParam extends GatewayToolErrorParam
	{
		public enum RestartServicePhase { Stop, Start }
		
		private RestartServicePhase phase;
		
		public FailedToRestartGatewayServiceErrorParam( RestartServicePhase phase )
		{
			this.phase = phase;
		}

		@Override
		public Object[] getErrorMessageParam()
		{
			return new Object[] { phase };
		}
	}
	
	public static class GatewayToolException extends Exception
	{
		private static final long serialVersionUID = 1385003626778922718L;

		private GatewayToolError error;
		private GatewayToolErrorParam errorParam;
		
		public GatewayToolException( GatewayToolError error )
		{
			this( error, null );
		}
		
		public GatewayToolException( GatewayToolError error, GatewayToolErrorParam errorParam )
		{
			if (error == null)
				error = GatewayToolError.UnknownError;
			this.error = error;
			this.errorParam = errorParam;
		}
		
		public int getErrorCode()
		{
			return error.getErrorCode();
		}
		
		public String getErrorMessage()
		{
			Object[] messageParam = (this.errorParam == null) ? null : this.errorParam.getErrorMessageParam();
			return EdgeCMWebServiceMessages.getMessage( error.getErrorMessage(), messageParam );
		}

		public GatewayToolErrorParam getErrorParam()
		{
			return errorParam;
		}
	}
	
	public static class RegistrationString
	{
		private String requestUrlPattern;
		private String queryStringPattern;
		private String requestHost;
		private int requestPort;
		private String requestProtocol;
		private String registrationKey;
		private String consoleUuid;
		private String gatewayUuid;

		public String getRequestUrlPattern()
		{
			return requestUrlPattern;
		}

		public void setRequestUrlPattern( String requestUrlPattern )
		{
			this.requestUrlPattern = requestUrlPattern;
		}

		public String getQueryStringPattern()
		{
			return queryStringPattern;
		}

		public void setQueryStringPattern( String queryStringPattern )
		{
			this.queryStringPattern = queryStringPattern;
		}

		public String getRequestHost()
		{
			return requestHost;
		}

		public void setRequestHost( String requestHost )
		{
			this.requestHost = requestHost;
		}

		public int getRequestPort()
		{
			return requestPort;
		}

		public void setRequestPort( int requestPort )
		{
			this.requestPort = requestPort;
		}

		public String getRequestProtocol()
		{
			return requestProtocol;
		}

		public void setRequestProtocol( String requestProtocol )
		{
			this.requestProtocol = requestProtocol;
		}

		public String getRegistrationKey()
		{
			return registrationKey;
		}

		public void setRegistrationKey( String registrationKey )
		{
			this.registrationKey = registrationKey;
		}

		public String getConsoleUuid()
		{
			return consoleUuid;
		}

		public void setConsoleUuid( String consoleUuid )
		{
			this.consoleUuid = consoleUuid;
		}

		public String getGatewayUuid()
		{
			return gatewayUuid;
		}

		public void setGatewayUuid( String gatewayUuid )
		{
			this.gatewayUuid = gatewayUuid;
		}
	}
	
	private static Logger logger = Logger.getLogger( GatewayToolCore.class );
	private static GatewayToolCore instance = new GatewayToolCore();
	
	private String udpHome = "";
	private String gatewayHostName = "";
	private int gatewayPort = 0;
	private String gatewayProtocol = "";
	private boolean isInitialized = false;
	
	private GatewayToolCore()
	{	
	}
	
	public static GatewayToolCore getInstance()
	{
		return instance;
	}
	
	public void initialize( boolean getUdpInfoFromRegistry, boolean loadNativeFacade ) throws Exception
	{
		logger.info( "Initializing GatewayToolCore (getUdpInfoFromRegistry: " + getUdpInfoFromRegistry + ") ..." );
		
		System.loadLibrary( "GatewayTool" );
		try {
			System.loadLibrary( "ASNative" );
		} catch (Throwable t) {
			logger.error("Failed to load ASNative.dll", t);
		}
		if(loadNativeFacade){
			int x = BaseWSJNI.setEdgeInstallPathEnv();
			if(x!=0){
				System.err.println("Failed to set Edge install path!");
			}
			try {
				System.loadLibrary("NativeFacade");
			} catch (Throwable t) {
				logger.error("Failed to load NativeFacade.dll", t);
			}
		}
		if (getUdpInfoFromRegistry)
			getUdpInfoFromRegistry();
		prepareTrustAllSSLEnv();
		
		this.isInitialized = true;
		
		logger.info( "GatewayToolCore was initialized." );
	}
	
	public void prepareTrustAllSSLEnv() throws
		NoSuchAlgorithmException,
		KeyManagementException,
		KeyStoreException
	{
		HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier()
			{
				@Override
				public boolean verify( String hostname, SSLSession session )
				{
					return true;
				}
			}
		);
		
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
		
		sc.init(
			new javax.net.ssl.X509KeyManager[] {},
			new TrustManager[] { new SelfSignTrustManager( null ) },
			new java.security.SecureRandom() );
		
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(
			sc.getSocketFactory() );
	}
	
	public boolean isInitialized()
	{
		return isInitialized;
	}

	private String PRODUCT_HOME = "SOFTWARE\\Arcserve\\Unified Data Protection\\Management";
	private String REGKEY_PROXY = "SOFTWARE\\Arcserve\\Unified Data Protection\\Management\\Proxy";
	
	public void getUdpInfoFromRegistry()
	{
		long result;
		
		GatewayToolNative.LongValue keyHandle = new GatewayToolNative.LongValue();
		result = GatewayToolNative.regOpenKey( GatewayToolNative.HKEY_LOCAL_MACHINE, PRODUCT_HOME, keyHandle );
		
		GatewayToolNative.StringValue strValue = new GatewayToolNative.StringValue();
		result = GatewayToolNative.regQueryStringValue( keyHandle.getValue(), "Path", strValue );
		this.udpHome = strValue.getValue();
		
		GatewayToolNative.LongValue keyHandleWebServer = new GatewayToolNative.LongValue();
		result = GatewayToolNative.regOpenKey( keyHandle.getValue(), "WebServer", keyHandleWebServer );
		
		strValue = new GatewayToolNative.StringValue();
		result = GatewayToolNative.regQueryStringValue( keyHandleWebServer.getValue(), "URL", strValue );
		setWebServerInfo( strValue.getValue() );
		
		result = GatewayToolNative.regCloseKey( keyHandleWebServer.getValue() );
		
		result = GatewayToolNative.regCloseKey( keyHandle.getValue() );
	}
	
	private void setWebServerInfo( String webServerUrl )
	{
		int index = webServerUrl.indexOf( ":" );
		this.gatewayProtocol = webServerUrl.substring( 0, index );
		
		index = webServerUrl.lastIndexOf( ":" );
		String portStr = webServerUrl.substring( index + 1, webServerUrl.length() );
		this.gatewayPort = Integer.parseInt( portStr );
		
		this.gatewayHostName = "localhost";
	}
	
	public String getUdpHome()
	{
		return udpHome;
	}

	public String getUdpConfigurationFolderPath()
	{
		String path = this.udpHome;
		if (!path.endsWith( "\\" ))
			path = path + "\\";
		return path + "Configuration";
	}

//	public IEdgeGatewayService connectConsole( String hostname, int port, String protocol ) throws Exception
//	{
//		String portName = ServiceInfoConstants.SERVICE_EDGE_CONSOLE_PROPER_PORT_NAME;
//		String serviceName = ServiceInfoConstants.SERVICE_EDGE_CONSOLE_PROPER_SERVICE_NAME;
//		
//		return connectWebService( hostname, port, protocol, portName, serviceName );
//	}
//	
//	@SuppressWarnings( "deprecation" )
//	private IEdgeGatewayService connectWebService( String hostname, int port, String protocol, String portName, String serviceName ) throws Exception
//	{
//		BaseWebServiceClientProxy webService = null;
//		
//		// connect web service
//		
//		try
//		{
//			String wsdlPath = "";
//			
//			wsdlPath = String.format( "/services/%s?wsdl", serviceName );
//
//			// Set ServiceInfo
//			ServiceInfo serviceInfo = new ServiceInfo();
//			serviceInfo.setBindingType( ServiceInfoConstants.SERVICE_BINDING_SOAP11 );
//			serviceInfo.setNamespace( ServiceInfoConstants.SERVICE_EDGE_PROPER_NAMESPACE );
//			serviceInfo.setPortName( portName );
//			serviceInfo.setServiceName( serviceName );
//
//			if (!protocol.endsWith( ":" ))
//				protocol = protocol + ":";
//			
//			String wsdlURL = protocol + "//" + hostname + ":" + port
//				+ "" //CommonUtil.CENTRAL_MANAGER_CONTEXT_PATH
//				+ wsdlPath;
//			serviceInfo.setWsdlURL( wsdlURL );
//
//			// Set ServiceInfoConstants
//			String serviceID = ServiceInfoConstants.SERVICE_ID_EDGE_PROPER;
//			
//			BaseWebServiceFactory serviceFactory = new BaseWebServiceFactory();
//			webService = serviceFactory.getWebService(
//				protocol, hostname, port, serviceID, serviceInfo, IEdgeGatewayService.class );
//			
//			if (webService == null)
//				throw new Exception();
//		}
//		catch (Exception e)
//		{
//			throw e;
//		}
//		
//		return (IEdgeGatewayService) webService.getService();
//	}
//	
//	public IEdgeGatewayService connectGatewayHost() throws Exception
//	{
//		String portName = ServiceInfoConstants.SERVICE_EDGE_PROPER_PORT_NAME;
//		String serviceName = ServiceInfoConstants.SERVICE_EDGE_PROPER_SERVICE_NAME;
//		
//		return connectWebService( this.gatewayHostName, this.gatewayPort, this.gatewayProtocol,
//			portName, serviceName );
//	}
	
	/**
	 * Parse encoded registration string.
	 * 
	 * @param	registrationString
	 * 			The encoded registration string. The parameter should not be null or an empty string.
	 * @return	A object contains all information in the passed in encoded string.
	 * @throws	GatewayToolException( GatewayToolError.InvalidRegistrationString )
	 */
	public GatewayRegistrationString parseRegistrationString( String registrationString ) throws GatewayToolException
	{
		if ((registrationString == null) || registrationString.trim().isEmpty())
			throw new GatewayToolException( GatewayToolError.InvalidRegistrationString );
		
		GatewayRegistrationString regStrObject;
		try
		{
			regStrObject = GatewayRegistrationString.parseEncodedString( registrationString );
		}
		catch (Exception e)
		{
			logger.error( "Error parsing registration string. The string: '" + registrationString + "'", e );
			throw new GatewayToolException( GatewayToolError.InvalidRegistrationString );
		}
		
		return regStrObject;
	}
	
	/**
	 * Register a gateway host to a gateway through a registration string.
	 * 
	 * @param	registrationString
	 * 			The registration string.
	 * @param	overwriteOld
	 * 			Whether to overwrite the old gateway host if the gateway was already bound with a one.
	 * @param	notifyGatewayHost
	 * 			Whether to notify the gateway host after successfully registered.
	 * 
	 * @throws	GatewayToolException( InvalidRegistrationString )
	 * @throws	GatewayToolException( FailedToGetLocalHostName )
	 * @throws	GatewayToolException( FailedToConnectConsole )
	 * @throws	GatewayToolException( FailedToLoginConsole )
	 * @throws	GatewayToolException( SpecifiedGatewayNotFound )
	 * @throws	GatewayToolException( InvalidRegistrationInfo )
	 * @throws	GatewayToolException( CannotRegisterToLocalGateway )
	 * @throws	GatewayToolException( FailedToOperateDatabase )
	 * @throws	GatewayToolException( UnknownConsoleError )
	 * @throws	GatewayToolException( FailedToWriteGatewaySettingsFile )
	 */
	public GatewayRegistrationResult registerGateway(
		String registrationString, String settingsFileFolder, boolean overwriteOld, boolean notifyGatewayHost, GatewayProxyInfo proxyInfo
		) throws GatewayToolException
	{
		logger.info( "Begin to register gateway. registrationString: " + registrationString +
			", settingsFileFolder: " + settingsFileFolder +
			", overwriteOld: " + overwriteOld +
			", notifyGatewayHost: " + notifyGatewayHost );
		logger.info("GatewayProxyInfo: " + proxyInfo.toString());
		// parse registration string
		
		GatewayRegistrationString regStr = this.parseRegistrationString( registrationString );
		
		// get local info
		
		String hostUuid = null;
		
		if (settingsFileFolder == null)
			settingsFileFolder = this.getUdpConfigurationFolderPath();
		GatewayMessageServiceSettings oldSettings =
			GatewayMessageServiceSettings.loadFromFolder( settingsFileFolder );
		if ((oldSettings.getHostUuid() != null) && !oldSettings.getHostUuid().trim().isEmpty())
			hostUuid = oldSettings.getHostUuid();
		
		if (hostUuid == null)
			hostUuid = UUID.randomUUID().toString();
		
		String hostName;
		
		try
		{
			InetAddress inetAddress = InetAddress.getLocalHost();
			hostName = inetAddress.getHostName();
		}
		catch (Exception e)
		{
			logger.error( "Failed to get local host name.", e );
			throw new GatewayToolException( GatewayToolError.FailedToGetLocalHostName );
		}
		
		GatewayRegistrationInfo regInfo = new GatewayRegistrationInfo();
		regInfo.setConsoleUuid( regStr.getConsoleUuid() );
		regInfo.setGatewayUuid( regStr.getGatewayUuid() );
		regInfo.setHostUuid( hostUuid.toString() );
		regInfo.setHostName( hostName );
		regInfo.setOverwriteOld( overwriteOld );
		
		// connect console
		
		IEdgeGatewayService edgeService;
		
		try
		{
			edgeService = EdgeGatewayBean.connectConsole(
				regStr.getRegSvrHostName(), regStr.getRegSvrPort(), regStr.getRegSvrProtocol(), proxyInfo);
		}
		catch (Exception e)
		{
			logger.error( "Cannot connect to the console. Host: " + regStr.getRegSvrHostName() +
				", Port: " + regStr.getRegSvrPort() + ", Protocol: " + regStr.getRegSvrProtocol(), e );
			
			FailedToConnectConsoleErrorParam errorParam = new FailedToConnectConsoleErrorParam(
				regStr.getRegSvrHostName(), regStr.getRegSvrPort(), regStr.getRegSvrProtocol() );
			throw new GatewayToolException( GatewayToolError.FailedToConnectConsole, errorParam );
		}
		
		// do registration
		
		GatewayRegistrationResult result = null;
		
		try
		{
			result = edgeService.registerGatewayHost( regInfo );
		}
		catch (EdgeServiceFault e)
		{
			String consoleErrorCode = e.getFaultInfo().getCode();
			if (consoleErrorCode.equals( EdgeServiceErrorCode.GATEWAY_InvalidRegInfo ))
			{
				logger.error( "Invalid registration info.", e );
				throw new GatewayToolException( GatewayToolError.InvalidRegistrationInfo );
			}
			else if (consoleErrorCode.equals( EdgeServiceErrorCode.GATEWAY_GatewayHostLoginFailed ))
			{
				logger.error( "Failed to login to console.", e );
				throw new GatewayToolException( GatewayToolError.FailedToLoginConsole );
			}
			else if (consoleErrorCode.equals( EdgeServiceErrorCode.GATEWAY_GatewayNotFound ))
			{
				logger.error( "The specified gateway cannot be found.", e );
				throw new GatewayToolException( GatewayToolError.SpecifiedGatewayNotFound );
			}
			else if (consoleErrorCode.equals( EdgeServiceErrorCode.GATEWAY_CannotRegisterToLocalGateway ))
			{
				logger.error( "Cannot register to local gateway.", e );
				throw new GatewayToolException( GatewayToolError.CannotRegisterToLocalGateway );
			}
			else if (consoleErrorCode.equals( EdgeServiceErrorCode.GATEWAY_GatewayRegisteredToAnotherHost ))
			{
				EdgeServiceFaultBean faultInfo = e.getFaultInfo();
				String occupierHostName = faultInfo.getMessageParameters()[0].toString();
				String siteName = faultInfo.getMessageParameters()[1].toString();
				logger.error( "The gateway was already registered to host '" + occupierHostName + "'" );
				GatewayRegisteredToAnotherHostErrorParam errorParam =
					new GatewayRegisteredToAnotherHostErrorParam( occupierHostName, siteName, hostName );
				throw new GatewayToolException( GatewayToolError.GatewayRegisteredToAnotherHost, errorParam );
			}
			else if (consoleErrorCode.equals( EdgeServiceErrorCode.Common_Service_Dao_Execption ))
			{
				logger.error( "Error operating on database.", e );
				throw new GatewayToolException( GatewayToolError.FailedToOperateDatabase );
			}
			else if (consoleErrorCode.equals( EdgeServiceErrorCode.GATEWAY_CannotRegisterWhenUpgrading ))
			{
				EdgeServiceFaultBean faultInfo = e.getFaultInfo();
				String siteName = faultInfo.getMessageParameters()[0].toString();
				String currentHostName = faultInfo.getMessageParameters()[1].toString();
				logger.error( "Cannot register to the gateway when it's upgrading.", e );
				CannotRegisterWhenUpgradingErrorParam errorParam =
					new CannotRegisterWhenUpgradingErrorParam( siteName, currentHostName );
				throw new GatewayToolException( GatewayToolError.CannotRegisterWhenUpgrading, errorParam );
			}
			else // unknown console error
			{
				logger.error( "Unknown console error. Error code: " + consoleErrorCode, e );
				throw new GatewayToolException( GatewayToolError.UnknownConsoleError );
			}
		}

		/*if(result.getBrokerCert() != null){
			FileOutputStream fileOutputStream;
			try {
				EdgeBrokerKeyStoreUtils brokerKSService = new EdgeBrokerKeyStoreUtils(getUdpHome());
				if(settingsFileFolder == null || settingsFileFolder.equals(this.getUdpConfigurationFolderPath())){
					brokerKSService.validateBrokerDir();
				}
				fileOutputStream = new FileOutputStream((settingsFileFolder == null || settingsFileFolder.equals(this.getUdpConfigurationFolderPath())) 
						? brokerKSService.getBrokerKSPath() : brokerKSService.getBrokerKSPathForRemoteSetup(settingsFileFolder));
				fileOutputStream.write(result.getBrokerCert());  
				fileOutputStream.flush();  
				fileOutputStream.close();  
			} catch (Exception e) {
				logger.error( "Invalid broker keystore", e );
			}  
		}*/
		
		// save settings
		try
		{
			GatewayMessageServiceSettings settings = new GatewayMessageServiceSettings();
			settings.setConsoleHost( regStr.getRegSvrHostName() );
			settings.setConsolePort( regStr.getRegSvrPort() );
			settings.setConsoleProtocol( regStr.getRegSvrProtocol() );
			settings.setConsoleUuid( regStr.getConsoleUuid() );
			settings.setGatewayUuid( regStr.getGatewayUuid() );
			settings.setHostUuid( regInfo.getHostUuid() );
			settings.setDebugMode( oldSettings.isDebugMode() );
			settings.saveIntoFolder( (settingsFileFolder == null) ? this.getUdpConfigurationFolderPath() : settingsFileFolder );
		}
		catch (Exception e)
		{
			logger.error( "Failed to write gateway settings file.", e );
			throw new GatewayToolException( GatewayToolError.FailedToWriteGatewaySettingsFile );
		}
		
		logger.info( "The gateway was registered successfully." );
		
		return result;
	}
	
	public void unregisterGateway( String settingsFileFolder, boolean notifyGatewayHost, GatewayProxyInfo proxyInfo
		) throws GatewayToolException
	{
		logger.info( "Begin to unregister gateway. settingsFileFolder: " + settingsFileFolder +
			", notifyGatewayHost: " + notifyGatewayHost );
		
		// parse console UUID, gateway UUID and host UUID
		
		String consoleUuid = null;
		String gatewayUuid = null;
		String hostUuid = null;
		
		if (settingsFileFolder == null)
			settingsFileFolder = this.getUdpConfigurationFolderPath();
		
		GatewayMessageServiceSettings oldSettings =
			GatewayMessageServiceSettings.loadFromFolder( settingsFileFolder );
		consoleUuid = oldSettings.getConsoleUuid();
		gatewayUuid = oldSettings.getGatewayUuid();
		hostUuid = oldSettings.getHostUuid();
		
		// connect console
		
		IEdgeGatewayService edgeService;
		
		try
		{
			edgeService = EdgeGatewayBean.connectConsole(
				oldSettings.getConsoleHost(), oldSettings.getConsolePort(), oldSettings.getConsoleProtocol(), proxyInfo);
		}
		catch (Exception e)
		{
			logger.error( "Cannot connect to the console. Host: " + oldSettings.getConsoleHost() +
				", Port: " + oldSettings.getConsolePort() + ", Protocol: " + oldSettings.getConsoleProtocol(), e );
			
			FailedToConnectConsoleErrorParam errorParam = new FailedToConnectConsoleErrorParam(
				oldSettings.getConsoleHost(), oldSettings.getConsolePort(), oldSettings.getConsoleProtocol() );
			throw new GatewayToolException( GatewayToolError.FailedToConnectConsole, errorParam );
		}
		
		// do unregistration
		
		try
		{
			GatewayUnregistrationInfo unregInfo = new GatewayUnregistrationInfo();
			unregInfo.setConsoleUuid( consoleUuid );
			unregInfo.setGatewayUuid( gatewayUuid );
			unregInfo.setHostUuid( hostUuid );
			
			edgeService.unregisterGatewayHost( unregInfo );
		}
		catch (EdgeServiceFault e)
		{
			String consoleErrorCode = e.getFaultInfo().getCode();
			if (consoleErrorCode.equals( EdgeServiceErrorCode.GATEWAY_InvalidRegInfo ))
			{
				logger.error( "Invalid registration info.", e );
				throw new GatewayToolException( GatewayToolError.InvalidRegistrationInfo );
			}
			else if (consoleErrorCode.equals( EdgeServiceErrorCode.GATEWAY_GatewayHostLoginFailed ))
			{
				logger.error( "Failed to login to console.", e );
				throw new GatewayToolException( GatewayToolError.FailedToLoginConsole );
			}
			else if (consoleErrorCode.equals( EdgeServiceErrorCode.GATEWAY_GatewayNotFound ))
			{
				logger.error( "The specified gateway cannot be found.", e );
				throw new GatewayToolException( GatewayToolError.SpecifiedGatewayNotFound );
			}
			else if (consoleErrorCode.equals( EdgeServiceErrorCode.GATEWAY_GatewayHostNotRegistered ))
			{
				logger.error( "The gateway host was not registered to the specified gateway.", e );
				throw new GatewayToolException( GatewayToolError.GatewayHostNotRegistered );
			}
			else if (consoleErrorCode.equals( EdgeServiceErrorCode.Common_Service_Dao_Execption ))
			{
				logger.error( "Error operating on database.", e );
				throw new GatewayToolException( GatewayToolError.FailedToOperateDatabase );
			}
			else // unknown console error
			{
				logger.error( "Unknown console error. Error code: " + consoleErrorCode, e );
				throw new GatewayToolException( GatewayToolError.UnknownConsoleError );
			}
		}
		
		// delete gateway settings file
		
		String settingsFilePath = null;
		try
		{
			settingsFilePath = this.getSettingsFilePath( settingsFileFolder );
			Files.deleteIfExists( (new File( settingsFilePath )).toPath() );
		}
		catch (Exception e)
		{
			logger.error( "Error delete gateway settings file: " + settingsFilePath, e );
			throw new GatewayToolException( GatewayToolError.FailedToDeleteGatewaySettingsFile );
		}
		
		logger.info( "The gateway was unregistered successfully." );
	}
	
	public void notifyRegResultToGatewayHost2( GatewayRegistrationResult result ) throws GatewayToolException
	{
		logger.info( "Begin to notify gateway's web service." );
		
		assert result != null : "Invalid registration result.";
		
		IEdgeGatewayService edgeService;
		
		try
		{
			edgeService = EdgeGatewayBean.connectGatewayHost(
				this.gatewayHostName, this.gatewayPort, this.gatewayProtocol );
		}
		catch (Exception e)
		{
			logger.error( "Cannot connect to the gateway host.", e );
			throw new GatewayToolException( GatewayToolError.FailedToConnectToGatewayHost );
		}
		
		try
		{
			edgeService.onGatewayHostRegistered( result );
		}
		catch (Exception e)
		{
			logger.error( "Failed to notify the gateway host.", e );
			throw new GatewayToolException( GatewayToolError.FailedToNotifyGatewayHost );
		}
		
		logger.info( "Successfully notified gateway's web service." );
	}
	
	public void notifyRegResultToGatewayHost( GatewayRegistrationResult result ) throws GatewayToolException
	{
		logger.info( "Begin to notify gateway's web service." );
		
		logger.info( "Notify gateway's web service by restarting the Windows service." );
		restartEdgeService();
		
		logger.info( "Successfully notified gateway's web service." );
	}
	
	public String getSettingsFileName()
	{
		return GatewayMessageServiceSettings.SETTING_FILE_NAME;
	}
	
	public String getSettingsFilePath( String settingsFileFolder )
	{
		if (settingsFileFolder == null)
			settingsFileFolder = this.getUdpConfigurationFolderPath();
		
		return this.makeFilePath( settingsFileFolder, this.getSettingsFileName() );
	}
	
	private String makeFilePath( String folderPath, String fileName )
	{
		if (!folderPath.endsWith( "\\" ))
			folderPath += "\\";
		return folderPath + fileName;
	}
	
	/**
	 * These follow several functions should be put in some common class. But the
	 * CommonUtil depends some native code when it's loaded and the EdgeCommonUtil
	 * depends on CommonUtil. To avoid depending on a bunch of useless native
	 * binaries, these functions have to be put here temporarily.
	 */

	public static final String EdgeWindowsServiceName = "CAARCAppSvc";
	
	public static boolean startEdgeService()
	{
		try
		{
			logger.info( "Begin to start Edge service..." );
			
			String command = "net start " + EdgeWindowsServiceName;
			Process process = Runtime.getRuntime().exec( command );
			int exitCode = process.waitFor();
			
			logger.info( "Finish starting Edge service with exit code " + exitCode + "." );
			
			return (exitCode == 0);
		}
		catch (Exception e)
		{
			logger.error( "Error starting Edge service.", e );
			return false;
		}
	}
	
	public static boolean stopEdgeService()
	{
		try
		{
			logger.info( "Begin to stop Edge service..." );
			
			String command = "net stop " + EdgeWindowsServiceName;
			Process process = Runtime.getRuntime().exec( command );
			int exitCode = process.waitFor();
			
			logger.info( "Finish stopping Edge service with exit code " + exitCode + "." );
			
			return (exitCode == 0);
		}
		catch (Exception e)
		{
			logger.error( "Error stopping Edge service.", e );
			return false;
		}
	}
	
	public void restartEdgeService() throws GatewayToolException
	{
		String logPrefix = getLogPrefixStatically();
		logger.info( logPrefix + "Restaring gateway service..." );
		
		if (!stopEdgeService())
		{
			throw new GatewayToolException( GatewayToolError.FailedToRestartGatewayService,
				new FailedToRestartGatewayServiceErrorParam( RestartServicePhase.Stop ) );
		}
		
		if (!startEdgeService())
		{
			throw new GatewayToolException( GatewayToolError.FailedToRestartGatewayService,
				new FailedToRestartGatewayServiceErrorParam( RestartServicePhase.Start ) );
		}
		
		logger.info( logPrefix + "Finished restarting gateway service." );
	}
	
	private EdgeAccount normalizeAccountInfo( String username, String password )
	{
		String domain = "";
		
		int index = username.indexOf( "\\" );
		if (index >= 0)
		{
			domain = username.substring( 0, index );
			username = username.substring( index + 1, username.length() );
		}
		
		EdgeAccount edgeAccount = new EdgeAccount();
		edgeAccount.setUserName( username );
		edgeAccount.setPassword( password );
		edgeAccount.setDomain( domain );

		return edgeAccount;
	}
	
	// This method was copied from NativeFacadeImpl.java to avoid depend on
	// too many un-related codes
	public int validateUser(String username, @NotPrintAttribute String password, String domain) throws EdgeServiceFault {
		int result = WSJNI.validate(username, domain, password);
		switch (result) {
			case 1: throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Login_WrongCredential, "Invalid user credentials !");
			case 2: throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Login_NotAdministrator, "Not administrator's group !");
		}
		return result;
	}
	
	public void setAdminAccount( String username, String password ) throws GatewayToolException
	{
		if ((username == null) || username.trim().isEmpty())
			throw new GatewayToolException( GatewayToolError.InvalidUsername );
		
		if ((password == null) || password.trim().isEmpty())
			throw new GatewayToolException( GatewayToolError.InvalidPassword );
		
		try
		{
			EdgeAccount account = normalizeAccountInfo( username, password );
			validateUser( account.getUserName(), account.getPassword(), account.getDomain() );
			int retValue = WSJNI.saveEdgeAccount( username, password );
			if (retValue != 0)
			{
				logger.error( "Failed to set credential. Return value: " + retValue );
				throw new GatewayToolException( GatewayToolError.UnknownConsoleError );
			}
		}
		catch (EdgeServiceFault e)
		{
			String consoleErrorCode = e.getFaultInfo().getCode();
			if (consoleErrorCode.equals( EdgeServiceErrorCode.Login_WrongCredential ))
			{
				logger.error( "Wrong credential. Username: " + username, e );
				throw new GatewayToolException( GatewayToolError.WrongCredential );
			}
			else if (consoleErrorCode.equals( EdgeServiceErrorCode.Login_NotAdministrator ))
			{
				logger.error( "The user is not in the Administrators group. Username: " + username, e );
				throw new GatewayToolException( GatewayToolError.NotAdministrator );
			}
		}
		catch (GatewayToolException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			logger.error( "Unexpected exception. Username: " + username, e );
		}
		
//		try
//		{
//			Account account = new Account();
//			account.setUsername( username );
//			account.setPassword( password );
//			account.setDomain( "" );
//			IEdgeConfigurationService configService = new ConfigurationServiceImpl();
//			configService.saveEdgeAccount( account );
//		}
//		catch (EdgeServiceFault e)
//		{
//			
//		}
//		catch (Exception e)
//		{
//			
//		}
	}
	
	public void setProxyInfo( GatewayProxyInfo proxyInfo ) throws GatewayToolException
	{
		String logPrefix = getLogPrefix();
		
		try
		{
			logger.info( logPrefix + "Begin to set proxy settings." );
			
			if (proxyInfo == null)
				throw new GatewayToolException( GatewayToolError.InvalidProxyInfo );
			
			if (proxyInfo.getProxyType() == null)
				throw new GatewayToolException( GatewayToolError.InvalidProxyInfo );
			
			if (proxyInfo.getProxyType() == GatewayProxyType.IEProxy)
			{
				proxyInfo.setServer( "" );
				proxyInfo.setPort( 0 );
				proxyInfo.setRequireAuthentication( false );
				proxyInfo.setUsername( "" );
				proxyInfo.setPassword( "" );
			}
			else if (proxyInfo.getProxyType() == GatewayProxyType.CustomProxy)
			{
				if (((proxyInfo.getServer() == null) || (proxyInfo.getServer().trim().isEmpty()) ||
					((proxyInfo.getPort() <= 0) || proxyInfo.getPort() >= 65536)))
					throw new GatewayToolException( GatewayToolError.InvalidProxyInfo );
				
				if (proxyInfo.isRequireAuthentication())
				{
					if (((proxyInfo.getUsername() == null) || (proxyInfo.getUsername().trim().isEmpty())) ||
						(proxyInfo.getPassword() == null))
						throw new GatewayToolException( GatewayToolError.InvalidProxyInfo );
				}
				else // no authentication
				{
					proxyInfo.setUsername( "" );
					proxyInfo.setPassword( "" );
				}
			}
					
			long result;
			
			GatewayToolNative.LongValue keyHandle = new GatewayToolNative.LongValue();
			result = GatewayToolNative.regOpenKey( GatewayToolNative.HKEY_LOCAL_MACHINE, REGKEY_PROXY, keyHandle );
			if (result != 0)
				throw new GatewayToolException( GatewayToolError.FailedToSaveProxyInfo,
					new FailedToSaveProxyInfoErrorParam( "Unknown" ) );
			
			result = GatewayToolNative.regSetLongValue( keyHandle.getValue(), "Type", proxyInfo.getProxyType().getValue() );
			if (result != 0)
				throw new GatewayToolException( GatewayToolError.FailedToSaveProxyInfo,
					new FailedToSaveProxyInfoErrorParam( "Type" ) );
			
			result = GatewayToolNative.regSetStringValue( keyHandle.getValue(), "Server", proxyInfo.getServer() );
			if (result != 0)
				throw new GatewayToolException( GatewayToolError.FailedToSaveProxyInfo,
					new FailedToSaveProxyInfoErrorParam( "Server" ) );
			
			result = GatewayToolNative.regSetLongValue( keyHandle.getValue(), "Port", proxyInfo.getPort() );
			if (result != 0)
				throw new GatewayToolException( GatewayToolError.FailedToSaveProxyInfo,
					new FailedToSaveProxyInfoErrorParam( "Port" ) );
			
			result = GatewayToolNative.regSetLongValue( keyHandle.getValue(), "RequireAccount",
				proxyInfo.isRequireAuthentication() ? 1 : 0 );
			if (result != 0)
				throw new GatewayToolException( GatewayToolError.FailedToSaveProxyInfo,
					new FailedToSaveProxyInfoErrorParam( "RequireAccount" ) );
			
			result = GatewayToolNative.regSetStringValue( keyHandle.getValue(), "Username", proxyInfo.getUsername() );
			if (result != 0)
				throw new GatewayToolException( GatewayToolError.FailedToSaveProxyInfo,
					new FailedToSaveProxyInfoErrorParam( "Username" ) );
			
			byte[] cppString = toCppString( proxyInfo.getPassword() );
			byte[] encryptedPwd = com.ca.arcflash.webservice.jni.WSJNI.AFEncryptBinary( cppString );
			//printByteArray( encryptedPwd );
			result = GatewayToolNative.regSetBinaryValue( keyHandle.getValue(), "Password",
				(encryptedPwd != null) ? encryptedPwd : new byte[0] );
			if (result != 0)
				throw new GatewayToolException( GatewayToolError.FailedToSaveProxyInfo,
					new FailedToSaveProxyInfoErrorParam( "Password" ) );
			
			logger.info( logPrefix + "Proxy settings were set successfully." );
		}
		catch (GatewayToolException e)
		{
			logger.error( logPrefix + "Failed to set proxy settings.", e );
			throw e;
		}
		catch (Exception e)
		{
			logger.error( logPrefix + "Unexpected exception.", e );
			throw new GatewayToolException( GatewayToolError.FailedToSaveProxyInfo,
				new FailedToSaveProxyInfoErrorParam( "Unknown" ) );
		}
	}
	
	private byte[] toCppString( String string ) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter( out, Charset.forName( "UTF-16LE" ) );
		writer.write( string );
		writer.flush();
		byte[] bytes = out.toByteArray();
		return Arrays.copyOf( bytes, bytes.length + 2 ); // add null-terminator, the function will padding it with zero
	}
	
	private String getLogPrefix()
	{
		return this.getClass().getSimpleName() + "." +
			Thread.currentThread().getStackTrace()[2].getMethodName() + "(): ";
	}
	
	private static String getLogPrefixStatically()
	{
		return "GatewayToolCore." +
			Thread.currentThread().getStackTrace()[2].getMethodName() + "(): ";
	}
	
//	private void printByteArray( byte[] array )
//	{
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < array.length; i ++)
//		{
//			if ((i > 0) && (i % 16 == 0))
//				sb.append( "\n" );
//			sb.append( String.format( "%02x ", array[i] ) );
//		}
//		sb.append( "\n" );
//		System.out.print( sb.toString() );
//	}
}

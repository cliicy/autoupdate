package com.ca.arcserve.edge.app.base.webservice.gateway.clitools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.ca.arcserve.edge.app.base.jni.BaseWSJNI;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayProxyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayProxyType;
import com.ca.arcserve.edge.app.base.webservice.gateway.EdgeGatewayProxyUtil;
import com.ca.arcserve.edge.app.base.webservice.gateway.GatewayRegistrationString;
import com.ca.arcserve.edge.app.base.webservice.gateway.clitools.GatewayToolCore.GatewayToolError;
import com.ca.arcserve.edge.app.base.webservice.gateway.clitools.GatewayToolCore.GatewayToolException;

public class GatewayTool4Setup
{
	private static final String PARAM_GATEWAYHOSTPORT = "gatewayHostPort";
	private static final String PARAM_GATEWAYHOSTPROTOCOL = "gatewayHostProtocol";
	private static final String PARAM_GATEWAYHOSTUSERNAME = "gatewayHostUsername";
	private static final String PARAM_GATEWAYHOSTPASSWORD = "gatewayHostPassword";
	private static final String PARAM_REGISTRATIONSTRING = "registrationString";
	private static final String PARAM_SETTINGSFILEFOLDER = "settingsFileFolder";
	private static final String PARAM_OVERWRITEOLD = "overwriteOld";
	private static final String PARAM_NOTIFYGATEWAYHOST = "notifyGatewayHost";
	private static final String PARAM_ISSUCCESSFUL = "isSuccessful";
	private static final String PARAM_ERRORCODE = "errorCode";
	private static final String PARAM_ERRORMESSAGE = "errorMessage";
	private static final String PARAM_NEEDCONFIRMATION = "needConfirmation";
	private static final String VALUE_YES = "y";
	private static final String VALUE_NO = "n";
	private static final String PARAM_PROXYTYPE = "ProxyType";
	private static final String PARAM_PROXSERVER = "ProxyServer";
	private static final String PARAM_PROXPORT = "ProxyPort";
	private static final String PARAM_PROXREQUIREACCOUNT = "ProxyRequireAccount";
	private static final String PARAM_USERNAME = "ProxyUserName";
	private static final String PARAM_PASSWORD = "ProxyPassword";
	private static final String PARAM_SYSTEMUSERNAME = "SystemUserName";
	private static final String PARAM_SYSTEMPASSWORD = "SystemPassword";
	
	private static Logger logger = Logger.getLogger( GatewayTool4Setup.class );
	private GatewayToolCore core = GatewayToolCore.getInstance();
	
	public static void main( String[] args )
	{
		GatewayTool4Setup program = new GatewayTool4Setup();
		program.run( args );
	}
	
	private void run( String[] args )
	{
		try
		{
			configLogFile();
			
			logger.info( "GatewayTool4Setup starts with " + args.length + " arguments. Arguments: " + Arrays.toString( args ) );
			
			if (args.length < 3)
			{
				System.out.println( "Usage: GatewayTool4Setup <COMMAND> <INPUTFILE> <OUTPUTFILE>" );
				System.exit( -1 );
				return;
			}
			
			this.core.initialize( false, false );
			
			String command = args[0];
			String inputName = args[1];
			String outputName = args[2];
			
			Properties inputData = getInput( inputName );
			Properties outputData = new Properties();
			
			if (command.equalsIgnoreCase( "ExtractInfo" ))
			{
				extractInfo( inputData, outputData );
			}
			else if (command.equalsIgnoreCase( "RegisterGateway" ))
			{
				registerGateway( inputData, outputData );
			}
			else if (command.equalsIgnoreCase( "UnregisterGateway" ))
			{
				unregisterGateway( inputData, outputData );
			}
			else if (command.equalsIgnoreCase( "PostInstallProcess" ))
			{
				postInstallProcess( inputData, outputData );
			}
			
			putOutput( outputName, outputData );
		}
		catch (Exception e)
		{
			logger.error( "Error encountered.", e );
		}
		finally
		{
			logger.info( "GatewayTool4Setup exits." );
		}
	}
	
	private static final String LOGFILENAME = "ARCAPP-GatewayTool4Setup.log";
	
	private void configLogFile()
	{
		try
		{
			String windowsFolder = System.getenv( "windir" );
			String tempFolder = makeFilePath( windowsFolder, "Temp" );
			String logFilePath = makeFilePath( tempFolder, LOGFILENAME );
			
			Logger rootLogger = Logger.getRootLogger();
			rootLogger.setLevel( Level.INFO );
			
			RollingFileAppender appender = new RollingFileAppender();
			appender.setName( "DefaultLogFile" );
			appender.setFile( logFilePath );
			appender.setMaxFileSize( "5120KB" );
			appender.setMaxBackupIndex( 10 );
			appender.setLayout( new PatternLayout( "%d{ISO8601} %t [%p] %x%m%n" ) );
			appender.activateOptions();
			rootLogger.addAppender( appender );
			
			System.out.println( "Log file: " + logFilePath );
		}
		catch (Throwable e)
		{
			System.out.printf( "Error configuring log files. (%s)\n", e.getMessage() );
		}
	}
	
	private Properties getInput( String inputName ) throws IOException
	{
		//Properties data = new Properties();
		//data.load( new FileInputStream( inputName ) );
		
		// read the input file by ourself to avoid limitations the properties file
		// may have, such as some character escape problems.
		
		Properties data = new Properties();
		InputStreamReader fileReader = getInputFileReader( inputName );
		BufferedReader reader = new BufferedReader( fileReader );
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			int index = line.indexOf( '=' );
			if (index == -1)
				continue;
			String name = line.substring( 0, index );
			name = name.trim();
			String value = line.substring( index + 1, line.length() );
			value = value.trim();
			data.setProperty( name, value );
		}
		reader.close();
		
		logger.info( "Input data: " + data );
		
		return data;
	}
	
	private InputStreamReader getInputFileReader( String inputName ) throws IOException
	{
		InputStream in = new FileInputStream( inputName );
		return new InputStreamReader( in, Charset.forName( "UTF-16" ) );
	}
	
	private void putOutput( String outputName, Properties outputData ) throws Exception
	{
		logger.info( "Output data: " + outputData );
		
		//outputData.store( new FileOutputStream( outputName ), this.getClass().getSimpleName() + " Output File" );
		
		// write the output file by ourself to gain more flexibilities.
		
		OutputStream out = new FileOutputStream( outputName );
		byte[] bom = new byte[] { (byte) 0xFF, (byte) 0xFE };
		out.write( bom ); // write BOM for UTF-16LE
		OutputStreamWriter fileWriter = new OutputStreamWriter( out, Charset.forName( "UTF-16LE" ) );
		
		Date now = new Date();
		PrintWriter writer = new PrintWriter( fileWriter );
		writer.println( "# " + this.getClass().getSimpleName() + " Output File" );
		writer.println( "# " + now );
		for (String name : outputData.stringPropertyNames())
		{
			writer.printf( "%s = %s", name, outputData.getProperty( name ) );
			writer.println();
		}
		writer.close();
	}
	
	private void extractInfo( Properties inputData, Properties outputData )
	{
		try
		{
			String registrationString = inputData.getProperty( PARAM_REGISTRATIONSTRING, "" );
			
			GatewayRegistrationString regStr = this.core.parseRegistrationString( registrationString );
			
			outputData.setProperty( PARAM_GATEWAYHOSTPORT, Integer.toString( regStr.getGatewayHostPort() ) );
			outputData.setProperty( PARAM_GATEWAYHOSTPROTOCOL, regStr.getGatewayHostProtocol() );
			outputData.setProperty( PARAM_GATEWAYHOSTUSERNAME, regStr.getGatewayHostUsername() );
			outputData.setProperty( PARAM_GATEWAYHOSTPASSWORD, regStr.getGatewayHostPassword() );
			
			outputData.setProperty( PARAM_ISSUCCESSFUL, VALUE_YES );
		}
		catch (GatewayToolException e)
		{
			outputData.setProperty( PARAM_ISSUCCESSFUL, VALUE_NO );
			outputData.setProperty( PARAM_ERRORCODE, Integer.toString( e.getErrorCode() ) );
			outputData.setProperty( PARAM_ERRORMESSAGE, e.getErrorMessage() );
		}
	}
	
	private void registerGateway( Properties inputData, Properties outputData )
	{
		boolean overwriteOld = false;
		
		try
		{
			String registrationString = inputData.getProperty( PARAM_REGISTRATIONSTRING, "" );
			String settingsFileFolder = inputData.getProperty( PARAM_SETTINGSFILEFOLDER, "" );
			overwriteOld = inputData.getProperty( PARAM_OVERWRITEOLD, VALUE_NO ).equalsIgnoreCase( VALUE_YES );
			boolean notifyGatewayHost = inputData.getProperty( PARAM_NOTIFYGATEWAYHOST, VALUE_NO ).equalsIgnoreCase( VALUE_YES );
			
			this.core.registerGateway( registrationString, settingsFileFolder, overwriteOld, notifyGatewayHost, getGatewayProxyInfo(inputData));
			
			outputData.setProperty( PARAM_ISSUCCESSFUL, VALUE_YES );
		}
		catch (GatewayToolException e)
		{
			outputData.setProperty( PARAM_ISSUCCESSFUL, VALUE_NO );
			outputData.setProperty( PARAM_ERRORCODE, Integer.toString( e.getErrorCode() ) );
			
			if (!overwriteOld &&
				(e.getErrorCode() == GatewayToolError.GatewayRegisteredToAnotherHost.getErrorCode()))
			{
				String message = EdgeCMWebServiceMessages.getMessage(
					"gatewayTool4Setup_OverwriteOldGatewayHostConfirm", e.getErrorParam().getErrorMessageParam() );
				outputData.setProperty( PARAM_ERRORMESSAGE, message );
				outputData.setProperty( PARAM_NEEDCONFIRMATION, VALUE_YES );
			}
			else
			{
				outputData.setProperty( PARAM_ERRORMESSAGE, e.getErrorMessage() );
				outputData.setProperty( PARAM_NEEDCONFIRMATION, VALUE_NO );
			}
		}
	}
	
	private GatewayProxyInfo getGatewayProxyInfo(Properties inputData){
		GatewayProxyInfo proxyInfo = new GatewayProxyInfo();
		String proxyType = inputData.getProperty( PARAM_PROXYTYPE, "0" );
		proxyInfo.setProxyType( GatewayProxyType.fromValue( Integer.valueOf(proxyType) ) );
		if(proxyInfo.getProxyType() == GatewayProxyType.IEProxy){
			String systemUserName = inputData.getProperty( PARAM_SYSTEMUSERNAME, "" );
			String systemPassword = inputData.getProperty( PARAM_SYSTEMPASSWORD, "" );
			String decryptSystemPassword = "";
			try{
				decryptSystemPassword = systemPassword.equals("") ? "" : BaseWSJNI.AFDecryptString(systemPassword);
			}catch(Exception e){
				logger.error( "Failed to decrypt system password", e );
			}
			proxyInfo = EdgeGatewayProxyUtil.getIEProxyInfo(systemUserName, decryptSystemPassword);
		}else if(proxyInfo.getProxyType() == GatewayProxyType.CustomProxy){
			String proxyServer = inputData.getProperty( PARAM_PROXSERVER, "" );
			String proxyPort = inputData.getProperty( PARAM_PROXPORT, "0" );
			proxyInfo.setServer(proxyServer);
			proxyInfo.setPort(Integer.valueOf(proxyPort));
			String proxyRequireAccount = inputData.getProperty( PARAM_PROXREQUIREACCOUNT, "n" );
			if(proxyRequireAccount.equalsIgnoreCase("y")){
				proxyInfo.setRequireAuthentication(true);
				String proxyUserName = inputData.getProperty( PARAM_USERNAME, "" );
				String proxyPassword = inputData.getProperty( PARAM_PASSWORD, "" );
				proxyInfo.setUsername(proxyUserName);
				try{
					proxyInfo.setPassword(proxyPassword.equals("") ? "" : BaseWSJNI.AFDecryptString(proxyPassword));	
				}catch(Exception e){
					logger.error( "Failed to decrypt proxy password", e );
				}
			}else{
				proxyInfo.setRequireAuthentication(false);
			}
		}
		return proxyInfo;
	}
	
	private void unregisterGateway( Properties inputData, Properties outputData )
	{
		try
		{
			logger.info( "Begin to unregister gateway host." );
			
			String settingsFileFolder = inputData.getProperty( PARAM_SETTINGSFILEFOLDER, "" );
			
			
			this.core.unregisterGateway( settingsFileFolder, false, getGatewayProxyInfo(inputData));
			
			outputData.setProperty( PARAM_ISSUCCESSFUL, VALUE_YES );
			
			logger.info( "Unregistering gateway host complete successfully." );
		}
		catch (Exception e)
		{
			logger.error( "Failed to do post installation process.", e );
			outputData.setProperty( PARAM_ISSUCCESSFUL, VALUE_NO );
		}
	}
	
	private void postInstallProcess( Properties inputData, Properties outputData )
	{
		try
		{
			logger.info( "Begin post installation process." );
			
			this.core.getUdpInfoFromRegistry();
			
			String settingsFileFolder = inputData.getProperty( PARAM_SETTINGSFILEFOLDER, "" );
			String srcFilePath = this.core.getSettingsFilePath( settingsFileFolder );
			String destFilePath = this.core.getSettingsFilePath( null );
			
			logger.info( "Copying gateway settings file from '" + srcFilePath + "' to '" + destFilePath + "'" );
			
			Files.copy( (new File( srcFilePath )).toPath(), (new File( destFilePath)).toPath(),
				StandardCopyOption.REPLACE_EXISTING );
			// remote gateway can connect console without certificate, so the code is commented
			/*EdgeBrokerKeyStoreUtils brokerKSService = new EdgeBrokerKeyStoreUtils(this.core.getUdpHome());
			brokerKSService.validateBrokerDir();
			String srcBrokerFilePath = brokerKSService.getBrokerKSPathForRemoteSetup(settingsFileFolder);
			String destBrokerFilePath = brokerKSService.getBrokerKSPath();
			
			logger.info( "Copying broker Certificate file from '" + srcFilePath + "' to '" + destFilePath + "'" );
			Files.copy( (new File( srcBrokerFilePath )).toPath(), (new File( destBrokerFilePath)).toPath(),
					StandardCopyOption.REPLACE_EXISTING );*/
	        
			outputData.setProperty( PARAM_ISSUCCESSFUL, VALUE_YES );
			
			logger.info( "Post installation complete successfully." );
		}
		catch (Exception e)
		{
			logger.error( "Failed to do post installation process.", e );
			outputData.setProperty( PARAM_ISSUCCESSFUL, VALUE_NO );
		}
	}
	
	private String makeFilePath( String folderPath, String fileName )
	{
		if (!folderPath.endsWith( "\\" ))
			folderPath += "\\";
		return folderPath + fileName;
	}
}

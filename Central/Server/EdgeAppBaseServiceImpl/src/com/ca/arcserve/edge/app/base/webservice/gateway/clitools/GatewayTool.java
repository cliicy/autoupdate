package com.ca.arcserve.edge.app.base.webservice.gateway.clitools;

import java.io.Console;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayProxyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayProxyType;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayRegistrationResult;
import com.ca.arcserve.edge.app.base.webservice.gateway.EdgeGatewayProxyUtil;
import com.ca.arcserve.edge.app.base.webservice.gateway.clitools.GatewayToolCore.GatewayRegisteredToAnotherHostErrorParam;
import com.ca.arcserve.edge.app.base.webservice.gateway.clitools.GatewayToolCore.GatewayToolError;
import com.ca.arcserve.edge.app.base.webservice.gateway.clitools.GatewayToolCore.GatewayToolException;

public class GatewayTool
{
	private GatewayToolCore core = GatewayToolCore.getInstance();
	
	public static void main( String[] args )
	{
		GatewayTool program = new GatewayTool();
		program.run( args );
	}
	
	private void run( String[] args )
	{
		Console console = System.console();
		if (console == null)
		{
			System.out.println( EdgeCMWebServiceMessages.getMessage("GatewayTool_RunInConsole") );
			return;
		}
		
		try
		{
			configLogFile( console );

			console.printf( EdgeCMWebServiceMessages.getMessage("GatewayTool_ToolName") + "\n" );
			console.printf( EdgeCMWebServiceMessages.getMessage("GatewayTool_ToolCopyright") + "\n" );
			console.printf( "\n" );
			
			this.core.initialize( true, true );
			
			console.printf( EdgeCMWebServiceMessages.getMessage("GatewayTool_InstallServer") + " " + this.core.getUdpHome() + "\n" );
			console.printf( "\n" );
			
			printHelp( console );
			console.printf( "\n" );
			
			for (;;)
			{
				String command = console.readLine( EdgeCMWebServiceMessages.getMessage( "GatewayTool_Configurator" ) + "> " );
				command = command.trim();
				
				try
				{
					if (command.isEmpty())
					{
						continue; // do nothing
					}
					else if (command.equalsIgnoreCase( "exit" ))
					{
						break;
					}
					else if (command.equalsIgnoreCase( "help" ))
					{
						printHelp( console );
					}
					else if (command.equalsIgnoreCase( "register" ))
					{
						register( console );
					}
					else if (command.equalsIgnoreCase( "unregister" ))
					{
						unregister( console );
					}
					else if (command.equalsIgnoreCase( "setadminacc" ))
					{
						setAdminAccount( console );
					}
					else if (command.equalsIgnoreCase( "setproxy" ))
					{
						setProxy( console );
					}
					else // unknown command
					{
						// The command may contains % characters which may be interpreted as
						// format tag by printf(). So, to avoid this problem, don't put the
						// message in the 1st parameter which is the format string.
						console.printf( "%s",
							EdgeCMWebServiceMessages.getMessage( "GatewayTool_UnknownCommand", command, "help" ) + "\n" );
					}
				}
				catch (Exception e)
				{
					System.out.println( e );
					e.printStackTrace();
				}
				
				console.printf( "\n" );
			}
			
		}
		catch (Exception e)
		{
			System.out.println( e );
			e.printStackTrace();
		}
	}
	
	private void printCommandInfo(Console console, String commandName, String commandDescribe){
		console.printf( "\t" );
		console.printf( commandName );
		console.printf( "\t" );
		if (commandName.length() < 8)
			console.printf( "\t" );
		console.printf( "- " );
		console.printf( commandDescribe );
		console.printf( "\n" );
	}
	
	private void printHelp( Console console )
	{
		console.printf( EdgeCMWebServiceMessages.getMessage("GatewayTool_Commands") );
		console.printf( "\n\n" );
		printCommandInfo(console, "register", EdgeCMWebServiceMessages.getMessage("GatewayTool_RegisterDescription"));
		printCommandInfo(console, "unregister", EdgeCMWebServiceMessages.getMessage("GatewayTool_UnregisterDescription"));
		printCommandInfo(console, "setadminacc", EdgeCMWebServiceMessages.getMessage("GatewayTool_SetadminaccDescription"));
		printCommandInfo(console, "setproxy", EdgeCMWebServiceMessages.getMessage("GatewayTool_SetProxyDescription"));
		printCommandInfo(console, "exit", EdgeCMWebServiceMessages.getMessage("GatewayTool_ExitDescription"));
	}
	
	private void configLogFile( Console console )
	{
		try
		{
			String configFileName = "log4j-ARCAPP-GatewayTool.properties";
			String udpHome = this.core.getUdpHome();
			if (!udpHome.endsWith( "\\" ))
				udpHome += "\\";
			System.setProperty( "PMLogPath", udpHome + "logs\\" );
	    	String absPath = udpHome + "Configuration\\" + configFileName;
			PropertyConfigurator.configureAndWatch( absPath );
			
			Logger root = Logger.getRootLogger();
			root.setLevel( Level.OFF );
		}
		catch (Throwable e)
		{
			console.printf( EdgeCMWebServiceMessages.getMessage("GatewayTool_ErrorConfigLogFile") );
			console.printf( " "+e.getMessage()+"\n");
		}
	}
	
	private void register( Console console )
	{
		// get registration string
		
		String string = console.readLine( EdgeCMWebServiceMessages.getMessage("GatewayTool_PasteRigistration") + "\n" );
		if ((string == null) || string.trim().isEmpty())
			console.printf( EdgeCMWebServiceMessages.getMessage("GatewayTool_InvalidRegistration") + "\n" );
		
		
		GatewayToolException exception = null;
		
		// try registration
		
		boolean overwriteOld = false;
		GatewayRegistrationResult result = null;
		
		try
		{
			result = core.registerGateway( string, null, overwriteOld, true, EdgeGatewayProxyUtil.getRegistyProxyInfo());
		}
		catch (GatewayToolException e)
		{
			exception = e;
		}
		
		// if gateway got occupied, try registration again with overwriting old
		
		if (exception != null)
		{
			if (exception.getErrorCode() == GatewayToolError.GatewayRegisteredToAnotherHost.getErrorCode())
			{
				GatewayRegisteredToAnotherHostErrorParam errorParam =
					(GatewayRegisteredToAnotherHostErrorParam) exception.getErrorParam();
				String prompt = EdgeCMWebServiceMessages.getMessage(
					"gatewayTool_OverwriteOldGatewayHostConfirm", exception.getErrorParam().getErrorMessageParam() );
				String answer = console.readLine( prompt, errorParam.getOccupierHostName() );
				if (answer.equalsIgnoreCase( "y" ))
				{
					try
					{
						overwriteOld = true;
						result = core.registerGateway( string, null, overwriteOld, true, EdgeGatewayProxyUtil.getRegistyProxyInfo());
					}
					catch (GatewayToolException e)
					{
						exception = e;
					}
				}
				else // user doesn't want to overwrite
				{
					return;
				}
			}
			else // error
			{
				console.printf( EdgeCMWebServiceMessages.getMessage("GatewayTool_ErrorRegistrateGateway") + exception.getErrorMessage() + "\n" );
				return;
			}
		}
		
		console.printf( EdgeCMWebServiceMessages.getMessage("GatewayTool_RegistrationSucceeded") + "\n" );
		
		// notify the gateway host
		
		try
		{
			core.notifyRegResultToGatewayHost( result );
		}
		catch (GatewayToolException e)
		{
			console.printf( EdgeCMWebServiceMessages.getMessage("GatewayTool_ErrorNotifyService") + "\n" );
			return;
		}
	}
	
	private void unregister( Console console )
	{
		try
		{
			core.unregisterGateway( null, true, EdgeGatewayProxyUtil.getRegistyProxyInfo());
		}
		catch (GatewayToolException e)
		{
			console.printf( EdgeCMWebServiceMessages.getMessage("GatewayTool_ErrorUnregistrate") + "\n" );
			return;
		}
		
		console.printf( EdgeCMWebServiceMessages.getMessage("GatewayTool_UnregistrateSucceeded") +"\n" );
	}
	
	private void setAdminAccount( Console console )
	{
		try
		{
			// get credential info
			
			String username = console.readLine( EdgeCMWebServiceMessages.getMessage("GatewayTool_Username") );
			if ((username == null) || username.trim().isEmpty())
				console.printf( EdgeCMWebServiceMessages.getMessage("GatewayTool_InvalidUsername") + "\n" );
			
			char[] passwordChars = console.readPassword( EdgeCMWebServiceMessages.getMessage("GatewayTool_Password") );
			String password = new String( passwordChars );
			if ((password == null) || password.trim().isEmpty())
				console.printf( EdgeCMWebServiceMessages.getMessage("GatewayTool_InvalidPassword") + "\n" );
			
			core.setAdminAccount( username, password );
		}
		catch (GatewayToolException e)
		{
			console.printf( EdgeCMWebServiceMessages.getMessage("GatewayTool_ErrorSetAdminacc")+" " + e.getErrorMessage() + "\n");
			return;
		}
		
		console.printf( EdgeCMWebServiceMessages.getMessage("GatewayTool_SetAdminaccSucceeded") + "\n" );
	}
	
	private void setProxy( Console console )
	{
		try
		{
			GatewayProxyInfo proxyInfo = new GatewayProxyInfo();
			
			String valueString;
			
			for (;;)
			{
				valueString = getUserInput( console,
					EdgeCMWebServiceMessages.getMessage( "GatewayTool_ProxyType" ) + " ", "" );
				int selection = tryParseInt( valueString, 0 );
				if (selection == 1)
				{
					proxyInfo.setProxyType( GatewayProxyType.IEProxy );
					break;
				}
				else if (selection == 2)
				{
					proxyInfo.setProxyType( GatewayProxyType.CustomProxy );
					break;
				}
				console.printf( EdgeCMWebServiceMessages.getMessage( "GatewayTool_ErrorMsg_InvalidProxyType" ) + "\n" );
			}
			
			if (proxyInfo.getProxyType() == GatewayProxyType.CustomProxy)
			{
				valueString = "";
				for (;;)
				{
					valueString = getUserInput( console,
						EdgeCMWebServiceMessages.getMessage( "GatewayTool_ProxyServer" ) + " ", "" );
					if (!valueString.trim().isEmpty())
						break;
					console.printf( EdgeCMWebServiceMessages.getMessage( "GatewayTool_ErrorMsg_InvalidProxyServer" ) + "\n" );
				}
				proxyInfo.setServer( valueString );
				
				int port = 0;
				for (;;)
				{
					valueString = getUserInput( console,
						EdgeCMWebServiceMessages.getMessage( "GatewayTool_ProxyPort" ) + " ", "" );
					port = tryParseInt( valueString, 0 );
					if ((port >= 1) && (port <= 65535))
						break;
					console.printf( EdgeCMWebServiceMessages.getMessage( "GatewayTool_ErrorMsg_InvalidProxyPort" ) + "\n" );
				}
				proxyInfo.setPort( port );
				
				valueString = "";
				for (;;)
				{
					valueString = getUserInput( console,
						EdgeCMWebServiceMessages.getMessage( "GatewayTool_RequireAuthentication" ) + " ", "" );
					if (valueString.equalsIgnoreCase( "y" ) || valueString.equalsIgnoreCase( "n" ))
						break;
					console.printf( EdgeCMWebServiceMessages.getMessage( "GatewayTool_PleaseInputYorN" ) + "\n" );
				}
				proxyInfo.setRequireAuthentication( valueString.equalsIgnoreCase( "y" ) );
				
				if (!proxyInfo.isRequireAuthentication())
				{
					proxyInfo.setUsername( "" );
					proxyInfo.setPassword( "" );
				}
				else // required
				{
					valueString = "";
					for (;;)
					{
						valueString = getUserInput( console,
							EdgeCMWebServiceMessages.getMessage( "GatewayTool_ProxyUserName" ) + " ", "" );
						if (!valueString.trim().isEmpty())
							break;
						console.printf( EdgeCMWebServiceMessages.getMessage( "GatewayTool_ErrorMsg_InvalidProxyUserName" ) + "\n" );
					}
					proxyInfo.setUsername( valueString );
					
					valueString = getUserPasswordInput( console,
						EdgeCMWebServiceMessages.getMessage( "GatewayTool_ProxyPassword" ) + " ", "" );
					proxyInfo.setPassword( valueString );
				}
			}
			
			core.setProxyInfo( proxyInfo );
		}
		catch (GatewayToolException e)
		{
			console.printf(
				EdgeCMWebServiceMessages.getMessage( "GatewayTool_ErrorSettingProxyInfo" ) + " " + e.getErrorMessage() + "\n" );
			return;
		}
		
		console.printf( EdgeCMWebServiceMessages.getMessage( "GatewayTool_SettingProxyInfoSucceeded" ) + "\n" );
		
		// notify the gateway host
		
		try
		{
			console.printf( EdgeCMWebServiceMessages.getMessage(
				"GatewayTool_RestartingGatewayService" ) + "\n" );
			
			core.restartEdgeService();
			
			console.printf( EdgeCMWebServiceMessages.getMessage(
				"GatewayTool_GatewayServiceRestartedSuccessfully" ) + "\n" );
		}
		catch (GatewayToolException e)
		{
			console.printf( EdgeCMWebServiceMessages.getMessage(
				"GatewayTool_FailedToRestartGatewayService" ) + "\n" );
			return;
		}
	}
	
	private String getUserInput( Console console, String promptMessage, String defaultValue )
	{
		String valueString = console.readLine( promptMessage );
		if ((valueString == null) || valueString.trim().isEmpty())
			valueString = defaultValue;
		if (valueString != null)
			valueString = valueString.trim();
		return valueString;
	}
	
	private String getUserPasswordInput( Console console, String promptMessage, String defaultValue )
	{
		char[] passwordChars = console.readPassword( promptMessage );
		String password = new String( passwordChars );
		if ((password == null) || password.trim().isEmpty())
			password = defaultValue;
		password = password.trim();
		return password;
	}
	
	private int tryParseInt( String string, int defaultValue )
	{
		try
		{
			return Integer.parseInt( string );
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}
}

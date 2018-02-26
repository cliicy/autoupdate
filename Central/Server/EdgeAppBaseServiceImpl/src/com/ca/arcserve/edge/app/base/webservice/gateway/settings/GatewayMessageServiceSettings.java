package com.ca.arcserve.edge.app.base.webservice.gateway.settings;

import java.io.File;
import java.io.StringWriter;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;

@XmlRootElement(name = "GatewayMessageServiceSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class GatewayMessageServiceSettings
{
	public static final int DEFAULT_CONSOLE_PORT = 8015;
	public static final String DEFAULT_CONSOLE_PROTOCOL = "http";
	
	private String consoleHost = "";
	private int consolePort = DEFAULT_CONSOLE_PORT;
	private String consoleProtocol = DEFAULT_CONSOLE_PROTOCOL;
	private String consoleUuid = "";
	private String gatewayUuid = "";
	private String hostUuid = "";
	private boolean isDebugMode = false;
	
	public String getConsoleHost()
	{
		return consoleHost;
	}

	public void setConsoleHost( String consoleHost )
	{
		this.consoleHost = consoleHost;
	}

	public int getConsolePort()
	{
		return consolePort;
	}

	public void setConsolePort( int consolePort )
	{
		this.consolePort = consolePort;
	}

	public String getConsoleProtocol()
	{
		return consoleProtocol;
	}

	public void setConsoleProtocol( String consoleProtocol )
	{
		this.consoleProtocol = consoleProtocol;
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
	
	public String getHostUuid()
	{
		return hostUuid;
	}

	public void setHostUuid( String hostUuid )
	{
		this.hostUuid = hostUuid;
	}

	public boolean isDebugMode()
	{
		return isDebugMode;
	}

	public void setDebugMode( boolean isDebugMode )
	{
		this.isDebugMode = isDebugMode;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( this.getClass().getSimpleName() + " { " );
		sb.append( "consoleHost = '" + consoleHost + "'" );
		sb.append( ", consolePort = " + consolePort );
		sb.append( ", consoleProtocol = '" + consoleProtocol + "'" );
		sb.append( ", consoleUuid = '" + consoleUuid + "'" );
		sb.append( ", gatewayUuid = '" + gatewayUuid + "'" );
		sb.append( ", hostUuid = '" + hostUuid + "'" );
		sb.append( ", isDebugMode = " + isDebugMode );
		sb.append( " }" );
		
		return sb.toString();
	}

	public static GatewayMessageServiceSettings load()
	{
		return loadFromFolder( null );
	}
	
	public static GatewayMessageServiceSettings load( String settingsFilePath )
	{
		GatewayMessageServiceSettings settings = null;
		
		File settingFile = new File( settingsFilePath );
		if (!settingFile.exists())
		{
			settings = new GatewayMessageServiceSettings();
		}
		else
		{
			try
			{
				settings = JAXB.unmarshal( settingFile, GatewayMessageServiceSettings.class );
			}
			catch (Exception e)
			{
				System.err.println( e.getMessage() );
				settings = new GatewayMessageServiceSettings();
			}
		}
		
		return settings;
	}
	
	public static GatewayMessageServiceSettings loadFromFolder( String folderPath )
	{
		String settingsFilePath = getMessageServiceSettingFilePath( folderPath );
		return load( settingsFilePath );
	}
	
	/**
	 * Save the configuration file into configuration folder which is
	 * get from registry.
	 */
	public void save()
	{
		String settingsFilePath = getMessageServiceSettingFilePath( null );
		this.save( settingsFilePath );
	}
	
	/**
	 * Save the configuration file to specified path.
	 */
	public void save( String path )
	{
		File settingsFile = new File( path );
		JAXB.marshal( this, settingsFile );
	}
	
	/**
	 * Save the configuration file into specified configuration folder.
	 */
	public void saveIntoFolder( String folderPath )
	{
		String settingsFilePath = getMessageServiceSettingFilePath( folderPath );
		this.save( settingsFilePath );
	}
	
	public static String SETTING_FILE_NAME = "GatewayMessageService.xml";
	
	public static String getMessageServiceSettingFilePath( String folderPath )
	{
		if (folderPath == null)
			folderPath = CommonUtil.getConfigurationFolder( EdgeApplicationType.CentralManagement );
		if (!folderPath.endsWith( "\\" ))
			folderPath += "\\";
		return folderPath + SETTING_FILE_NAME;
	}
	
	public static void main( String[] args )
	{
		GatewayMessageServiceSettings setting = new GatewayMessageServiceSettings();
		
		StringWriter writer = new StringWriter();
		JAXB.marshal( setting, writer );
		
		System.out.println( writer.toString() );
	}

	public boolean isValid()
	{
		return (hasValidValue( this.consoleHost ) &&
			((this.consolePort > 0) && (this.consolePort <= 65535)) &&
			hasValidValue( this.consoleProtocol ) &&
			(this.consoleProtocol.equalsIgnoreCase( "http" ) || this.consoleProtocol.equalsIgnoreCase( "https" )) &&
			hasValidValue( this.consoleUuid) &&
			hasValidValue( this.gatewayUuid ) &&
			hasValidValue( this.hostUuid ));
	}
	
	private boolean hasValidValue( String string )
	{
		if (string == null)
			return false;
		
		if (string.trim().isEmpty())
			return false;
		
		return true;
	}

}

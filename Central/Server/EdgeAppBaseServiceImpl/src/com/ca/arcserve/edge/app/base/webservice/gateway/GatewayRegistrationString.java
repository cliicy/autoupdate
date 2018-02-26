package com.ca.arcserve.edge.app.base.webservice.gateway;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class GatewayRegistrationString
{
	private static final String VALUE_SEPERATOR = "\n";
	private static Logger logger = Logger.getLogger( GatewayRegistrationString.class );
	
	private String regSvrHostName;
	private int regSvrPort;
	private String regSvrProtocol;
	private String consoleUuid;
	private String gatewayUuid;
	private String gatewayHostProtocol;
	private int gatewayHostPort;
	private String gatewayHostUsername;
	private String gatewayHostPassword;

	public String getRegSvrHostName()
	{
		return regSvrHostName;
	}

	public void setRegSvrHostName( String regSvrHostName )
	{
		this.regSvrHostName = regSvrHostName;
	}

	public int getRegSvrPort()
	{
		return regSvrPort;
	}

	public void setRegSvrPort( int regSvrPort )
	{
		this.regSvrPort = regSvrPort;
	}

	public String getRegSvrProtocol()
	{
		return regSvrProtocol;
	}

	public void setRegSvrProtocol( String regSvrProtocol )
	{
		this.regSvrProtocol = regSvrProtocol;
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

	public String getGatewayHostProtocol()
	{
		return gatewayHostProtocol;
	}

	public void setGatewayHostProtocol( String gatewayHostProtocol )
	{
		this.gatewayHostProtocol = gatewayHostProtocol;
	}

	public int getGatewayHostPort()
	{
		return gatewayHostPort;
	}

	public void setGatewayHostPort( int gatewayHostPort )
	{
		this.gatewayHostPort = gatewayHostPort;
	}

	public String getGatewayHostUsername()
	{
		return gatewayHostUsername;
	}

	public void setGatewayHostUsername( String gatewayHostUsername )
	{
		this.gatewayHostUsername = gatewayHostUsername;
	}

	public String getGatewayHostPassword()
	{
		return gatewayHostPassword;
	}

	public void setGatewayHostPassword( String gatewayHostPassword )
	{
		this.gatewayHostPassword = gatewayHostPassword;
	}

	public String toEncodedString()
	{
		List<String> valueList = new ArrayList<>();
		addValue( valueList, this.getRegSvrHostName() );
		addValue( valueList, this.getRegSvrPort() );
		addValue( valueList, this.getRegSvrProtocol() );
		addValue( valueList, this.getConsoleUuid() );
		addValue( valueList, this.getGatewayUuid() );
		addValue( valueList, this.getGatewayHostProtocol() );
		addValue( valueList, this.getGatewayHostPort() );
		addValue( valueList, this.getGatewayHostUsername() );
		addValue( valueList, this.getGatewayHostPassword() );
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < valueList.size(); i ++)
		{
			if (i > 0) sb.append( VALUE_SEPERATOR );
			String value = valueList.get( i );
			sb.append( value );
		}
		String plainText = sb.toString();
		String encrypted = Encoder.getInstance().encode( plainText );
		
		try
		{
			return URLEncoder.encode( encrypted, "UTF-8" );
		}
		catch (UnsupportedEncodingException e)
		{
			logger.error( "Error encoding registration key", e );
			return "";
		}
	}
	
	private void addValue( List<String> valueList, String value )
	{
		valueList.add( (value == null) ? "" : value );
	}
	
	private void addValue( List<String> valueList, int value )
	{
		valueList.add( Integer.toString( value ) );
	}
	
	public static GatewayRegistrationString parseEncodedString( String string ) throws Exception
	{
		String decoded = URLDecoder.decode( string.trim(), "UTF-8" );
		String decrypted = Encoder.getInstance().decode( decoded );
		
		String[] parts = decrypted.split( "\\n", -1 );
		
		GatewayRegistrationString regStr = new GatewayRegistrationString();
		regStr.setRegSvrHostName( parts[0] );
		regStr.setRegSvrPort( Integer.parseInt( parts[1] ) );
		regStr.setRegSvrProtocol( parts[2] );
		regStr.setConsoleUuid( parts[3] );
		regStr.setGatewayUuid( parts[4] );
		regStr.setGatewayHostProtocol( parts[5] );
		regStr.setGatewayHostPort( Integer.parseInt( parts[6] ) );
		regStr.setGatewayHostUsername( parts[7] );
		regStr.setGatewayHostPassword( parts[8] );
		
		return regStr;
	}
}

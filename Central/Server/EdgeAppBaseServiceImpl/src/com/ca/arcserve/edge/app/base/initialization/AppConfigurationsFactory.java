package com.ca.arcserve.edge.app.base.initialization;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.common.udpapplication.UDPApplication.UDPApplicationType;
import com.ca.arcserve.edge.app.base.initialization.console.ConsoleAppConfigurations;
import com.ca.arcserve.edge.app.base.initialization.gateway.GatewayAppConfigurations;

public class AppConfigurationsFactory
{
	private static Logger logger = Logger.getLogger( AppConfigurationsFactory.class );
	private static AppConfigurationsFactory instance = new AppConfigurationsFactory();
	
	private AppConfigurationsFactory()
	{
	}
	
	public static AppConfigurationsFactory getInstance()
	{
		return instance;
	}
	
	public IAppConfigurations getAppConfigurations( UDPApplicationType appType )
	{
		switch (appType)
		{
		case Console:
			return new ConsoleAppConfigurations();
			
		case Gateway:
			return new GatewayAppConfigurations();
			
		default:
			logger.error(
				getLogPrefix() + "Unsupported application type: " + appType, new Exception() );
			return null;
		}
	}
	
	private String getLogPrefix()
	{
		return this.getClass().getSimpleName() + "." +
			Thread.currentThread().getStackTrace()[2].getMethodName() + "(): ";
	}
}

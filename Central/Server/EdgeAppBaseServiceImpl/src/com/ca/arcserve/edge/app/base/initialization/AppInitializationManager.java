package com.ca.arcserve.edge.app.base.initialization;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ca.arcserve.edge.app.base.common.udpapplication.UDPApplication;
import com.ca.arcserve.edge.app.base.common.udpapplication.UDPApplication.UDPApplicationType;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.common.EdgeCommonServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;

public class AppInitializationManager
{
	private static Logger logger = Logger.getLogger( AppInitializationManager.class );
	private static AppInitializationManager instance = new AppInitializationManager();
	
	private IAppConfigurations appConfig = null;
	private boolean isInitalized = false;
	
	private AppInitializationManager()
	{
	}
	
	public static AppInitializationManager getInstance()
	{
		return instance;
	}
	
	public void initializeApplication( UDPApplicationType appType )
	{
		String logPrefix = getLogPrefix();
		
		if (this.isInitalized)
		{
			Exception ex = new Exception( "Try to Inialize more than once." );
			logger.warn( logPrefix + "The application was initialized already. Stack trace:", ex );
			return;
		}
		
		IAppConfigurations appConfig =
			AppConfigurationsFactory.getInstance().getAppConfigurations( appType );
		
		// ASNative.dll will be loaded here, and at the same time, class CommonUtil got
		// loaded, and all static member of the class got initialized. If error occurs before
		// this, all messages should go to Tomcat's logs. (Need to be tested)
		String logConfigFileName = appConfig.getLogsConfigFileName();
		configureLogs( logConfigFileName );
		
		try
		{
			logger.info( logPrefix + "-------------------------------------------------" );
			logger.info( logPrefix + "  ARCserve UDP Application (" + appType + ")" );
			logger.info( logPrefix + "-------------------------------------------------" );
			logger.info( logPrefix + "Begin initialize the application (" + appType + ")..." );
			logger.info( logPrefix + "Logs was initialized, check " + logConfigFileName + " for logs configuration details." );
			
			UDPApplication app = appConfig.createApplicationObject();
			UDPApplication.setInstance( app );
			
			List<IAppInitializer> initList = appConfig.getInitializationList();
			for (IAppInitializer initializer : initList)
			{
				try
				{
					initializer.initialize();
				}
				catch (Exception e)
				{
					logger.error(
						logPrefix + initializer.getClass().getSimpleName() + ".initialize() failed.", e );
					throw e;
				}
			}
			
			this.appConfig = appConfig;
			this.isInitalized = true;
			
			logger.info( logPrefix + "Application (" + appType + ") initialized successfully." );
		}
		catch (Exception e)
		{
			logger.error( logPrefix + "Application (" + appType + ") initialized failed.", e );
			throw e;
		}
		
		try
		{
			EdgeVersionInfo versionInfo = new EdgeCommonServiceImpl().getVersionInformation();
			StringBuilder sb = new StringBuilder();
			sb.append( logPrefix );
			sb.append( "Application version: " );
			sb.append( versionInfo.getMajorVersion() + "." + versionInfo.getMinorVersion() );
			sb.append( " build " + versionInfo.getBuildNumber() );
			if ((versionInfo.getUpdateNumber() != null) && !versionInfo.getUpdateNumber().trim().isEmpty())
				sb.append( "Update " + versionInfo.getUpdateNumber() + " build " + versionInfo.getUpdateBuildNumber() );
			logger.info( sb.toString() );
		}
		catch (Exception e)
		{
			logger.error( logPrefix + "Error getting version information.", e );
		}
		
		try
		{
			logger.info( logPrefix + "-------------------------------------------------" );
		}
		catch (Exception e)
		{
		}
	}
	
	public void uninitializeApplication()
	{
		if (!this.isInitalized)
			return;
		
		if (this.appConfig == null)
			return;
		
		List<IAppInitializer> initList = appConfig.getInitializationList();
		for (int i = initList.size() - 1; i >= 0; i --)
		{
			IAppInitializer initializer = initList.get( i );
			initializer.uninitialize();
		}
	}
	
	private String getLogPrefix()
	{
		return this.getClass().getSimpleName() + "." +
			Thread.currentThread().getStackTrace()[2].getMethodName() + "(): ";
	}
	
	private void configureLogs( String logConfigFileName )
	{
		try {
			
			System.setProperty("PMLogPath", CommonUtil.getLogFolder(EdgeApplicationType.CentralManagement));
	    	String log4jFile = CommonUtil.getConfigurationFolder(EdgeApplicationType.CentralManagement) + logConfigFileName;
			PropertyConfigurator.configureAndWatch(log4jFile);
			System.out.println("Configure Log4J succeed, file = " + log4jFile);
		} catch (Throwable e) {
			System.err.println("Failed to config Log4J. " + e.getMessage());
		}
	}
}

package com.ca.arcserve.edge.app.base.servlets;

import java.util.Locale;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.dllloader.DllLoader;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;

public class EnvironmentContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		EdgeExecutors.shutdownNow();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			String applicationType = sce.getServletContext().getInitParameter("ApplicationType");
			if (applicationType != null) {
				EdgeWebServiceContext.setApplicationType(EdgeApplicationType.valueOf(applicationType));				
			}
		} catch (Throwable e) {
			System.err.println("Failed to get application type. " + e.getMessage());
		}
		
		try {
			EdgeCommonUtil.initProtocolAndPort();
		} catch (Throwable e) {
			System.err.println("Failed to parse Tomcat's Server.xml to get port and protocol. " + e.getMessage());
		}
		
		try {
			//fanda03 fix 158826; must use getDateFormatLocale() not getServerLocale() because of en_GB.
			Locale local = DataFormatUtil.getDateFormatLocale();
			Locale.setDefault(local);
			CommonUtil.prepareTrustAllSSLEnv();
		} catch (Throwable e) {
			System.err.println("Failed to prepare for SSL access environment. " + e.getMessage());
		}
		
		try {
			DllLoader.loadBaseDlls();
		} catch (Throwable e) {
			System.err.println("Failed to load base native DLLs. " + e.getMessage());
		}
		
		EdgeCommonUtil.initialCommonNative();
		
		try {
			CommonUtil.generateUUIDForNecessary();
		} catch (Exception e) {
			System.err.println("Failed to generate UUID if necessary. " + e.getMessage());
		}
		
		configLog4J(sce);
		
		EdgeExecutors.start();
	}
	
	protected void configLog4J(ServletContextEvent sce) {
		String logFileName = sce.getServletContext().getInitParameter("LogFileName");
		if (logFileName == null || logFileName.isEmpty()) {
			logFileName = "log4j-ARCAPP.properties";
		}
		
		try {
			System.setProperty("PMLogPath", CommonUtil.getLogFolder(EdgeApplicationType.CentralManagement));
	    	String log4jFile = CommonUtil.getConfigurationFolder(EdgeApplicationType.CentralManagement) + logFileName;
			PropertyConfigurator.configureAndWatch(log4jFile);
			System.out.println("Configure Log4J succeed, file = " + log4jFile);
		} catch (Throwable e) {
			System.err.println("Failed to config Log4J. " + e.getMessage());
		}
	}

}

package com.ca.arcserve.edge.app.base.initialization.common;

import java.util.Locale;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.dllloader.DllLoader;
import com.ca.arcserve.edge.app.base.initialization.IAppInitializer;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;

public abstract class BaseEnvironmentInitializer implements IAppInitializer
{

	@Override
	public void initialize()
	{
		try {
//			String applicationType = sce.getServletContext().getInitParameter("ApplicationType");
//			if (applicationType != null) {
//				EdgeWebServiceContext.setApplicationType(EdgeApplicationType.valueOf(applicationType));				
//			}
			EdgeWebServiceContext.setApplicationType(EdgeApplicationType.CentralManagement);
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
		
		EdgeExecutors.start();
		
		doAppSpecificInitialization();
	}

	@Override
	public void uninitialize()
	{
		doAppSpecificUninitialization();
		
		EdgeExecutors.shutdownNow();
	}

	protected abstract void doAppSpecificInitialization();
	protected abstract void doAppSpecificUninitialization();
}

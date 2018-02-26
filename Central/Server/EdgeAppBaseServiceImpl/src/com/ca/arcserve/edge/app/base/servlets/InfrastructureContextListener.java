package com.ca.arcserve.edge.app.base.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.db.Configuration;
import com.ca.arcserve.edge.app.base.db.IConfiguration;
import com.ca.arcserve.edge.app.base.db.impl.ConnectionManagerUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;

public class InfrastructureContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			IConfiguration configuration = Configuration.getInstance(EdgeWebServiceImpl.DBConfigFilePath);
			ConnectionManagerUtil.initDBPool(configuration);
			DaoFactory.initDao(ConnectionManagerUtil.getDs(), new NativeFacadeImpl());
		} catch (Exception e) {
			System.err.println("Configure database connection pool failed. " + e.getMessage());
		}
	}

}

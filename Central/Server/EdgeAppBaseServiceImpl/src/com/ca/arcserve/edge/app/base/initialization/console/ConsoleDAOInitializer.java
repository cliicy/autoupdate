package com.ca.arcserve.edge.app.base.initialization.console;

import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.db.Configuration;
import com.ca.arcserve.edge.app.base.db.IConfiguration;
import com.ca.arcserve.edge.app.base.db.impl.ConnectionManagerUtil;
import com.ca.arcserve.edge.app.base.initialization.common.BaseAppInitializer;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;

public class ConsoleDAOInitializer extends BaseAppInitializer
{

	@Override
	protected void doInitialization()
	{
		try {
			IConfiguration configuration = Configuration.getInstance(EdgeWebServiceImpl.DBConfigFilePath);
			ConnectionManagerUtil.initDBPool(configuration);
			DaoFactory.initDao(ConnectionManagerUtil.getDs(), new NativeFacadeImpl());
		} catch (Exception e) {
			System.err.println("Configure database connection pool failed. " + e.getMessage());
		}
	}

	@Override
	protected void doUninitialization()
	{
	}

}

package com.ca.arcserve.edge.app.base.webservice.test;

import org.junit.Before;

import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.db.Configuration;
import com.ca.arcserve.edge.app.base.db.IConfiguration;
import com.ca.arcserve.edge.app.base.db.impl.ConnectionManagerUtil;
import com.ca.arcserve.edge.app.base.dllloader.DllLoader;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;

/**
 * base test class
 * 
 * @author zhati04
 */
public abstract class AbstractTestCase {
	protected static final String hostName = "zhata01-hv6";
	protected static final int domainId = 5;
	protected static final GatewayId gatewayId = new GatewayId(3);
	protected NativeFacadeImpl nativeFacade = new NativeFacadeImpl();
	@Before
	public void initDataSource()throws Exception{
		DllLoader.loadAsNative();
		EdgeExecutors.start();
		IConfiguration d = null;
		d = Configuration.getInstance(EdgeWebServiceImpl.DBConfigFilePath);
		ConnectionManagerUtil.initDBPool(d);
		DaoFactory.initDao(ConnectionManagerUtil.getDs(), new NativeFacadeImpl());
	}
}

package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import com.ca.arcserve.edge.app.base.appdaos.EdgeDaoCommonExecuter;

public class DataBaseConnectionFactory {
	private static DataBaseConnectionFactory connFactory = new DataBaseConnectionFactory();
	private DataBaseConnectionFactory() {}
	
	public static DataBaseConnectionFactory getInstance() {
		return connFactory;
	}
	
	public EdgeDaoCommonExecuter createEdgeDaoCommonExecuter() {
		return new EdgeDaoCommonExecuter(false);
	}
}

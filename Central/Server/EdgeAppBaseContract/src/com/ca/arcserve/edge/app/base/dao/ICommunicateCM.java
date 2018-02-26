package com.ca.arcserve.edge.app.base.dao;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public interface ICommunicateCM {
	
	/**Get latest database configuration from CM, and compare the latest configuration
	 * with local. If latest configuration differs from local, then reconnect to new db.
	 * @throws EdgeServiceFault, if fail to get new db or fail to connect to new db.
	 * @return if cm has the same database as report, return true, else false 
	 */
	boolean reConnectCmDatabase() throws EdgeServiceFault;
}

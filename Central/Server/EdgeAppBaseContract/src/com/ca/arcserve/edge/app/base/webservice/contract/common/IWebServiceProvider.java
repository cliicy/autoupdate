package com.ca.arcserve.edge.app.base.webservice.contract.common;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public interface IWebServiceProvider<P> {
	/**
	 * now it only support uulong webservice, so the serviceID parameter is useless;
	 *  for compatible with other webservice , please change the implementation;
	 */
	P getProxy(ConnectionContext context);
	
	/**
	 * Now the providers we implemented are all has a defect; it cannot detect if the proxy is closed and cannot prevent usage after close;
	 * to implement it we need a new dynamic proxy class; we leave this feature to develop in future;
	 * @param proxy
	 * @throws EdgeServiceFault
	 */
	void closeWsProxy(P proxy) throws EdgeServiceFault;
}

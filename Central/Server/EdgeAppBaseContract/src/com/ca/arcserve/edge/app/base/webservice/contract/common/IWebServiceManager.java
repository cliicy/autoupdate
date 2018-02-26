package com.ca.arcserve.edge.app.base.webservice.contract.common;



public interface IWebServiceManager {

	public<P> IWebServiceProvider<P>  getProviderByProxyType( Class<P> proxy_Class );

}

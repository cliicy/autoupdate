package com.ca.arcserve.edge.app.base.webservice.client;

import javax.xml.ws.WebServiceException;






public class BaseWebServiceClientProxy {
	protected int port;
	protected String protocol="http:";
	protected String host = "";
	protected Object service = null;

	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public BaseWebServiceClientProxy(int port, String protocol,Object service,String host) {
		super();
		this.port = port;
		this.protocol = protocol;
		this.service = service;
		this.host = host;
	}
	public Object getService() {
		return service;
	}
	public void setService(Object service) {
		this.service = service;
	}
	public IBaseService getBaseService(){
		if(service instanceof IBaseService)
			return (IBaseService)service;
			throw new WebServiceException("IBaseService is not supported");
	}
}

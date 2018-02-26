package com.ca.arcserve.edge.app.base.webservice.client;

public interface ISubWebServiceFactory {
	public  BaseWebServiceClientProxy getService(String protocol,String host, int port);
}

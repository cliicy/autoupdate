package com.ca.arcserve.edge.app.base.webservice.abintegration;

import java.io.Serializable;

import javax.xml.ws.WebServiceException;

public class ABFunWebServiceClientProxy implements Serializable {
	private static final long serialVersionUID = 1809645585770445662L;
	private int port;
	private String protocol;
	private String host;
	private Object service;

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getProtocol() {
		return this.protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public ABFunWebServiceClientProxy(IABFuncService service) {
		this.protocol = "http:";
		this.host = "";
		this.service = null;

		this.service = service;
	}

	public ABFunWebServiceClientProxy(int port, String protocol, Object service) {
		this(port, protocol, service, "");
	}

	public ABFunWebServiceClientProxy(int port, String protocol, Object service,
			String host) {
		this.protocol = "http:";
		this.host = "";
		this.service = null;

		this.port = port;
		this.protocol = protocol;
		this.service = service;
		this.host = host;
	}

	public IABFuncService getService() {
		if (this.service instanceof IABFuncService)
			return ((IABFuncService) this.service);
		throw new WebServiceException("IABFuncService is not supported");
	}

	public void setService(IABFuncService service) {
		this.service = service;
	}
}

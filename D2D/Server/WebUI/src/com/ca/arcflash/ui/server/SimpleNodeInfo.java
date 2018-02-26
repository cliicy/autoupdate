package com.ca.arcflash.ui.server;

import com.ca.arcflash.rps.webservice.RPSWebServiceClientProxy;
import com.ca.arcflash.ui.client.model.ConnectionProtocol;
import com.ca.arcflash.webservice.WebServiceClientProxy;

public class SimpleNodeInfo {
	public ConnectionProtocol protocol = ConnectionProtocol.HTTP;
	public String host;
	public int port;
	public String user;
	public String password;
	public String uuid;
	public RPSWebServiceClientProxy client;
	public WebServiceClientProxy clientD2D;
	
	public SimpleNodeInfo(){
		
	}

	public SimpleNodeInfo(String host, int port, ConnectionProtocol protocol,
			String user, String password) {
		super();
		this.host = host;
		this.port = port;
		this.protocol = protocol;
		this.user = user;
		this.password = password;
	}
	
	public boolean isInfoEquals(SimpleNodeInfo info){
		return host.equals(info.host) && port==info.port && user.equals(info.user) && password.equals(info.password);
	}
	
	public ConnectionProtocol getProtocol() {
		return protocol;
	}
	public void setProtocol(ConnectionProtocol protocol) {
		this.protocol = protocol;
	}
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
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public RPSWebServiceClientProxy getClient() {
		return client;
	}

	public void setClient(RPSWebServiceClientProxy client) {
		this.client = client;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public WebServiceClientProxy getClientD2D() {
		return clientD2D;
	}

	public void setClientD2D(WebServiceClientProxy clientD2D) {
		this.clientD2D = clientD2D;
	}
}

package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;

public class ConnectionContext implements Serializable {

	private static final long serialVersionUID = -6545599713292453982L;
	
	private GatewayEntity gateway;
	
	private String protocol;
	private String host;
	private int port;
	
	private String serviceId = "";
	private int connectTimeout;
	private int requestTimeout;
	
	private String domain;
	private String username;
	
	@NotPrintAttribute
	private String password;
	
	private String authUuid;
	
	private int authenticationType;
	
	public ConnectionContext() {
		
	}
	
	public ConnectionContext(Protocol protocol, String host, int port) {
		this.protocol = protocol == Protocol.Https ? "https" : "http";
		this.host = host;
		this.port = port;
	}
	
	public ConnectionContext(String protocol, String host, int port) {
		this.protocol = protocol;
		this.host = host;
		this.port = port;
	}
	
	public ConnectionContext buildServiceId(String serviceId) {
		this.serviceId = serviceId;
		return this;
	}
	
	public ConnectionContext buildCredential(String username, String password, String domain) {
		this.username = username;
		this.password = password;
		this.domain = domain;
		return this;
	}
	
	public ConnectionContext buildAuthUuid(String authUuid) {
		this.authUuid = authUuid;
		return this;
	}
	
	public GatewayEntity getGateway()
	{
		return gateway;
	}

	public void setGateway( GatewayEntity gateway )
	{
		this.gateway = gateway;
	}

	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
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
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public int getConnectTimeout() {
		return connectTimeout;
	}
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	public int getRequestTimeout() {
		return requestTimeout;
	}
	public void setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@EncryptSave
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAuthUuid() {
		return authUuid;
	}
	public void setAuthUuid(String authUuid) {
		this.authUuid = authUuid;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public int getAuthenticationType() {
		return authenticationType;
	}

	public void setAuthenticationType(int authenticationType) {
		this.authenticationType = authenticationType;
	}

	@Override
	public String toString() {
		return "ConnectionContext{gateway=" + ((gateway == null) ? "null" : gateway)
				+ ", protocol=" + protocol
				+ ", host=" + host 
				+ ", port=" + port 
				+ ", serviceId=" + serviceId
				+ ", usernmae=" + username
				+ ", domain=" + domain
				+ "}";
	}

}

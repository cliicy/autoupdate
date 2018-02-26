package com.ca.arcserve.edge.app.base.webservice.contract.vSphere;

import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public class ProxyConnectInfo {
	public ProxyConnectInfo(GatewayId gatewayId, String hostName, int port, String protocol,
			String username, String password, String domain, String uuid) {
		this.setGatewayId( gatewayId );
		this.hostName = hostName;
		this.port = port;
		this.protocol = protocol;
		this.username = username;
		this.password = password;
		this.domain = domain;
		this.uuid = uuid;
	}

	private String hostName;
	private int port;
	private String protocol;
	private String username;
	private String password;
	private String domain;
	private String uuid;
	private GatewayId gatewayId;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public GatewayId getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(GatewayId gatewayId) {
		if (gatewayId == null)
			gatewayId = GatewayId.INVALID_GATEWAY_ID;
		this.gatewayId = gatewayId;
	}
}

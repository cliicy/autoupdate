package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public class Hypervisor implements Serializable {

	private static final long serialVersionUID = 6123794649860684734L;
	private int id;
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;
	private String serverName;
	private String username;
	private String password;
	private Protocol protocol;
	private int port;
	private int socketCount;
	private boolean isVCenter;
	private String esxHost;
	public Hypervisor() {
	}
	
	public Hypervisor(GatewayId gatewayId, String serverName) {
		this.setGatewayId( gatewayId );
		this.serverName = serverName;
	}
	
	public Hypervisor(GatewayId gatewayId, String serverName, String username, String password) {
		this.setGatewayId( gatewayId );
		this.serverName = serverName;
		this.username = username;
		this.password = password;
	}
	
	public Hypervisor(GatewayId gatewayId, String serverName, String username, String password, Protocol protocol, int port) {
		this.setGatewayId( gatewayId );
		this.serverName = serverName;
		this.username = username;
		this.password = password;
		this.protocol = protocol;
		this.port = port;
	}
	
	public GatewayId getGatewayId()
	{
		return gatewayId;
	}

	public void setGatewayId( GatewayId gatewayId )
	{
		if (gatewayId == null)
			gatewayId = GatewayId.INVALID_GATEWAY_ID;
		this.gatewayId = gatewayId;
	}

	public DiscoveryESXOption toEsx() {
		DiscoveryESXOption option = new DiscoveryESXOption();
		
		option.setEsxServerName(serverName);
		option.setEsxUserName(username);
		option.setEsxPassword(password);
		option.setProtocol(protocol);
		option.setPort(port);
		
		return option;
	}
	
	public DiscoveryHyperVOption toHyperV() {
		DiscoveryHyperVOption option = new DiscoveryHyperVOption();
		
		option.setServerName(serverName);
		option.setUsername(username);
		option.setPassword(password);
		option.setGatewayId(gatewayId);
		
		return option;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
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

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getSocketCount() {
		return socketCount;
	}

	public void setSocketCount(int socketCount) {
		this.socketCount = socketCount;
	}

	public String getEsxHost() {
		return esxHost;
	}

	public void setEsxHost(String esxHost) {
		this.esxHost = esxHost;
	}

	public boolean isVCenter() {
		return isVCenter;
	}

	public void setVCenter(boolean isVCenter) {
		this.isVCenter = isVCenter;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}

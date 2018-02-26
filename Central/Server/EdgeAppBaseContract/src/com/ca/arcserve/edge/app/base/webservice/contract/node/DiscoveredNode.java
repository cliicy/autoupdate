package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public class DiscoveredNode implements Serializable {

	private static final long serialVersionUID = 5991469010688331725L;
	
	private int id;
	private String hostname;
	private String domain;
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
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

}

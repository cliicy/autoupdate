package com.ca.arcserve.edge.app.base.webservice.contract.discovery;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;

public class DiscoverySettingForESX extends DiscoverySetting{

	private static final long serialVersionUID = 2919468842154991267L;
	private Protocol protocol;
	private int port;
	
	public DiscoverySettingForESX() {

	}
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public Protocol getProtocol() {
		return protocol;
	}
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	
}

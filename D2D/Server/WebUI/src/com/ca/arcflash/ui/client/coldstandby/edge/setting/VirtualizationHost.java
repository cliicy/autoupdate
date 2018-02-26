package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import java.io.Serializable;

import com.sencha.gxt.core.client.util.Util;

public class VirtualizationHost implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6625661350528364158L;
	
	private String hostName;
	private String userName;
	private String password;
	private String protocol;
	private int    port;
	
	public VirtualizationHost() {
		
	}
	
	public VirtualizationHost(String hostName, String userName,String password, int port, String protocol) {
		this.hostName = hostName;
		this.userName = userName;
		this.password = password;
		this.port = port;
		this.protocol = protocol;
	}
	
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(this == obj)
			return true;
		
		VirtualizationHost virtualizationHost = (VirtualizationHost)obj;
		if(virtualizationHost == null) {
			return false;
		}
		if(Util.equalWithNull(virtualizationHost.getHostName(), hostName) &&
				Util.equalWithNull(virtualizationHost.getUserName(), userName) &&
				Util.equalWithNull(virtualizationHost.getPassword(), password) &&
				Util.equalWithNull(virtualizationHost.getProtocol(), protocol) &&
				Util.equalWithNull(virtualizationHost.getPort(), port)){
			return true;
		} else {
			return false;
		}
	}
	
	public void copyVirtualHost(VirtualizationHost host) {
		if(host == null)
			return;
		
		hostName = host.getHostName();
		userName = host.getUserName();
		password = host.getPassword();
		port = host.getPort();
		protocol = host.getProtocol();
	}
	
	public void clean() {
		hostName = "";
		userName = "";
		password = "";
		port = 0;
		protocol = "";
	}
}

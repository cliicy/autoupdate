package com.ca.arcserve.edge.app.base.webservice.contract.discovery;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public abstract class DiscoverySetting implements Serializable {
	
	private static final long serialVersionUID = 6541434944648626823L;
	private int id;
	private String hostname;
	private String username;
	private @NotPrintAttribute String password;
	private String filter;
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;
	private String siteName;
	
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
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
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
	
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		result = prime * result
			+ ((gatewayId == null) ? 0 : gatewayId.hashCode());
		result = prime * result
				+ ((siteName == null) ? 0 : siteName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiscoverySetting other = (DiscoverySetting) obj;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (id != other.id)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		
		if (!this.gatewayId.equals( other.getGatewayId() ))
			return false;
		
		if (siteName == null) {
			if (other.siteName != null)
				return false;
		} else if (!siteName.equals(other.siteName))
			return false;
		
		return true;
	}
}

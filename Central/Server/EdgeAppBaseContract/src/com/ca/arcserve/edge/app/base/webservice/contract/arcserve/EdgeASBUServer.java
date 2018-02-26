package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EdgeASBUServer {
	private int hostId;
	private int domainId;
	private int serverId;
	private String serverName;
	private String domainName;
	private int serverClass;
	private String username;
	private @NotPrintAttribute String password;
	private int port;
	private int protocol;
	private int authMode;
	private int serverStatus;
	private String siteName;
	
	public int getHostId() {
		return hostId;
	}
	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	public int getDomainId() {
		return domainId;
	}
	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public int getServerClass() {
		return serverClass;
	}
	public void setServerClass(int serverClass) {
		this.serverClass = serverClass;
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
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getProtocol() {
		return protocol;
	}
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
	public int getAuthMode() {
		return authMode;
	}
	public void setAuthMode(int authMode) {
		this.authMode = authMode;
	}
	public int getServerStatus() {
		return serverStatus;
	}
	public void setServerStatus(int serverStatus) {
		this.serverStatus = serverStatus;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}	
}

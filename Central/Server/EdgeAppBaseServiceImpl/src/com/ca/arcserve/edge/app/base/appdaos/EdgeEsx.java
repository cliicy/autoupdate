package com.ca.arcserve.edge.app.base.appdaos;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EdgeEsx {
	
	private int id;
	private String hostname;
	private String username;
	private @NotPrintAttribute String password;
	private int protocol;
	private int port;
	private int visible;
	private int servertype;
	private String uuid;
	private String description;
	private int isAutoDiscovery;
	public int getServertype() {
		return servertype;
	}
	public void setServertype(int servertype) {
		this.servertype = servertype;
	}
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
	@EncryptSave
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getProtocol() {
		return protocol;
	}
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getVisible() {
		return visible;
	}
	public void setVisible(int visible) {
		this.visible = visible;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getIsAutoDiscovery() {
		return isAutoDiscovery;
	}
	public void setIsAutoDiscovery(int isAutoDiscovery) {
		this.isAutoDiscovery = isAutoDiscovery;
	}
}

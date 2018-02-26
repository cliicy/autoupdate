package com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EsxEntity implements Serializable{
	private static final long serialVersionUID = -7106451306455435448L;
	
	private int id;
	private String hostName;
	private String userName;
	@NotPrintAttribute
	private String password;
	private int protocol;
	private int port;
	private int serverType;
	private int visible;
	private int essential;
	private int socketCount;
	private String description;
	private String uuid;
	private int gatewayId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getServerType() {
		return serverType;
	}
	public void setServerType(int serverType) {
		this.serverType = serverType;
	}
	public int getVisible() {
		return visible;
	}
	public void setVisible(int visible) {
		this.visible = visible;
	}
	public int getEssential() {
		return essential;
	}
	public void setEssential(int essential) {
		this.essential = essential;
	}
	public int getSocketCount() {
		return socketCount;
	}
	public void setSocketCount(int socketCount) {
		this.socketCount = socketCount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public int getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(int gatewayId) {
		this.gatewayId = gatewayId;
	}
}

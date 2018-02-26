package com.ca.arcserve.edge.app.rps.appdaos.model;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EdgeRpsConnectionInfo {
	private int node_id;
	private int protocol;
	private int port;
	private String username;
	private @NotPrintAttribute String password;
	private String major_version;
	private String minor_version;
	private String build_number;
	private String update_number;
	private int manage;
	private @NotPrintAttribute String uuid;
	
	@EncryptSave
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public int getManage() {
		return manage;
	}
	public void setManage(int managed) {
		this.manage = managed;
	}
	public int getNode_id() {
		return node_id;
	}
	public void setNode_id(int node_id) {
		this.node_id = node_id;
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
	public String getMajor_version() {
		return major_version;
	}
	public void setMajor_version(String major_version) {
		this.major_version = major_version;
	}
	public String getMinor_version() {
		return minor_version;
	}
	public void setMinor_version(String minor_version) {
		this.minor_version = minor_version;
	}
	public String getBuild_number() {
		return build_number;
	}
	public void setBuild_number(String build_number) {
		this.build_number = build_number;
	}
	public String getUpdate_number() {
		return update_number;
	}
	public void setUpdate_number(String update_number) {
		this.update_number = update_number;
	}
	
}

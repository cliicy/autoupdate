package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUAuthenticationType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;

public class D2DConnectInfo implements Serializable{

	private static final long serialVersionUID = -6246187451922783532L;
	private String username;
	private @NotPrintAttribute String password;
	private String uuid;
	private int port;
	private int type;
	private Protocol protocol;
	private String majorversion;
	private String minorversion;
	private String updateversionnumber;
	private String buildnumber;
	private NodeManagedStatus managed;
	private ASBUAuthenticationType authType;
	
	public NodeManagedStatus getManaged() {
		return managed;
	}

	public void setManaged(NodeManagedStatus managed) {
		this.managed = managed;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMajorversion() {
		return majorversion;
	}

	public void setMajorversion(String majorversion) {
		this.majorversion = majorversion;
	}

	public String getMinorversion() {
		return minorversion;
	}

	public void setMinorversion(String minorversion) {
		this.minorversion = minorversion;
	}

	public String getUpdateversionnumber() {
		return updateversionnumber;
	}

	public void setUpdateversionnumber(String updateversionnumber) {
		this.updateversionnumber = updateversionnumber;
	}

	public String getBuildnumber() {
		return buildnumber;
	}

	public void setBuildnumber(String buildnumber) {
		this.buildnumber = buildnumber;
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public ASBUAuthenticationType getAuthType() {
		return authType;
	}

	public void setAuthType(ASBUAuthenticationType authType) {
		this.authType = authType;
	}
}

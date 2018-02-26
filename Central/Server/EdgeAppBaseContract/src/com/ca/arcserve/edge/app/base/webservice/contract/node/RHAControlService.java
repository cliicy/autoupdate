/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;

/**
 * @author lijwe02
 * 
 */
public class RHAControlService implements Serializable {
	private static final long serialVersionUID = 1336187603285813756L;

	private int id;
	private int protocol;
	private String server;
	private int port;
	private String userName;
	private String password;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getWSDL() {
		return (protocol == Protocol.Http.ordinal() ? "http://" : "https://") + server + ":" + port
				+ "/ws_man/xosoapapi.asmx?wsdl";
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + id;
		hash = 31 * hash + port;
		hash = 31 * hash + protocol;
		hash = 31 * hash + (null == server ? 0 : server.hashCode());
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof RHAControlService)) {
			return false;
		}
		RHAControlService that = (RHAControlService) obj;
		return this.id == that.id && this.port == that.port && this.protocol == that.protocol
				&& (this.server == that.server || (this.server != null && this.server.equals(that.server)));
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
}

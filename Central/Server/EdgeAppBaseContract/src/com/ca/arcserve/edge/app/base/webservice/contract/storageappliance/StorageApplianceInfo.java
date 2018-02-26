package com.ca.arcserve.edge.app.base.webservice.contract.storageappliance;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;

public class StorageApplianceInfo implements Serializable {
	
	public enum ApplianceMode{
		STANDALONE(0,"Standalone"),
		// Feb sprint part 2 - Issue with previous update
		CLUSTER(1,"VServer"),
		VFILER(2,"Vfiler");
		
		private int id;
		private String name;
		
		private ApplianceMode(int id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public int getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
	}
	private static final long serialVersionUID = -8379664299054151927L;
	private int id;
	private String hostname;
	private String type;
	private String username;
	private @NotPrintAttribute String password;
	private int port;
	private Protocol protocol;
	private ApplianceMode mode;
	private String dataIp;
	private boolean verified = true;
	private String name; //site or gateway name
	
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	
	public ApplianceMode getMode() {
		return mode;
	}
	
	public void setMode(ApplianceMode mode) {
		this.mode = mode;
	}
	
	public String getDataIp() {
		return dataIp;
	}
	
	public void setDataIp(String dataIp) {
		this.dataIp = dataIp;
	}
	
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	
	public boolean isVerified() {
		return verified;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String siteName) {
		this.name = siteName;
	}

}

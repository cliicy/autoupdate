package com.ca.arcserve.edge.app.base.appdaos;

import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EdgeArcserveConnectInfo {
	private int hostid;
	private String causer;
	private String capasswd;
	private int authmode;
	private int protocol;
	private int port;
	private int type;
	private String version;
	private int gdb_branchid;
	private int managed;

	public int getManaged() {
		return managed;
	}

	public void setManaged(int managed) {
		this.managed = managed;
	}
	
	public int getGdb_branchid() {
		return gdb_branchid;
	}

	public void setGdb_branchid(int gdb_branchid) {
		this.gdb_branchid = gdb_branchid;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getHostid() {
		return hostid;
	}
	public void setHostid(int hostid) {
		this.hostid = hostid;
	}
	public String getCauser() {
		return causer;
	}
	public void setCauser(String causer) {
		this.causer = causer;
	}
	@EncryptSave
	public String getCapasswd() {
		return capasswd;
	}
	public void setCapasswd(String capasswd) {
		this.capasswd = capasswd;
	}
	public int getAuthmode() {
		return authmode;
	}
	public void setAuthmode(int authmode) {
		this.authmode = authmode;
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

}

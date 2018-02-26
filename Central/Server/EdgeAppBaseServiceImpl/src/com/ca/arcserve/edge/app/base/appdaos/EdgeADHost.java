package com.ca.arcserve.edge.app.base.appdaos;

public class EdgeADHost {
	
	private int rhostid;
	private String rhostname;
	private String domainname;
	private String username;
	
	public int getRhostid() {
		return rhostid;
	}
	public void setRhostid(int rhostid) {
		this.rhostid = rhostid;
	}
	public String getRhostname() {
		return rhostname;
	}
	public void setRhostname(String rhostname) {
		this.rhostname = rhostname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}
	public String getDomainname() {
		return domainname;
	}

}

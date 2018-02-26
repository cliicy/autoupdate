package com.ca.arcserve.edge.app.base.appdaos;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EdgeAD {
	
	private int id;
	private String username;
	@NotPrintAttribute
	private String password;
	private String filter;
	private String domainControler;
	private int isAutoDiscovery;
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
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
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	public String getDomainControler() {
		return domainControler;
	}
	public void setdomainControler(String domainControler) {
		this.domainControler = domainControler;
	}
	public int getIsAutoDiscovery() {
		return isAutoDiscovery;
	}
	public void setIsAutoDiscovery(int isAutoDiscovery) {
		this.isAutoDiscovery = isAutoDiscovery;
	}
}

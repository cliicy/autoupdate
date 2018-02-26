package com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class AdEntity implements Serializable{
	private static final long serialVersionUID = -3530104270594298159L;
	private int id;
	private String username;
	@NotPrintAttribute
	private String password;
	private String filter;
	private String domainControler;
	private int gatewayId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public void setDomainControler(String domainControler) {
		this.domainControler = domainControler;
	}
	public int getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(int gatewayId) {
		this.gatewayId = gatewayId;
	}
}

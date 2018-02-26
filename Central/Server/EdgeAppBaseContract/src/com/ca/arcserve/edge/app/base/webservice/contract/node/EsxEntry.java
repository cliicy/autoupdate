package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class EsxEntry implements Serializable {

	private static final long serialVersionUID = -3480391494706460399L;
	
	private int id;
	private String hostname;
	
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

}

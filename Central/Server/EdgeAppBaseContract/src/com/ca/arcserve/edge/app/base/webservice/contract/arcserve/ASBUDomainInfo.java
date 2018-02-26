package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import java.io.Serializable;
import java.util.List;

public class ASBUDomainInfo implements Serializable {
	private static final long serialVersionUID = -4241572404814576901L;

	private int id;
	private String name;
	private List<ASBUServerInfo> servers;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ASBUServerInfo> getServers() {
		return servers;
	}

	public void setServers(List<ASBUServerInfo> servers) {
		this.servers = servers;
	}
}

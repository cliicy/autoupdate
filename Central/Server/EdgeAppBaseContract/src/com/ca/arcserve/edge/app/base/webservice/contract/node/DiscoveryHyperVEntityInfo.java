package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType( XmlAccessType.FIELD )
public class DiscoveryHyperVEntityInfo implements Serializable {
	private static final long serialVersionUID = 6470795846287533916L;
	private boolean cluster;
	private String hypervName;
	private String ActiveNodeName;
	private List<String> serverList = new ArrayList<String>();
	
	
	public boolean isCluster() {
		return cluster;
	}
	public void setCluster(boolean cluster) {
		this.cluster = cluster;
	}
	public String getHypervName() {
		return hypervName;
	}
	public void setHypervName(String hypervName) {
		this.hypervName = hypervName;
	}
	public List<String> getServerList() {
		return serverList;
	}
	public void setServerList(List<String> serverList) {
		this.serverList = serverList;
	}
	public String getActiveNodeName() {
		return ActiveNodeName;
	}
	public void setActiveNodeName(String activeNodeName) {
		ActiveNodeName = activeNodeName;
	}
	
}


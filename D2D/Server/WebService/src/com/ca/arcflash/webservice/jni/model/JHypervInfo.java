package com.ca.arcflash.webservice.jni.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class JHypervInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private boolean isCluster;
	private String hypervName; //if standalone hyperv, hypervName is hyperv server name; if cluster, hypervName is cluster name
	private boolean isCurrentHostServer; //used for cluster
	private String nodeName; //used for cluster

	public String getHypervName() {
		return hypervName;
	}
	public void setHypervName(String hypervName) {
		this.hypervName = hypervName;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public boolean getCurrentHostServer() {
		return isCurrentHostServer;
	}
	public void setCurrentHostServer(boolean isCurrentHostServer) {
		this.isCurrentHostServer = isCurrentHostServer;
	}
	public boolean getCluster() {
		return isCluster;
	}
	public void setCluster(boolean isCluster) {
		this.isCluster = isCluster;
	}
}

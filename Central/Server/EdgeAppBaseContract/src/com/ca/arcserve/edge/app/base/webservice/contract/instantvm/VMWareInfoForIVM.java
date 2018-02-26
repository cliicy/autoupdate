package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;

public class VMWareInfoForIVM implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String esxHost;
	private String esxHostRefID;
	
	private boolean cluster;
	private String clusterName;  // Set when "isCluster" is true
	private String clusterRefID;  // Set when "isCluster" is true
	
	private String resourcePool;  // resource pool or virtual apps
	private String resourcePoolRefID;
	
	private String datacenter;
	private String datacenterRefID;
	
	public VMWareInfoForIVM() {
		this.esxHost = "";
		this.esxHostRefID = "";
		this.cluster = false;
		this.clusterName = "";
		this.clusterRefID = "";
		this.resourcePool = "";
		this.resourcePoolRefID = "";
		this.datacenter = "";
		this.datacenterRefID = "";
	}
	
	public String getEsxHost() {
		return esxHost;
	}
	public void setEsxHost(String esxHost) {
		this.esxHost = esxHost;
	}
	
	public String getEsxHostRefID() {
		return esxHostRefID;
	}
	public void setEsxHostRefID(String esxHostRefID) {
		this.esxHostRefID = esxHostRefID;
	}

	public boolean isCluster() {
		return cluster;
	}
	public void setCluster(boolean isCluster) {
		this.cluster = isCluster;
	}
	
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	
	public String getClusterRefID() {
		return clusterRefID;
	}
	public void setClusterRefID(String clusterRefID) {
		this.clusterRefID = clusterRefID;
	}
	
	public String getResourcePool() {
		return resourcePool;
	}
	public void setResourcePool(String resourcePool) {
		this.resourcePool = resourcePool;
	}
	
	public String getResourcePoolRefID() {
		return resourcePoolRefID;
	}
	public void setResourcePoolRefID(String resourcePoolRefID) {
		this.resourcePoolRefID = resourcePoolRefID;
	}
	
	public String getDatacenter() {
		return datacenter;
	}
	public void setDatacenter(String datacenter) {
		this.datacenter = datacenter;
	}
	
	public String getDatacenterRefID() {
		return datacenterRefID;
	}
	public void setDatacenterRefID(String datacenterRefID) {
		this.datacenterRefID = datacenterRefID;
	}
	
}

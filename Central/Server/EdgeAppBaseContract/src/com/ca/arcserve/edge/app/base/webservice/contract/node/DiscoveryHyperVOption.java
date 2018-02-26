package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public class DiscoveryHyperVOption implements Serializable{

	private static final long serialVersionUID = 2851186411411606750L;
	
	private String serverName;
	private String username;
	private @NotPrintAttribute String password;
	private int jobType	= 0;  // 0:manual job  1:schedule job
	private int id;
	private HypervProtectionType hypervProtectionType = HypervProtectionType.STANDALONEANDCLUSTER;
	private int taskId;
	private boolean isCluster;
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;
		
	public GatewayId getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(GatewayId gatewayId) {
		if (gatewayId == null)
			gatewayId = GatewayId.INVALID_GATEWAY_ID;
		this.gatewayId = gatewayId;
	}
	public boolean isCluster() {
		return isCluster;
	}
	public void setCluster(boolean isCluster) {
		this.isCluster = isCluster;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getJobType() {
		return jobType;
	}
	public void setJobType(int jobType) {
		this.jobType = jobType;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public HypervProtectionType getHypervProtectionType() {
		return hypervProtectionType;
	}
	public void setHypervProtectionType(HypervProtectionType hypervProtectionType) {
		this.hypervProtectionType = hypervProtectionType;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
}

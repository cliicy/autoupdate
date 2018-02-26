package com.ca.arcserve.edge.app.base.appdaos;

public class EdgePolicyHostUuid {
	private int hostId;
	private String policyUuid;
	private int policyId;
	private int policyType;
	private int deployStatus;
	
	public int getHostId() {
		return hostId;
	}
	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	public String getPolicyUuid() {
		return policyUuid;
	}
	public void setPolicyUuid(String policyUuid) {
		this.policyUuid = policyUuid;
	}
	public int getPolicyId() {
		return policyId;
	}
	public void setPolicyId(int policyId) {
		this.policyId = policyId;
	}
	public int getPolicyType() {
		return policyType;
	}
	public void setPolicyType(int policyType) {
		this.policyType = policyType;
	}
	public int getDeployStatus() {
		return deployStatus;
	}
	public void setDeployStatus(int deployStatus) {
		this.deployStatus = deployStatus;
	}
}

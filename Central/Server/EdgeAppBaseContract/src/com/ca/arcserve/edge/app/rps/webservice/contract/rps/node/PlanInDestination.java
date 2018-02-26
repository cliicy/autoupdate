package com.ca.arcserve.edge.app.rps.webservice.contract.rps.node;

import java.io.Serializable;
import java.util.List;

public class PlanInDestination implements Serializable{
	private static final long serialVersionUID = 1L;
	private String planUuid;
	private String planName;
	private long recoveryPointCount;
	private List<ProtectedNodeInDestination> nodeList;
	public String getPlanUuid() {
		return planUuid;
	}
	public void setPlanUuid(String planUuid) {
		this.planUuid = planUuid;
	}
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public long getRecoveryPointCount() {
		return recoveryPointCount;
	}
	public void setRecoveryPointCount(long recoveryPointCount) {
		this.recoveryPointCount = recoveryPointCount;
	}
	public List<ProtectedNodeInDestination> getNodeList() {
		return nodeList;
	}
	public void setNodeList(List<ProtectedNodeInDestination> nodeList) {
		this.nodeList = nodeList;
	}
}

package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;

import com.ca.arcflash.rps.webservice.data.policy.NatReplicationSettings;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;

public class RPSPolicyWrapper implements Serializable{
	private static final long serialVersionUID = 388191947057329269L;
	
	private RPSPolicy rpsPolicy = new RPSPolicy();
	private String taskId;
	// siteId and siteName used for Replication To remote RPS Belong which site
	private int siteId;
	@NonPlanContent
	private String siteName;
	// natReplicationSettings used for Replication To remote RPS NAT
	private NatReplicationSettings natReplicationSettings;
	
	public RPSPolicy getRpsPolicy() {
		return rpsPolicy;
	}
	public void setRpsPolicy(RPSPolicy rpsPolicy) {
		this.rpsPolicy = rpsPolicy;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public int getSiteId() {
		return siteId;
	}
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public NatReplicationSettings getNatReplicationSettings() {
		return natReplicationSettings;
	}
	public void setNatReplicationSettings(
			NatReplicationSettings natReplicationSettings) {
		this.natReplicationSettings = natReplicationSettings;
	}	
}

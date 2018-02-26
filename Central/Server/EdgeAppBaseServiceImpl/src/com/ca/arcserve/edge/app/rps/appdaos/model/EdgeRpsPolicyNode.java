package com.ca.arcserve.edge.app.rps.appdaos.model;


public class EdgeRpsPolicyNode extends EdgeRpsPolicy {
	private int map_id;
	private int deploy_status;
	private int deploy_reason;
	
	public int getMap_id() {
		return map_id;
	}
	public void setMap_id(int map_id) {
		this.map_id = map_id;
	}
	public int getDeploy_status() {
		return deploy_status;
	}
	public void setDeploy_status(int deploy_status) {
		this.deploy_status = deploy_status;
	}
	public int getDeploy_reason() {
		return deploy_reason;
	}
	public void setDeploy_reason(int deploy_reason) {
		this.deploy_reason = deploy_reason;
	}
	
}

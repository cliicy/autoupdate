package com.ca.arcserve.edge.app.base.webservice.contract.productdeploy;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployStatus;

public class DeployStatusInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private DeployStatus deployStatus;
	private String prograssMessage;
	private String warnningMessage;
	public DeployStatus getDeployStatus() {
		return deployStatus;
	}
	public void setDeployStatus(DeployStatus deployStatus) {
		this.deployStatus = deployStatus;
	}
	public String getPrograssMessage() {
		return prograssMessage;
	}
	public void setPrograssMessage(String prograssMessage) {
		this.prograssMessage = prograssMessage;
	}
	public String getWarnningMessage() {
		return warnningMessage;
	}
	public void setWarnningMessage(String warnningMessage) {
		this.warnningMessage = warnningMessage;
	}
}

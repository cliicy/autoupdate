package com.ca.arcserve.edge.app.base.webservice.contract.action;

public class ManageMultiNodesParameter extends ActionTaskParameter<Integer>{
	
	private static final long serialVersionUID = 1L;
	
	private boolean forceManage;
	
	public boolean isForceManage() {
		return forceManage;
	}
	public void setForceManage(boolean forceManage) {
		this.forceManage = forceManage;
	}
}

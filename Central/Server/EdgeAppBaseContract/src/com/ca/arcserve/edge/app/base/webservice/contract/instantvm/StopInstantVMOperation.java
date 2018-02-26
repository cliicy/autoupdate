package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;

public class StopInstantVMOperation implements Serializable {

	private static final long serialVersionUID = 1L; 
	
	private String instantVMJobUUID;
	private boolean forceRemove = false;

	public String getInstantVMJobUUID() {
		return instantVMJobUUID;
	}

	public void setInstantVMJobUUID(String instantVMJobUUID) {
		this.instantVMJobUUID = instantVMJobUUID;
	}

	public boolean isForceRemove() {
		return forceRemove;
	}

	public void setForceRemove(boolean forceRemove) {
		this.forceRemove = forceRemove;
	}

}

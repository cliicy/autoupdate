package com.ca.arcserve.edge.app.base.webservice.contract.action;

import java.io.Serializable;

public class UpdateMultiNodesParameter<T extends Serializable> extends ActionTaskParameter<T>{
	private static final long serialVersionUID = 886396499848701088L;
	private String globalUsername;
	private String globalPassword;
	private boolean forceManaged;
	private boolean usingOrignalCredential;
	
	public String getGlobalUsername() {
		return globalUsername;
	}
	public void setGlobalUsername(String globalUsername) {
		this.globalUsername = globalUsername;
	}
	public String getGlobalPassword() {
		return globalPassword;
	}
	public void setGlobalPassword(String globalPassword) {
		this.globalPassword = globalPassword;
	}
	public boolean isForceManaged() {
		return forceManaged;
	}
	public void setForceManaged(boolean forceManaged) {
		this.forceManaged = forceManaged;
	}
	public boolean isUsingOrignalCredential() {
		return usingOrignalCredential;
	}
	public void setUsingOrignalCredential(boolean usingOrignalCredential) {
		this.usingOrignalCredential = usingOrignalCredential;
	}
}

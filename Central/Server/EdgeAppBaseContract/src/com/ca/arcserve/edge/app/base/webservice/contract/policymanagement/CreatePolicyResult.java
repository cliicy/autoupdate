package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

import java.io.Serializable;

public class CreatePolicyResult implements Serializable {

	private static final long serialVersionUID = -2082745909602677246L;
	
	public static enum ErrorCode {
		Succeed, ProxyManagedByAnotherServer
	}
	
	private ErrorCode errorCode = ErrorCode.Succeed;
	private int newPolicyId;
	private int proxyHostId;
	private String proxyName;
	private String anotherServerName;
	
	public ErrorCode getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
	public int getNewPolicyId() {
		return newPolicyId;
	}
	
	public void setNewPolicyId(int newPolicyId) {
		this.newPolicyId = newPolicyId;
	}
	
	public int getProxyHostId() {
		return proxyHostId;
	}

	public void setProxyHostId(int proxyHostId) {
		this.proxyHostId = proxyHostId;
	}
	
	public String getProxyName() {
		return proxyName;
	}
	
	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}
	
	public String getAnotherServerName() {
		return anotherServerName;
	}
	
	public void setAnotherServerName(String anotherServerName) {
		this.anotherServerName = anotherServerName;
	}

}

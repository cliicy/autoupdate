package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.node.VMVerifyStatus.CheckType;

public class VMStatusDetail implements Serializable{
	
	private static final long serialVersionUID = 8993984586460446332L;
	private CheckType checkType;
	private int status;
	private int errorCode;
	private String[] parameters;
	public CheckType getCheckType() {
		return checkType;
	}
	public void setCheckType(CheckType checkType) {
		this.checkType = checkType;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String[] getParameters() {
		return parameters;
	}
	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
}

package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class AdminAccountValidationResultWithMessage implements Serializable{
	private static final long serialVersionUID = -8069783532751778237L;
	
	private AdminAccountValidationResult validationResult;
	private String validationMessage;

	public AdminAccountValidationResult getValidationResult() {
		return validationResult;
	}

	public void setValidationResult(AdminAccountValidationResult validationResult) {
		this.validationResult = validationResult;
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	public void setValidationMessage(String validationMessage) {
		this.validationMessage = validationMessage;
	}
}

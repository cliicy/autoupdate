package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;

public class EncryptionCompression implements Serializable{
	private static final long serialVersionUID = -8314664300757248456L;
	private boolean enableCompression;
	private boolean enableEncryption;
	private String sessionPassword;
	public boolean isEnableCompression() {
		return enableCompression;
	}
	public void setEnableCompression(boolean enableCompression) {
		this.enableCompression = enableCompression;
	}
	public boolean isEnableEncryption() {
		return enableEncryption;
	}
	public void setEnableEncryption(boolean enableEncryption) {
		this.enableEncryption = enableEncryption;
	}
	public String getSessionPassword() {
		return sessionPassword;
	}
	public void setSessionPassword(String sessionPassword) {
		this.sessionPassword = sessionPassword;
	}
}

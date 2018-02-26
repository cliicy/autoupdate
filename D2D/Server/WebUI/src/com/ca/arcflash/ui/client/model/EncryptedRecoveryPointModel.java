package com.ca.arcflash.ui.client.model;


public class EncryptedRecoveryPointModel extends RecoveryPointModel {
	
	private static final long serialVersionUID = 3071380330594586948L;
	
	public String getSessionPwd() {
		return get("encryptPwd");
	}
	
	public void setSessionPwd(String pwd) {
		set("encryptPwd", pwd);
	}
}

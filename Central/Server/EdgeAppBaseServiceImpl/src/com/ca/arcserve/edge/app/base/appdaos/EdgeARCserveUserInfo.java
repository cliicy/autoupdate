package com.ca.arcserve.edge.app.base.appdaos;

import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class EdgeARCserveUserInfo {
	private String causer;
	private String capasswd;
	
	@EncryptSave
	public String getCauser() {
		return causer;
	}
	public void setCauser(String causer) {
		this.causer = causer;
	}
	
	@EncryptSave
	public String getCapasswd() {
		return capasswd;
	}
	public void setCapasswd(String capasswd) {
		this.capasswd = capasswd;
	}
}

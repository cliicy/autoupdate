package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class JMountRecoveryPointParamsModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8582893783748193083L;
	
	private String rpsHostname;
	private String datastoreName;
	private String dest;
	private String domain;
	private String user;
	private String pwd;
	private String subPath;
	private String volGUID;
	private int encryptionType;
	private String encryptPassword;
	private String mountPath;

	public String getRpsHostname() {
		return rpsHostname;
	}
	public void setRpsHostname(String rpsHostname) {
		this.rpsHostname = ( rpsHostname==null ? "" : rpsHostname);
	}
	public String getDatastoreName() {
		return datastoreName;
	}
	public void setDatastoreName(String datastoreName) {
		this.datastoreName = (datastoreName==null) ? "" : datastoreName;
	}
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getSubPath() {
		return subPath;
	}
	public void setSubPath(String subPath) {
		this.subPath = subPath;
	}
	public String getVolGUID() {
		return volGUID;
	}
	public void setVolGUID(String volGUID) {
		this.volGUID = volGUID;
	}
	public int getEncryptionType() {
		return encryptionType;
	}
	public void setEncryptionType(int encryptionType) {
		this.encryptionType = encryptionType;
	}
	public String getEncryptPassword() {
		return encryptPassword;
	}
	public void setEncryptPassword(String encryptPassword) {
		this.encryptPassword = encryptPassword;
	}
	public String getMountPath() {
		return mountPath;
	}
	public void setMountPath(String mountPath) {
		this.mountPath = mountPath;
	}	

}

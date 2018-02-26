package com.ca.arcflash.webservice.jni.model;

import com.ca.arcflash.service.jni.model.JNetConnInfo;

public class CatalogJobContext {
	private long id;
	private long type;
	private String adminName;
	private String adminPass;
	private String vmIndentification;
	private JNetConnInfo connInfo;
	
	public CatalogJobContext() {
		
	}
	
	public CatalogJobContext(long id, long type, JNetConnInfo conn) {
		this.id = id;
		this.type = type;
		this.connInfo = conn;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getType() {
		return type;
	}
	public void setType(long type) {
		this.type = type;
	}
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	public String getAdminPass() {
		return adminPass;
	}
	public void setAdminPass(String adminPass) {
		this.adminPass = adminPass;
	}
	public JNetConnInfo getConnInfo() {
		return connInfo;
	}
	public void setConnInfo(JNetConnInfo connInfo) {
		this.connInfo = connInfo;
	}

	public String getVmIndentification() {
		return vmIndentification;
	}

	public void setVmIndentification(String vmIndentification) {
		this.vmIndentification = vmIndentification;
	}
}

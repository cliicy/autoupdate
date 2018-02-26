package com.ca.arcserve.edge.app.base.webservice.contract.destination;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class DestinationInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private int destinationId = -1;
	private String path;
	private String userName;
	private @NotPrintAttribute String password;
	public DestinationInfo(){} 
	public DestinationInfo( String path, String userName ) {
		this.path = path;
		this.userName = userName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getDestinationId() {
		return destinationId;
	}
	public void setDestinationId(int destinationId) {
		this.destinationId = destinationId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	@EncryptSave
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}

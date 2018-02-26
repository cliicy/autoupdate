package com.ca.arcserve.edge.app.base.webservice.contract.configuration;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;

public class CmInfo implements Serializable{
	
	private static final long serialVersionUID = -85882644448546869L;
	private String host;
	private int port;
	private String protocol;
	private String userName;
	private @NotPrintAttribute String password;
	
	public boolean contentEqualsOther(Object otherObject){
		if( this == otherObject )
			return true;
		if( null == otherObject ){
			return false;
		}
		
		if(otherObject instanceof CmInfo) {
			CmInfo temp = (CmInfo)otherObject;
			
			return this.stringsAreEqualIgnoreCase(this.host, temp.host)
				&& this.port == temp.port
				&& this.stringsAreEqualIgnoreCase(this.protocol, temp.protocol)
				&& this.stringsAreEqualIgnoreCase(this.userName, temp.userName)
				&& this.stringsAreEqualIgnoreCase(this.password, temp.password);
		}
		
		return false;
	}
	
	public boolean stringsAreEqualIgnoreCase(String source, String dest) {
		if(source==null){
			if(dest ==null) return true;
			else return false;
		}else
			return source.equalsIgnoreCase(dest);
	}

	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

}

package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class RPSConverterNode implements Serializable{
	private static final long serialVersionUID = 4827829448746254138L;
	private String converterName;
	private String converterProtocol;
	private int converterPort;
	private String converterUsername;
	private String converterPassword;
	public String getConverterName() {
		return converterName;
	}
	public void setConverterName(String converterName) {
		this.converterName = converterName;
	}
	public String getConverterProtocol() {
		return converterProtocol;
	}
	public void setConverterProtocol(String converterProtocol) {
		this.converterProtocol = converterProtocol;
	}
	public int getConverterPort() {
		return converterPort;
	}
	public void setConverterPort(int converterPort) {
		this.converterPort = converterPort;
	}
	public String getConverterUsername() {
		return converterUsername;
	}
	public void setConverterUsername(String converterUsername) {
		this.converterUsername = converterUsername;
	}
	public String getConverterPassword() {
		return converterPassword;
	}
	public void setConverterPassword(String converterPassword) {
		this.converterPassword = converterPassword;
	}
	
}

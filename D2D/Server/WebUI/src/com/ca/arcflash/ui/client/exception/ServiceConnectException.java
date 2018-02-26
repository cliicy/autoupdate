package com.ca.arcflash.ui.client.exception;


public class ServiceConnectException extends ClientException {
	
	private static final long serialVersionUID = 4551537403613180755L;
	
	private String errorCode;
	
	public ServiceConnectException(){
	
	}
	
	public ServiceConnectException(String errorCode, String displayMessage){
		this.errorCode = errorCode;
		this.setDisplayMessage(displayMessage);
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}

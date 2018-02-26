package com.ca.arcflash.ui.client.exception;


public class BusinessLogicException extends ClientException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3535815490018469685L;
	
	private String errorCode;
	
	public BusinessLogicException(){
	
	}
	
	public BusinessLogicException(String errorCode, String displayMessage){
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

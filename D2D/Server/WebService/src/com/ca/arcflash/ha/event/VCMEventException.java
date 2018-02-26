package com.ca.arcflash.ha.event;

public class VCMEventException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public VCMEventException(String message){
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}

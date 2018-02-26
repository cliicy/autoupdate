package com.ca.arcflash.ui.client.exception;

public class ClientException extends Exception {
	private static final long serialVersionUID = 2989542769848623359L;
	private String displayMessage;
	
	public String getDisplayMessage() {
		return displayMessage;
	}
	public void setDisplayMessage(String displayMessage) {
		this.displayMessage = displayMessage;
	}
}

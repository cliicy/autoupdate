package com.ca.arcserve.edge.app.base.webservice.contract.action;


public class SendRegistrationEmailsParameter extends ActionTaskParameter<Integer>{
	
	private static final long serialVersionUID = 1L;
	private String consoleURL;
	public String getConsoleURL() {
		return consoleURL;
	}
	public void setConsoleURL(String consoleURL) {
		this.consoleURL = consoleURL;
	}
}

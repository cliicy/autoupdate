/**
 * 
 */
package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.exception;

/**
 * @author lijwe02
 * 
 */
public class ConverterCredentialsInvalidException extends Exception {
	private static final long serialVersionUID = -1835102753222631473L;
	private String hostName;

	public ConverterCredentialsInvalidException() {

	}

	public ConverterCredentialsInvalidException(String message, String hostName) {
		super(message);
		this.hostName = hostName;
	}
	public ConverterCredentialsInvalidException(String hostName) {
		this.hostName = hostName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
}

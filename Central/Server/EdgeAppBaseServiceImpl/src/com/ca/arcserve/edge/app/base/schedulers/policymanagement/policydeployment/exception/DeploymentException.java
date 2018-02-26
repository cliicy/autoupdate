/**
 * 
 */
package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.exception;

/**
 * @author lijwe02
 * 
 */
public class DeploymentException extends Exception {
	private static final long serialVersionUID = 2098833349400946262L;

	private int errorCode;

	public DeploymentException() {
		super();
	}

	public DeploymentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DeploymentException(String message, Throwable cause) {
		super(message, cause);
	}

	public DeploymentException(String message) {
		super(message);
	}

	public DeploymentException(Throwable cause) {
		super(cause);
	}

	public DeploymentException(int errorCode) {
		this.errorCode = errorCode;
	}

	public DeploymentException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public DeploymentException(int errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}

	public DeploymentException(String message, int errorCode, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public DeploymentException(String message, int errorCode, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

}

package com.ca.arcflash.webservice.jni;

public class WSJNIException extends Exception {

	private static final long serialVersionUID = -6212227761513531443L;
	private int errorCode = 0;
	public WSJNIException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	@Override
	public String toString() {
		return "WSJNIException [errorCode=" + errorCode + ", getMessage()="
				+ getMessage() + "]";
	}
	
}

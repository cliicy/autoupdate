package com.ca.arcflash.webservice.common;

public class LicenseCheckException extends Exception {
	public static String FAIL_CONNECT_EDGE = "FAIL_CONNECT_EDGE";
	public static String EDGE_INTERNAL_ERROR = "EDGE_INTERNAL_ERROR";
	private String errorCode;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3032203623813381633L;
	
    public LicenseCheckException(String message) {
    	super(message);
    }
    
    public LicenseCheckException(String message, String errorCode) {
    	super(message);
    	this.errorCode = errorCode;
    }
    
    public LicenseCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public LicenseCheckException(Throwable cause) {
        super(cause);
    }
    
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}

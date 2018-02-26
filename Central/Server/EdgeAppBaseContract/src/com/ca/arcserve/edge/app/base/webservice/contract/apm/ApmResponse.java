package com.ca.arcserve.edge.app.base.webservice.contract.apm;

import java.io.Serializable;

public class ApmResponse implements Serializable{
	
	private static final long serialVersionUID = -2826446783245266272L;
	public static final int ERROR_SUCCESS = 0;
//	public static final int ERROR_CONNECT_BACKEND = 1;  //connection to back end exe is broken;
//	public static final int ERROR_TIMEOUT = 2;			//back end exe does not response
	public static final int ERROR_BACKEND = 3;				//back end exe return an error message
	
	private EdgePatchType patchType;
	private int errorCode;
	private String message;
	
	public ApmResponse(){
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public void setPatchType(EdgePatchType patchType) {
		this.patchType = patchType;
	}

	public EdgePatchType getPatchType() {
		return patchType;
	}
}

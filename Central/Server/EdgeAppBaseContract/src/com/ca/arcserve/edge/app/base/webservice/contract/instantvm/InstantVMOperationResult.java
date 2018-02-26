package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;

public class InstantVMOperationResult implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final long FAIL_CONNECT_RECOVERY_SERVER = -1000;
	private boolean result;
	private long errorCode;
	private String ivmJobUUID;
	public InstantVMOperationResult( boolean result ) {
		this.result = result;
	}
	public InstantVMOperationResult() {
		this.result = false;
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public long getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(long errorCode) {
		this.errorCode = errorCode;
	}
	public String getIVMJobUUID() {
		return ivmJobUUID;
	}
	public void setIVMJobUUID(String ivmJobUUID) {
		this.ivmJobUUID = ivmJobUUID;
	}

}

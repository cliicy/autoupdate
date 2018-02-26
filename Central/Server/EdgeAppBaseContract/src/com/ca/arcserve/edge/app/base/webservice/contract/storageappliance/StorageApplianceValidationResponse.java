package com.ca.arcserve.edge.app.base.webservice.contract.storageappliance;

import java.io.Serializable;

public class StorageApplianceValidationResponse implements Serializable{
	private static final long serialVersionUID = -428891338134886501L;
	
	public enum Reason{
		DUPLICATE,
		AUTHENTICTION,
		SUCCESS
	}

	private Reason reason;
	private String msg;
	
	public Reason getReason() {
		return reason;
	}
	public void setReason(Reason reason) {
		this.reason = reason;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}

package com.ca.arcserve.edge.app.base.webservice.contract.destination.cloudaccount;

import java.io.Serializable;

public class CloudAccountValidationResponse implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Reason{
		ACCOUNT_NAME_ALREADY_EXIST,
		BUCKET_NAME_ALREADY_EXIST,
		SUCCESS;
		
		public boolean equals(Reason other) {
			if (null == other) {
				return false;
			}
			return this.ordinal() == other.ordinal();
		}
	}
	
	private Reason reason;

	public Reason getReason() {
		return reason;
	}

	public void setReason(Reason reason) {
		this.reason = reason;
	}
	
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
	
}

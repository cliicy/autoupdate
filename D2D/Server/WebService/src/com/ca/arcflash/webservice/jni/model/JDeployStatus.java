package com.ca.arcflash.webservice.jni.model;

public class JDeployStatus {

	public static final int DEPLOY_IN_PROGRESS = 1;
	public static final int DEPLOY_SUCCESS = 2;
	public static final int DEPLOY_FAILED = 3;
	public static final int DEPLOY_NOT_STARTED = 4;
	public static final int DEPLOY_THIRD_PARTY = 5;
	public static final int DEPLOY_COPYING_IMAGE = 6;
	public static final int DEPLOY_WAITING = 7;

	private int status;

	private String message;

	private int percentage;
	
	private long msgCode;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public long getMsgCode() {
		return msgCode;
	}

	public void setMsgCode(long msgCode) {
		this.msgCode = msgCode;
	}

}

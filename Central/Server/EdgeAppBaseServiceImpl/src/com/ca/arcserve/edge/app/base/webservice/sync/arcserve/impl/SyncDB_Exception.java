package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class SyncDB_Exception extends Exception {
	private static final long serialVersionUID = -5761251744627968787L;
	private int errorCode = 0;
	private String errorMsg;

	SyncDB_Exception(Throwable e) {
		super(e);
	}

	public SyncDB_Exception(String msg) {
		super();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = new Date(System.currentTimeMillis());

		errorCode = 0;
		errorMsg = "[" + df.format(d) + "]" + " [Error:]" + 0
				+ " [Exception]: " + msg;

	}

	public SyncDB_Exception(int error, String msg) {
		super();
		errorCode = error;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = new Date(System.currentTimeMillis());

		errorCode = error;
		errorMsg = "[" + df.format(d) + "]" + " [Error:]" + error
				+ " [Exception]: " + msg;

	}

	@Override
	public String getMessage(){
		return errorMsg;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getErrorCode() {
		return errorCode;
	}	
}

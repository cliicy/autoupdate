package com.ca.arcflash.webservice.jni.model;

import java.io.Serializable;

public class JHypervResult implements Serializable{
	private static final long serialVersionUID = 1L;
	private String errorHyperv;
	public String getErrorHyperv() {
		return errorHyperv;
	}
	public void setErrorHyperv(String errorHyperv) {
		this.errorHyperv = errorHyperv;
	}
}

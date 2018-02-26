package com.ca.arcflash.webservice.jni.model;

import java.io.Serializable;

public class JRWLong implements Serializable{

	private static final long serialVersionUID = 1L;
	private long value = 0;
	
	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
}

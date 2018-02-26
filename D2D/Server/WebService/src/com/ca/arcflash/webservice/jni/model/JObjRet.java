package com.ca.arcflash.webservice.jni.model;

public class JObjRet<E> {
	private E item;
	private long retCode;

	public E getItem() {
		return item;
	}

	public void setItem(E item) {
		this.item = item;
	}

	public long getRetCode() {
		return retCode;
	}

	public void setRetCode(long retCode) {
		this.retCode = retCode;
	}
}

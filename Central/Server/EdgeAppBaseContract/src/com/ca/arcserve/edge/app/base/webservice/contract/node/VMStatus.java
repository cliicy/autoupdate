package com.ca.arcserve.edge.app.base.webservice.contract.node;


public enum VMStatus {
	
	VISIBLE(1),
	INVISIBLE(2),
	DELETED(3);
	
	private int value;
	private VMStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
}

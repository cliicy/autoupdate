package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

public enum InstantVMSortCol {
	name("name"),
	recoveryPoint("recoveryPoint"),
	vmLocation("vmLocation"),
	recoveryServer("recoveryServer"),
	description("description");

	private final String value;
	
	InstantVMSortCol(String value){
		this.value = value;
	}
	
	public String value() {
		return value;
	}

	public static InstantVMSortCol fromValue(String v) {
		for (InstantVMSortCol c : InstantVMSortCol.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
	
}

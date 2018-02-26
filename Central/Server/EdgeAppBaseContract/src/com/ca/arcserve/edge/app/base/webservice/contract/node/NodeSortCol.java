package com.ca.arcserve.edge.app.base.webservice.contract.node;

public enum NodeSortCol {
	hostname("rhostname"),
	policy("policyname"),
	policyStatus("policyStatus"),
	vmname("vmname"),
	vcenter("center"),
	d2dStatus("d2dStatus"),
	lastBackupResult("lastD2DBackupResult"),
	lastBackupTime("lastD2DBackupTime"),
	os("os"),
	username("username"),
	verifyStatus("verifyStatus"),
	jobs("jobPhase"),
	converter("converter"),
	nodeDescription("nodedesc"),
	siteName("siteName");

	private final String value;
	
	NodeSortCol(String value){
		this.value = value;
	}
	
	public String value() {
		return value;
	}

	public static NodeSortCol fromValue(String v) {
		for (NodeSortCol c : NodeSortCol.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
	
}

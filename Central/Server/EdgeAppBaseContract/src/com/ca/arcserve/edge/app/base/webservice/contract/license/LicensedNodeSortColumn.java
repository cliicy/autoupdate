package com.ca.arcserve.edge.app.base.webservice.contract.license;

public enum LicensedNodeSortColumn {
	nodeName("nodeName"),
	vmName("vmName"),
	hypervisor("hypervisor");
	
	private final String value;
	
	LicensedNodeSortColumn(String value){
		this.value = value;
	}
	
	public String value() {
		return value;
	}
}

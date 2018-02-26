package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

public enum ProtectedResourceType {
	
	node(false),
	group_esx(true),
	group_hyperv(true),
	group_customer(true);
	
	private boolean group;
	
	private ProtectedResourceType(boolean group) {
		this.group = group;
	}
	
	public boolean isGroup() {
		return group;
	}
	
	public static ProtectedResourceType parseInt(int value){
		switch (value) {
		case 0:
			return node;
		case 1:
			return group_esx;
		case 2:
			return group_hyperv;
		case 3:
			return group_customer;
		default:
			return node;
		}
	}
}

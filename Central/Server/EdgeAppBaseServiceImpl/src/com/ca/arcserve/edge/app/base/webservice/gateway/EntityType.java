package com.ca.arcserve.edge.app.base.webservice.gateway;

public enum EntityType {
	Node(0),
	VMBackupProxy(1),
	Converter(2),
	HyperVServer(3),
	VSphereEntity(4),
	vCloudGroup(5), //don't use this, use 4
	AD(6),
	Policy(7),
	CloudAccount(8), //this is for Filecopy cloud account
	StorageArray(9), //this is for hardware storage appliance
	ShareFolder(10),
	CustomerGroup(11)
	;
	private int value;
	private EntityType(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}

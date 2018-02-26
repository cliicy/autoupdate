package com.ca.arcflash.ui.client.model;

public enum CloudVendorType {
//	AmazonS3(0), WindowsAzure(1), IronMountain(2), I365(3), FileSystem(4), Eucalyptus(5), Invalid(-1);
	AmazonS3(0), WindowsAzure(1), Eucalyptus(5), CACloud(6);
	private int value;

	private CloudVendorType(int v) {
		this.value = v;
	}

	public int getValue() {
		return value;
	}
	
	
	
}

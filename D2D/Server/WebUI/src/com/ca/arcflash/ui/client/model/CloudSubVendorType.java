package com.ca.arcflash.ui.client.model;

public enum CloudSubVendorType {
//	AmazonS3(0), WindowsAzure(1), IronMountain(2), I365(3), FileSystem(4), Eucalyptus(5), Invalid(-1);
	AmazonS3(0), WindowsAzure(1), WindowsFujistu(2) ,Eucalyptus(5), AmazonS3Compatible(6), WindowsAzureCompatible(7);
	private int value;

	private CloudSubVendorType(int v) {
		this.value = v;
	}

	public int getValue() {
		return value;
	}
	
	public static CloudSubVendorType getCloudSubVendorById(int id){
		CloudSubVendorType[] subVendorTypes = CloudSubVendorType.values();
		for (CloudSubVendorType cloudSubVendorType : subVendorTypes) {
			if(id == cloudSubVendorType.getValue())
				return cloudSubVendorType;
		}
		
		return null;
	}
	
	
	
}
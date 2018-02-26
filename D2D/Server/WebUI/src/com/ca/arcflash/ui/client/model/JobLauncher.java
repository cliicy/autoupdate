package com.ca.arcflash.ui.client.model;

public enum JobLauncher {
	D2D(0),VSPHERE(1),RPS(2);
	private long value;
	
	private JobLauncher(long v){
		this.value = v;
	}
	
	public long getValue() {
		return value;
	}

}

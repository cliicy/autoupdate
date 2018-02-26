package com.ca.arcserve.edge.app.base.webservice.contract.productdeploy;

public enum ProductDeployPhase {
	Initialize(1), 
	Connect(2), 
	Verify(3), 
	CopyImage(4), 
	Install(5), 
	Reboot(6),
	Finish(7);
	private final int value;

	private ProductDeployPhase(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}

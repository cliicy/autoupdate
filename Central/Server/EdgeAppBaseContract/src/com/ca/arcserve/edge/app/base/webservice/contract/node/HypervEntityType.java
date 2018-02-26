package com.ca.arcserve.edge.app.base.webservice.contract.node;

public enum HypervEntityType {
	HypservStandAloneVM(1),
	HypervStandAloneVMINCluster(2),
	HypervServer(3),
	HypervCluster(4);
	
	private int value;
	
	HypervEntityType(int value){
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public HypervEntityType parseValue(int value){
		if(value==1){
			return HypservStandAloneVM;
		}else if (value == 2) {
			return HypervStandAloneVMINCluster;
		}else if(value == 3){
			return HypervServer;
		}else {
			return HypervCluster;
		}
	}
}

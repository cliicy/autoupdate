package com.ca.arcserve.edge.app.rps.webservice.contract.rps.policy;


public enum PolicyDeployReason {
	Assign(0),UnAssign(1);
	
	private final int value;
	
	PolicyDeployReason(int value){
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public static PolicyDeployReason parse(int value){
		switch(value){
		case 0:
			return PolicyDeployReason.Assign;
		case 1:
			return PolicyDeployReason.UnAssign;
			default:
				return null;
		}
	}

}

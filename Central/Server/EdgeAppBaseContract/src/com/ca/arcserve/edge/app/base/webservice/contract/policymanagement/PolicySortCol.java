package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

public enum PolicySortCol {
	
	planName("policyname");
//	protectedNodeCount("protectedNodeCount"),
//	sucNodeCount("sucNodeCount"),   
//	warningNodeCount("warningNodeCount"),   
//	errNodeCount("errNodeCount"),   
//	policyStatus("policyStatus"),
//	activeJobCount("activeJobCount");   

	private final String value;
	
	PolicySortCol(String value){
		this.value = value;
	}
	
	public String value() {
		return value;
	}

	public static PolicySortCol fromValue(String v) {
		for (PolicySortCol c : PolicySortCol.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}

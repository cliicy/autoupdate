package com.ca.arcserve.edge.app.rps.appdaos.model;

public enum RpsDaoGroupType {
	EdgeRpsUnAssignGroups(0),
	EdgeRpsAllGroups (-1);
	
	
	private final int value;
	
	RpsDaoGroupType(int value){
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public static RpsDaoGroupType parse(int value){
		switch (value) {
		case 0:
			return EdgeRpsUnAssignGroups;
		case -1:
			return EdgeRpsAllGroups;
		default:
			return null;
		}
	}
}

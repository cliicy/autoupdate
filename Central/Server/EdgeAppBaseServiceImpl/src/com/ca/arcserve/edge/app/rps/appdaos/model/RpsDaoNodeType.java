package com.ca.arcserve.edge.app.rps.appdaos.model;

public enum RpsDaoNodeType {
	EdgeRpsAllNodes (0);
	
	private final int value;
	
	RpsDaoNodeType(int value){
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public static RpsDaoNodeType pars(int value){
		switch(value){
		case 0:
			return EdgeRpsAllNodes;
		default:
			return null;
		}
	}
}

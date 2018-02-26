package com.ca.arcserve.edge.app.base.webservice.contract.node;

public enum NodeManagedStatus {
	
	/**
	 * The management status of node is unknown.
	 */
	Unknown,
	
	/**
	 * The node is managed by UDP console.
	 */
	Managed,
	
	/**
	 * The node is not managed by UDP console.
	 */
	Unmanaged;
	
	public static NodeManagedStatus parseInt(int value) {
		switch (value) {
		case 1:
			return Managed;
		case 2:
			return Unmanaged;
		default:
			return Unknown;
		}
	}
}	

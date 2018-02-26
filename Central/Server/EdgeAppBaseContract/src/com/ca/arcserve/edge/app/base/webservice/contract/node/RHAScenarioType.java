/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.node;

/**
 * @author lijwe02
 * 
 */
public enum RHAScenarioType {
	D2DIntegrated(1), HBBUIntegrated(2);

	private final int value;

	private RHAScenarioType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}

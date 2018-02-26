/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

/**
 * @author lijwe02
 * 
 */
public class RHAScenario implements Serializable {
	private static final long serialVersionUID = 1817050031869751628L;

	private long id;
	private String name;
	private String signature;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}

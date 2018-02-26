package com.ca.arcserve.edge.app.base.webservice.contract.discovery;

import com.ca.arcserve.edge.app.base.webservice.contract.node.HypervProtectionType;

public class DiscoverySettingForHyperV  extends DiscoverySetting {

	private static final long serialVersionUID = -7348904706860648669L;
	private HypervProtectionType hypervType;
	
	public DiscoverySettingForHyperV() {
		
	}

	public HypervProtectionType getHypervType() {
		return hypervType;
	}

	public void setHypervType(HypervProtectionType hypervType) {
		this.hypervType = hypervType;
	}
	
}

package com.ca.arcserve.edge.app.base.webservice.contract.discovery;

public class DiscoverySettingForAD extends DiscoverySetting {

	private static final long serialVersionUID = 3844859579421281520L;
	
	private String domainControler;

	public String getDomainControler() {
		return domainControler;
	}

	public void setDomainControler(String domainControler) {
		this.domainControler = domainControler;
	}

}

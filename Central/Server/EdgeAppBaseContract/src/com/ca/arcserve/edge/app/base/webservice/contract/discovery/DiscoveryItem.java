package com.ca.arcserve.edge.app.base.webservice.contract.discovery;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class DiscoveryItem implements Serializable, BeanModelTag {

	private static final long serialVersionUID = -2987458462792608012L;
	private DiscoverySetting discoverySetting;
	private DiscoveryHistory discoveryHistory;
	
	public DiscoverySetting getDiscoverySetting() {
		return discoverySetting;
	}
	public void setDiscoverySetting(DiscoverySetting discoverySetting) {
		this.discoverySetting = discoverySetting;
	}
	public DiscoveryHistory getDiscoveryHistory() {
		return discoveryHistory;
	}
	public void setDiscoveryHistory(DiscoveryHistory discoveryHistory) {
		this.discoveryHistory = discoveryHistory;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((discoverySetting == null) ? 0 : discoverySetting.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiscoveryItem other = (DiscoveryItem) obj;
		if (discoverySetting == null) {
			if (other.discoverySetting != null)
				return false;
		} else if (!discoverySetting.equals(other.discoverySetting))
			return false;
		return true;
	}
	
}

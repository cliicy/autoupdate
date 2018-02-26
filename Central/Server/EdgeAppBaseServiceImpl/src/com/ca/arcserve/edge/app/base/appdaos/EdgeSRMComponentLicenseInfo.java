package com.ca.arcserve.edge.app.base.appdaos;

public class EdgeSRMComponentLicenseInfo {
	public int getTopologyId() {
		return topologyId;
	}
	public void setTopologyId(int topologyId) {
		this.topologyId = topologyId;
	}
	public int getComponentId() {
		return componentId;
	}
	public void setComponentId(int componentId) {
		this.componentId = componentId;
	}
	public int getMajorVersion() {
		return majorVersion;
	}
	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}
	public int getMinorVersion() {
		return minorVersion;
	}
	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}
	public int getTotalLicenses() {
		return totalLicenses;
	}
	public void setTotalLicenses(int totalLicenses) {
		this.totalLicenses = totalLicenses;
	}
	public int getUsedLicenses() {
		return usedLicenses;
	}
	public void setUsedLicenses(int usedLicenses) {
		this.usedLicenses = usedLicenses;
	}
	public int getMinLicensesNeed() {
		return minLicensesNeed;
	}
	public void setMinLicensesNeed(int minLicensesNeed) {
		this.minLicensesNeed = minLicensesNeed;
	}
	
	int topologyId;
	int componentId;
	int majorVersion;
	int minorVersion;
	int totalLicenses;
	int usedLicenses;
	int minLicensesNeed;
}

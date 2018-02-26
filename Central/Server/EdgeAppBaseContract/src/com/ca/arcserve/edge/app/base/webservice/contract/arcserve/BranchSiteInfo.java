
package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

public class BranchSiteInfo {

    private Integer buildNumber;
    private String ip;
    private Integer majorVersion;
    private Integer minorVersion;
    private String serverName;
    private Integer servicePack;
    
	public Integer getBuildNumber() {
		return buildNumber;
	}
	public void setBuildNumber(Integer buildNumber) {
		this.buildNumber = buildNumber;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getMajorVersion() {
		return majorVersion;
	}
	public void setMajorVersion(Integer majorVersion) {
		this.majorVersion = majorVersion;
	}
	public Integer getMinorVersion() {
		return minorVersion;
	}
	public void setMinorVersion(Integer minorVersion) {
		this.minorVersion = minorVersion;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public Integer getServicePack() {
		return servicePack;
	}
	public void setServicePack(Integer servicePack) {
		this.servicePack = servicePack;
	}

}

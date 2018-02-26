
package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

public class GDBBranchInfo {

    public Integer getBuildNumber() {
		return buildNumber;
	}
	public void setBuildNumber(Integer buildNumber) {
		this.buildNumber = buildNumber;
	}
	public Integer getFullSyncStatus() {
		return fullSyncStatus;
	}
	public void setFullSyncStatus(Integer fullSyncStatus) {
		this.fullSyncStatus = fullSyncStatus;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Integer getUTCOffset() {
		return UTCOffset;
	}
	public void setUTCOffset(Integer uTCOffset) {
		UTCOffset = uTCOffset;
	}
	protected Integer buildNumber;
    protected Integer fullSyncStatus;
    protected Integer id;
    protected String ip;
    protected Integer majorVersion;
    protected Integer minorVersion;
    protected String serverName;
    protected Integer servicePack;
    protected String uuid;
    protected Integer UTCOffset;

    

}

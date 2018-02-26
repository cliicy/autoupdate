package com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class NodeExportEntity implements Serializable{
	
	private static final long serialVersionUID = -1155268125916549895L;
	
	//as_edge_host
	private int nodeId;
	private String nodeDescription;
	private Date lastUpdated;
	private String hostName;
	private String domainName;	
	private String ipAddress;
	private String osDescription;
	private String osType;
	private int visible;
	private int appStatus;
	private String serverPrincipalName;
	private int hostType;
	private int timezone;
	private long jobPhase;
	private int protectionTypeBitmap;
	private int rawMachineType;
	private String fqdnNames;
	
	//as_edge_connect_info
	private String userName;
	@NotPrintAttribute
	private String password;
	private String uuid;
	private String authUuid;
	private int protocol;
	private int port;
	private int type;
	private String majorVersion;
	private String minorVersion;
	private String buildNumber;
	private String updateNumber;
	private int status;
	private int managed;
	
	//as_edge_arcserve_connect_info
	private String caUser;
	@NotPrintAttribute
	private String caPassword;
	private int authMode;
	private int arcserveProtocol;
	private int arcservePort;
	private int arcserveType;
	private String arcserveVersion;
	private int arcserveManaged;
	private String arcserveUuid;
	
	//esx vm
	private int esxVmStatus;
	private String esxVmName;
	private String esxvmInstanceUuid;
	private String esxVmUuid;
	private String esxHost;
	private int esxEssential;
	private int esxSocketCount;
	private String esxVmXPath;
	private String esxVmGuestOS;
	
	//hypervVm
	private int hypervVmStatus;
	private String hypervVmName;
	private String hypervVmInstanceUuid;
	private String hypervVmUuid;
	private String hypervHost;
	private int hypervSocketCount;
	private String hypervVmGuestOS;
	
	//hypervisor
	private int adId;
	private int adStatus;
	private int esxId;
	private int hypervId;
	private String otherHypervisorHostName;
	private int otherHypervisorSocketCount;
	
	//gateway
	private int gatewayId;
	
	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeDescription() {
		return nodeDescription;
	}

	public void setNodeDescription(String nodeDescription) {
		this.nodeDescription = nodeDescription;
	}
	
	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getOsDescription() {
		return osDescription;
	}

	public void setOsDescription(String osDescription) {
		this.osDescription = osDescription;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public int getVisible() {
		return visible;
	}

	public void setVisible(int visible) {
		this.visible = visible;
	}

	public int getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(int appStatus) {
		this.appStatus = appStatus;
	}

	public String getServerPrincipalName() {
		return serverPrincipalName;
	}

	public void setServerPrincipalName(String serverPrincipalName) {
		this.serverPrincipalName = serverPrincipalName;
	}

	public int getHostType() {
		return hostType;
	}

	public void setHostType(int hostType) {
		this.hostType = hostType;
	}

	public int getTimezone() {
		return timezone;
	}

	public void setTimezone(int timezone) {
		this.timezone = timezone;
	}

	public long getJobPhase() {
		return jobPhase;
	}

	public void setJobPhase(long jobPhase) {
		this.jobPhase = jobPhase;
	}

	public int getProtectionTypeBitmap() {
		return protectionTypeBitmap;
	}

	public void setProtectionTypeBitmap(int protectionTypeBitmap) {
		this.protectionTypeBitmap = protectionTypeBitmap;
	}

	public int getRawMachineType() {
		return rawMachineType;
	}

	public void setRawMachineType(int rawMachineType) {
		this.rawMachineType = rawMachineType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@EncryptSave
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@EncryptSave
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@EncryptSave
	public String getAuthUuid() {
		return authUuid;
	}

	public void setAuthUuid(String authUuid) {
		this.authUuid = authUuid;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(String majorVersion) {
		this.majorVersion = majorVersion;
	}

	public String getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(String minorVersion) {
		this.minorVersion = minorVersion;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}

	public String getUpdateNumber() {
		return updateNumber;
	}

	public void setUpdateNumber(String updateNumber) {
		this.updateNumber = updateNumber;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getManaged() {
		return managed;
	}

	public void setManaged(int managed) {
		this.managed = managed;
	}

	public String getCaUser() {
		return caUser;
	}

	public void setCaUser(String caUser) {
		this.caUser = caUser;
	}

	@EncryptSave
	public String getCaPassword() {
		return caPassword;
	}

	public void setCaPassword(String caPassword) {
		this.caPassword = caPassword;
	}

	public int getAuthMode() {
		return authMode;
	}

	public void setAuthMode(int authMode) {
		this.authMode = authMode;
	}

	public int getArcserveProtocol() {
		return arcserveProtocol;
	}

	public void setArcserveProtocol(int arcserveProtocol) {
		this.arcserveProtocol = arcserveProtocol;
	}

	public int getArcservePort() {
		return arcservePort;
	}

	public void setArcservePort(int arcservePort) {
		this.arcservePort = arcservePort;
	}

	public int getArcserveType() {
		return arcserveType;
	}

	public void setArcserveType(int arcserveType) {
		this.arcserveType = arcserveType;
	}

	public String getArcserveVersion() {
		return arcserveVersion;
	}

	public void setArcserveVersion(String arcserveVersion) {
		this.arcserveVersion = arcserveVersion;
	}

	public int getArcserveManaged() {
		return arcserveManaged;
	}

	public void setArcserveManaged(int arcserveManaged) {
		this.arcserveManaged = arcserveManaged;
	}

	public String getArcserveUuid() {
		return arcserveUuid;
	}

	public void setArcserveUuid(String arcserveUuid) {
		this.arcserveUuid = arcserveUuid;
	}

	public int getEsxEssential() {
		return esxEssential;
	}

	public void setEsxEssential(int esxEssential) {
		this.esxEssential = esxEssential;
	}

	public int getAdId() {
		return adId;
	}

	public void setAdId(int adId) {
		this.adId = adId;
	}

	public int getEsxId() {
		return esxId;
	}

	public void setEsxId(int esxId) {
		this.esxId = esxId;
	}

	public int getHypervId() {
		return hypervId;
	}

	public void setHypervId(int hypervId) {
		this.hypervId = hypervId;
	}

	public int getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(int gatewayId) {
		this.gatewayId = gatewayId;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public int getEsxVmStatus() {
		return esxVmStatus;
	}

	public void setEsxVmStatus(int esxVmStatus) {
		this.esxVmStatus = esxVmStatus;
	}

	public String getEsxVmName() {
		return esxVmName;
	}

	public void setEsxVmName(String esxVmName) {
		this.esxVmName = esxVmName;
	}

	public String getEsxvmInstanceUuid() {
		return esxvmInstanceUuid;
	}

	public void setEsxvmInstanceUuid(String esxvmInstanceUuid) {
		this.esxvmInstanceUuid = esxvmInstanceUuid;
	}

	public String getEsxVmUuid() {
		return esxVmUuid;
	}

	public void setEsxVmUuid(String esxVmUuid) {
		this.esxVmUuid = esxVmUuid;
	}

	public String getEsxHost() {
		return esxHost;
	}

	public void setEsxHost(String esxHost) {
		this.esxHost = esxHost;
	}

	public int getEsxSocketCount() {
		return esxSocketCount;
	}

	public void setEsxSocketCount(int esxSocketCount) {
		this.esxSocketCount = esxSocketCount;
	}

	public String getEsxVmXPath() {
		return esxVmXPath;
	}

	public void setEsxVmXPath(String esxVmXPath) {
		this.esxVmXPath = esxVmXPath;
	}

	public String getEsxVmGuestOS() {
		return esxVmGuestOS;
	}

	public void setEsxVmGuestOS(String esxVmGuestOS) {
		this.esxVmGuestOS = esxVmGuestOS;
	}

	public int getHypervVmStatus() {
		return hypervVmStatus;
	}

	public void setHypervVmStatus(int hypervVmStatus) {
		this.hypervVmStatus = hypervVmStatus;
	}

	public String getHypervVmName() {
		return hypervVmName;
	}

	public void setHypervVmName(String hypervVmName) {
		this.hypervVmName = hypervVmName;
	}

	public String getHypervVmInstanceUuid() {
		return hypervVmInstanceUuid;
	}

	public void setHypervVmInstanceUuid(String hypervVmInstanceUuid) {
		this.hypervVmInstanceUuid = hypervVmInstanceUuid;
	}

	public String getHypervVmUuid() {
		return hypervVmUuid;
	}

	public void setHypervVmUuid(String hypervVmUuid) {
		this.hypervVmUuid = hypervVmUuid;
	}

	public String getHypervHost() {
		return hypervHost;
	}

	public void setHypervHost(String hypervHost) {
		this.hypervHost = hypervHost;
	}

	public int getHypervSocketCount() {
		return hypervSocketCount;
	}

	public void setHypervSocketCount(int hypervSocketCount) {
		this.hypervSocketCount = hypervSocketCount;
	}

	public String getHypervVmGuestOS() {
		return hypervVmGuestOS;
	}

	public void setHypervVmGuestOS(String hypervVmGuestOS) {
		this.hypervVmGuestOS = hypervVmGuestOS;
	}

	public int getAdStatus() {
		return adStatus;
	}

	public void setAdStatus(int adStatus) {
		this.adStatus = adStatus;
	}

	public String getOtherHypervisorHostName() {
		return otherHypervisorHostName;
	}

	public void setOtherHypervisorHostName(String otherHypervisorHostName) {
		this.otherHypervisorHostName = otherHypervisorHostName;
	}

	public int getOtherHypervisorSocketCount() {
		return otherHypervisorSocketCount;
	}

	public void setOtherHypervisorSocketCount(int otherHypervisorSocketCount) {
		this.otherHypervisorSocketCount = otherHypervisorSocketCount;
	}

	public String getFqdnNames() {
		return fqdnNames;
	}

	public void setFqdnNames(String fqdnNames) {
		this.fqdnNames = fqdnNames;
	}
}

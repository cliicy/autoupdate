package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;

public class ExportNode implements Serializable {
	private static final long serialVersionUID = -1985127219143415910L;
	private String nodeName;
	private String hostName;
	private String username;
	private @NotPrintAttribute String password;
	private String nodeDescription;
	private String domainName; // temporary not have this field value from
	private String ipAddress;
	private String osDesc;
	private String osType;
	private String isVisible; // temporary not have this field value from export
	private String appStatus;
	private String serverPrincipalName;
	private String hostType;
	private String timezone;
	private String jobPhase; // temporary not have this field value from export
	private String protectionType;
	private String machineType;
	private String port;
	private String protocol;
	
	//VM
	private String vmName;
	private String vmStatus;
	private String vmUUID;
	private String vmInstanceUUID;
	private String vmHost;
	
	//VMware private
	private String vmXPath;
	
	//Hyper-v private
	private String vmGuestOS;
	
	//Hypervisor
	private String hypervisorHostName;
	private String hypervisorProtocol;
	private String hypervisorUsername;
	private String hypervisorPassword;
	private String hypervisorVisible;
	private String hypervisorEssential;
	private String hypervisorSocketCount;
	
	//Hyper-v private
	private String hypervisorPort;
	private String hypervisorServerType;
	
	

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNodeDescription() {
		return nodeDescription;
	}

	public void setNodeDescription(String nodeDescription) {
		this.nodeDescription = nodeDescription;
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

	public String getOsDesc() {
		return osDesc;
	}

	public void setOsDesc(String osDesc) {
		this.osDesc = osDesc;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(String isVisible) {
		this.isVisible = isVisible;
	}

	public String getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}

	public String getServerPrincipalName() {
		return serverPrincipalName;
	}

	public void setServerPrincipalName(String serverPrincipalName) {
		this.serverPrincipalName = serverPrincipalName;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getJobPhase() {
		return jobPhase;
	}

	public void setJobPhase(String jobPhase) {
		this.jobPhase = jobPhase;
	}

	public String getProtectionType() {
		return protectionType;
	}

	public void setProtectionType(String protectionType) {
		this.protectionType = protectionType;
	}

	public String getMachineType() {
		return machineType;
	}

	public void setMachineType(String machineType) {
		this.machineType = machineType;
	}
	
	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public String getVmStatus() {
		return vmStatus;
	}

	public void setVmStatus(String vmStatus) {
		this.vmStatus = vmStatus;
	}

	public String getVmUUID() {
		return vmUUID;
	}

	public void setVmUUID(String vmUUID) {
		this.vmUUID = vmUUID;
	}



	public String getVmInstanceUUID() {
		return vmInstanceUUID;
	}

	public void setVmInstanceUUID(String vmInstanceUUID) {
		this.vmInstanceUUID = vmInstanceUUID;
	}

	public String getVmHost() {
		return vmHost;
	}

	public void setVmHost(String vmHost) {
		this.vmHost = vmHost;
	}

	public String getVmXPath() {
		return vmXPath;
	}

	public void setVmXPath(String vmXPath) {
		this.vmXPath = vmXPath;
	}

	public String getVmGuestOS() {
		return vmGuestOS;
	}

	public void setVmGuestOS(String vmGuestOS) {
		this.vmGuestOS = vmGuestOS;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostType() {
		return hostType;
	}

	public void setHostType(String hostType) {
		this.hostType = hostType;
	}

	public String getHypervisorHostName() {
		return hypervisorHostName;
	}

	public void setHypervisorHostName(String hypervisorHostName) {
		this.hypervisorHostName = hypervisorHostName;
	}

	public String getHypervisorProtocol() {
		return hypervisorProtocol;
	}

	public void setHypervisorProtocol(String hypervisorProtocol) {
		this.hypervisorProtocol = hypervisorProtocol;
	}

	public String getHypervisorUsername() {
		return hypervisorUsername;
	}

	public void setHypervisorUsername(String hypervisorUsername) {
		this.hypervisorUsername = hypervisorUsername;
	}

	public String getHypervisorPassword() {
		return hypervisorPassword;
	}

	public void setHypervisorPassword(String hypervisorPassword) {
		this.hypervisorPassword = hypervisorPassword;
	}

	public String getHypervisorVisible() {
		return hypervisorVisible;
	}

	public void setHypervisorVisible(String hypervisorVisible) {
		this.hypervisorVisible = hypervisorVisible;
	}

	public String getHypervisorEssential() {
		return hypervisorEssential;
	}

	public void setHypervisorEssential(String hypervisorEssential) {
		this.hypervisorEssential = hypervisorEssential;
	}

	public String getHypervisorSocketCount() {
		return hypervisorSocketCount;
	}

	public void setHypervisorSocketCount(String hypervisorSocketCount) {
		this.hypervisorSocketCount = hypervisorSocketCount;
	}

	public String getHypervisorPort() {
		return hypervisorPort;
	}

	public void setHypervisorPort(String hypervisorPort) {
		this.hypervisorPort = hypervisorPort;
	}

	public String getHypervisorServerType() {
		return hypervisorServerType;
	}

	public void setHypervisorServerType(String hypervisorServerType) {
		this.hypervisorServerType = hypervisorServerType;
	}
}

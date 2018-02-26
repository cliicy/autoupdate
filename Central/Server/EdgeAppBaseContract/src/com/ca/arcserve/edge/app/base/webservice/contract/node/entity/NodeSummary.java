package com.ca.arcserve.edge.app.base.webservice.contract.node.entity;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicenseMachineType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeProtectionStatus;

public class NodeSummary implements Serializable {

	private static final long serialVersionUID = -1155268125916549895L;
	
	private int id;
	private String description;
	private Date lastUpdated;
	private String hostname;
	private String domainName;	
	private String ipAddress;
	private String osDescription;
	private String osType;
	private int visible;
	private int appStatus;
	private String serverPrincipalName;
	private int hostType;
	private int timezone;
	private int protectionTypeBitmap;
	private LicenseMachineType machineType;
	private int rawMachineType;
	
	private String currentConsoleMachineNameForCollectDiag;
	private String currentConsoleIPForCollectDiag;
	
	private NodeProtectionStatus nodeStatus;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
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
	public int getProtectionTypeBitmap() {
		return protectionTypeBitmap;
	}
	public void setProtectionTypeBitmap(int protectionTypeBitmap) {
		this.protectionTypeBitmap = protectionTypeBitmap;
	}
	public LicenseMachineType getMachineType() {
		return machineType;
	}
	public void setMachineType(LicenseMachineType machineType) {
		this.machineType = machineType;
	}
	public int getRawMachineType() {
		return rawMachineType;
	}
	public void setRawMachineType(int rawMachineType) {
		this.rawMachineType = rawMachineType;
	}
	public NodeProtectionStatus getNodeStatus() {
		return nodeStatus;
	}
	public void setNodeStatus(NodeProtectionStatus nodeStatus) {
		this.nodeStatus = nodeStatus;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeSummary other = (NodeSummary) obj;
		if(id!=other.getId())
			return false;
		if(!StringUtil.isEqual(description, other.getDescription()))
			return false;
		if(!Utils.simpleObjectEquals(lastUpdated, other.getLastUpdated()))
			return false;
		if(!StringUtil.isEqual(hostname, other.getHostname()))
			return false;
		if(!StringUtil.isEqual(domainName, other.getDomainName()))
			return false;
		if(!StringUtil.isEqual(ipAddress, other.getIpAddress()))
			return false;
		if(!StringUtil.isEqual(osDescription, other.getOsDescription()))
			return false;
		if(!StringUtil.isEqual(osType, other.getOsType()))
			return false;
		if(visible != other.getVisible())
			return false;
		if(appStatus != other.getAppStatus())
			return false;
		if(!StringUtil.isEqual(serverPrincipalName, other.getServerPrincipalName()))
			return false;
		if(hostType != other.getHostType())
			return false;
		if(timezone != other.getTimezone())
			return false;
		if(protectionTypeBitmap != other.getProtectionTypeBitmap())
			return false;
		if(!Utils.simpleObjectEquals(machineType, other.getMachineType()))
			return false;
		if(rawMachineType != other.getRawMachineType())
			return false;
		return true;
	}
	
	public void update(NodeSummary other) {
		if(other == null)
			return;
		if(id!=other.getId())
			id = other.getId();
		if(!StringUtil.isEqual(description, other.getDescription()))
			description = other.getDescription();
		if(!Utils.simpleObjectEquals(lastUpdated, other.getLastUpdated()))
			lastUpdated=other.getLastUpdated();
		if(!StringUtil.isEqual(hostname, other.getHostname()))
			hostname=other.getHostname();
		if(!StringUtil.isEqual(domainName, other.getDomainName()))
			domainName = other.getDomainName();
		if(!StringUtil.isEqual(ipAddress, other.getIpAddress()))
			ipAddress = other.getIpAddress();
		if(!StringUtil.isEqual(osDescription, other.getOsDescription()))
			osDescription = other.getOsDescription();
		if(!StringUtil.isEqual(osType, other.getOsType()))
			osType = other.getOsType();
		if(visible != other.getVisible())
			visible=other.getVisible();
		if(appStatus != other.getAppStatus())
			appStatus = other.getAppStatus();
		if(!StringUtil.isEqual(serverPrincipalName, other.getServerPrincipalName()))
			serverPrincipalName = other.getServerPrincipalName();
		if(hostType != other.getHostType())
			hostType = other.getHostType();
		if(timezone != other.getTimezone())
			timezone = other.getTimezone();
		if(protectionTypeBitmap != other.getProtectionTypeBitmap())
			protectionTypeBitmap = other.getProtectionTypeBitmap();
		if(!Utils.simpleObjectEquals(machineType, other.getMachineType()))
			machineType = other.getMachineType();
		if(rawMachineType != other.getRawMachineType())
			rawMachineType = other.getRawMachineType();
	}
	
	public String getCurrentConsoleIPForCollectDiag() {
		return currentConsoleIPForCollectDiag;
	}
	public void setCurrentConsoleIPForCollectDiag(
			String currentConsoleIPForCollectDiag) {
		this.currentConsoleIPForCollectDiag = currentConsoleIPForCollectDiag;
	}
	public String getCurrentConsoleMachineNameForCollectDiag() {
		return currentConsoleMachineNameForCollectDiag;
	}
	public void setCurrentConsoleMachineNameForCollectDiag(
			String currentConsoleMachineNameForCollectDiag) {
		this.currentConsoleMachineNameForCollectDiag = currentConsoleMachineNameForCollectDiag;
	}
}

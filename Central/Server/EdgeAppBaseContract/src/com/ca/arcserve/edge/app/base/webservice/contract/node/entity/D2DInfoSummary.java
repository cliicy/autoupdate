package com.ca.arcserve.edge.app.base.webservice.contract.node.entity;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;

public class D2DInfoSummary implements Serializable{
	private static final long serialVersionUID = 1L;
	private int hostId;
	private String majorVersion;
	private String minorVersion;
	private String buildNumber;
	private String updateNumber;
	private int status;
	private NodeManagedStatus managedStatus;
	private NodeManagedStatus rpsManagedStatus;
	private int port;
	private int protocol;
	private String uuid;
	private String authUuid;
	private Date d2dLastUpdateTime; //the last update time for D2D sync
	private String username;
	private @NotPrintAttribute String password;
	private boolean d2dLastUpdateWarning = true; // is it warning for D2D last sync time
	private int d2dSyncFrequency; // D2D synchronize frequency
	
	public int getHostId() {
		return hostId;
	}
	public void setHostId(int hostId) {
		this.hostId = hostId;
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
	public NodeManagedStatus getManagedStatus() {
		return managedStatus;
	}
	public void setManagedStatus(NodeManagedStatus managedStatus) {
		this.managedStatus = managedStatus;
	}
	public NodeManagedStatus getRpsManagedStatus() {
		return rpsManagedStatus;
	}
	public void setRpsManagedStatus(NodeManagedStatus rpsManagedStatus) {
		this.rpsManagedStatus = rpsManagedStatus;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getProtocol() {
		return protocol;
	}
	public void setProtocol(int protocol) {
		this.protocol = protocol;
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
	public Date getD2dLastUpdateTime() {
		return d2dLastUpdateTime;
	}
	public void setD2dLastUpdateTime(Date d2dLastUpdateTime) {
		this.d2dLastUpdateTime = d2dLastUpdateTime;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@EncryptSave  
	public String getPassword() {
		return password;
	}
	public boolean isD2dLastUpdateWarning() {
		return d2dLastUpdateWarning;
	}
	public void setD2dLastUpdateWarning(boolean d2dLastUpdateWarning) {
		this.d2dLastUpdateWarning = d2dLastUpdateWarning;
	}
	public int getD2dSyncFrequency() {
		return d2dSyncFrequency;
	}
	public void setD2dSyncFrequency(int d2dSyncFrequency) {
		this.d2dSyncFrequency = d2dSyncFrequency;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		D2DInfoSummary other = (D2DInfoSummary) obj;
		
		if(hostId != other.getHostId())
			return false;
		if(!StringUtil.isEqual(majorVersion, other.getMajorVersion()))
			return false;
		if(!StringUtil.isEqual(minorVersion, other.getMinorVersion()))
			return false;
		if(!StringUtil.isEqual(buildNumber, other.getBuildNumber()))
			return false;
		if(!StringUtil.isEqual(updateNumber, other.getUpdateNumber()))
			return false;
		if(status != other.getStatus())
			return false;
		if(!Utils.simpleObjectEquals(managedStatus, other.getManagedStatus()))
			return false;
		if(!Utils.simpleObjectEquals(rpsManagedStatus, other.getRpsManagedStatus()))
			return false;
		if(port != other.getPort())
			return false;
		if(protocol != other.getProtocol())
			return false;
		if(!Utils.simpleObjectEquals(uuid, other.getUuid()))
			return false;
		if(!Utils.simpleObjectEquals(authUuid, other.getAuthUuid()))
			return false;
		if(!Utils.simpleObjectEquals(d2dLastUpdateTime, other.getD2dLastUpdateTime()))
			return false;
		if(!Utils.simpleObjectEquals(username, other.getUsername()))
			return false;
		if(!Utils.simpleObjectEquals(password, other.getPassword()))
			return false;
		if(d2dLastUpdateWarning != other.isD2dLastUpdateWarning())
			return false;
		if(d2dSyncFrequency != other.getD2dSyncFrequency())
			return false;
		return true;
	}
	
	public void update(D2DInfoSummary other) {
		if (other == null)
			return;
		if(hostId != other.getHostId())
			hostId = other.getHostId();
		if(!StringUtil.isEqual(majorVersion, other.getMajorVersion()))
			majorVersion = other.getMajorVersion();
		if(!StringUtil.isEqual(minorVersion, other.getMinorVersion()))
			minorVersion = other.getMinorVersion();
		if(!StringUtil.isEqual(buildNumber, other.getBuildNumber()))
			buildNumber = other.getBuildNumber();
		if(!StringUtil.isEqual(updateNumber, other.getUpdateNumber()))
			updateNumber = other.getUpdateNumber();
		if(status != other.getStatus())
			status = other.getStatus();
		if(!Utils.simpleObjectEquals(managedStatus, other.getManagedStatus()))
			managedStatus = other.getManagedStatus();
		if(!Utils.simpleObjectEquals(rpsManagedStatus, other.getRpsManagedStatus()))
			rpsManagedStatus = other.getRpsManagedStatus();
		if(port != other.getPort())
			port = other.getPort();
		if(protocol != other.getProtocol())
			protocol = other.getProtocol();
		if(!Utils.simpleObjectEquals(uuid, other.getUuid()))
			uuid = other.getUuid();
		if(!Utils.simpleObjectEquals(authUuid, other.getAuthUuid()))
			authUuid = other.getAuthUuid();
		if(!Utils.simpleObjectEquals(d2dLastUpdateTime, other.getD2dLastUpdateTime()))
			d2dLastUpdateTime = other.getD2dLastUpdateTime();
		if(!Utils.simpleObjectEquals(username, other.getUsername()))
			username = other.getUsername();
		if(!Utils.simpleObjectEquals(password, other.getPassword()))
			password = other.getPassword();
		if(d2dLastUpdateWarning != other.isD2dLastUpdateWarning())
			d2dLastUpdateWarning = other.isD2dLastUpdateWarning();
		if(d2dSyncFrequency != other.getD2dSyncFrequency())
			d2dSyncFrequency = other.getD2dSyncFrequency();
	}
}

package com.ca.arcserve.edge.app.base.webservice.contract.node.entity;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;

public class ArcserveInfoSummary implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int hostId;
	private int arcserveProtocol;
	private int arcservePort;
	private NodeManagedStatus arcserveManagedStatus;
	private ABFuncServerType arcserveServerType;
	private String arcserveBackupVersion;
	private int gdbId;
	private Date asbuLastUpdateTime; // the last update time for ARCserve sync
	private int arcSyncStatus;
	private int arcSyncChangeStatus;
	private boolean asbuLastUpdateWarning = true; // is it warning for ARCserve
	private int asbuSyncFrequency; // ARCserve Backup synchronize frequency
	
	public int getHostId() {
		return hostId;
	}
	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	public ABFuncServerType getArcserveServerType() {
		return arcserveServerType;
	}
	public void setArcserveServerType(ABFuncServerType arcserveServerType) {
		this.arcserveServerType = arcserveServerType;
	}
	public String getArcserveBackupVersion() {
		return arcserveBackupVersion;
	}
	public void setArcserveBackupVersion(String arcserveBackupVersion) {
		this.arcserveBackupVersion = arcserveBackupVersion;
	}
	public int getGdbId() {
		return gdbId;
	}
	public void setGdbId(int gdbId) {
		this.gdbId = gdbId;
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
	public NodeManagedStatus getArcserveManagedStatus() {
		return arcserveManagedStatus;
	}
	public void setArcserveManagedStatus(NodeManagedStatus arcserveManagedStatus) {
		this.arcserveManagedStatus = arcserveManagedStatus;
	}
	public Date getAsbuLastUpdateTime() {
		return asbuLastUpdateTime;
	}
	public void setAsbuLastUpdateTime(Date asbuLastUpdateTime) {
		this.asbuLastUpdateTime = asbuLastUpdateTime;
	}
	public int getArcSyncStatus() {
		return arcSyncStatus;
	}
	public void setArcSyncStatus(int arcSyncStatus) {
		this.arcSyncStatus = arcSyncStatus;
	}
	public int getArcSyncChangeStatus() {
		return arcSyncChangeStatus;
	}
	public void setArcSyncChangeStatus(int arcSyncChangeStatus) {
		this.arcSyncChangeStatus = arcSyncChangeStatus;
	}
	public boolean isAsbuLastUpdateWarning() {
		return asbuLastUpdateWarning;
	}
	public void setAsbuLastUpdateWarning(boolean asbuLastUpdateWarning) {
		this.asbuLastUpdateWarning = asbuLastUpdateWarning;
	}
	public int getAsbuSyncFrequency() {
		return asbuSyncFrequency;
	}
	public void setAsbuSyncFrequency(int asbuSyncFrequency) {
		this.asbuSyncFrequency = asbuSyncFrequency;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArcserveInfoSummary other = (ArcserveInfoSummary) obj;
		
		if(hostId != other.getHostId())
			return false;
		if(!Utils.simpleObjectEquals(arcserveServerType, other.getArcserveServerType()))
			return false;
		if(!StringUtil.isEqual(arcserveBackupVersion, other.getArcserveBackupVersion()))
			return false;
		if(gdbId != other.getGdbId())
			return false;
		if(!Utils.simpleObjectEquals(arcserveManagedStatus, other.getArcserveManagedStatus()))
			return false;
		if(arcservePort != other.getArcservePort())
			return false;
		if(arcserveProtocol != other.getArcserveProtocol())
			return false;
		if(!Utils.simpleObjectEquals(asbuLastUpdateTime, other.getAsbuLastUpdateTime()))
			return false;
		if(arcSyncStatus != other.getArcSyncStatus())
			return false;
		if(arcSyncChangeStatus != other.getArcSyncChangeStatus())
			return false;
		if(asbuLastUpdateWarning != other.isAsbuLastUpdateWarning())
			return false;
		if(asbuSyncFrequency != other.getAsbuSyncFrequency())
			return false;
		return true;
	}
	
	public void update(ArcserveInfoSummary other) {
		if (other == null)
			return;
		if(hostId != other.getHostId())
			hostId = other.getHostId();
		if(!Utils.simpleObjectEquals(arcserveServerType, other.getArcserveServerType()))
			arcserveServerType = other.getArcserveServerType();
		if(!StringUtil.isEqual(arcserveBackupVersion, other.getArcserveBackupVersion()))
			arcserveBackupVersion = other.getArcserveBackupVersion();
		if(gdbId != other.getGdbId())
			gdbId = other.getGdbId();
		if(!Utils.simpleObjectEquals(arcserveManagedStatus, other.getArcserveManagedStatus()))
			arcserveManagedStatus = other.getArcserveManagedStatus();
		if(arcservePort != other.getArcservePort())
			arcservePort = other.getArcservePort();
		if(arcserveProtocol != other.getArcserveProtocol())
			arcserveProtocol = other.getArcserveProtocol();
		if(!Utils.simpleObjectEquals(asbuLastUpdateTime, other.getAsbuLastUpdateTime()))
			asbuLastUpdateTime = other.getAsbuLastUpdateTime();
		if(arcSyncStatus != other.getArcSyncStatus())
			arcSyncStatus = other.getArcSyncStatus();
		if(arcSyncChangeStatus != other.getArcSyncChangeStatus())
			arcSyncChangeStatus = other.getArcSyncChangeStatus();
		if(asbuLastUpdateWarning != other.isAsbuLastUpdateWarning())
			asbuLastUpdateWarning = other.isAsbuLastUpdateWarning();
		if(asbuSyncFrequency != other.getAsbuSyncFrequency())
			asbuSyncFrequency = other.getAsbuSyncFrequency();
	}
}

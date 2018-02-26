package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

public class SyncFileQueueItem {
	private String folder        = null;
	private String fileName      = null;
	private boolean lastFileFlag = false;
	private String branchName    = null;
	private int rhostid = 0;
	private int syncType = ConfigurationOperator._ArcserveType;
	
	public int getSyncType() {
		return syncType;
	}
	public void setSyncType(int syncType) {
		this.syncType = syncType;
	}
	public int getRhostid() {
		return rhostid;
	}
	public void setRhostid(int rhostid) {
		this.rhostid = rhostid;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public boolean isLastFileFlag() {
		return lastFileFlag;
	}
	public void setLastFileFlag(boolean lastFileFlag) {
		this.lastFileFlag = lastFileFlag;
	}

}

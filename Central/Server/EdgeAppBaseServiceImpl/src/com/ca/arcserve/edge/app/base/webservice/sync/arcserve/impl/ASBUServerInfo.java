package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.SyncARCServerType;

public class ASBUServerInfo {
	private int hostid;
	private SyncARCServerType type;
	private boolean valid; 
	
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public int getHostid() {
		return hostid;
	}
	public void setHostid(int hostid) {
		this.hostid = hostid;
	}
	public SyncARCServerType getType() {
		return type;
	}
	public void setType(SyncARCServerType type) {
		this.type = type;
	}
}

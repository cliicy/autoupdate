package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.schedulers.IEdgeTaskItem;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.BranchSiteInfo;

public abstract class BranchSyncTask implements IEdgeTaskItem {
	
	private EdgeHost gdbServerInfo;
	private int branchHostID;
	private BranchSiteInfo branchSiteInfo;
	private String gdbUrl;
	private int	_retryTimes;
	private int	_retryInterval;
	private ASBUJobInfo jobInfo = null;
	
	public ASBUJobInfo getJobInfo() {
		return jobInfo;
	}
	public void setJobInfo(ASBUJobInfo jobInfo) {
		this.jobInfo = jobInfo;
	}
	public EdgeHost getGdbServerInfo() {
		return gdbServerInfo;
	}
	public void setGdbServerInfo(EdgeHost gdbServer) {
		this.gdbServerInfo = gdbServer;
	}
	public int getBranchHostID() {
		return branchHostID;
	}
	public void setBranchHostID(int branchHostID) {
		this.branchHostID = branchHostID;
	}
	public BranchSiteInfo getBranchSiteInfo() {
		return branchSiteInfo;
	}
	public void setBranchSiteInfo(BranchSiteInfo branchSiteInfo) {
		this.branchSiteInfo = branchSiteInfo;
	}
	
	public String getGdbUrl() {
		return gdbUrl;
	}
	public void setGdbUrl(String gdbUrl) {
		this.gdbUrl = gdbUrl;
	}
	public String getGdbServerName() {
		return gdbUrl.substring(gdbUrl.lastIndexOf("://")+3, gdbUrl.lastIndexOf(":"));
	}

	public int get_retryTimes() {
		return _retryTimes;
	}
	public void set_retryTimes(int retryTimes) {
		_retryTimes = retryTimes;
	}
	public int get_retryInterval() {
		return _retryInterval;
	}
	public void set_retryInterval(int retryInterval) {
		_retryInterval = retryInterval;
	}
	

}

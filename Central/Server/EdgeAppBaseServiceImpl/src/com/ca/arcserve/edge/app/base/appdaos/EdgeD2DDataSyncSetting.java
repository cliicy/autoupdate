package com.ca.arcserve.edge.app.base.appdaos;

public class EdgeD2DDataSyncSetting {
	private int RetryTimes;
	private int RetryInterval;
	private int ScheduleID;
	private int branchid;

	public int getRetryTimes() {
		return RetryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		RetryTimes = retryTimes;
	}

	public int getRetryInterval() {
		return RetryInterval;
	}

	public void setRetryInterval(int retryInterval) {
		RetryInterval = retryInterval;
	}

	public int getScheduleID() {
		return ScheduleID;
	}

	public void setScheduleID(int scheduleID) {
		ScheduleID = scheduleID;
	}

	public int getBranchid() {
		return branchid;
	}

	public void setBranchid(int branchid) {
		this.branchid = branchid;
	}

}

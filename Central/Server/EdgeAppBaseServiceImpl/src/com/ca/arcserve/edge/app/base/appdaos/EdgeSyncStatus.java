package com.ca.arcserve.edge.app.base.appdaos;

public class EdgeSyncStatus {
	private int		branchid;
	private long	last_cache_id;
	private int		status;
	private int     change_status;
	
	public int getChange_status() {
		return change_status;
	}

	public void setChange_status(int changeStatus) {
		change_status = changeStatus;
	}

	public void setBranchid(int branchid)
	{
		this.branchid = branchid;
	}
	
	public void setLast_cache_id(long last_cache_id)
	{
		this.last_cache_id = last_cache_id;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
	}
	
	public int getBranchid()
	{
		return branchid;
	}
	
	public long getLast_cache_id()
	{
		return last_cache_id;
	}
	
	public int getStatus()
	{
		return status;
	}
}

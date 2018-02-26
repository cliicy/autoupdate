package com.ca.arcserve.edge.app.base.webservice.contract.node.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;

public class JobSummary implements Serializable{
	private static final long serialVersionUID = 1L;
	private int hostId;
	private String jobMonitorKey;
	private boolean jobRunning;
	private boolean waittingJobToRun;
	private int lastBackupJobStatus; //com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobStatus
	private Date lastBackupJobTime;
	private List<JobHistory> latestJobHistories;
	
	public String getJobMonitorKey() {
		return jobMonitorKey;
	}
	public void setJobMonitorKey(String jobMonitorKey) {
		this.jobMonitorKey = jobMonitorKey;
	}
	public int getHostId() {
		return hostId;
	}
	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	public boolean isJobRunning() {
		return jobRunning;
	}
	public void setJobRunning(boolean jobRunning) {
		this.jobRunning = jobRunning;
	}
	public boolean isWaittingJobToRun() {
		return waittingJobToRun;
	}
	public void setWaittingJobToRun(boolean waittingJobToRun) {
		this.waittingJobToRun = waittingJobToRun;
	}
	public int getLastBackupJobStatus() {
		return lastBackupJobStatus;
	}
	public void setLastBackupJobStatus(int lastBackupJobStatus) {
		this.lastBackupJobStatus = lastBackupJobStatus;
	}
	public Date getLastBackupJobTime() {
		return lastBackupJobTime;
	}
	public void setLastBackupJobTime(Date lastBackupJobTime) {
		this.lastBackupJobTime = lastBackupJobTime;
	}
	public List<JobHistory> getLatestJobHistories() {
		return latestJobHistories;
	}
	public void setLatestJobHistories(List<JobHistory> latestJobHistories) {
		this.latestJobHistories = latestJobHistories;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobSummary other = (JobSummary) obj;
		
		if(hostId != other.getHostId())
			return false;
		if(!StringUtil.isEqual(jobMonitorKey, other.getJobMonitorKey()))
			return false;
		if(jobRunning != other.isJobRunning())
			return false;
		if(waittingJobToRun != other.isWaittingJobToRun())
			return false;
		if(!Utils.simpleObjectEquals(lastBackupJobStatus, other.getLastBackupJobStatus()))
			return false;
		if(!Utils.simpleObjectEquals(lastBackupJobTime, other.getLastBackupJobTime()))
			return false;
		if(latestJobHistories==null && other.getLatestJobHistories()!=null)
			return false;
		if(latestJobHistories!=null && other.getLatestJobHistories() == null)
			return false;
		if(latestJobHistories.size() != other.getLatestJobHistories().size())
			return false;
		for (JobHistory thisJobHistory : latestJobHistories) {
			if(!other.getLatestJobHistories().contains(thisJobHistory))
				return false;
		}
		return true;
	}
	
	public void update(JobSummary other) {
		if(other == null)
			return;
		if(hostId != other.getHostId())
			hostId = other.getHostId();
		if(!StringUtil.isEqual(jobMonitorKey, other.getJobMonitorKey()))
			jobMonitorKey = other.getJobMonitorKey();
		if(jobRunning != other.isJobRunning())
			jobRunning = other.isJobRunning();
		if(waittingJobToRun != other.isWaittingJobToRun())
			waittingJobToRun=other.isWaittingJobToRun();
		if(!Utils.simpleObjectEquals(lastBackupJobStatus, other.getLastBackupJobStatus()))
			lastBackupJobStatus = other.getLastBackupJobStatus();
		if(!Utils.simpleObjectEquals(lastBackupJobTime, other.getLastBackupJobTime()))
			lastBackupJobTime = other.getLastBackupJobTime();
		if(latestJobHistories==null
				||(latestJobHistories!=null && other.getLatestJobHistories() == null)
				|| latestJobHistories.size() != other.getLatestJobHistories().size()
				){
			latestJobHistories = other.getLatestJobHistories();
		}else {
			boolean isSame = true;
			for (JobHistory thisJobHistory : latestJobHistories) {
				if(!other.getLatestJobHistories().contains(thisJobHistory)){
					isSame = false;
					break;
				}
			}
			if(!isSame)
				latestJobHistories = other.getLatestJobHistories();
		}
	}
}

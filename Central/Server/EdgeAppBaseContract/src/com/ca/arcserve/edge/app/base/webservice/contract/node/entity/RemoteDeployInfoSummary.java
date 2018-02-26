package com.ca.arcserve.edge.app.base.webservice.contract.node.entity;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;

public class RemoteDeployInfoSummary implements Serializable{
	private static final long serialVersionUID = 1L;
	private int hostId;
	private int deployStatus = Integer.MIN_VALUE; //DeployStatus
	private int deployTaskStatus=Integer.MIN_VALUE;//Task status
	private int installationType;
	private Date deployTime;
	private boolean scheduleDeployCanceled = false;
	
	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}

	public int getDeployStatus() {
		return deployStatus;
	}

	public void setDeployStatus(int deployStatus) {
		this.deployStatus = deployStatus;
	}
	
	public int getDeployTaskStatus() {
		return deployTaskStatus;
	}

	public void setDeployTaskStatus(int deployTaskStatus) {
		this.deployTaskStatus = deployTaskStatus;
	}

	public int getInstallationType() {
		return installationType;
	}

	public void setInstallationType(int installationType) {
		this.installationType = installationType;
	}

	public Date getDeployTime() {
		return deployTime;
	}

	public void setDeployTime(Date deployTime) {
		this.deployTime = deployTime;
	}

	public boolean isScheduleDeployCanceled() {
		return scheduleDeployCanceled;
	}

	public void setScheduleDeployCanceled(boolean scheduleDeployCanceled) {
		this.scheduleDeployCanceled = scheduleDeployCanceled;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RemoteDeployInfoSummary other = (RemoteDeployInfoSummary) obj;
		
		if(hostId != other.getHostId())
			return false;
		if(deployStatus != other.getDeployStatus())
			return false;
		if(deployTaskStatus != other.getDeployTaskStatus())
			return false;
		if(installationType != other.getInstallationType())
			return false;
		if(!Utils.simpleObjectEquals(deployTime, other.getDeployTime()))
			return false;
		if(scheduleDeployCanceled != other.isScheduleDeployCanceled())
			return false;
		return true;
	}
	
	public void update(RemoteDeployInfoSummary other) {
		if (other == null)
			return;
		if(hostId != other.getHostId())
			hostId = other.getHostId();
		if(deployStatus != other.getDeployStatus())
			deployStatus = other.getDeployStatus();
		if(deployTaskStatus != other.getDeployTaskStatus())
			deployTaskStatus = other.getDeployTaskStatus();
		if(installationType != other.getInstallationType())
			installationType = other.getInstallationType();
		if(!Utils.simpleObjectEquals(deployTime, other.getDeployTime()))
			deployTime = other.getDeployTime();
		if(scheduleDeployCanceled != other.isScheduleDeployCanceled())
			scheduleDeployCanceled = other.isScheduleDeployCanceled();
	}
}

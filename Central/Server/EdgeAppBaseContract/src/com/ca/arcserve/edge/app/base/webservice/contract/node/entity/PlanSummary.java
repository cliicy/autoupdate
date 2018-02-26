package com.ca.arcserve.edge.app.base.webservice.contract.node.entity;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;

public class PlanSummary implements Serializable{
	private static final long serialVersionUID = 1L;
	private int id;
	private int hostId;
	private String name;
	private int enableStatus; //PlanEnableStatus
	private int deployStatus;
	private int deployReason;
	private int contentFlag;
	private Date lastSuccDeploy;
	private int policytype;
	private String deployError;
	private String deployWarning;
	private int deployWarningAcknowledged;
	private boolean isHasCrossSiteVsb;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getHostId() {
		return hostId;
	}
	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getEnableStatus() {
		return enableStatus;
	}
	public void setEnableStatus(int enableStatus) {
		this.enableStatus = enableStatus;
	}
	public int getDeployStatus() {
		return deployStatus;
	}
	public void setDeployStatus(int deployStatus) {
		this.deployStatus = deployStatus;
	}
	public int getDeployReason() {
		return deployReason;
	}
	public void setDeployReason(int deployReason) {
		this.deployReason = deployReason;
	}
	public int getContentFlag() {
		return contentFlag;
	}
	public void setContentFlag(int contentFlag) {
		this.contentFlag = contentFlag;
	}
	public Date getLastSuccDeploy() {
		return lastSuccDeploy;
	}
	public void setLastSuccDeploy(Date lastSuccDeploy) {
		this.lastSuccDeploy = lastSuccDeploy;
	}
	public int getPolicytype() {
		return policytype;
	}
	public void setPolicytype(int policytype) {
		this.policytype = policytype;
	}
	public String getDeployError() {
		return deployError;
	}
	public void setDeployError(String deployError) {
		this.deployError = deployError;
	}
	public String getDeployWarning() {
		return deployWarning;
	}
	public void setDeployWarning(String deployWarning) {
		this.deployWarning = deployWarning;
	}
	public int getDeployWarningAcknowledged() {
		return deployWarningAcknowledged;
	}
	public void setDeployWarningAcknowledged(int deployWarningAcknowledged) {
		this.deployWarningAcknowledged = deployWarningAcknowledged;
	}	
	public boolean isHasCrossSiteVsb() {
		return isHasCrossSiteVsb;
	}
	public void setHasCrossSiteVsb(boolean isHasCrossSiteVsb) {
		this.isHasCrossSiteVsb = isHasCrossSiteVsb;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlanSummary other = (PlanSummary) obj;
		if(id != other.getId())
			return false;
		if(hostId != other.getHostId())
			return false;
		if(!StringUtil.isEqual(name, other.getName()))
			return false;
		if(enableStatus != other.getEnableStatus())
			return false;
		if(deployStatus != other.getDeployStatus())
			return false;
		if(deployReason != other.getDeployReason())
			return false;
		if(contentFlag != other.getContentFlag())
			return false;
		if(!Utils.simpleObjectEquals(lastSuccDeploy, other.getLastSuccDeploy()))
			return false;
		if(policytype != other.getPolicytype())
			return false;
		if(!StringUtil.isEqual(deployError, other.getDeployError()))
			return false;
		if(!StringUtil.isEqual(deployWarning, other.getDeployWarning()))
			return false;
		if(deployWarningAcknowledged != other.getDeployWarningAcknowledged())
			return false;
		if(isHasCrossSiteVsb != other.isHasCrossSiteVsb())
			return false;
		return true;
	}
	
	public void update(PlanSummary other) {
		if(other == null)
			return;
		if(id != other.getId())
			id=other.getId();
		if(hostId != other.getHostId())
			hostId = other.getHostId();
		if(!StringUtil.isEqual(name, other.getName()))
			name = other.getName();
		if(enableStatus != other.getEnableStatus())
			enableStatus = other.getEnableStatus();
		if(deployStatus != other.getDeployStatus())
			deployStatus = other.getDeployStatus();
		if(deployReason != other.getDeployReason())
			deployReason = other.getDeployReason();
		if(contentFlag != other.getContentFlag())
			contentFlag = other.getContentFlag();
		if(!Utils.simpleObjectEquals(lastSuccDeploy, other.getLastSuccDeploy()))
			lastSuccDeploy = other.getLastSuccDeploy();
		if(policytype != other.getPolicytype())
			policytype = other.getPolicytype();
		if(!StringUtil.isEqual(deployError, other.getDeployError()))
			deployError = other.getDeployError();
		if(!StringUtil.isEqual(deployWarning, other.getDeployWarning()))
			deployWarning = other.getDeployWarning();
		if(deployWarningAcknowledged != other.getDeployWarningAcknowledged())
			deployWarningAcknowledged = other.getDeployWarningAcknowledged();
		if(isHasCrossSiteVsb != other.isHasCrossSiteVsb())
			isHasCrossSiteVsb = other.isHasCrossSiteVsb();
	}
}

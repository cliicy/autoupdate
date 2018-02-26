package com.ca.arcserve.edge.app.base.appdaos;

import java.util.Date;

public class EdgeHostPolicyMap
{
	private int hostId;
	private int policyType;
	private int policyId;
	private int deployStatus;
	private int deployReason;
	private int deployFlags;
	private int tryCount;
	private Date lastSuccDeploy;
	private Date lastUpdate;
	private int enablestatus;
	
	public int getHostId()
	{
		return hostId;
	}
	
	public void setHostId( int hostId )
	{
		this.hostId = hostId;
	}
	
	public int getPolicyType()
	{
		return policyType;
	}
	
	public void setPolicyType( int policyType )
	{
		this.policyType = policyType;
	}
	
	public int getPolicyId()
	{
		return policyId;
	}
	
	public void setPolicyId( int policyId )
	{
		this.policyId = policyId;
	}
	
	public int getDeployStatus()
	{
		return deployStatus;
	}
	
	public void setDeployStatus( int deployStatus )
	{
		this.deployStatus = deployStatus;
	}
	
	public int getDeployReason()
	{
		return deployReason;
	}
	
	public void setDeployReason( int deployReason )
	{
		this.deployReason = deployReason;
	}
	
	public int getDeployFlags()
	{
		return deployFlags;
	}
	
	public void setDeployFlags( int deployFlags )
	{
		this.deployFlags = deployFlags;
	}
	
	public int getTryCount()
	{
		return tryCount;
	}

	public void setTryCount( int tryCount )
	{
		this.tryCount = tryCount;
	}

	public Date getLastSuccDeploy()
	{
		return lastSuccDeploy;
	}
	
	public void setLastSuccDeploy( Date lastSuccDeploy )
	{
		this.lastSuccDeploy = lastSuccDeploy;
	}
	
	public Date getLastUpdate()
	{
		return lastUpdate;
	}
	
	public void setLastUpdate( Date lastUpdate )
	{
		this.lastUpdate = lastUpdate;
	}

	public int getEnablestatus() {
		return enablestatus;
	}

	public void setEnablestatus(int enablestatus) {
		this.enablestatus = enablestatus;
	}
	
}

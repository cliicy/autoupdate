package com.ca.arcserve.edge.app.base.appdaos;

public class EdgePolicyHost
{
	private int hostId;
	private String hostName;
	private int hostType;
	private int appStatus;
	private int deployStatus;
	private int deployReason;
	private int deployFlags;
	private int tryCount;
	private int policyType;
	private int policyId;
	private String policyName;
	
	public int getHostId()
	{
		return hostId;
	}
	
	public void setHostId( int hostId )
	{
		this.hostId = hostId;
	}
	
	public String getHostName()
	{
		return hostName;
	}
	
	public void setHostName( String hostName )
	{
		this.hostName = hostName;
	}
	
	public int getHostType()
	{
		return hostType;
	}

	public void setHostType( int hostType )
	{
		this.hostType = hostType;
	}

	public int getDeployStatus()
	{
		return deployStatus;
	}
	
	public int getAppStatus()
	{
		return appStatus;
	}

	public void setAppStatus( int appStatus )
	{
		this.appStatus = appStatus;
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

	public String getPolicyName()
	{
		return policyName;
	}

	public void setPolicyName( String policyName )
	{
		this.policyName = policyName;
	}
}

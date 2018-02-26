package com.ca.arcserve.edge.app.base.appdaos;

public class EdgePolicyDeployTask
{
	private int hostId;
	private int policyType;
	private int policyId;
	private int deployStatus;
	private int deployReason;
	private int deployFlags;
	private int productType;
	private boolean rpsTask;
	private int contentFlag;
	private int enableStatus;
	
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

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public boolean isRpsTask() {
		return rpsTask;
	}

	public void setRpsTask(boolean rpsTask) {
		this.rpsTask = rpsTask;
	}

	public int getContentFlag() {
		return contentFlag;
	}

	public void setContentFlag(int contentFlag) {
		this.contentFlag = contentFlag;
	}

	public int getDeployStatus() {
		return deployStatus;
	}

	public void setDeployStatus(int deployStatus) {
		this.deployStatus = deployStatus;
	}

	public int getEnableStatus() {
		return enableStatus;
	}

	public void setEnableStatus(int enableStatus) {
		this.enableStatus = enableStatus;
	}
}

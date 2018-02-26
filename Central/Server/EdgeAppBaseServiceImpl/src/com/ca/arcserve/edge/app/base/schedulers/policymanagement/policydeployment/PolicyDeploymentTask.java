package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment;

public class PolicyDeploymentTask
{
	private int hostId;
	private int policyType;
	private int policyId;
	private int deployReason;
	private int deployFlags;
	private Object taskParameters;
	private int productType;
	private boolean rpsTask;
	private int contentFlag;
	
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

	public Object getTaskParameters()
	{
		return taskParameters;
	}

	public void setTaskParameters( Object taskParameters )
	{
		this.taskParameters = taskParameters;
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

	@Override
	public String toString()
	{
		return "PolicyDeploymentTask:" +
			"\n    hostId:         " + hostId +
			"\n    policyType:     " + policyType +
			"\n    policyId:       " + policyId +
			"\n    deployReason:   " + deployReason +
			"\n    deployFlags:    " + deployFlags +
			"\n    taskParameters: " + taskParameters +
			"\n    productType:    " + productType +
			"\n    contentFlag:    " + contentFlag;
	}
	
}

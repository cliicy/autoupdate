package com.ca.arcserve.edge.app.base.appdaos;

public class EdgePolicyDeployWarningErrorMessage {
	private int hostid;
	private int policytype;
	private String warning;
	private String error;
	
	public int getHostid()
	{
		return hostid;
	}
	
	public void setHostid( int hostid )
	{
		this.hostid = hostid;
	}
	
	public int getPolicytype()
	{
		return policytype;
	}
	
	public void setPolicytype( int policytype )
	{
		this.policytype = policytype;
	}

	public String getWarning()
	{
		return warning;
	}
	
	public void setWarning(String warning)
	{
		this.warning = warning;
	}
	
	public String getError()
	{
		return error;
	}
	
	public void setError(String error)
	{
		this.error = error;
	}
}

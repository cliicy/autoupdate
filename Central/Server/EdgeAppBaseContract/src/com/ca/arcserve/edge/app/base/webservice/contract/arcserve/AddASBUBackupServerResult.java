package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import java.io.Serializable;

public class AddASBUBackupServerResult implements Serializable
{
	private static final long serialVersionUID = 6532421681477262884L;
	
	public enum AddASBUBackupServerReturnCode
	{
		UNKONW,
		SUCCESSFUL,
		FAILED,
	}

	private String hostName;
	private AddASBUBackupServerReturnCode returnCode;
	
	public String getHostName()
	{
		return hostName;
	}
	
	public void setHostName( String hostName )
	{
		this.hostName = hostName;
	}
	
	public AddASBUBackupServerReturnCode getReturnCode()
	{
		return returnCode;
	}
	
	public void setReturnCode( AddASBUBackupServerReturnCode returnCode )
	{
		this.returnCode = returnCode;
	}
}

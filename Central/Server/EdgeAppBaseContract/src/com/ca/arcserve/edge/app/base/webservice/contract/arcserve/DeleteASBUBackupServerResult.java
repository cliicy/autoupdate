package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import java.io.Serializable;

public class DeleteASBUBackupServerResult implements Serializable
{
	private static final long serialVersionUID = 6532421681477262884L;
	
	public enum DeleteASBUBackupServerReturnCode
	{
		Unknown,
		Proctected,
		Successful,
		Failed,
	}

	private int serverId;
	private DeleteASBUBackupServerReturnCode returnCode;
	
	public int getServerId()
	{
		return serverId;
	}

	public void setServerId( int serverId )
	{
		this.serverId = serverId;
	}

	public DeleteASBUBackupServerReturnCode getReturnCode()
	{
		return returnCode;
	}
	
	public void setReturnCode( DeleteASBUBackupServerReturnCode returnCode )
	{
		this.returnCode = returnCode;
	}
}

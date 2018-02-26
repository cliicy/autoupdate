package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.Date;

public class D2DBackupInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Date startTime;
	private int backupType;
	private D2DBackupResult backupResult = D2DBackupResult.Unknown;
	
	public Date getStartTime()
	{
		return startTime;
	}
	
	public void setStartTime( Date startTime )
	{
		this.startTime = startTime;
	}
	
	public int getBackupType()
	{
		return backupType;
	}

	public void setBackupType( int backupType )
	{
		this.backupType = backupType;
	}

	public D2DBackupResult getBackupResult()
	{
		return backupResult;
	}
	
	public void setBackupResult( D2DBackupResult backupResult )
	{
		this.backupResult = backupResult;
	}
}

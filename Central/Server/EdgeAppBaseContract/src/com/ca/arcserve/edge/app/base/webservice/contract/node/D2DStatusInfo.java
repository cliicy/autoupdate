package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class D2DStatusInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	//////////////////////////////////////////////////////////////////////////

	// backup info
	private D2DBackupInfo lastBackupInfo;
	
	// recovery point info
	private int recoveryPointRetentionCount;
	private int recoveryPointCount;
	private D2DStatus recoveryPointStatus;
	
	// destination info
	private String destinationVolumnOrPath;
	private boolean isDestinationAccessible;
	private long destinationFreeSpace;
	private D2DStatus destinationStatus;
	
	// overall info
	private D2DStatus overallStatus;
	
	//////////////////////////////////////////////////////////////////////////

	public D2DBackupInfo getLastBackupInfo()
	{
		return lastBackupInfo;
	}

	public void setLastBackupInfo( D2DBackupInfo lastBackupInfo )
	{
		this.lastBackupInfo = lastBackupInfo;
	}

	public int getRecoveryPointRetentionCount()
	{
		return recoveryPointRetentionCount;
	}

	public void setRecoveryPointRetentionCount( int recoveryPointRetentionCount )
	{
		this.recoveryPointRetentionCount = recoveryPointRetentionCount;
	}

	public int getRecoveryPointCount()
	{
		return recoveryPointCount;
	}

	public void setRecoveryPointCount( int recoveryPointCount )
	{
		this.recoveryPointCount = recoveryPointCount;
	}

	public D2DStatus getRecoveryPointStatus()
	{
		return recoveryPointStatus;
	}

	public void setRecoveryPointStatus( D2DStatus recoveryPointStatus )
	{
		this.recoveryPointStatus = recoveryPointStatus;
	}

	public String getDestinationVolumnOrPath()
	{
		return destinationVolumnOrPath;
	}

	public void setDestinationVolumnOrPath( String destinationVolumnOrPath )
	{
		this.destinationVolumnOrPath = destinationVolumnOrPath;
	}

	public boolean isDestinationAccessible()
	{
		return isDestinationAccessible;
	}

	public void setDestinationAccessible( boolean isDestinationAccessible )
	{
		this.isDestinationAccessible = isDestinationAccessible;
	}

	public long getDestinationFreeSpace()
	{
		return destinationFreeSpace;
	}

	public void setDestinationFreeSpace( long destinationFreeSpace )
	{
		this.destinationFreeSpace = destinationFreeSpace;
	}

	public D2DStatus getDestinationStatus()
	{
		return destinationStatus;
	}

	public void setDestinationStatus( D2DStatus destinationStatus )
	{
		this.destinationStatus = destinationStatus;
	}

	public D2DStatus getOverallStatus()
	{
		return overallStatus;
	}

	public void setOverallStatus( D2DStatus overallStatus )
	{
		this.overallStatus = overallStatus;
	}
	
}

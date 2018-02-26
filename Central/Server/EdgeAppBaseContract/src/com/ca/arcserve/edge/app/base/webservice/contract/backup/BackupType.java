package com.ca.arcserve.edge.app.base.webservice.contract.backup;

public enum BackupType
{
	/**
	 * Full backup job
	 */
	Full		( 0 ),
	
	/**
	 * Incremental backup job
	 */
	Incremental	( 1 ),
	
	/**
	 * Resync backup job
	 */
	Resync		( 2 );
	
	private int value;
	
	BackupType( int value )
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}
	
	public static BackupType fromValue( int value )
	{
		for (BackupType item : BackupType.values())
		{
			if (item.value == value)
				return item;
		}
		return null;
	}
}

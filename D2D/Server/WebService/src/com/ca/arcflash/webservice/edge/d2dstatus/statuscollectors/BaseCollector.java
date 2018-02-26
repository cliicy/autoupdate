package com.ca.arcflash.webservice.edge.d2dstatus.statuscollectors;

import java.util.Date;

import com.ca.arcflash.webservice.data.BackupInformationSummary;
import com.ca.arcflash.webservice.data.RecentBackup;
import com.ca.arcflash.webservice.edge.d2dstatus.ID2DStatusCollector;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DBackupType;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DJobStatus;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatus;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;

public abstract class BaseCollector implements ID2DStatusCollector
{

	protected void getD2DBackupInfo( D2DStatusInfo statusInfo, BackupInformationSummary backupSummary )
	{
		RecentBackup recentBackup = null;
		Date lastBackupTime = null;
		D2DBackupType lastBackupType = D2DBackupType.Unknown;
		int lastBackupJobStatus = 0; // unknown
		
		if (backupSummary.getRecentFullBackup() != null)
		{
			recentBackup = backupSummary.getRecentFullBackup();
			lastBackupTime = recentBackup.getTime();
			lastBackupType = D2DBackupType.FullBackup;
		}
		
		if ((backupSummary.getRecentIncrementalBackup() != null) &&
			((lastBackupTime == null) || backupSummary.getRecentIncrementalBackup().getTime().after( lastBackupTime )))
		{
			recentBackup = backupSummary.getRecentIncrementalBackup();
			lastBackupTime = recentBackup.getTime();
			lastBackupType = D2DBackupType.IncrementalBackup;
		}
		
		if ((backupSummary.getRecentResyncBackup() != null) &&
			((lastBackupTime == null) || backupSummary.getRecentResyncBackup().getTime().after( lastBackupTime )))
		{
			recentBackup = backupSummary.getRecentResyncBackup();
			lastBackupTime = recentBackup.getTime();
			lastBackupType = D2DBackupType.ResyncBackup;
		}
		
		if (recentBackup != null)
			lastBackupJobStatus = recentBackup.getStatus();
		
		statusInfo.setLastBackupStartTime( lastBackupTime );
		statusInfo.setLastBackupType( lastBackupType );
		statusInfo.setLastBackupJobStatus( convertJobStatusValueToEnum( lastBackupJobStatus ) );
		
		statusInfo.setLastBackupStatus( computeLastBackupStatus( statusInfo.getLastBackupJobStatus() ) );
	}
	
	private D2DJobStatus convertJobStatusValueToEnum( int statusValue )
	{
		switch (statusValue)
		{
		case 1:
			return D2DJobStatus.Finished;
			
		case 2:
			return D2DJobStatus.Failed;
			
		case 3:
			return D2DJobStatus.Active;
			
		case 4:
			return D2DJobStatus.Canceled;
			
		case 5:
			return D2DJobStatus.Crashed;
		}
		
		return D2DJobStatus.Unknown;
	}
	
	private D2DStatus computeLastBackupStatus( D2DJobStatus backupStatus )
	{
		switch (backupStatus)
		{
		case Finished:
			return D2DStatus.OK;
			
		case Crashed:
		case Failed:
			return D2DStatus.Error;
			
		default:
			return D2DStatus.Warning;
		}
	}
	
	protected void computeOverallStatus( D2DStatusInfo statusInfo )
	{
		D2DStatus overallStatus = D2DStatus.Unknown;
		
		if ((statusInfo.getLastBackupStatus() == D2DStatus.Error) ||
			(statusInfo.getRecoveryPointStatus() == D2DStatus.Error) ||
			(statusInfo.getDestinationStatus() == D2DStatus.Error) ||
			(!statusInfo.isDriverInstalled()) || (!statusInfo.isRestarted()) ||
			(!statusInfo.isBackupConfiged()))
			overallStatus = D2DStatus.Error;
		
		else if ((statusInfo.getLastBackupStatus() == D2DStatus.Warning) ||
			(statusInfo.getRecoveryPointStatus() == D2DStatus.Warning) ||
			(statusInfo.getDestinationStatus() == D2DStatus.Warning))
			overallStatus = D2DStatus.Warning;
		
		else
			overallStatus = D2DStatus.OK;
		
		statusInfo.setOverallStatus( overallStatus );
	}
	
}

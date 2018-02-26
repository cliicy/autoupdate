package com.ca.arcflash.webservice.edge.d2dstatus.statuscollectors;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.BackupInformationSummary;
import com.ca.arcflash.webservice.data.DestinationCapacity;
import com.ca.arcflash.webservice.data.MountSession;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DEstimatedValue;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatus;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;

public class D2DStatusCollector extends BaseCollector
{
	private static final Logger logger = Logger.getLogger( D2DStatusCollector.class );
	private static D2DStatusCollector instance = null;
	
	private static final int MIN_BACKUP_COUNT = 5;
	
	public static synchronized D2DStatusCollector getInstance()
	{
		if (instance == null)
			instance = new D2DStatusCollector();
		
		return instance;
	}

	@Override
	public D2DStatusInfo getStatusInfo( String uuid )
	{
		try
		{
			BackupInformationSummary backupSummary =
				BackupService.getInstance().getBackupInformationSummary();
			
			D2DStatusInfo statusInfo = new D2DStatusInfo();
			statusInfo.setDriverInstalled(CommonService.getInstance().isDriverInstalled());
			statusInfo.setRestarted(CommonService.getInstance().isRestartedAfterDriver());
			statusInfo.setBackupConfiged(backupSummary!=null);
			
			if (backupSummary!=null) 
			{				
				getD2DBackupInfo( statusInfo, backupSummary );
				getRecoveryPointInfo( statusInfo, backupSummary );
				getDestinationInfo( statusInfo, backupSummary );
			}
			
			computeOverallStatus( statusInfo );
			
			return statusInfo;
		}
		catch (Exception e)
		{
			logger.error( "Error getting D2D status info.", e );
			return D2DStatusInfo.NullObject;
		}
	}

	private void getRecoveryPointInfo( D2DStatusInfo statusInfo, BackupInformationSummary backupSummary )
	{
		MountSession[] mountedSessions = null;
		
		try
		{
			mountedSessions = CommonService.getInstance().getMountedSessions();
		}
		catch (Exception e)
		{
			logger.error( "Error getting mounted sessions.", e );
		}
		
		statusInfo.setIsUseBackupSets(backupSummary.isBackupSet());
		statusInfo.setRecoveryPointRetentionCount( backupSummary.getRetentionCount() );
		if(backupSummary.isBackupSet())
		{
			statusInfo.setRecoveryPointCount(backupSummary.getRecoverySetCount());
			statusInfo.setRecoveryPointStatus( getRecoveryPointStatus(
					statusInfo.getRecoveryPointRetentionCount(), backupSummary.getRecoverySetCount(), true)  );			
		}
		else
		{			
			statusInfo.setRecoveryPointCount( backupSummary.getRecoveryPointCount() );
			statusInfo.setRecoveryPointStatus( getRecoveryPointStatus(
					statusInfo.getRecoveryPointRetentionCount(), statusInfo.getRecoveryPointCount(), false ) );
		}

		statusInfo.setRecoveryPointMounted(
				((mountedSessions == null) || (mountedSessions.length == 0)) ? null : mountedSessions[0].getSessionPath() );
		
	}
	
	private D2DStatus getRecoveryPointStatus( int retentionCount, int recoveryPointCount, boolean isUseBackupSets )
	{
		if (recoveryPointCount <= 0)
			return D2DStatus.Error;
		
		if(!isUseBackupSets){
			if ((recoveryPointCount > 0) && (recoveryPointCount < retentionCount))
				return D2DStatus.Warning;
		}
		else{
			if ((recoveryPointCount > 0) && (recoveryPointCount < (retentionCount +1)))
				return D2DStatus.Warning;
		}
		
		return D2DStatus.OK;
	}
	
	private void getDestinationInfo( D2DStatusInfo statusInfo, BackupInformationSummary backupSummary )
	{
		DestinationCapacity destCapacity = backupSummary.getDestinationCapacity();
		
		statusInfo.setDestinationPath( backupSummary.getBackupDestination() );
		statusInfo.setDestinationAccessible( backupSummary.getErrorCode() == 0 );
		statusInfo.setDestinationFreeSpace( destCapacity.getTotalFreeSize() );
		statusInfo.setDestinationEstimatedBackupCount( MIN_BACKUP_COUNT );
		
		long threshold = 0;
		D2DEstimatedValue estimatedValue = D2DEstimatedValue.FLASE;
		try
		{
			BackupConfiguration backupConfig = BackupService.getInstance().getBackupConfiguration();
			if ((backupConfig != null) && backupConfig.isEnableSpaceNotification())
			{
				final String MeasureUnitPercent = "%";
//				final String MeasureUnitMegabyte = "MB";
				
				if (backupConfig.getSpaceMeasureUnit().equals( MeasureUnitPercent ))
				{
					threshold = destCapacity.getTotalVolumeSize() * (new Double( backupConfig.getSpaceMeasureNum() )).longValue() / 100;
				}
				else
				{
					threshold = (new Double( backupConfig.getSpaceMeasureNum() )).longValue() * 1024 * 1024;
				}
			}
			else
			{
				estimatedValue = D2DEstimatedValue.TRUE;
				final int BackupType_Incremental = 1;
				final int BackupStatus_Finished = 1;
				
				RecoveryPoint[] recPoints = BackupService.getInstance().getMostRecentRecoveryPoints(
					BackupType_Incremental, BackupStatus_Finished, -1 );
				
				long totalSize = 0;
				int num = 0;
				for (RecoveryPoint recPoint : recPoints)
					if (recPoint.getBackupType() == 1) { //BackupTypeModel.Incremental						
						totalSize += recPoint.getDataSize();
						num++;
					}
				
				if(num > 0) {
					threshold = totalSize/num * MIN_BACKUP_COUNT;
				}
			}
		}
		catch (Exception e)
		{
			logger.error( "Error getting threshold in bytes.", e );
		}
		
		D2DStatus destStatus = D2DStatus.Unknown;
		if (destCapacity.getTotalVolumeSize() != 0) {			
			if (destCapacity.getTotalFreeSize() > threshold) 
			{
				destStatus = D2DStatus.OK;
			} 
			else 
			{			
				statusInfo.setEstimatedValue(estimatedValue);
				destStatus = D2DStatus.Warning;
			}
		} 
		else 
		{			
			destStatus = D2DStatus.Error;
		}
		
		statusInfo.setDestinationStatus( destStatus );
	}
	
}

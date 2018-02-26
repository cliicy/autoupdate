package com.ca.arcflash.webservice.edge.d2dstatus.statuscollectors;

import java.util.Arrays;
import java.util.List;

//import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.BackupInformationSummary;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DEstimatedValue;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatus;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;

public class VSphereStatusCollector extends BaseCollector
{
	//private static final Logger logger = Logger.getLogger( VSphereStatusCollector.class );
	private static VSphereStatusCollector instance = null;
	
	public static synchronized VSphereStatusCollector getInstance()
	{
		if (instance == null)
			instance = new VSphereStatusCollector();
		
		return instance;
	}

	@Override
	public D2DStatusInfo getStatusInfo( String uuid )
	{
		try {
			String vmInstanceUuid = uuid;
			
			VirtualMachine vm = new VirtualMachine();
			vm.setVmInstanceUUID(vmInstanceUuid);
			BackupInformationSummary summary = VSphereService.getInstance().getBackupInformationSummaryWithLicInfo(vm);
			BackupConfiguration configuration = BackupService.getInstance().getBackupConfiguration();
			D2DStatusInfo statusInfo = new D2DStatusInfo();
			
			getD2DBackupInfo( statusInfo, summary );
			getVMRecoveryPointInfo( statusInfo, summary );
			getVMDestinationInfo( statusInfo, summary, configuration );
			
			computeOverallStatus( statusInfo );

			return statusInfo;
		} catch (Exception e) {
			return null;
		}
		
	}

	private void getVMDestinationInfo(D2DStatusInfo statusInfo,
		BackupInformationSummary summary, BackupConfiguration configuration) throws ServiceException {
		D2DStatus destinationStatus = D2DStatus.Unknown;
		D2DEstimatedValue estimatedValue = D2DEstimatedValue.FLASE;
		long threshold = 0;
		
		// destination
		if (summary.getDestinationCapacity()!=null) {
			if (summary.getErrorCode()!=0) {
				statusInfo.setDestinationAccessible(false);
			} else {
				statusInfo.setDestinationAccessible(true);
			}
			if (summary.getDestinationCapacity().getTotalVolumeSize()==0) {
				destinationStatus = D2DStatus.Error;
			}
			if (summary.getBackupDestination()!=null) {
				if (!summary.getBackupDestination().startsWith("\\\\")) {
					String destination = summary.getBackupDestination();
					List<String> driveLetters = Arrays.asList(CommonService.getInstance().getVersionInfo().getLocalDriverLetters());
					String maxLengthMatch = "";
					for (int i = 0, count = driveLetters == null ? 0 : driveLetters.size(); i < count; i++) {
						String driverLetter = driveLetters.get(i).toLowerCase();
						if(destination.startsWith(driverLetter) && driverLetter.length() > maxLengthMatch.length())
							maxLengthMatch = driverLetter;
					}
					summary.setBackupDestination(maxLengthMatch);
				}
			}
		}
		
		if(configuration != null && configuration.isEnableSpaceNotification()) {
			if("%".equals(configuration.getSpaceMeasureUnit())) {
				if(summary.getDestinationCapacity() != null) 
					threshold = (long) (summary.getDestinationCapacity().getTotalVolumeSize() * summary.getSpaceMeasureNum() / 100);
			}
			else
				threshold = (long) (configuration.getSpaceMeasureNum()* 1024 * 1024);
		} else {
			RecoveryPoint[] recoverPoints = BackupService.getInstance().getMostRecentRecoveryPoints(1,1,-1);
			if(recoverPoints!=null && recoverPoints.length!=0){
				long totalSize = 0;
				int num = 0;
				for (RecoveryPoint pointModel : recoverPoints) {
					if(pointModel.getBackupType() == 1) {
						totalSize += pointModel.getDataSize();
						num++;
					}
				}
				if(num > 0) {
					threshold = totalSize/num * 5;
				}
			}
			estimatedValue = D2DEstimatedValue.TRUE;
		}
		
		if (summary.getDestinationCapacity()!=null) {
			long freeSize = summary.getDestinationCapacity().getTotalFreeSize();
			if (summary.getDestinationCapacity().getTotalVolumeSize() != 0){
				if (freeSize <= threshold){
					destinationStatus = D2DStatus.Warning;
					statusInfo.setEstimatedValue(estimatedValue);
				} else {
					if (destinationStatus.ordinal()<D2DStatus.OK.ordinal()) {						
						destinationStatus = D2DStatus.OK;
					}
				}
			}
		}
		
		statusInfo.setDestinationFreeSpace(summary.getDestinationCapacity().getTotalFreeSize());
		statusInfo.setDestinationStatus(destinationStatus);
		statusInfo.setDestinationPath(summary.getBackupDestination());
		
	}
	
	private void getVMRecoveryPointInfo(D2DStatusInfo statusInfo,
			BackupInformationSummary summary) {
		// recover points
		D2DStatus recoveryPointStatus = D2DStatus.Unknown;
		
		if(summary.isBackupSet()){
			if (summary.getRecoverySetCount() <= 0){
				recoveryPointStatus = D2DStatus.Error;
			}else if (summary.getRecoverySetCount()>0 && summary.getRecoverySetCount()<(summary.getRetentionCount()+1)){
				recoveryPointStatus = D2DStatus.Warning;
			}else if (summary.getRecoverySetCount()>=(summary.getRetentionCount()+1)){
				recoveryPointStatus = D2DStatus.OK;
			}
			statusInfo.setRecoveryPointCount(summary.getRecoverySetCount());
			
		}else{
			if (summary.getRecoveryPointCount() <= 0){
				recoveryPointStatus = D2DStatus.Error;
			}else if (summary.getRecoveryPointCount()>0 && summary.getRecoveryPointCount()<summary.getRetentionCount()){
				recoveryPointStatus = D2DStatus.Warning;
			}else if (summary.getRecoveryPointCount()>=summary.getRetentionCount()){
				recoveryPointStatus = D2DStatus.OK;
			}
			
			statusInfo.setRecoveryPointCount(summary.getRecoveryPointCount());
		}
		
		statusInfo.setIsUseBackupSets(summary.isBackupSet());			
		statusInfo.setRecoveryPointRetentionCount(summary.getRetentionCount());
		statusInfo.setRecoveryPointStatus(recoveryPointStatus);
	}
	
}

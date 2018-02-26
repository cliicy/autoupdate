package com.ca.arcflash.webservice.service.internal;

import java.util.Date;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreRunningState;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.BackupInformationSummary;
import com.ca.arcflash.webservice.data.DestinationCapacity;
import com.ca.arcflash.webservice.data.RPSDataStoreInfo;
import com.ca.arcflash.webservice.data.RecentBackup;
import com.ca.arcflash.webservice.data.backup.BackupStatus;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.jni.model.JBackupInfo;
import com.ca.arcflash.webservice.jni.model.JBackupInfoSummary;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.util.ServiceUtils;

public class BackupSummaryConverter {
	public BackupInformationSummary convert(JBackupInfoSummary source){
		BackupInformationSummary result = new BackupInformationSummary();
		
		//Destination Capacity
		DestinationCapacity destinationCapacity = getDestCapacity(source);
		
		//Recovery Point Count
		result.setRecoveryPointCount(source.getRecoveryPointCount());
		
		result.setRecoveryPointCount4Repeat(source.getRecoveryPointCount4Repeat());
		result.setRecoveryPointCount4Day(source.getRecoveryPointCount4Day());
		result.setRecoveryPointCount4Week(source.getRecoveryPointCount4Week());
		result.setRecoveryPointCount4Month(source.getRecoveryPointCount4Month());	
		
		result.setRecoverySetCount(source.getReocverySetCount());
		result.setErrorCode(source.getErrorCode());
		
		//Most Recent Backup
		for(JBackupInfo backupInfo : source.getBackupInfoList()){
			if(!BackupConverterUtil.validateDataFormat(backupInfo.getDate()+" "+backupInfo.getTime()))
				continue;
			Date date = BackupConverterUtil.string2Date(backupInfo.getDate()+" "+backupInfo.getTime());
			int type = BackupConverterUtil.string2BackupType(backupInfo.getType());
			int status = BackupConverterUtil.string2BackupStatus(backupInfo.getStatus());
			
			if (status == BackupStatus.Finished)
				result.setTotalSuccessfulCount(result.getTotalSuccessfulCount()+1);
			else if (status == BackupStatus.Failed)
				result.setTotalFailedCount(result.getTotalFailedCount()+1);
			else if(status == BackupStatus.Canceled)
				result.setTotalCanceledCount(result.getTotalCanceledCount()+1);
			else if(status == BackupStatus.Crashed)
				result.setTotalCrashedCount(result.getTotalCrashedCount()+1);
			
			if (type == BackupType.Full){
				if (result.getRecentFullBackup()==null){
					result.setRecentFullBackup(new RecentBackup());
					result.getRecentFullBackup().setTime(date);
					result.getRecentFullBackup().setType(type);
					result.getRecentFullBackup().setStatus(status);
					result.getRecentFullBackup().setTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(date));
				}else if (date.compareTo(result.getRecentFullBackup().getTime())>0){
					result.getRecentFullBackup().setTime(date);
					result.getRecentFullBackup().setType(type);
					result.getRecentFullBackup().setStatus(status);
					result.getRecentFullBackup().setTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(date));
				}
			}
			
			if (type == BackupType.Incremental){
				if (result.getRecentIncrementalBackup()==null){
					result.setRecentIncrementalBackup(new RecentBackup());
					result.getRecentIncrementalBackup().setTime(date);
					result.getRecentIncrementalBackup().setType(type);
					result.getRecentIncrementalBackup().setStatus(status);
					result.getRecentIncrementalBackup().setTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(date));
				}else if (date.compareTo(result.getRecentIncrementalBackup().getTime())>0){
					result.getRecentIncrementalBackup().setTime(date);
					result.getRecentIncrementalBackup().setType(type);
					result.getRecentIncrementalBackup().setStatus(status);
					result.getRecentIncrementalBackup().setTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(date));
				}
			}
			
			if (type == BackupType.Resync){
				if (result.getRecentResyncBackup()==null){
					result.setRecentResyncBackup(new RecentBackup());
					result.getRecentResyncBackup().setTime(date);
					result.getRecentResyncBackup().setType(type);
					result.getRecentResyncBackup().setStatus(status);
					result.getRecentResyncBackup().setTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(date));
				}else if (date.compareTo(result.getRecentResyncBackup().getTime())>0){
					result.getRecentResyncBackup().setTime(date);
					result.getRecentResyncBackup().setType(type);
					result.getRecentResyncBackup().setStatus(status);
					result.getRecentResyncBackup().setTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(date));
				}
			}
		}
		
		result.setDestinationCapacity(destinationCapacity);
		return result;
	}

	public DestinationCapacity getDestCapacity(JBackupInfoSummary source) {
		DestinationCapacity destinationCapacity = new DestinationCapacity();
		destinationCapacity.setFullBackupSize(StringUtil.string2Long(source.getDestinationInfo().getFullBackupSize(), 0));
		destinationCapacity.setIncrementalBackupSize(StringUtil.string2Long(source.getDestinationInfo().getIncrementalBackupSize(), 0));
		destinationCapacity.setResyncBackupSize(StringUtil.string2Long(source.getDestinationInfo().getResyncBackupSize(), 0));
		destinationCapacity.setTotalVolumeSize(StringUtil.string2Long(source.getDestinationInfo().getTotalSize(), 0));
		destinationCapacity.setTotalFreeSize(StringUtil.string2Long(source.getDestinationInfo().getTotalFreeSize(), 0));
		destinationCapacity.setCatalogSize(StringUtil.string2Long(source.getDestinationInfo().getCatalogSize(), 0));
		return destinationCapacity;
	}
	
	public RPSDataStoreInfo convert(DataStoreStatusListElem[] source) throws ServiceException{
		DataStoreRunningState runningState = DataStoreRunningState.parseInt((int)source[0].getDataStoreStatus().getOverallStatus());
		if (runningState != DataStoreRunningState.RUNNING)
			throw new ServiceException(FlashServiceErrorCode.RPS_DATASTORE_STOPPED);
		
		RPSDataStoreInfo rpsDSInfo = new RPSDataStoreInfo();
		rpsDSInfo.setVersion(source[0].getDataStoreSetting().getVersion());
		rpsDSInfo.setDataStorePath(source[0].getDataStoreSetting().getDSCommSetting().getStorePath());
		rpsDSInfo.setTotalSize(source[0].getDataStoreStatus().getCommonStoreStatus().getDataVolumeTotalSize());
		rpsDSInfo.setDirSize(source[0].getDataStoreStatus().getCommonStoreStatus().getDataDirSize());
		rpsDSInfo.setFreeSize(source[0].getDataStoreStatus().getCommonStoreStatus().getDataVolumeFreeSize());
		rpsDSInfo.setDedupe(source[0].getDataStoreSetting().getEnableGDD()==1?true:false);
		if (rpsDSInfo.isDedupe()) {
			if (source[0].getDataStoreStatus().getGDDStoreStatus() == null)
				throw new ServiceException(FlashServiceErrorCode.RPS_DATASTORE_DEDUPE_NOT_ACCESS);
			rpsDSInfo.setIndexPath(source[0].getDataStoreSetting().getGDDSetting().getIndexStorePath());
			rpsDSInfo.setIndexTotalSize(source[0].getDataStoreStatus().getGDDStoreStatus().getPrimaryRoleStatus().getIndexVolumeTotalSize());
			rpsDSInfo.setIndexDirSize(source[0].getDataStoreStatus().getGDDStoreStatus().getPrimaryRoleStatus().getIndexDirSize());
			rpsDSInfo.setIndexFreeSize(source[0].getDataStoreStatus().getGDDStoreStatus().getPrimaryRoleStatus().getIndexVolumeFreeSize());
			rpsDSInfo.setDataPath(source[0].getDataStoreSetting().getGDDSetting().getDataStorePath());
			rpsDSInfo.setDataTotalSize(source[0].getDataStoreStatus().getGDDStoreStatus().getDataRoleStatusArray()[0].getDataVolumeTotalSize());
			rpsDSInfo.setDataDirSize(source[0].getDataStoreStatus().getGDDStoreStatus().getDataRoleStatusArray()[0].getDataDirSize());
			rpsDSInfo.setDataFreeSize(source[0].getDataStoreStatus().getGDDStoreStatus().getDataRoleStatusArray()[0].getDataVolumeFreeSize());
			rpsDSInfo.setHashPath(source[0].getDataStoreSetting().getGDDSetting().getHashStorePath());
			rpsDSInfo.setHashTotalSize(source[0].getDataStoreStatus().getGDDStoreStatus().getHashRoleStatusArray()[0].getHashVolumeTotalSize());
			rpsDSInfo.setHashDirSize(source[0].getDataStoreStatus().getGDDStoreStatus().getHashRoleStatusArray()[0].getHashDirSize());
			rpsDSInfo.setHashFreeSize(source[0].getDataStoreStatus().getGDDStoreStatus().getHashRoleStatusArray()[0].getHashVolumeFreeSize());
		}

	    return rpsDSInfo;
	}
}

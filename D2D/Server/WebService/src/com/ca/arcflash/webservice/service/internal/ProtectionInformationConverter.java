package com.ca.arcflash.webservice.service.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.service.jni.model.JProtectionInfo;
import com.ca.arcflash.webservice.data.ProtectionInformation;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.service.BackupService;

public class ProtectionInformationConverter {
	public List<ProtectionInformation> convert(JProtectionInfo[] sources, boolean isDedupe){
		List<ProtectionInformation> result = new ArrayList<ProtectionInformation>();
		
		for(JProtectionInfo source : sources)
			result.add(convert(source, isDedupe));
		
		//return result.toArray(new ProtectionInformation[0]);
		return result;
	}
	
	public ProtectionInformation convert(JProtectionInfo source, boolean isDedupe){
		ProtectionInformation result = new ProtectionInformation();
		
		result.setBackupType(BackupConverterUtil.string2BackupType(source.getType()));
		result.setCount(StringUtil.string2Int(source.getCount(), 0));
		result.setTotalLogicalSize(StringUtil.string2Long(source.getTotalLogicalSize(), 0));
		result.setSize(StringUtil.string2Long(source.getTotalSize(), 0));
		result.setDedupe(isDedupe);
		if(!StringUtil.isEmptyOrNull(source.getLastBackupTime())){
			Date serverDate = BackupConverterUtil.string2Date(source.getLastBackupTime());
			String serverDateString = StringUtil.date2String(serverDate);
			result.setLastBackupTime(serverDateString);
		}
		try {
			BackupConfiguration backupConfiguration = BackupService.getInstance().getBackupConfiguration();
			if (backupConfiguration!=null){
				if (result.getBackupType() == BackupType.Full)
					result.setShedule(backupConfiguration.getFullBackupSchedule());
				else if (result.getBackupType() == BackupType.Incremental)
					result.setShedule(backupConfiguration.getIncrementalBackupSchedule());
				else if (result.getBackupType() == BackupType.Resync)
					result.setShedule(backupConfiguration.getResyncBackupSchedule());
			}
		} catch (Exception e) {
			Logger.getLogger(ProtectionInformationConverter.class).
				error(e.getMessage() == null? e : e.getMessage());
		}
		
		
		return result;
	}
}

package com.ca.arcflash.webservice.service;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.service.AbstractMergeService.MergeEvent;

public class BackupSetService extends BaseBackupSetService {
	private static Logger logger = Logger.getLogger(BackupSetService.class);
	
	private BackupSetService() {
		
	}
	
	private static class InstanceClass {
		public static BackupSetService INSTANCE = new BackupSetService();
	}
	
	public static BackupSetService getInstance() {
		return InstanceClass.INSTANCE;
	}
	
	/**
	 * after retention policy or backup schedule changed, we may need to 
	 * change the backup set flag only for current backup set
	 * since we need to get recovery points, it's a time consuming work,
	 * so we'd call this method asynchronously
	 * @param configuration
	 */
	public synchronized void markBackupSetFlag(BackupConfiguration configuration) {
		logger.debug("Enter markBackupSetFlag");
		markBackupSetFlag(configuration.getRetentionPolicy(), configuration.getDestination(),
				"", configuration.getUserName(), configuration.getPassword(), null);
		try {
			MergeService.getInstance().resumeMerge(MergeEvent.SCHEDULE_BEGIN);
		}catch(Exception e) {
			logger.error("Failed to resume merge job");
		}
		logger.debug("End markBackupSetFlag");
	}
	
	protected boolean existScheduledFullJob(int date, VirtualMachine vm) {
		return BackupService.getInstance().isScheduledFullJob(date);
	}
	
	protected boolean existScheduledBackup(int date, VirtualMachine vm) {
		return BackupService.getInstance().existScheduledBackup(date);
	}
}

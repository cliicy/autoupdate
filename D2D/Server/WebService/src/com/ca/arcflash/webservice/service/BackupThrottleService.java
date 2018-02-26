package com.ca.arcflash.webservice.service;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.ca.arcflash.webservice.data.backup.BackupConfiguration;


public class BackupThrottleService extends AbstractBackupThrottleService {

	private static final Logger logger = Logger.getLogger(BackupThrottleService.class);	
	
	private static BackupThrottleService INSTANCE = SingletonInstance.INSTANCE;
	BackupConfiguration backupConf = null;

	public BackupThrottleService() {
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		
	}
	
	public synchronized static BackupThrottleService getInstance(){
		return INSTANCE;
	}

	private static class SingletonInstance {
		static BackupThrottleService INSTANCE = new BackupThrottleService();
	}

	@Override
	protected String getThrottleJobName(String vmInstanceUUID) {
		return BACKUP_THROTTLE_JOB_NAME;
	}
	
	public void scheduleBackupThrottleJob(){
		logger.debug("Schedule backup throttle job");

		try {
			backupConf = BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e1) {
			logger.error("Failed to get backup configuration");
		}

		unschedule(null);
    	if(backupConf != null && backupConf.getBackupDataFormat() == 1) {
    		schedule();
    	}
	}
}

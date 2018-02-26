package com.ca.arcflash.webservice.scheduler;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.ServiceException;

public class BackupThrottleJob extends AbstractBackupThrottleJob {
	private static final Logger logger = Logger.getLogger(BackupThrottleJob.class);
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		super.execute(context);
		BackupConfiguration backupConf = null;
		try {
			backupConf = BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e1) {
			logger.error("Failed to get backup configuration");
		}
		
        long throttling = getCurrentThrottling(backupConf);
        logger.info("set throttling: "+ throttling);
        BackupService.getInstance().setThrottling(throttling);
        
        updateThrottling4Jobs(throttling);
	}
}

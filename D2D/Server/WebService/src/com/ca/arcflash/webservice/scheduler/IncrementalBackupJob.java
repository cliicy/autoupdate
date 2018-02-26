package com.ca.arcflash.webservice.scheduler;

import org.apache.log4j.Logger;
import org.quartz.impl.JobDetailImpl;

import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.job.rps.BackupJobArg;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.service.ServiceContext;

public class IncrementalBackupJob extends BaseBackupJob {
private static final Logger logger = Logger.getLogger(FullBackupJob.class);
	
	@Override
	public JJobScript generateBackupJobScript(BackupConfiguration configuration, ArchiveConfiguration archiveConfig) throws Exception {
		JJobScript result = null;
		try {
			result = jobConverter.convert(configuration,
					BackupType.Incremental, ServiceContext.getInstance()
							.getLocalMachineName(), isDaily, isWeekly,
					isMonthly, bCatalogGenerate, archiveConfig);
		} catch (Exception e) {
			logger.error("Generate backup job script exception", e);
			throw e;
		}
		
		return result;
	}
	
	@Override
	protected BackupJobArg getBackupJobArg(JobDetailImpl jobDetail, BackupConfiguration configuration) {		
		BackupJobArg arg =  super.getBackupJobArg(jobDetail, configuration);
		arg.setJobMethod(BackupType.Incremental);
		return arg;
	}

	@Override
	protected int getJobMethod() {
		return BackupType.Incremental;
	}
}

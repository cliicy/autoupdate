/**
 * 
 */
package com.ca.arcflash.webservice.scheduler;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.archive.ArchiveConfigurationConstants;
import com.ca.arcflash.webservice.data.archive.ArchiveJobScript;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.service.ArchiveCatalogSyncService;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.DeleteArchiveService;
import com.ca.arcflash.webservice.service.PurgeArchiveService;
import com.ca.arcflash.webservice.service.RestoreArchiveService;
import com.ca.arcflash.webservice.service.ServiceException;

/**
 * @author manpi01
 *
 */
public class ArchiveSourceDeleteJob extends BaseArchiveJob {
	private static final Logger logger = Logger.getLogger(ArchiveCatalogJob.class);
	
	@Override
	protected void afterComplete(JJobMonitor jJM) {
		if (jJM!=null && jJM.getUlJobPhase() == JobExitPhase) {
			try {
				sendEmail(jJM);
//				saveRSSFeed(jJM);
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
		}
		isStopJM = false;
		
		if(getJobStatus() != JOBSTATUS_CRASH && getJobStatus() != JOBSTATUS_FAILED && getJobStatus() != JOBSTATUS_CANCELLED){
			try {
				Thread.sleep(1000*60);
			} catch (InterruptedException e) {
				logger.error("sleep error after archive source delete job");
			}
			DeleteArchiveService.getInstance().submitArchiveSourceDeleteJob();
		}
		
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("executing archive source delete job");
		JobDetail jobDetail = context.getJobDetail();
		Object currentJobType = jobDetail.getJobDataMap().get(BaseService.JOB_TYPE);
		if(currentJobType == null){
			logger.error("current job type is null");
			return;
		}
		logger.debug("current job type is " + currentJobType);
		setJobType((long)currentJobType);
		Date date = context.getFireTime();
		
		if (PurgeArchiveService.getInstance().isPurgeJobRunning() 
				|| ArchiveService.getInstance().isArchiveBackupJobRunning()
				|| RestoreArchiveService.getInstance().isArchiveRestoreJobRunning()
				|| ArchiveCatalogSyncService.getInstance().isArchiveCatalogSyncJobRunning()
				|| DeleteArchiveService.getInstance().isArchiveSourceDeleteJobRunning()){
			logger.debug("There is another job running...");
			return;
		}
		ArchiveJobScript jobScript = null;
		
		jobScript = (ArchiveJobScript) context.getJobDetail().getJobDataMap().get("JobScript");
		if(jobScript == null){
			logger.error("job script is null - return");
			return;
		}
		initShrmemid();
		jobScript.setUlShrMemID(shrmemid);
		
		setSharedPath(jobScript);
		
		if(preprocess(jobScript)){
			logger.info("archive source delete job submitted to native facade.");
			if(0 != DeleteArchiveService.getInstance().archiveSourceDelete(jobScript)){
				logger.error("submit archive source delete job error");
				stopJobMonitor();
				setJobStatus(JOBSTATUS_FAILED);
				EdgeDataSynchronization.SetSyncDataFlag();
				return;
			}
			synchronized (phaseLock) {
				while (jobPhase != JobExitPhase)
					try {
						phaseLock.wait();
					} catch (InterruptedException e) {
						logger.error(e.getMessage() == null ? e : e.getMessage());
					}
			}
			retryJobWhenFailedOrCrashed(jobDetail);
			
			EdgeDataSynchronization.SetSyncDataFlag();
		}
		
	}

	private void retryJobWhenFailedOrCrashed(JobDetail jobDetail) {
		if(getJobStatus() == JOBSTATUS_FAILED || getJobStatus() == JOBSTATUS_CRASH){
			logger.debug("job failed or crashed, job id is " + shrmemid);
			if(DeleteArchiveService.getInstance().isDeleteMakeupJob()){
				logger.debug("The current job is a makeup job - return");
				DeleteArchiveService.getInstance().cleanDeleteMakeupJobSchedule();
				return;
			}
			DeleteArchiveService.getInstance().createDeleteMakeupSchedule(jobDetail);
		}
	}

	@Override
	protected long getDefaultJobType() {
		return getJobType();
	}
	
	private void setSharedPath(ArchiveJobScript jobScript){
		try {
			BackupConfiguration backupConfig = BackupService.getInstance().getBackupConfiguration();
			if(!StringUtil.isEmptyOrNull(jobScript.getBackupDestinationPath()) && !jobScript.getBackupDestinationPath().startsWith("\\\\")){
				jobScript.setBackupDestinationPath(backupConfig.getDestination());
				/*jobScript.setBackupUserName(backupConfig.getAdminUserName());
				jobScript.setbackupPassword(backupConfig.getAdminPassword());*/
				jobScript.setBackupUserName(backupConfig.getUserName());
				jobScript.setbackupPassword(backupConfig.getPassword());
				jobScript.setBackupDestType(ArchiveConfigurationConstants.SHARED_PATH); // 0 if non - rps
			}
			String sessionStubPath = getBackupSessionStubPath(jobScript.getPAFNodeList().get(0).getPwszSessPath());
			jobScript.getPAFNodeList().get(0).setPwszSessPath(jobScript.getBackupDestinationPath() + sessionStubPath);
			jobScript.getPAFNodeList().get(0).setPwszUserName(jobScript.getBackupUserName());
			jobScript.getPAFNodeList().get(0).setPwszUserPW(jobScript.getbackupPassword());
			
		} catch (ServiceException e) {
			logger.error("get backup configuration error", e);
		}
		
	}
	
	private String getBackupSessionStubPath(String backupSessionPath){
		if(StringUtil.isEmptyOrNull(backupSessionPath))
			return "";
		int length = backupSessionPath.substring(0, backupSessionPath.lastIndexOf("\\")).lastIndexOf("\\");
		return backupSessionPath.substring(length, backupSessionPath.length());
	}
}

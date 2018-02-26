package com.ca.arcflash.webservice.service;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationConfig;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationDetailsConfig;
import com.ca.arcflash.webservice.data.archive.RestoreArchiveJob;
import com.ca.arcflash.webservice.data.job.rps.ArchiveJobArg;
import com.ca.arcflash.webservice.scheduler.ArchiveCatalogJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.ServiceUtils;

public class ArchiveCatalogSyncService extends BaseService {
	private static final ArchiveCatalogSyncService archiveCatalogInstance = new ArchiveCatalogSyncService();
	private static final Logger logger = Logger.getLogger(ArchiveCatalogSyncService.class);
	
	private Scheduler scheduler;
	
	public ArchiveCatalogSyncService()
	{
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
	}
	
	public static ArchiveCatalogSyncService getInstance(){
		return archiveCatalogInstance;
	}
	
	public ArchiveDestinationDetailsConfig getArchiveChangedDestinationDetails(ArchiveDestinationConfig in_archiveDestConfig) throws ServiceException
	{
		ArchiveDestinationDetailsConfig config = new ArchiveDestinationDetailsConfig();
		try {
			config = getNativeFacade().getArchiveChangedDestinationDetails(in_archiveDestConfig);
		} catch (Throwable e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		return config;
	}
	
	public long submitArchiveCatalogSyncJob(RestoreArchiveJob in_archiveRestoreJobDetails) throws ServiceException{
		logger.debug("archive restore() - start");
			
		long ret = -1;
		//check whether there is running jobs
		if (scheduler == null)
		{
			logger.error("Failed to find the Scheduler to submit the archive catalog sync job.");
			return ret;
		}
		
		if(isArchiveCatalogSyncJobRunning())
		{
			logger.info("Another archive catalog sync job is in progress, please wait");
			throw generateAxisFault(FlashServiceErrorCode.ArchiveCatalog_SYNCJOB_IN_PROGRESS);
		}
		if (PurgeArchiveService.getInstance().isPurgeJobRunning() || RestoreArchiveService.getInstance().isArchiveRestoreJobRunning() || ArchiveService.getInstance().isArchiveBackupJobRunning()){
			logger.info("Another job is running, throwing Common_OtherJobIsRunning in archive submitRestoreJob");
			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
		}
		
		try {
			JobDetailImpl jobDetail = new JobDetailImpl(JOB_NAME_ARCHIVE_CATALOGSYNC,JOB_GROUP_ARCHIVE_CATALOGSYNC,ArchiveCatalogJob.class);
			jobDetail.getJobDataMap().put("Job", in_archiveRestoreJobDetails);
			jobDetail.getJobDataMap().put("NativeFacade", this.getNativeFacade());
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0,0);
			trigger.setName(TRIGGER_NAME_ARCHIVE_CATALOG);
			scheduler.scheduleJob(jobDetail, trigger);
			
			checkForMergeRunning(ServiceUtils.jobType2String(Constants.AF_JOBTYPE_ARCHIVE_CATALOGSYNC, 0));
			
			ret = 0;//success
			logger.info("archive submit Catalog sync job - end");
		} catch(ServiceException se) {
			throw se;
		}catch(org.quartz.ObjectAlreadyExistsException alreadyExistsEx)
		{
			logger.info("Another job is runnign, throwing org.quartz.ObjectAlreadyExistsException in archive submitRestoreJob");
			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
		}
		catch (Throwable e) {
			logger.error("archive submitRestoreJob()", e);
			throw generateInternalErrorAxisFault();
		}
		return ret;
		
	}
	
	public boolean isArchiveCatalogSyncJobRunning(){
		boolean bRunning = false;
		logger.debug("checking whether another Catalog Sync job is running");
		try {
			logger.debug("calling native facade");
			bRunning = getNativeFacade().IsArchiveCatalogSyncJobRunning();
			logger.debug("another Catalog Sync job status" + bRunning);
		} catch (Throwable e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		logger.debug("another Catalog Sync job status" + bRunning);
		return bRunning;
	}

	public long archveCatalogNow(ArchiveJobArg jobArg) throws ServiceException {
		logger.debug("archive restore() - start");
		
		long ret = -1;
		//check whether there is running jobs
		if (scheduler == null)
		{
			logger.error("Failed to find the Scheduler to submit the archive catalog sync job.");
			return ret;
		}
		
		if(isArchiveCatalogSyncJobRunning())
		{
			logger.info("Another archive catalog sync job is in progress, please wait");
			throw generateAxisFault(FlashServiceErrorCode.ArchiveCatalog_SYNCJOB_IN_PROGRESS);
		}
		if (PurgeArchiveService.getInstance().isPurgeJobRunning() || RestoreArchiveService.getInstance().isArchiveRestoreJobRunning() || ArchiveService.getInstance().isArchiveBackupJobRunning()){
			logger.info("Another job is running, throwing Common_OtherJobIsRunning in archive submitRestoreJob");
			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
		}
		
		try {
			JobDetailImpl jobDetail = new JobDetailImpl(jobArg.getJobDetailName(),
					jobArg.getJobDetailGroup() + Constants.RUN_NOW, ArchiveCatalogJob.class);
			jobDetail.getJobDataMap().put("JobScript", jobArg.getJobScript());
			jobDetail.getJobDataMap().put("NativeFacade", this.getNativeFacade());
			jobDetail.getJobDataMap().put(Constants.RUN_NOW, Boolean.TRUE);
			jobDetail.getJobDataMap().put(JOB_ID, jobArg.getJobId());
			
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0,0);
			trigger.setName(TRIGGER_NAME_ARCHIVE_CATALOG);
			scheduler.scheduleJob(jobDetail, trigger);
			
			checkForMergeRunning(ServiceUtils.jobType2String(Constants.AF_JOBTYPE_ARCHIVE_CATALOGSYNC, 0));
			
			ret = 0;//success
			logger.info("archive submit Catalog sync job - end");
		} catch(ServiceException se) {
			throw se;
		}catch(org.quartz.ObjectAlreadyExistsException alreadyExistsEx)
		{
			logger.info("Another job is runnign, throwing org.quartz.ObjectAlreadyExistsException in archive submitRestoreJob");
			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
		}
		catch (Throwable e) {
			logger.error("archive submitRestoreJob()", e);
			throw generateInternalErrorAxisFault();
		}
		return ret;
	}
}

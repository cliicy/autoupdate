package com.ca.arcflash.webservice.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationConfig;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationVolumeConfig;
import com.ca.arcflash.webservice.data.archive.RestoreArchiveJob;
import com.ca.arcflash.webservice.data.job.rps.ArchiveJobArg;
import com.ca.arcflash.webservice.jni.model.JRWLong;
import com.ca.arcflash.webservice.scheduler.ArchiveRestoreJob;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.validator.ArchiveConfigurationValidator;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.ServiceUtils;

public class RestoreArchiveService extends BaseService {
	private static final Logger logger = Logger.getLogger(BrowserService.class);
	private static final RestoreArchiveService instance = new RestoreArchiveService();
	private static final JobMonitor archiveRestoreJobMonitor = new JobMonitor(-1);
	private ArchiveConfigurationValidator restoreJobValidator = new ArchiveConfigurationValidator();

	private Scheduler scheduler;
	
	private RestoreArchiveService(){
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
	}
	
	public static RestoreArchiveService getInstance(){
		return instance;
	}

	public JobMonitor getArchiveRestoreJobMonitor() {
		synchronized (archiveRestoreJobMonitor) {
			if (archiveRestoreJobMonitor.getJobId() == -1L)
				return null;

			return archiveRestoreJobMonitor;
		}
	}
	
	public JobMonitor getJobMonitorInternal() {
		return archiveRestoreJobMonitor;
	}

	public ArchiveDestinationVolumeConfig[] getArchiveDestinationItems(ArchiveDestinationConfig archiveDestConfig) throws ServiceException{
		logger.debug("getArchiveDestinationItems - start");
		
		ArchiveDestinationVolumeConfig[] archiveDestinationVolumesList = null;
		String hostName = archiveDestConfig.getstrHostname();
		try {
			InetAddress localMachine = InetAddress.getLocalHost();
			
			if(hostName == null || hostName.length() == 0)
				hostName = localMachine.getHostName();

			List<String> VolumesList = getNativeFacade().getArchiveDestinationVolumes(hostName,archiveDestConfig);
			
			archiveDestinationVolumesList = new ArchiveDestinationVolumeConfig[VolumesList.size()];
			int iVolumeNum = 0;
			for(String strVolume : VolumesList)
			{
				long lVolumeHandle = getNativeFacade().getArchivedVolumeHandle(strVolume);
				
				JRWLong childrenCnt = new JRWLong();
				getNativeFacade().GetArchiveChildrenCount(lVolumeHandle,"",childrenCnt);//WSJNI.GetArchiveChildrenCount(lVolumeHandle, "", childrenCnt);
				
				archiveDestinationVolumesList[iVolumeNum] = new ArchiveDestinationVolumeConfig();
				archiveDestinationVolumesList[iVolumeNum].setvolumeHandle(lVolumeHandle);
				archiveDestinationVolumesList[iVolumeNum].setDisplayName(strVolume);
				archiveDestinationVolumesList[iVolumeNum].setGuid(strVolume);
				archiveDestinationVolumesList[iVolumeNum].setCatalogFilePath("");
				archiveDestinationVolumesList[iVolumeNum].setChildrenCount(childrenCnt.getValue());
				iVolumeNum++;
			}
			
		} catch (UnknownHostException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		
		logger.debug("getArchiveDestinationItems - end");
		return archiveDestinationVolumesList;
	}
	
	public long submitRestoreJob(RestoreArchiveJob in_archiveRestoreJobDetails) throws ServiceException{
		logger.debug("archive restore() - start");
			
		long ret = -1;
		//check whether there is running jobs
		try{
			restoreJobValidator.validateArchiveJob(in_archiveRestoreJobDetails);
		}catch(ServiceException e){
			logger.debug("Restore Job Validation Failed:"+e.getErrorCode());
			throw e;
		}catch(Exception e){
			logger.debug(e);
		}

		if (scheduler == null)
		{
			logger.error("Failed to find the Scheduler to submit the restore job.");
			return ret;
		}
		
		if (PurgeArchiveService.getInstance().isPurgeJobRunning() || isArchiveRestoreJobRunning() || ArchiveService.getInstance().isArchiveBackupJobRunning() || ArchiveCatalogSyncService.getInstance().isArchiveCatalogSyncJobRunning()){
			logger.info("Another job is runnign, throwing Common_OtherJobIsRunning in archive submitRestoreJob");
			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
		}
		
		try {
			JobDetail jobDetail = new JobDetailImpl(JOB_NAME_ARCHIVE_RESTORE,JOB_GROUP_ARCHIVE_RESTORE_NAME,ArchiveRestoreJob.class);
			jobDetail.getJobDataMap().put("Job", in_archiveRestoreJobDetails);
			jobDetail.getJobDataMap().put("NativeFacade", this.getNativeFacade());
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0,0);
			trigger.setName(TRIGGER_NAME_ARCHIVE_RESTORE);
			scheduler.scheduleJob(jobDetail, trigger);
			
			checkForMergeRunning(ServiceUtils.jobType2String(Constants.AF_JOBTYPE_ARCHIVE_RESTORE, 0));
			
			ret = 1;
			logger.debug("archive submitRestoreJob - end");
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
	
	//madra04 
	public Boolean ValidateRestoreArchiveJob(
			RestoreArchiveJob in_archiveRestoreJobDetails)
			throws ServiceException {
		boolean ret = true;
		logger.debug("archive restore() - start");
		if (in_archiveRestoreJobDetails == null)
			throw new ServiceException(FlashServiceErrorCode.Common_NullParameter);		
		try {
			restoreJobValidator
					.validateRestoreEncryptionDetails(in_archiveRestoreJobDetails);
		} catch (ServiceException e) {
			logger.debug("Restore Job Validation Failed:" + e.getErrorCode());
			throw e;
		} catch (Exception e) {
			logger.debug(e);
		}

		return ret;

	}
	

	public Boolean ValidateRestoreJob(
			RestoreArchiveJob in_archiveRestoreJobDetails)
			throws ServiceException {
		boolean ret = false;
		logger.debug("archive restore() - start");
		if (in_archiveRestoreJobDetails == null)
			throw new ServiceException(FlashServiceErrorCode.Common_NullParameter);		
		try {
			ret = restoreJobValidator
					.validateRestoreDetails(in_archiveRestoreJobDetails);
		} catch (ServiceException e) {
			logger.debug("Restore Job Validation Failed:" + e.getErrorCode());
			throw e;
		} catch (Exception e) {
			logger.debug(e);
		}

		return ret;

	}
	
	public boolean isArchiveRestoreJobRunning(){
		boolean bRunning = false;
		logger.debug("checking whether another restore job is running");
		try {
			logger.debug("calling native facade");
			bRunning = getNativeFacade().IsArchiveRestoreJobRunning();
			logger.debug("another restore job status" + bRunning);
		} catch (Throwable e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
		logger.debug("another restore job status" + bRunning);
		return bRunning;
	}

	public long archiveRestoreNow(ArchiveJobArg jobArg) throws ServiceException {
		logger.debug("archive restore() - start");
		
		long ret = -1;
		//check whether there is running jobs		
		
		if (PurgeArchiveService.getInstance().isPurgeJobRunning() || isArchiveRestoreJobRunning() 
				|| ArchiveService.getInstance().isArchiveBackupJobRunning() || ArchiveCatalogSyncService.getInstance().isArchiveCatalogSyncJobRunning()){
			logger.info("Another job is runnign, throwing Common_OtherJobIsRunning in archive submitRestoreJob");
			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
		}
		
		try {
			JobDetail jobDetail = new JobDetailImpl(jobArg.getJobDetailName(),
					jobArg.getJobDetailGroup() + Constants.RUN_NOW, ArchiveRestoreJob.class);
			jobDetail.getJobDataMap().put("JobScript", jobArg.getJobScript());
			jobDetail.getJobDataMap().put("NativeFacade", this.getNativeFacade());
			jobDetail.getJobDataMap().put(Constants.RUN_NOW, Boolean.TRUE);
			jobDetail.getJobDataMap().put(JOB_ID, jobArg.getJobId());
			
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0,0);
			trigger.setName(TRIGGER_NAME_ARCHIVE_RESTORE);
			scheduler.scheduleJob(jobDetail, trigger);
			
			checkForMergeRunning(ServiceUtils.jobType2String(Constants.AF_JOBTYPE_ARCHIVE_RESTORE, 0));
			
			ret = 1;
			logger.debug("archive submitRestoreJob - end");
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

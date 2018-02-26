package com.ca.arcflash.webservice.scheduler;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.archive.ArchiveJobScript;
import com.ca.arcflash.webservice.data.archive.RestoreArchiveJob;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.PurgeArchiveService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.internal.ArchiveJobConverter;

public class ArchiveRestoreJob extends BaseArchiveJob{
	private static final Logger logger = Logger.getLogger(ArchiveRestoreJob.class);
	protected ArchiveJobConverter jobConverter = new ArchiveJobConverter();
	private String fileCopyLocation;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (PurgeArchiveService.getInstance().isPurgeJobRunning() 
				|| ArchiveService.getInstance().isArchiveBackupJobRunning()){
			logger.debug("There is another job running...");
			return;
		}
		RestoreArchiveJob job = (RestoreArchiveJob) context.getJobDetail()
				.getJobDataMap().get("Job");
		NativeFacade nativeFacade = (NativeFacade) context.getJobDetail()
				.getJobDataMap().get("NativeFacade");
		ArchiveJobScript targetJob = (ArchiveJobScript) context.getJobDetail()
				.getJobDataMap().get("JobScript");
		isOnDemand = true;
		fileCopyLocation = parseFileCopyLocation(job);
		String NodeName = null;
		if(targetJob != null){
			NodeName = targetJob.getPAFNodeList().get(0).getPwszNodeName();
		}else
			NodeName = job.getArchiveNodes()[0].getNodeName();
		if(NodeName == null || NodeName.length() == 0)
		{
			NodeName = ServiceContext.getInstance().getLocalMachineName();
		}
		
		if(targetJob == null)
			targetJob = generateArchiveRestoreJobScript(shrmemid,job, NodeName);
		//targetJob = convert2ArchiveRestoreJobScript(job, ServiceContext.getInstance().getLocalMachineName());
/*		Boolean runNow = context.getJobDetail().getJobDataMap().getBoolean(Constants.RUN_NOW);
		
		if(checkRpsPolicy4ArchiveJob(runNow, context, targetJob)){
			return;
		}*/
		if(StringUtil.isEmptyOrNull(targetJob.getPwszCatalogDirPath())){
			targetJob.setPwszCatalogDirPath(ArchiveService.getInstance().getCatalogPath4AllArchiveJobs());
		}
		if(shrmemid <= 0)
			initShrmemid();
		if(preprocess(targetJob)){
			try {
				logger.info("Archive restore job submitted to native facade.");
				nativeFacade.archiveRestore(targetJob);
			} catch (Throwable e) {
				logger.error("execute(native api restore job)", e);
				
			}
			setJobType(Constants.AF_JOBTYPE_ARCHIVE_RESTORE);
		}
		
		//sonle01: Inform sync Data to Edge
		EdgeDataSynchronization.SetSyncDataFlag();
	}
	
	private ArchiveJobScript generateArchiveRestoreJobScript(long shrmemid,RestoreArchiveJob job,
			String localMachineName) {
		
		ArchiveJobScript archiveJob = null;
		archiveJob = jobConverter.convertArchiveRestoreJobToScript(shrmemid, job, localMachineName);
		
		return archiveJob;
	}
			
	/*protected void updateJobMonitor(JJobMonitor jJM) {
		logger.debug("updateJobMonitor(JJobMonitor) - start");

		JobMonitor jmon = RestoreArchiveService.getInstance().getJobMonitorInternal();
		synchronized (jmon) {
			jmon.setId(shrmemid);
			jmon.setBackupStartTime(jJM.getUlBackupStartTime());
			jmon.setCurrentProcessDiskName(jJM.getWszDiskName());
			jmon.setEstimateBytesDisk(jJM.getUlEstBytesDisk());
			jmon.setEstimateBytesJob(jJM.getUlEstBytesJob());
			jmon.setFlags(jJM.getUlFlags());
			jmon.setJobMethod(jJM.getUlJobMethod());
			jmon.setJobPhase(jJM.getUlJobPhase());
			jmon.setJobStatus(jJM.getUlJobStatus());
			jmon.setJobType(jJM.getUlJobType());
			jmon.setSessionID(jJM.getUlSessionID());
			jmon.setTransferBytesDisk(jJM.getUlXferBytesDisk());
			jmon.setTransferBytesJob(jJM.getUlXferBytesJob());
			jmon.setElapsedTime(jJM.getUlElapsedTime());
			jmon.setVolMethod(jJM.getUlVolMethod());
		}

		logger.debug("updateJobMonitor(JJobMonitor) - end");
	}*/
	
/*	@Override
	protected void runJob() {
		logger.debug("run() - start");
		JJobMonitor jJM = null;
		long jobMonitorHandle = 0;
		try {
			jobMonitorHandle = CommonService.getInstance().getNativeFacade().createJobMonitor(shrmemid);
			logger.debug("Job monitor handle:"+jobMonitorHandle);
			long begin = System.currentTimeMillis();
			long current = begin;
			while (true) {					
				try{
					if (isStopJM) {
						break;
					}

					jJM = CommonService.getInstance().getNativeFacade().GetJobMonitor(jobMonitorHandle);
					
					current = System.currentTimeMillis();
					long duration = (current - begin)/1000/60;
					if(duration > 10L && (jJM == null || jJM.getUlJobPhase() == 0))// read for 10 min and still no job. break it.
					{
						logger.info(duration + " min passed for job:" + shrmemid);
						break;							
					}						
					
					if (jJM != null){
						updateJobMonitor(jJM);
						if (jJM.getUlJobPhase() == JobExitPhase) {
							break;
						}
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						logger.error("run()", e);
						e.printStackTrace();
					}
				}catch(Exception e){
					logger.error("get job monitor", e);
				}
			}
		}
		finally {
			*//** the basebackup job's waiting for the phaseLock lock, anyway, we need to liberate it*//*
			 synchronized (phaseLock) {
				 if(jJM!=null && jJM.getUlJobPhase() == JobExitPhase)
				 {
					 jobPhase = JobExitPhase;
					 jobStatus = jJM.getUlJobStatus();						
				 }
				 phaseLock.notifyAll();
			 }
			 

			try {
				if (jobMonitorHandle != 0) {
					CommonService.getInstance().getNativeFacade()
							.releaseJobMonitor(jobMonitorHandle);
				}
			} catch (Throwable e) {
				logger.error("Error when release job monitor", e);
			}

			releaseJobLock();
			if (jJM!=null && jJM.getUlJobPhase() == JobExitPhase) {
				BaseArchiveJob.makeupMissedPurge();
				BaseArchiveJob.makeupMissedArchive();
				
				try{
					sendEmail(jJM);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					logger.debug(e.getMessage());
				}
				
			}
			isStopJM = false;
		}
		logger.debug("run() - end");
	}*/

	@Override
	protected void afterComplete(JJobMonitor in_JJM) {
		if (in_JJM!=null && in_JJM.getUlJobPhase() == JobExitPhase) {
			makeupMissedPurge();
			makeupMissedFCPurge();
			makeupMissedFileCopy();
			makeupMissedFileArchive();
			
			try{
				//setting the job type to Job moniter
				in_JJM.setUlJobType(Constants.AF_JOBTYPE_ARCHIVE_RESTORE);
				sendEmail(in_JJM);
				saveRSSFeed(in_JJM);
			}
			catch (Exception e)
			{
				
				logger.debug(e.getMessage());
			}
			
		}
		isStopJM = false;
	}

	@Override
	protected long getDefaultJobType() {
		return JobType.JOBTYPE_FILECOPY_RESTORE;
	}
	
	@Override
	protected String getArchiveDestination(){
		return fileCopyLocation; 
	}
}





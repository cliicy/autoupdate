package com.ca.arcflash.webservice.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveJobScript;
import com.ca.arcflash.webservice.data.archive.JobScriptArchiveNode;
import com.ca.arcflash.webservice.data.archive.RestoreArchiveJob;
import com.ca.arcflash.webservice.data.restore.RestoreJobType;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.PurgeArchiveService;
import com.ca.arcflash.webservice.service.RestoreArchiveService;

public class ArchiveCatalogJob extends BaseArchiveJob {

	private static final Logger logger = Logger.getLogger(ArchiveCatalogJob.class);
	
	private String fileCopyLocation;
	
	@Override
	protected void afterComplete(JJobMonitor jJM) {
		if (jJM!=null && jJM.getUlJobPhase() == JobExitPhase) {
			try {
				makeupMissedPurge();
				makeupMissedFCPurge();
				sendEmail(jJM);
			} catch (Exception e) {
				
				logger.debug(e.getMessage());
			}
		 }
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("executing archive catalog sync job");
		
		if (PurgeArchiveService.getInstance().isPurgeJobRunning() 
				|| ArchiveService.getInstance().isArchiveBackupJobRunning() || RestoreArchiveService.getInstance().isArchiveRestoreJobRunning()){
			logger.debug("There is another job running...");
			return;
		}
		
		RestoreArchiveJob catalogJob = (RestoreArchiveJob) context.getJobDetail().getJobDataMap().get("Job");
		ArchiveJobScript jobScript = (ArchiveJobScript) context.getJobDetail().getJobDataMap().get("JobScript");
		NativeFacade nativeFacade = (NativeFacade)context.getJobDetail().getJobDataMap().get("NativeFacade");
		Object jobID = context.getJobDetail().getJobDataMap().get(BaseService.JOB_ID);
		if(jobID != null)
			this.shrmemid = (Long)jobID;
						
		/*ArchiveConfiguration configuration = null;
		try {
			configuration = ArchiveService.getInstance().getArchiveConfiguration();
			if(configuration == null) return;
		} catch (ServiceException e) {
			logger.error("ArchiveService.getInstance().getArchiveConfiguration() error");
			e.printStackTrace();
		}*/
		fileCopyLocation = parseFileCopyLocation(catalogJob);
		if(jobScript == null) 
			jobScript = generateArchiveCatalogJobScript(shrmemid,catalogJob);
		if(jobScript == null) return ;
		
		if(StringUtil.isEmptyOrNull(jobScript.getPwszCatalogDirPath())){
			jobScript.setPwszCatalogDirPath(ArchiveService.getInstance().getCatalogPath4AllArchiveJobs());
		}
		
		if(shrmemid <= 0)
			initShrmemid();
		Boolean runNow = context.getJobDetail().getJobDataMap().getBoolean(Constants.RUN_NOW);
		
//		if(checkRpsPolicy4ArchiveJob(runNow, context, jobScript)){
//			return;
//		}
		if(preprocess(jobScript)){
			try {
				logger.info("Archive Catalog job submitted to native facade.");
				nativeFacade.archiveCatalogSync(jobScript);
			} catch (Throwable e) {
				logger.error("execute(native api for Archive Catalog job)", e);
				
			}
			setJobType(Constants.AF_JOBTYPE_ARCHIVE_CATALOGSYNC);
		}
		
	}

	private ArchiveJobScript generateArchiveCatalogJobScript(long shrmemid,
			RestoreArchiveJob catalogJob) {
		
		ArchiveJobScript archiveRestoreJobScript = new ArchiveJobScript();
		
		archiveRestoreJobScript.setUlVersion(1);//xml version
		archiveRestoreJobScript.setUsJobType(BaseArchiveJob.Job_Type_ArchiveCatalogSync);
		logger.debug("convert() - Job Type = " + BaseArchiveJob.Job_Type_ArchiveCatalogSync);
		archiveRestoreJobScript.setUsJobMethod(0);//incremental
		archiveRestoreJobScript.setUlShrMemID(shrmemid);
		
		if (catalogJob.getJobType() == 0) {
			catalogJob.setJobType(RestoreJobType.FileSystem);
		}
		
		archiveRestoreJobScript.setDwArchiveDestType(catalogJob.getArchiveDestType());
		archiveRestoreJobScript.setDiskDestInfo(catalogJob.getArchiveDiskInfo());
		
		ArchiveCloudDestInfo cloudInfo = catalogJob.getArchiveCloudInfo();
		if(cloudInfo != null)//should check for Null condition
		{
			String BucketName = cloudInfo.getcloudBucketName();
			String EncodedBucketName = cloudInfo.getEncodedCloudBucketName();
			if(BucketName!= null && BucketName.length() > 0)					
				cloudInfo.setcloudBucketName(BucketName);				
					
			if(EncodedBucketName!= null && EncodedBucketName.length() > 0)							
				cloudInfo.setEncodedCloudBucketName(EncodedBucketName);			
			else if(BucketName!= null && BucketName.length() > 0)
				cloudInfo.setEncodedCloudBucketName(BucketName);
		}
		
		archiveRestoreJobScript.setCloudDestInfo(cloudInfo);
		
		JobScriptArchiveNode archiveNode = new JobScriptArchiveNode();
		archiveNode.setPwszNodeName(catalogJob.getArchiveNodes()[0].getNodeName());
		
		List<JobScriptArchiveNode> listOfArchiveNodes = new ArrayList<JobScriptArchiveNode>();
		listOfArchiveNodes.add(archiveNode);
		
		archiveRestoreJobScript.setNNodeItems(listOfArchiveNodes.size());
		archiveRestoreJobScript.setPAFNodeList(listOfArchiveNodes);
		
		archiveRestoreJobScript.setPwszAfterJob("");
		archiveRestoreJobScript.setPwszBeforeJob("");
		archiveRestoreJobScript.setPwszComments("run Archive catalog sync job");
		archiveRestoreJobScript.setPwszPrePostUser("");
		archiveRestoreJobScript.setPwszPrePostPassword("");
		
		archiveRestoreJobScript.setPwszCatalogDirPath(catalogJob.getSessionPath());
		archiveRestoreJobScript.setPwszCatalogDirUserName(catalogJob.getCatalogFolderUser());
		archiveRestoreJobScript.setPwszCatalogDirPassword(catalogJob.getCatalogFolderPassword());
		
		return archiveRestoreJobScript;
	}

	@Override
	protected long getDefaultJobType() {
		return JobType.JOBTYPE_FILECOPY_CATALOGSYNC;
	}	
	
	@Override
	protected String getArchiveDestination(){
		return fileCopyLocation; 
	}
}

package com.ca.arcflash.webservice.scheduler;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.service.MergeService;
import com.ca.arcflash.webservice.service.VSphereMergeService;

public class EmailSenderForManualMergeJob implements Job {
	private static final Logger logger = Logger.getLogger(EmailSenderForManualMergeJob.class);
	
	@Override
	public void execute(JobExecutionContext jobContext) throws JobExecutionException {	
		logger.info("Running email sender job");
		MergeService.getInstance().sendEmailOnMergePausedManually(null);
		
		MergeStatus[] vmStatus = VSphereMergeService.getInstance().getMergeStatusList();		
		for(MergeStatus status : vmStatus){
			VSphereMergeService.getInstance().sendEmailOnMergePausedManually(status.getUUID());
		}
	}
}

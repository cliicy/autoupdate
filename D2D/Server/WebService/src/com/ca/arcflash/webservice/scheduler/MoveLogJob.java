package com.ca.arcflash.webservice.scheduler;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.webservice.jni.WSJNI;

public class MoveLogJob implements Job {
	Logger logger = Logger.getLogger(MoveLogJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0)
			throws JobExecutionException {
		logger.info("Execute move log job - start");
		logger.info("The next fire time is " + arg0.getTrigger().getNextFireTime());
		if(WSJNI.AFMoveLogs() != 0)
			logger.error("Move log failed");
		logger.info("Execute move log job - end");
	}
}

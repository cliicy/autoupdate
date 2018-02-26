package com.ca.arcflash.webservice.scheduler;

import org.quartz.JobExecutionContext;

public interface IComputeMissedJob {
	public void initJobContext(JobExecutionContext missedJobContext, JobExecutionContext runningJobContext);
	JobExecutionContext compute(JobExecutionContext incoming);
	JobExecutionContext computeForHBBU(JobExecutionContext incoming); 
}

package com.ca.arcflash.jobqueue;

import com.ca.arcflash.common.CommonUtil;

public class JobQueueFactory {

	private static String JobQueueLocation = CommonUtil.D2DHAInstallPath+"Configuration\\AFJobQueue\\"; 
	private static JobQueue jobQueue = null;

	public static synchronized JobQueue getDefaultJobQueue() {
		if(jobQueue == null) {
			jobQueue = new JobQueue(JobQueueLocation);
		}
		
		return jobQueue;
	}

}

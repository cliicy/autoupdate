package com.ca.arcflash.webservice.service.internal;

import java.util.Hashtable;
import java.util.Timer;

import com.ca.arcflash.webservice.data.JobMonitor;

public interface IVSphereJobQueue {
	final Timer timer = new Timer();
	public static final int JOBQUEUE_VM_PRIORITY = 0;
	public static final int JOBQUEUE_VAPPCHILDVM_PRIORITY = 1;
	
	public void initJobQueue();

	public void destroy();
	
	public boolean isJobWaiting(String targetuuid);
	
	public boolean removeWaitingJob(String targetuuid);
	
	public int validateRunningCount(int vmType);
	
	public boolean isJobRunning(String vmIndentification,String jobType);
	
	public void saveJobQueueToFile();
	
	public void readJobQueueFromFile();
	
	public Hashtable<String, JobMonitor> getWaitingJobTable();
	
	public long getWaitingJobID(String vmIdentification);
	
	public void removeWaitingJobByDatastoreUUID(String datastoreUUID);
}

package com.ca.arcflash.webservice.service.internal;

public class VSphereJobContext {
	
	public final static int JOB_LAUNCHER_D2D = 0;
	public final static int JOB_LAUNCHER_VSPHERE = 1;
	
	private int jobLauncher;
	
	private String launcherInstanceUUID;
	
	private String executerInstanceUUID;
	
	private int jobType;
	
	private long jobId;
	
	private String vmName;

	private int priority;    // 0 - VM , 1 - Child VM under vApp, 2 - vApp 
	
	private long masterJobId;
	
	public int getJobLauncher() {
		return jobLauncher;
	}

	public void setJobLauncher(int jobLauncher) {
		this.jobLauncher = jobLauncher;
	}


	public String getLauncherInstanceUUID() {
		return launcherInstanceUUID;
	}

	public void setLauncherInstanceUUID(String launcherInstanceUUID) {
		this.launcherInstanceUUID = launcherInstanceUUID;
	}

	public String getExecuterInstanceUUID() {
		return executerInstanceUUID;
	}

	public void setExecuterInstanceUUID(String executerInstanceUUID) {
		this.executerInstanceUUID = executerInstanceUUID;
	}

	public int getJobType() {
		return jobType;
	}

	public void setJobType(int jobType) {
		this.jobType = jobType;
	}

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public long getMasterJobId() {
		return masterJobId;
	}

	public void setMasterJobId(long masterJobId) {
		this.masterJobId = masterJobId;
	}

}

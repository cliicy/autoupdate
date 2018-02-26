package com.ca.arcflash.webservice.jni.model;

/**
 * The class is to record the information of the running job after webservice crashs. 
 * @author zhawe03
 *
 */
public class JJobContext {
	long         dwJobId;//The job id
    long         dwQueueType;//Job queue type 1 for regular 2 for on demand both of them for catalog job
    							//unknow for other jobs
    long         dwJobType;//the job type
    long         dwProcessId;
    long         dwJMShmId;//same as job id
    String       launcherInstanceUUID;
    long         dwLauncher;
    String       executerInstanceUUID;
    long 	     dwPriority;//the job priority, 0 - VM , 1 - Child VM under vApp, 2 - vApp, vApp job has the highest priority
    long		 dwMasterJobId; // specific for vApp master job, so that child jobs can associate with.
    String		 generatedDestination; //vApp child VM backup destination path
    
	public long getDwJobId() {
		return dwJobId;
	}
	public void setDwJobId(long dwJobId) {
		this.dwJobId = dwJobId;
	}
	public long getDwQueueType() {
		return dwQueueType;
	}
	public void setDwQueueType(long dwQueueType) {
		this.dwQueueType = dwQueueType;
	}
	public long getDwJobType() {
		return dwJobType;
	}
	public void setDwJobType(long dwJobType) {
		this.dwJobType = dwJobType;
	}
	public long getDwProcessId() {
		return dwProcessId;
	}
	public void setDwProcessId(long dwProcessId) {
		this.dwProcessId = dwProcessId;
	}
	public long getDwJMShmId() {
		return dwJMShmId;
	}
	public void setDwJMShmId(long dwJMShmId) {
		this.dwJMShmId = dwJMShmId;
	}
	public String getLauncherInstanceUUID() {
		return launcherInstanceUUID;
	}
	public void setLauncherInstanceUUID(String launcherInstanceUUID) {
		this.launcherInstanceUUID = launcherInstanceUUID;
	}
	public long getDwLauncher() {
		return dwLauncher;
	}
	public void setDwLauncher(long dwLauncher) {
		this.dwLauncher = dwLauncher;
	}
	public String getExecuterInstanceUUID() {
		return executerInstanceUUID;
	}
	public void setExecuterInstanceUUID(String executerInstanceUUID) {
		this.executerInstanceUUID = executerInstanceUUID;
	}
	public long getDwPriority() {
		return dwPriority;
	}
	public void setDwPriority(long dwPriority) {
		this.dwPriority = dwPriority;
	}
	public long getDwMasterJobId() {
		return dwMasterJobId;
	}
	public void setDwMasterJobId(long dwMasterJobId) {
		this.dwMasterJobId = dwMasterJobId;
	}
	public String getGeneratedDestination() {
		return generatedDestination;
	}
	public void setGeneratedDestination(String generatedDestination) {
		this.generatedDestination = generatedDestination;
	}
}

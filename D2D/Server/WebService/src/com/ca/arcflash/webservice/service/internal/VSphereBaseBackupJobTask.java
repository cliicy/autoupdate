package com.ca.arcflash.webservice.service.internal;

import java.util.Date;

import com.ca.arcflash.webservice.scheduler.VSphereBackupJob;

public abstract class VSphereBaseBackupJobTask implements Runnable{

	public VSphereBaseBackupJobTask() {
		submitTime = new Date();
	}
	
	protected VSphereBackupJob backupJob;
	
	protected Date submitTime;


	public VSphereBackupJob getBackupJob() {
		return backupJob;
	}

	public void setBackupJob(VSphereBackupJob backupJob) {
		this.backupJob = backupJob;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}
	
	abstract int getJobPriority();
	
	@Override
	public void run() {
		
	}

}

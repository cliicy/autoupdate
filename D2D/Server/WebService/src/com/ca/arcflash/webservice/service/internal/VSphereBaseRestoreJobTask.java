package com.ca.arcflash.webservice.service.internal;

import java.util.Date;

import com.ca.arcflash.webservice.scheduler.VSphereRecoveryJob;
import com.ca.arcflash.webservice.scheduler.VSphereRestoreJob;

public abstract class VSphereBaseRestoreJobTask implements Runnable{

	public VSphereBaseRestoreJobTask() {
		submitTime = new Date();
	}
	
	protected VSphereRestoreJob restoreJob;
	
	protected VSphereRecoveryJob recoveryJob;

	protected Date submitTime;


	public VSphereRestoreJob getRestoreJob() {
		return restoreJob;
	}

	public void setBackupJob(VSphereRestoreJob restoreJob) {
		this.restoreJob = restoreJob;
	}

	public VSphereRecoveryJob getRecoveryJob() {
		return recoveryJob;
	}

	public void setRecoveryJob(VSphereRecoveryJob recoveryJob) {
		this.recoveryJob = recoveryJob;
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

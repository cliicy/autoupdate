package com.ca.arcflash.webservice.service.internal;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.webservice.data.vsphere.VMRestoreJob;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.VSphereRestoreJob;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.VSphereService;

public class VSphereRestoreJobTask extends VSphereBaseRestoreJobTask {

	private static final Logger logger = Logger.getLogger(VSphereRestoreJobTask.class);
	private JobExecutionContext context;
	private int jobQueuePriority = VSphereJobQueue.JOBQUEUE_VM_PRIORITY;
	
	public VSphereRestoreJobTask(VSphereRestoreJob vSphereRestoreJob, JobExecutionContext context){
		this.restoreJob = vSphereRestoreJob;
		this.context = context;
		
		Object priority = context.getJobDetail().getJobDataMap().get("jobQueuePriority");
		if(priority != null) {
			jobQueuePriority = (int)priority;
		}
	}
	
	public VirtualMachine getVirtualMachine(){
		JobDetail jobDetail = context.getJobDetail();
		VirtualMachine vm = (VirtualMachine)jobDetail.getJobDataMap().get("vm");
		return vm;
	}
	
	public String getVMInstanceUUID(){
		return getVirtualMachine().getVmInstanceUUID();
	}
	
	public int getJobType(){
		JobDetail jobDetail = context.getJobDetail();
		Integer jobType = jobDetail.getJobDataMap().getInt("jobType");
		return jobType;
	}
	
	public String getJobName(){
		JobDetail jobDetail = context.getJobDetail();
		String jobName = jobDetail.getJobDataMap().getString("jobName");
		return jobName;
	}
	
	public VMRestoreJob getVMRestoreJob(){
		JobDetail jobDetail = context.getJobDetail();
		String rpsPolicyUUID = jobDetail.getJobDataMap().getString(BaseService.RPS_POLICY_UUID);
		String rpsDataStoreUUID = jobDetail.getJobDataMap().getString(BaseService.RPS_DATASTORE_UUID);
		String rpsDataStoreName = jobDetail.getJobDataMap().getString(BaseService.RPS_DATASTORE_DISPLAY_NAME);
		RpsHost rpsHost = (RpsHost) jobDetail.getJobDataMap().get(BaseService.RPS_HOST);
		boolean runNow = jobDetail.getJobDataMap().getBoolean(Constants.RUN_NOW);;
		Object jobID = jobDetail.getJobDataMap().get(VSphereService.JOB_ID);
		
		VMRestoreJob job = new VMRestoreJob();
		job.setVmInstanceUUID(getVMInstanceUUID());
		job.setRpsPolicyUUID(rpsPolicyUUID);
		job.setRpsDataStoreUUID(rpsDataStoreUUID);
		job.setRpsDataStoreName(rpsDataStoreName);
		job.setRpsHost(rpsHost);
		job.setRunNow(runNow);
		job.setJobID(jobID);
		return job;
	}
	
	@Override
	public void run() {
		VMRestoreJob job = getVMRestoreJob();
		try {
			VSphereRestoreJobQueue.getInstance().addJobToRunningQueue(job);
			restoreJob.executeRestore(context);
		} catch (JobExecutionException e) {
			logger.error(e);
		}finally{
			VSphereRestoreJobQueue.getInstance().removeJobFromRunningQueue(job);
		}
	}
	
	public void setJobPriority(int priority) {
		jobQueuePriority = priority;
	}
	
	public int getJobPriority() {
		return jobQueuePriority;
	}
}

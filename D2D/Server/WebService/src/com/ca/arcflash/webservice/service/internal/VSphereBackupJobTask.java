package com.ca.arcflash.webservice.service.internal;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VMBackupJob;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.scheduler.VSphereBackupJob;
import com.ca.arcflash.webservice.service.VSphereService;

public class VSphereBackupJobTask extends VSphereBaseBackupJobTask {

	private static final Logger logger = Logger.getLogger(VSphereBackupJobTask.class);
	private JobExecutionContext context;
	private int jobQueuePriority = VSphereJobQueue.JOBQUEUE_VM_PRIORITY;
	
	public VSphereBackupJobTask(VSphereBackupJob vSphereBackupJob, JobExecutionContext context){
		this.backupJob = vSphereBackupJob;
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
	
	public VMBackupJob getVMBackupJob(){
		JobDetail jobDetail = context.getJobDetail();
		Integer jobType = jobDetail.getJobDataMap().getInt("jobType");
		VirtualMachine vm = (VirtualMachine)jobDetail.getJobDataMap().get("vm");
		String jobName = jobDetail.getJobDataMap().getString("jobName");
		VMBackupJob job = new VMBackupJob();
		job.setJobName(jobName);
		job.setJobType(jobType);
		job.setVm(vm);
		
		try
		{
			VMBackupConfiguration backupConfig = VSphereService.getInstance().getBackupConfiguration(vm.getVmInstanceUUID());			
			job.setVmType(backupConfig.getBackupVM().getVmType()); // set VM type to know if it is VMware or Hyper-V
		}
		catch(Exception e)
		{
			logger.error(e);
		}	
		
		return job;
	}
	
	public void setBackupJob(String jobName, int jobType){
		JobDetail jobDetail = context.getJobDetail();
		jobDetail.getJobDataMap().put("jobType", jobType);
		jobDetail.getJobDataMap().put("jobName", jobName);
	}
	
	@Override
	public void run() {
		VMBackupJob job = getVMBackupJob();
		try {
			VSphereJobQueue.getInstance().addJobToRunningQueue(job);
			backupJob.executeBackupJob(context);
		} catch (JobExecutionException e) {
			logger.error(e);
		}finally{
			VSphereJobQueue.getInstance().removeJobFromRunningQueue(job);
		}
	}
	
	public void setJobPriority(int priority) {
		jobQueuePriority = priority;
	}
	
	public int getJobPriority() {
		return jobQueuePriority;
	}

	public JobExecutionContext getContext()
	{
		return context;
	}
}

package com.ca.arcflash.webservice.service.internal;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VMRestoreJob;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.VSphereRecoveryJob;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;

public class VSphereRestartRestoreJobTask extends VSphereBaseRestoreJobTask {

	private static final Logger logger = Logger.getLogger(VSphereRestartRestoreJobTask.class);
	private VSphereJobContext context;
	
	public VSphereRestartRestoreJobTask(VSphereJobContext context, VSphereRecoveryJob recoveryJob){
		this.context = context;
		this.recoveryJob = recoveryJob;
	}
	
	private VMRestoreJob getVMRestoreJob(){
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(context.getExecuterInstanceUUID());
		try {
			VMBackupConfiguration config = VSphereService.getInstance().getVMBackupConfiguration(vm);
			BackupVM backupVM = config.getBackupVM();
			vm.setVmHostName(backupVM.getVmHostName());
			vm.setVmName(backupVM.getVmName());
			vm.setVmVMX(backupVM.getVmVMX());
		} catch (ServiceException e) {
			logger.error(e);
		}
		
		VMRestoreJob vmRestoreJob = new VMRestoreJob();
		vmRestoreJob.setVmInstanceUUID(context.getExecuterInstanceUUID());
		vmRestoreJob.setRpsDataStoreName(restoreJob.getDataStoreDisplayName());
		return vmRestoreJob;
	}
	
	@Override
	public void run() {
		
		VMRestoreJob vmRestoreJob = getVMRestoreJob();
		try {
			VSphereRestoreJobQueue.getInstance().addJobToRunningQueue(vmRestoreJob);
			synchronized (restoreJob.phaseLock) 
			 {
			     while( restoreJob.getJobPhase() != Constants.JobExitPhase){
			    	 restoreJob.phaseLock.wait();
			     }
		     }
			logger.info("The restart restore job completes. ");
		} catch (Exception e) {
			logger.error(e);
		}
		finally{
			VSphereRestoreJobQueue.getInstance().removeJobFromRunningQueue(vmRestoreJob);
		}
	}
	
	public int getJobPriority() {
		return context.getPriority();
	}

	public String getVMInstanceUUID(){
		return context.getExecuterInstanceUUID();
	}


}

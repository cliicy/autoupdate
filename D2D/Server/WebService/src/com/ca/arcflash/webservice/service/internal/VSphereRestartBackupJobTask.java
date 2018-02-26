package com.ca.arcflash.webservice.service.internal;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VMBackupJob;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.scheduler.VSphereBackupJob;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;

public class VSphereRestartBackupJobTask extends VSphereBaseBackupJobTask {

	private static final Logger logger = Logger.getLogger(VSphereRestartBackupJobTask.class);
	private VSphereJobContext context;
	
	public VSphereRestartBackupJobTask(VSphereJobContext context, VSphereBackupJob backupJob){
		this.context = context;
		this.backupJob = backupJob;
	}
	
	private VMBackupJob getVMBackupJob(){
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(context.getExecuterInstanceUUID());
		
		int vmType = 0;
		try {
			VMBackupConfiguration config = VSphereService.getInstance().getVMBackupConfiguration(vm);
			BackupVM backupVM = config.getBackupVM();
			vm.setVmHostName(backupVM.getVmHostName());
			vm.setVmName(backupVM.getVmName());
			vm.setVmVMX(backupVM.getVmVMX());
			
			vmType = backupVM.getVmType();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
		
		VMBackupJob vmBackupJob = new VMBackupJob();
		vmBackupJob.setJobName("");
		vmBackupJob.setJobType(context.getJobType());
		vmBackupJob.setVm(vm);
		vmBackupJob.setVmType(vmType); // set VM type to know if it is VMware or Hyper-V
		
		return vmBackupJob;
	}
	
	@Override
	public void run() {
		
		VMBackupJob vmBackupJob = getVMBackupJob();
		try {
			VSphereJobQueue.getInstance().addJobToRunningQueue(vmBackupJob);
			String msg = String.format("vmName[%s] vmHostName[%s] vmInstanceUUID[%s]",
					vmBackupJob.getVm().getVmName(), vmBackupJob.getVm().getVmHostName(), vmBackupJob.getVm().getVmInstanceUUID());
			String newMsg = msg + StringUtil.enFormat(" jobPhase[%d]", backupJob.getJobPhase());
			logger.info("Wait the restart job to complete. "+newMsg);
			synchronized (backupJob.phaseLock) 
			 {
			     while( backupJob.getJobPhase() != Constants.JobExitPhase){
			    	 backupJob.phaseLock.wait();
			     }
		     }
			String newMsg2 = msg + StringUtil.enFormat(" jobPhase[%d]", backupJob.getJobPhase());
			logger.info("The restart job completes. " + newMsg2);
		} catch (Exception e) {
			logger.error(e);
		}
		finally{
			VSphereJobQueue.getInstance().removeJobFromRunningQueue(vmBackupJob);
		}
	}
	
	public int getJobPriority() {
		return context.getPriority();
	}

	public String getVMInstanceUUID(){
		return context.getExecuterInstanceUUID();
	}

}

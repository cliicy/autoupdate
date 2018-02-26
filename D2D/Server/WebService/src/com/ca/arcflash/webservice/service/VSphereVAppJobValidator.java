package com.ca.arcflash.webservice.service;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.internal.VSphereJobQueue;

public class VSphereVAppJobValidator {
	private static final Logger logger = Logger.getLogger(VSphereVAppJobValidator.class);
	private boolean isLaunchedByMasterJob;
	private VMBackupConfiguration configuration;
	private VSphereService vsphereService = VSphereService.getInstance();
	private BackupVM backupVM;
	private String runningVMNames = "";
	private String waitingVMNames = "";
	
	public enum JOB_STATUS {
		UNKNOWN, WAITING, RUNNING;
	}
	public VSphereVAppJobValidator(VMBackupConfiguration configuration, boolean launchedByMasterJob) {
		this.configuration = configuration;
		isLaunchedByMasterJob = launchedByMasterJob;
		backupVM = configuration.getBackupVM();
	}
	
	public boolean isLaunchedByMasterJob() {
		return isLaunchedByMasterJob;
	}

	public void setLaunchedByMasterJob(boolean isLaunchedByMasterJob) {
		this.isLaunchedByMasterJob = isLaunchedByMasterJob;
	}

	public VMBackupConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(VMBackupConfiguration configuration) {
		this.configuration = configuration;
	}
	
	private boolean IsVAppChildVM(BackupVM vm) {
		return StringUtil.isEmptyOrNull(vm.getGroupInstanceUUID()) ? false : true;
	}
	
	private String getChildVMNameByInstanceUUID(String instanceUUID) {
		if(backupVM.getVAppMemberVMs() != null) {
			for(BackupVM memberVM : backupVM.getVAppMemberVMs()) {
				if(0 == instanceUUID.compareToIgnoreCase(memberVM.getInstanceUUID())) {
					return memberVM.getVmName();
				}
			}
		}
		return "";
	}
	
	private HashMap<String, Integer> GetChildVMJobStatus() {
		HashMap<String, Integer> statusMap = new HashMap<String, Integer>();
		if(backupVM.getVAppMemberVMs() != null) {
			for(BackupVM memberVM : backupVM.getVAppMemberVMs()) {
				if(VSphereJobQueue.getInstance().isJobRunning(memberVM.getInstanceUUID(), String.valueOf(Constants.AF_JOBTYPE_VM_BACKUP))) {
					statusMap.put(memberVM.getInstanceUUID(), JOB_STATUS.RUNNING.ordinal());
				} else if(VSphereJobQueue.getInstance().isJobWaiting(memberVM.getInstanceUUID())) {
					statusMap.put(memberVM.getInstanceUUID(), JOB_STATUS.WAITING.ordinal());
				} else {
					statusMap.put(memberVM.getInstanceUUID(), JOB_STATUS.UNKNOWN.ordinal());
				}
			}
		}
		return statusMap;
	}
	
	public void validate() throws ServiceException {
		if(vsphereService.isVAppNode(backupVM)) {
			boolean hasWarning = false;
			HashMap<String, Integer> statusMap = GetChildVMJobStatus();
			if(!statusMap.isEmpty()) {
				for(Entry<String, Integer> entry : statusMap.entrySet()) {
					if(entry.getValue() == JOB_STATUS.RUNNING.ordinal()) {
						hasWarning = true;
						runningVMNames += getChildVMNameByInstanceUUID(entry.getKey()) + ", ";
					} else if(entry.getValue() == JOB_STATUS.WAITING.ordinal()) {
						hasWarning = true;
						waitingVMNames += getChildVMNameByInstanceUUID(entry.getKey()) + ", ";
					}
				}
			}
			if(!StringUtil.isEmptyOrNull(runningVMNames)) {
				runningVMNames.substring(0, runningVMNames.length() - 1);
				logger.info("Running job of vApp child VM names are (" + runningVMNames + ")");
			}
			if(!StringUtil.isEmptyOrNull(waitingVMNames)) {
				waitingVMNames.substring(0, waitingVMNames.length() - 1);
				logger.info("Waiting job of vApp child VM names are (" + waitingVMNames + ")");
			}
			if(hasWarning) {
				throw new ServiceException(FlashServiceErrorCode.Common_VAppChildVMJobIsRunningOrWaiting, new Object[]{backupVM.getVmName()});
			}
		}else if(IsVAppChildVM(backupVM)) {
			//The vm backup job is launched on demand
			if(!isLaunchedByMasterJob) {
				String vAppInstanceUUID = backupVM.getGroupInstanceUUID();
				if(VSphereJobQueue.getInstance().isJobRunning(vAppInstanceUUID, String.valueOf(Constants.AF_JOBTYPE_VM_BACKUP))) {
					VMBackupConfiguration vmBackupConfiguration = vsphereService.getBackupConfiguration(vAppInstanceUUID);
					String vAppName = vmBackupConfiguration.getBackupVM().getVmName();
					throw new ServiceException(FlashServiceErrorCode.Common_VAppJobIsRunning, new Object[]{vAppName, backupVM.getVmName()});
				} else if(VSphereJobQueue.getInstance().isJobRunning(backupVM.getInstanceUUID(), String.valueOf(Constants.AF_JOBTYPE_VM_BACKUP))) {
					throw vsphereService.generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
				}
			}
		} 
	}
}

package com.ca.arcflash.webservice.service;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.service.AbstractMergeService.MergeEvent;

public class VSphereBackupSetService extends BaseBackupSetService {
private static Logger logger = Logger.getLogger(VSphereBackupSetService.class);
	
	private VSphereBackupSetService() {
		
	}
	
	private static class InstanceClass {
		public static VSphereBackupSetService INSTANCE = new VSphereBackupSetService();
	}
	
	public static VSphereBackupSetService getInstance() {
		return InstanceClass.INSTANCE;
	}
	
	/**
	 * after retention policy or backup schedule changed, we may need to 
	 * change the backup set flag only for current backup set
	 * since we need to get recovery points, it's a time consuming work,
	 * so we'd call this method asynchronously
	 * @param configuration
	 */
	public synchronized void markBackupSetFlag(VMBackupConfiguration configuration) {
		logger.debug("Enter markBackupSetFlag");
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(configuration.getBackupVM().getInstanceUUID());
		
		markBackupSetFlag(configuration.getRetentionPolicy(), configuration.getBackupVM().getDestination(),
				"", configuration.getUserName(), configuration.getPassword(), vm);
		
		try {
			VSphereMergeService.getInstance().resumeVMMerge(MergeEvent.SCHEDULE_BEGIN, 
					configuration.getBackupVM().getInstanceUUID());
		}catch(Exception e) {
			logger.error("Failed to resume merge job");
		}
		
		logger.debug("End markBackupSetFlag");
	}
	
	@Override
	protected boolean existScheduledFullJob(int date, VirtualMachine vm) {
		return VSphereService.getInstance().isScheduledFullJob(date, vm.getVmInstanceUUID());
	}
	
	@Override
	protected boolean existScheduledBackup(int date, VirtualMachine vm) {
		return VSphereService.getInstance().existScheduledBackup(date, vm);
	}
	
	@Override
	protected BackupConfiguration getBackupConfiguration(String vmInstanceUUID) {
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vmInstanceUUID);
		try {
			return VSphereService.getInstance().getVMBackupConfiguration(vm);
		} catch (ServiceException e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	@Override
	protected String getDestinationFromBackupConfiguration(BackupConfiguration configuration) {
		return ((VMBackupConfiguration)configuration).getBackupVM().getDestination();
	}
	
	@Override
	protected String getUserFromBackupConfiguration(BackupConfiguration configuration) {
		return ((VMBackupConfiguration)configuration).getBackupVM().getUsername();
	}
	
	@Override
	protected String getPasswordFromBackupConfiguration(BackupConfiguration configuration) {
		return ((VMBackupConfiguration)configuration).getBackupVM().getPassword();
	}
}

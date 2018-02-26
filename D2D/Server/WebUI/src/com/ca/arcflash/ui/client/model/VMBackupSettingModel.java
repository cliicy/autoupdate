package com.ca.arcflash.ui.client.model;


public class VMBackupSettingModel extends BaseVSpherePolicyModel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BackupVMModel backupVM;
	
	public BackupVMModel getBackupVM() {
		return backupVM;
	}
	public void setBackupVM(BackupVMModel backupVM) {
		this.backupVM = backupVM;
	}
	
}

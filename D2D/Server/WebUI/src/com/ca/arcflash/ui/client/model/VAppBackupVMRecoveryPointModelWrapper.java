package com.ca.arcflash.ui.client.model;

import java.io.Serializable;


public class VAppBackupVMRecoveryPointModelWrapper implements Serializable {
	private static final long serialVersionUID = 946302477876782829L;
	private BackupVMModel backupVMModel;
	private RecoveryPointModel recoveryPointModel;
	
	public VAppBackupVMRecoveryPointModelWrapper() {
		
	}
	
	public VAppBackupVMRecoveryPointModelWrapper(BackupVMModel backupVMModel, RecoveryPointModel recoveryPointModel) {
		this.backupVMModel = backupVMModel;
		this.recoveryPointModel = recoveryPointModel;
	}
	
	public BackupVMModel getBackupVMModel() {
		return backupVMModel;
	}
	public void setBackupVMModel(BackupVMModel backupVMModel) {
		this.backupVMModel = backupVMModel;
	}
	public RecoveryPointModel getRecoveryPointModel() {
		return recoveryPointModel;
	}
	public void setRecoveryPointModel(RecoveryPointModel recoveryPointModel) {
		this.recoveryPointModel = recoveryPointModel;
	}
	
	
	
}

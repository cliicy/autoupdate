package com.ca.arcflash.webservice.jni.model;

import com.ca.arcflash.service.jni.model.JRestorePoint;

public class JVAppChildBackupVMRestorePointWrapper {
	private JBackupVM backupVM;
	private JRestorePoint restorePoint;

	public JVAppChildBackupVMRestorePointWrapper() {

	}

	public JVAppChildBackupVMRestorePointWrapper(JBackupVM backupVM, JRestorePoint restorePoint) {
		this.backupVM = backupVM;
		this.restorePoint = restorePoint;
	}

	public JBackupVM getBackupVM() {
		return backupVM;
	}

	public void setBackupVM(JBackupVM backupVM) {
		this.backupVM = backupVM;
	}

	public JRestorePoint getRestorePoint() {
		return restorePoint;
	}

	public void setRestorePoint(JRestorePoint restorePoint) {
		this.restorePoint = restorePoint;
	}
}
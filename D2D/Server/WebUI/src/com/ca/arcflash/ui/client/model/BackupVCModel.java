package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class BackupVCModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6725394155200177396L;
	
	public Boolean getIsFullMachine() {
		return (Boolean)get("isFullMachine");
	}
	public void setIsFullMachine(Boolean fullMachine) {
		set("isFullMachine", fullMachine);
	}
	
	private List<BackupVMModel> backupVMList;

	public List<BackupVMModel> getBackupVMList() {
		return backupVMList;
	}
	public void setBackupVMList(List<BackupVMModel> backupVMList) {
		this.backupVMList = backupVMList;
	}
	

}

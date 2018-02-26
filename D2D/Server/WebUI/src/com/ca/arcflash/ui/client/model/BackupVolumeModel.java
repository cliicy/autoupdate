package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class BackupVolumeModel extends BaseModelData {
	public Boolean getIsFullMachine() {
		return (Boolean)get("isFullMachine");
	}
	public void setIsFullMachine(Boolean fullMachine) {
		set("isFullMachine", fullMachine);
	}
	
	public List<String> selectedVolumesList;
	public List<String> backupSelectedVolumesList;
	public List<String> backupSelectedRefsDedupeVolumesListDetails;
	public List<String> allRefsDedupeVolumesList;
	public List<String> allRefsVolumesList;
	public List<String> allDedupeVolumesList;

}

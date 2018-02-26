package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class DestinationCapacityModel extends BaseModelData {

	private static final long serialVersionUID = -8633685121334594627L;
	
	public Long getFullBackupSize() {
		return (Long)get("fullBackupSize");
	}
	public void setFullBackupSize(Long fullBackupSize) {
		set("fullBackupSize", fullBackupSize);
	}
	public Long getIncrementalBackupSize() {
		return (Long)get("incrementalBackupSize");
	}
	public void setIncrementalBackupSize(Long incrementalBackupSize) {
		set("incrementalBackupSize", incrementalBackupSize);
	}
	public Long getResyncBackupSize() {
		return (Long)get("resyncBackupSize");
	}
	public void setResyncBackupSize(Long resyncBackupSize) {
		set("resyncBackupSize", resyncBackupSize);
	}
	public Long getTotalVolumeSize() {
		return (Long)get("totalVolumeSize");
	}
	public void setTotalVolumeSize(Long totalVolumeSize) {
		set("totalVolumeSize", totalVolumeSize);
	}
	public Long getTotalFreeSize() {
		return (Long)get("totalFreeSize");
	}
	public void setTotalFreeSize(Long totalFreeSize) {
		set("totalFreeSize", totalFreeSize);
	}
	public Long getCatalogSize() {
		return (Long)get("catalogSize");
	}
	public void setCatalogSize(Long catalogSize) {
		set("catalogSize", catalogSize);
	}
}

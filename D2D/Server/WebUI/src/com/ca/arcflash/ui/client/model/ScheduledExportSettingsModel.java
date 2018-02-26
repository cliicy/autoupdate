package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ScheduledExportSettingsModel extends BaseModelData {

	private static final long serialVersionUID = -270857906822111274L;
	
	public void setEncryptionAlgorithm(Integer b) {
		set("encryptionAlgorithm", b);
	}
	
	public Integer getEncryptionAlgorithm() {
		return (Integer)get("encryptionAlgorithm");
	}
	
	public void setEncryptionKey(String b) {
		set("encryptionKey", b);
	}
	
	public String getEncryptionKey() {
		return (String) get("encryptionKey");
	}
	
	public void setDestination(String destination) {
		set("destination", destination);
	}
	
	public String getDestination() {
		return (String) get("destination");
	}
	
	public void setDestUserName(String destUserName) {
		set("destUserName", destUserName);
	}
	
	public String getDestUserName() {
		return (String) get("destUserName");
	}

	public void setDestPassword(String destPassword) {
		set("destPassword", destPassword);
	}
	
	public String getDestPassword() {
		return (String) get("destPassword");
	}
	
	public Boolean getEnableScheduledExport() {
		return (Boolean) get("enableScheduledExport");
	}
	
	public void setEnableScheduledExport(Boolean enable) {
		set("enableScheduledExport", enable);
	}
	
	public Integer getExportInterval() {
		return (Integer) get("exportInterval");
	}
	
	public void setExportInterval(Integer interval) {
		set("exportInterval", interval);
	}
	
	public Integer getKeepRecoveryPoints() {
		return (Integer) get("keepRecoveryPoints");
	}
	
	public void setKeepRecoveryPoints(Integer keepRecoveryPoints) {
		set("keepRecoveryPoints", keepRecoveryPoints);
	}
	
	public Integer getCompressionLevel() {
		return (Integer) get("compressionLevel");
	}
	
	public void setCompressionLevel(Integer compressionLevel) {
		set("compressionLevel", compressionLevel);
	}

}

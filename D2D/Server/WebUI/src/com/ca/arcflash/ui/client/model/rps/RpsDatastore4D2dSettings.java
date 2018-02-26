package com.ca.arcflash.ui.client.model.rps;

import com.extjs.gxt.ui.client.data.BaseModel;

public class RpsDatastore4D2dSettings extends BaseModel{
	public String getDataStoreName() {
		return get("dataStoreName");
	}

	public void setDataStoreName(String dataStoreName) {
		set("dataStoreName", dataStoreName);
	}

	public String getDataStoreDisplayName() {
		return get("dataStoreDisplayName");
	}

	public void setDataStoreDisplayName(String dataStoreDisplayName) {
		set("dataStoreDisplayName", dataStoreDisplayName);
	}
	
	public Boolean isEnableCompression() {
		return get("EnableCompression", false);
	}
	
	public void setEnableCompression(Boolean enableCompression) {
		set("EnableCompression", enableCompression);
	}
	
	public Boolean isEnableEncryption() {
		return get("EnableEncryption", false);
	}
	
	public void setEnableEncryption(Boolean enableEncryption) {
		set("EnableEncryption", enableEncryption);
	}
	
	public void setEnableDeduplicate(Boolean enableDeduplicate) {
		set("EnableDeduplicate", enableDeduplicate);
	}
	
	public Boolean isEnableDeduplicate() {
		return get("EnableDeduplicate", false);
	}
	
}

package com.ca.arcflash.ui.client.model.encrypt;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class EncryptionAlgModel extends BaseModelData {
	
	public Integer getAlgType() {
		return (Integer)get("libType");
	}
	
	public void setAlgType(Integer type) {
		set("libType", type);
	}
	
	public String getName() {
		return (String)get("name");
	}
	
	public void setName(String name) {
		set("name", name);
	}
	
	public String getDisplayNameResId() {
		return (String)get("displayNameResId");
	}
	
	public void setDisplayNameResId(String displayNameResId) {
		set("displayNameResId", displayNameResId);
	}
//	public String getLibDisplayName() {
//		return (String)get("libDisplayName");
//	}
//	
//	public void setLibDisplayName(String libDisplayName) {
//		set("libDisplayName", libDisplayName);
//	}
	
}

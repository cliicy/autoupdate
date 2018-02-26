package com.ca.arcflash.ui.client.model.encrypt;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class EncryptionLibModel extends BaseModelData {
	
	public Integer getLibType() {
		return (Integer)get("libType");
	}
	
	public void setLibType(Integer type) {
		set("libType", type);
	}
	
	public String getLibName() {
		return (String)get("libName");
	}
	
	public void setLibName(String libName) {
		set("libName", libName);
	}
	
	public String getLibDisplayNameResID() {
		return (String)get("libDisplayName");
	}
	
	public void setLibDisplayNameResID(String libDisplayName) {
		set("libDisplayName", libDisplayName);
	}
	
//	public EncryptionAlgModel[] getAlgorithms() {
//		return (EncryptionAlgModel[])get("algorithms");
//	}
//	
//	public void setAlgorithms(EncryptionAlgModel[] algs) {
//		set("algorithms", algs);
//	}
	
	 public EncryptionAlgModel[] algorithms;
}

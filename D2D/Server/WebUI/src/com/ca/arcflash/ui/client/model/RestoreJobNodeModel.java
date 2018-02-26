package com.ca.arcflash.ui.client.model;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RestoreJobNodeModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8495071463872818989L;

	public Integer getSessionNumber() {
		return (Integer)get("sessionNumber");
	}
	public void setSessionNumber(Integer sessionNumber) {
		set("sessionNumber", sessionNumber);		
	}
	public String getEncryptPassword() {
		return get("encryptPassword");
	}
	
	public void setEncryptPassword(String encryptPassword) {
		set("encryptPassword", encryptPassword);
	}
	public ArrayList<com.ca.arcflash.ui.client.model.RestoreJobItemModel> listOfRestoreJobItems; 
	 	
}

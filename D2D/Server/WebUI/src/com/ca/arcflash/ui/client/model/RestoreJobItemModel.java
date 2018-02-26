package com.ca.arcflash.ui.client.model;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RestoreJobItemModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5844967948680622044L;
	
	public String getPath() {
		return (String) get("path");
	}
	public void setPath(String path) {
		set("path", path);
	}
	public Integer getSubSessionNum() {
		return (Integer) get("subSessionNum");
	}
	public void setSubSessionNum(Integer subSessionNum) {
		set("subSessionNum", subSessionNum);
	}
		
	public ArrayList<com.ca.arcflash.ui.client.model.RestoreJobItemEntryModel> listOfFiles; 
		
}

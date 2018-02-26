package com.ca.arcflash.ui.client.model;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RestoreJobItemEntryModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3440867008658067826L;

	public Integer getType() {
		return (Integer)get("type");
	}
	public void setType(Integer type) {
		set("type", type);
	}
	public String getPath() {
		return (String)get("path");
	}
	public void setPath(String path) {
		set("path", path);
	}
	
	// for Exchange GRT item
	
	public ArrayList<RestoreJobExchSubItemModel> listOfExchSubItems;
	
	public ArrayList<GridTreeNode> listOfADItems;
}

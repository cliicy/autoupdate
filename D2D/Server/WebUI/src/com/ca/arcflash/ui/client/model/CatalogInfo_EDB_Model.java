package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;


public class CatalogInfo_EDB_Model extends BaseModelData
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 897667763034185317L;
	
	public void setEdbName(String edbName){
		set("edbName", edbName);
	}
	
	public String getEdbName(){
		return (String)get("edbName");
	}
	
	public void setIsCatalogCreated(Boolean isCatalogCreated){
		set("isCatalogCreated", isCatalogCreated);
	}
	
	public Boolean getIsCatalogCreated(){
		return (Boolean)get("isCatalogCreated");
	}

}

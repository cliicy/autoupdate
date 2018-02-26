package com.ca.arcflash.ui.client.model.rps;

import com.extjs.gxt.ui.client.data.BaseModel;

public class RpsDS4FCCatalogSettings extends BaseModel{

	public void setDatastorePath(String datastorePath){
		set("DatastorePath", datastorePath);
	}
	
	public String getDatastorePath(){
		return get("DatastorePath");
	}
	
	public void setDatastoreUserName(String datastoreUserName){
		set("DatastoreUserName", datastoreUserName);
	}
	
	public String getDatastoreUserName(){
		return get("DatastoreUserName");
	}
	
	public void setDatastorePassword(String datastorePassword){
		set("DatastorePassword", datastorePassword);
	}
	
	public String getDatastorePassword(){
		return get("DatastorePassword");
	}
}

package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class DataStorePolicyModel extends BaseModelData{
	private static final long serialVersionUID = 1L;

	public void setName(String name){
		set("name",name);
	}
	public String getName(){
		return get("name");
	}

	public void setDisplayName(String name){
		set("displayname",name);
	}
	public String getDisplayName(){
		return get("displayname");
	}
	
	public void setDataStoreId(Integer dedupId){
		set("dedupId",dedupId);
	}
	public Integer getDataStoreId(){
		return get("dedupId");
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null || !(obj instanceof DataStorePolicyModel))
			return false;
		return getName().equals(((DataStorePolicyModel)obj).getName());
	}
}

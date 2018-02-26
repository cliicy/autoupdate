package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class BackupVMOriginalInfoModel extends BaseModelData {
	
	public void setOriginalVcName(String originalVcName){
		set("originalVcName",originalVcName);
	}
	
	public String getOriginalVcName(){
		return get("originalVcName");
	}
	
	public void setOriginalEsx(String originalEsx){
		set("originalEsx",originalEsx);
	}
	
	public String getOriginalEsx(){
		return get("originalEsx");
	}
	
	public void setOriginalResourcePool(String originalResourcePool){
		set("originalResourcePool",originalResourcePool);
	}
	
	public String getOriginalResourcePool(){
		return get("originalResourcePool");
	}

}

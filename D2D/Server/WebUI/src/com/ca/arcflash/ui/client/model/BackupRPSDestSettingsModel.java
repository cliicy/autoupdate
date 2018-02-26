package com.ca.arcflash.ui.client.model;

import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class BackupRPSDestSettingsModel extends BaseModelData {
	private static final long serialVersionUID = -3874512177792143450L;
	
	public RpsHostModel rpsHost;
	
	public String getRpsPolicy(){
		return (String)get("RpsPolicy");
	}

	public void setRpsPolicy(String rpsPolicy){
		set("RpsPolicy", rpsPolicy);
	}

	public String getRpsPolicyUUID(){
		return (String)get("RpsPolicyUUID");
	}

	public void setRpsPolicyUUID(String rpsPolicyuuid){
		set("RpsPolicyUUID", rpsPolicyuuid);
	}
	
	public String getRPSDataStoreName(){
		return (String)get("DataStoreName");
	}
	
	public void setRPSDataStoreName(String dataStoreName){
		set("DataStoreName", dataStoreName);
	}
	
	public String getRPSDataStoreUUID(){
		return (String)get("DataStoreUUID");
	}
	
	public void setRPSDataStoreUUID(String dataStoreUUID){
		set("DataStoreUUID", dataStoreUUID);
	}
}

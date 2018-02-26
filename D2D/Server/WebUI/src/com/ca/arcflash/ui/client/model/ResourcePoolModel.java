package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ResourcePoolModel extends BaseModelData {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void setPoolName(String resourcePoolName){
		set("poolName",resourcePoolName);
	}
	
	public String getPoolName(){
		return get("poolName");
	}
	
	public String getPoolMoref() {
		return get("poolMoref");
	}

	public void setPoolMoref(String poolMoref) {
		set("poolMoref",poolMoref);
	}

	public String getParentPoolMoref() {
		return get("parentPoolMoref");
	}

	public void setParentPoolMoref(String parentPoolMoref) {
		set("parentPoolMoref",parentPoolMoref);
	} 

}

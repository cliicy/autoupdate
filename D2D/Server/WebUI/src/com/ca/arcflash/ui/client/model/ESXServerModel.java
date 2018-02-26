package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ESXServerModel extends BaseModelData implements Comparable<ESXServerModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3913579087228187344L;

	public String getESXName(){
		return (String)get("esxName");
	}
	
	public void setESXName(String esxName){
		set("esxName",esxName);
	}
	
	public void setDcName(String dcName){
		set("dcName",dcName);
	}
	
	public String getDcName(){
		return get("dcName");
	}

	@Override
	public int compareTo(ESXServerModel o) {
		// TODO Auto-generated method stub
		return getESXName().compareTo(o.getESXName());
	}

}

package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class CloudVendorInfoModel extends BaseModelData {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3311603028365700896L;

	public CloudVendorInfoModel() {
		
	}

	public CloudVendorInfoModel(int type, String name,String url,int subVendorType) {
		setType(type);
		setName(name);	
		setUrl(url);
		setSubVendorType(subVendorType);
		
	}

	
	public int getSubVendorType() {
		return (Integer) get("subVendorType");
	}

	public void setSubVendorType(int subVendorType) {
		set("subVendorType", subVendorType);
	}
	
	public int getType() {
		return (Integer) get("type");
	}

	public void setType(int type) {
		set("type", type);
	}

	public String getUrl() {
		return get("url");
	}

	public void setUrl(String url) {
		set("url", url);
	}
	

	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}
	

}

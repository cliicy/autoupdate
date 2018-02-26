package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VirtualApplicationModel extends BaseModelData {
	private static final long serialVersionUID = -2050279923032510329L;

	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}
	
	public String getUuid() {
		return (String) get("uuid");
	}
	
	public void setUuid(String uuid) {
		set("uuid", uuid);
	}
	
	public VCloudDirectorModel getVCloudDirector() {
		return (VCloudDirectorModel) get("director");
	}
	
	public void setVCloudDirector(VCloudDirectorModel director) {
		set("director", director);
	}
}
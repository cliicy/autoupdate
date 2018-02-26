package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ApplicationModel extends BaseModelData {
	private static final long serialVersionUID = -9106803866988908880L;
	private String[] affectedMnt;
	private ApplicationComponentModel[] componentModel;
	
	public String getAppName() {
		return get("appName");
	}
	public void setAppName(String appName) {
		set("appName", appName);
	}
	public String[] getAffectedMnt() {
		return affectedMnt;
	}
	public void setAffectedMnt(String[] affectedMnt) {
		this.affectedMnt = affectedMnt;
	}
	public ApplicationComponentModel[] getComponents() {
		return componentModel;
	}
	public void setComponents(ApplicationComponentModel[] components) {
		this.componentModel = components;
	}
}

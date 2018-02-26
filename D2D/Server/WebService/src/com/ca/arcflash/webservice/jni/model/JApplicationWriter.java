package com.ca.arcflash.webservice.jni.model;

import java.util.List;

public class JApplicationWriter {

	private String appName;
	private List<String> affectedMnt;
	private List<JApplicationComponect> componentList;

	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public List<JApplicationComponect> getComponentList() {
		return componentList;
	}
	public void setComponentList(List<JApplicationComponect> componentList) {
		this.componentList = componentList;
	}
	public List<String> getAffectedMnt() {
		return affectedMnt;
	}
	public void setAffectedMnt(List<String> affectedMnt) {
		this.affectedMnt = affectedMnt;
	}
}

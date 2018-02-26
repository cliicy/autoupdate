package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;
import java.util.Map;

public class EdgeAppInfo implements Serializable {

	private static final long serialVersionUID = -8536214219090752444L;
	
	private String appHostName;
	
	private EdgeVersionInfo version;
	private Map<String, String> preferences;
	private boolean enableImportRemoteNodesFromFile = false;

	public void setVersion(EdgeVersionInfo version) {
		this.version = version;
	}

	public EdgeVersionInfo getVersion() {
		return version;
	}

	public Map<String, String> getPreferences() {
		return preferences;
	}

	public void setPreferences(Map<String, String> preferences) {
		this.preferences = preferences;
	}

	public boolean isEnableImportRemoteNodesFromFile() {
		return enableImportRemoteNodesFromFile;
	}

	public void setEnableImportRemoteNodesFromFile(boolean enableImportRemoteNodesFromFile) {
		this.enableImportRemoteNodesFromFile = enableImportRemoteNodesFromFile;
	}
	
	public String getAppHostName() {
		return appHostName;
	}

	public void setAppHostName(String appHostName) {
		this.appHostName = appHostName;
	}
	
}

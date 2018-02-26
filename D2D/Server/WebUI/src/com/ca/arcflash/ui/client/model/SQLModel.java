package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class SQLModel extends BaseModelData {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5481449536662617L;

	public SQLModel() {
	}

	public SQLModel(String instanceName, String instanceDisplayName,
			String dbName, String dbDisplayName, String newDbName,
			String newFileLoc, String path) {
		set("instanceName", instanceName);
		set("instanceDisplayName", instanceDisplayName);
		set("dbName", dbName);
		set("dbDisplayName", dbDisplayName);
		set("newDbName", newDbName);
		set("newFileLoc", newFileLoc);
		set("path", path);
	}

	public String getInstanceName() {
		return get("instanceName");
	}

	public void setInstanceName(String instanceName) {
		set("instanceName", instanceName);
	}

	public String getInstanceDisplayName() {
		return get("instanceDisplayName");
	}

	public void setInstanceDisplayName(String instanceDisplayName) {
		set("instanceDisplayName", instanceDisplayName);
	}

	public String getDbName() {
		return get("dbName");
	}

	public void setDbDisplayName(String dbDisplayName) {
		set("dbDisplayName", dbDisplayName);
	}

	public String getDbDisplayName() {
		return get("dbDisplayName");
	}

	public void setDbName(String dbName) {
		set("dbName", dbName);
	}

	public String getNewDbName() {
		return get("newDbName");
	}

	public void setNewDbName(String newDbName) {
		set("newDbName", newDbName);
	}

	public String getNewFileLoc() {
		return get("newFileLoc");
	}

	public void setNewFileLoc(String newFileLoc) {
		set("newFileLoc", newFileLoc);
	}
	
	public String getTranslatedFilePath() {
		return get("translatedNewFileLoc");
	}
	
	public void setTranslatedFilePath(String newLoc) {
		set("translatedNewFileLoc", newLoc);
	}

	public String getPath() {
		return get("path");
	}

	public void setPath(String newFileLoc) {
		set("path", newFileLoc);
	}

}

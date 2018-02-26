package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class NetworkPathModel  extends BaseModelData{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String getDriverletter() {
		return (String)get("driverletter");
	}
	public void setDriverletter(String driverletter) {
		set("driverletter", driverletter);
	}
	public String getRemotePath() {
		return (String)get("remotePath");
	}
	public void setRemotePath(String remotePath) {
		set("remotePath", remotePath);
	}
}

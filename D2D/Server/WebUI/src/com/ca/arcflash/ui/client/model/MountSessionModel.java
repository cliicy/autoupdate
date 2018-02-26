package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class MountSessionModel extends BaseModelData {
	
	public Long getSessionNum() {
		return (Long)get("sessionNum");
	}
	
	public void setSessionNum(Long sessNum) {
		set("sessionNum", sessNum);
	}

	public String getSessionPath() {
		return (String)get("sessionPath");
	}
	
	public void setSessionPath(String path) { 
		set("sessionPath", path);
	}
}

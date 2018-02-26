package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class AlternativePathModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String getAlterPath() {
		return (String)get("alterPath");
	}
	
	public void setAlterPath(String alterPath) {
		set("alterPath", alterPath);		
	}
	
	public void setMaxPathLength(Long maxPathLen) {
		set("maxPathLength", maxPathLen);	
	}
	
	public Long getMaxPathLength() {
		return (Long) get("maxPathLength");
	}
}

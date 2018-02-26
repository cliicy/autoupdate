package com.ca.arcflash.ui.client.homepage;

import com.extjs.gxt.ui.client.data.BaseModel;

public class DiagLogModel extends BaseModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String logCollectionDesciptionText;
	public String id;
	
	public DiagLogModel(String logCollectDescriptionText, String id) {
		setLogCollectionDesciptionText(logCollectDescriptionText);
		setId(id);
		// TODO Auto-generated constructor stub
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	public String getLogCollectionDesciptionText() {
		return logCollectionDesciptionText;
	}

	public void setLogCollectionDesciptionText(
			String logCollectionDesciptionText) {
		this.logCollectionDesciptionText = logCollectionDesciptionText;
	}

	
}

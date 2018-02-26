package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class EdgeInfoModel extends BaseModelData {
	
	public String getEdgeHostName() {
		return get("edgeHostName");
	}

	public void setEdgeHostName(String edgeHostName) {
		set("edgeHostName",edgeHostName);
	}

	public String getEdgeUrl() {
		return get("edgeUrl");
	}

	public void setEdgeUrl(String edgeUrl) {
		set("edgeUrl",edgeUrl);
	}

}

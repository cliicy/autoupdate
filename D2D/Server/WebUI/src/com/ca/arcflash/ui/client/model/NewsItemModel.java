package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class NewsItemModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5247856545639059625L;

	public String getTitle() {
		return get("title");
	}
	public void setTitle(String title) {
		set("title", title);
	}
	public String getLink() {
		return get("link");
	}
	public void setLink(String link) {
		set("link", link);
	}
	public String getDescription() {
		return get("description");
	}
	public void setDescription(String description) {
		set("description", description);
	}
}

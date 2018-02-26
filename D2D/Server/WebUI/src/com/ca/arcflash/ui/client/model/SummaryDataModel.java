package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class SummaryDataModel extends BaseModelData {

	private static final long serialVersionUID = 2220528053668922638L;

	public SummaryDataModel(String name, String path, Long size) {
		super();
		setName(name);
		setPath(path);
		setSize(size);
	}

	public String getName() {
		return (String) get("name");
	}

	public void setName(String name) {
		set("name", name);
	}

	public String getPath() {
		return (String) get("path");
	}

	public void setPath(String path) {
		set("path", path);
	}

	public Long getSize() {
		return (Long) get("size");
	}

	public void setSize(Long size) {
		set("size", size);
	}

}

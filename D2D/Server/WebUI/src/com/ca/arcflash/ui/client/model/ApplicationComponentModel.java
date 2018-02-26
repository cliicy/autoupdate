package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ApplicationComponentModel extends BaseModelData {
	private static final long serialVersionUID = 9125985599738286551L;
	private String[] affectedMnt;
	private String[] fileList;
	public String getName() {
		return (String)get("name");
	}
	public void setName(String name) {
		set("name", name);
	}
	public String[] getAffectedMnt() {
		return affectedMnt;
	}
	public void setAffectedMnt(String[] affectedMnt) {
		this.affectedMnt = affectedMnt;
	}
	public String[] getFileList() {
		return fileList;
	}
	public void setFileList(String[] fileList) {
		this.fileList = fileList;
	}
}

package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class FileSystemOptionModel extends BaseModelData {
	
	private static final long serialVersionUID = -4702436246123699594L;
	
	public Boolean isOverwriteExistingFiles() {
		return get("overwriteExistingFiles");
	}
	public void setOverwriteExistingFiles(Boolean overwriteExistingFiles) {
		set("overwriteExistingFiles", overwriteExistingFiles);
	}
	public Boolean isReplaceActiveFiles() {
		return get("replaceActiveFiles");
	}
	public void setReplaceActiveFiles(Boolean replaceActiveFiles) {
		set("replaceActiveFiles", replaceActiveFiles);
	}
	public Boolean isCreateBaseFolder() {
		return get("createBaseFolder");
	}
	public void setCreateBaseFolder(Boolean createBaseFolder) {
		set("createBaseFolder",createBaseFolder);
	}
	public Boolean isRename() {
		return get("rename");
	}
	public void setRename(Boolean rename) {
		set("rename",rename);
	}
}

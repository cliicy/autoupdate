package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ArchiveDiskDestInfoModel extends BaseModelData {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4879476137602534889L;
	public String getArchiveDiskDestPath(){
		return get("archiveDiskDestPath");
	}
	public void setArchiveDiskDestPath(String diskDestInfoDestPath){
		set("archiveDiskDestPath",diskDestInfoDestPath);
	}
	public String getArchiveDiskUserName(){
		return get("archiveDiskUserName");
	}
	public void setArchiveDiskUserName(String diskDestUserName){
		set("archiveDiskUserName",diskDestUserName);
	}
	public String getArchiveDiskPassword(){
		return get("archiveDiskPassword");
	}
	public void setArchiveDiskPassword(String diskDestPassword){
		set("archiveDiskPassword",diskDestPassword);
	}
}

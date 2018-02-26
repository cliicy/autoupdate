package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class FileModel extends BaseModelData
{
	private static final long serialVersionUID = 6820823673644365960L;
	
	public String getName()
	{
		return (String)get("name");
	}
	public void setName(String name)
	{
		set("name", name);
	}
	
	public String getPath()
	{
		return (String)get("path");
	}
	public void setPath(String name)
	{
		set("path", name);
	}
	
	public Boolean isVolume() {
		return (Boolean)get("isVolume");
	}
	
	public void setIsVolume(Boolean isVolume) {
		set("isVolume", isVolume);
	}	
	
	public Boolean isNetworkPath() {
		return (Boolean)get("isNetworkPath");
	}
	
	public void setIsNetworkPath(Boolean isNetworkPath) {
		set("isNetworkPath", isNetworkPath);
	}
	
	public String getNetworkPath() {
		return (String)get("networkPath");
	}
	
	public void setNetworkPath(String networkPath) {
		set("networkPath", networkPath);
	}
	
	public String getUserName() {
		return (String)get("userName");
	}
	
	public void setUserName(String userName) {
		set("userName", userName);
	}
	
	public String getPassword() {
		return (String)get("password");
	}
	
	public void setPassword(String password) {
		set("password", password);
	}
	
	public Integer getType() {
		return (Integer) get("type");
	}

	public void setType(Integer type) {
		set("type", type);
	}	
	
	public String getCreateFolderPath() {
		return (String)get("createFolderPath");
	}
	
	public void setCreateFolder(String createFolderPath) {
		set("createFolderPath", createFolderPath);
	}
	
	public Integer getIsShow() {
		return (Integer)get("isShow");
	}
	public void setIsShow(Integer isShow) {
		set("isShow", isShow);
	}
	
	public String getIsDeduped() {
		return (String)get("isDedupe");
	}
	public void setIsdeduped(String isDedupe) {
		set("isDedupe", isDedupe);
	}
}

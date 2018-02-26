package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ArchiveCatalogItemModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4193851475707558337L;
	
	private ArchiveFileVersionNode[] fileVersionsList;
	
	public ArchiveFileVersionNode[] getfileVersionsList()
	{
		return fileVersionsList;
	}
	
	public void setfileVersionsList(ArchiveFileVersionNode[] in_fileVersionsList)
	{
		this.fileVersionsList = in_fileVersionsList;
	}
	
	public String getPath() {
		return (String) get("path");		
	}

	public void setPath(String path) {
		set("path", path);		
	}

	public Long getVolumeHandle() {
		return (Long) get("VolumeHandle");
	}

	public void setVolumeHandle(Long in_VolumeHandle) {
		set("VolumeHandle", in_VolumeHandle);
	}

	public Integer getType() {
		return (Integer) get("type");
	}

	public void setType(Integer type) {
		set("type", type);
	}
	
	public Integer getArchiveType() {
		return (Integer) get("ArchiveType");
	}

	public void setArchiveType(Integer in_ArchiveType) {
		set("ArchiveType", in_ArchiveType);
	}
	
	public Integer getVersionsCount() {
		return (Integer) get("VersionsCount");
	}

	public void setVersionsCount(Integer in_VersionsCount) {
		set("VersionsCount", in_VersionsCount);
	}

	public Long getSize() {
		return (Long) get("size");
	}

	public void setSize(Long size) {
		set("size", size);
	}

	public Date getDate() {
		return (Date) get("date");
	}

	public void setDate(Date date) {
		set("date", date);
	}

	public String getName() {
		return (String)get("name");
	}

	public void setName(String name) {
		set("name",name);
	}
	
	public String getFullPath()
	{
		return (String) get("fullPath");
	}
	public void setFullPath(String fullPath)
	{
		set("fullPath", fullPath);
	}
	
	public Boolean getChecked()
	{
		return (Boolean) get("checked");		
	}
	public void setChecked(Boolean checked)
	{
		set("checked", checked);
	}

	public Long getChildrenCount() {
		return (Long) get("childrenCount");
	}

	public void setChildrenCount(Long childrenCount) {
		set("childrenCount", childrenCount);
	}
}

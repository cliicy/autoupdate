package com.ca.arcflash.webservice.jni.model;

import java.util.List;

public class JArchiveCatalogDetail {
	private String name;
	private long VolumeHandle;
	private String VersionsCount;
	private String fullPath;
	private String path;
	private long childrenCount;
	private int type;
	
	private List<JArchiveFileVersionDetail> fileVersionsList;
	
    public void setArchiveCatalogDetails(String in_name, long in_VolumeHandle, String in_VersionsCount,String in_fullPath, long in_childrenCount, int in_type)
    {
    	this.name = in_name;
    	this.VolumeHandle = in_VolumeHandle;
    	this.VersionsCount = in_VersionsCount;
    	this.path = in_fullPath;
    	this.childrenCount = in_childrenCount;
    	this.type = in_type;
    	return;
    }
	
    public void setFileVersionsList(List<JArchiveFileVersionDetail> in_FileVersionsList)
    {
    	this.fileVersionsList = in_FileVersionsList;
    }
    
    public List<JArchiveFileVersionDetail> getfileVersionsList()
    {
    	return fileVersionsList;
    }
    
    public void setfileVersionsList(List<JArchiveFileVersionDetail> in_fileVersionsList)
    {
    	this.fileVersionsList = in_fileVersionsList;
    }
    
	public String getPath() {
		return path;		
	}

	public void setPath(String path) {
		this.path = path;		
	}

	public Long getVolumeHandle() {
		return VolumeHandle;
	}

	public void setVolumeHandle(Long in_VolumeHandle) {
		this.VolumeHandle = in_VolumeHandle;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public String getVersionsCount() {
		return VersionsCount;
	}

	public void setVersionsCount(String in_VersionsCount) {
		this.VersionsCount = in_VersionsCount;
	}

/*	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}*/

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getFullPath()
	{
		return fullPath;
	}
	public void setFullPath(String in_fullPath)
	{
		this.fullPath = in_fullPath;
	}

	public Long getChildrenCount() {
		return childrenCount;
	}

	public void setChildrenCount(Long in_childrenCount) {
		this.childrenCount = in_childrenCount;
	}
}

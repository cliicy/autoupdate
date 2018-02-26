package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class HyperVHostStorageModel extends BaseModelData
{
	private static final long serialVersionUID = -112018309764978500L;
	// network adapter info
	private String tag_drive = "drive";
	private String tag_freeSize = "freesize";
	private String tag_totalSize = "totalsize";
	private String tag_path = "path";
	
	public String getDrive()
	{
		return get(tag_drive);
	}
	public void setDrive(String drive)
	{
		set(tag_drive, drive);
	}
	
	public long getFreeSize()
	{
		return (Long)get(tag_freeSize);
	}
	public void setFreeSize(Long freeSize)
	{
		set(tag_freeSize, freeSize);
	}
	
	public long getTotalSize()
	{
		return (Long)get(tag_totalSize);
	}
	public void setTotalSize(Long totalSize)
	{
		set(tag_totalSize, totalSize);
	}
	public String getPath() {
		return (String)get(tag_path);
	}
	public void setPath(String path) {
		set(tag_path, path);
	}
}

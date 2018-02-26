package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RestoreJobArchiveItemNodeModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1165764361912157325L;

	public Long getVolumeHandle()
	{
		return (Long)get("VolumeHandle");
	}
	public void setVolumeHandle(Long in_VolumeHandle)
	{
		set("VolumeHandle", in_VolumeHandle);
	}
	
	public String getVolumeName()
	{
		return get("VolumeName");
	}
	public void setVolumeName(String in_VolumeName)
	{
		set("VolumeName", in_VolumeName);
	}
	
	public Long getSize()
	{
		return (Long)get("size");
	}
	
	public void setSize(Long size)
	{
		set("size", size);
	}
	
	public Date getDate()
	{
		return (Date)get("date");			
	}
	public void setDate(Date date)
	{
		set("date", date);
	}
	//
	public String getFullPath()
	{
		return (String)get("FullPath");		
	}
	public void setFullPath(String path)
	{
		set("FullPath", path);
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

	public Integer getVersion() {
		return (Integer) get("Version");
	}

	public void setVersion(Integer in_Version) {
		set("Version", in_Version);
	}
	
}

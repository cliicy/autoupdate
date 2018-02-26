package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ArchiveGridTreeNode extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3400243003093538456L;
	
	public ArchiveGridTreeNode()
	{
		set("SelectedVersion", -1);
		set("checked", false);
		set("VersionsCount", 0);
	}
	
	private ArchiveFileVersionNode[] fileVersionsList;
	
	public ArchiveFileVersionNode[] getfileVersionsList()
	{
		return fileVersionsList;
	}
	
	public void setfileVersionsList(ArchiveFileVersionNode[] in_fileVersionsList)
	{
		this.fileVersionsList = in_fileVersionsList;
	}
	
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
	
	public String getGuid()
	{
         return get("GUID");
	}
  	public void setGuid(String guid)
  	{
          set("GUID",guid);
  	}

	public Boolean getChecked()
	{
		return (Boolean) get("checked");
	}
	public void setChecked(Boolean check)
	{
		set("checked", check);
	}
	
	public String getName()
	{
		return (String)get("name");
	}
	public void setName(String name)
	{
		set("name", name);
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
	public String getCatalogFilePath()
	{
		return (String)get("catalogFilePath");		
	}
	public void setCatalogFilePath(String path)
	{
		set("catalogFilePath", path);
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

	public Integer getSelectedVersion() {
		return (Integer)get("SelectedVersion");
	}

	public void setSelectedVersion(Integer iSelectedVersion) {
		set("SelectedVersion", iSelectedVersion);
	}
	
	public String getPath()
	{
		return (String) get("path");
	}
	public void setPath(String path)
	{
		set("path", path);
	}
	
	public String getFullPath()
	{
		return (String) get("fullPath");
	}
	public void setFullPath(String fullPath)
	{
		set("fullPath", fullPath);
	}
	
	public Boolean getSelectable()
	{
		return (Boolean) get("selectable");
	}
	public void setSelectable(Boolean selectable)
	{
		set("selectable", selectable);
	}
	
	public void setDisplayName(String in_DisplayName) {
		set("displayName", in_DisplayName);
	}
	public String getDisplayName()
	{
		return get("displayName");
	}

	public void setUserChecked(Boolean value) {
		set("userChecked", value);
	}

	public Boolean isUserChecked() {
		return get("userChecked");
	}

	public Long getChildrenCount() {
		return (Long) get("childrenCount");
	}

	public void setChildrenCount(Long childrenCount) {
		set("childrenCount", childrenCount);
	}
	
	public Long getfOptions() {
		return (Long) get("fOptions");
	}

	public void setfOptions(Long in_fOptions) {
		set("fOptions", in_fOptions);
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (obj instanceof ArchiveGridTreeNode) {
			boolean isEqual = this.toId().equalsIgnoreCase(((ArchiveGridTreeNode) obj).toId());
			return isEqual;
		} else {
			return false;
		}
	}

	public String toId() {
		String id = "";
		
		if (this.getVolumeName() != null) {
			id += "@" + this.getVolumeName()+":\\";
		}		
		
		if (this.getCatalogFilePath() != null) {
			id += "@" + this.getCatalogFilePath();
		}	
		
		if (this.getGuid() != null) {
			id += "@" + this.getGuid();
		}		

		return id.toString();
	}

	private Integer id = null;

	public void setId(Integer id) {
		this.id = id;
	}

	private int getId() {
		if (id == null) {
			id = this.toId().hashCode();
		}
		return id;
	}
}

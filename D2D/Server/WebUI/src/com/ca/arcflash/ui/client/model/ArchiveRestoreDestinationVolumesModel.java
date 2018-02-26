package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ArchiveRestoreDestinationVolumesModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1096993360285210016L;

	public String getDisplayName() {
		return (String)get("displayName");
	}
	public void setDisplayName(String displayName) {
		set("displayName", displayName);
	}
	
	public Long getvolumeHandle() {
		return (Long) get("volumeHandle");
	}

	public void setvolumeHandle(Long volumeHandle) {
		set("volumeHandle", volumeHandle);
	}
	
	public String getGuid() {
		return (String)get("Guid");
	}
	public void setGuid(String guid) {
		set("Guid", guid);
	}
	
	public String getCatalogFilePath() {
		return get("catalogFilePath");
	}
	public void setCatalogFilePath(String catalogFilePath) {
		set("catalogFilePath", catalogFilePath);
	}
	public String getType() {
		return get("type");
	}
	public void setType(String type) {
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
	public Long getVolDataSize() {
		return get("volDataSize");
	}
	public void setVolDataSize(Long volDataSize) {
		set("volDataSize", volDataSize);
	}

	public Long getChildrenCount() {
		return (Long) get("childrenCount");
	}

	public void setChildrenCount(Long childrenCount) {
		set("childrenCount", childrenCount);
	}
}

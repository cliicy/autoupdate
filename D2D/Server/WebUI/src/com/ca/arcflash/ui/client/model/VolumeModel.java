package com.ca.arcflash.ui.client.model;

public class VolumeModel  extends FileModel{

	public String getDisplayName() {
		return (String)get("displayName");
	}
	public void setDisplayName(String displayName) {
		set("displayName", displayName);
	}
	
	public Integer getType() {
		return (Integer)get("type");
	}
	public void setType(Integer type) {
		set("type", type);
	}
	public Long getTotalSize() {
		return (Long)get("totalSize");
	}
	
	public void setTotalSize(Long freeSize) {
		set("totalSize",freeSize);
	}
	public Long getFreeSize() {
		return (Long)get("freeSize");
	}
	
	public void setFreeSize(Long freeSize) {
		set("freeSize",freeSize);
	}
	
	public Integer getSelectedState() {
		return (Integer)get("selectedState");
	}
	public void setSelectedState(Integer selectedState) {
		set("selectedState", selectedState);
	}
	public String getGUID() {
		return (String)get("GUID");
	}
	public void setGUID(String GUID) {
		set("GUID", GUID);
	}
	public Integer getLayout() {
		return (Integer)get("layout");
	}
	public void setLayout(Integer layout) {
		set("layout", layout);
	}
	public Integer getFileSysType() {
		return (Integer)get("fileSysType");
	}
	public void setFileSysType(Integer fileSysType) {
		set("fileSysType", fileSysType);
	}
	public Integer getStatus() {
		return (Integer)get("status");
	}
	public void setStatus(Integer status) {
		set("status", status);
	}
	public Integer getSubStatus() {
		return (Integer)get("subStatus");
	}
	public void setSubStatus(Integer subStatus) {
		set("subStatus", subStatus);
	}
	public Integer getIsShow() {
		return (Integer)get("isShow");
	}
	public void setIsShow(Integer isShow) {
		set("isShow", isShow);
	}
	public Integer getMsgID() {
		return (Integer)get("msgID");
	}
	public void setMsgID(Integer msgID) {
		set("msgID", msgID);
	}
	
	public Boolean getIsEmpty() {
		return (Boolean)get("isEmpty");
	}
	public void setIsEmpty(Boolean isEmpty) {
		set("isEmpty", isEmpty);
	}
	public String getDataStore() {
		return (String)get("DataStore");
	}
	public void setDataStore(String datastore) {
		set("DataStore", datastore);
	}
}

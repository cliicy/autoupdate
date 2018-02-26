package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RecoveryPointItemModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1425838944643620177L;

	public String getDisplayName() {
		return (String)get("displayName");
	}
	public void setDisplayName(String displayName) {
		set("displayName", displayName);
	}
	public String getGuid() {
		return (String)get("Guid");
	}
	public void setGuid(String guid) {
		set("Guid", guid);
	}
	public Long getSubSessionID() {
		return (Long)get("subSessionID");
	}
	public void setSubSessionID(Long subSessionID) {
		set("subSessionID", subSessionID);
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
	public Long getVolDataSizeB() {
		return get("volDataSizeB");
	}
	public void setVolDataSizeB(Long volDataSizeB) {
		set("volDataSizeB", volDataSizeB);
	}

	public Long getChildrenCount() {
		return (Long) get("childrenCount");
	}

	public void setChildrenCount(Long childrenCount) {
		set("childrenCount", childrenCount);
	}
	public void setVolAttr(Integer volAttr) {
		set("volAttr", volAttr);		
	}
	public Integer getVolAttr() {
		return (Integer)get("volAttr");
	}
	
	public Boolean isHasDriverLetter(){
		return get("hasDriverLetter");
	}
	
	public void setHasDriverLetter(Boolean hasDriverLetter){
		set("hasDriverLetter",hasDriverLetter);
	}
	public Boolean isHasReplicaDB(){
		return get("hasReplicaDB");
	}
	
	public void setHasReplicaDB(Boolean hasReplicaDB){
		set("hasReplicaDB",hasReplicaDB);
	}
}

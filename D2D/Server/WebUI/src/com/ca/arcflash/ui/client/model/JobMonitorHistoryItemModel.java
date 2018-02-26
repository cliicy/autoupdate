package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class JobMonitorHistoryItemModel extends BaseModelData{

	private static final long serialVersionUID = -733174386188043232L;
	
	public Long getSessionID() {
		return (Long)get("SessionID");
	}
	public void setSessionID(Long sessionID) {
		set("SessionID", sessionID);
	}
	public Long getReadSpeed(){
		return (Long)get("readSpeed");
	}
	public void setReadSpeed(Long readSpeed){
		set("readSpeed", readSpeed);
	}
	public Long getWriteSpeed(){
		return (Long)get("writeSpeed");
	}
	public void setWriteSpeed(Long writeSpeed){
		set("writeSpeed", writeSpeed);
	}

}

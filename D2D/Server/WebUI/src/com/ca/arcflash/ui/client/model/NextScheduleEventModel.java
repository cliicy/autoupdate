package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class NextScheduleEventModel extends BaseModelData{

	private static final long serialVersionUID = -6422413693921054611L;
	
	public Date getDate() {
		return get("date");
	}
	public void setDate(Date date) {
		set("date",date);
	}
	public Integer getBackupType() {
		return get("backupType");
	}
	public void setBackupType(Integer backupType) {
		set("backupType", backupType);
	}
	
	public String getarchiveEvent() {
		return get("archiveEvent");
	}
	public void setarchiveEvent(String in_archiveEvent) {
		set("archiveEvent", in_archiveEvent);
	}
	
	public Long getServerTimeZoneOffset() {
		return (Long)get("ServerTZOffset");
	}
	
	public void setServerTimeZoneOffset(Long offset) {
		set("ServerTZOffset", offset);
	}
}

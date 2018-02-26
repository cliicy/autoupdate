package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ProtectionInformationModel extends BaseModelData {

	private static final long serialVersionUID = -8858968283627783823L;
	private BackupSettingsScheduleModel schedule;
	
	public Integer getBackupType() {
		return (Integer)get("backupType");
	}
	public void setBackupType(Integer backupType) {
		set("backupType",backupType);
	}
	public Integer getCount() {
		return (Integer)get("count");
	}
	public void setCount(Integer count) {
		set("count",count);
	}
	public Long getTotalLogicalSize() {
	    return (Long)get("totalLogicalSize");
	}
	public void setTotalLogicalSize(Long totalLogicalSize) {
		set("totalLogicalSize",totalLogicalSize);
	}
	public Long getSize() {
		return (Long)get("size");
	}
	public void setSize(Long size) {
		set("size",size);
	}
	public Boolean isDedupe() {
		return (Boolean)get("isDedupe");
	}
	public void setDedupe(boolean isDedupe) {
		set("isDedupe", isDedupe);
	}
	public BackupSettingsScheduleModel getSchedule() {
		return schedule;
	}
	public void setSchedule(BackupSettingsScheduleModel schedule) {
		this.schedule = schedule;
	}
	public Date getNextRunTime() {
		return get("nextRunTime");
	}
	public void setNextRunTime(Date nextRunTime) {
		set("nextRunTime",nextRunTime);
	}
	public String getLastBackupTime() {
		return get("lastBackupTime");
	}
	public void setLastBackupTime(String lastBackupTime) {
		set("lastBackupTime",lastBackupTime);
	}

	public Long getNextTimeZoneOffset() {
		return (Long)get("ServerTimeZoneOffset");
	}
	
	public void setNextTimeZoneOffset(Long offset) {
		set("ServerTimeZoneOffset", offset);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ProtectionInformationModel))
			return false;
		
		return ((ProtectionInformationModel)obj).getBackupType() == getBackupType();
	}
	
}

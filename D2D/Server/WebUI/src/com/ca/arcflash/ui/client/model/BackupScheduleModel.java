package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class BackupScheduleModel extends BaseModelData{

	private static final long serialVersionUID = -6901964354365970528L;
	
	public Integer getInterval() {
		return (Integer)get("interval");
	}

	public void setInterval(Integer interval) {
		set("interval", interval);
	}

	public Integer getIntervalUnit() {
		return (Integer)get("intervalUnit");
	}

	public void setIntervalUnit(Integer intervalUnit) {
		set("intervalUnit", intervalUnit);
	}

	public Boolean isEnabled() {
		return (Boolean)get("enabled");
	}

	public void setEnabled(Boolean enabled) {
		set("enabled", enabled);
	}
}

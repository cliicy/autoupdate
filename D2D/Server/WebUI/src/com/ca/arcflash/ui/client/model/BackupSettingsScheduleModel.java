package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class BackupSettingsScheduleModel extends BaseModelData{

	private boolean enabled;
	private int interval;
	private int intervalUnit;
	
	public int getInterval() {
		return interval;
	}

	public void setInterval(Integer i) {
		interval = i;
	}

	public int getIntervalUnit() {
		return intervalUnit;
	}

	public void setIntervalUnit(Integer i) {
		intervalUnit = i;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
}

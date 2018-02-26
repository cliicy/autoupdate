package com.ca.arcflash.ui.client.backup.schedule;

import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class EveryDayScheduleModel extends BaseModelData{	
	private static final long serialVersionUID = -5113678616373521000L;
	private boolean generateCatalog;
	private int retentionCount;
	private DayTimeModel dayTime;
	private int bkpType;
	private boolean enabled;
	private boolean checkRecoveryPoint;
	
	private Boolean[] dayEnabled = new Boolean[]{true,true,true,true,true,true,true};
	
	public boolean isGenerateCatalog() {
		return generateCatalog;
	}
	public void setGenerateCatalog(boolean generateCatalog) {
		this.generateCatalog = generateCatalog;
	}
	public int getRetentionCount() {
		return retentionCount;
	}
	public void setRetentionCount(int retentionCount) {
		this.retentionCount = retentionCount;
	}
	public DayTimeModel getDayTime() {
		return dayTime;
	}
	public void setDayTime(DayTimeModel dayTime) {
		this.dayTime = dayTime;
	}
	public int getBkpType() {
		return bkpType;
	}
	public void setBkpType(int bkpType) {
		this.bkpType = bkpType;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public Boolean[] getDayEnabled() {
		return dayEnabled;
	}
	public void setDayEnabled(Boolean[] dayEnabled) {
		this.dayEnabled = dayEnabled;
	}
	public boolean isCheckRecoveryPoint() {
		return checkRecoveryPoint;
	}
	public void setCheckRecoveryPoint(boolean checkRecoveryPoint) {
		this.checkRecoveryPoint = checkRecoveryPoint;
	}
}

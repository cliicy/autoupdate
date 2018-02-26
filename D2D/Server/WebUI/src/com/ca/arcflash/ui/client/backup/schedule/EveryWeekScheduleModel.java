package com.ca.arcflash.ui.client.backup.schedule;

import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class EveryWeekScheduleModel extends BaseModelData{
	private static final long serialVersionUID = 2913400017141552041L;
	private boolean enabled;
	private int bkpType;
	private int dayOfWeek;
	private int retentionCount;
	private boolean generateCatalog;
	private DayTimeModel dayTime;
	private boolean checkRecoveryPoint;
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public int getBkpType() {
		return bkpType;
	}
	public void setBkpType(int bkpType) {
		this.bkpType = bkpType;
	}
	public int getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public int getRetentionCount() {
		return retentionCount;
	}
	public void setRetentionCount(int retentionCount) {
		this.retentionCount = retentionCount;
	}
	public boolean isGenerateCatalog() {
		return generateCatalog;
	}
	public void setGenerateCatalog(boolean generateCatalog) {
		this.generateCatalog = generateCatalog;
	}
	public DayTimeModel getDayTime() {
		return dayTime;
	}
	public void setDayTime(DayTimeModel dayTime) {
		this.dayTime = dayTime;
	}
	public boolean isCheckRecoveryPoint() {
		return checkRecoveryPoint;
	}
	public void setCheckRecoveryPoint(boolean checkRecoveryPoint) {
		this.checkRecoveryPoint = checkRecoveryPoint;
	}
}

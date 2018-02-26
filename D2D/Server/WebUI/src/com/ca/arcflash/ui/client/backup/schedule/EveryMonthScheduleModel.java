package com.ca.arcflash.ui.client.backup.schedule;

import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class EveryMonthScheduleModel extends BaseModelData{
	private static final long serialVersionUID = -5836021612266973290L;
	private int weekDayOfMonth;
	private int weekNumOfMonth;
	private int dayOfMonth;
	private int retentionCount;
	private boolean dayOfMonthEnabled;
	private boolean generateCatalog;
	private boolean weekOfMonthEnabled;
	private int bkpType;
	private boolean enabled;
	private DayTimeModel dayTime;
	private boolean checkRecoveryPoint;

	public int getDayOfMonth() {
		return dayOfMonth;
	}
	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	public int getRetentionCount() {
		return retentionCount;
	}
	public void setRetentionCount(int retentionCount) {
		this.retentionCount = retentionCount;
	}
	public boolean isDayOfMonthEnabled() {
		return dayOfMonthEnabled;
	}
	public void setDayOfMonthEnabled(boolean dayOfMonthEnabled) {
		this.dayOfMonthEnabled = dayOfMonthEnabled;
	}
	public boolean isGenerateCatalog() {
		return generateCatalog;
	}
	public void setGenerateCatalog(boolean generateCatalog) {
		this.generateCatalog = generateCatalog;
	}
	public boolean isWeekOfMonthEnabled() {
		return weekOfMonthEnabled;
	}
	public void setWeekOfMonthEnabled(boolean weekOfMonthEnabled) {
		this.weekOfMonthEnabled = weekOfMonthEnabled;
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
	public DayTimeModel getDayTime() {
		return dayTime;
	}
	public void setDayTime(DayTimeModel dayTime) {
		this.dayTime = dayTime;
	}
	public int getWeekDayOfMonth() {
		return weekDayOfMonth;
	}
	public void setWeekDayOfMonth(int weekDayOfMonth) {
		this.weekDayOfMonth = weekDayOfMonth;
	}
	public int getWeekNumOfMonth() {
		return weekNumOfMonth;
	}
	public void setWeekNumOfMonth(int weekNumOfMonth) {
		this.weekNumOfMonth = weekNumOfMonth;
	}
	public boolean isCheckRecoveryPoint() {
		return checkRecoveryPoint;
	}
	public void setCheckRecoveryPoint(boolean checkRecoveryPoint) {
		this.checkRecoveryPoint = checkRecoveryPoint;
	}
}

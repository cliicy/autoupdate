package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RetentionModel extends BaseModelData {
	private static final long serialVersionUID = -5449173268064913704L;
	
	public DayTimeModel dailyBackupTime;
	
	public RetentionModel (){
		dailyBackupTime = new DayTimeModel ();
		dailyBackupTime.setHour(8);
		dailyBackupTime.setMinute(30);
		setDailyUseLastBackup (true);
		setWeeklyBackupTime (6);
		setMonthlyUseLastBackup (true);
		setMonthlyBackupTime (6);
	}
	
//	public Integer getRentionCount() {
//		return (Integer)get("RetentionCount");
//	}
//	
//	public void setRetentionCount(Integer count) {
//		set("RetentionCount", count);
//	}
	
	public Boolean isDailyUseLastBackup() {
		return (Boolean)get("DailyLastBackup");
	}
	
	public void setDailyUseLastBackup(Boolean use) {
		set("DailyLastBackup", use);
	}
	
//	public Integer getDailyCount() {
//		return (Integer)get("DayCount");
//	}
//	
//	public void setDailyCount(Integer count) {
//		set("DayCount", count);
//	}
//	
//	public Integer getWeeklyCount() {
//		return (Integer)get("WeekCount");
//	}
//	
//	public void setWeeklyCount(Integer count) {
//		set("WeekCount", count);
//	}
//	
//	public Integer getMontlyCount() {
//		return (Integer)get("MonthlyCount");
//	}
//	
//	public void setMonthlyCount(Integer count) {
//		set("MonthlyCount", count);
//	}
	
	public Integer getWeekBackupTime() {
		return (Integer)get("WeeklyBackup");
	}
	
	public void setWeeklyBackupTime(Integer weekDay) {
		set("WeeklyBackup", weekDay);
	}
	
	public Boolean isMonthlyUseLastBackup() {
		return (Boolean)get("MonthlyUseLastBackup");
	}
	
	public void setMonthlyUseLastBackup(Boolean use) {
		set("MonthlyUseLastBackup", use);
	}
	
	public Integer getMonthlyBackupTime() {
		return (Integer)get("MonthlyBackup");
	}
	
	public void setMonthlyBackupTime(Integer weekDay) {
		set("MonthlyBackup", weekDay);
	}
	
//	public Boolean isUseAdvancedRetention() {
//		return (Boolean)get("useAdvancedRetention");
//	}
//	
//	public void setUseAdvancedRetention(Boolean use) {
//		set("useAdvancedRetention", use);
//	}
}

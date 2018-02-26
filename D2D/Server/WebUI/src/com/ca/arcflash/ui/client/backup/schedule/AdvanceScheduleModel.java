package com.ca.arcflash.ui.client.backup.schedule;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class AdvanceScheduleModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1476927985037273762L;
	public List<DailyScheduleDetailItemModel> daylyScheduleDetailItemModel;
	public PeriodScheduleModel periodScheduleModel;
	
	public Long getBackupStartTime()
	{
		return (Long)get("BackupStartTime");
	}
	public void setBackupStartTime(Long backupStartTime)
	{
		set("BackupStartTime", backupStartTime);
	}
//	public Boolean getIsEnableSchedule(){
//		return (Boolean)get("enableSchedule");
//	}
//	public void setIsEnableBackup(Boolean isEnableSchedule){
//		set("enableSchedule", isEnableSchedule);
//	}
}

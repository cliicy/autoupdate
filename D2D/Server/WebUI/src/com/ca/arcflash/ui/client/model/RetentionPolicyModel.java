package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RetentionPolicyModel extends BaseModelData {
    private static final long serialVersionUID = 6780775115212302153L;
    
    public Boolean isUseBackupSet() {
    	return (Boolean)get("useBackupSet", Boolean.FALSE);
    }
    
    public void setUseBackupSet(Boolean backupSet) {
    	set("useBackupSet", backupSet);
    }
    
    //the following part is for merge schedule and retention count
    //Merge Schedule Start
    public Integer getRetentionCount() {
    	return (Integer)get("retentionCount");
    }
    
    public void setRetentionCount(Integer count) {
    	set("retentionCount", count);
    }
    
    public Boolean isUseTimeRange() {
    	return (Boolean)get("useTimeRange", Boolean.FALSE);
    }
    
    public void setUseTimeRange(Boolean timeRange) {
    	set("useTimeRange", timeRange);
    }
    
    public Integer getStartTimeHour() {
    	return (Integer)get("StartHour");
    }
    
    public void setStartTimeHour(Integer startHour) {
    	set("StartHour", startHour);
    }
    
    public Integer getEndTimeHour() {
    	return (Integer)get("EndHour");
    }
    
    public void setEndTimeHour(Integer endHour) {
    	set("EndHour", endHour);
    }
    
    public Integer getStartTimeMinutes() {
    	return (Integer)get("StartMinutes");
    }
    
    public void setStartTimeMinutes(Integer EndMinutes) {
    	set("StartMinutes", EndMinutes);
    }
    
    public Integer getEndTimeMinutes() {
    	return (Integer)get("EndMinutes");
    }
    
    public void setEndTimeMinutes(Integer EndMinutes) {
    	set("EndMinutes", EndMinutes);
    }
    //Merge Schedule End
    //Backup Set Start
    public Integer getBackupSetCount() {
    	return (Integer)get("BackupSetCount");
    }
    
    public void setBackupSetCount(Integer BackupSetCount) {
    	set("BackupSetCount", BackupSetCount);
    }
    
	public Boolean isUseWeekly() {
		return (Boolean)get("useWeekly", Boolean.FALSE);
	}
	
	public void setUseWeekly(Boolean useWeekly) {
		set("useWeekly", useWeekly);
	}
	
	public Integer getDayOfWeek() {
		return (Integer)get("DayOfWeek");
	}
	
	public void setDayOfWeek(Integer dayOfWeek) {
		set("DayOfWeek", dayOfWeek);
	}
	
	public Integer getDayOfMonth() {
		return (Integer)get("DayOfMonth");
	}
	
	public void setDayOfMonth(Integer dayOfMonth) {
		set("DayOfMonth", dayOfMonth);		
	}
	
	public Boolean isStartWithFirst() {
		return (Boolean)get("startWithFirst", Boolean.FALSE);
	}
	
	public void setStartWithFirst(Boolean startWithFirst) {
		set("startWithFirst", startWithFirst);
	}
	//Backup Set Ends
}

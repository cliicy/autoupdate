package com.ca.arcflash.ui.client.backup.schedule;

import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class ScheduleDetailItemModel extends BaseModelData{

	/**
	 * 
	 */
	public DayTimeModel startTimeModel;
	public DayTimeModel endTimeModel;
	
	private static final long serialVersionUID = 7744626302034212205L;
	public Integer getJobType(){
		return (Integer)get("jobType");
	}
	public void setJobType(Integer jobType){
		set("jobType", jobType);
	}
	
	//Interval IntervalUnit
	public Integer getInterval(){
		return (Integer)get("interval");
	}
	public void setInterval(Integer interval){
		set("interval", interval);
	}
	
	public Integer getIntervalUnit(){
		return (Integer)get("intervalUnit");
	}
	public void setIntervalUnit(int intervalUnit){
		set("intervalUnit", intervalUnit);
	}
	public Boolean isRepeatEnabled()
	{
		return (Boolean)get("repeatEnabled");
	}
	
	public void setRepeatEnabled(Boolean repeatEnabled)
	{
		set("repeatEnabled", repeatEnabled);
	}
}

package com.ca.arcflash.ui.client.backup.advschedule;

import com.ca.arcflash.ui.client.backup.schedule.EveryDayScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryMonthScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryWeekScheduleModel;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.data.BaseModel;

public class ScheduleItemModel extends BaseModel{

	public DayTimeModel startTimeModel=new DayTimeModel();
	public DayTimeModel endTimeModel=new DayTimeModel(); 
	
	private static final long serialVersionUID = 7744626302034212205L;
	private int[] dayOfWeekArray = new int[7];
	
	public Integer getScheduleType(){
		return (Integer)get("scheduleType", 0);
	}
	public void setScheduleType(Integer scheduleType){
		set("scheduleType", scheduleType);
	}
	
	public Integer getJobType(){
		return (Integer)get("jobType", 1);
	}
	public void setJobType(Integer jobType){
		set("jobType", jobType);
	}
	
	public String getDescription(){
		return (String)get("description");
	}
	public void setDescription(String strDescription){
		set("description", strDescription);
	}
	
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
	
	public Integer getDayofWeek(int i){
		return dayOfWeekArray[i];
	}
	public void setDayofWeek(int i, int j){
		dayOfWeekArray[i] = j;
	}	
	
	public Long getThrottle(){
		return (Long)get("throttle");
	}
	public void setThrottle(long throttle){
		set("throttle", throttle);
	}
	
	public Integer getThrottleUnit(){
		return (Integer)get("throttleUnit", 0);
	}
	public void setThrottleUnit(int throttleUnit){
		set("throttleUnit", throttleUnit);
	}
	
	public EveryDayScheduleModel getEveryDaySchedule(){
		return (EveryDayScheduleModel)get("everyDaySchedule");
	}
	public void setEveryDaySchedule(EveryDayScheduleModel everyDaySchedule){
		set("everyDaySchedule", everyDaySchedule);
	}
		
	public EveryWeekScheduleModel getEveryWeekSchedule(){
		return (EveryWeekScheduleModel)get("everyWeekSchedule");
	}
	public void setEveryWeekSchedule(EveryWeekScheduleModel everyWeekSchedule){
		set("everyWeekSchedule", everyWeekSchedule);
	}
	
	public EveryMonthScheduleModel getEveryMonthSchedule(){
		return (EveryMonthScheduleModel)get("everyMonthlySchedule");
	}
	public void setEveryMonthSchedule(EveryMonthScheduleModel everyMonthlySchedule){
		set("everyMonthlySchedule", everyMonthlySchedule);
	}
	
	public String getEveryMonthDate(){
		return (String)get("everyMonthDate");
	}
	
	public void setEveryMonthDate(String everyMonthDate){
		set("everyMonthDate", everyMonthDate);
	}
}

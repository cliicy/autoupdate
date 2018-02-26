package com.ca.arcserve.edge.app.base.scheduler;

import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;


public interface  EdgeTrigger{

	public IScheduleCallBack getCallback() ;
	public void setCallback(IScheduleCallBack callback) ;
	public ScheduleData getScheduleData();
	public void setScheduleData(ScheduleData scheduleData);
	public Object getArgs();
	public void setArgs(Object args);
	public int getRepeat_count() ;
	public void setRepeat_count(int repeartCount);
	public int getTimesTriggered();
	public List<Date> getScheduleDates(Date begin, Date end);
}

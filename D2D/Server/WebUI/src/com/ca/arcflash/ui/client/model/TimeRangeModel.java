package com.ca.arcflash.ui.client.model;

import java.util.Date;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class TimeRangeModel extends BaseModelData {
	public static final int TIME_PERIODS = 4;
	public Date getStartDate()
	{
		return (Date)get("startdate");			
	}
	public void setStartDate(Date date)
	{
		set("startdate", date);
	}
	public Date getEndDate()
	{
		return (Date)get("enddate");			
	}
	public void setEndDate(Date date)
	{
		set("enddate", date);
	}
	public String getRange()
	{
		return (String)get("rangeString");		
	}
	public void setRange(String range)
	{
		set("rangeString", range);
	}
	
	public Integer getCount()
	{
		return (Integer) get("count");
	}
	public void setCount(Integer i)
	{
		set("count", i);
	}
	
	public int compare(TimeRangeModel model1) {
		if (getStartDate() == null)
			return -1;
		if (model1 == null || model1.getStartDate() == null)
			return 1;
		if(getStartDate().getHours() < model1.getStartDate().getHours())
			return -1;
		else if(getStartDate().getHours() == model1.getStartDate().getHours()
				&& getStartDate().getMinutes() < model1.getStartDate().getMinutes())
			return -1;
		return 1;
	}
}

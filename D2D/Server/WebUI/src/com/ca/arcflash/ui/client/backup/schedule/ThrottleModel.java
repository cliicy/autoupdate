package com.ca.arcflash.ui.client.backup.schedule;

import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class ThrottleModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5069384731835954620L;
	/**
	 * 
	 */
	public DayTimeModel startTimeModel;
	public DayTimeModel endTimeModel;
	
	public Long getThrottleValue(){
		if(get("throttleValue") == null)
			return 0L;
		else
			return (Long)get("throttleValue");
	}
	public void setThrottleValue(Long throttleValue){
		set("throttleValue", throttleValue);
	}
	
	
	public Integer getUnit(){
		return (Integer)get("throttleUnit", 0);
	}
	public void setUnit(int throttleUnit){
		set("throttleUnit", throttleUnit);
	}
}

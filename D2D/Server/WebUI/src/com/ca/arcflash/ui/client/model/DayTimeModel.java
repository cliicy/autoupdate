package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * A class just used to specify the hour and minutes of a day, with 0-23 as hour range
 * and 0-59 as minutes range, for time using AM/PM, user need to convert the hour to 
 * 0-23.
 *
 */
public class DayTimeModel extends BaseModelData {
	
	private static final long serialVersionUID = -587078075006292175L;
	public DayTimeModel(){}
	public DayTimeModel(Integer hour, Integer min){
		setHour(hour);
		setMinute(min);
	}
	public Integer getHour() {
		return (Integer)get("HOUR");
	}
	
	public void setHour(Integer hour) {
		set("HOUR", hour);
	}
	
	public Integer getMinutes() {
		return (Integer)get("MINUTE");
	}
	
	public void setMinute(Integer min) {
		set("MINUTE", min);
	}
}

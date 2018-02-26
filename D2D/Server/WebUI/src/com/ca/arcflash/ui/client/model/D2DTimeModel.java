package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class D2DTimeModel extends BaseModelData {
	private static final long serialVersionUID = -3558640210200917626L;

	public Integer getYear() {
		return (Integer)get("year");
	}
	
	public void setYear(Integer year) {
		set("year", year);
	}
	
	public Integer getMonth() {
		return (Integer)get("month");
	}
	
	public void setMonth(Integer month) {
		set("month", month);
	}
	
	public Integer getDay() {
		return (Integer)get("day");
	}
	
	public void setDay(Integer day) {
		set("day", day);
	}
	
	public Integer getHour() {
		return (Integer)get("hour");
	}
	
	public void setHour(Integer hour) {
		set("hour", hour);
	}
	
	public Integer getMinute() {
		return (Integer)get("minute");
	}
	
	public void setMinute(int minute) {
		set("minute", minute);
	}
	
	public Integer getSecond() {
		return (Integer)get("second");
	}
	
	public void setSecond(int second) {
		set("second", second);
	}
	
	public Integer getHourOfDay() {
		return (Integer)get("hourOfDay");
	}
	
	public void setHourOfDay(Integer hourofDay) {
		set("hourOfDay", hourofDay);
	}
	
	public Integer getAMPM(){
		Integer value = get("AMPM");
		if(value == null)
			return -1;
		else 
			return value;
	}
	
	public void setAMPM(Integer am) {
		set("AMPM", am);
	}
	
	public void fromJavaDate(Date date) {
		this.setYear(date.getYear() + 1900);
		setMonth(date.getMonth());
		setDay(date.getDate());
		setHourOfDay(date.getHours());
		setMinute(date.getMinutes());
	}
}

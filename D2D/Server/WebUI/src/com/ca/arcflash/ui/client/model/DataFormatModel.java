package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class DataFormatModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 122438199879243210L;
	
	public String getTimeFormat(){
		return (String)get("timeFormat");
	}
	public void setTimeFormat(String timeFormat){
		set("timeFormat", timeFormat);
	}
	public String getShortTimeFormat(){
		return (String)get("shortTimeFormat");
	}
	public void setShortTimeFormat(String shortTimeFormat){
		set("shortTimeFormat", shortTimeFormat);
	}
	public String getTimeDateFormat(){
		return (String)get("timeDateFormat");
	}
	public void setTimeDateFormat(String timeDateFormat){
		set("timeDateFormat", timeDateFormat);
	}
	public String getDateFormat(){
		return (String)get("dateFormat");
	}
	public void setDateFormat(String dateFormat){
		set("dateFormat", dateFormat);
	}
}

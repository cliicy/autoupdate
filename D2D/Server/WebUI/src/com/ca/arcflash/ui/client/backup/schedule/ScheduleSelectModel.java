package com.ca.arcflash.ui.client.backup.schedule;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ScheduleSelectModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2847577572565983996L;
	public ScheduleSelectModel(String text, int value){
		setText(text);
		setValue(value);
	}
	public void setText(String text){
		set("text", text);
	}
	public String getText(){
		return (String)get("text");
	}
	public void setValue(int value){
		set("value", value);
	}
	public int getValue(){
	 return (Integer)get("value");
	}
}

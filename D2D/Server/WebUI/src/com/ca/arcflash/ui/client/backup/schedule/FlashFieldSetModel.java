package com.ca.arcflash.ui.client.backup.schedule;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class FlashFieldSetModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2710124043582393424L;
	public FlashFieldSetModel(){
		
	}
	public FlashFieldSetModel(String text, Integer value){
		setText(text);
		setValue(value);
	}
	public String getText() {
		return (String)get("text");
	}
	public void setText(String text) {
		set("text", text);
	}
	public Integer getValue() {
		return (Integer)get("value");
	}
	public void setValue(Integer value) {
		set("value", value);
	}
	

}

package com.ca.arcflash.ui.client.backup.advschedule;

import com.ca.arcflash.ui.client.common.AdsTimeField;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;


public class ScheduleAddDetailItemTimePanel extends LayoutContainer {
	private AdsTimeField timeField;
	private DayTimeModel defaultTimeModel;
	
	public ScheduleAddDetailItemTimePanel(DayTimeModel defaultTimeModel){
		this.defaultTimeModel = defaultTimeModel;
		addTimeRow(this);
	}
	
	private void addTimeRow(LayoutContainer container){
		timeField = new AdsTimeField("b63c3ba1-c06b-4757-ac4e-722d927ae312");
		timeField.setTimeValue(defaultTimeModel);
		timeField.setEditable(false);
		timeField.setAllowBlank(false);
		timeField.setWidth(180);
		
		container.add(timeField);		
	
	}
	
	public DayTimeModel getValue(){
		return timeField.getTimeValue();
	}
	
	public void setValue(DayTimeModel timeModel){
		timeField.setTimeValue(timeModel);
	}
	
	public boolean validate() {
		if(!timeField.validate())
			return false;		
		return true;
	}
	
	public DayTimeModel getTimeModel(){
		return timeField.getTimeValue();
	}	
}

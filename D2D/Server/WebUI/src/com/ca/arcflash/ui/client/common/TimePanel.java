package com.ca.arcflash.ui.client.common;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class TimePanel extends LayoutContainer {
	private BaseSimpleComboBox<String> hourCombo;
	private BaseSimpleComboBox<String> minuteCombo;
	private BaseSimpleComboBox<String> amCombo;
	
	/**
	 * The id is used to differentiate the debug id of the panels
	 * @param id
	 */
	public TimePanel(String id) {
		LabelField label;
		TableData tabData;
		
		TableLayout timeLayout = new TableLayout();
		timeLayout.setColumns(4);
		setLayout(timeLayout);
		
		hourCombo = new BaseSimpleComboBox<String>();
		hourCombo.ensureDebugId("1F8A5757-E56F-4490-A90F-DC66BA781D90" + id);
		hourCombo.setEditable(false);
		hourCombo.setTriggerAction(TriggerAction.ALL);
		for (int i = ScheduleUtils.minHour(); i <= ScheduleUtils.maxHour(); i++) {
			String val;
			if(ScheduleUtils.isHourPrefix())
				val = ScheduleUtils.prefixZero( i, 2 );
			else
				val = new Integer(i).toString();
			hourCombo.add(val);
		}
		hourCombo.setWidth(44);
		hourCombo.setSimpleValue(hourCombo.getStore().getAt(0).getValue());
		
		tabData = new TableData();
		add(hourCombo, tabData);
		
		label = new LabelField();
		label.setValue(":");
		label.setWidth(15);
		
		tabData = new TableData();
		tabData.setHorizontalAlign(HorizontalAlignment.CENTER);
		add(label, tabData);
		
		minuteCombo = new BaseSimpleComboBox<String>();
		minuteCombo.ensureDebugId("1F40BFBC-9FD7-4437-9CE2-1A85148B2AB6" + id);
		minuteCombo.setEditable(false);
		minuteCombo.setTriggerAction(TriggerAction.ALL);
		for (int i = 0; i < 60; i++) {
			String val;
			if(ScheduleUtils.isMinutePrefix())
				val = ScheduleUtils.prefixZero( i, 2 );
			else
				val = new Integer(i).toString();
			minuteCombo.add(val);
		}
		minuteCombo.setWidth(44);
		minuteCombo.setSimpleValue(minuteCombo.getStore().getAt(0).getValue());
		tabData = new TableData();
		add(minuteCombo, tabData);
		
		if( !ScheduleUtils.is24Hours())
		{
			amCombo = new BaseSimpleComboBox<String>();
			amCombo.ensureDebugId("3EB070CA-E8E9-49f9-9E0B-229967B24DF2" + id);
			amCombo.setEditable(false);
			amCombo.setTriggerAction(TriggerAction.ALL);	
			amCombo.add(UIContext.Constants.scheduleStartTimeAM());
			amCombo.add(UIContext.Constants.scheduleStartTimePM());
			amCombo.setSimpleValue(UIContext.Constants.scheduleStartTimeAM());
			amCombo.setWidth(64);
			tabData = new TableData();
			tabData.setPadding(5);
			add(amCombo, tabData);
		}
	}
	
	public String getHour() {
		return hourCombo.getSimpleValue();
	}
	
	public String getMinute() {
		return minuteCombo.getSimpleValue();
	}
	
	public String getAM() {
		if(amCombo != null)
			return amCombo.getSimpleValue();
		else 
			return null;
	}
	
	/**
	 * 
	 * @param hour   the hours between 0-23.
     * @param minute the minutes between 0-59.
	 */
	public void setValue(int hour, int minute) {
		String hourVal, minuteVal;
		boolean isAM = false;
		
		if( !ScheduleUtils.is24Hours() ){							//for 12 hours
			if( ScheduleUtils.minHour() == 0 ){					//	for 0-11 clock
				if( hour < 12 ){					//		for am
					isAM = true;
				}
				else{								//		for pm
					isAM = false;
					if( hour == 12 )				//			translate 12:30 to 0:30 pm
						hour = 0;
					else
						hour = hour - 12;			//			translate 18:30 to 6:30 pm
				}
			}
			else{									//	for 1-12 clock
				if( hour < 12 ){					//		for am
					isAM = true;
					if( hour == 0 )					//			translate 0:30 to 12:30 am
						hour = 12;
				}
				else{								//		for pm
					isAM = false;
					if( hour != 12 )				//			translate 12:30 to 12:30 pm
						hour -= 12;					//			translate 18:30 to 6:30 pm
				}
			}
		}
		else{										//for 24 hours
			if( ScheduleUtils.minHour() == 1)						//	for 1-24 clock
			{
				if( hour == 0 )						//		translate 0:30 to 24:30
					hour = 24;
			}
		}
		
		if( ScheduleUtils.isHourPrefix() )
			hourVal = ScheduleUtils.prefixZero( hour, 2 );
		else
			hourVal = Integer.toString(hour);
		if( ScheduleUtils.isMinutePrefix() )
			minuteVal = ScheduleUtils.prefixZero( minute, 2 );
		else
			minuteVal =Integer.toString(minute);
		
		hourCombo.setSimpleValue(hourVal);
		minuteCombo.setSimpleValue(minuteVal);
		if( !ScheduleUtils.is24Hours() )
			if( isAM )
				amCombo.setSimpleValue(UIContext.Constants.scheduleStartTimeAM());
			else
				amCombo.setSimpleValue(UIContext.Constants.scheduleStartTimePM());
	}
	
	public DayTimeModel getTimeValue() {
		DayTimeModel model = new DayTimeModel();
		int hour = Integer.parseInt(getHour());
		int minute = Integer.parseInt(getMinute());
		boolean isAM = false;
		if( amCombo != null && amCombo.getValue() != null )
			if( amCombo.getValue().getValue() == UIContext.Constants.scheduleStartTimeAM() )
				isAM = true;
			else
				isAM = false;

		if( !ScheduleUtils.is24Hours() ){							//for 12 hours
			if( ScheduleUtils.minHour() == 0 ){					//	for 0-11 clock
				if( !isAM ){						//		for pm
					if( hour == 0 )					//			translate 0:30 pm to 12:30
						hour = 12;
					else						
						hour += 12;					//			translate 6:30 pm to 18:30
				}
			}
			else{									//	for 1-12 clock
				if( isAM ){							//		for am
					if( hour == 12 )				//			translate 12:30 am to 0:30
						hour = 0;
				}
				else{								//		for pm
					if( hour != 12 )				//			translate 12:30 pm to 12:30
						hour += 12;					//			translate 6:30 pm to 18:30
				}
			}
		}
		else{										//for 24 hours
			if( ScheduleUtils.minHour() == 1 ){					//	for 1-24 clock
				if( hour == 24 )					//		translate 24:30 to 0:30
					hour = 0;						
			}
		}
		model.setHour(hour);
		model.setMinute(minute);
		return model;
	}
}

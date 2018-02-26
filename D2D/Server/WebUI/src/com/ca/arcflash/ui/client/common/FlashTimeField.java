package com.ca.arcflash.ui.client.common;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

/**
 * A common time field which can deal with different time formats.
 * The value set for this field is what you want to show for end user, 
 * this widget will not handle server time/ui time conversion. 
 * <p/>
 * To avoid format error and keep consistency, it's recommended that all D2D 
 * and CentralApps UI use this widget to display time.
 * <p/>
 * Please check {@code com.ca.arcflash.ui.client.backup.StartDateTimeSetting} 
 * 	for sample usage.
 *
 */
public class FlashTimeField extends LayoutContainer {
	private BaseSimpleComboBox<String> hourCombo;
	private BaseSimpleComboBox<String> minuteCombo;
	private BaseSimpleComboBox<String> amCombo;
	
	private String hourTooltip;
	private String minuteTooltip;
	private String amTooltip;
	
	private int hour = -1;
	private int minutes = -1;
	
	private String hourID;
	private String minuteID;
	private String amID;
	
	private boolean disableMinutes;
	
	public FlashTimeField() {
		this(-1, -1);
	}
	/**
	 * 
	 * @param hourOfDay the hour (0-23)
	 * @param minutes the minutes (0-59)
	 */
	public FlashTimeField(int hourOfDay, int minutes) {
		this(hourOfDay, minutes, "", "", "");
	}
	
	/**
	 * @param hourOfDay the hour (0-23)
	 * @param minutes the minutes (0-59)
	 * @param hourToolTip tool tip for the hour field
	 * @param minuteToolTip tool tip for the minutes field
	 * @param amToolTip tool tip for the am/pm field if exists
	 */
	public FlashTimeField(int hourOfDay, int minutes, String hourToolTip, 
			String minuteToolTip, String amToolTip) {
		this.hour = hourOfDay;
		this.minutes = minutes;
		hourTooltip = hourToolTip;
		minuteTooltip = minuteToolTip;
		amTooltip = amToolTip;
		this.hourID = "3E96164F-B6EC-4494-95E2-F3E30493C759";
		this.minuteID = "A62431A2-0231-4b9f-827F-D561E6CEE4E5";
		this.amID = "8A5DB004-AA5F-4c6b-91B1-D2A0EC738DAD";
		Render();
	}
	
	public void setValue(Time time){
		this.setHour(time.getHour());
		this.setMinute(time.getMinutes());
	}
	
	public Time getValue() {
		return new Time(gethour(), getMinute());
	}
	
	public DayTimeModel getValueModel() {
		return new DayTimeModel(gethour(), getMinute());
	}
	
	public void setEditable(boolean isEditable) {
		hourCombo.setEnabled(isEditable);
		minuteCombo.setEnabled(isEditable);
		amCombo.setEnabled(isEditable);
	}
	
	/**
	 * 
	 * @return the hour user select on the UI
	 */
	public int getInputHour() {
		return Integer.valueOf(hourCombo.getSimpleValue()).intValue();
	}
	
	/**
	 * @return return the AM/PM value, 0 for AM, 1 for PM, -1 if it's 24 hour format.
	 */
	public int getAMPM() {
		boolean isAM = false;
		if( amCombo != null && amCombo.getValue() != null )
			if(amCombo.getValue().getValue()
					.equals(UIContext.Constants.scheduleStartTimeAM()))
				isAM = true;
			else
				isAM = false;
		if(!Utils.is24Hours()) {
			if(isAM)
				return 0;
			else
				return  1;
		}else
			return -1;
	}
	
	public void setMinutesEnabled(boolean enable) {
		disableMinutes = !enable;
		minuteCombo.setEnabled(enable);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		hourCombo.setEnabled(enabled);
		minuteCombo.setEnabled(!disableMinutes);
	}
	
	private void Render() {
		LabelField label;
		TableData tabData;
		
		TableLayout timeLayout = new TableLayout();
		timeLayout.setColumns(4);
		setLayout(timeLayout);
		
		hourCombo = new BaseSimpleComboBox<String>();
		hourCombo.ensureDebugId(hourID);
		hourCombo.setEditable(false);
		hourCombo.setTriggerAction(TriggerAction.ALL);
		for (int i = Utils.minHour(); i <= Utils.maxHour(); i++) {
			String val;
			if(Utils.isHourPrefix())
				val = Utils.prefixZero( i, 2 );
			else
				val = new Integer(i).toString();
			hourCombo.add(val);
		}
		hourCombo.setWidth(60);
		hourCombo.setFieldLabel(UIContext.Constants.scheduleStartTime());
		if(hourTooltip != null && !hourTooltip.isEmpty()) {
			Utils.addToolTip(hourCombo, hourTooltip);
		}
		if(Utils.isHourPrefix())
			hourCombo.setSimpleValue(Utils.prefixZero(0, 2));
		else
			hourCombo.setSimpleValue(String.valueOf(Utils.minHour()));
		tabData = new TableData();
		add(hourCombo, tabData);
		
		label = new LabelField();
		label.setValue(":");
		label.setWidth(15);
		
		tabData = new TableData();
		tabData.setHorizontalAlign(HorizontalAlignment.CENTER);
		add(label, tabData);
		
		minuteCombo = new BaseSimpleComboBox<String>();
		minuteCombo.ensureDebugId(minuteID);
		if(minuteTooltip != null && !minuteTooltip.isEmpty()) {
			Utils.addToolTip(minuteCombo, minuteTooltip);
		}
		
		minuteCombo.setEditable(false);
		minuteCombo.setTriggerAction(TriggerAction.ALL);
		for (int i = 0; i < 60; i++) {
			String val;
			if(Utils.isMinutePrefix())
				val = Utils.prefixZero( i, 2 );
			else
				val = new Integer(i).toString();
			minuteCombo.add(val);
		}
		if(Utils.isMinutePrefix()) {
			minuteCombo.setSimpleValue(Utils.prefixZero(0, 2));
		}else {
			minuteCombo.setSimpleValue("0");
		}
		minuteCombo.setWidth(60);
		tabData = new TableData();
		add(minuteCombo, tabData);
		
		if( !Utils.is24Hours())
		{
			amCombo = new BaseSimpleComboBox<String>();
			amCombo.ensureDebugId(amID);
			if(amTooltip != null && !amTooltip.isEmpty()) {
				Utils.addToolTip(amCombo, amTooltip);
			}
			amCombo.setEditable(false);
			amCombo.setTriggerAction(TriggerAction.ALL);	
			amCombo.add(UIContext.Constants.scheduleStartTimeAM());
			amCombo.add(UIContext.Constants.scheduleStartTimePM());
			amCombo.setWidth(64);
			amCombo.setSimpleValue(UIContext.Constants.scheduleStartTimeAM());
			//amCombo.setStyleAttribute("margin-left", "5px");
			tabData = new TableData();
			tabData.setPadding(5);
			add(amCombo, tabData);
		}
		
		if(hour != -1){
			setHour(hour);
		}
		
		if(minutes != -1) {
			setMinute(minutes);
		}
	}
	
	private int gethour() {
		String strHour = hourCombo.getValue() == null ? "0" :hourCombo.getValue().getValue();
		int hour = Integer.valueOf(strHour).intValue();
		boolean isAM = false;
		if( amCombo != null && amCombo.getValue() != null )
			if( amCombo.getValue().getValue() == UIContext.Constants.scheduleStartTimeAM() )
				isAM = true;
			else
				isAM = false;

		if( !Utils.is24Hours() ){							//for 12 hours
			if( Utils.minHour() == 0 ){					//	for 0-11 clock
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
			if( Utils.minHour() == 1 ){					//	for 1-24 clock
				if( hour == 24 )					//		translate 24:30 to 0:30
					hour = 0;						
			}
		}
		return hour;
	}
	
	private int getMinute() {
		String strMinute = minuteCombo.getValue() == null ? "" : minuteCombo
				.getValue().getValue();
		return Integer.valueOf(strMinute).intValue();
	}
	
	private void setHour(int hour) {
		boolean isAM = false;
		String hourVal = "";
		if( !Utils.is24Hours() ){							//for 12 hours
			if( Utils.minHour() == 0 ){					//	for 0-11 clock
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
			if( Utils.minHour() == 1)						//	for 1-24 clock
			{
				if( hour == 0 )						//		translate 0:30 to 24:30
					hour = 24;
			}
		}
		
		if( Utils.isHourPrefix() )
			hourVal = Utils.prefixZero( hour, 2 );
		else
			hourVal = Integer.toString(hour);
		
		
		hourCombo.setSimpleValue(hourVal);
		
		if( !Utils.is24Hours() )
			if( isAM )
				amCombo.setSimpleValue(UIContext.Constants.scheduleStartTimeAM());
			else
				amCombo.setSimpleValue(UIContext.Constants.scheduleStartTimePM());
	}
	
	private void setMinute(int minute) {
		String minuteVal = "";
		if( Utils.isMinutePrefix() )
			minuteVal = Utils.prefixZero( minute, 2 );
		else
			minuteVal =Integer.toString(minute);
		minuteCombo.setSimpleValue(minuteVal);
	}
	
	public void setDebugId(String hourID, String minuteID, String amID) {
		this.hourID = hourID;
		this.minuteID = minuteID;
		this.amID = amID;
	}
	public BaseSimpleComboBox<String> getHourCombo() {
		return hourCombo;
	}
	public BaseSimpleComboBox<String> getMinuteCombo() {
		return minuteCombo;
	}
	public BaseSimpleComboBox<String> getAmCombo() {
		return amCombo;
	}
}

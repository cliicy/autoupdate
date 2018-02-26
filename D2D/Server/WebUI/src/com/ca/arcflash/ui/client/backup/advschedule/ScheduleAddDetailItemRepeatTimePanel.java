package com.ca.arcflash.ui.client.backup.advschedule;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.FlashUIMessages;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class ScheduleAddDetailItemRepeatTimePanel extends LayoutContainer{

	private static FlashUIMessages uiMessages = UIContext.Messages;
	private static FlashUIConstants uiConstants = UIContext.Constants;
	
	private BaseComboBox<ScheduleInfo> repeatCombo;
	private NumberField repeatSpinnerField;
	
	public static final int Minute_Unit 	= 0;
	public static final int Hour_Unit 		= 1;
	public static final int Day_Unit	 	= 2;
	
	private ScheduleInfo hour_info;
	private ScheduleInfo min_info;	

	public ScheduleAddDetailItemRepeatTimePanel(){
		TableLayout tableLayout = new TableLayout();
		tableLayout.setColumns(2);
		this.setLayout(tableLayout);			
		
		repeatSpinnerField = new NumberField();
		repeatSpinnerField.setAllowBlank(false);
		repeatSpinnerField.setAllowNegative(false);
		repeatSpinnerField.setAllowDecimals(false);
		repeatSpinnerField.ensureDebugId("ac9eae1d-55a2-4091-ba69-7af99bbe96c7");
		repeatSpinnerField.setWidth(50);

		this.add(repeatSpinnerField);		

		repeatCombo = new BaseComboBox<ScheduleInfo>();
		repeatCombo.ensureDebugId("affae517-1147-4f3f-a96c-01f5e9114216");
		repeatCombo.setDisplayField(ScheduleInfo.NAMEFIELD);
		repeatCombo.setEditable(false);		
		repeatCombo.setStore(CreateScheduleInfo());
		repeatCombo.setWidth(130);
		setValue(3, Hour_Unit);
		repeatCombo.addSelectionChangedListener(new SelectionChangedListener<ScheduleInfo>() {
					@Override
					public void selectionChanged(
							SelectionChangedEvent<ScheduleInfo> se) {
						if (repeatCombo.getValue() == min_info) {
							repeatSpinnerField.setMinValue(15);
							repeatSpinnerField.setValue(15);
						} else {
							repeatSpinnerField.setMinValue(1);
							repeatSpinnerField.setValue(1);
						}
						repeatSpinnerField.validate();
					}
				});
		
		this.add(repeatCombo);
	}
		
	public ListStore<ScheduleInfo> CreateScheduleInfo()
	{
		ListStore<ScheduleInfo> scheduleListStore = new ListStore<ScheduleInfo>();
		
		hour_info = new ScheduleInfo();
		hour_info.setName(UIContext.Constants.scheduleLabelHours());
		hour_info.setIntervalUnit(Hour_Unit);
		scheduleListStore.add(hour_info);
		
		min_info = new ScheduleInfo();
		min_info.setName(UIContext.Constants.scheduleLabelMinutes());
		min_info.setIntervalUnit(Minute_Unit);
		scheduleListStore.add(min_info);
		
		return scheduleListStore;
	}
	
	public boolean validateRepeatVal(DayTimeModel startTime, DayTimeModel endTime){
		boolean isValidate = true;
		int maxValue = 0;
		ScheduleInfo info = repeatCombo.getValue();
		int repeatUnit = info.getIntervalUnit();
		String repeatStr = info.getName();
		if(repeatSpinnerField.getValue() == null){
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), uiConstants.regularScheduleRepeatValueRequired(), MessageBox.ERROR);
			return false;
		}		
		int repeatValue = repeatSpinnerField.getValue().intValue();
		int diffHours = endTime.getHour()-startTime.getHour();
		int endTimeMins = endTime.getHour()*60 + endTime.getMinutes();
		int startTimeMins = startTime.getHour()*60 + startTime.getMinutes();
		int totalDiffMins = endTimeMins - startTimeMins;
		
		if(startTimeMins>=endTimeMins&&endTimeMins==0){
			//endtime is end of day 0:00
			diffHours+=24;
			totalDiffMins+=1440;
		}
		if(totalDiffMins<15){
				String message = UIContext.Constants.scheduleStartTimeBeforeEndTime15Mins();
				ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), message, MessageBox.ERROR);
				return false;
		}
		if(repeatUnit==Hour_Unit){
			if (totalDiffMins<60){
				maxValue = totalDiffMins;
				repeatStr = UIContext.Constants.scheduleLabelMinutes();
			}else{
				if (startTime.getMinutes()> endTime.getMinutes()){
					maxValue = diffHours - 1;
				}else{
					maxValue = diffHours;
				}
			}
			if(repeatValue*60>totalDiffMins || totalDiffMins<60){
				isValidate = false;
			}
			if (repeatValue<=0){
				String message = UIContext.Constants.scheduleRepeatValueAbove0();
				ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), message, MessageBox.ERROR);
				return false;
			}
		}
		else if(repeatUnit==Minute_Unit){
			if (repeatValue<15){
				String message = UIContext.Constants.scheduleMinRepeatValueIs15Minutes();
				ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), message, MessageBox.ERROR);
				return false;
			}
			if(repeatValue>totalDiffMins){
				isValidate = false;
				maxValue = totalDiffMins;
			}
		}
		if(!isValidate){
			String message = UIContext.Messages.scheduleMaxRepeatValue(maxValue, repeatStr);
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(), message, MessageBox.ERROR);
			return false;
		}
		return  true;
	}
	
	public boolean getValue(ScheduleItemModel model)
	{
		if (repeatSpinnerField != null) {
			try {
				Number i = repeatSpinnerField.getValue();
				Integer repeatVal = i.intValue();
				model.setInterval(repeatVal);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Integer unit = repeatCombo.getValue().getIntervalUnit();
			if (unit != null) {
				model.setIntervalUnit(unit);
			}
		}
		return true;
	}
	
	class ScheduleInfo extends BaseModelData
	{
		private static final long serialVersionUID = -1190005990510526623L;
		public static final String NAMEFIELD = "Name";
		public static final String INTERVALUNITFIELD = "IntervalUnit";
		
		public String getName() {
			return get(NAMEFIELD);
		}
		public void setName(String name) {
			set(NAMEFIELD, name);
		}
		public Integer getIntervalUnit()
		{
			return get(INTERVALUNITFIELD);
		}
		public void setIntervalUnit(Integer unit)
		{
			set(INTERVALUNITFIELD, unit);
		}
	}

	public void setValue(int interval, int unit) {
		switch (unit) {
		case Hour_Unit:
			repeatCombo.setValue(hour_info);
			break;
		case Minute_Unit:
			repeatCombo.setValue(min_info);			
			break;
		} 
		repeatSpinnerField.setValue(interval);

	}

}

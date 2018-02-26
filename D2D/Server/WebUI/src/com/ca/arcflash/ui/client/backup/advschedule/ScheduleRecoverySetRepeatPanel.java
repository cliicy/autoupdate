package com.ca.arcflash.ui.client.backup.advschedule;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.HasValidateValue;
import com.ca.arcflash.ui.client.model.BackupScheduleModel;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;

public class ScheduleRecoverySetRepeatPanel extends LayoutContainer implements HasValidateValue<BackupScheduleModel> {
	private CheckBox repeatBox;
	private BaseComboBox<ScheduleInfo> repeatCombo;
	private NumberField repeatTextField;
	public static final int Minute_Unit 	= 0;
	public static final int Hour_Unit 		= 1;
	public static final int Day_Unit	 	= 2;
	
	private ScheduleInfo day_info;
	private ScheduleInfo hour_info;
	private ScheduleInfo min_info;
	
	private 
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
	
	public ScheduleRecoverySetRepeatPanel(){
		HorizontalPanel panel = new HorizontalPanel();
		panel.setVerticalAlign(VerticalAlignment.MIDDLE);
		repeatBox = new CheckBox();
		repeatBox.setValue(false);
		repeatBox.addListener(Events.Change, new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				repeatTextField.setEnabled(repeatBox.getValue());
				repeatCombo.setEnabled(repeatBox.getValue());
			}
		});
		
		panel.add(repeatBox);
		LabelField label = new LabelField(UIContext.Constants.scheduleRepeat());
		label.setStyleAttribute("margin-right", "5px");
		label.setStyleAttribute("margin-left", "5px");
		panel.add(label);
		
		repeatTextField = new NumberField();		
		repeatTextField.setAllowDecimals(false);
		repeatTextField.setAllowNegative(false);
		repeatTextField.setAllowBlank(false);
		repeatTextField.setMinValue(1);
		repeatTextField.setValue(1);
		repeatTextField.setWidth(86);
		repeatTextField.disable();
		repeatTextField.setStyleAttribute("margin-right", "17px");
		panel.add(repeatTextField);
		
		repeatCombo = new BaseComboBox<ScheduleInfo>();
		repeatCombo.setDisplayField(ScheduleInfo.NAMEFIELD);
		repeatCombo.setEditable(false);		
		repeatCombo.setStore(CreateScheduleInfo());
		repeatCombo.setWidth(132);
		repeatCombo.setValue(day_info);
		repeatCombo.disable();
		repeatCombo.addSelectionChangedListener(new SelectionChangedListener<ScheduleInfo>() {
					@Override
					public void selectionChanged(
							SelectionChangedEvent<ScheduleInfo> se) {
							if (repeatCombo.getValue() == min_info) {
								repeatTextField.setMinValue(15);	
								repeatTextField.setValue(15);							
							} else {
								repeatTextField.setMinValue(1);
								repeatTextField.setValue(1);								
							}
							repeatTextField.validate();
						}					
				});
		panel.add(repeatCombo);
		
		this.add(panel);
		
	}
	
	public ListStore<ScheduleInfo> CreateScheduleInfo()
	{
		ListStore<ScheduleInfo> scheduleListStore = new ListStore<ScheduleInfo>();
		
		day_info = new ScheduleInfo();
		day_info.setName(UIContext.Constants.scheduleLabelDays());
		day_info.setIntervalUnit(Day_Unit);
		scheduleListStore.add(day_info);
		
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

	@Override
	public void buildValue(BackupScheduleModel value) {
		value.setEnabled(repeatBox.getValue());
		if(repeatBox.getValue()){
			value.setInterval(repeatTextField.getValue().intValue());
			value.setIntervalUnit(repeatCombo.getValue().getIntervalUnit());
		}
		
	}

	@Override
	public void applyValue(BackupScheduleModel value) {
		if(value == null)
			return;
		
		repeatBox.setValue(value.isEnabled());
		if(value.isEnabled()){
			repeatTextField.setValue(value.getInterval());			
			Integer unit = value.getIntervalUnit();
			if (unit == null) {
				unit = 0;
			}

			switch (unit) {
			case Day_Unit:
				repeatCombo.setValue(day_info);
				break;
			case Hour_Unit:
				repeatCombo.setValue(hour_info);
				break;
			case Minute_Unit:
				repeatCombo.setValue(min_info);
				repeatTextField.setMinValue(15);
				break;
			}

		}
		
	}

	private boolean isNumberFieldValid(double value, double minValue, double maxValue) {
		if(value < minValue || value > maxValue){
			return false;
		}
		return true;
	}
	
	@Override
	public boolean validate() {
		boolean isValid=true;
		if(repeatBox.getValue()){
			if(repeatTextField.isRendered()){
				isValid = repeatTextField.validate();
			}else{
				isValid = isNumberFieldValid(repeatTextField.getValue().doubleValue(), 
						repeatTextField.getMinValue().doubleValue(), repeatTextField.getMaxValue().doubleValue());
			}
		}
		return isValid;
	}
	
	public void setRepeatDefaultValue(boolean enabled){
		if(repeatBox != null){
			repeatBox.setValue(enabled);
		}
	}
}

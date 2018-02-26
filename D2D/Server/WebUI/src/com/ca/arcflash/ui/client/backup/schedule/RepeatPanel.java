package com.ca.arcflash.ui.client.backup.schedule;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class RepeatPanel extends LayoutContainer{

	private BaseComboBox<ScheduleInfo> repeatCombo;
	private NumberField repeatTextField;
	private ScheduleDetailItemModel model;
	
	public static final int Minute_Unit 	= 0;
	public static final int Hour_Unit 		= 1;
	public static final int Day_Unit	 	= 2;
	
	private ScheduleInfo hour_info;
	private ScheduleInfo min_info;
	private ScheduleDetailItemWindow parentWindow;
	
	public RepeatPanel(ScheduleDetailItemWindow parentWindow){
		this.parentWindow = parentWindow;
	}
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.setColumns(2);
		this.setLayout(tableLayout);			
		
		repeatTextField = new NumberField();
		repeatTextField.ensureDebugId("ac9eae1d-55a2-4091-ba69-7af99bbe96c7");
		repeatTextField.setAllowDecimals(false);
		repeatTextField.setAllowNegative(false);
		repeatTextField.setAllowBlank(false);
		repeatTextField.setWidth(50);
		TableData td = new TableData();
		td.setStyleName("schedule_repeatfield_td");
		this.add(repeatTextField, td);
		

		repeatCombo = new BaseComboBox<ScheduleInfo>();
		repeatCombo.ensureDebugId("affae517-1147-4f3f-a96c-01f5e9114216");
		repeatCombo.setDisplayField(ScheduleInfo.NAMEFIELD);
		repeatCombo.setEditable(false);		
		repeatCombo.setStore(CreateScheduleInfo());
		repeatCombo.setWidth(112);
		setValue();
		repeatCombo.addSelectionChangedListener(new SelectionChangedListener<ScheduleInfo>() {
					@Override
					public void selectionChanged(
							SelectionChangedEvent<ScheduleInfo> se) {
							if (repeatCombo.getValue() == min_info) {
								repeatTextField.setMinValue(15);
								repeatTextField.setValue(15);
								//if(!validate(false))
									//repeatTextField.setValue(15);
									
									
							} else {
								repeatTextField.setMinValue(1);
								repeatTextField.setValue(1);
								//if(!validate(false))
								//	repeatTextField.setValue(1);
								
							}
							repeatTextField.validate();
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
		int repeatValue = repeatTextField.getValue().intValue();
		int diffHours = endTime.getHour()-startTime.getHour();
		int endTimeMins = endTime.getHour()*60 + endTime.getMinutes();
		int startTimeMins = startTime.getHour()*60 + startTime.getMinutes();
		int totalDiffMins = endTimeMins - startTimeMins;
		if(totalDiffMins<15){
			//show the message
			String title = UIContext.productNameD2D;
			String message = UIContext.Constants.scheduleStartTimeBeforeEndTime15Mins();
			ScheduleUtils.showMesssageBox(title, message, MessageBox.ERROR);
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
				String title = UIContext.productNameD2D;
				String message = UIContext.Constants.scheduleRepeatValueAbove0();
				ScheduleUtils.showMesssageBox(title, message, MessageBox.ERROR);
				return false;
			}
		}
		else if(repeatUnit==Minute_Unit){
			if (repeatValue<15){
				String title = UIContext.productNameD2D;
				String message = UIContext.Constants.scheduleMinRepeatValueIs15Minutes();
				ScheduleUtils.showMesssageBox(title, message, MessageBox.ERROR);
				return false;
			}
			if(repeatValue>totalDiffMins){
				isValidate = false;
				maxValue = totalDiffMins;
			}
		}
		if(!isValidate){
			String title = UIContext.productNameD2D;
			String message = UIContext.Messages.scheduleMaxRepeatValue(maxValue, repeatStr);
			ScheduleUtils.showMesssageBox(title, message, MessageBox.ERROR);
			return false;
		}
		return  true;
	}
	
	public boolean getValue(ScheduleDetailItemModel model)
	{
		if (repeatTextField != null) {
			try {
				Number i = repeatTextField.getValue();
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

	public void setValue(ScheduleDetailItemModel model) {
		this.model = model;
		if(isRendered()){
			setValue();
		} 
	}

	private void setValue(){
		if(model == null)
			return;
		
		repeatTextField.setValue(model.getInterval());
		repeatTextField.setMinValue(1);
		Integer unit = model.getIntervalUnit();
		if (unit == null) {
			unit = 0;
		}

		switch (unit) {
		case Hour_Unit:
			repeatCombo.setValue(hour_info);
			break;
		case Minute_Unit:
			repeatCombo.setValue(min_info);
			repeatTextField.setMinValue(15);
			break;
		}
	}
	public boolean validate(boolean isShowMessage){
		boolean showError = false;
		String msgString = null;
		
		int bkpType= parentWindow.getJobType();
		
		boolean isValid = repeatTextField.validate();
		if (!isValid)
			return false;

		Number n = repeatTextField.getValue();
		int value = n.intValue();
		if (UIContext.hasBLILic) {
			if (repeatCombo.getValue() == hour_info && value > 8760) {
				showError = true;
				msgString = UIContext.Constants
						.backupSettingsErrorHoursTooLarge();
				repeatTextField.setValue(8760);
			} else if (repeatCombo.getValue() == min_info && value > 525600) {
				showError = true;
				msgString = UIContext.Constants
						.backupSettingsErrorMinutesTooLarge();
				repeatTextField.setValue(525600);
			} else if (value < 1) {
				showError = true;
				msgString = UIContext.Constants
						.backupSettingsErrorScheduleTooSmall();
				repeatTextField.setValue(1);
			}
		} else {
			if (bkpType == ScheduleUtils.FULL_BACKUP) {
				// <=7 days.
				if (repeatCombo.getValue() == hour_info && value > 7 * 24) {
					showError = true;
					msgString = UIContext.Messages
							.backupSettingsErrorHoursTooLargeForNoLic(7 * 24);
					repeatTextField.setValue(7 * 24);
				} else if (repeatCombo.getValue() == min_info
						&& value > 7 * 24 * 60) {
					showError = true;
					msgString = UIContext.Messages
							.backupSettingsErrorMinutesTooLargeForNoLic(7 * 24 * 60);
					repeatTextField.setValue(7 * 24 * 60);
				} else if (value < 1) {
					showError = true;
					msgString = UIContext.Constants
							.backupSettingsErrorScheduleTooSmall();
					repeatTextField.setValue(1);
				}
			} else if (bkpType == ScheduleUtils.INC_BACKUP) {
				// >= 1 hour.
				if (repeatCombo.getValue() == hour_info && value > 8760) {
					showError = true;
					msgString = UIContext.Constants
							.backupSettingsErrorHoursTooLarge();
					repeatTextField.setValue(8760);
				} else if (repeatCombo.getValue() == min_info && value > 525600) {
					showError = true;
					msgString = UIContext.Constants
							.backupSettingsErrorMinutesTooLarge();
					repeatTextField.setValue(525600);
				} else {
					if (value < 1) {
						showError = true;
						msgString = UIContext.Constants
								.backupSettingsErrorScheduleTooSmall();
						repeatTextField.setValue(1);
					} else if (repeatCombo.getValue() == min_info && value < 60) {
						showError = true;
						msgString = UIContext.Messages
								.backupSettingsErrorMinutesTooSmallForNoLic(60);
						repeatTextField.setValue(60);
					}
				}
			}
		}

		if (showError && isShowMessage) {
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.ERROR);
			msg.setTitleHtml(UIContext.Constants.backupSettingsSettings());
			msg.setMessage(msgString);
			msg.setModal(true);
			Utils.setMessageBoxDebugId(msg);
			msg.show();
			return false;
		}

		return true;
	}
	
	
	public void setEditable(boolean isEditable){
		repeatCombo.setEnabled(isEditable);
		repeatTextField.setEnabled(isEditable);
	}
	
	public void setDebugID(String repeatTextFieldID,String 	repeatComboID){
		
	}
}

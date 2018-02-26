package com.ca.arcflash.ui.client.backup.schedule;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AdsTimeField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Image;

public class AdvStartDateTimeSetting extends LayoutContainer{

	LayoutContainer daylightContainer = null;
	private AdsTimeField timeField;
	private DateField dateField;
	private CheckBox  disableScheduleCheckBox;
	
	private LabelField dateTimeSettingsHeader;
	
	@SuppressWarnings("deprecation")
	public AdvStartDateTimeSetting(SchedulePanel parentPanel)
	{
		
		RowLayout layout = new RowLayout(Orientation.VERTICAL);
		setLayout(layout);
		
		LabelField label;
		TableData tabData ;
		dateTimeSettingsHeader = new LabelField();
		dateTimeSettingsHeader.setValue(UIContext.Constants.scheduleStartDateTime());
		dateTimeSettingsHeader.addStyleName("restoreWizardSubItem");
		add(dateTimeSettingsHeader);
		
		// Add sub description.
		label = new LabelField();
		label.setValue(parentPanel.getScheduleLabelDescription());
		add(label);
		
		daylightContainer = new LayoutContainer();
		TableLayout tlayout = new TableLayout();
		tlayout.setWidth("100%");
		tlayout.setCellPadding(1);
		tlayout.setCellSpacing(1);
		tlayout.setColumns(2);
		daylightContainer.setLayout(tlayout);
		Image image = IconHelper.create("images/status_small_warning.png").createImage();
		TableData tdata = new TableData();
		tdata.setVerticalAlign(VerticalAlignment.TOP);
		daylightContainer.add(image,tdata);
		
		LabelField daylight = new LabelField();
		daylight.setValue(UIContext.Constants.settingsDaylightChange());
		daylight.addStyleName("StartDateSetting");
		daylightContainer.add(daylight, new TableData());
		
		add(daylightContainer);
		daylightContainer.hide();
		
		LayoutContainer startTimePane = new LayoutContainer();
		TableLayout tabLayout = new TableLayout();
		tabLayout.setWidth("90%");
		tabLayout.setCellPadding(1);
		tabLayout.setCellSpacing(1);
		tabLayout.setColumns(4);
		startTimePane.setLayout(tabLayout);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.scheduleStartDate());
		label.addStyleName("StartDateSetting");
		
		dateField = new DateField();
		dateField.ensureDebugId("C1C54199-7BDB-40d9-94C3-A519662F5C13");
		// Tool tip
		ToolTipConfig tipConfig = new ToolTipConfig(parentPanel.getScheduleStartDateToolTip());
		ToolTip tip = new ToolTip(dateField, tipConfig);
		tip.setHeaderVisible(false);
		dateField.setMaxValue(Utils.maxDate);
		dateField.setMinValue(Utils.minDate);
		dateField.setEditable(true);
		dateField.getPropertyEditor().setFormat(DateTimeFormat.getShortDateFormat());
		
		tabData = new TableData();
		tabData.setWidth("15%");	
		tabData.setHorizontalAlign(HorizontalAlignment.LEFT);
		startTimePane.add(label, tabData);
		
		tabData = new TableData();
		tabData.setWidth("35%");	
		tabData.setHorizontalAlign(HorizontalAlignment.LEFT);
		startTimePane.add(dateField, tabData);
				
		label = new LabelField();
		label.setValue(UIContext.Constants.scheduleStartTime());
		label.addStyleName("StartTimeSetting");
		
		tabData = new TableData();
		tabData.setWidth("15%");	
		tabData.setHorizontalAlign(HorizontalAlignment.RIGHT);
		startTimePane.add(label, tabData);
		
		timeField = new AdsTimeField("2dc1eb12-6641-46b6-a044-37bf3d22f054");
		timeField.ensureDebugId("758fef39-a56c-409a-aaec-76a9d1d1d36b");
		timeField.setEditable(false);
		timeField.setToolTip(parentPanel.getScheduleStartTimeToolTip());
		
		tabData = new TableData();
		tabData.setWidth("35%");	
		tabData.setHorizontalAlign(HorizontalAlignment.LEFT);
		startTimePane.add(timeField, tabData);
		
		add(startTimePane);
		
		LayoutContainer scheduleContainer = new LayoutContainer();
		TableLayout scheduleLayout = new TableLayout();
		scheduleLayout.setWidth("100%");
		scheduleLayout.setCellPadding(1);
		scheduleLayout.setCellSpacing(1);
		scheduleContainer.setLayout(scheduleLayout);
		disableScheduleCheckBox = new CheckBox();
		disableScheduleCheckBox.ensureDebugId("23c47a2f-f249-4427-af79-86c6a55aec1a");
		disableScheduleCheckBox.setBoxLabel(UIContext.Constants.scheduleDisable());
		disableScheduleCheckBox.setToolTip(parentPanel.getScheduleDisableToolTip());
		scheduleContainer.add(disableScheduleCheckBox);
		add(scheduleContainer);
	}
	
	public void setIsScheduleEnable(boolean isEnable){
		disableScheduleCheckBox.setValue(!isEnable);
	}
	public boolean getIsScheduleEnable(){
		return (!disableScheduleCheckBox.getValue());
	}
	public boolean validte(){
		return true;
	}
	
	public void setStartDateTime(Date startDate, long serverTimezoneOffset) {
		//make the date in client look like server time in text. 
		int hour, minute;
		
		if(serverTimezoneOffset != 0 && serverTimezoneOffset != -1 && serverTimezoneOffset != UIContext.serverVersionInfo.getTimeZoneOffset())
			daylightContainer.show();
		
		startDate = Utils.localTimeToServerTime(startDate, serverTimezoneOffset);
		dateField.setValue(startDate);
		DateWrapper wrapper = new DateWrapper(startDate);
		
		hour = wrapper.getHours();
		minute = wrapper.getMinutes();
		timeField.setValue(hour, minute);
		
	}
	
	public void setStartDateTime(Date startDate)
	{
		setStartDateTime(startDate, -1);
	}
	
	public Date getServerStartTime() {
		
		Date newDate = dateField.getValue();
		DateWrapper wrapper = new DateWrapper(newDate);
		DayTimeModel dayTimeModel = timeField.getTimeValue();
		int hour = dayTimeModel.getHour();
		int minute = dayTimeModel.getMinutes();
		
		DateWrapper newWrapper = wrapper.addHours(hour).addMinutes(minute);
		return newWrapper.asDate();
	}
	
	public Date getStartDateTime()
	{
		return getStartDateTime(-1);
	}
	
	public Date getStartDateTime(long serverTimeZoneOffset) {
		Date date = this.getServerStartTime();
		return Utils.serverTimeToLocalTime(date, serverTimeZoneOffset);
	}

	public LabelField getDateTimeSettingsHeader() {
		return dateTimeSettingsHeader;
	}

	public void setDateTimeSettingsHeader(LabelField dateTimeSettingsHeader) {
		this.dateTimeSettingsHeader = dateTimeSettingsHeader;
	}
}
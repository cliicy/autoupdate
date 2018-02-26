package com.ca.arcflash.ui.client.backup;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.FlashTimeField;
import com.ca.arcflash.ui.client.common.FormatUtil;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.D2DTimeModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;

public class StartDateTimeSetting extends LayoutContainer{
	ToolTipConfig tipConfig = null;
	ToolTip tip = null;
//	LayoutContainer daylightContainer = null;

	private FlashTimeField timeField;
	private DateField dateField;
	
	private LabelField dateTimeSettingsHeader;
	
	@SuppressWarnings("deprecation")
	public StartDateTimeSetting()
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
		label.setValue(UIContext.Constants
				.scheduleLabelScheduleDescription());
		add(label);
		
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
		tipConfig = new ToolTipConfig(UIContext.Constants.scheduleStartDateTooltip());
		tip = new ToolTip(dateField, tipConfig);
		tip.setHeaderVisible(false);
		dateField.setMaxValue(Utils.maxDate);
		dateField.setMinValue(Utils.minDate);
		dateField.setEditable(false);
		dateField.getPropertyEditor().setFormat(FormatUtil.getShortDateFormat());
		
		tabData = new TableData();
		tabData.setWidth("15%");	
		tabData.setHorizontalAlign(HorizontalAlignment.RIGHT);
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
		
		timeField = new FlashTimeField(-1, -1, UIContext.Constants.scheduleStartTimeTooltip1(),
				UIContext.Constants.scheduleStartTimeTooltip2(), UIContext.Constants.scheduleStartTimeTooltip3());
		timeField.setDebugId("D024385F-7C0A-4257-B519-FED2F4B0E58A", 
				"130D7175-8CAE-4e74-AB92-6785AC2B0D37", 
				"0A041564-79E7-413e-9997-6F4653D823D8");
		tabData = new TableData();
		tabData.setWidth("35%");	
		tabData.setHorizontalAlign(HorizontalAlignment.LEFT);
		startTimePane.add(timeField, tabData);
		
		add(startTimePane);
	}	
	
	public void setStartDateTime(Date startDate, long serverTimezoneOffset) {
		//make the date in client look like server time in text. 
		int hour, minute;

		if(serverTimezoneOffset == -1) {
			serverTimezoneOffset = UIContext.serverVersionInfo.getTimeZoneOffset().longValue();
		}
		TimeZone timeZone = TimeZone.createTimeZone((int)serverTimezoneOffset / (-60 * 1000));
		int diff = (startDate.getTimezoneOffset() - timeZone.getOffset(startDate)) * 60000;
	    Date keepDate = new Date(startDate.getTime() + diff);
	    Date keepTime = keepDate;
	    if (keepDate.getTimezoneOffset() != startDate.getTimezoneOffset()) {
	      if (diff > 0) {
	        diff -= Utils.NUM_MILLISECONDS_IN_DAY;
	      } else {
	        diff += Utils.NUM_MILLISECONDS_IN_DAY;
	      }
	      keepTime = new Date(startDate.getTime() + diff);
	    }

		dateField.setValue(keepDate);
		DateWrapper wrapper = new DateWrapper(keepTime);
		
		hour = wrapper.getHours();
		minute = wrapper.getMinutes();
		timeField.setValue(new Time(hour, minute));
	}
	
	public void setStartDateTime(Date startDate)
	{
		setStartDateTime(startDate, -1);
	}
	
	public Date getServerStartTime() {
		Date newDate = dateField.getValue();
		DateWrapper wrapper = new DateWrapper(newDate);
		int hour = gethour();
		int minute = getMinute();
		DateWrapper newWrapper = wrapper.addHours(hour)
	       .addMinutes(minute);
		return newWrapper.asDate();
	}
	
	public int gethour() {
		return timeField.getValue().getHour();
	}
	
	public int getMinute() {
		return timeField.getValue().getMinutes();
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
	
	public Date getStartDate() {
		return dateField.getValue();
	}
	
	public void setStartDate(Date value) {
		dateField.setValue(value);
	}
	
	public void setUserStartTime(D2DTimeModel model) {
		int hour = model.getHourOfDay(), minute = model.getMinute();
		String hourVal, minuteVal;
		boolean isAM = false;
		Date keepDate = new Date(model.getYear() - 1900, 
				model.getMonth(), model.getDay());
		
		dateField.setValue(keepDate);
		timeField.setValue(new Time(hour, minute));
	}
	
	public D2DTimeModel getUserSetTime() {
		D2DTimeModel time = new D2DTimeModel();
		Date date = this.getStartDate();
		time.setYear(date.getYear() + 1900);
		time.setMonth(date.getMonth());
		time.setDay(date.getDate());
		time.setHour(timeField.getInputHour());
		time.setMinute(getMinute());
		time.setHourOfDay(gethour());
		time.setAMPM(timeField.getAMPM());
		
		boolean isAM = false;
		
		
		return time;
	}
}
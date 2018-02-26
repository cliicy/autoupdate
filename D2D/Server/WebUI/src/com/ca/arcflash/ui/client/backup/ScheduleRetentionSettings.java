package com.ca.arcflash.ui.client.backup;

import com.ca.arcflash.ui.client.*;
import com.ca.arcflash.ui.client.backup.ScheduleSubSettings.BkpType;
import com.ca.arcflash.ui.client.backup.ScheduleSubSettings.ScheduleInfo;
import com.ca.arcflash.ui.client.common.*;
import com.ca.arcflash.ui.client.common.TimeField;
import com.ca.arcflash.ui.client.model.*;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;

public class ScheduleRetentionSettings {

	private Radio radioLastBkofDay;
	private Radio redioBackupStAt;
	private TimeField timeFieldDaily;
	
	private BaseComboBox<WeekModel> comboWeekday;
	
	private Radio radioWeeklyBkForMonth;
	private Radio radioLastBkofMonth;
	private BaseComboBox<WeekModel> comboMonthlyDay;

	private ListStore<WeekModel> weeklyDays= new ListStore<WeekModel>();
	private ListStore<WeekModel> monthlyDays = new ListStore<WeekModel>();
	
	private String name;
	private String title;
	private String description;
	private ScheduleRetentionSettings thisPanel;

	public ScheduleRetentionSettings()
	{
		thisPanel = this;
	}
	
	public ScheduleRetentionSettings(String name, String title, String description)
	{
		this.name = name;
		this.title = title;
		this.description = description;
		thisPanel = this;
	}
	
	private BaseComboBox<WeekModel> createComboWeekDay(ListStore<WeekModel> store) {
		BaseComboBox<WeekModel> weekDay = new BaseComboBox<WeekModel>();
		weekDay.setStore(store);
		weekDay.setDisplayField("name");
		weekDay.setEditable(false);
		for(int i = 1; i < 8; i ++) {
			WeekModel model = new WeekModel();
			model.setDay(i);
			model.setName(Utils.getDayofWeek(i));
			store.add(model);
		}
		
		weekDay.setWidth(150);
		
		return weekDay;
	}
	
	public FieldSet RenderDailySettings()
	{
		FieldSet fieldSet = new FieldSet();
		fieldSet.setCollapsible(false);
		fieldSet.setHeadingHtml("Daily Backups to retain:");
		fieldSet.ensureDebugId("4F1EF1DE-2FB7-4490-8461-F0A295F94C61");
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("100%");
		fieldSet.setLayout(tl);
		
		LayoutContainer firstLine = new LayoutContainer();
		LayoutContainer secondLine = new LayoutContainer();

		fieldSet.add(firstLine);
		fieldSet.add(secondLine);

		tl = new TableLayout();
		tl.setColumns(2);
		tl.setCellHorizontalAlign(HorizontalAlignment.LEFT);
		tl.setWidth("100%");
		secondLine.setLayout(tl);
		
		radioLastBkofDay = new Radio();
		radioLastBkofDay.ensureDebugId("96158636-DFDE-4996-A605-D987F9A25CA7");
		radioLastBkofDay.setBoxLabel(UIContext.Constants.retentionLastBkOfDay());
		firstLine.add(radioLastBkofDay);

		TableData td = new TableData();
		td.setWidth("50%");
		redioBackupStAt = new Radio();
		redioBackupStAt.ensureDebugId("5275F541-42FA-4c79-9415-8C55121B10D2");
		redioBackupStAt.setBoxLabel(UIContext.Constants.retentionBkStartAt());
		secondLine.add(redioBackupStAt,td);
		
		RadioGroup rg = new RadioGroup();
		rg.add(radioLastBkofDay);
		rg.add(redioBackupStAt);
		
		rg.addListener(Events.Change, new Listener<BaseEvent>() {
		      public void handleEvent(BaseEvent be) {
		    	  if ( redioBackupStAt.getValue())
		    		  timeFieldDaily.enable();
		    	  else
		    		  timeFieldDaily.disable();
			      }
			    });


		// one time field
		timeFieldDaily = new TimeField("backupRetention");
		timeFieldDaily.ensureDebugId("6C3EF3B5-3367-4d33-946D-AE304085F6F2");
		timeFieldDaily.setEditable(false);
		timeFieldDaily.setValue(8, 30);
		timeFieldDaily.disable();	
		secondLine.add(timeFieldDaily,td);
		
		return fieldSet;
	}
	
	public FieldSet RenderWeeklySettings()
	{
		FieldSet fieldSet = new FieldSet();
		fieldSet.setCollapsible(false);
		fieldSet.setHeadingHtml(UIContext.Constants.retentionWeeklyWhat()); //"Weekly Backups to retain:"
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		tl.setCellHorizontalAlign(HorizontalAlignment.LEFT);
		tl.setWidth("100%");
		fieldSet.setLayout(tl);
		TableData td = new TableData();
		
		td.setWidth("50%");
		// add one label
		LabelField lf = new LabelField();
		lf.setValue(UIContext.Constants.retentionLastBkWeekly());	
		lf.setStyleAttribute("margin-left", "15px");
		fieldSet.add(lf, td);

		// create combobox
		comboWeekday = createComboWeekDay (weeklyDays);
		comboWeekday.ensureDebugId("865200AB-2CC4-480b-B398-E95E8E762685");		
		fieldSet.add (comboWeekday, td);

		return fieldSet;
	}
	
	public FieldSet RenderMonthlySettings()
	{
		FieldSet fieldSet = new FieldSet();
		fieldSet.setCollapsible(false);
		fieldSet.setHeadingHtml(UIContext.Constants.retentionMonthlyWhat());
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("100%");
		fieldSet.setLayout(tl);
		
		LayoutContainer firstLine = new LayoutContainer();
		LayoutContainer secondLine = new LayoutContainer();

		fieldSet.add(firstLine);
		fieldSet.add(secondLine);

		tl = new TableLayout();
		tl.setColumns(2);
		tl.setWidth("100%");
		tl.setCellHorizontalAlign(HorizontalAlignment.LEFT);
		secondLine.setLayout(tl);
		
		radioWeeklyBkForMonth = new Radio();
		radioWeeklyBkForMonth.ensureDebugId("A8640FE0-0FCD-4429-AEC0-A25C999BCC0F");
		radioWeeklyBkForMonth.setBoxLabel(UIContext.Constants.retentionDailyBkMonthly());
		firstLine.add(radioWeeklyBkForMonth);

		TableData td = new TableData();
		td.setWidth("50%");
		radioLastBkofMonth = new Radio();
		radioLastBkofMonth.ensureDebugId("D3F74959-1C41-4254-A1AA-E0A94EF4D113");
		radioLastBkofMonth.setBoxLabel(UIContext.Constants.retentionLastBkMonthly());
		secondLine.add(radioLastBkofMonth, td);
		
		RadioGroup rg = new RadioGroup();
		rg.add(radioWeeklyBkForMonth);
		rg.add(radioLastBkofMonth);
		rg.addListener(Events.Change, new Listener<BaseEvent>() {
		      public void handleEvent(BaseEvent be) {
		    	  if ( radioLastBkofMonth.getValue())
		    		  comboMonthlyDay.enable();
		    	  else
		    		  comboMonthlyDay.disable();
			      }
			    });

		// add combobox
		comboMonthlyDay = createComboWeekDay(monthlyDays);
		comboMonthlyDay.ensureDebugId("9F83484F-B6FE-4a2b-AA1F-FF032A413852");
		secondLine.add(comboMonthlyDay, td);
		
		return fieldSet;		
	}
	
	public LayoutContainer Render()
	{	
		
		// draw daily settings
		LayoutContainer layoutContainer = new LayoutContainer();
		layoutContainer.add (RenderDailySettings());
		layoutContainer.add (RenderWeeklySettings());
		layoutContainer.add (RenderMonthlySettings());
		
		return layoutContainer;
	
	}


	public RetentionModel Save()
	{
		RetentionModel model = new RetentionModel();
		if ( radioLastBkofDay.getValue()){
			model.setDailyUseLastBackup( true );
		}else{
			model.setDailyUseLastBackup( false );
		}
		model.dailyBackupTime = timeFieldDaily.getTimeValue();
		
		model.setWeeklyBackupTime(comboWeekday.getValue().getDay()) ;
		
		if ( radioWeeklyBkForMonth.getValue()){
			 model.setMonthlyUseLastBackup(true);
		}else{
			 model.setMonthlyUseLastBackup(false);
		}
		 
		model.setMonthlyBackupTime(comboMonthlyDay.getValue().getDay());

		return model;
	}
	
	private boolean isRefreshing = false;
	
	public WeekModel getWeekDay(Integer value) {
		for(int i = 0; i < weeklyDays.getCount(); i ++) {
			WeekModel model = weeklyDays.getAt(i);
			if(model.getDay() == value)
				return model;
		}
		return null;
	}
	
	public void RefreshData(RetentionModel model) {
		isRefreshing = true;
		
		if (model == null){
			radioLastBkofDay.setValue(true);
			Utils.setComboboxValue(comboWeekday, 0);
			radioWeeklyBkForMonth.setValue(true);
			Utils.setComboboxValue(comboMonthlyDay, 0);
		}else{
			if ( model.isDailyUseLastBackup() ){
				radioLastBkofDay.setValue(true);
				redioBackupStAt.setValue(false);
			}else{
				radioLastBkofDay.setValue(false);
				redioBackupStAt.setValue(true);
			}
			
			if ( model.dailyBackupTime != null)
					timeFieldDaily.setTimeValue(model.dailyBackupTime);
			
			comboWeekday.setValue(getWeekDay (model.getWeekBackupTime()));
			
			if ( model.isMonthlyUseLastBackup()){
				radioWeeklyBkForMonth.setValue(true);
				radioLastBkofMonth.setValue(false);
			}else{
				radioWeeklyBkForMonth.setValue(false);
				radioLastBkofMonth.setValue(true);
			}
			
			comboMonthlyDay.setValue(getWeekDay(model.getMonthlyBackupTime()));
		}
		
		isRefreshing = false;
	}

	private boolean Validate(boolean isShowMessage){
		boolean showError = false;
		String msgString = null;
		return true;
	}
	
	public boolean Validate() {
		return Validate(true);
	}
	
	public void setEditable(boolean isEditable){
		radioLastBkofDay.setEnabled(isEditable);
		redioBackupStAt.setEnabled(isEditable);
		timeFieldDaily.setEnabled(isEditable);
		
		comboWeekday.setEnabled(isEditable);
		
		radioWeeklyBkForMonth.setEnabled(isEditable);
		radioLastBkofMonth.setEnabled(isEditable);
		comboMonthlyDay.setEnabled(isEditable);
	}
	
	public void setDebugID(String never, String repeat, String repCom, String repTxt){
	}

}

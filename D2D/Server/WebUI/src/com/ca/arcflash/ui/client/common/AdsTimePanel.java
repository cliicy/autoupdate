package com.ca.arcflash.ui.client.common;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Label;

public class AdsTimePanel extends LayoutContainer {
	
	private TimeLabel[] 	hourLabels;
	private TimeLabel[] 	minuteLabels;
	
	private TimeLabel 		selectedHourLable;
	private TimeLabel 		selectedMinuteLabel;
	
	private int 			defaultHour=-1;
	private int 			defualtMinute=-1;
	
	private IUpdate 		update;
	
	public void setUpdate(IUpdate update){
		this.update = update;
	}
	
	/**
	 * The id is used to differentiate the debug id of the panels
	 * @param id
	 */
	public AdsTimePanel(String id) {	
		this.ensureDebugId("a8be93d2-3ab5-4779-a9f2-ec4bb3434de0");
		hourLabels = new TimeLabel[24];
		minuteLabels = new TimeLabel[4];
		
		TableLayout timeLayout = new TableLayout();
		timeLayout.setColumns(2);
		timeLayout.setCellPadding(1);
		timeLayout.setCellSpacing(1);
		setLayout(timeLayout);
		
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		LabelField label = new LabelField(UIContext.Constants.scheduleTimePanelHour());
		label.addStyleName("schedule_time_text");
		add(label, td);
		
		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		label = new LabelField(UIContext.Constants.scheduleTimePanelMinute());
		label.addStyleName("schedule_time_text");
		add(label, td);
		
		LayoutContainer hourContainer = getHourContainer(id);
		hourContainer.ensureDebugId("b74db967-12e7-4887-818f-85ff0039e53f");
		add(hourContainer);
		
		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		LayoutContainer minuteContainer = getMinuteContainer(id);
		minuteContainer.ensureDebugId("ddf56f3b-7f63-4592-87ed-6e4cfcfd1c55");
		add(minuteContainer, td);
		
		setValue(0, 0);
		
		addButtons();
	}
	
	private void addButtons() {		
		Button okButton = new Button();
		okButton.setText(UIContext.Constants.ok());		
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {
					update.close();
				}
			});
		
	//	this.addButton(okButton);	
		

		TableData td = new TableData();
		td.setColspan(2);
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		add(okButton, td);
	}

	private LayoutContainer getHourContainer(String id){
		
		if(ScheduleUtils.is24Hours()){
			return getHourContainer(id, 0, 24);
		}
		else{
			LayoutContainer container = new LayoutContainer();
			TableLayout tl = new TableLayout(2);
			container.setLayout(tl);
			
			LabelField label = new LabelField(UIContext.Constants.scheduleStartTimeAM());
			//label.setStyleAttribute("font-weight", "bold");
			TableData td = new TableData();
			td.setHorizontalAlign(HorizontalAlignment.CENTER);
			td.setVerticalAlign(VerticalAlignment.MIDDLE);
			container.add(label, td);
			
			container.add(getHourContainer(id, 0, 12));
			
			td = new TableData();
			td.setColspan(2);
			container.add(new Html("<div class=\"schedule_time_line\"></div>"), td);
			
			label = new LabelField(UIContext.Constants.scheduleStartTimePM());
			//label.setStyleAttribute("font-weight", "bold");
			td = new TableData();
			td.setHorizontalAlign(HorizontalAlignment.CENTER);
			td.setVerticalAlign(VerticalAlignment.MIDDLE);
			container.add(label, td);
			
			container.add(getHourContainer(id, 12, 24));
			
			return container;
		}
		
	}
	
	private LayoutContainer getHourContainer(String id,int start, int end){
		LayoutContainer hourContainer = new LayoutContainer();
		TableLayout hourTableLayout = new TableLayout(6);
		hourTableLayout.setCellPadding(1);
		hourTableLayout.setCellSpacing(1);
		hourContainer.setLayout(hourTableLayout);
		
		for(int i=start; i<end; i++){
			final TimeLabel label = new TimeLabel(getHourLabelText(i), i);
			label.setStyleName("schedule_time_label");
			label.ensureDebugId("ced0a78e-11d0-4234-832d-f3065a09e3a4");
			label.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					setLabelStyle(true,selectedHourLable, label);
					cleanLastLabelStyle(selectedHourLable, label);
					selectedHourLable = label;
					dealWithClickEvents();
				}
			});
			label.addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
					setLabelStyle(true, selectedHourLable,label);
				}
			});
			label.addMouseOutHandler(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					setLabelStyle(false,selectedHourLable, label);
				}
			});
			
			hourLabels[i] = label;
			hourContainer.add(label);
		}
		return hourContainer;
	}
	private void dealWithClickEvents(){
		if(update != null){
			update.refresh();
			
			if((selectedHourLable!=null) && (selectedHourLable.getClickedCount() >=2)){
				cleanSelectedLabelCount();
				//update.close();
			}
			else if((selectedMinuteLabel!=null) && (selectedMinuteLabel.getClickedCount() >=2)){
				cleanSelectedLabelCount();
				//update.close();
			}
			else if((selectedHourLable!=null) &&(selectedHourLable.getClickedCount()>=1)
			 && (selectedMinuteLabel!=null) && (selectedMinuteLabel.getClickedCount() >=1)){
				cleanSelectedLabelCount();
				//update.close();
			}
		
		}
	}
	private void cleanSelectedLabelCount(){
		if(selectedHourLable!=null)
			selectedHourLable.setClickedCount(0);
		if(selectedMinuteLabel!=null)
			selectedMinuteLabel.setClickedCount(0);
		
	}
	
	private String getHourLabelText(int hour){
		if(ScheduleUtils.is24Hours()){
			return formatLabelText(hour);
		}
		else{
			if(hour%12 == 0){
				hour = 12;
			}
			else{
				hour = hour%12;
			}
			return formatLabelText(hour);
		}
	}
	private String formatLabelText(int i){
		if(i<10){
			return "0"+i;
		}
		else {
			return ""+i;
		}
	}
	
	private void cleanLastLabelStyle(TimeLabel oldSelectedLabel, TimeLabel newSelectedLabel){
		newSelectedLabel.incrementClickCount();
		if((oldSelectedLabel == null) ||(newSelectedLabel == oldSelectedLabel))
			return;
		
		oldSelectedLabel.setClickedCount(0);
		oldSelectedLabel.setStyleName("schedule_time_label");
	}
	
	private int getSelectedLabelValue(TimeLabel label){
		if(label == null)
			return -1;
		
		return label.getValue();
	}
	private void setLabelStyle(boolean isActive,TimeLabel selectLabel, TimeLabel label){
		if(label.getValue() == getSelectedLabelValue(selectLabel))
			return;
		
		if(isActive)
			label.setStyleName("schedule_time_label_active");
		else
			label.setStyleName("schedule_time_label");
		
	}
	
	private LayoutContainer getMinuteContainer(String id){
		LayoutContainer container = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(1);
		tableLayout.setCellPadding(1);
		tableLayout.setCellSpacing(1);
		container.setLayout(tableLayout);
		
		int i = 0;
		while(i<60){
			final TimeLabel label = new TimeLabel(formatLabelText(i), i);
			label.setStyleName("schedule_time_label");
			label.ensureDebugId("b0b1aac6-30e9-48cb-be8a-a18899c98075");
			label.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					setLabelStyle(true,selectedMinuteLabel,label);
					cleanLastLabelStyle(selectedMinuteLabel, label);
					selectedMinuteLabel = label;
					dealWithClickEvents();
				}
			});
			label.addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
					setLabelStyle(true, selectedMinuteLabel,label);
				}
			});
			label.addMouseOutHandler(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					setLabelStyle(false,selectedMinuteLabel, label);
				}
			});
			/*if((i==30) && (!ScheduleUtils.is24Hours()))
				container.add(new Html("<HR>"));*/
			
			minuteLabels[i/15] =label; 
			container.add(label);
			i= i+15;
		}
		return container;
	}
	
	public int getHour() {
		int hour = getSelectedLabelValue(selectedHourLable);
		return hour < 0? defaultHour:hour;
	}
	
	public int getMinute() {
		int minute = getSelectedLabelValue(selectedMinuteLabel);
		return minute <0 ? defualtMinute:minute;
	}
	
	/**
	 * 
	 * @param hour   the hours between 0-23.
     * @param minute the minutes between 0-59.
	 */
	public void setValue(int hour, int minute) {
		if(defaultHour != hour){
			for(int i=0; i< hourLabels.length; i++){
				TimeLabel label = hourLabels[i];
				if(label.getValue() == hour){
					label.setStyleName("schedule_time_label_active");
					selectedHourLable = label;
				}else if(label.getValue() == defaultHour){
					label.setStyleName("schedule_time_label");
				}
			}
			defaultHour = hour;
		}
		
		if(defualtMinute != minute){
			for(int i=0; i< minuteLabels.length; i++){
				TimeLabel label = minuteLabels[i];
				if(label.getValue() == minute){
					label.setStyleName("schedule_time_label_active");
					selectedMinuteLabel = label;
				}else if(label.getValue() == defualtMinute){
					label.setStyleName("schedule_time_label");
					selectedMinuteLabel = label;
				}
			}
			defualtMinute = minute;
		}
	}
	
	public DayTimeModel getTimeValue() {
		DayTimeModel model = new DayTimeModel();
		model.setHour(getHour());
		model.setMinute(getMinute());
		return model;
	}
}

interface IUpdate{
	void refresh();
	void close();
}

class TimeLabel extends Label{
	private int value;
	private int clickedCount;
	
	public int getClickedCount() {
		return clickedCount;
	}

	public void setClickedCount(int clickedCount) {
		this.clickedCount = clickedCount;
	}
	public void incrementClickCount(){
		int newClickCount = clickedCount +1;
		setClickedCount(newClickCount);
	}

	public TimeLabel(String text, int value){
		setText(text);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
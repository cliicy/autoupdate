package com.ca.arcflash.ui.client.backup.schedule;

import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AdsTimeField;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class ScheduleDetailItemWindow extends Window {
	
	private ScheduleDetailItemModel model;
	
	private RepeatPanel repeatPanel;
	
	private AdsTimeField startTime;
	
	private AdsTimeField endTime;
	
	private BaseComboBox<FlashFieldSetModel> jobTypeBox;
	
	private SelectionListener<ButtonEvent> OKButtonListener;
	
	private Button OKButton;
	
	private ScheduleDetail parentPanel;
	private ScheduleDaySelectionPanel daySelectionPanel;
	private List<Integer> modifyItemIdxList;
//	private FieldSet repeatFieldSet;
	
	public ScheduleDetailItemWindow(ScheduleDetailItemModel model, boolean isNewAdd, ScheduleDetail parent ) {
		this.parentPanel = parent;
		this.model = model;
		this.setWidth(496);
		this.setResizable(false);
	
		String windowHeaderString = isNewAdd?parentPanel.getScheduleAddWindowHeader():parentPanel.getScheduleEditWindowHeader();
		this.setHeadingHtml(windowHeaderString);	
		
		OKButton = new Button(UIContext.Constants.ok());
		OKButton.ensureDebugId("02f54fa2-520c-406b-b9e9-b76d5c346b83");
		OKButton.setWidth(80);
		
		Button cancelButton = new Button(UIContext.Constants.backupSettingsCancel());
		cancelButton.ensureDebugId("8d32647c-e7e5-4393-8abd-cb3c9bff1d1a");
		cancelButton.setWidth(80);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});
		
		Button helpButton = new Button(UIContext.Constants.help());
		helpButton.setWidth(80);
		helpButton.ensureDebugId("ee1532f0-fb2e-410d-87bd-ddf25c375f44");
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				String URL = parentPanel.getScheduleHelpURL();
				HelpTopics.showHelpURL(URL);
			}
		});
		this.addButton(OKButton);
		this.addButton(cancelButton);
		this.addButton(helpButton);
	}
	
	public void setModifyItemIndexList(List<Integer> modifyItemIndexList){
		this.modifyItemIdxList = modifyItemIndexList;
	}
	
	public void setOKButtonListener(SelectionListener<ButtonEvent> OKButtonListener){
		this.OKButtonListener = OKButtonListener;
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
		LayoutContainer container = new LayoutContainer();
		container.setStyleAttribute("margin", "5px");
		this.add(container);
		
		TableLayout tl = new TableLayout(2);
		tl.setWidth("100%");
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		container.setLayout(tl);
		
		addJobTypeRow(container);
		addStartTimeRow(container);
		addRepeatFieldSet(container);
		
		if(OKButtonListener!=null)
			OKButton.addSelectionListener(OKButtonListener);
	}
	
	private void addWidget(LayoutContainer container, LabelField label, Widget widget){
		TableData td = new TableData();
		td.setWidth("32%");
		container.add(label, td);
		
		td = new TableData();
		td.setWidth("68%");
		container.add(widget, td);
	}
	
	private void addStartTimeRow(LayoutContainer container){
		LabelField startLabelField = new LabelField(UIContext.Constants.scheduleStartAt());
		startTime = new AdsTimeField("b63c3ba1-c06b-4757-ac4e-722d927ae312");
		startTime.ensureDebugId("4572811a-df57-4d4a-929c-6d57e90c5c4f");
		startTime.setTimeValue(model.startTimeModel);
		startTime.setEditable(false);
		startTime.setAllowBlank(false);
		startTime.setWidth(180);
		
		addWidget(container, startLabelField, startTime);
		
		daySelectionPanel = new ScheduleDaySelectionPanel();
		daySelectionPanel.setBoxesDefaultValue(modifyItemIdxList, this.parentPanel.getDayofWeek());
		LabelField lField = new LabelField("");
		addWidget(container,lField, daySelectionPanel);
	}
	
	private void addRepeatFieldSet(LayoutContainer container){
		LayoutContainer innerContainer = new LayoutContainer();
		TableLayout tlField = new TableLayout(2);
		tlField.setWidth("100%");
		tlField.setCellPadding(2);
		tlField.setCellSpacing(2);
		innerContainer.setLayout(tlField);
		
		addEndTimeRow(innerContainer);
		addRepeatRow(innerContainer);		
		
	/*	repeatFieldSet = new FieldSet();
		repeatFieldSet.ensureDebugId("e3c04b53-652f-4043-94e1-7a032b720d78");
		repeatFieldSet.setHeading(UIContext.Constants.scheduleRepeatCheckBox());
		repeatFieldSet.setCheckboxToggle(true);  	   
		repeatFieldSet.add(innerContainer);
		repeatFieldSet.setExpanded(model.isRepeatEnabled());
			    
		// sync the shadow's size when the fieldset collapse/expand, otherwise the window's shadow size won't be changed automatically
		final Window thisWindow = this;
		repeatFieldSet.addListener(Events.Collapse, new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{
				thisWindow.sync(true);
			}
		});

		repeatFieldSet.addListener(Events.Expand, new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{
				thisWindow.sync(true);
			}
		});
			   */ 
		TableData td = new TableData();
		td.setWidth("100%");
		td.setColspan(2);
		container.add(innerContainer, td);	
	}
	private void addEndTimeRow(LayoutContainer container){
		LabelField endLabelField = new LabelField(UIContext.Constants.scheduleStopAt());
		endTime = new AdsTimeField("8820cf88-96df-4145-b20e-ab4762fbe902");
		endTime.ensureDebugId("79ebf329-7db3-4ec4-930d-6cf9fb9736d0");
		endTime.setTimeValue(model.endTimeModel);
		endTime.setEditable(false);
		endTime.setAllowBlank(false);
		endTime.setWidth(180);
		
		addWidget(container, endLabelField, endTime);
	}
	

	private void addRepeatRow(LayoutContainer container) {
		LabelField repeatLabel = new LabelField(UIContext.Constants.scheduleRepeat());
		repeatPanel = new RepeatPanel(this);
		repeatPanel.setValue(model);
		
		addWidget(container, repeatLabel, repeatPanel);
	}
	
	private void addJobTypeRow(LayoutContainer container){
		LabelField label = new LabelField(UIContext.Constants.homepageRecentBackupColumnTypeHeader());
	
		ListStore<FlashFieldSetModel> jobTypeModels = parentPanel.getJobTypeModels();
		jobTypeBox = new BaseComboBox<FlashFieldSetModel>();
		jobTypeBox.setDisplayField("text");
		jobTypeBox.setValueField("value");
		jobTypeBox.ensureDebugId("e8fb7e78-ccd3-4751-b898-78d24250387d");
		jobTypeBox.setStore(jobTypeModels);
		jobTypeBox.setEditable(false);
		jobTypeBox.setAllowBlank(false);
		jobTypeBox.setWidth(180);
		FlashFieldSetModel selectModel = null;
		for (FlashFieldSetModel flashFieldSetModel : jobTypeModels.getModels()) {
			if(selectModel == null){
				selectModel = flashFieldSetModel;
				continue;
			}
				
			if(flashFieldSetModel.getValue() == model.getJobType()){
				selectModel = flashFieldSetModel;
				break;
			}
		}
		jobTypeBox.setValue(selectModel);
		
		addWidget(container, label, jobTypeBox);
	}
	
	public ScheduleDetailItemModel save() {
		model.startTimeModel = startTime.getTimeValue();
		model.endTimeModel = endTime.getTimeValue();
		model.setJobType(jobTypeBox.getValue().getValue());
		repeatPanel.getValue(model);
		model.setRepeatEnabled(true);//repeatFieldSet.isExpanded());
		
		adjustEndTimeForRepeatEnabled(model);
		
		return model;
	}
	
	public boolean validate() {
		if(!startTime.validate())
			return false;
		
		if(!jobTypeBox.validate())
			return false;
		
		// check the repeat interval and endTime when repeat is enabled
//		if (repeatFieldSet.isExpanded())
//		{
			//if (!repeatPanel.validate(true))
				//return false;

			if (!endTime.validate())
				return false;
			if (!repeatPanel.validateRepeatVal(startTime.getTimeValue(), endTime.getTimeValue()))
				return false;
//		}
		
		return true;
	}
	
	public int getJobType(){
		return jobTypeBox.getValue().getValue();
	}
	
	public ScheduleDetailItemModel getCurrentModel() {
		ScheduleDetailItemModel itemModel = new ScheduleDetailItemModel();
		itemModel.startTimeModel = startTime.getTimeValue();
		itemModel.endTimeModel = endTime.getTimeValue();
		itemModel.setJobType(jobTypeBox.getValue().getValue());
		repeatPanel.getValue(itemModel);
		itemModel.setRepeatEnabled(true);//repeatFieldSet.isExpanded());
		adjustEndTimeForRepeatEnabled(itemModel);
		
		return itemModel;
	}
	
	public List<Integer> getDayIndexs(){
		return daySelectionPanel.getDayIndexs();
	}
	
	private void adjustEndTimeForRepeatEnabled(ScheduleDetailItemModel model)
	{
		// if repeat is disabled, set the end time = start time + 15 minutes
		if (model != null && model.isRepeatEnabled() != null && !model.isRepeatEnabled())
		{
			model.setInterval(15);
			model.setIntervalUnit(RepeatPanel.Minute_Unit);
				
			int startTimeMins = model.startTimeModel.getHour()*60 + model.startTimeModel.getMinutes();
			int endTimeMins = startTimeMins + 15;
			
			if (endTimeMins > 23*60 + 59)
			{
				endTimeMins = 23*60 + 59;
			}
			
			model.endTimeModel.setHour(endTimeMins / 60);
			model.endTimeModel.setMinute(endTimeMins % 60);
		}
	}
}

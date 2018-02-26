package com.ca.arcflash.ui.client.backup.advschedule;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.FlashUIMessages;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils.ScheduleTypeModel;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class ScheduleAddItemRegularBackupPanel extends ScheduleAddItemBasePanel {
	private ScheduleItemModel model;
	private ScheduleAddDetailItemTypePanel jobTypePanel;
	private ScheduleAddDetailItemTimePanel startTimePanel;
	private ScheduleAddDetailItemTimePanel endTimePanel;
	private ScheduleAddDetailItemDaySelectionPanel daySelectionPanel;
	private ScheduleAddDetailItemRepeatTimePanel repeatPanel;
	private LayoutContainer repeatContainer;
	private CheckBox repeatCheckBox;
	private FlashUIConstants uiConstants=UIContext.Constants;
	private static FlashUIMessages uiMessages= UIContext.Messages;
	public ScheduleAddItemRegularBackupPanel(ScheduleItemModel model) {
		this.model = model;
		this.ensureDebugId("28866937-2d84-4a4b-aa41-547a5a7bb0ff");
		LayoutContainer container = getBaseLayoutContainer();
		
		addJobTypeRow(container);
		addStartTimeRow(container);
		addSelectionDayRow(container);
		addRepeatFieldRow(container);	
		setDefaultJobType();
		this.add(container);
	}
	
	private void addStartTimeRow(LayoutContainer container){
		LabelField startLabel = new LabelField(UIContext.Constants.scheduleStartAt());
		DayTimeModel startDayTimeModel = new DayTimeModel();
		startDayTimeModel.setHour(8);
		startDayTimeModel.setMinute(0);
		startTimePanel = new ScheduleAddDetailItemTimePanel(startDayTimeModel);
		ScheduleUtils.addWidget(container,startLabel,startTimePanel);
	}
	
	private void addSelectionDayRow(LayoutContainer container){
		LabelField dayLabel = new LabelField("");
		daySelectionPanel = new ScheduleAddDetailItemDaySelectionPanel();
		ScheduleUtils.addWidget(container,dayLabel,daySelectionPanel);		
	}
	
	private void addEndTimeRow(LayoutContainer container){
		LabelField endLabel = new LabelField(UIContext.Constants.scheduleStopAt());
		DayTimeModel endDayTimeModel = new DayTimeModel();
		endDayTimeModel.setHour(18);
		endDayTimeModel.setMinute(0);
		endTimePanel = new ScheduleAddDetailItemTimePanel(endDayTimeModel);
		container.add(endLabel);
		container.add(endTimePanel);
	}
	
	private void addRepeatFieldRow(LayoutContainer container){	
		LabelField repeatLabel = new LabelField(uiConstants.regularScheduleRepeat());		
		repeatCheckBox = new CheckBox();
		repeatCheckBox.setBoxLabel("");
		repeatCheckBox.setValue(true);
		repeatCheckBox.addListener(Events.Change, new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				repeatContainer.setEnabled(repeatCheckBox.getValue());
			}
		});	
		ScheduleUtils.addWidget(container, repeatLabel, repeatCheckBox);
		
		repeatContainer = new LayoutContainer();		
		repeatContainer.setBorders(true);
		TableLayout tl = new TableLayout(2);
		tl.setWidth("100%");
		tl.setCellSpacing(5);
		repeatContainer.setLayout(tl);	
		
		LabelField everyLabel = new LabelField(uiConstants.regularScheduleEvery()); 
		repeatPanel = new ScheduleAddDetailItemRepeatTimePanel();
		repeatContainer.add(everyLabel);
		repeatContainer.add(repeatPanel);
		
		addEndTimeRow(repeatContainer);
		
		ScheduleUtils.addWidget(container, new LabelField(""), repeatContainer);

	}	

	protected void addJobTypeRow(LayoutContainer container){
		LabelField label = new LabelField(UIContext.Constants.periodBackupType());
		jobTypePanel = new ScheduleAddDetailItemTypePanel();		
		ScheduleUtils.addWidget(container, label, jobTypePanel);
	}
	
	protected void setDefaultJobType(){
		jobTypePanel.setValue(ScheduleUtils.INC_BACKUP);
	}
	
	public void setRepeatFieldEnable(boolean isEnable){
		repeatCheckBox.setValue(isEnable);
		repeatCheckBox.setEnabled(isEnable);
		repeatContainer.setEnabled(isEnable);
	}
	
	@Override
	public ScheduleItemModel save() {
		saveItemModel(model);		
		return model;
	}
	
	private void saveItemModel(ScheduleItemModel itemModel){
		itemModel.setScheduleType(ScheduleTypeModel.RepeatJob);
		itemModel.setJobType(jobTypePanel.getValue());
		itemModel.startTimeModel = startTimePanel.getValue();
		daySelectionPanel.getValue(itemModel);
		itemModel.endTimeModel = endTimePanel.getValue();
		repeatPanel.getValue(itemModel);
		itemModel.setRepeatEnabled(repeatCheckBox.getValue());
		if (repeatCheckBox.getValue()) {
			itemModel.setDescription(uiMessages.scheduleDescriptionBackup(
					ScheduleUtils.getJobTypeStr(itemModel.getJobType()),
					ScheduleUtils.getScheduleRepeatStr(itemModel.getInterval(),
							itemModel.getIntervalUnit())));
		} else {
			itemModel.setDescription(uiMessages.scheduleDescriptionBackupNonRepeat(
							ScheduleUtils.getJobTypeStr(itemModel.getJobType()),
							ScheduleUtils.formatTime(itemModel.startTimeModel)));
		}		
		adjustEndTimeForRepeatEnabled(itemModel);
	}
	
	@Override
	public boolean validate() {
		if(!startTimePanel.validate() || !endTimePanel.validate())
			return false;
		
		if (repeatCheckBox.getValue() && (!repeatPanel.validateRepeatVal(startTimePanel.getTimeModel(), endTimePanel.getTimeModel())))
				return false;
	
		List<Integer> dayList = daySelectionPanel.getDayIndexs();
		if(dayList.isEmpty()){
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(),uiConstants.selectADay(), MessageBox.ERROR);			
			return false;
		}
		
		return true;
	}
	
	public int getJobType(){
		return jobTypePanel.getValue();
	}
	
	private void adjustEndTimeForRepeatEnabled(ScheduleItemModel model)
	{
		// if repeat is disabled, set the end time = start time + 15 minutes
		if (model != null && model.isRepeatEnabled() != null && !model.isRepeatEnabled())
		{
			model.setInterval(15);
			model.setIntervalUnit(ScheduleAddDetailItemRepeatTimePanel.Minute_Unit);
				
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
	
	@Override
	public ScheduleItemModel getCurrentModel() {
		ScheduleItemModel itemModel = new ScheduleItemModel();
		saveItemModel(itemModel);
		return itemModel;
	}

	@Override
	public void updateData() {
		jobTypePanel.setValue(model.getJobType());
		startTimePanel.setValue(model.startTimeModel);
		repeatCheckBox.setValue(model.isRepeatEnabled());
		if(model.isRepeatEnabled()){
			endTimePanel.setValue(model.endTimeModel);
			repeatPanel.setValue(model.getInterval(), model.getIntervalUnit());
		}
		
		List<Integer> modifyItemIdxList = new ArrayList<Integer>();
		for(int i=0; i<7; i++){
			if(model.getDayofWeek(i)==ScheduleUtils.SELECTED)
				modifyItemIdxList.add(i);
		}
		daySelectionPanel.setValue(modifyItemIdxList);		
	}
	
}

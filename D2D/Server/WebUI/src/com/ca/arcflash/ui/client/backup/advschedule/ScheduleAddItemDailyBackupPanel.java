package com.ca.arcflash.ui.client.backup.advschedule;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.FlashUIMessages;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.EveryDayScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils.ScheduleTypeModel;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;

public class ScheduleAddItemDailyBackupPanel extends ScheduleAddItemBasePanel {
	private ScheduleItemModel model;
	private ScheduleAddDetailItemTypePanel jobTypePanel;
//	private ScheduleAddDetailItemCatalogPanel catalogPanel;
	private ScheduleAddDetailItemTimePanel startTimePanel;
	private ScheduleAddDetailItemDaySelectionPanel daySelectionPanel;
	private NumberField recoveryPoint;
	private static FlashUIMessages uiMessages=UIContext.Messages;
	private static FlashUIConstants uiConstants=UIContext.Constants;
	public ScheduleAddItemDailyBackupPanel(ScheduleItemModel model){
		this.model = model;
		this.ensureDebugId("28866937-2d84-4a4b-aa41-547a5a7bb07f");
		LayoutContainer container = getBaseLayoutContainer();
		
		addJobTypeRow(container);
//		addCatalogRow(container);
		addStartTimeRow(container);
		addSelectionDayRow(container);
		addRetentionCountRow(container);
		setDefaultJobType();
		this.add(container);
	}
	
	protected void setDefaultJobType(){
		jobTypePanel.setValue(ScheduleUtils.INC_BACKUP);
	}
	
	private void addJobTypeRow(LayoutContainer container){
		LabelField label = new LabelField(UIContext.Constants.periodBackupType());
		jobTypePanel = new ScheduleAddDetailItemTypePanel();		
		ScheduleUtils.addWidget(container, label, jobTypePanel);
	}
	
	private void addSelectionDayRow(LayoutContainer container){
		LabelField dayLabel = new LabelField("");
		daySelectionPanel = new ScheduleAddDetailItemDaySelectionPanel();
		ScheduleUtils.addWidget(container,dayLabel,daySelectionPanel);		
	}
	
//	private void addCatalogRow(LayoutContainer container){
//		LabelField label = new LabelField(uiConstants.scheduleCatalog());
//		catalogPanel = new ScheduleAddDetailItemCatalogPanel();		
//		ScheduleUtils.addWidget(container, label, catalogPanel);
//	}
	
	private void addStartTimeRow(LayoutContainer container){
		LabelField startLabel = new LabelField(UIContext.Constants.scheduleStartAt());
		DayTimeModel startDayTimeModel = new DayTimeModel();
		startDayTimeModel.setHour(22);
		startDayTimeModel.setMinute(0);
		startTimePanel = new ScheduleAddDetailItemTimePanel(startDayTimeModel);
		ScheduleUtils.addWidget(container,startLabel,startTimePanel);
	}
	
	private void addRetentionCountRow(LayoutContainer container){
		LabelField retentionLabel = new LabelField(uiConstants.retentionCount());
		recoveryPoint = new NumberField();
		recoveryPoint.setValue(7);
		recoveryPoint.setMaxValue(UIContext.maxRecoveryPointLimit);
		recoveryPoint.setMinValue(1);
		recoveryPoint.setWidth(180);
		recoveryPoint.setAllowBlank(false);
		recoveryPoint.setAllowNegative(false);
		recoveryPoint.setAllowDecimals(false);
		ScheduleUtils.addWidget(container,retentionLabel,recoveryPoint);
	}
	
	@Override
	public ScheduleItemModel getCurrentModel() {
		ScheduleItemModel itemModel = new ScheduleItemModel();
		saveModelValue(itemModel);
		return itemModel;

	}
	
	@Override
	public ScheduleItemModel save() {
		saveModelValue(model);
		
		return model;
	}
	
	private void saveModelValue(ScheduleItemModel itemModel){
		itemModel.setScheduleType(ScheduleTypeModel.OnceDailyBackup);
		itemModel.startTimeModel = startTimePanel.getTimeModel();
		itemModel.setJobType(jobTypePanel.getValue());	
		itemModel.setDescription(uiMessages.scheduleDescriptionDailyBackup(ScheduleUtils.getJobTypeStr(itemModel.getJobType())));
		daySelectionPanel.getValue(itemModel);
		
//		boolean generateCatalog = catalogPanel.getValue();
		EveryDayScheduleModel daily = new EveryDayScheduleModel();
		daily.setEnabled(true);
		daily.setBkpType(jobTypePanel.getValue());
		daily.setDayTime(itemModel.startTimeModel);
		daily.setRetentionCount(recoveryPoint.getValue().intValue());	
		Boolean[] dayenabled=new Boolean[7];
		for(int i=0;i<7;i++){
			dayenabled[i]=itemModel.getDayofWeek(i)==1;
		}
		daily.setDayEnabled(dayenabled);
//		daily.setGenerateCatalog(generateCatalog);
		
		itemModel.setEveryDaySchedule(daily);
	}
	
	@Override
	public boolean validate() {
		if(!startTimePanel.validate() || recoveryPoint.getValue() == null || !recoveryPoint.validate())
			return false;
		List<Integer> dayList = daySelectionPanel.getDayIndexs();
		if(dayList.isEmpty()){
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(),uiConstants.selectADay(), MessageBox.ERROR);			
			return false;
		}
		return true;
	}

	@Override
	public void updateData() {
		jobTypePanel.setValue(model.getEveryDaySchedule().getBkpType());
//		catalogPanel.setValue(model.getEveryDaySchedule().isGenerateCatalog()? 1: 0);
		startTimePanel.setValue(model.getEveryDaySchedule().getDayTime());
		recoveryPoint.setValue(model.getEveryDaySchedule().getRetentionCount());	
		List<Integer> modifyItemIdxList = new ArrayList<Integer>();
		for(int i=0; i<7; i++){
			if(model.getDayofWeek(i)==ScheduleUtils.SELECTED)
				modifyItemIdxList.add(i);
		}
		daySelectionPanel.setValue(modifyItemIdxList);
	}
	
}

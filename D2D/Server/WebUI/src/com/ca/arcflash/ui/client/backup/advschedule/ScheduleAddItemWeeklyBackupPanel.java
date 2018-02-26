package com.ca.arcflash.ui.client.backup.advschedule;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.FlashUIMessages;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.EveryWeekScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils.ScheduleTypeModel;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.UILayoutConstans;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;

public class ScheduleAddItemWeeklyBackupPanel extends ScheduleAddItemBasePanel {
	private ScheduleItemModel model;	
	private ScheduleAddDetailItemTypePanel jobTypePanel;
//	private ScheduleAddDetailItemCatalogPanel catalogPanel;
	private ScheduleAddDetailItemTimePanel startTimePanel;

	private NumberField recoveryPoint;
	private ComboBox<ModelData> comboWeeklyDayOfWeek;
	private static FlashUIConstants uiConstants= UIContext.Constants;
	private static FlashUIMessages uiMessages= UIContext.Messages;
	
	public ScheduleAddItemWeeklyBackupPanel(ScheduleItemModel model){
		this.model = model;
		this.ensureDebugId("28866937-2d84-4a4b-aa41-547a5a7bb07f");	
		LayoutContainer container = getBaseLayoutContainer();
		
		addJobTypeRow(container);
//		addCatalogRow(container);
		addStartTimeRow(container);
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
	
//	private void addCatalogRow(LayoutContainer container){
//		LabelField label = new LabelField(uiConstants.scheduleCatalog());
//		catalogPanel = new ScheduleAddDetailItemCatalogPanel();		
//		ScheduleUtils.addWidget(container, label, catalogPanel);
//	}
	
	private void addStartTimeRow(LayoutContainer container){
		LabelField startLabel = new LabelField(UIContext.Constants.scheduleStartAt());
		HorizontalPanel timePanel = new HorizontalPanel(); 
		DayTimeModel startDayTimeModel = new DayTimeModel();
		startDayTimeModel.setHour(22);
		startDayTimeModel.setMinute(0);
		startTimePanel = new ScheduleAddDetailItemTimePanel(startDayTimeModel);
		timePanel.add(startTimePanel);
		comboWeeklyDayOfWeek = getWeekCombox();
		comboWeeklyDayOfWeek.setWidth(UILayoutConstans.UI_RIGHT_TEXT_WIDTH_HALF);
		comboWeeklyDayOfWeek.setStyleAttribute("padding-left", "10px");
		timePanel.add(comboWeeklyDayOfWeek);
		ScheduleUtils.addWidget(container,startLabel,timePanel);
	}	

	private void addRetentionCountRow(LayoutContainer container){
		LabelField retentionLabel = new LabelField(uiConstants.retentionCount());
		recoveryPoint = new NumberField();
		recoveryPoint.setValue(5);
		recoveryPoint.setMaxValue(UIContext.maxRecoveryPointLimit);
		recoveryPoint.setMinValue(1);
		recoveryPoint.setWidth(180);
		recoveryPoint.setAllowBlank(false);
		recoveryPoint.setAllowNegative(false);
		recoveryPoint.setAllowDecimals(false);
		ScheduleUtils.addWidget(container,retentionLabel,recoveryPoint);
	}
	
	private ComboBox<ModelData> getWeekCombox() {
		ListStore<ModelData> weekStore = new ListStore<ModelData>();
		BaseComboBox<ModelData> cbWeek = new BaseComboBox<ModelData>();
		cbWeek.setDisplayField("text");
		cbWeek.setValueField("dayOfWeek");
		cbWeek.setStore(weekStore);
		cbWeek.setEditable(false);
		cbWeek.setAllowBlank(false);	

		ModelData selectModel = null;
		for (int i = 0; i < ScheduleUtils.oneWeek.length; i++) {
			ModelData md = new BaseModelData();
			md.set("text", ScheduleUtils.oneWeek[i]);
			md.set("dayOfWeek", i + 1);
			weekStore.add(md);

			if (UIContext.Constants.scheduleFriday().equals(ScheduleUtils.oneWeek[i])) {
				selectModel = md;
			}
		}
		cbWeek.setValue(selectModel);
		return cbWeek;
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
		itemModel.setScheduleType(ScheduleTypeModel.OnceWeeklyBackup);
		itemModel.startTimeModel = startTimePanel.getTimeModel();
		itemModel.setJobType(jobTypePanel.getValue());		
		itemModel.setDescription(uiMessages.scheduleDescriptionWeeklyBackup(ScheduleUtils.getJobTypeStr(itemModel.getJobType())));

		for(int i=0; i<7; i++){
			itemModel.setDayofWeek(i, 0);
		}
		int dayOfWeek = comboWeeklyDayOfWeek.getValue().<Integer> get(comboWeeklyDayOfWeek.getValueField());
		itemModel.setDayofWeek((dayOfWeek-1), 1);
		
//		boolean generateCatalog = catalogPanel.getValue();
		EveryWeekScheduleModel weekly = new EveryWeekScheduleModel();
		weekly.setEnabled(true);
		weekly.setBkpType(jobTypePanel.getValue());
		weekly.setDayOfWeek(dayOfWeek);
		weekly.setRetentionCount(recoveryPoint.getValue().intValue());
//		weekly.setGenerateCatalog(generateCatalog);
		weekly.setDayTime(startTimePanel.getTimeModel());
		itemModel.setEveryWeekSchedule(weekly);
	}
	
	@Override
	public boolean validate() {
		if(!startTimePanel.validate() || recoveryPoint.getValue()==null || !recoveryPoint.validate())
			return false;
		
		return true;
	}

	@Override
	public void updateData() {
		jobTypePanel.setValue(model.getEveryWeekSchedule().getBkpType());
//		catalogPanel.setValue(model.getEveryWeekSchedule().isGenerateCatalog()?1:0);
		startTimePanel.setValue(model.getEveryWeekSchedule().getDayTime());
		recoveryPoint.setValue(model.getEveryWeekSchedule().getRetentionCount());
		for(ModelData weekDayModel : comboWeeklyDayOfWeek.getStore().getModels()) {
			if(((Integer)weekDayModel.get("dayOfWeek")) == model.getEveryWeekSchedule().getDayOfWeek()){
				comboWeeklyDayOfWeek.setValue(weekDayModel);
				return;
			}			
		}	
	}
	
}

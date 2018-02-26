package com.ca.arcflash.ui.client.backup.advschedule;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.FlashUIMessages;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.EveryMonthScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils.ScheduleTypeModel;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import java.util.Date;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class ScheduleAddItemMonthlyBackupPanel extends ScheduleAddItemBasePanel {
	private ScheduleItemModel model;	
	private ScheduleAddDetailItemTypePanel jobTypePanel;
//	private ScheduleAddDetailItemCatalogPanel catalogPanel;
	private ScheduleAddDetailItemTimePanel startTimePanel;
	
	private NumberField recoveryPoint;
	private static FlashUIMessages uiMessages=UIContext.Messages;
	private static FlashUIConstants uiConstants=UIContext.Constants;
	private RadioGroup rgDayOfMonth;
	private Radio rdDayOfMonth;
	private ComboBox<ModelData> comboDayOfMonth;
	private Radio rdWeekOfMonth;
	private ComboBox<ModelData> comboWeekNumOfMonth;
	private ComboBox<ModelData> comboWeekDayOfMonth;
	
	public ScheduleAddItemMonthlyBackupPanel(ScheduleItemModel model){
		this.model = model;
		this.ensureDebugId("28866937-2d84-4a4b-aa41-547a5a7bb07f");
		LayoutContainer container = getBaseLayoutContainer();
		
		addJobTypeRow(container);
//		addCatalogRow(container);
		addStartTimeRow(container);
		addDayOrWeekRow(container);
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
		DayTimeModel startDayTimeModel = new DayTimeModel();
		startDayTimeModel.setHour(22);
		startDayTimeModel.setMinute(0);
		startTimePanel = new ScheduleAddDetailItemTimePanel(startDayTimeModel);
		ScheduleUtils.addWidget(container,startLabel,startTimePanel);
	}
	
	private void addDayOrWeekRow(LayoutContainer container){
		LabelField startLabel = new LabelField("");
		LayoutContainer dayweekContainer = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(3);
		tl.setCellPadding(4);
		tl.setCellSpacing(0);
		dayweekContainer.setLayout(tl);
		
		rgDayOfMonth = new RadioGroup();
		//shaji02: for adding debug id.
		rgDayOfMonth.ensureDebugId("8728887d-646c-47df-a9da-23f01643b92d");
		rgDayOfMonth.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				if (rgDayOfMonth.getValue() == rdDayOfMonth) {
					comboDayOfMonth.setEnabled(true);
					comboWeekDayOfMonth.setEnabled(false);
					comboWeekNumOfMonth.setEnabled(false);
				} else {
					comboDayOfMonth.setEnabled(false);
					comboWeekDayOfMonth.setEnabled(true);
					comboWeekNumOfMonth.setEnabled(true);
				}
			}
		});
		rdDayOfMonth = new Radio();
		//shaji02: for adding debug id.
		rdDayOfMonth.ensureDebugId("4b782430-fffc-44a8-b1bf-dc3b3ce5c699");
		rdDayOfMonth.setValue(true);
		rdDayOfMonth.setBoxLabel(UIContext.Constants.Day());
		TableData daytd = new TableData();
		daytd.setWidth("69px");
		dayweekContainer.add(rdDayOfMonth, daytd);
		rgDayOfMonth.add(rdDayOfMonth);

		comboDayOfMonth = new BaseComboBox<ModelData>();
		comboDayOfMonth.setEditable(false);
		comboDayOfMonth.setValueField("value");
		ListStore<ModelData> store = new ListStore<ModelData>();		
		ModelData defaultmd = null;
		for (int i = 1; i < 33; i += 1) {
			ModelData md = new BaseModelData();
			md.set("value", i);
			if (i == 1)
				defaultmd = md;
			if (i == 32) {
				md.set("text", UIContext.Constants.selectLastDayOfMonth());

			} else
				md.set("text", i + "");

			store.add(md);
		}
		comboDayOfMonth.setStore(store);
		comboDayOfMonth.setValue(defaultmd);
		Utils.addToolTip(comboDayOfMonth, UIContext.Constants.selectDayofMonthTooltip());
		comboDayOfMonth.setWidth("100px");
		TableData tweek = new TableData();
		tweek.setColspan(2);
		dayweekContainer.add(comboDayOfMonth, tweek);

		// Sub Item4
		rdWeekOfMonth = new Radio();
		//shaji02: for adding debug id.
		rdWeekOfMonth.ensureDebugId("2095207f-ca1e-419c-9d44-18fd1e57cf21");
		rdWeekOfMonth.setBoxLabel(UIContext.Constants.Week());
		dayweekContainer.add(rdWeekOfMonth, daytd);
		rgDayOfMonth.add(rdWeekOfMonth);

		comboWeekNumOfMonth = getWeekNumCombox();
		comboWeekNumOfMonth.setWidth("100px");
		comboWeekNumOfMonth.setEnabled(false);
		
		dayweekContainer.add(comboWeekNumOfMonth);
		comboWeekDayOfMonth = getWeekCombox();
		comboWeekDayOfMonth.setEnabled(false);
		comboWeekDayOfMonth.setWidth("110px");	
		comboWeekDayOfMonth.setStyleAttribute("padding-left", "5px");
		
		dayweekContainer.add(comboWeekDayOfMonth);

		
		ScheduleUtils.addWidget(container,startLabel,dayweekContainer);
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
	
	private ComboBox<ModelData> getWeekNumCombox() {
		ListStore<ModelData> weekStore = new ListStore<ModelData>();
		BaseComboBox<ModelData> comboWeekNum = new BaseComboBox<ModelData>();
		comboWeekNum.setDisplayField("text");
		comboWeekNum.setValueField("value");
		comboWeekNum.setStore(weekStore);
		comboWeekNum.setEditable(false);
		comboWeekNum.setAllowBlank(false);

		String[] weekNum = {UIContext.Constants.Last(), UIContext.Constants.First()};		

		ModelData selectModel = null;
		for (int i = 0; i < weekNum.length; i++) {
			ModelData md = new BaseModelData();
			md.set("text", weekNum[i]);
			md.set("value", i);
			weekStore.add(md);

			if (selectModel == null) {
				selectModel = md;
			}
		}
		comboWeekNum.setValue(selectModel);
		return comboWeekNum;
	}
	
	private void addRetentionCountRow(LayoutContainer container){
		LabelField retentionLabel = new LabelField(uiConstants.retentionCount());
		recoveryPoint = new NumberField();
		recoveryPoint.setValue(12);
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
		itemModel.setScheduleType(ScheduleTypeModel.OnceMonthlyBackup);
		itemModel.startTimeModel = startTimePanel.getTimeModel();
		itemModel.setJobType(jobTypePanel.getValue());		
		itemModel.setDescription(uiMessages.scheduleDescriptionMonthlyBackup(ScheduleUtils.getJobTypeStr(itemModel.getJobType())));
		
		boolean dayOfMonthEnabled = false;
		boolean weekOfMonthEnabled = false;

		if (rgDayOfMonth.getValue() == rdDayOfMonth)
			dayOfMonthEnabled = true;
		else
			weekOfMonthEnabled = true;

		int weekDayOfMonth = comboWeekDayOfMonth.getValue().<Integer> get(comboWeekDayOfMonth.getValueField());
		int weekNumOfMonth = comboWeekNumOfMonth.getValue().<Integer> get(comboWeekNumOfMonth.getValueField());
		int dayOfMonth = comboDayOfMonth.getValue().<Integer> get(comboDayOfMonth.getValueField());
//		boolean generateCatalog = catalogPanel.getValue();
				
		EveryMonthScheduleModel monthly =  new EveryMonthScheduleModel();
		monthly.setWeekNumOfMonth(weekNumOfMonth);
		monthly.setWeekDayOfMonth(weekDayOfMonth);
		monthly.setDayOfMonth(dayOfMonth);
		monthly.setRetentionCount(recoveryPoint.getValue().intValue());
//		monthly.setGenerateCatalog(generateCatalog);
		monthly.setDayOfMonthEnabled(dayOfMonthEnabled);
		monthly.setWeekOfMonthEnabled(weekOfMonthEnabled);
		monthly.setBkpType(jobTypePanel.getValue());
		monthly.setEnabled(true);		
		monthly.setDayTime(itemModel.startTimeModel);

		itemModel.setEveryMonthSchedule(monthly);
		
		Date scheduleDate = ModelConverter.calendarConvertToDate(monthly);
		DateWrapper dateWrapper = new DateWrapper(scheduleDate);
		int nDayOfWeek = dateWrapper.getDayInWeek();
		
		for(int i=0; i<7; i++){
			itemModel.setDayofWeek(i, 0);
		}
		
		itemModel.setDayofWeek(nDayOfWeek, ScheduleUtils.SELECTED);
		itemModel.setEveryMonthDate(String.valueOf(scheduleDate.getMonth()+1) + "/" + String.valueOf(scheduleDate.getDate()));			
		
	}
		

	@Override
	public boolean validate() {
		if(!startTimePanel.validate() || recoveryPoint.getValue() == null || !recoveryPoint.validate())
			return false;
		
		return true;
	}

	@Override
	public void updateData() {
		jobTypePanel.setValue(model.getEveryMonthSchedule().getBkpType());
//		catalogPanel.setValue(model.getEveryMonthSchedule().isGenerateCatalog()?1:0);
		startTimePanel.setValue(model.getEveryMonthSchedule().getDayTime());
		recoveryPoint.setValue(model.getEveryMonthSchedule().getRetentionCount());
		rdDayOfMonth.setValue(model.getEveryMonthSchedule().isDayOfMonthEnabled());
		rdWeekOfMonth.setValue(model.getEveryMonthSchedule().isWeekOfMonthEnabled());
		if(model.getEveryMonthSchedule().isDayOfMonthEnabled()){
			for(ModelData dayModel : comboDayOfMonth.getStore().getModels()) {
				if(((Integer)dayModel.get("value")) == model.getEveryMonthSchedule().getDayOfMonth()){
					comboDayOfMonth.setValue(dayModel);
					break;
				}			
			}	
		}
		
		if(model.getEveryMonthSchedule().isWeekOfMonthEnabled()){
			for(ModelData weekNumModel : comboWeekNumOfMonth.getStore().getModels()) {
				if(((Integer)weekNumModel.get("value")) == model.getEveryMonthSchedule().getWeekNumOfMonth()){
					comboWeekNumOfMonth.setValue(weekNumModel);
					break;
				}			
			}
		
			for(ModelData weekDayModel : comboWeekDayOfMonth.getStore().getModels()) {
				if(((Integer)weekDayModel.get("dayOfWeek")) == model.getEveryMonthSchedule().getWeekDayOfMonth()){
					comboWeekDayOfMonth.setValue(weekDayModel);
					break;
				}			
			}	
		}
		
	}
	
}

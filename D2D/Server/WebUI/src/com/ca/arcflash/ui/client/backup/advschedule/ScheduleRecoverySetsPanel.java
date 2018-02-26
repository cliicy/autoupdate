package com.ca.arcflash.ui.client.backup.advschedule;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.common.AdvScheduleUtil;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.HasValidateValue;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.RetentionPolicyModel;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.user.client.ui.Widget;

public class ScheduleRecoverySetsPanel extends LayoutContainer implements HasValidateValue<BackupSettingsModel>{
	private static FlashUIConstants uiConstants=UIContext.Constants;
	private NumberField recoverySetField;
	private CardLayout contentLayout;
	private LayoutContainer weeklyPanel;
	private LayoutContainer monthlyPanel;
	private Radio weekRadio;
	private Radio monthRadio;	
	private ComboBox<ModelData> comboWeekDay;
	private ComboBox<ModelData> comboWeekNum;
	private ComboBox<ModelData> comboWeekNumOfMonth;
	private ComboBox<ModelData> comboDayOfMonth;
	private Radio lastDayRadio;
	private Radio dateRadio;
	
	public ScheduleRecoverySetsPanel(){
		LayoutContainer container = this;
		LabelField labelField = new LabelField(uiConstants.recoverySetNumbers());
		labelField.setStyleName("schedule_item_lable");
		container.setStyleAttribute("margin-top", "10px");
		container.setStyleAttribute("position", "relative");
		container.add(labelField);		
		container.add(createRecoverySetPanel());
	}
	
	private LayoutContainer createRecoverySetPanel() {
		LayoutContainer recoverySetPanel = new LayoutContainer();
		recoverySetPanel.setLayout(AdvScheduleUtil.createLineLayout());
		recoverySetPanel.setStyleAttribute("margin-top", "10px");
		recoverySetPanel.add(AdvScheduleUtil.createFormLayout(uiConstants.recoverySetStart(), createWeeklyOrMonthlyPanel()), AdvScheduleUtil.createLineLayoutData());
		recoverySetField = new NumberField();
		recoverySetField.setMaxValue(UIContext.maxBSLimit);
		recoverySetField.setMinValue(1);
		recoverySetField.setValue(2);
		recoverySetField.setAllowBlank(false);
		recoverySetField.setAllowDecimals(false);
		recoverySetField.setValidateOnBlur(true);
		recoverySetField.setWidth(120);
		recoverySetField.ensureDebugId("3DAC17D9-EBB7-4d54-B340-35DB7D19765B");
		recoverySetField.getMessages().setMaxText(
	    		UIContext.Messages.settingsBackupSetCountExceedMax(UIContext.maxBSLimit));
		recoverySetField.getMessages().setMinText(
	    		UIContext.Constants.settingsRetentionCountErrorTooLow());
		Utils.addToolTip(recoverySetField, UIContext.Constants.backupsetNumberTooltip());
		recoverySetPanel.add(AdvScheduleUtil.createFormLayout(uiConstants.recoverySetNumbersRetain(), recoverySetField), AdvScheduleUtil.createLineLayoutData());
		return recoverySetPanel;
	}

	private LayoutContainer createWeeklyOrMonthlyPanel(){
		HorizontalPanel panel = new HorizontalPanel();
		panel.setHeight(100);
		LayoutContainer rightPanelContainer = createRightPanel();
		LayoutContainer leftPanelContainer = createLeftPanel();
		panel.add(leftPanelContainer);
		panel.add(rightPanelContainer);
		return panel;
	}
	
	private LayoutContainer createLeftPanel(){
		LayoutContainer leftContainer = new LayoutContainer();
		RadioGroup rsRG = new RadioGroup();
		rsRG.setOrientation(Orientation.VERTICAL);
		weekRadio = new Radio();
		weekRadio.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				contentLayout.setActiveItem(weeklyPanel);
			}
		});			
		weekRadio.setBoxLabel(uiConstants.recoverySetWeekly());
		weekRadio.setValue(true);
		leftContainer.add(weekRadio);
		
		monthRadio = new Radio();
		monthRadio.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				contentLayout.setActiveItem(monthlyPanel);				
			}
		});			
		monthRadio.setBoxLabel(uiConstants.recoverySetMonthly());
		leftContainer.add(monthRadio);
		rsRG.add(weekRadio);
		rsRG.add(monthRadio);
		return leftContainer;
	}
	
	private LayoutContainer createRightPanel(){
		LayoutContainer rightContainer = new LayoutContainer();
		contentLayout = new CardLayout();
		rightContainer.setLayout(contentLayout);
		weeklyPanel = createWeeklyPanel();
		monthlyPanel = createMonthlyPanel();
		rightContainer.add(weeklyPanel);
		rightContainer.add(monthlyPanel);
		return rightContainer;
	}
	
	private LayoutContainer addWidget(String name, Widget[] comps, VerticalAlignment verticalAlignment){
		HorizontalPanel p=new HorizontalPanel();
		p.ensureDebugId("133be4d9-0fb4-467c-a6d2-f022eb5a3c08");
		TableData td = new TableData();
		td.setWidth("60");
		td.setVerticalAlign(verticalAlignment);
		LabelField labelField = new LabelField(name);
		labelField.setWidth(60);
		labelField.setStyleAttribute("white-space", "nowrap");
		p.add(labelField, td);	
		
		HorizontalPanel compPanel = new HorizontalPanel();
		int size = comps.length;
		td = new TableData();
		td.setVerticalAlign(verticalAlignment);
		td.setMargin(10);
		for (int index = 0; index < size; index++) {
			compPanel.add(comps[index],td);
		}
		
		td = new TableData();
		td.setVerticalAlign(verticalAlignment);
		p.add(compPanel, td);
		
		return p;
		
	}
	
	private LayoutContainer createWeeklyPanel(){
		LayoutContainer weeklyContainer = new LayoutContainer();
		weeklyContainer.setStyleAttribute("margin-left", "10px");
		weeklyContainer.setWidth(300);

		comboWeekDay = getWeekCombox();		
		weeklyContainer.add(addWidget(uiConstants.recoverSetOn(), new Widget[]{comboWeekDay}, VerticalAlignment.TOP), new FlowData(0,0,10,0));
		
		//with the first/last recovery point
		comboWeekNum = getWeekNumCombox();
		LabelField recoverySetLable = new LabelField(uiConstants.recoverySetRecoveryPoint());
		recoverySetLable.setStyleAttribute("white-space", "nowrap");
		weeklyContainer.add(addWidget(uiConstants.recoverySetWithThe(),new Widget[]{comboWeekNum, recoverySetLable}, VerticalAlignment.MIDDLE),new FlowData(0,0,10,0));
		
		return weeklyContainer;
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

			if (UIContext.Constants.First().equals(weekNum[i])) {
				selectModel = md;
			}
		}
		comboWeekNum.setValue(selectModel);
		return comboWeekNum;
	}
	
	private LayoutContainer createMonthlyPanel(){
		LayoutContainer monthlyContainer = new LayoutContainer();
		monthlyContainer.setWidth(300);
		monthlyContainer.setStyleAttribute("margin-left", "10px");		

		RadioGroup rsRG = new RadioGroup();
		rsRG.setOrientation(Orientation.VERTICAL);
		lastDayRadio = new Radio();
		lastDayRadio.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				if(comboDayOfMonth != null)
					comboDayOfMonth.disable();				
			}
		});				
		
		lastDayRadio.setBoxLabel(uiConstants.recoverySetLastDay());
		lastDayRadio.setValue(true);
		monthlyContainer.add(addWidget(uiConstants.recoverySetOnThe(), new Widget[]{lastDayRadio} , VerticalAlignment.TOP),new FlowData(0,0,10,0));
		
		//on the date
	
		dateRadio = new Radio();
		dateRadio.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				if(comboDayOfMonth != null)
					comboDayOfMonth.enable();				
			}
		});				
		dateRadio.setBoxLabel(uiConstants.recoverySetDate());
		rsRG.add(lastDayRadio);
		rsRG.add(dateRadio);	
		comboDayOfMonth = new BaseComboBox<ModelData>();
		comboDayOfMonth.setEditable(false);
		comboDayOfMonth.setValueField("value");
		ListStore<ModelData> store = new ListStore<ModelData>();		
		ModelData defaultmd = null;
		for (int i = 1; i < 32; i ++) {
			ModelData md = new BaseModelData();
			md.set("value", i);
			md.set("text", i + "");
			if (i == 1){			
				defaultmd = md;
			}						
			store.add(md);			
		}
		comboDayOfMonth.setStore(store);
		comboDayOfMonth.setValue(defaultmd);		
		comboDayOfMonth.setWidth("98px");
		comboDayOfMonth.disable();
		monthlyContainer.add(addWidget(" ", new Widget[]{dateRadio, comboDayOfMonth},VerticalAlignment.TOP ),new FlowData(0,0,10,0));
		
		//with the first/last recovery point	
		comboWeekNumOfMonth = getWeekNumCombox();
		LabelField recoverySetLable = new LabelField(uiConstants.recoverySetRecoveryPoint());
		recoverySetLable.setStyleAttribute("white-space", "nowrap");
		monthlyContainer.add(addWidget(uiConstants.recoverySetWithThe(), new Widget[]{comboWeekNumOfMonth, recoverySetLable},VerticalAlignment.MIDDLE ),new FlowData(0,0,10,0));
		
		return monthlyContainer;
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
			
			if (UIContext.Constants.scheduleSunday().equals(ScheduleUtils.oneWeek[i])) {
				selectModel = md;
			}
		}
		cbWeek.setValue(selectModel);
		return cbWeek;
	}
	
	@Override
	public void buildValue(BackupSettingsModel value) {
		RetentionPolicyModel retentionPolicy = value.retentionPolicy;
		retentionPolicy.setUseBackupSet(true);
		retentionPolicy.setBackupSetCount(recoverySetField.getValue().intValue());
		retentionPolicy.setUseWeekly(weekRadio.getValue());
		if(weekRadio.getValue()){
			retentionPolicy.setDayOfWeek(comboWeekDay.getValue().<Integer> get(comboWeekDay.getValueField()));
			retentionPolicy.setStartWithFirst(comboWeekNum.getValue().<Integer> get(comboWeekNum.getValueField())==1?true:false);
		}else{
			if(lastDayRadio.getValue())
				retentionPolicy.setDayOfMonth(32);
			else {
				retentionPolicy.setDayOfMonth(comboDayOfMonth.getValue().<Integer> get(comboDayOfMonth.getValueField()));
			}
			retentionPolicy.setStartWithFirst(comboWeekNumOfMonth.getValue().<Integer> get(comboWeekNumOfMonth.getValueField())==1?true:false);
		}		
	}

	@Override
	public void applyValue(BackupSettingsModel value) {
		RetentionPolicyModel retentionPolicy = value.retentionPolicy;
		recoverySetField.setValue(retentionPolicy.getBackupSetCount());
		weekRadio.setValue(retentionPolicy.isUseWeekly());
		monthRadio.setValue(!retentionPolicy.isUseWeekly());
		if(retentionPolicy.isUseWeekly()){
			for(ModelData weekDayModel : comboWeekDay.getStore().getModels()) {
				if(((Integer)weekDayModel.get("dayOfWeek")) == retentionPolicy.getDayOfWeek()){
					comboWeekDay.setValue(weekDayModel);
					break;
				}			
			}
			
			for(ModelData weekNumModel : comboWeekNum.getStore().getModels()){
				if(retentionPolicy.isStartWithFirst()){
					if(((Integer)weekNumModel.get("value")) == 1)
						comboWeekNum.setValue(weekNumModel);					
				}else{ 
					if((Integer)weekNumModel.get("value") == 0)
						comboWeekNum.setValue(weekNumModel);					
				}
			}
		}else{
			boolean bLastDay = (retentionPolicy.getDayOfMonth() == 32)?true:false;
			lastDayRadio.setValue(bLastDay);
			dateRadio.setValue(!bLastDay);
			if(!bLastDay){
				for(ModelData monthDayModel : comboDayOfMonth.getStore().getModels()){
					if(((Integer)monthDayModel.get("value")) == retentionPolicy.getDayOfMonth()){
						comboDayOfMonth.setValue(monthDayModel);
					}
				}
			}
			
			for(ModelData weekNumModel : comboWeekNumOfMonth.getStore().getModels()){
				if(retentionPolicy.isStartWithFirst()){
					if(((Integer)weekNumModel.get("value")) == 1)
						comboWeekNumOfMonth.setValue(weekNumModel);					
				}else{ 
					if((Integer)weekNumModel.get("value") == 0)
						comboWeekNumOfMonth.setValue(weekNumModel);					
				}
			}
		}
		
		
	}
	
	private boolean isNumberFieldValid(int value, int minValue, int maxValue) {
		if(value < minValue || value > maxValue){
			return false;
		}
		return true;
	}
	
	@Override
	public boolean validate() {
		boolean isValid = true;
		if(recoverySetField.isRendered()){
			isValid = this.recoverySetField.validate();
		}else{
			isValid = isNumberFieldValid( recoverySetField.getValue().intValue(), 1, (int)UIContext.maxBSLimit);
		}
		return isValid;
	}
	
}

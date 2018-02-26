package com.ca.arcflash.ui.client.backup.advschedule;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.FlashUIMessages;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils.ScheduleTypeModel;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class ScheduleAddItemThrottleWindow extends ScheduleAddItemBaseWindow{		
	private ScheduleAddDetailItemTimePanel startTimePanel;
	private ScheduleAddDetailItemTimePanel endTimePanel;
	private ScheduleAddDetailItemDaySelectionPanel daySelectionPanel;
	private NumberField throttleField;
	private static Integer allowMaxThrottleValue = new Integer(99999);
	private static Integer allowMinThrottleValue = new Integer(1);
	private ComboBox<ModelData> unitBox;
	private LabelField throttleUnitLabel;
	private final static String[] setUnit = { UIContext.Constants.scheduleThrottleUnitMbps(), UIContext.Constants.scheduleThrottleUnitKbps()};
	
	private static FlashUIConstants uiConstants= UIContext.Constants;
	private static FlashUIMessages uiMessages= UIContext.Messages;
	public ScheduleAddItemThrottleWindow(ScheduleItemModel model, boolean isNewAdd) {
		super(model, isNewAdd);	
		this.setWidth(510);
		this.setHeight(280);
		
		LayoutContainer container = new LayoutContainer();
		container.ensureDebugId("99585d0c-a23f-406e-9d14-d14bbd5971ff");
		container.setStyleAttribute("margin", "5px");
		this.add(container);
		
		TableLayout tl = new TableLayout(2);
		tl.setWidth("100%");
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		container.setLayout(tl);
		
		addThrottle(container);
		addStartTimeRow(container);
		addSelectionDayRow(container);
		addEndTimeRow(container);

	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);

		if(OKButtonListener!=null)
			OKButton.addSelectionListener(OKButtonListener);

	}
	
	private void addUnitComboBox(LayoutContainer container){
		ListStore<ModelData> unitStore = new ListStore<ModelData>();
		unitBox = new BaseComboBox<ModelData>();
		unitBox.setDisplayField("text");
		unitBox.setValueField("setUnit");
		unitBox.setStore(unitStore);
		unitBox.setEditable(false);
		unitBox.setAllowBlank(false);
		unitBox.setWidth(80);

		ModelData selectModel = null;
		for (int i = 0; i < setUnit.length; i++) {
			ModelData md = new BaseModelData();
			md.set("text", setUnit[i]);
			md.set("setUnit", i+1);
			unitStore.add(md);
			
			if(uiConstants.scheduleThrottleUnitMbps().equals(setUnit[i])) {
				selectModel = md;
			}
		}
		unitBox.setValue(selectModel);

		container.add(unitBox);

	}

	
	private void addThrottle(LayoutContainer container){
		LabelField startLabel = new LabelField(uiConstants.throughputLimit());
		HorizontalPanel throttlePanel = new HorizontalPanel();
		throttlePanel.setVerticalAlign(VerticalAlignment.MIDDLE);
		throttleField = new NumberField();
		throttleField.ensureDebugId("0d1e6551-8e0e-41c2-b430-b324946456ef");
		throttleField.setWidth(178);
		throttleField.setAllowBlank(false);
		throttleField.setAllowDecimals(false);
		throttleField.setAllowNegative(false);
		throttleField.setValue(null);
		throttleField.focus();
		throttleField.setMaxValue(allowMaxThrottleValue);
		throttleField.setMinValue(allowMinThrottleValue);
		throttleField.setStyleAttribute("margin-right", "20px");
		throttlePanel.add(throttleField);
		addUnitComboBox(throttlePanel);
		throttleUnitLabel = new LabelField(ScheduleUtils.getThrottleUnit(this.model.getThrottleUnit()));//new LabelField(uiConstants.scheduleThrottleUnitMBMin());
		throttlePanel.add(throttleUnitLabel);
		setThrottleUnitValue();
		ScheduleUtils.addWidget(container,startLabel,throttlePanel);
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
		ScheduleUtils.addWidget(container,endLabel,endTimePanel);
	}


	@Override
	public ScheduleItemModel save() {
		saveModelData(model);
		return model;		
	}
	
	@Override
	public boolean validate() {
		if(throttleField.getValue() == null || !throttleField.validate()){
			return false;
		}
	
		if(!startTimePanel.validate())
			return false;
		if(!endTimePanel.validate())
			return false;
		
		List<Integer> dayList = daySelectionPanel.getDayIndexs();
		if(dayList.isEmpty()){
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(),uiConstants.selectADay(), MessageBox.ERROR);			
			return false;
		}
		
		return true;
	}
	
	@Override
	public ScheduleItemModel getCurrentModel() {
		ScheduleItemModel itemModel = new ScheduleItemModel();
		saveModelData(itemModel);
		return itemModel;
	}
	
	private void saveModelData(ScheduleItemModel itemModel){
		itemModel.setScheduleType(ScheduleTypeModel.RepeatJob);
		itemModel.startTimeModel = startTimePanel.getTimeModel();
		itemModel.endTimeModel = endTimePanel.getTimeModel();
		itemModel.setJobType(ScheduleUtils.THROTTLE);
		daySelectionPanel.getValue(itemModel);
		itemModel.setThrottle(throttleField.getValue().longValue());
		itemModel.setThrottleUnit(getThrottleUnitValue());
		itemModel.setDescription(uiMessages.scheduleDescriptionThrottle(itemModel.getThrottle(), ScheduleUtils.getThrottleUnit(itemModel.getThrottleUnit())));
	}
	
	@Override
	protected String getScheduleAddWindowHeader(){
		return UIContext.Constants.scheduleThrottleAddWindowHeader();
	}
	
	@Override
	protected String getScheduleEditWindowHeader(){
		return UIContext.Constants.scheduleThrottleEditWindowHeader();
	}
	
	@Override
	protected String getScheduleHelpURL(){
		return UIContext.externalLinks.getBackupSettingAdvanceThrottlingHelp();
	}

	@Override
	public void updateData() {
		throttleField.setValue(model.getThrottle());
		startTimePanel.setValue(model.startTimeModel);
		endTimePanel.setValue(model.endTimeModel);
		List<Integer> modifyItemIdxList = new ArrayList<Integer>();
		for(int i=0; i<7; i++){
			if(model.getDayofWeek(i)==ScheduleUtils.SELECTED)
				modifyItemIdxList.add(i);
		}
		daySelectionPanel.setValue(modifyItemIdxList);
		
		setThrottleUnitValue();
		
	}
	
	private void setThrottleUnitValue(){
		if(model.getThrottleUnit() == ScheduleUtils.Mbps_Unit || model.getThrottleUnit() == ScheduleUtils.Kbps_Unit){
			unitBox.show();
			throttleUnitLabel.hide();
			
			for(ModelData unitModel : unitBox.getStore().getModels()) {
				if(((Integer)unitModel.get("setUnit")) == model.getThrottleUnit()){
					unitBox.setValue(unitModel);
					return;
				}			
			}			
		}else {
			unitBox.hide();
			throttleUnitLabel.show();			
		}

	}
	
	private int getThrottleUnitValue(){
		int nValue = ScheduleUtils.MB_MIN_Unit;
		if(unitBox.isVisible()){
			nValue = (Integer)unitBox.getValue().get("setUnit");
		}
		return nValue;
	}
}

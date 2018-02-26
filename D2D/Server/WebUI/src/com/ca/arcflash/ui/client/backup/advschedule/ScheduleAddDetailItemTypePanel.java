package com.ca.arcflash.ui.client.backup.advschedule;

import com.ca.arcflash.ui.client.backup.schedule.FlashFieldSetModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

public class ScheduleAddDetailItemTypePanel extends LayoutContainer {
	private BaseComboBox<FlashFieldSetModel> jobTypeBox;
	private ScheduleAddItemRegularBackupPanel parent;
	
	public ScheduleAddDetailItemTypePanel(){
		addJobTypeRow(this);
	}
	public ScheduleAddDetailItemTypePanel(ScheduleAddItemRegularBackupPanel parent){
		this.parent = parent;
		addJobTypeRow(this);
	}
	
	private void addJobTypeRow(LayoutContainer container){
		ListStore<FlashFieldSetModel> jobTypeModels = ScheduleUtils.getBackupTypeModels();
		jobTypeBox = new BaseComboBox<FlashFieldSetModel>();
		jobTypeBox.setDisplayField("text");
		jobTypeBox.setValueField("value");
		jobTypeBox.ensureDebugId("e8fb7e78-ccd3-4751-b898-78d24250387d");
		jobTypeBox.setStore(jobTypeModels);
		jobTypeBox.setEditable(false);
		jobTypeBox.setAllowBlank(false);
		jobTypeBox.setWidth(180);
		jobTypeBox.addSelectionChangedListener(new SelectionChangedListener<FlashFieldSetModel>(){	
			@Override
			public void selectionChanged(SelectionChangedEvent<FlashFieldSetModel> se) {
				onJobTypeChanged(se.getSelectedItem());				
			}
			
		});
		//setJobType(jobTypeBox, ScheduleUtils.INC_BACKUP);		
		container.add(jobTypeBox);
	}
	private void setJobType(ComboBox<FlashFieldSetModel> bkpType, int selbkpType) {
		bkpType.setValue(getBkpType(bkpType, selbkpType, true));
	}

	protected void onJobTypeChanged(FlashFieldSetModel selected){
		
	}
	
	public ScheduleAddItemRegularBackupPanel getParent(){
		return parent;
	}
	
	private FlashFieldSetModel getBkpType(ComboBox<FlashFieldSetModel> bkpType, int selbkpType, boolean retDefault) {
		FlashFieldSetModel selectModel = null;
		for (FlashFieldSetModel flashFieldSetModel : bkpType.getStore().getModels()) {
			if (selectModel == null && retDefault) {
				selectModel = flashFieldSetModel;
			} else if (flashFieldSetModel.getValue() == selbkpType) {
				selectModel = flashFieldSetModel;
				break;
			}
		}

		return selectModel;
	}
	
	public int getValue(){
		return jobTypeBox.getValue().getValue();
	}
	
	public void setValue(int jobType){
		for(FlashFieldSetModel jobTypeModel : jobTypeBox.getStore().getModels()) {
			if(jobTypeModel.getValue() == jobType){
				jobTypeBox.setValue(jobTypeModel);
				return;
			}			
		}		
	}
}

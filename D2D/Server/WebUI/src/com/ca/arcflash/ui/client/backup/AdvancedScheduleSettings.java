package com.ca.arcflash.ui.client.backup;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.AdvanceScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.BackupSchedulePanel;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.DisclosurePanel;

public class AdvancedScheduleSettings{
	
	private BackupSettingsContent backupSettings;
	
	private BackupSchedulePanel scheduleSettings;
	private StartDateTimeSetting startTimeContainer;
	
	public BackupSchedulePanel GetBackupSchedulePanel(){
		return this.scheduleSettings;
	}
	
	public AdvancedScheduleSettings(BackupSettingsContent parent) {
		backupSettings = parent;
	}
	
	public LayoutContainer Render() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("97%");
		container.setLayout(tl);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.backupSettingsSchedule());
		label.addStyleName("restoreWizardTitle");
		container.add(label);
		
		//start time
		LayoutContainer timeContainer = new LayoutContainer();		
		startTimeContainer = new StartDateTimeSetting();
		startTimeContainer.getDateTimeSettingsHeader().hide();
		timeContainer.add(startTimeContainer);
		timeContainer.add(new Html("<HR>"));
		DisclosurePanel dcPanel = Utils.getDisclosurePanel(UIContext.Constants.scheduleStartDateTime());
		dcPanel.add(timeContainer);
		container.add(dcPanel);
		
		//Weekly schedule
		if(scheduleSettings == null) {
			scheduleSettings = new BackupSchedulePanel(!backupSettings.getDestination().backupToRPS());
		}
		
		container.add(scheduleSettings);	
		
		return container;
	}

	public void setEditable(boolean isEnabled) {
		scheduleSettings.setEditable(isEnabled);
	}

	public void RefreshData(BackupSettingsModel model, boolean isEdit, boolean showMergeGrid) {
		AdvanceScheduleModel scheduleModel = model.advanceScheduleModel;
		if(scheduleModel == null){
			scheduleModel = new AdvanceScheduleModel();
			model.advanceScheduleModel = scheduleModel;
		}
		scheduleSettings.refresh(scheduleModel, isEdit, showMergeGrid);
	}

	public boolean Validate() {
		if(!scheduleSettings.validate()){
			return false;
		}
		return true;
	}

	public Date getServerStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public void Save(long result) {
		scheduleSettings.save();
	}
}

package com.ca.arcflash.ui.client.vsphere.setting;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.EveryDayScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryMonthScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryWeekScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.PeriodScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils.ScheduleTypeModel;
import com.ca.arcflash.ui.client.common.HasValidateValue;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;

public class ScheduleVMRecoveryPointCheckPanel extends LayoutContainer implements HasValidateValue<BackupSettingsModel>{
	protected static FlashUIConstants uiConstants= UIContext.Constants;
	private CheckBox dailyCheckBox;
	private CheckBox weeklyCheckBox;
	private CheckBox monthlyCheckBox;
	private CheckBox customCheckBox;
	private Listener<BaseEvent> listener;
	private LabelField checkRPDesctiption;
	
	public ScheduleVMRecoveryPointCheckPanel(){
		this.setStyleAttribute("margin-top", "20px");
		checkRPDesctiption = new LabelField(uiConstants.planVMBackupVSphereCheckRecoveryPointTitle());		
		LabelField labelFSCatalog = new LabelField(uiConstants.planVMBackupVSphereCheckRecoveryPointDescription());		
		this.add(Utils.createFormLayout(checkRPDesctiption, labelFSCatalog));
		
		LayoutContainer container = new LayoutContainer();
		dailyCheckBox = new CheckBox();
		dailyCheckBox.setBoxLabel(uiConstants.scheduleGenerateCatalogDaily());
		dailyCheckBox.ensureDebugId("f78eed55-e18c-458c-978f-c7e647785743");	
		dailyCheckBox.disable();
		dailyCheckBox.addListener(Events.OnClick,listener);
		container.add(dailyCheckBox);
					
		weeklyCheckBox = new CheckBox();
		weeklyCheckBox.setBoxLabel(uiConstants.scheduleGenerateCatalogWeekly());
		weeklyCheckBox.ensureDebugId("eecea717-a9db-4ef0-b593-326ca8ad1745");	
		weeklyCheckBox.disable();
		weeklyCheckBox.addListener(Events.OnClick,listener);
		container.add(weeklyCheckBox);
		
		monthlyCheckBox = new CheckBox();
		monthlyCheckBox.setBoxLabel(uiConstants.scheduleGenerateCatalogMonthly());
		monthlyCheckBox.ensureDebugId("7258dffc-b2eb-4b7a-85da-5e44fed569b0");	
		monthlyCheckBox.disable();
		monthlyCheckBox.addListener(Events.OnClick,listener);
		container.add(monthlyCheckBox);
		
		customCheckBox = new CheckBox();
		customCheckBox.setBoxLabel(uiConstants.scheduleGenerateCatalogCustom());
		customCheckBox.ensureDebugId("1323c5dd-5609-45e8-a84f-265df2b371a6");	
		customCheckBox.addListener(Events.OnClick,listener);
		container.add(customCheckBox);	
		this.add(Utils.createFormLayout("",container), Utils.createLineLayoutData());
	}
	
	@Override
	public void buildValue(BackupSettingsModel value) {
		
	}

	@Override
	public void applyValue(BackupSettingsModel value) {
		PeriodScheduleModel periodSchedule = value.advanceScheduleModel.periodScheduleModel;
		EveryDayScheduleModel daySchedule = periodSchedule.dayScheduleModel;
		dailyCheckBox.setValue(daySchedule != null && daySchedule.isEnabled() && daySchedule.isCheckRecoveryPoint());
		EveryWeekScheduleModel weekSchedule = periodSchedule.weekScheduleModel;
		weeklyCheckBox.setValue(weekSchedule!=null && weekSchedule.isEnabled() && weekSchedule.isCheckRecoveryPoint());
		EveryMonthScheduleModel monthSchedule = periodSchedule.monthScheduleModel;
		monthlyCheckBox.setValue(monthSchedule!=null && monthSchedule.isEnabled() && monthSchedule.isCheckRecoveryPoint());
		customCheckBox.setValue(value.getCheckRecoveryPoint());
	}

	@Override
	public boolean validate() {	
		return true;
	}
	
	public void enableRecoveryPointCheck(int backupType){
		if(backupType == ScheduleTypeModel.OnceDailyBackup){
			dailyCheckBox.enable();
		}else if(backupType == ScheduleTypeModel.OnceWeeklyBackup){
			weeklyCheckBox.enable();
		}else if(backupType == ScheduleTypeModel.OnceMonthlyBackup){
			monthlyCheckBox.enable();
		}
	}
	
	public void disableRecoveryPointCheck(int backupType){
		if(backupType == ScheduleTypeModel.OnceDailyBackup){
			dailyCheckBox.setValue(false);
			dailyCheckBox.disable();
		}else if(backupType == ScheduleTypeModel.OnceWeeklyBackup){
			weeklyCheckBox.setValue(false);
			weeklyCheckBox.disable();
		}else if(backupType == ScheduleTypeModel.OnceMonthlyBackup){
			monthlyCheckBox.setValue(false);
			monthlyCheckBox.disable();
		}
	}
}

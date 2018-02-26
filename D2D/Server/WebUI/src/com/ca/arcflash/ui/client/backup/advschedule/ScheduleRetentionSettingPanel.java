package com.ca.arcflash.ui.client.backup.advschedule;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.EveryDayScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryMonthScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryWeekScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.PeriodScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils.ScheduleTypeModel;
import com.ca.arcflash.ui.client.common.HasValidateValue;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.RetentionPolicyModel;

public class ScheduleRetentionSettingPanel extends ScheduleRetentionSettingBasePanel implements HasValidateValue<BackupSettingsModel>{
	
	private static FlashUIConstants uiConstants=UIContext.Constants;
	
	public ScheduleRetentionSettingPanel(){
		super();
		disableRetentionCount(ScheduleTypeModel.OnceDailyBackup);
		disableRetentionCount(ScheduleTypeModel.OnceWeeklyBackup);
		disableRetentionCount(ScheduleTypeModel.OnceMonthlyBackup);
		txDailyRetain.setAllowBlank(false);
		txWeeklyRetain.setAllowBlank(false);
		txMonthlyRetain.setAllowBlank(false);
	}
	
	@Override
	public void buildValue(BackupSettingsModel value) {
		PeriodScheduleModel periodSchedule = value.advanceScheduleModel.periodScheduleModel;
		if(txDailyRetain.getValue()!=null){
			periodSchedule.dayScheduleModel.setEnabled(true);
			periodSchedule.dayScheduleModel.setRetentionCount(txDailyRetain.getValue().intValue());
		}else{
			periodSchedule.dayScheduleModel.setEnabled(false);
			periodSchedule.dayScheduleModel.setRetentionCount(0);
		}
		
		if(txWeeklyRetain.getValue() != null){
			periodSchedule.weekScheduleModel.setEnabled(true);
			periodSchedule.weekScheduleModel.setRetentionCount(txWeeklyRetain.getValue().intValue());
		}else{
			periodSchedule.weekScheduleModel.setEnabled(false);
			periodSchedule.weekScheduleModel.setRetentionCount(0);
		}
		
	
		if(txMonthlyRetain.getValue() != null){
			periodSchedule.monthScheduleModel.setEnabled(true);
			periodSchedule.monthScheduleModel.setRetentionCount(txMonthlyRetain.getValue().intValue());
		}else{
			periodSchedule.monthScheduleModel.setEnabled(false);
			periodSchedule.monthScheduleModel.setRetentionCount(0);
		}
				
		value.setRetentionCount(txRepeatRetain.getValue().intValue());
		value.setBackupDataFormat(1);//Advanced Backup Data Format
		value.retentionPolicy = saveData();
	}
	
	public RetentionPolicyModel saveData() {
		RetentionPolicyModel model = new RetentionPolicyModel();
		model.setUseBackupSet(false);
		model.setRetentionCount(txRepeatRetain.getValue().intValue());
		return model;
	}
	
	
	@Override
	public void applyValue(BackupSettingsModel value) {
		PeriodScheduleModel periodSchedule = value.advanceScheduleModel.periodScheduleModel;
		if(periodSchedule != null) {
			EveryDayScheduleModel daySchedule = periodSchedule.dayScheduleModel;
			if(daySchedule != null && daySchedule.isEnabled()){
				txDailyRetain.setValue(daySchedule.getRetentionCount());
			}else {
				txDailyRetain.setValue(null); 
			}
			EveryWeekScheduleModel weekSchedule = periodSchedule.weekScheduleModel;
			if(weekSchedule != null && weekSchedule.isEnabled()){
				txWeeklyRetain.setValue(weekSchedule.getRetentionCount());
			}else {
				txWeeklyRetain.setValue(null); 
			}
			
			EveryMonthScheduleModel monthSchedule = periodSchedule.monthScheduleModel;
			if(monthSchedule != null && monthSchedule.isEnabled()){
				txMonthlyRetain.setValue(monthSchedule.getRetentionCount());
			}else {
				txMonthlyRetain.setValue(null); 
			}
		} else {
			txDailyRetain.setValue(null);
			txWeeklyRetain.setValue(null); 
			txMonthlyRetain.setValue(null);
		}
		txRepeatRetain.setValue(value.getRetentionCount());
	}

	
	@Override
	public boolean validate() {	
		
		return super.validateValue();
	}

}

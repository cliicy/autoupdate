package com.ca.arcflash.ui.client.backup.advschedule;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils.ScheduleTypeModel;
import com.ca.arcflash.ui.client.common.AdvScheduleUtil;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;

public class ScheduleRetentionSettingBasePanel extends LayoutContainer {	
	private static FlashUIConstants uiConstants=UIContext.Constants;
	
	protected NumberField txRepeatRetain;
	protected NumberField txDailyRetain;
	protected NumberField txWeeklyRetain;
	protected NumberField txMonthlyRetain;
	protected HorizontalPanel dailybkContainer = new HorizontalPanel();
	protected HorizontalPanel weeklybkContainer = new HorizontalPanel();
	protected HorizontalPanel monthlybkContainer = new HorizontalPanel();
	protected HorizontalPanel custombkContainer = new HorizontalPanel();
	protected final static int FieldWidth=147;
	protected NotificateSet notificationSet;
		
	public ScheduleRetentionSettingBasePanel(){
		LayoutContainer container = this;
		container.setStyleAttribute("margin-top", "20px");
		LabelField labelField = new LabelField(getRecoveryPointNumbersLabel());		
		container.add(AdvScheduleUtil.createFormLayout(labelField, createRetentionPanel()), AdvScheduleUtil.createLineLayoutData());
		container.setStyleAttribute("position", "relative");
		notificationSet = new NotificateSet();
		container.add(notificationSet.getNotificateFieldSet());
	
	}
	
	protected String getRecoveryPointNumbersLabel(){
		return uiConstants.scheduleRecoveryPointRetention();
	}
	
	protected String getRecoveryPointNumbersNotificationLabel(){
		return uiConstants.recoveryPointNotification();
	}
		
	private LayoutContainer createRetentionPanel() {
		LayoutContainer retentionPanel = new LayoutContainer();
		retentionPanel.setLayout(new RowLayout(Orientation.VERTICAL));
		retentionPanel.setStyleAttribute("margin-top", "10px");
		
		TableData tdw = new TableData();
		tdw.setStyle("padding: 0px 15px 10px 0px;");

		//Daily	
		txDailyRetain = getSpinnerField(0, UIContext.maxRecoveryPointLimit);
		dailybkContainer.setVerticalAlign(VerticalAlignment.MIDDLE);
		dailybkContainer.add(txDailyRetain, tdw);
		dailybkContainer.add(new LabelField(uiConstants.recoveryPointDailyBackup()),tdw);		
		retentionPanel.add(dailybkContainer);
				
		//Weekly
		txWeeklyRetain = getSpinnerField(0, UIContext.maxRecoveryPointLimit);
		weeklybkContainer.setVerticalAlign(VerticalAlignment.MIDDLE);
		weeklybkContainer.add(txWeeklyRetain, tdw);
		weeklybkContainer.add(new LabelField(uiConstants.recoveryPointWeeklyBackup()), tdw);		
		retentionPanel.add(weeklybkContainer);
		
		// Monthly
		txMonthlyRetain = getSpinnerField(0, UIContext.maxRecoveryPointLimit);
		monthlybkContainer.setVerticalAlign(VerticalAlignment.MIDDLE);
		monthlybkContainer.add(txMonthlyRetain, tdw);
		monthlybkContainer.add(new LabelField(uiConstants.recoveryPointMonthlyBackup()), tdw);		
		retentionPanel.add(monthlybkContainer);
		
		//Repeat
		txRepeatRetain = getSpinnerField(31, (int)UIContext.maxRecoveryPointLimit);
		txRepeatRetain.setAllowBlank(false);
		custombkContainer.setVerticalAlign(VerticalAlignment.MIDDLE);
		custombkContainer.add(txRepeatRetain, tdw);
		custombkContainer.add(new LabelField(uiConstants.recoveryPointRegularBackups()), tdw);		
		retentionPanel.add(custombkContainer);

		return retentionPanel;
	}
	
	private NumberField getSpinnerField(int defaultValue, final int max) {
		final NumberField spinnerfield = new NumberField();	
		spinnerfield.setWidth(FieldWidth);
		spinnerfield.setAllowDecimals(false);
		spinnerfield.setAllowNegative(false);
		spinnerfield.setValue(defaultValue);
		spinnerfield.setMaxValue(max);
		spinnerfield.setMinValue(1);
		return spinnerfield;
	}	
	
	public void updateRetetntionCount(int backupType, int count) {
		if(backupType == ScheduleTypeModel.OnceDailyBackup){
			dailybkContainer.enable();
			txDailyRetain.setValue(count);
		}else if (backupType == ScheduleTypeModel.OnceWeeklyBackup){
			weeklybkContainer.enable();
			txWeeklyRetain.setValue(count);
		}else if(backupType == ScheduleTypeModel.OnceMonthlyBackup){
			monthlybkContainer.enable();
			txMonthlyRetain.setValue(count);
		}
	}

	public void disableRetentionCount(int backupType){
		if(backupType == ScheduleTypeModel.OnceDailyBackup){
			dailybkContainer.disable();			
			txDailyRetain.setValue(null);
		}else if (backupType == ScheduleTypeModel.OnceWeeklyBackup){
			weeklybkContainer.disable();			
			txWeeklyRetain.setValue(null);
		}else if(backupType == ScheduleTypeModel.OnceMonthlyBackup){
			monthlybkContainer.disable();			
			txMonthlyRetain.setValue(null);
		}

	}
	
	public int getRetentionCount(int backupType){
		if(backupType == ScheduleTypeModel.OnceDailyBackup){
			return txDailyRetain.getValue().intValue();
		}else if (backupType == ScheduleTypeModel.OnceWeeklyBackup){
			return txWeeklyRetain.getValue().intValue();			
		}else if(backupType == ScheduleTypeModel.OnceMonthlyBackup){
			return txMonthlyRetain.getValue().intValue();
		}
		return 0;
	}
	
	protected boolean validateTotalRectentionCount(){
		boolean isValid = true;
		long repeatRetain = txRepeatRetain.getValue() == null ? 0 : txRepeatRetain.getValue().longValue();
		int dailyRetain = txDailyRetain.getValue() == null ? 0 : txDailyRetain.getValue().intValue();
		int weeklyRetain = txWeeklyRetain.getValue() == null ? 0 : txWeeklyRetain.getValue().intValue();
		int monthlyRetain = txMonthlyRetain.getValue() == null ? 0 : txMonthlyRetain.getValue().intValue();
		
		if((repeatRetain + dailyRetain + weeklyRetain + monthlyRetain) > UIContext.maxRecoveryPointLimit){
			notificationSet.showDisplayErrorNotificateSet(uiConstants.scheduleRecoveryPointCountExceed());
			isValid = false; 
		}
		
		return isValid;
	}
	
	private boolean isNumberFieldValid(int minValue, int maxValue) {
		Long value = txRepeatRetain.getValue().longValue();
		if(value < minValue || value > maxValue){
			return false;
		}
		return true;
	}
	
	protected boolean validateValue(){
		notificationSet.removeMessageFromErrorNotificationSet(uiConstants.scheduleRecoveryPointCountExceed());
		
		boolean isValid = true;
		if(txRepeatRetain.isRendered()){
			if(!this.txRepeatRetain.validate())
				return false;
		}else{
			if(!isNumberFieldValid(1, UIContext.maxRecoveryPointLimit))
				return false;
		}
				
		if((txDailyRetain.isRendered() && !txDailyRetain.validate())
				||(txWeeklyRetain.isRendered() && !txWeeklyRetain.validate())
				||(txMonthlyRetain.isRendered() && !txMonthlyRetain.validate())){
			return false;
		}		
		
		if(!validateTotalRectentionCount()){
			return false;
		}

		
		return isValid;
	}

}

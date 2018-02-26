package com.ca.arcflash.ui.client.backup.advschedule;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AdvScheduleUtil;
import com.ca.arcflash.ui.client.common.HasValidateValue;
import com.ca.arcflash.ui.client.model.BackupScheduleModel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class ScheduleRecoverySetBackupPanel extends LayoutContainer implements HasValidateValue<BackupSettingsModel> {
	private ScheduleRecoverySetStartDateTimePanel startTimeContainer;
	private ScheduleRecoverySetRepeatPanel incrementalPanel;
	private ScheduleRecoverySetRepeatPanel fullPanel;
	private ScheduleRecoverySetRepeatPanel verifyPanel;
	public ScheduleRecoverySetBackupPanel(){
		LayoutContainer recoverySetContainer=this;
		recoverySetContainer.setLayout(AdvScheduleUtil.createLineLayout());
		startTimeContainer = new ScheduleRecoverySetStartDateTimePanel();
		recoverySetContainer.add(AdvScheduleUtil.createFormLayout(UIContext.Constants.scheduleStartDateTime(), startTimeContainer), AdvScheduleUtil.createLineLayoutData());
		
		incrementalPanel = new ScheduleRecoverySetRepeatPanel();
		incrementalPanel.setRepeatDefaultValue(true);
		recoverySetContainer.add(AdvScheduleUtil.createFormLayout(UIContext.Constants.scheduleLabelIncrementalBackup(), incrementalPanel), AdvScheduleUtil.createLineLayoutData());
		fullPanel = new ScheduleRecoverySetRepeatPanel();
		recoverySetContainer.add(AdvScheduleUtil.createFormLayout(UIContext.Constants.scheduleLabelFullBackup(), fullPanel), AdvScheduleUtil.createLineLayoutData());
		verifyPanel = new ScheduleRecoverySetRepeatPanel();
		recoverySetContainer.add(AdvScheduleUtil.createFormLayout(UIContext.Constants.scheduleLabelResyncBackup(), verifyPanel), AdvScheduleUtil.createLineLayoutData());
	}
	
	@Override
	public void buildValue(BackupSettingsModel value) {
		Date selectedDate = startTimeContainer.getStartDateTime(UIContext.serverVersionInfo.getTimeZoneOffset());
		value.setBackupStartTime(selectedDate.getTime());
		value.startTime = startTimeContainer.getUserSetTime();
		BackupScheduleModel fullBackupSchedule = new BackupScheduleModel();
		fullPanel.buildValue(fullBackupSchedule);
		value.fullSchedule = fullBackupSchedule;
		
		BackupScheduleModel incBackupSchedule = new BackupScheduleModel();
		incrementalPanel.buildValue(incBackupSchedule);
		value.incrementalSchedule = incBackupSchedule;
		
		BackupScheduleModel verifyBackupSchedule = new BackupScheduleModel();
		verifyPanel.buildValue(verifyBackupSchedule);
		value.resyncSchedule = verifyBackupSchedule;
		
		value.setBackupDataFormat(0); //Standard Backup Data Format
	}

	@Override
	public void applyValue(BackupSettingsModel value) {
		Date backupStartTime ;
		if(value.getBackupStartTime() > 0)
			backupStartTime = new Date(value.getBackupStartTime());
		else{
			backupStartTime = new Date();
			long startTimeInMilliseconds = backupStartTime.getTime();
			//set backup start time plus 5 minutes
			startTimeInMilliseconds += 5 * 60 * 1000;
			backupStartTime.setTime(startTimeInMilliseconds);
		}
		if(value.startTime != null){
			startTimeContainer.setUserStartTime(value.startTime);
		}else {
			startTimeContainer.setStartDateTime(backupStartTime);
		}
		
		fullPanel.applyValue(value.fullSchedule);
		incrementalPanel.applyValue(value.incrementalSchedule);
		verifyPanel.applyValue(value.resyncSchedule);
		
	}

	@Override
	public boolean validate() {
		return startTimeContainer.getStartDateTime() != null &&  (fullPanel.validate() 
				&& incrementalPanel.validate() && verifyPanel.validate());
	}
	
}

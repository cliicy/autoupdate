package com.ca.arcflash.ui.client.backup.schedule;

import com.ca.arcflash.ui.client.UIContext;

/**
 * The class is only for backup advanced schedule panel, if you want to
 * use the <code>SchedulePanel</code> structure, please extends that class
 *
 */
public class BackupSchedulePanel extends SchedulePanel {
	
	public ScheduleDetail[] GetScheduleDetails(){
		return this.scheduleDetails;
	}
	
	public void SetScheduleDetailsMergeNotShow(){
		for(int i = 0; i<scheduleDetails.length; i++){
			scheduleDetails[i].HideMerge();
		}		
	}
	
	public void SetScheduleDetailsMergeShow(){
		for(int i = 0; i<scheduleDetails.length; i++){
			scheduleDetails[i].ShowMerge();
		}		
	}
	
	public BackupSchedulePanel(boolean showMergeGrid) {
		super(showMergeGrid);
	}
	
	protected void initScheduleDetails(boolean showMergeGrid) {
		scheduleDetails = new BackupScheduleDetail[7];
		for(int i=0;i<7;i++){
			scheduleDetails[i] = new BackupScheduleDetail(this,i,showMergeGrid, i+1);
		}		
	}
	
	@Override
	protected String getScheduleSubTitle(){
		return UIContext.Constants.scheduleWhentoRun();
	}

	@Override
	protected String getScheduleLabelDescription() {
		return UIContext.Constants.scheduleLabelScheduleDescription();
	}

	@Override
	protected String getScheduleStartDateToolTip() {
		return UIContext.Constants.scheduleStartDateTooltip();
	}

	@Override
	protected String getScheduleStartTimeToolTip() {
		return UIContext.Constants.scheduleStartTimeTooltip();
	}
	
	@Override
	protected String getScheduleDisableToolTip(){
		return UIContext.Constants.scheduleDisableTooltip();
	}
	
	@Override
	protected String getScheduleEnableToolTip(){
		return UIContext.Constants.scheduleBackupEnableTip();
	}

}

package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.BackupSettingsContent;
import com.ca.arcflash.ui.client.common.FlashTimeField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.RetentionPolicyModel;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class RetentionPanel extends LayoutContainer {
	
	private NumberField recoveryPointsNumber;
	
	private Radio radioRunAny;
	private Radio radioRunSchedule;
	
	private FlashTimeField startTime;
	private FlashTimeField endTime;
	private LayoutContainer timeContainer;

	private boolean useBackupSet = false;
	
	private static final int INTERVAL_LIMIT = 30;//minutes
	
	public RetentionPanel(BackupSettingsContent w) {
		
		this.setBorders(true);
		this.setStyleAttribute("margin-left", "5px");
		
		TableLayout totalLayout = new TableLayout();
		totalLayout.setColumns(1);
		totalLayout.setCellPadding(2);
		totalLayout.setCellSpacing(0);
		totalLayout.setWidth("97%");
		this.setLayout(totalLayout);
				
		LabelField rpDesp = new LabelField();
		rpDesp.setValue(UIContext.Constants.settingRecoveryPointsNumCon());
//		rpDesp.setStyleAttribute("margin-left", "4px");
		this.add(rpDesp);
		
		recoveryPointsNumber = new NumberField();
		if(GXT.isIE)
			recoveryPointsNumber.setStyleAttribute("margin-left", "8px");
		else
			recoveryPointsNumber.setStyleAttribute("margin-left", "15px");
	    recoveryPointsNumber.setMaxValue(UIContext.maxRPLimit);
	    recoveryPointsNumber.setMinValue(1);
	    recoveryPointsNumber.setValue(31);
	    recoveryPointsNumber.setAllowBlank(false);
	    recoveryPointsNumber.setAllowDecimals(false);
	    recoveryPointsNumber.setValidateOnBlur(true);
	    recoveryPointsNumber.setWidth(100);
	    recoveryPointsNumber.ensureDebugId("0713C190-FB3C-4862-8840-BE7D4BD694DA");
	    recoveryPointsNumber.getMessages().setMaxText(
	    		UIContext.Messages.settingsRetentionCountExceedMax(UIContext.maxRPLimit));
	    recoveryPointsNumber.getMessages().setMinText(
	    		UIContext.Constants.settingsRetentionCountErrorTooLow());
	    
		this.add(recoveryPointsNumber);
	    	
//	    LayoutContainer lcMerge = new LayoutContainer();
//	    
//		TableLayout layout = new TableLayout();
//		layout.setColumns(1);
//		layout.setCellPadding(4);
//		layout.setCellSpacing(0);
//		layout.setWidth("90%");
//		lcMerge.setLayout(layout);
	    //if (!UIContext.isAdvSchedule){
	    	addMergeSchedule(this);	    
		    addTimeContainer(this);
	    //}
//	    add(lcMerge);
//	    this.add(backupsetValues);
    }
	
	private void addMergeSchedule(LayoutContainer container) {
		LabelField mergeDesp = new LabelField(UIContext.Constants.mergeScheduleDesp());
		container.add(mergeDesp);
	    
	    radioRunAny = new Radio(){
			@Override
            protected void onClick(ComponentEvent be) {
				super.onClick(be);
	            timeContainer.hide();
            }
	    };
	    radioRunAny.setBoxLabel(UIContext.Constants.mergeRunAnyLabel());
	    radioRunAny.setStyleAttribute("margin-left", "15px");
	    Utils.addToolTip(radioRunAny, UIContext.Constants.mergeRunAnyToolTip());
	    radioRunAny.ensureDebugId("sBF0AD441-0159-47cc-A62C-9F88BCD3036F");
	    radioRunAny.setValue(true);
	    
	    radioRunSchedule = new Radio(){

			@Override
            protected void onClick(ComponentEvent be) {
				super.onClick(be);
	            timeContainer.show();
            }
	    };
	    radioRunSchedule.setBoxLabel(UIContext.Constants.mergeRunScheduleLabel());
	    radioRunSchedule.setStyleAttribute("margin-left", "15px");
	    Utils.addToolTip(radioRunSchedule, UIContext.Constants.mergeRunScheduleTooltip());
	    radioRunSchedule.ensureDebugId("41885DBB-5F82-45a7-8FAE-FA384D1222DE");
	    radioRunSchedule.setValue(false);
	    
	    RadioGroup rg = new RadioGroup();
	    rg.setOrientation(Orientation.VERTICAL);
	    rg.add(radioRunAny);
	    rg.add(radioRunSchedule);
	    container.add(radioRunAny);
	    container.add(radioRunSchedule);
	}
	
	private void addTimeContainer(LayoutContainer container) {
		timeContainer = new LayoutContainer();
	    TableLayout tl = new TableLayout();
	    tl.setWidth("100%");
	    tl.setColumns(5);
	    timeContainer.setLayout(tl);
	    
	    LabelField from = new LabelField(UIContext.Constants.mountPanelTimeFrom());
	    from.setStyleAttribute("margin-left", "25px");
	    TableData td = new TableData();
	    td.setWidth("12%");
	    td.setHorizontalAlign(HorizontalAlignment.LEFT);
	    timeContainer.add(from, td);
	    startTime = new FlashTimeField(1, 0);
	    td = new TableData();
	    td.setWidth("35%");
	    timeContainer.add(startTime, td);
	    LabelField to = new LabelField(UIContext.Constants.mountPanelTimeTo());
	    td = new TableData();
	    td.setWidth("10%");
	    td.setHorizontalAlign(HorizontalAlignment.RIGHT);
	    timeContainer.add(to, td);
	    endTime = new FlashTimeField(18, 0);
	    td = new TableData();
	    td.setWidth("35%");
	    timeContainer.add(endTime, td);
	    
	    td = new TableData();
	    td.setWidth("5%");
//	    timeContainer.add(new LabelField(), td);
	    container.add(timeContainer);
	    timeContainer.hide();
	}
	
	public boolean validate(boolean useRecoverySet) {
		if(!useRecoverySet) {
			//verify retention count
			Number n = recoveryPointsNumber.getValue();
			if (n == null || n.intValue() == 0)
			{
				String title = UIContext.Constants.backupSettingsDestination();
				String msgStr = UIContext.Constants.settingsRetentionCountErrorTooLow();
				recoveryPointsNumber.setValue(1);
				this.popupMessage(title, msgStr, MessageBox.ERROR, null, null);
				return false;
			}
			else if (n.intValue() > UIContext.maxRPLimit)
			{
				String title = UIContext.Constants.backupSettingsDestination();
				String msgStr = UIContext.Messages.settingsRetentionCountExceedMax(UIContext.maxRPLimit);
				recoveryPointsNumber.setValue(UIContext.maxRPLimit);	
				recoveryPointsNumber.fireEvent(Events.Change);
				this.popupMessage(title, msgStr, MessageBox.ERROR, null, null);
				return false;
			}
			
			if(radioRunSchedule.getValue()) {
				if(startTime.getValue().getHour() == endTime.getValue().getHour()
					&& startTime.getValue().getMinutes() == endTime.getValue().getMinutes()) {
					String title = UIContext.Constants.backupSettingsDestination();
					String msgStr = UIContext.Constants.mergeRetentionSameTimeError();
					this.popupMessage(title, msgStr, MessageBox.ERROR, null, null);
					return false;
				}
				
				/*if(!checkTimeInterval()) {
					return false;
				}*/
			}
		}
		return true;
	}
	
	private boolean checkTimeInterval() {
		DateWrapper dwStart = new DateWrapper();
		dwStart = dwStart.clearTime();
		dwStart = dwStart.addHours(startTime.getValue().getHour());
		dwStart = dwStart.addMinutes(startTime.getValue().getMinutes());
		
		DateWrapper dwEnd = new DateWrapper();
		dwEnd = dwEnd.clearTime();
		dwEnd = dwEnd.addHours(endTime.getValue().getHour());
		dwEnd = dwEnd.addMinutes(endTime.getValue().getMinutes());
		
		if(dwEnd.before(dwStart)) {
			//if endTime is next day's time
			dwEnd = dwEnd.addDays(1);
		}
		
		if(dwEnd.getTime() - dwStart.getTime() < INTERVAL_LIMIT * 60 * 1000) {
			String title = UIContext.Constants.backupSettingsDestination();
			String msgStr = UIContext.Messages.mergeJobTimeIntervalLimit(INTERVAL_LIMIT);
			this.popupMessage(title, msgStr, MessageBox.ERROR, null, null);
			return false;
		}
		
		return true;
	}
	
	public void refreshData(RetentionPolicyModel model) {
		if(model == null)
			return;
		if(model.isUseTimeRange() != null && model.isUseTimeRange()) {
			radioRunSchedule.setValue(true);
			timeContainer.show();
		}else {
			//if (!UIContext.isAdvSchedule)
				radioRunAny.setValue(true);
		}
		
		if(model.getStartTimeHour() != null && model.getStartTimeHour() >= 0) {
			startTime.setValue(new Time(model.getStartTimeHour(), 
					model.getStartTimeMinutes() != null ? model.getStartTimeMinutes() : 0));
		}
		
		if(model.getEndTimeHour() != null && model.getEndTimeHour() >= 0) {
			endTime.setValue(new Time(model.getEndTimeHour(),
					model.getEndTimeMinutes() != null ? model.getEndTimeMinutes() : 0));
		}
		
		if(model.getRetentionCount() != null && model.getRetentionCount() > 0){
			recoveryPointsNumber.setValue(model.getRetentionCount());
		}
	}
	
	public RetentionPolicyModel saveData() { 
		RetentionPolicyModel model = new RetentionPolicyModel();
		model.setUseBackupSet(useBackupSet);
		model.setUseTimeRange(radioRunSchedule.getValue());
		model.setRetentionCount(recoveryPointsNumber.getValue().intValue());
		Time starttime = startTime.getValue();
		Time endtime = endTime.getValue();
		model.setStartTimeHour(starttime.getHour());
		model.setStartTimeMinutes(starttime.getMinutes());
		model.setEndTimeHour(endtime.getHour());
		model.setEndTimeMinutes(endtime.getMinutes());		
		return model;
	}
	
	public void setEditable(boolean isEditable) {
		this.radioRunAny.setEnabled(isEditable);
		this.radioRunSchedule.setEnabled(isEditable);
		this.recoveryPointsNumber.setEnabled(isEditable);
		startTime.setEnabled(isEditable);
		endTime.setEnabled(isEditable);
	}
	
	private void popupMessage(String title, String message, String icon, 
			String buttons, Listener<MessageBoxEvent> callback) {
		MessageBox msg = new MessageBox();
		msg.setIcon(icon);
		msg.setTitleHtml(title);
		msg.setMessage(message);
		if(buttons != null && !buttons.isEmpty()) {
			msg.setButtons(buttons);
		}		
		if(callback != null)
			msg.addCallback(callback);
		msg.setModal(true);
		Utils.setMessageBoxDebugId(msg);
		msg.show();
	}
	
	public NumberField getRetentionCountField() {
		return this.recoveryPointsNumber;
	}
	
	public int getRetentionCount() {
		return recoveryPointsNumber.getValue().intValue();
	}
}

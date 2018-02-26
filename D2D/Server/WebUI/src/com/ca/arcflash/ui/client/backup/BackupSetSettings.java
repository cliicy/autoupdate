package com.ca.arcflash.ui.client.backup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.homepage.RetentionPanel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.RetentionPolicyModel;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class BackupSetSettings extends LayoutContainer{
	
	private RetentionPanel retention;
	private LayoutContainer setTotalContainer;
	private BaseDestinationSettings destinationSettings;	
	private HorizontalPanel warningContainer;
	private RetentionPolicyModel model;

	private Radio week;
	private Radio month;
	private Radio retainRPRadio;
	private Radio backupSetRadio;
	private NumberField backupsetNumField;
	private BaseSimpleComboBox<String> dateSavedBox;
	private BaseSimpleComboBox<String> monthSavedBox;
//	private BaseSimpleComboBox<String> startItemBox;
	
	private Radio firstBackup;
	private Radio lastBackup;	
	private HorizontalPanel notePanel;
			
	private static final String[] weekDays = {UIContext.Constants.selectFirDayOfWeek(), UIContext.Constants.selectSecDayOfWeek(), 
												UIContext.Constants.selectThiDayOfWeek(), UIContext.Constants.selectFouDayOfWeek(), 
												UIContext.Constants.selectFifDayOfWeek(), UIContext.Constants.selectSixDayOfWeek(),
												UIContext.Constants.selectSevDayOfWeek()};
//	private static final String[] startItem = {UIContext.Constants.selectFirBackupOfDay(), UIContext.Constants.selectLastBackupOfDay()};
//	private static int MIN_FIELD_WIDTH = 250;
	
	public BackupSetSettings(BackupSettingsContent w)
	{		
		destinationSettings = w.getDestination();
		
		TableLayout panelLayout = new TableLayout();
		panelLayout.setColumns(1);		
		panelLayout.setWidth("97%");
		this.setLayout(panelLayout);		
		
		LayoutContainer radioContainer = new LayoutContainer();
		radioContainer.setStyleAttribute("margin-left", "0px");
		
		TableLayout radioLayout = new TableLayout();
		radioLayout.setColumns(2);
		radioLayout.setCellPadding(2);
		radioLayout.setWidth("97%");
		radioContainer.setLayout(radioLayout);
										
		//merge radio & backupsetRadio		
		RadioGroup rg2 = new RadioGroup();
		retainRPRadio = new Radio() {
			@Override
            protected void onClick(ComponentEvent be) {
				super.onClick(be);
				retention.setVisible(true);
				setTotalContainer.setVisible(false);
				updateRetentionPolicyWarning();
				if(destinationSettings instanceof D2DDestinationSettings){
					((D2DDestinationSettings)destinationSettings).retentionPolicyChanged(false);
				}
				notePanel.hide();
            }
		};
		retainRPRadio.ensureDebugId("A3589D86-4D97-457d-9064-D508DBFDE889");
		retainRPRadio.setBoxLabel(UIContext.Constants.settingRecoveryPointsNum());
		Utils.addToolTip(retainRPRadio, UIContext.Constants.selectRPOptionTooltip(),30000);
		retainRPRadio.setValue(true);		
		rg2.add(retainRPRadio);
		TableData td = new TableData();
		td.setWidth("50%");
		radioContainer.add(retainRPRadio, td);
		
		backupSetRadio = new Radio() {
				@Override
	            protected void onClick(ComponentEvent be) {
					super.onClick(be);
					setTotalContainer.setVisible(true);
					retention.setVisible(false);
					updateRetentionPolicyWarning();
					if(destinationSettings instanceof D2DDestinationSettings){
						((D2DDestinationSettings)destinationSettings).retentionPolicyChanged(true);
					}
					notePanel.show();
	            }			
		};
		backupSetRadio.ensureDebugId("CDBCB78F-D287-465c-927C-11D8B366D700");
		backupSetRadio.setBoxLabel(UIContext.Constants.settingBackupsetsNum());
		Utils.addToolTip(backupSetRadio, UIContext.Constants.selectBackupSetOptionTooltip(),30000);
		backupSetRadio.setValue(false);		
		
		rg2.add(backupSetRadio);
		td = new TableData();
		td.setWidth("50%");
		radioContainer.add(backupSetRadio, td);
		this.add(radioContainer);
		
		Image warningImage = AbstractImagePrototype.create(UIContext.IconBundle.logWarning()).createImage();
		notePanel = new HorizontalPanel();
		notePanel.setStyleAttribute("padding-left", "4px");
		notePanel.setWidth("97%");
		LabelField setNumNoteLabel = new LabelField();
		setNumNoteLabel.setValue(UIContext.Constants.recoverySetNumNote());
		TableData tdw = new TableData();
		tdw.setStyle("padding: 2px 3px 3px 0px;");
		notePanel.add(warningImage, tdw);
		notePanel.add(setNumNoteLabel);
		this.add(notePanel);
		notePanel.hide();
		
		warningImage = AbstractImagePrototype.create(UIContext.IconBundle.logWarning()).createImage();
		warningContainer = new HorizontalPanel();
		warningContainer.setStyleAttribute("padding-left", "4px");
		warningContainer.setWidth("97%");
		
		LabelField warningMessage = new LabelField(UIContext.Constants.changeRetentionPolicyWarning());
		tdw = new TableData();
		tdw.setStyle("padding: 2px 3px 3px 0px;");
		warningContainer.add(warningImage, tdw);
		warningContainer.add(warningMessage);
		this.add(warningContainer);
		
		retention = new RetentionPanel(w);
		retention.setVisible(true);		
		this.add(retention);
		
		addBackupSetContainer();
		this.add(setTotalContainer);
		
		setTotalContainer.setVisible(false);
		
		this.add(setTotalContainer);
		warningContainer.setVisible(false);	

//		SettingPresenter.getInstance().addListener(new Listener<AppEvent>(){
//			@Override
//			public void handleEvent(AppEvent be) {
//				if(be.<Integer>getData("format") == 0){	
//					backupSetRadio.setVisible(true);				
//				}else{
//					retainRPRadio.setValue(true);
//					backupSetRadio.setVisible(false);
//				}				
//			}});
	}
	
	private void updateRetentionPolicyWarning(){
		if(model != null){
			if((model.isUseBackupSet() != null && model.isUseBackupSet()) 
					&& retainRPRadio.getValue()) 
				warningContainer.setVisible(true);
			else if(model.isUseBackupSet() != null && !model.isUseBackupSet() 
					&& backupSetRadio.getValue()){
				warningContainer.setVisible(true);
			}else {
				warningContainer.setVisible(false);
			}
		}else{
			warningContainer.setVisible(false);
		}
	}
	
	private void addBackupSetContainer() {
		setTotalContainer = new LayoutContainer();
		setTotalContainer.setBorders(true);
		setTotalContainer.setStyleAttribute("margin-left", "5px");
		
		TableLayout secTLayout = new TableLayout();
		secTLayout.setColumns(2);
		secTLayout.setCellPadding(2);
		secTLayout.setWidth("97%");
		setTotalContainer.setLayout(secTLayout);					

		//Label:A set always starts with a full backup and ends the next starts.....
		LabelField backupsetNumlabel = new LabelField();
		backupsetNumlabel.setValue(UIContext.Constants.settingBackupSetNumCon());
		TableData tableData = new TableData();
		tableData.setWidth("100%");
		tableData.setColspan(2);
		setTotalContainer.add(backupsetNumlabel, tableData);	
		
		
		backupsetNumField = new NumberField();
		/*if(GXT.isIE)
			backupsetNumField.setStyleAttribute("margin-left", "8px");
		else
			backupsetNumField.setStyleAttribute("margin-left", "15px");*/
		backupsetNumField.setStyleAttribute("margin-left", "5px");
		backupsetNumField.setMaxValue(UIContext.maxBSLimit);
		backupsetNumField.setMinValue(1);
		backupsetNumField.setValue(2);
		backupsetNumField.setAllowBlank(false);
		backupsetNumField.setAllowDecimals(false);
		backupsetNumField.setValidateOnBlur(true);
		backupsetNumField.setWidth(120);
		backupsetNumField.ensureDebugId("3DAC17D9-EBB7-4d54-B340-35DB7D19765B");
		backupsetNumField.getMessages().setMaxText(
	    		UIContext.Messages.settingsBackupSetCountExceedMax(UIContext.maxBSLimit));
		backupsetNumField.getMessages().setMinText(
	    		UIContext.Constants.settingsRetentionCountErrorTooLow());
		Utils.addToolTip(backupsetNumField, UIContext.Constants.backupsetNumberTooltip());				
		tableData = new TableData();
		tableData.setColspan(1);
		tableData.setWidth("40%");
		setTotalContainer.add(new LabelField(), tableData);
		tableData = new TableData();
		tableData.setColspan(1);
		tableData.setWidth("60%");
		setTotalContainer.add(backupsetNumField, tableData);

		//Set When to start a new backup set label
		LabelField setBackupsetNumInfo = new LabelField(UIContext.Constants.startBackupsetTooltip());
		tableData = new TableData();
		tableData.setWidth("100%");
		tableData.setColspan(2);
		setTotalContainer.add(setBackupsetNumInfo,tableData);
		
		addWhenToStartContainer();
	}
	
	private void addWhenToStartContainer() {
		//Selected day of week
		//Selected day of month
		//week radio
		RadioGroup rg = new RadioGroup();		
		week = new Radio() {
			@Override
            protected void onClick(ComponentEvent be) {
				super.onClick(be);
				dateSavedBox.setEnabled(true);
				monthSavedBox.setEnabled(false);
            }
		};
		week.ensureDebugId("E2B40D43-0488-40e2-94ED-4B066ABE80EA");
		week.setBoxLabel(UIContext.Constants.settingBackupDate());
		week.setStyleAttribute("margin-left", "15px");
		Utils.addToolTip(week, UIContext.Constants.selectDayofWeekTooltip());
		week.setValue(true);		
		rg.add(week);
		TableData td1 = new TableData();
		td1.setWidth("40%");
//		td1.setStyle("margin-left:15px");
		setTotalContainer.add(week,td1);

		//week combobox
		dateSavedBox = new BaseSimpleComboBox<String>();
		dateSavedBox.ensureDebugId("47C7A99A-084D-4572-966F-BB47CE69D2AD");
		dateSavedBox.setEditable(false);		
		for (int i = 0; i < 7; i+= 1) {
			dateSavedBox.add(weekDays[i]);
		}
		dateSavedBox.setSimpleValue(weekDays[0]);
		Utils.addToolTip(dateSavedBox, UIContext.Constants.selectDayofWeekTooltip());
		dateSavedBox.setWidth(120);
		dateSavedBox.setStyleAttribute("margin-left", "5px");
		dateSavedBox.setEnabled(true);
		TableData td2 = new TableData();
		td2.setWidth("60%");
		setTotalContainer.add(dateSavedBox,td2);
								
		//month radio
		month = new Radio() {
			@Override
            protected void onClick(ComponentEvent be) {
				super.onClick(be);
				monthSavedBox.setEnabled(true);
				dateSavedBox.setEnabled(false);
				BackupSetSettings.this.repaint();
            }
		};
		month.ensureDebugId("E08FDBE5-E85C-4bc7-9CDA-E78981C93F13");
		month.setBoxLabel(UIContext.Constants.settingBackupDateofMonth());
		month.setStyleAttribute("margin-left", "15px");
		Utils.addToolTip(month, UIContext.Constants.selectDayofMonthTooltip());
		rg.add(month);		
		TableData td3 = new TableData();
		td3.setWidth("40%");
		setTotalContainer.add(month,td3);
				
		//month ComboBox
		monthSavedBox = new BaseSimpleComboBox<String>();
		monthSavedBox.ensureDebugId("D21A414D-31FB-4025-92DD-8594E0CC3B64");
		monthSavedBox.setEditable(false);
		for (int i = 1; i < 33; i+= 1) {
			if(i == 32)
				monthSavedBox.add(UIContext.Constants.selectLastDayOfMonth());
			else
				monthSavedBox.add(i + "");			
		}
		monthSavedBox.setSimpleValue(1 + "");
		Utils.addToolTip(monthSavedBox, UIContext.Constants.selectDayofMonthTooltip());
		monthSavedBox.setWidth(120);
		monthSavedBox.setStyleAttribute("margin-left", "5px");
		monthSavedBox.setEnabled(false);										
//		tableContainer.add(monthRadioContainer);
		TableData td4 = new TableData();
		td4.setWidth("60%");
		setTotalContainer.add(monthSavedBox,td4);
//		setTotalContainer.add(tableContainer);		

		//first label
		LabelField startBackupInfo = new LabelField(UIContext.Constants.startBackupSetInformation());
		TableData td7 = new TableData();
		td7.setColspan(2);
		setTotalContainer.add(startBackupInfo,td7);
		
		RadioGroup rsStartRG = new RadioGroup();
		rsStartRG.setOrientation(Orientation.VERTICAL);
		firstBackup = new Radio();
		firstBackup.ensureDebugId("59B2860F-A6BA-43c7-AC18-4D7B1AFFD6C7");
		firstBackup.setBoxLabel(UIContext.Constants.startWithFirstBackup());
		firstBackup.setStyleAttribute("margin-left", "15px");
		setTotalContainer.add(firstBackup, td7);
		lastBackup = new Radio();
		lastBackup.ensureDebugId("D4EACFBF-A92E-48b1-84E5-C935673CDB6A");
		lastBackup.setBoxLabel(UIContext.Constants.startWithLastBackup());
		lastBackup.setStyleAttribute("margin-left", "15px");
		setTotalContainer.add(lastBackup, td7);
		firstBackup.setValue(true);
		rsStartRG.add(firstBackup);
		rsStartRG.add(lastBackup);		
	}
	
	public void RefreshData(BackupSettingsModel model, boolean isEdit) {
		if(model == null)
			return;
		RetentionPolicyModel retentionPolicy = model.retentionPolicy;
		this.model = model.retentionPolicy;
		
//		if (isEdit && (model.getBackupDataFormat() != null && model.getBackupDataFormat() == 0)) {				
//			this.backupSetRadio.setVisible(true);			
//		}else{
//			this.backupSetRadio.setVisible(false);
//		}
		
		if(retentionPolicy !=null && retentionPolicy.isUseBackupSet() != null) {
			if(retentionPolicy.isUseBackupSet()) {
				notePanel.show();
				backupSetRadio.setValue(true);
				retainRPRadio.setValue(false);
				retention.setVisible(false);
				setTotalContainer.setVisible(true);
				
				if(retentionPolicy.getBackupSetCount() != null && retentionPolicy.getBackupSetCount() > 0)
					backupsetNumField.setValue(retentionPolicy.getBackupSetCount());
				if(retentionPolicy.isUseWeekly() != null && retentionPolicy.isUseWeekly()) {
					week.setValue(true);
					dateSavedBox.setEnabled(true);
					month.setValue(false);
					monthSavedBox.setEnabled(false);
					
					if(retentionPolicy.getDayOfWeek() != null)
						dateSavedBox.setSimpleValue(weekDays[retentionPolicy.getDayOfWeek() - 1]);
				} else if(retentionPolicy.isUseWeekly() != null && !retentionPolicy.isUseWeekly()){
					week.setValue(false);
					dateSavedBox.setEnabled(false);
					month.setValue(true);
					monthSavedBox.setEnabled(true);
					if(retentionPolicy.getDayOfMonth() != null) {
						if(retentionPolicy.getDayOfMonth() == 32)
							monthSavedBox.setSimpleValue(UIContext.Constants.selectLastDayOfMonth());
						else
							monthSavedBox.setSimpleValue(retentionPolicy.getDayOfMonth() + "");
					}
				}
				
				if(retentionPolicy.isStartWithFirst() != null && retentionPolicy.isStartWithFirst())
//					startItemBox.setSimpleValue(startItem[0]);
					firstBackup.setValue(true);
				else if(retentionPolicy.isStartWithFirst() != null && !retentionPolicy.isStartWithFirst())
					lastBackup.setValue(true);
				
			}			
			else {
				backupSetRadio.setValue(false);
				retainRPRadio.setValue(true);
				retention.setVisible(true);
				setTotalContainer.setVisible(false);	
				
				retention.refreshData(retentionPolicy);
			}		
		}						
	}
		
	public RetentionPolicyModel Save() {		
		if (this == null || !this.isRendered())
			return null;
		RetentionPolicyModel retentionPolicy = retention.saveData();
		
		retentionPolicy.setUseBackupSet(backupSetRadio.getValue());
		retentionPolicy.setBackupSetCount(backupsetNumField.getValue().intValue());
		retentionPolicy.setUseWeekly(week.getValue());
		retentionPolicy.setDayOfWeek(dateSavedBox.getSelectedIndex() + 1);
		retentionPolicy.setDayOfMonth(monthSavedBox.getSelectedIndex() + 1);
//		retentionPolicy.setStartWithFirst(startItemBox.getSimpleValue().equals(UIContext.Constants.selectFirBackupOfDay()));
		retentionPolicy.setStartWithFirst(firstBackup.getValue());
						
		return retentionPolicy;
	}
	
	public boolean Validate() {
		if (this == null || !this.isRendered())
			return true;

			return backupSetNumValidate() && retention.validate(backupSetRadio.getValue());

	}
	
	private boolean backupSetNumValidate() {
		if(backupSetRadio.getValue()){
			Number n = backupsetNumField.getValue();
			if (n == null || n.intValue() == 0)
			{
				//Protection Settings
				String title = UIContext.Constants.backupSettingsDestination();
				//Minimum backup set number.
				String msgStr = UIContext.Constants.settingsBackupSetCountErrorTooLow();
				backupsetNumField.setValue(1);
				this.popupMessage(title, msgStr, MessageBox.ERROR, null, null);
				return false;
			}
			else if (n.intValue() > UIContext.maxBSLimit)
			{
				String title = UIContext.Constants.backupSettingsDestination();
				//Maximum backup set number
				String msgStr = UIContext.Messages.settingsBackupSetCountExceedMax(UIContext.maxBSLimit);
				backupsetNumField.setValue(UIContext.maxBSLimit);	
				backupsetNumField.fireEvent(Events.Change);
				this.popupMessage(title, msgStr, MessageBox.ERROR, null, null);
				return false;
			}
		}
		
		return true;
	}

	public void setEditable(boolean isEditable){	
	    backupSetRadio.setEnabled(isEditable);
	    setTotalContainer.setEnabled(isEditable);
		week.setEnabled(isEditable);
	    month.setEnabled(isEditable);
	    dateSavedBox.setEnabled(isEditable); 
	    monthSavedBox.setEnabled(isEditable); 
//	    startItemBox.setEnabled(isEditable);
	    firstBackup.setEnabled(isEditable);
	    lastBackup.setEnabled(isEditable);
	    
	    retainRPRadio.setEnabled(isEditable);
		retention.setEditable(isEditable);		
	}
		
	public int getRetentionCount(){
		return retention.getRetentionCount();
	}
	
	public NumberField getRetentionCountField() {
		return retention.getRetentionCountField();
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
}

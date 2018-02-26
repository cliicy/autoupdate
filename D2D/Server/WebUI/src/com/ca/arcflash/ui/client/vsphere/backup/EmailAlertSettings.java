package com.ca.arcflash.ui.client.vsphere.backup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.VSphereBackupSettingModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.i18n.client.NumberFormat;

public class EmailAlertSettings {
	
	private VSphereBackupSettingWindow parentWindow;

	private LayoutContainer container;
	
	
	private EmailSettingsWindow settingsDlg;
	// notification
	private CheckBox enableEmailOnMissedJob;
	private CheckBox enableEmail;// Failed or Crash
	private CheckBox enableEmailOnSuccess;
	private CheckBox enableSpaceNotification;
	private CheckBox enableEmailOnHostNotFound;
	private CheckBox enableEmailOnDataStoreNotEnough;
	private CheckBox enableEmailOnLicensefailure;
	
	private Button	settingsButton; 
	
	private NumberField spaceMeasureNum;
	private BaseSimpleComboBox<String> spaceMeasureUnit;
	
	public static final String MeasureUnitPercent = "%";
	public static final String MeasureUnitMegabyte = "MB";
	
	private String oldValue;
	private double cachePecentage;
	
	private VSphereBackupSettingModel settingsModel;	
	
	public EmailAlertSettings(VSphereBackupSettingWindow w)
	{
		parentWindow = w;
	}
	
	public LayoutContainer Render(){
		container = new LayoutContainer();
		TableLayout rl = new TableLayout();
		rl.setWidth("97%");
		//rl.setHeight("95%");
		container.setLayout(rl);
				
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.backupSettingsPrePost());
		label.addStyleName("restoreWizardTitle");
		container.add(label);
		
		/*label = new LabelField();
		label.setText(UIContext.Constants.advancedLabelNotifications());
		label.addStyleName("restoreWizardSubItem");
		container.add(label);*/
		
		LayoutContainer tableContainer = new LayoutContainer();
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.setCellPadding(2);
		tableLayout.setCellSpacing(2);
		tableLayout.setColumns(4);
		tableLayout.setWidth("100%");		
		tableContainer.setLayout(tableLayout);

		TableData notification = new TableData();
		notification.setColspan(4);
		notification.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.advancedLabelSendEmail());
		tableContainer.add(label,notification);
		
		enableEmailOnMissedJob = new CheckBox();
		enableEmailOnMissedJob.ensureDebugId("1CDCDDA0-A626-49c6-8C78-AE1EE0D084EA");
		enableEmailOnMissedJob.setBoxLabel(UIContext.Constants.advancedCheckboxEnableEmailOnMissedJobs());
		enableEmailOnMissedJob.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				settingsButton.setEnabled(enableEmail.getValue() 
									   || enableEmailOnSuccess.getValue()
									   || enableSpaceNotification.getValue()
									   || enableEmailOnMissedJob.getValue()
									   || enableEmailOnHostNotFound.getValue()
									   || enableEmailOnDataStoreNotEnough.getValue()
									   || enableEmailOnLicensefailure.getValue());				
			}
	
		});
		
		TableData missedJobTD = new TableData();
		missedJobTD.setColspan(4);
		tableContainer.add(enableEmailOnMissedJob, missedJobTD);
		
		enableEmailOnHostNotFound = new CheckBox();
		enableEmailOnHostNotFound.ensureDebugId("209CA0D3-BC9D-4ee4-8385-EE9792065EA2");
		enableEmailOnHostNotFound.setBoxLabel(UIContext.Constants.serverNotReachable());
		enableEmailOnHostNotFound.addListener(Events.Change, new Listener<FieldEvent>()
				{
			@Override
			public void handleEvent(FieldEvent be) {
				settingsButton.setEnabled(enableEmail.getValue() 
						 || enableEmailOnSuccess.getValue()
						   || enableSpaceNotification.getValue()
						   || enableEmailOnMissedJob.getValue()
						   || enableEmailOnHostNotFound.getValue()
						   || enableEmailOnDataStoreNotEnough.getValue()
						   || enableEmailOnLicensefailure.getValue());				
			}
			
				});
		
		TableData hostNotFoundTD = new TableData();
		hostNotFoundTD.setColspan(4);
		tableContainer.add(enableEmailOnHostNotFound, hostNotFoundTD);
		
		enableEmailOnDataStoreNotEnough = new CheckBox();
		enableEmailOnDataStoreNotEnough.ensureDebugId("B6A86D0E-F05C-4b99-8308-8F10183184A6");
		enableEmailOnDataStoreNotEnough.setBoxLabel(UIContext.Constants.datastoreNotEnough());
		enableEmailOnDataStoreNotEnough.addListener(Events.Change, new Listener<FieldEvent>()
				{
			@Override
			public void handleEvent(FieldEvent be) {
				settingsButton.setEnabled(enableEmail.getValue() 
						 || enableEmailOnSuccess.getValue()
						   || enableSpaceNotification.getValue()
						   || enableEmailOnMissedJob.getValue()
						   || enableEmailOnHostNotFound.getValue()
						   || enableEmailOnDataStoreNotEnough.getValue()
						   || enableEmailOnLicensefailure.getValue());					
			}
			
				});
		
		TableData dataStoreNotEnoughTD = new TableData();
		dataStoreNotEnoughTD.setColspan(4);
		tableContainer.add(enableEmailOnDataStoreNotEnough, dataStoreNotEnoughTD);
		
		enableEmailOnLicensefailure = new CheckBox();
		enableEmailOnLicensefailure.ensureDebugId("25ECD278-50CD-463b-B1CC-84DB1AA42769");
		enableEmailOnLicensefailure.setBoxLabel(UIContext.Constants.licenseFailure());
		enableEmailOnLicensefailure.addListener(Events.Change, new Listener<FieldEvent>()
				{
			@Override
			public void handleEvent(FieldEvent be) {
				settingsButton.setEnabled(enableEmail.getValue() 
						 || enableEmailOnSuccess.getValue()
						   || enableSpaceNotification.getValue()
						   || enableEmailOnMissedJob.getValue()
						   || enableEmailOnHostNotFound.getValue()
						   || enableEmailOnDataStoreNotEnough.getValue()
						   || enableEmailOnLicensefailure.getValue());				
			}
			
				});
		
		TableData LicensefailureTD = new TableData();
		LicensefailureTD.setColspan(4);
		tableContainer.add(enableEmailOnLicensefailure, LicensefailureTD);
		
		enableEmail = new CheckBox();
		enableEmail.ensureDebugId("EE1F67A8-8A9E-496b-B0FC-3EC6DB00ACCC");
		enableEmail.setBoxLabel(UIContext.Constants.advancedCheckboxEnableEmail());
		enableEmail.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				settingsButton.setEnabled(enableEmail.getValue() 
						 || enableEmailOnSuccess.getValue()
						   || enableSpaceNotification.getValue()
						   || enableEmailOnMissedJob.getValue()
						   || enableEmailOnHostNotFound.getValue()
						   || enableEmailOnDataStoreNotEnough.getValue()
						   || enableEmailOnLicensefailure.getValue());	
			}
	
		});
		TableData failTD = new TableData();
		failTD.setColspan(4);
		tableContainer.add(enableEmail, failTD);
		
		enableEmailOnSuccess = new CheckBox();
		enableEmailOnSuccess.ensureDebugId("69B14FBC-6AFC-4a1b-972E-AE5A48D78E8E");
		enableEmailOnSuccess.setBoxLabel(UIContext.Constants.advancedCheckboxEnableEmailOnSuccess());
		enableEmailOnSuccess.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				settingsButton.setEnabled(enableEmail.getValue() 
						 || enableEmailOnSuccess.getValue()
						   || enableSpaceNotification.getValue()
						   || enableEmailOnMissedJob.getValue()
						   || enableEmailOnHostNotFound.getValue()
						   || enableEmailOnDataStoreNotEnough.getValue()
						   || enableEmailOnLicensefailure.getValue());		
			}
	
		});
		TableData successTD = new TableData();
		successTD.setColspan(4);
		tableContainer.add(enableEmailOnSuccess, successTD);
		
		enableSpaceNotification = new CheckBox();
		enableSpaceNotification.ensureDebugId("D8D500C4-3826-43f9-AD77-9B0255F80BF1");
		enableSpaceNotification.setBoxLabel(UIContext.Constants.advancedCheckboxEnableSpaceNotification());
		enableSpaceNotification.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				settingsButton.setEnabled(enableEmail.getValue() 
						 || enableEmailOnSuccess.getValue()
						   || enableSpaceNotification.getValue()
						   || enableEmailOnMissedJob.getValue()
						   || enableEmailOnHostNotFound.getValue()
						   || enableEmailOnDataStoreNotEnough.getValue()
						   || enableEmailOnLicensefailure.getValue());	
				spaceMeasureNum.setEnabled(enableSpaceNotification.getValue());
				spaceMeasureUnit.setEnabled(enableSpaceNotification.getValue());
			}
	
		});
		TableData spaceNotificationTD = new TableData();
		spaceNotificationTD.setWidth("50%");
		tableContainer.add(enableSpaceNotification, spaceNotificationTD);
			
		spaceMeasureNum = new NumberField();
		spaceMeasureNum.ensureDebugId("F6A9E164-64E0-40a0-AEF5-DFD62806E7A4");
		spaceMeasureNum.setAllowNegative(false);
		spaceMeasureNum.setEnabled(false);
		spaceMeasureNum.setWidth(100);
		spaceMeasureNum.setValue(5);
		TableData spaceMeasureNumTD = new TableData();
		spaceMeasureNumTD.setWidth("15");
		spaceMeasureNumTD.setHorizontalAlign(HorizontalAlignment.RIGHT);
		tableContainer.add(spaceMeasureNum,spaceMeasureNumTD);
		
		spaceMeasureUnit = new BaseSimpleComboBox<String>();
		spaceMeasureUnit.ensureDebugId("652C3C58-B7EE-424d-BFE4-A1E990F8E7DF");
		spaceMeasureUnit.setEnabled(false);
		spaceMeasureUnit.setWidth(60);
		spaceMeasureUnit.add(MeasureUnitPercent);
		spaceMeasureUnit.add(MeasureUnitMegabyte);
		spaceMeasureUnit.setSimpleValue(MeasureUnitPercent);
		oldValue = MeasureUnitPercent;

		spaceMeasureUnit.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {		

			@Override
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<String>> se) {
				
				if(parentWindow.destModel==null){
					return;
				}
				
				String value = se.getSelectedItem().getValue();
				if(value.equals(oldValue)){
					return;
				}
				
				double freeSpace = spaceMeasureNum.getValue().doubleValue();
				long totalVolumeSize = parentWindow.destModel.getTotalVolumeSize();
				totalVolumeSize = totalVolumeSize>>20;
				if(totalVolumeSize == 0){
					spaceMeasureNum.setValue(0);
					return;
				}
				NumberFormat formatter = NumberFormat.getFormat("000.00");		
				if(MeasureUnitPercent.equals(value)&&MeasureUnitMegabyte.equals(oldValue)){
					oldValue = MeasureUnitPercent;
					cachePecentage = (freeSpace/totalVolumeSize);
					spaceMeasureNum.setValue(Double.valueOf(formatter.format((freeSpace/totalVolumeSize)*100)));
				}
				if(MeasureUnitMegabyte.equals(value)&&MeasureUnitPercent.equals(oldValue)){
					oldValue = MeasureUnitMegabyte;
					cachePecentage = cachePecentage == 0 ? freeSpace/100 : cachePecentage;
					spaceMeasureNum.setValue(Math.round((cachePecentage*totalVolumeSize)));
				}
			}
		});
		
		TableData spaceMeasureUnitTD = new TableData();
		tableContainer.add(spaceMeasureUnit,spaceMeasureUnitTD);
		
		settingsButton = new Button(){

			@Override
			protected void onDisable() {
				addStyleName("item-disabled");
				super.onDisable();		   
			}

			@Override
			protected void onEnable() {
				removeStyleName("item-disabled");
				super.onEnable();
			}
			
		};
			
		settingsButton.ensureDebugId("E0A5C5A5-F008-4775-B1FE-FEB8EE853271");
		settingsButton.setText(UIContext.Constants.advancedButtonSettings());
		// Tooltip
		Utils.addToolTip(settingsButton, UIContext.Constants.advancedButtonSettingsTooltip());
		settingsButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
								
				settingsDlg = new EmailSettingsWindow();
				settingsDlg.setModal(true);
				settingsDlg.show();
				settingsDlg.setSettings(settingsModel);
				
				
				settingsDlg.addWindowListener( new WindowListener(){
					public void	windowHide(WindowEvent we)
					{
						//Click ok the button should not be null
						if (we.getButtonClicked() != null)
						{
							settingsDlg.saveSettings(settingsModel);
						}
					}
				});
			}			
		});
		TableData emailConfigTD = new TableData();
		emailConfigTD.setWidth("20%");
		emailConfigTD.setHorizontalAlign(HorizontalAlignment.LEFT);
		tableContainer.add(settingsButton, emailConfigTD);
		
		container.add(tableContainer);
		
		return container;
	}
	
	public void Save(){
		
		Boolean b1 = enableEmailOnMissedJob.getValue();		
		parentWindow.model.setEnableEmailOnMissedJob(b1);
		
		Boolean b = enableEmail.getValue();		
		parentWindow.model.setEnableEmail(b);
		
		Boolean b2 = enableEmailOnSuccess.getValue();
		parentWindow.model.setEnableEmailOnSuccess(b2);
		
		Boolean b3 = enableSpaceNotification.getValue();
		parentWindow.model.setEnableSpaceNotification(b3);
		
		Boolean b4 = enableEmailOnHostNotFound.getValue();
		parentWindow.model.setEnableEmailOnHostNotFound(b4);
		
		Boolean b5 = enableEmailOnDataStoreNotEnough.getValue();
		parentWindow.model.setEnableEmailOnDataStoreNotEnough(b5);
		
		Boolean b6 = enableEmailOnLicensefailure.getValue();
		parentWindow.model.setEnableEmailOnLicensefailure(b6);
		
		if(b3)
		{
			parentWindow.model.setSpaceMeasureNum(spaceMeasureNum.getValue().doubleValue());
			parentWindow.model.setSpaceMeasureUnit(spaceMeasureUnit.getSimpleValue());
		}
		
		if (b1 || b || b2 || b3 || b4 || b5 || b6)
		{
			settingsDlg.saveSettings(parentWindow.model);			
		}
		
	}
	
	public boolean Validate(){
		if(enableSpaceNotification.getValue())
		{
			if ( spaceMeasureNum.getValue() == null){
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
				msg.setMessage(UIContext.Constants.destinationThresholdValueBlank());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				
				return false;
			}
			
			if (MeasureUnitPercent.equals(this.spaceMeasureUnit.getValue().getValue())
					&&  spaceMeasureNum.getValue().longValue()>100){
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
				msg.setMessage(UIContext.Constants.destinationThresholdAlertPercent());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
		}
		
		if (enableEmail.getValue() || enableEmailOnSuccess.getValue()
				|| enableSpaceNotification.getValue() || enableEmailOnMissedJob.getValue()
				|| enableEmailOnHostNotFound.getValue() || enableEmailOnDataStoreNotEnough.getValue()
				|| enableEmailOnLicensefailure.getValue())
		{						
			int ret = settingsDlg.validate();	
			if (ret != 0)
			{
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
				msg.setMessage(UIContext.Constants.mustConfigEmailSettins());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				
				return false;
			}
		}
		return true;
	}
	
	public void RefreshData(VSphereBackupSettingModel model) {
		if (model != null)
		{
			settingsModel = model;
			if (model.getEnableEmailOnMissedJob() != null)
			{
				enableEmailOnMissedJob.setValue(model.getEnableEmailOnMissedJob());					
			}
			else
			{
				enableEmailOnMissedJob.setValue(false);
			}	
			
			if (model.getEnableEmail() != null)
			{
				enableEmail.setValue(model.getEnableEmail());					
			}
			else
			{
				enableEmail.setValue(false);
			}
			if (model.getEnableEmailOnSuccess() != null)
			{
				enableEmailOnSuccess.setValue(model.getEnableEmailOnSuccess());				
			}
			else
			{
				enableEmailOnSuccess.setValue(false);
			}
			if(model.getEnableEmailOnHostNotFound() !=null){
				this.enableEmailOnHostNotFound.setValue(model.getEnableEmailOnHostNotFound());
			}else{
				this.enableEmailOnHostNotFound.setValue(false);
			}
			
			if(model.getEnableEmailOnDataStoreNotEnough()!=null){
				this.enableEmailOnDataStoreNotEnough.setValue(model.getEnableEmailOnDataStoreNotEnough());
			}else{
				this.enableEmailOnDataStoreNotEnough.setValue(false);
			}
			
			if(model.getEnableEmailOnLicensefailure()!=null){
				this.enableEmailOnLicensefailure.setValue(model.getEnableEmailOnLicensefailure());
			}else{
				this.enableEmailOnLicensefailure.setValue(false);
			}
			if(model.getEnableSpaceNotification() != null)
			{
				enableSpaceNotification.setValue(model.getEnableSpaceNotification());
				if(model.getEnableSpaceNotification().booleanValue() == true)
				{
					spaceMeasureNum.setValue(model.getSpaceMeasureNum());
					spaceMeasureUnit.setSimpleValue(model.getSpaceMeasureUnit());
					oldValue = model.getSpaceMeasureUnit();
				}
			}
			else
			{
				enableSpaceNotification.setValue(false);
			}
			settingsButton.setEnabled(enableEmail.getValue() 
								   || enableEmailOnSuccess.getValue()
								   || enableSpaceNotification.getValue()
								   || enableEmailOnMissedJob.getValue()
								   || enableEmailOnHostNotFound.getValue()
								   || enableEmailOnDataStoreNotEnough.getValue()
								   || enableEmailOnLicensefailure.getValue());
		
		}
		
		settingsDlg = new EmailSettingsWindow();
		settingsDlg.setSettings(settingsModel);
		this.container.repaint();
		
	}

}

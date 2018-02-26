package com.ca.arcflash.ui.client.backup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.SettingsGroupType;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Widget;

public class AdvancedSettings {
	
	private BackupSettingsContent parentWindow;

	// notification
	//private CheckBox enableEmailOnMissedJob;
	//private CheckBox enableEmail;// Failed or Crash
	//private CheckBox enableEmailOnSuccess;
	//private CheckBox enableSpaceNotification;
	
	//public Button	settingsButton; 
	
	private CheckBox preCheckbox;
	private CheckBox postCheckbox;
	private CheckBox postSnapshotCheckbox;
	private CheckBox postCheckBox2;
	private CheckBox onExitCodeCheckbox;
	private Radio runJobRadio;
	private Radio failJobRadio;
	
	private TextField<String> preCommand;
	private TextField<String> postCommand;
	private TextField<String> postSnapshotCommand;
	private NumberField exitCodeNumberField;
		
	private TextField<String> usernameTF;
	private PasswordTextField passwordTF;
	
	private LayoutContainer container;
	//private EmailSettingsWindow settingsDlg;
	
	//private NumberField spaceMeasureNum;
	//private BaseSimpleComboBox<String> spaceMeasureUnit;
	
	static final int DEFAULT_EXIT_CODE = 0;
	static final int QJDTO_B_RUN_JOB = 0x00001000;
	static final int QJDTO_B_FAIL_JOB = 0x00002000;	
	public static final String MeasureUnitPercent = "%";
	public static final String MeasureUnitMegabyte = "MB";
	
	private SettingsGroupType settingsGroupType;
	//old value for freespace unit;
	//private String oldValue;
	//private double cachePecentage;
	
	public AdvancedSettings(BackupSettingsContent w){
		this(w, null);
	}
	
	public AdvancedSettings(BackupSettingsContent w, SettingsGroupType settingsGroupType)
	{
		parentWindow = w;
		this.settingsGroupType = settingsGroupType;
	}
	
	public LayoutContainer Render()
	{
		container = new LayoutContainer();
		container.setScrollMode(Scroll.AUTOY);
		TableLayout rl = new TableLayout();
		rl.setWidth("95%");
//		rl.setHeight("75%");
		container.setLayout(rl);
				
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.backupSettingsPrePost());
		label.addStyleName("restoreWizardTitle");
		container.add(label);
		
		if (settingsGroupType == SettingsGroupType.VMBackupSettings)
			container.add(createWarningMessage());
		
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

		/*TableData notification = new TableData();
		notification.setColspan(4);
		notification.setHorizontalAlign(HorizontalAlignment.LEFT);*/
		
		/*label = new LabelField();
		label.setText(UIContext.Constants.advancedLabelSendEmail());
		tableContainer.add(label,notification);*/
		
/*		enableEmailOnMissedJob = new CheckBox();
		enableEmailOnMissedJob.setBoxLabel(UIContext.Constants.advancedCheckboxEnableEmailOnMissedJobs());
		enableEmailOnMissedJob.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				settingsButton.setEnabled(enableEmail.getValue() 
									   || enableEmailOnSuccess.getValue()
									   || enableSpaceNotification.getValue()
									   || enableEmailOnMissedJob.getValue() || (settingsModel.getSelfUpdatesSettings() != null ? settingsModel.getSelfUpdatesSettings().getNotifyOnNewUpdates() : false));				
			}
	
		});*/
		
		/*TableData missedJobTD = new TableData();
		missedJobTD.setColspan(4);
		tableContainer.add(enableEmailOnMissedJob, missedJobTD);
		
		enableEmail = new CheckBox();
		enableEmail.setBoxLabel(UIContext.Constants.advancedCheckboxEnableEmail());
		enableEmail.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				settingsButton.setEnabled(enableEmail.getValue() 
									   || enableEmailOnSuccess.getValue()
									   || enableSpaceNotification.getValue()
									   || enableEmailOnMissedJob.getValue()||(settingsModel.getSelfUpdatesSettings() != null ? settingsModel.getSelfUpdatesSettings().getNotifyOnNewUpdates() : false));	
			}
	
		});
		TableData failTD = new TableData();
		failTD.setColspan(4);
		tableContainer.add(enableEmail, failTD);
		
		enableEmailOnSuccess = new CheckBox();
		enableEmailOnSuccess.setBoxLabel(UIContext.Constants.advancedCheckboxEnableEmailOnSuccess());
		enableEmailOnSuccess.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				settingsButton.setEnabled(enableEmail.getValue() 
						               || enableEmailOnSuccess.getValue()
						               || enableSpaceNotification.getValue()
						               || enableEmailOnMissedJob.getValue() || (settingsModel.getSelfUpdatesSettings() != null ? settingsModel.getSelfUpdatesSettings().getNotifyOnNewUpdates() : false));	
			}
	
		});
		TableData successTD = new TableData();
		successTD.setColspan(4);
		tableContainer.add(enableEmailOnSuccess, successTD);
		
		enableSpaceNotification = new CheckBox();
		enableSpaceNotification.setBoxLabel(UIContext.Constants.advancedCheckboxEnableSpaceNotification());
		enableSpaceNotification.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				settingsButton.setEnabled(enableEmail.getValue() 
									   || enableEmailOnSuccess.getValue()
									   || enableSpaceNotification.getValue()
									   || enableEmailOnMissedJob.getValue() || (settingsModel.getSelfUpdatesSettings() != null ? settingsModel.getSelfUpdatesSettings().getNotifyOnNewUpdates() : false));	
				spaceMeasureNum.setEnabled(enableSpaceNotification.getValue());
				spaceMeasureUnit.setEnabled(enableSpaceNotification.getValue());
			}
	
		});
		TableData spaceNotificationTD = new TableData();
		spaceNotificationTD.setWidth("50%");
		tableContainer.add(enableSpaceNotification, spaceNotificationTD);
			
		spaceMeasureNum = new NumberField();
		spaceMeasureNum.setAllowNegative(false);
		spaceMeasureNum.setEnabled(false);
		spaceMeasureNum.setWidth(100);
		spaceMeasureNum.setValue(5);
		TableData spaceMeasureNumTD = new TableData();
		spaceMeasureNumTD.setWidth("15");
		spaceMeasureNumTD.setHorizontalAlign(HorizontalAlignment.RIGHT);
		tableContainer.add(spaceMeasureNum,spaceMeasureNumTD);
		
		spaceMeasureUnit = new BaseSimpleComboBox<String>();
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
			
		settingsButton.setText(UIContext.Constants.advancedButtonSettings());
		// Tooltip
		settingsButton.setToolTip(UIContext.Constants.advancedButtonSettingsTooltip());
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
		
		container.add(new Html("<HR>"));*/
			
		DisclosurePanel disPanel = Utils.getDisclosurePanel(UIContext.Constants.advancedLabelActions());
		
		LayoutContainer disContainer = new LayoutContainer();
		
		/*label = new LabelField();
		label.setText(UIContext.Constants.advancedLabelActions());
		label.addStyleName("restoreWizardSubItem");
		container.add(label);*/
		
		label = new LabelField();
		label.setValue(UIContext.Constants.advancedLabelActionsDescription());
		disContainer.add(label);
		
		
		//Username/Password Section
		LayoutContainer tablePasswordContainer = new LayoutContainer();
		
		TableLayout tablePasswordLayout = new TableLayout();
		tablePasswordLayout.setCellPadding(2);
		tablePasswordLayout.setCellSpacing(2);
		tablePasswordLayout.setColumns(2);
		tablePasswordLayout.setWidth("100%");		
		tablePasswordContainer.setLayout(tablePasswordLayout);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelEncryptionUser());
		tablePasswordContainer.add(label);				
				
		usernameTF = new TextField<String>();
		usernameTF.ensureDebugId("33879BBE-C9D5-4de4-9904-7F0B6AF6EABA");
		// usernameTF.setToolTip(UIContext.Constants.advancedActionsUserNameTooltip());
		Utils.addToolTip(usernameTF, UIContext.Constants.advancedActionsUserNameTooltip());
		usernameTF.setWidth(300);		
		tablePasswordContainer.add(usernameTF);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelEncyrptionPassword());
		tablePasswordContainer.add(label);		
		
		passwordTF = new PasswordTextField();
		passwordTF.setPassword(true);		
		passwordTF.ensureDebugId("5F41447F-9D89-4546-BCE0-D9369A746148");
		// passwordTF.setToolTip(UIContext.Constants.advancedActionsPasswordTooltip());
		Utils.addToolTip(passwordTF, UIContext.Constants.advancedActionsPasswordTooltip());
		passwordTF.setWidth(300);		
		tablePasswordContainer.add(passwordTF);
		
		disContainer.add(tablePasswordContainer);
		
		//Pre/Post Section		
		LayoutContainer commandContainer = new LayoutContainer();
				
		TableLayout layout = new TableLayout();
		layout.setCellPadding(2);
		layout.setCellSpacing(2);
		layout.setColumns(2);
		layout.setWidth("100%");
		
		commandContainer.setLayout(layout);
		
		TableData td = new TableData();
		td.setWidth("100%");
		td.setColspan(2);
		
		preCheckbox = new CheckBox();
		preCheckbox.setBoxLabel(UIContext.Constants.advancedCheckboxRunCommandBefore());
		preCheckbox.addListener(Events.Change, new Listener<FieldEvent>()
		{

			@Override
			public void handleEvent(FieldEvent be) {
				preCommand.setEnabled(preCheckbox.getValue());
				exitCodeNumberField.setEnabled(preCheckbox.getValue() && onExitCodeCheckbox.getValue());
				onExitCodeCheckbox.setEnabled(preCheckbox.getValue());
				
				runJobRadio.setEnabled(onExitCodeCheckbox.getValue() && preCheckbox.getValue());
				failJobRadio.setEnabled(onExitCodeCheckbox.getValue()  && preCheckbox.getValue());
			}
	
		});		
		preCheckbox.ensureDebugId("5A4D4FE9-A590-481e-B4FC-ABEED5AA1CB2");
		commandContainer.add(preCheckbox, td);
		
		preCommand = new TextField<String>();
		preCommand.setWidth(450);
		preCommand.ensureDebugId("45A82C9F-78AF-4b07-87E6-5B465B1CCE87");	
		Utils.addToolTip(preCommand, UIContext.Constants.advancedActionsCmdEditBoxTooltip());
		commandContainer.add(preCommand, td);
		
		td = new TableData();		
		td.setColspan(1);
		td.setWidth("30%");
		
		onExitCodeCheckbox = new CheckBox();
		onExitCodeCheckbox.setBoxLabel(UIContext.Constants.advancedCheckboxOnExitCode());
		onExitCodeCheckbox.addStyleName("restoreWizardLeftSpacing");
		onExitCodeCheckbox.addListener(Events.Change, new Listener<FieldEvent>()
		{

			@Override
			public void handleEvent(FieldEvent be) {
				runJobRadio.setEnabled(onExitCodeCheckbox.getValue());
				failJobRadio.setEnabled(onExitCodeCheckbox.getValue());
				exitCodeNumberField.setEnabled(onExitCodeCheckbox.getValue());
				
			}
	
		});
		onExitCodeCheckbox.ensureDebugId("9B1B8D41-3DA8-49ae-B9FC-88C0FF89587B");
		commandContainer.add(onExitCodeCheckbox, td);
		
		
		
		td = new TableData();
		td.setColspan(1);
		td.setWidth("70%");
		
		exitCodeNumberField = new NumberField();
		exitCodeNumberField.setWidth(200);
		//exitCodeNumberField.setPropertyEditorType(Integer.class);
		exitCodeNumberField.setMaxValue(Integer.MAX_VALUE);
		exitCodeNumberField.setMinValue(Integer.MIN_VALUE);
		
		exitCodeNumberField.ensureDebugId("1F3381FC-ADFE-4e9a-BE19-9AF80187992E");
		Utils.addToolTip(exitCodeNumberField, UIContext.Constants.advancedActionsCmdOnExitCodeTooltip());
		commandContainer.add(exitCodeNumberField, td);
		
		td = new TableData();
		td.setWidth("100%");
		td.setColspan(2);
				
		runJobRadio = new Radio();
		runJobRadio.addStyleName("leftIndentFifty");
		runJobRadio.setBoxLabel(UIContext.Constants.advancedCheckboxRunJob());
		Utils.addToolTip(runJobRadio, UIContext.Constants.advancedActionsCmdRunJobTooltip());
		runJobRadio.ensureDebugId("05DF866A-ED8E-44cd-AE6E-05D45405BD3F");
		commandContainer.add(runJobRadio, td);
		
		failJobRadio = new Radio();
		failJobRadio.addStyleName("leftIndentFifty");
		failJobRadio.setBoxLabel(UIContext.Constants.advancedCheckboxFailJob());
		Utils.addToolTip(failJobRadio, UIContext.Constants.advancedActionsCmdFailJobTooltip());
		failJobRadio.ensureDebugId("E47C323B-A164-440d-B3B4-555A686049E8");
		commandContainer.add(failJobRadio, td);
		
		RadioGroup group = new RadioGroup();
		group.setName("runfailgroup");
		group.add(runJobRadio);
		group.add(failJobRadio);
		
		//preCommand = new TextField<String>();
		//preCommand.setWidth(450);
		//commandContainer.add(preCommand, td);
		
		
		//Post Snapshot
		postSnapshotCheckbox= new CheckBox();
		postSnapshotCheckbox.ensureDebugId("6BE3695C-BFA9-44ac-A0E5-A9239C517CAD");
		postSnapshotCheckbox.setBoxLabel(UIContext.Constants.advancedCheckboxRunCommandAfterSnapshot());
		postSnapshotCheckbox.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				postSnapshotCommand.setEnabled(postSnapshotCheckbox.getValue());						
			}
		});		
		commandContainer.add(postSnapshotCheckbox, td);
		
		postSnapshotCommand = new TextField<String>();
		postSnapshotCommand.ensureDebugId("FA86AD75-CCA7-42d4-9C1A-40FD6B0DB06A");
		postSnapshotCommand.setWidth(450);
		Utils.addToolTip(postSnapshotCommand, UIContext.Constants.advancedActionsSnapshotEditboxTooltip());
		commandContainer.add(postSnapshotCommand, td);
		
		
		
				
		postCheckbox = new CheckBox();
		postCheckbox.ensureDebugId("E01219DD-AFA3-4d7f-A845-E3A4F729AAFB");
		postCheckbox.setBoxLabel(UIContext.Constants.advancedCheckboxRunCommandAfter());
		postCheckbox.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				postCommand.setEnabled(postCheckbox.getValue());				
				postCheckBox2.enable();
			}
	
		});
		commandContainer.add(postCheckbox, td);
		
		postCommand = new TextField<String>();
		postCommand.ensureDebugId("38495258-4115-4b2f-8D6B-D5E1FBE416B8");
		postCommand.setWidth(450);
		Utils.addToolTip(postCommand, UIContext.Constants.advancedActionsAfterBackupEditboxTooltip());
		commandContainer.add(postCommand, td);
		postCheckBox2 = new CheckBox();  
		postCheckBox2.ensureDebugId("E01219DD-AFA3-4d7f-A845-E3A4F729ABAA");
		postCheckBox2.setBoxLabel(UIContext.Constants.advancedCheckboxRunCommandEvenFails());
		
		if (settingsGroupType == SettingsGroupType.VMBackupSettings) //for now, only add this checkbox to UI when it is on proxy UI - by Liang.Shu
			commandContainer.add(postCheckBox2,td);
		disContainer.add(commandContainer);
		disPanel.add(disContainer);
		container.add(disPanel);
		
		return container;
	}

	private Widget createWarningMessage() {
		LayoutContainer tableContainer = new LayoutContainer();
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.setCellPadding(0);
		tableLayout.setCellSpacing(0);
		tableLayout.setColumns(2);
		tableLayout.setWidth("100%");		
		tableContainer.setLayout(tableLayout);

		tableContainer.add(AbstractImagePrototype.create(UIContext.IconBundle.logWarning()).createImage());
		tableContainer.add(new LabelField(UIContext.Constants.nonWindowsPrePostScriptWarning()));
		return tableContainer;
	}

	public void RefreshData(BackupSettingsModel model) {
		
		if (model != null)
		{
			String pre = model.getCommandBeforeBackup();
			
			boolean isHavingPreCmd =false;
			
			if (pre == null || pre.trim().length() == 0)
			{
				//Default		
				preCheckbox.setValue(false);
				preCommand.setEnabled(false);
				exitCodeNumberField.setEnabled(false);
				onExitCodeCheckbox.setEnabled(false);
				runJobRadio.setEnabled(false);
				failJobRadio.setEnabled(false);			
			}
			else 
			{
				preCheckbox.setValue(true);
				preCommand.setValue(pre);
				preCommand.setEnabled(true);
				isHavingPreCmd = true;
			}
						
			String post = model.getCommandAfterBackup();
			if (post == null || post.trim().length() == 0)
			{
				//Default
				postCheckbox.setValue(false);
				postCommand.setEnabled(false);
				postCheckBox2.setValue(false);
			}
			else 
			{
				postCheckbox.setValue(true);
				postCommand.setValue(post);
				postCommand.setEnabled(true);
				if(model.getRunCommandEvenFailed() != null){
						postCheckBox2.setValue(model.getRunCommandEvenFailed());//lds by Liang.Shu
				}else {
					postCheckBox2.setValue(false);
				}
				
			}
			
			if (!isHavingPreCmd || model.getEnablePreExitCode() == null || model.getEnablePreExitCode() == false)
			{
				onExitCodeCheckbox.setValue(false);
				exitCodeNumberField.setEnabled(false);
				exitCodeNumberField.setValue(0);
			}
			else
			{
				Integer preExitCode = model.getPreExitCode();
				onExitCodeCheckbox.setValue(true);
				exitCodeNumberField.setEnabled(true);
				exitCodeNumberField.setValue(preExitCode);	
			}
			
			Boolean skipJob = model.getSkipJob();
			if (skipJob == null)
			{
				//Default check runJob
				runJobRadio.setValue(true);
				runJobRadio.setEnabled(false);
				failJobRadio.setEnabled(false);
			}
			else
			{
				failJobRadio.setValue(skipJob);
				runJobRadio.setValue(!skipJob);
			}
			
			if (model.getActionsUserName() != null)
			{
				usernameTF.setValue(model.getActionsUserName());
			}
			if (model.getActionsPassword() != null)
			{
				passwordTF.setValue(model.getActionsPassword());
			}
			
			
			String postSnap = model.getCommandAfterSnapshot();
			if (postSnap == null || postSnap.trim().isEmpty())
			{
				postSnapshotCheckbox.setValue(false);
				postSnapshotCommand.setEnabled(false);
			}
			else
			{
				postSnapshotCheckbox.setValue(true);
				postSnapshotCommand.setValue(postSnap);
				postSnapshotCommand.setEnabled(true);
			}
			
			/*if (model.getEnableEmailOnMissedJob() != null)
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
			
			if(model.getSelfUpdatesSettings() != null)
			{
				settingsButton.setEnabled(model.getSelfUpdatesSettings().getNotifyOnNewUpdates());
			}
			
			if (model.getEnableEmailOnSuccess() != null)
			{
				enableEmailOnSuccess.setValue(model.getEnableEmailOnSuccess());				
			}
			else
			{
				enableEmailOnSuccess.setValue(false);
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
								   || enableEmailOnMissedJob.getValue() || (model.getSelfUpdatesSettings() != null ? model.getSelfUpdatesSettings().getNotifyOnNewUpdates() : false));*/
		}
		else
		{
			exitCodeNumberField.setValue(0);
		}
		
/*		settingsDlg = new EmailSettingsWindow();
		settingsDlg.setSettings(settingsModel);*/
		
		this.container.repaint();
		
	}
	
	public void Save()
	{
		if (preCheckbox.getValue())
		{
			SettingPresenter.model.setCommandBeforeBackup(preCommand.getValue());
			
			
			if (onExitCodeCheckbox.getValue())
			{
				SettingPresenter.model.setEnablePreExitCode(true);
				
				Number n = exitCodeNumberField.getValue();
				if (n != null)
				{
					Integer exitCode = n.intValue();
					SettingPresenter.model.setPreExitCode(exitCode);
					if (runJobRadio.getValue())
					{
						SettingPresenter.model.setSkipJob(false);
					}
					else
					{
						SettingPresenter.model.setSkipJob(true);
					}
				}
			}
			else
			{
				SettingPresenter.model.setEnablePreExitCode(false);
			}
		}
		else
		{
			SettingPresenter.model.setCommandBeforeBackup("");
			SettingPresenter.model.setEnablePreExitCode(false);
		}
		
		if (postCheckbox.getValue())
		{
			SettingPresenter.model.setCommandAfterBackup(postCommand.getValue());
			SettingPresenter.model.setRunCommandEvenFailed(postCheckBox2.getValue());
		}
		else
		{
			SettingPresenter.model.setCommandAfterBackup("");
			SettingPresenter.model.setRunCommandEvenFailed(false);
		}
		
		if (postSnapshotCheckbox.getValue())
		{
			SettingPresenter.model.setCommandAfterSnapshot(postSnapshotCommand.getValue());		
		}
		else
		{
			SettingPresenter.model.setCommandAfterSnapshot("");
		}
		
		
		SettingPresenter.model.setActionsUserName(usernameTF.getValue()==null ? "":usernameTF.getValue());
		
		
		SettingPresenter.model.setActionsPassword(passwordTF.getValue()==null ? "":passwordTF.getValue());
		
		
		/*Boolean b1 = enableEmailOnMissedJob.getValue();		
		parentWindow.model.setEnableEmailOnMissedJob(b1);
		
		Boolean b = enableEmail.getValue();		
		parentWindow.model.setEnableEmail(b);
		
		Boolean b2 = enableEmailOnSuccess.getValue();
		parentWindow.model.setEnableEmailOnSuccess(b2);
		
		Boolean b3 = enableSpaceNotification.getValue();
		parentWindow.model.setEnableSpaceNotification(b3);

		if(b3)
		{
			parentWindow.model.setSpaceMeasureNum(spaceMeasureNum.getValue().doubleValue());
			parentWindow.model.setSpaceMeasureUnit(spaceMeasureUnit.getSimpleValue());
		}
		
		if (b1 || b || b2 || b3)
		{
			settingsDlg.saveSettings(parentWindow.model);			
		}*/
				
	}
	public boolean Validate()
	{		

		/*if(enableSpaceNotification.getValue())
		{
			if ( spaceMeasureNum.getValue() == null){
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitle(UIContext.Constants.messageBoxTitleError());
				msg.setMessage(UIContext.Constants.destinationThresholdValueBlank());
				msg.setModal(true);
				msg.show();
				
				return false;
			}
			
			if (MeasureUnitPercent.equals(this.spaceMeasureUnit.getValue().getValue())
					&&  spaceMeasureNum.getValue().longValue()>100){
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitle(UIContext.Constants.messageBoxTitleError());
				msg.setMessage(UIContext.Constants.destinationThresholdAlertPercent());
				msg.setModal(true);
				msg.show();
				return false;
			}
		}
		
		if (enableEmail.getValue() || enableEmailOnSuccess.getValue()
				|| enableSpaceNotification.getValue() || enableEmailOnMissedJob.getValue() || (parentWindow.model.getSelfUpdatesSettings() != null ? parentWindow.model.getSelfUpdatesSettings().getNotifyOnNewUpdates() : false))
		{						
			int ret = settingsDlg.validate();	
			if (ret != 0)
			{
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitle(UIContext.Constants.messageBoxTitleError());
				msg.setMessage(UIContext.Constants.mustConfigEmailSettins());
				msg.setModal(true);
				msg.show();
				
				return false;
			}
		}		*/
		if (preCheckbox.getValue())
		{
			String val = preCommand.getValue();
			if (val == null || val.trim().isEmpty())
			{
				//Fail empty pre command
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				String title = "";
				if(parentWindow.isShowForVSphere()){
					title = UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
				}else{
					title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
				}
				msg.setTitleHtml(title);
				msg.setMessage(UIContext.Constants.advancedPreCommandBlank());
				msg.setModal(true);
				msg.getDialog().getButtonById(Dialog.OK).ensureDebugId("B47444CB-583A-4c51-A06A-AD4F647293BB");
				msg.show();
				return false;
			}
			else if (!validateCommandExists(val))
			{
				MessageBox msg = new MessageBox();
				
				msg.setIcon(MessageBox.ERROR);
				String title = "";
				if(parentWindow.isShowForVSphere()){
					title = UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
				}else{
					title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
				}
				msg.setTitleHtml(title);
				msg.setMessage(UIContext.Constants.advancedPreCommandDNE());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
			
			if(onExitCodeCheckbox.getValue()){
				 if(!exitCodeNumberField.validate()){
					 return false;
				}
			}
			
		}
		if (postSnapshotCheckbox.getValue())
		{
			String val = postSnapshotCommand.getValue();
			if (val == null || val.trim().isEmpty())
			{
				//Fail empty postSnapshotCommand
				MessageBox msg = new MessageBox();
				msg.getDialog().getButtonById(Dialog.OK).ensureDebugId("1DF5EA3C-530D-43e8-892E-639F823D8C2E");
				msg.setIcon(MessageBox.ERROR);
				String title = "";
				if(parentWindow.isShowForVSphere()){
					title = UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
				}else{
					title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
				}
				msg.setTitleHtml(title);
				msg.setMessage(UIContext.Constants.advancedPostSnapCommandBlank());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
			else if (!validateCommandExists(val))
			{
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				String title = "";
				if(parentWindow.isShowForVSphere()){
					title = UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
				}else{
					title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
				}
				msg.setTitleHtml(title);
				msg.setMessage(UIContext.Constants.advancedPostSnapCommandDNE());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
		}		
		if (postCheckbox.getValue())
		{
			String val = postCommand.getValue();
			if (val == null || val.trim().isEmpty())
			{
				//Fail empty postCommand
				MessageBox msg = new MessageBox();
				msg.getDialog().getButtonById(Dialog.OK).ensureDebugId("94E115F7-56A3-4844-BDE8-1FA2F445DAB6");
				msg.setIcon(MessageBox.ERROR);
				String title = "";
				if(parentWindow.isShowForVSphere()){
					title = UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
				}else{
					title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
				}
				msg.setTitleHtml(title);
				msg.setMessage(UIContext.Constants.advancedPostCommandBlank());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
			else if (!validateCommandExists(val))
			{
				MessageBox msg = new MessageBox();
				msg.getDialog().getButtonById(Dialog.OK).ensureDebugId("D036E29D-F058-4060-8708-F6F3CCD2DF4F");
				msg.setIcon(MessageBox.ERROR);
				String title = "";
				if(parentWindow.isShowForVSphere()){
					title = UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
				}else{
					title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
				}
				msg.setTitleHtml(title);
				msg.setMessage(UIContext.Constants.advancedPostCommandDNE());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
		}
		if ((preCheckbox.getValue() || 
			postCheckbox.getValue()  ||
			postSnapshotCheckbox.getValue()) &&
			(usernameTF.getValue() == null || 
					usernameTF.getValue().trim().isEmpty()))
		{
			MessageBox msg = new MessageBox();
			msg.getDialog().getButtonById(Dialog.OK).ensureDebugId("12806B84-8C73-4dde-B80E-A516507986D6");
			msg.setIcon(MessageBox.ERROR);
			String title = "";
			if(parentWindow.isShowForVSphere()){
				title = UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
			}else{
				title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
			}
			msg.setTitleHtml(title);
			msg.setMessage(UIContext.Constants.advancedUserNameCannotBeBlank());
			msg.setModal(true);
			Utils.setMessageBoxDebugId(msg);
			msg.show();
			return false;
		}
		
		if ((preCheckbox.getValue() || 
				postCheckbox.getValue()  ||
				postSnapshotCheckbox.getValue()) &&
				(passwordTF.getValue() == null || 
						passwordTF.getValue().trim().isEmpty()))
			{
				MessageBox msg = new MessageBox();
				msg.getDialog().getButtonById(Dialog.OK).ensureDebugId("805F1CC2-933C-4c50-9BAA-488B82730C6E");
				msg.setIcon(MessageBox.ERROR);
				String title = "";
				if(parentWindow.isShowForVSphere()){
					title = UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
				}else{
					title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
				}
				msg.setTitleHtml(title);
				msg.setMessage(UIContext.Constants.advancedPasswordCannotBeBlank());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
			
		return true;
	}

	private boolean validateCommandExists(String val) {
		//TODO: Check that the command actually exists on the server
		return true;
		
	}
	
	public void setEditable(boolean isEditable){
		preCheckbox.setEnabled(isEditable);
		postCheckbox.setEnabled(isEditable);
		postCheckBox2.setEnabled(isEditable);
		postSnapshotCheckbox.setEnabled(isEditable);
		
		onExitCodeCheckbox.setEnabled(isEditable);
		runJobRadio.setEnabled(isEditable);
		failJobRadio.setEnabled(isEditable);
		
		preCommand.setEnabled(isEditable);
		postCommand.setEnabled(isEditable);
		postSnapshotCommand.setEnabled(isEditable);
		exitCodeNumberField.setEnabled(isEditable);
			
		usernameTF.setEnabled(isEditable);
		passwordTF.setEnabled(isEditable);
	}
	
/*	public CheckBox getEnableSpaceNotification() {
		return enableSpaceNotification;
	}*/
}

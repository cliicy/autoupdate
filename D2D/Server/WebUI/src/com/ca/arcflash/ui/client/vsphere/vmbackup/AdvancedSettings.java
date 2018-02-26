package com.ca.arcflash.ui.client.vsphere.vmbackup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.VMBackupSettingModel;
import com.ca.arcflash.ui.client.model.VSphereBackupSettingModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.HTML;

public class AdvancedSettings {
	
	private VMBackupSettingWindow parentWindow;

	// notification
	private CheckBox enableEmailOnMissedJob;
	private CheckBox enableEmail;// Failed or Crash
	private CheckBox enableEmailOnSuccess;
	private CheckBox enableSpaceNotification;
	
	private CheckBox preCheckbox;
	private CheckBox postCheckbox;
	private CheckBox postSnapshotCheckbox;
	
	private CheckBox onExitCodeCheckbox;
	private Radio runJobRadio;
	private Radio failJobRadio;
	
	private TextField<String> preCommand;
	private TextField<String> postCommand;
	private TextField<String> postSnapshotCommand;
	private NumberField exitCodeNumberField;
		
	private LayoutContainer container;
	
	private VMBackupSettingModel settingsModel;	
	private NumberField spaceMeasureNum;
	private BaseSimpleComboBox<String> spaceMeasureUnit;
	
	static final int DEFAULT_EXIT_CODE = 0;
	static final int QJDTO_B_RUN_JOB = 0x00001000;
	static final int QJDTO_B_FAIL_JOB = 0x00002000;	
	public static final String MeasureUnitPercent = "%";
	public static final String MeasureUnitMegabyte = "MB";
	//old value for freespace unit;
	private String oldValue;
	private double cachePecentage;
	
	public AdvancedSettings(VMBackupSettingWindow w)
	{
		parentWindow = w;
	}
	
	public LayoutContainer Render()
	{
		container = new LayoutContainer();
		container.setScrollMode(Scroll.AUTOY);
		TableLayout rl = new TableLayout();
		rl.setWidth("97%");
		rl.setHeight("95%");
		container.setLayout(rl);
				
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.backupSettingsPrePost());
		label.addStyleName("restoreWizardTitle");
		container.add(label);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.advancedLabelNotifications());
		label.addStyleName("restoreWizardSubItem");
		container.add(label);
		
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
		enableEmailOnMissedJob.ensureDebugId("D32B7FF6-DEF7-4840-920D-9B090C19EB85");
		enableEmailOnMissedJob.setBoxLabel(UIContext.Constants.advancedCheckboxEnableEmailOnMissedJobs());
		
		
		TableData missedJobTD = new TableData();
		missedJobTD.setColspan(4);
		tableContainer.add(enableEmailOnMissedJob, missedJobTD);
		
		enableEmail = new CheckBox();
		enableEmail.ensureDebugId("FFA476A0-1807-4321-A02B-16EC04E0D297");
		enableEmail.setBoxLabel(UIContext.Constants.advancedCheckboxEnableEmail());
		
		TableData failTD = new TableData();
		failTD.setColspan(4);
		tableContainer.add(enableEmail, failTD);
		
		enableEmailOnSuccess = new CheckBox();
		enableEmailOnSuccess.ensureDebugId("F3BE8E0B-7A83-4d8c-A350-7CD60ACC734F");
		enableEmailOnSuccess.setBoxLabel(UIContext.Constants.advancedCheckboxEnableEmailOnSuccess());
		
		TableData successTD = new TableData();
		successTD.setColspan(4);
		tableContainer.add(enableEmailOnSuccess, successTD);
		
		enableSpaceNotification = new CheckBox();
		enableSpaceNotification.ensureDebugId("E9CA32D2-D6CE-44d0-BEF6-BF6AC71A7D64");
		enableSpaceNotification.setBoxLabel(UIContext.Constants.advancedCheckboxEnableSpaceNotification());
		
		TableData spaceNotificationTD = new TableData();
		spaceNotificationTD.setWidth("50%");
		tableContainer.add(enableSpaceNotification, spaceNotificationTD);
			
		spaceMeasureNum = new NumberField();
		spaceMeasureNum.ensureDebugId("3897D41E-DAB3-4a85-AFB2-DCAE3725B074");
		spaceMeasureNum.setAllowNegative(false);
		spaceMeasureNum.setEnabled(false);
		spaceMeasureNum.setWidth(100);
		spaceMeasureNum.setValue(5);
		TableData spaceMeasureNumTD = new TableData();
		spaceMeasureNumTD.setWidth("15");
		spaceMeasureNumTD.setHorizontalAlign(HorizontalAlignment.RIGHT);
		tableContainer.add(spaceMeasureNum,spaceMeasureNumTD);
		
		spaceMeasureUnit = new BaseSimpleComboBox<String>();
		spaceMeasureUnit.ensureDebugId("7D2A9883-750A-4ea9-B3FE-A0CE8D0C4443");
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
		
		
		TableData emailConfigTD = new TableData();
		emailConfigTD.setWidth("20%");
		emailConfigTD.setHorizontalAlign(HorizontalAlignment.LEFT);
		tableContainer.add(new HTML(""), emailConfigTD);
		
		container.add(tableContainer);
		
		container.add(new Html("<HR>"));
			
		label = new LabelField();
		label.setValue(UIContext.Constants.advancedLabelActions());
		label.addStyleName("restoreWizardSubItem");
		container.add(label);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.advancedLabelActionsDescription());
		container.add(label);
		
		
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
		preCheckbox.ensureDebugId("2CD2C140-BC41-4fd3-9C8C-D607E30753CA");
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
		commandContainer.add(preCheckbox, td);
		
		preCommand = new TextField<String>();
		preCommand.ensureDebugId("52333707-FF88-42c9-BCAD-2644890E900E");
		preCommand.setWidth(450);
		Utils.addToolTip(preCommand, UIContext.Constants.advancedActionsCmdEditBoxTooltip());
		commandContainer.add(preCommand, td);
		
		td = new TableData();		
		td.setColspan(1);
		td.setWidth("30%");
		
		onExitCodeCheckbox = new CheckBox();
		onExitCodeCheckbox.ensureDebugId("73E43DFC-8ACA-4ad2-9CB2-82353ADFCED8");
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
		commandContainer.add(onExitCodeCheckbox, td);
		
		
		
		td = new TableData();
		td.setColspan(1);
		td.setWidth("70%");
		
		exitCodeNumberField = new NumberField();
		exitCodeNumberField.ensureDebugId("B57A0141-EFB2-4be2-9D31-7A4A04A27AEC");
		exitCodeNumberField.setWidth(200);
		Utils.addToolTip(exitCodeNumberField, UIContext.Constants.advancedActionsCmdOnExitCodeTooltip());
		commandContainer.add(exitCodeNumberField, td);
		
		td = new TableData();
		td.setWidth("100%");
		td.setColspan(2);
				
		runJobRadio = new Radio();
		runJobRadio.ensureDebugId("3F4C4698-B113-43bd-9AF2-144AFF1579F5");
		runJobRadio.addStyleName("leftIndentFifty");
		runJobRadio.setBoxLabel(UIContext.Constants.advancedCheckboxRunJob());
		Utils.addToolTip(runJobRadio, UIContext.Constants.advancedActionsCmdRunJobTooltip());
		commandContainer.add(runJobRadio, td);
		
		failJobRadio = new Radio();
		failJobRadio.ensureDebugId("B073B112-6737-4976-8631-BB5ABCA521ED");
		failJobRadio.addStyleName("leftIndentFifty");
		failJobRadio.setBoxLabel(UIContext.Constants.advancedCheckboxFailJob());
		Utils.addToolTip(failJobRadio, UIContext.Constants.advancedActionsCmdFailJobTooltip());
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
		postSnapshotCheckbox.ensureDebugId("28B6828A-681E-4dee-AFAF-BF9FF238BCB9");
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
		postSnapshotCommand.ensureDebugId("11D58D61-4CC3-48a5-94F3-584E711250A8");
		postSnapshotCommand.setWidth(450);
		Utils.addToolTip(postSnapshotCommand, UIContext.Constants.advancedActionsSnapshotEditboxTooltip());
		commandContainer.add(postSnapshotCommand, td);
		
		
		
				
		postCheckbox = new CheckBox();
		postCheckbox.ensureDebugId("EBF296BB-37BA-440c-B361-D700C4D28E75");
		postCheckbox.setBoxLabel(UIContext.Constants.advancedCheckboxRunCommandAfter());
		postCheckbox.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				postCommand.setEnabled(postCheckbox.getValue());				
			}
	
		});
		commandContainer.add(postCheckbox, td);
		
		postCommand = new TextField<String>();
		postCommand.ensureDebugId("F8C1F57E-18C7-4166-AD4D-6DAB44627B22");
		postCommand.setWidth(450);
		Utils.addToolTip(postCommand, UIContext.Constants.advancedActionsAfterBackupEditboxTooltip());
		commandContainer.add(postCommand, td);
		
		container.add(commandContainer);
		
		return container;
	}

	public void RefreshData(VMBackupSettingModel model) {
		
		if (model != null)
		{
			settingsModel = model;
			
			String pre = model.getCommandBeforeBackup();
			
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
			}
						
			String post = model.getCommandAfterBackup();
			if (post == null || post.trim().length() == 0)
			{
				//Default
				postCheckbox.setValue(false);
				postCommand.setEnabled(false);
			}
			else 
			{
				postCheckbox.setValue(true);
				postCommand.setValue(post);
				postCommand.setEnabled(true);
				
			}
			
			if (model.getEnablePreExitCode() == null || model.getEnablePreExitCode() == false)
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
			
		}
		else
		{
			exitCodeNumberField.setValue(0);
		}
		
		this.container.repaint();
		
	}
	
}

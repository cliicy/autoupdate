package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.SRMPkiAlertSettingPanel;
import com.ca.arcflash.ui.client.coldstandby.DisclourePanelImageBundles;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.EmailUtils;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.DestinationCapacityModel;
import com.ca.arcflash.ui.client.model.EmailAlertsModel;
import com.ca.arcflash.ui.client.model.IEmailConfigModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;

public class EmailAlerts {
	
	DisclosurePanel BackupJobAlertsPanel;
	DisclosurePanel DiskSpaceAlertsPanel;
	DisclosurePanel UpdatesAlertsPanel;
	DisclosurePanel PKIAlertsPanel;
	
	private PreferencesSettingsContent parentWindow;
	//private BackupSettingsWindow backupSettingsWindow;

	// notification
	private CheckBox enableEmailOnMissedJob;
	private CheckBox enableEmail;// Failed or Crash
	private CheckBox enableEmailOnSuccess;
	private CheckBox enableEmailOnMergeFailure;
	private CheckBox enableEmailOnMergeSuccess;
	private CheckBox enableSpaceNotification;
	private CheckBox enableUpdatesNotification;
	private CheckBox enableEmailOnSrmPkiAlert;
	
	public Button	settingsButton; 
	
	private LayoutContainer container;
	private PreferencesEmailSettingsWindow settingsDlg;
	private SRMPkiAlertSettingPanel alertSettingPanel;
	
	private EmailAlertsModel emailSettingsModel;	
	private IEmailConfigModel edgeEmailSettingsModel;	
	private NumberField spaceMeasureNum;
	private BaseSimpleComboBox<String> spaceMeasureUnit;
	
	static final int DEFAULT_EXIT_CODE = 0;
	static final int QJDTO_B_RUN_JOB = 0x00001000;
	static final int QJDTO_B_FAIL_JOB = 0x00002000;	
	public static final String MeasureUnitPercent = "%";
	public static final String MeasureUnitMegabyte = UIContext.Constants.MB();
	private static final double DEFAUL_MEASURE_VALUE = 5;
	
	//old value for freespace unit;
	private String oldValue;
//	private double cachePecentage;
	
	private CheckBox enableSettings;
	private LayoutContainer childContainer;
	
	public EmailAlerts(PreferencesSettingsContent prefWindow)
	{
		parentWindow = prefWindow;
	}
	
	public LayoutContainer Render()
	{
		container = new LayoutContainer();
		container.setScrollMode(Scroll.AUTOY);
		TableLayout rl = new TableLayout();
		rl.setWidth("97%");
		rl.setHeight("60%");
		container.setLayout(rl);
				
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.emailAlerts());
		label.addStyleName("restoreWizardTitle");
		container.add(label);
		
		enableSettings = new CheckBox();
		enableSettings.ensureDebugId("b9d9d98b-ef1c-40ea-bd7b-353f843aa3cf");
		enableSettings.setValue(false);
		enableSettings.setBoxLabel(UIContext.Constants.enableEmailAlertSettings());
		container.add(enableSettings);
		enableSettings.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				boolean enabled = enableSettings.getValue();
				setEnableSettings(enabled);
			}
		});
		
		childContainer = new LayoutContainer();
		rl = new TableLayout();
		childContainer.setLayout(rl);
		
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
		settingsButton.ensureDebugId("c6c9ea0a-991e-4e54-bc9f-a6176bf4cf87");
		// Tooltip
		Utils.addToolTip(settingsButton, UIContext.Constants.advancedButtonSettingsTooltip());
		
		settingsButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
								
				settingsDlg = new PreferencesEmailSettingsWindow();
				settingsDlg.setModal(true);
				settingsDlg.show();
				
				
				if((edgeEmailSettingsModel!=null)&&(isEmptyOrNull(emailSettingsModel.getSMTP()))) {
					settingsDlg.setSettings(edgeEmailSettingsModel);
				}
				else {
					settingsDlg.setSettings(emailSettingsModel);
				}
				
				settingsDlg.addWindowListener( new WindowListener(){
					public void	windowHide(WindowEvent we)
					{
						//Click ok the button should not be null
						if (we.getButtonClicked() != null)
						{
							settingsDlg.saveSettings(emailSettingsModel);
						}
					}
				});
			}			
		});
		
		TableData emailConfigTD = new TableData();
//		settingsButton.setEnabled(false);
		settingsButton.setStyleAttribute("margin-top", "8px");
		settingsButton.setStyleAttribute("margin-bottom", "8px");
		emailConfigTD.setWidth("20%");
		emailConfigTD.setHorizontalAlign(HorizontalAlignment.LEFT);
		childContainer.add(settingsButton, emailConfigTD);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.preferencesLabelNotifications());
		label.addStyleName("restoreWizardSubItem");
		childContainer.add(label);
		
		LayoutContainer tableContainer = new LayoutContainer();
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.setCellPadding(2);
		tableLayout.setCellSpacing(2);
		tableLayout.setColumns(3);
		tableLayout.setWidth("100%");		
		tableContainer.setLayout(tableLayout);

		TableData notification = new TableData();
		notification.setColspan(3);
		notification.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.preferencesLabelSendEmail());
		tableContainer.add(label,notification);
		
		//Backup jobs related
		BackupJobAlertsPanel = new DisclosurePanel((DisclourePanelImageBundles) 
				GWT.create(DisclourePanelImageBundles.class), 
				UIContext.Constants.backupJobAlerts(), false);
		//BackupJobAlertsPanel.ensureDebugId("99e12c3c-69ba-46c9-8112-e415b94694f6");
		BackupJobAlertsPanel.setWidth("100%");
		BackupJobAlertsPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		BackupJobAlertsPanel.setOpen(true);

		FlexTable backupJobAlertsTable = new FlexTable();
		backupJobAlertsTable.setCellPadding(5);
		backupJobAlertsTable.setCellSpacing(10);
		
		enableEmailOnMissedJob = new CheckBox();
		enableEmailOnMissedJob.ensureDebugId("221fba6b-d12e-428b-93a9-cb3eedbe58c4");
		enableEmailOnMissedJob.setBoxLabel(UIContext.Constants.preferencesCheckboxEnableEmailOnMissedJobs());
		enableEmailOnMissedJob.setStyleAttribute("white-space", "normal");
//		enableEmailOnMissedJob.addListener(Events.Change, new Listener<FieldEvent>()
//		{
//			@Override
//			public void handleEvent(FieldEvent be) {
//				settingsButton.setEnabled(enableEmail.getValue() 
//									   || enableEmailOnSuccess.getValue()
//									   || enableSpaceNotification.getValue()
//									   || enableEmailOnMissedJob.getValue() 
//									   || enableUpdatesNotification.getValue()
//									   || enableEmailOnSrmPkiAlert.getValue());				
//			}
//	
//		});
		
		backupJobAlertsTable.setWidget(0, 0, enableEmailOnMissedJob);
		backupJobAlertsTable.getCellFormatter().setVerticalAlignment(0, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);

		enableEmail = new CheckBox();
		enableEmail.ensureDebugId("22418f26-5050-4cf3-b51f-814c35e69f55");
		enableEmail.setBoxLabel(UIContext.Constants.preferencesCheckboxEnableEmail());
		enableEmail.setStyleAttribute("white-space", "normal");

		
		backupJobAlertsTable.setWidget(1, 0, enableEmail);
		backupJobAlertsTable.getCellFormatter().setVerticalAlignment(1, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		
		enableEmailOnSuccess = new CheckBox();
		enableEmailOnSuccess.ensureDebugId("60b00c92-d5fe-43ab-984f-999df29da3fe");
		enableEmailOnSuccess.setBoxLabel(UIContext.Constants.preferencesCheckboxEnableEmailOnSuccess());
		enableEmailOnSuccess.setStyleAttribute("white-space", "normal");		
		backupJobAlertsTable.setWidget(2, 0, enableEmailOnSuccess);
		backupJobAlertsTable.getCellFormatter().setVerticalAlignment(2, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		
		// mail alert for merge failed due to sessions are mounted
		enableEmailOnMergeFailure = new CheckBox();
		enableEmailOnMergeFailure.ensureDebugId("57CC563B-94AE-4729-8847-E50F302BC84D");
		enableEmailOnMergeFailure.setBoxLabel(UIContext.Constants.preferencesCheckboxEnableEmailOnMergeFailure());
		enableEmailOnMergeFailure.setStyleAttribute("white-space", "normal");		
		backupJobAlertsTable.setWidget(3, 0, enableEmailOnMergeFailure);
		backupJobAlertsTable.getCellFormatter().setVerticalAlignment(3, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		
		enableEmailOnMergeSuccess = new CheckBox();
		enableEmailOnMergeSuccess.ensureDebugId("59C2351D-60DB-4889-B610-6CDEB2B34663");
		enableEmailOnMergeSuccess.setBoxLabel(UIContext.Constants.preferencesCheckboxEnableEmailOnMergeSuccess());
		enableEmailOnMergeSuccess.setStyleAttribute("white-space", "normal");		
		backupJobAlertsTable.setWidget(4, 0, enableEmailOnMergeSuccess);
		backupJobAlertsTable.getCellFormatter().setVerticalAlignment(3, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		
		backupJobAlertsTable.setWidget(5, 0, new Label());
		
		BackupJobAlertsPanel.add(backupJobAlertsTable);
		
		TableData backupAlertsTD = new TableData();
		backupAlertsTD.setColspan(3);
		tableContainer.add(BackupJobAlertsPanel, backupAlertsTD);
		
		//Disk space related
		DiskSpaceAlertsPanel = new DisclosurePanel((DisclourePanelImageBundles) 
				GWT.create(DisclourePanelImageBundles.class), 
				UIContext.Constants.diskSpaceAlerts(), false);
		DiskSpaceAlertsPanel.setWidth("100%");
		DiskSpaceAlertsPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		DiskSpaceAlertsPanel.setOpen(true);
		
		LayoutContainer diskPanel = new LayoutContainer();
		tableLayout = new TableLayout(3);
		//tableLayout.setCellPadding(5);
		tableLayout.setCellSpacing(10);
		tableLayout.setWidth("100%");
		diskPanel.setLayout(tableLayout);
			
		enableSpaceNotification = new CheckBox();
		enableSpaceNotification.ensureDebugId("704fa4e1-c526-45ae-8158-bda9df3645e7");
		enableSpaceNotification.setBoxLabel(UIContext.Constants.preferencesCheckboxEnableSpaceNotification());
		enableSpaceNotification.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
//				settingsButton.setEnabled(enableEmail.getValue() 
//									   || enableEmailOnSuccess.getValue()
//									   || enableSpaceNotification.getValue()
//									   || enableEmailOnMissedJob.getValue() 
//									   || enableUpdatesNotification.getValue()
//									   || enableEmailOnSrmPkiAlert.getValue());	
				spaceMeasureNum.setEnabled(enableSpaceNotification.getValue());
				spaceMeasureUnit.setEnabled(enableSpaceNotification.getValue());
			}
	
		});
		TableData tableData = new TableData();
		tableData.setWidth("65%");
		tableData.setVerticalAlign(VerticalAlignment.MIDDLE);
		diskPanel.add(enableSpaceNotification, tableData);
		//DiskSpaceAlertsPanel.add(diskSpaceAlertsTable);
			
		spaceMeasureNum = new NumberField();
		spaceMeasureNum.ensureDebugId("8a9dad3f-231d-4e59-a24a-aaf64344d555");
		spaceMeasureNum.setAllowNegative(false);
		spaceMeasureNum.setEnabled(false);
		spaceMeasureNum.setWidth(100);
		spaceMeasureNum.setValue(Math.round(DEFAUL_MEASURE_VALUE));
		
		tableData = new TableData();
		tableData.setWidth("10%");
		tableData.setVerticalAlign(VerticalAlignment.MIDDLE);
		diskPanel.add(spaceMeasureNum, tableData);
				
		spaceMeasureUnit = new BaseSimpleComboBox<String>();
		spaceMeasureUnit.ensureDebugId("1750d838-bf6e-4bfc-89fb-f38e87752f60");
		spaceMeasureUnit.setEnabled(false);
		spaceMeasureUnit.setWidth(60);
		spaceMeasureUnit.add(MeasureUnitPercent);
		spaceMeasureUnit.add(MeasureUnitMegabyte);
		spaceMeasureUnit.setSimpleValue(MeasureUnitPercent);
		oldValue = MeasureUnitPercent;

		spaceMeasureUnit.addListener(Events.Select, new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				/*DestinationCapacityModel destCapacity = parentWindow.model.getdestCapacityModel();
				
				if(destCapacity==null){
					return;
				}*/
					
				String value = spaceMeasureUnit.getSimpleValue();
				if(value.equals(oldValue)){
					return;
				}
				
				/*double freeSpace = spaceMeasureNum.getValue().doubleValue();
				long totalVolumeSize = destCapacity.getTotalVolumeSize();
				totalVolumeSize = totalVolumeSize>>20;
				if(totalVolumeSize == 0){
					spaceMeasureNum.setValue(0);
					return;
				}
//*/				NumberFormat formatter = NumberFormat.getFormat("000.00");		
				if(MeasureUnitPercent.equals(value)&&MeasureUnitMegabyte.equals(oldValue)){
					oldValue = MeasureUnitPercent;
//					cachePecentage = (freeSpace/totalVolumeSize);
					spaceMeasureNum.setValue(Double.valueOf(formatter.format(DEFAUL_MEASURE_VALUE)));
				}
				if(MeasureUnitMegabyte.equals(value)&&MeasureUnitPercent.equals(oldValue)){
					oldValue = MeasureUnitMegabyte;
//					cachePecentage = cachePecentage == 0 ? freeSpace/100 : cachePecentage;
					spaceMeasureNum.setValue(Math.round(DEFAUL_MEASURE_VALUE));
				}
			}
		});
		
		tableData = new TableData();
//		tableData.setWidth("10%");
		tableData.setVerticalAlign(VerticalAlignment.MIDDLE);
		diskPanel.add(spaceMeasureUnit, tableData);
		DiskSpaceAlertsPanel.add(diskPanel);
		
		TableData diskSpaceAlertsTD = new TableData();
		diskSpaceAlertsTD.setColspan(3);
		tableContainer.add(DiskSpaceAlertsPanel, diskSpaceAlertsTD);
		
		// updates related
		UpdatesAlertsPanel = new DisclosurePanel(
				(DisclourePanelImageBundles) GWT.create(DisclourePanelImageBundles.class), 
				UIContext.Constants.updatesAlerts(), false);
		UpdatesAlertsPanel.setWidth("100%");
		UpdatesAlertsPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		UpdatesAlertsPanel.setOpen(true);
		
		FlexTable updatesAlertsTable = new FlexTable();
		updatesAlertsTable.setCellPadding(5);
		updatesAlertsTable.setCellSpacing(10);
		
		enableUpdatesNotification = new CheckBox();
		enableUpdatesNotification.ensureDebugId("54ae9ee1-2609-4c4b-a3c7-7a5e588cc306");
		enableUpdatesNotification.setBoxLabel(UIContext.Constants.preferencesCheckBoxEnableNewUpdatesAvailable());
//		enableUpdatesNotification.addListener(Events.Change, new Listener<FieldEvent>()
//		{
//			@Override
//			public void handleEvent(FieldEvent be) {
//				settingsButton.setEnabled(enableEmail.getValue() 
//						               || enableEmailOnSuccess.getValue()
//						               || enableSpaceNotification.getValue()
//						               || enableEmailOnMissedJob.getValue() 
//						               || enableUpdatesNotification.getValue()
//						               || enableEmailOnSrmPkiAlert.getValue());	
//			}
//	
//		});
		
		updatesAlertsTable.setWidget(0, 0, enableUpdatesNotification);
		updatesAlertsTable.getCellFormatter().setVerticalAlignment(0, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		UpdatesAlertsPanel.add(updatesAlertsTable);
		
		TableData updatesAlertsTD = new TableData();
		updatesAlertsTD.setColspan(3);
		tableContainer.add(UpdatesAlertsPanel, updatesAlertsTD);
		
		//PKI related
		PKIAlertsPanel = new DisclosurePanel((DisclourePanelImageBundles) 
				GWT.create(DisclourePanelImageBundles.class), 
				UIContext.Constants.pkiAlerts(), false);
		PKIAlertsPanel.setWidth("100%");
		PKIAlertsPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		PKIAlertsPanel.setOpen(true);
		
		FlexTable PKIAlertsTable = new FlexTable();
		PKIAlertsTable.setCellPadding(5);
		PKIAlertsTable.setCellSpacing(10);
		
		enableEmailOnSrmPkiAlert = new CheckBox();
		enableEmailOnSrmPkiAlert.ensureDebugId("4ae7cbe6-d663-4ea1-a7de-7f1d233e0027");
		enableEmailOnSrmPkiAlert.setBoxLabel(UIContext.Constants.advancedCheckBoxEnableEmailOnSrmPkiAlert());
		enableEmailOnSrmPkiAlert.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
//				settingsButton.setEnabled(enableEmail.getValue() 
//						               || enableEmailOnSuccess.getValue()
//						               || enableSpaceNotification.getValue()
//						               || enableEmailOnMissedJob.getValue()
//						               || enableUpdatesNotification.getValue()
//						               || enableEmailOnSrmPkiAlert.getValue());	
				alertSettingPanel.setEnabled(enableEmailOnSrmPkiAlert.getValue());
			}
	
		});
		PKIAlertsTable.setWidget(0, 0, enableEmailOnSrmPkiAlert);
		PKIAlertsTable.getCellFormatter().setVerticalAlignment(0, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		
		alertSettingPanel = new SRMPkiAlertSettingPanel();
		alertSettingPanel.setEnabled(false);
		
		PKIAlertsTable.setWidget(1, 0, alertSettingPanel);
		PKIAlertsTable.getCellFormatter().setVerticalAlignment(1, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		PKIAlertsTable.setWidth("100%");
		PKIAlertsPanel.add(PKIAlertsTable);
		
		TableData PKIAlertsTD = new TableData();
		PKIAlertsTD.setColspan(3);
		tableContainer.add(PKIAlertsPanel, PKIAlertsTD);

		childContainer.add(tableContainer);
		
		TableData data = new TableData();
		data.setStyle("padding-left:8px");
		container.add(childContainer, data);
		
		return container;
	}

	public void RefreshData(EmailAlertsModel model) {
		emailSettingsModel = model; 
		if(emailSettingsModel == null) {
			emailSettingsModel = new EmailAlertsModel();
//			emailSettingsModel.setEnableEmail(true); // for default value.
//			emailSettingsModel.setEnableSrmPkiAlert(true);
		}
		
		if(emailSettingsModel.getEnableSettings() != null) {
			enableSettings.setValue(emailSettingsModel.getEnableSettings());
		} else {
			enableSettings.setValue(false);
		}

		if (emailSettingsModel.getEnableEmailOnMissedJob() != null) {
			enableEmailOnMissedJob.setValue(emailSettingsModel.getEnableEmailOnMissedJob());
		} else {
			enableEmailOnMissedJob.setValue(false);
		}

		if (emailSettingsModel.getEnableEmail() != null) {
			enableEmail.setValue(emailSettingsModel.getEnableEmail());
		} else {
			enableEmail.setValue(false);
		}

		if (emailSettingsModel.getEnableEmailOnSuccess() != null) {
			enableEmailOnSuccess.setValue(model.getEnableEmailOnSuccess());
		} else {
			enableEmailOnSuccess.setValue(false);
		}
		
		if (emailSettingsModel.getEnableEmailOnMergeFailure() != null) {
			enableEmailOnMergeFailure.setValue(model.getEnableEmailOnMergeFailure());
		} else {
			enableEmailOnMergeFailure.setValue(false);
		}
		
		if (emailSettingsModel.getEnableEmailOnMergeSuccess() != null) {
			enableEmailOnMergeSuccess.setValue(model.getEnableEmailOnMergeSuccess());
		} else {
			enableEmailOnMergeSuccess.setValue(false);
		}
		
		if (emailSettingsModel.getEnableSpaceNotification() != null) {
			enableSpaceNotification.setValue(emailSettingsModel.getEnableSpaceNotification());
			if (emailSettingsModel.getEnableSpaceNotification().booleanValue() == true) {
				spaceMeasureNum.setValue(emailSettingsModel.getSpaceMeasureNum());
				spaceMeasureUnit.setSimpleValue(emailSettingsModel.getSpaceMeasureUnit());
				oldValue = emailSettingsModel.getSpaceMeasureUnit();
			}
			spaceMeasureNum.setEnabled(emailSettingsModel.getEnableSpaceNotification().booleanValue());
			spaceMeasureUnit.setEnabled(emailSettingsModel.getEnableSpaceNotification().booleanValue());
		} else {
			enableSpaceNotification.setValue(false);
		}
		if (emailSettingsModel.getEnableEmailOnNewUpdates() != null) {
			enableUpdatesNotification.setValue(emailSettingsModel.getEnableEmailOnNewUpdates());
		} else {
			enableUpdatesNotification.setValue(false);
		}

		if (emailSettingsModel.getEnableSrmPkiAlert() != null) {
			enableEmailOnSrmPkiAlert.setValue(emailSettingsModel.getEnableSrmPkiAlert());
			if (emailSettingsModel.getEnableSrmPkiAlert().booleanValue() == true) {
				alertSettingPanel.setEnabled(true);
				/*
				 * alertSettingPanel.setCpuAlertUtilThreshold(model.
				 * getCpuAlertUtilThreshold());
				 * alertSettingPanel.setMemoryAlertUtilThreshold
				 * (model.getMemoryAlertUtilThreshold());
				 * alertSettingPanel.setDiskAlertUtilThreshold
				 * (model.getDiskAlertUtilThreshold());
				 * alertSettingPanel.setNetworkAlertUtilThreshold
				 * (model.getMemoryAlertUtilThreshold());
				 */
			} else {
				alertSettingPanel.setEnabled(false);
			}
		} else {
			enableEmailOnSrmPkiAlert.setValue(false);
			alertSettingPanel.setEnabled(false);
		}
		
		alertSettingPanel.loadData(emailSettingsModel);

			
//			settingsButton.setEnabled(enableEmail.getValue() 
//								   || enableEmailOnSuccess.getValue()
//								   || enableSpaceNotification.getValue()
//								   || enableEmailOnMissedJob.getValue() 
//								   || enableUpdatesNotification.getValue()
//								   || enableEmailOnSrmPkiAlert.getValue());
		
		EmailUtils.mergeEmailSettings(emailSettingsModel, edgeEmailSettingsModel);
		settingsDlg = new PreferencesEmailSettingsWindow();
		settingsDlg.setSettings(emailSettingsModel);
		
		// enable or disable the email settings.
		setEnableSettings(enableSettings.getValue());
		
		this.container.repaint();
	}
	
	public void Save()
	{
		Boolean enabled = enableSettings.getValue();
		emailSettingsModel.setEnableSettings(enabled);
		
		Boolean b1 = enableEmailOnMissedJob.getValue();		
		emailSettingsModel.setEnableEmailOnMissedJob(b1);
		
		Boolean b = enableEmail.getValue();		
		emailSettingsModel.setEnableEmail(b);
		
		Boolean b2 = enableEmailOnSuccess.getValue();
		emailSettingsModel.setEnableEmailOnSuccess(b2);
		
		Boolean b3 = enableSpaceNotification.getValue();
		emailSettingsModel.setEnableSpaceNotification(b3);
		
		Boolean b4 = enableUpdatesNotification.getValue();
		emailSettingsModel.setEnableEmailOnNewUpdates(b4);

		Boolean b6 = enableEmailOnMergeFailure.getValue();
		emailSettingsModel.setEnableEmailOnMergeFailure(b6);

		emailSettingsModel.setEnableEmailOnMergeSuccess(
				enableEmailOnMergeSuccess.getValue());
		
		if(b3)
		{
			emailSettingsModel.setSpaceMeasureNum(spaceMeasureNum.getValue().doubleValue());
			emailSettingsModel.setSpaceMeasureUnit(spaceMeasureUnit.getSimpleValue());
		}
		
		Boolean b5 = enableEmailOnSrmPkiAlert.getValue();
		emailSettingsModel.setEnableSrmPkiAlert(b5);
		if (enabled)
		{
			settingsDlg.saveSettings(emailSettingsModel);			
		}
		
		alertSettingPanel.saveData(emailSettingsModel);
		
		parentWindow.model.setEmailAlerts(emailSettingsModel);		
	}
	public boolean Validate()
	{		
		if(!enableSettings.getValue()) {
			return true;
		}

		if(enableSpaceNotification.getValue())
		{
			if ( spaceMeasureNum.getValue() == null){
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
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
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
				msg.setMessage(UIContext.Constants.destinationThresholdAlertPercent());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
		}
		
		if (enableSettings.getValue())
		{						
			int ret = settingsDlg.validate();	
			if (ret != 0)
			{
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
				msg.setMessage(UIContext.Constants.mustConfigEmailSettins());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
		}
		
		if (alertSettingPanel.verifySettingData() != null) {
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.ERROR);
			msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
			msg.setMessage(alertSettingPanel.verifySettingData());
			msg.setModal(true);
			Utils.setMessageBoxDebugId(msg);
			msg.show();
			return false;
		}
		return true;
	}
	
	public CheckBox getEnableSpaceNotification() {
		return enableSpaceNotification;
	}
	
	private boolean isEmptyOrNull(String target) {
		if(target == null) {
			return true;
		}
		else if(target.length()==0) {
			return true;
		}
		else {
			return false;
		}
	}
	public void setDefaultEmail(IEmailConfigModel emailConfigModel)
	{
				
		if(emailConfigModel!=null)
		{
			edgeEmailSettingsModel = emailConfigModel;	
		}
	}
	
	public void setEnableSettings(boolean enabled) {
		
		settingsButton.setEnabled(enabled);
		enableEmailOnMissedJob.setEnabled(enabled);
		enableEmail.setEnabled(enabled);// Failed or Crash
		enableEmailOnSuccess.setEnabled(enabled);
		enableEmailOnMergeFailure.setEnabled(enabled);
		enableEmailOnMergeSuccess.setEnabled(enabled);
		enableSpaceNotification.setEnabled(enabled);
		enableUpdatesNotification.setEnabled(enabled);
		enableEmailOnSrmPkiAlert.setEnabled(enabled);
		if(enabled && enableEmailOnSrmPkiAlert.getValue())
			alertSettingPanel.setEnabled(enabled);
		else
			alertSettingPanel.setEnabled(false);
		
		if(enabled && enableSpaceNotification.getValue()) {
			spaceMeasureNum.setEnabled(enabled);
			spaceMeasureUnit.setEnabled(enabled);
		} else {
			spaceMeasureNum.setEnabled(false);
			spaceMeasureUnit.setEnabled(false);
		}
	}
	
	public void setEditable(boolean isEditable){
		enableSettings.setEnabled(isEditable);
		settingsButton.setEnabled(isEditable);
		enableEmailOnMissedJob.setEnabled(isEditable);
		enableEmail.setEnabled(isEditable);// Failed or Crash
		enableEmailOnSuccess.setEnabled(isEditable);
		enableEmailOnMergeFailure.setEnabled(isEditable);
		enableEmailOnMergeSuccess.setEnabled(isEditable);
		enableSpaceNotification.setEnabled(isEditable);
		enableUpdatesNotification.setEnabled(isEditable);
		enableEmailOnSrmPkiAlert.setEnabled(isEditable);
		alertSettingPanel.setEnabled(isEditable);
		spaceMeasureNum.setEnabled(isEditable);
		spaceMeasureUnit.setEnabled(isEditable);
	}
}

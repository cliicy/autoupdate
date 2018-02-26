package com.ca.arcflash.ui.client.vsphere.setting;

import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.EmailUtils;
import com.ca.arcflash.ui.client.common.ISettingsContent;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.homepage.PreferencesEmailSettingsWindow;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.EmailAlertsModel;
import com.ca.arcflash.ui.client.model.IEmailConfigModel;
import com.ca.arcflash.ui.client.model.VMBackupSettingModel;
import com.ca.arcflash.ui.client.model.VSphereBackupSettingModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
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
import com.google.gwt.user.client.ui.Widget;

public class EmailAlertSettings extends LayoutContainer implements ISettingsContent{
	
//	private VSphereBackupSettingContent parentWindow;

//	private LayoutContainer container;
	
	// notification
	private CheckBox enableEmailOnMissedJob;
	private CheckBox enableEmail;// Failed or Crash
	private CheckBox enableEmailOnSuccess;
	private CheckBox enableSpaceNotification;
	private CheckBox enableEmailOnHostNotFound;
	private CheckBox enableEmailOnDataStoreNotEnough;
	private CheckBox enableEmailOnLicensefailure;
	private CheckBox enableEmailOnMergeFailure;
	private CheckBox enableEmailOnMergeSuccess;
	private CheckBox enableEmailOnJobQueue;
	private CheckBox enableEmailOnCheckRPSFailure;
	
	private Button	settingsButton; 
	PreferencesEmailSettingsWindow settingsDlg;
	
	private NumberField spaceMeasureNum;
	private BaseSimpleComboBox<String> spaceMeasureUnit;
	
	public static final String MeasureUnitPercent = "%";
	public static final String MeasureUnitMegabyte = "MB";
	
//	private String oldValue;
//	private double cachePecentage;
	
	ISettingsContentHost contentHost;
	private boolean isForEdge = false;
	private int settingsContentId = -1;
	
	private VSphereBackupSettingModel settingsModel;
	private IEmailConfigModel edgeEmailSettingsModel;	
	
	public EmailAlertSettings()
	{
//		parentWindow = w;
	}
	
	private void doInitialization(){
		TableLayout rl = new TableLayout();
		rl.setWidth("97%");
		//rl.setHeight("95%");
		setLayout(rl);
				
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.emailAlerts());
		label.addStyleName("restoreWizardTitle");
		add(label);
		
		/*label = new LabelField();
		label.setText(UIContext.Constants.advancedLabelNotifications());
		label.addStyleName("restoreWizardSubItem");
		container.add(label);*/
		
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
		
		Listener<FieldEvent> listener = new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				setButtonStatus();
			}
		};
		
		label = new LabelField();
		label.setValue(UIContext.Constants.advancedLabelSendEmail());
		tableContainer.add(label,notification);
		
		enableEmailOnMissedJob = new CheckBox();
		enableEmailOnMissedJob.ensureDebugId("361EE431-2358-4005-9F75-34E10C475405");
		enableEmailOnMissedJob.setBoxLabel(UIContext.Constants.advancedCheckboxEnableEmailOnMissedJobs());
		enableEmailOnMissedJob.addListener(Events.Change, listener);
		
		TableData missedJobTD = new TableData();
		missedJobTD.setColspan(3);
		tableContainer.add(enableEmailOnMissedJob, missedJobTD);

		enableEmailOnDataStoreNotEnough = new CheckBox();
		enableEmailOnDataStoreNotEnough.ensureDebugId("45C27151-14AB-45c0-81A6-A0AEDA553421");
		enableEmailOnDataStoreNotEnough.setBoxLabel(UIContext.Constants.datastoreNotEnough());
		enableEmailOnDataStoreNotEnough.addListener(Events.Change, listener);
		
		TableData dataStoreNotEnoughTD = new TableData();
		dataStoreNotEnoughTD.setColspan(3);
		//tableContainer.add(enableEmailOnDataStoreNotEnough, dataStoreNotEnoughTD);
		
		enableEmailOnLicensefailure = new CheckBox();
		enableEmailOnLicensefailure.ensureDebugId("enableEmailOnLicensefailure");
		enableEmailOnLicensefailure.setBoxLabel(UIContext.Constants.licenseFailure());
		enableEmailOnLicensefailure.addListener(Events.Change, listener);
		enableEmailOnLicensefailure.setVisible(false);
		
		//TableData LicensefailureTD = new TableData();
		//LicensefailureTD.setColspan(3);
		//tableContainer.add(enableEmailOnLicensefailure, LicensefailureTD);
		
		enableEmail = new CheckBox();
		enableEmail.ensureDebugId("BB830C7E-2E4C-4133-B6E6-2B1609195D8F");
		enableEmail.setBoxLabel(UIContext.Constants.preferencesCheckboxEnableEmailOnHBBUFailure());
		enableEmail.addListener(Events.Change, listener);
		
		TableData failTD = new TableData();
		failTD.setColspan(3);
		tableContainer.add(enableEmail, failTD);
		
		enableEmailOnSuccess = new CheckBox();
		enableEmailOnSuccess.ensureDebugId("2252264A-7F28-444f-9E38-64B362B29C48");
		enableEmailOnSuccess.setBoxLabel(UIContext.Constants.preferencesCheckboxEnableEmailOnHBBUSuccess());
		enableEmailOnSuccess.addListener(Events.Change, listener);
		
		TableData successTD = new TableData();
		successTD.setColspan(3);
		tableContainer.add(enableEmailOnSuccess, successTD);
		
		enableEmailOnMergeFailure = new CheckBox();
		enableEmailOnMergeFailure.setBoxLabel(UIContext.Constants.preferencesCheckboxEnableEmailOnMergeFailure());
		enableEmailOnMergeFailure.addListener(Events.Change, listener);
		enableEmailOnMergeFailure.ensureDebugId("9EA6D3F3-1412-4a19-A2AD-81752F44EF79");
		TableData mergeFailureTD = new TableData();
		mergeFailureTD.setColspan(3);
		tableContainer.add(enableEmailOnMergeFailure, mergeFailureTD);
		
		enableEmailOnMergeSuccess = new CheckBox();
		enableEmailOnMergeSuccess.setBoxLabel(UIContext.Constants.preferencesCheckboxEnableEmailOnMergeSuccess());
		enableEmailOnMergeSuccess.addListener(Events.Change, listener);
		enableEmailOnMergeSuccess.ensureDebugId("B996A4BF-9AF1-48aa-B24F-F9E6E9B6B54E");
		mergeFailureTD = new TableData();
		mergeFailureTD.setColspan(3);
		tableContainer.add(enableEmailOnMergeSuccess, mergeFailureTD);
		
		enableEmailOnCheckRPSFailure = new CheckBox();
		enableEmailOnCheckRPSFailure.ensureDebugId("361EE431-2358-4005-9F75-34E10C475319");  
		
		enableEmailOnCheckRPSFailure.setBoxLabel(UIContext.Constants.preferencesCheckboxEnableEmailOnRecoveryPointCheckFailure());
		enableEmailOnCheckRPSFailure.addListener(Events.Change, listener);

		TableData RPSCheckFailTD = new TableData();
		RPSCheckFailTD.setColspan(3);
		tableContainer.add(enableEmailOnCheckRPSFailure,RPSCheckFailTD);
		enableEmailOnJobQueue = new CheckBox();
		enableEmailOnJobQueue.ensureDebugId("AF8DF564-C1BC-4e87-AB8F-866DA9A436D7");
		enableEmailOnJobQueue.setBoxLabel(UIContext.Constants.vsphereJobQueue());
		enableEmailOnJobQueue.addListener(Events.Change, listener);
		TableData jobQueueTD = new TableData();
		jobQueueTD.setColspan(3);
		tableContainer.add(enableEmailOnJobQueue, jobQueueTD);
		
		//hypervisor not reachable
		enableEmailOnHostNotFound = new CheckBox();
		enableEmailOnHostNotFound.ensureDebugId("3A1620B1-9302-4ed1-9FBF-C168B8600180");
		enableEmailOnHostNotFound.setBoxLabel(UIContext.Constants.preferencesCheckboxEnableEmailOnHBBUHyperviorNotReach());
		enableEmailOnHostNotFound.addListener(Events.Change, listener);
		TableData hostNotFoundTD = new TableData();
		hostNotFoundTD.setColspan(3);
		tableContainer.add(enableEmailOnHostNotFound, hostNotFoundTD);
		
		enableSpaceNotification = new CheckBox();
		enableSpaceNotification.ensureDebugId("F351BE9B-34DF-4eb0-AC0D-994149505EEC");
		enableSpaceNotification.setBoxLabel(UIContext.Constants.advancedCheckboxEnableSpaceNotification());
		enableSpaceNotification.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				setButtonStatus();
				spaceMeasureNum.setEnabled(enableSpaceNotification.getValue()&&isForEdge);
				spaceMeasureUnit.setEnabled(enableSpaceNotification.getValue()&&isForEdge);
			}
		});
		
		TableData spaceNotificationTD = new TableData();
		spaceNotificationTD.setWidth("50%");
		tableContainer.add(enableSpaceNotification, spaceNotificationTD);
			
		spaceMeasureNum = new NumberField();
		spaceMeasureNum.ensureDebugId("431E7B15-EC85-4470-B032-0BEA44EF950C");
		spaceMeasureNum.setAllowNegative(false);
		spaceMeasureNum.setEnabled(false);
		spaceMeasureNum.setWidth(100);
		spaceMeasureNum.setValue(5);
		TableData spaceMeasureNumTD = new TableData();
		spaceMeasureNumTD.setWidth("15");
		spaceMeasureNumTD.setHorizontalAlign(HorizontalAlignment.RIGHT);
		tableContainer.add(spaceMeasureNum,spaceMeasureNumTD);
		
		spaceMeasureUnit = new BaseSimpleComboBox<String>();
		spaceMeasureUnit.ensureDebugId("FD0CD511-C2E9-4da1-A37D-26A21830318A");
		spaceMeasureUnit.setEnabled(false);
		spaceMeasureUnit.setEditable(false);
		spaceMeasureUnit.setWidth(60);
		spaceMeasureUnit.add(MeasureUnitPercent);
		spaceMeasureUnit.add(MeasureUnitMegabyte);
		spaceMeasureUnit.setSimpleValue(MeasureUnitPercent);
//		oldValue = MeasureUnitPercent;

//		spaceMeasureUnit.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {		
//
//			@Override
//			public void selectionChanged(
//					SelectionChangedEvent<SimpleComboValue<String>> se) {
//				
//				if(parentWindow.destModel==null){
//					return;
//				}
//				
//				String value = se.getSelectedItem().getValue();
//				if(value.equals(oldValue)){
//					return;
//				}
//				
//				double freeSpace = spaceMeasureNum.getValue().doubleValue();
//				long totalVolumeSize = parentWindow.destModel.getTotalVolumeSize();
//				totalVolumeSize = totalVolumeSize>>20;
//				if(totalVolumeSize == 0){
//					spaceMeasureNum.setValue(0);
//					return;
//				}
//				NumberFormat formatter = NumberFormat.getFormat("000.00");		
//				if(MeasureUnitPercent.equals(value)&&MeasureUnitMegabyte.equals(oldValue)){
//					oldValue = MeasureUnitPercent;
//					cachePecentage = (freeSpace/totalVolumeSize);
//					spaceMeasureNum.setValue(Double.valueOf(formatter.format((freeSpace/totalVolumeSize)*100)));
//				}
//				if(MeasureUnitMegabyte.equals(value)&&MeasureUnitPercent.equals(oldValue)){
//					oldValue = MeasureUnitMegabyte;
//					cachePecentage = cachePecentage == 0 ? freeSpace/100 : cachePecentage;
//					spaceMeasureNum.setValue(Math.round((cachePecentage*totalVolumeSize)));
//				}
//			}
//		});
		
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
			
		settingsButton.ensureDebugId("CBC90ED9-6981-467e-9BC7-E4B3ED8BD1FA");
		settingsButton.setText(UIContext.Constants.advancedButtonSettings());
		// Tooltip
		Utils.addToolTip(settingsButton, UIContext.Constants.advancedButtonSettingsTooltip());
		settingsButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
								
				settingsDlg = new PreferencesEmailSettingsWindow(AppType.VSPHERE);
				settingsDlg.setModal(true);
				settingsDlg.show();
				settingsDlg.setSettings(settingsModel);
				if(!isForEdge){
					settingsDlg.setEnabled(false);
				}
				
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
		emailConfigTD.setStyle("padding-left: 10px;");
		emailConfigTD.setColspan(3);
		tableContainer.add(settingsButton, emailConfigTD);
		
		add(tableContainer);
		
		VSphereBackupSettingContent.emailAlert = this;
		
	}
	
	private void setButtonStatus() {
		boolean isEnableButton = enableEmail.getValue() 
		   || enableEmailOnSuccess.getValue()
		   || enableSpaceNotification.getValue()
		   || enableEmailOnMissedJob.getValue()
		   || enableEmailOnHostNotFound.getValue()
		   || enableEmailOnLicensefailure.getValue()
		   || enableEmailOnMergeFailure.getValue()
		   || enableEmailOnJobQueue.getValue()
		   || enableEmailOnMergeSuccess.getValue()
		   || enableEmailOnCheckRPSFailure.getValue();
		
		
		settingsButton.setEnabled(isEnableButton);	
	}
	
	public void save(BackupSettingsModel setteingsModel){
		
		Boolean b1 = enableEmailOnMissedJob.getValue();		
		setteingsModel.setEnableEmailOnMissedJob(b1);
		
		Boolean b = enableEmail.getValue();		
		setteingsModel.setEnableEmail(b);
		
		Boolean b2 = enableEmailOnSuccess.getValue();
		setteingsModel.setEnableEmailOnSuccess(b2);
		
		Boolean b3 = enableSpaceNotification.getValue();
		setteingsModel.setEnableSpaceNotification(b3);
		
		Boolean b7 = enableEmailOnMergeFailure.getValue();
		setteingsModel.setEnableEmailOnMergeFailure(b7);
		
		Boolean b9 = enableEmailOnMergeSuccess.getValue();
		setteingsModel.setEnableEmailOnMergeSuccess(b9);
		
		Boolean b10 = enableEmailOnCheckRPSFailure.getValue();  
		setteingsModel.setEnableEmailOnRecoveryPointCheckFailure(b10);
		if(setteingsModel instanceof VSphereBackupSettingModel) {
			VSphereBackupSettingModel model = (VSphereBackupSettingModel)setteingsModel;
			Boolean b4 = enableEmailOnHostNotFound.getValue();
			model.setEnableEmailOnHostNotFound(b4);
			
			Boolean b5 = false;
			model.setEnableEmailOnDataStoreNotEnough(b5);
			
			Boolean b6 = enableEmailOnLicensefailure.getValue();
			model.setEnableEmailOnLicensefailure(b6);
			
			boolean b8 = enableEmailOnJobQueue.getValue();
			model.setEnableEmailOnJobQueue(b8);
			
			if (b1 || b || b2 || b3 || b4 || b5 || b6 || b7 || b8 || b9 || b10)
			{
				if(settingsDlg != null)
					settingsDlg.saveSettings(model);
			}
		}
		
		if(b3)
		{
			setteingsModel.setSpaceMeasureNum(spaceMeasureNum.getValue().doubleValue());
			setteingsModel.setSpaceMeasureUnit(spaceMeasureUnit.getSimpleValue());
		}
		
	}
	
	public void RefreshData(BackupSettingsModel model) {
		if (model != null)
		{
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
			
			if (model.getEnableEmailOnMergeFailure() != null)
			{
				enableEmailOnMergeFailure.setValue(model.getEnableEmailOnMergeFailure());				
			}
			else
			{
				enableEmailOnMergeFailure.setValue(false);
			}
			
			if (model.getEnableEmailOnMergeSuccess() != null)
			{
				enableEmailOnMergeSuccess.setValue(model.getEnableEmailOnMergeSuccess());				
			}
			else
			{
				enableEmailOnMergeSuccess.setValue(false);
			}
			
			if (model.getEnableEmailOnRecoveryPointCheckFailure() != null)
			{
				enableEmailOnCheckRPSFailure.setValue(model.getEnableEmailOnRecoveryPointCheckFailure());  // lds
			}
			else {
				enableEmailOnCheckRPSFailure.setValue(false);
			}
			
			if(model instanceof VSphereBackupSettingModel) {
				VSphereBackupSettingModel vmModel = (VSphereBackupSettingModel)model;
				this.settingsModel = vmModel;
				
				if (vmModel.isBackupToRps()) {
					enableEmailOnJobQueue.setVisible(false);
					enableEmailOnHostNotFound.setVisible(false);
					enableSpaceNotification.setVisible(false);
					spaceMeasureNum.setVisible(false);
					spaceMeasureUnit.setVisible(false);
				}
				
				if(vmModel.getEnableEmailOnHostNotFound() !=null){
					this.enableEmailOnHostNotFound.setValue(vmModel.getEnableEmailOnHostNotFound());
				}else{
					this.enableEmailOnHostNotFound.setValue(false);
				}
				
				if(vmModel.getEnableEmailOnDataStoreNotEnough()!=null){
					this.enableEmailOnDataStoreNotEnough.setValue(vmModel.getEnableEmailOnDataStoreNotEnough());
				}else{
					this.enableEmailOnDataStoreNotEnough.setValue(false);
				}
				
				if(vmModel.getEnableEmailOnLicensefailure()!=null){
					this.enableEmailOnLicensefailure.setValue(vmModel.getEnableEmailOnLicensefailure());
				}else{
					this.enableEmailOnLicensefailure.setValue(false);
				}
				
				if(vmModel.getEnableEmailOnJobQueue()!=null){
					this.enableEmailOnJobQueue.setValue(vmModel.getEnableEmailOnJobQueue());
				}else{
					this.enableEmailOnJobQueue.setValue(false);
				}
				
				EmailUtils.mergeEmailSettings(this.settingsModel, edgeEmailSettingsModel);
				settingsDlg = new PreferencesEmailSettingsWindow();
				settingsDlg.setSettings(this.settingsModel);
				settingsDlg.setAppType(AppType.VSPHERE);
			}
			
			if(model.getEnableSpaceNotification() != null)
			{
				enableSpaceNotification.setValue(model.getEnableSpaceNotification());
				if(model.getEnableSpaceNotification().booleanValue() == true)
				{
					spaceMeasureNum.setValue(model.getSpaceMeasureNum());
					spaceMeasureUnit.setSimpleValue(model.getSpaceMeasureUnit());
//					oldValue = model.getSpaceMeasureUnit();
				}
			}
			else
			{
				enableSpaceNotification.setValue(false);
			}
		}
		
		setButtonStatus();
		
		this.repaint();
		
	}
	
	public void makeAllElementReadOnly(){
		disable();
	}

	@Override
	public Widget getWidget() {
		return this;
	}

	@Override
	public void initialize(ISettingsContentHost contentHost, boolean isForEdge) {
		this.contentHost = contentHost;
		this.isForEdge = isForEdge;
		
		this.doInitialization();
		
		/*if (!this.isForEdge)
			disable();*/
	}

	@Override
	public boolean isForEdge() {
		return isForEdge;
	}

	@Override
	public void loadData() {
		onLoadingCompleted();
	}

	private void onLoadingCompleted() {
		//this.contentHost
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.LoadData,
				ISettingsContentHost.OperationResults.Succeeded,
				this.settingsContentId );
	}

	@Override
	public void loadDefaultData() {
		onLoadingCompleted();
	}

	@Override
	public void saveData() {
		//this.contentHost
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.SaveData,
				ISettingsContentHost.OperationResults.Succeeded,
				this.settingsContentId );
	}

	@Override
	public void setId(int settingsContentId) {
		this.settingsContentId = settingsContentId;		
	}

	@Override
	public void setIsForEdge(boolean isForEdge) {
		this.isForEdge = isForEdge;		
	}

	@Override
	public void setDefaultEmail(IEmailConfigModel emailConfigModel)
	{

		if(emailConfigModel!=null){
			edgeEmailSettingsModel = emailConfigModel;
		}
		
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

	@Override
	public void validate() {
		boolean validate = true;
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
				
				validate = false;
			}
			
			if (validate && MeasureUnitPercent.equals(this.spaceMeasureUnit.getValue().getValue())
					&&  spaceMeasureNum.getValue().longValue()>100){
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
				msg.setMessage(UIContext.Constants.destinationThresholdAlertPercent());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				validate = false;
			}
		}
		
		
		if (enableEmail.getValue() || enableEmailOnSuccess.getValue()
				|| enableSpaceNotification.getValue() || enableEmailOnMissedJob.getValue()
				|| enableEmailOnHostNotFound.getValue() || enableEmailOnDataStoreNotEnough.getValue()
				|| enableEmailOnLicensefailure.getValue() ||  enableEmailOnMergeFailure.getValue()
				|| enableEmailOnJobQueue.getValue() || enableEmailOnMergeSuccess.getValue()
				|| enableEmailOnCheckRPSFailure.getValue() )
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
				
				validate = false;
			}
		}
		
		onValidatingCompleted(validate);
	}
	
	private void onValidatingCompleted(boolean isSuccessful)
	{
		//this.contentHost
		SettingPresenter.getInstance().onAsyncOperationCompleted(
			ISettingsContentHost.Operations.Validate,
			isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
				ISettingsContentHost.OperationResults.Failed,
			this.settingsContentId );
	}

	public void setEditable(boolean isEditable){
		enableEmailOnMissedJob.setEnabled(isEditable);
		enableEmail.setEnabled(isEditable);// Failed or Crash
		enableEmailOnSuccess.setEnabled(isEditable);
		enableSpaceNotification.setEnabled(isEditable);
		enableEmailOnHostNotFound.setEnabled(isEditable);
		enableEmailOnDataStoreNotEnough.setEnabled(isEditable);
		enableEmailOnLicensefailure.setEnabled(isEditable);
		//settingsButton.setEnabled(isEditable);
		spaceMeasureNum.setEnabled(isEditable);
		spaceMeasureUnit.setEnabled(isEditable);
		enableEmailOnMergeFailure.setEnabled(isEditable);
		enableEmailOnMergeSuccess.setEnabled(isEditable);
		enableEmailOnJobQueue.setEnabled(isEditable);
		enableEmailOnCheckRPSFailure.setEnabled(isEditable);
	}

	@Override
	public boolean isForLiteIT() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setisForLiteIT(boolean isForLiteIT) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<SettingsTab> getTabList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void switchTab(String tabId) {
		// TODO Auto-generated method stub
		
	}
}

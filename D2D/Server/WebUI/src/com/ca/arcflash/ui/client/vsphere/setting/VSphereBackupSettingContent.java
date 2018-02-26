package com.ca.arcflash.ui.client.vsphere.setting;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.BackupSettingsContent;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.SettingsGroupType;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.VSphereCommonSettingTab;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.exception.ClientException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.VMBackupSettingModel;
import com.ca.arcflash.ui.client.model.VMStatusModel;
import com.ca.arcflash.ui.client.model.VSphereBackupSettingModel;
import com.ca.arcflash.ui.client.service.Broker;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;

public class VSphereBackupSettingContent extends BackupSettingsContent{
	
	public static EmailAlertSettings emailAlert;
//	LayoutContainer emailAlertContainer;
	
//	private ToggleButton emailAlertButton;
//	private ToggleButton emailAlertLabel;
//	private ClickHandler emailAlertButtonHandler;
	private static int buttonSelected;
	
	public VSphereBackupSettingContent() {
	}
	
	public VSphereBackupSettingContent(SettingsGroupType settingsGroupType){
		this.settingsGroupType = settingsGroupType;
	}
	
	@Override
	protected void createDestinationSettings() {
		setDestination(new DestinationSettings(this));
		destinationContainer = new LayoutContainer();
		destinationContainer.add(getDestination().Render());
	}
	
	@Override
	protected void addEmailAlertToDeckPanel() {
		super.addEmailAlertToDeckPanel();
//		emailAlert = new EmailAlertSettings(this);
//		emailAlertContainer = new LayoutContainer();
//		emailAlertContainer.add(emailAlert.Render());
//		emailAlertContainer.setStyleAttribute("padding", "10px");
//		deckPanel.add(emailAlertContainer);

	}
	
	@Override
	protected void addEmailAlertButtonPanel() {
		super.addEmailAlertButtonPanel();
//		emailAlertButtonHandler = new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				deckPanel.showWidget(STACK_EMAILALERT);
//				destinationButton.setDown(false);
//				advancedButton.setDown(false);
//				settingsButton.setDown(false);
//				scheduleButton.setDown(false);
//				emailAlertButton.setDown(true);
//				
//				destinationLabel.setDown(false);
//				advancedLabel.setDown(false);
//				settingsLabel.setDown(false);
//				scheduleLabel.setDown(false);
//				emailAlertLabel.setDown(true);
//				contentHost.setCaption(UIContext.Messages.backupSettingsWindowWithTap(UIContext.Constants.vSphereSettingEmailAlert()));
//			}
//			
//		};
//		
//		emailAlertButton = new ToggleButton(UIContext.IconBundle.backupAdvanced()
//				.createImage());
//		emailAlertButton.setStylePrimaryName("demo-ToggleButton");
//		emailAlertButton.addClickHandler(emailAlertButtonHandler);
//		
//		toggleButtonPanel.add(emailAlertButton);
//		emailAlertLabel = new ToggleButton(UIContext.Constants.vSphereSettingEmailAlert());
//		emailAlertLabel.setStylePrimaryName("tb-settings");
//		emailAlertLabel.addClickHandler(emailAlertButtonHandler);
//		toggleButtonPanel.add(emailAlertLabel);
	}

	@Override
	public void RefreshData()
	{
		super.RefreshData();
		if(emailAlert != null)
			emailAlert.RefreshData(SettingPresenter.model);
	}
	
	@Override
	protected void checkBLIOnLoad() {
		UIContext.hasBLILic = true;
		UIContext.maxRPLimit = UIContext.maxRPLimitDEFAULT;
	}
	
	@Override
	public void loadData() {
		LoadSettings();
		schedule.setEditable(false);
	}

	@Override
	protected boolean Save() {
		contentHost.increaseBusyCount(UIContext.Constants.settingsMaskText());
		if (isForEdge()) {
			return this.SaveForEdge();
		}
		
		saveBackupConfiguration();		
		
		return true;
	}
	
	protected boolean SaveForEdge() {
		if (!this.getDestination().checkShareFolder())
		{
			deckPanel.showWidget(STACK_DESTINATION);
			onSavingCompleted(false);
			contentHost.decreaseBusyCount();
			return false;
		}
		
		if(this.getDestination().backupToRPS()){
			saveComplete();	
		}else {
			this.getDestination().validateRemotePath(new BaseAsyncCallback<Boolean>(false) {
				
				@Override
				public void onFailure(Throwable caught) {
					super.onFailure(caught);
					deckPanel.showWidget(STACK_DESTINATION);
					onSavingCompleted(false);
					contentHost.decreaseBusyCount();
				}
				
				@Override
				public void onSuccess(Boolean result) {
					if (!result) {
						deckPanel.showWidget(STACK_DESTINATION);
						onSavingCompleted(false);
						contentHost.decreaseBusyCount();
						return;
					}
					saveComplete();				
				}
				
			});
		}
		
		return true;
	}

	private void saveComplete(){
		if(contentHost instanceof VSphereCommonSettingTab) {
			VSphereCommonSettingTab tab = (VSphereCommonSettingTab)contentHost;
			tab.getD2dSettings().setBackupSettingsModel(SettingPresenter.model);
		}
		
		saveBackupConfiguration();	
	}

	@Override
	protected boolean validateEmailAlert() {
		return super.validateEmailAlert();
//		if(emailAlert.Validate()){
//			this.emailAlert.Save();
//			return true;
//		}else{
//			deckPanel.showWidget(STACK_EMAILALERT);
//			this.onSavingCompleted( false );
//			return false;
//		}
	}
	
	@Override
	protected void downEmailAlertButtonAndLabel() {
		super.downEmailAlertButtonAndLabel();
//		emailAlertButton.setDown(false);
//		emailAlertLabel.setDown(false);
	}
	
	
	@Override
	public void enableEditing(boolean isEnabled) {
		super.enableEditing(isEnabled);
//		if(!isEnabled)
//			emailAlert.makeAllElementReadOnly();
	}

	protected boolean isShowForVSphere() {
		return true;
	}
	
	@Override
	protected void fetchDataFromServer(final 
			BaseAsyncCallback<BackupSettingsModel> callback) {
		BaseAsyncCallback<VMBackupSettingModel> detailsCallback = new BaseAsyncCallback<VMBackupSettingModel>(){
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
			
			@Override
			public void onSuccess(VMBackupSettingModel result) {
				callback.onSuccess(convertToVSphereBackupSettingModel(result));
			}
		};
		
		Broker.loginService.getVMBackupConfiguration(UIContext.backupVM, detailsCallback);
	}

	@Override
	protected void checkVolumeAndSavingSettings() {
		validateProxy();
	}
	
	private void validateProxy() {
		contentHost.increaseBusyCount(UIContext.Constants.settingsMaskText());
		final VSphereBackupSettingModel vsphereModel = (VSphereBackupSettingModel)SettingPresenter.model;
		try{
			Broker.loginService.validateProxyInfo(vsphereModel,
						new BaseAsyncCallback<Integer>() {
							@Override
							public void onSuccess(Integer result) {
								contentHost.decreaseBusyCount();
								if(result == VMStatusModel.VM_STATUS_WARNING_VIX_STATUS_OK){
									onValidatingCompleted(true);
								}else{
									if (!isShowVIXNotInstallMessage(vsphereModel)) {
										onValidatingCompleted(true);
										return;
									}
									String warningMsg = "";
									if(result == VMStatusModel.VM_STATUS_WARNING_VIX_STATUS_NOT_INSTALL){
										warningMsg = UIContext.Messages.vSphereVixNotInstallWarning(vsphereModel.vSphereProxyModel.getVSphereProxyName());
									}else if (result == VMStatusModel.VM_STATUS_WARNING_VIX_STATUS_OUT_OF_DATE){
										warningMsg = UIContext.Messages.vSphereVixOutOfDateWarning(vsphereModel.vSphereProxyModel.getVSphereProxyName());
									}
									MessageBox mb = new MessageBox();
									mb.setMinWidth(400);
									mb.setIcon("ext-mb-warning-for-vsphere");
									mb.setButtons(MessageBox.YESNO);
									mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNamevSphere));
									mb.setMessage(UIContext.Messages.vSphereTestProxyWarningConfirmMsg(warningMsg));
									Utils.setMessageBoxDebugId(mb);
									mb.addCallback(new Listener<MessageBoxEvent>()
									{
										public void handleEvent(MessageBoxEvent be)
										{
											if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
												onValidatingCompleted(true);
											}
											else {
												onValidatingCompleted(false);
											}
												
										}
									});
									mb.show();
								}
									
							}

							@Override
							public void onFailure(Throwable caught) {
									contentHost.decreaseBusyCount();
									String errorMessage = null;
									if (caught instanceof ServiceConnectException){
										String errorCode = ((ServiceConnectException)caught).getErrorCode();
										
										//can't use new policy 
										if(errorCode.equals("42949672987")){
											errorMessage = UIContext.Messages.vSphereTestProxyLowerVersion(vsphereModel.vSphereProxyModel.getVSphereProxyName());
											String msg = UIContext.Messages.vSphereTestProxyWarningConfirmMsg(errorMessage);
											MessageBox mb = new MessageBox();
											mb.setIcon(MessageBox.WARNING);
											mb.setButtons(MessageBox.YESNO);
											mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNamevSphere));
											mb.setMessage(msg);
											mb.addCallback(new Listener<MessageBoxEvent>()
											{
												public void handleEvent(MessageBoxEvent be)
												{
													if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
														onValidatingCompleted(true);
													}
													else {
														onValidatingCompleted(false);
													}
														
												}
											});
											Utils.setMessageBoxDebugId(mb);
											mb.show();
										}
										
										//Host not found
										if(errorCode.equals("4294967304")){
											errorMessage = UIContext.Messages.vSphereTestProxyHostNotFoundError(vsphereModel.vSphereProxyModel.getVSphereProxyName(),vsphereModel.vSphereProxyModel.getVSphereProxyName());
											MessageBox mb = new MessageBox();
											mb.setIcon(MessageBox.ERROR);
											mb.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
											mb.setMessage(errorMessage);
											Utils.setMessageBoxDebugId(mb);
											mb.show();
											onValidatingCompleted(false);
										}
										//Not D2d installed
										if(errorCode.equals("4294967297")){
											errorMessage = UIContext.Messages.vSphereTestProxyWarning(UIContext.productNameD2D,vsphereModel.vSphereProxyModel.getVSphereProxyName());
											String msg = UIContext.Messages.vSphereTestProxyWarningConfirmMsg(errorMessage);
											MessageBox mb = new MessageBox();
											mb.setIcon(MessageBox.WARNING);
											mb.setButtons(MessageBox.YESNO);
											mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNamevSphere));
											mb.setMessage(msg);
											mb.addCallback(new Listener<MessageBoxEvent>()
											{
												public void handleEvent(MessageBoxEvent be)
												{
													if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
														onValidatingCompleted(true);
													}
													else {
														onValidatingCompleted(false);
													}
														
												}
											});
											Utils.setMessageBoxDebugId(mb);
											mb.show();
										}
										
									}else{
										errorMessage = ((ClientException) caught).getDisplayMessage();
										MessageBox mb = new MessageBox();
										mb.setIcon(MessageBox.ERROR);
										mb.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
										mb.setMessage(errorMessage);
										Utils.setMessageBoxDebugId(mb);
										mb.show();
										onValidatingCompleted(false);
									}
								
							}
						});
			} catch (Exception ce) {
				ce.printStackTrace();
			}
		
	}
	
	@Override
	protected void saveBackupConfiguration(){
		if(emailAlert != null)
			emailAlert.save(SettingPresenter.model);
		
		Broker.loginService.saveVShpereBackupSetting((VSphereBackupSettingModel)SettingPresenter.model,
				new BaseAsyncCallback<Long>() {
					@Override
					public void onSuccess(Long result) {
						onSavingCompleted(true);
						contentHost.decreaseBusyCount();
					}

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						onSavingCompleted(false);
						contentHost.decreaseBusyCount();
					}
				});
	}
	
	protected BackupSettingsModel createNewSettingModel() {
		return new VSphereBackupSettingModel();
	}
	
	private VSphereBackupSettingModel convertToVSphereBackupSettingModel(VMBackupSettingModel vmConfig){
		if(vmConfig == null ){
			return null;
		}
		VSphereBackupSettingModel vsphereConfig = new VSphereBackupSettingModel();
		vsphereConfig.incrementalSchedule = vmConfig.incrementalSchedule;
		vsphereConfig.fullSchedule = vmConfig.fullSchedule;
		vsphereConfig.resyncSchedule = vmConfig.resyncSchedule;
		vsphereConfig.vSphereProxyModel = vmConfig.vSphereProxyModel;
		vsphereConfig.retentionPolicy = vmConfig.retentionPolicy;
		
		vsphereConfig.setActionsUserName(vmConfig.getActionsUserName());
		vsphereConfig.setActionsPassword(vmConfig.getActionsPassword());
		vsphereConfig.setAdminUserName(vmConfig.getAdminUserName());
		vsphereConfig.setAdminPassword(vmConfig.getAdminPassword());
		vsphereConfig.setBackupStartTime(vmConfig.getBackupStartTime());
		vsphereConfig.setStartTimezoneOffset(vmConfig.getStartTimezoneOffset());
		vsphereConfig.startTime = vmConfig.startTime;
		vsphereConfig.setChangedBackupDest(vmConfig.getChangedBackupDest());
		vsphereConfig.setChangedBackupDestType(vmConfig.getChangedBackupDestType());
		vsphereConfig.setCommandBeforeBackup(vmConfig.getCommandBeforeBackup());
		vsphereConfig.setCommandAfterBackup(vmConfig.getCommandAfterBackup());
		vsphereConfig.setRunCommandEvenFailed(vmConfig.getRunCommandEvenFailed());
		vsphereConfig.setCommandAfterSnapshot(vmConfig.getCommandAfterSnapshot());
		vsphereConfig.setCompressionLevel(vmConfig.getCompressionLevel());
		vsphereConfig.setContent(vmConfig.getContent());
		
		
		vsphereConfig.setDestination(vmConfig.getDestination());
		vsphereConfig.setDestUserName(vmConfig.getDestUserName());
		vsphereConfig.setDestPassword(vmConfig.getDestPassword());
		
		vsphereConfig.setEnableEmail(vmConfig.getEnableEmail());
		vsphereConfig.setEnableEmailOnDataStoreNotEnough(vmConfig.getEnableEmailOnDataStoreNotEnough());
		vsphereConfig.setEnableEmailOnHostNotFound(vmConfig.getEnableEmailOnHostNotFound());
		vsphereConfig.setEnableEmailOnLicensefailure(vmConfig.getEnableEmailOnLicensefailure());
		vsphereConfig.setEnableEmailOnMissedJob(vmConfig.getEnableEmailOnMissedJob());
		vsphereConfig.setEnableEmailOnSuccess(vmConfig.getEnableEmailOnSuccess());
		vsphereConfig.setEnableEmailOnMergeFailure(vmConfig.getEnableEmailOnMergeFailure());
		vsphereConfig.setEnableEmailOnMergeSuccess(vmConfig.getEnableEmailOnMergeSuccess());
		vsphereConfig.setEnableEmailOnRecoveryPointCheckFailure(vmConfig.getEnableEmailOnRecoveryPointCheckFailure());
		vsphereConfig.setEnableEmailOnJobQueue(vmConfig.getEnableEmailOnJobQueue());
		
		vsphereConfig.setEnableEncryption(vmConfig.getEnableEncryption());
		vsphereConfig.setEnablePreExitCode(vmConfig.getEnablePreExitCode());
		vsphereConfig.setEnableSpaceNotification(vmConfig.getEnableSpaceNotification());
		vsphereConfig.setEnableHTMLFormat(vmConfig.getEnableHTMLFormat());
		vsphereConfig.setEncryptionAlgorithm(vmConfig.getEncryptionAlgorithm());
		vsphereConfig.setEncryptionKey(vmConfig.getEncryptionKey());
		
		vsphereConfig.setFromAddress(vmConfig.getFromAddress());
		vsphereConfig.setGrowthRate(vmConfig.getGrowthRate());
		vsphereConfig.setRetentionCount(vmConfig.getRetentionCount());
		
		
		vsphereConfig.setThrottling(vmConfig.getThrottling());
		
		
		vsphereConfig.setPreExitCode(vmConfig.getPreExitCode());
		vsphereConfig.setSkipJob(vmConfig.getSkipJob());
		
		
		
		vsphereConfig.setPurgeSQLLogDays(vmConfig.getPurgeSQLLogDays());
		vsphereConfig.setPurgeExchangeLogDays(vmConfig.getPurgeExchangeLogDays());
		
		vsphereConfig.setSpaceMeasureNum(vmConfig.getSpaceMeasureNum());
		vsphereConfig.setSpaceMeasureUnit(vmConfig.getSpaceMeasureUnit());
		
		vsphereConfig.setMailPwd(vmConfig.getMailPwd());
		vsphereConfig.setMailService(vmConfig.getMailService());
		vsphereConfig.setMailUser(vmConfig.getMailUser());
		vsphereConfig.setProxyAddress(vmConfig.getProxyAddress());
		vsphereConfig.setProxyPassword(vmConfig.getProxyPassword());
		vsphereConfig.setProxyUsername(vmConfig.getProxyUsername());
		vsphereConfig.setProxyPort(vmConfig.getProxyPort());
		vsphereConfig.setRecipients(vmConfig.getRecipients());
		vsphereConfig.setSMTP(vmConfig.getSMTP());
		vsphereConfig.setSmtpPort(vmConfig.getSmtpPort());
		vsphereConfig.setSubject(vmConfig.getSubject());
		vsphereConfig.setGenerateCatalog(vmConfig.getGenerateCatalog());
		vsphereConfig.setExchangeGRTSetting(vmConfig.getExchangeGRTSetting());
		
		// defect 74174
		vsphereConfig.setEnableProxy(vmConfig.isEnableProxy());
		vsphereConfig.setEnableProxyAuth(vmConfig.isEnableProxyAuth());
		vsphereConfig.setEnableMailAuth(vmConfig.isEnableMailAuth());
		
		vsphereConfig.setSpaceSavedAfterCompression(vmConfig.getSpaceSavedAfterCompression());
		vsphereConfig.advanceScheduleModel = vmConfig.advanceScheduleModel;
		//fanda03 defect 102889
		vsphereConfig.setPreAllocationValue(vmConfig.getPreAllocationValue());
		vsphereConfig.setBackupToRps(vmConfig.isBackupToRps());
		vsphereConfig.rpsDestSettings = vmConfig.rpsDestSettings;
		vsphereConfig.scheduledExportSettingsModel = vmConfig.scheduledExportSettingsModel;
		
		vsphereConfig.setBackupDataFormat(vmConfig.getBackupDataFormat()==null?1:vmConfig.getBackupDataFormat().intValue());
		vsphereConfig.setVmwareTransportModes(vmConfig.getVmwareTransportModes());
		
		vsphereConfig.setVmwareQuiescenceMethod(vmConfig.getVmwareQuiescenceMethod());
		vsphereConfig.setHyperVConsistentSnapshotType(vmConfig.getHyperVConsistentSnapshotType());
		vsphereConfig.setHyperVCrashConsistentSnapshotWithSavedVMState(vmConfig.getHyperVCrashConsistentSnapshotWithSavedVMState());
		vsphereConfig.setHyperVSnapshotSeparationIndividually(vmConfig.getHyperVSnapshotSeparationIndividually());
		vsphereConfig.setCheckRecoveryPoint(vmConfig.getCheckRecoveryPoint());
		
		//bug 744631 
		vsphereConfig.setEnableSsl(vmConfig.isEnableSsl());
		vsphereConfig.setEnableTls(vmConfig.isEnableTls());
		return vsphereConfig;
	}
	
	protected boolean isShowVIXNotInstallMessage(VSphereBackupSettingModel config) {
		String pre = config.getCommandBeforeBackup();
		String post = config.getCommandAfterBackup();
		String postSnap = config.getCommandAfterSnapshot();
		if (config.getPurgeSQLLogDays() != 0 || config.getPurgeExchangeLogDays() != 0 ||
		!isEmptyOrNull(pre) || !isEmptyOrNull(post) || !isEmptyOrNull(postSnap))
			return true;

		return false;
	}

	private boolean isEmptyOrNull(String target) {
		if (target == null || target.equals("") || target.trim().equals(""))
			return true;
		return false;
	}

	public static int getButtonSelected() {
		return buttonSelected;
	}

	public static void setButtonSelected(int buttonSelected) {
		VSphereBackupSettingContent.buttonSelected = buttonSelected;
	}
}

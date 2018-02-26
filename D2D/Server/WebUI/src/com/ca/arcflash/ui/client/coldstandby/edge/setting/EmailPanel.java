package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.jobscript.alert.AlertJobScript;
import com.ca.arcflash.jobscript.alert.EmailModel;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.DisclourePanelImageBundles;
import com.ca.arcflash.ui.client.coldstandby.VCMMessages;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.EmailUtils;
import com.ca.arcflash.ui.client.common.ISettingsContent;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.homepage.PreferencesEmailSettingsWindow;
import com.ca.arcflash.ui.client.model.EmailConfigModel;
import com.ca.arcflash.ui.client.model.IEmailConfigModel;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class EmailPanel extends LayoutContainer implements ISettingsContent{
	CheckBox missHeatbeatCheckBox;
	CheckBox autoFaioverCheckBox;
	CheckBox manualFailOverCheckBox;
	CheckBox replicationSpaceCheckBox;
	NumberField replicationSpaceNumberField;
	BaseSimpleComboBox<String> replicationSpaceComboBox;
	CheckBox replicationErrorCheckBox;
//	CheckBox LicenseCheckBox;
	CheckBox notReachableCheckBox;
	CheckBox conversionSuccess;
	CheckBox failoverFailure;
	
	Button emailSettingButton;
	PreferencesEmailSettingsWindow emailSettingsWindow;
	IEmailConfigModel emailConfigModel;
	IEmailConfigModel edgeEmailConfigModel;
	
	ISettingsContentHost contentHost;
	
	boolean isForRemoteVCM;
	
	public EmailPanel( boolean isForRemoteVCM )
	{
		this.isForRemoteVCM = isForRemoteVCM;
	}
	
	private void doInitialization() {
		this.ensureDebugId("7a180960-f47b-4310-8da7-48ba33c27f12");
		
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("100%");
		tl.setCellPadding(2);
		container.ensureDebugId("ebf04923-10ac-472d-964f-10935b745014");
		container.setLayout(tl);
		container.setScrollMode(Scroll.AUTO);
		
		//title and description
		Label titleLabel = new Label(UIContext.Constants.coldStandbySettingEmailAlertTitle());
		titleLabel.ensureDebugId("567ba42d-bec3-430a-866d-71726d4e70b8");
		titleLabel.setStyleName("coldStandbySettingTitle");
		container.add(titleLabel);

		Label descriptionLabel = new Label(UIContext.Constants.coldStandbySettingEmailAlertDescription());
		descriptionLabel.ensureDebugId("9ef8a106-1183-411e-bcc0-1447d8f5b3ce");
		descriptionLabel.setStyleName("coldStandbySettingDescription");
		container.add(descriptionLabel);
		
		container.add(getEmailLayout());

		this.add(container);
		
		WizardContext.getWizardContext().setEmailPanel(this);
		
		setButtonStatus();
		
		//setEnabled(this.isForEdge);
	}
	
	@SuppressWarnings("deprecation")
	private Widget getEmailLayout() {
		DisclosurePanel emailDisPanel = new DisclosurePanel(
				(DisclourePanelImageBundles) GWT.create(DisclourePanelImageBundles.class),
				UIContext.Constants.coldStandbySettingEmailAlertDeckPanel(), false);
		emailDisPanel.ensureDebugId("8513578d-d1d8-49a3-b6a9-96440effde46");
		emailDisPanel.setWidth("100%");
		emailDisPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		emailDisPanel.setOpen(true);

		LayoutContainer emailContainer = new LayoutContainer();
		TableLayout emailTableLayout = new TableLayout();
		emailTableLayout.setColumns(1);
		emailTableLayout.setWidth("100%");
		emailTableLayout.setCellPadding(4);
		emailContainer.ensureDebugId("69123126-a433-4865-8ec4-8503f7b81c1d");
		emailContainer.setLayout(emailTableLayout);
		
		missHeatbeatCheckBox = new CheckBox();
		missHeatbeatCheckBox.setBoxLabel(UIContext.Constants.coldStandbySettingEmailAlertMissHeatbeat());
		missHeatbeatCheckBox.setStyleName("setting-text-label");
		missHeatbeatCheckBox.ensureDebugId("08705d2b-c2dd-4202-8a52-3bf3d2020b6c");
		missHeatbeatCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				setButtonStatus();
			}
		});
		if (!this.isForRemoteVCM)
			emailContainer.add(missHeatbeatCheckBox);
		
		autoFaioverCheckBox = new CheckBox();
		autoFaioverCheckBox.setBoxLabel(UIContext.Constants.coldStandbySettingEmailAlertAutoFailover());
		autoFaioverCheckBox.setStyleName("setting-text-label");
		autoFaioverCheckBox.ensureDebugId("ffd3d0cb-7817-4f5f-bfd7-694a671ff6b6");
		autoFaioverCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				setButtonStatus();
			}
		});
		if (!this.isForRemoteVCM)
			emailContainer.add(autoFaioverCheckBox);
		
		manualFailOverCheckBox = new CheckBox();
		manualFailOverCheckBox.setBoxLabel(UIContext.Constants.coldStandbySettingEmailAlertManualFailover());
		manualFailOverCheckBox.setStyleName("setting-text-label");
		manualFailOverCheckBox.ensureDebugId("f8b8fce2-0a82-43f6-8b3b-a9c469cca92a");
		manualFailOverCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				setButtonStatus();
			}
		});
		if (!this.isForRemoteVCM)
			emailContainer.add(manualFailOverCheckBox);
		
		LayoutContainer spaceContainer = new LayoutContainer();
		TableLayout spaceTableLayout = new TableLayout();
		spaceTableLayout.setColumns(4);
		spaceContainer.setLayout(spaceTableLayout);
		
		replicationSpaceCheckBox = new CheckBox();
		replicationSpaceCheckBox.setBoxLabel(UIContext.Constants.coldStandbySettingEmailAlertReplicationSpace());
		replicationSpaceCheckBox.setStyleName("setting-text-label");
		replicationSpaceCheckBox.ensureDebugId("b4317a30-abd0-4f55-b145-731f72e8ba0b");
		replicationSpaceCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent fe) {
				
				if(replicationSpaceCheckBox.getValue()) {
					replicationSpaceNumberField.enable();
					replicationSpaceComboBox.enable();
				}
				else {
					replicationSpaceNumberField.disable();
					replicationSpaceComboBox.disable();
				}
				setButtonStatus();
			}
		});
		spaceContainer.add(replicationSpaceCheckBox);
		
		replicationSpaceNumberField = new NumberField();
		replicationSpaceNumberField.setValue(5);
		replicationSpaceNumberField.setWidth(100);
		replicationSpaceNumberField.ensureDebugId("d088c146-704d-483c-88f7-f9b0adf839fa");
		//replicationSpaceNumberField.setStyleName("setting-text-label");
		replicationSpaceNumberField.setAllowBlank(false);
		replicationSpaceNumberField.setAllowDecimals(true);
		replicationSpaceNumberField.setAllowNegative(false);
		replicationSpaceNumberField.setAllowBlank(false);
		replicationSpaceNumberField.setMaxLength(10);
		//replicationSpaceNumberField.setRegex("[1-9][0-9]*");
		replicationSpaceNumberField.disable();
		spaceContainer.add(replicationSpaceNumberField);
		replicationSpaceNumberField.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				boolean vali = replicationSpaceNumberField.validate();
				if(!vali) {
					placeholder.setVisible(true);
				}
				else {
					placeholder.setVisible(false);
				}
			}
		});
		
		placeholder = new Label();
		placeholder.setWidth("20px");
		placeholder.setVisible(false);
		spaceContainer.add(placeholder);
				
		replicationSpaceComboBox = new BaseSimpleComboBox<String>();
		replicationSpaceComboBox.ensureDebugId("2d339aed-cec8-4abe-a553-81acbbfbf872");
		replicationSpaceComboBox.setEditable(false);
		replicationSpaceComboBox.add("%");
		replicationSpaceComboBox.add("MB");
		replicationSpaceComboBox.setSimpleValue("%");
		replicationSpaceNumberField.setMaxValue(100);
		replicationSpaceComboBox.disable();
		//replicationSpaceComboBox.setStyleName("setting-text-label");
		replicationSpaceComboBox.setWidth(60);
		replicationSpaceComboBox.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
			
			@Override
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<String>> se) {
				// TODO Auto-generated method stub
				SimpleComboValue<String> selectedItem = se.getSelectedItem();
				if(selectedItem.getValue().equals("%")){
					replicationSpaceNumberField.setValue(5);
					replicationSpaceNumberField.setMaxValue(100);
					replicationSpaceNumberField.validate();
					placeholder.setVisible(false);
				}
				else{
					replicationSpaceNumberField.setValue(200);
					replicationSpaceNumberField.setMaxValue(9999999999l);
					replicationSpaceNumberField.validate();
					placeholder.setVisible(false);
				}
			}
		});
		
		TableData spaceTD = new TableData();
		spaceTD.setPadding(5);
		spaceContainer.add(replicationSpaceComboBox, spaceTD);
		
		emailContainer.add(spaceContainer);
		
		replicationErrorCheckBox = new CheckBox();
		replicationErrorCheckBox.setBoxLabel(VCMMessages.coldStandbySettingEmailAlertReplicationError());
		replicationErrorCheckBox.setStyleName("setting-text-label");
		replicationErrorCheckBox.ensureDebugId("bf350c3f-19b0-4643-ac00-c744202a3b23");
		replicationErrorCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				setButtonStatus();
			}
		});
		emailContainer.add(replicationErrorCheckBox);
		
		conversionSuccess = new CheckBox();
		conversionSuccess.setBoxLabel(VCMMessages.coldStandbySettingEmailAlertConversionSuccess());
		conversionSuccess.setStyleName("setting-text-label");
		conversionSuccess.ensureDebugId("d10250f0-6ca0-42b7-adc2-e35ebef95858");
		conversionSuccess.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				setButtonStatus();
			}
		});
		emailContainer.add(conversionSuccess);
		
		notReachableCheckBox = new CheckBox();
		notReachableCheckBox.setBoxLabel(UIContext.Constants.coldStandbySettingEmailAlertNotReachable());
		notReachableCheckBox.setStyleName("setting-text-label");
		notReachableCheckBox.ensureDebugId("cea43e66-0c89-439c-9d37-bdc83856e13d");
		notReachableCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				setButtonStatus();
			}
		});
		emailContainer.add(notReachableCheckBox);
		
//		LicenseCheckBox = new CheckBox();
//		LicenseCheckBox.setBoxLabel(UIContext.Constants.coldStandbySettingEmailAlertLicense());
//		LicenseCheckBox.setStyleName("setting-text-label");
//		LicenseCheckBox.ensureDebugId("6625e285-f241-40e3-a66e-8f5befa58401");
//		LicenseCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {
//			@Override
//			public void handleEvent(FieldEvent be) {
//				setButtonStatus();
//			}
//		});
//		emailContainer.add(LicenseCheckBox);

		failoverFailure = new CheckBox();
		failoverFailure.setBoxLabel(UIContext.Constants.coldStandbySettingEmailAlertProvisionFailure());
		failoverFailure.setStyleName("setting-text-label");
		failoverFailure.ensureDebugId("8ab50ad7-61d8-4918-94d5-6889beb99968");
		failoverFailure.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				setButtonStatus();
			}
		});
		emailContainer.add(failoverFailure);

		
		
		
		
		emailConfigModel = new EmailConfigModel();
		emailSettingsWindow = new PreferencesEmailSettingsWindow();
		emailSettingsWindow.setSettings(emailConfigModel);
		emailSettingsWindow.setAppType(AppType.VCM);
		
		emailSettingButton = new Button(UIContext.Constants.settingsEmailSettings());
		emailSettingButton.ensureDebugId("83c10c09-c1ee-4dd9-ac85-bc207be951af");
		emailSettingButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				
				emailSettingsWindow.setSettings(emailConfigModel);
				emailSettingsWindow.setModal(true);
				emailSettingsWindow.show();
				if(!isForEdge){
					emailSettingsWindow.setEnabled(false);
				}
				
				emailSettingsWindow.addWindowListener( new WindowListener(){
					public void	windowHide(WindowEvent we)
					{
						//Click ok the button should not be null
						if (we.getButtonClicked() != null)
						{
							emailSettingsWindow.saveSettings(emailConfigModel);
						}
					}
				});
			}
		});
		emailContainer.add(emailSettingButton);
		
		emailDisPanel.add(emailContainer);
		return emailDisPanel;
	}

	private void setButtonStatus() {
		
		boolean configEmail = autoFaioverCheckBox.getValue() || manualFailOverCheckBox.getValue()
						|| replicationSpaceCheckBox.getValue() || replicationErrorCheckBox.getValue()
						/*|| LicenseCheckBox.getValue() */|| notReachableCheckBox.getValue() 
						|| missHeatbeatCheckBox.getValue() || conversionSuccess.getValue()
						|| failoverFailure.getValue();
		
		if(configEmail){
			emailSettingButton.enable();
		}else{
			emailSettingButton.disable();
		}

	}

	private boolean validateSettings() {
		boolean result = true;
		if(replicationSpaceCheckBox.getValue()) {
			if(replicationSpaceComboBox.getSimpleValue().equals("%")) {
				replicationSpaceNumberField.setMinValue(1);
				replicationSpaceNumberField.setMaxValue(100);
			}
			else{
				replicationSpaceNumberField.setMinValue(0);
			}
			result = replicationSpaceNumberField.validate() && replicationSpaceComboBox.validate();
			
			if(!result) {
				onValidatingCompleted(result);
				return result;
			}
		}
		if(emailSettingButton.isEnabled()) {
			int ret = emailSettingsWindow.validate();	
			if (ret != 0)
			{
				onValidatingCompleted(false);
				
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameVCM));
				msg.setMessage(UIContext.Constants.mustConfigEmailSettins());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
		}
		
		onValidatingCompleted(result);
		
		return result;
	}

	protected void populateAlertJobScript(AlertJobScript alertJobScript) {
		
		EmailModel emailModel = new EmailModel();
		emailModel.setContent("");
		
		if(emailSettingButton.isEnabled()) {
			
			boolean isEnableProxy = emailConfigModel.isEnableProxy();
			emailModel.setEnableProxy(isEnableProxy);
			if(isEnableProxy) {
				emailModel.setProxyAddress(emailConfigModel.getProxyAddress());
				emailModel.setProxyPort(emailConfigModel.getProxyPort());
				
				boolean isEnableProxyAuth = emailConfigModel.isEnableProxyAuth();
				emailModel.setProxyAuth(isEnableProxyAuth);
				if(isEnableProxyAuth){
					emailModel.setProxyPassword(emailConfigModel.getProxyPassword());
					emailModel.setProxyUsername(emailConfigModel.getProxyUsername());
				}

			}
		
			emailModel.setMailPassword(emailConfigModel.getMailPwd());
			emailModel.setUseSsl(emailConfigModel.isEnableSsl());
			emailModel.setSmptPort(emailConfigModel.getSmtpPort());
			emailModel.setMailUser(emailConfigModel.getMailUser());
			emailModel.setUseTls(emailConfigModel.isEnableTls());
			emailModel.setMailAuth(emailConfigModel.isEnableMailAuth());
			emailModel.setSMTP(emailConfigModel.getSMTP());
			emailModel.setSubject(emailConfigModel.getSubject());
			emailModel.setFromAddress(emailConfigModel.getFromAddress());
			emailModel.setHtmlFormat(emailConfigModel.getEnableHTMLFormat());
			String[] recipients = emailConfigModel.getRecipients().toArray(new String[emailConfigModel.getRecipients().size()]);
			emailModel.setRecipients(recipients);
			emailModel.setMailService(emailConfigModel.getMailService());
		}
		
	
		alertJobScript.setEmailModel(emailModel);
		alertJobScript.setMissHeatbeat(missHeatbeatCheckBox.getValue());
		alertJobScript.setAutoFaiover(autoFaioverCheckBox.getValue());
		alertJobScript.setManualFailover(manualFailOverCheckBox.getValue());
//		alertJobScript.setLicense(LicenseCheckBox.getValue());
		alertJobScript.setNotreachable(notReachableCheckBox.getValue());
		alertJobScript.setReplicationError(replicationErrorCheckBox.getValue());
		alertJobScript.setReplicationSpaceWarning(replicationSpaceCheckBox.getValue());
		alertJobScript.setSpaceMeasureNumber(replicationSpaceNumberField.getValue().floatValue());
		alertJobScript.setSpaceMeasureUnit(replicationSpaceComboBox.getSimpleValue());
		alertJobScript.setConversionSuccess(conversionSuccess.getValue());
		alertJobScript.setFailoverFailure(failoverFailure.getValue());
		
	}

	protected boolean isStringEmpty(String target) {
		if(target == null) {
			return true;
		}
		else {
			return target.isEmpty();
		}
	}
	
	protected void populateUI(AlertJobScript alertJobScript) {
		if(alertJobScript == null) {
			return;
		}
		
		EmailModel emailModel = alertJobScript.getEmailModel();
		if(emailModel == null) {
			emailModel = new EmailModel();
			emailModel.setEnableProxy(false);
			emailModel.setMailAuth(false);
			emailModel.setURL(null);
		}
		
			boolean isEnableProxy = emailModel.isEnableProxy();
			emailConfigModel.setEnableProxy(isEnableProxy);
			if(isEnableProxy) {
				emailConfigModel.setProxyAddress(emailModel.getProxyAddress());
				emailConfigModel.setProxyPort(emailModel.getProxyPort());
				boolean isEnableProxyAuth = emailModel.isProxyAuth();
				emailConfigModel.setEnableProxyAuth(isEnableProxyAuth);
				if(isEnableProxyAuth){
					emailConfigModel.setProxyPassword(emailModel.getProxyPassword());
					emailConfigModel.setProxyUsername(emailModel.getProxyUsername());
				}
			}
			emailConfigModel.setMailPwd(emailModel.getMailPassword());
			emailConfigModel.setEnableSsl(emailModel.isUseSsl());
			emailConfigModel.setSmtpPort(emailModel.getSmptPort());
			emailConfigModel.setMailUser(emailModel.getMailUser());
			emailConfigModel.setEnableTls(emailModel.isUseTls());
			emailConfigModel.setEnableMailAuth(emailModel.isMailAuth());
			emailConfigModel.setSMTP(emailModel.getSMTP());
			emailConfigModel.setSubject(emailModel.getSubject());
			emailConfigModel.setFromAddress(emailModel.getFromAddress());
			emailConfigModel.setEnableHTMLFormat(emailModel.isHtmlFormat());
			String[] recipients = emailModel.getRecipients();
			if(recipients != null) {
				ArrayList<String> tempRecipients = new ArrayList<String>();
				for (int i = 0; i < recipients.length; i++) {
					tempRecipients.add(recipients[i]);
				}
				emailConfigModel.setRecipients(tempRecipients);
			}
			
			emailConfigModel.setMailService(emailModel.getMailService());
			
			EmailUtils.mergeEmailSettings(emailConfigModel, edgeEmailConfigModel);
			emailSettingsWindow.setSettings(emailConfigModel);
		
		
		missHeatbeatCheckBox.setValue(alertJobScript.isMissHeatbeat());
		autoFaioverCheckBox.setValue(alertJobScript.isAutoFaiover());
		manualFailOverCheckBox.setValue(alertJobScript.isManualFailover());
//		LicenseCheckBox.setValue(alertJobScript.isLicense());
		notReachableCheckBox.setValue(alertJobScript.isNotreachable());
		replicationErrorCheckBox.setValue(alertJobScript.isReplicationError());
		replicationSpaceCheckBox.setValue(alertJobScript.isReplicationSpaceWarning());
		replicationSpaceNumberField.setValue(alertJobScript.getSpaceMeasureNumber());
		conversionSuccess.setValue(alertJobScript.isConversionSuccess());
		failoverFailure.setValue(alertJobScript.isFailoverFailure());
		String spaceMeasureUnit = alertJobScript.getSpaceMeasureUnit();
		replicationSpaceComboBox.setSimpleValue(spaceMeasureUnit);
		if("%".equals(spaceMeasureUnit))
			replicationSpaceNumberField.setMaxValue(100);
		else
			replicationSpaceNumberField.setMaxValue(9999999999l);
		
		setButtonStatus();
		
		if(!this.isForEdge) {
			missHeatbeatCheckBox.setEnabled(isForEdge);
			autoFaioverCheckBox.setEnabled(isForEdge);
			manualFailOverCheckBox.setEnabled(isForEdge);
//			LicenseCheckBox.setEnabled(isForEdge);
			notReachableCheckBox.setEnabled(isForEdge);
			replicationErrorCheckBox.setEnabled(isForEdge);
			replicationSpaceCheckBox.setEnabled(isForEdge);
			conversionSuccess.setEnabled(isForEdge);
			failoverFailure.setEnabled(isForEdge);
			replicationSpaceNumberField.setEnabled(false);
			replicationSpaceComboBox.setEnabled(false);
		}
	}
	
//	private boolean isEmptyOrNull(String target) {
//		if(target == null) {
//			return true;
//		}
//		else if(target.length()==0) {
//			return true;
//		}
//		else {
//			return false;
//		}
//	}
	
	public void setDefaultEmailSettings(IEmailConfigModel emailModel)
	{
		if(emailModel !=null)
		{
			edgeEmailConfigModel = emailModel;
			EmailUtils.mergeEmailSettings(emailConfigModel, edgeEmailConfigModel);
			emailSettingsWindow.setSettings(emailConfigModel);
		}
	}
	

	@Override
	public void setEnabled(boolean enabled) {
		
		if(!enabled) {
			missHeatbeatCheckBox.setEnabled(enabled);
			autoFaioverCheckBox.setEnabled(enabled);
			manualFailOverCheckBox.setEnabled(enabled);
			replicationSpaceCheckBox.setEnabled(enabled);
			replicationSpaceNumberField.setEnabled(enabled);
			replicationSpaceComboBox.setEnabled(enabled);
			replicationErrorCheckBox.setEnabled(enabled);
//			LicenseCheckBox.setEnabled(enabled);
			notReachableCheckBox.setEnabled(enabled);
			conversionSuccess.setEnabled(enabled);
			failoverFailure.setEnabled(enabled);
		}
		
	}

	
	//////////////////////////////////////////////////////////////////////////
	//
	//  ADDED FOR EDGE
	//
	//////////////////////////////////////////////////////////////////////////



	private boolean isForEdge = false;
	private int settingsContentId = -1;
	
	private Label placeholder;
	
	@Override
	public void initialize( ISettingsContentHost contentHost, boolean isForEdge )
	{
		this.contentHost = contentHost;
		this.isForEdge = isForEdge;
		
		this.doInitialization();
	}
	
	@Override
	public boolean isForEdge()
	{
		return this.isForEdge;
	}

	@Override
	public void setIsForEdge( boolean isForEdge )
	{
		this.isForEdge = isForEdge;
	}
	
	@Override
	public void setId( int settingsContentId )
	{
		this.settingsContentId = settingsContentId;
	}

	@Override
	public Widget getWidget()
	{
		return this;
	}

	@Override
	public void loadData()
	{
		onLoadingCompleted(true);
	}

	@Override
	public void loadDefaultData()
	{
		onLoadingCompleted(true);
	}

	@Override
	public void saveData()
	{
		onSavingCompleted(true);
	}
	
	@Override
	public void validate()
	{
		validateSettings();
	}
	
	@Override
	public void setDefaultEmail(IEmailConfigModel iEmailConfigModel)
	{
		setDefaultEmailSettings(iEmailConfigModel);
	}
	
	private void onLoadingCompleted( boolean isSuccessful )
	{
		//if (this.isForEdge)
		//{
			//this.contentHost.
		 SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.LoadData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
		//}
	}
	
	private void onSavingCompleted( boolean isSuccessful )
	{
		//if (this.isForEdge)
		//{
			//this.contentHost
			SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.SaveData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
		//}
	}
	
	private void onValidatingCompleted( boolean isSuccessful )
	{
		//if (this.isForEdge)
		//{
			//this.contentHost
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.Validate,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
		//}
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

package com.ca.arcflash.ui.client.coldstandby.setting;

import java.util.ArrayList;

import com.ca.arcflash.jobscript.alert.AlertJobScript;
import com.ca.arcflash.jobscript.alert.EmailModel;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.DisclourePanelImageBundles;
import com.ca.arcflash.ui.client.coldstandby.VCMMessages;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.homepage.PreferencesEmailSettingsWindow;
import com.ca.arcflash.ui.client.model.EmailConfigModel;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;

public class EmailPanel extends WizardPage{
	CheckBox missHeatbeatCheckBox;
	CheckBox autoFaioverCheckBox;
	CheckBox manualFailOverCheckBox;
	CheckBox replicationSpaceCheckBox;
	NumberField replicationSpaceNumberField;
	BaseSimpleComboBox<String> replicationSpaceComboBox;
	CheckBox replicationErrorCheckBox;
//	CheckBox LicenseCheckBox;
	CheckBox notReachableCheckBox;
	
	Button emailSettingButton;
	PreferencesEmailSettingsWindow emailSettingsWindow;
	boolean	isMailServerSave;
	EmailConfigModel emailConfigModel;
	
	@SuppressWarnings("deprecation")
	public EmailPanel() {
		this.isMailServerSave = false;
		this.ensureDebugId("7a180960-f47b-4310-8da7-48ba33c27f12");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.ensureDebugId("ebf04923-10ac-472d-964f-10935b745014");
		verticalPanel.setTableWidth("100%");
		verticalPanel.setWidth("100%");
		verticalPanel.setScrollMode(Scroll.AUTO);
		
		//title and description
		/*Label titleLabel = new Label(UIContext.Constants.coldStandbySettingEmailAlertTitle());
		titleLabel.ensureDebugId("567ba42d-bec3-430a-866d-71726d4e70b8");
		titleLabel.setStyleName("coldStandbySettingTitle");
		titleLabel.getElement().getStyle().setPadding(8, Unit.PX);
		verticalPanel.add(titleLabel);

		Label descriptionLabel = new Label(UIContext.Constants.coldStandbySettingEmailAlertDescription());
		descriptionLabel.ensureDebugId("9ef8a106-1183-411e-bcc0-1447d8f5b3ce");
		descriptionLabel.setStyleName("coldStandbySettingDescription");
		descriptionLabel.getElement().getStyle().setPaddingLeft(8, Unit.PX);
		verticalPanel.add(descriptionLabel);*/

		DisclosurePanel emailDisPanel = new DisclosurePanel(
				(DisclourePanelImageBundles) GWT.create(DisclourePanelImageBundles.class),
				UIContext.Constants.coldStandbySettingEmailAlertDeckPanel(), false);
		emailDisPanel.ensureDebugId("8513578d-d1d8-49a3-b6a9-96440effde46");
		emailDisPanel.setWidth("100%");
		emailDisPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		emailDisPanel.setOpen(true);

		FlexTable emailTable = new FlexTable();
		emailTable.ensureDebugId("69123126-a433-4865-8ec4-8503f7b81c1d");
		emailTable.setCellPadding(4);
		emailTable.setCellSpacing(4);
		
		missHeatbeatCheckBox = new CheckBox();
		missHeatbeatCheckBox.setBoxLabel(UIContext.Constants.coldStandbySettingEmailAlertMissHeatbeat());
		missHeatbeatCheckBox.setStyleName("setting-text-label");
		missHeatbeatCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				setButtonStatus();
			}
		});
		emailTable.setWidget(0, 0, missHeatbeatCheckBox);
		
		autoFaioverCheckBox = new CheckBox();
		autoFaioverCheckBox.setBoxLabel(UIContext.Constants.coldStandbySettingEmailAlertAutoFailover());
		autoFaioverCheckBox.setStyleName("setting-text-label");
		autoFaioverCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				setButtonStatus();
			}
		});
		emailTable.setWidget(1, 0, autoFaioverCheckBox);
		
		manualFailOverCheckBox = new CheckBox();
		manualFailOverCheckBox.setBoxLabel(UIContext.Constants.coldStandbySettingEmailAlertManualFailover());
		manualFailOverCheckBox.setStyleName("setting-text-label");
		manualFailOverCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				setButtonStatus();
			}
		});
		emailTable.setWidget(2, 0, manualFailOverCheckBox);
		
		FlexTable flexTable = new FlexTable();
		//hPanel.setStyleName("setting-text-label");
		//hPanel.setStyleAttribute("margin","5px,0px,0px,0px");
		replicationSpaceCheckBox = new CheckBox();
		replicationSpaceCheckBox.setBoxLabel(UIContext.Constants.coldStandbySettingEmailAlertReplicationSpace());
		replicationSpaceCheckBox.setStyleName("setting-text-label");
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
		//hPanel.add(replicationSpaceCheckBox);
		flexTable.setWidget(0, 0, replicationSpaceCheckBox);
		
		replicationSpaceNumberField = new NumberField();
		replicationSpaceNumberField.setValue(5);
		replicationSpaceNumberField.setWidth(120);
		replicationSpaceNumberField.setStyleName("setting-text-label");
		replicationSpaceNumberField.setAllowBlank(false);
		replicationSpaceNumberField.setAllowDecimals(true);
		replicationSpaceNumberField.setAllowNegative(false);
		replicationSpaceNumberField.setAllowBlank(false);
		//replicationSpaceNumberField.setRegex("[1-9][0-9]*");
		replicationSpaceNumberField.disable();
		//hPanel.add(replicationSpaceNumberField);
		flexTable.setWidget(0, 1, replicationSpaceNumberField);
		
		replicationSpaceComboBox = new BaseSimpleComboBox<String>();
		replicationSpaceComboBox.setEditable(false);
		replicationSpaceComboBox.add("%");
		replicationSpaceComboBox.add("MB");
		replicationSpaceComboBox.setSimpleValue("%");
		replicationSpaceComboBox.disable();
		replicationSpaceComboBox.setStyleName("setting-text-label");
		replicationSpaceComboBox.setWidth("100%");
		//hPanel.add(replicationSpaceComboBox);
		flexTable.setWidget(0, 2, replicationSpaceComboBox);
		
		emailTable.setWidget(3, 0, flexTable);
		
		
		replicationErrorCheckBox = new CheckBox();
		replicationErrorCheckBox.setBoxLabel(VCMMessages.coldStandbySettingEmailAlertReplicationError());
		replicationErrorCheckBox.setStyleName("setting-text-label");
		replicationErrorCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				setButtonStatus();
			}
		});
		emailTable.setWidget(4, 0, replicationErrorCheckBox);
		
		notReachableCheckBox = new CheckBox();
		notReachableCheckBox.setBoxLabel(UIContext.Constants.coldStandbySettingEmailAlertNotReachable());
		notReachableCheckBox.setStyleName("setting-text-label");
		notReachableCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				setButtonStatus();
			}
		});
		emailTable.setWidget(5, 0, notReachableCheckBox);
		
//		LicenseCheckBox = new CheckBox();
//		LicenseCheckBox.setBoxLabel(UIContext.Constants.coldStandbySettingEmailAlertLicense());
//		LicenseCheckBox.setStyleName("setting-text-label");
//		LicenseCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {
//			@Override
//			public void handleEvent(FieldEvent be) {
//				setButtonStatus();
//			}
//		});
//		emailTable.setWidget(6, 0, LicenseCheckBox);
		
		emailSettingsWindow = new PreferencesEmailSettingsWindow();
		emailConfigModel = new EmailConfigModel();
		emailSettingsWindow.setSettings(emailConfigModel);
		emailSettingButton = new Button(UIContext.Constants.settingsEmailSettings());
		emailSettingButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				//settingsDlg = new PreferencesEmailSettingsWindow();
				emailSettingsWindow.setModal(true);
				emailSettingsWindow.show();
				
				emailSettingsWindow.addWindowListener( new WindowListener(){
					public void	windowHide(WindowEvent we)
					{
						//Click ok the button should not be null
						if (we.getButtonClicked() != null)
						{
							
							emailSettingsWindow.saveSettings(emailConfigModel);
							isMailServerSave = true;
						}
					}
				});
				
			}
		});
		emailTable.setWidget(7, 0, emailSettingButton);
		
		emailDisPanel.add(emailTable);
		verticalPanel.add(emailDisPanel);
		
		this.add(verticalPanel);
		
		setButtonStatus();
	}

	private void setButtonStatus() {
		
		if(autoFaioverCheckBox.getValue()) {
			emailSettingButton.enable();
		}
		else if(manualFailOverCheckBox.getValue()) {
			emailSettingButton.enable();
		}
		else if(replicationSpaceCheckBox.getValue()) {
			emailSettingButton.enable();
		}
		else if(replicationErrorCheckBox.getValue()) {
			emailSettingButton.enable();
		}
//		else if(LicenseCheckBox.getValue()) {
//			emailSettingButton.enable();
//		}
		else if(notReachableCheckBox.getValue()) {
			emailSettingButton.enable();
		}
		else if(missHeatbeatCheckBox.getValue()) {
			emailSettingButton.enable();
		}
		else {
			emailSettingButton.disable();
		}
	}

	protected boolean validate(){
		boolean result = true;
		if(replicationSpaceCheckBox.getValue()) {
			result = replicationSpaceNumberField.validate() && replicationSpaceComboBox.validate();
		}
		
		return result;
	}
	
	protected boolean checkMailServer() {
		boolean result= true;
		if(emailSettingButton.isEnabled()) {
			
			if(!isMailServerSave) {
				result = false;
			}
		}
		return result;
	}

	protected void populateAlertJobScript(AlertJobScript alertJobScript) {
		
		if(isMailServerSave)
		{
			EmailModel emailModel = new EmailModel();
			emailModel.setContent("");
			
			boolean isEnableProxy = emailConfigModel.isEnableProxy();
			emailModel.setEnableProxy(isEnableProxy);
			if(isEnableProxy) {
				emailModel.setProxyAddress(emailConfigModel.getProxyAddress());
				emailModel.setProxyPassword(emailConfigModel.getProxyPassword());
				emailModel.setProxyUsername(emailConfigModel.getProxyUsername());
				emailModel.setProxyPort(emailConfigModel.getProxyPort());
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
			emailModel.setRecipients(emailConfigModel.getRecipients().toArray(new String[0]));
			emailModel.setMailService(emailConfigModel.getMailService());
			alertJobScript.setEmailModel(emailModel);
		}
		else {
			alertJobScript.setEmailModel(null);
		}
	
		alertJobScript.setMissHeatbeat(missHeatbeatCheckBox.getValue());
		alertJobScript.setAutoFaiover(autoFaioverCheckBox.getValue());
		alertJobScript.setManualFailover(manualFailOverCheckBox.getValue());
//		alertJobScript.setLicense(LicenseCheckBox.getValue());
		alertJobScript.setNotreachable(notReachableCheckBox.getValue());
		alertJobScript.setReplicationError(replicationErrorCheckBox.getValue());
		alertJobScript.setReplicationSpaceWarning(replicationSpaceCheckBox.getValue());
		alertJobScript.setSpaceMeasureNumber(replicationSpaceNumberField.getValue().floatValue());
		alertJobScript.setSpaceMeasureUnit(replicationSpaceComboBox.getSimpleValue());
		
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
		if((emailModel != null)&&(!isStringEmpty(emailModel.getSMTP()))) {
			
			boolean isEnableProxy = emailModel.isEnableProxy();
			emailConfigModel.setEnableProxy(isEnableProxy);
			if(isEnableProxy) {
				emailConfigModel.setProxyAddress(emailModel.getProxyAddress());
				emailConfigModel.setProxyPassword(emailModel.getProxyPassword());
				emailConfigModel.setProxyUsername(emailModel.getProxyUsername());
				emailConfigModel.setProxyPort(emailModel.getProxyPort());
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
			if((recipients!=null)&&(recipients.length>0))
			{
				ArrayList<String> recList = new ArrayList<String>();
				for(int i = 0; i<recipients.length;i++)
				{
					recList.add(recipients[i]);
				}
				emailConfigModel.setRecipients(recList);
				
			}
			emailConfigModel.setMailService(emailModel.getMailService());
			emailSettingsWindow.setSettings(emailConfigModel);
			isMailServerSave = true;
		}
		
		missHeatbeatCheckBox.setValue(alertJobScript.isMissHeatbeat());
		autoFaioverCheckBox.setValue(alertJobScript.isAutoFaiover());
		manualFailOverCheckBox.setValue(alertJobScript.isManualFailover());
//		LicenseCheckBox.setValue(alertJobScript.isLicense());
		notReachableCheckBox.setValue(alertJobScript.isNotreachable());
		replicationErrorCheckBox.setValue(alertJobScript.isReplicationError());
		replicationSpaceCheckBox.setValue(alertJobScript.isReplicationSpaceWarning());
		replicationSpaceNumberField.setValue(alertJobScript.getSpaceMeasureNumber());
		replicationSpaceComboBox.setSimpleValue(alertJobScript.getSpaceMeasureUnit());
		
		setButtonStatus();
	}
	
	@Override
	public String getDescription() {
		return UIContext.Constants.advancedButtonSettings();
	}

	@Override
	public String getTitle() {
		return UIContext.Constants.advancedLabelSendEmail();
	}

	@Override
	protected void activate() {

	}
	
}

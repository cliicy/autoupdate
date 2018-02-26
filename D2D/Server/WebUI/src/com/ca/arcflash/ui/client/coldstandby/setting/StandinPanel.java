package com.ca.arcflash.ui.client.coldstandby.setting;

import com.ca.arcflash.jobscript.base.JobType;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.HyperV;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ARCFlashStorage;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMwareESXStorage;
import com.ca.arcflash.jobscript.replication.VMwareVirtualCenterStorage;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.DisclourePanelImageBundles;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.ConnectionProtocol;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;

class StandinPanel extends WizardPage {

	private static final int WIDTH_TEXTFIELD = 200;
	private static final int DEFAULT_PORT = 8014;
	private static final String DEFAULT_USERNAME = "Administrator";

	TextField<String> textFieldMonitorServer;
	TextField<String> textFieldUserName;
	PasswordTextField textFieldPassword;
	RadioButton httpRadio;
	RadioButton httpsRadio;
	NumberField textFieldPort;
	CheckBox checkBoxProxy;
	//private HeartBeatPropertiesWindow heartBeatPropertiesWindow;
	private HeartBeatJobScript originalJobScript;

	RadioButton radioAuto;
	RadioButton radioManual;

	NumberField timeoutField;
	NumberField frequencyField;

	@SuppressWarnings("deprecation")
	public StandinPanel() {
		this.ensureDebugId("4d78f3c5-5e15-4f87-ba7d-d6aa2fb1f5cb");

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.ensureDebugId("d4af7241-b322-48c7-8494-e7cc1e447ca1");
		verticalPanel.setTableWidth("100%");
		verticalPanel.setWidth("100%");
		verticalPanel.setScrollMode(Scroll.AUTO);

		DisclosurePanel serverSettingPanel = new DisclosurePanel(
				(DisclourePanelImageBundles) GWT
						.create(DisclourePanelImageBundles.class),
				UIContext.Constants.coldStandbySettingStandinMonitoring(),
				false);
		serverSettingPanel
				.ensureDebugId("db40228e-d620-4efa-9c7e-f08dcca4d934");
		serverSettingPanel.setWidth("100%");
		serverSettingPanel
				.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		serverSettingPanel.setOpen(true);

		FlexTable serverSettingTable = new FlexTable();
		serverSettingTable
				.ensureDebugId("57d0cc09-a80a-4226-8df0-39b897df7534");
		serverSettingTable.setCellPadding(4);
		serverSettingTable.setCellSpacing(4);

		Label titleLabel = new Label();
		titleLabel.ensureDebugId("43ac9276-f7b9-4b13-a39b-fd50378924f0");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants
				.coldStandbySettingStandinMonitorServer());
		serverSettingTable.setWidget(0, 0, titleLabel);
		serverSettingTable.getCellFormatter().setVerticalAlignment(0, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);

		textFieldMonitorServer = new TextField<String>();
		textFieldMonitorServer
				.ensureDebugId("aae2d710-90a5-4480-a042-780e996ecb11");
		textFieldMonitorServer.setAllowBlank(false);
		textFieldMonitorServer.setWidth(WIDTH_TEXTFIELD);
		serverSettingTable.setWidget(0, 1, textFieldMonitorServer);
		serverSettingTable.getCellFormatter().setVerticalAlignment(0, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);

		titleLabel = new Label();
		titleLabel.ensureDebugId("41124a47-91e6-4423-857f-feea2bf3d1f0");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants
				.coldStandbySettingStandinUserName());
		serverSettingTable.setWidget(1, 0, titleLabel);
		serverSettingTable.getCellFormatter().setVerticalAlignment(1, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);

		textFieldUserName = new TextField<String>();
		textFieldUserName.ensureDebugId("a8a9a8c3-d621-4253-9db9-0b8982ad9379");
		textFieldUserName.setAllowBlank(false);
		textFieldUserName.setWidth(WIDTH_TEXTFIELD);
		textFieldUserName.setValue(DEFAULT_USERNAME);
		serverSettingTable.setWidget(1, 1, textFieldUserName);
		serverSettingTable.getCellFormatter().setVerticalAlignment(1, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);

		titleLabel = new Label();
		titleLabel.ensureDebugId("e40fefd0-9729-49ef-b6df-32eaa4ad6fae");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants
				.coldStandbySettingStandinPassword());
		serverSettingTable.setWidget(2, 0, titleLabel);
		serverSettingTable.getCellFormatter().setVerticalAlignment(2, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);

		textFieldPassword = new PasswordTextField();
		textFieldPassword.ensureDebugId("ef67f2a4-03c1-4713-ab64-28d433818860");
		textFieldPassword.setAllowBlank(false);
		textFieldPassword.setPassword(true);
		textFieldPassword.setWidth(WIDTH_TEXTFIELD);
		serverSettingTable.setWidget(2, 1, textFieldPassword);
		serverSettingTable.getCellFormatter().setVerticalAlignment(2, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);

		titleLabel = new Label();
		titleLabel.ensureDebugId("4e5c0314-a531-4233-a388-b5e2f4c85336");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants
				.coldStandbySettingStandinProtocol());
		serverSettingTable.setWidget(3, 0, titleLabel);
		serverSettingTable.getCellFormatter().setVerticalAlignment(3, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);

		HorizontalPanel protocolPanel = new HorizontalPanel();
		protocolPanel.ensureDebugId("214edda3-2b72-4fe9-be3f-0f9413173c23");
		protocolPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		httpRadio = new RadioButton("MonitorProtocol");
		httpRadio.setStyleName("panel-text-value");
		httpRadio.ensureDebugId("d629d5d3-8f30-480a-b86a-6b592f890c72");
		httpRadio.getElement().getStyle().setPaddingLeft(0, Unit.PX);
		httpRadio.setText(UIContext.Constants
				.coldStandbySettingStandinProtocolHTTP());
		httpRadio.setValue(true);

		httpsRadio = new RadioButton("MonitorProtocol");
		httpsRadio.setStyleName("panel-text-value");
		httpsRadio.ensureDebugId("9945d11f-fae6-4559-a894-e930b3e2085d");
		httpsRadio.setText(UIContext.Constants
				.coldStandbySettingStandinProtocolHTTPS());

		protocolPanel.add(httpRadio);
		protocolPanel.add(httpsRadio);
		serverSettingTable.setWidget(3, 1, protocolPanel);

		titleLabel = new Label();
		titleLabel.ensureDebugId("0dd31789-2d38-4add-b4e1-72571256a4bf");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingStandinPort());
		serverSettingTable.setWidget(4, 0, titleLabel);
		serverSettingTable.getCellFormatter().setVerticalAlignment(4, 0,
				HasVerticalAlignment.ALIGN_TOP);

		textFieldPort = new NumberField();
		textFieldPort.ensureDebugId("044d3eda-3e61-4be4-8ec9-82b5c62cdab4");
		textFieldPort.setAllowDecimals(false);
		textFieldPort.setAllowNegative(false);
		textFieldPort.setAllowBlank(false);
		textFieldPort.setRegex("[1-9][0-9]*");
		textFieldPort.getMessages().setRegexText(UIContext.Constants.coldStandbySettingInvalidInteger());
		textFieldPort.setWidth(80);
		textFieldPort.setValue(DEFAULT_PORT);
		serverSettingTable.setWidget(4, 1, textFieldPort);

		checkBoxProxy = new CheckBox();
		checkBoxProxy.setBoxLabel(UIContext.Constants
				.coldStandbySettingStandinProxy());
		Utils.addToolTip(checkBoxProxy, UIContext.Constants
				.coldStandbySettingStandinVDDK());
		checkBoxProxy.setVisible(false);
		checkBoxProxy.setValue(true);
		checkBoxProxy.setStyleName("panel-text-value");
		serverSettingTable.setWidget(5, 0, checkBoxProxy);
		serverSettingTable.getFlexCellFormatter().setColSpan(5, 0, 2);

		serverSettingPanel.add(serverSettingTable);
		verticalPanel.add(serverSettingPanel);

		DisclosurePanel recoveryPanel = new DisclosurePanel(
				(DisclourePanelImageBundles) GWT
						.create(DisclourePanelImageBundles.class),
				UIContext.Constants.coldStandbySettingStandinRecovery(), false);
		recoveryPanel.ensureDebugId("47553451-d84b-482f-97bb-32c33012b09c");
		recoveryPanel.setWidth("100%");
		recoveryPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		recoveryPanel.setOpen(true);

		FlexTable recoveryTable = new FlexTable();
		recoveryTable.ensureDebugId("a6dd9934-d722-41b9-af21-76243c778ef8");
		recoveryTable.setCellPadding(4);
		recoveryTable.setCellSpacing(4);

		radioManual = new RadioButton("RecoveryType");
		radioManual.ensureDebugId("dbc4ef33-d3de-44de-9a8b-c664ebacd043");
		radioManual.setText(UIContext.Constants
				.coldStandbySettingStandinRecoveryTypeManual());
		radioManual.setStyleName("panel-text-value");
		radioManual.getElement().getStyle().setPaddingLeft(0, Unit.PX);
		recoveryTable.setWidget(0, 0, radioManual);

		radioAuto = new RadioButton("RecoveryType");
		radioAuto.ensureDebugId("e7d7e15d-8910-4b68-83b8-465b3e61881e");
		radioAuto.setStyleName("panel-text-value");
		radioAuto.getElement().getStyle().setPaddingLeft(0, Unit.PX);
		radioAuto.setText(UIContext.Constants
				.coldStandbySettingStandinRecoveryTypeAutomatic());
		radioAuto.setValue(true);
		recoveryTable.setWidget(1, 0, radioAuto);


		recoveryPanel.add(recoveryTable);
		verticalPanel.add(recoveryPanel);

		DisclosurePanel heartBeatPanel = new DisclosurePanel(
				(DisclourePanelImageBundles) GWT
						.create(DisclourePanelImageBundles.class),
				UIContext.Constants
						.coldStandbySettingStandinHeartBeatProperties(), false);
		heartBeatPanel.ensureDebugId("1ea58164-29f8-4813-9392-00f07d0137bb");
		heartBeatPanel.setWidth("100%");
		heartBeatPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		heartBeatPanel.setOpen(true);

		FlexTable heartBeatTable = new FlexTable();
		heartBeatTable.ensureDebugId("7cdfe74c-6dbc-416d-9f61-4cf7ee8d60eb");
		heartBeatTable.setCellPadding(4);
		heartBeatTable.setCellSpacing(4);

		Label label = new Label();
		label.ensureDebugId("afb91dc3-9721-489b-80c7-d4e1761b704f"); //$NON-NLS-1$
		label.setStyleName("setting-text-label"); //$NON-NLS-1$
		label.setText(UIContext.Constants.HeartBeatPropertiesWindowTimeout()); //$NON-NLS-1$
		heartBeatTable.setWidget(0, 0, label);

		timeoutField = new NumberField();
		timeoutField.ensureDebugId("5c341c76-3b84-4d79-bad1-5a6f28642cb3"); //$NON-NLS-1$
		timeoutField.setMinValue(1);
		timeoutField.setMaxValue(60 * 60);
		timeoutField.setValue(30);
		timeoutField.setAllowBlank(false);
		timeoutField.setAllowDecimals(false);
		timeoutField.setRegex("[1-9][0-9]*");
		timeoutField.getMessages().setRegexText(UIContext.Constants.coldStandbySettingInvalidInteger());
		heartBeatTable.setWidget(0, 1, timeoutField);

		label = new Label();
		label.setStyleName("setting-text-label"); //$NON-NLS-1$
		label.setText(UIContext.Constants.seconds());
		label.getElement().getStyle().setColor("DarkGray"); //$NON-NLS-1$
		heartBeatTable.setWidget(0, 2, label);

		label = new Label();
		label.ensureDebugId("9d731d62-7e7e-482d-a992-44f6d1cb65e0"); //$NON-NLS-1$
		label.setStyleName("setting-text-label"); //$NON-NLS-1$
		label.setText(UIContext.Constants.HeartBeatPropertiesWindowFrequency()); //$NON-NLS-1$
		heartBeatTable.setWidget(1, 0, label);

		frequencyField = new NumberField();
		frequencyField.ensureDebugId("3f592f99-69f1-4dc5-95d3-6610a1d57946"); //$NON-NLS-1$
		frequencyField.setMinValue(1);
		frequencyField.setMaxValue(60 * 60);
		frequencyField.setValue(5);
		frequencyField.setAllowBlank(false);
		frequencyField.setAllowDecimals(false);
		frequencyField.setRegex("[1-9][0-9]*");
		frequencyField.getMessages().setRegexText(UIContext.Constants.coldStandbySettingInvalidInteger());
		heartBeatTable.setWidget(1, 1, frequencyField);

		label = new Label();
		label.setStyleName("setting-text-label"); //$NON-NLS-1$
		label.setText(UIContext.Constants.seconds());
		label.getElement().getStyle().setColor("DarkGray"); //$NON-NLS-1$
		heartBeatTable.setWidget(1, 2, label);

		heartBeatPanel.add(heartBeatTable);
		verticalPanel.add(heartBeatPanel);

		this.add(verticalPanel);
	}

	public boolean validate() {
		return textFieldMonitorServer.validate()
				&& textFieldUserName.validate() && textFieldPassword.validate()
				&& textFieldPort.validate()
				&& timeoutField.validate() && frequencyField.validate();
	}

	protected void populateBeatJobScript(HeartBeatJobScript script) {
		script.setHeartBeatMonitorHostName(textFieldMonitorServer.getValue());
		script.setHeartBeatMonitorPort(textFieldPort.getValue().intValue());
		script.setJobType(JobType.HeartBeat);
		script.setHeartBeatMonitorUserName(textFieldUserName.getValue());
		script.setHeartBeatMonitorPassword(textFieldPassword.getValue());
		script.setHeartBeatMonitorProtocol(getProtocol().toString());
		
		script.setHeartBeatFrequencyInSeconds(frequencyField.getValue().longValue());
		script.setHeatBeatTimeoutInSeconds(timeoutField.getValue().intValue());
	

	}

	protected void populateFailoverJobScript(FailoverJobScript failoverScript) {
	
		failoverScript.setHeartBeatFailoverTimeoutInSecond(timeoutField.getValue().intValue());
		
		failoverScript.setAutoFailover(getFailoverJobScriptWay());

		if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.HyperV) {
			HyperV hyperV = (HyperV) failoverScript.getFailoverMechanism().get(0);

			hyperV.setHostName(textFieldMonitorServer.getValue());
			hyperV.setUserName(textFieldUserName.getValue());
			hyperV.setPassword(textFieldPassword.getValue());
		}
	}

	protected void populateUI(HeartBeatJobScript heartBeatScript,
			FailoverJobScript failoverScript,
			ReplicationJobScript replicationScript) {
		originalJobScript = heartBeatScript;
		if (originalJobScript == null)
			return;

		textFieldMonitorServer.setValue(heartBeatScript
				.getHeartBeatMonitorHostName());
		textFieldPort.setValue(heartBeatScript.getHeartBeatMonitorPort());
		textFieldUserName.setValue(heartBeatScript
				.getHeartBeatMonitorUserName());
		textFieldPassword.setValue(heartBeatScript
				.getHeartBeatMonitorPassword());

		if (ConnectionProtocol.HTTP.toString().equals(
				heartBeatScript.getHeartBeatMonitorProtocol()))
			httpRadio.setValue(true);
		else
			httpsRadio.setValue(true);

		if (failoverScript != null) {
			radioAuto.setValue(failoverScript.isAutoFailover());
			radioManual.setValue(!failoverScript.isAutoFailover());
		}
		
		timeoutField.setValue(heartBeatScript.getHeatBeatTimeoutInSeconds());
		frequencyField.setValue(heartBeatScript.getHeartBeatFrequencyInSeconds());
		
		if((replicationScript!=null)&&(failoverScript!=null)){
			
			if(failoverScript.getVirtualType()==VirtualizationType.VMwareESX){
				VMwareESXStorage storage = (VMwareESXStorage) replicationScript.getReplicationDestination().get(0);
				checkBoxProxy.setValue(storage.isProxyEnabled());
			}
			else if(failoverScript.getVirtualType()==VirtualizationType.VMwareVirtualCenter){
				VMwareVirtualCenterStorage storage = (VMwareVirtualCenterStorage)replicationScript.getReplicationDestination().get(0);
				checkBoxProxy.setValue(storage.isProxyEnabled());
			}
		}
	}

	private ConnectionProtocol getProtocol() {
		if (httpRadio.getValue())
			return ConnectionProtocol.HTTP;
		else
			return ConnectionProtocol.HTTPS;
	}

	private Boolean getFailoverJobScriptWay() {
		if (radioAuto.getValue())
			return true;
		else
			return false;
	}

	@Override
	public String getDescription() {
		return UIContext.Constants.coldStandbySettingStandinDescription();
	}

	@Override
	public String getTitle() {
		return UIContext.Constants.coldStandbySettingStandinTitle();
	}

	@Override
	protected void activate() {

		VirtualizationType virtualizationType = WizardContext
				.getWizardContext().getVirtulizationType();
		if ((virtualizationType == VirtualizationType.VMwareESX)
				|| (virtualizationType == VirtualizationType.VMwareVirtualCenter)) {

			checkBoxProxy.setVisible(true);
			textFieldMonitorServer.enable();
			textFieldUserName.enable();
			textFieldPassword.enable();
		} else {
			checkBoxProxy.setVisible(false);
			textFieldMonitorServer.setValue(WizardContext.getWizardContext()
					.getHypervHost());
			textFieldUserName.setValue(WizardContext.getWizardContext()
					.getHypervUsername());
			textFieldPassword.setValue(WizardContext.getWizardContext()
					.getHypervPassword());

			textFieldMonitorServer.disable();
			textFieldUserName.disable();
			textFieldPassword.disable();
		}

	}

	protected void populateReplicationJobScript(ReplicationJobScript replicationScript) {
		//replicationScript.setAutoReplicate(getReplicationWay());
		if (WizardContext.getWizardContext().getVirtulizationType() == VirtualizationType.HyperV) {
			ARCFlashStorage storage = (ARCFlashStorage) replicationScript
					.getReplicationDestination().get(0);
			storage.setUserName(textFieldUserName.getValue());
			storage.setPassword(textFieldPassword.getValue());
			storage.setMonitorPort(textFieldPort.getValue().intValue());
		}
		else if(WizardContext.getWizardContext().getVirtulizationType()==VirtualizationType.VMwareESX){
			VMwareESXStorage storage = (VMwareESXStorage) replicationScript.getReplicationDestination().get(0);
			storage.setProxyEnabled(checkBoxProxy.getValue());
		}
		else if(WizardContext.getWizardContext().getVirtulizationType()==VirtualizationType.VMwareVirtualCenter){
			VMwareVirtualCenterStorage storage = (VMwareVirtualCenterStorage)replicationScript.getReplicationDestination().get(0);
			storage.setProxyEnabled(checkBoxProxy.getValue());
		}
	}
}

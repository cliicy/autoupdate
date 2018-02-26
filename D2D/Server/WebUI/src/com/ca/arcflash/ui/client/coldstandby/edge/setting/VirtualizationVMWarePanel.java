package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.VMwareESX;
import com.ca.arcflash.jobscript.failover.VMwareVirtualCenter;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.replication.Protocol;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMwareESXStorage;
import com.ca.arcflash.jobscript.replication.VMwareVirtualCenterStorage;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.ConnectionProtocol;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

class VirtualizationVMWarePanel extends LayoutContainer {

	private static final int VMWARESERVER_TYPE_ESX = 1;
	private static final int VMWARESERVER_TYPE_VCENTER = 2;
	private static final int WIDTH_TEXTFIELD = 200;
	private static final int MAX_INPUT_LENGTH = 260;

	//private final ColdStandbyServiceAsync service = GWT.create(ColdStandbyService.class);

	TextField<String> textFieldServer;
	TextField<String> textFieldUserName;
	PasswordTextField textFieldPassword;
	Radio httpRadio;
	Radio httpsRadio;
	NumberField textFieldPort;
	BaseComboBox<BaseModel> esxNodeBox;

	private int serverType;
	private String version;

	public VirtualizationVMWarePanel() {
		  
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(4);
		tl.setCellPadding(4);
		//tl.setCellSpacing(4);
		container.ensureDebugId("712162f1-05b2-459b-a14e-b7c71af0a693");
		container.setLayout(tl);

		Label titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingVirtualizationServerName());
		container.add(titleLabel);
		
		textFieldServer = new TextField<String>();
		textFieldServer.ensureDebugId("3c969d6f-75c1-47f6-8ebd-73f93813093b");
		textFieldServer.setAllowBlank(false);
		textFieldServer.setWidth(WIDTH_TEXTFIELD);
		textFieldServer.setMaxLength(MAX_INPUT_LENGTH);
		Utils.addToolTip(textFieldServer, UIContext.Constants.coldStandbySettingVMwareHostTip());
		container.add(textFieldServer);

		// set the protocol control
		titleLabel = new Label();
		titleLabel.ensureDebugId("4e5c0314-a531-4233-a388-b5e2f4c85336");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingStandinProtocol());
		container.add(titleLabel);

		HorizontalPanel protocolPanel = new HorizontalPanel();
		protocolPanel.ensureDebugId("214edda3-2b72-4fe9-be3f-0f9413173c23");
		protocolPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		RadioGroup rgProtocol = new RadioGroup();
		httpRadio = new Radio(); //new RadioButton("Protocol");
		rgProtocol.add(httpRadio);
//		httpRadio.setStyleName("panel-text-value");
		httpRadio.ensureDebugId("d06eeef5-344b-4b12-9579-43ed7c3d3c3b");
		httpRadio.getElement().getStyle().setPaddingLeft(0, Unit.PX);
		httpRadio.setBoxLabel(UIContext.Constants.coldStandbySettingStandinProtocolHTTP());
		httpRadio.setTitle(UIContext.Constants.coldStandbySettingVMwareProtocolTip());

		httpsRadio = new Radio(); //new RadioButton("Protocol");
		rgProtocol.add(httpsRadio);
//		httpsRadio.setStyleName("panel-text-value");
		httpsRadio.ensureDebugId("991a056c-81ba-48b8-832e-57fe4f9f0c0f");
		httpsRadio.setBoxLabel(UIContext.Constants.coldStandbySettingStandinProtocolHTTPS());
		httpsRadio.setValue(true);
		httpRadio.setTitle(UIContext.Constants.coldStandbySettingVMwareProtocolTip());

		rgProtocol.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (httpRadio.getValue()) {
					textFieldPort.setValue(80);
				} else {
					textFieldPort.setValue(443);
				}

			}
		});
		protocolPanel.add(httpRadio);
		protocolPanel.add(httpsRadio);
		container.add(protocolPanel);
		
		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingVirtualizationUserName());
		container.add(titleLabel);

		textFieldUserName = new TextField<String>();
		textFieldUserName.ensureDebugId("8e673721-aff1-499d-8e13-f4a4b1bf8ed4");
		textFieldUserName.setAllowBlank(false);
		textFieldUserName.setWidth(WIDTH_TEXTFIELD);
		textFieldUserName.setMaxLength(MAX_INPUT_LENGTH);
		Utils.addToolTip(textFieldUserName, UIContext.Constants.coldStandbySettingVMwareUsernameTip());
		container.add(textFieldUserName);

		titleLabel = new Label();
		titleLabel.ensureDebugId("0dd31789-2d38-4add-b4e1-72571256a4bf");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingStandinPort());
		container.add(titleLabel);

		textFieldPort = new NumberField();
		textFieldPort.ensureDebugId("044d3eda-3e61-4be4-8ec9-82b5c62cdab4");
		textFieldPort.setAllowDecimals(false);
		textFieldPort.setAllowNegative(false);
		textFieldPort.setRegex("[1-9][0-9]*");
		textFieldPort.getMessages().setRegexText(UIContext.Constants.coldStandbySettingInvalidInteger());
		textFieldPort.setAllowBlank(false);
		textFieldPort.setWidth(80);
		textFieldPort.setValue(443);
		Utils.addToolTip(textFieldPort, UIContext.Constants.coldStandbySettingVMwarePortTip());
		
		container.add(textFieldPort);
		
		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingVirtualizationPassword());
		container.add(titleLabel);

		textFieldPassword = new PasswordTextField();
		textFieldPassword.ensureDebugId("e2e14eff-d52f-4fd4-ac0e-bd7929caa69f");
		textFieldPassword.setAllowBlank(false);
		textFieldPassword.setWidth(WIDTH_TEXTFIELD);
		textFieldPassword.setPassword(true);
		textFieldPassword.setMaxLength(MAX_INPUT_LENGTH);
		Utils.addToolTip(textFieldPassword, UIContext.Constants.coldStandbySettingVMwarePasswordTip());
		TableData td = new TableData();
		td.setColspan(3);
		container.add(textFieldPassword, td);
		
		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingVirtualizationESXNode());
		container.add(titleLabel);
		
		ListStore<BaseModel> esxNodeStore = new ListStore<BaseModel>();
		esxNodeBox = new BaseComboBox<BaseModel>();
		esxNodeBox.setTitle(UIContext.Constants.coldStandbySettingEsxNodeTip());
		esxNodeBox.setEditable(false);
		esxNodeBox.setAllowBlank(false);
		esxNodeBox.setTriggerAction(TriggerAction.ALL);
		esxNodeBox.setStore(esxNodeStore);
		esxNodeBox.ensureDebugId("613de673-01ea-4378-8d43-50deca2bcee6");
		esxNodeBox.setDisplayField("esxNode");
		esxNodeBox.setWidth(WIDTH_TEXTFIELD);
		esxNodeBox.setEmptyText(UIContext.Constants.coldStandbySettingVirtualizationConnectTip());
		td = new TableData();
		td.setColspan(3);
		container.add(esxNodeBox, td);
		
		this.add(container);
	}

	public boolean validate() {
		return textFieldServer.validate()
				&& textFieldUserName.validate() && textFieldPassword.validate()
				&& textFieldPort.validate();


	}

	public String getProtocol() {
		if (httpRadio.getValue())
			return ConnectionProtocol.HTTP.toString();
		else
			return ConnectionProtocol.HTTPS.toString();
	}

	private void setProtocol(String protocol) {
		if (protocol.equals(ConnectionProtocol.HTTP.toString())) {
			httpRadio.setValue(true);
			httpsRadio.setValue(false);
		} else {
			httpRadio.setValue(false);
			httpsRadio.setValue(true);
		}
	}

	protected void populateFailoverJobScript(FailoverJobScript failoverScript) {
		
		BaseModel esxNode=WizardContext.getWizardContext().getESXHostModel();
		if(esxNode==null){
			GWT.log("The esxNode is null");
			return;
		}
		
		if (serverType == VMWARESERVER_TYPE_ESX){
			failoverScript.setVirtualType(VirtualizationType.VMwareESX);
			VMwareESX esx = (VMwareESX)failoverScript.getFailoverMechanism().get(0);
			
			//BaseModel esxNode = comboESXNode.getValue();
			esx.setDataCenter((String)esxNode.get("dataCenter"));
			esx.setEsxName((String)esxNode.get("esxNode"));
			//esx.setDataStore(comboDataStorage.getSimpleValue());
			esx.setHostName(textFieldServer.getValue());
			esx.setVirtualizationType(VirtualizationType.VMwareESX);
			esx.setUserName(textFieldUserName.getValue());
			esx.setPassword(textFieldPassword.getValue());
			esx.setVersion(version);
			esx.setPort(textFieldPort.getValue().intValue());
			esx.setProtocol(getProtocol());
		}else{
			failoverScript.setVirtualType(VirtualizationType.VMwareVirtualCenter);
			VMwareVirtualCenter vCenter = (VMwareVirtualCenter)failoverScript.getFailoverMechanism().get(0);
			
			//BaseModel esxNode = comboESXNode.getValue();
			vCenter.setDataCenter((String)esxNode.get("dataCenter"));
			vCenter.setHostName(textFieldServer.getValue());
			vCenter.setVirtualizationType(VirtualizationType.VMwareVirtualCenter);
			vCenter.setESXHostName(textFieldServer.getValue());
			vCenter.setEsxName((String)esxNode.get("esxNode"));
			//vCenter.setDataStore(comboDataStorage.getSimpleValue());
			vCenter.setUserName(textFieldUserName.getValue());
			vCenter.setPassword(textFieldPassword.getValue());
			vCenter.setVersion(version);
			vCenter.setPort(textFieldPort.getValue().intValue());
			vCenter.setProtocol(getProtocol());
		}
	}
	
	public void populateReplicationJobScript(ReplicationJobScript replicationScript) {
		if (serverType == VMWARESERVER_TYPE_ESX){
			VMwareESXStorage storage = new VMwareESXStorage();
			replicationScript.getReplicationDestination().add(storage);
			
			//storage.setDataStorage(comboDataStorage.getSimpleValue());
			storage.setDestProtocol(Protocol.VMwareESX);
			storage.setESXHostName(textFieldServer.getValue());
			storage.setESXUserName(textFieldUserName.getValue());
			storage.setESXPassword(textFieldPassword.getValue());
			storage.setPort(textFieldPort.getValue().intValue());
			storage.setProtocol(getProtocol());
			storage.setProxyEnabled(WizardContext.getWizardContext().isProxyForDataTransfer());
		}else{
			VMwareVirtualCenterStorage storage = new VMwareVirtualCenterStorage();
			replicationScript.getReplicationDestination().add(storage);
			
			BaseModel esxNode=WizardContext.getWizardContext().getESXHostModel();
			if(esxNode==null){
				GWT.log("The esxNode is null");
				return;
			}
			
			//storage.setDataStorage(comboDataStorage.getSimpleValue());
			storage.setDestProtocol(Protocol.VMwareVCenter);
			storage.setEsxName((String)esxNode.get("esxNode"));
			storage.setVirtualCenterHostName(textFieldServer.getValue());
			storage.setVirtualCenterUserName(textFieldUserName.getValue());
			storage.setVirtualCenterPassword(textFieldPassword.getValue());
			storage.setDcName((String)esxNode.get("dataCenter"));
			storage.setPort(textFieldPort.getValue().intValue());
			storage.setProtocol(getProtocol());
			storage.setProxyEnabled(WizardContext.getWizardContext().isProxyForDataTransfer());
		}
	}

	protected void populateUI(FailoverJobScript failoverScript) {
		//isPopulatingUI = true;
		if (failoverScript.getVirtualType() == VirtualizationType.VMwareESX) {
			VMwareESX esx = (VMwareESX) failoverScript.getFailoverMechanism().get(0);

			version = esx.getVersion();
			serverType = VMWARESERVER_TYPE_ESX;
			textFieldServer.setValue(esx.getHostName());
			textFieldUserName.setValue(esx.getUserName());
			textFieldPassword.setValue(esx.getPassword());
			setProtocol(esx.getProtocol());
			textFieldPort.setValue(esx.getPort());

		} else {
			VMwareVirtualCenter esx = (VMwareVirtualCenter) failoverScript.getFailoverMechanism().get(0);

			version = esx.getVersion();
			serverType = VMWARESERVER_TYPE_VCENTER;
			textFieldServer.setValue(esx.getHostName());
			textFieldUserName.setValue(esx.getUserName());
			textFieldPassword.setValue(esx.getPassword());
			setProtocol(esx.getProtocol());
			textFieldPort.setValue(esx.getPort());

		}

		//isPopulatingUI = false;
	}

	public VirtualizationType getVirtulizationType() {
		if (serverType == VMWARESERVER_TYPE_ESX)
			return VirtualizationType.VMwareESX;
		else
			return VirtualizationType.VMwareVirtualCenter;
	}
	
	protected void setVMWareType(int type){
		this.serverType=type;
	}
	protected void setVMWareVersion(String version) {
		this.version=version;
	}
	
	public VirtualizationHost getCurrentHost() {
		return new VirtualizationHost(textFieldServer.getValue(), textFieldUserName.getValue(),
				textFieldPassword.getValue(), textFieldPort.getValue().intValue(), getProtocol());
	}

	@Override
	public void setEnabled(boolean enabled) {
		textFieldServer.setEnabled(enabled);
		textFieldUserName.setEnabled(enabled);
		textFieldPassword.setEnabled(enabled);
		httpRadio.setEnabled(enabled);
		httpsRadio.setEnabled(enabled);
		textFieldPort.setEnabled(enabled);
	}
}

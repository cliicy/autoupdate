package com.ca.arcflash.ui.client.coldstandby.setting;

import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.VMwareESX;
import com.ca.arcflash.jobscript.failover.VMwareVirtualCenter;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.replication.Protocol;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMwareESXStorage;
import com.ca.arcflash.jobscript.replication.VMwareVirtualCenterStorage;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.model.ConnectionProtocol;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

class VirtualizationVMWarePanel extends Composite {

	private static final int VMWARESERVER_TYPE_ESX = 1;
	private static final int VMWARESERVER_TYPE_VCENTER = 2;
	private static final int WIDTH_TEXTFIELD = 200;
	private static final int MAX_INPUT_LENGTH = 260;

	//private final ColdStandbyServiceAsync service = GWT.create(ColdStandbyService.class);

	TextField<String> textFieldServer;
	TextField<String> textFieldUserName;
	PasswordTextField textFieldPassword;
	RadioButton httpRadio;
	RadioButton httpsRadio;
	NumberField textFieldPort;

	private int serverType;
	private String version;

	FlexTable serverSettingTable = new FlexTable();

	public VirtualizationVMWarePanel() {

		
		serverSettingTable.ensureDebugId("712162f1-05b2-459b-a14e-b7c71af0a693");
		serverSettingTable.setCellPadding(4);
		serverSettingTable.setCellSpacing(4);

		Label titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants
				.coldStandbySettingVirtualizationServerName());
		serverSettingTable.setWidget(0, 0, titleLabel);
		serverSettingTable.getCellFormatter().setVerticalAlignment(0, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);

		textFieldServer = new TextField<String>();
		textFieldServer.ensureDebugId("3c969d6f-75c1-47f6-8ebd-73f93813093b");
		textFieldServer.setAllowBlank(false);
		textFieldServer.setWidth(WIDTH_TEXTFIELD);
		textFieldServer.setMaxLength(MAX_INPUT_LENGTH);
		serverSettingTable.setWidget(0, 1, textFieldServer);
		serverSettingTable.getCellFormatter().setVerticalAlignment(0, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);

		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants
				.coldStandbySettingVirtualizationUserName());
		serverSettingTable.setWidget(1, 0, titleLabel);
		serverSettingTable.getCellFormatter().setVerticalAlignment(1, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);

		textFieldUserName = new TextField<String>();
		textFieldUserName.ensureDebugId("8e673721-aff1-499d-8e13-f4a4b1bf8ed4");
		textFieldUserName.setAllowBlank(false);
		textFieldUserName.setWidth(WIDTH_TEXTFIELD);
		textFieldUserName.setMaxLength(MAX_INPUT_LENGTH);

		serverSettingTable.setWidget(1, 1, textFieldUserName);
		serverSettingTable.getCellFormatter().setVerticalAlignment(1, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);

		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants
				.coldStandbySettingVirtualizationPassword());
		serverSettingTable.setWidget(2, 0, titleLabel);
		serverSettingTable.getCellFormatter().setVerticalAlignment(2, 0,
				HasVerticalAlignment.ALIGN_MIDDLE);

		textFieldPassword = new PasswordTextField();
		textFieldPassword.ensureDebugId("e2e14eff-d52f-4fd4-ac0e-bd7929caa69f");
		textFieldPassword.setAllowBlank(false);
		textFieldPassword.setWidth(WIDTH_TEXTFIELD);
		textFieldPassword.setPassword(true);
		textFieldPassword.setMaxLength(MAX_INPUT_LENGTH);
		serverSettingTable.setWidget(2, 1, textFieldPassword);
		serverSettingTable.getCellFormatter().setVerticalAlignment(2, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);

		// set the protocol control
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

		httpRadio = new RadioButton("Protocol");
		httpRadio.setStyleName("panel-text-value");
		httpRadio.ensureDebugId("d06eeef5-344b-4b12-9579-43ed7c3d3c3b");
		httpRadio.getElement().getStyle().setPaddingLeft(0, Unit.PX);
		httpRadio.setText(UIContext.Constants
				.coldStandbySettingStandinProtocolHTTP());

		httpsRadio = new RadioButton("Protocol");
		httpsRadio.setStyleName("panel-text-value");
		httpsRadio.ensureDebugId("991a056c-81ba-48b8-832e-57fe4f9f0c0f");
		httpsRadio.setText(UIContext.Constants
				.coldStandbySettingStandinProtocolHTTPS());
		httpsRadio.setValue(true);

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
		textFieldPort.setRegex("[1-9][0-9]*");
		textFieldPort.getMessages().setRegexText(UIContext.Constants.coldStandbySettingInvalidInteger());
		textFieldPort.setAllowBlank(false);
		textFieldPort.setWidth(80);
		textFieldPort.setValue(443);
		
		setDefaultPort();
		serverSettingTable.setWidget(4, 1, textFieldPort);
		
		this.initWidget(serverSettingTable);
	}

	protected void addAMD64ArchTip() {
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.ensureDebugId("009383c0-ecf4-4fb9-b45e-cdaa5ece09a1");
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		Image warningImage = AbstractImagePrototype.create(UIContext.IconBundle.logWarning()).createImage();
		hPanel.add(warningImage);
		
		
		Label titleLabel = new Label();
		titleLabel.ensureDebugId("16f4f39f-ba44-461f-9d3d-f2a059f7835b");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingAMD64Msg());
		hPanel.add(titleLabel);
		
		serverSettingTable.setWidget(5, 0, hPanel);
		serverSettingTable.getFlexCellFormatter().setColSpan(5, 0, 2);
		serverSettingTable.getCellFormatter().setVerticalAlignment(5, 0,
				HasVerticalAlignment.ALIGN_TOP);
		
	}
	private void setDefaultPort(){
		httpRadio.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				// TODO Auto-generated method stub
				if(event.getValue()){
					textFieldPort.setValue(80);
				}
			}
		});
		
		httpsRadio.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				// TODO Auto-generated method stub
				if(event.getValue()){
					textFieldPort.setValue(443);
				}
			}
		});
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
			esx.setHostName(textFieldServer.getValue().trim());
			esx.setVirtualizationType(VirtualizationType.VMwareESX);
			esx.setUserName(textFieldUserName.getValue().trim());
			esx.setPassword(textFieldPassword.getValue());
			esx.setVersion(version);
			esx.setPort(textFieldPort.getValue().intValue());
			esx.setProtocol(getProtocol());
		}else{
			failoverScript.setVirtualType(VirtualizationType.VMwareVirtualCenter);
			VMwareVirtualCenter vCenter = (VMwareVirtualCenter)failoverScript.getFailoverMechanism().get(0);
			
			//BaseModel esxNode = comboESXNode.getValue();
			vCenter.setDataCenter((String)esxNode.get("dataCenter"));
			vCenter.setHostName(textFieldServer.getValue().trim());
			vCenter.setVirtualizationType(VirtualizationType.VMwareVirtualCenter);
			vCenter.setESXHostName(textFieldServer.getValue().trim());
			vCenter.setEsxName((String)esxNode.get("esxNode"));
			//vCenter.setDataStore(comboDataStorage.getSimpleValue());
			vCenter.setUserName(textFieldUserName.getValue().trim());
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
			storage.setESXHostName(textFieldServer.getValue().trim());
			storage.setESXUserName(textFieldUserName.getValue().trim());
			storage.setESXPassword(textFieldPassword.getValue());
			storage.setPort(textFieldPort.getValue().intValue());
			storage.setProtocol(getProtocol());
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
			storage.setVirtualCenterHostName(textFieldServer.getValue().trim());
			storage.setVirtualCenterUserName(textFieldUserName.getValue().trim());
			storage.setVirtualCenterPassword(textFieldPassword.getValue());
			storage.setDcName((String)esxNode.get("dataCenter"));
			storage.setPort(textFieldPort.getValue().intValue());
			storage.setProtocol(getProtocol());
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

	private String convert2TypeString(int vmwareserverTypeEsx) {
		if (VMWARESERVER_TYPE_ESX == vmwareserverTypeEsx)
			return "WMware ESX Host";
		else if (VMWARESERVER_TYPE_VCENTER == vmwareserverTypeEsx)
			return "WMware Virtual Center";
		return UIContext.Constants.unknown();
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

}

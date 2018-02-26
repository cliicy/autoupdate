package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.HyperV;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.replication.ARCFlashStorage;
import com.ca.arcflash.jobscript.replication.Protocol;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

class VirtualizationHyperVPanel extends LayoutContainer {
	private static final int WIDTH_TEXTFIELD	=	200;
	private static final String DEFAULT_USERNAME = "Administrator";
	
	TextField<String> textFieldServer;
	TextField<String> textFieldUserName;
	PasswordTextField textFieldPassword;
	Radio httpRadio;
	Radio httpsRadio;
	NumberField textFieldPort;
	
	public VirtualizationHyperVPanel(){
		
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(4);
		tl.setCellPadding(4);
		container.ensureDebugId("50436abd-07b5-4587-b989-7f980b56d3da");
		container.setLayout(tl);

		Label titleLabel = new Label();
		titleLabel.ensureDebugId("1aabebf6-81aa-4d3e-9c9b-d943d402ca9d");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingVirtualizationServerNameHyperV());
		container.add(titleLabel);

		textFieldServer = new TextField<String>();
		textFieldServer.ensureDebugId("1d090add-059a-48be-bd34-b81ae489916b");
		textFieldServer.setAllowBlank(false);
		textFieldServer.setWidth(WIDTH_TEXTFIELD);
		Utils.addToolTip(textFieldServer, UIContext.Messages.coldStandbySettingHyperVHostTip(UIContext.productNameD2D));
		textFieldServer.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				WizardContext.getWizardContext().setMonitorServer(textFieldServer.getValue());
			}
			
		});
		
//		textFieldServer.addListener(Events.KeyUp, new Listener<BaseEvent>() {
//
//			@Override
//			public void handleEvent(BaseEvent be) {
//				System.out.println("KeyUp");
//				WizardContext.getWizardContext().setMonitorServer(textFieldServer.getValue());
//			}
//			
//		});
		
		container.add(textFieldServer);

		titleLabel = new Label();
		titleLabel.ensureDebugId("a0477097-46e0-451c-865d-c8f1dd22c841");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants
				.coldStandbySettingStandinProtocol());
		container.add(titleLabel);

		HorizontalPanel protocolPanel = new HorizontalPanel();
		protocolPanel.ensureDebugId("b23443bf-d3c5-4ad7-8fcf-81a4b55705c6");
		protocolPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		RadioGroup rgProtocol = new RadioGroup();
		httpRadio = new Radio(); //new RadioButton("HyperVProtocol");
		rgProtocol.add(httpRadio);
//		httpRadio.setStyleName("panel-text-value");
		httpRadio.ensureDebugId("6878c278-3c46-425f-9232-2530ad87ec22");
		httpRadio.getElement().getStyle().setPaddingLeft(0, Unit.PX);
		httpRadio.setBoxLabel(UIContext.Constants.coldStandbySettingStandinProtocolHTTP());
		httpRadio.setValue(true);
		httpRadio.setTitle(UIContext.Messages.coldStandbySettingHyperVProtocolTip(UIContext.productNameD2D));

		httpsRadio = new Radio(); //new RadioButton("HyperVProtocol");
		rgProtocol.add(httpsRadio);
//		httpsRadio.setStyleName("panel-text-value");
		httpsRadio.ensureDebugId("a2497871-74b4-47e0-905e-57b34d0e7a5}");
		httpsRadio.setBoxLabel(UIContext.Constants.coldStandbySettingStandinProtocolHTTPS());
		httpsRadio.setTitle(UIContext.Messages.coldStandbySettingHyperVProtocolTip(UIContext.productNameD2D));

		rgProtocol.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (httpRadio.getValue()) {
					WizardContext.getWizardContext().setMonitorProtocol(true);
				} else {
					WizardContext.getWizardContext().setMonitorProtocol(false);
				}
			}
		});
		protocolPanel.add(httpRadio);
		protocolPanel.add(httpsRadio);
		container.add(protocolPanel);
		
		titleLabel = new Label();
		titleLabel.ensureDebugId("63fe62ed-d905-44ee-9464-feb22bd81fbc");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants
				.coldStandbySettingStandinUserName());
		container.add(titleLabel);

		textFieldUserName = new TextField<String>();
		textFieldUserName.ensureDebugId("0913286a-6124-4ab0-857b-62e59ba4f906");
		textFieldUserName.setAllowBlank(false);
		textFieldUserName.setWidth(WIDTH_TEXTFIELD);
		textFieldUserName.setValue(DEFAULT_USERNAME);
		Utils.addToolTip(textFieldUserName, UIContext.Constants.coldStandbySettingHyperVUsernameTip());
		textFieldUserName.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				WizardContext.getWizardContext().setMonitorUsername(textFieldUserName.getValue());
			}
			
		});
		container.add(textFieldUserName);
		
		titleLabel = new Label();
		titleLabel.ensureDebugId("181f9f40-a7a9-4597-95de-06e5cd365b16");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingStandinPort());
		container.add(titleLabel);

		textFieldPort = new NumberField();
		textFieldPort.ensureDebugId("67217129-f9d4-4a53-b795-dc30f670da5d");
		textFieldPort.setAllowDecimals(false);
		textFieldPort.setAllowNegative(false);
		textFieldPort.setAllowBlank(false);
		textFieldPort.setRegex("[1-9][0-9]*");
		textFieldPort.getMessages().setRegexText(UIContext.Constants.coldStandbySettingInvalidInteger());
		textFieldPort.setWidth(80);
		textFieldPort.setValue(8014);
		Utils.addToolTip(textFieldPort, UIContext.Messages.coldStandbySettingHyperVPortTip(UIContext.productNameD2D));
		textFieldPort.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				WizardContext.getWizardContext().setMonitorPort(textFieldPort.getValue().intValue());
			}
			
		});
		container.add(textFieldPort);
		

		titleLabel = new Label();
		titleLabel.ensureDebugId("b5f26070-52f4-4039-a013-f27d13de8c41");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants
				.coldStandbySettingStandinPassword());
		container.add(titleLabel);

		textFieldPassword = new PasswordTextField();
		textFieldPassword.ensureDebugId("0fda3278-3eed-4b95-b92e-1b5989832f09");
		textFieldPassword.setAllowBlank(false);
		textFieldPassword.setPassword(true);
		textFieldPassword.setWidth(WIDTH_TEXTFIELD);
		Utils.addToolTip(textFieldPassword, UIContext.Constants.coldStandbySettingHyperVPasswordTip());
		textFieldPassword.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				WizardContext.getWizardContext().setMonitorPassword(textFieldPassword.getValue());
			}
			
		});
		container.add(textFieldPassword);

		this.add(container);
	}
	
	protected String getProtocol() {
		if(httpRadio.getValue()){
			return "http";
		}
		else{
			return "https";
		}
	}
	
	
	public boolean validate(){
		return textFieldServer.validate() && textFieldUserName.validate()
			&& textFieldPassword.validate()&& textFieldPort.validate();
	}
	
	protected void populateFailoverJobScript(FailoverJobScript failoverScript){
		failoverScript.setVirtualType(VirtualizationType.HyperV);
		HyperV hyperV = (HyperV) failoverScript.getFailoverMechanism().get(0);

		hyperV.setHostName(textFieldServer.getValue());
		hyperV.setUserName(textFieldUserName.getValue());
		hyperV.setPassword(textFieldPassword.getValue());
	}
	
	protected void populateUI(FailoverJobScript failoverScript, ReplicationJobScript replicationScript){
		//HyperV hyperV = (HyperV)failoverScript.getFailoverMechanism().get(0);
		ARCFlashStorage storage = (ARCFlashStorage)replicationScript.getReplicationDestination().get(0);
		
		textFieldServer.setValue(storage.getHostName());
		textFieldUserName.setValue(storage.getUserName());
		textFieldPassword.setValue(storage.getPassword());
		if("http".equalsIgnoreCase(storage.getMonitorProtocol())){
			httpRadio.setValue(true);
		}
		else {
			httpsRadio.setValue(true);
		}
		textFieldPort.setValue(storage.getMonitorPort());
		
	}

	public void populateReplicationJobScript(ReplicationJobScript replicationScript) {
		replicationScript.getReplicationDestination().add(new ARCFlashStorage());
		ARCFlashStorage storage = (ARCFlashStorage)replicationScript.getReplicationDestination().get(0);
		storage.setHostName(textFieldServer.getValue());
		//storage.setNetworkThrottlingInKB(textFieldNetworkThrottling.getValue().intValue());
		//storage.setPort(textFieldPort.getValue().intValue());
		//storage.setPath(textFieldDataLocation.getValue());
		storage.setDesCompressType(1);
		storage.setDestProtocol(Protocol.HeartBeatMonitor);
		
		storage.setUserName(textFieldUserName.getValue());
		storage.setPassword(textFieldPassword.getValue());
		storage.setMonitorPort(textFieldPort.getValue().intValue());
		storage.setMonitorProtocol(getProtocol());
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

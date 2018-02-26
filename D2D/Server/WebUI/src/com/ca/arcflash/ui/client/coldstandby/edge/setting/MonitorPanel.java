package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMwareESXStorage;
import com.ca.arcflash.jobscript.replication.VMwareVirtualCenterStorage;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class MonitorPanel extends LayoutContainer{

	private static final int WIDTH_TEXTFIELD = 200;
	private static final int DEFAULT_PORT = 8014;
	private static final String DEFAULT_USERNAME = "Administrator";

	LayoutContainer inputContainer;
	LayoutContainer messageContainer;
	TextField<String> textFieldMonitorServer;
	TextField<String> textFieldUserName;
	PasswordTextField textFieldPassword;
	Radio httpRadio;
	Radio httpsRadio;
	NumberField textFieldPort;
	CheckBox checkBoxProxy;
	Text messageText;
	
	public MonitorPanel(){

		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(4);
		tl.setCellPadding(4);
		//tl.setCellSpacing(4);
		container.ensureDebugId("57d0cc09-a80a-4226-8df0-39b897df7534");
		container.setLayout(tl);

		
		Label titleLabel = new Label();
		titleLabel.ensureDebugId("43ac9276-f7b9-4b13-a39b-fd50378924f0");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants
				.coldStandbySettingStandinMonitorServer());
		container.add(titleLabel);

		textFieldMonitorServer = new TextField<String>();
		textFieldMonitorServer
				.ensureDebugId("aae2d710-90a5-4480-a042-780e996ecb11");
		textFieldMonitorServer.setAllowBlank(false);
		textFieldMonitorServer.setWidth(WIDTH_TEXTFIELD);
		Utils.addToolTip(textFieldMonitorServer, UIContext.Messages.coldStandbySettingMonitorHostTip(UIContext.productNameD2D));
		container.add(textFieldMonitorServer);

		titleLabel = new Label();
		titleLabel.ensureDebugId("4e5c0314-a531-4233-a388-b5e2f4c85336");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants
				.coldStandbySettingStandinProtocol());
		container.add(titleLabel);

		HorizontalPanel protocolPanel = new HorizontalPanel();
		protocolPanel.ensureDebugId("214edda3-2b72-4fe9-be3f-0f9413173c23");
		protocolPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		RadioGroup rgProtocol = new RadioGroup();
		httpRadio = new Radio(); //new RadioButton("MonitorProtocol");
		rgProtocol.add(httpRadio);
//		httpRadio.setStyleName("panel-text-value");
		httpRadio.ensureDebugId("d629d5d3-8f30-480a-b86a-6b592f890c72");
		httpRadio.getElement().getStyle().setPaddingLeft(0, Unit.PX);
		httpRadio.setBoxLabel(UIContext.Constants
				.coldStandbySettingStandinProtocolHTTP());
		httpRadio.setValue(true);
		httpRadio.setTitle(UIContext.Messages.coldStandbySettingHyperVProtocolTip(UIContext.productNameD2D));

		httpsRadio = new Radio(); //new RadioButton("MonitorProtocol");
		rgProtocol.add(httpsRadio);
//		httpsRadio.setStyleName("panel-text-value");
		httpsRadio.ensureDebugId("9945d11f-fae6-4559-a894-e930b3e2085d");
		httpsRadio.setBoxLabel(UIContext.Constants
				.coldStandbySettingStandinProtocolHTTPS());
		httpsRadio.setTitle(UIContext.Messages.coldStandbySettingHyperVProtocolTip(UIContext.productNameD2D));

		protocolPanel.add(httpRadio);
		protocolPanel.add(httpsRadio);
		container.add(protocolPanel);
		
		
		titleLabel = new Label();
		titleLabel.ensureDebugId("41124a47-91e6-4423-857f-feea2bf3d1f0");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants
				.coldStandbySettingStandinUserName());
		container.add(titleLabel);

		textFieldUserName = new TextField<String>();
		textFieldUserName.ensureDebugId("a8a9a8c3-d621-4253-9db9-0b8982ad9379");
		textFieldUserName.setAllowBlank(false);
		textFieldUserName.setWidth(WIDTH_TEXTFIELD);
		textFieldUserName.setValue(DEFAULT_USERNAME);
		Utils.addToolTip(textFieldUserName, UIContext.Constants.coldStandbySettingHyperVUsernameTip());
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
		textFieldPort.setAllowBlank(false);
		textFieldPort.setRegex("[1-9][0-9]*");
		textFieldPort.getMessages().setRegexText(UIContext.Constants.coldStandbySettingInvalidInteger());
		textFieldPort.setWidth(80);
		textFieldPort.setValue(DEFAULT_PORT);
		Utils.addToolTip(textFieldPort, UIContext.Messages.coldStandbySettingHyperVPortTip(UIContext.productNameD2D));
		container.add(textFieldPort);

		titleLabel = new Label();
		titleLabel.ensureDebugId("e40fefd0-9729-49ef-b6df-32eaa4ad6fae");
		titleLabel.setStyleName("setting-text-label");
		titleLabel.setText(UIContext.Constants.coldStandbySettingStandinPassword());
		container.add(titleLabel);
		
		textFieldPassword = new PasswordTextField();
		textFieldPassword.ensureDebugId("ef67f2a4-03c1-4713-ab64-28d433818860");
		textFieldPassword.setAllowBlank(false);
		textFieldPassword.setPassword(true);
		textFieldPassword.setWidth(WIDTH_TEXTFIELD);
		Utils.addToolTip(textFieldPassword, UIContext.Constants.coldStandbySettingHyperVPasswordTip());
		TableData td = new TableData();
		td.setColspan(3);
		container.add(textFieldPassword,td);

		checkBoxProxy = new CheckBox();
		checkBoxProxy.setBoxLabel(UIContext.Constants.coldStandbySettingStandinProxy());
		Utils.addToolTip(checkBoxProxy, UIContext.Constants.coldStandbySettingStandinVDDK());
		checkBoxProxy.setVisible(true);
		checkBoxProxy.setValue(true);
		checkBoxProxy.setStyleName("panel-text-value");
		checkBoxProxy.ensureDebugId("daffed19-56b1-45a3-a6d5-e6750529f3b1");
		TableData tableData = new TableData();
		tableData.setColspan(4);
		container.add(checkBoxProxy, tableData);
		
		this.inputContainer = container;
		this.add(inputContainer);
		
		this.messageContainer = new LayoutContainer();
		this.messageContainer.ensureDebugId( "29674023-ed35-40b2-abef-ac6429084615" );
		this.messageContainer.setLayout( new RowLayout( Orientation.VERTICAL ) );
		this.messageText = new Text( UIContext.Constants.coldStandbySettingVirtualizationMonitorSameAsServerSettings() );
		this.messageContainer.add( this.messageText, new RowData( 1, 1, new Margins( 5, 5, 5, 5 ) ) );
		this.messageContainer.setVisible( false );

		this.add( messageContainer );
	}
	
	public void setInputPanelVisible( boolean isVisible )
	{
		this.inputContainer.setVisible( isVisible );
	}
	
	public void setMessagePanelVisiable( boolean isVisible, VirtualizationType virtualType )
	{
		switch (virtualType)
		{
		case VMwareESX:
		case VMwareVirtualCenter:
			this.messageText.setText(
				UIContext.Constants.coldStandbySettingVirtualizationMonitorIsTheConverter() );
			break;
			
		case HyperV:
			this.messageText.setText(
				UIContext.Constants.coldStandbySettingVirtualizationMonitorSameAsServerSettings() );
			break;
			
		default:
			this.messageText.setText( "" );
			break;
		}
		
		this.messageContainer.setVisible( isVisible );
	}
	
	public void setESXProxyVisible(boolean status){
		checkBoxProxy.setVisible(status);
	}
	
	protected boolean validate() {
		if ("localhost".equalsIgnoreCase(textFieldMonitorServer.getValue()) || "127.0.0.1".equalsIgnoreCase(textFieldMonitorServer.getValue())) {			
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.ERROR);
			msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameVCM));
			msg.setMessage(UIContext.Constants.vcmMonitorNameAlert());
			msg.setModal(true);
			msg.show();
			return false;
		} else {			
			return textFieldMonitorServer.validate()&&textFieldUserName.validate() 
							&& textFieldPassword.validate() && textFieldPort.validate();
		}
	}
	
	protected String getProtocol() {
		if(httpRadio.getValue()){
			return "http";
		}
		else{
			return "https";
		}
	}
	
	protected void setProtocol(boolean isHttp) {
		if(isHttp){
			httpRadio.setValue(true);
		}
		else {
			httpsRadio.setValue(true);
		}
	}
	
	protected void populateUI(HeartBeatJobScript heartBeatJobScript, ReplicationJobScript replicationJobScript) {
		if(heartBeatJobScript == null) {
			return;
		}
		textFieldMonitorServer.setValue(heartBeatJobScript.getHeartBeatMonitorHostName());
		textFieldUserName.setValue(heartBeatJobScript.getHeartBeatMonitorUserName());
		textFieldPassword.setValue(heartBeatJobScript.getHeartBeatMonitorPassword());
		textFieldPort.setValue(heartBeatJobScript.getHeartBeatMonitorPort());
		if("http".equalsIgnoreCase(heartBeatJobScript.getHeartBeatMonitorProtocol())) {
			httpRadio.setValue(true);
		}
		else {
			httpsRadio.setValue(true);
		}
		
		if(replicationJobScript == null) {
			return;
		}
		if(replicationJobScript.getVirtualType() == VirtualizationType.VMwareESX) {
			VMwareESXStorage storage = (VMwareESXStorage)replicationJobScript.getReplicationDestination().get(0);
			checkBoxProxy.setValue(storage.isProxyEnabled());
		}
		else if(replicationJobScript.getVirtualType() == VirtualizationType.VMwareVirtualCenter) {
			VMwareVirtualCenterStorage storage = (VMwareVirtualCenterStorage)replicationJobScript.getReplicationDestination().get(0);
			checkBoxProxy.setValue(storage.isProxyEnabled());
		}
		
	}
	
	public VirtualizationHost getCurrentMonitor() {
		return new VirtualizationHost(textFieldMonitorServer.getValue().trim(), textFieldUserName.getValue().trim(),
				textFieldPassword.getValue(), textFieldPort.getValue().intValue(), getProtocol());
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		textFieldMonitorServer.setEnabled(enabled);
		textFieldUserName.setEnabled(enabled);
		textFieldPassword.setEnabled(enabled);
		textFieldPort.setEnabled(enabled);
		httpRadio.setEnabled(enabled);
		httpsRadio.setEnabled(enabled);
		checkBoxProxy.setEnabled(enabled);
	}
}

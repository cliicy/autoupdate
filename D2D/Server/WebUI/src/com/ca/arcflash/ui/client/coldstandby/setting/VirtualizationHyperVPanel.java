package com.ca.arcflash.ui.client.coldstandby.setting;

import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.HyperV;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.replication.ARCFlashStorage;
import com.ca.arcflash.jobscript.replication.Protocol;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.model.ConnectionProtocol;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;

class VirtualizationHyperVPanel extends Composite {
	private static final int WIDTH_TEXTFIELD	=	200;
	private static final int MAX_INPUT_LENGTH = 260;
	
	TextField<String> textFieldServer;
	TextField<String> textFieldUserName;
	PasswordTextField textFieldPassword;
	//NumberField textFieldPort;
	
	public VirtualizationHyperVPanel(){
		
		FlexTable serverSettingTable = new FlexTable();
		serverSettingTable.ensureDebugId("6216553f-3da3-4984-be81-e815ee1218e9"); //$NON-NLS-1$
		serverSettingTable.setCellPadding(4);
		serverSettingTable.setCellSpacing(4);
		
		Label titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");  //$NON-NLS-1$
		titleLabel.setText(UIContext.Constants.coldStandbySettingVirtualizationServerNameHyperV()); 
		serverSettingTable.setWidget(0, 0, titleLabel);
		serverSettingTable.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		
		textFieldServer = new TextField<String>();
		textFieldServer.ensureDebugId("e940b310-aad7-4fd2-a5bb-4f81ba4d8a45"); //$NON-NLS-1$
		textFieldServer.setAllowBlank(false);
		textFieldServer.setWidth(WIDTH_TEXTFIELD);
		textFieldServer.setMaxLength(MAX_INPUT_LENGTH);
		serverSettingTable.setWidget(0, 1, textFieldServer);
		serverSettingTable.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		
		//set user name
		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label"); 
		titleLabel.setText(UIContext.Constants.coldStandbySettingVirtualizationUserName()); 
		serverSettingTable.setWidget(1, 0, titleLabel);
		serverSettingTable.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		
		textFieldUserName = new TextField<String>();
		textFieldUserName.ensureDebugId("92a49667-b1fc-4bc1-8dab-b014d166877e");
		textFieldUserName.setAllowBlank(false);
		textFieldUserName.setWidth(WIDTH_TEXTFIELD);
		textFieldUserName.setMaxLength(MAX_INPUT_LENGTH);
		
		serverSettingTable.setWidget(1, 1, textFieldUserName);
		serverSettingTable.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		
		//set password
		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label"); 
		titleLabel.setText(UIContext.Constants.coldStandbySettingVirtualizationPassword()); 
		serverSettingTable.setWidget(2, 0, titleLabel);
		serverSettingTable.getCellFormatter().setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		
		textFieldPassword = new PasswordTextField();
		textFieldPassword.ensureDebugId("2fbd563c-0fa3-4c1c-9632-f1c416289b02");
		textFieldPassword.setAllowBlank(false);
		textFieldPassword.setWidth(WIDTH_TEXTFIELD);
		textFieldPassword.setPassword(true);
		textFieldPassword.setMaxLength(MAX_INPUT_LENGTH);
		serverSettingTable.setWidget(2, 1, textFieldPassword);
		serverSettingTable.getCellFormatter().setVerticalAlignment(2, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		
		/*titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label");  //$NON-NLS-1$
		titleLabel.setText(UIContext.Constants.coldStandbySettingVirtualizationDataTransferPort());  //$NON-NLS-1$
		serverSettingTable.setWidget(3, 0, titleLabel);
		serverSettingTable.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
		
		textFieldPort = new NumberField();
		textFieldPort.ensureDebugId("15fcc70e-9b71-4649-8e6c-2674d042cddd"); //$NON-NLS-1$
		textFieldPort.setAllowBlank(false);
		textFieldPort.setAllowDecimals(false);
		textFieldPort.setAllowNegative(false);
		textFieldPort.setWidth(WIDTH_TEXTFIELD);
		textFieldPort.setValue(4090);
		serverSettingTable.setWidget(3, 1, textFieldPort);
		serverSettingTable.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_MIDDLE);*/
		
		this.initWidget(serverSettingTable);
	}
	
	private ConnectionProtocol getProtocol(){
		return ConnectionProtocol.HTTP;
		
		/*if (httpRadio.getValue())
			return ConnectionProtocol.HTTP;
		else
			return ConnectionProtocol.HTTPS;*/
	}
	
	public boolean validate(){
		return textFieldServer.validate() && textFieldUserName.validate()
			&& textFieldPassword.validate();
	}
	
	protected void populateFailoverJobScript(FailoverJobScript failoverScript){
		failoverScript.setVirtualType(VirtualizationType.HyperV);
	}
	
	protected void populateUI(FailoverJobScript failoverScript, ReplicationJobScript replicationScript){
		HyperV hyperV = (HyperV)failoverScript.getFailoverMechanism().get(0);
		ARCFlashStorage storage = (ARCFlashStorage)replicationScript.getReplicationDestination().get(0);
		
		textFieldServer.setValue(hyperV.getHostName().trim());
		textFieldUserName.setValue(hyperV.getUserName().trim());
		textFieldPassword.setValue(hyperV.getPassword());
		//textFieldPort.setValue(storage.getPort());
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
	}
}

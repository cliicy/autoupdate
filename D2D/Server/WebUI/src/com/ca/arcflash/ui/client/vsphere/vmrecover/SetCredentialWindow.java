package com.ca.arcflash.ui.client.vsphere.vmrecover;


import java.util.ArrayList;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyServiceAsync;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.DiskModel;
import com.ca.arcflash.ui.client.model.VMBackupSettingModel;
import com.ca.arcflash.ui.client.model.VMStorage;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.ca.arcflash.ui.client.restore.FSRestoreOptionsPanel;
import com.ca.arcflash.ui.client.restore.RestoreContext;
import com.ca.arcflash.ui.client.restore.RestoreOptionsPanel;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

public class SetCredentialWindow extends Window {
	
	final LoginServiceAsync service = GWT.create(LoginService.class);
	
	private SetCredentialWindow thisWindow;
	
	private RestoreOptionsPanel parent;
	
	private TextField<String> vcTextField;
	private TextField<String> vmNameTextField;
	private Radio httpProtocol;
	private Radio httpsProtocol;
	private RadioGroup protocolGroup;
	private NumberField portTextField = new NumberField();
	
	private TextField<String> usernameField;
	
	private PasswordTextField passwordField;
	
	private TextField<String> vmUsername;
	
	private PasswordTextField vmPassword;
	
	private KeyNav<ComponentEvent> enterKey;
	
	private Button okButton;
	
	private Button cancelButton;
	
	private int MIN_BUTTON_WIDTH = 90; 
	
	private boolean isBrowse = false;
	
	private PathSelectionPanel pathSelectionForOtherVM;
	
	public SetCredentialWindow(RestoreOptionsPanel parentPanel,final AsyncCallback<Boolean> callback,boolean isBrowse){
		this.thisWindow = this;
		this.parent = parentPanel;
		this.setWidth(350);
		this.isBrowse = isBrowse;
		portTextField.ensureDebugId("1B4603E5-D110-4290-A2D5-91C6A30A8B36");

		enterKey = new KeyNav<ComponentEvent>(this) {
			public void handleEvent(ComponentEvent ce) {
				if (ce.getKeyCode() == 13)
					okButton.fireEvent(Events.Select);
				else if (ce.getKeyCode() == 27)
					cancelButton.fireEvent(Events.Select);
			}
		};
		
		this.setHeadingHtml(UIContext.Constants.setCredentialForOriginalLocation());
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellPadding(4);
		layout.setWidth("95%");
		this.setLayout(layout);
		
		TableData tb = new TableData();
		tb.setWidth("30%");
		tb.setHorizontalAlign(HorizontalAlignment.RIGHT);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryVirtualCenterLabel());
		this.add(label, tb);
		
		tb = new TableData();
		tb.setWidth("70%");
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		vcTextField = new TextField<String>();
		vcTextField.setReadOnly(true);
		vcTextField.disable();
		vcTextField.setWidth("100%");
		vcTextField.setValue(parentPanel.getBackupVMModel().getEsxServerName());
		this.add(vcTextField, tb);
		
		
		tb = new TableData();
		tb.setWidth("30%");
		tb.setHorizontalAlign(HorizontalAlignment.RIGHT);
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryVMNameLabel());
		this.add(label, tb);
		
		tb = new TableData();
		tb.setWidth("70%");
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		vmNameTextField = new TextField<String>();
		vmNameTextField.ensureDebugId("D87BA2D9-2366-4324-9C9C-05FBC77982C6");
		vmNameTextField.setReadOnly(true);
		vmNameTextField.disable();
		vmNameTextField.setWidth("100%");
		vmNameTextField.setValue(parentPanel.getBackupVMModel().getVMName());
		this.add(vmNameTextField, tb);
		
		tb = new TableData();
		tb.setWidth("30%");
		tb.setHorizontalAlign(HorizontalAlignment.RIGHT);
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryProtocolLabel());
		this.add(label, tb);
		
		tb = new TableData();
		tb.setWidth("70%");
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		LayoutContainer protocolContainer = new LayoutContainer();
		TableLayout protocolTL = new TableLayout();
		protocolTL.setColumns(2);
		protocolContainer.setLayout(protocolTL);
		
		TableData proData = new TableData();
		
		httpProtocol = new Radio();
		httpProtocol.ensureDebugId("AAAE800F-17F1-4e27-AF1C-6CB9D2AADC93");
		httpProtocol.setBoxLabel(UIContext.Constants.vmRecoveryProtocolHttp());
		httpProtocol.addListener(Events.Change, new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				if(httpProtocol.getValue()==true){
					portTextField.setValue(80);
				}
			}
		});
		
		protocolContainer.add(httpProtocol, proData);
		httpsProtocol = new Radio();
		httpsProtocol.ensureDebugId("A220A70E-B86E-41bf-AF3A-67E224048004");
		httpsProtocol.setBoxLabel(UIContext.Constants.vmRecoveryProtocolHttps());
		httpsProtocol.addListener(Events.Change, new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				if(httpsProtocol.getValue()==true){
					portTextField.setValue(443);
				}
			}
		});
		protocolContainer.add(httpsProtocol, proData);
		
		protocolGroup = new RadioGroup();
		protocolGroup.add(httpProtocol);
		protocolGroup.add(httpsProtocol);
		
		if(parentPanel.getBackupVMModel().getProtocol()!=null && parentPanel.getBackupVMModel().getProtocol().equalsIgnoreCase("http")){
			protocolGroup.setValue(httpProtocol);
		}else{
			protocolGroup.setValue(httpsProtocol);
		}
		
		this.add(protocolContainer, tb);
		
		tb = new TableData();
		tb.setWidth("30%");
		tb.setHorizontalAlign(HorizontalAlignment.RIGHT);
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryPortLabel());
		this.add(label, tb);
		
		tb = new TableData();
		tb.setWidth("70%");
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		//portTextField = new NumberField();
		//portTextField.setReadOnly(true);
		//portTextField.disable();
		portTextField.setAllowBlank(false);
		portTextField.setWidth("100%");
		portTextField.setValue(parentPanel.getBackupVMModel().getPort());
		this.add(portTextField, tb);
		
		tb = new TableData();
		tb.setWidth("30%");
		tb.setHorizontalAlign(HorizontalAlignment.RIGHT);
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryUsernameLabel());
		this.add(label, tb);
		
		tb = new TableData();
		tb.setWidth("70%");
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		usernameField = new TextField<String>();
		usernameField.ensureDebugId("3F2684D5-710A-4df0-974F-A538B8F5FA2A");
		usernameField.setAllowBlank(false);
		usernameField.setWidth("100%");
		this.add(usernameField, tb);
		
		tb = new TableData();
		tb.setWidth("30%");
		tb.setHorizontalAlign(HorizontalAlignment.RIGHT);
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryPasswordLabel());
		this.add(label, tb);
		
		tb = new TableData();
		tb.setWidth("70%");
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		passwordField = new PasswordTextField();
		passwordField.ensureDebugId("89554DC6-0327-4d80-8BA9-03394893694A");
		passwordField.setPassword(true);
		//passwordField.setAllowBlank(false);
		passwordField.setWidth("100%");
		this.add(passwordField, tb);
		
		if(!(parent instanceof VMRecoveryOptionsPanel)){
			tb = new TableData();
			tb.setWidth("30%");
			tb.setHorizontalAlign(HorizontalAlignment.RIGHT);
			label = new LabelField();
			label.setValue(UIContext.Constants.vmRecoveryVMUserName());
			this.add(label, tb);
			
			tb = new TableData();
			tb.setWidth("70%");
			tb.setHorizontalAlign(HorizontalAlignment.LEFT);
			vmUsername = new TextField<String>();
			vmUsername.setAllowBlank(false);
			vmUsername.setWidth("100%");
			this.add(vmUsername, tb);
			
			tb = new TableData();
			tb.setWidth("30%");
			tb.setHorizontalAlign(HorizontalAlignment.RIGHT);
			label = new LabelField();
			label.setValue(UIContext.Constants.vmRecoveryVMPassword());
			this.add(label, tb);
			
			tb = new TableData();
			tb.setWidth("70%");
			tb.setHorizontalAlign(HorizontalAlignment.LEFT);
			vmPassword = new PasswordTextField();
			vmPassword.setPassword(true);
			//passwordField.setAllowBlank(false);
			vmPassword.setWidth("100%");
			this.add(vmPassword, tb);
		}
		if(isBrowse){
			vmUsername.addListener(Events.KeyUp, new Listener<BaseEvent>() {

				@Override
				public void handleEvent(BaseEvent be) {
					if(vmUsername.getValue()==null || vmUsername.getValue().length()==0){
						pathSelectionForOtherVM.setEnabled(false);
					}else{
						pathSelectionForOtherVM.setEnabled(true);
					}
				}
				
			});
			tb = new TableData();
			tb.setWidth("30%");
			tb.setHorizontalAlign(HorizontalAlignment.RIGHT);
			label = new LabelField();
			label.setValue(UIContext.Constants.Destination()+":");
			this.add(label, tb);
			
			tb = new TableData();
			tb.setWidth("70%");
			tb.setHorizontalAlign(HorizontalAlignment.LEFT);
			pathSelectionForOtherVM = new PathSelectionPanel(null,(FSRestoreOptionsPanel)parent);
			pathSelectionForOtherVM.setWidth("100%");
			pathSelectionForOtherVM.setMode(PathSelectionPanel.RESTORE_ALT_VM_MODE);
			pathSelectionForOtherVM
					.setTooltipMode(PathSelectionPanel.TOOLTIP_RESTORE_ALT_VM_MODE);
			//pathSelection.setAllowBlank(false);
			pathSelectionForOtherVM.setPathFieldLength(128);
			pathSelectionForOtherVM.setEnabled(false);
			this.add(pathSelectionForOtherVM,tb);
			
		}
		this.add(new HTML(""));
		
		layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellSpacing(5);
		LayoutContainer container = new LayoutContainer();
		container.setLayout(layout);
		
		tb = new TableData();
		tb.setWidth("70%");
		tb.setHorizontalAlign(HorizontalAlignment.RIGHT);
		this.add(container,tb);
		okButton = new Button();
		okButton.ensureDebugId("59AC0E8C-D4DD-4d8c-8ADC-8E733F8EA1C1");
		okButton.setText(UIContext.Constants.ok());
		okButton.setMinWidth(MIN_BUTTON_WIDTH);
		container.add(okButton, new TableData(Style.HorizontalAlignment.RIGHT,
				Style.VerticalAlignment.MIDDLE));
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!usernameField.isValid()){
					return;
				}
				if(!passwordField.isValid()){
					return;
				}
				if(!portTextField.isValid()){
					return;
				}
				if(!(parent instanceof VMRecoveryOptionsPanel)){
					if(!vmUsername.isValid()){
						return;
					}
					if(!vmPassword.isValid()){
						return;
					}
				}
				
				final String username = usernameField.getValue();
				final String password = passwordField.getValue();
				final int port = portTextField.getValue().intValue();
				final String protocol = httpsProtocol.getValue()==true?"https":"http";
				final VirtualCenterModel vcModel = new VirtualCenterModel();
				vcModel.setPassword(password);
				vcModel.setPort(port);
				vcModel.setProtocol(protocol);
				vcModel.setUsername(username);
				vcModel.setVcName(parent.getBackupVMModel().getEsxServerName());
				thisWindow.mask(UIContext.Constants.connectToVC());
				if(!(parent instanceof VMRecoveryOptionsPanel)){
					service.validateVC(vcModel, new BaseAsyncCallback<Integer>(){
						@Override
						public void onFailure(Throwable caught){
							super.onFailure(caught);
							callback.onSuccess(false);
							thisWindow.unmask();
						}
						@Override
						public void onSuccess(Integer result){
							if(result!=0){
								MessageBox msg = new MessageBox();
								msg.setIcon(MessageBox.ERROR);
								msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
								msg.setMessage(UIContext.Constants.messageBoxConnectVCFail());
								msg.setModal(true);
								msg.show();
								callback.onSuccess(false);
								thisWindow.unmask();
							}else{
								parent.getBackupVMModel().setEsxUsername(username);
								parent.getBackupVMModel().setEsxPassword(password);
								parent.getBackupVMModel().setPort(port);
								parent.getBackupVMModel().setProtocol(protocol);
								parent.getBackupVMModel().setUsername(vmUsername.getValue());
								parent.getBackupVMModel().setPassword(vmPassword.getValue());
								if(thisWindow.isBrowse){
									parent.getBackupVMModel().setDestination(pathSelectionForOtherVM.getDestination());
								}
								thisWindow.unmask();
								parent.validate();
								thisWindow.hide();
							}
						}
					});
				}else{
					final BackupVMModel backupVMModel = parent.getBackupVMModel();
					
					service.validateRecoveryVMToOriginal(vcModel, parent.getBackupVMModel(), RestoreContext.getRecoveryPointModel().getSessionID(), new BaseAsyncCallback<VMStorage[]>(){
						@Override
						public void onFailure(Throwable caught){
							super.onFailure(caught);
							callback.onSuccess(false);
							thisWindow.unmask();
						}
						@Override
						public void onSuccess(VMStorage[] result){
							if (!parent.doesOverwriteVM())
								checkVMNameExist(vcModel, backupVMModel, result, callback);
							else
								validateDataStore(backupVMModel, result, username, password, port, protocol, callback);
						}
					});
				}
				
			}
			
		});
		
		cancelButton = new Button();
		cancelButton.ensureDebugId("9D4EB3A8-91A6-4d72-8F83-DAE835B5A789");
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.setMinWidth(MIN_BUTTON_WIDTH);
		container.add(cancelButton, new TableData(Style.HorizontalAlignment.RIGHT,
				Style.VerticalAlignment.MIDDLE));

		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.hide();
			}

		});		
	}
	
	private void checkVMNameExist(final VirtualCenterModel vcModel, final BackupVMModel backupVMModel,
					              final VMStorage[] vmStorateResult, 
		                          final AsyncCallback<Boolean> callback)
	{
		final String vmName = vmNameTextField.getValue();
		final String username = usernameField.getValue();
		final String password = passwordField.getValue();
		final int port = portTextField.getValue().intValue();
		final String protocol = httpsProtocol.getValue()==true?"https":"http";
		String vcName = vcModel.getVcName();
		
		final ColdStandbyServiceAsync coldStandbyService = GWT.create(ColdStandbyService.class);
		
		final String esxName = backupVMModel.getSubVMEsxHost();
		
		coldStandbyService.isVMWareVMNameExist(
				vcName,
				username, 
				password,
				protocol,
				true,
				port,
				esxName, null, vmName, new BaseAsyncCallback<Boolean>(){
					@Override
					public void onFailure(Throwable caught) {
						MessageBox mb = new MessageBox();
						mb.setIcon(MessageBox.ERROR);
						mb.setButtons(MessageBox.OK);
						mb.setMessage(UIContext.Messages.vmWareVMExists(vmName));
						mb.show();
						callback.onSuccess(false);
						thisWindow.unmask();
						thisWindow.hide();
					}

					@Override
					public void onSuccess(Boolean result) {
						if (result)
						{
							MessageBox mb = new MessageBox();
							mb.setIcon(MessageBox.ERROR);
							mb.setButtons(MessageBox.OK);
							mb.setMessage(UIContext.Messages.vmWareVMExists(vmName));
							mb.show();
							callback.onSuccess(false);
							thisWindow.unmask();
							thisWindow.hide();
						}
						else
							validateDataStore(backupVMModel, vmStorateResult, username, password, port,
 					                          protocol, callback);
						
					}
				});
	}
	
	private void validateDataStore(final BackupVMModel backupVMModel, VMStorage[] result, 
					               final String username, final String password, final int port,
 					               final String protocol, final AsyncCallback<Boolean> callback)
	{
		if(backupVMModel.diskList != null){
			boolean isDatastoreEnough = true;
			String datastores = "";
			for(VMStorage datastore : result){
				datastore.diskList = new ArrayList<DiskModel>();
			}
			for(int i=0; i < backupVMModel.diskList.size(); i++){
				DiskModel diskModel = backupVMModel.diskList.get(i);
				String dataStoreName = diskModel.getDiskDataStore().replaceAll("\\[", "").replaceAll("\\]", "");
				for(VMStorage datastore : result){
					if(datastore.getName().equals(dataStoreName)){
						datastore.diskList.add(diskModel);
						break;
					}
				}
			}
			for(VMStorage datastore : result){
				if(datastore.diskList!=null && datastore.diskList.size()>0){
					long dataStoreSize = datastore.getFreeSize();
					long totalDiskSize = 0L;
					for(DiskModel diskModel : datastore.diskList){
						totalDiskSize += diskModel.getSize();
					}
					if(dataStoreSize < totalDiskSize){
						isDatastoreEnough=false;
						datastores += datastore.getName()+",";
					}
					
				}
			}
			if(!isDatastoreEnough){
				datastores = datastores.substring(0, datastores.length()-1);
				String msg = UIContext.Messages.vSphereDataStoreNotEnough(datastores);
				String productName = UIContext.productNameD2D;
				if(UIContext.uiType == 1){
					productName = UIContext.productNamevSphere;
				}
				MessageBox mb = new MessageBox();
				mb.setIcon(MessageBox.WARNING);
				mb.setButtons(MessageBox.YESNO);
				mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(productName));
				mb.setMessage(msg);
				mb.addCallback(new Listener<MessageBoxEvent>()
				{
					public void handleEvent(MessageBoxEvent be)
					{
						if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
							parent.getBackupVMModel().setEsxUsername(username);
							parent.getBackupVMModel().setEsxPassword(password);
							parent.getBackupVMModel().setPort(port);
							parent.getBackupVMModel().setProtocol(protocol);
							thisWindow.unmask();
							parent.validate();
							thisWindow.hide();
						}
						else {
							callback.onSuccess(false);
							thisWindow.unmask();
							thisWindow.hide();
							
						}
							
					}
				});
				mb.show();
			}else{
				parent.getBackupVMModel().setEsxUsername(username);
				parent.getBackupVMModel().setEsxPassword(password);
				parent.getBackupVMModel().setPort(port);
				parent.getBackupVMModel().setProtocol(protocol);
				thisWindow.unmask();
				parent.validate();
				thisWindow.hide();
			}
		}else{
			parent.getBackupVMModel().setEsxUsername(username);
			parent.getBackupVMModel().setEsxPassword(password);
			parent.getBackupVMModel().setPort(port);
			parent.getBackupVMModel().setProtocol(protocol);
			thisWindow.unmask();
			parent.validate();
			thisWindow.hide();
		}	
	}
	public boolean isBrowse() {
		return isBrowse;
	}

	public void setBrowse(boolean isBrowse) {
		this.isBrowse = isBrowse;
	}
	
	public int getPort(){
		return portTextField.getValue().intValue();
	}
	public String getUsername(){
		return usernameField.getValue();
	}
	public String getPassword(){
		return passwordField.getValue();
	}
	
	public String getProtocol(){
		return httpsProtocol.getValue()==true?"https":"http";
	}

	public String getVMUsername(){
		return vmUsername.getValue();
	}
	
	public String getVMPassword(){
		return vmPassword.getValue();
	}
	
	private void getDefaultValue(){
		if(UIContext.backupVM != null && RestoreContext.getVMModel()!=null){
			BackupVMModel currentVM = UIContext.backupVM;
			BackupVMModel selectVM = RestoreContext.getVMModel();
			if(currentVM.getVmInstanceUUID().equals(selectVM.getVmInstanceUUID())){
				VMBackupSettingModel currentVMSetting = (VMBackupSettingModel)RestoreContext.getBackupModel();
				usernameField.setValue(currentVMSetting.getBackupVM().getEsxUsername());
				passwordField.setValue(currentVMSetting.getBackupVM().getEsxPassword());
				portTextField.setValue(currentVMSetting.getBackupVM().getPort());
				if(!isNullOrEmpty(currentVMSetting.getBackupVM().getUsername())){
					vmUsername.setValue(currentVMSetting.getBackupVM().getUsername());
					if(isBrowse){
						pathSelectionForOtherVM.setEnabled(true);
					}
				}
				if(!isNullOrEmpty(currentVMSetting.getBackupVM().getPassword())){
					vmPassword.setValue(currentVMSetting.getBackupVM().getPassword());
				}
			}
		}
	}
	
	private boolean isNullOrEmpty(String input){
		return input == null || input.equals("");
	}
	
	@Override
	protected void onRender(Element target, int index){
		super.onRender(target, index);
		getDefaultValue();
		this.setFocusWidget(usernameField);
		this.passwordField.focus();
		
	}
	
}

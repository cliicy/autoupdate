package com.ca.arcflash.ui.client.vsphere.vmrecover;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class HyperVConnectionPanel extends LayoutContainer {
	
	private TextField<String> hostField;
	private TextField<String> usernameField;
	private PasswordTextField passwordField;
	private BackupVMModel backupVM;
	private TextField<String> vmNameTextField;
	
	public HyperVConnectionPanel(BackupVMModel backupVM, HorizontalAlignment labelAlignment){
		this.backupVM = backupVM;
		
		TableLayout layout = new TableLayout(2);
		layout.setWidth("100%");
		setLayout(layout);
		
		TableData tb = new TableData();
		tb.setWidth("120");
		tb.setHorizontalAlign(labelAlignment);
		LabelField hostLabel = new LabelField(UIContext.Constants.hyperVClusterServerNameCaption());
		add(hostLabel, tb);
		
		tb = new TableData();
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		hostField = new TextField<String>();
		hostField.setAllowBlank(false);
		hostField.setWidth("180");
		add(hostField,tb);
		
		if (backupVM!=null){
			tb = new TableData();
			tb.setWidth("35%");
			tb.setHorizontalAlign(labelAlignment);
			LabelField vmLabel = new LabelField();
			vmLabel.setValue(UIContext.Constants.vmRecoveryVMNameLabel());
			add(vmLabel, tb);
			
			tb = new TableData();
			tb.setHorizontalAlign(HorizontalAlignment.LEFT);
			
			vmNameTextField = new TextField<String>();
			vmNameTextField.setStyleAttribute("padding-top", "6px");
			vmNameTextField.ensureDebugId("D87BA2D9-2366-4324-9C9C-05FBC77982C6");
			vmNameTextField.setReadOnly(true);
			vmNameTextField.setWidth("180");
			vmNameTextField.disable();
			
			add(vmNameTextField, tb);
		}
		
		tb = new TableData();
		tb.setHorizontalAlign(labelAlignment);
		LabelField usernameLabel = new LabelField(UIContext.Constants.UserName());
		add(usernameLabel,tb);
		
		tb = new TableData();
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		usernameField = new TextField<String>();
		usernameField.setAllowBlank(false);
		usernameField.setWidth("180");
		usernameField.setStyleAttribute("padding-top", "6px");
		add(usernameField, tb);
		
		tb = new TableData();

		tb.setHorizontalAlign(labelAlignment);
		LabelField passwordLabel = new LabelField(UIContext.Constants.Password());
		add(passwordLabel,tb);
		
		tb = new TableData();
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		passwordField = new PasswordTextField();
		passwordField.setPassword(true);
		passwordField.setWidth("180");
		passwordField.setStyleAttribute("padding-top", "6px");
		add(passwordField, tb);
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		if (backupVM!=null){
			hostField.setReadOnly(true);
			hostField.disable();
			hostField.setValue(backupVM.getEsxServerName());
			
			vmNameTextField.setValue(backupVM.getVMName());
			usernameField.setValue(backupVM.getEsxUsername());
		}
	}
	
	public String getHyperVHostName(){
		return hostField.getValue();
	}
	
	public String getHyperVUserName(){
		return usernameField.getValue();
	}
	
	public String getHyperVPassword(){
		return passwordField.getValue();
	}
	
	public void clearPassword(){
		passwordField.clear();
	}

	public TextField<String> getHostField() {
		return hostField;
	}

	public TextField<String> getUsernameField() {
		return usernameField;
	}
	
	public String getVMInstanceUUID()
	{
		if (backupVM != null)
		    return backupVM.getVmInstanceUUID();

		return "";
	}
	
	public String getVMName()
	{
		if (backupVM != null)
		    return backupVM.getVMName();
		
		return "";
	}
	
	public boolean validate(){
		if (!hostField.validate() || !usernameField.validate() || !passwordField.validate())
			return false;
		return true;
	}
}

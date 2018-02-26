package com.ca.arcflash.ui.client.coldstandby.setting;

import com.ca.arcflash.jobscript.failover.VMwareNetworkAdapter;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyServiceAsync;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WMwareNetworkAdapterWindow extends Dialog {
	private final ColdStandbyServiceAsync service = GWT.create(ColdStandbyService.class);

	private static final int WIDTH_WINDOW		=	500;
	
	private Dialog thisDialog;
	private TextField<String> fieldNetworkAdapterName;
	private BaseSimpleComboBox<String> comboAdapterType;
	private BaseSimpleComboBox<String> comboNetworkConnection;
	private TextField<String> macAddress;
	private TextField<String> ipAddress;
	private TextField<String> subnetMaskAddress;
	private TextField<String> gatewayAddress;
	private TextField<String> preferredNDSAddress;
	private TextField<String> alternateDNSAddress;
	private CheckBox checkBoxConnectPowerOn;
	private CheckBox checkBoxConnected;
	private FormData formData = new FormData("-20");
	
	private boolean hasEverTriedToConnectVM;
	private VMwareNetworkAdapter adapter;
	private FieldSet macAddressFieldSet;
	private FieldSet ipAddressFieldSet;
	private Type type;
	private boolean isValid;
	private IPValidator ipValidator = new IPValidator();
	private MACValidator macValidator = new MACValidator();
	
	public enum Type{
		Add, Edit
	}
	
	public WMwareNetworkAdapterWindow() {
		this.thisDialog = this;
		this.setHeadingHtml("Add/Modify Network Adapter");
		this.setWidth(WIDTH_WINDOW);
		this.setResizable(false);
		this.setShadow(false);
		this.setAutoHeight(true);
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setWidth("100%");
		
		verticalPanel.add(setupBasicInformationPart());
		verticalPanel.add(setupMACAddressPart());
		verticalPanel.add(setupIPAddressPart());
		verticalPanel.add(setupDevicePart());
		
		this.add(verticalPanel);
		this.setButtons(Dialog.OKCANCEL);
		this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				handleOKButton();
			}
		});
		this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				thisDialog.hide();
			}
		});
	}
	
	@Override
	public void render(Element target, int index) {
		super.render(target, index);
		this.getBody().setStyleAttribute("padding", "4px");
	}

	private Widget setupBasicInformationPart() {
		FormLayout formLayout = new FormLayout();
		formLayout.setLabelWidth(150);
		
		FieldSet fieldSet = new FieldSet();
		fieldSet.setHeadingHtml("Basic Information");
		fieldSet.setLayout(formLayout);
		
		fieldNetworkAdapterName = new TextField<String>();
		fieldNetworkAdapterName.setValidateOnBlur(false);
		fieldNetworkAdapterName.setAllowBlank(false);
		fieldNetworkAdapterName.setFieldLabel("Adapter Name");
		fieldNetworkAdapterName.setStyleAttribute("padding-right", "6px");
		fieldSet.add(fieldNetworkAdapterName, formData);
		
		comboAdapterType = new BaseSimpleComboBox<String>();
		comboAdapterType.setStore(new ListStore<SimpleComboValue<String>>());
		comboAdapterType.setAllowBlank(false);
		comboAdapterType.setFieldLabel("Adapter Type");
		fieldSet.add(comboAdapterType, formData);
		
		comboNetworkConnection = new BaseSimpleComboBox<String>();
		comboNetworkConnection.setAllowBlank(false);
		comboNetworkConnection.setFieldLabel("Network Connection");
		fieldSet.add(comboNetworkConnection, formData);
		
		return fieldSet;
	}
	
	private Widget setupMACAddressPart() {
		FormLayout formLayout = new FormLayout();
		formLayout.setLabelWidth(150);
		
		macAddressFieldSet = new FieldSet();
		macAddressFieldSet.setCheckboxToggle(true);
		macAddressFieldSet.setHeadingHtml("Specify MAC Address by manual");
		macAddressFieldSet.setLayout(formLayout);
		
		macAddress = new TextField<String>();
		macAddress.setValidator(macValidator);
		macAddress.setAllowBlank(false);
		macAddress.setMaxLength(17);
		macAddress.setFieldLabel("MAC Address");
		macAddressFieldSet.add(macAddress, formData);
		
		return macAddressFieldSet;
	}
	
	private Widget setupIPAddressPart() {
		FormLayout formLayout = new FormLayout();
		formLayout.setLabelWidth(150);
		
		ipAddressFieldSet = new FieldSet();
		ipAddressFieldSet.setCheckboxToggle(true);
		ipAddressFieldSet.setHeadingHtml("Specify IP Address by manual");
		ipAddressFieldSet.setLayout(formLayout);
		
		ipAddress = new TextField<String>();
		ipAddress.setValidator(ipValidator);
		ipAddress.setFieldLabel("IP Address");
		ipAddress.setAllowBlank(false);
		ipAddressFieldSet.add(ipAddress, formData);
		
		subnetMaskAddress = new TextField<String>();
		subnetMaskAddress.setValidator(ipValidator);
		subnetMaskAddress.setFieldLabel("Subnet mask");
		subnetMaskAddress.setAllowBlank(false);
		ipAddressFieldSet.add(subnetMaskAddress, formData);
		
		gatewayAddress = new TextField<String>();
		gatewayAddress.setValidator(ipValidator);
		gatewayAddress.setFieldLabel("Default gateway");
		gatewayAddress.setAllowBlank(false);
		ipAddressFieldSet.add(gatewayAddress, formData);
		
		preferredNDSAddress = new TextField<String>();
		preferredNDSAddress.setValidator(ipValidator);
		preferredNDSAddress.setFieldLabel("Preferred DNS");
		preferredNDSAddress.setAllowBlank(false);
		ipAddressFieldSet.add(preferredNDSAddress, formData);
		
		alternateDNSAddress = new TextField<String>();
		alternateDNSAddress.setValidator(ipValidator);
		alternateDNSAddress.setFieldLabel("Alternate DNS");
		alternateDNSAddress.setAllowBlank(false);
		ipAddressFieldSet.add(alternateDNSAddress, formData);
		
		return ipAddressFieldSet;
	}
	
	private Widget setupDevicePart() {
		FieldSet fieldSet = new FieldSet();
		fieldSet.setHeadingHtml("Device Status");
		fieldSet.setLayout(new FlowLayout());
		
		checkBoxConnected = new CheckBox();
		checkBoxConnected.setBoxLabel("Connected");
		fieldSet.add(checkBoxConnected);
		
		checkBoxConnectPowerOn = new CheckBox();
		checkBoxConnectPowerOn.setBoxLabel("Connect at power on");
		fieldSet.add(checkBoxConnectPowerOn);
		
		return fieldSet;
	}

	
	
	@Override
	protected void afterShow() {
		super.afterShow();
		
		isValid = false;
		if (type == Type.Add){
			this.fieldNetworkAdapterName.enable();
			this.fieldNetworkAdapterName.setValue(null);
			this.fieldNetworkAdapterName.clearInvalid();
			this.macAddressFieldSet.collapse();
			this.macAddress.setValue(null);
			this.macAddress.clearInvalid();
			this.ipAddressFieldSet.collapse();
			this.ipAddress.setValue(null);
			this.ipAddress.clearInvalid();
			this.subnetMaskAddress.setValue(null);
			this.subnetMaskAddress.clearInvalid();
			this.gatewayAddress.setValue(null);
			this.gatewayAddress.clearInvalid();
			this.preferredNDSAddress.setValue(null);
			this.preferredNDSAddress.clearInvalid();
			this.alternateDNSAddress.setValue(null);
			this.alternateDNSAddress.clearInvalid();
			this.checkBoxConnected.setValue(true);
			this.checkBoxConnectPowerOn.setValue(true);
		}
		
		if (type == Type.Edit)
			populateUI();
		
		if (!hasEverTriedToConnectVM){
			hasEverTriedToConnectVM = true;
		
			comboAdapterType.mask();
			service.getESXNodeNetworkAdapterTypes(WizardContext.getWizardContext().getVMwareHost(),
					WizardContext.getWizardContext().getVMwareUsername(),
					WizardContext.getWizardContext().getVMwarePassword(), 
					WizardContext.getWizardContext().getVMwareProtocol(),
					WizardContext.getWizardContext().getVMwarePort(),
					WizardContext.getWizardContext().getESXHostModel(), new BaseAsyncCallback<String[]>(){
	
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							comboAdapterType.unmask();
						}
	
						@Override
						public void onSuccess(String[] result) {
							comboAdapterType.unmask();
							for (String type: result){
								comboAdapterType.add(type);
							}
							
							if (type == Type.Add && result !=null && result.length>0){
								comboAdapterType.setSimpleValue(result[0]);
							}
							
							if (type == Type.Edit)
								comboAdapterType.setSimpleValue(adapter.getAdapterType());
						}
					});
			
			comboNetworkConnection.mask();
			service.getESXNodeNetworkConnections(WizardContext.getWizardContext().getVMwareHost(),
					WizardContext.getWizardContext().getVMwareUsername(),
					WizardContext.getWizardContext().getVMwarePassword(), 
					WizardContext.getWizardContext().getVMwareProtocol(),
					WizardContext.getWizardContext().getVMwarePort(),
					WizardContext.getWizardContext().getESXHostModel(), new BaseAsyncCallback<String[]>(){
	
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							comboNetworkConnection.unmask();
						}
	
						@Override
						public void onSuccess(String[] result) {
							comboNetworkConnection.unmask();
							for (String type: result){
								comboNetworkConnection.add(type);
							}
							
							if (type == Type.Add && result !=null && result.length>0){
								comboNetworkConnection.setSimpleValue(result[0]);
							}
							
							if (type == Type.Edit)
								comboNetworkConnection.setSimpleValue(adapter.getNetworkLabel());
						}
						
					});
		}else{
			if (type == Type.Add && comboAdapterType.getStore().getCount()>0){
				comboAdapterType.setSimpleValue(comboAdapterType.getStore().getAt(0).getValue());
			}
			
			if (type == Type.Add && comboNetworkConnection.getStore().getCount()>0){
				comboNetworkConnection.setSimpleValue(comboNetworkConnection.getStore().getAt(0).getValue());
			}
			
			if (type == Type.Edit)
				comboNetworkConnection.setSimpleValue(adapter.getNetworkLabel());
			
			if (type == Type.Edit)
				comboAdapterType.setSimpleValue(adapter.getAdapterType());
		}
		
		
	}

	protected void handleOKButton() {
		if (this.validate()){
			isValid = true;
			thisDialog.hide();
		}
	}

	public VMwareNetworkAdapter getAdapter() {
		if (adapter == null)
			adapter = new VMwareNetworkAdapter();
		
		adapter.setAdapterName(fieldNetworkAdapterName.getValue());
		adapter.setAdapterType(this.comboAdapterType.getSimpleValue());		
		adapter.setNetworkLabel(this.comboNetworkConnection.getSimpleValue());
		
		if (this.macAddressFieldSet.isExpanded()){
			adapter.setMACAddress(this.macAddress.getValue());
		}else
			adapter.setMACAddress(null);
		
		if (this.ipAddressFieldSet.isExpanded()){
			adapter.setDynamicIP(true);
			adapter.getIP().add(this.ipAddress.getValue());
			adapter.setSubnetMask(this.subnetMaskAddress.getValue());
			adapter.setGateway(this.gatewayAddress.getValue());
			adapter.setPreferredDNS(this.preferredNDSAddress.getValue());
			adapter.setAlternateDNS(this.alternateDNSAddress.getValue());
		}else{
			adapter.setDynamicIP(false);
			adapter.setSubnetMask(null);
			adapter.setGateway(null);
			adapter.setPreferredDNS(null);
			adapter.setAlternateDNS(null);
		}
		
		adapter.setConnectAtPowerOn(this.checkBoxConnectPowerOn.getValue());
		adapter.setConnected(this.checkBoxConnected.getValue());
		
		return adapter;
	}



	public void setAdapter(VMwareNetworkAdapter adapter) {
		this.adapter = adapter;
		populateUI();
	}
	
	private void populateUI() {
		if (adapter==null)
			return;
		
		this.fieldNetworkAdapterName.disable();
		this.fieldNetworkAdapterName.setValue(adapter.getAdapterName());
		if (adapter.getMACAddress() !=null && !adapter.getMACAddress().isEmpty()){
			this.macAddressFieldSet.expand();
			this.macAddress.setValue(adapter.getMACAddress());
		}else{
			this.macAddressFieldSet.collapse();
			this.macAddress.setValue(null);
			this.macAddress.clearInvalid();
		}
		
		if (adapter.isDynamicIP()){
			this.ipAddressFieldSet.expand();
//			this.ipAddress.setValue(adapter.getIP());
			this.subnetMaskAddress.setValue(adapter.getSubnetMask());
			this.gatewayAddress.setValue(adapter.getGateway());
			this.preferredNDSAddress.setValue(adapter.getPreferredDNS());
			this.alternateDNSAddress.setValue(adapter.getAlternateDNS());
		}else{
			this.ipAddressFieldSet.collapse();
			this.ipAddress.setValue(null);
			this.ipAddress.clearInvalid();
			this.subnetMaskAddress.setValue(null);
			this.subnetMaskAddress.clearInvalid();
			this.gatewayAddress.setValue(null);
			this.gatewayAddress.clearInvalid();
			this.preferredNDSAddress.setValue(null);
			this.preferredNDSAddress.clearInvalid();
			this.alternateDNSAddress.setValue(null);
			this.alternateDNSAddress.clearInvalid();
		}
		
		this.checkBoxConnected.setValue(adapter.isConnected());
		this.checkBoxConnectPowerOn.setValue(adapter.isConnectAtPowerOn());
	}


	public boolean validate(){
		return this.fieldNetworkAdapterName.validate() && this.comboAdapterType.validate() && this.comboNetworkConnection.validate()
				&& (!this.macAddressFieldSet.isExpanded() || (this.macAddressFieldSet.isExpanded() && this.macAddress.validate()))
				&& (!this.ipAddressFieldSet.isExpanded() || (this.ipAddressFieldSet.isExpanded() && this.ipAddress.validate() && this.subnetMaskAddress.validate() && this.gatewayAddress.validate() && this.preferredNDSAddress.validate() && this.alternateDNSAddress.validate()));
	}

	public void setType(Type type) {
		this.type = type;
	}

	public boolean isValid() {
		return isValid;
	}
}

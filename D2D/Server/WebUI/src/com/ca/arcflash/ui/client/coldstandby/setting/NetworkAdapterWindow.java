package com.ca.arcflash.ui.client.coldstandby.setting;

import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyServiceAsync;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NetworkAdapterWindow extends Dialog {
	protected final ColdStandbyServiceAsync service = GWT.create(ColdStandbyService.class);
	
	protected Dialog thisDialog;
	protected Label fieldNetworkAdapterName;

	protected RadioButton dynamicIPRadio;
	protected RadioButton staticIPRadio;
	protected FieldSet ipAddressFieldSet;
	protected TextField<String> ipAddress;
	protected TextField<String> subnetMaskAddress;
	protected TextField<String> gatewayAddress;
	
	protected RadioButton dynamicDNSRadio;
	protected RadioButton staticDNSRadio;
	protected FieldSet dnsAddressFieldSet;
	protected TextField<String> preferredNDSAddress;
	protected TextField<String> alternateDNSAddress;
	
	protected IPValidator ipValidator = new IPValidator();
	protected FormData formData = new FormData("-20");
	
	protected NetworkAdapter configuredAdapter;
	protected NetworkAdapter currentAdapter;

	public NetworkAdapterWindow() {
		// TODO Auto-generated constructor stub
		this.thisDialog = this;
		this.setHeadingHtml( UIContext.Constants.coldStandbySettingIPTitle());
		this.setWidth(450);
		this.setResizable(false);
		this.setShadow(false);
		this.setAutoHeight(true);
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setWidth("100%");
		
		verticalPanel.add(setupIPRadionButtons());
		verticalPanel.add(setupIPAddressPart());
		verticalPanel.add(setupDNSRadioButtons());
		verticalPanel.add(setupDNSAddressPart());
		
		
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
	
	private Widget setupIPRadionButtons() {
		LayoutContainer layoutContainer=new LayoutContainer();
		layoutContainer.setHeight(50);
		VBoxLayout vBoxLayout=new VBoxLayout();
		//vBoxLayout.setPadding(new Padding(5));
		vBoxLayout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
		layoutContainer.setLayout(vBoxLayout);
		
		dynamicIPRadio=new RadioButton("IP");
		dynamicIPRadio.ensureDebugId("22d8f28d-2567-431a-9670-2351132d4095");
		dynamicIPRadio.setText(UIContext.Constants.coldStandbySettingDynamicIP());
		dynamicIPRadio.setStyleName("setting-text-label");
		dynamicIPRadio.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				// TODO Auto-generated method stub
				if(event.getValue()){
					setIPStatus(true);
					setIP();
				}
				
			}
		});
		dynamicIPRadio.setValue(true);
		
		staticIPRadio=new RadioButton("IP");
		staticIPRadio.ensureDebugId("ea6d12c1-6e45-401d-bffe-328e36cf8ae5");
		staticIPRadio.setText(UIContext.Constants.coldStandbySettingStaticIP());
		staticIPRadio.setStyleName("setting-text-label");
		staticIPRadio.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				// TODO Auto-generated method stub
				if(event.getValue()){
					setIPStatus(false);
					setIP();
					staticDNSRadio.setValue(true);
					setDNSStatus(false);
					setDNS();
				}
			}
		});
		
		layoutContainer.add(dynamicIPRadio);
		layoutContainer.add(staticIPRadio);
		return layoutContainer;
	}
	private Widget setupDNSRadioButtons(){
		LayoutContainer layoutContainer=new LayoutContainer();
		layoutContainer.setHeight(50);
		VBoxLayout vBoxLayout=new VBoxLayout();
		//vBoxLayout.setPadding(new Padding(5));
		layoutContainer.setLayout(vBoxLayout);
		
		dynamicDNSRadio=new RadioButton("dns");
		dynamicDNSRadio.ensureDebugId("2b8de3ab-8e64-4edb-a1b7-91e16a026c9c");
		dynamicDNSRadio.setText(UIContext.Constants.coldStandbySettingDynamicDNS());
		dynamicDNSRadio.setStyleName("setting-text-label");
		dynamicDNSRadio.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				// TODO Auto-generated method stub
				if(event.getValue()){
					setDNSStatus(true);
					setDNS();
				}
			}
			
		});
		dynamicDNSRadio.setValue(true);
		
		staticDNSRadio=new RadioButton("dns");
		staticDNSRadio.ensureDebugId("3c5db0ff-2a76-4d58-aa66-6f1aec76175b");
		staticDNSRadio.setText(UIContext.Constants.coldStandbySettingStaticDNS());
		staticDNSRadio.setStyleName("setting-text-label");
		staticDNSRadio.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				// TODO Auto-generated method stub
				if(event.getValue()){
					setDNSStatus(false);
					setDNS();
				}
				
			}
		});
		
		layoutContainer.add(dynamicDNSRadio);
		layoutContainer.add(staticDNSRadio);
		return layoutContainer;
	}
	private Widget setupIPAddressPart() {
		FormLayout formLayout = new FormLayout();
		formLayout.setLabelWidth(150);
		
		ipAddressFieldSet = new FieldSet();
		ipAddressFieldSet.setLayout(formLayout);
		
		ipAddress = new TextField<String>();
		ipAddress.setValidator(ipValidator);
		ipAddress.setFieldLabel(UIContext.Constants.coldStandbySettingIPAddress());
		ipAddress.setAllowBlank(false);
		ipAddress.ensureDebugId("d42f4ef4-6590-42c8-b55b-193d4783c9a0");
		ipAddress.setStyleName("setting-text-label");
		ipAddressFieldSet.add(ipAddress, formData);
		
		subnetMaskAddress = new TextField<String>();
		subnetMaskAddress.setValidator(ipValidator);
		subnetMaskAddress.setFieldLabel(UIContext.Constants.coldStandbySettingSubnetMask());
		subnetMaskAddress.setAllowBlank(false);
		subnetMaskAddress.ensureDebugId("18c7c4a1-0a70-4140-807a-1d3156ef9bd8");
		subnetMaskAddress.setStyleName("setting-text-label");
		ipAddressFieldSet.add(subnetMaskAddress, formData);
		
		gatewayAddress = new TextField<String>();
		gatewayAddress.setValidator(ipValidator);
		gatewayAddress.setFieldLabel(UIContext.Constants.coldStandbySettingDefaultGateway());
		gatewayAddress.ensureDebugId("43e68e41-05af-4aa8-9013-bc8be69f5a10");
		//gatewayAddress.setAllowBlank(false);
		gatewayAddress.setStyleName("setting-text-label");
		ipAddressFieldSet.add(gatewayAddress, formData);
		
		return ipAddressFieldSet;
	}
	
	private Widget setupDNSAddressPart() {
		FormLayout formLayout = new FormLayout();
		formLayout.setLabelWidth(150);
		
		dnsAddressFieldSet = new FieldSet();
		dnsAddressFieldSet.setLayout(formLayout);
		
		preferredNDSAddress = new TextField<String>();
		preferredNDSAddress.setValidator(ipValidator);
		preferredNDSAddress.setFieldLabel(UIContext.Constants.coldStandbySettingPreferredDNS());
		preferredNDSAddress.ensureDebugId("e7fc199f-5243-4784-95a0-b9bb6abffa10");
		preferredNDSAddress.setAllowBlank(false);
		preferredNDSAddress.setStyleName("setting-text-label");
		dnsAddressFieldSet.add(preferredNDSAddress, formData);
		
		alternateDNSAddress = new TextField<String>();
		alternateDNSAddress.setValidator(ipValidator);
		alternateDNSAddress.setFieldLabel(UIContext.Constants.coldStandbySettingAlterDNS());
		alternateDNSAddress.ensureDebugId("492a5a3c-7031-4ec6-90e2-9e61916dfe2b");
		//alternateDNSAddress.setAllowBlank(false);
		alternateDNSAddress.setStyleName("setting-text-label");
		dnsAddressFieldSet.add(alternateDNSAddress, formData);
		
		return dnsAddressFieldSet;
	}
	
	protected void handleOKButton() {
		if (this.validate()){
			saveNetworkAdapter();
			thisDialog.hide();
		}
	}


	public void setAdapter(NetworkAdapter configuredNetworkAdapter,NetworkAdapter currentNetworkAdapter) {
		this.configuredAdapter=configuredNetworkAdapter;
		this.currentAdapter=currentNetworkAdapter;
		populateUI();
	}
	private void setIPStatus(boolean isDynamicIP){
		if(isDynamicIP){
			ipAddress.disable();
			subnetMaskAddress.disable();
			gatewayAddress.disable();
		}
		else{
			ipAddress.enable();
			subnetMaskAddress.enable();
			gatewayAddress.enable();
		}
	}
	private void setDNSStatus(boolean isDynamicDNS){
		if(isDynamicDNS){
			preferredNDSAddress.disable();
			alternateDNSAddress.disable();
		}
		else{
			preferredNDSAddress.enable();
			alternateDNSAddress.enable();
		}
	}
	
	private void setIP(){
		NetworkAdapter adapter=null;
		if(dynamicIPRadio.getValue()){
			adapter=currentAdapter;
			
			ipAddress.setValue("");
			subnetMaskAddress.setValue("");
			gatewayAddress.setValue("");
			
		}
		else{
			adapter=configuredAdapter;
			
			if(adapter.getIP().size()>0){
				ipAddress.setValue(adapter.getIP().get(0));
			}
			subnetMaskAddress.setValue(adapter.getSubnetMask());
			gatewayAddress.setValue(adapter.getGateway());
			
		}
		

	}
	
	private void setDNS(){
		NetworkAdapter adapter=null;
		if(dynamicDNSRadio.getValue()){
			adapter=currentAdapter;
			
			preferredNDSAddress.setValue("");
			alternateDNSAddress.setValue("");
		}
		else{
			adapter=configuredAdapter;
			
			preferredNDSAddress.setValue(adapter.getPreferredDNS());
			alternateDNSAddress.setValue(adapter.getAlternateDNS());
		}
	}
	private void populateUI() {
		
		NetworkAdapter adapter=null;
		if(configuredAdapter!=null){
			adapter=configuredAdapter;
		}
		else{
			adapter=currentAdapter;
		}
		
		ipAddressFieldSet.expand();

		dynamicIPRadio.setValue(adapter.isDynamicIP());
		staticIPRadio.setValue(!adapter.isDynamicIP());
		setIP();
		
		dynamicDNSRadio.setValue(adapter.isDynamicDNS());
		staticDNSRadio.setValue(!adapter.isDynamicDNS());
		setDNS();
		
		setIPStatus(adapter.isDynamicIP());
		setDNSStatus(adapter.isDynamicDNS());
	}
	
	private boolean validateNetworkMaskAddress() {
		String[] ips= subnetMaskAddress.getValue().split("\\.");
		if(ips.length!=4){
			//Warning message box
			return false;
		}
		long ip=0;
		for(int i=0;i<ips.length;i++){
			long temp=Long.parseLong(ips[i])<<((3-i)*8);
			ip=ip+temp;
		}
		
		boolean isFoundZero=false;
		int zeroCount=0;
		int OneCount=0;
		String subMaskString=Long.toBinaryString(ip);
		if(subMaskString.length()>32){
			subMaskString=subMaskString.substring(subMaskString.length()-32);
		}
		
		for(int i=0;i<subMaskString.length();i++){
			if(subMaskString.charAt(i)=='1'){
				OneCount++;
				if(isFoundZero){
					return false;
				}
			}
			else{
				zeroCount++;
				if(!isFoundZero)
					isFoundZero=true;
			}
		}
		if((zeroCount==subMaskString.length())||(OneCount==subMaskString.length())){
			return false;
		}

		return true;
	}
	private void saveNetworkAdapter(){
		
		if (configuredAdapter==null){
			return;
		}
		
		configuredAdapter.setDynamicIP(dynamicIPRadio.getValue());
		if(staticIPRadio.getValue()){
			if(configuredAdapter.getIP().size()>0){
				configuredAdapter.getIP().set(0, ipAddress.getValue());
			}
			configuredAdapter.setSubnetMask(subnetMaskAddress.getValue());
			configuredAdapter.setGateway(gatewayAddress.getValue());
		}

		configuredAdapter.setDynamicDNS(dynamicDNSRadio.getValue());
		if(staticDNSRadio.getValue()){
			configuredAdapter.setPreferredDNS(preferredNDSAddress.getValue());
			configuredAdapter.setAlternateDNS(alternateDNSAddress.getValue());
		}

	}
	public boolean validate(){
		boolean IPResult=false;
		boolean DNSResult=false;
		
		if(staticIPRadio.getValue()){
			IPResult=this.ipAddress.validate() && this.subnetMaskAddress.validate();
			
			if((gatewayAddress.getValue()!=null)&&(!gatewayAddress.getValue().isEmpty())){
				IPResult=IPResult&&this.gatewayAddress.validate();
			}
			
			if(IPResult){
				IPResult=validateNetworkMaskAddress();
				if(!IPResult){
					MessageBox messageBox = new MessageBox();
					messageBox.getDialog().ensureDebugId("fec8b707-ddfa-4cb1-b73a-2b34669527a8");
					messageBox.setIcon(MessageBox.WARNING);
					messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameVCM));
					messageBox.setMessage(UIContext.Constants.coldStandbySettingInvalidSubnetMask());
					messageBox.show();
				}
			}
			
		}
		else{
			IPResult=true;
		}
		
		DNSResult=true;
		if(staticDNSRadio.getValue()){
			DNSResult=this.preferredNDSAddress.validate();
			/*if(!preferredNDSAddress.getValue().isEmpty()){
				DNSResult=this.preferredNDSAddress.validate();
			}*/
			
			if((alternateDNSAddress.getValue()!=null)&&(!alternateDNSAddress.getValue().isEmpty())){
				DNSResult=DNSResult&&this.alternateDNSAddress.validate();
			}
		}

		return IPResult&&DNSResult;
	}

}

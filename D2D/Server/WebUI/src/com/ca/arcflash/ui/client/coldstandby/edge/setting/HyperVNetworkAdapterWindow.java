package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

class HyperVNetworkAdapterWindow extends Dialog {

	private TextField<String> fieldNetworkLabel;
	private TextField<String> fieldIPAddress;
	private TextField<String> fieldMacAddress;
	private ComboBox<BeanModel> virtualNetwork;
	
	private Dialog thisDialog;
	
	public HyperVNetworkAdapterWindow() {
		this.thisDialog = this;
		this.setSize(400, 300);
		this.setHeadingHtml("Add/Modify Network Adapter");
		this.setLayout(new FitLayout());
		this.ensureDebugId("73469212-4b4c-4f25-9a01-0c1f78c538d6");
		
		FormPanel panel = new FormPanel();
		panel.setLabelWidth(150);
		panel.setLabelAlign(LabelAlign.RIGHT);
		panel.setHeaderVisible(false);
		
		FormData data = new FormData("100%");
		
		fieldNetworkLabel = new TextField<String>();
		fieldNetworkLabel.setFieldLabel("Network Adapter Name");
		panel.add(fieldNetworkLabel, data);
		
		virtualNetwork = new ComboBox<BeanModel>();
		ListStore<BeanModel> store = new ListStore<BeanModel>();
		//store.add(BeanModelAdapter.createModel(Arrays.asList(VirtualNetwork.values())));
		virtualNetwork.setStore(store);
		virtualNetwork.setDisplayField("name");
		virtualNetwork.setFieldLabel("Virtual Network");
		virtualNetwork.setTriggerAction(TriggerAction.ALL);
		virtualNetwork.setForceSelection(true);
		virtualNetwork.setEditable(false);
		virtualNetwork.setValue(store.getAt(0));
		panel.add(virtualNetwork,data);
		
		FieldSet ipAddressFieldSet = new FieldSet();
		ipAddressFieldSet.setCheckboxToggle(true);
		ipAddressFieldSet.setExpanded(false);
		ipAddressFieldSet.setHeadingHtml("Specify IP Address Manually");
		ipAddressFieldSet.setLayout(new FormLayout());
		
		fieldIPAddress = new TextField<String>();
		fieldIPAddress.setFieldLabel("IP Address");
		ipAddressFieldSet.add(fieldIPAddress, data);
		panel.add(ipAddressFieldSet,data);
		
		FieldSet fieldSetMacAddress = new FieldSet();
		fieldSetMacAddress.setCheckboxToggle(true);
		fieldSetMacAddress.setExpanded(false);
		fieldSetMacAddress.setHeadingHtml("Specify Mac Address Manually");
		fieldSetMacAddress.setLayout(new FormLayout());
		
		fieldMacAddress = new TextField<String>();
		fieldMacAddress.setFieldLabel("MAC Address");
		fieldSetMacAddress.add(fieldMacAddress, data);
		panel.add(fieldSetMacAddress, data);
		
		this.add(panel);
		
		this.setButtons(Dialog.OKCANCEL);
		this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				populateNetworkAdapter();
			}
		});
		this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				thisDialog.setVisible(false);
			}
		});
		
		this.setHideOnButtonClick(true);
	}
	
	private void populateNetworkAdapter(){
		BaseModel model = new BaseModel();
		model.set("adapterName", fieldNetworkLabel.getValue());
		model.set("ipAddress", fieldIPAddress.getValue()==null?"DHCP":fieldIPAddress.getValue());
		model.set("macAddress", fieldMacAddress.getValue()==null?"Automatically Assign":fieldMacAddress.getValue());
		//VirtualNetwork network = (VirtualNetwork)virtualNetwork.getValue().getBean();
		//model.set("virtualNetwork", network.getName());
	}
	
	
}

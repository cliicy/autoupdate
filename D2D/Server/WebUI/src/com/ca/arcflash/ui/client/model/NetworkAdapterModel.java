package com.ca.arcflash.ui.client.model;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;


public class NetworkAdapterModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8019190552694288131L;

	
	public NetworkAdapterModel(NetworkAdapter networkAdapter){
		
		set("networkAdapter", networkAdapter);
		
		BaseSimpleComboBox<String> adaperTypeComboBox=new BaseSimpleComboBox<String>();
		adaperTypeComboBox.setStore(new ListStore<SimpleComboValue<String>>());
		adaperTypeComboBox.setAllowBlank(false);
		adaperTypeComboBox.setFieldLabel("Adapter Type");
		set("adapterTypeComboBox", adaperTypeComboBox);
		
		BaseSimpleComboBox<String> comboNetworkConnection = new BaseSimpleComboBox<String>();
		comboNetworkConnection.setAllowBlank(false);
		comboNetworkConnection.setFieldLabel("Network Connection");
		set("networkConnectComboBox", comboNetworkConnection);
		
	}
	
	@SuppressWarnings("unchecked")
	public BaseSimpleComboBox<String> getAdapterTypeComboBox(){
		return (BaseSimpleComboBox<String>)get("adapterTypeComboBox");
	}
	
	public String getAdapterTypeComboBox3(){
		return get("adapterTypeComboBox3");
	}
	
	public void setAdapterTypeComboBox3(String value){
		set("adapterTypeComboBox3", value);
	}
	@SuppressWarnings("unchecked")
	public BaseSimpleComboBox<String> getNetworkConnectComboBox(){
		return (BaseSimpleComboBox<String>)get("networkConnectComboBox");
	}
	
	public String getNetworkConnectComboBox3(){
		return get("networkConnectComboBox3");
	}
	
	public void setNetworkConnectComboBox3(String value){
		set("networkConnectComboBox3", value);
	}

	public NetworkAdapter getNetworkAdapter(){
		return (NetworkAdapter)get("networkAdapter");
	}
	
	public void setNetworkAdapter(NetworkAdapter networkAdapter){
		set("networkAdapter", networkAdapter);
	}
	
}

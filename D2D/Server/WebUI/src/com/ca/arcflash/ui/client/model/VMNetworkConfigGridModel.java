package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

public class VMNetworkConfigGridModel extends BaseModelData
{
	private static final long serialVersionUID = 7101256566104388593L;

	public VMNetworkConfigGridModel(VMNetworkConfigInfoModel infoModel){
		set("infoModel", infoModel);
		
		ListStore<BaseModelData> configurationStore = new ListStore<BaseModelData>();
		ComboBox<BaseModelData> configurationComboBox = new ComboBox<BaseModelData>();
		configurationComboBox.setStore(configurationStore);
		configurationComboBox.setFieldLabel("display");
		set("avalibaleConfigComboBox", configurationComboBox);
		
		ListStore<BaseModelData> adapterTypeStore = new ListStore<BaseModelData>();
		ComboBox<BaseModelData> adapterTypeComboBox = new ComboBox<BaseModelData>();
		adapterTypeComboBox.setStore(adapterTypeStore);
		adapterTypeComboBox.setFieldLabel("type");
		set("adapterTypeConfigComboBox", adapterTypeComboBox);
	}
	
	public ComboBox<BaseModelData> getAvaliableConfigInfoComboBox(){
		return get("avalibaleConfigComboBox");
	}
	
	public ComboBox<BaseModelData> getAdapterTypeConfigComboBox(){
		return get("adapterTypeConfigComboBox");
	}
	
	public VMNetworkConfigInfoModel getInfoModel(){
		return get("infoModel");
	}
	
	public void setInfoModel(VMNetworkConfigInfoModel model){
		set("infoModel",model);
	}
}

package com.ca.arcflash.ui.client.model;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;

public class DataStoreModel extends BaseModelData{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7415307915306060781L;
	private ComboBox<BaseModelData> diskTypeCombo;
	
	private boolean isSpecifyAll;
	
	public DataStoreModel(DiskModel diskModel) {
		set("diskModel", diskModel);

		ListStore<ModelData> esxlListStore = new ListStore<ModelData>();
		ComboBox<ModelData> esxDataStoreComboBox = new ComboBox<ModelData>();
		esxDataStoreComboBox.setStore(esxlListStore);
		esxDataStoreComboBox.setFieldLabel("DataStore Type");
		set("esxDataStoreComboBox", esxDataStoreComboBox);

		ListStore<ModelData> diskTypeStore = new ListStore<ModelData>();
		ComboBox<ModelData> diskTypeComboBox = new ComboBox<ModelData>();
		diskTypeComboBox.setStore(diskTypeStore);
		diskTypeComboBox.setFieldLabel("name");
		set("diskTypeComboBox", diskTypeComboBox);
	}
	
	public ComboBox<ModelData>  getEsxDataStoreComboBox(){
		return get("esxDataStoreComboBox");
	}
	
	public DiskModel getDiskModel(){
		return get("diskModel");
	}
	
	public void setDiskModel(DiskModel model){
		set("diskModel",model);
	}
	
	public ComboBox<ModelData> getDiskTypeComboBox(){
		return get("diskTypeComboBox");
	}

	public boolean isSpecifyAll() {
		return isSpecifyAll;
	}

	public void setSpecifyAll(boolean isSpecifyAll) {
		this.isSpecifyAll = isSpecifyAll;
	}
	
	public ComboBox<BaseModelData> getDiskTypeComboBox(DiskModel disk){
		diskTypeCombo = GenerateDiskTypeCombo(disk);
		return diskTypeCombo;
	}
	
	public long getDiskType(){
		long type = diskTypeCombo.getValue().get("value");
		if (type == DiskModel.VMware_VDISK_TYPE_ORIGINAL)
			return getDiskModel().getDiskType();
		return type;
	}
	
	private static ComboBox<BaseModelData> GenerateDiskTypeCombo(DiskModel disk) {
		BaseModelData thickLazy;
		BaseModelData thickEager;
		BaseModelData thin;
		//BaseModelData original;
		
		ListStore<BaseModelData> conigurationStore = new ListStore<BaseModelData>();
		ComboBox<BaseModelData> diskTypeCombo = new ComboBox<BaseModelData>();
		diskTypeCombo.setWidth(150);
		diskTypeCombo.setStore(conigurationStore);
		diskTypeCombo.setDisplayField("display");
		diskTypeCombo.setEditable(false);
		diskTypeCombo.setTriggerAction(TriggerAction.ALL);
		
		thickLazy = new BaseModelData();
		thickLazy.set("display", UIContext.Constants.recoverVMwareVDiskThickLazy());
		thickLazy.set("value", DiskModel.VMware_VDISK_TYPE_THICK_LAZY);
		conigurationStore.add(thickLazy);
		
		thickEager = new BaseModelData();
		thickEager.set("display", UIContext.Constants.recoverVMwareVDiskThickEager());
		thickEager.set("value", DiskModel.VMware_VDISK_TYPE_THICK_EAGER);
		conigurationStore.add(thickEager);
		
		thin = new BaseModelData();
		thin.set("display", UIContext.Constants.recoverVMwareVDiskThin());
		thin.set("value", DiskModel.VMware_VDISK_TYPE_THIN);
		conigurationStore.add(thin);
		
		/*original= new BaseModelData();
		original.set("display", UIContext.Constants.recoverVMHyperVDiskTypeKeepSame());
		original.set("value", DiskModel.VMware_VDISK_TYPE_ORIGINAL);
		conigurationStore.add(original);*/
		
		if (disk.getDiskType() == DiskModel.VMware_VDISK_TYPE_THICK_LAZY)
			diskTypeCombo.setValue(thickLazy);
		else if (disk.getDiskType() == DiskModel.VMware_VDISK_TYPE_THICK_EAGER)
			diskTypeCombo.setValue(thickEager);
		else if (disk.getDiskType() == DiskModel.VMware_VDISK_TYPE_THIN)
			diskTypeCombo.setValue(thin);
		
		return diskTypeCombo;
	}
}

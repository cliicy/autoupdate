package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class DiskDataStoreModel extends BaseModelData {
	
	public DiskDataStoreModel(){
		this.setDiskType(new Long(0));
		this.setQuickRecovery(0);
	}
	
	public void setDisk(String disk){
		set("disk",disk);
	}
	
	public String getDisk(){
		return get("disk");
	}
	
	public void setDataStore(String datastore){
		set("datastore",datastore);
	}
	
	public String getDatastore(){
		return get("datastore");
	}
	
	public void setDataStoreId(String datastoreId){
		set("datastoreId", datastoreId);
	}
	
	public String getDatastoreId(){
		return get("datastoreId");
	}
	
	public void setDiskName(String diskName){
		set("diskName",diskName);
	}
	
	public String getDiskName(){
		return get("diskName");
	}
	
	public void setDiskType(Long diskType) {
		set("diskType", diskType);
	}
	
	public Long getDiskType() {
		return (Long) get("diskType");
	}
	
	public void setVolumeName(String volumeName){
		set("volumeName",volumeName);
	}
	
	public Long getQuickRecovery() {
		return get("quickRecovery");
	}

	public void setQuickRecovery(long quickRecovery) {
		set("quickRecovery",quickRecovery);
	}
}

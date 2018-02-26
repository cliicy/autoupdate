package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class DiskModel extends BaseModelData{
	
	public static long HYPERV_VDISK_TYPE_DYNAMIC 	= 0;
	public static long HYPERV_VDISK_TYPE_FIXED		= 1;
	public static long HYPERV_VDISK_TYPE_FIXED_QUICK= 2;
	public static long HYPERV_VDISK_TYPE_ORIGINAL	= 3;
	
	public static long VMware_VDISK_TYPE_THICK_LAZY		= 0;
	public static long VMware_VDISK_TYPE_THIN			= 1;
	public static long VMware_VDISK_TYPE_THICK_EAGER	= 2;
	public static long VMware_VDISK_TYPE_ORIGINAL		= 3;
	
	public Integer getDiskNumber() {
		return get("diskNumber");
	}

	public void setDiskNumber(Integer diskNumber) {
		set("diskNumber",diskNumber);
	}

	public Long getSize() {
		return get("size");
	}

	public void setSize(long size) {
		set("size",size);
	}

	public Integer getSignature() {
		return get("signature");
	}

	public void setSignature(Integer signature) {
		set("signature",signature);
	}

	public Long getDiskType() {
		return get("diskType");
	}

	public void setDiskType(long diskType) {
		set("diskType", diskType);
	}

	public String getControllerType() {
		return get("controllerType");
	}

	public void setControllerType(String controllerType) {
		set("controllerType", controllerType);
	}

	public String getPartitionType() {
		return get("partitionType");
	}

	public void setPartitionType(String partitionType) {
		set("partitionType", partitionType);
	}

	public void setDiskUrl(String diskUrl){
		set("diskUrl",diskUrl);
	}
	
	public String getDiskUrl(){
		return get("diskUrl");
	}
	
	public void setDiskDataStore(String diskDataStore){
		set("diskDataStore",diskDataStore);
	}
	
	public String getDiskDataStore(){
		return get("diskDataStore");
	}
	
	public void setDiskDataStoreId(String diskDataStoreId){
		set("diskDataStoreId",diskDataStoreId);
	}
	
	public String getDiskDataStoreId(){
		return get("diskDataStoreId");
	}
	
	/*public List<VMVolumeModel> getVolumes() {
		return get("volumes");
	}

	public void setVolumes(List<VMVolumeModel> volumes) {
		set("volumes", volumes);
	}*/
	
	public List<VMVolumeModel> volumeModelList;
	
	

}

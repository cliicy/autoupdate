package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VCloudVirtualDataCenterModel extends BaseModelData {
	private static final long serialVersionUID = -6972349006170531490L;
	private List<VirtualCenterModel> vCenters;
	private List<VCloudStorageProfileModel> storageProfiles;
	private List<VMNetworkConfigInfoModel> availableNetworks;

	public String getName() {
		return (String) get("name");
	}

	public void setName(String name) {
		set("name", name);
	}
	
	public String getId() {
		return (String) get("id");
	}
	
	public void setId(String id) {
		set("id", id);
	}
	
	public String getAllocationModel() {
		return (String) get("allocationModel");
	}
	
	public void setAllocationModel(String model) {
		set("allocationModel", model);
	}
	
	public Integer getCPUCount() {
		return (Integer) get("cpuCount");
	}
	
	public void setCPUCount(Integer cpuCount) {
		set("cpuCount", cpuCount);
	}
	
	public Long getMemoryLimit() { // bytes
		return (Long) get("memoryLimit");
	}
	
	public void setMemoryLimit(Long memoryLimit) { //bytes
		set("memoryLimit", memoryLimit);
	}
	
	public List<VMNetworkConfigInfoModel> getAvailableNetworks() {
		return availableNetworks;
	}
	
	public void setAvailableNetworks(List<VMNetworkConfigInfoModel> networks) {
		this.availableNetworks = networks;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getSupportedHardwareVersions() {
		return (List<String>) get("hardwareVersions");
	}
	
	public void setSupportedHardwareVersions(List<String> versions) {
		set("hardwareVersions", versions);
	}
	
	public Boolean getIsMatchedOriginal() {
		return get("isMatchedOriginal");
	}
	
	public void setIsMatchedOriginal(Boolean isMatchedOriginal) {
		set("isMatchedOriginal", isMatchedOriginal);
	}
	
	public List<VirtualCenterModel> getVCenters() {
		return vCenters;
	}
	
	public void setVCenters(List<VirtualCenterModel> vCenters) {
		this.vCenters = vCenters;
	}
	
	public List<VCloudStorageProfileModel> getStorageProfiles() {
		return storageProfiles;
	}
	
	public void setStorageProfiles(List<VCloudStorageProfileModel> storageProfiles) {
		this.storageProfiles = storageProfiles;
	}
}
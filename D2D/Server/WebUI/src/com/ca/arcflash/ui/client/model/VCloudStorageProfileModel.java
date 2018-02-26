package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VCloudStorageProfileModel extends BaseModelData {
	private static final long serialVersionUID = 4840735262503015575L;
	private List<VMStorage> storages;

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
	
	public Long getFreeSize() { // bytes: limitedSize - requestedSize
		return (Long) get("freeSize");
	}
	
	public void setFreeSize(Long freeSize) { // bytes: limitedSize - requestedSize
		set("freeSize", freeSize);
	}
	
	public Long getUsedCapacity() {
		return (Long) get("usedCapacity");
	}
	
	public void setUsedCapacity(Long usedCapacity) {
		set("usedCapacity", usedCapacity);
	}

	public List<VMStorage> getStorages() {
		return storages;
	}

	public void setStorages(List<VMStorage> VMStorage) {
		this.storages = VMStorage;
	}
}
package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VCloudOrgnizationModel extends BaseModelData {
	private static final long serialVersionUID = -1020343786292780115L;
	private List<VCloudVirtualDataCenterModel> vitrualDataCenters;

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
	
	public String getFullName() {
		return (String) get("fullName");
	}
	
	public void setFullName(String fullName) {
		set("fullName", fullName);
	}
	
	public String getDescription() {
		return (String) get("description");
	}
	
	public void setDescription(String description) {
		set("description", description);
	}

	public List<VCloudVirtualDataCenterModel> getVitrualDataCenters() {
		return vitrualDataCenters;
	}

	public void setVitrualDataCenters(List<VCloudVirtualDataCenterModel> vitrualDataCenters) {
		this.vitrualDataCenters = vitrualDataCenters;
	}
}
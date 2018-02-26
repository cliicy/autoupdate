package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VCloudDirectorModel extends BaseModelData {
	private static final long serialVersionUID = -565933169086192435L;
	private List<VCloudOrgnizationModel> organizations;
	private VCloudVirtualDataCenterModel originalVDC;
	private VCloudVirtualDataCenterModel targetVDC;

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
	
	public String getUsername() {
		return (String) get("username");
	}

	public void setUsername(String username) {
		set("username", username);
	}

	public String getPassword() {
		return (String) get("password");
	}

	public void setPassword(String password) {
		set("password", password);
	}

	public String getProtocol() {
		return (String) get("protocol");
	}

	public void setProtocol(String protocol) {
		set("protocol", protocol);
	}

	public Integer getPort() {
		return (Integer) get("port");
	}

	public void setPort(Integer port) {
		set("port", port);
	}
	
	public List<VCloudOrgnizationModel> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(List<VCloudOrgnizationModel> organizations) {
		this.organizations = organizations;
	}

	public VCloudVirtualDataCenterModel getOriginalVDC() {
		return originalVDC;
	}

	public void setOriginalVDC(VCloudVirtualDataCenterModel originalVDC) {
		this.originalVDC = originalVDC;
	}

	public VCloudVirtualDataCenterModel getTargetVDC() {
		return targetVDC;
	}

	public void setTargetVDC(VCloudVirtualDataCenterModel selectedVDC) {
		this.targetVDC = selectedVDC;
	}
}
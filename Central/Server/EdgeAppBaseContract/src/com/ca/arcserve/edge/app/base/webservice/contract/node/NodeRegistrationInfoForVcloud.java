package com.ca.arcserve.edge.app.base.webservice.contract.node;

import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntity;

public class NodeRegistrationInfoForVcloud extends NodeRegistrationInfo{
	private static final long serialVersionUID = 1L;
	private VsphereEntity vCloudEntity;
	public VsphereEntity getvCloudEntity() {
		return vCloudEntity;
	}
	public void setvCloudEntity(VsphereEntity vCloudEntity) {
		this.vCloudEntity = vCloudEntity;
	}
}

package com.ca.arcserve.edge.app.base.webservice.contract.license;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class LicensedVmInfo implements Serializable, BeanModelTag{
	private static final long serialVersionUID = -4436124941364150522L;
	
	private String vmName;
	private int nodeId;
	
	public String getVmName() {
		return vmName;
	}
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	
}

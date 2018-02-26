package com.ca.arcserve.edge.app.base.webservice.contract.vcm;

public class VmEdgeMap {
	
	int vmId;
	
	int edgeId;
	
	String vmInstanceUuid;

	public int getVmId() {
		return vmId;
	}

	public void setVmId(int vmId) {
		this.vmId = vmId;
	}

	public int getEdgeId() {
		return edgeId;
	}

	public void setEdgeId(int edgeId) {
		this.edgeId = edgeId;
	}

	public String getVmInstanceUuid() {
		return vmInstanceUuid;
	}

	public void setVmInstanceUuid(String vmInstanceUuid) {
		this.vmInstanceUuid = vmInstanceUuid;
	}
	

}

package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class VMRegistrationInfo implements Serializable {

	private static final long serialVersionUID = 8953377633910606211L;
	
	private NodeRegistrationInfo nodeInfo;
	private DiscoveryVirtualMachineInfo vmInfo;
	
	public NodeRegistrationInfo getNodeInfo() {
		return nodeInfo;
	}
	public void setNodeInfo(NodeRegistrationInfo nodeInfo) {
		this.nodeInfo = nodeInfo;
	}
	public DiscoveryVirtualMachineInfo getVmInfo() {
		return vmInfo;
	}
	public void setVmInfo(DiscoveryVirtualMachineInfo vmInfo) {
		this.vmInfo = vmInfo;
	}

}

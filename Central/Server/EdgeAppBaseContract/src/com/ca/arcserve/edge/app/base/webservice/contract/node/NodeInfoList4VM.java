package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.NodeInfo;

public class NodeInfoList4VM implements Serializable {

	private static final long serialVersionUID = -5306329513244957887L;
	private List<NodeGroup> groupList;
	private List<NodeInfo> nodeList;
	private List<EdgeEsxVmInfo> vmInforList;
	
	public List<NodeGroup> getGroupList() {
		return groupList;
	}
	public void setGroupList(List<NodeGroup> groupList) {
		this.groupList = groupList;
	}
	public List<NodeInfo> getNodeList() {
		return nodeList;
	}
	public void setNodeList(List<NodeInfo> nodeList) {
		this.nodeList = nodeList;
	}
	public List<EdgeEsxVmInfo> getVmInforList() {
		return vmInforList;
	}
	public void setVmInforList(List<EdgeEsxVmInfo> vmInforList) {
		this.vmInforList = vmInforList;
	}
}

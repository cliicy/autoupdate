package com.ca.arcserve.edge.app.rps.webservice.contract.rps.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;

public class AddRpsNodesResult implements Serializable{

	private static final long serialVersionUID = 1429743017434378075L;

	private List<NodeRegistrationInfo> rpsNodes = new ArrayList<NodeRegistrationInfo>();
	private List<Integer> haveExistedRpsNodeIds = new ArrayList<Integer>();

	public List<NodeRegistrationInfo> getRpsNodes() {
		return rpsNodes;
	}
	public void setRpsNodes(List<NodeRegistrationInfo> rpsNodes) {
		this.rpsNodes = rpsNodes;
	}
	public List<Integer> getHaveExistedRpsNodeIds() {
		return haveExistedRpsNodeIds;
	}
	public void setHaveExistedRpsNodeIds(List<Integer> haveExistedRpsNodeIds) {
		this.haveExistedRpsNodeIds = haveExistedRpsNodeIds;
	}
}

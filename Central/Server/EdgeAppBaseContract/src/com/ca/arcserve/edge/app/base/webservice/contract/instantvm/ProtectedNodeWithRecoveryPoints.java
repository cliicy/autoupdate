package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;
import java.util.List;

import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;

public class ProtectedNodeWithRecoveryPoints implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ProtectedNodeInDestination protectedNode;
	private List<RecoveryPoint> recoveryPointList;
	public ProtectedNodeInDestination getProtectedNode() {
		return protectedNode;
	}
	public void setProtectedNode(ProtectedNodeInDestination protectedNode) {
		this.protectedNode = protectedNode;
	}
	public List<RecoveryPoint> getRecoveryPointList() {
		return recoveryPointList;
	}
	public void setRecoveryPointList(List<RecoveryPoint> recoveryPointList) {
		this.recoveryPointList = recoveryPointList;
	}
	
}

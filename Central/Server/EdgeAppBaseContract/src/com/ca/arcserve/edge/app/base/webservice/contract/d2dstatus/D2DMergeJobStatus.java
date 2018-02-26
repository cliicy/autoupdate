package com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus;

import java.io.Serializable;

import com.ca.arcflash.webservice.data.merge.MergeStatus;

public class D2DMergeJobStatus implements Serializable {

	private static final long serialVersionUID = -1666534976076792727L;
	
	private int nodeId;
	private MergeStatus status;
	
	public int getNodeId() {
		return nodeId;
	}
	
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public MergeStatus getStatus() {
		return status;
	}

	public void setStatus(MergeStatus status) {
		this.status = status;
	}

}

package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;

public class RecoveryServerResult implements Serializable{

	private static final long serialVersionUID = 4649822827067271752L;

//	public static final int RPSAGENT_DONT_EXIST_CURRENT_CONSLE = 11;
//	public static final int HYPERVAGENT_DONT_EXIST_CURRENT_CONSLE = 12;
//	public static final int WINDOWS_AGENT_MANAGED_OTHERS = 13;
//	
//	public static final int D2D_DONT_INSTALL =21;
//	public static final int AGENT_UPPER_SIX_VERSION =22;
//	
//	public static final int REMOTE_NODE_NULL =23;
//	public static final int UPDATE_NODE_ERROR =24;
	private boolean hasError;
	private String errorCode;
	private NodeDetail nodeDetail;
	private NodeRegistrationInfo recoveryServer;
	private boolean addNode;
	private String errorMessage;
	
	public RecoveryServerResult(){}
	

	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public NodeDetail getNodeDetail() {
		return nodeDetail;
	}

	public void setNodeDetail(NodeDetail nodeDetail) {
		this.nodeDetail = nodeDetail;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public boolean isHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public NodeRegistrationInfo getRecoveryServer() {
		return recoveryServer;
	}

	public void setRecoveryServer(NodeRegistrationInfo recoveryServer) {
		this.recoveryServer = recoveryServer;
	}

	public boolean isAddNode() {
		return addNode;
	}

	public void setAddNode(boolean addNode) {
		this.addNode = addNode;
	}
	
	
}

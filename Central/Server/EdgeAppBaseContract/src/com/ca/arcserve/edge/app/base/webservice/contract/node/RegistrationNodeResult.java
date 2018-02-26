package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class RegistrationNodeResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3987005984730551661L;
	private String[] errorCodes;
	private int hostID;
	private String nodeUUID;
	private String[] errorCodes2;
	public String[] getErrorCodes() {
		return errorCodes;
	}
	public void setErrorCodes(String[] errorCodes) {
		this.errorCodes = errorCodes;
	}
	public int getHostID() {
		return hostID;
	}
	public void setHostID(int hostID) {
		this.hostID = hostID;
	}
	public String[] getErrorCodes2() {
		return errorCodes2;
	}
	public void setErrorCodes2(String[] errorCodes2) {
		this.errorCodes2 = errorCodes2;
	}
	public String getNodeUUID() {
		return nodeUUID;
	}
	public void setNodeUUID(String nodeUUID) {
		this.nodeUUID = nodeUUID;
	}
	
}

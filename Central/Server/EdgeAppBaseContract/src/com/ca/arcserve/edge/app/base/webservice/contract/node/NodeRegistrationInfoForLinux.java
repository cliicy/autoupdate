package com.ca.arcserve.edge.app.base.webservice.contract.node;

public class NodeRegistrationInfoForLinux extends NodeRegistrationInfo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2656045361278639839L;
	private boolean existLinuxBackupServer;
	private String nodeUUID;
	private int linuxBackupServerIdUsedForValidation;
	
	public boolean isExistLinuxBackupServer() {
		return existLinuxBackupServer;
	}

	public void setExistLinuxBackupServer(boolean existLinuxBackupServer) {
		this.existLinuxBackupServer = existLinuxBackupServer;
	}

	public String getNodeUUID() {
		return nodeUUID;
	}

	public void setNodeUUID(String nodeUUID) {
		this.nodeUUID = nodeUUID;
	}

	public int getLinuxBackupServerIdUsedForValidation() {
		return linuxBackupServerIdUsedForValidation;
	}

	public void setLinuxBackupServerIdUsedForValidation(
			int linuxBackupServerIdUsedForValidation) {
		this.linuxBackupServerIdUsedForValidation = linuxBackupServerIdUsedForValidation;
	}
	

}

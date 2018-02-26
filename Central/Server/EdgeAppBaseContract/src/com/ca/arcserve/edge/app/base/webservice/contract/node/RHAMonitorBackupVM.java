/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

/**
 * @author lijwe02
 * 
 */
public class RHAMonitorBackupVM implements Serializable {
	private static final long serialVersionUID = -6904840503351183337L;
	private String cntrlAppName;
	private String vmInstanceUUID;
	private String backupVMNodeName;
	private String backupDestinationDir;
	private String backupVMName;

	public String getCntrlAppName() {
		return cntrlAppName;
	}

	public void setCntrlAppName(String cntrlAppName) {
		this.cntrlAppName = cntrlAppName;
	}

	public String getVmInstanceUUID() {
		return vmInstanceUUID;
	}

	public void setVmInstanceUUID(String vmInstanceUUID) {
		this.vmInstanceUUID = vmInstanceUUID;
	}

	public String getBackupVMNodeName() {
		return backupVMNodeName;
	}

	public void setBackupVMNodeName(String backupVMNodeName) {
		this.backupVMNodeName = backupVMNodeName;
	}

	public String getBackupDestinationDir() {
		return backupDestinationDir;
	}

	public void setBackupDestinationDir(String backupDestinationDir) {
		this.backupDestinationDir = backupDestinationDir;
	}

	public String getBackupVMName() {
		return backupVMName;
	}

	public void setBackupVMName(String backupVMName) {
		this.backupVMName = backupVMName;
	}
}

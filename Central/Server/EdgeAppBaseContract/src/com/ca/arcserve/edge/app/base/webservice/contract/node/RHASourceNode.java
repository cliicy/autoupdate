/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

/**
 * @author lijwe02
 * 
 */
public class RHASourceNode implements Serializable {
	private static final long serialVersionUID = -1047750039016820298L;
	private long scenarioId;
	private String scenarioName;
	private boolean scenarioNameChanged = false;
	private String vmInstanceUUID;
	private String nodeName;
	private String converter;
	private boolean converterChanged = false;
	private String recoveryPointFolder;
	private boolean recoveryPointFolderChanged = false;
	private RHASourceStatus status = RHASourceStatus.New;
	private RHAScenarioType scenarioType;
	private String vmName;
	private String hypervisorName;
	private String masterHost;
	private String masterIp;
	private String replicaHost;
	private String replicaIp;

	public RHASourceNode() {

	}

	public RHASourceNode(RHASourceNode that) {
		this.scenarioId = that.scenarioId;
		this.scenarioName = that.scenarioName;
		this.scenarioNameChanged = that.scenarioNameChanged;
		this.vmInstanceUUID = that.vmInstanceUUID;
		this.nodeName = that.nodeName;
		this.converter = that.converter;
		this.converterChanged = that.converterChanged;
		this.recoveryPointFolder = that.recoveryPointFolder;
		this.recoveryPointFolderChanged = that.recoveryPointFolderChanged;
		this.status = that.status;
		this.scenarioType = that.scenarioType;
		this.vmName = that.vmName;
		this.hypervisorName = that.hypervisorName;
		this.masterHost = that.masterHost;
		this.masterIp = that.masterIp;
		this.replicaHost = that.replicaHost;
		this.replicaIp = that.replicaIp;
	}

	public long getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(long scenarioId) {
		this.scenarioId = scenarioId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getConverter() {
		return converter;
	}

	public void setConverter(String converter) {
		this.converter = converter;
	}

	public String getRecoveryPointFolder() {
		return recoveryPointFolder;
	}

	public void setRecoveryPointFolder(String recoveryPointFolder) {
		this.recoveryPointFolder = recoveryPointFolder;
	}

	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("[scenarioId=").append(scenarioId);
		strBuf.append(", scenarioName=").append(scenarioName);
		strBuf.append(", nodeName=").append(nodeName);
		strBuf.append(", vmInstanceUUID=").append(vmInstanceUUID);
		strBuf.append(", vmName=").append(vmName);
		strBuf.append(", converter=").append(converter);
		strBuf.append(", recoveryPointFolder=").append(recoveryPointFolder).append("]");
		return strBuf.toString();
	}

	public RHASourceStatus getStatus() {
		return status;
	}

	public void setStatus(RHASourceStatus status) {
		this.status = status;
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public boolean isConverterChanged() {
		return converterChanged;
	}

	public void setConverterChanged(boolean converterChanged) {
		this.converterChanged = converterChanged;
	}

	public boolean isRecoveryPointFolderChanged() {
		return recoveryPointFolderChanged;
	}

	public void setRecoveryPointFolderChanged(boolean recoveryPointFolderChanged) {
		this.recoveryPointFolderChanged = recoveryPointFolderChanged;
	}

	public boolean isScenarioNameChanged() {
		return scenarioNameChanged;
	}

	public void setScenarioNameChanged(boolean scenarioNameChanged) {
		this.scenarioNameChanged = scenarioNameChanged;
	}

	public boolean isNeedRedeploy() {
		return (isConverterChanged() || isRecoveryPointFolderChanged());
	}

	public RHAScenarioType getScenarioType() {
		return scenarioType;
	}

	public void setScenarioType(RHAScenarioType scenarioType) {
		this.scenarioType = scenarioType;
	}

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public String getHypervisorName() {
		return hypervisorName;
	}

	public void setHypervisorName(String hypervisorName) {
		this.hypervisorName = hypervisorName;
	}

	public String getMasterHost() {
		return masterHost;
	}

	public void setMasterHost(String masterHost) {
		this.masterHost = masterHost;
	}

	public String getMasterIp() {
		return masterIp;
	}

	public void setMasterIp(String masterIp) {
		this.masterIp = masterIp;
	}

	public String getReplicaHost() {
		return replicaHost;
	}

	public void setReplicaHost(String replicaHost) {
		this.replicaHost = replicaHost;
	}

	public String getReplicaIp() {
		return replicaIp;
	}

	public void setReplicaIp(String replicaIp) {
		this.replicaIp = replicaIp;
	}

	public String getVmInstanceUUID() {
		return vmInstanceUUID;
	}

	public void setVmInstanceUUID(String vmInstanceUUID) {
		this.vmInstanceUUID = vmInstanceUUID;
	}
}

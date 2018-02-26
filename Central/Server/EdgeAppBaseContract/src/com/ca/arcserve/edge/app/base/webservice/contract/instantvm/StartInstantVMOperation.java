package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.RecoveryPointInformationForCPM;

public class StartInstantVMOperation implements Serializable { 
	
	private static final long serialVersionUID = 1L;
	private NodeRegistrationInfo proxyInfo;
	private RecoveryPointInformationForCPM recoveryPointForCPM;
	private HypervisorWrapper hypervisorWrapper;
	private VMInfoInCPM vmInfo;
	private boolean needInstallNFS = false;

	public 	StartInstantVMOperation() {
	}
	public NodeRegistrationInfo getProxyInfo() {
		return proxyInfo;
	}
	public void setProxyInfo(NodeRegistrationInfo proxyInfo) {
		this.proxyInfo = proxyInfo;
	}	
	public RecoveryPointInformationForCPM getRecoveryPointForCPM() {
		return recoveryPointForCPM;
	}
	public void setRecoveryPointForCPM(
			RecoveryPointInformationForCPM recoveryPointForCPM) {
		this.recoveryPointForCPM = recoveryPointForCPM;
	}

	public VMInfoInCPM getVmInfo() {
		return vmInfo;
	}
	public void setVmInfo(VMInfoInCPM vmInfo) {
		this.vmInfo = vmInfo;
	}
	public HypervisorWrapper getHypervisorWrapper() {
		return hypervisorWrapper;
	}
	public void setHypervisorWrapper(HypervisorWrapper hypervisorWrapper) {
		this.hypervisorWrapper = hypervisorWrapper;
	}
	
	public boolean isNeedInstallNFS() {
		return needInstallNFS;
	}
	public void setNeedInstallNFS(boolean needInstallNFS) {
		this.needInstallNFS = needInstallNFS;
	}
	@Override
	public String toString() {
		return "proxy node: " + proxyInfo.getId() +", "+ proxyInfo.getNodeName() +"\n\r " +
			"hypervisor: " +  hypervisorWrapper.getHyperVisor().getServerName() +"\n\r " + 
			"recoverypoint: " + recoveryPointForCPM.toString();
	}
}

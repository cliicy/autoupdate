package com.ca.arcserve.edge.app.base.webservice.contract.node.entity;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;

public class VmInfoSummary implements Serializable {
	private static final long serialVersionUID = 1L;
	private int hostId;
	private String vmName;
	private String vmInstanceUUID;
	private String hypervisor;
	private int vmStatus; // VmStatus
	private int verifyStatus;
	private String esxName;

	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public String getHypervisor() {
		return hypervisor;
	}

	public void setHypervisor(String hypervisor) {
		this.hypervisor = hypervisor;
	}

	public String getVmInstanceUUID() {
		return vmInstanceUUID;
	}

	public void setVmInstanceUUID(String vmInstanceUUID) {
		this.vmInstanceUUID = vmInstanceUUID;
	}

	public int getVmStatus() {
		return vmStatus;
	}

	public void setVmStatus(int vmStatus) {
		this.vmStatus = vmStatus;
	}

	public int getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(int verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VmInfoSummary other = (VmInfoSummary) obj;
		if (hostId != other.getHostId())
			return false;
		if (!StringUtil.isEqual(vmName, other.getVmName()))
			return false;
		if (!StringUtil.isEqual(vmInstanceUUID, other.getVmInstanceUUID()))
			return false;
		if (!StringUtil.isEqual(hypervisor, other.getHypervisor()))
			return false;
		if (vmStatus != other.getVmStatus())
			return false;
		if (verifyStatus != other.getVerifyStatus())
			return false;
		if (esxName != other.getEsxName())
			return false;
		return true;
	}

	public void update(VmInfoSummary other) {
		if (other == null)
			return;
		if (hostId != other.getHostId())
			hostId = other.getHostId();
		if (!StringUtil.isEqual(vmName, other.getVmName()))
			vmName = other.getVmName();
		if (!StringUtil.isEqual(vmInstanceUUID, other.getVmInstanceUUID()))
			vmInstanceUUID = other.getVmInstanceUUID();
		if (!StringUtil.isEqual(hypervisor, other.getHypervisor()))
			hypervisor = other.getHypervisor();
		if (vmStatus != other.getVmStatus())
			vmStatus = other.getVmStatus();
		if (verifyStatus != other.getVerifyStatus())
			verifyStatus = other.getVerifyStatus();
		if (esxName != other.getEsxName())
			esxName = other.getEsxName();
	}

	public String getEsxName() {
		return esxName;
	}

	public void setEsxName(String esxName) {
		this.esxName = esxName;
	}

}

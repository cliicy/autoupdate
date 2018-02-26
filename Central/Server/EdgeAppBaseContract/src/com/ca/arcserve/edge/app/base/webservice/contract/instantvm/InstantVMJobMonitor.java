package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;

public class InstantVMJobMonitor extends FlashJobMonitor implements Serializable {
	
	private static final long serialVersionUID = -7022905807766310004L;
	
	private String ivmJobUUID;
	private String vmName;
	private String hypervisor;
	private String proxy;
	private String vmPath;
	private RecoveryPoint recoveryPoint;

	public String getIvmJobUUID() {
		return ivmJobUUID;
	}
	public void setIvmJobUUID(String ivmJobUUID) {
		this.ivmJobUUID = ivmJobUUID;
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
	public String getProxy() {
		return proxy;
	}
	public void setProxy(String proxy) {
		this.proxy = proxy;
	}
	public String getVmPath() {
		return vmPath;
	}
	public void setVmPath(String vmPath) {
		this.vmPath = vmPath;
	}
	public RecoveryPoint getRecoveryPoint() {
		return recoveryPoint;
	}
	public void setRecoveryPoint(RecoveryPoint recoveryPoint) {
		this.recoveryPoint = recoveryPoint;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ivmJobUUID == null) ? 0 : ivmJobUUID.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstantVMJobMonitor other = (InstantVMJobMonitor) obj;
		if (ivmJobUUID == null) {
			if (other.ivmJobUUID != null)
				return false;
		} else if (!ivmJobUUID.equals(other.ivmJobUUID))
			return false;
		return true;
	}	
	

}

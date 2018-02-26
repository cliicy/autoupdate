package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.MachineConfigure;

public class RecoveryPointInfoForInstantVM implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private MachineConfigure machineConfigure;
	private boolean containsBootAndSystemVolume;
		
	public MachineConfigure getMachineConfigure() {
		return machineConfigure;
	}
	public void setMachineConfigure(MachineConfigure machineConfigure) {
		this.machineConfigure = machineConfigure;
	}
	public boolean containsBootAndSystemVolume() {
		return containsBootAndSystemVolume;
	}
	public void setContainsBootAndSystemVolume(boolean containsBootAndSystemVolume) {
		this.containsBootAndSystemVolume = containsBootAndSystemVolume;
	}	
}

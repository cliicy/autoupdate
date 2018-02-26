package com.ca.arcflash.webservice.jni.model;


public class JMachineDetail {
	/*
	 * 0: physical machine
	 * 1: hyperV VM 
	 * 2: vmware VM
	 * 3: Hbbu 
	 */
	private int machineType = 0;
	
	//host name of the machine
	private String hostName = null;
	
	//the hypervisor host name in which this machine runs if this machine is a virtual machine.
	private String hypervisorHostName = null;
	
	// For HBBU machine type
	private int hyperVisorNumberOfProcessors = 0; //used for HBBU node
	private int hyperVisorNumberOfLogicalProcessors = 0; //used for HBBU node

	// For other machine types
	private int osMajorVersion = 0;
	private int osMinorVersion = 0;
	private int osProductType = 0;
	private int osSuiteMask;

	// For other machine types
	private int numberOfProcessors = 0; //used for local node
	private int numberOfLogicalProcessors = 0; //used for local node

	public int getMachineType() {
		return machineType;
	}
	public void setMachineType(int machineType) {
		this.machineType = machineType;
	}

	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHypervisorHostName() {
		return hypervisorHostName;
	}
	public void setHypervisorHostName(String hypervisorHostName) {
		this.hypervisorHostName = hypervisorHostName;
	}

	public int getOsMajorVersion() {
		return osMajorVersion;
	}
	public void setOsMajorVersion(int majorVersion) {
		this.osMajorVersion = majorVersion;
	}

	public int getOsMinorVersion() {
		return osMinorVersion;
	}
	public void setOsMinorVersion(int minorVersion) {
		this.osMinorVersion = minorVersion;
	}

	public int getOsProductType() {
		return osProductType;
	}
	public void setOsProductType(int productType) {
		this.osProductType = productType;
	}

	public int getOsSuiteMask() {
		return osSuiteMask;
	}
	public void setOsSuiteMask(int osSuiteMask) {
		this.osSuiteMask = osSuiteMask;
	}

	public int getHyperVisorNumberOfProcessors() {
		return hyperVisorNumberOfProcessors;
	}
	public void setHyperVisorNumberOfProcessors(int hyperVisorNumberOfProcessors) {
		this.hyperVisorNumberOfProcessors = hyperVisorNumberOfProcessors;
	}

	public int getHyperVisorNumberOfLogicalProcessors() {
		return hyperVisorNumberOfLogicalProcessors;
	}
	public void setHyperVisorNumberOfLogicalProcessors(int hyperVisorNumberOfLogicalProcessors) {
		this.hyperVisorNumberOfLogicalProcessors = hyperVisorNumberOfLogicalProcessors;
	}

	public int getNumberOfProcessors() {
		return numberOfProcessors;
	}
	public void setNumberOfProcessors(int numberOfProcessors) {
		this.numberOfProcessors = numberOfProcessors;
	}

	public int getNumberOfLogicalProcessors() {
		return numberOfLogicalProcessors;
	}
	public void setNumberOfLogicalProcessors(int numberOfLogicalProcessors) {
		this.numberOfLogicalProcessors = numberOfLogicalProcessors;
	}
}

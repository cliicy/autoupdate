package com.ca.arcflash.webservice.jni.model;

public class JJobScriptBackupVM {
	
	private String vmName;
	private String vmUUID;
	private String vmInstanceUUID;
	private String vmHostName;
	private String vmVMX;
	private String osUsername;
	private String osPassword;
	private int state;
	private int vmType;
	
	public String getVmName() {
		return vmName;
	}
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	public String getVmUUID() {
		return vmUUID;
	}
	public void setVmUUID(String vmUUID) {
		this.vmUUID = vmUUID;
	}
	public String getVmHostName() {
		return vmHostName;
	}
	public void setVmHostName(String vmHostName) {
		this.vmHostName = vmHostName;
	}
	public String getVmVMX() {
		return vmVMX;
	}
	public void setVmVMX(String vmVMX) {
		this.vmVMX = vmVMX;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getOsUsername() {
		return osUsername;
	}
	public void setOsUsername(String osUsername) {
		this.osUsername = osUsername;
	}
	public String getOsPassword() {
		return osPassword;
	}
	public void setOsPassword(String osPassword) {
		this.osPassword = osPassword;
	}
	public String getVmInstanceUUID() {
		return vmInstanceUUID;
	}
	public void setVmInstanceUUID(String vmInstanceUUID) {
		this.vmInstanceUUID = vmInstanceUUID;
	}
	public int getVmType() {
		return vmType;
	}
	public void setVmType(int vmType) {
		this.vmType = vmType;
	}
}

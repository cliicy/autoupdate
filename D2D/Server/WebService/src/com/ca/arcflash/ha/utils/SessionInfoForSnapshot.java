package com.ca.arcflash.ha.utils;
/**
 * Include disk information for attaching
 * @author gonro07
 *
 */
public class SessionInfoForSnapshot {
	String diskSignature;
	String diskFile;
	boolean bootDisk;
	boolean systemDisk;
	private String controllerType;
	public String getDiskSignature() {
		return diskSignature;
	}
	public void setDiskSignature(String diskSignature) {
		this.diskSignature = diskSignature;
	}
	public String getDiskFile() {
		return diskFile;
	}
	public void setDiskFile(String diskFile) {
		this.diskFile = diskFile;
	}
	public boolean isBootDisk() {
		return bootDisk;
	}
	public void setBootDisk(boolean bootDisk) {
		this.bootDisk = bootDisk;
	}
	public boolean isSystemDisk() {
		return systemDisk;
	}
	public void setSystemDisk(boolean systemDisk) {
		this.systemDisk = systemDisk;
	}
	@Override
	public String toString() {
		return "SessionInfoForSnapshot [bootDisk=" + bootDisk + ", diskFile="
				+ diskFile + ", diskSignature=" + diskSignature
				+ ", systemDisk=" + systemDisk + "]";
	}
	public void setControllerType(String controllerType) {
		this.controllerType = controllerType;
	}
	public String getControllerType() {
		return controllerType;
	}
	
}

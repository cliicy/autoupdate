package com.ca.arcflash.webservice.jni.model;

public class JMountPoint {
	private String MountID;
	private String diskSignature;
	private long mountHandle = 0;
	public void setDiskSignature(String diskSignature) {
		this.diskSignature = diskSignature;
	}
	public String getDiskSignature() {
		return diskSignature;
	}
	public void setMountID(String mountID) {
		MountID = mountID;
	}
	public String getMountID() {
		return MountID;
	}
	public void setMountHandle(long mountHandle) {
		this.mountHandle = mountHandle;
	}
	public long getMountHandle() {
		return mountHandle;
	}

	}

package com.ca.arcflash.webservice.jni.model;

import java.util.ArrayList;
import java.util.List;


public class JDisk {
	
	private int diskNumber;
	
	private long size;
	
	private int signature;
	
	private int diskType;
	
	private String controllerType;
	
	private String partitionType;
	
	private String diskUrl;
	
	private String diskDataStore;
	
	private List<JVolume> volumes = new ArrayList<JVolume>();

	public int getDiskNumber() {
		return diskNumber;
	}

	public void setDiskNumber(int diskNumber) {
		this.diskNumber = diskNumber;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public int getSignature() {
		return signature;
	}

	public void setSignature(int signature) {
		this.signature = signature;
	}

	public int getDiskType() {
		return diskType;
	}

	public void setDiskType(int diskType) {
		this.diskType = diskType;
	}

	public String getControllerType() {
		return controllerType;
	}

	public void setControllerType(String controllerType) {
		this.controllerType = controllerType;
	}

	public String getPartitionType() {
		return partitionType;
	}

	public void setPartitionType(String partitionType) {
		this.partitionType = partitionType;
	}

	public List<JVolume> getVolumes() {
		return volumes;
	}

	public void setVolumes(List<JVolume> volumes) {
		this.volumes = volumes;
	}

	public String getDiskUrl() {
		return diskUrl;
	}

	public void setDiskUrl(String diskUrl) {
		this.diskUrl = diskUrl;
	}

	public String getDiskDataStore() {
		return diskDataStore;
	}

	public void setDiskDataStore(String diskDataStore) {
		this.diskDataStore = diskDataStore;
	}

}
